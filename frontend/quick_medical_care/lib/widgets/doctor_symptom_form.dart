import 'package:flutter/material.dart';
import 'form_styles.dart'; // asigură-te că e calea corectă

class DoctorSymptomForm extends StatefulWidget {
  const DoctorSymptomForm({super.key});

  @override
  State<DoctorSymptomForm> createState() => _DoctorSymptomFormState();
}

class _DoctorSymptomFormState extends State<DoctorSymptomForm> {
  final TextEditingController _ageController = TextEditingController();
  final TextEditingController _symptomController = TextEditingController();
  int? _selectedSex; // 1 = Male, 0 = Female

  @override
  void dispose() {
    _ageController.dispose();
    _symptomController.dispose();
    super.dispose();
  }

  void _handleDiagnosis() {
    final age = _ageController.text.trim();
    final symptoms = _symptomController.text.trim();

    // Afișare pentru test
    print("Symptoms: $symptoms");
    print("Age: $age");
    print("Sex: ${_selectedSex == 1 ? 'Male' : 'Female'} ($_selectedSex)");
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TextField(
          controller: _symptomController,
          maxLines: 5,
          decoration: InputDecoration(
            hintText: "Describe patient's symptoms",
            filled: true,
            fillColor: Colors.transparent,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            contentPadding: const EdgeInsets.all(16),
          ),
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            Expanded(
              flex: 2,
              child: TextField(
                controller: _ageController,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(
                  hintText: 'Age',
                  border: OutlineInputBorder(),
                  contentPadding:
                      EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                ),
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              flex: 3,
              child: DropdownButtonFormField<int>(
                value: _selectedSex,
                items: const [
                  DropdownMenuItem(value: 1, child: Text('Male')),
                  DropdownMenuItem(value: 0, child: Text('Female')),
                ],
                onChanged: (value) => setState(() => _selectedSex = value ?? 0),
                decoration: customInputDecoration(labelText: 'Sex'),
                // decoration: const InputDecoration(
                //   hintText: 'Sex',
                //   border: OutlineInputBorder(),
                //   contentPadding:
                //       EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                // ),
              ),
            ),
          ],
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
