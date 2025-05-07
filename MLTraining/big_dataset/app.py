import os
import flask
from flask import Flask, request, jsonify
import joblib
from utils_data import scaler_path, saved_models_dir
import tensorflow as tf
import utils_data
from train_diff_prediction import predict_differential
from utils_data import sex, age, m, joblib_extension
from sklearn.preprocessing import MinMaxScaler
import numpy as np

if not os.path.exists(scaler_path + joblib_extension):
    exit(1)
min_max_scaler = joblib.load(scaler_path + joblib_extension)
if not os.path.exists(saved_models_dir + 'neural_network_pathology.keras'):
    exit(1)
model = tf.keras.models.load_model(saved_models_dir + 'neural_network_pathology.keras')
evidences = utils_data.obtain_evidences()
conditions = utils_data.obtain_conditions()
evidences_dict = utils_data.obtain_evidences_as_dict()
conditions_dict = utils_data.obtain_conditions_as_dict()
conditions_dict_index = utils_data.obtain_conditions_as_dict_index()

app = Flask(__name__)

@app.route('/obtain-condition')
def obtain_conditions():
    data = request.get_json(force=True)
    patient_evidences = data['symptoms']
    patient_sex = 1 if data[sex] == m else 0
    patient_age = data[age]
    patient_age = min_max_scaler.transform([[patient_age]])[0][0]
    patient_features = np.zeros(2 + len(evidences))
    patient_features[0] = patient_age
    patient_features[1] = patient_sex
    for patient_evidence in patient_evidences:
        patient_features[evidences_dict[patient_evidence]] = 1
    y_pred = np.argmax(model.predict(np.array(patient_features)))[0]
    diagnosis = conditions_dict_index[y_pred]
    differential_diagnosis = predict_differential(np.array(patient_features))[0]
    return jsonify({'diagnosis': diagnosis, 'differential_doagnosis': differential_diagnosis})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5014)