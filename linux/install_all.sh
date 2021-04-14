#!/bin/bash

# Module is official part of Linux (I/O-Warrior USB devices)
# https://github.com/torvalds/linux/blob/master/drivers/usb/misc/iowarrior.c


#lsmod  | grep iowarrior
#modprobe  iowarrior

if test "`whoami`" != "root"
then
	echo "Only root can do this..."
	exit 1
fi

if ! test -e /etc/udev/rules.d/10-iowarrior.rules
then
	cp 10-iowarrior.rules  /etc/udev/rules.d/10-iowarrior.rules
	which udevadm > /dev/null  && udevadm control --reload-rules
	which udevadm > /dev/null  && udevadm trigger
	test -e '/sys/module/iowarrior/uevent' && echo "change" >  /sys/module/iowarrior/uevent
fi

cd src

if ! test -e /usr/lib/libiowkit.a
then
	$P=`pwd`
	cd  iowkit1.5/libiowkit-1.5.0
	make clean 2> /dev/null >/dev/null
	./configure
	make -j2
	make install
	make clean
	cd $P 
fi

echo "Compiling lcd_write..."
gcc main.c -g -o lcd_write -liowkit

if ! test -e /usr/local/bin
then 
	mkdir -p /usr/local/bin
fi

echo "Copy lcd_write to /usr/local/bin..."
cp lcd_write /usr/local/bin

echo "Testing LCD with Hello World..."
/usr/local/bin/lcd_write "Hello" "World"

exit 0 
