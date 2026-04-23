import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/domain/providers/tourist_provider.dart';
import 'package:frontend/domain/providers/secure_storage_provider.dart';
import 'package:frontend/presentation/routes/routes.dart';

class TouristPaymentScreen extends StatefulWidget {
  final String rentalId;
  final String rentalName;
  final String startDate;
  final String endDate;
  final double totalPrice;
  final int nights;

  const TouristPaymentScreen({
    super.key,
    required this.rentalId,
    required this.rentalName,
    required this.startDate,
    required this.endDate,
    required this.totalPrice,
    required this.nights,
  });

  @override
  State<TouristPaymentScreen> createState() => _TouristPaymentScreenState();
}

class _TouristPaymentScreenState extends State<TouristPaymentScreen> {
  final TouristProvider _touristProvider = TouristProvider();
  
  String? _selectedMethod;
  bool _isLoading = false;
  String? _error;
  String? _successReservationId;

  final List<Map<String, String>> _paymentMethods = [
    {'id': 'credit_card', 'name': 'Tarjeta de Crédito', 'icon': 'credit_card'},
    {'id': 'paypal', 'name': 'PayPal', 'icon': 'account_balance_wallet'},
    {'id': 'cash', 'name': 'Efectivo', 'icon': 'money'},
  ];

  IconData _getIcon(String iconName) {
    switch (iconName) {
      case 'credit_card':
        return Icons.credit_card;
      case 'account_balance_wallet':
        return Icons.account_balance_wallet;
      case 'money':
        return Icons.money;
      default:
        return Icons.payment;
    }
  }

  Future<void> _processPayment() async {
    if (_selectedMethod == null) {
      setState(() {
        _error = 'Selecciona un método de pago';
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _error = null;
    });

    final secureStorage = context.read<SecureStorageProvider>();
    final token = await secureStorage.read('token');

    if (token == null) {
      setState(() {
        _error = 'Sesión expirada';
        _isLoading = false;
      });
      return;
    }

    final reservation = await _touristProvider.createReservation(
      token: token,
      rentalId: widget.rentalId,
      startingDate: widget.startDate,
      endDate: widget.endDate,
    );

    if (_touristProvider.error != null) {
      final errorMsg = _touristProvider.error!.toLowerCase();
      if (errorMsg.contains('403') || errorMsg.contains('forbidden') || errorMsg.contains('superpuesta') || errorMsg.contains('403')) {
        setState(() {
          _isLoading = false;
        });
        if (mounted) {
          Navigator.pop(context);
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Fechas no disponibles. Por favor selecciona otras fechas.'),
              backgroundColor: Colors.red,
            ),
          );
        }
        return;
      } else if (errorMsg.contains('400')) {
        setState(() {
          _error = 'Fechas inválidas';
          _isLoading = false;
        });
      } else {
        setState(() {
          _error = _touristProvider.error;
          _isLoading = false;
        });
      }
      return;
    }

    if (reservation == null) {
      setState(() {
        _error = 'Error al crear la reservación';
        _isLoading = false;
      });
      return;
    }

    final payment = await _touristProvider.createPayment(
      token: token,
      reservationId: reservation.id,
      amount: widget.totalPrice,
    );

    if (_touristProvider.error != null || payment == null) {
      await _touristProvider.cancelReservationHost(
        token: token,
        reservationId: reservation.id,
      );
      setState(() {
        _error = 'Error al procesar el pago. La reservación ha sido cancelada.';
        _isLoading = false;
      });
      return;
    }

    setState(() {
      _successReservationId = reservation.id;
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF7F7F7),
      appBar: AppBar(
        title: const Text(
          'Pago',
          style: TextStyle(
            color: Color(0xFFFF385C),
            fontWeight: FontWeight.w700,
          ),
        ),
        backgroundColor: Colors.transparent,
        elevation: 0,
        foregroundColor: const Color(0xFFFF385C),
      ),
      body: _successReservationId != null ? _buildSuccess() : _buildForm(),
    );
  }

  Widget _buildForm() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(12),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  widget.rentalName,
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    const Icon(Icons.calendar_today, size: 16, color: Colors.grey),
                    const SizedBox(width: 4),
                    Text(
                      '${widget.startDate} - ${widget.endDate}',
                      style: const TextStyle(color: Colors.grey),
                    ),
                  ],
                ),
                const SizedBox(height: 4),
                Text(
                  '${widget.nights} noche${widget.nights > 1 ? 's' : ''}',
                  style: const TextStyle(color: Colors.grey),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(12),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'Método de pago',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 16),
                ..._paymentMethods.map((method) => _buildMethodOption(method)),
              ],
            ),
          ),
          const SizedBox(height: 16),
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(12),
            ),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      'Total a pagar',
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 16,
                      ),
                    ),
                    Text(
                      '\$${widget.totalPrice.toStringAsFixed(0)}',
                      style: const TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 20,
                        color: Color(0xFFFF385C),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
          if (_error != null) ...[
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.red[50],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Row(
                children: [
                  const Icon(Icons.error_outline, color: Colors.red),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      _error!,
                      style: const TextStyle(color: Colors.red),
                    ),
                  ),
                ],
              ),
            ),
          ],
          const SizedBox(height: 24),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: _isLoading || _selectedMethod == null
                  ? null
                  : _processPayment,
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFFF385C),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
              child: _isLoading
                  ? const SizedBox(
                      height: 20,
                      width: 20,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        valueColor:
                            AlwaysStoppedAnimation<Color>(Colors.white),
                      ),
                    )
                  : Text(
                      'Pagar \$${widget.totalPrice.toStringAsFixed(0)}',
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMethodOption(Map<String, String> method) {
    final isSelected = _selectedMethod == method['id'];
    return GestureDetector(
      onTap: () {
        setState(() {
          _selectedMethod = method['id'];
        });
      },
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: const Color(0xFFF7F7F7),
          borderRadius: BorderRadius.circular(8),
          border: isSelected
              ? Border.all(color: const Color(0xFFFF385C), width: 2)
              : null,
        ),
        child: Row(
          children: [
            Icon(
              _getIcon(method['icon']!),
              color: isSelected
                  ? const Color(0xFFFF385C)
                  : Colors.grey,
            ),
            const SizedBox(width: 12),
            Text(
              method['name']!,
              style: TextStyle(
                fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                color: isSelected ? const Color(0xFFFF385C) : Colors.black,
              ),
            ),
            const Spacer(),
            if (isSelected)
              const Icon(
                Icons.check_circle,
                color: Color(0xFFFF385C),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildSuccess() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.check_circle,
              size: 80,
              color: Color(0xFFFF385C),
            ),
            const SizedBox(height: 24),
            const Text(
              'Pago Exitoso',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            Text(
              '\$${widget.totalPrice.toStringAsFixed(0)}',
              style: const TextStyle(
                fontSize: 32,
                fontWeight: FontWeight.bold,
                color: Color(0xFFFF385C),
              ),
            ),
            const SizedBox(height: 8),
            const Text(
              'Total pagado',
              style: TextStyle(color: Colors.grey),
            ),
            const SizedBox(height: 32),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () {
                  Navigator.pushNamedAndRemoveUntil(
                    context,
                    Routes.touristHomeScreen,
                    (route) => false,
                  );
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFFFF385C),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                child: const Text(
                  'Volver al Inicio',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}