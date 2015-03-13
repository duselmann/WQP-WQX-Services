package gov.usgs.cida.wqp.util;


import gov.usgs.cida.wqp.exception.WQPGatewayException;
import gov.usgs.cida.wqp.exception.WQPGatewayExceptionID;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

/**
 * SystemUtils
 * @author prusso
 *<br /><br />
 *	This class exposes many utility methods used dealing with system methods
 *	such as I/O.  The majority of the methods here are statically provided so
 *	they can be exposed and utilized outside of the package this utility
 *	resides in.
 */
public class SystemUtils {
	private final Logger log = Logger.getLogger(getClass());
	
	public static boolean PROFILE = false;
	
	static {
		// TODO
	}
	
	public SystemUtils() {
        log.trace(getClass());
	}
	
	
	public boolean createZipFromFilematch(String path, final String filename) {
		String zipFileName = path + File.separator + filename + ".zip";
		
		File directory = new File(path);
		
		if(!directory.exists()) {
			String msg = "SystemUtils.createZipFromFilematch() Error: Directory " + path + " does not exist";
			log.error(msg);
			return false;
		}
		
		File existingFile = new File(zipFileName);
		if(existingFile.exists()) {
			String msg = "SystemUtils.createZipFromFilematch() Warning: Zip file " + zipFileName + " exists.  Deleting prior to new shapefile creation.";
			log.info(msg);
			existingFile.delete();
		}
		
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(name.indexOf(filename) != -1) {
					return true;
				}
				return false;
			}
		};			
		
		String[] directoryFiles = directory.list(filter);

		try {
			final int BUFFER = 2048;
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(zipFileName);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

			byte data[] = new byte[BUFFER];

			for (String shpFilename : directoryFiles) {
				String file = path + File.separator + shpFilename;
				FileInputStream fi = new FileInputStream(file);
				origin = new BufferedInputStream(fi, BUFFER);

				ZipEntry entry = new ZipEntry(shpFilename);
				out.putNextEntry(entry);
				int count;

				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}

				origin.close();
				
				File deleteFile = new File(file);
	        	deleteFile.delete();
			}
			out.close();
		} catch (IOException e) {
			log.error(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public String uncompressGzipAsString(byte[] content) throws WQPGatewayException {
    	ByteArrayInputStream bytein = new ByteArrayInputStream(content);
    	GZIPInputStream gzin;
		try {
			gzin = new java.util.zip.GZIPInputStream(bytein);
		} catch (IOException e) {
			String msg = "SystemUtils.uncompressGzipAsString() Exception : Error creating GZIPInputStream from content [" +
        			 e.getMessage() + "]";
	   			log.error(msg);
	   			
	   			WQPGatewayExceptionID id = WQPGatewayExceptionID.GZIP_ERROR;					
	   			throw new WQPGatewayException(id, "SystemUtils", "uncompressGzipAsString()", msg);
		}
    	ByteArrayOutputStream byteout = new java.io.ByteArrayOutputStream();
    	
    	int res = 0;
    	byte buf[] = new byte[1024];
    	while (res >= 0) {
    	    try {
				res = gzin.read(buf, 0, buf.length);
			} catch (IOException e) {
				String msg = "SystemUtils.uncompressGzipAsString() Exception : Error reading gzip content [" +
            			 e.getMessage() + "]";
		   			log.error(msg);
		   			
		   			WQPGatewayExceptionID id = WQPGatewayExceptionID.GZIP_ERROR;					
		   			throw new WQPGatewayException(id, "SystemUtils", "uncompressGzipAsString()", msg);
			}
    	    if (res > 0) {
    	        byteout.write(buf, 0, res);
    	    }
    	}
    	
    	String stringContent;
		try {
			stringContent = byteout.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			String msg = "SystemUtils.uncompressGzipAsString() Exception : Error reading gzip content [" +
        			 e.getMessage() + "]";
   			log.error(msg);
   			
   			WQPGatewayExceptionID id = WQPGatewayExceptionID.GZIP_NOT_UTF8;					
   			throw new WQPGatewayException(id, "SystemUtils", "uncompressGzipAsString()", msg);
		}
    	
    	return stringContent;
    }
    
    public byte[] compressStringToGzip(String content) throws WQPGatewayException {
    	if((content == null) || (content.length() == 0)) {
    		content = "";
    	}
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
		try {
			gzip = new GZIPOutputStream(out);
		} catch (IOException e) {
			String msg = "SystemUtils.uncompressGzipAsString() Exception : Error creating GZIPOutputStream [" +
       			 e.getMessage() + "]";
  			log.error(msg);
  			
  			WQPGatewayExceptionID id = WQPGatewayExceptionID.UTIL_GZIP_COMPRESSION_ERROR;					
  			throw new WQPGatewayException(id, "ProxyUtil", "compressStringToGzip()", msg);
		}
        try {
			gzip.write(content.getBytes());
		} catch (IOException e) {
			String msg = "SystemUtils.uncompressGzipAsString() Exception : Error writing content to GZIPOutputStream [" +
	       			 e.getMessage() + "]";
	  			log.error(msg);
	  			
	  			WQPGatewayExceptionID id = WQPGatewayExceptionID.UTIL_GZIP_COMPRESSION_ERROR;					
	  			throw new WQPGatewayException(id, "SystemUtils", "compressStringToGzip()", msg);
		}
        try {
			gzip.close();
		} catch (IOException e) {
			String msg = "SystemUtils.uncompressGzipAsString() Warning : Error closing GZIPOutputStream [" +
	       			 e.getMessage() + "]";
  			log.error(msg);
		}
        
        return out.toByteArray();
    }
}