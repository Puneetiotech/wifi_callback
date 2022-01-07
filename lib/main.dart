import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_form_builder/flutter_form_builder.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const BatteryWidget(),
    );
  }
}

class BatteryWidget extends StatefulWidget {
  const BatteryWidget({Key? key}) : super(key: key);

  @override
  _BatteryWidgetState createState() => _BatteryWidgetState();
}

class _BatteryWidgetState extends State<BatteryWidget> {
  static const platform = MethodChannel('samples.flutter.dev/battery');

  static final _formKey = GlobalKey<FormBuilderState>();

  Future<void> connectToWiFi(String ssid, String password) async {
    try {
      await platform
          .invokeMethod('connect', {"ssid": ssid, "password": password});
    } on PlatformException catch (e) {}
  }

  Future<void> submitForm() async {
    _formKey.currentState!.save();
    var ssid = _formKey.currentState!.value['ssid'];
    var password = _formKey.currentState!.value['password'];
    //  print(ssid);
    //  print(password);
    await connectToWiFi(ssid, password);
    //print(res);
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        body: FormBuilder(
          key: _formKey,
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              children: [
                FormBuilderTextField(
                  name: 'ssid',
                ),
                FormBuilderTextField(
                  name: 'password',
                ),
                ElevatedButton(
                  onPressed: submitForm,
                  child: const Text('Submit'),
                ),
                //Text(_networkLevel),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
