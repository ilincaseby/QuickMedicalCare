class RequestResponse {
  final int statusCode;
  final String? errorBody;
  final Map<String, dynamic>? body;

  RequestResponse({
    required this.statusCode,
    this.errorBody,
    this.body,
  });
}

enum RequestMethod {
  get,
  post,
  put,
  delete,
}
