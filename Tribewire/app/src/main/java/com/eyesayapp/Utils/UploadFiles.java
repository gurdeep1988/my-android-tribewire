package com.eyesayapp.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class UploadFiles {
	long size = 0;

	public boolean isProfilePic;

	public boolean isProfilePic() {
		return isProfilePic;
	}

	public void setProfilePic(boolean isProfilePic) {
		this.isProfilePic = isProfilePic;
	}

	public String SendRecord(String path, Context context) throws IOException {
		String res = "";
		long len = 0;
		size = 0;
		Log.e("", "The File Path is " + path);
		File tmpFile = new File(path);
		Log.e("", "===========The File Name is " + tmpFile.getName());
		if (tmpFile.exists()) {
			len = tmpFile.length();
		}
		if (len > 0) {
			size = len;
			try {

				// Part parts[] = { new FilePart("name=" + len, tmpFile,
				// "audio/wav", "audio/amr") ,new
				// StringPart(Constants.API_KEY_TAG,Constants.API_KEY),new
				// StringPart(Constants.REF_ID_TAG,CommonFunctions
				// .getPref(context, CommonFunctions.REF_ID, "")),new
				// StringPart(Constants.SESSION_TAG,CommonFunctions
				// .getPref(context, Constants.SESSION, ""))};

				// Part parts[] = { new FilePart(tmpFile.getName(), tmpFile)
				// ,new StringPart(Constants.API_KEY_TAG,Constants.API_KEY),new
				// StringPart(Constants.REF_ID_TAG,CommonFunctions
				// .getPref(context, CommonFunctions.REF_ID, "")),new
				// StringPart(Constants.SESSION_TAG,CommonFunctions
				// .getPref(context, Constants.SESSION, ""))};
				res = SendData(context, tmpFile.toString());
			} catch (FileNotFoundException e) {
				// 09-12 16:31:50.999: E/FILE respone(22225): <p>Message:
				// Undefined index: file_attachment</p>

				e.printStackTrace();
			}
		}
		return res.toString();
	}

	public String SendData(Context context, String filePath) throws IOException {
		String responseStr = "";

		if (CommonFunctions.CheckNetConnectivity(context)) {

			Debugger.debugE("Posting URL: " + CommonFunctions.DEMO_SERVER_URL
					+ CommonFunctions.UPLOAD_FILE_SHAHBAZ);

			try {
				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

				filePath = filePath.replace("/mnt", "");
				Log.e("", "My File Path " + filePath);

				NameValuePair nameValuePair = new NameValuePair(
						Constants.API_KEY_TAG, Constants.API_KEY);
				nameValuePairList.add(nameValuePair);

				NameValuePair nameValuePair1 = new NameValuePair(
						Constants.REF_ID_TAG, CommonFunctions.getPref(context,
								CommonFunctions.REF_ID, ""));
				nameValuePairList.add(nameValuePair1);

				NameValuePair nameValuePair2 = new NameValuePair(
						Constants.SESSION_TAG, CommonFunctions.getPref(context,
								Constants.SESSION, ""));
				nameValuePairList.add(nameValuePair2);

				Log.e("", "Posting first param name value " + nameValuePair);

				// 09-12 16:43:07.204: E/FILE respone(25921): <p>Message:
				// Undefined index: file_attachment</p>

				if (filePath != null && filePath.length() > 0) {
					NameValuePair nameValuePair3 = new NameValuePair(
							"file_attachment", filePath);
					nameValuePairList.add(nameValuePair3);
					Log.e("", "Posting second param name value "
							+ nameValuePair3);
					if (isProfilePic()) {
						NameValuePair nameValuePair4 = new NameValuePair(
								"is_profile_pic", "profile");
						nameValuePairList.add(nameValuePair4);
						Log.e("", "Posting second param name value "
								+ nameValuePair4);
					}
					
				}

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpPost httpPost = new HttpPost(
						CommonFunctions.DEMO_SERVER_URL
								+ CommonFunctions.UPLOAD_FILE_SHAHBAZ);
				// HttpPost httpPost = new
				// HttpPost(CommonFunctions.DEMO_SERVER_URL+TribeWireConstants.PROFILE_PIC_URL);
				int responseCode = -1;
				try {
					MultipartEntity entity = new MultipartEntity(
							HttpMultipartMode.BROWSER_COMPATIBLE);

					for (int index = 0; index < nameValuePairList.size(); index++) {
						if (nameValuePairList.get(index).getName()
								.equalsIgnoreCase("file_attachment")) {
							// if (nameValuePairList.get(index).getName()
							// .equalsIgnoreCase("profile_pic")) {

							entity.addPart(nameValuePairList.get(index)
									.getName(), new FileBody(new File(
									nameValuePairList.get(index).getValue())));
							Log.e("",
									"The Entity Data is 1" + entity.toString());
						} else {
							// Normal string data
							entity.addPart(nameValuePairList.get(index)
									.getName(), new StringBody(
									nameValuePairList.get(index).getValue()));
							Log.e("",
									"The Entity Data is 2" + entity.toString());
						}
					}
					Log.e("", "The Entity Data is " + entity.toString());

					httpPost.setEntity(entity);

					HttpResponse response = httpClient.execute(httpPost,
							localContext);
					responseCode = response.getStatusLine().getStatusCode();
					responseStr = EntityUtils.toString(response.getEntity());
					Debugger.debugE("Posting Response: " + responseStr);
					// Log.e("",
					// "=============Response From Server is "
					// + );
				} catch (IOException e) {
					e.printStackTrace();
					Debugger.debugE("Error in open connection :" + e.toString());

					CommonFunctions
							.writeLog("=======359 CommonFunctions  "
									+ e.getMessage() + " Response Code "
									+ responseCode);
					return "";
				}

			} catch (Exception e) {
				e.printStackTrace();

				CommonFunctions.writeLog("=======366 CommonFunctions  "
						+ e.getMessage());
				return "";
			}

			return responseStr;

		}
		Log.e("FILE respone", "**" + responseStr);
		return responseStr.toString();
	}

}
