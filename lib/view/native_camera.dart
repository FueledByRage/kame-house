import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class NativeCamera extends StatefulWidget {
  const NativeCamera({super.key});

  @override
  State<NativeCamera> createState() => _NativeCameraState();
}

class _NativeCameraState extends State<NativeCamera> {
  List<Offset> _landmarks = [];
  late final EventChannel _eventChannel;
  late final MethodChannel _methodChannel;
  StreamSubscription? _subscription;

  @override
  void initState() {
    super.initState();

    _eventChannel = const EventChannel('pose_landmarks');
    _methodChannel = const MethodChannel('CAMERA');
    _subscription = _eventChannel.receiveBroadcastStream().listen((event) {
      final List<dynamic> list = event as List;

      setState(() {
        _landmarks =
            list.map((e) {
              final pair = e as List;
              return Offset(pair[0].toDouble(), pair[1].toDouble());
            }).toList();
      });
    });
  }

  void _toggleCamera() async {
    try {
      await _methodChannel.invokeMethod('TOGGLE_CAMERA');
    } catch (e) {
      print('Erro ao alternar câmera: $e');
    }
  }

  @override
  void dispose() {
    _subscription?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        const AndroidView(viewType: 'native-camera-view'),
        Positioned.fill(
          child: IgnorePointer(
            child: CustomPaint(painter: LandmarkPainter(_landmarks)),
          ),
        ),
        Positioned(
          bottom: 60,
          right: 50,
          child: FloatingActionButton(
            onPressed: _toggleCamera,
            child: const Icon(Icons.cameraswitch),
          ),
        ),
      ],
    );
  }
}

class LandmarkPainter extends CustomPainter {
  final List<Offset> points;

  LandmarkPainter(this.points);

  @override
  void paint(Canvas canvas, Size size) {
    final paint =
        Paint()
          ..color = Colors.green
          ..strokeWidth = 6
          ..style = PaintingStyle.fill;

    for (final point in points) {
      final dx = point.dx * size.width;
      final dy = point.dy * size.height;
      canvas.drawCircle(Offset(dx, dy), 5, paint);
    }
  }

  @override
  bool shouldRepaint(covariant LandmarkPainter oldDelegate) {
    return oldDelegate.points != points;
  }
}
