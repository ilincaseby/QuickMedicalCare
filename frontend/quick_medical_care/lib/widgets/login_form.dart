import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:loader_overlay/loader_overlay.dart';
import 'package:quick_medical_care/screens/doctor_homepage.dart';
import 'package:quick_medical_care/screens/user_homepage.dart';
import 'package:quick_medical_care/widgets/snack_bar_error.dart';
import '../utils/http_request.dart' as reqClient;
import '../utils/models.dart' as models;
import '../utils/secure_data.dart' as secureStorageClient;
import '../screens/register_page.dart';

class LoginForm extends StatefulWidget {
  const LoginForm({super.key});

  @override
  State<LoginForm> createState() => _LoginFormState();
}

class _LoginFormState extends State<LoginForm> {
  @override
  void initState() {
    super.initState();

    // Afișează loaderul după ce e gata overlay-ul
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        //context.loaderOverlay.show(); // sau hide(), cum vrei
      }
    });
  }

  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();

  // void _showError(BuildContext context, final String input) {
  //   final text = input;
  //   final snackBar = SnackBar(
  //     content: Container(
  //       padding: const EdgeInsets.all(12),
  //       decoration: BoxDecoration(
  //         color: Colors.redAccent,
  //         borderRadius: BorderRadius.circular(12),
  //       ),
  //       child: Text(
  //         text,
  //         style: const TextStyle(color: Colors.white),
  //       ),
  //     ),
  //     backgroundColor: Colors.transparent,
  //     elevation: 0,
  //     duration: const Duration(seconds: 1),
  //     behavior: SnackBarBehavior.floating,
  //   );

  //   ScaffoldMessenger.of(context).showSnackBar(snackBar);
  // }

  void _handleLogin() {
    final email = _emailController.text;
    final password = _passwordController.text;

    reqClient.request(
      models.RequestMethod.post,
      "/api/v1/authentication/login",
      headers: {
        "Content-Type": "application/json",
      },
      body: {
        "email": email,
        "password": password,
      },
    ).then((response) {
      if (response.statusCode >= 200 && response.statusCode <= 299) {
        if (response.body != null && !kIsWeb) {
          secureStorageClient.save("email", email);
          secureStorageClient.save("password", password);
          var role = response.body?["role"];
          secureStorageClient.save("role", role);
          secureStorageClient.save(
              "access-token", response.body?["access-token"]);
          secureStorageClient.save(
              "antiCsrfToken", response.body?["antiCsrfToken"]);
          secureStorageClient.save(
              "refresh-token", response.body?["refresh-token"]);
          if (role != "ROLE_DOCTOR") {
            Navigator.pushAndRemoveUntil(
                context,
                MaterialPageRoute(builder: (context) => UserHomepage()),
                (route) => false);
          } else {
            Navigator.pushAndRemoveUntil(
                context,
                MaterialPageRoute(builder: (context) => DoctorHomepage()),
                (route) => false);
          }
          return;
        }
      }
      int code = response.statusCode;
      String failedMessage = code == 403
          ? "Wrong credentials, please try again"
          : "${response.errorBody}, code: $code";
      showError(context, failedMessage);
    }).catchError((error) {
      showError(context, "Something went wrong");
    });
  }

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TextField(
          controller: _emailController,
          decoration: const InputDecoration(
            hintText: 'email',
            hintStyle: TextStyle(
              fontSize: 18,
              letterSpacing: 1.2,
              color: Colors.black54,
            ),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.black54),
            ),
          ),
        ),
        const SizedBox(height: 20),
        TextField(
          controller: _passwordController,
          obscureText: true,
          decoration: const InputDecoration(
            hintText: 'password',
            hintStyle: TextStyle(
              fontSize: 18,
              letterSpacing: 1.2,
              color: Colors.black54,
            ),
            enabledBorder: UnderlineInputBorder(
              borderSide: BorderSide(color: Colors.black54),
            ),
          ),
        ),
        const SizedBox(height: 30),
        ElevatedButton(
          onPressed: _handleLogin,
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.teal.shade600,
            padding: const EdgeInsets.symmetric(horizontal: 60, vertical: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(30),
            ),
          ),
          child: const Text(
            'LOG IN',
            style: TextStyle(fontSize: 18),
          ),
        ),
        const SizedBox(height: 24),
        const Text(
          'New here? Register now!',
          style: TextStyle(
            fontSize: 16,
            color: Colors.black54,
          ),
        ),
        const SizedBox(height: 8),
        OutlinedButton(
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const RegisterPage()),
            );
          },
          style: OutlinedButton.styleFrom(
            foregroundColor: Colors.black,
            padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 12),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(30),
            ),
          ),
          child: const Text('REGISTER'),
        ),
      ],
    );
  }
}
