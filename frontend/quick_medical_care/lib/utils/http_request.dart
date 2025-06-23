import 'dart:convert';

import 'package:http/http.dart' as http;
import './models.dart' as models;

var client = http.Client();
var scheme = "https";
var host = "sawfish-golden-promptly.ngrok-free.app";

Future<models.RequestResponse> request(
  models.RequestMethod method,
  String path, {
  Map<String, dynamic>? headers,
  Map<String, dynamic>? body,
  Map<String, dynamic>? queryParams,
  bool? onlyText,
}) async {
  final response = await methodBasedRequest(
    method,
    path,
    headers,
    body,
    queryParams,
  );
  if (response.statusCode >= 200 && response.statusCode < 299) {
    var respBody;
    if (onlyText != null && onlyText) {
      respBody = {"response": response.body};
    } else {
      respBody = json.decode(response.body);
    }
    return models.RequestResponse(
      statusCode: response.statusCode,
      body: respBody,
    );
  }
  return models.RequestResponse(
    statusCode: response.statusCode,
    errorBody: response.body,
  );
}

Future<http.Response> methodBasedRequest(
    models.RequestMethod method,
    String path,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? body,
    Map<String, dynamic>? queryParams) async {
  final url = Uri(
    scheme: scheme,
    host: host,
    path: path,
    queryParameters: queryParams?.map((k, v) => MapEntry(k, v.toString())),
  );
  switch (method) {
    case models.RequestMethod.get:
      return await client.get(
        url,
        headers: headers?.map((k, v) => MapEntry(k, v.toString())),
      );
    case models.RequestMethod.post:
      return await client.post(
        url,
        headers: headers?.map((k, v) => MapEntry(k, v.toString())),
        body: body != null ? json.encode(body) : null,
      );
    case models.RequestMethod.put:
      return await client.put(
        url,
        headers: headers?.map((k, v) => MapEntry(k, v.toString())),
        body: body != null ? json.encode(body) : null,
      );
    default:
      return await client.delete(
        url,
        headers: headers?.map((k, v) => MapEntry(k, v.toString())),
        body: body != null ? json.encode(body) : null,
      );
  }
}
