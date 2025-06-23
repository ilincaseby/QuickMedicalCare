from flask import Flask, jsonify, request, abort
from joblib import load
import json
import numpy as np

app = Flask(__name__)


@app.route("/prognosis", methods=["POST"])
def obtain_prognosis():
    data = request.get_json(force=True)
    symptoms = data["symptoms"]
    symptoms_arr = np.zeros(len(symptoms_encoding))
    for symptom in symptoms:
        if symptom in symptoms_encoding:
            index = symptoms_encoding[symptom]
            symptoms_arr[index] = 1
    symptoms_arr = symptoms_arr.reshape(1, -1)
    result = model.predict_proba(symptoms_arr)
    index_disease = -1
    val = 0
    max_probabilities = [prob[0][0] for prob in result]

    max_index = np.argmax(max_probabilities)

    # for i in range(0, len(result[0])):
    #     if result[0][i] > val:
    #         index_disease = i
    #         val = result[0][i]
    # if index_disease == -1:
    #     return jsonify({'response': 'Insufficient data'})
    prognosis = ""
    for k, v in disease_encoding.items():
        if v == max_index:
            prognosis = k
    return jsonify({'response': f'{prognosis} probability {np.max(max_probabilities)}'})


if __name__ == "__main__":
    model = load("mlpPrognosisModel.joblib")
    symptoms_encoding = {}
    disease_encoding = {}
    with open("symptoms_index_encoding.json", "r") as f:
        symptoms_encoding = json.load(f)
    with open("diseases_index_encoding.json", "r") as f:
        disease_encoding = json.load(f)
    
    app.run(host='0.0.0.0', port=5013)
