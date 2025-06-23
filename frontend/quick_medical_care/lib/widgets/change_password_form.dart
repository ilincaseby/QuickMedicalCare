import 'package:flutter/material.dart';

class ChangePasswordForm extends StatefulWidget {
  const ChangePasswordForm({super.key});

  @override
  State<ChangePasswordForm> createState() => _ChangePasswordFormState();
}

class _ChangePasswordFormState extends State<ChangePasswordForm> {
  final TextEditingController _oldPasswordController = TextEditingController();
  final TextEditingController _newPasswordController = TextEditingController();

  @override
  void dispose() {
    _oldPasswordController.dispose();
    _newPasswordController.dispose();
    super.dispose();
  }

  void changePassword() {
    final oldPassword = _oldPasswordController.text.trim();
    final newPassword = _newPasswordController.text.trim();

    // TODO: implementează logica de schimbare a parolei aici
    print('Old Password: $oldPassword');
    print('New Password: $newPassword');
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TextField(
          controller: _oldPasswordController,
          obscureText: true,
          decoration: const InputDecoration(
            hintText: 'Old Password',
            border: OutlineInputBorder(),
            contentPadding: EdgeInsets.all(16),
          ),
        ),
        const SizedBox(height: 16),
        TextField(
          controller: _newPasswordController,
          obscureText: true,
          decoration: const InputDecoration(
            hintText: 'New Password',
            border: OutlineInputBorder(),
            contentPadding: EdgeInsets.all(16),
          ),
        ),
        const SizedBox(height: 24),
        ElevatedButton(
          onPressed: changePassword,
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.teal.shade600,
            padding: const EdgeInsets.symmetric(horizontal: 60, vertical: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(30),
            ),
          ),
          child: const Text(
            'Change Password',
            style: TextStyle(fontSize: 18),
          ),
        ),
      ],
    );
  }
}
