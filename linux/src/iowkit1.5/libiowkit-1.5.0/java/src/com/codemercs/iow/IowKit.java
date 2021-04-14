/*
 * Created on 10.06.2005
 */
package com.codemercs.iow;

/**
 * <p>The Java class to access the IO-Warrior uses the API of the dynamic library iowkit
 * (iowkit.dll or libiowkit.so). The class is directly implemented in the dynamic library
 * itself via JNI. The normal API is the same for Windows and Linux so the JNI part uses
 * a single source for the implementation on both systems.</p>
 * <p>The API of the Java class closely resembles the normal API of the dynamic library.
 * Each API function is represented by a method where the name prefix "IowKit" has been
 * dropped. The class already carries that name so there is no need to repeat it.</p>
 * <p>The type IOWKIT_HANDLE is represented as long. That poses no problem because it is
 * only handed around.</p>
 * <p>With that in mind most of the methods are documented through their API counterparts.
 * No parameter will ever carry values big enough to cause problems with signed versus
 * unsigned values.</p>
 * @author Thomas Wagner
 * @author Robert Marquardt
 * @version 1.5
 */
public class IowKit {

    static {
	System.loadLibrary("iowkit");
    }
	
    /**
     * <p>Opens all available IO-Warrior devices and returns the handle to the first device
     * found.</p>
     * <p>(wraps <code>IOWKIT_HANDLE IOWKIT_API IowKitOpenDevice(VOID)</code>)</p>
     * <p>Calling this function several times is possible, but not advisable. The devices get
     * reenumerated and therefore the position in the list for a specific device may change.</p>
     * <p>Returning the first IO-Warrior found makes it simpler for programmers to handle the
     * use of only one IO-Warrior.</p>
     * <p>libiowkit.so for Linux only handles a maximum of 8 IO-Warriors wheras iowkit.dll for
     * Windows handles 16 IO-Warriors.</p>
     *
     * @return The value returned is an opaque handle to the specific device to be used in most of
     * the other functions of the API. The return value for failure is 0. The most common reason
     * for failure is of course that no IO-Warrior is connected.
     */
    static public native long openDevice();
	
    /**
     * <p>Close all IO-Warriors.</p>
     * <p>(wraps <code>VOID IOWKIT_API IowKitCloseDevice(IOWKIT_HANDLE devHandle)</code>)</p>
     * <p>You must call this function when you are done using IO-Warriors in your program.
     * If multiple IO-Warriors are present all will be closed by this function.</p>
     * @param devHandle openDevice() and closeDevice() use a IOWKIT_HANDLE for the most common
     * case of only one IO-Warrior connected to the computer. This way you do not have to think
     * about getNumDevs() or getDeviceHandle() at all. As of dynamic library version 1.4 the
     * function ignores the parameter completely. Since it closes all opened IO-Warriors anyway,
     * there is no real need to check if the parameter is the IOWKIT_HANDLE returned by openDevice().
     * The parameter is now only retained for compatibility and cleaner looking sources. If you
     * handle only a single IO-Warrior in your program then openDevice() and closeDevice() look 
     * and work as intuition suggests.
     */
    static public native void closeDevice(long devHandle);
	
    /**
     * Write an array of bytes to pipe numPipe of IO-Warrior.
     * <p>(wraps <code>ULONG IOWKIT_API IowKitWrite(IOWKIT_HANDLE devHandle, ULONG numPipe,PCHAR buffer, ULONG length)</code>)</p>
     * <p>The return value is the number of bytes written. Each int of buffer contains a single
     * byte only. The upper bytes of the ints are ignored. The length of the array determines
     * the number of bytes to write.</p>
     * <p>Writing something else than a single report of the correct size and a valid report ID
     * for the pipe fails for Windows. The function allows writing to the I/O pins through pipe 0
     * and Special Mode functions through pipe 1. To be completely compatible with the Windows
     * version libiowkit.so expects a ReportID 0 for pipe 0 (I/O pins) even if Linux does not have
     * a ReportID on pipe 0. The ReportID is stripped from the data sent to the device.</p>
     * <p>Sample write to pipe 0 of an IO-Warrior 40:</p>
     * <p>DWORD value consists of 32 bits, which correspond to the 32 IO-Warrior 40 I/O pins. Each
     * bit has the following meaning:</p>
     * <p>When a 1 is written to a pin the output driver of that pin is off and the pin is pulled 
     * high by an internal resistor. The pin can now be used as an input or an output with high state.</p>
     * <p>When a 0 is written to a pin the output driver is switched on pulling the pin to ground. 
     * The pin is now a output driving low.</p>
     * <p>For example, writing 0 (all 32 bits are zero) to IO-Warrior sets all pins as outputs driving
     * low (so if you have LEDs connected to them they will be on).</p>
     * <p>Reading the status of the pins does always return the logic level on the pins, not the value
     * written to the pin drivers.</p>
     * <p>Writing 0xFFFFFFFF (value in hex, all 32 bits set) sets all pins as inputs.</p>
     * <p>Note that if you want to use a pin as an input, you must first set it up as input, in other 
     * words, you must write 1 to it. For connected LEDs this means they go off.</p>

     * <p>To switch off all LEDs of an IO-Warrior 40 write an array of</p>
     * <p>0, // ReportID</p>
     * <p>0xff, 0xff, 0xff, 0xff // 32 bits</p>
     * <p>to pipe 0.</p>
     *
     * @param devHandle Handle of the desired IO-Warrior.
     * @param numPipe Number of the pipe (see IO-Warrior specs) to write to.
     * @param buffer Report buffer to write.
     * @return Number of bytes written.
     */
    static public native long write(long devHandle, long numPipe, int[] buffer);

