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
                    with codecs.open( os.path.join(xxx,release,x), encoding='utf-8') as fd:
                        for package in deb822.Deb822.iter_paragraphs(fd):
                            found = False
                            if 'source' in flags_search and \
                                ( ( 'Source' in package.keys() and needle == package['Source'] )  or \
                                  ( 'Source' not in package.keys() and needle == package['Package'] ) ):
                                found = True
                            if ( 'equals' in flags_search and needle == package['Package']  ) or \
                               ('contains' in flags_search and needle in package['Package'] ) or \
                               ('description' in flags_search and needle in package['Description']):
                               found = True
                            if found:
                                global_result.append({'package':dict(package),'release':release, 'index':x})
        return global_result
