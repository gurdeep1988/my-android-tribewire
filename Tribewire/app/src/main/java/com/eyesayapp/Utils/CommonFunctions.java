package com.eyesayapp.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.eyesayapp.asyn.RestApiCall;
import com.tribewire.app.ContactListSelection;
import com.tribewire.app.ConversationMessages;
import com.tribewire.app.EyeSayDefaultMessage;
import com.tribewire.app.EyesaySendText;
import com.tribewire.app.GroupListSelection;
import com.tribewire.app.Login;
import com.tribewire.app.MessageReceiver;
import com.tribewire.app.R;
import com.tribewire.app.TenFourConversations;
import com.tribewire.app.TenFourRecordVoice;
import eyesay.obj.Friend;
import eyesay.parser.TenFourMessageXML;

public class CommonFunctions {

	public static String TILE = "tile";
	public static String EMAIL_COMPANY = "email_tile";
	public static String TILE_LOCAL_PATH = "tile_local_path";
	public static String U_TILE_LOCAL_PATH = "u_tile_local_path";
	public static String IS_LOGIN = "login_tribewire";
	public static String PHONE_NUMBER = "number";
	public static String EMAIL = "email";
	public static String REF_ID = "refid";
	public static final String BTN_OK = "btn_ok";
	public static final String BTN_PARTY_STARTED = "btn_paty_started";
	public static String PLAYBACK = "playback";
	public static String DEFAULT_REPLY = "reply";
	public static String PREF_PACKFAV_PURCHASE_STATE = "innerpurchase1";
	public static String PURCHASE_STATE_PURCHASED = "purchased";
	public static String PURCHASE_STATE_NOT_PURCHASED = "notpurchased";
	public static String UPGRADE_PACKAGE1 = "tenfourmeupgrade1";
	// http://dev.eyesay.com/
	public static String DEMO_SERVER_URL = "http://app.tribewireinside.com/";
	public static String BASE_URL = "http://107.20.202.43/new_demo/"; // "http://107.20.202.43/";
	public static String UPLOAD_URL = "api/file_upload_temp.php";
	public static String UPLOAD_URL_GS = "api/file_upload_gs.php";
	// public static String UPLOAD_URL = "api/upload_video123.php";
	public static String GET_NEW_MESSAGE = "api/get_new_messages";
	public static String UPDATE_READ_STATUS = "api/update_conversation_read_status";
	public static int NOTIFICATION_ID = 101;
	public static int UNREAD_ID = 102;
	public static String FB_APP_ID = "110862205611506";

	// Credential Records for Amazon

	public static final String ACCESS_KEY_ID = "AKIAJLTEBNHW4LVQDVWA";
	public static final String SECRET_KEY = "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";
	public static final String MEDIA_BUCKET = "ten-foure-large";
	public static final String AUDIO_FORMAT = "audio/wav";
	public static final String VIDEO_FORMAT = "video/3gpp";
	public static final String TINY_URL_LINK = "http://is.gd/create.php?format=simple&url=";
	public static final String MSG_RECEIVED = "msg_received";
	public static String SENT_RECEIVE_MESSAGE = "api/receive_sent_message-gs.php?";
	//
	public static String READ_CONV = "api/message_isread";
	public static final String LOGIN_URL = "api/userlogin";
	public static final String CONTACT_URL = "api/get_contanct_list";
	public static final String LOGOUT_URL = "api/logout_m";
	public static final String TILES_URL = "api/Get_company_tile";
	public static String SEND_MESSAGE_SHAHBZ = "api/send_instant_message?";
	public static String GET_INBOX = "api/get_all_conversations";
	public static String GET_CHAT_NOTIFICATION = "api/get_chat_notifications";
	public static String GET_SPECIFIC_CONVERSATION = "api/get_conversation_messages";
	public static String GET_COMPANY_TILE = "api/Get_company_tile";
	public static String GET_GROUP_MEMBER_LIST = "api/get_group_members_list";
	public static String DELETE_USER_MESSAGE = "api/delete_user_message";
	public static String UPLOAD_FILE_SHAHBAZ = "api/upload_file";
	public static final String CREATE_GROUP = "api/create_group";
	public static final String UPDATE_GROUP = "api/update_group";
	public static final String DELETE_GROUP = "api/delete_group";
	public static final String GET_GROUP_LIST = "api/get_groups_list";

	public static String uploadedUrl;

	/**
	 * 
	 * @param uploadedUrl
	 *            Stores the Current Information regarding the Amazon Upload URL
	 *            OF Media Files
	 * 
	 */
	public static void setUploadedUrl(String uploadedUrl) {
		CommonFunctions.uploadedUrl = uploadedUrl;
	}

	public static String getUploadedUrl() {
		return uploadedUrl;
	}

