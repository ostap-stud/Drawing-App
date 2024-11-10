#include <WiFi.h>
#include <ESPmDNS.h>
#include "FastLED.h"
#include <ESPAsyncWebServer.h>
#include <AsyncTCP.h>
#include <ArduinoJson.h>
#include <EEPROM.h>
#include <WifiManagerHTML.h>

#define LED_PIN 4                 // Пін ESP32, до якого підключена матриця
#define NUM_LEDS 256              // Кількість світлодіодів в матриці (наприклад 8x8)
#define WIFI_CONFIG_RESET_PIN 14  // Пін, при подачі живлення до якого очищаються збережі дані Wifi мережі

AsyncWebServer server(80);        // Асинхронний веб-сервер

String receivedData = "";         // Об'єкт акумуляції отриманих даних

CRGB leds[NUM_LEDS];              // Масив для роботи з LED-матрицею

void setup() {
  EEPROM.begin(400);
  Serial.begin(115200);
  
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(WIFI_CONFIG_RESET_PIN, INPUT);

  if(!checkWifiCreds()){
    Serial.println("No WIFI credentials stored in memory. Loading form...");
    digitalWrite(LED_BUILTIN, HIGH);
    wifiManagerForm();
  }

  String ssid = EEPROM.readString(100);
  String password = EEPROM.readString(200);

  WiFi.mode(WIFI_STA);

  WiFi.begin(ssid, password);                   // Підключення до мережі
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");
  Serial.println(WiFi.localIP());               // Вивід отриманого IP-адресу пристрою

  if(!MDNS.begin("esp32")){                     // Запуск служби mDNS -> URL серверу: http://esp32.local/
    Serial.println("Error setting up mDNS...");
    while(true){
      delay(1000);
    }
  }

  Serial.println("mDNS responder started!");

  FastLED.addLeds<WS2812B, LED_PIN, GRB>(leds, NUM_LEDS);               // Конфігурація бібліотеки для роботи з LED-матрицею WS2812B

  FastLED.clear();
  FastLED.show();

  setLedsHandle();
  setClearHandle();
  
  server.begin();
}

void loop() {
  if(digitalRead(WIFI_CONFIG_RESET_PIN) == HIGH){
    Serial.println("Clearing WiFi credentials from memory...");
    clearEEPROM();
    digitalWrite(LED_BUILTIN, HIGH);
    wifiManagerForm();
  }
  // Мигання вбудованого світлодіода після успішного виконання всіх налаштувань в методі setup()
  digitalWrite(LED_BUILTIN, HIGH);
  delay(2000);
  digitalWrite(LED_BUILTIN, LOW);
  delay(2000);
}

void clearEEPROM(){
  for(int i = 0; i < 400; i++){
    EEPROM.put(i, 0);
  }
  EEPROM.commit();
}

void setLedsHandle(){
  server.on("/setLED", HTTP_POST, [](AsyncWebServerRequest *request){   // Визначення логіки на отриманий POST-запит
    receivedData = "";                                                  // Очищення об'єкту акумуляції
  }, NULL, [](AsyncWebServerRequest *request, uint8_t *data, size_t len, size_t index, size_t total){

    // Необхідно спершу акумулювати дані для їх подальшого використання
    receivedData += String((char*)data).substring(0, len);

    if(index + len == total){
      // Виділяю необхідний розмір на динамічній пам'яті для JSON-документа
      DynamicJsonDocument doc(4096);
      // Десеріалізація, що може повернути помилку
      DeserializationError error = deserializeJson(doc, receivedData);

      if (error) {
        Serial.println(error.c_str());
        request->send(400, "application/json", "{\"error\":\"Invalid JSON format\"}");
        return;
      }

      if (!doc["colors"].is<JsonArray>()) {
        Serial.println("Invalid JSON, expected array");
        request->send(400, "application/json", "{\"error\":\"Expected an array\"}");
        return;
      }

      JsonArray array = doc["colors"];

      // Перевірка розміру масиву
      if (array.size() != 256) {
        Serial.println("Invalid array size, expected 256");
        request->send(400, "application/json", "{\"error\":\"Array size must be 256\"}");
        return;
      }

      FastLED.clear();
      FastLED.show();

      for(int i = 0; i < 16; i++){
        for (int j = 0; j < 16; j++) {
          int32_t argb = array[j + i * 16];
          uint8_t a = (argb >> 24) & 0xFF; // Альфа-канал (не використовується)
          uint8_t r = (argb >> 16) & 0xFF; // Червоний
          uint8_t g = (argb >> 8) & 0xFF;  // Зелений
          uint8_t b = argb & 0xFF;         // Синій

          // Оскільки адресація світлодіодів на даній матриці визначена серпантиновим напрямком,
          // її коректне заповнення потребує наступної логіки:
          if(i % 2 == 0){
            leds[(15 - j) + i * 16].setRGB(r, g, b);
          }else{
            leds[j + i * 16].setRGB(r, g, b);
          }
        } 
      }
      
      FastLED.show(); // Оновлення кольорів на матриці

      request->send(200, "application/json", "{\"status\":\"leds_updated\"}");  // Відповідь у разі успішної обробки запиту
    }
    
  });
}

