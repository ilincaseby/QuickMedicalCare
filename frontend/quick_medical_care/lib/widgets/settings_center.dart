import 'package:flutter/material.dart';
import 'package:quick_medical_care/screens/change_data_page.dart';
import 'package:quick_medical_care/screens/change_password_page.dart';

class SettingCenter extends StatefulWidget {
  const SettingCenter({
    super.key,
  });

  @override
  State<SettingCenter> createState() => _SettingCenterState();
}

class _SettingCenterState extends State<SettingCenter> {
  void _handleChangePersonalData() {
    Navigator.push(context,
        MaterialPageRoute(builder: (context) => const ChangeDataPage()));
  }

  void _handleChangePassword() {
    Navigator.push(context,
        MaterialPageRoute(builder: (context) => const ChangePasswordPage()));
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ElevatedButton(
          onPressed: _handleChangePersonalData,
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.teal.shade600,
            padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(30),
            ),
          ),
          child: const Text(
            'Change Personal Data',
            style: TextStyle(fontSize: 18),
          ),
        ),
        const SizedBox(height: 20),
        ElevatedButton(
          onPressed: _handleChangePassword,
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.teal.shade600,
            padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 12),
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
