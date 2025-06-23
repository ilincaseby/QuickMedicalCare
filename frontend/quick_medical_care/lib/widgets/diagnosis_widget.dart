import 'package:flutter/material.dart';

class DiagnosisWidget extends StatelessWidget {
  final String diagnosisText;
  final List<MapEntry<String, double>>? differentialDiagnosis;

  const DiagnosisWidget({
    super.key,
    required this.diagnosisText,
    this.differentialDiagnosis,
  });

  Widget buildBox({required String leftText, String? rightText}) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 6),
      padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 14),
      decoration: BoxDecoration(
        border: Border.all(
            color: const Color.fromARGB(255, 182, 252, 245), width: 1),
        borderRadius: BorderRadius.circular(10),
        color: Colors.teal.withOpacity(0.05),
      ),
      child: Row(
        children: [
          Expanded(
            child: Text(
              leftText,
              style: const TextStyle(fontSize: 16),
            ),
          ),
          if (rightText != null)
            Text(
              rightText,
              style: const TextStyle(fontSize: 16),
            ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.only(bottom: 6),
          child: Text(
            'Diagnosis',
            style: TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 18,
            ),
          ),
        ),
        buildBox(leftText: diagnosisText),
        if (differentialDiagnosis != null &&
            differentialDiagnosis!.isNotEmpty) ...[
          const Padding(
            padding: EdgeInsets.only(top: 16, bottom: 6),
            child: Text(
              'Differential Diagnosis',
              style: TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 18,
              ),
            ),
          ),
          ...differentialDiagnosis!.map(
            (entry) => buildBox(
              leftText: entry.key,
              rightText: '${(entry.value * 100).toStringAsFixed(1)}%',
            ),
          ),
        ],
      ],
    );
  }
}
