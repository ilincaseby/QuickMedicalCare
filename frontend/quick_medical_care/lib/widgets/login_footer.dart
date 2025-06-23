import 'package:flutter/material.dart';

class LoginFooter extends StatelessWidget {
  const LoginFooter({super.key});

  @override
  Widget build(BuildContext context) {
    return Align(
      alignment: Alignment.centerRight, // mută totul mai spre dreapta
      child: Padding(
        padding: const EdgeInsets.only(right: 16.0, bottom: 24),
        child: Row(
          mainAxisSize:
              MainAxisSize.min, // pentru ca row-ul să nu ocupe tot spațiul
          children: [
            const Text(
              'Powered by',
              style: TextStyle(color: Colors.black54, fontSize: 16),
            ),
            const SizedBox(width: 8),
            Image.asset(
              'assets/ai_icon_f.png',
              height: 80, // mai mare decât 30
            ),
          ],
        ),
      ),
    );
  }
}
