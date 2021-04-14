#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#ifndef min
#define min(a, b) (((a) < (b)) ? (a) : (b))
#endif

#include "lcd_tools.c"

IOWKIT_HANDLE	iowHandle = NULL;

static char*	ledOnPacket = "\x00\xF7\xFF";
static char*	ledOffPacket = "\x00\xFF\xFF";
static char*	enableRC5Packet  = "\x0c\x01\x00\x00\x00\x00\x00\x00";
static char*	disableRC5Packet = "\x0c\x00\x00\x00\x00\x00\x00\x00";

static char* 	buffer = "Hallo Welt";

int
main(int argc,char** argv)
{	
	iowHandle = IowKitOpenDevice();
	if( !iowHandle )
	{
		fprintf(stderr,"could not open io-warrior");
		return 0;
	}	
//	WriteSimple(iowHandle, 0xFFFF,0xFFFF);

	IowKitWrite( iowHandle, 1, enableRC5Packet, 8 );


	IowKitWrite( iowHandle, 0, ledOnPacket, 3 );
	enableIIC( iowHandle, TRUE );
	enableLCD( iowHandle, TRUE );	
	initLCD( iowHandle, 4 );	
	DisplayControl( iowHandle, TRUE, FALSE, FALSE );
	EntryModeSet( iowHandle, TRUE, FALSE );



	if(argc==2){
		setDRAMaddressByLineNumber( iowHandle, 1 );
		LCD_PrintLine( iowHandle, argv[1] );
	} else if(argc==3) { 		
		setDRAMaddressByLineNumber( iowHandle, 1 );
		LCD_PrintLine( iowHandle, argv[1] );
		setDRAMaddressByLineNumber( iowHandle, 2 );
		LCD_PrintLine( iowHandle, argv[2] );
	}

/*
   IOWKIT24_IO_REPORT report;
   memset(&report, 0, IOWKIT24_IO_REPORT_SIZE); //Set var report to 0 for protect mal-function
   report.ReportID = 40;
   report.Value = 255;     

	ULONG rc=IowKitWrite(iowHandle, IOW_PIPE_IO_PINS, (char*) &report, IOWKIT24_IO_REPORT_SIZE);
*/
/* Blink testen

	StatusAuslesen(iowHandle);
	int i;

	for(i=0; i<30; i++){
		WriteSimple(iowHandle, 0xFFFF, 0xFFFF);
		sleep(1);
		WriteSimple(iowHandle, 0x0000, 0x0000);
		sleep(1);
	}
*/

	exit(0);
}


