#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>



const int pin_open = 23;
const int pin_close = 22;

int n1 = 2;
int n2 = 4;


const char* ssid = "WIFI-1";
const char* password = "71061080jjsv";

const char* serverName = "http://192.168.0.31:7008/get_lock";

const char* serverNameDevices = "http://192.168.0.31:7008/get_device_for_lock";
String arraydevices[50];
int r=0,t=0;


unsigned long lastTime = 0;
// Timer set to 10 minutes (600000)
//unsigned long timerDelay = 600000;
// Set timer to 5 seconds (5000)
unsigned long timerDelay = 1000;


void setup() {
  pinMode(pin_open, OUTPUT);
  pinMode(pin_close, OUTPUT);
  pinMode(n1, OUTPUT);
  pinMode(n2, OUTPUT);
  
  digitalWrite(pin_open, LOW);
  digitalWrite(pin_close, LOW);
  digitalWrite(n1, LOW);
  digitalWrite(n2, LOW);

  Serial.begin(115200);

  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
 
  Serial.println("Timer set to 5 seconds (timerDelay variable), it will take 5 seconds before publishing the first reading.");
  
  if(WiFi.status()== WL_CONNECTED){
      WiFiClient client;
      HTTPClient http;
      http.begin(client, serverNameDevices);
      http.addHeader("Content-Type", "application/x-www-form-urlencoded");
      String httpRequestData = "id_lock=1";           
      
      int httpResponseCode = http.POST(httpRequestData);
      Serial.print("HTTP Response code: ");
      Serial.println(httpResponseCode);
      
      if (httpResponseCode>0){
        String response = http.getString();
        char* ptr = strtok((char*) response.c_str(), ",");
        byte i = 0;
        
        while (ptr) {
          arraydevices[i] = ptr;
          ptr = strtok(NULL, ",");
          i++;
        }
        t = int(i);
        for(int k=0 ;k<i ;k++){
          Serial.println(arraydevices[k]);
        }
        Serial.println(response);
      }
      http.end();
  }
  delay(5000);
}

void loop() {

  if ((millis() - lastTime) > timerDelay) {
    //Check WiFi connection status
    if(WiFi.status()== WL_CONNECTED){
      WiFiClient client;
      HTTPClient http;
    
      // Your Domain name with URL path or IP address with path
      http.begin(client, serverName);

      // Specify content-type header
      http.addHeader("Content-Type", "application/x-www-form-urlencoded");
      // Data to send with HTTP POST
      String httpRequestData = "id=1";           
      // Send HTTP POST request
      int httpResponseCode = http.POST(httpRequestData);
      Serial.print("HTTP Response code: ");
      Serial.println(httpResponseCode);
      
      if (httpResponseCode>0){
        String response = http.getString();
        Serial.println(response);
        DynamicJsonDocument doc(1024);
        deserializeJson(doc, response);
        int stat_g = doc["stat"];
        int stat_lock = doc["stat_lock"];
        if(stat_g==1 && stat_lock==1){
          digitalWrite(pin_open, HIGH);
          digitalWrite(pin_close, LOW);
          digitalWrite(n1, HIGH);
          digitalWrite(n2, LOW);
     
        }else if(stat_g==1 && stat_lock==0){
          digitalWrite(pin_open, LOW);
          digitalWrite(pin_close, HIGH);
          digitalWrite(n1, LOW);
          digitalWrite(n2, LOW);
        }else if(stat_g==0){
          digitalWrite(pin_open, HIGH);
          digitalWrite(pin_close, HIGH);
          digitalWrite(n1, LOW);
          digitalWrite(n2, LOW);
        }else{
          digitalWrite(n1, LOW);
          digitalWrite(n2, LOW);
        }
       
      }else{
        digitalWrite(pin_open, LOW);
        digitalWrite(pin_close, LOW);
        digitalWrite(n1, LOW);
        digitalWrite(n2, LOW);
      }


      http.end();
    }
    else {
      Serial.println("WiFi Disconnected");
      digitalWrite(pin_open, LOW);
      digitalWrite(pin_close, LOW);
      digitalWrite(n1, LOW);
      digitalWrite(n2, LOW);
    }
    
    lastTime = millis();
  }

}
