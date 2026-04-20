import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/domain/models/rental.dart';
import 'package:frontend/domain/providers/host_provider.dart';
import 'package:frontend/domain/providers/secure_storage_provider.dart';
import 'package:frontend/presentation/routes/routes.dart';

class HostHomeScreen extends StatefulWidget {
  const HostHomeScreen({super.key});

  @override
  State<HostHomeScreen> createState() => _HostHomeScreenState();
}

class _HostHomeScreenState extends State<HostHomeScreen> {
  late HostProvider _hostProvider;

  @override
  void initState() {
    super.initState();
    _hostProvider = HostProvider();
    _loadRentals();
  }

  Future<void> _loadRentals() async {
    final secureStorage = context.read<SecureStorageProvider>();
    final token = await secureStorage.read('token');
    if (token != null) {
      await _hostProvider.loadRentals(token);
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
        ),
        body: Consumer<HostProvider>(
          builder: (context, hostProvider, child) {
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
                    ElevatedButton(
                      onPressed: _loadRentals,
                      child: const Text('Retry'),
                    ),
                  ],
                ),
              );
            }

            if (hostProvider.rentals.isEmpty) {
              return Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(
                      Icons.home_outlined,
                      size: 64,
                      color: Colors.grey,
                    ),
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
                        Navigator.pushNamed(context, Routes.createRentalScreen);
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
                  return _RentalCard(rental: rental);
                },
              ),
            );
          },
        ),
        floatingActionButton: FloatingActionButton.extended(
          backgroundColor: const Color(0xFFFF385C),
          foregroundColor: Colors.white,
          onPressed: () {
            Navigator.pushNamed(context, Routes.createRentalScreen);
          },
          icon: const Icon(Icons.add),
          label: const Text('Add Property'),
        ),
      ),
    );
  }
}

class _RentalCard extends StatelessWidget {
  final Rental rental;

  const _RentalCard({required this.rental});

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
              child: Container(
                height: 150,
                width: double.infinity,
                color: Colors.grey[300],
                child: const Icon(Icons.image, size: 48, color: Colors.grey),
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
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 8,
                        vertical: 4,
                      ),
                      decoration: BoxDecoration(
                        color: rental.isEnable
                            ? Colors.green[100]
                            : Colors.red[100],
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Text(
                        rental.isEnable ? 'Active' : 'Inactive',
                        style: TextStyle(
                          color: rental.isEnable ? Colors.green : Colors.red,
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
                    if (rental.reviewAverage > 0)
                      Row(
                        children: [
                          const Icon(Icons.star, size: 16, color: Colors.amber),
                          const SizedBox(width: 4),
                          Text(
                            rental.reviewAverage.toStringAsFixed(1),
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
              ],
            ),
          ),
        ],
      ),
    );
  }
}
