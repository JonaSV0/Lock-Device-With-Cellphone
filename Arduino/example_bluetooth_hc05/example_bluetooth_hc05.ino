#include <BluetoothSerial.h>

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT;


#define BT_DISCOVER_TIME  2000


static bool btScanAsync = true;
static bool btScanSync = true;


void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32test"); //Bluetooth device name
  Serial.println("The device started, now you can pair it with bluetooth!");
}

void loop() {
  init_scan();
  delay(1000);
}

void init_scan(){
  
  if (btScanSync) {
    Serial.println("Starting discover...");
    BTScanResults *pResults = SerialBT.discover(BT_DISCOVER_TIME);
    if (pResults){
      pResults->dump(&Serial);

      for (int i=0; i < pResults->getCount(); i++) {
        BTAdvertisedDevice* device = pResults->getDevice(i);
        Serial.printf("Device: %s\n", device->getAddress().toString().c_str());
      }
    }else{
      Serial.println("Error on BT Scan, no result!");
    }
  }

}