    /**
     * Read data from IO-Warrior.
     * <p>(wraps <code>ULONG IOWKIT_API IowKitRead(IOWKIT_HANDLE devHandle, ULONG numPipe,PCHAR buffer, ULONG length)</code>)</p>
     * <p>This function reads length bytes from IO-Warrior and returns the bytes in an array of
     * ints. Each byte of data is returned as an int. No sign extension happens for the byte so
     * the value of each int is always in the range of 0..255.</p>
     * <p>Note that you must specify the number of the pipe (see IO-Warrior specs) to read from.
     * numPipe ranges from 0 to IOWKIT_MAX_PIPES-1.</p>
     * <p>Since the IO-Warriors are HID devices, you can only read the data in chunks called
     * reports. Each report is preceded by a ReportID byte. The "IOWarriorDatasheet.pdf"
     * elaborates about that. Keep in mind that data is always returned in report chunks, so
     * reading 5 bytes from the IO-pins of an IO-Warrior 24 would only return an array of 3 ints
     * because the IO-Warrior 24 has a 3 byte report whereas an IO-Warrior 40 has a 5 byte report.</p>
     * <p>The Special Mode pipe has a report size of 8 bytes for all IO-Warriors.</p>
     * <p>Linux does not have a ReportID byte of 0 for pipe 0 (I/O pins). To be completely
     * compatible with Windows libiowkit.so adds that ReportID to the data.</p>
     * <p>As of dynamic library version 1.4 the function correctly reads several reports at once.</p>
     * <strong><p>ATTENTION!</p>
     * </strong><p>This function blocks the current thread until something changes on IO-Warrior
     * (i.e. until user presses a button connected to an input pin, or until IIC data arrives),
     * so if you do not want your program to be blocked you should use a separate thread for reading
     * from IO-Warrior. If you do not want a blocking read use readNonBlocking(). Alternatively you
     * can set the read timeout with setTimeout() to force read() to fail when the timeout elapsed.</p>
     * @param devHandle Handle of the disired IO-Warrior.
     * @param numPipe Number of the pipe (see IO-Warrior specs) to read from.
     * @param length Desired amount of bytes.
     * @return int[] An arry of int with the data read from the IOWarrior. If no data was read, because
     * we encountered a timeout, or there was an error reading from the device. The length of 
     * the array will be 0. 
     */
    static public native int[] read(long devHandle, long numPipe, long length);	
	
    /**
     * Read data from an IO-Warrior without blocking.
     * <p>(wraps <code>ULONG IOWKIT_API IowKitReadNonBlocking(IOWKIT_HANDLE devHandle, 
     * ULONG numPipe,PCHAR buffer, ULONG length)</code>)</p>
     * Though this method uses the same parameters as {@link IowKit#read} it behaves differently.
     * The method will always return immediatly. If there is any data to be read it is returned
     * in an array of ints. If no new data is available the length of the array returned will be 0.
     * <p>Linux does not have a ReportID byte of 0 for pipe 0 (I/O pins). To be completely
     * compatible with Windows libiowkit.so adds that ReportID to the data.</p>
     * <p>As of dynamic library version 1.4 the function correctly reads several reports at once.</p>
     * @param devHandle Handle of the disired IO-Warrior.
     * @param numPipe Number of the pipe (see IO-Warrior specs) to read from.
     * @param length Desired amount of bytes.
     * @return int[] An arry of int with the data read from the IOWarrior. If no data was read, 
     * the length of the array returned will be 0. 
     */
    static public native int[] readNonBlocking(long devHandle, long numPipe, long length);	

