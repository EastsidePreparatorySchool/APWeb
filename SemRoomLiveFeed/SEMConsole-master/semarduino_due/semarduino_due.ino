//
// SEM EPS for Arduino Due
// Input: Analog in A0, A1, A2, A3, hsync and vsync at D3 and D4
// Output: USB frames to PC
//




// USB communication headers

#define COMMAND_BYTES 16
byte headerConnect[COMMAND_BYTES]  = {'E', 'P', 'S', '_', 'S', 'E', 'M', '_', 'C', 'O', 'N', 'N', 'E', 'C', 'T', '.'};
byte headerReady[COMMAND_BYTES]    = {'E', 'P', 'S', '_', 'S', 'E', 'M', '_', 'R', 'E', 'A', 'D', 'Y', '.', '.', '.'};
byte headerFrame[COMMAND_BYTES]    = {'E', 'P', 'S', '_', 'S', 'E', 'M', '_', 'F', 'R', 'A', 'M', 'E', '.', '.', '.'};
byte headerBytes[COMMAND_BYTES]    = {'E', 'P', 'S', '_', 'S', 'E', 'M', '_', 'B', 'Y', 'T', 'E', 'S', '.', '.', '.'};
byte headerEndFrame[COMMAND_BYTES] = {'E', 'P', 'S', '_', 'S', 'E', 'M', '_', 'E', 'N', 'D', 'F', 'R', 'A', 'M', 'E'};
byte headerReset[COMMAND_BYTES]    = {'E', 'P', 'S', '_', 'S', 'E', 'M', '_', 'R', 'E', 'S', 'E', 'T', '.', '.', '.'};
byte headerIdle[COMMAND_BYTES]     = {'E', 'P', 'S', '_', 'S', 'E', 'M', '_', 'I', 'D', 'L', 'E', '.', '.', '.', '.'};
byte headerMeta[COMMAND_BYTES]     = {'E', 'P', 'S', '_', 'S', 'E', 'M', '_', 'M', 'E', 'T', 'A', '.', '.', '.', '.'};

#define SENTINEL_BYTES 16
byte sentinelTrailer[SENTINEL_BYTES] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF};

struct BytesParams {
  byte      headerBytes[16];
  uint32_t  checkSum;         // now includes everything from here on, to just before sentinel trailer
  uint16_t  line;
  uint16_t  bytes;
};

struct BytesParams *g_pbp;


//
// resolution recognition (autosync) structures and array
//

struct Resolution {
  int scanLineTime; // scan length (in microseconds) for recognizing this resolution
  int tolerance;    // +- microseconds
  int numPixels;    // number of pixels we will set up to scan for line
  int numChannels;  // number of channels for every pixel
  int numLines;     // documented number of lines (in actuality, we will obey vsync)
  int numSamples;   // manual scan: samples per pixel, ignored for DMA
};

struct Resolution *g_pCurrentRes;


// resolutions are stored in this array in ascending order of horizontal scan times
struct Resolution g_allRes[] = {
  // scan line time, tolerance, pixels, channels, spec lines, samples
  {           160,    40,    200,        1,        182,     1 }, // RAPID2 mag 100
  {          1183,   200,   1200,        1,        536,     1 }, // RAPID2 mag 10
  //  {          5790,   500,   2000,        1,        840,     1 }, // SLOW1
  {          5790,   500,   1000,        1,        840,    16 }, // SLOW1
  {         10800,   500,   3200,        1,       2276,     8 }, // H3V5
  {         33326,   500,   4000,        1,       3000,    22 }, // H6V7
  {        240000, 11000,   4000,        1,       3000,    40 }  // H8V9
};

#define FAST_DROP_COUNT 3

#define NUM_MODES (sizeof(g_allRes)/sizeof(struct Resolution))




#define VSYNC_PIN   2
#define HSYNC_PIN   3
#define SELECT_PIN(ch)  (ch+4)

#define PIN_KV_FINE     22
#define PIN_KV_SCALE    28

#define PIN_MAG_LOW_DIGIT   32
#define PIN_MAG_HIGH_DIGIT  40
#define PIN_MAG_EXPONENT    46

#define PIN_WD              52


#define PHASE_IDLE                0
#define PHASE_READY_TO_MEASURE    1
#define PHASE_MEASURING           2
#define PHASE_READY_FOR_SCAN      3
#define PHASE_SCANNING            4
#define PHASE_CHECK               5

#define NOP __asm__ __volatile__ ("nop\n\t")

#define REASON_IDLE   0
#define REASON_NO_RES 1
#define REASON_DROP   2
#define REASON_VSYNC  3

int g_line1 = 0; 
int g_line2 = 0; 
int g_numChannels = 1;

