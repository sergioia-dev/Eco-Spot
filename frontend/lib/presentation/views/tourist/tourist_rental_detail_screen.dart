import 'package:flutter/material.dart';
import 'package:frontend/domain/models/rental.dart';
import 'package:frontend/domain/models/rental_image.dart';
import 'package:frontend/domain/models/review.dart';
import 'package:frontend/presentation/routes/routes.dart';

class TouristRentalDetailScreen extends StatefulWidget {
  final Rental rental;

  const TouristRentalDetailScreen({super.key, required this.rental});

  @override
  State<TouristRentalDetailScreen> createState() =>
      _TouristRentalDetailScreenState();
}

class _TouristRentalDetailScreenState extends State<TouristRentalDetailScreen> {
  final PageController _pageController = PageController();
  int _currentPage = 0;

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final rental = widget.rental;

    return Scaffold(
      backgroundColor: const Color(0xFFF7F7F7),
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            expandedHeight: 300,
            pinned: true,
            backgroundColor: const Color(0xFFFF385C),
            foregroundColor: Colors.white,
            flexibleSpace: FlexibleSpaceBar(
              background: _buildImageCarousel(rental.images),
            ),
          ),
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    rental.name,
                    style: const TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                    ),
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
                        '${rental.city}, ${rental.country}',
                        style: const TextStyle(color: Colors.grey),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  if (rental.reviewAverage != null) ...[
                    Row(
                      children: [
                        const Icon(
                          Icons.star,
                          size: 16,
                          color: Color(0xFFFF385C),
                        ),
                        const SizedBox(width: 4),
                        Text(
                          rental.reviewAverage!.toStringAsFixed(1),
                          style: const TextStyle(
                            fontWeight: FontWeight.bold,
                            color: Color(0xFFFF385C),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                  ],
                  const Text(
                    'Detalles',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  _buildAmenities(rental),
                  const SizedBox(height: 16),
                  if (rental.description != null) ...[
                    const Text(
                      'Descripción',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      rental.description!,
                      style: const TextStyle(color: Colors.grey, height: 1.5),
                    ),
                  ],
                  const SizedBox(height: 16),
                  const Text(
                    'Opiniones',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  _buildReviews(rental.reviews),
                  const SizedBox(height: 100),
                ],
              ),
            ),
          ),
        ],
      ),
      bottomNavigationBar: _buildBottomBar(rental),
    );
  }

  Widget _buildImageCarousel(List<RentalImage> images) {
    if (images.isEmpty) {
      return Container(
        color: Colors.grey[300],
        child: const Icon(Icons.image, size: 64, color: Colors.grey),
      );
    }

    return Stack(
      alignment: Alignment.bottomCenter,
      children: [
        PageView.builder(
          controller: _pageController,
          onPageChanged: (index) {
            setState(() {
              _currentPage = index;
            });
          },
          itemCount: images.length,
          itemBuilder: (context, index) {
            final image = images[index];
            final imageUrl =
                'http://10.0.2.2:8080/images/${image.id}.${image.extension}';
            return Image.network(
              imageUrl,
              fit: BoxFit.cover,
              errorBuilder: (context, error, stackTrace) {
                return Container(
                  color: Colors.grey[300],
                  child: const Icon(Icons.image, size: 64),
                );
              },
            );
          },
        ),
        if (images.length > 1)
          Positioned(
            bottom: 16,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List.generate(
                images.length,
                (index) => Container(
                  margin: const EdgeInsets.symmetric(horizontal: 4),
                  width: 8,
                  height: 8,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: _currentPage == index
                        ? Colors.white
                        : Colors.white.withValues(alpha: 0.5),
                  ),
                ),
              ),
            ),
          ),
      ],
    );
  }

  Widget _buildAmenities(Rental rental) {
    return Wrap(
      spacing: 16,
      runSpacing: 8,
      children: [
        _buildAmenity(Icons.people, '${rental.peopleQuantity} huéspedes'),
        _buildAmenity(Icons.bed, '${rental.rooms} habitaciones'),
        _buildAmenity(Icons.bathtub, '${rental.bathrooms} baños'),
        _buildAmenity(Icons.square_foot, '${rental.size} m²'),
      ],
    );
  }

  Widget _buildAmenity(IconData icon, String text) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 20, color: const Color(0xFFFF385C)),
        const SizedBox(width: 4),
        Text(text, style: const TextStyle(fontSize: 14)),
      ],
    );
  }

  Widget _buildReviews(List<dynamic>? reviews) {
    if (reviews == null || reviews.isEmpty) {
      return const Text(
        'No hay opiniones aún',
        style: TextStyle(color: Colors.grey),
      );
    }

    final parsedReviews = reviews
        .map((e) => Review.fromJson(e as Map<String, dynamic>))
        .toList();

    if (parsedReviews.isEmpty) {
      return const Text(
        'No hay opiniones aún',
        style: TextStyle(color: Colors.grey),
      );
    }

    return Column(
      children: parsedReviews
          .map((review) => _buildReviewCard(review))
          .toList(),
    );
  }

  Widget _buildReviewCard(Review review) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: List.generate(
              5,
              (index) => Icon(
                index < review.qualification ? Icons.star : Icons.star_border,
                size: 16,
                color: const Color(0xFFFF385C),
              ),
            ),
          ),
          if (review.opinion != null && review.opinion!.isNotEmpty) ...[
            const SizedBox(height: 8),
            Text(review.opinion!, style: const TextStyle(color: Colors.grey)),
          ],
        ],
      ),
    );
  }

  Widget _buildBottomBar(Rental rental) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.1),
            blurRadius: 8,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: SafeArea(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '\$${rental.valueNight.toStringAsFixed(0)}',
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFFFF385C),
                  ),
                ),
                const Text(
                  'por noche',
                  style: TextStyle(color: Colors.grey, fontSize: 12),
                ),
              ],
            ),
            ElevatedButton(
              onPressed: () {
                Navigator.pushNamed(
                  context,
                  Routes.touristReservationFormScreen,
                  arguments: {
                    'rentalId': rental.id,
                    'rentalName': rental.name,
                    'valueNight': rental.valueNight,
                  },
                );
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFFF385C),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(
                  horizontal: 24,
                  vertical: 12,
                ),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
              child: const Text(
                'Reservar',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
