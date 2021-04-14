package com.codemercs.install;

import java.io.*;
import java.util.regex.PatternSyntaxException;

/**
 * Small utility that removes the <code>iowkit-library</code>
 * from the JVM. 
 * @author Eberhard Fahle
 */ 
public class IowkitUninstall {

    public static void main(String [] argv) {
	PlatformUninstaller pu=null;
	if(argv.length!=2) {
	    System.err.println("We need 2 arguments!");
	    System.exit(1);
	}
	String os=System.getProperty("os.name");
	if(os==null) {
	    System.err.println("Unable to detect Operating-System!");
	    System.exit(1);
	}
	if(os.toLowerCase().equals("linux")) {
	    pu=new PlatformUninstaller(argv[0],argv[1]);
	}
	else {
	    System.err.println("Operating-System \""+os+"\" unsupported!");
	    System.exit(1);
	}
	if(!pu.libraryExists()) {
	    System.err.println("Can't find library \""+argv[0]+"\" to be removed");
	}
	else {
	    if(!pu.deleteLibrary()) {
		System.err.println("Unable to remove library \""+argv[0]+"\"");
	    }
	}
	if(!pu.jarExists()) {
 	    System.err.println("Can't find jar-file \""+argv[1]+"\" to be removed");
	}
	else {
	    if(!pu.deleteJar()) {
		System.err.println("Unable to remove jar-file \""+argv[1]+"\"");
	    }
	}
	System.exit(0);
    }
}