    /**
     * Return last value read from IO-Warrior pipe 0 (I/O pins).
     * <p>(wraps <code>BOOLEAN IOWKIT_API IowKitReadImmediate(IOWKIT_HANDLE devHandle, PDWORD value)</code>)</p>
     * <p>The function returns a DWORD as an array of 4 ints if a new value has arrived 
     * otherwise it returns an array of 0 ints. Each byte of data is returned as an int. No sign
     * extension happens for the byte so the value of each int is always in the range of 0..255.</p>
     * <p>The function can only read the I/O pins via pipe 0 so it does not need a numPipe
     * parameter. It also abstracts from the number of I/O pins the device has. It always returns
     * 32 bits as a DWORD. For the IO-Warrior24 which has only 16 I/O pins the upper WORD of the
     * DWORD is set to 0.</p>
     * <p>Internally 8 reports are buffered to allow using a timer to call IowKitReadImmediate()
     * without losing fast bursts of reports. IowKitRead() also uses the buffered reports so do
     * not mix calls to IowKitRead() and IowKitReadImmediate() without careful consideration.
     * This is not true for the Linux version yet.</p>
     * <p>There is no non-blocking read for pipe 1 (Special Mode Functions) because pipe 1 has
     * a command interface where you write a command and then read the answer.</p>
     * @param devHandle Handle of the disired IO-Warrior.
     * @return Array of 4 ints if succeedes otherwise an empty array.
     */
    static public native int[] readImmediate(long devHandle);	
	
    /**
     * Returns the number of IO-Warriors devices present.
     * <p>(wraps <code>ULONG IOWKIT_API IowKitGetNumDevs(VOID)</code>)</p>
     * <p>The function has to be called after openDevice() to return meaningful results.
     * Plugging or unplugging IO-Warriors after calling openDevice() is not handled. The number
     * getNumDevs() returns stays the same.</p>
     * @return Number of connected IO-Warrior devices.
     */
    static public native long getNumDevs();
	
    /**
     * Access a specific IO-Warrior present.
     * <p>(wraps <code>IOWKIT_HANDLE IOWKIT_API IowKitGetDeviceHandle(ULONG numDevice)</code>)</p>
     * <p>numDevice is an index into the available IO-Warrior devices. The number range is
     * 1 to getNumDevs(). Any value outside that range returns 0. getDeviceHandle(1) returns
     * the same handle as openDevice().</p>
     * <p>Understand this function as an extension to openDevice(). openDevice() has opened all
     * IO-Warriors but has only returned the first one found. getDeviceHandle() allows to access
     * the other devices found.</p>
     * @param numDevice Index into the available IO-Warrior devices.
     * @return The device handle of the releated IO-Warrior device.
     */
    static public native long getDeviceHandle(long numDevice);
	
    /**
     * Return the Product ID of the IO-Warrior device identified by iowHandle.
     * <p>(wraps <code>ULONG IOWKIT_API IowKitGetProductId(IOWKIT_HANDLE iowHandle)</code>)</p>
     * <p>The Product ID is a 16-bit Word identifying the specific kind of IO-Warrior. For
     * easier compatibility with VB6 the function returns a 32-bit DWORD with the upper word
     * set to 0.</p>
     * <p>IOWKIT_PRODUCT_ID_IOW40 (0x1500, $1500, &amp;H1500) is returned for an IO-Warrior 40
     * whereas IOWKIT_PRODUCT_ID_IOW24 (0x1501, $1501, &amp;H1501) is returned for an IO-Warrior
     * 24. 0 is returned for an invalid iowHandle. The value is cached in the dynamic library
     * because access to the device needs some msecs.</p>
     * @param devHandle Handle of the disired IO-Warrior.
     * @return Product ID of the IO-Warrior device as long. 0x1500 for an IO-Warrior 40,
     * 0x1501 for an IO-Warrior 24 and 0 for an invalid iowHandle.
     */
    static public native long getProductId(long devHandle);	

    /**
     * Return the revision of the firmware of the IO-Warrior device identified by iowHandle.
     * <p>(wraps <code>ULONG IOWKIT_API IowKitGetRevision(IOWKIT_HANDLE iowHandle)</code>)</p>
     * <p>The revision is a 16-bit Word telling the revision of the firmware. For easier 
     * compatibility with VB6 the function returns a 32-bit DWORD with the upper word set
     * to 0. The revision consists of 4 hex digits. $1021 designates the current revision
     * <code>1.0.2.1</code>. 0 is returned for an invalid iowHandle. Legacy IO-Warriors
     * (without serial number) have a revision &lt; <code>1.0.1.0</code> (0x1010,  $1010,
     * &amp;H1010).</p>
     * <p>The value is cached in the dynamic library because access to the device needs some
     * msecs.</p>
     * @param devHandle Handle of the disired IO-Warrior.
     * @return Revision of the firmware as long or 0 for an invalid iowHandle.
     * @since dynamic library version 1.4.
     */
    static public native long getRevision(long devHandle);
		
