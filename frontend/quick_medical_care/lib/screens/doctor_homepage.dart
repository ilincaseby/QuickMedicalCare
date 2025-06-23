import 'package:flutter/material.dart';
import '../widgets/login_header.dart';
import '../widgets/doctor_symptom_form.dart';
import '../widgets/settings_footer.dart';
import '../utils/secure_data.dart' as secureStorageClient;
import 'package:quick_medical_care/screens/settings_page.dart';
import 'package:quick_medical_care/screens/login_page.dart';

class DoctorHomepage extends StatelessWidget {
  const DoctorHomepage({super.key});

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
            DoctorSymptomForm(),
            const Spacer(),
            SettingsLogoutButtons(
              onSettingsPressed: () {
                Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => const SettingsPage()));
              },
              onLogoutPressed: () {
                secureStorageClient.deleteAll();
                Navigator.pushAndRemoveUntil(
                    context,
                    MaterialPageRoute(builder: (context) => const LoginPage()),
                    (route) => false);
              },
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}