	public static boolean CheckNetConnectivity(Context mContext) {
		ConnectivityManager connec = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)
			return true;
		return false;
	}

	public static boolean isFullVersion(Context context) {
		String purchased = getPref(context, PREF_PACKFAV_PURCHASE_STATE,
				PURCHASE_STATE_NOT_PURCHASED);
		return purchased == PURCHASE_STATE_PURCHASED;
	}

	public static int getGroupLimit() {
		return 5;
	}

	public static int getFavouriteLimit() {
		return 20;
	}

	public synchronized static boolean checkForEqual(String cellno,
			String cellNumberOfCall) {

		if (cellno.startsWith("0"))
			cellno = cellno.substring(1);

		if (cellNumberOfCall.startsWith("0"))
			cellNumberOfCall = cellNumberOfCall.substring(1);

		if (cellNumberOfCall.contains("-"))
			cellNumberOfCall = cellNumberOfCall.replace("-", "");

		if (cellno.length() > cellNumberOfCall.length()) {
			int startIndex = (cellno.length() - cellNumberOfCall.length());
			if (cellNumberOfCall.equals(cellno.substring(startIndex)))
				return true;
		} else {
			int startIndex = (cellNumberOfCall.length() - cellno.length());
			if (cellno.equals(cellNumberOfCall.substring(startIndex)))
				return true;
		}
		return false;
	}

	public static boolean isNetworkAvailable(Context c) {
		ConnectivityManager connectivityManager = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null)
			return networkInfo.getDetailedState().equals(
					NetworkInfo.DetailedState.CONNECTED);
		return false;
	}

	public static boolean isInternetConnected(Context mContext) {
		ConnectivityManager connec = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)
			return true;
		return false;
	}

	public static String LastErrorMessage = "";

	public static String postNamevalueDataOnServer(String urlString,
			String message, String messageType, String receiver,
			String conv_id, String serverFileName, Context context,
			boolean isEmergency, boolean isBroadCast) throws IOException {

		String responseResult = null;

		try {

			try {
				HttpPost httpPost = new HttpPost(urlString);

				List<org.apache.http.NameValuePair> nameValuePair = new ArrayList<org.apache.http.NameValuePair>(
						9);
				nameValuePair.add(new BasicNameValuePair("api_key",
						Constants.API_KEY));
				nameValuePair.add(new BasicNameValuePair("user_id",
						CommonFunctions.getPref(context,
								CommonFunctions.REF_ID, "")));
				nameValuePair
						.add(new BasicNameValuePair("comp_id", CommonFunctions
								.getPref(context, Constants.COM_ID, "")));
				// if (conv_id!=null) {
				nameValuePair.add(new BasicNameValuePair("conv_id", conv_id));
				// }
				if (isEmergency) {
					nameValuePair.add(new BasicNameValuePair("is_emergency",
							"1"));
				} else {
					nameValuePair.add(new BasicNameValuePair("is_emergency",
							"0"));
				}

				nameValuePair
						.add(new BasicNameValuePair("session", CommonFunctions
								.getPref(context, Constants.SESSION, "")));
				nameValuePair.add(new BasicNameValuePair("text",
						android.util.Base64.encodeToString(message.getBytes(),
								android.util.Base64.DEFAULT)));
				// nameValuePair.add(new BasicNameValuePair("msg_type", "0"));
                       if (!isBroadCast) {
           				nameValuePair.add(new BasicNameValuePair("recipients", Base64
        						.encode(receiver.getBytes())));

					}
                       else{
                    	   
                    	   nameValuePair.add(new BasicNameValuePair("recipients",""));
                    	   nameValuePair.add(new BasicNameValuePair("con_type","2"));
                       }
				Log.e("",
						"---------------------------------base 64 is--------------------------- "
								+ Base64.encode(receiver.getBytes()));

				Log.e("", "================   The Server File Name is "
						+ serverFileName);
				if (serverFileName != null && serverFileName.length() > 2) {
					nameValuePair.add(new BasicNameValuePair("attachment",
							serverFileName));

					String attachment_type = "2";
					if (serverFileName.endsWith(".mp4")) {
						attachment_type = "1";
					} else if (serverFileName.endsWith(".mp3")) {
						attachment_type = "2";
					} else if (serverFileName.endsWith(".jpg")
							|| serverFileName.endsWith(".jpeg")
							|| serverFileName.endsWith(".png")
							|| serverFileName.endsWith(".gif")) {
						attachment_type = "3";
					}

					Log.e("", "================   The Attachment type is  "
							+ attachment_type);
				
						nameValuePair.add(new BasicNameValuePair("attachment_type",
								attachment_type));
				
					
				}

				Log.e("", "api_key " + Constants.API_KEY);
				Log.e("",
						"user_id "
								+ CommonFunctions.getPref(context,
										CommonFunctions.REF_ID, ""));
				Log.e("",
						"comp_id "
								+ CommonFunctions.getPref(context,
										Constants.COM_ID, ""));
				Log.e("", "conv_id " + conv_id);

				if (isEmergency) {
					Log.e("", "is_emergency===  1");
				} else {
					Log.e("", "is_emergency===  0");
				}

				Log.e("",
						"session "
								+ CommonFunctions.getPref(context,
										Constants.SESSION, ""));
				Log.e("",
						"text "
								+ android.util.Base64.encodeToString(
										message.getBytes(),
										android.util.Base64.DEFAULT));
				// Log.e("", "The Api Msg Type 0");
				Log.e("", "recipients  " + Base64.encode(receiver.getBytes()));

				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

				DefaultHttpClient client = new DefaultHttpClient();

				HttpResponse httpResponse = client.execute(httpPost);
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				Log.e("", "The Status Code from the Server is " + statusCode);
				if (statusCode == HttpStatus.SC_OK) {
					responseResult = EntityUtils.toString(httpResponse
							.getEntity());
				} else {
					Log.e("",
							"=============== The Wrong respone in error mode is "
									+ EntityUtils.toString(httpResponse
											.getEntity()));
					responseResult = statusCode
							+ " Server Error , Please try after some time ";
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseResult;

	}

	public static String postNameValueDataOnServer(Context context, int size,
			List<String> nameList, List<String> valueList, String url) {
		String responseResult = "";

		try {

			HttpPost httpPost = new HttpPost(url);

			List<org.apache.http.NameValuePair> nameValuePair = new ArrayList<org.apache.http.NameValuePair>(
					size);

			Log.e("", "Url is " + url + " == List size is " + nameList.size());
			for (int i = 0; i < nameList.size(); i++) {
				nameValuePair.add(new BasicNameValuePair(nameList.get(i),
						valueList.get(i)));
				Log.e("", " =Name Param " + nameList.get(i)
						+ " ==== Value List " + valueList.get(i));

			}

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

			DefaultHttpClient client = new DefaultHttpClient();

			HttpResponse httpResponse = client.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.e("", "The Status Code from the Server is " + statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				responseResult = EntityUtils.toString(httpResponse.getEntity());
			}

			else {
				responseResult = statusCode
						+ " Server Error , Please try after some time \n"
						+ EntityUtils.toString(httpResponse.getEntity());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseResult;
	}

	public static String postData(Context context, int size,
			List<String> nameList, List<String> valueList, String url) {
		String responseResult = "";

		try {

			HttpGet httpPost = new HttpGet(
					"http://dev.eyesay.com/api/set_profile_pic?user_id=NDI5&api_key=65e2y3e5s4a2y54&session=6a1dbf8dbd6459f33712c58562e66718&profile_pic=https://m.ak.fbcdn.net/profile.ak/hprofile-ak-prn2/t1/1780691_627345683979801_1994700623_n.jpg");

			// List<org.apache.http.NameValuePair> nameValuePair = new
			// ArrayList<org.apache.http.NameValuePair>(
			// size);

			Log.e("", "Url is " + url + " == List size is " + nameList.size());
			// for (int i = 0; i < nameList.size(); i++) {
			// nameValuePair.add(new BasicNameValuePair(nameList.get(i),
			// valueList.get(i)));
			// Log.e("", " =Name Param " + nameList.get(i)
			// + " ==== Value List " + valueList.get(i));
			//
			// }
			//
			// httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

			DefaultHttpClient client = new DefaultHttpClient();

			HttpResponse httpResponse = client.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.e("", "The Status Code from the Server is " + statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				responseResult = EntityUtils.toString(httpResponse.getEntity());
			}

			else {
				responseResult = statusCode
						+ " Server Error , Please try after some time ";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseResult;
	}

	public static String postDataOnServer(String urlString, byte[] content)
			throws IOException {
		Debugger.debugE("Posting URL: " + urlString);
		Debugger.debugE("Posting Request: " + new String(content));

		String responseStr = "";
		try {
			URL url = null;
			HttpURLConnection con = null;
			try {
				url = new URL(urlString);
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setConnectTimeout(30000);
				con.connect();

			} catch (IOException e) {
				Debugger.debugE("Error in open connection :" + e.toString());
				LastErrorMessage = "ERR1 :" + e.getMessage();
				return "";
			}

			OutputStream out;
			out = con.getOutputStream();
			out.write(content, 0, content.length);
			out.flush();
			out.close();
			String decodedString = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()), 8000);

			while ((decodedString = in.readLine()) != null)
				responseStr = responseStr.concat(decodedString);
			in.close();

			Debugger.debugE("Posting Response: " + responseStr);

		} catch (Exception e) {
			LastErrorMessage = "ERR2 :" + e.getMessage();
			return "";
		}
		return responseStr;
	}

	// TODO TEMP METHOD CREATED BY GURDEEP FOR TESTING PURPOSE

	public static String postUserProfile(String urlString, byte[] content,
			String filePath) throws IOException {
		Debugger.debugE("Posting URL: " + urlString);
		Debugger.debugE("Posting Request: " + new String(content));
		String responseStr = "";
		try {
			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			String string = new String(content);
			string = string.replace("str=", "");
			Log.e("", "Posting Profile  Param" + string);
			filePath = filePath.replace("/mnt", "");
			Log.e("", "My File Path " + filePath);

			org.apache.commons.httpclient.NameValuePair nameValuePair = new org.apache.commons.httpclient.NameValuePair(
					"str", string);

			nameValuePairList.add(nameValuePair);
			Log.e("", "Posting first param name value " + nameValuePair);
			if (filePath != null && filePath.length() > 0) {
				NameValuePair nameValuePair1 = new org.apache.commons.httpclient.NameValuePair(
						"photo", filePath);
				nameValuePairList.add(nameValuePair1);
				Log.e("", "Posting second param name value " + nameValuePair1);
			}

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(urlString);
			int responseCode = -1;
			try {
				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

				for (int index = 0; index < nameValuePairList.size(); index++) {
					if (nameValuePairList.get(index).getName()
							.equalsIgnoreCase("photo")) {
						// If the key equals to "image", we use FileBody to
						// transfer
						// the data
						entity.addPart(
								nameValuePairList.get(index).getName(),
								new FileBody(new File(nameValuePairList.get(
										index).getValue())));
						Log.e("", "The Entity Data is 1" + entity.toString());
					} else {
						// Normal string data
						entity.addPart(nameValuePairList.get(index).getName(),
								new StringBody(nameValuePairList.get(index)
										.getValue()));
						Log.e("", "The Entity Data is 2" + entity.toString());
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

				LastErrorMessage = "ERR1 :" + e.getMessage();
				CommonFunctions.writeLog("=======359 CommonFunctions  "
						+ e.getMessage() + " Response Code " + responseCode);
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
			LastErrorMessage = "ERR2 :" + e.getMessage();
			CommonFunctions.writeLog("=======366 CommonFunctions  "
					+ e.getMessage());
			return "";
		}

		return responseStr;
	}

	public static void setPref(Context c, String pref, String val) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
		e.putString(pref, val);
		e.commit();
	}

	public static void setPref(Context c, String pref, boolean val) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
		e.putBoolean(pref, val);
		e.commit();
	}

	public static void setPref(Context c, String pref, Long val) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
		e.putLong(pref, val);
		e.commit();
	}

	public static void setPref(Context c, String pref, Integer val) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
		e.putInt(pref, val);
		e.commit();
	}

	public static String getPref(Context c, String pref, String val) {
		return PreferenceManager.getDefaultSharedPreferences(c).getString(pref,
				val);
	}

	public static long getPref(Context c, String pref, Long val) {
		return PreferenceManager.getDefaultSharedPreferences(c).getLong(pref,
				val);
	}

	public static Integer getPref(Context c, String pref, Integer val) {
		return PreferenceManager.getDefaultSharedPreferences(c).getInt(pref,
				val);
	}

	public static Boolean getPref(Context c, String pref, Boolean val) {
		return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(
				pref, val);
	}

	public static void showAlert(Context c, String msg, String title) {
		new AlertDialog.Builder(c)
				.setMessage(msg)
				.setTitle(title)
				.setPositiveButton(c.getResources().getString(R.string.ok),
						null).show();
	}

	public static void showUpgradeDialog(final Activity c, String msg,
			String title, final String pref, final boolean finishit) {
		new AlertDialog.Builder(c)
				.setMessage(msg)
				.setTitle(title)
				.setPositiveButton("Upgrade Now",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Intent i = new Intent(c,
								// PurchaseScreen.class);
								// i.putExtra("scheme", pref);
								// c.startActivity(i);
								if (finishit)
									c.finish();
							}
						})
				.setNegativeButton("Maybe later",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (finishit)
									c.finish();
							}
						})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						if (finishit)
							c.finish();
					}
				}).create().show();
	}

	/**
	 * Hexadecimal NCRs
	 * 
	 * @param str
	 * @return
	 */
	public static String convertHex(String str) {

		// return "&#1601;&#1610;&#1580;&#1575;&#1610";
		StringBuffer ostr = new StringBuffer();

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);

			// Does the char need to be converted to unicode?
			if ((ch >= 0x0020) && (ch <= 0x007e)) {
				ostr.append(ch); // No.
			} else // Yes.
			{
				ostr.append("&#x");

				// Get hex value of the char.
				String hex = Integer.toHexString(str.charAt(i) & 0xFFFF);

				ostr.append(hex.toUpperCase());

				ostr.append(";");
			}
		}
		Debugger.debugE("HEX123 : " + ostr.toString());
		// Return the stringbuffer cast as a string.
		return (new String(ostr));
	}

	public static String getTag(String resp, String tagName) {
		try {
			int len = tagName.length() + 2;
			return resp.substring(resp.indexOf("<" + tagName) + len,
					resp.indexOf("</" + tagName));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Used to Retrive the Registration Information from The Serve in the Json
	 * Format and parsing it to get the RefId
	 * 
	 * @return
	 */

	public static String getRefrenceId(String res) {
		String refId = "0";
		try {
			JSONObject jsonObject = new JSONObject(res);
			if (jsonObject.getString("success").equalsIgnoreCase("1")) {
				refId = jsonObject.getString("refid");

			} else {
				refId = "0";
				// TODO Message has to handle
				jsonObject.getString("message");
			}
		} catch (JSONException e) {
			// TODO: handle exception
		}

		return refId;
	}

	public static boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	public static String getBaseURL() throws IOException {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String ePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			if (!ePath.endsWith("/"))
				ePath = ePath + "/CRecord/";

			File videoirectory = new File(ePath + "MyVideo/");

			if (!videoirectory.exists()) {

				videoirectory.mkdirs();
			} else {

			}
			File AudioDirectory = new File(ePath + "MyAudio/");
			if (!AudioDirectory.exists()) {

				AudioDirectory.mkdirs();
			} else {

			}

			File tmpDirectory = new File(ePath + "temporary/");
			if (!tmpDirectory.exists()) {

				tmpDirectory.mkdirs();
			} else {

			}
			File eyesaydownload = new File(ePath + "eyesaydownload/");
			if (!eyesaydownload.exists()) {

				eyesaydownload.mkdirs();
			} else {

			}
			File imageDir = new File(ePath + "/MyImage");
			if (!imageDir.exists()) {

				imageDir.mkdirs();
			} else {

			}
			return ePath;

		}
		return "";

	}

	public static void startContctService(Context context) {
		Debugger.debugE("StartContactService Called");
		/*
		 * AlarmManager am = (AlarmManager) context
		 * .getSystemService(Context.ALARM_SERVICE); Intent i = new
		 * Intent(context, ContactReceiver.class); PendingIntent pI =
		 * PendingIntent.getService(context, 0, i, 0); try { am.cancel(pI); }
		 * catch (Exception e) { Debugger.debugE("Error in Alarm of contact" +
		 * e.getMessage()); } long interval = 15 * 60 * 1000;
		 * am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
		 * 1000, interval, pI);
		 */
	}

	public static void startMessageService(Context context) {
		Debugger.debugE("StartMessageService Called");
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		try {
			Log.e("",
					"Service Class Name is " + MessageReceiver.class.toString());

		} catch (Exception e) {
			// TODO: handle exception
		}

		Intent i = new Intent(context, MessageReceiver.class);

		PendingIntent pI = PendingIntent.getService(context, 0, i, 0);
		try {
			am.cancel(pI);
		} catch (Exception e) {
			Debugger.debugE("Error in Alarm of message" + e.getMessage());
		}
		long interval = 15 * 1000;
		am.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 1000, interval, pI);
	}

	public static void CancelAlarm(Context context) {
		Intent intent = new Intent(context, MessageReceiver.class);
		PendingIntent sender = PendingIntent.getService(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	public static boolean SendGroupMessage(Context context, boolean flag,
			String baseFileUrl, String number, String names, boolean reply,
			String converationId, ArrayList<String> grList,
			ArrayList<GroupModel> gMList) {

		return false;
	}

	class TempAsyn extends AsyncTask<Object, Void, Void> {

		@Override
		protected Void doInBackground(Object... params) {

			String filePathe = (String) params[0];
			Context context = (Context) params[1];

			com.eyesayapp.Utils.UploadFiles files = new com.eyesayapp.Utils.UploadFiles();

			try {
				String Response = files.SendRecord(filePathe, context);

				Log.e("", "===============The Response is " + Response);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	// Start of Send text Group Message
	public static boolean SendTextGroupMessage(Context context, boolean flag,
			String baseFileUrl, String number, String names, boolean reply,
			String converationId, ArrayList<String> grList,
			ArrayList<GroupModel> gMList, String message) {

		try {

			Log.e("",
					"The Base File Url Path is " + baseFileUrl
							+ "Reply Status :" + reply + " Gr List Size "
							+ grList.size());
			String Type = "2";
			if (!flag) {
				Type = "2";
				// Toast.makeText(context, "flag is false ",
				// Toast.LENGTH_LONG).show();
			}

			try {
				// message =
				// android.util.Base64.encodeToString(message.getBytes(),
				// android.util.Base64.DEFAULT);

				message = message.replace("\n", "");
				// message = Base64Coder.encodeString(message);
				Log.e("", "=====================  Base 64 Encoded message "
						+ message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String sender = CommonFunctions.getPref(context,
					CommonFunctions.PHONE_NUMBER, "");
			Log.e("", "The Sender Name is " + sender);
			if (CheckNetConnectivity(context)) {
				// eyesay.Utils.UploadFiles files = new
				// eyesay.Utils.UploadFiles();
				DataBase db = new DataBase(context);
				// db.open();
				String file_url = "(null)";
				String serverFileName = "";
				try {

					String Response = "";

					// files.SendRecord(baseFileUrl, context);

					if (baseFileUrl != null
							&& baseFileUrl.length() > 0
							&& (baseFileUrl.endsWith(".jpeg")
									|| baseFileUrl.toLowerCase().endsWith(
											".jpg") || baseFileUrl
									.toLowerCase().endsWith(".png"))) {

						UploadFiles files = new UploadFiles();
						String Response1 = files.SendRecord(baseFileUrl,
								context);

						if (Response1 != null && Response1.length() > 0) {

							try {
								JSONObject jsonObject1 = new JSONObject(
										Response1);
								JSONObject jsonObject = jsonObject1
										.getJSONObject("response");
								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {

									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Video file Name is "
													+ fileName
													+ "==============");
									Type = "2";
									Response = Constants.IMAGE_BASE_URL
											+ fileName;
									file_url = Constants.IMAGE_BASE_URL
											+ fileName;
									try {
										String extension = fileName.substring(
												fileName.indexOf("."),
												fileName.length());

										fileName = fileName.substring(0,
												fileName.indexOf("."));
										fileName = fileName + "_thumb"
												+ extension;
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									Log.e("",
											"============Error while uploading Video to server==========");
									Response = "";
									Type = "2";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					} else if (baseFileUrl != null
							&& baseFileUrl.length() > 0
							&& (baseFileUrl.endsWith(".mp4")
									|| baseFileUrl.endsWith(".mov") || baseFileUrl
										.endsWith(".3gpp"))) {
						UploadFiles files1 = new UploadFiles();
						String Response2 = files1.SendRecord(baseFileUrl,
								context);

						if (Response2 != null && Response2.length() > 0) {

							try {
								JSONObject jsonObject = new JSONObject(
										Response2);
								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {

									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Video file Name is "
													+ fileName
													+ "==============");
									Type = "3";
									Response = Constants.VIDEO_BASE_URL
											+ fileName;
									file_url = Constants.VIDEO_BASE_URL
											+ fileName;
								} else {
									Log.e("",
											"============Error while uploading Video to server==========");
									Response = "";
									Type = "2";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}

					else if (baseFileUrl != null && baseFileUrl.length() > 0
							&& baseFileUrl.endsWith(".wav")) {
						UploadFiles files2 = new UploadFiles();
						String Response3 = files2.SendRecord(baseFileUrl,
								context);

						if (Response3 != null && Response3.length() > 0) {

							try {
								JSONObject jsonObject1 = new JSONObject(
										Response3);
								JSONObject jsonObject = jsonObject1
										.getJSONObject("response");
								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {

									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Audio file Name is "
													+ fileName
													+ "==============");
									Type = "4";
									Response = Constants.AUDIO_BASE_URL
											+ fileName;
									file_url = Constants.AUDIO_BASE_URL
											+ fileName;
								} else {
									Log.e("",
											"============Error while uploading Audio to server==========");
									Response = "";
									Type = "2";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}

					if (context instanceof ContactListSelection) {
						try {
							ContactListSelection contactListSelection = (ContactListSelection) context;
							if (contactListSelection.eyesayonWayDialog != null) {
								contactListSelection.eyesayonWayDialog
										.getHandlerObj().sendEmptyMessage(10);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (context instanceof GroupListSelection) {
						try {
							GroupListSelection groupListSelection = (GroupListSelection) context;
							if (groupListSelection.eyesayonWayDialog != null) {
								groupListSelection.eyesayonWayDialog
										.getHandlerObj().sendEmptyMessage(10);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (context instanceof TenFourRecordVoice) {
						try {
							TenFourRecordVoice tenFourRecordVoice = (TenFourRecordVoice) context;
							if (tenFourRecordVoice.eyesayonWayDialog != null) {
								tenFourRecordVoice.eyesayonWayDialog
										.getHandlerObj().sendEmptyMessage(10);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (context instanceof EyesaySendText) {
						try {
							EyesaySendText eyesaySendText = (EyesaySendText) context;
							// if (eyesaySendText.eyesayonWayDialog != null) {
							// eyesaySendText.eyesayonWayDialog
							// .getHandlerObj().sendEmptyMessage(10);
							// }
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					if (true) {

						// if (!Response.equals("")
						// && Response!=null && Response.length()>0) {
						// String FileURL = CommonFunctions.getTag(Response,
						// "file_url");
						Debugger.debugE("file Url " + file_url);
						String tempConversationId = "";
						if (converationId != null) {
							try {
								Log.e("", "The Convertion Id is "
										+ converationId);
							} catch (Exception e) {
								e.printStackTrace();
							}
							tempConversationId = converationId;

						} else {
							tempConversationId = "tfc" + new Date().getTime();
							Log.e("", "Else case Conversation id  "
									+ tempConversationId);
						}
						Response = CommonFunctions.postNamevalueDataOnServer(
								CommonFunctions.DEMO_SERVER_URL
										+ CommonFunctions.SEND_MESSAGE_SHAHBZ,
								message, Type, number, converationId,
								serverFileName, context, true,false);
						Log.e("", "The Response from the Server is " + Response);
						String success = "";
						String str_msg = "";

						/**
						 * {"error":false, "error_msg":"",
						 * "resp_msg":"Message Sent Successfully!",
						 * "conv_id":"NA=="}
						 */

						String serverConvId = "";
						try {
							JSONObject jsonObject1 = new JSONObject(Response);
							JSONObject jsonObject = jsonObject1
									.getJSONObject("response");
							success = jsonObject.getString("error");
							str_msg = jsonObject.getString("resp_msg");
							serverConvId = jsonObject.getString("conv_id");
						} catch (Exception e) {
							e.printStackTrace();
						}
						// Log.e("Response:", Response);
						// if (!Response.equals("")
						// && CommonFunctions.getTag(Response, "status")
						// .equals("1") && Response != null) {
						if (!Response.equals("")
								&& success.equals("false")
								&& Response != null
								&& str_msg
										.equalsIgnoreCase("Message Sent Successfully!")) {

							try {

								// CommonFunctions.showAlert(Recorder.this,
								// "Message Sent Successfully", "10-4");

								// db = new DataBase(context);
								try {
									// FlurryAgent.logEvent("Message sent");
								} catch (Exception e) {
									e.printStackTrace();
								}
								db.open();
								long d = System.currentTimeMillis();
								Debugger.debugE("Sender : " + sender
										+ " Receiver : " + number + "===Date :"
										+ d);
								int sender_status = 1;
								try {
									message = android.util.Base64
											.encodeToString(message.getBytes(),
													android.util.Base64.DEFAULT);
								} catch (Exception e) {
									e.printStackTrace();
								}
								// Create a Loop Here

								try {
									String senderName = "";
									try {
										senderName = CommonFunctions.getPref(
												context, Constants.FIRST_NAME,
												"")
												+ CommonFunctions
														.getPref(
																context,
																Constants.LAST_NAME,
																"");
									} catch (Exception e) {
										e.printStackTrace();
									}
									for (int i = 0; i < grList.size(); i++) {

										String groupName = grList.get(i);
										String groupMembers = gMList.get(i)
												.getGroupMember().trim();

										try {
											String name = "";

											if (!reply) {

												String tmp = db
														.fetchConversationID(groupName);
												// db.close();
												if (tmp.equals("")) {
													converationId = serverConvId;
												} else
													converationId = serverConvId;
											}

											Log.e("",
													"The Name of the Group is "
															+ groupName);
											groupMembers = groupMembers
													.replace("-", "");
											groupMembers = sortStringArray(groupMembers
													+ ","
													+ CommonFunctions
															.getPref(
																	context,
																	CommonFunctions.PHONE_NUMBER,
																	""));
											SimpleDateFormat sdf = new SimpleDateFormat(
													"yyyy-MM-dd HH:mm:ss");
											String formattedDate = sdf
													.format(new Date());
											Log.e("",
													"Formatted Date is ========== "
															+ formattedDate
															+ "==========");
											if (groupName
													.equalsIgnoreCase("Emergency")) {
												// TODO
												// No need to insert emergency
												// data record in local database

												db.insert(
														DataBase.Receive_table,
														DataBase.Receive_int,
														new String[] {
																"0",
																CommonFunctions
																		.getPref(
																				context,
																				CommonFunctions.PHONE_NUMBER,
																				""),
																names,
																Type,
																file_url,
																"" + d,
																"0",
																"1",
																converationId,
																"0",
																""
																		+ sender_status,
																"0", "0",
																groupMembers,
																"0", "0",
																message, "0",
																formattedDate,
																"", senderName });

											} else {
												db.insert(
														DataBase.Receive_table,
														DataBase.Receive_int,
														new String[] {
																"0",
																CommonFunctions
																		.getPref(
																				context,
																				CommonFunctions.PHONE_NUMBER,
																				""),
																names,
																Type,
																file_url,
																"" + d,
																"0",
																"1",
																converationId,
																"0",
																""
																		+ sender_status,
																"0", "0",
																groupMembers,
																message, "0",
																formattedDate,
																"", senderName });

												// db.insert(
												// DataBase.Receive_table,
												// DataBase.Receive_int,
												// new String[] {
												// "0",
												// CommonFunctions
												// .getPref(
												// context,
												// CommonFunctions.PHONE_NUMBER,
												// ""), names,
												// Type, file_url, "" + d,
												// "0", "1", converationId,
												// "0", "" + sender_status,
												// number, "0", "0", message,
												// "0", formattedDate, "",
												// senderName });

											}
										} catch (Exception e) {
											e.printStackTrace();
										}

									}

								} catch (Exception e) {
									e.printStackTrace();
								}
								Toast.makeText(
										context,
										context.getString(R.string.msg_sent_successfully),
										Toast.LENGTH_LONG).show();
								db.close();
								return true;

							} catch (Exception e) {
								e.printStackTrace();
								return false;
							}

						} else {
							// when no contact is selected and user pressed send
							// message button ... . .. . . . . . . . .. . . . .
							// .. .
							// . . . . .

							AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(
									context);

							// set title
							alertDialogBuilder1.setTitle("EyeSay");

							// set dialog message
							alertDialogBuilder1
									.setMessage(
											"No contact is selected, first select the contacts!")
									.setCancelable(false)
									.setPositiveButton(
											"ok",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													// if this button is
													// clicked,
													// close
													// current activity

													dialog.cancel();
												}
											});

							// create alert dialog
							AlertDialog alertDialog1 = alertDialogBuilder1
									.create();

							// show it
							alertDialog1.show();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					Debugger.debugE("Exception " + e.getMessage());

				} finally {
					try {
						db.close();
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
			}

			return false;

		} catch (Exception e) {
			return false;
		}

	}

	public static boolean SendTextMessage(Context context, boolean flag,
			String baseFileUrl, String number, String names, boolean reply,
			String converationId, String message, boolean isBroadCastMessage) {
		// String Response = "";
		try {

			Log.e("", "The Base File Url Path is " + baseFileUrl);
			String Type = "0";
			String serverFileName = "";
			if (!flag) {
				Type = "0";
				// Toast.makeText(context, "flag is false ",
				// Toast.LENGTH_LONG).show();
			}

			try {
				// message = android.util.Base64.encodeToString(
				// message.getBytes(), android.util.Base64.DEFAULT);
				message = message.replace("\n", "");
				// message = Base64Coder.encodeString(message);
				Log.e("", "=====================  Base 64 Encoded message "
						+ message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String sender = CommonFunctions.getPref(context,
					CommonFunctions.PHONE_NUMBER, "");
			Log.e("", "The Sender Name is " + sender);
			if (CheckNetConnectivity(context)) {

				DataBase db = new DataBase(context);

				// db.open();
				// Object[] params= new Object[2];
				// params[0]= baseFileUrl;
				// params[1]= context;
				// new CommonFunctions().new TempAsyn().execute(params);

				try {
					String file_url = "(null)";
					String Response = "";

					// files.SendRecord(baseFileUrl, context);

					if (baseFileUrl != null
							&& baseFileUrl.length() > 0
							&& (baseFileUrl.endsWith(".jpeg")
									|| baseFileUrl.toLowerCase().endsWith(
											".jpg") || baseFileUrl
									.toLowerCase().endsWith(".png"))) {

						UploadFiles files = new UploadFiles();
						String Response1 = files.SendRecord(baseFileUrl,
								context);

						// TYPE = 3 // Its an image
						// TYPE = 0 // Its an text message only
						// TYPE = 2 // Its an Audio File
						if (Response1 != null && Response1.length() > 0) {

							try {
								// JSONObject jsonObject = new JSONObject(
								// Response1);

								JSONObject jsonObject1 = new JSONObject(
										Response1);
								JSONObject jsonObject = jsonObject1
										.getJSONObject("response");
								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {

									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Video file Name is "
													+ fileName
													+ "==============");
									Type = "3";
									Response = Constants.IMAGE_BASE_URL
											+ fileName;
									try {
										String extension = fileName.substring(
												fileName.indexOf("."),
												fileName.length());

										fileName = fileName.substring(0,
												fileName.indexOf("."));
										fileName = fileName + "_thumb"
												+ extension;
									} catch (Exception e) {
										e.printStackTrace();
									}

									file_url = Constants.IMAGE_BASE_URL
											+ fileName;
								} else {
									Log.e("",
											"============Error while uploading Video to server==========");
									Response = "";

								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					} else if (baseFileUrl != null
							&& baseFileUrl.length() > 0
							&& (baseFileUrl.endsWith(".mp4")
									|| baseFileUrl.endsWith(".mov") || baseFileUrl
										.endsWith(".3gpp"))) {
						UploadFiles files1 = new UploadFiles();
						String Response2 = files1.SendRecord(baseFileUrl,
								context);

						if (Response2 != null && Response2.length() > 0) {

							try {
								JSONObject jsonObject = new JSONObject(
										Response2);
								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {

									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Video file Name is "
													+ fileName
													+ "==============");
									Type = "4";
									Response = Constants.VIDEO_BASE_URL
											+ fileName;
									file_url = Constants.VIDEO_BASE_URL
											+ fileName;
								} else {
									Log.e("",
											"============Error while uploading Video to server==========");
									Response = "";

								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}

					else if (baseFileUrl != null && baseFileUrl.length() > 0
							&& baseFileUrl.endsWith(".wav")) {
						UploadFiles files2 = new UploadFiles();
						String Response3 = files2.SendRecord(baseFileUrl,
								context);

						if (Response3 != null && Response3.length() > 0) {

							try {
								// JSONObject jsonObject = new JSONObject(
								// Response3);
								JSONObject jsonObject1 = new JSONObject(
										Response3);
								JSONObject jsonObject = jsonObject1
										.getJSONObject("response");
								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {

									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Audio file Name is "
													+ fileName
													+ "==============");
									Type = "2";
									Response = Constants.AUDIO_BASE_URL
											+ fileName;
									file_url = Constants.AUDIO_BASE_URL
											+ fileName;
								} else {
									Log.e("",
											"============Error while uploading Audio to server==========");
									Response = "";

								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}
					if (context instanceof ContactListSelection) {
						try {
							ContactListSelection contactListSelection = (ContactListSelection) context;
							if (contactListSelection.eyesayonWayDialog != null) {
								contactListSelection.eyesayonWayDialog
										.getHandlerObj().sendEmptyMessage(10);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (context instanceof GroupListSelection) {
						try {
							GroupListSelection groupListSelection = (GroupListSelection) context;
							if (groupListSelection.eyesayonWayDialog != null) {
								groupListSelection.eyesayonWayDialog
										.getHandlerObj().sendEmptyMessage(10);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (context instanceof TenFourRecordVoice) {
						try {
							TenFourRecordVoice tenFourRecordVoice = (TenFourRecordVoice) context;
							if (tenFourRecordVoice.eyesayonWayDialog != null) {
								tenFourRecordVoice.eyesayonWayDialog
										.getHandlerObj().sendEmptyMessage(10);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					else if (context instanceof EyesaySendText) {
						try {
							EyesaySendText eyesaysendtext = (EyesaySendText) context;
							// if (eyesaysendtext.eyesayonWayDialog != null) {
							// eyesaysendtext.eyesayonWayDialog
							// .getHandlerObj().sendEmptyMessage(10);
							// }
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					// if (!Response.equals("")
					// && CommonFunctions.getTag(Response, "status").equals(
					// "1")
					// && !CommonFunctions.getTag(Response, "file_url")
					// .equals("")) {

					if (true) {
						try {

							// FlurryAgent.logEvent("Message Sent to individual");
							// FlurryAgent.logEvent("Message Sent");
						} catch (Exception e) {
							// TODO: handle exception
						}

						// String FileURL = Response;
						// boolean uploadSucess=
						// CommonFunctions.uploadFileToAmazon(baseFileUrl,
						// "android"
						// + System.currentTimeMillis(),
						// "video/mp4");
						//
						// if (uploadSucess) {
						// String amazonUploadUrl =
						// CommonFunctions.getUploadedUrl();
						// if (amazonUploadUrl != null &&
						// amazonUploadUrl.length() >
						// 0) {
						// FileURL =
						// CommonFunctions.getTinyUrl(amazonUploadUrl);
						// Debugger.debugE("file Url " + FileURL);
						//
						// }
						// }
						String tempConversationId = "";
						if (converationId != null) {
							try {
								Log.e("", "The Convertion Id is "
										+ converationId);
							} catch (Exception e) {
								e.printStackTrace();
							}
							tempConversationId = converationId;

						} else {
							tempConversationId = "tfc" + new Date().getTime();
							Log.e("", "Else case Conversation id  "
									+ tempConversationId);
						}

						Response = CommonFunctions.postNamevalueDataOnServer(
								CommonFunctions.DEMO_SERVER_URL
										+ CommonFunctions.SEND_MESSAGE_SHAHBZ,
								message, Type, number, converationId,
								serverFileName, context, false,isBroadCastMessage);
						Log.e("", "The Response from the Server is " + Response);
						String success = "";
						String str_msg = "";

						/**
						 * {"error":false, "error_msg":"",
						 * "resp_msg":"Message Sent Successfully!",
						 * "conv_id":"NA=="}
						 */

						String serverConvId = "";
						try {
							JSONObject jsonObject = new JSONObject(Response);
							JSONObject responseObj = jsonObject
									.getJSONObject("response");

							success = responseObj.getString("error");
							str_msg = responseObj.getString("resp_msg");
							serverConvId = responseObj.getString("conv_id");
						} catch (Exception e) {
							e.printStackTrace();
						}
						// Log.e("Response:", Response);
						// if (!Response.equals("")
						// && CommonFunctions.getTag(Response, "status")
						// .equals("1") && Response != null) {
						if (!Response.equals("")
								&& success.equals("false")
								&& Response != null
								&& str_msg
										.equalsIgnoreCase("Message Sent Successfully!")) {
							// CommonFunctions.showAlert(Recorder.this,
							// "Message Sent Successfully", "10-4");

							// db = new DataBase(context);
							db.open();
							long d = System.currentTimeMillis();
							Debugger.debugE("Sender : " + sender
									+ " Receiver : " + number + "===Date :" + d);
							int sender_status = 1;
							try {
								message = android.util.Base64.encodeToString(
										message.getBytes(),
										android.util.Base64.DEFAULT);
							} catch (Exception e) {
								e.printStackTrace();
							}
							String senderName = "";
							try {
								senderName = CommonFunctions.getPref(context,
										Constants.FIRST_NAME, "")
										+ CommonFunctions.getPref(context,
												Constants.LAST_NAME, "");
							} catch (Exception e) {
								e.printStackTrace();
							}
							// Create a Loop Here
							try {
								// String name = "";

								if (reply) {

									Log.e("", "The Name of the User is "
											+ names);
									number = number.replace("-", "");
									try {
										number = CommonFunctions
												.sortStringArray(number
														+ ","
														+ CommonFunctions
																.getPref(
																		context,
																		CommonFunctions.PHONE_NUMBER,
																		""));

										Log.e("", "The Number of the User is "
												+ number);

									} catch (Exception e) {
										e.printStackTrace();
									}
									if (!isBroadCastMessage) {

										db.insert(
												DataBase.Receive_table,
												DataBase.Receive_int,
												new String[] {
														"0",
														CommonFunctions
																.getPref(
																		context,
																		CommonFunctions.PHONE_NUMBER,
																		""),
														names, Type, file_url,
														"" + d, "0", "0",
														converationId, "0",
														"" + sender_status,
														"0", "0", number,
														message, senderName });
										updateReadValue(context, converationId);
										ContentValues values = new ContentValues();
										values.put(DataBase.conversationId,
												converationId);
										values.put(DataBase.isEmergency, 0);
										values.put(DataBase.isRead, "1");

										db.insert_content(
												DataBase.emergency_table,
												DataBase.emergency_int, values);

									}
									// db.insert(
									// DataBase.Receive_table,
									// DataBase.Receive_int,
									// new String[] {
									// "0",
									// CommonFunctions
									// .getPref(
									// context,
									// CommonFunctions.PHONE_NUMBER,
									// ""),
									// names, Type, file_url,
									// "" + d, "0", "1",
									// converationId, "0",
									// "" + sender_status,
									// number, "0", "0",
									// message, "0",
									// formattedDate, "",
									// senderName });
								}

								else {

									if (!reply) {
										String tmp = db
												.fetchConversationID(names);

										if (tmp.equals("")) {

											converationId = serverConvId;
										} else
											converationId = serverConvId;
									}

									Log.e("", "The Name of the User is "
											+ names + "and Number is " + number);
									SimpleDateFormat sdf = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss");
									String formattedDate = sdf
											.format(new Date());
									Log.e("", "Formatted Date is ========== "
											+ formattedDate + "==========");

									if (!isBroadCastMessage) {
										// TODO Type changes from 1 to 0 near
										// conversation Id
										db.insert(
												DataBase.Receive_table,
												DataBase.Receive_int,
												new String[] {
														"0",
														CommonFunctions
																.getPref(
																		context,
																		CommonFunctions.PHONE_NUMBER,
																		""),
														names, Type, file_url,
														"" + d, "0", "0",
														converationId, "0",
														"" + sender_status,
														number, "0", "0",
														message, "0",
														formattedDate, "",
														senderName });

										updateReadValue(context, converationId);
										ContentValues values = new ContentValues();
										values.put(DataBase.conversationId,
												converationId);
										values.put(DataBase.isEmergency, 0);
										values.put(DataBase.isRead, "1");

										db.insert_content(
												DataBase.emergency_table,
												DataBase.emergency_int, values);

									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

							Toast.makeText(
									context,
									context.getString(R.string.msg_sent_successfully),
									Toast.LENGTH_LONG).show();
							db.close();
							return true;
						} else {
							// when no contact is selected and user pressed send
							// message button ... . .. . . . . . . . .. . . . .
							// .. .
							// . . . . .

							AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(
									context);

							// set title
							alertDialogBuilder1.setTitle("Tribewire");

							// set dialog message
							alertDialogBuilder1
									.setMessage(
											"Server Error , Please try after some time")
									.setCancelable(false)
									.setPositiveButton(
											"ok",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													// if this button is
													// clicked,
													// close
													// current activity

													dialog.cancel();
												}
											});

							// create alert dialog
							AlertDialog alertDialog1 = alertDialogBuilder1
									.create();

							// show it
							alertDialog1.show();
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
					Debugger.debugE("Exception " + e.getMessage());

				} finally {
					try {
						db.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			CommonFunctions
					.writeLog("Error in CommonFunctions Send message when Seding single message "
							+ e.getMessage());
		}
		return false;
	}

	private static void updateReadValue(Context context, String convId) {
		try {
			List<String> nameList = new ArrayList<String>();
			nameList.add("api_key");
			nameList.add("conv_id");
			List<String> valueList = new ArrayList<String>();
			valueList.add(Constants.API_KEY);
			valueList.add(convId);
			// new RestApiCall(
			// ConversationMessages.this,
			// ,
			// nameList, valueList).start();
			//
			CommonFunctions.postNameValueDataOnServer(context, nameList.size(),
					nameList, valueList, CommonFunctions.DEMO_SERVER_URL
							+ CommonFunctions.READ_CONV);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static JSONObject SendFailedTextMessage(Context context,
			boolean flag, String baseFileUrl, String number, String names,
			boolean reply, String converationId, String message,
			String messageId) {
		// String Response = "";
		try {

			Log.e("", "The Base File Url Path is " + baseFileUrl);
			String Type = "2";
			String serverFileName = "";
			if (!flag) {
				Type = "2";
			}

			try {

				message = message.replace("\n", "");

				Log.e("", "=====================  Base 64 Encoded message "
						+ message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String sender = CommonFunctions.getPref(context,
					CommonFunctions.PHONE_NUMBER, "");
			Log.e("", "The Sender Name is " + sender);
			if (CheckNetConnectivity(context)) {

				DataBase db = new DataBase(context);

				// db.open();
				// Object[] params= new Object[2];
				// params[0]= baseFileUrl;
				// params[1]= context;
				// new CommonFunctions().new TempAsyn().execute(params);

				try {
					String file_url = "(null)";
					String Response = "";

					// files.SendRecord(baseFileUrl, context);

					if (baseFileUrl != null
							&& baseFileUrl.length() > 0
							&& (baseFileUrl.endsWith(".jpeg")
									|| baseFileUrl.toLowerCase().endsWith(
											".jpg") || baseFileUrl
									.toLowerCase().endsWith(".png"))) {

						UploadFiles files = new UploadFiles();
						String Response1 = files.SendRecord(baseFileUrl,
								context);

						if (Response1 != null && Response1.length() > 0) {

							try {
								JSONObject jsonObject = new JSONObject(
										Response1);
								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {

									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Video file Name is "
													+ fileName
													+ "==============");
									Type = "2";
									Response = Constants.IMAGE_BASE_URL
											+ fileName;
									file_url = Constants.IMAGE_BASE_URL
											+ fileName;
									try {
										String extension = fileName.substring(
												fileName.indexOf("."),
												fileName.length());

										fileName = fileName.substring(0,
												fileName.indexOf("."));
										fileName = fileName + "_thumb"
												+ extension;
									} catch (Exception e) {
										e.printStackTrace();
									}

								} else {
									Log.e("",
											"============Error while uploading Video to server==========");
									Response = "";
									Type = "2";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					} else if (baseFileUrl != null
							&& baseFileUrl.length() > 0
							&& (baseFileUrl.endsWith(".mp4")
									|| baseFileUrl.endsWith(".mov") || baseFileUrl
										.endsWith(".3gpp"))) {
						UploadFiles files1 = new UploadFiles();
						String Response2 = files1.SendRecord(baseFileUrl,
								context);

						if (Response2 != null && Response2.length() > 0) {

							try {
								JSONObject jsonObject = new JSONObject(
										Response2);
								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {

									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Video file Name is "
													+ fileName
													+ "==============");
									Type = "3";
									Response = Constants.VIDEO_BASE_URL
											+ fileName;
									file_url = Constants.VIDEO_BASE_URL
											+ fileName;
								} else {
									Log.e("",
											"============Error while uploading Video to server==========");
									Response = "";
									Type = "2";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}

					else if (baseFileUrl != null && baseFileUrl.length() > 0
							&& baseFileUrl.endsWith(".wav")) {
						UploadFiles files2 = new UploadFiles();
						String Response3 = files2.SendRecord(baseFileUrl,
								context);

						if (Response3 != null && Response3.length() > 0) {

							try {
								JSONObject jsonObject = new JSONObject(
										Response3);
								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {
									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Audio file Name is "
													+ fileName
													+ "==============");
									Type = "4";
									Response = Constants.AUDIO_BASE_URL
											+ fileName;
									file_url = Constants.AUDIO_BASE_URL
											+ fileName;
								} else {
									Log.e("",
											"============Error while uploading Audio to server==========");
									Response = "";
									Type = "2";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}
					// if (context instanceof TenFourConversations) {
					// try {
					// TenFourConversations contactListSelection =
					// (TenFourConversations) context;
					// if (contactListSelection.eyesayonWayDialog != null) {
					// contactListSelection.eyesayonWayDialog
					// .getHandlerObj().sendEmptyMessage(10);
					// }
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					// }

					else if (context instanceof GroupListSelection) {
						try {
							GroupListSelection groupListSelection = (GroupListSelection) context;
							if (groupListSelection.eyesayonWayDialog != null) {
								groupListSelection.eyesayonWayDialog
										.getHandlerObj().sendEmptyMessage(10);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (context instanceof TenFourRecordVoice) {
						try {
							TenFourRecordVoice tenFourRecordVoice = (TenFourRecordVoice) context;
							if (tenFourRecordVoice.eyesayonWayDialog != null) {
								tenFourRecordVoice.eyesayonWayDialog
										.getHandlerObj().sendEmptyMessage(10);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					else if (context instanceof EyesaySendText) {
						try {
							EyesaySendText eyesaysendtext = (EyesaySendText) context;
							// if (eyesaysendtext.eyesayonWayDialog != null) {
							// eyesaysendtext.eyesayonWayDialog
							// .getHandlerObj().sendEmptyMessage(10);
							// }
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					if (true) {
						try {
							//
							// FlurryAgent.logEvent("Message Sent to individual");
							// FlurryAgent.logEvent("Message Sent");
						} catch (Exception e) {
							// TODO: handle exception
						}

						String tempConversationId = "";
						if (converationId != null) {
							try {
								Log.e("", "The Convertion Id is "
										+ converationId);
							} catch (Exception e) {
								e.printStackTrace();
							}
							tempConversationId = converationId;

						} else {
							tempConversationId = "tfc" + new Date().getTime();
							Log.e("", "Else case Conversation id  "
									+ tempConversationId);
						}

						Response = CommonFunctions.postNamevalueDataOnServer(
								CommonFunctions.DEMO_SERVER_URL
										+ CommonFunctions.SEND_MESSAGE_SHAHBZ,
								message, Type, number, converationId,
								serverFileName, context, false,false);
						Log.e("", "The Response from the Server is " + Response);
						String success = "";
						String str_msg = "";

						/**
						 * {"error":false, "error_msg":"",
						 * "resp_msg":"Message Sent Successfully!",
						 * "conv_id":"NA=="}
						 */

						String serverConvId = "";
						try {
							JSONObject jsonObject = new JSONObject(Response);
							success = jsonObject.getString("error");
							str_msg = jsonObject.getString("resp_msg");
							serverConvId = jsonObject.getString("conv_id");
						} catch (Exception e) {
							e.printStackTrace();
						}
						// Log.e("Response:", Response);
						// if (!Response.equals("")
						// && CommonFunctions.getTag(Response, "status")
						// .equals("1") && Response != null) {
						if (!Response.equals("")
								&& success.equals("false")
								&& Response != null
								&& str_msg
										.equalsIgnoreCase("Message Sent Successfully!")) {
							// CommonFunctions.showAlert(Recorder.this,
							// "Message Sent Successfully", "10-4");

							// db = new DataBase(context);
							db.open();
							long d = System.currentTimeMillis();
							Debugger.debugE("Sender : " + sender
									+ " Receiver : " + number + "===Date :" + d);
							int sender_status = 1;
							try {
								message = android.util.Base64.encodeToString(
										message.getBytes(),
										android.util.Base64.DEFAULT);
							} catch (Exception e) {
								e.printStackTrace();
							}
							// Create a Loop Here
							try {
								// String name = "";

								if (reply) {
								}

								else {

									if (!reply) {
										String tmp = db
												.fetchConversationID(names);

										if (tmp.equals("")) {

											converationId = serverConvId;
										} else
											converationId = serverConvId;
									}

									Log.e("", "The Name of the User is "
											+ names + "and Number is " + number);
									// db.insert(
									// DataBase.Receive_table,
									// DataBase.Receive_int,
									// new String[] {
									// "0",
									// CommonFunctions
									// .getPref(
									// context,
									// CommonFunctions.PHONE_NUMBER,
									// ""),
									// names, Type, file_url,
									// "" + d, "0", "1",
									// converationId, "0",
									// "" + sender_status,
									// number, "0", "0",
									// message });
									ContentValues cv = new ContentValues();
									cv.put("file_url", file_url);
									cv.put("isdraft", "0");
									if (converationId != null
											&& converationId.length() > 0) {
										if (converationId.startsWith("tfc")) {
											cv.put("conversation_Id",
													serverConvId);
										}
									}
									db.update(DataBase.Receive_table,
											DataBase.Receive_int, "_id = "
													+ messageId, cv);

								}

							} catch (Exception e) {
								e.printStackTrace();
							}

							// Toast.makeText(
							// context,
							// context.getString(R.string.msg_sent_successfully),
							// Toast.LENGTH_LONG).show();
							db.close();

							JSONObject jsonObject = null;
							try {
								jsonObject = new JSONObject();
								jsonObject.put("file_url", file_url);
								jsonObject.put("isdraft", "0");
								jsonObject.put("conversation_Id", serverConvId);
								jsonObject.put("messagetype", Type);

							} catch (Exception e) {
								e.printStackTrace();
							}

							return jsonObject;
						} else {
							// when no contact is selected and user pressed send
							// message button ... . .. . . . . . . . .. . . . .
							// .. .
							// . . . . .

							/*
							 * AlertDialog.Builder alertDialogBuilder1 = new
							 * AlertDialog.Builder( context);
							 * 
							 * // set title
							 * alertDialogBuilder1.setTitle("EyeSay");
							 * 
							 * // set dialog message alertDialogBuilder1
							 * .setMessage(
							 * "No contact is selected, first select the contacts!"
							 * ) .setCancelable(false) .setPositiveButton( "ok",
							 * new DialogInterface.OnClickListener() { public
							 * void onClick( DialogInterface dialog, int id) {
							 * // if this button is // clicked, // close //
							 * current activity
							 * 
							 * dialog.cancel(); } });
							 * 
							 * // create alert dialog AlertDialog alertDialog1 =
							 * alertDialogBuilder1 .create();
							 * 
							 * // show it alertDialog1.show();
							 */
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
					Debugger.debugE("Exception " + e.getMessage());

				} finally {
					try {
						db.close();
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			CommonFunctions
					.writeLog("Error in CommonFunctions Send message when Seding single message "
							+ e.getMessage());
		}
		return null;
	}

	public static boolean SendChatTextMessage(Context context, boolean flag,
			String baseFileUrl, String number, String names, boolean reply,
			String converationId, String message, boolean isInsertDB) {
		// String Response = "";
		try {

			Log.e("", "The Base File Url Path is " + baseFileUrl);
			String Type = "0";
			String serverFileName = "";
			if (!flag) {
				Type = "0";
				// Toast.makeText(context, "flag is false ",
				// Toast.LENGTH_LONG).show();
			}

			try {
				// message = android.util.Base64.encodeToString(
				// message.getBytes(), android.util.Base64.DEFAULT);
				message = message.replace("\n", "");
				// message = Base64Coder.encodeString(message);
				Log.e("", "=====================  Base 64 Encoded message "
						+ message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String sender = CommonFunctions.getPref(context,
					CommonFunctions.PHONE_NUMBER, "");
			Log.e("", "The Sender Name is " + sender);
			if (CheckNetConnectivity(context)) {

				DataBase db = new DataBase(context);

				// db.open();
				// Object[] params= new Object[2];
				// params[0]= baseFileUrl;
				// params[1]= context;
				// new CommonFunctions().new TempAsyn().execute(params);

				try {
					String file_url = "(null)";
					String Response = "";

					// files.SendRecord(baseFileUrl, context);

					// TYPE = 3 // Its an image
					// TYPE = 0 // Its an text message only
					// TYPE = 2 // Its an Audio File
					if (baseFileUrl != null
							&& baseFileUrl.length() > 0
							&& (baseFileUrl.endsWith(".jpeg")
									|| baseFileUrl.toLowerCase().endsWith(
											".jpg") || baseFileUrl
									.toLowerCase().endsWith(".png"))) {

						UploadFiles files = new UploadFiles();
						String Response1 = files.SendRecord(baseFileUrl,
								context);

						if (Response1 != null && Response1.length() > 0) {

							try {
								JSONObject jsonObject1 = new JSONObject(
										Response1);
								JSONObject jsonObject = jsonObject1
										.getJSONObject("response");

								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {

									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Video file Name is "
													+ fileName
													+ "==============");
									Type = "3";
									Response = Constants.IMAGE_BASE_URL
											+ fileName;
									file_url = Constants.IMAGE_BASE_URL
											+ fileName;
									try {
										String extension = fileName.substring(
												fileName.indexOf("."),
												fileName.length());

										fileName = fileName.substring(0,
												fileName.indexOf("."));
										fileName = fileName + "_thumb"
												+ extension;
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									Log.e("",
											"============Error while uploading Video to server==========");
									Response = "";
									Type = "0";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					} else if (baseFileUrl != null
							&& baseFileUrl.length() > 0
							&& (baseFileUrl.endsWith(".mp4")
									|| baseFileUrl.endsWith(".mov") || baseFileUrl
										.endsWith(".3gpp"))) {
						UploadFiles files1 = new UploadFiles();
						String Response2 = files1.SendRecord(baseFileUrl,
								context);

						if (Response2 != null && Response2.length() > 0) {

							try {
								JSONObject jsonObject = new JSONObject(
										Response2);
								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {

									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Video file Name is "
													+ fileName
													+ "==============");
									Type = "4";
									Response = Constants.VIDEO_BASE_URL
											+ fileName;
									file_url = Constants.VIDEO_BASE_URL
											+ fileName;
								} else {
									Log.e("",
											"============Error while uploading Video to server==========");
									Response = "";

								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}

					else if (baseFileUrl != null && baseFileUrl.length() > 0
							&& baseFileUrl.endsWith(".wav")) {
						UploadFiles files2 = new UploadFiles();
						String Response3 = files2.SendRecord(baseFileUrl,
								context);

						if (Response3 != null && Response3.length() > 0) {

							try {
								JSONObject jsonObject1 = new JSONObject(
										Response3);
								JSONObject jsonObject = jsonObject1
										.getJSONObject("response");
								String error = jsonObject.getString("error");
								if (error.equalsIgnoreCase("false")) {

									String fileName = jsonObject
											.getString("file_name");
									serverFileName = fileName;
									Log.e("",
											"======== The Audio file Name is "
													+ fileName
													+ "==============");
									Type = "2";
									Response = Constants.AUDIO_BASE_URL
											+ fileName;
									file_url = Constants.AUDIO_BASE_URL
											+ fileName;
								} else {
									Log.e("",
											"============Error while uploading Audio to server==========");
									Response = "";
									Type = "0";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					}
					// if (context instanceof TenFourConversations) {
					// try {
					// TenFourConversations contactListSelection =
					// (TenFourConversations) context;
					// if (contactListSelection.eyesayonWayDialog != null) {
					// contactListSelection.eyesayonWayDialog
					// .getHandlerObj().sendEmptyMessage(10);
					// }
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					// }
					else if (context instanceof GroupListSelection) {
						try {
							GroupListSelection groupListSelection = (GroupListSelection) context;
							if (groupListSelection.eyesayonWayDialog != null) {
								groupListSelection.eyesayonWayDialog
										.getHandlerObj().sendEmptyMessage(10);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (context instanceof TenFourRecordVoice) {
						try {
							TenFourRecordVoice tenFourRecordVoice = (TenFourRecordVoice) context;
							if (tenFourRecordVoice.eyesayonWayDialog != null) {
								tenFourRecordVoice.eyesayonWayDialog
										.getHandlerObj().sendEmptyMessage(10);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					else if (context instanceof EyesaySendText) {
						try {
							EyesaySendText eyesaysendtext = (EyesaySendText) context;
							// if (eyesaysendtext.eyesayonWayDialog != null) {
							// eyesaysendtext.eyesayonWayDialog
							// .getHandlerObj().sendEmptyMessage(10);
							// }
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					// if (!Response.equals("")
					// && CommonFunctions.getTag(Response, "status").equals(
					// "1")
					// && !CommonFunctions.getTag(Response, "file_url")
					// .equals("")) {

					if (true) {
						try {

							// FlurryAgent.logEvent("Message Sent to individual");
							// FlurryAgent.logEvent("Message Sent");
						} catch (Exception e) {
							// TODO: handle exception
						}

						// String FileURL = Response;
						// boolean uploadSucess=
						// CommonFunctions.uploadFileToAmazon(baseFileUrl,
						// "android"
						// + System.currentTimeMillis(),
						// "video/mp4");
						//
						// if (uploadSucess) {
						// String amazonUploadUrl =
						// CommonFunctions.getUploadedUrl();
						// if (amazonUploadUrl != null &&
						// amazonUploadUrl.length() >
						// 0) {
						// FileURL =
						// CommonFunctions.getTinyUrl(amazonUploadUrl);
						// Debugger.debugE("file Url " + FileURL);
						//
						// }
						// }
						String tempConversationId = "";
						if (converationId != null) {
							try {
								Log.e("", "The Convertion Id is "
										+ converationId);
							} catch (Exception e) {
								e.printStackTrace();
							}
							tempConversationId = converationId;

						} else {
							tempConversationId = "tfc" + new Date().getTime();
							Log.e("", "Else case Conversation id  "
									+ tempConversationId);
						}

						Response = CommonFunctions.postNamevalueDataOnServer(
								CommonFunctions.DEMO_SERVER_URL
										+ CommonFunctions.SEND_MESSAGE_SHAHBZ,
								message, Type, number, converationId,
								serverFileName, context, false,false);
						Log.e("", "The Response from the Server is " + Response);
						String success = "";
						String str_msg = "";

						/**
						 * {"error":false, "error_msg":"",
						 * "resp_msg":"Message Sent Successfully!",
						 * "conv_id":"NA=="}
						 */

						Log.e("", "============== The response from server "
								+ Response);
						String serverConvId = "";
						try {
							JSONObject jsonObject1 = new JSONObject(Response);
							JSONObject jsonObject = jsonObject1
									.getJSONObject("response");
							success = jsonObject.getString("error");
							str_msg = jsonObject.getString("resp_msg");
							serverConvId = jsonObject.getString("conv_id");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// if (!Response.equals("")
						// && success.equals("false")
						// && Response != null
						// && str_msg
						// .equalsIgnoreCase("Message Sent Successfully!")) {
						//
						if (!Response.equals("") && success.equals("false")
								&& Response != null) {

							db.open();
							long d = System.currentTimeMillis();
							Debugger.debugE("Sender : " + sender
									+ " Receiver : " + number + "===Date :" + d);
							int sender_status = 1;
							try {
								message = android.util.Base64.encodeToString(
										message.getBytes(),
										android.util.Base64.DEFAULT);
							} catch (Exception e) {
								e.printStackTrace();
							}
							// Create a Loop Here
							try {
								// String name = "";
								String senderName = "";
								try {
									senderName = CommonFunctions.getPref(
											context, Constants.FIRST_NAME, "")
											+ " "
											+ CommonFunctions.getPref(context,
													Constants.LAST_NAME, "");
								} catch (Exception e) {
									// TODO: handle exception
								}

								if (reply) {

									Log.e("", "The Name of the User is "
											+ names);
									number = number.replace("-", "");
									try {
										number = CommonFunctions
												.sortStringArray(number
														+ ","
														+ CommonFunctions
																.getPref(
																		context,
																		CommonFunctions.PHONE_NUMBER,
																		""));

										Log.e("", "The Number of the User is "
												+ number);

									} catch (Exception e) {
										e.printStackTrace();
									}

									String[] string = new String[] {
											"0",
											CommonFunctions
													.getPref(
															context,
															CommonFunctions.PHONE_NUMBER,
															""), names, Type,
											file_url, "" + d, "0", "0",
											converationId, "0",
											"" + sender_status, "0", "0",
											number, message, "", "", "", "",
											"" + senderName };
									if (isInsertDB) {
										// Means if reply is from Company FYi
										// true then no need to insert message
										// to databse
										// as server will handle this concept
										if (!reply) {
											db.insert(DataBase.Receive_table,
													DataBase.Receive_int,
													string);
										}
									}

									Log.e("",
											"=========== String Array Length is ==========="
													+ string.length);

								}

								else {

									if (!reply) {
										String tmp = db
												.fetchConversationID(names);

										if (tmp.equals("")) {

											converationId = serverConvId;
										} else
											converationId = serverConvId;
									}

									Log.e("", "The Name of the User is "
											+ names + "and Number is " + number);
									SimpleDateFormat sdf = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss");
									String formattedDate = sdf
											.format(new Date());
									if (isInsertDB) {
										if (!reply) {
											db.insert(
													DataBase.Receive_table,
													DataBase.Receive_int,
													new String[] {
															"0",
															CommonFunctions
																	.getPref(
																			context,
																			CommonFunctions.PHONE_NUMBER,
																			""),
															names, Type,
															file_url, "" + d,
															"0", "0",
															converationId, "0",
															"" + sender_status,
															number, "0", "0",
															message, "0",
															formattedDate, "",
															senderName });
										}

									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

							db.close();
							return true;
						} else {
							// when no contact is selected and user pressed send
							// message button ... . .. . . . . . . . .. . . . .
							// .. .
							// . . . . .

							AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(
									context);

							// set title
							alertDialogBuilder1.setTitle("Tribewire");

							// set dialog message
							alertDialogBuilder1
									.setMessage(
											"Server error while sending message , Please try again later")
									.setCancelable(false)
									.setPositiveButton(
											"ok",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													// if this button is
													// clicked,
													// close
													// current activity

													dialog.cancel();
												}
											});

							// create alert dialog
							AlertDialog alertDialog1 = alertDialogBuilder1
									.create();

							// show it
							alertDialog1.show();
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
					Debugger.debugE("Exception " + e.getMessage());

				} finally {
					try {
						db.close();
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			CommonFunctions
					.writeLog("Error in CommonFunctions Send message when Seding single message "
							+ e.getMessage());
		}
		return false;
	}

	public static boolean SendMessage(Context context, boolean flag,
			String baseFileUrl, String number, String names, boolean reply,
			String converationId) {
		return false;
	}

	public static void setFont(Context c, TextView tv, boolean isBold,
			boolean isItalic) {
		try {
			if (tv != null) {
				tv.setTypeface(getTypeFace(c, isBold, isItalic));
			}

		} catch (Exception e) {
		}
	}

	public static void setDigitalFont(Context c, TextView tv) {
		try {
			if (tv != null) {
				tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
						"fonts/LEDFont.ttf"));
			}

		} catch (Exception e) {
		}
	}

	private static Typeface getTypeFace(Context c, boolean isBold,
			boolean isItalic) {
		if (isBold)
			return Typeface.createFromAsset(c.getAssets(),
					"fonts/helveticaBold.ttf");
		if (isItalic)
			return Typeface.createFromAsset(c.getAssets(),
					"fonts/helveticaItalic.ttf");
		return Typeface.createFromAsset(c.getAssets(),
				"fonts/helveticaNormal.ttf");

	}

	public static void setFont(Context c, EditText tv, boolean isBold,
			boolean isItalic) {

		try {
			if (tv != null) {
				tv.setTypeface(getTypeFace(c, isBold, isItalic));
			}
		} catch (Exception e) {
		}
	}

	public static void setFont(Context c, MultiAutoCompleteTextView tv,
			boolean isBold, boolean isItalic) {
		try {
			if (tv != null) {
				tv.setTypeface(getTypeFace(c, isBold, isItalic));
			}
		} catch (Exception e) {
		}
	}

	public static boolean downloadFile(String fileURL, String targetFileURL) {
		Debugger.debugE(" downloadFile url============= Normal" + fileURL);
		try {
			CommonFunctions.getBaseURL();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			URL url = null;
			HttpURLConnection con = null;
			try {
				url = new URL(fileURL);
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setDoOutput(false);
				con.setDoInput(true);
				con.setConnectTimeout(10000);
				con.connect();
			} catch (Exception e) {
				Debugger.debugE(" PostDataOnServer" + e.toString());
				return false;
			}
			targetFileURL = targetFileURL.replace("%20", " ");
			targetFileURL = targetFileURL.replace("%26", "&");

			File f = new File(targetFileURL);
			Log.e("", "File Created ================== " + f.createNewFile());
			FileOutputStream fos = new FileOutputStream(f);
			BufferedInputStream in = new BufferedInputStream(
					con.getInputStream());

			int len = 0;
			byte[] buff = new byte[1024];

			int done = 0;

			while ((len = in.read(buff)) > 0) {
				done += len;
				fos.write(buff, 0, len);
			}
			in.close();
			fos.close();
			return true;
		} catch (SocketTimeoutException e) {
			Debugger.debugE(" downloadFile 1" + e.toString());

		} catch (Exception e) {
			Debugger.debugE(" downloadFile 2" + e.toString());
		}
		return false;
	}

	public static boolean downloadFileFromAmazon(String fileURL,
			String targetFileURL) {
		Debugger.debugE(" downloadFile url Amazon=======" + fileURL);
		try {
			CommonFunctions.getBaseURL();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			targetFileURL = targetFileURL.replace("%20", " ");
			targetFileURL = targetFileURL.replace("%26", "&");

			File f = new File(targetFileURL);
			Log.e("", "File Created ================== " + f.createNewFile());
			FileOutputStream fos = new FileOutputStream(f);

			int len = 0;
			byte[] buff = new byte[1024];

			int done = 0;
			// http://ten-foure-large.s3.amazonaws.com/1360733979-iphone.mp4

			AmazonS3Client s3Client = new AmazonS3Client(
					new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_KEY));
			S3Object s3Object = s3Client.getObject(
					CommonFunctions.getMediaBucket(),
					fileURL.substring(fileURL.lastIndexOf('/') + 1));
			// S3Object s3Object=
			// s3Client.getObject(CommonFunctions.getMediaBucket(),
			// "1360733979-iphone.mp4");
			Log.e("", "The Object Cache Control is "
					+ s3Object.getObjectMetadata().getCacheControl());
			Log.e("", "The Object Content Disposition is "
					+ s3Object.getObjectMetadata().getContentDisposition());
			Log.e("", "The Object Content Encoding is "
					+ s3Object.getObjectMetadata().getContentEncoding());
			Log.e("", "The Object Content Length is "
					+ s3Object.getObjectMetadata().getContentLength());
			Log.e("", "The Object Content MD5 is "
					+ s3Object.getObjectMetadata().getContentMD5());
			Log.e("", "The Object Content Type is "
					+ s3Object.getObjectMetadata().getContentType());
			Log.e("", "The Object Content ETag is "
					+ s3Object.getObjectMetadata().getETag());
			Log.e("", "The Object Content Expiration Time RuleId is "
					+ s3Object.getObjectMetadata().getExpirationTimeRuleId());
			Log.e("",
					"******************* RAW META DATA STARTS*****************************************");

			Map<String, Object> metaDataMap = s3Object.getObjectMetadata()
					.getRawMetadata();
			for (Map.Entry<String, Object> entry : metaDataMap.entrySet()) {
				Log.e("", "Entry Key  ====" + entry.getKey() + "/ Entry Value "
						+ entry.getValue());
				Log.e("",
						"===================================================================");
			}
			Log.e("",
					"********************RAW META DATA  ENDS**************************************");

			Log.e("",
					"********************USER META DATA STARTS**************************************");
			Map<String, String> userDataMap = s3Object.getObjectMetadata()
					.getUserMetadata();
			for (Map.Entry<String, String> entry : userDataMap.entrySet()) {
				Log.e("", "Entry Key  ====" + entry.getKey() + "/ Entry Value "
						+ entry.getValue());
				Log.e("",
						"====================USER META DATA ENDS=========================================");
			}

			S3ObjectInputStream objectInputStream = s3Client.getObject(
					CommonFunctions.getMediaBucket(),
					fileURL.substring(fileURL.lastIndexOf('/') + 1))
					.getObjectContent();
			// S3ObjectInputStream objectInputStream=
			// s3Client.getObject(CommonFunctions.getMediaBucket(),
			// "1360733979-iphone.mp4").getObjectContent();
			while ((len = objectInputStream.read(buff)) > 0) {
				done += len;
				fos.write(buff, 0, len);
			}
			objectInputStream.close();
			fos.close();
			return true;
		} catch (SocketTimeoutException e) {
			Debugger.debugE(" downloadFile 1" + e.toString());

		} catch (Exception e) {
			Debugger.debugE(" downloadFile 2" + e.toString());
		}
		return false;
	}

	public static String getAudioURL() throws IOException {
		String ePath = "";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			ePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			if (!ePath.endsWith("/"))
				ePath = ePath + "/CRecord/MyAudio/";

		}
		return ePath;
	}

	public static String getImageRL() throws IOException {
		String ePath = "";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			ePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			if (!ePath.endsWith("/"))
				ePath = ePath + "/CRecord/MyImage/";

		}
		return ePath;
	}

	public static String getVideoURL() throws IOException {
		String ePath = "";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			ePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			if (!ePath.endsWith("/"))
				ePath = ePath + "/CRecord/MyVideo/";

		}
		return ePath;
	}

	public static String getDownloadURL() throws IOException {
		String ePath = "";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			ePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			if (!ePath.endsWith("/"))
				ePath = ePath + "/CRecord/eyesaydownload/";

		}
		return ePath;
	}

	public static String getmonth(int m) {
		switch (m) {
		case 0:
			return "JAN";
		case 1:
			return "FEB";
		case 2:
			return "MAR";
		case 3:
			return "APR";
		case 4:
			return "MAY";
		case 5:
			return "JUN";
		case 6:
			return "JUL";
		case 7:
			return "AUG";
		case 8:
			return "SEP";
		case 9:
			return "OCT";
		case 10:
			return "NOV";
		case 11:
			return "DEC";
		}
		return null;
	}

	public static String getmonthInt(int m) {
		switch (m) {
		case 0:
			return "1";
		case 1:
			return "2";
		case 2:
			return "3";
		case 3:
			return "4";
		case 4:
			return "5";
		case 5:
			return "6";
		case 6:
			return "7";
		case 7:
			return "8";
		case 8:
			return "9";
		case 9:
			return "10";
		case 10:
			return "11";
		case 11:
			return "12";
		}
		return null;
	}

	public static String getdate(Long d) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(d);
		int hour = c.get(Calendar.HOUR);
		String ampm = "AM";
		if (c.get(Calendar.AM_PM) == Calendar.PM)
			ampm = "PM";

		if (hour == 0 && ampm.equals("PM"))
			hour = 12;

		String day = "" + c.get(Calendar.DAY_OF_MONTH);// time.getDate();
		if (Integer.parseInt(day) < 10)
			day = "0" + day;

		String min = "" + c.get(Calendar.MINUTE);
		if (Integer.parseInt(min) < 10)
			min = "0" + min;

		String temp = " " + getmonth(c.get(Calendar.MONTH)) + " " + day + " "
				+ c.get(Calendar.YEAR) + " "
				+ ((hour > 9) ? hour : ("0" + hour)) + ":" + min + " " + ampm;

		if (c.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(
				Calendar.DAY_OF_MONTH)
				&& c.get(Calendar.MONTH) == Calendar.getInstance().get(
						Calendar.MONTH))
			temp = "Today " + ((hour > 9) ? hour : ("0" + hour)) + ":" + min
					+ " " + ampm;

		return temp;
	}

	public static String getdateAno(Long d) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(d);

		Calendar myCalnder = Calendar.getInstance();
		myCalnder.setTimeInMillis(System.currentTimeMillis());
		try {
			if (c.get(Calendar.YEAR) == myCalnder.get(Calendar.YEAR)) {
				// Date serverDate = new Date(d);
				Date myDate = new Date(System.currentTimeMillis());
				long diff = 0;
				try {
					// dateObj= formater1.parse(date);
					diff = ((myDate.getTime() - d) / (86400000));
				} catch (Exception e) {
				}
				String days = "Year Ago";
				Log.e("", "========================== The difference  is "
						+ diff);
				if (diff == 0) {

					int hour = c.get(Calendar.HOUR);
					int minute = c.get(Calendar.MINUTE);
					String ampm = "AM";
					if (c.get(Calendar.AM_PM) == Calendar.PM)
						ampm = "PM";

					if ((ampm.equalsIgnoreCase("PM") || ampm
							.equalsIgnoreCase("AM")) && hour == 0) {
						hour = 12;
					}
					Log.e("", "========================== The difference  0 "
							+ hour + ":" + minute + " " + ampm);
					String convertedMiniute = "" + minute;
					if (convertedMiniute.length() == 1) {
						convertedMiniute = "0" + minute;
					}

					Log.e("",
							"========= Server Data " + c.get(Calendar.DATE)
									+ " ====  Local Date"
									+ myCalnder.get(Calendar.DATE));
					if ((c.get(Calendar.DATE) == myCalnder.get(Calendar.DATE))) {
						return days = hour + ":" + convertedMiniute + " "
								+ ampm;

					} else {
						return days = "Yesterday " + hour + ":"
								+ convertedMiniute + " " + ampm;
					}

				} else if (diff == 1) {
					int hour = c.get(Calendar.HOUR);
					int minute = c.get(Calendar.MINUTE);
					String ampm = "AM";
					if (c.get(Calendar.AM_PM) == Calendar.PM)
						ampm = "PM";
					Log.e("", "========================== The difference  1 "
							+ hour + ":" + minute + " " + ampm);
					String convertedMiniute = "" + minute;
					if (convertedMiniute.length() == 1) {
						convertedMiniute = "0" + minute;
					}
					if (ampm.equalsIgnoreCase("PM") && hour == 0) {
						hour = 12;
					}
					return days = "Yesterday at " + hour + ":"
							+ convertedMiniute + " " + ampm;

				} else if (diff > 1) {
					String month = getmonth(c.get(Calendar.MONTH));
					int date = c.get(Calendar.DATE);
					// return days = String.valueOf(diff) + " " +"DAYS AGO";
					Log.e("", "========================== The difference  > 1 "
							+ month + " " + date);
					return days = month + " " + date;
				} else {
					return days;
				}

			} else {

				int MM = c.get(Calendar.MONTH) + 1;
				int DD = c.get(Calendar.DATE);
				int YYYY = c.get(Calendar.YEAR);

				return "" + MM + " " + DD + " " + YYYY;
			}

			// long currentMillis = System.currentTimeMillis();
			// long requiredMillis = currentMillis - d;
			// long seconds = requiredMillis / 1000;
			//
			// int serverDate = c.get(Calendar.DATE);
			// int myDate = myCalender.get(Calendar.DATE);
			// int serverMonth = c.get(Calendar.MONTH);
			// int myMonth = myCalender.get(Calendar.MONTH);
			//
			// if (myDate == serverDate) {
			//
			// return "Today";
			// } else if (myDate > serverDate && ((myDate - serverDate)) == 1) {
			// return " Yesterday";
			// } else if (myDate > serverDate && ((myDate - serverDate)) == 2) {
			// return " Third Day ";
			// } else {
			// return "Year";
			// }

			// if (seconds < 60) {
			//
			// return "few seconds ago";
			// }
			// if (seconds > 60 && seconds < 3600) {
			//
			// long minutes = seconds / 60;
			// if (minutes > 1) {
			//
			// return minutes + " minutes ago ";
			//
			// } else {
			//
			// return minutes + " minute ago ";
			//
			// }
			// }
			// long fiveHoursSecond = 5 * 3600;
			//
			// if (seconds >= 3600 && seconds <= fiveHoursSecond) {
			//
			// long hours = seconds / 3600;
			//
			// return hours + " hour ago";
			//
			// }
			/*
			 * long onedaySeconds = 24 * 60 * 60; if (seconds >= fiveHoursSecond
			 * && seconds <= onedaySeconds) { int hour = c.get(Calendar.HOUR);
			 * String ampm = "AM"; if (c.get(Calendar.AM_PM) == Calendar.PM)
			 * ampm = "PM";
			 * 
			 * // if (hour == 0 && ampm.equals("PM")) // hour = 12; String min =
			 * "" + c.get(Calendar.MINUTE); // if (Integer.parseInt(min) < 10)
			 * // min = "0" + min;
			 * 
			 * //return " Today at " + hour + ":" + min + ampm; return "" + hour
			 * + ":" + min + ampm; } long sevendaySeconds = 7 * 24 * 60 * 60; if
			 * (seconds >= onedaySeconds && seconds <= sevendaySeconds) { int
			 * hour = c.get(Calendar.HOUR); String ampm = "am"; if
			 * (c.get(Calendar.AM_PM) == Calendar.PM) ampm = "pm"; String min =
			 * "" + c.get(Calendar.MINUTE);
			 * 
			 * int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			 * 
			 * String day = getNameofDay(dayOfWeek);
			 * 
			 * return " " + day + " at " + hour + ":" + min + ampm; }
			 */

			// if (condition) {
			//
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}

		// int hour = c.get(Calendar.HOUR);
		//
		// String ampm = "AM";
		// if (c.get(Calendar.AM_PM) == Calendar.PM)
		// ampm = "PM";
		//
		// if (hour == 0 && ampm.equals("PM"))
		// hour = 12;
		//
		String day = "" + c.get(Calendar.DAY_OF_MONTH);// time.getDate();
		if (Integer.parseInt(day) < 10)
			day = "0" + day;
		//
		// String min = "" + c.get(Calendar.MINUTE);
		// if (Integer.parseInt(min) < 10)
		// min = "0" + min;

		int year = c.get(Calendar.YEAR);
		String stringYear = "" + year;
		// stringYear = stringYear.substring(2, 4);
		// String temp = " " + getmonthInt(c.get(Calendar.MONTH)) + "/" + day
		// + "/" + stringYear + " ";

		String temp = " " + getNameofMon(c.get(Calendar.MONTH)) + " " + day
				+ ", " + stringYear + " ";
		// + ((hour > 9) ? hour : ("0" + hour)) + ":" + min + " ";

		// if (c.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(
		// Calendar.DAY_OF_MONTH)
		// && c.get(Calendar.MONTH) == Calendar.getInstance().get(
		// Calendar.MONTH))
		// temp = "Today";

		return temp;
	}

	private static String getNameofMon(int dayValue) {

		if (dayValue == 0) {
			return "Jan";
		} else if (dayValue == 1) {
			return "Feb";
		} else if (dayValue == 2) {
			return "March";
		} else if (dayValue == 3) {
			return "April";
		} else if (dayValue == 4) {
			return "May";
		} else if (dayValue == 5) {
			return "June";
		} else if (dayValue == 6) {
			return "July";
		} else if (dayValue == 7) {
			return "Aug";
		} else if (dayValue == 8) {
			return "Sept";
		} else if (dayValue == 9) {
			return "Oct";
		} else if (dayValue == 10) {
			return "Nov";
		} else if (dayValue == 11) {
			return "Dec";
		} else {
			return "";
		}
		// TODO Auto-generated method stub

	}

	private static String getNameofDay(int dayValue) {

		if (dayValue == 1) {
			return "Sunday";
		} else if (dayValue == 2) {
			return "Monday";
		} else if (dayValue == 3) {
			return "Tuesday";
		} else if (dayValue == 4) {
			return "Wednesday";
		} else if (dayValue == 5) {
			return "Thursday";
		} else if (dayValue == 6) {
			return "Friday";
		} else if (dayValue == 7) {
			return "Saturday";
		} else {
			return "";
		}
		// TODO Auto-generated method stub

	}

	/***
	 * 
	 * @param filePath
	 * @param fileName
	 * @param format
	 * @param context
	 * @return
	 * 
	 *         Used to upload the Audio Or Video File Amazon Server
	 */
	public static synchronized boolean uploadImageToAmazon(
			final String filePath, final String fileName, final String format) {

		boolean isUpload = false;

		Log.e("", "The File Path is " + filePath + " and The File Name is "
				+ fileName);
		AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(
				ACCESS_KEY_ID, SECRET_KEY));

		try {
			s3Client.createBucket(getMediaBucket());

			Log.e("", "Uploading Waiting...........");
			File f = new java.io.File(filePath);
			FileInputStream fileInputStream = new FileInputStream(f);
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength((f.length()));

			objectMetadata.setContentType(format);

			// Log.e("", "Content Type "+objectMetadata.getContentType());
			// Log.e("",
			// "Content Disposition "+objectMetadata.getContentDisposition());
			// Log.e("",
			// "Content Encoding "+objectMetadata.getContentEncoding());
			// Log.e("", "Content ContentMd5 "+objectMetadata.getContentMD5());
			// PutObjectRequest por = new PutObjectRequest(getMediaBucket(),
			// fileName, new
			// java.io.File(filePath)).withCannedAcl(CannedAccessControlList.PublicReadWrite);
			// // Content
			// type
			// is
			// determined
			// by
			// file
			// extension.
			PutObjectRequest por = new PutObjectRequest(getMediaBucket(),
					fileName, fileInputStream, objectMetadata)
					.withCannedAcl(CannedAccessControlList.PublicRead);

			Log.e("", "the Key is " + por.getKey());

			s3Client.putObject(por);
			por.setProgressListener(new ProgressListener() {

				@Override
				public void progressChanged(ProgressEvent pe) {
					Log.e("",
							"============ Uploaded File Part is "
									+ pe.getBytesTransfered());

				}
			});

			Log.e("", "Object Added Uploading Waiting...........");
			try {
				ResponseHeaderOverrides override = new ResponseHeaderOverrides();
				// override.setContentType("image/jpeg");
				override.setContentType(format);
				Log.e("", "Retriving the Audio Data Waiting...........");
				// Generate the presigned URL.
				long expirationMillis = 15 * 24 * 60 * 60 * 1000;
				Date expirationDate = new Date(System.currentTimeMillis()
						+ expirationMillis);
				GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
						getMediaBucket(), fileName);
				urlRequest.setExpiration(expirationDate);
				Log.e("", "Generating Please wait  Waiting...........");
				// urlRequest.setExpiration(expirationDate);
				urlRequest.setResponseHeaders(override);

				URL url = s3Client.generatePresignedUrl(urlRequest);
				// URL url =
				// s3Client.generatePresignedUrl(getMediaBucket(),por.getKey(),expirationDate,HttpMethod.GET);
				// Log.e("",
				// "the name of the Owner is "+s3Client.getBucketAcl(getMediaBucket()).getOwner().getDisplayName());
				Log.e("", "The New Url is " + url.toString());
				Log.e("",
						"The My Another  Url is "
								+ s3Client.getResourceUrl(getMediaBucket(),
										por.getKey()));

				// Log.e("", "=============the Url is " + url.toString());

				setUploadedUrl(s3Client.getResourceUrl(getMediaBucket(),
						por.getKey()));

				// setUploadedUrl(getTinyUrl(url.toString()));
				// setUploadedUrl(url.toString());
				// setUploadedUrl("https://"+getMediaBucket()+".s3.amazonaws.com/"+fileName);

				isUpload = true;

			} catch (Exception e) {
				e.printStackTrace();

				isUpload = false;

				setUploadedUrl("");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			isUpload = false;

			setUploadedUrl("");
			// displayAlert("Upload Failure", exception.getMessage(),
			// context);
		}

		return isUpload;
	}

	/***
	 * 
	 * @param filePath
	 * @param fileName
	 * @param format
	 * @param context
	 * @return
	 * 
	 *         Used to upload the Audio Or Video File Amazon Server
	 */
	public static synchronized boolean uploadFileToAmazon(
			final String filePath, final String fileName, final String format) {

		boolean isUpload = false;

		Log.e("", "The File Path is " + filePath + " and The File Name is "
				+ fileName);
		AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(
				ACCESS_KEY_ID, SECRET_KEY));

		try {
			s3Client.createBucket(getMediaBucket());

			Log.e("", "Uploading Waiting...........");
			File f = new java.io.File(filePath);
			FileInputStream fileInputStream = new FileInputStream(f);
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength((f.length()));
			objectMetadata.setContentType(format);

			// Log.e("", "Content Type "+objectMetadata.getContentType());
			// Log.e("",
			// "Content Disposition "+objectMetadata.getContentDisposition());
			// Log.e("",
			// "Content Encoding "+objectMetadata.getContentEncoding());
			// Log.e("", "Content ContentMd5 "+objectMetadata.getContentMD5());
			// PutObjectRequest por = new PutObjectRequest(getMediaBucket(),
			// fileName, new
			// java.io.File(filePath)).withCannedAcl(CannedAccessControlList.PublicReadWrite);
			// // Content
			// type
			// is
			// determined
			// by
			// file
			// extension.
			PutObjectRequest por = new PutObjectRequest(getMediaBucket(),
					fileName, fileInputStream, objectMetadata)
					.withCannedAcl(CannedAccessControlList.PublicRead);

			Log.e("", "the Key is " + por.getKey());

			s3Client.putObject(por);
			por.setProgressListener(new ProgressListener() {

				@Override
				public void progressChanged(ProgressEvent pe) {
					Log.e("",
							"============ Uploaded File Part is "
									+ pe.getBytesTransfered());

				}
			});

			Log.e("", "Object Added Uploading Waiting...........");
			try {
				ResponseHeaderOverrides override = new ResponseHeaderOverrides();
				// override.setContentType("image/jpeg");
				override.setContentType(format);
				Log.e("", "Retriving the Audio Data Waiting...........");
				// Generate the presigned URL.
				long expirationMillis = 15 * 24 * 60 * 60 * 1000;
				Date expirationDate = new Date(System.currentTimeMillis()
						+ expirationMillis);
				GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
						getMediaBucket(), fileName);
				urlRequest.setExpiration(expirationDate);
				Log.e("", "Generating Please wait  Waiting...........");
				// urlRequest.setExpiration(expirationDate);
				urlRequest.setResponseHeaders(override);

				URL url = s3Client.generatePresignedUrl(urlRequest);
				// URL url =
				// s3Client.generatePresignedUrl(getMediaBucket(),por.getKey(),expirationDate,HttpMethod.GET);
				// Log.e("",
				// "the name of the Owner is "+s3Client.getBucketAcl(getMediaBucket()).getOwner().getDisplayName());
				Log.e("", "The New Url is " + url.toString());
				Log.e("",
						"The My Another  Url is "
								+ s3Client.getResourceUrl(getMediaBucket(),
										por.getKey()));

				// Log.e("", "=============the Url is " + url.toString());

				setUploadedUrl(s3Client.getResourceUrl(getMediaBucket(),
						por.getKey()));

				// setUploadedUrl(getTinyUrl(url.toString()));
				// setUploadedUrl(url.toString());
				// setUploadedUrl("https://"+getMediaBucket()+".s3.amazonaws.com/"+fileName);

				isUpload = true;

			} catch (Exception e) {
				e.printStackTrace();

				isUpload = false;

				setUploadedUrl("");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			isUpload = false;

			setUploadedUrl("");
			// displayAlert("Upload Failure", exception.getMessage(),
			// context);
		}

		return isUpload;
	}

	public static String getTinyUrl(String amazonUrl) {
		String tinyUrl = "";
		try {
			HttpGet httpGet = new HttpGet(TINY_URL_LINK
					+ URLEncoder.encode(amazonUrl));
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
			HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
			if (HttpStatus.SC_OK == httpResponse.getStatusLine()
					.getStatusCode()) {
				HttpEntity httpEntity = httpResponse.getEntity();
				tinyUrl = EntityUtils.toString(httpEntity);

				Log.e("", "The Tiny Url is  " + tinyUrl);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tinyUrl;

	}

	public static String getAnotherTinyUrl(String mUrl) {
		String myTinyUrl = "";
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			String urlTemplate = "http://tinyurl.com/api-create.php?url="
					+ URLEncoder.encode(mUrl);
			// String uri = String.format(urlTemplate, URLEncoder.encode(mUrl));
			HttpGet request = new HttpGet(urlTemplate);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			InputStream in = entity.getContent();
			try {
				int statusCode = response.getStatusLine().getStatusCode();
				Log.e("", "The Tiny Irl Status Code ============" + statusCode);
				if (statusCode == HttpStatus.SC_OK) {
					// TODO: Support other encodings
					String enc = "utf-8";
					Reader reader = new InputStreamReader(in, enc);
					BufferedReader bufferedReader = new BufferedReader(reader);
					String tinyUrl = bufferedReader.readLine();
					if (tinyUrl != null) {
						myTinyUrl = tinyUrl;
						Log.e("", "My Tiny Url is ==================="
								+ myTinyUrl);
					} else {
						throw new IOException("empty response");
					}
				} else {
					String errorTemplate = "unexpected response: %d";
					String msg = String.format(errorTemplate, statusCode);
					throw new IOException(msg);
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}

		return myTinyUrl;
	}

	// Display an Alert message for an error or failure.
	protected static void displayAlert(String title, String message,
			Context context) {
		AlertDialog.Builder confirm = new AlertDialog.Builder(context);
		confirm.setTitle(title);
		confirm.setMessage(message);
		confirm.setCancelable(true);
		confirm.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		confirm.show().show();
	}

	// returns the Bucket Path of the Media Files along with Access Key which
	// are stored on to the Amazon Server
	public static String getMediaBucket() {
		// return (MEDIA_BUCKET + ACCESS_KEY_ID + MEDIA_BUCKET).toLowerCase();

		return (MEDIA_BUCKET).toLowerCase();
	}

	public static boolean copy(File src, File dst) throws IOException {
		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void clearActivityStack(Activity activity) {
		try {
			List<Activity> activities = ActivityObj.getActivityInstance();
			int size = activities.size();
			Log.e("", "Stack Activity List Size Before removing " + size);
			if (activities != null && size > 0) {
				activities.remove(activity);
				Log.e("", "Activity Instance Removed Successfully "
						+ activity.getClass().getSimpleName());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static boolean isServiceRunning(String serviceClassName,
			Context context) {
		String serviceName = serviceClassName;
		serviceName = serviceName.replace("class", "");
		serviceName = serviceName.trim();
		Log.e("", "My Service is " + serviceName);
		final ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		final List<RunningServiceInfo> services = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		Log.e("",
				"===========The Size of Running Services are "
						+ services.size());
		for (RunningServiceInfo runningServiceInfo : services) {
			Log.e("", "Running Services"
					+ runningServiceInfo.service.getClassName().trim()
					+ ":My Service=" + serviceName);
			if (runningServiceInfo.service.getClassName().trim()
					.equalsIgnoreCase(serviceName)) {
				Log.e("", "if====================================");
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param serviceClassName
	 * @param context
	 * @return Used to check if service is already running or not
	 */
	public static boolean isMyServiceRunning(String serviceClassName,
			Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			Log.e("",
					"The Running Services are "
							+ service.service.getClassName());
			if (serviceClassName.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static void checkMessagefromServer(final Context context) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					Looper.prepare();

					if (CommonFunctions.CheckNetConnectivity(context)) {
						DataBase db = new DataBase(context);
						try {
							db.open();
							int ref_id = CommonFunctions.getPref(context,
									CommonFunctions.REF_ID, 0);
							String Response = CommonFunctions.postDataOnServer(
									CommonFunctions.BASE_URL
											+ CommonFunctions.GET_INBOX,
									("user_id=" + ref_id).getBytes());
							if (!Response.equals("") && Response != null) {
								TenFourMessageXML receivemessage = new TenFourMessageXML();
								receivemessage.parceXML(Response);

								if (!receivemessage.error) {
									Debugger.debugE("Received Message +"
											+ receivemessage.totalMessage);
									if (receivemessage.tenFourMessages.size() > 0) {
										for (int i = 0; i < receivemessage.tenFourMessages
												.size(); i++) {

											ContentValues cv = new ContentValues();
											cv = receivemessage.tenFourMessages
													.get(i);
											if (((String) receivemessage.tenFourMessages
													.get(i).get("receiver"))
													.trim().split(",").length == 1) {

												String senderNumber = ((String) cv
														.get("sender")).trim();
												senderNumber = senderNumber
														.trim();
												Log.e("",
														"The Sender Number is "
																+ senderNumber);
												int count = db
														.fetchCounts(
																DataBase.Friends_table,
																DataBase.Friends_int,
																new String[] { "sr_no" },
																Friend.NUMBER
																		+ "='"
																		+ cv.get("sender")
																		+ "' and "
																		+ Friend.STATUS
																		+ "=-1");
												if (count == 0) // check sender
																// if he is
																// blocked
												{
													Debugger.debugE("Sender : "
															+ cv.get("sender")
															+ " Receiver : "
															+ cv.get("receiver"));
													cv.put("type", 1); // incoming
													cv.put("status", 0);
													cv.put("reply", 0);
													String senderNames = "";
													Integer sender_status_known = 1;
													Cursor findName = db
															.fetch(DataBase.Friends_table,
																	DataBase.Friends_int,
																	"number like'%"
																			+ ("" + cv
																					.get("sender"))
																					.substring(3)
																			+ "'");
													if (findName.getCount() > 0) {
														findName.moveToFirst();
														if (findName.getCount() == 1) {
															senderNames = findName
																	.getString(2);

														} else {

															while (!findName
																	.isAfterLast()) {
																if (checkForEqual(
																		findName.getString(3),
																		""
																				+ cv.get("sender"))) {
																	senderNames = findName
																			.getString(2);
																}
																findName.moveToNext();
															}

														}
													}

													else {
														senderNames = senderNumber;
													}

													findName.close();
													if (senderNames.equals("")) {
														/*
														 * try { if
														 * (CommonFunctions
														 * .CheckNetConnectivity
														 * (getBaseContext())) {
														 * String resp =
														 * CommonFunctions
														 * .postDataOnServer
														 * (CommonFunctions
														 * .BASE_URL +
														 * CommonFunctions
														 * .FRIENDS_URL,
														 * XMLRequestBuilder
														 * .ActiveFriendsByteArray
														 * (
														 * cv.get("sender").toString
														 * (),
														 * getBaseContext()));
														 * if (resp != null &&
														 * !resp.equals("") &&
														 * CommonFunctions
														 * .getTag(resp,
														 * "status"
														 * ).equals("1")) {
														 * FriendsParser parser
														 * = new
														 * FriendsParser();
														 * parser
														 * .parceXML(resp); if
														 * (!parser.error) {
														 * ContentValues vc =
														 * new ContentValues();
														 * String
														 * name=parser.vFriends
														 * .get
														 * (0).get("name").toString
														 * (); StringBuffer
														 * sb=new
														 * StringBuffer(name);
														 * sb.replace(0,1,
														 * name.toUpperCase
														 * ().substring(0,1));
														 * name=sb.toString();
														 * 
														 * vc.put("name", name);
														 * vc.put("invite", 1);
														 * vc.put("status", 1);
														 * vc.put("groupid", 0);
														 * vc.put("email", "");
														 * vc.put("asscid", 0);
														 * vc.put("number",
														 * parser
														 * .vFriends.get(0)
														 * .get("reg_number"
														 * ).toString());
														 * db.insert_content
														 * (DataBase
														 * .Friends_table,
														 * DataBase.Friends_int,
														 * vc); senderNames =
														 * parser
														 * .vFriends.get(0)
														 * .get("name"
														 * ).toString();
														 * sender_status_known =
														 * 1; } else throw new
														 * Exception(""); } else
														 * throw new
														 * Exception(""); } else
														 * throw new
														 * Exception(""); }
														 * catch (Exception e) {
														 * senderNames =
														 * cv.get("sender"
														 * ).toString();
														 * sender_status_known =
														 * 0; }
														 */
													}

													try {
														findName.close();
													} catch (Exception e) {
														// TODO: handle
														// exception
													}
													// cv.put("receiver",
													// senderNames);
													// cv.put("sender",
													// senderNames);
													cv.put("sender",
															CommonFunctions
																	.getPref(
																			context,
																			CommonFunctions.PHONE_NUMBER,
																			""));
													cv.put("sender_status",
															sender_status_known);

													String myConversationId = Utils
															.fetchConversationID(
																	senderNames,
																	db);
													try {
														Log.e("",
																"The Db Conversation Id is "
																		+ myConversationId);
													} catch (Exception e) {
														// TODO: handle
														// exception
													}
													// String tmpConvId =
													// db.fetchConversationID(senderNames);
													if (myConversationId != null
															&& myConversationId
																	.length() > 0) {
														cv.put("conversation_Id",
																myConversationId);
													} else {
														cv.put("conversation_Id",
																""
																		+ System.currentTimeMillis());
													}
													// if
													// (!tmpConvId.equals("")&&
													// !(tmpConvId==null))
													// cv.put("conversation_Id",
													// tmpConvId);

													String rec = cv
															.get("receiver")
															.toString().trim();

													/*
													 * String tmp[] =
													 * rec.split(","); rec = "";
													 * for (int j = 0; j <
													 * tmp.length; j++) { if
													 * (tmp[j] != null &&
													 * !tmp[j]
													 * .trim().equals("")) rec
													 * += tmp[j] + ","; }
													 * if(rec.endsWith(",")) rec
													 * = rec.substring(0,
													 * rec.length() - 1);
													 * 
													 * cv.put("receiver", rec);
													 */
													if (!rec.contains(",")
															&& checkForEqual(
																	rec,
																	CommonFunctions
																			.getPref(
																					context,
																					CommonFunctions.PHONE_NUMBER,
																					""))) {
														Log.e("",
																"The Name of the Receiver is "
																		+ rec);
														// cv.put("receiver",
														// "Me");
														if (senderNames != null
																&& senderNames
																		.length() > 0) {
															cv.put("receiver",
																	senderNames);
														} else {
															cv.put("receiver",
																	senderNumber);
														}

													} else {
														if (senderNames != null
																&& senderNames
																		.length() > 0) {
															cv.put("receiver",
																	senderNames);
														} else {
															cv.put("receiver",
																	senderNumber);
														}
														/*
														 * String[] receivers =
														 * cv
														 * .get("receiver").toString
														 * ().split(",");
														 * senderNames = ""; for
														 * (int j = 0; j <
														 * receivers.length;
														 * j++) { if
														 * (receivers[j] != null
														 * &&
														 * !receivers[j].trim(
														 * ).equals("")) { if
														 * (!checkForEqual
														 * (receivers[j].trim(),
														 * CommonFunctions
														 * .getPref
														 * (MessageReceiver
														 * .this,
														 * CommonFunctions
														 * .PHONE_NUMBER, "")))
														 * { Cursor findreceiver
														 * = db.fetch(DataBase.
														 * Friends_table,
														 * DataBase.Friends_int,
														 * "number='" +
														 * receivers[j] + "'");
														 * if
														 * (findreceiver.getCount
														 * () > 0) {
														 * findreceiver
														 * .moveToFirst();
														 * senderNames +=
														 * findreceiver
														 * .getString(2) + ",";
														 * 
														 * } else { senderNames
														 * += receivers[j] +
														 * ","; }
														 * findreceiver.close();
														 * 
														 * } else { senderNames
														 * += "Me,"; } } } if
														 * (senderNames
														 * .endsWith(","))
														 * senderNames =
														 * senderNames
														 * .substring(0,
														 * senderNames
														 * .lastIndexOf(","));
														 * 
														 * Debugger.debugE(
														 * "receiver's name" +
														 * senderNames);
														 * cv.put("receiver",
														 * senderNames);
														 */
													}
													db.insert_content(
															DataBase.Receive_table,
															DataBase.Receive_int,
															cv);
												}
											} else {
												String receiverNumber = "";
												String receiverNames = "";

												String senderNumber = "";

												Integer sender_status_known = 1;
												String myNumber = CommonFunctions
														.getPref(
																context,
																CommonFunctions.PHONE_NUMBER,
																"");
												Log.e("", "my Number is "
														+ myNumber);

												try {
													Cursor findName = db
															.fetch(DataBase.Friends_table,
																	DataBase.Friends_int,
																	"number like'%"
																			+ ("" + cv
																					.get("sender"))
																					.substring(3)
																			+ "'");
													if (findName.getCount() > 0) {
														findName.moveToFirst();
														if (findName.getCount() == 1) {
															receiverNames += findName
																	.getString(2)
																	+ ",";

														} else {

															while (!findName
																	.isAfterLast()) {
																if (checkForEqual(
																		findName.getString(3),
																		""
																				+ cv.get("sender"))) {
																	receiverNames += findName
																			.getString(2);
																}
																findName.moveToNext();
															}

														}
													} else {
														receiverNames += cv
																.get("sender")
																+ ",";
													}
													findName.close();
												} catch (Exception e) {
													e.printStackTrace();

												}
												String receiverArray[] = ((String) cv
														.get("receiver"))
														.trim().split(",");
												senderNumber = ((String) cv
														.get("sender")).trim();
												receiverNumber += senderNumber
														+ ",";

												for (int j = 0; j < receiverArray.length; j++) {
													cv = receivemessage.tenFourMessages
															.get(i);
													String singleReceiverNumber = receiverArray[j];

													senderNumber = senderNumber
															.trim();
													Log.e("",
															"The name of the sender is "
																	+ senderNumber);

													try {

														if (!(singleReceiverNumber
																.equalsIgnoreCase(myNumber))) {
															Log.e("",
																	"My Number does not exists ");
															Cursor findName = db
																	.fetch(DataBase.Friends_table,
																			DataBase.Friends_int,
																			"number like '%"
																					+ (""
																							+ singleReceiverNumber + "'"));
															if (findName
																	.getCount() > 0) {
																findName.moveToFirst();
																if (findName
																		.getCount() == 1) {
																	receiverNames += findName
																			.getString(2)
																			+ ",";

																} else {

																	while (!findName
																			.isAfterLast()) {
																		if (checkForEqual(
																				findName.getString(3),
																				""
																						+ singleReceiverNumber)) {
																			receiverNames += findName
																					.getString(2);
																		}
																		findName.moveToNext();
																	}

																}
															} else {
																receiverNames += singleReceiverNumber
																		+ ",";
															}
															findName.close();

														} else {
															Log.e("",
																	"My Number exists so Ignoring It "
																			+ receiverNames);
														}

													} catch (Exception e) {
														e.printStackTrace();

													}

													if (!(singleReceiverNumber
															.equalsIgnoreCase(myNumber))) {
														receiverNumber += singleReceiverNumber
																.trim() + ",";
													}

												}

												if (receiverNumber
														.endsWith(",")) {
													receiverNumber = receiverNumber
															.trim()
															.substring(
																	0,
																	receiverNumber
																			.length() - 1);
												}
												if (receiverNames.endsWith(",")) {
													receiverNames = receiverNames
															.trim()
															.substring(
																	0,
																	receiverNames
																			.length() - 1);
												}

												// String tempReceiver = "";
												// String[] tempReceiverArray =
												// receiverNumber.split(",");
												// for (int j = 0; j <
												// tempReceiverArray.length;
												// j++) {
												// tempReceiver
												// +="'"+tempReceiverArray[j]+"'"+",";
												//
												//
												// }
												// if
												// (tempReceiver.endsWith(","))
												// {
												// tempReceiver=
												// tempReceiver.substring(0,
												// tempReceiver.length()-1);
												// }
												//
												// Log.e("",
												// "The final Tempreceiver Numbers are "+tempReceiver);
												Log.e("",
														"The Receiver Numbers are "
																+ receiverNumber);
												Log.e("",
														"The Receiver Names are "
																+ receiverNames);
												receiverNumber = CommonFunctions.sortStringArray(receiverNumber
														+ ","
														+ CommonFunctions
																.getPref(
																		context,
																		CommonFunctions.PHONE_NUMBER,
																		""));
												Cursor c = null;
												try {
													// "'%Smith%'"
													c = db.fetch(
															DataBase.Receive_table,
															DataBase.Receive_int,
															"groupreceiver = '"
																	+ receiverNumber
																	+ "'");

													if (c != null
															&& c.getCount() > 0) {
														String groupName = c
																.getString(3)
																.trim();
														cv.put("type", 1); // incoming.
														cv.put("status", 0);
														cv.put("reply", 0);
														cv.put("sender",
																CommonFunctions
																		.getPref(
																				context,
																				CommonFunctions.PHONE_NUMBER,
																				""));
														cv.put("sender_status",
																sender_status_known);
														cv.put("receiver",
																groupName);
														cv.put("groupreceiver",
																receiverNumber);
														String myConversationId = Utils
																.fetchConversationID(
																		groupName,
																		db);
														try {
															Log.e("",
																	"The Db Conversation Id is "
																			+ myConversationId);
														} catch (Exception e) {
															e.printStackTrace();
														}
														// String tmpConvId =
														// db.fetchConversationID(senderNames);
														if (myConversationId != null
																&& myConversationId
																		.length() > 0) {
															cv.put("conversation_Id",
																	myConversationId);
														} else {
															cv.put("conversation_Id",
																	""
																			+ System.currentTimeMillis());
														}
													}

													else {
														cv.put("type", 1); // incoming
														cv.put("status", 0);
														cv.put("reply", 0);
														cv.put("sender",
																CommonFunctions
																		.getPref(
																				context,
																				CommonFunctions.PHONE_NUMBER,
																				""));
														cv.put("sender_status",
																sender_status_known);
														cv.put("receiver",
																receiverNames);
														cv.put("groupreceiver",
																receiverNumber);
														String myConversationId = Utils
																.fetchConversationID(
																		receiverNames,
																		db);
														try {
															Log.e("",
																	"The Db Conversation Id is "
																			+ myConversationId);
														} catch (Exception e) {
															e.printStackTrace();
														}
														// String tmpConvId =
														// db.fetchConversationID(senderNames);
														if (myConversationId != null
																&& myConversationId
																		.length() > 0) {
															cv.put("conversation_Id",
																	myConversationId);
														} else {
															cv.put("conversation_Id",
																	""
																			+ System.currentTimeMillis());
														}
														// Put as new Record
													}

												} catch (Exception e) {
													e.printStackTrace();
												} finally {
													c.close();
												}

												db.insert_content(
														DataBase.Receive_table,
														DataBase.Receive_int,
														cv);

											}

										}

										/*
										 * if (context instanceof Registration)
										 * {
										 * 
										 * Registration registration =
										 * (Registration) context; Intent
										 * notificationIntent = new Intent(
										 * registration,
										 * TenFourConversations.class);
										 * notificationIntent .putExtra(
										 * CommonFunctions.MSG_RECEIVED, true);
										 * registration
										 * .startActivity(notificationIntent);
										 * Debugger.debugE(
										 * "================================== Registration Screen If"
										 * ); registration.finish();
										 * 
										 * } else if (context instanceof
										 * SplashScreenOne) { Debugger.debugE(
										 * "================================== Splash Screen If"
										 * ); SplashScreenOne splashScreenOne =
										 * (SplashScreenOne) context;
										 * 
										 * Intent notificationIntent = new
										 * Intent( splashScreenOne,
										 * TenFourConversations.class);
										 * notificationIntent .putExtra(
										 * CommonFunctions.MSG_RECEIVED, true);
										 * splashScreenOne
										 * .startActivity(notificationIntent);
										 * splashScreenOne.finish();
										 * 
										 * }
										 */
									}

									else {

										/*
										 * if (context instanceof Registration)
										 * { Registration registration =
										 * (Registration) context; Intent
										 * notificationIntent = new Intent(
										 * registration,
										 * EyeSayDefaultAudioVideo.class);
										 * 
										 * registration
										 * .startActivity(notificationIntent);
										 * registration.finish();
										 * Debugger.debugE(
										 * "================================== Registration Screen Else"
										 * );
										 * 
										 * } else if (context instanceof
										 * SplashScreenOne) { SplashScreenOne
										 * splashScreenOne = (SplashScreenOne)
										 * context; Intent notificationIntent =
										 * new Intent( splashScreenOne,
										 * EyeSayDefaultAudioVideo.class);
										 * 
										 * splashScreenOne
										 * .startActivity(notificationIntent);
										 * splashScreenOne.finish();
										 * Debugger.debugE(
										 * "================================== Splash Screen Else"
										 * ); }
										 */
									}

								}

								else {
									Debugger.debugE("==================================In Small Else ========================================");
									/*
									 * if (context instanceof Registration) {
									 * Registration registration =
									 * (Registration) context; Intent
									 * notificationIntent = new Intent(
									 * registration,
									 * EyeSayDefaultAudioVideo.class);
									 * 
									 * registration
									 * .startActivity(notificationIntent);
									 * registration.finish(); Debugger.debugE(
									 * "================================== Registration Screen Else"
									 * );
									 * 
									 * } else if (context instanceof
									 * SplashScreenOne) { SplashScreenOne
									 * splashScreenOne = (SplashScreenOne)
									 * context; Intent notificationIntent = new
									 * Intent( splashScreenOne,
									 * EyeSayDefaultAudioVideo.class);
									 * 
									 * splashScreenOne
									 * .startActivity(notificationIntent);
									 * splashScreenOne.finish();
									 * Debugger.debugE(
									 * "================================== Splash Screen Else"
									 * ); }
									 */

								}
							}

							else {

								Debugger.debugE("==================================In main Else ========================================");
								/*
								 * if (context instanceof Registration) {
								 * Registration registration = (Registration)
								 * context; Intent notificationIntent = new
								 * Intent( registration,
								 * EyeSayDefaultAudioVideo.class);
								 * 
								 * registration
								 * .startActivity(notificationIntent);
								 * registration.finish(); Debugger.debugE(
								 * "================================== Registration Screen Else"
								 * );
								 * 
								 * } else if (context instanceof
								 * SplashScreenOne) { SplashScreenOne
								 * splashScreenOne = (SplashScreenOne) context;
								 * Intent notificationIntent = new Intent(
								 * splashScreenOne,
								 * EyeSayDefaultAudioVideo.class);
								 * 
								 * splashScreenOne
								 * .startActivity(notificationIntent);
								 * splashScreenOne.finish(); Debugger.debugE(
								 * "================================== Splash Screen Else"
								 * ); }
								 */

							}
							Debugger.debugE("call the mesage reciever");
						} catch (IOException e) {
							Debugger.debugE("Error in Message Receiver");
							e.printStackTrace();
						} catch (RuntimeException e) {
							Debugger.debugE("Error in Message Receiver");
							e.printStackTrace();
						} catch (Exception e) {
							Debugger.debugE("Error in Message Receiver");
							e.printStackTrace();
						} finally {
							db.close();
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				Looper.loop();
			}

		});
		thread.start();
	}

	public static void checkMessagefromServer1(final Context context) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					Looper.prepare();
					CommonFunctions
							.writeLog("==================Checking message from server==============");
					if (CommonFunctions.CheckNetConnectivity(context)) {
						CommonFunctions
								.writeLog("==================Net Connected==============");
						int count = getMessageCount(context);
						CommonFunctions
								.writeLog("==================Message Count =============="
										+ count);
						if (count == 0) {
							boolean isMsgPresent = getOldSentReceiveMessage(context);
							CommonFunctions
									.writeLog("==================Checking message from server==============");
							if (isMsgPresent) {
								CommonFunctions
										.writeLog("==================Old Message Present==============");
								Intent intent = new Intent(context,
										TenFourConversations.class);
								intent.putExtra(CommonFunctions.MSG_RECEIVED,
										true);
								context.startActivity(intent);

							} else {
								CommonFunctions
										.writeLog("=============Old Message Not Present=============");
								Intent intent = new Intent(context,
										EyeSayDefaultMessage.class);
								context.startActivity(intent);
							}
						}

						else {
							Intent intent = new Intent(context,
									EyeSayDefaultMessage.class);
							context.startActivity(intent);
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (context instanceof Login) {
							Login login = (Login) context;
							login.finish();
						}
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
				Looper.loop();
			}

		});
		thread.start();
	}

	private static void startTenFourConversationScreen(Context context) {
		/*
		 * if (context instanceof Registration) {
		 * 
		 * Registration registration = (Registration) context; Intent
		 * notificationIntent = new Intent(registration,
		 * TenFourConversations.class);
		 * notificationIntent.putExtra(CommonFunctions.MSG_RECEIVED, true);
		 * registration.startActivity(notificationIntent);
		 * 
		 * Debugger.debugE(
		 * "================================== Registration Screen If");
		 * registration.finish(); CommonFunctions
		 * .writeLog("=========== Pushing TenFourConversations  ==========");
		 * 
		 * } else if (context instanceof SplashScreenOne) {
		 * Debugger.debugE("================================== Splash Screen If"
		 * ); SplashScreenOne splashScreenOne = (SplashScreenOne) context;
		 * 
		 * Intent notificationIntent = new Intent(splashScreenOne,
		 * TenFourConversations.class);
		 * notificationIntent.putExtra(CommonFunctions.MSG_RECEIVED, true);
		 * splashScreenOne.startActivity(notificationIntent);
		 * 
		 * splashScreenOne.finish(); CommonFunctions
		 * .writeLog("=========== Pushing TenFourConversations  ==========");
		 * 
		 * }
		 */
	}

	private static void startEyesayDefaultScreen(Context context) {
	}

	/**
	 * 
	 * @param ctx
	 * @return
	 * 
	 *         {"error":false, "error_msg":"", "inbox_list": [
	 *         {"conversation_id":"OQ==", "recipient_names":
	 *         "Dave Brown, Frank3 Cleary, Frankie Cleary",
	 *         "last_message":"YUdscWFnPT0=",
	 *         "conversation_start_date":"2013-09-11 08:45:19",
	 *         "last_message_date":"2013-09-11 08:45:19"} ] }
	 */
	private static boolean getOldSentReceiveMessage(Context ctx) {
		boolean messagePresent = false;
		try {

			String api_key = Constants.API_KEY;
			String ref_id = CommonFunctions.getPref(ctx,
					CommonFunctions.REF_ID, "");
			String com_id = CommonFunctions.getPref(ctx, Constants.COM_ID, "");
			String session = CommonFunctions
					.getPref(ctx, Constants.SESSION, "");
			List<String> nameList = new ArrayList<String>();
			nameList.add(Constants.API_KEY_TAG);
			nameList.add(Constants.REF_ID_TAG);
			nameList.add(Constants.COMP_ID_TAG);
			nameList.add(Constants.SESSION_TAG);
			List<String> valueList = new ArrayList<String>();
			valueList.add(api_key);
			valueList.add(ref_id);
			valueList.add(com_id);
			valueList.add(session);

			String response = CommonFunctions
					.postNameValueDataOnServer(ctx, nameList.size(), nameList,
							valueList, CommonFunctions.DEMO_SERVER_URL
									+ CommonFunctions.GET_INBOX);

			CommonFunctions.writeLog("==========  Old Message Response is "
					+ response);
			Log.e("", "The Response is " + response);
			if (response != null && response.length() > 0) {
				try {

					JSONObject jsonObject = new JSONObject(response);
					String error = jsonObject.getString("error");
					if (error.equalsIgnoreCase("false")) {
						JSONArray inbox_List = jsonObject
								.getJSONArray("inbox_list");
						if (inbox_List != null && inbox_List.length() > 0) {
							DataBase db = null;
							db = new DataBase(ctx);
							db.open();
							try {

								for (int i = 0; i < inbox_List.length(); i++) {
									JSONObject jsonObject2 = inbox_List
											.getJSONObject(i);

									/**
									 * "_id", "message_id", "sender",
									 * "receiver", "message_type", "file_url",
									 * "date_time", "type", "status",
									 * "conversation_Id", "reply",
									 * "sender_status",
									 * "favourite","played","groupreceiver"
									 * ,"textmessage"
									 */
									/*
									 * db.insert( DataBase.Receive_table,
									 * DataBase.Receive_int, new String[] {
									 * "0"//,"message_id" CommonFunctions
									 * .getPref( context,
									 * CommonFunctions.PHONE_NUMBER,
									 * ""),//"sender" name, // receiver Type, //
									 * message_type file_url,// file_url "" +
									 * d,//date_time "0", //type == whether
									 * incoming or outgoing "1",// status ==
									 * message read status converationId,//
									 * conversation_Id "0", // reply === Just
									 * neglect this paramter "" +
									 * sender_status,// sender_status // Just
									 * neglect this param , otherwise used for
									 * black status number, // favourite == Not
									 * in used as we get "0", // played == to
									 * check msg played or not "0", //
									 * groupreceiver == neglect this param
									 * message// textmessage == to store message
									 * });
									 */

									/**
									 * "conversation_id":"OQ==",
									 * "recipient_names":
									 * "Dave Brown, Frank3 Cleary, Frankie Cleary"
									 * , "last_message":"YUdscWFnPT0=",
									 * "conversation_start_date"
									 * :"2013-09-11 08:45:19",
									 * "last_message_date"
									 * :"2013-09-11 08:45:19"}
									 */
									ContentValues cv = new ContentValues();
									cv.put("message_id", "0");
									cv.put("sender", CommonFunctions.getPref(
											ctx, CommonFunctions.REF_ID, ""));

									String receiver = getRecipientsId(
											db,
											jsonObject2
													.getString("recipient_names"));

									cv.put("receiver", receiver);
									cv.put("message_type", "2");
									cv.put("file_url", "");

									cv.put("date_time",
											""
													+ getLongMillisfromDate(jsonObject2
															.getString("last_message_date")));

									cv.put("type", "1");
									cv.put("status", "0");
									cv.put("conversation_Id", jsonObject2
											.getString("conversation_id"));
									cv.put("favourite", "56");
									cv.put("played", "0");

									cv.put("textmessage", jsonObject2
											.getString("last_message"));
									db.insert_content(DataBase.Receive_table,
											DataBase.Receive_int, cv);

								}

							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								db.close();
							}

							messagePresent = true;
						} else {
							messagePresent = false;
						}

					}

					else {
						messagePresent = false;

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return messagePresent;
	}

	public static long getLongMillisfromDate(String stringDate) {

		// "last_message_date":"2013-09-11 08:45:19"
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		TimeZone tz = TimeZone.getTimeZone("UTC");
		sdf.setTimeZone(tz);

		Date date = null;
		try {
			date = sdf.parse(stringDate);

			// Log.e("",
			// "============ The offset is "
			// + tz.getOffset(date.getTime()));
			/*
			 * Calendar calendar2 = Calendar.getInstance(); Date nowDate = new
			 * Date(calendar2.getTimeInMillis());
			 * 
			 * int dstOffset = (calendar2.get(Calendar.DST_OFFSET)/1000);
			 * 
			 * TimeZone tz = TimeZone.getDefault(); int offsetFromUtc =
			 * tz.getOffset(nowDate.getTime()); long requiredOffset =
			 * offsetFromUtc/1000;
			 */
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("in milliseconds:---------- " + date.getTime()
		// + "========= " + date.toString());

		return date.getTime();
	}

	public static String getRecipientsId(DataBase db, String recipientsName) {

		String recipientsId = "";
		Log.e("", "***************** Recipients Names are ************"
				+ recipientsName);
		try {
			StringTokenizer stringTokenizer = new StringTokenizer(
					recipientsName, ",");
			while (stringTokenizer.hasMoreElements()) {
				String name = stringTokenizer.nextElement().toString();
				name = name.trim();
				recipientsId += name + ",";

			}
			recipientsId = recipientsId.substring(0, recipientsId.length() - 1);
			Log.e("", "=========== Now the required Ids are ========"
					+ recipientsId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recipientsId;

	}

	private static int getMessageCount(Context ctx) {
		DataBase db = null;
		int count = 0;
		try {
			db = new DataBase(ctx);
			db.open();

			count = db.getCount(DataBase.Receive_table, DataBase.Receive_int,
					null);
			Log.e("", "=========================== " + count);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return count;

	}

	public static void insertDataToDrafts(DataBase db, String sender,
			String receiver, String dest, String phoneNumber) {
		try {

			ContentValues cv = new ContentValues();
			cv.put("message_id", "");
			cv.put("sender", sender);
			cv.put("receiver", receiver);
			if (dest.endsWith(".3gpp")) {
				cv.put("message_type", 1);
			} else {
				cv.put("message_type", 0);
			}
			try {
				cv.put("file_url", dest);
			} catch (Exception e) {
				e.printStackTrace();
			}
			cv.put("date_time", "" + System.currentTimeMillis());
			cv.put("type", 1);
			cv.put("phonenumber", phoneNumber);
			db.insert_content(DataBase.drafts_table, DataBase.drafts_int, cv);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// finally{
		// if (db!=null) {
		// db.close();
		// }
		// }

	}

	public static boolean checkFileToSend(String source) {
		boolean isFile = false;
		try {
			File file = new File(source);

			Log.e("", "================File Exists " + file.exists()
					+ "========= File Length " + file.length());
			if (file.exists() && file.length() > 0) {

				isFile = true;

			} else {

				isFile = false;

			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return isFile;

	}

	public static void sendFailMessageToDrafts(Context ctx, String numbers,
			String names, String destFilePath, String sourceFilepath) {

		try {
			try {
				Log.e("", "================== " + CommonFunctions.getAudioURL());

				// if (sourceFilepath!=null && sourceFilepath.length()>0 &&
				// sourceFilepath.endsWith(EyesaySendText.IMAGE_EXTENSION)) {
				CommonFunctions.copy(new File(sourceFilepath), new File(
						destFilePath));
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		DataBase db = null;
		try {
			db = new DataBase(ctx);
			db.open();
			String[] numberlist = numbers.split(",");
			String[] namelist = names.split(",");

			for (int i = 0; i < numberlist.length; i++) {
				try {
					CommonFunctions.insertDataToDrafts(db, CommonFunctions
							.getPref(ctx, CommonFunctions.PHONE_NUMBER, ""),
							namelist[i], destFilePath, numberlist[i]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public static Bitmap decodeSampledBitmapFromResource(InputStream is,
			int reqWidth, int reqHeight) {
		if (is != null) {

			try {
				is.reset();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(is, null, options);
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			try {
				is.reset();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return BitmapFactory.decodeStream(is, null, options);

		} else {
			return null;
		}

	}

	static Bitmap bitmap = null;

	public static Bitmap decodeSampledBitmapFromResource1(InputStream is,
			int reqWidth, int reqHeight, HttpEntity entity, String url) {
		if (is != null) {

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, options);
			int imageHeight = options.outHeight;
			int imageWidth = options.outWidth;
			String imageType = options.outMimeType;

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;

			try {
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				final HttpClient client = new DefaultHttpClient();

				final HttpGet getRequest = new HttpGet(url);
				try {
					HttpResponse response = client.execute(getRequest);
					final int statusCode = response.getStatusLine()
							.getStatusCode();
					if (statusCode != HttpStatus.SC_OK) {
						Log.e("ImageDownloader", "Error " + statusCode
								+ " while retrieving bitmap from " + url);
						return null;
					}
					final HttpEntity entity1 = response.getEntity();
					if (entity1 != null) {
						InputStream inputStream = null;
						try {
							is = entity1.getContent();
							/*
							 * return BitmapFactory.decodeStream(inputStream);
							 * // Bug on slow connections, fixed in future
							 * release.
							 */

							// return BitmapFactory.decodeStream(new
							// FlushedInputStream(
							// inputStream));

						} catch (Exception e) {
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

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return BitmapFactory.decodeStream(is, null, options);

		} else {
			return null;
		}

	}

	public static Bitmap decodeSampledBitmapFromFile(String file, int reqWidth,
			int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(file, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(file, options);
	}

	public static Bitmap decodeBitmapResouece(Resources res, int id,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		// BitmapFactory.decodeFile(file, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeResource(res, id, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public static String sortStringArray(String data) {

		//
		// We have some string numbers separated by comma. First we
		// need to split it so we can get each individual number.
		//

		String[] numbers = data.split(",");

		//
		// Convert the string numbers into Integer and placed it into
		// an array of Integer.
		//
		Long[] intValues = new Long[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			intValues[i] = Long.parseLong(numbers[i].trim());
		}

		//
		// Sort the number in ascending order using the
		// Collections.sort() method.
		//
		Collections.sort(Arrays.asList(intValues));

		//
		// Convert back the sorted number into string using the
		// StringBuilder object. Prints the sorted string numbers.
		//
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < intValues.length; i++) {
			Long intValue = intValues[i];
			builder.append(intValue);
			if (i < intValues.length - 1) {
				builder.append(", ");
			}
		}
		System.out.println("Before = " + data);
		System.out.println("After  = " + builder.toString());
		return builder.toString();
	}

	public static void writeLog(String logMessage) {
		try {
			String baseUrl = CommonFunctions.getBaseURL();
			File file = new File(baseUrl + "temporary/eyesaylog.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(file, true)));
			out.println(logMessage + "===="
					+ new Date(System.currentTimeMillis()));
			out.close();

			// generate whatever data you want

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
