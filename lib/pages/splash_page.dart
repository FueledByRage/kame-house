import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'home_page.dart';

class SplashPage extends StatefulWidget {
  const SplashPage({super.key});

  @override
  State<SplashPage> createState() => _SplashPageState();
}

class _SplashPageState extends State<SplashPage> {
  @override
  void initState() {
    super.initState();
    _checkPermission();
  }

  Future<void> _checkPermission() async {
    final status = await Permission.camera.status;

    if (status.isGranted) {
      _goToHome();
    } else {
      final result = await Permission.camera.request();

      if (result.isGranted) {
        _goToHome();
      } else {
        _showPermissionDialog();
      }
    }
  }

  void _goToHome() {
    Navigator.of(
      context,
    ).pushReplacement(MaterialPageRoute(builder: (_) => const HomePage()));
  }

  void _showPermissionDialog() {
    showDialog(
      context: context,
      builder:
          (_) => AlertDialog(
            title: const Text("Permissão necessária"),
            content: const Text("Precisamos da câmera para continuar."),
            actions: [
              TextButton(
                child: const Text("Fechar"),
                onPressed: () => Navigator.of(context).pop(),
              ),
              TextButton(
                child: const Text("Tentar novamente"),
                onPressed: () {
                  Navigator.of(context).pop();
                  _checkPermission();
                },
              ),
            ],
          ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.camera_alt, size: 80),
            SizedBox(height: 20),
            Text("Carregando...", style: TextStyle(fontSize: 18)),
          ],
        ),
      ),
    );
  }
}