//
// Buffers are really independent of the resolution we are tracking, just make them big enough
//

#define NUM_BUFFERS   2 // fill one with DMA while main program reads and copies the other
#define BUFFER_LENGTH (5000 * 8 + 64)
#define BUFFER_BYTES  (BUFFER_LENGTH * sizeof(uint16_t))
#define NEXT_BUFFER(n)((n+1)%NUM_BUFFERS) // little macro to aid switch to next buffer

volatile int currentBuffer;
volatile int nextBuffer;
uint16_t *padcBuffer[NUM_BUFFERS];
volatile unsigned long g_adcLineTimeStart;
volatile unsigned long g_adcLineTime;
volatile bool g_adcInProgress;
int g_lineBytes;
volatile int g_phase;
volatile int g_measuredLineTime;
volatile int g_reason;
volatile int g_argument;
volatile int g_numLines;
volatile int g_resFaults;
int g_fResetRes;
volatile int g_drop;
struct Resolution *g_lastRes;
int g_timeFrame;
volatile bool g_fFrameInProgress;
volatile bool g_fSlow;
volatile bool g_fLineReady;
volatile int g_pixels;
volatile int g_count;
uint16_t * g_pDest;





#define DROP_LINES 5;
#define MAX_RES_FAULTS 10
#define MAX_ERRORS  100
#define USB_TIMEOUT 100
#define USB_MIN_WRITE_BUFFER_SIZE 60









// code to blink the built-in LED n times

void blinkBuiltInLED(int n) {

  for (int i = 0; i < n; i++) {
    // blink built-in LED
    digitalWrite(LED_BUILTIN, HIGH);
    delay(200);
    digitalWrite(LED_BUILTIN, LOW);
    if (i < n - 1) {
      delay(200);
    }
  }
}


//
// ERROR HALT
//

void halt(int blinks) {
  while (true) {
    blinkBuiltInLED(blinks);
    delay(1000);
  }
}

//
// software reset
//

void reset() {

  blinkBuiltInLED(3);
  detachInterrupt(VSYNC_PIN);
  detachInterrupt(HSYNC_PIN);

  freeLineBuffers(); // safe to do

  while (SerialUSB.available()) {
    SerialUSB.read();
  }
  delay(100);

  setup();
}

void setup() {
  // start USB
  SerialUSB.begin(0);
  SerialUSB.write(headerReset, 16);

  // set up built-in blink LED, custom led, pushButton
  pinMode (LED_BUILTIN, OUTPUT);
  // visual signal that we are alive
  blinkBuiltInLED(1);


  for (int i = PIN_KV_FINE; i < PIN_WD; i++) {
    pinMode(i, INPUT);
  }
  pinMode(PIN_WD, INPUT_PULLUP);

  for (int i = 0; i < 4; i++) {
    pinMode (SELECT_PIN(i), INPUT);
  }
  pinMode(8, OUTPUT);
  digitalWrite(8, HIGH);

  int n;
  byte buffer[16];

  // initialize buffer pointers to NULL
  g_pbp = NULL;
  for (int i = 0; i < NUM_BUFFERS; i++) {
    padcBuffer[i] = NULL;
  }


  // wait for USB connect command from host
  do {
    do {
      // wait for any request
      do {
        delayMicroseconds(20);
        n = SerialUSB.available();
      } while (n == 0);


      // read the request
      SerialUSB.readBytes(buffer, 16);

      // read any extra bytes
      while (SerialUSB.available()) {
        SerialUSB.read();
      }
    } while (n != COMMAND_BYTES);
  } while (memcmp(buffer, headerConnect, COMMAND_BYTES) != 0);

  // acknowledge connection request
  SerialUSB.write(headerReady, 16);
  SerialUSB.flush();
  blinkBuiltInLED(2);
  g_pCurrentRes = NULL;
  g_lastRes = NULL;
  g_phase = PHASE_CHECK;
  g_reason = REASON_IDLE;
  g_drop = DROP_LINES;
  g_timeFrame = 0;
  g_fFrameInProgress = false;
  g_fSlow = false;
  g_fResetRes = true;

  setupInterrupts();
}



void computeCheckSum(int line, int bytes) {
  // compute checkSum
  long checkSum = 0;
  uint16_t *pWord = (uint16_t *)&g_pbp[1];
  for (int i = 0; i < bytes / 2; i++) {
    checkSum += *pWord++;
  }

  g_pbp->checkSum = checkSum + line + bytes;
  g_pbp->line = line;
  g_pbp->bytes = bytes;
}


void sendLine(int bytes) {
  if (okToWrite()) {
    SerialUSB.write((uint8_t *)g_pbp, sizeof(struct BytesParams) +  bytes + sizeof(sentinelTrailer));
  }
}



