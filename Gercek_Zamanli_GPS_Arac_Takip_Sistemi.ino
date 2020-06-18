#include<SoftwareSerial.h>
#include<ESP8266WiFi.h>
#include<TinyGPS.h>
#include<FirebaseArduino.h>
#include<ArduinoJson.h>

#define FIREBASE_HOST "real-time-gps-tracking-system.firebaseio.com"
#define FIREBASE_AUTH "BO1gcltYLGSDuoYCrQBHI1CNA3PKb1ykPiWiDAQU"
#define WIFI_SSID "TurkTelekom_ZFV37"
#define WIFI_PASSWORD "6941c1d84Ff5a"

SoftwareSerial modulHaberlesme(4, 5);
TinyGPS gpsModulu;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  modulHaberlesme.begin(9600);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Baglaniliyor.");
  while(WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("Baglanildi: ");
  Serial.println(WiFi.localIP());
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}

void loop() {
  // put your main code here, to run repeatedly:
  smartdelay(1000);
  Serial.println();
  //uint8_t uyduSayisi = gpsModulu.satellites();
  //Serial.println("Baglanilan Uydu Sayisi: "); Serial.println(uyduSayisi);
  float enlem, boylam;
  unsigned long age;
  gpsModulu.f_get_position(&enlem, &boylam, &age);
  Serial.print("Enlem: "); Serial.println(enlem, 6);
  Serial.print("Boylam: "); Serial.println(boylam, 6);
  //int denizSeviyesindenYukseklik = gpsModulu.f_altitude();
  //Serial.print("Deniz Seviyesinden Yükseklik(m): "); Serial.println(denizSeviyesindenYukseklik);
  int hiz = gpsModulu.f_speed_kmph();
  Serial.print("Hız(km/saat): "); Serial.println(hiz);
  //int crs = gpsModulu.f_course();
  //Serial.print("crs: "); Serial.println(crs);
  int yil;
  byte ay, gun, saat, dakika, saniye, hundredths;
  unsigned long age2;
  gpsModulu.crack_datetime(&yil, &ay, &gun, &saat, &dakika, &saniye, &hundredths, &age2);
  if(saat >= 21) {
    switch(saat) {
      case 21:
        saat = 0;
        break;
      case 22:
        saat = 1;
        break;
      case 23:
        saat = 2;
        break;
      default:
        break;
    }
  }
  else {
    saat = saat + 3;
  }
  Serial.print("Tarih: "); Serial.println(String(gun) + ". " + String(ay) + ". " + String(yil));
  Serial.print("Zaman: "); Serial.println(String(saat) + ":" + String(dakika) + ":" + String(saniye));
  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& GPSVerisi = jsonBuffer.createObject();
  GPSVerisi["enlem"] = enlem;
  GPSVerisi["boylam"] = boylam;
  GPSVerisi["hiz"] = hiz;
  GPSVerisi["tarih"] = String(gun) + ". " + String(ay) + ". " + String(yil);
  GPSVerisi["zaman"] = String(saat) + ":" + String(dakika) + ":" + String(saniye);
  if(gpsModulu.satellites() != 255) {
    Firebase.set("GPS Verileri", GPSVerisi);
    if(Firebase.failed()) {
      Serial.print("ayarlama /GPS Verileri Basarisiz:");
      Serial.println(Firebase.error());
    }
  }
  delay(2000);
}

static void smartdelay(unsigned long ms) {
  unsigned long start = millis();
  do {
    while(modulHaberlesme.available())
      gpsModulu.encode(modulHaberlesme.read());
  } while (millis() - start < ms);
}
