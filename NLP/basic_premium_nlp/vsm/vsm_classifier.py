from flask import Flask, request, jsonify
import pandas as pd
import numpy as np
from nltk import WordNetLemmatizer, word_tokenize
from nltk.corpus import stopwords
import nltk
from gensim.models import KeyedVectors
import string

app = Flask(__name__)

def import_vsm():
    vectorized_symptoms = np.load('vsm_dict.npy', allow_pickle=True).item()
    return vectorized_symptoms

def get_stopwords():
    nltk.download('stopwords')
    medical_stopwords_keep = [
        "not", "no", "without", "never", "none", "neither", "nor", "n\'t"
        "always", "constant", "sudden", "recent", "persistent", "chronic", "acute",
        "left", "right", "upper", "lower", "side", "part", "area", "region", "zone",
        "feel", "seem", "have", "experience", "report", "complain",
        "mild", "severe", "moderate", "intense", "sharp", "dull", "worsening", "improving",
        "in", "on", "at", "during", "of"
    ]
    medical_stopwords = [stopword for stopword in (stopwords.words('english') + ['\'s', ]) if stopword not in medical_stopwords_keep]
    return medical_stopwords

def get_tf_idf(sentences: list) -> dict:
    word_dict = {}
    word_document_occurence_dict = {}
    total_words = 0
    for sentence in sentences:
        set_words_in_document = set()
        for word in sentence.split():
            if word in model:
                total_words += 1
                if word in word_dict:
                    word_dict[word] += 1
                else:
                    word_dict[word] = 1
                set_words_in_document.add(word)
        for word in set_words_in_document:
            if word in word_document_occurence_dict:
                word_document_occurence_dict[word] += 1
            else:
                word_document_occurence_dict[word] = 1
    tf_dict = {word: (occurences / total_words) for word, occurences in word_dict.items()}
    idf_dict = {word: ((len(sentence) + 1) / (occurences + 1)) for word, occurences in word_document_occurence_dict.items()}
    tf_idf_dict = {word: (tf * idf_dict[word]) for word, tf in tf_dict.items()}
    return tf_idf_dict

def get_vectorized_symptom(sentences: list, model: KeyedVectors) -> np.ndarray:
    tf_idf_dict = get_tf_idf(sentences)
    symptom_vector = np.zeros(300)
    for sentence in sentences:
        sentence_vector = np.zeros(300)
        for word in sentence.split():
            if word in model:
                sentence_vector += model[word] * tf_idf_dict[word]
        symptom_vector += sentence_vector
    return symptom_vector / len(sentences)
    

def preprocess_description(description: str, wdl: WordNetLemmatizer):
    description = description.lower()
    description_tokens = word_tokenize(description, preserve_line=True)
    description_tokens = [token for token in description_tokens if token not in (string.punctuation+"'\’`\'\'``") or token in medical_stopwords]
    words = [wdl.lemmatize(token) for token in description_tokens]
    words = list(filter(lambda word: word not in medical_stopwords, words))
    whitespaces = [' ' for _ in range(len(words))]
    return ''.join([word + whitespace for word, whitespace in zip(words, whitespaces)])

def best_cosine_similarity(vectorized_symptoms: dict, sentence: str, model: KeyedVectors) -> str:
    sentence = preprocess_description(sentence, wdl)
    sentence_vector = get_vectorized_symptom([sentence], model)
    best_symptom = None
    max_cos = 0.0
    for key, val in vectorized_symptoms.items():
        cos = (np.dot(val, sentence_vector) / ((np.linalg.norm(val) * np.linalg.norm(sentence_vector))))
        if cos > max_cos and cos > 0.7:
            best_symptom = key
            max_cos = cos
    if best_symptom is None:
        return None
    return best_symptom

@app.route('/classify', methods=['POST'])
def classify():
    data = request.get_json(force=True)
    print(data)
    message = data['message']
    messages = message.split('.')
    response = []
    for msg in messages:
        res = best_cosine_similarity(vectorized_symptoms, msg, model)
        if res is not None:
            response.append(res)
    print(response)
    return jsonify({'response': response})

if __name__=='__main__':
    vectorized_symptoms = import_vsm()
    wdl = WordNetLemmatizer()
    medical_stopwords = get_stopwords()
    model = KeyedVectors.load_word2vec_format('GoogleNews-vectors-negative300.bin', binary=True)
    
    app.run(host='0.0.0.0', port=5002)