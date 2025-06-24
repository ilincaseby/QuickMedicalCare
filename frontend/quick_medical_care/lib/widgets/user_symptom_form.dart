import 'package:flutter/material.dart';
import 'package:quick_medical_care/main.dart';
import 'package:quick_medical_care/screens/diagnosis_page.dart';
import '../utils/models.dart' as models;
import '../utils/http_request.dart' as reqClient;
import '../utils/secure_data.dart' as secureStorageClient;
import 'package:quick_medical_care/widgets/snack_bar_check.dart';
import 'package:quick_medical_care/widgets/snack_bar_error.dart';
import 'package:loader_overlay/loader_overlay.dart';

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

  String path = 'api/v1/prognosis/predict';

  //body message

  Future<models.RequestResponse> _requestDiagnosis(String message) async {
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
      },
    );
  }

  void _handleDiagnosis() async {
    final symptoms = _symptomController.text.trim();
    context.loaderOverlay.show();
    _requestDiagnosis(symptoms).then((response) {
      context.loaderOverlay.hide();
      if (response.statusCode == 200) {
        String diagnosisText = response.body!['response'] ?? '';
        Navigator.push(
            context,
            MaterialPageRoute(
                builder: (context) =>
                    DiagnosisPage(diagnosisText: diagnosisText)));
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
