#!/bin/bash

source NLP/venv/bin/activate
cd NLP/vsm
python vsm_classifier.py
cd ../llm
python qwen2.5_classifier.py
cd ../..
deactivate