package com.emprovise.util.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;

/**
 * @author pp63680
 * FileLock Utility to block the access to a File for Multiple Processes across multiple JVM's
 * 
 */
public class FileLockUtil {

	/**
	 * The File which is blocked from access by other processes.
	 */
	private File file = null;
	
	/**
	 * The FileLock which is acquired if no other process is holding the FileLock. 
	 */
	private FileLock fileLock = null;
	
	/**
	 * The actual RandomAccess FileStream which is used to lock the file.
	 */
	private RandomAccessFile randomAccessFile = null;
	
	/**
	 * The file channel associated with the specified lock file input stream.
	 */
	private FileChannel channel = null;
	
	/**
	 * Creates a new instance of FileLockUtil class by trying to access the file lock 
	 * for the specified filePath or creates the file if it does not exists in the specified filePath.
	 * It also adds the {@link ShutdownHook} to the current JVM process.
	 * @param filePath
	 * 		Full file path for the {@link File} to Lock
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public FileLockUtil(String filePath) throws IOException, FileNotFoundException {
		super();
		
    	Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    	
       	file = new File(filePath);
       	
       	if(!file.exists()) {
       		
       		File parentDirectory = file.getParentFile();

			if (parentDirectory != null) {
				parentDirectory.mkdirs();
			}
			
       		file.createNewFile();
       	}
       	
       	randomAccessFile = new RandomAccessFile(file, "rw");
       	channel = randomAccessFile.getChannel();
       	fileLock = channel.tryLock();
	}

	/**
	 * Checks if this process holds the the lock to the specified file.
	 * @return 
	 * 		true if any thread holds this lock and false otherwise.
	 */
	public boolean hasLock() {
		return (fileLock != null) ? true : false;
	}
	
	/**
	 * Releases the lock acquired by the current process and delete the file.
	 */
	public void releaseLock() throws IOException {

   		try {
   			if (fileLock != null && fileLock.isValid()) {
   				fileLock.release();
   			}
   		    if (randomAccessFile != null) {
   		    	randomAccessFile.close();
   		    }
		} catch (IOException ex) { }

   		channel.close();
   		randomAccessFile.close();
   		file.delete();
	}
	
	/**
	 * Writes the specified name/value parameters to the lock file.
	 * @param params 
	 * 		A {@link Map} Object with the name/value pairs of the properties to write to the lock file.
	 * @throws Exception
	 */
	public void writeLockFile(Map<String, String> params) throws Exception { 
		writeChannelFile(file, channel, params);
	}
	
	/**
	 * Reads the specified parameter name from the lock file to return its corresponding value.
	 * @param parameter
	 * 		The name of the parameter to read from the lock file.
	 * @return
	 * 		Value of the specified parameter name from the lock file
	 * @throws Exception
	 */
	public String readLockFile(String parameter) throws Exception {
		return readChannelFile(file, channel, parameter);
	}
	
	/**
	 * Writes the specified name/value parameters to the specified lock {@link File} using the specified {@link FileChannel}.
	 * @param lockFile
	 * 		The lock {@link File} to write on.
	 * @param channel
	 * 		The file channel associated with the specified lock file input stream
	 * @param params
	 * 		A {@link Map} Object with the name/value pairs of the properties to write to the lock file.
	 * @throws IOException
	 */
	public static void writeChannelFile(File lockFile, FileChannel channel, Map<String, String> params) throws IOException {
		
	    Charset charset = Charset.forName("UTF-8");
		CharsetEncoder encoder = charset.newEncoder();

		File parentDirectory = lockFile.getParentFile();

		if (parentDirectory != null) {
			parentDirectory.mkdirs();
		}
			
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String line = entry.getKey() + "=" + entry.getValue() + "\n";
	        channel.write(encoder.encode(CharBuffer.wrap(line))); // Write the buffer contents to the channel
	        channel.force(false); // Force them out to the disk
		}
	}
	
	/**
	 * Reads the specified parameter name from the lock {@link File} using the specified {@link FileChannel} to return its corresponding value.
	 * @param lockFile
	 * 		The lock {@link File} to read from.
	 * @param channel
	 * 		The file channel associated with the specified lock file input stream
	 * @param parameter
	 * 		The name of the parameter to read from the lock file.
	 * @return
	 * 		Value of the specified parameter name from the lock file
	 * @throws IOException
	 * @throws CharacterCodingException
	 */
	public static String readChannelFile(File lockFile, FileChannel channel, String parameter) throws IOException, CharacterCodingException {
		
		channel = channel.position(0);
		ByteBuffer buffer = ByteBuffer.allocate(512);
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();
		String value = null;
		 
		if(lockFile.exists()) {
				
			String text;
			int bytesread = channel.read(buffer);
	        if (bytesread > -1) {
		       buffer.flip();
		       text = decoder.decode(buffer).toString();
		       buffer.clear();
		       
		       String lines[] = text.split("\\r?\\n");
		       
		       for (String line : lines ) {
					if(line.contains(parameter)) {
						value = line.substring(line.indexOf(parameter) + parameter.length());
						value = value.replaceAll("[:=]", "").trim();
						break;
					}
		    	}
	        }
		}
		
		return value;
	}
	
	/**
	 * The Shutdown Hook in order to release the File lock and delete the lock File once the current JVM process terminates  
	 * @author pp63680
	 *
	 */
	private class ShutdownHook extends Thread {
	    public void run() {
	    	try {
				releaseLock();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
}
