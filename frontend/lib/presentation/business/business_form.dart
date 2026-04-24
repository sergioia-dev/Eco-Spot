// Modelo sugerido para Negocio
import 'package:flutter/material.dart';

class BusinessModel {
  String? id, name, description, contact, city, country, location, menu;
  bool isEnable = true;
  List<String> images = [];
}

class AddBusinessScreen extends StatefulWidget {
  @override
  _AddBusinessScreenState createState() => _AddBusinessScreenState();
}

class _AddBusinessScreenState extends State<AddBusinessScreen> {
  bool _isEnable = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(title: Text("Registrar Negocio", style: TextStyle(color: Colors.black)), backgroundColor: Colors.white, elevation: 0),
      body: SingleChildScrollView(
        padding: EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildGalleryBox(), // Campo images
            SizedBox(height: 25),
            
            _label("NOMBRE DEL NEGOCIO"),
            _input("name", "Nombre comercial"),
            
            _label("DESCRIPCIÓN"),
            _input("description", "Describe la esencia de tu negocio...", maxLines: 4),
            
            _label("CONTACTO"),
            _input("contact", "Teléfono o email de contacto"),
            
            Row(
              children: [
                Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_label("CIUDAD"), _input("city", "Florencia")])),
                SizedBox(width: 15),
                Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_label("PAÍS"), _input("country", "Italia")])),
              ],
            ),
            
            _label("UBICACIÓN (MAPS)"),
            _input("location", "Enlace de Google Maps"),
            
            _label("ENLACE DEL MENÚ / CARTA"),
            _input("menu", "https://tunegocio.com/menu", isUrl: true),
            
            // Campo isEnable
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 10),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text("NEGOCIO ACTIVO", style: TextStyle(fontWeight: FontWeight.bold, color: Colors.grey[700])),
                  Switch(
                    value: _isEnable,
                    activeColor: Color(0xFFD6334D),
                    onChanged: (val) => setState(() => _isEnable = val),
                  ),
                ],
              ),
            ),
            
            SizedBox(height: 30),
            _buildSubmitButton(),
            SizedBox(height: 40),
          ],
        ),
      ),
    );
  }

  // --- Mismos Helpers adaptados al estilo de Negocios ---
  Widget _label(String text) => Padding(
    padding: EdgeInsets.only(top: 15, bottom: 8),
    child: Text(text, style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.grey[800])),
  );

  Widget _input(String field, String hint, {int maxLines = 1, bool isUrl = false}) {
    return Container(
      decoration: BoxDecoration(color: Color(0xFFF5F6F9), borderRadius: BorderRadius.circular(12)),
      child: TextField(
        maxLines: maxLines,
        decoration: InputDecoration(
          hintText: hint,
          contentPadding: EdgeInsets.all(16),
          border: InputBorder.none,
          prefixIcon: isUrl ? Icon(Icons.restaurant_menu, size: 18) : null,
        ),
      ),
    );
  }

  Widget _buildGalleryBox() {
    return Container(
      width: double.infinity, height: 140,
      decoration: BoxDecoration(color: Color(0xFFFFFBFC), borderRadius: BorderRadius.circular(15), border: Border.all(color: Colors.pink[50]!)),
      child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
        Icon(Icons.cloud_upload_outlined, color: Color(0xFFD6334D), size: 30),
        SizedBox(height: 8),
        Text("Galería de Fotos (images)", style: TextStyle(fontWeight: FontWeight.bold))
      ]),
    );
  }

  Widget _buildSubmitButton() {
    return SizedBox(
      width: double.infinity, height: 55,
      child: ElevatedButton(
        style: ElevatedButton.styleFrom(backgroundColor: Color(0xFFD6334D), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12))),
        onPressed: () {},
        child: Text("Enviar para Revisión", style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
      ),
    );
  }
}