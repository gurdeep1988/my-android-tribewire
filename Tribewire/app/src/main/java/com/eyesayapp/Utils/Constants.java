package com.eyesayapp.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

public class Constants {

	public static final String API_KEY = "65e2y3e5s4a2y54";
	public static final String COM_ID = "com_id";
	public static final String LAST_NAME = "lname";
	public static final String FIRST_NAME= "fname";
	public static final String SESSION = "session";
	
	
	
	
	public static String API_KEY_TAG   = "api_key";
	public static String REF_ID_TAG    = "user_id";
	public static String COMP_ID_TAG   = "comp_id";
	public static String COMP_ID_TAG_NEW   = "company_id";
	public static String PROFILE_PIC   = "profile_pic";
	public static String CONV_ID_TAG   = "conv_id";
	public static String SESSION_TAG   = "session";
	public static String LIMIT  = "limit";
	
	public static String FYI_DATE   = "fyi_date";
	public static String FYI_SUBJECT   = "fyi_subject";
	public static String FYI_TEXT   = "text";
	public static String FYI_LINK   = "href";
	
	// Group Tags
	public static String GROUP_NAME   = "group_name";
	public static String GROUP_MEMBERS   = "group_members";
	
	
	
	public static final String VIDEO_BASE_URL   = CommonFunctions.DEMO_SERVER_URL+"uploads/";	
	public static final String AUDIO_BASE_URL   = CommonFunctions.DEMO_SERVER_URL+"uploads/audios/";
	public static final String IMAGE_BASE_URL   = CommonFunctions.DEMO_SERVER_URL+"uploads/photos/";
	
	
	
	
	
	private static List<String> nameList;
	public static List<String> getNameList() {
		if (nameList == null) {
			
			nameList   = new ArrayList<String>();
			nameList.add(Constants.API_KEY_TAG);
			nameList.add(Constants.REF_ID_TAG);
			nameList.add(Constants.COMP_ID_TAG);
			nameList.add(Constants.SESSION_TAG);	
		} 
		
		return nameList;
		
		

	}
	private static List<String> valueList;
	public static List<String> getValueList(Context context) {
		// TODO Auto-generated method stub

		String api_key = Constants.API_KEY;
		String ref_id = CommonFunctions
				.getPref(context, CommonFunctions.REF_ID, "");
		String com_id = CommonFunctions
				.getPref(context, Constants.COM_ID, "");
		
		
		String session = CommonFunctions
				.getPref(context, Constants.SESSION, "");
		
		if (valueList == null) {
			valueList   = new ArrayList<String>();
			valueList.add(api_key);
			valueList.add(ref_id);
			valueList.add(com_id);
			valueList.add(session);
		}
		return valueList;
		
	}
	
	public static void setValueListNull() {
		if (valueList!=null) {
			valueList  = null;
		}

	}

	public static Bitmap getBitmap(Uri uri,Context context) {

		// Uri uri = getImageUri(path);
		InputStream in = null;
		try {
			final int IMAGE_MAX_SIZE = 700000; // 1.2MP
			in = context.getContentResolver().openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
			Log.d("Constants", "scale = " + scale + ", orig-width: " + o.outWidth
					+ ", orig-height: " + o.outHeight);

			Bitmap b = null;
			in =context.getContentResolver().openInputStream(uri);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				Log.d("Constants", "1th scale operation dimenions - width: " + width
						+ ",height: " + height);

				double y = Math.sqrt(IMAGE_MAX_SIZE
						/ (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
						(int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();
			} else {
				b = BitmapFactory.decodeStream(in);
			}
			// FileOutputStream fileOutputStream = new
			// FileOutputStream(getTempFile(this));
			// while (in.available()!=-1) {
			// //in.read();
			//
			// try {
			// fileOutputStream.write(in.read());
			// fileOutputStream.close();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			in.close();

			// Log.d(TAG, "bitmap size - width: " +b.getWidth() + ", height: " +
			// b.getHeight());

			Log.e("", "");
			return b;
		} catch (IOException e) {
			Log.e("", e.getMessage(), e);
			return null;
		}
	}
	
	public static String getUserids(String name, Context context) {
		Cursor c=null;
		String userIds  = "";
		try {
			DataBase db = new DataBase(context);
			db.open();
			
			
			try {
				String[] rec = name.split(",");

					for (int i = 0; i < rec.length; i++) {

						if (rec[i] != null && !rec[i].trim().equals("")) {
							c = db.fetch(DataBase.Friends_table,
									DataBase.Friends_int,
									"name ='" + rec[i].trim() + "'");
							
							if (c.getCount() > 0) {
								c.moveToFirst();
								if (c.getInt(c.getColumnIndex("status")) != -1) {
									userIds +="-"+c.getString(c
											.getColumnIndex("number")) + "-,";
									//NameCount++;
								}
							} else {
								userIds += "-"+rec[i].trim() + "-,";
								//NameCount++;
							}
							c.close();
						}
					}
					if (userIds.endsWith(","))
						userIds = userIds
								.substring(0, userIds.lastIndexOf(","));
					
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (c!=null) {
					c.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			db.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return userIds;

	}
}
