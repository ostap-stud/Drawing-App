
# Drawing App.

Interactive color control system for the __WS2812B__ LED matrix using the __ESP32 microcontroller__, which receives commands from the __Android application__ via the local network.<br/>
The main task during the development was to ensure that the color data for each LED is transmitted via HTTP POST requests from the mobile application, processed by the ESP32, and displayed on the LED matrix.<br/>

## Used Hardware

- ESP-WROOM-32 ([Pinout](https://randomnerdtutorials.com/esp32-pinout-reference-gpios/))
- WS2812B LED matrix
- Matrix power supply ~(5V, 2A)
- Resistor ~500 Ohm
- Breadboard, jumpers

## ESP Configuration

Firstly ESP WiFi module starts in Access Point mode, so user connects to it and accesses network credentials settings.

<p align="center">
  <img src="https://github.com/user-attachments/assets/e209a018-6972-456c-8fbc-b3499dbe9d79" height="500px"/>
  <img src="https://github.com/user-attachments/assets/4f5ff3f2-28e3-4244-af16-a780095426dc" height="500px"/>
</p>

After that, device is restarting in WiFi Station Mode, and if it successfully connects to your local network the LED pin starts blinking, so you can begin using Drawing App. for sending drawings to matrix.

## Application

User can draw on _16 by 16 field_, change painter color, save drawings locally and publishing it to remote (**Cloud Firestore**) server with public access. On the ESP side __mDNS__ protocol is used, so you can send drawing to _"esp32.local"_, which is default domain name of ESP side, specified in [Sketch](https://github.com/ostap-stud/Drawing-App/blob/master/sketch_sep4a/sketch_sep4a.ino).

Examples:

<p align="center">
  <img src="https://github.com/user-attachments/assets/49307bfd-3b56-41a4-bd79-eb111d300fd0" height="500px"/>
  <img src="https://github.com/user-attachments/assets/42e73385-2479-4e50-8d01-3d0a0c852768" height="500px"/>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/4e923200-e06e-4cc5-9474-077d0b72d8f6" height="300px"/>
  <img src="https://github.com/user-attachments/assets/753721af-869f-438a-a228-83608e444271" height="300px"/>
</p>

Local & Remote drawings, with searching ability:

<p align="center">
  <img src="https://github.com/user-attachments/assets/b81a0b17-43e3-40be-b90f-87839565f28d" height="500px"/>
  <img src="https://github.com/user-attachments/assets/c0f1ebc9-aff6-4fba-a664-1b134b5e6728" height="500px"/>
  <img src="https://github.com/user-attachments/assets/a39dd7d9-31a4-4fd3-ab07-ad3d9cf49f89" height="500px"/>
</p>

_Color picking_ and _Network error_ Dialogs:

<p align="center">
  <img src="https://github.com/user-attachments/assets/e0d93e8d-521f-40f1-86ab-a9f652559f02" height="500px"/>
  <img src="https://github.com/user-attachments/assets/08401f5a-4b44-4f6e-be6e-b4a2a93bac03" height="500px"/>
</p>

To be able to publish your works, you have to **Sign In** (at the current moment only with _Google_).

It looks like this:

<p align="center">
  <img src="https://github.com/user-attachments/assets/fa9ef049-1ae5-4006-8b4a-0a20c743ced1" height="500px"/>
  <img src="https://github.com/user-attachments/assets/ed4084b3-4cc8-4eeb-9784-bd110b8fb3fa" height="500px"/>
  <img src="https://github.com/user-attachments/assets/afcb3248-80f1-4a39-b21b-edfa144942c0" height="500px"/>
</p>