int setupLineBuffers() {
  // compute raw byte number for one scan line
  int bytes = (g_pCurrentRes->numPixels * g_pCurrentRes->numChannels * sizeof(uint16_t));

  for (int i = 0; i < NUM_BUFFERS; i++) {
    padcBuffer[i] = (uint16_t *)malloc(bytes);
    if (padcBuffer[i] == NULL) {
      halt(3);
    }
  }

  // compute buffer size for whole USB command, allocate, and fill with known info
  int bufferSize = sizeof(struct BytesParams) +  bytes + sizeof(sentinelTrailer);
  g_pbp = (struct BytesParams *) malloc (bufferSize * (g_pCurrentRes->numSamples > 1 ? 2 : 1)); // if resolution indicates, allocate twice the words
  if (g_pbp == NULL) {
    halt(4);
  }

  memcpy(&g_pbp->headerBytes, headerBytes, sizeof(headerBytes));
  memcpy(((byte *)&g_pbp[1]) + bytes, sentinelTrailer, sizeof (sentinelTrailer));
  g_pbp->bytes = bytes;

  // return the size for USB send
  return bufferSize;
}




void freeLineBuffers() {
  for (int i = NUM_BUFFERS - 1; i >= 0; i--) {
    if (padcBuffer != NULL) {
      free(padcBuffer[i]);
      padcBuffer[i] = NULL;
    }
  }

  if (g_pbp != NULL) {
    free (g_pbp);
    g_pbp = NULL;
  }
}


struct Frame {
  uint8_t   headerFrame[16];
  uint16_t  numChannels;
  uint16_t  numPixels;
  uint16_t  numLines;
  uint16_t  scanTime;
  uint16_t  lines[4];
};

void sendFrameHeader() {
  static struct Frame h = {{'E', 'P', 'S', '_', 'S', 'E', 'M', '_', 'F', 'R', 'A', 'M', 'E', '.', '.', '.'}, 0, 0, 0, 0, {0, 0, 0, 0}};


  h.numChannels = g_pCurrentRes->numChannels;
  h.numPixels = g_pCurrentRes->numPixels;
  h.numLines = g_pCurrentRes->numLines;
  h.scanTime = g_measuredLineTime;

  switch (g_pCurrentRes->numChannels) {
    case 4:
      h.lines[0] = 0;
      h.lines[1] = 0;
      h.lines[2] = 0;
      h.lines[3] = 0;
      break;

    case 2:
      // list just the specific 2 channels we are capturing
      h.lines[0] = g_line1;
      h.lines[1] = g_line2;
      h.lines[2] = 0;
      h.lines[3] = 0;
      break;

    case 1:
      // list just the specific 1 channel we are capturing in FAST
      h.lines[0] = g_line1;
      h.lines[1] = 0;
      h.lines[2] = 0;
      h.lines[3] = 0;
      break;
  }
  if (okToWrite()) {
    SerialUSB.write((uint8_t *)&h, sizeof(h));
  }
}


int readDigit(int lowPin, int bits) {
  int result = 0;
  for (int i = bits - 1; i >= 0; i--) {
    result <<= 1;
    result += digitalRead(lowPin + i) == HIGH ? 1 : 0;
  }
  return result;
}



struct Meta {
  uint8_t   headerMeta[16];
  uint32_t  magnification;
  uint32_t  kv;
  uint32_t  wd;
};


void sendMeta() {
  static struct Meta m = {{'E', 'P', 'S', '_', 'S', 'E', 'M', '_', 'M', 'E', 'T', 'A', '.', '.', '.', '.'}, 0, 0, 0};
  m.kv = readDigit(PIN_KV_FINE, 4);
  m.kv += 10 * readDigit(PIN_KV_SCALE, 2);

  m.magnification = readDigit(PIN_MAG_LOW_DIGIT, 4) + 10 * readDigit(PIN_MAG_HIGH_DIGIT, 4);
  int exponent = readDigit(PIN_MAG_EXPONENT, 3);
  for (int i = 0; i < exponent; i++) {
    m.magnification *= 10;
  }

  m.wd = digitalRead(PIN_WD) == HIGH ? 15 : 39;

  if (okToWrite()) {
    SerialUSB.write((uint8_t *)&m, sizeof(m));
  }
}
struct EndFrame {
  uint8_t   header[16];
  uint16_t  lineTime;
  uint16_t  frameTime;
};

void sendEndFrame(int lineTime, int frameTime) {
  static struct EndFrame h = {{'E', 'P', 'S', '_', 'S', 'E', 'M', '_', 'E', 'N', 'D', 'F', 'R', 'A', 'M', 'E'}, 0, 0};

  h.lineTime = lineTime;
  h.frameTime = frameTime;
  if (okToWrite()) {
    SerialUSB.write((uint8_t *)&h, sizeof(h));     // send EFRAME (end frame), line time, frame time
  }
}







