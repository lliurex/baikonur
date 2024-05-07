from celery import Celery
import codecs
import glob
import requests
import json
import subprocess as s
import pathlib

celery = Celery(
        __name__,
        broker="redis://redis_server:6379/0",
        backend="redis://redis_server:6379/0",
        task_routes={'build_package':'sbuild',
                     'includedeb':'repobuilder'}
        )
@celery.task(name='includedeb')
def includedeb( distribution, buildpath ):
    if "testing" in distribution:
        p = s.Popen('reprepro -C testing includedeb {distribution} {buildpath}/*.deb && reprepro export'.format( distribution=distribution, buildpath= buildpath ),shell=True, cwd='/srv/repository/baikonur/{}/unstable'.format(distribution.split("-")[0]))
    else:
        p = s.Popen('reprepro includedeb {distribution} {buildpath}/*.deb && reprepro export'.format( distribution=distribution, buildpath= buildpath ),shell=True, cwd='/srv/repository/baikonur/{}/unstable'.format(distribution.split("-")[0]))
    p.communicate()

