#include <OneWire.h>
#include <LiquidCrystal.h>

// light sensor
int LIGHT_PIN = 0; // analog
int lastLightValue = 0;

// relay
int RELAY_A_PIN = 4;
int RELAY_B_PIN = 5;
int relayAState = HIGH;
int relayBState = HIGH;

//Temperature chip i/o
int THERMO_PIN = 2;
OneWire ds(THERMO_PIN);
float lastTemperatureValue = 0;

// lcd(RS,Enable, D4, D5, D6, D7)
LiquidCrystal lcd(6, 7, 8, 9, 10, 11);

// input string from serial
String inputString = "";
boolean inputStringComplete = false;

char command = '-';

void parseSerialInput();
void processLightSensor();
void processTemperatureSensor();

void publishLight();
void publishRelayA();
void publishRelayB();
void publishTemperature();

bool forceUpdate = true;
bool firstRun = true;

byte charEhacek[8] = {
	B01010,
	B00100,
	B01110,
	B10001,
	B11111,
	B10000,
	B01110,
	B00000
};
int E_S_HACKEM = 0;

byte charEcarka[8] = {
	B00010,
	B00100,
	B01110,
	B10001,
	B11111,
	B10000,
	B01110,
	B00000
};
int E_S_CARKOU = 1;

byte charIcarka[8] = {
	B00010,
	B00100,
	B01100,
	B00100,
	B00100,
	B00100,
	B01110,
	B00000
};
int I_S_CARKOU = 2;

byte charStupen[8] = {
	B00110,
	B01001,
	B01001,
	B00110,
	B00000,
	B00000,
	B00000
};
int STUPEN = 3;

void setup() {
	lcd.createChar(E_S_CARKOU, charEcarka);
	lcd.createChar(E_S_HACKEM, charEhacek);
	lcd.createChar(I_S_CARKOU, charIcarka);
	lcd.createChar(STUPEN, charStupen);

	lcd.begin(20, 4);
	lcd.noCursor();
	lcd.home();

	lcd.write("Nastavovani...");

	Serial.begin(9600);
	inputString.reserve(200);

	pinMode(RELAY_A_PIN, OUTPUT);
	digitalWrite(RELAY_A_PIN, relayAState);
	pinMode(RELAY_B_PIN, OUTPUT);
	digitalWrite(RELAY_B_PIN, relayBState);

	lastTemperatureValue = getTemp();
	lastLightValue = getLight();

	lcd.home();
	lcd.write("Cekam na pripojeni");

	while (!Serial) {
	}

	publishLight();
	publishTemperature();
	publishRelayA();
	publishRelayB();

	lcd.clear();
}

void loop() {

	parseSerialInput();

	/*if (inputStringComplete) {
		lcd.setCursor(0, 3);
		lcd.print(inputString);
		for (int i = inputString.length(); i < 20; i++) {
			lcd.print(" ");
		}
		inputString = "";
		inputStringComplete = false;
	}*/

	processLightSensor();

	processTemperatureSensor();

	if (forceUpdate) {
		lcd.clear();
		lcd.noCursor();
		publishLight();
		publishRelayA();
		publishRelayB();
		publishTemperature();
		forceUpdate = false;
	}

	firstRun = false;
	delay(100);
}

void processLightSensor() {
	int sensorValue = getLight();
	if (abs(lastLightValue - sensorValue) > 1 || firstRun) {
		lastLightValue = sensorValue;
		publishLight();
	}
}

void publishLight() {
	lcd.setCursor(0, 0);
	lcd.print("Osv");
	lcd.write(E_S_HACKEM);
	lcd.print("tlen");
	lcd.write(I_S_CARKOU);
	lcd.print(": ");
	lcd.print(lastLightValue);
	lcd.print(" %");
	if (lastLightValue < 10) lcd.print(" ");
	else if (lastLightValue < 100) lcd.print(" ");
	for (int i = 16; i < 20; i++) {
		lcd.print(" ");
	}

	Serial.print(":l:");
	Serial.println(lastLightValue);
}