void SerialUSB_write_uint16_t(uint16_t word) {
  SerialUSB.write((byte)(word & 255));
  SerialUSB.write((byte)(word >> 8));
}

void SerialUSB_write_uint32_t(uint32_t word) {
  SerialUSB.write((byte)(word & 255));
  SerialUSB.write((byte)((word >> 8) & 255));
  SerialUSB.write((byte)((word >> 16) & 255));
  SerialUSB.write((byte)(word >> 24));
}


void adcConfigureGain() {
  adc_enable_anch(ADC);

  adc_set_channel_input_gain(ADC, (adc_channel_num_t)(g_APinDescription[0].ulADCChannelNumber), ADC_GAINVALUE_0);
  adc_set_channel_input_gain(ADC, (adc_channel_num_t)(g_APinDescription[1].ulADCChannelNumber), ADC_GAINVALUE_0);
  adc_set_channel_input_gain(ADC, (adc_channel_num_t)(g_APinDescription[2].ulADCChannelNumber), ADC_GAINVALUE_0);
  adc_set_channel_input_gain(ADC, (adc_channel_num_t)(g_APinDescription[3].ulADCChannelNumber), ADC_GAINVALUE_0);

  adc_disable_channel_input_offset(ADC, (adc_channel_num_t)(g_APinDescription[0].ulADCChannelNumber));
  adc_disable_channel_input_offset(ADC, (adc_channel_num_t)(g_APinDescription[1].ulADCChannelNumber));
  adc_disable_channel_input_offset(ADC, (adc_channel_num_t)(g_APinDescription[2].ulADCChannelNumber));
  adc_disable_channel_input_offset(ADC, (adc_channel_num_t)(g_APinDescription[3].ulADCChannelNumber));
}



//
// set up analog-to-digital conversions
//

