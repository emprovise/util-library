package com.emprovise.util.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	final static int BUFFER = 2048;

    private static void addToArchive(File srcFile, String destinationPath) throws IOException {
		FileInputStream fileStream = new FileInputStream(srcFile);
    	BufferedInputStream bufferedStream = new BufferedInputStream(fileStream, BUFFER);

		FileOutputStream destStream = new FileOutputStream(new File(destinationPath + ".zip"));
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(destStream));

    	byte data[] = new byte[BUFFER];
        ZipEntry entry = new ZipEntry(srcFile.getName());
        out.putNextEntry(entry);        
        int count;
        while ((count = bufferedStream.read(data, 0, BUFFER)) != -1) {
            out.write(data, 0, count);
            out.flush();
        }

		out.close();
    }
    
    public static byte[] archiveToBytes(byte[][] bytes, String[] filenames) throws IOException {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ZipOutputStream zos = new ZipOutputStream(baos);
    	String filename = null;

    	for (int i = 0; i < bytes.length; i++) {
    		if(bytes[i] != null) {
        		if(i < filenames.length && filenames[i] != null) {
        			filename = filenames[i];
        		}
            	ZipEntry entry = new ZipEntry(filename);
            	entry.setSize(bytes[i].length);
            	zos.putNextEntry(entry);
            	zos.write(bytes[i]);
    		}
		}
    	
    	zos.closeEntry();
    	zos.close();
    	return baos.toByteArray();
    }
 }  
 