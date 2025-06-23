from transformers import AutoModelForCausalLM, AutoTokenizer
import torch

# Alege device-ul (MPS pentru Mac)
device = torch.device("mps" if torch.backends.mps.is_available() else "cpu")

# Încarcă modelul fine-tunat și tokenizer-ul
model_path = "./distilgpt2"  # sau calea ta
tokenizer = AutoTokenizer.from_pretrained(model_path)
model = AutoModelForCausalLM.from_pretrained(model_path).to(device)

# Setează pad_token (obligatoriu la GPT-uri distilate)
tokenizer.pad_token = tokenizer.eos_token

# ---------- INPUT ----------
prompt = "how are you?"

# Tokenizare + generare
inputs = tokenizer(prompt, return_tensors="pt").to(device)
outputs = model.generate(**inputs, max_new_tokens=50)

# Decode + afișare rezultat
generated_text = tokenizer.decode(outputs[0], skip_special_tokens=True)
print("\n🧠 Output generat:\n")
print(generated_text)