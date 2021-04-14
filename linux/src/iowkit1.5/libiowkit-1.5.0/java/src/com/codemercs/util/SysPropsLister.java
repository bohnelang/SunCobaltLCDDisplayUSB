package com.codemercs.util;

import java.util.*;
import java.io.*;


/**
 * List the all System.properties 'java.library.path' and
 * 'java.ext.dirs' to stdout.
 */
public class SysPropsLister {
    public static void main(String [] argv) {
	PrintStream ps=System.out;
	ArrayList al=new ArrayList();
	try {
	    if(argv.length!=0)
		ps=new PrintStream(argv[0]);
	}
	catch(FileNotFoundException fnfe) {
	    System.out.println("Unable to create outputfile \""+argv[0]+"\" !");
	    System.exit(1);
	}
	Properties p=System.getProperties();
	Enumeration e=p.propertyNames();
	while(e.hasMoreElements()) {
	    String key=(String)e.nextElement();
	    String value=p.getProperty(key,"Key is unset!");
	    al.add(new String(key+"="+value));
	}
	Collections.sort(al);
	for(int i=0;i<al.size();i++)
	    ps.println((String)al.get(i));
	System.exit(0);
    }
}
	    
