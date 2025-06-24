import 'package:flutter/material.dart';
import 'package:quick_medical_care/screens/change_data_page.dart';
import 'package:quick_medical_care/screens/change_password_page.dart';
import 'package:quick_medical_care/screens/doctor_homepage.dart';
import 'package:quick_medical_care/screens/user_homepage.dart';
import 'package:quick_medical_care/widgets/snack_bar_check.dart';
import 'package:quick_medical_care/widgets/snack_bar_error.dart';
import '../utils/models.dart' as models;
import '../utils/http_request.dart' as reqClient;
import '../utils/secure_data.dart' as secureStorageClient;

class UpgradeRoleCenter extends StatefulWidget {
  const UpgradeRoleCenter({
    super.key,
  });

  @override
  State<UpgradeRoleCenter> createState() => _UpgradeRoleCenter();
}

// api/v1/authentication/upgrade?upgradeRole={ROLE_DOCTOR, ROLE_USER_PREMIUM}

class _UpgradeRoleCenter extends State<UpgradeRoleCenter> {
  String basePath = 'api/v1/authentication/upgrade';

  Future<models.RequestResponse> _requestUpgrade(String role) async {
    var antiCsrfToken = await secureStorageClient.get("antiCsrfToken");
    var accessToken = await secureStorageClient.get("access-token");

    return reqClient.request(
      models.RequestMethod.put,
      basePath,
      headers: {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": antiCsrfToken,
        "Authorization": "Bearer ${accessToken ?? ''}",
      },
      queryParams: {
        'upgradeRole': role,
      },
      onlyText: true,
    );
  }

  void _handlePremiumRoleUpgrade() async {
    _requestUpgrade('ROLE_USER_PREMIUM').then((response) {
      if (response.statusCode == 200) {
        secureStorageClient.save('role', 'ROLE_USER_PREMIUM');
        showCheck(context, 'Role upgraded');
        Navigator.pushAndRemoveUntil(
            context,
            MaterialPageRoute(builder: (context) => const UserHomepage()),
            (route) => false);
        return;
      }
      if (response.statusCode == 409) {
        showError(context, 'User already surpassed or has this role');
        return;
      }
      showError(context, 'Something went wrong, code: ${response.statusCode}');
    }).catchError((error) {
      showError(context, 'Something went wrong');
    });
  }

  void _handleDoctorRoleUpgrade() async {
    _requestUpgrade('ROLE_DOCTOR').then((response) {
      if (response.statusCode == 200) {
        secureStorageClient.save('role', 'ROLE_DOCTOR');
        showCheck(context, 'Role upgraded');
        Navigator.pushAndRemoveUntil(
            context,
            MaterialPageRoute(builder: (context) => const DoctorHomepage()),
            (route) => false);
        return;
      }
      if (response.statusCode == 409) {
        showError(context, 'User already surpassed or has this role');
        return;
      }
      showError(context, 'Something went wrong, code: ${response.statusCode}');
    }).catchError((error) {
      showError(context, 'Something went wrong');
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ElevatedButton(
          onPressed: _handlePremiumRoleUpgrade,
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.teal.shade600,
            padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(30),
            ),
          ),
          child: const Text(
            'Premium Role',
            style: TextStyle(fontSize: 18),
          ),
        ),
        const SizedBox(height: 20),
        ElevatedButton(
          onPressed: _handleDoctorRoleUpgrade,
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.teal.shade600,
            padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(30),
            ),
          ),
          child: const Text(
            'Doctor Role',
            style: TextStyle(fontSize: 18),
          ),
        ),
      ],
    );
  }
}
