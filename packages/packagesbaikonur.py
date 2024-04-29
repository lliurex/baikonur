from flask import Flask, request, render_template
from packageFinder import PackageFinder
import json
import subprocess as s
application = Flask(__name__)
configuration = json.load(open('/run/secrets/packagesconfig','r'))
application.config['JSON_AS_ASCII'] = False

@application.route("/build/",methods=['POST'],strict_slashes=False)
def build():
    repo = request.form.get('repo')
    branch = request.form.get('branch')
    upstream = request.form.get('upstream','')
    p = s.Popen( "/usr/bin/clone_github_repo {repo} {branch} {upstream}".format(repo=repo,branch=branch,upstream=upstream), shell=True, stdout=s.PIPE, stderr=s.PIPE )
    (stdout,stderr)=p.communicate()
    result = "Github request: \n  Repo : " + str(request.form["repo"])+"\n  Branch : "+request.form["branch"] +"\n" 
    if upstream != '':
        result += '  Upstream : ' + upstream + "\n"
    return result

@application.route("/find/",strict_slashes=False)
def index():
    return render_template("search.html")

@application.route("/find/search",methods=['POST'])
def search():
    result = request.form
    options = []
    if 'equals' in request.form:
        options.append('equals')
    if 'contains' in request.form:
        options.append('contains')
    if 'description' in request.form:
        options.append('description')
    if 'source' in request.form:
        options.append('source')
        options.append('equals')
    if 'needle' not in request.form:
        return "Keyword is needed"
    if 'repository' not in request.form:
        return "Repository is needed"
    app = PackageFinder(configuration['url_base'], result['repository'] + "/" + configuration['search_path'], configuration['pool_path'])
    context = {}
    context['packages'] = app.search(result['needle'],options)
    context['baseurl'] = app.base_url
    if 'json' in request.form:
        response = application.response_class(
                response = json.dumps(context,ensure_ascii=False),
                mimetype='application/json')
        return response
    return render_template("result.html", **context)



if __name__ == "__main__":
    application.run(host="0.0.0.0", port=80)
