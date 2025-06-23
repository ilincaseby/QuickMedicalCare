#!/bin/bash

source venv/bin/activate

cd basic_premium_nlp/llm
python qwen3_classifier.py &

cd ../vsm
python vsm_classifier.py &

cd ../../doctor_nlp
python flask_classifier.py &

cd ..
