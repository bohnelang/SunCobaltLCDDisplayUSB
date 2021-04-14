package com.codemercs.install;

import java.io.*;
import java.util.regex.PatternSyntaxException;

/**
 * The class for platform-specific installation under linux.
 * @author Eberhard Fahle
 */ 
class LinuxInstaller extends PlatformInstaller {
    /**
     * Creates a new LinuxInstaller.
     * @param libLocation a String that contains the directory where the 
     * lib ist to be installed.
     * @param libName the name of the library that is to be installed
     * @param jarName the name of the jar-file that contains the native interface-definitions
     */
    public LinuxInstaller(String libLocation, String libName, String jarName) {
	super(libLocation,libName,jarName);
    }

    /**
     * Copy the library to the java.library.path.
     * The linux-implementation of this method simply creates a symbolic link to the library.
     * @param javaLibDir the directory for the library
     * @return boolean true if the lib was sucessfully copied, false otherwise.
     */
    public boolean copyLib(File javaLibDir) {
	Runtime rt=null;	//The runtime-object for the ln command
	Process proc=null;	//The process for the ln command
	StringBuffer errList=new StringBuffer(); //Store what linux says on System.err
	BufferedReader errReader=null;	//This will read the errors from the runtime
	try {
	    rt=Runtime.getRuntime();
	    //the command to be executed by the runtime
	    String [] cmd={"ln","-s","-f",target.toString(),javaLibDir.toString()};
	    proc=rt.exec(cmd);	
	    InputStream iserr=proc.getErrorStream();
	    errReader=new BufferedReader(new InputStreamReader(proc.getErrorStream()));
	    String rtmsg;
	    while((rtmsg=errReader.readLine())!=null)
		errList.append(rtmsg+"\n");
	    if(proc.waitFor()!=0) {
		System.out.println(errList.toString());
		return false;
	    }
	}
	catch(IOException ioe) {
	    return false;
	}
	catch(InterruptedException ie) {
	    return false;
	}
	return true;
    }
}
