import 'package:flutter/material.dart';

InputDecoration customInputDecoration({required String labelText}) {
  return InputDecoration(
    labelText: labelText,
    border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
    focusedBorder: OutlineInputBorder(
      borderRadius: BorderRadius.circular(8),
      borderSide: BorderSide(),
    ),
  );
}
