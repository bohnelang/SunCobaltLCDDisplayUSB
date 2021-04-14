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


        #LOAD=`top -b -n 1  | grep "average" | sed s/^.*average://g | sed s/" "//g | sed s/","/" "/g | awk {'print $1" "$2'}`
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

