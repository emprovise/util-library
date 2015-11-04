package com.emprovise.util.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * File Utility to read and write to the file in various formats.
 * @author PP63680
 *
 */
public class FileUtil {

	/**
	 * Environment independent line separator value. 
	 */
	public static String newLine = System.getProperty("line.separator");
	
	/**
	 *  
	 * @param fileName
	 * 		The full name of the file to be read including the path if file is located outside the class path.
	 * @return {@link String}
	 * 		Text read from the text file using the default system character-set.
	 * @throws IOException
	 */
	public static String readFile(String fileName) throws IOException {

		StringBuilder contents = new StringBuilder();
		BufferedReader buffReader = null;

		try {

			File inputFile = new File(fileName);

			if (inputFile.exists()) {

				String line = null;
				buffReader = new BufferedReader(new FileReader(inputFile));

				while ((line = buffReader.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			}
		} finally {
			if (buffReader != null) {
				buffReader.close();
			}
		}

		return contents.toString();
	}

	/**
	 * Reads the specified value of the parameter or the property from the text file.
	 * @param txtFile
	 * 		The full name of the file to be read including the path if file is located outside the class path.
	 * @param parameter
	 * 		The name of the parameter to be read from the file.
	 * @return {@link String} 
	 * 		value of the parameter read from the file.  
	 * @throws IOException
	 */
	public static String readParameterFromTextFile(File txtFile, String parameter) throws IOException {
		
		BufferedReader br = null;
		String value = null;
		 
		try {
 
			if(txtFile.exists()) {
				
				String line;
				br = new BufferedReader(new FileReader(txtFile));
	 
				while ((line = br.readLine()) != null) {
					
					if(line.contains(parameter)) {
						value = line.substring(line.indexOf(parameter) + parameter.length());
						value = value.replaceAll("[=]", "").trim();
						break;
					}
				}
			}
		} finally {
			if (br != null)br.close();
		}
		
		return value;
	}
	
	/**
	 * Writes name-value pairs or properties from the {@link Map} to the specified file.
	 * @param txtFile
	 * 		The full name of the file  to write to, including the path if file is located outside the class path.
	 * @param params
	 * 		The name-value pairs to be written to the file similar to a property file.
	 * @throws IOException 
	 * @throws Exception
	 */
	public static void writeParametersToTextFile(File txtFile, Map<String, String> params) throws IOException {
		
		BufferedWriter writer = null;

		try {
			
			createParentDirectories(txtFile);
			
			if(!txtFile.exists()) {
				txtFile.createNewFile();
			}
			
			writer = new BufferedWriter(new FileWriter(txtFile));
			
			for (Map.Entry<String, String> entry : params.entrySet()) {
				writer.write(entry.getKey() + "=" + entry.getValue());
				writer.newLine();
			}
			
		} finally {
			
			if(writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Creates all the parent directories for the specified files.
	 * @param file
	 * 		{@link File} who's parent directory needs to be created.
	 */
	private static void createParentDirectories(File file) {
		File parentDirectory = file.getParentFile();

		if (parentDirectory != null) {
			parentDirectory.mkdirs();
		}
	}

	/**
	 * Writes the specified text to the text file.
	 * @param filename
	 * 		The full name of the file to write to, including the path if file is located outside the class path.
	 * @param text
	 * 		The text to be written to the file.
	 * @param doAppend
	 * 		If true then the text is appended to the end of the file else the text is overwritten.
	 * @throws IOException
	 */
	public static void writeTextToFile(String filename, String text, boolean doAppend) throws IOException {
		
		File file = new File(filename);
		createParentDirectories(file);
		
		Writer output = null;
		output = new BufferedWriter(new FileWriter(file, doAppend));
		output.write(text);
		output.flush();
		output.close();
	}

	/**
	 * Writes to the specified text file the elements of the passed {@link ArrayList} line by line. 
	 * @param filename
	 * 		The full name of the file to write to, including the path if file is located outside the class path.
	 * @param textList
	 * 		An {@link ArrayList} of lines to write to the file.
	 * @param doAppend
	 * 		If true then the text is appended to the end of the file else the text is overwritten. 
	 * @throws IOException
	 */
	public static void writeTextToFile(String filename, List<String> textList, boolean doAppend) throws IOException {

		Writer output = null;
		File file = new File(filename);
		createParentDirectories(file);
		
		output = new BufferedWriter(new FileWriter(file, doAppend));

		for (String line : textList) {
			output.write(line);
			output.write(newLine);
		}
		
		output.flush();
		output.close();
	}

	/**
	 * Reads the specified text file line by line and returns the lines read as an {@link ArrayList}
	 * @param filename
	 * 		The full name of the file to be read including the path if file is located outside the class path.
	 * @return
	 * 		An {@link ArrayList} of lines read from the file.
	 * @throws IOException
	 */
	public static List<String> readLinesFromTextFile(String filename) throws IOException {
	
	  BufferedReader bufferedReader = null;
	  List<String> records = new ArrayList<String>();
	  String line = null;
		
	  try {
		  // wrap a BufferedReader around FileReader
		  bufferedReader = new BufferedReader(new FileReader(filename));

		  // use the readLine method of the BufferedReader to read one line at a time.
		  // the readLine method returns null when there is nothing else to read.
		  while ((line = bufferedReader.readLine()) != null) {
			  
			  if(line != null) {
				  line = line.trim();
				  
				  if(!line.isEmpty()) {
					  records.add(line);
				  }
			  }
		  }
	  }
	  finally {
		  if(bufferedReader != null) {
			  bufferedReader.close();
		  }
	  }
	  
	  return records;
	}

	/**
	 * Search for the specified filename in the directory. If the filename is not found in the current
	 * directory, search in upper hierarchy of the directory structure depending on the parentLevel specified.
	 *   
	 * @param directory
	 * 		{@link File} to search for the specified filename.
	 * @param fileName
	 * 		Name of the file to be searched in the directory. 
	 * @param parentLevel
	 * 		Parent directory level until which search should be performed.
	 * @return
	 * 		Directory in which the specified filename is found.
	 */
	public static File findFileInDirectory(File directory, String fileName, int parentLevel) {
		
		File dirContainingFile = null;
		
		if(!directory.exists()) {
			return null;
		}
		
		if(!directory.isDirectory()) {
			directory = directory.getParentFile();
		}

		for (File file : directory.listFiles()) {
			
			if(file.getName().equals(fileName)) {
				dirContainingFile = directory;
				break;
			}
		}
		
		if(dirContainingFile == null && parentLevel > 0) {
			dirContainingFile = findFileInDirectory(directory.getParentFile(), fileName, parentLevel-1);
		}
		
		return dirContainingFile;
	}

	/**
	 * Uses FileChannel with transferTo() method to copy the file from the specified source to destination
	 * directory. 
	 * 
	 * @param source
	 * 		{@link File} specifying source file along with full path. 
	 * @param destination
	 * 		{@link File} specifying destination file along with full path.
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination) throws IOException {

		FileChannel in = new FileInputStream(source).getChannel();
        FileChannel out = new FileOutputStream(destination).getChannel();
        in.transferTo (0, in.size(), out);
	}
	
	/**
	 * Deletes all the files present in the specified directory without deleting any
	 * sub-directories or files in the sub-directories and ignoring the specified file
	 * name.
	 * 
	 * @param directory
	 * 		{@link File} specifying the directory containing the files to be deleted.
	 * @param ignoreFilename
	 *  		{@link String} specifying the file name to be ignored while deletion.
	 */
	public static void deleteFilesInDirectory(File directory, final String ignoreFilename) {
		
		if(directory.exists() && directory.isDirectory()) {
			
			FileFilter filter = new FileFilter() {
	            @Override
	            public boolean accept(File pathname) {
	            	
	            	if(pathname.isFile() && !pathname.getName().equals(ignoreFilename)) {
	            		return true;
	            	}
	            	
	               return false;
	            }
	         };
	         
			File[] listFiles = directory.listFiles(filter);
			
			for (File file : listFiles) {
				file.delete();
			}
		}
	}
}
