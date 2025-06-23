from flask import Flask, Request

app = Flask(__name__)

@app.route("/ping", methods=['GET'])
def hello_world():
    return "pong"

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8080)