import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class LoginHeader extends StatelessWidget {
  const LoginHeader({super.key});

  @override
  Widget build(BuildContext context) {
    return Align(
      alignment: Alignment.center, // opțional, dar recomandat să specifici
      child: Column(
        mainAxisSize: MainAxisSize
            .min, // recomandat dacă vrei ca Row să aibă dimensiunea minimă
        children: [
          Text(
            'QuickMedicalCare',
            style: GoogleFonts.pacifico(
                color: Color.fromARGB(255, 0, 77, 64), fontSize: 32),
          ),
          Image.asset(
            'assets/LogoWoutBG.png',
            height: 200,
          ),
        ],
      ),
    );
    // return Image.asset(
    //   'assets/LogoWoutBG.png',
    //   height: 220,
    // );
  }
}