    /**
     * Returns a String containing the serial number string from the device.
     * <p>(wraps <code>BOOLEAN IOWKIT_API IowKitGetSerialNumber(IOWKIT_HANDLE iowHandle, PWCHAR serialNumber)</code>)</p>
     * <p>The content is always 8 ASCII characters long. Each character is an uppercase hex
     * digit. "00000AC3" for example. For legacy IO-Warriors without serial number (revision
     * &lt; 1.0.1.0) "00000000" is returned.</p>
     * @param devHandle Handle of the disired IO-Warrior.
     * @return Serial number string.
     */
    static public native String getSerialNumber(long devHandle);
	
    /**
     * Set read I/O timeout in milliseconds.
     * <p>(wraps <code>BOOLEAN IOWKIT_API IowKitSetTimeout(IOWKIT_HANDLE devHandle, ULONG timeout)</code>)</p>
     * <p>It is possible to lose reports with HID devices. Since reading a HID device is
     * a blocking call it is possible to block your application in that case. setTimeout()
     * makes read() fail if it does not read a report in the allotted time. If read() times
     * out, you have to restart any pending transaction (for example, IIC write or read
     * transaction) from the beginning.</p>
     * <p>It is recommended to use 1 second (1000) or bigger timeout values.</p>
     * @param devHandle Handle of the disired IO-Warrior.
     * @param timeout Desired timeout value. Values bigger than 1000 are recommended.
     * @return True if the operation succeedes.
     */
    static public native boolean setTimeout(long devHandle, long timeout);
	
    /**
     * Set write I/O timeout in milliseconds.
     * <p>(wraps <code>BOOLEAN IOWKIT_API IowKitSetWriteTimeout(IOWKIT_HANDLE devHandle, ULONG timeout)</code>)</p>
     * <p>setWriteTimeout() makes write() fail if it does not write a report in the
     * allotted time. If write() times out, you have to restart any pending transaction
     * (for example, IIC write transaction) from the beginning.</p>
     * <p>Failure of write() is uncommon. Check your hardware if you encounter write errors.</p>
     * <p>libiowkit.so does not implement setWriteTimeout() yet.</p>
     * <p>It is recommended to use 1 second (1000) or bigger timeout values.</p>
     * @param devHandle Handle of the disired IO-Warrior.
     * @param timeout Desired timeout value. Values bigger than 1000 are recommended.
     * @return True if the operation succeedes.
     */
    static public native boolean setWriteTimeout(long devHandle, long timeout);
		
    /**
     * Cancel a read or write operation under way on one of the pipes.
     * <p>(wraps <code>BOOLEAN IOWKIT_API IowKitCancelIo(IOWKIT_HANDLE devHandle, ULONG numPipe)</code>)</p>
     * <p>This function is seldom used, because you need several threads in your program to be
     * able to call it at all. read() blocks the thread so you need another thread for
     * cancelling. Setting the timeouts is an easier way for handling read or write problems.</p>
     * <p>The function cancels pending read and write operations simultaneously.</p>
     * @param devHandle Handle of the disired IO-Warrior. 
     * @param numPipe Handle of the disired pipe. 
     * @return True if the operation succeedes.
     */
    static public native boolean cancelIo(long devHandle, long numPipe);
	
    /**
     * Return a String identifying the dynamic library version. This function has been added
     * with 1.3 version of the dynamic library. Currently it returns "IO-Warrior Kit V1.4".
     * <p>(wraps <code>PCHAR IOWKIT_API IowKitVersion(VOID)</code>)</p>
     * <p>This method works even before openDevice() is called.</p>
     * @return The librarys's version string.
     */
    static public native String version();	
	
    /**
     * Returns the internal Windows thread handle used to read the I/O pins of the IO-Warrior.
     * <p>(wraps <code>HANDLE IOWKIT_API IowKitGetThreadHandle(IOWKIT_HANDLE iowHandle)</code>)</p>
     * <p>The function is only for programmers with expert knowledge about threads. It is
     * provided for manipulations like raising or lowering thread priority.</p>
     * <p>Since Linux does not need a thread for implementing the IO-Warrior functions,
     * getThreadHandle() always returns 0 then.</p>
     * @param devHandle Handle of the disired IO-Warrior. 
     * @return  The internal Windows thread handle used to read the I/O pins of this IO-Warrior.
     */
    static public native long getThreadHandle(long devHandle);
}
