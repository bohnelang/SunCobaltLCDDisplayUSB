package com.codemercs.test;

import com.codemercs.iow.IowKit;

/**
 * A simple demo for the IowKit-Library functions.
 * After the application is started from the console it will list
 * all IOWarriors.
 * Then it will blink the Led's on the Starter-Kits.
 * After that it will try to read 10 Reports from each device and print
 * them to the console.
 */
public class IowBlink {
    /** Constant for the ProductId of an IOWarrior40 */
    public static final long PID_IOW40=0x1500L;
    /** Constant for the ProductId of an IOWarrior24 */
    public static final long PID_IOW24=0x1501L;
    /** Constant for the ProductId of an IOWarrior56 */
    public static final long PID_IOW56=0x1503L;
	
    private long []handles=new long[0];
	
    /**
     * Opens the IowKit library
     */
    public IowBlink() {
	if(IowKit.openDevice()!=0L) {
	    handles=new long[(int)IowKit.getNumDevs()];
	    for(int i=0,j=1;i<handles.length;i++,j++)
		handles[i]=IowKit.getDeviceHandle(j);
	    listDevices();
	    blink();
	    readNonBlocking();
	    IowKit.closeDevice(0L);
	}
	else
	    System.out.println("No IOWarriors found");
	System.exit(0);
    }
		
	
    private void listDevices() {
	System.out.println("Found "+handles.length+" IOWarriors");
	for(int i=0,j=1;i<handles.length;i++,j++) {
	    System.out.print(j+" - ");
	    System.out.print("Product = "+Long.toHexString(IowKit.getProductId(handles[i])));
	    System.out.print(" Serial = "+IowKit.getSerialNumber(handles[i]));
	    System.out.print(" Revision = "+Long.toHexString(IowKit.getRevision(handles[i])));
	    System.out.println(" Handle = "+Long.toHexString(handles[i]));
	    IowKit.setWriteTimeout(handles[i],1000L);
	}
    }

    private void blink() {
	int [] data=null;
	int ledOn=0x00;
	int ledOff=0xFF;
	int ledState;
	for(int i=0;i<10;i++) {
	    if((i%2)!=0)
		ledState=ledOff;
	    else
		ledState=ledOn;
	    for(int j=0;j<handles.length;j++) {
		long pid=IowKit.getProductId(handles[j]);
		if(pid==PID_IOW24) { 
		    data=new int[3];
		    data[0]=0x00;
		    data[1]=ledState;
		    data[2]=0xFF;
		}
		else if(pid==PID_IOW40) { 
		    data=new int[5];
		    data[0]=0x00;
		    data[1]=0xFF;
		    data[2]=0xFF;
		    data[3]=0xFF;
		    data[4]=ledState;
		}
		else if(pid==PID_IOW56) { 
		    data=new int[8];
		    data[0]=0x00;
		    data[1]=0xFF;
		    data[2]=0xFF;
		    data[3]=0xFF;
		    data[4]=0xFF;
		    data[5]=0xFF;
		    data[6]=0xFF;
		    data[7]=ledState;
		}
		else {
		    //no starterkit for the power vampire
		    //so we don't blink here
		    continue;
		}
		if(IowKit.write(handles[j],0,data)!=(long)data.length) {
		    System.out.println("Error writing to device No."+(j+1));
		}
		else {
		    try {
			Thread.sleep(250);
		    }
		    catch(InterruptedException ie) {
		    }
		}
	    }
	}
	// Now clear the buffers which have been filled by the previous
	// writes to the IO-PINs
	for(int j=0;j<handles.length;j++) {
	    boolean bufferEmpty=false;
	    int [] inData={};
	    long pid=IowKit.getProductId(handles[j]);
	    while(!bufferEmpty) {
		if(pid==PID_IOW24) {
		    inData=IowKit.readNonBlocking(handles[j],0,3);
		}	
		else if(pid==PID_IOW40) { 
		    inData=IowKit.readNonBlocking(handles[j],0,5);
		}
		else if(pid==PID_IOW56) { 
		    inData=IowKit.readNonBlocking(handles[j],0,8);
		}
		if(inData.length==0)
		    bufferEmpty=true;
	    }
	}				
    }
	
    private void readNonBlocking() {
	int inData[]=null;
	System.out.println("Now push the button on the Starterkit (waits for 10 seonds)");
	long pid=IowKit.getProductId(handles[0]);
	for(int i=0;i<100;i++) {
	    if(pid==PID_IOW24) {
		inData=IowKit.readNonBlocking(handles[0],0,3);
	    }	
	    else if(pid==PID_IOW40) { 
		inData=IowKit.readNonBlocking(handles[0],0,5);
	    }
	    else if(pid==PID_IOW56) { 
		inData=IowKit.readNonBlocking(handles[0],0,8);
	    }
	    if(inData.length!=0) {
		System.out.print("read "+inData.length+" Bytes ");
		for(int k=0;k<inData.length;k++) {
		    String dString;
		    if(inData[k]<16)
			dString=new String("0"+Integer.toHexString(inData[k]));
		    else
			dString=Integer.toHexString(inData[k]);
		    System.out.print(dString +" ");
		}
		System.out.println();
	    }
	    try {
		Thread.sleep(100);
	    }
	    catch(InterruptedException ie) {
	    }
	}
	System.out.println();
    }
	
    /**
     * Starts the demo.
     * If the IowKit-library for the current plattform is not installed
     * properly, the application will exit with an error message.
     */
    public static void main(String [] argv) {
	try {
	    System.out.println("Sample Application for the Iowkit-library");
	    System.out.print("Checking Library Version...");
	    System.out.println(IowKit.version());
	}
	catch(Throwable e) {
	    System.out.println("The IowKit plattform-library was not found!");
	    System.out.println("Error reported from the Java runtime:\n"+e);
	    System.exit(0);
	}
	IowBlink iob=new IowBlink();
    }
}
