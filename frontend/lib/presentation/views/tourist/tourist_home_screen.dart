import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/domain/models/rental.dart';
import 'package:frontend/domain/providers/tourist_provider.dart';
import 'package:frontend/domain/providers/secure_storage_provider.dart';
import 'package:frontend/presentation/widgets/tourist_sidebar.dart';
import 'package:frontend/presentation/widgets/tourist_bottom_nav.dart';

class TouristHomeScreen extends StatefulWidget {
  const TouristHomeScreen({super.key});

  @override
  State<TouristHomeScreen> createState() => _TouristHomeScreenState();
}

class _TouristHomeScreenState extends State<TouristHomeScreen> {
  late TouristProvider _touristProvider;
  int _selectedCategory = 0;
  int _currentNavIndex = 0;

  @override
  void initState() {
    super.initState();
    _touristProvider = TouristProvider();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadItems();
    });
  }

  Future<void> _loadItems() async {
    final secureStorage = context.read<SecureStorageProvider>();
    final token = await secureStorage.read('token');
    if (token != null) {
      await _touristProvider.loadItemsByLocation(token);
    }
  }

  void _selectCategory(int index) {
    setState(() {
      _selectedCategory = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider.value(
      value: _touristProvider,
      child: Scaffold(
        backgroundColor: const Color(0xFFF7F7F7),
        appBar: AppBar(
          title: const Text(
            'EcoSpot',
            style: TextStyle(
              color: Color(0xFFFF385C),
              fontWeight: FontWeight.w700,
            ),
          ),
          backgroundColor: Colors.transparent,
          elevation: 0,
          foregroundColor: const Color(0xFFFF385C),
          actions: [Icon(Icons.notifications)],
        ),

        drawer: const TouristSidebar(),
        body: Consumer<TouristProvider>(
          builder: (context, touristProvider, child) {
            return Column(
              children: [
                _buildCategoryFilter(),
                Expanded(child: _buildItemsList(touristProvider)),
              ],
            );
          },
        ),
        bottomNavigationBar: TouristBottomNav(
          currentIndex: _currentNavIndex,
          onTap: (index) {
            setState(() {
              _currentNavIndex = index;
            });
            if (index == 1) {
              Navigator.pushNamed(context, 'tourist_search');
            }
          },
        ),
      ),
    );
  }

  Widget _buildCategoryFilter() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          FilterChip(
            label: const Text('Rentals'),
            selected: _selectedCategory == 0,
            onSelected: (_) => _selectCategory(0),
            selectedColor: const Color(0xFFFF385C).withValues(alpha: 0.2),
            checkmarkColor: const Color(0xFFFF385C),
            labelStyle: TextStyle(
              color: _selectedCategory == 0
                  ? const Color(0xFFFF385C)
                  : Colors.grey[700],
            ),
          ),
          const SizedBox(width: 8),
          FilterChip(
            label: const Text('Businesses'),
            selected: _selectedCategory == 1,
            onSelected: (_) => _selectCategory(1),
            selectedColor: const Color(0xFFFF385C).withValues(alpha: 0.2),
            checkmarkColor: const Color(0xFFFF385C),
            labelStyle: TextStyle(
              color: _selectedCategory == 1
                  ? const Color(0xFFFF385C)
                  : Colors.grey[700],
            ),
          ),
          const SizedBox(width: 8),
          FilterChip(
            label: const Text('Experiences'),
            selected: _selectedCategory == 2,
            onSelected: (_) => _selectCategory(2),
            selectedColor: const Color(0xFFFF385C).withValues(alpha: 0.2),
            checkmarkColor: const Color(0xFFFF385C),
            labelStyle: TextStyle(
              color: _selectedCategory == 2
                  ? const Color(0xFFFF385C)
                  : Colors.grey[700],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildItemsList(TouristProvider touristProvider) {
    if (touristProvider.isLoading) {
      return const Center(
        child: CircularProgressIndicator(color: Color(0xFFFF385C)),
      );
    }

    if (touristProvider.error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'Error: ${touristProvider.error}',
              style: const TextStyle(color: Colors.red),
            ),
            const SizedBox(height: 16),
            ElevatedButton(onPressed: _loadItems, child: const Text('Retry')),
          ],
        ),
      );
    }

    final items = _getSelectedItems(touristProvider);

    if (items.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              _selectedCategory == 0
                  ? Icons.home_outlined
                  : _selectedCategory == 1
                  ? Icons.business
                  : Icons.explore,
              size: 64,
              color: Colors.grey,
            ),
            const SizedBox(height: 16),
            Text(
              _selectedCategory == 0
                  ? 'No rentals nearby'
                  : _selectedCategory == 1
                  ? 'No businesses nearby'
                  : 'No experiences nearby',
              style: const TextStyle(fontSize: 18, color: Colors.grey),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadItems,
      color: const Color(0xFFFF385C),
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: items.length,
        itemBuilder: (context, index) {
          final item = items[index];
          return _buildItemCard(item, _selectedCategory == 0);
        },
      ),
    );
  }

  List<dynamic> _getSelectedItems(TouristProvider provider) {
    final touristItem = provider.touristItem;
    if (touristItem == null) return [];

    switch (_selectedCategory) {
      case 0:
        return touristItem.rentals.cast<dynamic>();
      case 1:
        return touristItem.businesses.cast<dynamic>();
      case 2:
        return touristItem.experiences.cast<dynamic>();
      default:
        return [];
    }
  }

  Widget _buildItemCard(dynamic item, bool isRental) {
    String name;
    String city;
    String country;
    double valueNight;
    String? imageUrl;

    if (isRental && item is Rental) {
      name = item.name;
      city = item.city;
      country = item.country;
      valueNight = item.valueNight;
      imageUrl = item.images.isNotEmpty ? item.images.first.imageUrl : null;
    } else {
      final map = item as Map<String, dynamic>;
      name = map['name'] ?? 'Unknown';
      city = map['city'] ?? '';
      country = map['country'] ?? '';
      valueNight = (map['valueNight'] as num?)?.toDouble() ?? 0.0;
      final images = map['images'] as List<dynamic>? ?? [];
      imageUrl = images.isNotEmpty
          ? "http://10.0.2.2:8080/images/${images.first['id']}.${images.first['extension']}"
          : null;
    }

    return Card(
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
                Text(
                  name,
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    const Icon(Icons.location_on, size: 16, color: Colors.grey),
                    const SizedBox(width: 4),
                    Text(
                      '$city, $country',
                      style: const TextStyle(color: Colors.grey),
                    ),
                  ],
                ),
                if (valueNight > 0) ...[
                  const SizedBox(height: 8),
                  Text(
                    '\$${valueNight.toStringAsFixed(0)}/night',
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFFFF385C),
                    ),
                  ),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }
}