void initializeADC() {
  // convert from Ax input pin numbers to ADC channel numbers

  int channel1 = 7 - g_line1;
  int channel2 = 7 - g_line2;

  pmc_enable_periph_clk(ID_ADC);
  adc_init(ADC, SystemCoreClock, ADC_FREQ_MAX, ADC_STARTUP_FAST);
  analogReadResolution(12);
  adcConfigureGain();
  ADC->ADC_MR &= 0xFFFF0000;     // mode register "prescale" zeroed out.
  ADC->ADC_CHDR = 0xFFFFFFFF;   // disable all channels

  if (!g_fSlow) {
    ADC->ADC_MR |= 0x80000000;     // high bit indicates to use sequence numbers
    ADC->ADC_EMR |= (1 << 24);    // turn on channel numbers

    switch (g_pCurrentRes->numChannels) {
      case 4:
        // set 4 channels
        ADC->ADC_CHER = 0xF0;         // enable ch 7, 6, 5, 4 -> pins a0, a1, a2, a3
        ADC->ADC_SEQR1 = 0x76540000;  // produce these channel readings for every completion
        break;

      case 2:
        // set 2 channels
        ADC->ADC_CHER = (1 << channel1) | (1 << channel2);
        ADC->ADC_SEQR1 = (channel1 << (channel1 * 4)) | (channel2 << (channel2 * 4));
        break;

      case 1:
        //todo: make sure this works
        ADC->ADC_CHER = 1 << channel1; // todo: does this work for channels other than A0?
        ADC->ADC_SEQR1 = channel1 << (channel1 * 4);
        break;
    }
    NVIC_EnableIRQ(ADC_IRQn);

    //ADC->ADC_IDR = ~(1 << 27);              // disable other interrupts
    ADC->ADC_IER = 1 << 27;                 // enable the DMA one
    ADC->ADC_RPR = (uint32_t)padcBuffer[0]; // set up DMA buffer
    ADC->ADC_RCR = g_pCurrentRes->numPixels * g_pCurrentRes->numChannels;           // and number of words
    ADC->ADC_RNPR = (uint32_t)padcBuffer[1]; // next DMA buffer
    ADC->ADC_RNCR = g_pCurrentRes->numPixels * g_pCurrentRes->numChannels;

    currentBuffer = 0;
    nextBuffer = 1;

    ADC->ADC_PTCR = 1;
    ADC->ADC_CR = 2;
  } else {
    // slow mode
    ADC->ADC_MR &= 0x7FFF0000;        // mode register "prescale" zeroed out to scan at highest speed
                                      // no high bit indicates to NOT use sequence numbers
    ADC->ADC_CHER = 0x80 >> g_line1;  // enable ADC on selected pin
    ADC->ADC_MR |= 0x80;              // set free running mode on ADC
 }
  g_adcInProgress = false;


  /*
   Some help for those working with the sequence registers with the DUE ADC: 
 
The ADC in the DUE is connected so that the Arduino pins A0,A1,A2,A3 correspond to the AVR ADC channels ch7, ch6, ch5, and ch4, respectively.  The converter can be programmed to acquire a sequence of channels with one trigger. Instructions for configuring the acquisition are given in section 43 of the SAM3x data sheet, but are rather confusing, in my opinion.
To use the registers that determine the sequence of channels that are acquired, follow these steps:
 
How many acquisitions do you want in your sequence? You must enable this many channels in the ADC_CHER. (channel enable register) 
Which channels do you want in your sequence?  These channels must be included in the ones that you enable in ADC_CHER.
EXAMPLE:  Want to have a sequence with four acquisitions.  The channels to be acquired are only two, channel 7 and channel 6. (arduino A0 and A1) 
SOLUTIONS:  four bits must be set in ADC_CHER.  Of these 4, two must be channel 7 and channel 6, and the other two are arbitrary.
            example SOL1:  CHER set up for    ch0,ch1 ch6,ch7       CHER= 0x00C3
            example SOL2:  CHER  set up for   ch1, ch6, ch7, ch12  CHER= 0x10C2
 
Now, write the channels that you want acquired into the sequence register positions for the channels that you have enabled. 
Work in numerical order from the lowest numbered acquisition channel to the highest numbered acquisition channel.  
Note that the labels in the sequence register documentation are numbered from 1 instead of zero.  This is simply a confusion
(or error) in the documentation. (that is, for example, USCH1 corresponds to the adc channel #0....USCH10 corresponds to the adc channel #9) 

EXAMPLE:  You want to acquire 7,7,6,6 with every trigger
                  USING SOL1 (ch0,ch1,ch6,ch7)
                                  SEQR1= (7<<0) | (7<<4) |(6<<24) |(6<<28); //=0x66000077
                            USING SOL2 (ch1,ch6,ch7,ch12)
                                SEQR1= (7<<4) | (7<<24) |(6<<28);  //=0x67000070
                                SEQR2=(6<<16);                                  // =0x60000
EXAMPLE2:  You want to acquire 6,7,6,7 with every trigger 
                  USING SOL1 (ch0,ch1,ch6,ch7)
                                SEQR1= (6<<0) | (7<<4) |(6<<24) |(7<<28);//=0x76000076
                  USING SOL2 (ch1,ch6,ch7,ch12)
                               SEQR1= (6<<4) | (7<<24) |(6<<28);  //=0x67000060
                               SEQR2=(7<<16);                                   //=0x70000
 
   I have attached code that acquires A1,A0,A1,A0.... like the  SOL1, example 2 above.  The ADC runs in the background, always overwriting a length 4 data array.  (Note that in this code, the ADC is clock is slowed down below the recommended speed.)  
 
 
   * 
   * 
   * 
   // Arduino Due ADC->DMA 
// modified by contravalent from code on the  internet by "Stimmer"
// this routine reads the two arduino channels A0 and A1
// fills the array global_ADCounts_Array in the background, with data from the ADC  

 pmc_enable_periph_clk(ID_ADC);   //power management controller told to turn on adc
 ADC->ADC_CR |=1; //reset the adc
 ADC->ADC_MR= 0x9038ff00;  //this setting is used by arduino. 
  // prescale :  ADC clock is mck/((prescale+1)*2).  mck is 84MHZ. 
  // prescale : 0xFF=255=164.0625KHz
  ADC->ADC_MR &=0xFFFF00FF;   //mode register "prescale" zeroed out. 
  ADC->ADC_MR |=0x0000ff00;   //slow down the adc clock so we don't interrupt so often . this divide sets it very slow
  ADC->ADC_EMR |= (1<<24);    // turn on channel numbers
  ADC->ADC_CHDR=0xFFFFFFFF;   // disable all channels   
  ADC->ADC_CHER=0x00C3;       //   use channels 0,1, 6 and 7
  ADC->ADC_MR |=0x80000000;   //USEQ bit set, saying use the sequence
  ADC->ADC_SEQR1=0x76000076;  // use0->6, use1->7, use6->6, use7->7 
  ADC->ADC_SEQR2=0x0000;
  
  NVIC_EnableIRQ(ADC_IRQn); // interrupt controller set to enable adc.
  ADC->ADC_IDR=~((1<<27)); // interrupt disable register, disables all interrupts but ENDRX
  ADC->ADC_IER=(1<<27);   // interrupt enable register, enables only ENDRX
  Serial.println();
  Serial.print("mode register ="); Serial.println(REG_ADC_MR, HEX); 
  Serial.print("channel enabled register ="); Serial.println(REG_ADC_CHSR, HEX);
  Serial.print("sequence register1 ="); Serial.println(REG_ADC_SEQR1, HEX); 
  Serial.print("interrupts ="); Serial.println(REG_ADC_IMR, HEX); delay(5000);
 // following are the DMA controller registers for this peripheral
 // "receive buffer address" 
 ADC->ADC_RPR=(uint32_t) global_ADCounts_Array;   // DMA receive pointer register  points to beginning of global_ADCount
 // "receive count" 
 ADC->ADC_RCR=NUM_CHANNELS;  //  receive counter set to 4
 // "next-buffer address"
 ADC->ADC_RNPR=(uint32_t)global_ADCounts_Array; // next receive pointer register DMA global_ADCounts_Arrayfer  points to second set of data 
 // and "next count"
 ADC->ADC_RNCR=NUM_CHANNELS;   //  and next counter is set to 4
 // "transmit control register"
 ADC->ADC_PTCR=1;  // transfer control register for the DMA is set to enable receiver channel requests
 // now that all things are set up, it is safe to start the ADC.....
 ADC->ADC_MR |=0x80; // mode register of adc bit seven, free run, set to free running. starts ADC


   */
}

