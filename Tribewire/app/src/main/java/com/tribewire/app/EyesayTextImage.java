package com.tribewire.app;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.ImageDownloader1;
import com.eyesayapp.Utils.StorageImageDownloader;

public class EyesayTextImage extends Activity {

	private TextView eyesaytextview;
	private ImageView eyesaytxtImage;

	private String image_url = "";
    private boolean drafts = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.eyesaytextimage);

		eyesaytextview = (TextView) findViewById(R.id.eyesaytextmsg);
		eyesaytxtImage = (ImageView) findViewById(R.id.eyesaytextimage);
		super.onCreate(savedInstanceState);
		try {
			image_url = getIntent().getStringExtra("imageurl");
			image_url = image_url.replace("_thumb", "");
			Log.e("", "The Image Url here is " + image_url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			drafts = getIntent().getBooleanExtra("draft",false);
			Log.e("", "The Image Url here is " + image_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {

		super.onResume();

		if (drafts) {
			try {
				StorageImageDownloader.getInstance().download(image_url, eyesaytxtImage, this);
			} catch (Exception e) {
				e.printStackTrace();
				
				Toast.makeText(this, "Error while previewing image", Toast.LENGTH_LONG).show();
			}
		}
		else {
			if (CommonFunctions.isInternetConnected(this)) {
				downloadTextImage();
			} else {
				CommonFunctions
						.showAlert(
								this,
								"Unable to download , please check your internet connectivity",
								"Network Error");
			}
		}
	}

	// Used to download Image from Amazon
	private void downloadTextImage() {
		try {

			if (image_url != null && image_url.length() > 0
					&& image_url.startsWith("http")) {
				
				ImageDownloader1.getInstance().download(image_url,
						eyesaytxtImage,this);
				//handler.sendEmptyMessage(2);
			} else {
				
				try {
					eyesaytxtImage.setImageURI(Uri.parse(image_url));
				} catch (Exception e) {
					e.printStackTrace();
				}

//				Toast.makeText(this, "Invalid dow]nload address  ",
//						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			CommonFunctions
					.showAlert(this,
							"Server Error, Please try after some time",
							"Network Error");
		}

	}
	
	Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			if (msg.what==1) {
				
			}
			else if (msg.what==2) {
				Bitmap bitmap = downloadBitmap(image_url);
				eyesaytxtImage.setImageBitmap(bitmap);
			}
			
		};
	};
	
	Bitmap downloadBitmap(String url) {

		
			final int IO_BUFFER_SIZE = 4 * 1024;

			/* AndroidHttpClient is not allowed to be used from the main thread */
			/*final HttpClient client = (mode == Mode.NO_ASYNC_TASK) ? new DefaultHttpClient()
					: AndroidHttpClient.newInstance("Android"); */
			
			final HttpClient client = new DefaultHttpClient();
			
			
			final HttpGet getRequest = new HttpGet(url);
			try {
				HttpResponse response = client.execute(getRequest);
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					Log.e("ImageDownloader", "Error " + statusCode
							+ " while retrieving bitmap from " + url);
					return null;
				}
				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						inputStream = entity.getContent();
						/*
						 * return BitmapFactory.decodeStream(inputStream); // Bug on
						 * slow connections, fixed in future release.
						 */

						return BitmapFactory.decodeStream(inputStream);
					} catch(Exception e){
						e.printStackTrace();
							
					}
					
					finally {
						if (inputStream != null) {
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			} catch (IOException e) {
				getRequest.abort();
				Log.e("", "I/O error while retrieving bitmap from " + url);
			} catch (IllegalStateException e) {
				getRequest.abort();
				Log.e("", "Incorrect URL: " + url);
			} catch (Exception e) {
				getRequest.abort();
				e.printStackTrace();
			  Log.e("", "Error while retrieving bitmap from " + url);
			} finally {
				
				/*if ((client instanceof HttpClient)) {
					((HttpClient) client).close();
				} */
			}
			return null;
		

	}
	
	@Override
	protected void onDestroy() {
		try {
			ImageDownloader1.getInstance().clearCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

}