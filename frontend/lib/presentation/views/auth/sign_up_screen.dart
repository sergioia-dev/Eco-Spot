import 'package:flutter/material.dart';
import 'package:frontend/domain/repository_interfaces/auth_interface.dart';
import 'package:frontend/presentation/routes/routes.dart';
import 'package:frontend/util/validators/validators.dart';

class SignUpScreen extends StatefulWidget {
  final AuthInterface authInterface;

  const SignUpScreen({super.key, required this.authInterface});

  @override
  State<SignUpScreen> createState() => _SignUpScreenState();
}

class _SignUpScreenState extends State<SignUpScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _surnameController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _obscurePassword = true;
  bool _isLoading = false;

  String _selectedCity = 'MEDELLIN';
  String _selectedCountry = 'COLOMBIA';
  String _selectedRole = 'TOURIST';

  static const Map<String, List<String>> _countryCities = {
    'COLOMBIA': [
      'MEDELLIN',
      'BOGOTA',
      'CALI',
      'BARRANQUILLA',
      'CARTAGENA',
      'BUCARAMANGA',
      'SANTA_MARTA',
      'MANIZALES',
      'PEREIRA',
      'ARMENIA',
    ],
    'ARGENTINA': [
      'BUENOS_AIRES',
      'CORDOBA',
      'ROSARIO',
      'Mendoza',
      'TUCUMAN',
      'CHACO',
      'CORRIENTES',
      'NEUQUEN',
    ],
    'CHILE': [
      'SANTIAGO',
      'VALPARAISO',
      'CONCEPCION',
      'LA_SERENA',
      'ANTOFAGASTA',
      'RANCAGUA',
      'TALCA',
      'CHILLAN',
    ],
    'MEXICO': [
      'MEXICO_CITY',
      'GUADALAJARA',
      'MONTERREY',
      'CANCUN',
      'PUEBLA',
      'TOLUCA',
      'VERACRUZ',
      'LEON',
      'TIJUANA',
      'MERIDA',
    ],
    'PERU': [
      'LIMA',
      'AREQUIPA',
      'CUSCO',
      'TRUJILLO',
      'CHICLAYO',
      'IQUITOS',
      'HUANCAYO',
      'PIURA',
      'CALLAO',
      'AYACUCHO',
    ],
    'ECUADOR': [
      'QUITO',
      'GUAYAQUIL',
      'CUENCA',
      'MACHALA',
      'SANTO_DOMINGO',
      'MANTA',
      'PORTOVIEJO',
      'AMBATO',
      'MILAGRO',
      'ESMERALDAS',
    ],
    'PANAMA': [
      'PANAMA_CITY',
      'COLON',
      'DAVID',
      'BOCAS_DEL_TORO',
      'PENONOME',
      'CHITRE',
      'SANTIAGO',
      'LA_CHORRERA',
      'AGUADULCE',
      'LOS_SANTOS',
    ],
    'COSTA_RICA': [
      'SAN_JOSE',
      'ALAJUELA',
      'CARTAGO',
      'HEREDIA',
      'LIBERIA',
      'LIMON',
      'PUNTARENAS',
      'QUEPOS',
      'TURRIALBA',
      'CORDOBILLA',
    ],
    'URUGUAY': [
      'MONTEVIDEO',
      'SALTO',
      'PUNTA_DEL_ESTE',
      'COLONIA',
      'MALDONADO',
      'Paysandu',
      'RIVERA',
      'ARTIGAS',
      'CERRO_LARGO',
      'SORIANO',
    ],
    'BRAZIL': [
      'SAO_PAULO',
      'RIO_DE_JANEIRO',
      'BRASILIA',
      'SALVADOR',
      'FORTALEZA',
      'BELO_HORIZONTE',
      'MANAUS',
      'CURITIBA',
      'RECIFE',
      'PORTO_ALEGRE',
    ],
  };

  static List<String> get _countryList => _countryCities.keys.toList();

  List<String> get _citiesForSelectedCountry =>
      _countryCities[_selectedCountry] ?? [];

  static const List<String> _roles = [
    'TOURIST',
    'HOST',
    'BUSINESS',
    'EXPERIENCE',
  ];

  @override
  void dispose() {
    _nameController.dispose();
    _surnameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  void _submit() async {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isLoading = true;
      });

      final success = await widget.authInterface.signUp(
        name: _nameController.text,
        surname: _surnameController.text,
        city: _selectedCity,
        country: _selectedCountry,
        email: _emailController.text,
        password: _passwordController.text,
        rol: _selectedRole,
      );

      setState(() {
        _isLoading = false;
      });

      if (success && mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Account created successfully! Please sign in.'),
            backgroundColor: Colors.green,
          ),
        );
        Navigator.pushReplacementNamed(context, Routes.signInScreen);
      } else if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Email already registered'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF7F7F7),
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.black),
          onPressed: () =>
              Navigator.pushReplacementNamed(context, Routes.signInScreen),
        ),
      ),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                'Create Account',
                style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),
              const Text(
                'Join Eco Spot today',
                style: TextStyle(fontSize: 16, color: Colors.grey),
              ),
              const SizedBox(height: 24),
              Container(
                padding: const EdgeInsets.all(24.0),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Form(
                  key: _formKey,
                  child: Column(
                    children: [
                      Row(
                        children: [
                          Expanded(
                            child: TextFormField(
                              controller: _nameController,
                              decoration: const InputDecoration(
                                labelText: 'Name',
                                border: InputBorder.none,
                                enabledBorder: InputBorder.none,
                                focusedBorder: InputBorder.none,
                                filled: true,
                                fillColor: Color(0xFFF7F7F7),
                              ),
                              validator: validateName,
                            ),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: TextFormField(
                              controller: _surnameController,
                              decoration: const InputDecoration(
                                labelText: 'Surname',
                                border: InputBorder.none,
                                enabledBorder: InputBorder.none,
                                focusedBorder: InputBorder.none,
                                filled: true,
                                fillColor: Color(0xFFF7F7F7),
                              ),
                              validator: validateSurname,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 16),
                      DropdownButtonFormField<String>(
                        initialValue: _selectedCity,
                        decoration: const InputDecoration(
                          labelText: 'City',
                          border: InputBorder.none,
                          enabledBorder: InputBorder.none,
                          focusedBorder: InputBorder.none,
                          filled: true,
                          fillColor: Color(0xFFF7F7F7),
                        ),
                        items: _citiesForSelectedCountry.map((city) {
                          return DropdownMenuItem(
                            value: city,
                            child: Text(city),
                          );
                        }).toList(),
                        onChanged: (value) {
                          setState(() {
                            _selectedCity = value!;
                          });
                        },
                      ),
                      const SizedBox(height: 16),
                      DropdownButtonFormField<String>(
                        initialValue: _selectedCountry,
                        decoration: const InputDecoration(
                          labelText: 'Country',
                          border: InputBorder.none,
                          enabledBorder: InputBorder.none,
                          focusedBorder: InputBorder.none,
                          filled: true,
                          fillColor: Color(0xFFF7F7F7),
                        ),
                        items: _countryList.map((country) {
                          return DropdownMenuItem(
                            value: country,
                            child: Text(country),
                          );
                        }).toList(),
                        onChanged: (value) {
                          setState(() {
                            _selectedCountry = value!;
                            _selectedCity = _countryCities[value]!.first;
                          });
                        },
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _emailController,
                        keyboardType: TextInputType.emailAddress,
                        decoration: const InputDecoration(
                          labelText: 'Email',
                          border: InputBorder.none,
                          enabledBorder: InputBorder.none,
                          focusedBorder: InputBorder.none,
                          filled: true,
                          fillColor: Color(0xFFF7F7F7),
                        ),
                        validator: validateEmail,
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _passwordController,
                        obscureText: _obscurePassword,
                        decoration: InputDecoration(
                          labelText: 'Password',
                          border: InputBorder.none,
                          enabledBorder: InputBorder.none,
                          focusedBorder: InputBorder.none,
                          filled: true,
                          fillColor: const Color(0xFFF7F7F7),
                          suffixIcon: IconButton(
                            icon: Icon(
                              _obscurePassword
                                  ? Icons.visibility_off
                                  : Icons.visibility,
                            ),
                            onPressed: () {
                              setState(() {
                                _obscurePassword = !_obscurePassword;
                              });
                            },
                          ),
                        ),
                        validator: validatePassword,
                      ),
                      const SizedBox(height: 16),
                      DropdownButtonFormField<String>(
                        initialValue: _selectedRole,
                        decoration: const InputDecoration(
                          labelText: 'Role',
                          border: InputBorder.none,
                          enabledBorder: InputBorder.none,
                          focusedBorder: InputBorder.none,
                          filled: true,
                          fillColor: Color(0xFFF7F7F7),
                        ),
                        items: _roles.map((role) {
                          return DropdownMenuItem(
                            value: role,
                            child: Text(role),
                          );
                        }).toList(),
                        onChanged: (value) {
                          setState(() {
                            _selectedRole = value!;
                          });
                        },
                      ),
                      const SizedBox(height: 24),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: _isLoading ? null : _submit,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFFFF385C),
                            foregroundColor: Colors.white,
                            padding: const EdgeInsets.symmetric(vertical: 16),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(4),
                            ),
                            side: const BorderSide(color: Color(0xFFFF385C)),
                          ),
                          child: _isLoading
                              ? const SizedBox(
                                  height: 20,
                                  width: 20,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2,
                                    valueColor: AlwaysStoppedAnimation<Color>(
                                      Colors.white,
                                    ),
                                  ),
                                )
                              : const Text(
                                  'Sign Up',
                                  style: TextStyle(
                                    fontSize: 16,
                                    fontWeight: FontWeight.w700,
                                  ),
                                ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
              Text.rich(
                TextSpan(
                  text: 'Already have an account? ',
                  children: [
                    WidgetSpan(
                      child: GestureDetector(
                        onTap: () {
                          Navigator.pushReplacementNamed(
                            context,
                            Routes.signInScreen,
                          );
                        },
                        child: const Text(
                          'Sign In',
                          style: TextStyle(
                            color: Color(0xFFFF385C),
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
