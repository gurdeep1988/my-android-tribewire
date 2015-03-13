package com.eyesayapp.Utils;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.provider.SyncStateContract.Constants;
import android.util.Log;

public class HttpFileUploader {

	private URL connectURL;
	
	private String responseString;
	
	private String fileName;

	private FileInputStream fileInputStream = null;
	
	private final String lineEnd = "\r\n";

	private final String twoHyphens = "--";

	private final String boundary = "*****";

	private String parameters;
	
	private final String TAG = this.getClass().getSimpleName();

	public HttpFileUploader(String urlString, String fileName, String parameters) throws MalformedURLException{

			connectURL = new URL(urlString);

			this.fileName = fileName;
			
			this.parameters = parameters;

	}


	public boolean doStart(FileInputStream stream){ 
		
		fileInputStream = stream;

		return upload();
	} 

	
	private boolean upload(){

		boolean success = false;
		
		try
		{
			//------------------ CLIENT REQUEST
			Log.i(TAG, "About to upload database...");
			
			// Open a HTTP connection to the URL
			HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
			
			// Allow Inputs
			conn.setDoInput(true);

			// Allow Outputs
			conn.setDoOutput(true);

			// Don't use a cached copy.
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");

			conn.setRequestProperty("Connection", "Keep-Alive");

			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			
			  // stuff the Authorization request header
	//	    byte[] encodedPassword = ( "************" + ":" + "***********" ).getBytes();
		    
//		    String encoded = new String(Base64.encodeBase64(encodedPassword));

		//    conn.setRequestProperty( "Authorization", "Basic " + encoded);

			DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );

			dos.writeBytes(twoHyphens + boundary + lineEnd);

			dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\"" + this.fileName+"\"" + lineEnd);
			
			//TODO:
			//dos.writeBytes(this.parameters);
			
			dos.writeBytes(lineEnd);	
			
			Log.i(TAG ,"Headers have been written");

			// create a buffer of maximum size
			int bytesAvailable = fileInputStream.available();

			int maxBufferSize = 1024;
			
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			
			byte[] buffer = new byte[bufferSize];

			// read file and write it into form...
			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0)
			{
				dos.write(buffer, 0, bufferSize);

				bytesAvailable = fileInputStream.available();
				
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);

			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			
			dos.writeBytes(twoHyphens + boundary + lineEnd);

			dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\"" + this.fileName+"\"" + lineEnd);
			
			//TODO:
			//dos.writeBytes(this.parameters);
			
			dos.writeBytes(lineEnd);	
			// close streams
			
			Log.i(TAG ,"Database file with name " + this.fileName + " has been successfully written");
			
			fileInputStream.close();
			
			dos.flush();

			InputStream is = conn.getInputStream();
			
			// retrieve the response from server
			
			int ch;

			StringBuffer b = new StringBuffer();
			
			while( ( ch = is.read() ) != -1 ){
				b.append( (char)ch );
			}
			
			this.responseString = b.toString();
		
			if (responseString.equals("success")) {
				
				success = true;
				
			}else if (responseString.equals("error")) {
				success = false;
			}
			
			Log.i(TAG, "Response String from server:"+responseString);
			
			dos.close();

		}
		catch (MalformedURLException ex)
		{
			Log.e(TAG , "error: " + ex.getMessage());
			
		}

		catch (IOException ioe)
		{
			Log.e(TAG , "error: " + ioe.getMessage());
			
		}
		
		return success;
	}

	
	public String getResponse() {
		
		return this.responseString;
	
	}


}