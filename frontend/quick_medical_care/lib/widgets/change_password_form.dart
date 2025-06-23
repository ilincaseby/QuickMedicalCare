import 'package:flutter/material.dart';
import 'package:quick_medical_care/utils/secure_data.dart';
import 'package:quick_medical_care/widgets/snack_bar_check.dart';
import 'package:quick_medical_care/widgets/snack_bar_error.dart';
import '../utils/http_request.dart' as reqClient;
import '../utils/models.dart' as models;
import '../utils/secure_data.dart' as secureStorageClient;

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

  void changePassword() async {
    if (_oldPasswordController.text.isEmpty ||
        _newPasswordController.text.isEmpty) {
      showError(context, "Please complete fields");
      return;
    }

    var reqBody = {
      "oldPassword": _oldPasswordController.text,
      "password": _newPasswordController.text,
    };

    var antiCsrfToken = await secureStorageClient.get("antiCsrfToken");
    var accessToken = await secureStorageClient.get("access-token");
    reqClient
        .request(
      models.RequestMethod.put,
      "/api/v1/changeInfo/changePassword",
      headers: {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": antiCsrfToken,
        "Authorization": "Bearer ${accessToken ?? ''}",
      },
      body: reqBody,
      onlyText: true,
    )
        .then((response) {
      if (response.statusCode >= 200 && response.statusCode <= 299) {
        showCheck(context, "Password changed successfully");
        Navigator.pop(context);
        return;
      }
      String? errorMsg = response.errorBody;
      if (errorMsg == null || errorMsg.toString().isEmpty) {
        errorMsg = "Something went wrong";
      }
      showError(context, errorMsg);
    }).catchError((error) {
      showError(context, "Something went wrong");
    });
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
