abstract class AuthInterface {
  Future<String?> signIn(String email, String password);

  Future<bool> signUp({
    required String name,
    required String surname,
    required String city,
    required String country,
    required String email,
    required String password,
    required String rol,
  });
}