int getLight() {
	int sensorValue = analogRead(LIGHT_PIN);
	sensorValue = constrain(sensorValue, 0, 1000);
	sensorValue = map(sensorValue, 0, 1000, 0, 100);
	return 100 - sensorValue;
}

void processTemperatureSensor() {
	float temperature = getTemp();
	if (temperature == -1000) return;
	if (abs(lastTemperatureValue - temperature) > 0.1 || firstRun) {
		lastTemperatureValue = temperature;
		publishTemperature();
	}
}

void publishTemperature() {
	lcd.setCursor(0, 1);
	lcd.print("Teplota: ");
	lcd.print(lastTemperatureValue);
	lcd.print(" ");
	lcd.write(STUPEN);
	lcd.print("C");
	for (int i = 16; i < 20; i++) {
		lcd.print(" ");
	}

	Serial.print(":t:");
	Serial.println(lastTemperatureValue);
}

void parseSerialInput() {
	inputString = "";
	while (Serial.available()) {
		char inChar = (char) Serial.read();
		if (command != '-' && inChar != ':') {
			switch (command) {
				case 'r':
					if (inChar == '1') {
						relayAState = LOW;
					} else {
						relayAState = HIGH;
					}
					publishRelayA();
					break;
				case 's':
					if (inChar == '1') {
						relayBState = LOW;
					} else {
						relayBState = HIGH;
					}
					publishRelayB();
					break;
			}
			command = '-';
		} else {
			if (command == '-') command = inChar;
			if (command == 'u') {
				forceUpdate = true;
				command = '-';
			}
		}
		inputString += inChar;
		inputStringComplete = true;
	}
}

void publishRelayA() {
	lcd.setCursor(0, 2);
	lcd.print("Rel");
	lcd.write(E_S_CARKOU);
	lcd.print(" A: ");
	lcd.print(relayAState ? "V" : "Z");

	digitalWrite(RELAY_A_PIN, relayAState);

	Serial.print(":r:");
	Serial.println(!relayAState);
}

void publishRelayB() {
	lcd.setCursor(10, 2);
	lcd.print("Rel");
	lcd.write(E_S_CARKOU);
	lcd.print(" B: ");
	lcd.print(relayBState ? "V" : "Z");

	digitalWrite(RELAY_B_PIN, relayBState);

	Serial.print(":s:");
	Serial.println(!relayBState);
}

float getTemp() {
	//returns the temperature from one DS18S20 in DEG Celsius

	byte data[12];
	byte addr[8];

	if (!ds.search(addr)) {
		//no more sensors on chain, reset search
		ds.reset_search();
		return -1000;
	}

	if (OneWire::crc8(addr, 7) != addr[7]) {
		Serial.println("CRC is not valid!");
		return -1000;
	}

	if (addr[0] != 0x10 && addr[0] != 0x28) {
		Serial.print("Device is not recognized");
		return -1000;
	}

	ds.reset();
	ds.select(addr);
	ds.write(0x44, 1); // start conversion, with parasite power on at the end

	byte present = ds.reset();
	ds.select(addr);
	ds.write(0xBE); // Read Scratchpad

	for (int i = 0; i < 9; i++) { // we need 9 bytes
		data[i] = ds.read();
	}

	ds.reset_search();

	byte MSB = data[1];
	byte LSB = data[0];
	float CONT_REMAIN = data[6];
	float CONT_PER_C = data[7];

	float tempRead = ((MSB << 8) | LSB); //using two's compliment

	int TReading = (MSB << 8) + LSB;

	int SignBit = TReading & 0x8000; // test most sig bit
	if (SignBit) // negative
	{
		TReading = (TReading ^ 0xffff) + 1; // 2's comp
	}
	int Tc_100 = (6 * TReading) + TReading / 4;

	float TemperatureSum = (float) Tc_100 / 100.0;

	return TemperatureSum;
}
