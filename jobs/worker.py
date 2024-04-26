from celery import Celery

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

@celery.task(name="build_package")
def build_package(source_name, version_package, build_path, distribution, upstream):
    pass
