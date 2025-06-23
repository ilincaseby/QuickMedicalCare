#!/bin/bash

source macos_venv/bin/activate

cd big_dataset
python app.py &
cd ..

cd small_dataset
python prognosis.py &
cd ..

