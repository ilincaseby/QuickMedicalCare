import 'package:flutter/material.dart';
import 'package:quick_medical_care/widgets/snack_bar_check.dart';
import 'package:quick_medical_care/widgets/snack_bar_error.dart';
import 'form_styles.dart';
import '../utils/http_request.dart' as reqClient;
import '../utils/models.dart' as models;

class RegisterForm extends StatefulWidget {
  const RegisterForm({super.key});
  @override
  State<RegisterForm> createState() => _RegisterFormState();
}

class _RegisterFormState extends State<RegisterForm> {
  final _formKey = GlobalKey<FormState>();

  final _usernameController = TextEditingController();
  final _firstNameController = TextEditingController();
  final _lastNameController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _ageController = TextEditingController();
  final _weightController = TextEditingController();
  final _heightController = TextEditingController();
  final _countryController = TextEditingController();

  int _sex = 1;
  bool _smoker = false;
  int _alcoholConsumptionFreq = 0;

  void handleRegister() {
    final int? age = int.tryParse(_ageController.text);
    if (age == null) {
      showError(context, "Age not completed");
      return;
    }

    final double? weight = double.tryParse(_weightController.text);
    if (weight == null) {
      showError(context, "Weight not completed");
      return;
    }

    final double? height = double.tryParse(_heightController.text);
    if (height == null) {
      showError(context, "Height not completed");
      return;
    }

    final reqBody = {
      "username": _usernameController.text,
      "firstName": _firstNameController.text,
      "lastName": _lastNameController.text,
      "email": _emailController.text,
      "password": _passwordController.text,
      "age": age,
      "weight": weight,
      "height": height,
      "sex": _sex,
      "country": _countryController.text,
      "smoker": _smoker,
      "alcoholConsumptionFreq": _alcoholConsumptionFreq,
    };

    reqClient
        .request(
      models.RequestMethod.post,
      "/api/v1/authentication/register",
      headers: {
        "Content-Type": "application/json",
      },
      body: reqBody,
      onlyText: true,
    )
        .then((response) {
      if (response.statusCode >= 200 && response.statusCode <= 299) {
        showCheck(context, "User registered successfully");
        Navigator.pop(context);
        return;
      }
      showError(context, response.errorBody.toString());
    }).catchError((error) {
      print(error);
      showError(context, "Something went wrong");
    });
  }

  @override
  void dispose() {
    _usernameController.dispose();
    _firstNameController.dispose();
    _lastNameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _ageController.dispose();
    _weightController.dispose();
    _heightController.dispose();
    _countryController.dispose();
    super.dispose();
  }

  Widget buildDoubleInputRow(Widget left, Widget right) {
    return Row(
      children: [
        Expanded(
            child: Padding(padding: const EdgeInsets.all(8.0), child: left)),
        Expanded(
            child: Padding(padding: const EdgeInsets.all(8.0), child: right)),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          buildDoubleInputRow(
            TextField(
              controller: _emailController,
              decoration: const InputDecoration(labelText: 'Email'),
            ),
            TextField(
              controller: _usernameController,
              decoration: const InputDecoration(labelText: 'Username'),
            ),
          ),
          buildDoubleInputRow(
            TextField(
              controller: _lastNameController,
              decoration: const InputDecoration(labelText: 'Last Name'),
            ),
            TextField(
              controller: _firstNameController,
              decoration: const InputDecoration(labelText: 'First Name'),
            ),
          ),
          buildDoubleInputRow(
            TextField(
              controller: _passwordController,
              obscureText: true,
              decoration: const InputDecoration(labelText: 'Password'),
            ),
            TextField(
              controller: _ageController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'Age'),
            ),
          ),
          buildDoubleInputRow(
            TextField(
              controller: _weightController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'Weight (kg)'),
            ),
            TextField(
              controller: _heightController,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(labelText: 'Height (cm)'),
            ),
          ),
          buildDoubleInputRow(
            TextField(
              controller: _countryController,
              decoration: const InputDecoration(labelText: 'Country'),
            ),
            SwitchListTile(
              title: const Text(
                "Smoker",
                style: TextStyle(fontSize: 18), // text mai mic
              ),
              value: _smoker,
              onChanged: (value) => setState(() => _smoker = value),
              contentPadding: const EdgeInsets.symmetric(
                  horizontal: 8.0), // mai puțin padding
              dense: true, // face tile-ul mai compact vertical
            ),
          ),
          DropdownButtonFormField<int>(
            value: _sex,
            items: const [
              DropdownMenuItem(value: 1, child: Text('Male')),
              DropdownMenuItem(value: 0, child: Text('Female')),
            ],
            onChanged: (value) => setState(() => _sex = value ?? 0),
            decoration: customInputDecoration(labelText: 'Sex'),
          ),
          Padding(
            padding: const EdgeInsets.fromLTRB(8.0, 12.0, 8.0, 0), // top 12
            child: DropdownButtonFormField<int>(
              value: _alcoholConsumptionFreq,
              items: const [
                DropdownMenuItem(value: 0, child: Text('Never')),
                DropdownMenuItem(value: 1, child: Text('Occasionally')),
                DropdownMenuItem(value: 2, child: Text('Frequently')),
              ],
              onChanged: (value) =>
                  setState(() => _alcoholConsumptionFreq = value ?? 0),
              decoration:
                  customInputDecoration(labelText: 'Alcohol Consumption'),
            ),
          ),
          const SizedBox(height: 20),
          ElevatedButton(
            onPressed: handleRegister,
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.teal.shade600,
              padding: const EdgeInsets.symmetric(horizontal: 60, vertical: 12),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(30),
              ),
            ),
            child: const Text(
              'REGISTER',
              style: TextStyle(fontSize: 18),
            ),
          ),
        ],
      ),
    );
  }
}
