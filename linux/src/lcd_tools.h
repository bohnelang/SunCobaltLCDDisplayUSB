#include <iowkit.h>


void LCD_PrintLine( IOWKIT_HANDLE iowHandle, char* string );
long setDRAMaddress( IOWKIT_HANDLE iowHandle, long address );
long setDRAMaddressByLineNumber( IOWKIT_HANDLE iowHandle, int lineNumber );
long enableIIC( IOWKIT_HANDLE iowHandle, BOOLEAN enable );
long enableLCD( IOWKIT_HANDLE iowHandle, BOOLEAN enable );
long initLCD( IOWKIT_HANDLE iowHandle, long numLines );
long DisplayControl( IOWKIT_HANDLE iowHandle, BOOLEAN displayOn, BOOLEAN cursorOn, BOOLEAN cursorBlink );
long EntryModeSet( IOWKIT_HANDLE iowHandle, BOOLEAN moveForward, BOOLEAN shiftDisplay );
long ClearLCD( IOWKIT_HANDLE iowHandle );
