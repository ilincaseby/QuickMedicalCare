import 'package:flutter/material.dart';
import '../widgets/login_header.dart';
import '../widgets/register_form.dart';
import '../widgets/change_data_form.dart';
import '../widgets/diagnosis_widget.dart';
import '../widgets/login_footer.dart';

class DiagnosisPage extends StatelessWidget {
  const DiagnosisPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [Color(0xFF63C3F0), Color(0xFF46A4D3)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
          ),
        ),
        width: double.infinity,
        padding: const EdgeInsets.symmetric(horizontal: 32),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Spacer(),
            const LoginHeader(),
            const SizedBox(height: 40),
            DiagnosisWidget(
              diagnosisText: "Asthma",
              // differentialDiagnosis: [
              //   MapEntry("COPD", 0.25),
              //   MapEntry("Bronchitis", 0.15),
              //   MapEntry("Pneumonia", 0.10),
              // ],
            ),
            const Spacer(),
            // const LoginFooter(),
            // const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}
