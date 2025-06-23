import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:shared_preferences/shared_preferences.dart';

const secureStorage = FlutterSecureStorage();

Future<bool> save(
  String key,
  String value,
) async {
  try {
    await secureStorage.write(key: key, value: value);
    return true;
  } catch (error) {
    print(error.toString());
    return false;
  }
}

Future<String?> get(
  String key,
) async {
  try {
    final data = await secureStorage.read(key: key);
    return data;
  } catch (error) {
    print(error.toString());
    return null;
  }
}

Future<void> deleteAll() async {
  try {
    await secureStorage.deleteAll();
  } catch (error) {
    print(error.toString());
    return;
  }
}

class UniversalStorage {
  static const _secure = FlutterSecureStorage();

  static Future<void> write(String key, String value) async {
    if (kIsWeb) {
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString(key, value);
    } else {
      await _secure.write(key: key, value: value);
    }
  }

  static Future<String?> read(String key) async {
    if (kIsWeb) {
      final prefs = await SharedPreferences.getInstance();
      return prefs.getString(key);
    } else {
      return await _secure.read(key: key);
    }
  }

  static Future<void> delete(String key) async {
    if (kIsWeb) {
      final prefs = await SharedPreferences.getInstance();
      await prefs.remove(key);
    } else {
      await _secure.delete(key: key);
    }
  }
}
