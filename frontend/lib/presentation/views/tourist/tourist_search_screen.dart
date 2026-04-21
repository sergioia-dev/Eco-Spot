import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/domain/models/rental.dart';
import 'package:frontend/domain/providers/tourist_provider.dart';
import 'package:frontend/domain/providers/secure_storage_provider.dart';
import 'package:frontend/presentation/widgets/tourist_sidebar.dart';
import 'package:frontend/presentation/widgets/tourist_bottom_nav.dart';

class TouristSearchScreen extends StatefulWidget {
  const TouristSearchScreen({super.key});

  @override
  State<TouristSearchScreen> createState() => _TouristSearchScreenState();
}

class _TouristSearchScreenState extends State<TouristSearchScreen> {
  late TouristProvider _touristProvider;
  int _selectedCategory = 0;
  int _currentNavIndex = 1;
  final TextEditingController _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _touristProvider = TouristProvider();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _search() async {
    final searchBy = _searchController.text.trim();
    if (searchBy.isEmpty) return;

    final secureStorage = context.read<SecureStorageProvider>();
    final token = await secureStorage.read('token');
    if (token != null) {
      String? category;
      switch (_selectedCategory) {
        case 1:
          category = 'RENTAL';
          break;
        case 2:
          category = 'BUSINESS';
          break;
        case 3:
          category = 'EXPERIENCE';
          break;
        default:
          category = null;
      }
      await _touristProvider.searchItems(
        token: token,
        searchBy: searchBy,
        category: category,
      );
    }
  }

  void _selectCategory(int index) {
    setState(() {
      _selectedCategory = index;
    });
    if (_searchController.text.trim().isNotEmpty) {
      _search();
    }
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider.value(
      value: _touristProvider,
      child: Scaffold(
        backgroundColor: const Color(0xFFF7F7F7),
        appBar: AppBar(
          title: const Text(
            'Search',
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
        body: Column(
          children: [
            Padding(
              padding: const EdgeInsets.all(16),
              child: TextField(
                controller: _searchController,
                decoration: InputDecoration(
                  hintText: 'Search for rentals, experiences...',
                  prefixIcon: const Icon(Icons.search),
                  suffixIcon: IconButton(
                    icon: const Icon(Icons.send),
                    onPressed: _search,
                  ),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                onSubmitted: (_) => _search(),
              ),
            ),
            _buildCategoryFilter(),
            Expanded(child: _buildResultsList()),
          ],
        ),
        bottomNavigationBar: TouristBottomNav(
          currentIndex: _currentNavIndex,
          onTap: (index) {
            setState(() {
              _currentNavIndex = index;
            });
            if (index == 0) {
              Navigator.pushReplacementNamed(context, 'tourist_home');
            }
          },
        ),
      ),
    );
  }

  Widget _buildCategoryFilter() {
    final categories = ['All', 'Rentals', 'Businesses', 'Experiences'];
    return SizedBox(
      height: 50,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 16),
        itemCount: categories.length,
        itemBuilder: (context, index) {
          return Padding(
            padding: const EdgeInsets.only(right: 8),
            child: FilterChip(
              label: Text(categories[index]),
              selected: _selectedCategory == index,
              onSelected: (_) => _selectCategory(index),
              selectedColor: const Color(0xFFFF385C).withValues(alpha: 0.2),
              checkmarkColor: const Color(0xFFFF385C),
              labelStyle: TextStyle(
                color: _selectedCategory == index
                    ? const Color(0xFFFF385C)
                    : Colors.grey[700],
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildResultsList() {
    return Consumer<TouristProvider>(
      builder: (context, touristProvider, child) {
        if (touristProvider.isLoading) {
          return const Center(
            child: CircularProgressIndicator(color: Color(0xFFFF385C)),
          );
        }

        final results = touristProvider.searchResults?.results ?? [];

        if (results.isEmpty) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.search,
                  size: 64,
                  color: Colors.grey,
                ),
                const SizedBox(height: 16),
                const Text(
                  'Search for something',
                  style: TextStyle(fontSize: 18, color: Colors.grey),
                ),
                const SizedBox(height: 8),
                const Text(
                  'Enter a search term to find rentals, experiences...',
                  style: TextStyle(color: Colors.grey),
                ),
              ],
            ),
          );
        }

        return RefreshIndicator(
          onRefresh: _search,
          color: const Color(0xFFFF385C),
          child: ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: results.length,
            itemBuilder: (context, index) {
              return _buildItemCard(results[index]);
            },
          ),
        );
      },
    );
  }

  Widget _buildItemCard(Map<String, dynamic> item) {
    final name = item['name'] ?? 'Unknown';
    final city = item['city'] ?? '';
    final country = item['country'] ?? '';
    final valueNight = (item['valueNight'] as num?)?.toDouble() ?? 0.0;
    final images = item['images'] as List<dynamic>? ?? [];
    String? imageUrl;

    if (images.isNotEmpty) {
      final firstImage = images.first;
      if (firstImage is Map<String, dynamic>) {
        imageUrl =
            "http://10.0.2.2:8080/images/${firstImage['id']}.${firstImage['extension']}";
      } else if (firstImage is Rental) {
        imageUrl = firstImage.images.isNotEmpty ? firstImage.images.first.imageUrl : null;
      }
    }

    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (imageUrl != null)
            ClipRRect(
              borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
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
              borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
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