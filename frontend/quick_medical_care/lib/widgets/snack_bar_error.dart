import 'package:flutter/material.dart';

void showError(BuildContext context, final String input) {
  final text = input;
  final snackBar = SnackBar(
    content: Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.redAccent,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        text,
        style: const TextStyle(color: Colors.white),
      ),
    ),
    backgroundColor: Colors.transparent,
    elevation: 0,
    duration: const Duration(seconds: 1),
    behavior: SnackBarBehavior.floating,
  );

  ScaffoldMessenger.of(context).showSnackBar(snackBar);
}
