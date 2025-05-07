import pandas as pd
import numpy as np
from utils_data import obtain_conditions
from utils_data import obtain_general_evidences
from sklearn.preprocessing import MinMaxScaler
import ast
import re
import json
import os
import joblib
from utils_data import scaler_path, saved_models_dir

# Load conditions and evidences
conditions = obtain_conditions()
general_evidences = obtain_general_evidences()

# Load dataset
df = pd.read_csv("huggingface_dataset/ddxplus/train.csv")
df['SEX'] = df['SEX'].map({'M': 1, 'F': 0})
if not os.path.isdir(saved_models_dir):
    os.makedirs(saved_models_dir)
if os.path.exists(scaler_path + ".joblib"):
    min_max_scaler = joblib.load(scaler_path + ".joblib")
else:
    min_max_scaler = MinMaxScaler()
    min_max_scaler.fit(df[['AGE']])
    joblib.dump(min_max_scaler, scaler_path + ".joblib")

df['AGE'] = min_max_scaler.transform(df[['AGE']])


# Initialize condition vector dictionary
condition_vector_dict = {condition: (np.zeros(len(general_evidences)), 0) for condition in conditions}

# Compile regex pattern for evidence processing
pattern = re.compile('_@_[a-zA-Z0-9_]*')

# Create a dictionary for evidence indexing
evidence_dict = {general_evidences[i]: i for i in range(len(general_evidences))}

# Process each row in the dataset
for _, data_s in df.iterrows():
    arr = np.zeros(len(general_evidences))
    evidences_string = data_s['EVIDENCES']
    evidences_parts = [pattern.sub('', aux_evidence) for aux_evidence in ast.literal_eval(evidences_string)]
    
    # Update evidence array
    for evidence in evidences_parts:
        if evidence in evidence_dict:
            arr[evidence_dict[evidence]] = 1
    
    # Update condition vector dictionary
    pathology = data_s['PATHOLOGY']
    if pathology in condition_vector_dict:
        embedding, occ = condition_vector_dict[pathology]
        condition_vector_dict[pathology] = (embedding + arr, occ + 1)

# Normalize the embeddings
for key, val in condition_vector_dict.items():
    arr, occ = val
    if occ > 0:  # Avoid division by zero
        condition_vector_dict[key] = arr / occ
    else:
        condition_vector_dict[key] = arr

# Serialize the condition vector dictionary to JSON
condition_vector_dict_serializable = {
    key: value.tolist() for key, value in condition_vector_dict.items()
}

# Save to file
with open("saved_models/condition_embeddings.json", "w") as f:
    json.dump(condition_vector_dict_serializable, f)