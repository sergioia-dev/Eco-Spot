import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/domain/models/rental.dart';
import 'package:frontend/domain/providers/host_provider.dart';
import 'package:frontend/domain/providers/secure_storage_provider.dart';
import 'package:frontend/presentation/routes/routes.dart';
import 'package:frontend/presentation/views/host/rental_form_screen.dart';

class HostHomeScreen extends StatefulWidget {
  const HostHomeScreen({super.key});

  @override
  State<HostHomeScreen> createState() => _HostHomeScreenState();
}

class _HostHomeScreenState extends State<HostHomeScreen> {
  late HostProvider _hostProvider;
  bool _includeDisabled = false;

  @override
  void initState() {
    super.initState();
    _hostProvider = HostProvider();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadRentals();
    });
  }

  Future<void> _loadRentals() async {
    final secureStorage = context.read<SecureStorageProvider>();
    final token = await secureStorage.read('token');
    if (token != null) {
      await _hostProvider.loadRentals(token, includeDisabled: _includeDisabled);
    }
  }

  void _toggleIncludeDisabled() {
    setState(() {
      _includeDisabled = !_includeDisabled;
    });
    _loadRentals();
  }

  Future<void> _logout() async {
    final secureStorage = context.read<SecureStorageProvider>();
    await secureStorage.deleteAll();
    if (mounted) {
      Navigator.pushReplacementNamed(context, Routes.signInScreen);
    }
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider.value(
      value: _hostProvider,
      child: Scaffold(
        backgroundColor: const Color(0xFFF7F7F7),
        appBar: AppBar(
          title: const Text(
            'Host Concierge',
            style: TextStyle(
              color: Color(0xFFFF385C),
              fontWeight: FontWeight.w700,
            ),
          ),
          backgroundColor: Colors.transparent,
          elevation: 0,
          foregroundColor: const Color(0xFFFF385C),
          actions: [
            IconButton(icon: const Icon(Icons.logout), onPressed: _logout),
          ],
        ),
        body: Consumer<HostProvider>(
          builder: (context, hostProvider, child) {
            return Column(
              children: [
                Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 16,
                    vertical: 8,
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      FilterChip(
                        label: Text(
                          _includeDisabled ? 'All rentals' : 'Active only',
                        ),
                        selected: _includeDisabled,
                        onSelected: (_) => _toggleIncludeDisabled(),
                        selectedColor: const Color(
                          0xFFFF385C,
                        ).withValues(alpha: 0.2),
                        checkmarkColor: const Color(0xFFFF385C),
                        labelStyle: TextStyle(
                          color: _includeDisabled
                              ? const Color(0xFFFF385C)
                              : Colors.grey[700],
                        ),
                      ),
                    ],
                  ),
                ),
                Expanded(child: _buildRentalsList(hostProvider)),
              ],
            );
          },
        ),
        floatingActionButton: FloatingActionButton.extended(
          backgroundColor: const Color(0xFFFF385C),
          foregroundColor: Colors.white,
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const RentalFormScreen()),
            );
          },
          icon: const Icon(Icons.add),
          label: const Text('Add Property'),
        ),
      ),
    );
  }

  Widget _buildRentalsList(HostProvider hostProvider) {
    if (hostProvider.isLoading) {
      return const Center(
        child: CircularProgressIndicator(color: Color(0xFFFF385C)),
      );
    }

    if (hostProvider.error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'Error: ${hostProvider.error}',
              style: const TextStyle(color: Colors.red),
            ),
            const SizedBox(height: 16),
            ElevatedButton(onPressed: _loadRentals, child: const Text('Retry')),
          ],
        ),
      );
    }

    if (hostProvider.rentals.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.home_outlined, size: 64, color: Colors.grey),
            const SizedBox(height: 16),
            const Text(
              'No rentals yet',
              style: TextStyle(fontSize: 18, color: Colors.grey),
            ),
            const SizedBox(height: 8),
            const Text(
              'Create your first rental!',
              style: TextStyle(color: Colors.grey),
            ),
            const SizedBox(height: 24),
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const RentalFormScreen(),
                  ),
                );
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFFF385C),
                foregroundColor: Colors.white,
              ),
              child: const Text('Create Rental'),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadRentals,
      color: const Color(0xFFFF385C),
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: hostProvider.rentals.length,
        itemBuilder: (context, index) {
          final rental = hostProvider.rentals[index];
          return GestureDetector(
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => RentalFormScreen(rental: rental),
                ),
              );
            },
            onLongPress: () async {
              final confirm = await showDialog<bool>(
                context: context,
                builder: (context) => AlertDialog(
                  title: const Text('Delete Rental'),
                  content: Text(
                    'Are you sure you want to delete "${rental.name}"?',
                  ),
                  actions: [
                    TextButton(
                      onPressed: () => Navigator.pop(context, false),
                      child: const Text('Cancel'),
                    ),
                    TextButton(
                      onPressed: () => Navigator.pop(context, true),
                      style: TextButton.styleFrom(foregroundColor: Colors.red),
                      child: const Text('Delete'),
                    ),
                  ],
                ),
              );

              if (confirm == true && context.mounted) {
                final secureStorage = context.read<SecureStorageProvider>();
                final token = await secureStorage.read('token');
                if (token != null && context.mounted) {
                  final success = await hostProvider.deleteRental(
                    token,
                    rental.id,
                  );
                  if (!success && context.mounted) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text(
                          hostProvider.error ?? 'Failed to delete rental',
                        ),
                        backgroundColor: Colors.red,
                      ),
                    );
                  }
                }
              }
            },
            child: _RentalCard(
              rental: rental,
              onToggle: (enabled) async {
                final confirm = await showDialog<bool>(
                  context: context,
                  builder: (context) => AlertDialog(
                    title: Text(enabled ? 'Enable Rental' : 'Disable Rental'),
                    content: Text(
                      enabled
                          ? 'Are you sure you want to enable this rental?'
                          : 'Are you sure you want to disable this rental?',
                    ),
                    actions: [
                      TextButton(
                        onPressed: () => Navigator.pop(context, false),
                        child: const Text('Cancel'),
                      ),
                      TextButton(
                        onPressed: () => Navigator.pop(context, true),
                        child: Text(enabled ? 'Enable' : 'Disable'),
                      ),
                    ],
                  ),
                );

                if (confirm == true && context.mounted) {
                  final secureStorage = context.read<SecureStorageProvider>();
                  final token = await secureStorage.read('token');
                  if (token != null && context.mounted) {
                    final success = await hostProvider.toggleRentalEnable(
                      token,
                      rental.id,
                      enabled,
                    );
                    if (!success && context.mounted) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(
                          content: Text(
                            hostProvider.error ?? 'Failed to toggle rental',
                          ),
                          backgroundColor: Colors.red,
                        ),
                      );
                    }
                  }
                }
              },
              onViewReservations: () {
                Navigator.pushNamed(
                  context,
                  Routes.reservationsScreen,
                  arguments: {'rentalId': rental.id, 'rentalName': rental.name},
                );
              },
            ),
          );
        },
      ),
    );
  }
}

