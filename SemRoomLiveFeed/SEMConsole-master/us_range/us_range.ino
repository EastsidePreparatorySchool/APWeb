
#define ECHO_PIN    11    // Echo Pin
#define TRIG_PIN    12    // Trigger Pin
#define LED_PIN     13    // Onboard LED
#define SPEAKER_PIN 7     // speaker out
#define NUM_VALUES  50    // size of averaging array
#define AVG_DECAY   50
#define TRIGGER     2

long values[NUM_VALUES];
int index = 0;       


void setup() {
  Serial.begin (9600);
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(LED_PIN, OUTPUT); 

  for (int i=0; i<NUM_VALUES; i++) {
    values[i] = 0;
  }
  tone(SPEAKER_PIN, 2000,500);
  delay(500);
  noTone(SPEAKER_PIN);
}
  static double prevAvg = 0; 

void loop() {
  long duration;
  double change;
  double rAvg =0;
  double dAvg =0;

  // measure
  duration = measureUltrasonic();
  Serial.print(duration);
  Serial.print(",");

  dAvg = decayAvg(duration*2/3);
  Serial.print(dAvg);
  Serial.print(",");

  rAvg = runningAvg(duration) / 3;
  Serial.print(rAvg);
  Serial.print(",");

  change = abs(dAvg-prevAvg);
  Serial.println(change*50);
  Serial.println();
  
  if (change > TRIGGER) {
    tone(SPEAKER_PIN, 1000+change*50);
    digitalWrite(LED_PIN, HIGH);
  }
  
  // delay 20ms before next reading.
  delay(20);

  // reset LED and speaker
  digitalWrite(LED_PIN, LOW);
  noTone(SPEAKER_PIN);

  prevAvg = dAvg;

}

long measureUltrasonic() {
  long duration;
  
  // send trigger signal
  digitalWrite(TRIG_PIN, LOW); 
  delayMicroseconds(2); 
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10); 
  digitalWrite(TRIG_PIN, LOW);
  
  // get echo pulse
  duration = pulseIn(ECHO_PIN, HIGH);
  
  // clip the signal
  if (duration > 2000) {
    duration = 2000;
  }

  return duration;
}


// running avg 

double runningAvg(long value) {
  long minVal = 20000;
  long maxVal = 0;
  int i, minIndex, maxIndex;

  values[index] = value;
  index = (++index % NUM_VALUES);

  // find min and max
  for (i=0; i< NUM_VALUES; i++) {
    if (values[i] < minVal) {
      minVal = values[i];
      minIndex = i;
    }
    if (values[i] < maxVal) {
      maxVal = values[i];
      maxIndex = i;
    }
  }

  // calculate avg without min or max

  double avg = 0; 
  int count = 0;
  for (i= 0; i< NUM_VALUES; i++) {
    if (i != maxIndex && i != minIndex) {
      count++;
      avg += values[i];
    }
  }
  
  return avg/count;
}

double decayAvg(long value) {
  static double rAvg = 0;

  rAvg *= (1.0 - (1.0/AVG_DECAY));
  rAvg += ((double) value)/AVG_DECAY;
  
  return rAvg;
}

