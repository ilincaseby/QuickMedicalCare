import 'package:flutter/material.dart';
import 'form_styles.dart';
import 'package:quick_medical_care/widgets/snack_bar_error.dart';
import '../utils/models.dart' as models;
import '../utils/http_request.dart' as reqClient;
import '../utils/secure_data.dart' as secureStorageClient;
import 'package:loader_overlay/loader_overlay.dart';
import 'package:quick_medical_care/screens/diagnosis_page.dart';
import 'dart:math';

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

  String path = 'api/v1/prognosis/predict';

  Future<models.RequestResponse> _requestDiagnosis(
      String message, String sex, int age) async {
    var antiCsrfToken = await secureStorageClient.get("antiCsrfToken");
    var accessToken = await secureStorageClient.get("access-token");
    return reqClient.request(
      models.RequestMethod.post,
      path,
      headers: {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": antiCsrfToken,
        "Authorization": "Bearer ${accessToken ?? ''}",
      },
      body: {
        'message': message,
        'age': age,
        'sex': sex,
      },
    );
  }

  void _handleDiagnosis() {
    final int? age = int.tryParse(_ageController.text);
    if (age == null) {
      showError(context, "Age not completed");
      return;
    }
    final String sex = _selectedSex == 1 ? 'M' : 'F';
    final symptoms = _symptomController.text.trim();

    context.loaderOverlay.show();
    _requestDiagnosis(symptoms, sex, age).then((response) {
      context.loaderOverlay.hide();
      if (response.statusCode == 200) {
        String diagnosisText = response.body!['diagnosis'] ?? '';
        List<MapEntry<String, double>>? differentialDiagnosis;
        if ((response.body!["differential_diagnosis"] as List).isNotEmpty) {
          differentialDiagnosis = (response.body!["differential_diagnosis"]
                  as List)
              .map<MapEntry<String, double>>((aux) => MapEntry<String, double>(
                  aux[0] as String, (aux[1] as num).toDouble()))
              .toList();
          differentialDiagnosis.removeRange(
              min(3, differentialDiagnosis.length),
              differentialDiagnosis.length);
        }
        Navigator.push(
            context,
            MaterialPageRoute(
                builder: (context) => DiagnosisPage(
                      diagnosisText: diagnosisText,
                      differentialDiagnosis: differentialDiagnosis,
                    )));
        return;
      }
      if (response.statusCode == 404) {
        showError(context, 'User or personal data could not be found');
        return;
      }
      if (response.statusCode == 500) {
        showError(context, 'AI/ML models malfunction or not enough symptoms');
        return;
      }
    }).catchError((error) {
      showError(context, 'Something went wrong');
    });
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
