import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from utils_data import obtain_conditions, obtain_evidences, obtain_general_evidences, sex, age, m, f, scaler_path, saved_models_dir, joblib_extension
import json
import os
import joblib
import re

embeddings_path = os.getenv("EMBEDDINGS_PATH", default="saved_models/condition_embeddings.json")
test_path = os.getenv("TEST_DIFF_PATH", default="preprocessed_dataset/preprocessed_test.csv")
validate_path = os.getenv("VALIDATE_DIFF_PATH", default="preprocessed_dataset/preprocessed_validate.csv")
features = [age, sex] + obtain_evidences()
evidences = features[2:]
labels = obtain_conditions()
condition_embeddings = {}
with open(embeddings_path, "r") as f:
    condition_embeddings = json.load(f)
condition_embeddings = {key: np.array(val) for key, val in condition_embeddings.items()}
general_evidences = obtain_general_evidences()
general_evidences_dict = {general_evidences[i]: i for i in range(len(general_evidences))}
p = re.compile('_@_\w*')

# a * b = |a|*|b|* cos(a, b)
# cos(a, b) = (a * b) / (|a| * |b|)

def cosine_similarity(a, b):
    return np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b))

def read_test_csv(path):
    df = pd.read_csv(path)
    df[sex] = df[sex].map({m: 1, f: 0})
    if not os.path.isdir(saved_models_dir):
        os.makedirs(saved_models_dir)
    if os.path.exists(scaler_path + joblib_extension):
        min_max_scaler = joblib.load(scaler_path + joblib_extension)
    else:
        min_max_scaler = MinMaxScaler()
        min_max_scaler.fit(df[[age]])
        joblib.dump(min_max_scaler, scaler_path + joblib_extension)
    df[age] = min_max_scaler.transform(df[[age]])
    return df

def transform_evidences_array(x):
    res = np.zeros(len(general_evidences))
    for i in range(len(x)):
        if x[i] == 1:
            evidence = evidences[i]
            generic_evidence = p.sub("", evidence)
            res[general_evidences_dict[generic_evidence]] = 1
    return res

def compute_differential_cosine_method(x, threshold):
    diff_conditions = {}
    generic_evidences_for_x = transform_evidences_array(x)
    for key, val in condition_embeddings.items():
        conditions_similarity = cosine_similarity(generic_evidences_for_x, val)
        if conditions_similarity > threshold:
            diff_conditions[key] = conditions_similarity
    return diff_conditions

def predict_differential(x):
    answer = []
    for item in x:
        diff_conditions_for_x = compute_differential_cosine_method(item[2:], 0.7)
        if len(diff_conditions_for_x) < 2:
            diff_conditions_for_x = compute_differential_cosine_method(item[2:], 0.2)
        list_conditions = [(key, val) for key, val in diff_conditions_for_x.items()]
        most_matching_conditions = sorted(list_conditions, key= lambda a: a[1], reverse=True)[:min(5, len(list_conditions))]
        total = sum([element[1] for element in most_matching_conditions])
        for i in range(len(most_matching_conditions)):
            most_matching_conditions[i] = (most_matching_conditions[i][0], most_matching_conditions[i][1] / total)
        answer.append(most_matching_conditions)
    return answer


def accuracy_differential(y, y_pred):
    y_len = len(y)
    y_pred_len = len(y_pred)
    if y_len != y_pred_len:
        return 0.0
    predicted_score = 0
    for i in range(y_len):
        predicted_set = set([element[0] for element in y_pred[i]])
        true_set = set()
        for j in range(len(labels)):
            if y[i][j] > 0:
                true_set.add(labels[j])
        total_predicted = 0
        for cond in predicted_set:
            if cond in true_set:
                total_predicted += 1
        if len(predicted_set) > 0:
            predicted_score += (total_predicted / len(predicted_set))
    
    return predicted_score / y_len


def test_differential_diagnosis(path):
    df = read_test_csv(path)
    X = df[features].values
    Y = df[labels].values
    y_pred = predict_differential(X)
    print(accuracy_differential(Y, y_pred))


if __name__ == '__main__':
    test_differential_diagnosis(test_path)
    test_differential_diagnosis(validate_path)