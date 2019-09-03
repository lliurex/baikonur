from flask import Flask, request, escape, render_template
from packageFinder import PackageFinder
import json

application = Flask(__name__)
configuration = json.loads(open('/run/secrets/packagesconfig','r'))

@application.route("/")
def index():
    return render_template("search.html")

@application.route("/search",methods=['POST'])
def search():
    result = request.form
    options = []
    if 'equals' in request.form:
        options.append('equals')
    if 'contains' in request.form:
        options.append('contains')
    if 'description' in request.form:
        options.append('description')
    if 'needle' not in request.form:
        return "Keyword is needed"
    app = PackageFinder(configuration['url_base'], configuration['search_path'], configuration['pool_path'])
    context = {}
    context['packages'] = app.search(result['needle'],options)
    context['baseurl'] = app.base_url
    return render_template("result.html", **context)
    


if __name__ == "__main__":
    application.run(host="0.0.0.0", port=80)
