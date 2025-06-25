from flask import Flask, request, jsonify

import json
import utils

with open("evidences/release_evidences_simplified_no_values_examples.json", "r") as file:
        no_values_data = json.load(file)

with open("evidences/release_evidences_simplified_no_value_meaning_examples.json", "r") as file:
    no_value_meaning_data = json.load(file)

with open("evidences/release_evidences_simplified_value_meaning_examples.json", "r") as file:
    multiple_values_data = json.load(file)

localization_based_codes = ['E_55', 'E_57', 'E_133', 'E_152']

localization_values = {code: multiple_values_data[code] for code in localization_based_codes}

localization_v_codes = multiple_values_data[localization_based_codes[0]]['codes']

no_location_multiple_values_e_codes = ['E_54', 'E_130', 'E_135', 'E_131', 'E_204']

no_location_multiple_values_values = {code: multiple_values_data[code] for code in no_location_multiple_values_e_codes}

app = Flask(__name__)

@app.route('/classify', methods=['POST'])
def classify():
    data = request.get_json(force=True)
    message = data['message']
    # response = ["E_53","E_97","E_65","E_9","E_88","E_151","E_212","E_175","E_20","E_56_@_7","E_152_@_V_26"]
    response = []
    no_values_data_keys_list = list(no_values_data.keys())
    n = int(len(no_values_data_keys_list) / 50)
    for i in range(n):
        start = i * 50
        stop = min((i + 1) * 50, len(no_values_data_keys_list))
        values_loop = no_values_data_keys_list[start: stop]
        batch = {key: no_values_data[key] for key in values_loop}
        prompt = utils.build_prompt_single_value(message, values_loop, batch)
        symptoms = utils.get_symptoms(prompt)
        response += symptoms
    prompt = utils.build_prompt_w_scales(message, no_value_meaning_data)
    symptoms = utils.get_symptoms(prompt)
    response += symptoms
    prompt = utils.build_prompt_localized(message, localization_values, localization_v_codes)
    symptoms = utils.get_symptoms(prompt)
    response += symptoms
    prompt = utils.build_multi_e_code_prompt(message, no_location_multiple_values_values)
    symptoms = utils.get_symptoms(prompt)
    response += symptoms
    return jsonify({"response": response})



if __name__ == '__main__':
    with open("evidences/release_evidences_simplified_no_values_examples.json", "r") as file:
        no_values_data = json.load(file)

    with open("evidences/release_evidences_simplified_no_value_meaning_examples.json", "r") as file:
        no_value_meaning_data = json.load(file)

    with open("evidences/release_evidences_simplified_value_meaning_examples.json", "r") as file:
        multiple_values_data = json.load(file)

    app.run(host='0.0.0.0', port=5003)