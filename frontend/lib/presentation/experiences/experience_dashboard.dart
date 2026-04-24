import 'package:flutter/material.dart';

class ExperiencesDashboard extends StatelessWidget {
  // Lista de ejemplo con tus datos estructurados
  final List<Map<String, dynamic>> experiences = [
    {
      "id": "1",
      "name": "Private Wine Tasting",
      "description": "Exclusive tour...",
      "price": "240",
      "city": "Stellenbosch",
      "country": "South Africa",
      "startingDate": "2024-05-01",
      "isEnable": true,
      "images": ["https://via.placeholder.com/400"]
    },
    // Añade más aquí...
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Color(0xFFF8F9FA),
      appBar: AppBar(
        title: Text("Mis Experiencias", style: TextStyle(color: Colors.black, fontWeight: FontWeight.bold)),
        backgroundColor: Colors.white,
        elevation: 0,
        actions: [CircleAvatar(backgroundColor: Colors.grey[200], child: Icon(Icons.person, color: Colors.black))],
      ),
      body: ListView.builder(
        padding: EdgeInsets.all(16),
        itemCount: experiences.length,
        itemBuilder: (context, index) {
          final item = experiences[index];
          return Container(
            margin: EdgeInsets.only(bottom: 20),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(20),
              boxShadow: [BoxShadow(color: Colors.black12, blurRadius: 10, offset: Offset(0, 4))],
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Stack(
                  children: [
                    ClipRRect(
                      borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
                      child: Image.network(item['images'][0], height: 180, width: double.infinity, fit: BoxFit.cover),
                    ),
                    Positioned(
                      top: 12, left: 12,
                      child: _statusBadge(item['isEnable']),
                    ),
                  ],
                ),
                Padding(
                  padding: EdgeInsets.all(16),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(item['name'], style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                          Text("${item['city']}, ${item['country']}", style: TextStyle(color: Colors.grey)),
                        ],
                      ),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.end,
                        children: [
                          Text("\$${item['price']}", style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFFE91E63))),
                          Text("por persona", style: TextStyle(fontSize: 10, color: Colors.grey)),
                        ],
                      )
                    ],
                  ),
                )
              ],
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: Color(0xFFE91E63),
        child: Icon(Icons.add),
        onPressed: () { /* Navegar a crear experiencia */ },
      ),
    );
  }

  Widget _statusBadge(bool isEnable) {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: isEnable ? Colors.green[400] : Colors.grey[600],
        borderRadius: BorderRadius.circular(10),
      ),
      child: Text(isEnable ? "ACTIVA" : "INACTIVA", style: TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.bold)),
    );
  }
}