const int living_room_pin = 3;
const int master_room_pin = 5;
const int kitchen_pin = 9;
const int bathroom_pin = 10;

String input = "";
bool stringComplete = false;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

  pinMode(living_room_pin, OUTPUT);
  pinMode(master_room_pin, OUTPUT);
  pinMode(bathroom_pin, OUTPUT);
  pinMode(kitchen_pin, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  if (stringComplete) {
    parse_and_exec(input);

    input = "";
    stringComplete = false;
  }
}

void serialEvent() {
  while (Serial.available()) {
    char in = (char)Serial.read();

    input += in;

    if (in == '\n') {
      stringComplete = true;
    }
  }
}

void parse_and_exec(String command){
  int separator_index = command.indexOf(':');
  
  if (separator_index == -1) return;

  String room_name = command.substring(0 , separator_index);
  String value = command.substring(separator_index + 1);

  int intensity = value.toInt();

  int pwm_value = map(intensity , 0 , 5 , 0 , 255);

  if (room_name == "Living Room") {
    analogWrite(living_room_pin, pwm_value);
  }
  else if (room_name == "Master Bedroom") {
    analogWrite(master_room_pin, pwm_value);
  }
  else if (room_name == "Bathroom") {
    analogWrite(bathroom_pin, pwm_value);
  }
  else if (room_name == "Kitchen") {
    analogWrite(kitchen_pin, pwm_value);
  }

}
