package com.codemercs.install;

import java.io.*;
import java.util.regex.PatternSyntaxException;

/**
 * Base class for platform-specific installation classes.
 * @author Eberhard Fahle
 */ 
abstract class PlatformInstaller {
    /** The the name of the library we want to install */
    protected final String libName;
    /** The is the plattform-specific name of the library */
    protected final String libFileName;
    /** The directory where the iowkit is installed */
    protected File libDir=null;
    /** The library */
    protected File target=null;
    /** The name of the jar that contains the native-interface */
    protected String jarName;
    /** The file for the jar */
    protected File jarFile=null;

    /**
     * Creates a new PlatformInstaller.
     * @param libLocation a String that contains the directory where the 
     * lib ist to be installed.
     * @param libName the name of the library that is to be installed
     * @param jarName the name of the jar-file that contains the native interface-definitions
     */
    protected PlatformInstaller(String libLocation, String libName, String jarName) {
	this.libName=libName;
	this.libFileName=System.mapLibraryName(libName);
	try {
	    this.libDir=new File(libLocation).getCanonicalFile();
	}
	catch(IOException ioe) {
	    this.libDir=null;
	}
	if(!jarName.endsWith(".jar"))
	    jarName=jarName+".jar";
	this.jarName=jarName;
    }

    /**
     * Tests wether the library to be installed exits.
     * @return boolean True if the library exist, false otherwise.
     */
    public boolean libraryExists() {
	if(!libDir.exists())   
	    return false;
	if(!libDir.isDirectory()) 
	    return false;
	target=new File(libDir,libFileName);
	if(!target.exists())   
	    return false;
	if(!target.isFile()) 
	    return false;
	return true;
    }
    
    /**
     * Tests wether the jarfile to be installed exits.
     * @return boolean True if the jarfile exists and can be read 
     * for copying, false otherwise.
     */
    public boolean jarExists() {
	if(jarName==null || jarName.equals(""))
	    return false;
	try {
	    jarFile=new File(jarName).getCanonicalFile();
	}
	catch(IOException ioe) {
	    jarFile=null;
	    return false;
	}
	if(!jarFile.exists())   
	    return false;
	if(!jarFile.isFile()) 
	    return false;
	if(!jarFile.canRead()) 
	    return false;
	return true;
    }
    
    /**
     * Gets the first entry of the java.library.path.
     * @return File the filedescritptor for the first entry of the java.library.path.
     * Return null :<br/>
     * If there are no entries in the system.property <br/>
     * If the directory does not exist<br/>
     * If we cannot write into the directory<br/>
     */
    public File getJavaLibraryDirectory() {
	File javaLibDir=null;
	String libPath;
	String [] splitLibPath=null;
 	//get the library path for the JVM
	libPath=System.getProperty("java.library.path");
	if(libPath==null || libPath.equals("")) 
	    return null;
	try {
	    splitLibPath=libPath.split(System.getProperty("path.separator"));
	}
	catch(PatternSyntaxException pse) {
	    //I guess this can never happen, but ..??
	    return null;
	}
	if(splitLibPath.length==0) {
	    //No entries ? Huh...
	    return null;
	}
	try {
	    javaLibDir=new File(splitLibPath[0]).getCanonicalFile();
	}
	catch(IOException ioe) {
	    javaLibDir=null;
	    return null;
	}
	if(!(javaLibDir.exists() && javaLibDir.isDirectory() && javaLibDir.canWrite())) 
	    javaLibDir=null;
    	return javaLibDir;
    }

    /**
     * Gets the first entry of the java.ext.dirs.
     * @return File the filedescriptor for the first entry of the java.ext.dirs.
     * Return null :<br/>
     * If there are no entries in the system.property <br/>
     * If the directory does not exist<br/>
     * If we cannot write into the directory<br/>
     */
    public File getJavaExtentionsDirectory() {
	File javaExtDir=null;
	String extPath;
	String [] splitExtPath=null;
 	//get the extrary path for the JVM
	extPath=System.getProperty("java.ext.dirs");
	if(extPath==null || extPath.equals("")) 
	    return null;
	try {
	    splitExtPath=extPath.split(System.getProperty("path.separator"));
	}
	catch(PatternSyntaxException pse) {
	    //I guess this can never happen, but ..??
	    return null;
	}
	if(splitExtPath.length==0) {
	    //No entries ? Huh...
	    return null;
	}
	try {
	    javaExtDir=new File(splitExtPath[0]).getCanonicalFile();
	}
	catch(IOException ioe) {
	    javaExtDir=null;
	    return null;
	}
	if(!(javaExtDir.exists() && javaExtDir.isDirectory() && javaExtDir.canWrite())) 
	    javaExtDir=null;
    	return javaExtDir;
    }

    /**
     * Copy the library to the java.library.path.
     * The implementation of this method is platform-specific.
     * @param javaLibDir the directory for the library
     * @return boolean true if the lib was sucessfully copied, false otherwise.
     */
    public abstract boolean copyLib(File javaLibDir);

    /**
     * Copy the jar-file to the java.ext.dirs.
     * @param javaExtDir the directory for the jar-file
     * @return boolean true if the jar was sucessfully copied, false otherwise.
     */
    public boolean copyJar(File javaExtDir) {
	boolean retval=false;
	FileInputStream fis=null;
	FileOutputStream fos=null;
	if(jarFile==null || javaExtDir==null)
	    return retval;
	File target=new File(javaExtDir,jarName);
	try {
	    fis=new FileInputStream(jarFile);
	    fos=new FileOutputStream(target);
	    byte [] buffer=new byte[1024];
	    while(true) {
		int bytesRead=fis.read(buffer);
		if(bytesRead==-1)
		    break;
		fos.write(buffer,0,bytesRead);
	    }
	    retval=true;
	}
	catch(FileNotFoundException fnfe) {
	    retval=false;
	}
	catch(IOException ioe) {
	    retval=false;
	}
	return retval;
    }
}
