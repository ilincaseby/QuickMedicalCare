import 'package:flutter/material.dart';

class SettingsLogoutButtons extends StatefulWidget {
  final VoidCallback onSettingsPressed;
  final VoidCallback onLogoutPressed;

  const SettingsLogoutButtons({
    super.key,
    required this.onSettingsPressed,
    required this.onLogoutPressed,
  });

  @override
  State<SettingsLogoutButtons> createState() => _SettingsLogoutButtonsState();
}

class _SettingsLogoutButtonsState extends State<SettingsLogoutButtons> {
  double _settingsScale = 1.0;
  double _logoutScale = 1.0;

  void _onTapDownSettings(_) {
    setState(() {
      _settingsScale = 1.3; // mărește la apăsare
    });
  }

  void _onTapUpSettings(_) {
    setState(() {
      _settingsScale = 1.0; // revine la dimensiunea normală
    });
    widget.onSettingsPressed();
  }

  void _onTapCancelSettings() {
    setState(() {
      _settingsScale = 1.0;
    });
  }

  void _onTapDownLogout(_) {
    setState(() {
      _logoutScale = 1.3;
    });
  }

  void _onTapUpLogout(_) {
    setState(() {
      _logoutScale = 1.0;
    });
    widget.onLogoutPressed();
  }

  void _onTapCancelLogout() {
    setState(() {
      _logoutScale = 1.0;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        GestureDetector(
          onTapDown: _onTapDownSettings,
          onTapUp: _onTapUpSettings,
          onTapCancel: _onTapCancelSettings,
          child: AnimatedScale(
            scale: _settingsScale,
            duration: const Duration(milliseconds: 100),
            curve: Curves.easeOut,
            child: Image.asset(
              'assets/settings_icon.png',
              width: 48,
              height: 48,
            ),
          ),
        ),
        GestureDetector(
          onTapDown: _onTapDownLogout,
          onTapUp: _onTapUpLogout,
          onTapCancel: _onTapCancelLogout,
          child: AnimatedScale(
            scale: _logoutScale,
            duration: const Duration(milliseconds: 100),
            curve: Curves.easeOut,
            child: Image.asset(
              'assets/logout.png',
              width: 48,
              height: 48,
            ),
          ),
        ),
      ],
    );
  }
}
