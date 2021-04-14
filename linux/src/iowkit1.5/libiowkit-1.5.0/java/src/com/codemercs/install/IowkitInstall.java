package com.codemercs.install;

import java.io.*;
import java.util.regex.PatternSyntaxException;

/**
 * Small utility that makes the <code>iowkit-library</code>
 * available to the JVM. 
 * @author Eberhard Fahle
 */ 
public class IowkitInstall {

    public static void main(String [] argv) {
	PlatformInstaller pi=null;
	if(argv.length!=3) {
	    System.err.println("We need 3 arguments!");
	    System.exit(1);
	}
	String os=System.getProperty("os.name");
	if(os==null) {
	    System.err.println("Unable to detect Operating-System!");
	    System.exit(1);
	}
	if(os.toLowerCase().equals("linux")) {
	    pi=new LinuxInstaller(argv[0],argv[1],argv[2]);
	}
	else {
	    System.err.println("Operating-System \""+os+"\" unsupported!");
	    System.exit(1);
	}
	if(!pi.libraryExists()) {
	    System.err.println("Can't find library \""+argv[1]+"\" to be installed");
	    System.exit(1);
	}
	if(!pi.jarExists()) {
	    System.err.println("Can't find jar-file \""+argv[2]+"\" to be installed");
	    System.exit(1);
	}
	File javaLibDir=pi.getJavaLibraryDirectory();
	if(javaLibDir==null) {
	    System.err.println("Can't find java library directory");
	    System.exit(1);
	}
	File javaExtDir=pi.getJavaExtentionsDirectory();
	if(javaExtDir==null) {
	    System.err.println("Can't find java extensions directory");
	    System.exit(1);
	}
	if(!pi.copyLib(javaLibDir)) {
	    System.err.println("Error while copying the native library");
	    System.exit(1);
	}
	if(!pi.copyJar(javaExtDir)) {
	    System.err.println("Error while copying the jar-file");
	    System.exit(1);
	}
	System.exit(0);
    }
}
