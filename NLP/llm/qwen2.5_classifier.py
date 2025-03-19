from ollama import chat 
from ollama import ChatResponse
import pandas as pd
import re
df = pd.read_csv('symptom_description.csv')
symptoms = [row[len(row) - 1] for _, row in df.iterrows()]
def qwen2_5_classifier(message: str) -> str:
    response = chat(model="qwen2.5:3b", messages=[
        {
            'role': 'user',
            'content': f'Based on the provided list of symtoms {symptoms}, I want to classify, with one or more of the classes present in the list, the following sentence: \"{message}\", without any words, just a plain list the predictions. Here are some predefined sentences for fine-tuning:{df}'
        }
    ])
    return response.message.content

from flask import Flask, request, jsonify

if __name__=='__main__':
    app = Flask(__name__)
    @app.route('/classify', methods=['GET'])
    def classify():
        data = request.get_json(force=True)
        message = data['message']
        response = qwen2_5_classifier(message)
        symptoms = re.split(r'[\n \[\]\t,;]+', response)
        symptoms = [symptom.strip("'\"") for symptom in symptoms if symptom != '']
        return jsonify({'response': symptoms})

    app.run(port=5001)