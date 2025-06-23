from ollama import chat
import ast

def build_multi_e_code_prompt(message: str, e_code_data: dict) -> str:
    e_code_sections = []
    for e_code, e_info in e_code_data.items():
        example = e_info["example"]
        v_codes = "\n".join(e_info["codes"])
        section = f"E_CODE: {e_code}\nMeaning: {example}\nAvailable V_CODEs:\n{v_codes}"
        e_code_sections.append(section)

    e_code_str = "\n\n".join(e_code_sections)

    return f"""
        You are a medical AI assistant trained to extract structured symptom information from patient descriptions.

        Each symptom type is identified by an E_CODE. Each E_CODE has specific possible values called V_CODEs.

        ---

        Your Task:
        1. For each E_CODE, check if it applies to the message.
        2. If it does, extract one or more matching V_CODEs.
        3. Output each match in **this exact format**: E_CODE_@_V_CODE
        - Example: E_01_@_V_09
        - DO NOT use any other format like E_CODE_E_01_V_09 (this is INVALID)
        - DO NOT include extra prefixes or suffixes.
        - DO NOT use just @ — it must be exactly "_@_"
        4. If nothing matches, return an empty list: []

        ---

        E_CODE definitions and values:
        {e_code_str}

        ---

        Input message:
        \"{message}\"

        ---

        Output:
        - List of strings formatted exactly like: ["E_CODE_@_V_CODE"]
        """

def build_prompt_localized(message, e_codes_dict, v_codes_list):
    e_code_str = "\n".join([f"{code} → \"{info['example']}\"" for code, info in e_codes_dict.items()])
    v_code_str = "\n".join(v_codes_list)

    return f"""
        You are a medical AI assistant trained to extract structured symptom information from patient descriptions.

        You are given:
        - A list of symptom types (E_CODEs), each with a short example that defines its meaning.
        - A shared list of body part locations (V_CODEs) to choose from.

        ---

        Task:
        1. Carefully read the input message.
        2. Check if **any** of the E_CODE examples clearly match the meaning of parts of the message.
        3. For **each matching E_CODE**, choose all relevant V_CODEs that describe **exactly** the mentioned body part locations.
        4. Only select a V_CODE if the message refers **explicitly and unambiguously** to that body part.
        - **Do not guess** or infer based on context.
        - **If the reference is not completely clear, skip that V_CODE.**
        5. If there is **no clear match** for an E_CODE, skip it entirely.
        6. Only use E_CODEs and V_CODEs from the lists below. Do not invent anything.

        ---

        E_CODEs and their semantic meaning (via example):
        {e_code_str}

        ---

        V_CODEs (shared across all E_CODEs):
        {v_code_str}

        ---

        Input message:
        \"{message}\"

        ---

        Output rules:
        - Format: ["E_CODE_@_V_CODE", ...]
        - IMPORTANT: Use **exactly** "_@_" (with underscores). This is required.
        - ✅ Correct example: ["E_55_@_V_27", "E_133_@_V_163"]
        - ❌ Incorrect formats: ["E_55@V_27"], ["E_55-V_27"], ["E_CODE_E_55_V_27"]
        - Only include E_CODE_@_V_CODE pairs if both match clearly.
        - If nothing matches, return: []
        - Do not include explanations, comments, or any other text. Just the list.
        """

def build_prompt_w_scales(message: str, symptoms_with_scales: dict):
    return f"""
        You are a medical AI trained to detect and quantify symptom severity from natural language.\n\n"
        Input sentence: \"{message}\"\n\n
        You are given a list of symptom codes with associated questions and scales (0 to 10):\n
        {symptoms_with_scales}\n\n
        Your job:\n
        1. Identify which symptoms from the list are explicitly or semantically mentioned in the sentence.\n
        2. Estimate the intensity of each symptom on the 0–10 scale, based on words like 'mild', 'severe', etc.\n
        3. Return only clearly expressed symptoms.\n\n
        Output format:\n
        A valid Python list of strings, where each string is in the format: 'E_CODE_@_VALUE', for example: [\"E_136_@_9\", \"E_56_@_7\"]\n\n
        Rules:\n
        - DO NOT invent symptoms.\n
        - DO NOT include explanations.\n
        - DO NOT output anything other than the list.\n
        - ONLY include symptoms from the given dictionary.\n
        - If no matching symptom is present, return an empty list: []
        """

def build_prompt_single_value(message: str, symptoms_codes: list, batch: dict):
    return f"""
        You are a strict symptom classifier.\n
        You are given:\n- A sentence describing symptoms: \"{message}\"\n
        A list of valid symptom codes: {symptoms_codes}\n\n
        Your job is to return a Python list of symptom codes that are clearly and directly indicated in the sentence.\n
        Do NOT infer or guess the cause of the symptoms.\n
        Do NOT include codes that are not explicitly or semantically mentioned.\n
        You ARE allowed to match equivalent everyday phrases, e.g., 'high temperature' → 'fever', 'throwing up' → 'vomiting'.\n
        Only return the most direct and clearly stated symptoms you are very sure about.\n\n
        Here are some labeled examples:\n{batch}\n\n
        Output a valid Python list of symptom codes, and nothing else.
        """

def llm_classify_on_prompt(message: str) -> str:
    response = chat(model="qwen3:4b", messages=[
        {
            'role': 'user',
            'content': message         
        }
    ])
    return response.message.content

def get_symptoms(message: str):
    llm_result = llm_classify_on_prompt(message)
    after_think = llm_result.split("</think>")[-1].strip()
    try:
        result_list = ast.literal_eval(after_think)
        return result_list
    except Exception as e:
        return []