from celery import Celery
import debian.deb822
import asyncio
import codecs
import glob
import requests
import json
import subprocess as s
import pathlib
from telegram_sender import TelegramSender

celery = Celery(
        __name__,
        broker="redis://redis_server:6379/0",
        backend="redis://redis_server:6379/0",
        task_routes={'build_package':'sbuild',
                     'includedeb':'repobuilder'}
        )
@celery.task(name='includedeb')
def includedeb( distribution, buildpath ):
    pass

def telegram_send(state, package, build_file=''):
    t = TelegramSender()
    t.send_message( state, package, build_file)

def write_state(build_path, state):
    with open(build_path + "/baikonur_state_build" , 'w') as fd:
        fd.write("{state}\n".format(state=state))
    return state

def get_chroot_build( distribution ):
    name_chroot = ''
    if distribution.startswith('bionic'):
        name_chroot = 'lliurex19'
    elif distribution.startswith('focal'):
        name_chroot = 'lliurex21'
    elif distribution.startswith('lliurex22'):
        name_chroot = 'lliurex22'
    elif distribution.startswith('jammy'):
        name_chroot = 'lliurex23'
    elif distribution.startswith('noble'):
        name_chroot = 'lliurex25'
    else:
        name_chroot = None
    return name_chroot

def get_upstream_version( version_package ):
    first = version_package.split(":")
    if len(first) > 1:
        second = first[1]
    else:
        second = first[0]
    list_version = second.split("-")[0:-1]
    if len(list_version) > 0:
        return '-'.join(list_version)
    else:
        return second

def check_package_exists(source_name, distribution, version):
    
    if "testing" in distribution:
        return False
    files = {'needle':(None,source_name),'repository':(None,'baikonur/'+distribution.split('-')[0]+'/unstable'),'source':(None,'yes'),'json':(None,'yes')}
    response = requests.post('http://find/find/search',files=files)  
    all_result = json.loads(response.content.decode('utf-8'))
    for package in all_result['packages']:
        if source_name == package['package']['Package'] and version == package['package']['Version']:
            return True
    return False


def get_changes_file( build_path):
    changes = None
    changes_path = glob.glob(build_path + "/*.baikonur")
    if len(changes_path) > 0:
        changes = debian.deb822.Changes( codecs.open( changes_path[0] ,encoding='utf-8' ) )
    return changes

@celery.task(name="build_package")
def build_package(source_name, version_package, build_path, distribution, upstream):
    state = write_state(build_path, "info")
    telegram_send("info", source_name )
    package_path = None
    # changes_file = get_changes_file(build_path)
    try:
        state = write_state(build_path, 'chroot')
        name_chroot = get_chroot_build(distribution)
        if name_chroot is None:
            raise Exception()
        state = write_state(build_path, 'exists')
        package_exists = check_package_exists( source_name, distribution, version_package )
        if package_exists:
            raise Exception()
        state = write_state(build_path, 'false')
        for x in pathlib.Path(build_path).iterdir():
            if x.is_dir():
                package_path = x
                break
        if package_path is None:
            return 1
        if upstream is not None:
            cmd = 'git archive --format=tar.gz -o ../{package_source}_{upstream_version}.orig.tar.gz {upstream}'.format(\
                    package_source=source_name, \
                    upstream_version=get_upstream_version(version_package), \
                    upstream=upstream)
            p = s.Popen(cmd, shell=True, cwd=package_path).communicate()

        sbuild_process = s.Popen('sbuild -d {}'.format(name_chroot), shell=True, stdout=s.PIPE, stderr=s.PIPE, cwd=package_path)
        ( std, err ) = sbuild_process.communicate()
        if sbuild_process.returncode != 0:
            raise Exception()
        state = write_state(build_path, 'true')
        includedeb.delay( distribution, build_path )

        # check result 
        # All ok -> call reprepro & telegram sender with build 
        # Fail call telegram fail
    except:
        pass
    package_path = package_path if package_path is not None else ''
    telegram_send(state, source_name, build_path)
    



