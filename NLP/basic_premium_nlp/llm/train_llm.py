from transformers import AutoModelForCausalLM, AutoTokenizer
import numpy as np
import pandas as pd
from datasets import Dataset

import torch

device = torch.device("mps" if torch.backends.mps.is_available() else "cpu")

model_name = "distilgpt2"
tokenizer = AutoTokenizer.from_pretrained(model_name)


model = AutoModelForCausalLM.from_pretrained(
    model_name,
    torch_dtype=torch.float16
)

model.to(device)

print("obtained tokenizer and model")

def dataset_for_training():
    df = pd.read_csv('description_labelling.csv')
    examples = []
    for _, row in df.iterrows():
        symptom = row.iloc[-1]
        description = row.iloc[0]
        examples.append(f'Text: {description}\nOutput: {symptom}\n')
    dataset = Dataset.from_dict({"text": examples})
    return dataset

def tokenize(dataset_item):
    tokenizer.pad_token = tokenizer.eos_token
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
    output_dir="./distilgpt2",
    per_device_train_batch_size=8,
    num_train_epochs=1,
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

print("obtained trainer after setting training args")

trainer.train()

print("model trained")

model.save_pretrained("./distilgpt2")
tokenizer.save_pretrained("./distilgpt2")

print("model saved")

"""
prompt = "Salut! Ce pot face pentru tine azi?"
inputs = tokenizer(prompt, return_tensors="pt").to(device)

outputs = model.generate(**inputs, max_new_tokens=50)
print(tokenizer.decode(outputs[0], skip_special_tokens=True))
"""