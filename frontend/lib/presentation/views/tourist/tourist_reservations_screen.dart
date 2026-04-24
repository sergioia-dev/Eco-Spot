import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/domain/models/rental.dart';
import 'package:frontend/domain/providers/tourist_provider.dart';
import 'package:frontend/domain/providers/secure_storage_provider.dart';
import 'package:frontend/presentation/routes/routes.dart';
import 'package:frontend/presentation/widgets/tourist_sidebar.dart';
import 'package:frontend/presentation/widgets/tourist_bottom_nav.dart';

class TouristReservationsScreen extends StatefulWidget {
  const TouristReservationsScreen({super.key});

  @override
  State<TouristReservationsScreen> createState() =>
      _TouristReservationsScreenState();
}

class _TouristReservationsScreenState extends State<TouristReservationsScreen> {
  late TouristProvider _touristProvider;
  int _currentNavIndex = 2;
  bool _showUpcoming = true;
  bool _hasSearched = false;

  @override
  void initState() {
    super.initState();
    _touristProvider = TouristProvider();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadReservations();
    });
  }

  Future<void> _loadReservations() async {
    final secureStorage = context.read<SecureStorageProvider>();
    final token = await secureStorage.read('token');
    if (token != null) {
      _hasSearched = true;
      await _touristProvider.loadUserReservations(
        token,
        upcoming: _showUpcoming,
      );
    }
  }

  void _toggleUpcoming() {
    setState(() {
      _showUpcoming = !_showUpcoming;
    });
    _loadReservations();
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider.value(
      value: _touristProvider,
      child: Scaffold(
        backgroundColor: const Color(0xFFF7F7F7),
        appBar: AppBar(
          title: const Text(
            'My Reservations',
            style: TextStyle(
              color: Color(0xFFFF385C),
              fontWeight: FontWeight.w700,
            ),
          ),
          backgroundColor: Colors.transparent,
          elevation: 0,
          foregroundColor: const Color(0xFFFF385C),
          actions: const [Icon(Icons.notifications)],
        ),
        drawer: const TouristSidebar(),
        body: Column(
          children: [
            _buildFilterToggle(),
            Expanded(child: _buildReservationsList()),
          ],
        ),
        bottomNavigationBar: TouristBottomNav(
          currentIndex: _currentNavIndex,
          onTap: (index) {
            setState(() {
              _currentNavIndex = index;
            });
            if (index == 0) {
              Navigator.pushReplacementNamed(context, Routes.touristHomeScreen);
            } else if (index == 1) {
              Navigator.pushNamed(context, Routes.touristSearchScreen);
            } else if (index == 3) {
              Navigator.pushNamed(context, Routes.touristProfileScreen);
            }
          },
        ),
      ),
    );
  }

  Widget _buildFilterToggle() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          FilterChip(
            label: const Text('Upcoming'),
            selected: _showUpcoming,
            onSelected: (_) => _toggleUpcoming(),
            selectedColor: const Color(0xFFFF385C).withValues(alpha: 0.2),
            checkmarkColor: const Color(0xFFFF385C),
            labelStyle: TextStyle(
              color: _showUpcoming ? const Color(0xFFFF385C) : Colors.grey[700],
            ),
          ),
          const SizedBox(width: 8),
          FilterChip(
            label: const Text('Past'),
            selected: !_showUpcoming,
            onSelected: (_) => _toggleUpcoming(),
            selectedColor: const Color(0xFFFF385C).withValues(alpha: 0.2),
            checkmarkColor: const Color(0xFFFF385C),
            labelStyle: TextStyle(
              color: !_showUpcoming
                  ? const Color(0xFFFF385C)
                  : Colors.grey[700],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildReservationsList() {
    return Consumer<TouristProvider>(
      builder: (context, touristProvider, child) {
        if (touristProvider.isLoading) {
          return const Center(
            child: CircularProgressIndicator(color: Color(0xFFFF385C)),
          );
        }

        final reservations = touristProvider.userReservations;

        if (!_hasSearched) {
          return const Center(
            child: CircularProgressIndicator(color: Color(0xFFFF385C)),
          );
        }

        if (reservations.isEmpty) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(
                  Icons.calendar_today_outlined,
                  size: 64,
                  color: Colors.grey,
                ),
                const SizedBox(height: 16),
                Text(
                  _showUpcoming
                      ? 'No upcoming reservations'
                      : 'No past reservations',
                  style: const TextStyle(fontSize: 18, color: Colors.grey),
                ),
              ],
            ),
          );
        }

        return RefreshIndicator(
          onRefresh: _loadReservations,
          color: const Color(0xFFFF385C),
          child: ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: reservations.length,
            itemBuilder: (context, index) {
              return _buildReservationCard(reservations[index]);
            },
          ),
        );
      },
    );
  }

  Widget _buildReservationCard(Map<String, dynamic> item) {
    final reservationId = item['reservationId'] as String? ?? '';
    final id = item['id'] as String? ?? '';
    final name = item['name'] as String? ?? 'Unknown';
    final city = item['city'] as String? ?? '';
    final country = item['country'] as String? ?? '';
    final price = (item['price'] as num?)?.toDouble() ?? 0.0;
    final startingDate = item['startingDate'] as String? ?? '';
    final endDate = item['endDate'] as String? ?? '';
    final isCancelled = item['isCancelled'] as bool? ?? false;
    final description = item['description'] as String?;
    final contact = item['contact'] as String? ?? '';
    final size = item['size'] as int? ?? 0;
    final peopleQuantity = item['peopleQuantity'] as int? ?? 0;
    final rooms = item['rooms'] as int? ?? 0;
    final bathrooms = item['bathrooms'] as int? ?? 0;
    final valueNight = (item['valueNight'] as num?)?.toDouble() ?? 0.0;
    final reviewAverage = (item['reviewAverage'] as num?)?.toDouble();
    final isEnable = item['enable'] as bool? ?? true;
    final location = item['location'] as String?;
    final images = item['images'] as List<dynamic>? ?? [];
    String? imageUrl;

    final rental = Rental(
      id: id,
      name: name,
      description: description,
      contact: contact,
      size: size,
      peopleQuantity: peopleQuantity,
      rooms: rooms,
      bathrooms: bathrooms,
      city: city,
      country: country,
      location: location,
      valueNight: valueNight,
      isEnable: isEnable,
      reviewAverage: reviewAverage,
      images: [],
      reviews: null,
    );

    if (images.isNotEmpty) {
      final firstImage = images.first;
      if (firstImage is Map<String, dynamic>) {
        imageUrl =
            'http://10.0.2.2:8080/images/${firstImage['id']}.${firstImage['extension']}';
      } else if (firstImage is Rental) {
        imageUrl = firstImage.images.isNotEmpty
            ? firstImage.images.first.imageUrl
            : null;
      }
    }

    return GestureDetector(
      onTap: () {
        Navigator.pushNamed(
          context,
          Routes.touristRentalDetailScreen,
          arguments: rental,
        );
      },
      child: Card(
        margin: const EdgeInsets.only(bottom: 16),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (imageUrl != null)
              ClipRRect(
                borderRadius: const BorderRadius.vertical(
                  top: Radius.circular(12),
                ),
                child: Image.network(
                  imageUrl,
                  height: 150,
                  width: double.infinity,
                  fit: BoxFit.cover,
                  errorBuilder: (context, error, stackTrace) {
                    return Container(
                      height: 150,
                      color: Colors.grey[300],
                      child: const Icon(Icons.image, size: 48),
                    );
                  },
                ),
              )
            else
              ClipRRect(
                borderRadius: const BorderRadius.vertical(
                  top: Radius.circular(12),
                ),
                child: Container(
                  height: 150,
                  width: double.infinity,
                  color: Colors.grey[300],
                  child: const Icon(Icons.image, size: 48),
                ),
              ),
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Expanded(
                        child: Text(
                          name,
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      if (isCancelled)
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 8,
                            vertical: 4,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.red,
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: const Text(
                            'Cancelada',
                            style: TextStyle(
                              color: Colors.white,
                              fontSize: 12,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      const Icon(
                        Icons.location_on,
                        size: 16,
                        color: Colors.grey,
                      ),
                      const SizedBox(width: 4),
                      Text(
                        '$city, $country',
                        style: const TextStyle(color: Colors.grey),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      const Icon(
                        Icons.calendar_today,
                        size: 16,
                        color: Colors.grey,
                      ),
                      const SizedBox(width: 4),
                      Text(
                        '$startingDate - $endDate',
                        style: const TextStyle(color: Colors.grey),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '\$${price.toStringAsFixed(0)} total',
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFFFF385C),
                    ),
                  ),
                  if (_showUpcoming && !isCancelled) ...[
                    const SizedBox(height: 12),
                    SizedBox(
                      width: double.infinity,
                      child: OutlinedButton.icon(
                        onPressed: () => _showCancelDialog(reservationId),
                        icon: const Icon(Icons.cancel, color: Colors.red),
                        label: const Text(
                          'Cancelar Reservación',
                          style: TextStyle(color: Colors.red),
                        ),
                        style: OutlinedButton.styleFrom(
                          side: const BorderSide(color: Colors.red),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(8),
                          ),
                        ),
                      ),
                    ),
                  ],
                  if (!_showUpcoming && !isCancelled) ...[
                    const SizedBox(height: 12),
                    SizedBox(
                      width: double.infinity,
                      child: OutlinedButton.icon(
                        onPressed: () {
                          Navigator.pushNamed(
                            context,
                            Routes.touristReviewScreen,
                            arguments: {'rentalId': id, 'rentalName': name},
                          );
                        },
                        icon: const Icon(Icons.star, color: Color(0xFFFF385C)),
                        label: const Text(
                          'Añadir Reseña',
                          style: TextStyle(color: Color(0xFFFF385C)),
                        ),
                        style: OutlinedButton.styleFrom(
                          side: const BorderSide(color: Color(0xFFFF385C)),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(8),
                          ),
                        ),
                      ),
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _showCancelDialog(String reservationId) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Cancelar Reservación'),
        content: const Text(
          '¿Estás seguro de que quieres cancelar esta reservación? Esta acción no se puede deshacer.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('No'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text(
              'Sí, Cancelar',
              style: TextStyle(color: Colors.red),
            ),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      await _cancelReservation(reservationId);
    }
  }

  Future<void> _cancelReservation(String reservationId) async {
    final secureStorage = context.read<SecureStorageProvider>();
    final token = await secureStorage.read('token');

    if (token == null) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Sesión expirada'),
            backgroundColor: Colors.red,
          ),
        );
      }
      return;
    }

    final success = await _touristProvider.cancelReservationHost(
      token: token,
      reservationId: reservationId,
    );

    if (mounted) {
      if (success) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Reservación cancelada'),
            backgroundColor: Color(0xFFFF385C),
          ),
        );
        _loadReservations();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(_touristProvider.error ?? 'Error al cancelar'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }
}

