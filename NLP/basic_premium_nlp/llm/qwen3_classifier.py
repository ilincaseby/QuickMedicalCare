from ollama import chat 
from ollama import ChatResponse
import pandas as pd
import re
df = pd.read_csv('symptom_description.csv')
symptoms = [row[len(row) - 1] for _, row in df.iterrows()]

def qwen3_classifier(message: str) -> str:
    # response = chat(model="qwen2.5:3b", messages=[
    #     {
    #         'role': 'user',
    #         'content': f'Based on the provided list of symtoms {symptoms}, I want to classify, with one or more of the classes present in the list, the following sentence: \"{message}\", without any words, just a plain list the predictions. Here are some predefined sentences for fine-tuning:{df}'
    #     }
    # ])
    response = chat(model="qwen3:4b", messages=[
    {
        "role": "user",
        "content": (
            f"You are an AI model trained to classify medical symptoms. "
            f"Given a list of possible classes {symptoms} and a new sentence: \"{message}\", "
            f"predict the relevant classes from the list. Only return a JSON array with the predicted class names, "
            f"without any explanation, formatting, or extra text.\n"
            f"If a symptom from the list is relevant, return it EXACTLY as written in the list. Do not change a single character. For example, do NOT write \"swollen_lymph_nodes\" if the list says \"swelled_lymph_nodes\".\n"
            f"Here are some example training sentences and their labels for context:\n{df}\n\n"
            f"Your answer must be a valid Python list of strings."
        )
        # "content": (
        #     f"You are an AI model trained to classify medical symptoms.\n"
        #     f"You are given:\n"
        #     f" - A fixed list of possible symptom class names: {symptoms}\n"
        #     f" - A new sentence: \"{message}\"\n"
        #     f"Your task is to predict the relevant class names **exactly as they appear in the list above**.\n"
        #     f"RULES:"
        #     f' - Return ONLY a raw Python list (e.g., ["symptom1", "symptom2"]) and NOTHING ELSE. Do not add explanations, formatting, or any other text.'
        #     f" - Do NOT rephrase, correct, change case, or modify the returned symptom class names in any way.\n"
        #     f" - Only include class names from the given list.\n"
        #     f' - Under no circumstances are you allowed to modify any symptom label from the list. If a symptom fits, you must return it **exactly as it appears in the list**, without changing even a single character. No replacements like "swollen" instead of "swelled", or any other variation. Just copy-paste from the list.'
        #     f" - Return ONLY a valid Python list of strings, with no explanation, formatting, or extra text.\n\n"
        #     f"Here are some example training sentences and their labels for context:\n{df}\n\n"
        #     f"Output format: [\"symptom1\", \"symptom2\"]"
        # )
    }
])
    return response.message.content

from flask import Flask, request, jsonify

if __name__=='__main__':
    app = Flask(__name__)
    @app.route('/classify', methods=['POST'])
    def classify():
        data = request.get_json(force=True)
        message = data['message']
        response = qwen3_classifier(message)
        index = response.index('</think>') + len('</think>')
        response = response[index:]
        symptoms = re.split(r'[\n \[\]\t,;]+', response)
        symptoms = [symptom.strip("'\"") for symptom in symptoms if symptom != '']

        return jsonify({'response': symptoms})

    app.run(host='0.0.0.0', port=5001)