void ADC_Handler() {
  // move DMA pointers to next buffer

  int flags = ADC->ADC_ISR;                           // read interrupt register
  if (flags & (1 << 27)) {                            // if this was a completed DMA
    stopADC();
    nextBuffer = NEXT_BUFFER(nextBuffer);             // get the next buffer (and let the main program know)
    ADC->ADC_RNPR = (uint32_t)padcBuffer[nextBuffer]; // put it in place
    ADC->ADC_RNCR = g_pCurrentRes->numPixels * g_pCurrentRes->numChannels;
    g_adcInProgress = false;
  }
}

void stopADC() {
  ADC->ADC_MR &= 0xFFFFFF7F;                        // disable free run mode
  g_adcLineTime = micros() - g_adcLineTimeStart;    // record microseconds
}

void startADC() {
  if (!g_adcInProgress) {
    ADC->ADC_MR |= 0x80;    // adc free running
    g_adcInProgress = true;
    g_adcLineTimeStart = micros();
  }

}


void setupInterrupts() {
  pinMode(VSYNC_PIN, INPUT);
  pinMode(HSYNC_PIN, INPUT);
  attachInterrupt(VSYNC_PIN, vsyncHandler, CHANGE);  // catch edge of vsync to get ready for measuring
  attachInterrupt(HSYNC_PIN, hsyncHandler, CHANGE);  // catch edge of hsync to start ADC
}


void flipLED() {
  volatile static bool fOn = false;

  if (fOn) {
    digitalWrite(LED_BUILTIN, LOW);
    fOn = false;
  } else {
    digitalWrite(LED_BUILTIN, HIGH);
    fOn = true;
  }
}

void vsyncHandler() {
  if (digitalRead(VSYNC_PIN) == HIGH) { // rising edge
    switch (g_phase) {
      case PHASE_CHECK:
        //todo: ?
        g_phase = PHASE_READY_TO_MEASURE;
        break;

      case PHASE_SCANNING:
      case PHASE_READY_TO_MEASURE:
      case PHASE_MEASURING:
      case PHASE_READY_FOR_SCAN:
        // we should never get here, but hey
        break;
    }
  } else { // falling edge
    switch (g_phase) {
      case PHASE_SCANNING:
        // time to end the frame and send the image
        g_reason = REASON_VSYNC;
        g_phase = PHASE_IDLE;
        break;

      case PHASE_IDLE:
      case PHASE_READY_TO_MEASURE:
      case PHASE_MEASURING:
      case PHASE_READY_FOR_SCAN:
        // we should never get here, but hey, just stop everything
        //g_phase = PHASE_IDLE;
        break;
    }
  }
}

