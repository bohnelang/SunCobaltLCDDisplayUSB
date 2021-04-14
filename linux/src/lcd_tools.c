#include "lcd_tools.h"


// print line with up to 20 characters to lcd
void LCD_PrintLine( IOWKIT_HANDLE iowHandle, char* string )
{
	char flag = 0x80;
	char* current;
	
	char buffer[21];
	buffer[20] = 0x00;
	memset( buffer, 0x20, 20 );
	memcpy( buffer, string, strlen(string) );

	current = buffer;

	while( *current )
	{
		char packet[8];
		memset( packet, 0x00, 8 );

		packet[0] = 0x05;
		packet[1] = flag | (char)min( strlen(current), 6 );
		memcpy( packet+2, current, (char)min( strlen(current), 6 ) );

		IowKitWrite( iowHandle, 1, packet, 8 );

		current += min( strlen(current), 6 );
	}
}

// set lcd address to write to
long setDRAMaddress( IOWKIT_HANDLE iowHandle, long address )
{
	char packet[8];
	memset( packet, 0x00, 8 );
	packet[0] = 0x05;
	packet[1] = 0x01;
	packet[2] = 0x80 | (char)address;
	return IowKitWrite( iowHandle, 1, packet, 8 );	
}

// set lcd address to write to by line number
long setDRAMaddressByLineNumber( IOWKIT_HANDLE iowHandle, int lineNumber )
{
	long address = 0x00;
	if( lineNumber < 1 || lineNumber > 4 ) return 0;
	if( lineNumber == 2 ) address |= 0x40;
	else if( lineNumber == 3 ) address |= 0x14;
	else if( lineNumber == 4 ) address |= 0x54;
	return setDRAMaddress( iowHandle, address );
}

// enable iic mode of iowarrior
long enableIIC( IOWKIT_HANDLE iowHandle, BOOLEAN enable )
{
	char packet[8];
	memset( packet, 0, 8 );
	packet[0] = 0x01;
	packet[1] = (char)enable;
	return IowKitWrite( iowHandle, 1, packet, 8 );
}

// enable lcd
long enableLCD( IOWKIT_HANDLE iowHandle, BOOLEAN enable )
{
	char packet[8];
	memset( packet, 0, 8 );	
	packet[0] = 0x04;
	packet[1] = enable ? 0x01 : 0x00;
	return IowKitWrite( iowHandle, 1, packet, 8 );
}

// initialize lcd
long initLCD( IOWKIT_HANDLE iowHandle, long numLines )
{
	char packet[8] = { 0x05, 0x03, 0x28, 0x01, 0x0c, 0x00, 0x00, 0x00 };
	packet[2] |= numLines > 1 ? 0x10 : 0x00 ;
	return IowKitWrite( iowHandle, 1, packet, 8 );		
}

// setup display control 
long DisplayControl( IOWKIT_HANDLE iowHandle, BOOLEAN displayOn, BOOLEAN cursorOn, BOOLEAN cursorBlink )
{			
	char packet[8] = { 0x05, 0x01, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00 };
	packet[2] |= displayOn ? 0x04 : 0x00;
	packet[2] |= cursorOn ? 0x02 : 0x00;
	packet[2] |= cursorBlink ? 0x01 : 0x00;
	return IowKitWrite( iowHandle, 1, packet, 8 );
}

// setup entrymode
long EntryModeSet( IOWKIT_HANDLE iowHandle, BOOLEAN moveForward, BOOLEAN shiftDisplay )
{
	char packet[8] = { 0x05, 0x01, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00 };
	packet[2] |= moveForward ? 0x02 : 0x00;
	packet[2] |= shiftDisplay ? 0x01 : 0x00;
	return IowKitWrite( iowHandle, 1, packet, 8 );
}

long ClearLCD( IOWKIT_HANDLE iowHandle )
{
	char packet[8] = { 0x05, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00 };	
	return IowKitWrite( iowHandle, 1, packet, 8 );
}

// value = lower Byte, value2 = higher Byte
int WriteSimple(IOWKIT_HANDLE devHandle, long value, long value2)
{
   IOWKIT56_IO_REPORT rep;

   // Init report
   memset(&rep, 0xff, IOWKIT56_IO_REPORT_SIZE);
   rep.ReportID = 0;
   switch (IowKitGetProductId(devHandle))
   {
   // Write simple value to IOW40
   case IOWKIT_PRODUCT_ID_IOW40:
      rep.Bytes[3] = (BYTE) value;
      return IowKitWrite(devHandle, IOW_PIPE_IO_PINS, (PCHAR) &rep, IOWKIT40_IO_REPORT_SIZE) == IOWKIT40_IO_REPORT_SIZE;
   // Write simple value to IOW24
   case IOWKIT_PRODUCT_ID_IOW24:
      rep.Bytes[0] = (BYTE) value;
      rep.Bytes[1] = (BYTE) value2;
      return IowKitWrite(devHandle, IOW_PIPE_IO_PINS, (PCHAR) &rep, IOWKIT24_IO_REPORT_SIZE) == IOWKIT24_IO_REPORT_SIZE;
   case IOWKIT_PRODUCT_ID_IOW56:
   // Write simple value to IOW56
      rep.Bytes[6] = (BYTE) value;
      return IowKitWrite(devHandle, IOW_PIPE_IO_PINS, (PCHAR) &rep, IOWKIT56_IO_REPORT_SIZE) == IOWKIT56_IO_REPORT_SIZE;
   default:
      return FALSE;
   }
}

int StatusAuslesen(IOWKIT_HANDLE devHandle)
{

 // ###################### STATUS AUSLESEN ####################
   // Init report
   // Report ID 0 is for writing to 32 input/output pins
   long rc;
   int j;
   long buffer;
   IOWKIT56_IO_REPORT rep;
   rep.ReportID = 0xFF;
      // Read non blocking
   // Init report
   memset(&rep, 0, IOWKIT24_IO_REPORT_SIZE);   
   rc = IowKitReadImmediate(devHandle, &buffer);
   printf("%lx\n",buffer);
   printf("IOWKIT24_IO_REPORT_SIZE : %d\n",IOWKIT24_IO_REPORT_SIZE - 1);
   printf("ReadNonBl(): rc=%u bits=", (unsigned int)rc);
   for (j = IOWKIT24_IO_REPORT_SIZE - 1; j >= 0; j--)
      printf("%02x", rep.Bytes[j]);
   printf("\n\n\n");

   return rc;
}

