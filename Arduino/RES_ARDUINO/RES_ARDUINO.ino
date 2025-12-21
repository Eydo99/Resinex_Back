const int living_room_pin = 3;
const int master_room_pin = 5;
const int kitchen_pin = 9;
const int bathroom_pin = 10;
const int pir_pin_global=8;
const int buzzer_global_pin=11;
bool prev_motion_state=false;
bool alarm_active=false;


String input = "";
bool stringComplete = false;



const int temp_buzzer_pin = 6;
int temp_val;
bool tempAlarmEnabled = true;
bool tempAlarmActive = false;
float currentTemp = 0;
const float TEMP_LIMIT = 35.0;
unsigned long lastTempRead = 0;
const unsigned long TEMP_INTERVAL = 2000; 



void parse_and_exec(String command);
void handle_ack_command(String command);
void handle_light_command(String command);
void triggerMotionEvent();


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

  analogWrite(buzzer_global_pin,0);

  pinMode(living_room_pin, OUTPUT);
  pinMode(master_room_pin, OUTPUT);
  pinMode(bathroom_pin, OUTPUT);
  pinMode(kitchen_pin, OUTPUT);
  pinMode(buzzer_global_pin,OUTPUT);
  pinMode(pir_pin_global,INPUT);
  pinMode(A0, INPUT);
  pinMode(temp_buzzer_pin, OUTPUT);
  noTone(temp_buzzer_pin);



  
}

void loop() {
  // put your main code here, to run repeatedly:
  if (stringComplete) {
    parse_and_exec(input);

    input = "";
    stringComplete = false;
  }

  bool current_motion_state=digitalRead(pir_pin_global);
  if(current_motion_state && !prev_motion_state && !alarm_active)
  {
    triggerMotionEvent();
    alarm_active=true;
  }
  prev_motion_state=current_motion_state;
  if(millis()-lastTempRead>TEMP_INTERVAL){
    readTemperature();
    lastTempRead=millis();

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
  command.trim();
  if(command.startsWith("ACK:"))
  {
    handle_ack_command(command);
    return;
  }
  else
  {
    handle_light_command(command);
  }
}


void handle_ack_command(String command)
{
  if (command.startsWith("ACK:MOTION:")) {
    analogWrite(buzzer_global_pin,0);
    alarm_active = false;
    Serial.println("ACK received, alarm cleared");
  }
   // Temperature SAFE
  if (command == "SAFE") {
    tempAlarmEnabled = false;
    noTone(temp_buzzer_pin);
    tempAlarmActive = false;
    Serial.println("TEMP SAFE");
    return;
  }

  // Resume temperature monitoring
  if (command == "RESUME") {
    tempAlarmEnabled = true;
    Serial.println("TEMP RESUME");
  }
}


void handle_light_command(String command)
{
  int separator_index = command.indexOf(':');
  if (separator_index == -1) return;

  String room_name = command.substring(0, separator_index);
  String value = command.substring(separator_index + 1);

  int intensity = value.toInt();
  int pwm_value = map(intensity, 0, 5, 0, 255);

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

  void triggerMotionEvent()
  {
    Serial.println("EVENT:MOTION:GLOBAL");
    analogWrite(buzzer_global_pin,60);
  }

  void readTemperature() {
  temp_val = analogRead(A0);
  float mv = (temp_val / 1024.0) * 5000.0;
  currentTemp = (mv / 10.0);

  Serial.print("TEMP:");
  Serial.println(currentTemp);

  if (tempAlarmEnabled && currentTemp >= TEMP_LIMIT && !tempAlarmActive) {
    tone(temp_buzzer_pin, 1000);
    tempAlarmActive = true;
  }

  if (currentTemp < TEMP_LIMIT && tempAlarmActive && tempAlarmEnabled) {
    noTone(temp_buzzer_pin);
    tempAlarmActive = false;
  }
  
  
}




