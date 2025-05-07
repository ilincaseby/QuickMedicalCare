import json
import ast
from platform import release

sex = 'SEX'
age = 'AGE'
m = 'M'
f = 'F'
scaler_path = 'saved_models/age_scaler'
saved_models_dir = 'saved_models'
dir_preprocessed_dataset = 'preprocessed_dataset'
joblib_extension = '.joblib'

def obtain_evidences():
    with open("huggingface_dataset/ddxplus/release_evidences.json", "r") as file:
        release_evidences = json.load(file)
    set_evidences = []
    for key, value in release_evidences.items():
        if len(value['possible-values']) > 0:
            for val in value['possible-values']:
                set_evidences.append(f'{key}_@_{val}')
        else:
            set_evidences.append(f'{key}')
    return set_evidences

def obtain_evidences_as_dict():
    evidences = obtain_evidences()
    evidences_dict = {}
    for i in range(len(evidences)):
        evidences_dict[evidences[i]] = i
    return evidences_dict

def obtain_conditions():
    with open("huggingface_dataset/ddxplus/release_conditions.json") as file:
        release_conditions = json.load(file)
    return [a for a in release_conditions.keys()]

def obtain_conditions_as_dict():
    conditions = obtain_conditions()
    conditions_dict = {}
    for i in range(len(conditions)):
        conditions_dict[conditions[i]] = i
    return conditions_dict

def obtain_conditions_as_dict_index():
    conditions = obtain_conditions()
    conditions_dict = {}
    for i in range(len(conditions)):
        conditions_dict[i] = conditions[i]
    return conditions_dict

def obtain_general_evidences():
    with open("huggingface_dataset/ddxplus/release_evidences.json", "r") as file:
        release_evidences = json.load(file)
    set_evidences = []
    for key, _ in release_evidences.items():
        set_evidences.append(f'{key}')
    return set_evidences

def convert_to_dict(dataframe, dictionary, set_evidences, set_conditions):
    for _, row in dataframe.iterrows():
        dict_record = {}
        dict_record[age] = row[age]
        dict_record[sex] = row[sex]
        evidences_string = row['EVIDENCES']
        evidences_parts = ast.literal_eval(evidences_string)
        differential_diagnosis_string = row['DIFFERENTIAL_DIAGNOSIS']
        differential_diagnosis_parts = ast.literal_eval(differential_diagnosis_string)
        for evidence in evidences_parts:
            dict_record[evidence] = 1
        for evidence in set_evidences:
            if evidence not in dict_record:
                dict_record[evidence] = 0
        length_min_probs = min(len(differential_diagnosis_parts), 3)
        sum_min_probs = sum(a[1] for a in differential_diagnosis_parts[:length_min_probs])
        for diagnosis_prob in differential_diagnosis_parts[:length_min_probs]:
            dict_record[diagnosis_prob[0]] = diagnosis_prob[1] / sum_min_probs
        for diagnosis in set_conditions:
            if diagnosis not in dict_record:
                dict_record[diagnosis] = 0
        for key, val in dict_record.items():
            dictionary[key].append(val)

def convert_to_dict_non_diff(dataframe, dictionary, set_evidences, set_conditions):
    for _, row in dataframe.iterrows():
        dict_record = {}
        dict_record[age] = row[age]
        dict_record[sex] = row[sex]
        evidences_string = row['EVIDENCES']
        evidences_parts = ast.literal_eval(evidences_string)
        differential_diagnosis_string = row['PATHOLOGY']
        for evidence in evidences_parts:
            dict_record[evidence] = 1
        for evidence in set_evidences:
            if evidence not in dict_record:
                dict_record[evidence] = 0
        dict_record[differential_diagnosis_string] = 1
        for diagnosis in set_conditions:
            if diagnosis not in dict_record:
                dict_record[diagnosis] = 0
        for key, val in dict_record.items():
            dictionary[key].append(val)

def introduce_conditions(conditions, dict_for_insert):
    for condition in conditions:
        dict_for_insert[condition] = []

def introduce_evidences(evidences, dict_for_insert):
    for evidence in evidences:
        dict_for_insert[evidence] = []

def get_json_from_file(filepath):
    with open(filepath, "r") as file:
        release_evidences = json.load(file)
    return release_evidences