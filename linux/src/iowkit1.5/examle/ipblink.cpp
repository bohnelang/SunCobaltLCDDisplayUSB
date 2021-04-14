//
// ioblink.cpp - Blinking LEDs sample
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

#include <iowkit.h>

#define FALSE 0
#define TRUE 1

// Write simple value
BOOLEAN WriteSimple(IOWKIT_HANDLE devHandle, DWORD value)
{
  IOWKIT_REPORT rep;
  IOWKIT56_IO_REPORT rep56;

  // Init report
  switch (IowKitGetProductId(devHandle))
    {
      // Write simple value to IOW40
    case IOWKIT_PRODUCT_ID_IOW40:
      memset(&rep, 0xff, sizeof(rep));
      rep.ReportID = 0;
      rep.Bytes[3] = (UCHAR) value;
      return IowKitWrite(devHandle, IOW_PIPE_IO_PINS,
			 (PCHAR) &rep, IOWKIT40_IO_REPORT_SIZE) == IOWKIT40_IO_REPORT_SIZE;
      // Write simple value to IOW24
    case IOWKIT_PRODUCT_ID_IOW24:
      memset(&rep, 0xff, sizeof(rep));
      rep.ReportID = 0;
      rep.Bytes[0] = (UCHAR) value;
      return IowKitWrite(devHandle, IOW_PIPE_IO_PINS,
			 (PCHAR) &rep, IOWKIT24_IO_REPORT_SIZE) == IOWKIT24_IO_REPORT_SIZE;
      // Write simple value to IOW56
    case IOWKIT_PRODUCT_ID_IOW56:
      memset(&rep56, 0xff, sizeof(rep56));
      rep56.ReportID = 0;
      rep56.Bytes[6] = (UCHAR) value;
      return IowKitWrite(devHandle, IOW_PIPE_IO_PINS,
			 (PCHAR) &rep56, IOWKIT56_IO_REPORT_SIZE) == IOWKIT56_IO_REPORT_SIZE;
    default:
      return FALSE;
    }
}

ULONG ReadSimple(IOWKIT_HANDLE devHandle, IOWKIT56_IO_REPORT *rep56)
{
  memset(rep56, 0x00, IOWKIT56_IO_REPORT_SIZE);
  switch (IowKitGetProductId(devHandle))
    {
    case IOWKIT_PRODUCT_ID_IOW40:
      return IowKitRead(devHandle, IOW_PIPE_IO_PINS, (PCHAR) rep56, IOWKIT40_IO_REPORT_SIZE);
    case IOWKIT_PRODUCT_ID_IOW24:
      return IowKitRead(devHandle, IOW_PIPE_IO_PINS, (PCHAR) rep56, IOWKIT24_IO_REPORT_SIZE);
    case IOWKIT_PRODUCT_ID_IOW56:
      return IowKitRead(devHandle, IOW_PIPE_IO_PINS, (PCHAR) rep56, IOWKIT56_IO_REPORT_SIZE);
    default:
      return 0;
    }
}

ULONG ReadSimpleNonBlocking(IOWKIT_HANDLE devHandle, IOWKIT56_IO_REPORT *rep56)
{
  memset(rep56, 0x00, IOWKIT56_IO_REPORT_SIZE);
  switch (IowKitGetProductId(devHandle))
    {
    case IOWKIT_PRODUCT_ID_IOW40:
      return IowKitReadNonBlocking(devHandle, IOW_PIPE_IO_PINS, (PCHAR) rep56, IOWKIT40_IO_REPORT_SIZE);
    case IOWKIT_PRODUCT_ID_IOW24:
      return IowKitReadNonBlocking(devHandle, IOW_PIPE_IO_PINS, (PCHAR) rep56, IOWKIT24_IO_REPORT_SIZE);
    case IOWKIT_PRODUCT_ID_IOW56:
      return IowKitReadNonBlocking(devHandle, IOW_PIPE_IO_PINS, (PCHAR) rep56, IOWKIT56_IO_REPORT_SIZE);
    default:
      return 0;
    }
}

void drainBuffers(IOWKIT_HANDLE devHandle) {
  char dummy[64];
  int pid=0;
  int readsize=0;
  
  pid=(IowKitGetProductId(devHandle));
  switch(pid) {
    case IOWKIT_PRODUCT_ID_IOW40:
      readsize=IOWKIT40_IO_REPORT_SIZE;
      break;
    case IOWKIT_PRODUCT_ID_IOW24:
      readsize=IOWKIT24_IO_REPORT_SIZE;
      break;
    case IOWKIT_PRODUCT_ID_IOW56:
      readsize=IOWKIT56_IO_REPORT_SIZE;
      break;
  }
  while(IowKitReadNonBlocking(devHandle, IOW_PIPE_IO_PINS, dummy, readsize))
    ;
}

  
int main(int argc, char* argv[]) {
  IOWKIT_HANDLE iows[IOWKIT_MAX_DEVICES];
  int i, j;
  ULONG bits;
  int numIows;
  unsigned short sn[9];
  char snt[9];
  ULONG rc;
  DWORD pid;
  IOWKIT_HANDLE devHandle;
  IOWKIT56_IO_REPORT rep56;


  // Open device
  devHandle = IowKitOpenDevice();
  if (devHandle == NULL)
    {
      printf("Failed to open device\n");
      goto out;
    }
  // Get number of IOWs in system
  numIows = IowKitGetNumDevs();
  printf("%d IOWs in system\n", numIows);
  // Get all IOW handles
  for (i = 0; i < numIows; i++)
    {
      // Get device handle and init object
      iows[i] = IowKitGetDeviceHandle(i + 1);
      // Get serial number
      IowKitGetSerialNumber(iows[i], sn);
      pid = IowKitGetProductId(iows[i]);
      for (j = 0; j < 9; j++)
	snt[j] = sn[j];
      printf("%d PID %x, S/N \"%s\"\n", i + 1, (unsigned int) pid, snt);
      IowKitSetWriteTimeout(iows[i], 1000);
    }

  printf("Blinking LEDs...\n");
  //srand(time(NULL));
  // Blinking'
  for (i = 0; i < 100; i++)
    {
      bits = rand();
      //bits = 0x00000000;
      // Make every IOW blink
      for (j = 0; j < numIows; j++)
	{
	  // Write to simple endpoint
	  rc = WriteSimple(iows[j], bits);
	  usleep(20000);
	  //                        ReadSimpleNonBlocking(iows[j], &rep56);
	}
      // Sleep for 25ms
      usleep(25000);
    }
  printf("Blinking complete\n");
  // Set LEDs off
  for (i = 0; i < numIows; i++) {
    // Write to simple endpoint
    WriteSimple(iows[i], 0xFFFFFFFF);
    usleep(20000);
    //                ReadSimpleNonBlocking(iows[i], &rep56);
  }


  // Close device
  IowKitCloseDevice(devHandle);
 out:
  return 0;
}
