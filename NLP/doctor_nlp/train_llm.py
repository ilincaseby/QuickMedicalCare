from transformers import AutoModelForCausalLM, AutoTokenizer
import numpy as np
import pandas as pd
import json
from datasets import Dataset

import torch

device = torch.device("mps" if torch.backends.mps.is_available() else "cpu")

model_name = "Qwen/Qwen2.5-3B"
tokenizer = AutoTokenizer.from_pretrained(model_name)

model = AutoModelForCausalLM.from_pretrained(
    model_name,
    torch_dtype=torch.float16
)

model.to(device)

def dataset_for_training():
    data = {}
    with open("release_evidences.json", "r") as file:
        data = json.load(file)
    examples = []
    for key, val in data.items():
        if len(val['possible-values']) == 0:
            examples.append(f'Probable evidence: {key}\nQuestion that answer will respond to: {val['question_en']}')
        else:
            if len(val['possible-values']) > 0 and len(val['value_meaning']) == 0:
                possible_values = val['possible-values']
                for possible_value in possible_values:
                    examples.append(f'Evidence: {key}@{possible_value}\nQuestion that suits evidence: {val['question_en']}\nThe value on scale(after \'@\\): {possible_value}')
            else:
                possible_values = val['possible-values']
                for possible_value in possible_values:
                    examples.append(f'Evidence: {key}@{possible_value}\nQuestion that suits evidence: {val['question_en']}\nValue meaning(after \'@\\): {val['value_meaning'][possible_value]}')
    dataset = Dataset.from_dict({"text": examples})
    return dataset

def tokenize(dataset_item):
    return tokenizer(
        dataset_item["text"],
        truncation=True,
        padding="max_length",
        max_length=512,
    )


def tokenize_dataset(dataset):
    return dataset.map(tokenize, batched=True)

from transformers import DataCollatorForLanguageModeling, Trainer, TrainingArguments

data_collator = DataCollatorForLanguageModeling(
    tokenizer=tokenizer,
    mlm=False
)

training_args = TrainingArguments(
    output_dir="./qwen-finetuned",
    per_device_train_batch_size=1,
    num_train_epochs=3,
    logging_steps=10,
    save_strategy="epoch",
    push_to_hub=False
)

trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=tokenize_dataset(dataset_for_training()),
    tokenizer=tokenizer,
    data_collator=data_collator
)

trainer.train()

model.save_pretrained("./qwen-finetuned")
tokenizer.save_pretrained("./qwen-finetuned")

"""
prompt = "Salut! Ce pot face pentru tine azi?"
inputs = tokenizer(prompt, return_tensors="pt").to(device)

outputs = model.generate(**inputs, max_new_tokens=50)
print(tokenizer.decode(outputs[0], skip_special_tokens=True))
"""