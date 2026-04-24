// Modelo sugerido para Experiencia
import 'package:flutter/material.dart';

class ExperienceModel {
  String? id, name, description, contact, city, country, location;
  double? price;
  DateTime? startingDate, endDate;
  bool isEnable = true;
  List<String> images = [];
}

class AddExperienceScreen extends StatefulWidget {
  @override
  _AddExperienceScreenState createState() => _AddExperienceScreenState();
}

class _AddExperienceScreenState extends State<AddExperienceScreen> {
  bool _isEnable = true; // Control para el campo isEnable

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: Text("Crear Experiencia", style: TextStyle(color: Colors.black)),
        backgroundColor: Colors.white,
        elevation: 0,
        iconTheme: IconThemeData(color: Colors.black),
      ),
      body: SingleChildScrollView(
        padding: EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildPhotoUpload(), // El widget de "images"
            SizedBox(height: 20),
            
            _label("NOMBRE DE LA EXPERIENCIA"),
            _input("name", "Ej: Tour de Vinos Premium"),
            
            _label("DESCRIPCIÓN"),
            _input("description", "Detalles de la experiencia...", maxLines: 3),
            
            Row(
              children: [
                Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_label("CONTACTO"), _input("contact", "+123..."),])),
                SizedBox(width: 10),
                Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_label("PRECIO"), _input("price", "0.00", isNumber: true),])),
              ],
            ),
            
            Row(
              children: [
                Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_label("CIUDAD"), _input("city", "Mendoza"),])),
                SizedBox(width: 10),
                Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_label("PAÍS"), _input("country", "Argentina"),])),
              ],
            ),
            
            _label("UBICACIÓN (GOOGLE MAPS)"),
            _input("location", "URL de ubicación"),
            
            Row(
              children: [
                Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_label("FECHA INICIO"), _input("startingDate", "YYYY-MM-DD"),])),
                SizedBox(width: 10),
                Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [_label("FECHA FIN"), _input("endDate", "YYYY-MM-DD"),])),
              ],
            ),
            
            // Campo isEnable
            ListTile(
              contentPadding: EdgeInsets.zero,
              title: _label("¿ESTÁ HABILITADA?"),
              trailing: Switch(
                value: _isEnable,
                activeColor: Color(0xFFE91E63),
                onChanged: (val) => setState(() => _isEnable = val),
              ),
            ),
            
            SizedBox(height: 30),
            _submitButton("Guardar Experiencia"),
          ],
        ),
      ),
    );
  }

  // Widgets de ayuda (Helpers)
  Widget _label(String text) => Padding(
    padding: EdgeInsets.only(top: 15, bottom: 5),
    child: Text(text, style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.grey[600])),
  );

  Widget _input(String field, String hint, {int maxLines = 1, bool isNumber = false}) {
    return TextField(
      maxLines: maxLines,
      keyboardType: isNumber ? TextInputType.number : TextInputType.text,
      decoration: InputDecoration(
        hintText: hint,
        filled: true,
        fillColor: Colors.grey[100],
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(10), borderSide: BorderSide.none),
      ),
    );
  }

  Widget _buildPhotoUpload() {
    return Container(
      height: 120, width: double.infinity,
      decoration: BoxDecoration(color: Color(0xFFFFF0F3), borderRadius: BorderRadius.circular(15), border: Border.all(color: Colors.pink[100]!)),
      child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
        Icon(Icons.add_a_photo, color: Color(0xFFE91E63)),
        Text("Subir Imágenes", style: TextStyle(color: Color(0xFFE91E63), fontWeight: FontWeight.bold))
      ]),
    );
  }

  Widget _submitButton(String text) => SizedBox(
    width: double.infinity, height: 50,
    child: ElevatedButton(
      onPressed: () {},
      style: ElevatedButton.styleFrom(backgroundColor: Color(0xFFE91E63), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12))),
      child: Text(text, style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
    ),
  );
}