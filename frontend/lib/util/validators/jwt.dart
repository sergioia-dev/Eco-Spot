import 'dart:convert';

Map<String, dynamic> decodeJwt(String token) {
  final parts = token.split('.');
  if (parts.length != 3) {
    throw const FormatException('Invalid JWT token');
  }

  final payload = parts[1];
  final padded = payload.padRight(
    payload.length + (4 - payload.length % 4) % 4,
    '=',
  );
  final decoded = base64Decode(padded.replaceAll('-_', '+/'));
  return jsonDecode(utf8.decode(decoded)) as Map<String, dynamic>;
}

String? getRolFromToken(String token) {
  final payload = decodeJwt(token);
  return payload['rol'] as String?;
}

