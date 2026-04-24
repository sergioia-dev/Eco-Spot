import 'package:flutter/material.dart';

class BusinessPortfolio extends StatelessWidget {
  final List<Map<String, dynamic>> businesses = [
    {
      "id": "101",
      "name": "Avenue Gastronomico",
      "city": "Los Angeles",
      "country": "USA",
      "menu": "https://menu.com/avenue",
      "isEnable": true,
      "images": ["https://via.placeholder.com/400"]
    },
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        leading: Icon(Icons.sort, color: Color(0xFFD6334D)),
        title: Text("Eco Spot Business", style: TextStyle(color: Color(0xFFD6334D), fontWeight: FontWeight.bold)),
        centerTitle: true,
        backgroundColor: Colors.white,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: EdgeInsets.symmetric(horizontal: 20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SizedBox(height: 10),
            Text("PORTAFOLIO ACTIVO", style: TextStyle(letterSpacing: 1.2, fontSize: 10, fontWeight: FontWeight.bold, color: Colors.grey)),
            SizedBox(height: 5),
            Text("Tus Negocios", style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
            SizedBox(height: 20),
            
            // Generamos las tarjetas
            ...businesses.map((biz) => _buildBusinessCard(biz)).toList(),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {},
        backgroundColor: Color(0xFFD6334D),
        icon: Icon(Icons.business_center),
        label: Text("Nuevo Negocio"),
      ),
    );
  }

  Widget _buildBusinessCard(Map<String, dynamic> biz) {
    return Container(
      margin: EdgeInsets.only(bottom: 25),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ClipRRect(
            borderRadius: BorderRadius.circular(15),
            child: Stack(
              children: [
                Image.network(biz['images'][0], height: 200, width: double.infinity, fit: BoxFit.cover),
                if (!biz['isEnable'])
                  Container(
                    height: 200, width: double.infinity, 
                    color: Colors.black45,
                    child: Center(child: Text("DESHABILITADO", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold))),
                  ),
              ],
            ),
          ),
          SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(biz['name'], style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  Text("${biz['city']} • ${biz['country']}", style: TextStyle(color: Colors.grey[600])),
                ],
              ),
              IconButton(
                icon: Icon(Icons.restaurant_menu, color: Color(0xFFD6334D)),
                onPressed: () { /* Abrir URL de biz['menu'] */ },
              )
            ],
          ),
          Divider(height: 30),
        ],
      ),
    );
  }
}