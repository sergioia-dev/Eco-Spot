import 'package:frontend/data/repository_implementations/auth_repository.dart';

void main() {
  AuthRepository authRepository = AuthRepository();
  authRepository.signIn("sergioidarraga2110@gmail.com", "Sergio123");
}