void setClearHandle(){
  server.on("/clear", HTTP_POST, [](AsyncWebServerRequest *request){
    FastLED.clear();
    FastLED.show();
    request->send(200, "application/json", "{\"status\":\"leds_cleared\"}");
  }, NULL, NULL);
}

/*
     Wifi Manager Server
*/
void wifiManagerForm(){
  String s = EEPROM.readString(100);
  String p = EEPROM.readString(200);
  //const char* ssid     = "ESP32 WiFi Manager";
  char APname[11]; 
  sprintf(APname, "ESP_%X", ESP.getEfuseMac());
  const char* password = "12345678";

  Serial.println("Setting Access Point...");
  WiFi.mode(WIFI_AP);
  WiFi.softAP(APname, password);
  IPAddress IP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(IP);

  setManagerRootHandler();
  setManagerFormHandler();
  server.begin();

  Serial.print("Waiting for Wifi configuration process completion");
  while(true){
    Serial.print(".");
    delay(2000);
  }
}

// Перевірка Wifi даних
bool checkWifiCreds(){
  Serial.println("Checking WIFI credentials");
  String s = EEPROM.readString(100);
  String p = EEPROM.readString(200);
  //#if DEBUG
  Serial.print("Found credentials: ");
  Serial.print(s);
  Serial.print("/");
  Serial.println(p);
  delay(5000);
  //#endif
  if(s.length() > 0 && p.length() > 0){
    return true;
  }else{
    return false;
  }
}

// Запис у постійну енергонезалежну пам'ять (EEPROM)
bool writeToMemory(String ssid, String pass){
  char buff1[30];
  char buff2[30];
  ssid.toCharArray(buff1,30);
  pass.toCharArray(buff2,30); 
  EEPROM.writeString(100,buff1);
  EEPROM.writeString(200,buff2);
  delay(100);
  String s = EEPROM.readString(100);
  String p = EEPROM.readString(200);
  //#if DEBUG
  Serial.print("Stored SSID, password, are: ");
  Serial.print(s);
  Serial.print(" / ");
  Serial.print(p);
  //#endif
  if(ssid == s && pass == p){
    return true;  
  }else{
    return false;
  }
}

// Обробник головної сторінки
void setManagerRootHandler(){
  server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send(200, "text/html", FORM_HTML);
  });
}

// Обробник даних форми
void setManagerFormHandler(){
  server.on("/", HTTP_POST, [](AsyncWebServerRequest *request){
    if(request->hasArg("ssid") && request->hasArg("password")){
      String response_success="<h1>Success</h1>";
      response_success +="<h2>Device will restart in 3 seconds</h2>";
      String response_error="<h1>Error</h1>";
      response_error +="<h2><a href='/'>Go back</a>to try again";
      if(writeToMemory(String(request->arg("ssid")),String(request->arg("password")))){
        request->send(200, "text/html", response_success);
        EEPROM.commit();
        delay(3000);
        ESP.restart();
      }else{
        request->send(200, "text/html", response_error);
      }
    }else{
      request->send(200, "text/html", "<h1>Error: There were no arguments passed!</h1>");
    }
  });
}