class _RentalCard extends StatelessWidget {
  final Rental rental;
  final Function(bool)? onToggle;
  final VoidCallback? onViewReservations;

  const _RentalCard({
    required this.rental,
    this.onToggle,
    this.onViewReservations,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (rental.images.isNotEmpty)
            ClipRRect(
              borderRadius: const BorderRadius.vertical(
                top: Radius.circular(12),
              ),
              child: Image.network(
                rental.images.first.imageUrl,
                height: 150,
                width: double.infinity,
                fit: BoxFit.cover,
                errorBuilder: (context, error, stackTrace) {
                  return Container(
                    height: 150,
                    color: Colors.grey[300],
                    child: const Icon(Icons.broken_image, size: 48),
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
                child: const Icon(Icons.home, size: 48, color: Colors.grey),
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
                        rental.name,
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    Switch(
                      value: rental.isEnable,
                      activeTrackColor: const Color(0xFFFF385C),
                      onChanged: onToggle != null
                          ? (value) => onToggle!(value)
                          : null,
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    const Icon(Icons.location_on, size: 16, color: Colors.grey),
                    const SizedBox(width: 4),
                    Text(
                      '${rental.city}, ${rental.country}',
                      style: const TextStyle(color: Colors.grey),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    const Icon(Icons.people, size: 16, color: Colors.grey),
                    const SizedBox(width: 4),
                    Text(
                      '${rental.peopleQuantity} guests',
                      style: const TextStyle(color: Colors.grey),
                    ),
                    const SizedBox(width: 16),
                    const Icon(Icons.bed, size: 16, color: Colors.grey),
                    const SizedBox(width: 4),
                    Text(
                      '${rental.rooms} rooms',
                      style: const TextStyle(color: Colors.grey),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    if ((rental.reviewAverage ?? 0) > 0)
                      Row(
                        children: [
                          const Icon(Icons.star, size: 16, color: Colors.amber),
                          const SizedBox(width: 4),
                          Text(
                            (rental.reviewAverage ?? 0).toStringAsFixed(1),
                            style: const TextStyle(fontWeight: FontWeight.bold),
                          ),
                        ],
                      ),
                    Text(
                      '\$${rental.valueNight.toStringAsFixed(0)}/night',
                      style: const TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFFFF385C),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                SizedBox(
                  width: double.infinity,
                  child: OutlinedButton.icon(
                    onPressed: onViewReservations,
                    icon: const Icon(Icons.calendar_today),
                    label: const Text('See Reservations'),
                    style: OutlinedButton.styleFrom(
                      foregroundColor: const Color(0xFFFF385C),
                      side: const BorderSide(color: Color(0xFFFF385C)),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