void hsyncHandler() {
  if (digitalRead(HSYNC_PIN) == HIGH) {
    switch (g_phase) {
      case PHASE_IDLE:
        // not doing anything right now
        break;

      case PHASE_READY_TO_MEASURE:
        // start stopwatch, switch phase
        if (g_drop == 0) {
          g_measuredLineTime = micros();
          g_phase = PHASE_MEASURING;
          g_drop = DROP_LINES;
        } else {
          g_drop--;
        }
        break;

      case PHASE_MEASURING:
        // take scan time, get ready to start scanning (initiated by main program, need to set up ADC first)
        g_measuredLineTime = micros() - g_measuredLineTime;
        g_phase = PHASE_READY_FOR_SCAN;
        g_numLines = 0;
        break;

      case PHASE_SCANNING:
        if (!g_fSlow) {
          // start ADC (completion handled by ADC interrupt)
          startADC();
        } else {
          uint32_t value;
          int i;
          int pixels = g_pixels;
          int count = g_count;
          uint32_t *pDest = (uint32_t *)g_pDest;

          noInterrupts();
          while (pixels--) {
            value = 0;
            i = count;
            while (i--) {
              value += ((ADC->ADC_CDR[7-g_line1]) & 0x0FFF);  // get values, no tag
            }
            *pDest++ = value; // tag as channel 0 and store
          }
          g_fLineReady = true;
          g_pDest = (uint16_t *)&g_pbp[1];
          g_count = g_pCurrentRes->numSamples;
          g_pixels = g_pCurrentRes->numPixels;
          interrupts();
        }

        ++g_numLines;
        break;
    }
  }
}


//
// takes line scan time in us, returns pointer to resolution info, or null if not recognized
//

struct Resolution *getResolution(int lineTime) {
  int i;

  // go through known resolutions

  for (i = 0; i < NUM_MODES; i++) {
    if (lineTime > (g_allRes[i].scanLineTime - g_allRes[i].tolerance) && lineTime < (g_allRes[i].scanLineTime + g_allRes[i].tolerance)
        && g_allRes[i].numChannels == g_numChannels) {
      return &g_allRes[i];
    }
  }

  return NULL;
}




bool okToWrite() {
  return (SerialUSB.availableForWrite() > USB_MIN_WRITE_BUFFER_SIZE);
}





void loop () {
  int timeLineScan = 0;
  int line;
  static int dropCount = FAST_DROP_COUNT;

  //
  // let hsync measure the time
  //

  while (g_phase == PHASE_READY_TO_MEASURE) {
  }

  while (g_phase == PHASE_MEASURING) {
  }

  //
  // time has been measured by hsync, let's do our calculations, set up the scan buffers and start scanning!
  //
  if (g_phase == PHASE_READY_FOR_SCAN) {
    if (g_measuredLineTime < 300) {
      if (dropCount > 0) {
        g_reason = REASON_DROP;
        g_argument = g_measuredLineTime;
        g_phase = PHASE_CHECK;
        dropCount--;
      } else {
        dropCount = FAST_DROP_COUNT;
      }
    }
  }

  if (g_phase == PHASE_READY_FOR_SCAN)  {
    g_pCurrentRes = getResolution(g_measuredLineTime);
    if (g_pCurrentRes != NULL) {
      if (g_pCurrentRes != g_lastRes || g_fResetRes) {
        adjustToNewRes();
        g_lastRes = g_pCurrentRes;
        g_fResetRes = false;
      }

      // if lowres, make sure the queue has room. if hires, we have time to block on the write
      if (okToWrite()) {
        // send meta data
        sendMeta();

        g_fFrameInProgress = true;
        sendFrameHeader();
        flipLED();
        g_timeFrame = millis();
        g_fLineReady = false;
        g_pDest = (uint16_t *)&g_pbp[1];
        g_count = g_pCurrentRes->numSamples;
        g_pixels = g_pCurrentRes->numPixels;
        g_phase = PHASE_SCANNING;
      } else {
        g_phase = PHASE_CHECK;
      }
    } else {// res not recognized
      if (g_resFaults++ > MAX_RES_FAULTS) {
        g_resFaults = 0;
        g_reason = REASON_NO_RES;
        g_argument = g_measuredLineTime;
        g_phase = PHASE_IDLE;
      } else {
        g_phase = PHASE_READY_TO_MEASURE;
      }
    }
  }


  //
  // main line scanning
  // vsync will get us out of this
  //
  while (g_phase == PHASE_SCANNING) {
    // wait for scan completion, get line out of the way of the DMA controller
    line = scanAndCopyOneLine();

    // keep track of maximum scan time (we report this so we can dial it in and get max possible pixel res)
    timeLineScan = max(timeLineScan, g_adcLineTime);

    // compute check sum and fill in line and bytes
    computeCheckSum(line, g_lineBytes);

    // send the line
    sendLine(g_lineBytes);



    // catch any abort message
    if (checkAbort()) {
      reset();
      return;
    }



  }

  //
  // aftermath
  // send frame, check for abort
  //
  if (g_phase == PHASE_IDLE) {
    if (g_fFrameInProgress) {
      g_timeFrame = millis() - g_timeFrame;
      //flipLED();
      sendEndFrame (timeLineScan, g_reason);
      g_fFrameInProgress = false;
    } else {
      // reasons
      // 0 idle
      // 1 no res
      // 2 changed res
      // 3 other vsync
      sendIdle(g_reason, g_argument);
      sendMeta();
    }
    g_fFrameInProgress = false;
    g_phase = PHASE_CHECK;
  }

  if (g_phase == PHASE_CHECK) {
    // check for abort
    if (checkAbort()) {
      reset();
      return;
    }
  }


}

