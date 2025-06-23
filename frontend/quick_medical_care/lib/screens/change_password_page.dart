import 'package:flutter/material.dart';
import '../widgets/login_header.dart';
import '../widgets/register_form.dart';
import '../widgets/change_password_form.dart';
import '../widgets/login_footer.dart';

class ChangePasswordPage extends StatelessWidget {
  const ChangePasswordPage({super.key});

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
            ChangePasswordForm(),
            const Spacer(),
            // const LoginFooter(),
            // const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}
