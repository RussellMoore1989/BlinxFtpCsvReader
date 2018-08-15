package com.blinx.ftp.FtpCsvConvertor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.opencsv.CSVReader;

/**
 * App that connects to localhost ftp server, downloads the country.csv.
 * Then uses csv reader to print some infomation in the file
 */
public class App 
{
    //@SuppressWarnings("resource")
	public static void main( String[] args ) throws FileNotFoundException
    {
    	
    	FTPClient ftp = new FTPClient();
    	String username = "user";
    	String password = "qwerty";
        boolean error = false;
        
        try {
        	int reply;
        	String server = "localhost";
        	ftp.connect(server);
        	ftp.login(username, password);
        	System.out.println("Connected to " + server + ".");
        	System.out.print(ftp.getReplyString());

        	// After connection attempt, you should check the reply code to verify
        	// success.
        	reply = ftp.getReplyCode();

        	if(!FTPReply.isPositiveCompletion(reply)) {
        		ftp.disconnect();
        		System.err.println("FTP server refused connection.");
        		System.exit(1);
        	}
        } catch(IOException e) {
        	error = true;
        	e.printStackTrace();
        } finally {
        	
        	if(ftp.isConnected()) {
        		try {
        			ftp.enterLocalPassiveMode();
            		System.out.println("Entering passive mode");
        			ftp.setFileType(FTP.BINARY_FILE_TYPE);
        			String csvFile = "/country.csv";
        			File downloadFile = new File("C:/Users/Rap/Downloads/country.csv");
        			
        			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                    InputStream inputStream = ftp.retrieveFileStream(csvFile);
                    byte[] bytesArray = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                        outputStream.write(bytesArray, 0, bytesRead);
                    }
         
                    boolean success = ftp.completePendingCommand();
                    if (success) {
                        System.out.println("File has been downloaded successfully.");
                    }
                    outputStream.close();
                    inputStream.close();
        			
                    try {
	                	CSVReader reader = new CSVReader(new FileReader(downloadFile));
	                    List<String[]> line = reader.readAll();
	                    
	                    for(int i = 0; i < line.size(); i++ ) {
	                        System.out.println("Country [code= " + line.get(i)[4] + " , name=" + line.get(i)[5] + "]");
	                    }
	                    reader.close();
	                    
	                } catch (IOException e) {
	                    e.printStackTrace();
					}
                    
        		} catch(IOException ioe) {
        			System.out.println(ioe);
        		}

        		
        		try {
        			ftp.logout();
                    ftp.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
	            
	          }
        	System.exit(error ? 1 : 0);
        }  	
       
    	//possibly use this command to keep alive for a certain period.
        //ftp.setControlKeepAliveTimeout(600);

    }
}