bool checkAbort() {
  static long lastTime = 0;
  char o = 0;
  char k;



  while (SerialUSB.available()) {
    k = SerialUSB.read();
    if (o == 'A' && k == 'B') {
      return true;
    }
    if (o == 'O' && k == 'K') {
      lastTime = millis();
      //return false;
    }
    if (o == 'C' && k == 'H') {
      readChannelSelection();
      //return false;
    }
    o = k;
  }

  return false;
}


void readChannelSelection() {
  if (SerialUSB.available()) {
    int old1 = g_line1;
    int old2 = g_line2;
    int oldnum = g_numChannels;

    int line1, line2, numChannels;

    line1 = 0;
    line2 = 0;
    numChannels = 0;

    unsigned int selectedChannels = SerialUSB.read();
    unsigned int mask = 1;
    int i;
    for (i = 0; i < 4; i++) {
      if (selectedChannels & mask) {
        line1 = i;
        selectedChannels &= ~mask;
        numChannels = 1;
        break;
      }
      mask <<= 1;
    }
    if (i != 4) {
      for (; i < 4; i++) {
        if (selectedChannels & mask) {
          line2 = i;
          selectedChannels &= ~mask;
          numChannels = 2;
          break;
        }
        mask <<= 1;
      }
    }

    if (i  != 4 &&  selectedChannels != 0) {
      numChannels = 4;
    }

    // catch "no channels selected" and go back to default
    if (numChannels == 0) {
      line1 = 0;
      line2 = 0;
      numChannels = 1;
    }
    
    
    if (old1 != line1 || old2 != line2 || oldnum != numChannels) {
      noInterrupts();
      g_line1 = line1;
      g_line2 = line2;
      g_numChannels = numChannels;
      g_fResetRes = true;
      interrupts();
//      blinkBuiltInLED(g_numChannels);
//      if (g_numChannels != 4) {
//        delay(1000);
//        blinkBuiltInLED(g_line1 + 1);
//        if (g_numChannels == 2) {
//          delay(1000);
//          blinkBuiltInLED(g_line2 + 1);
//        }
//      }
    }

  }
}

void adjustToNewRes() {
  // free previous buffers (safe to do)
  freeLineBuffers();

  //
  // calculate some basic frame parameters and allocate buffer, init adc
  //
  g_lineBytes = (g_pCurrentRes->numPixels * g_pCurrentRes->numChannels * sizeof(uint16_t));
  setupLineBuffers();
  g_fSlow = (g_pCurrentRes->numSamples > 1);
  initializeADC();
}



int scanAndCopyOneLine() {
  int line = g_numLines;

  if (g_fSlow) {
    while (!g_fLineReady) { // wait for scanned line
      if (g_phase != PHASE_SCANNING) {
        memset ((uint8_t *) g_pDest, 0, g_lineBytes);
        return line;
      }
    }


    int pixels = g_lineBytes / 2;
    uint32_t *pSource = (uint32_t *)&g_pbp[1];
    uint16_t *pDest = (uint16_t *) pSource;
    int factor = (g_pCurrentRes->numSamples);
    while (pixels--) {
      *pDest++ = (*pSource++ / factor) | ((7-g_line1)<<12); // divide by number of samples, add channel marker
    }
    memcpy(((byte *)&g_pbp[1]) + g_lineBytes, sentinelTrailer, sizeof (sentinelTrailer));
    g_fLineReady = false;
  } else {
    // in fast mode, wait for ADC DMA to finish

    //    while (NEXT_BUFFER(currentBuffer) == nextBuffer);   // while current and next are one apart
    while (g_adcInProgress);                           // while current and next are one apart

    // put the line somewhere safe from adc, just past the params header:
    memcpy(&g_pbp[1], padcBuffer[currentBuffer], g_lineBytes);
    currentBuffer = NEXT_BUFFER(currentBuffer);                         // set next buffer for waiting
  }
  return line;
}


void sendIdle(int reason, int argument) {
  if (okToWrite()) {
    SerialUSB.write(headerIdle, 16);
    SerialUSB_write_uint32_t(reason);
    SerialUSB_write_uint32_t(argument);
  }
}


/*
  setup:
  ADC->ADC_MR |= 0x80;  //set free running mode on ADC
  ADC->ADC_CHER = 0x80; //enable ADC on pin A0
  }

  sample:
    while((ADC->ADC_ISR & 0x80)==0); // wait for conversion
    values[i]=ADC->ADC_CDR[7]; //get values

*/








