# Display Adapter for Cobalt RAQ 3/4 LCD Display with Linux USB 

https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/Pinout_Project.jpg
![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltDisplayUSB/blob/main/img/display1.jpg)

About 15 years ago I developed a adapter to interface Sun Cobalt RaQ 4 LCD display with a normal mainboard by USB self-made adapter.

**Remark: This project is outdated** because the IO-Warrior24 from Code Mercenaries is not longer in stock and sold. :-(

## Background: 
This github page should document how to use the Sun Cobalt display in general. 

## My project:
### Images 

![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/coretemp1.jpg)

![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/inside1.jpg)


![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/adapter1.jpg)

![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/adapter2.jpg)


![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/usb1.jpg)

![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/start1.jpg)

![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/lcd1.jpg)

![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/lcdconnection1.jpg)

![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/front1.jpg)

### PCB (printed circuit board) of my adapter
#### Eagle 6.6 files (http://eagle.autodesk.com/eagle/software-versions/10)

![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/eagle/iow24/pic1.jpg)
![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/eagle/iow24/pic2.jpg)

https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/eagle/iow24/iow24my5.sch
https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/eagle/iow24/iow24my5.brd

Eagle library for IO-Warrier:
https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/eagle/iow24/io-warrior.lbr

#### Mini serie
Ordered  my mini serie from LitOn https://www.leiton.de/kalkulation.html

### Source code
#### Linux

All Linux kernel > 2.6 have a native support for the I/O-Warrior USB devices! There is no extra installation of a kernel modul. thus is is PNP - Plug and Play :-)  
iowarrior kernel module: https://github.com/torvalds/linux/blob/main/drivers/usb/misc/iowarrior.c

System 
```
[   33.071956] iowarrior 3-2:1.0: IOWarrior product=0x1501, serial=00002357 interface=0 now attached to iowarrior0
[   33.080787] iowarrior 3-2:1.1: IOWarrior product=0x1501, serial=00002357 interface=1 now attached to iowarrior1
[   33.080933] usbcore: registered new interface driver iowarrior
```

Program to interface LCD
```
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "lcd_tools.c"

IOWKIT_HANDLE   iowHandle = NULL;

static char*    ledOnPacket = "\x00\xF7\xFF";
static char*    ledOffPacket = "\x00\xFF\xFF";
static char*    enableRC5Packet  = "\x0c\x01\x00\x00\x00\x00\x00\x00";
static char*    disableRC5Packet = "\x0c\x00\x00\x00\x00\x00\x00\x00";

int
main(int argc,char** argv)
{
        iowHandle = IowKitOpenDevice();
        if( !iowHandle )
        {
                fprintf(stderr,"could not open io-warrior");
                return 0;
        }

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
        
        exit(0);
}
```

Call this program by: 
```
#> lcd_write "Hello" "World".
```

Display system health and other information on front display of the Cobalt - call this in crontab:
```
#!/bin/bash


# apt install lm-sensors
# apt install install hddtemp
# apt install dnsutils
# apt install wget

SELF=$$; (sleep 58 && kill -9 $SELF 2> /dev/null )&

LCD=/usr/local/bin/lcd_write
HDDTEMP=/usr/sbin/hddtemp

HOST=`hostname`

IPADDR_EXT=`nslookup $HOST | grep "^Address: " | sed s/"Address: "//g`
MYIIP=`wget -q -O - 'https://checkip.amazonaws.com/'`

IPADDR_EXT=$MYIIP
if [ "$IPADDR_EXT" = "" ]; then
        IPADDR_EXT="IP Addr Not Set"
fi

HOSTNAME=`cat /etc/hosts | grep $HOST| awk {'print $2'} `
IPADDR=`cat /etc/hosts | grep $HOST| awk {'print $1'}`
DELAY=2


for I in 1 2 3
do

        TEMP=`$HDDTEMP -n /dev/sda`
        TEMP="Disk1: $TEMP C"

        TERM_DISK=`$HDDTEMP -n /dev/sdb`
        TERM_DISK="Disk2: $TERM_DISK C"

        TERMCPU=`sensors | tail -2 | awk {'print $3'}`

        LOAD=`cat /proc/loadavg | awk {'print $1" "$2'}`
        LOAD="Load: $LOAD"

        DATE=`date +"%R %F"`

        DEV="/"
        DISK1=`df -h $DEV |  tail -1 | awk {'print $5" "$3" "$4'}`
        DISK1="$DEV: $DISK1"

        DEV="/data"
        DISK2=`df -h $DEV |  tail -1 | awk {'print $5" "$3" "$4'}`
        DISK2="$DEV: $DISK2"

        $LCD "$HOSTNAME" "$IPADDR"
        sleep $DELAY

        $LCD "$HOSTNAME" "$IPADDR_EXT"
        sleep $DELAY


        $LCD "$DATE" "$LOAD"
        sleep $DELAY

        $LCD "$TEMP" "$TERM_DISK"
        sleep $DELAY

        $LCD "$DISK1" "$DISK2"
        sleep $DELAY

        $LCD "CPU: $TERMCPU" ""
        sleep $DELAY
done



```

## Pinout


### HD44780 Display of the Cobalt (HDM16216L-5)
![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/HD44780_Pinout.jpg)

![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/doc/127855466716216l5.pdf)

### Pinout cobalt and IOW24
![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/Pinout_Project.jpg)

## History
Nice article in German language about IO-Warrier ICs: https://www.heise.de/ct/artikel/Draht-nach-draussen-288792.html


## Epilogue
It is a pitty that the most ICs of IO-Warrier are not longer available. But I guess this is the way of live - DIL and DIP ICs seems not longer assembeled for the consumer market. Especially the USB I/O  Controller like CY7C63513C (https://www.digikey.de/product-detail/de/cypress-semiconductor-corp/CY7C63513C-PVXC/428-2253-5-ND/1154401) from  Cypress Semiconductor Corporation are at their end-of-live. There are less breadboard friendly ICs to find online. At least is the  IO-Warrior28  https://www.codemercs.com/de/io/full-speed from Code Mercenaries.

It seems that in a time of Arduino and Rasberry PI with its own GPIO-ports any native I/O-USB controllers like the IO-Warrier are not longer necessary. There are some I/O controller with I2C interface still available. 

If you know a uptodate breadboard friendly USB I/O controller please let me know. 

## Ralated sites:
### RAQ550 Pinout (This is from a RAQ550 and https://metalab.at/wiki/Blinkenwall.com/Controller )
![Title](https://raw.githubusercontent.com/bohnelang/SunCobaltLCDDisplayUSB/main/imgs/RAQ550_Pinout.jpg)


