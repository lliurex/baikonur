from devscripts import control
from debian import deb822
import codecs
import os

class PackageFinder:

    def __init__(self, base_url, search_path, folder):
        self.base_url = base_url
        self.search_path = search_path
        self.folder = folder


    def search(self, needle, flags_search):
        #flags_search = ['equals','contains','description','depends',]
        xxx = os.path.join(self.folder,self.search_path)
        list_distributions = os.listdir(xxx)

        global_result = []
        for release in list_distributions:
            if os.path.exists(os.path.join(xxx,release,'Release')):
                index_file = deb822.Changes( codecs.open( os.path.join( xxx, release, 'Release' ), encoding='utf-8' ) )
                
                # Extract index files from Release file.
                list_files = [ w.strip().split(" ")[2] for w in index_file['md5sum'].split("\n") if w.endswith('Packages') ]

                for x in list_files:
                    all_packages = control.Control(os.path.join(xxx,release,x))
                    for package in all_packages.paragraphs:
                        if ( 'equals' in flags_search and needle == package['Package']  ) or \
                           ('contains' in flags_search and needle in package['Package'] ) or \
                           ('description' in flags_search and needle in package['Description']):
                                global_result.append({'package':package,'release':release, 'index':x})
        return global_result
