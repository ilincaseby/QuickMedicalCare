import 'package:flutter/material.dart';
import 'package:quick_medical_care/screens/register_page.dart';
import 'screens/login_page.dart';
import 'screens/user_homepage.dart';
import 'screens/settings_page.dart';
import 'screens/doctor_homepage.dart';
import 'screens/change_password_page.dart';
import 'screens/change_data_page.dart';
import 'screens/diagnosis_page.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: LoginPage(),
      debugShowCheckedModeBanner: false,
    );
  }
}
