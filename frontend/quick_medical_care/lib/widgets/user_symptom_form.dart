import 'package:flutter/material.dart';

class SymptomForm extends StatefulWidget {
  const SymptomForm({super.key});

  @override
  State<SymptomForm> createState() => _SymptomFormState();
}

class _SymptomFormState extends State<SymptomForm> {
  final TextEditingController _symptomController = TextEditingController();

  @override
  void dispose() {
    _symptomController.dispose();
    super.dispose();
  }

  void _handleDiagnosis() {
    final symptoms = _symptomController.text.trim();
    // Aici pui logica ta pentru diagnostic
    // Ex: trimite la backend, sau accesează AI-ul tău
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TextField(
          controller: _symptomController,
          maxLines: 5,
          decoration: InputDecoration(
            hintText: 'Describe your symptoms',
            filled: true,
            fillColor: Colors.transparent,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: BorderSide.none,
            ),
            contentPadding: const EdgeInsets.all(16),
          ),
        ),
        const SizedBox(height: 24),
        ElevatedButton(
          onPressed: _handleDiagnosis,
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.teal.shade600,
            padding: const EdgeInsets.symmetric(horizontal: 60, vertical: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(30),
            ),
          ),
          child: const Text(
            'Get Diagnosis',
            style: TextStyle(fontSize: 18),
          ),
        ),
      ],
    );
  }
}
