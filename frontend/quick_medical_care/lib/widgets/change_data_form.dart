import 'package:flutter/material.dart';
import 'form_styles.dart'; // asigură-te că e calea corectă

class ChangePersonalDataForm extends StatefulWidget {
  final bool isDoctor;

  const ChangePersonalDataForm({super.key, required this.isDoctor});

  @override
  State<ChangePersonalDataForm> createState() => _ChangePersonalDataFormState();
}

class _ChangePersonalDataFormState extends State<ChangePersonalDataForm> {
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _firstNameController = TextEditingController();
  final TextEditingController _lastNameController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _ageController = TextEditingController();
  final TextEditingController _weightController = TextEditingController();
  final TextEditingController _heightController = TextEditingController();
  final TextEditingController _sexController = TextEditingController();
  final TextEditingController _countryController = TextEditingController();
  final TextEditingController _smokerController = TextEditingController();
  final TextEditingController _alcoholConsumptionController =
      TextEditingController();

  int _sex = 0;
  bool _smoker = false;
  int _alcoholConsumptionFreq = 0;

  @override
  void dispose() {
    _usernameController.dispose();
    _firstNameController.dispose();
    _lastNameController.dispose();
    _emailController.dispose();
    _ageController.dispose();
    _weightController.dispose();
    _heightController.dispose();
    _sexController.dispose();
    _countryController.dispose();
    _smokerController.dispose();
    _alcoholConsumptionController.dispose();
    super.dispose();
  }

  void changeData() {
    // TODO: implement your change data logic here
  }

  Widget buildDoubleInputRow(Widget left, Widget right) {
    return Row(
      children: [
        Expanded(
            child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: left,
        )),
        Expanded(
            child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: right,
        )),
      ],
    );
  }

  InputDecoration _inputDecoration(String label) {
    return InputDecoration(
      labelText: label,
      border: const OutlineInputBorder(),
      contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 14),
    );
  }

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        children: [
          buildDoubleInputRow(
            TextFormField(
              controller: _usernameController,
              decoration: _inputDecoration('Username'),
            ),
            TextFormField(
              controller: _emailController,
              keyboardType: TextInputType.emailAddress,
              decoration: _inputDecoration('Email'),
            ),
          ),
          buildDoubleInputRow(
            TextFormField(
              controller: _firstNameController,
              decoration: _inputDecoration('First Name'),
            ),
            TextFormField(
              controller: _lastNameController,
              decoration: _inputDecoration('Last Name'),
            ),
          ),
          if (!widget.isDoctor) ...[
            buildDoubleInputRow(
              TextFormField(
                controller: _ageController,
                keyboardType: TextInputType.number,
                decoration: _inputDecoration('Age'),
              ),
              TextFormField(
                controller: _weightController,
                keyboardType: TextInputType.number,
                decoration: _inputDecoration('Weight'),
              ),
            ),
            buildDoubleInputRow(
              TextFormField(
                controller: _heightController,
                keyboardType: TextInputType.number,
                decoration: _inputDecoration('Height'),
              ),
              TextFormField(
                controller: _countryController,
                decoration: _inputDecoration('Country'),
              ),
            ),
            buildDoubleInputRow(
              DropdownButtonFormField<int>(
                value: _sex,
                items: const [
                  DropdownMenuItem(value: 1, child: Text('Male')),
                  DropdownMenuItem(value: 0, child: Text('Female')),
                ],
                onChanged: (value) => setState(() => _sex = value ?? 0),
                decoration: customInputDecoration(labelText: 'Sex'),
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
          ],
          const SizedBox(height: 20),
          ElevatedButton(
            onPressed: changeData,
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.teal.shade600,
              padding: const EdgeInsets.symmetric(horizontal: 60, vertical: 12),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(30),
              ),
            ),
            child: const Text(
              'Change Data',
              style: TextStyle(fontSize: 18),
            ),
          ),
        ],
      ),
    );
  }
}
