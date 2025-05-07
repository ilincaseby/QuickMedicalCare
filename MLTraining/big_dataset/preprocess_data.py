from genericpath import isdir
from tarfile import data_filter
import pandas as pd
import os
import utils_data
from utils_data import dir_preprocessed_dataset

evidences_path = "huggingface_dataset/ddxplus/release_evidences.json"
conditions_path = "huggingface_dataset/ddxplus/release_conditions.json"
train_dataset_path = "huggingface_dataset/ddxplus/train.csv"
test_dataset_path = "huggingface_dataset/ddxplus/test.csv"
validate_dataset_path = "huggingface_dataset/ddxplus/validate.csv"
train_diff_preprocessed_path = "preprocessed_dataset/preprocessed_train.csv"
test_diff_preprocessed_path = "preprocessed_dataset/preprocessed_test.csv"
validate_diff_preprocessed_path = "preprocessed_dataset/preprocessed_validate.csv"
train_non_diff_preprocessed_path = "preprocessed_dataset/preprocessed_train_non_diff.csv"
test_non_diff_preprocessed_path = "preprocessed_dataset/preprocessed_test_non_diff.csv"
validate_non_diff_preprocessed_path = "preprocessed_dataset/preprocessed_validate_non_diff.csv"



dict_paths_non_diff = {'train': (train_dataset_path, train_non_diff_preprocessed_path),
                       'test': (test_dataset_path, test_non_diff_preprocessed_path),
                       'validate': (validate_dataset_path, validate_non_diff_preprocessed_path)}

dict_paths_diff = {'train': (train_dataset_path, train_diff_preprocessed_path),
                       'test': (test_dataset_path, test_diff_preprocessed_path),
                       'validate': (validate_dataset_path, validate_diff_preprocessed_path)}

def obtain_evidence_condition_json_list():
    evidences_dict = utils_data.get_json_from_file(evidences_path)
    conditions_dict = utils_data.get_json_from_file(conditions_path)
    conditions = [cond for cond in conditions_dict.keys()]
    evidences = []
    for key, value in evidences_dict.items():
        if len(value['possible-values']) > 0:
            for val in value['possible-values']:
                evidences.append(f'{key}_@_{val}')
        else:
            evidences.append(f'{key}')
    return conditions, evidences

def obtain_df_from_csv(dataset_path):
    df = pd.read_csv(dataset_path)
    df['AGE'] = df['AGE'].fillna(df['AGE'].median())
    df['SEX'] = df['SEX'].fillna(df['SEX'].mode()[0])
    return df

def obtain_dictionary(evidences, conditions):
    dict_data = {'AGE': [], 'SEX': []}
    utils_data.introduce_evidences(evidences, dict_data)
    utils_data.introduce_conditions(conditions, dict_data)
    return dict_data

def conds_evidences_data_df_getter(dataset_path):
    conditions, evidences = obtain_evidence_condition_json_list()
    dict_data = obtain_dictionary(evidences, conditions)
    df = obtain_df_from_csv(dataset_path)
    return conditions, evidences, dict_data, df

def preprocess_data_diff(dataset_path, dataset_destination_path):
    conditions, evidences, dict_data, df = conds_evidences_data_df_getter(dataset_path)
    utils_data.convert_to_dict(df, dict_data, evidences, conditions)
    pd.DataFrame(dict_data).to_csv(dataset_destination_path)

def preprocess_data_non_diff(dataset_path, dataset_destination_path):
    conditions, evidences, dict_data, df = conds_evidences_data_df_getter(dataset_path)
    utils_data.convert_to_dict_non_diff(df, dict_data, evidences, conditions)
    pd.DataFrame(dict_data).to_csv(dataset_destination_path)

if __name__ == "__main__":
    if not os.path.isdir(dir_preprocessed_dataset):
        os.makedirs(dir_preprocessed_dataset)
    diff = os.getenv('DIFF', 'False').lower() == 'true'
    dataset = os.getenv(key='DATASET', default='train')
    print(f"Preprocess begins for {dataset} in {diff} format...")
    if diff is True:
        preprocess_data_diff(dict_paths_diff[dataset][0], dict_paths_diff[dataset][1])
    else:
        preprocess_data_non_diff(dict_paths_non_diff[dataset][0], dict_paths_non_diff[dataset][1])
    print("Preprocess done")

