package com.codemercs.test;

import java.lang.reflect.Method;

/**
 * A small test-program for the Iowkit-lib.
 * It uses the java reflection-api to lookup the Iowkit.class. 
 * If that was successfull, it tries to lookup the version() method
 * and print the returned String to System.out.
 * @author Eberhard Fahle
 */ 
public class IowkitTest {
    public static void main(String [] argv) {
	try {
	    Class c=Class.forName("com.codemercs.iow.IowKit");
	    Method m=c.getDeclaredMethod("version",null);
	    String version=(String)m.invoke(null,null);
	    System.out.println("Iowkit-Library is found!");
	    System.out.println("Iowkit for Java version is : \""+version+"\"\n");
	}
	catch(Exception e) {
	    System.err.println("Unable to find class \"IowKit\"! An Exception was thrown ...\n"+e+"\n");
	    System.exit(-1);
	}

	System.exit(0);
    }
}
