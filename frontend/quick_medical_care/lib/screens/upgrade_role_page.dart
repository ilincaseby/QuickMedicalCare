import 'package:flutter/material.dart';
import 'package:quick_medical_care/widgets/login_header.dart';
import 'package:quick_medical_care/widgets/upgrade_role_center.dart';

class UpgradeRolePage extends StatelessWidget {
  const UpgradeRolePage({super.key});

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
            UpgradeRoleCenter(),
            const Spacer(),
            // const LoginFooter(),
            // const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}
