package com.tribewire.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.GroupMemberService;
import com.eyesayapp.Utils.SyncAddressBook;
import com.eyesayapp.Utils.TribeWireConstants;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.asyn.CheckUserCrentials;
import com.eyesayapp.asyn.ContactSync;
import com.eyesayapp.asyn.GetGroupList;
import com.eyesayapp.asyn.IContactResult;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.RestApiCall;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Login extends Activity implements OnClickListener, IHttpRequest,
		IContactResult {

	private EditText userName;
	private EditText password;
	private Button btn_submit;
	static ProgressDialog progressDialog;
	Login login = this;
	private String contactResponse = null;

	Typeface tf;
	TextView txtForgotPassword;
	/**
	 * GCM Variables Start
	 */
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	String SENDER_ID = "965404619941";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCM Demo";

	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();

	String regid;

	/**
	 * GCM Variable End
	 */
	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				// finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(this);
		int appVersion = getAppVersion(this);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = "";

		try {
			registrationId = prefs.getString(PROPERTY_REG_ID, "");
			Log.e("", "============== The Registration Id === "
					+ registrationId);
			if (registrationId.isEmpty()) {
				Log.i(TAG, "Registration not found.");
				return "";
			}
			// Check if app was updated; if so, it must clear the registration
			// ID
			// since the existing regID is not guaranteed to work with the new
			// app version.
			int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
					Integer.MIN_VALUE);
			int currentVersion = getAppVersion(context);
			if (registeredVersion != currentVersion) {
				Log.i(TAG, "App version changed.");
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return this.getSharedPreferences(DemoActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		// Your implementation here.
		checkUserCrentials();
		
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(Login.this);
					}
					regid = gcm.register(SENDER_ID);
					
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over
					// HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.
					

					// For this demo: we don't need to send it because the
					// device will send
					// upstream messages to a server that echo back the message
					// using the
					// 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(Login.this, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// mDisplay.append(msg + "\n");
				if (userName.getText().length()>0 && password.getText().length()>0) {
					
					sendRegistrationIdToBackend();
				}
				
				Log.e("",
						"=============================== The Registration Id "
								+ msg);
			}
		}.execute(null, null, null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/Helvetica.ttf");
		userName = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		txtForgotPassword = (TextView) findViewById(R.id.forgot_password);
		userName.setTypeface(tf);
		password.setTypeface(tf);
		txtForgotPassword.setTypeface(tf);
		txtForgotPassword.setOnClickListener(this);
		btn_submit = (Button) findViewById(R.id.submit);
		btn_submit.setOnClickListener(this);
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(this);

			if (regid.isEmpty()) {
				registerInBackground();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
			Toast.makeText(
					this,
					"No valid Google Play Services APK found , Please install valid google play service apk file",
					Toast.LENGTH_LONG).show();
		}

		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {

		try {
			checkPlayServices();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ActivityStatus.setActivity(this);
		super.onResume();
	}

	
	@Override
	protected void onPause() {
		ActivityStatus.setActivity(null);
		super.onPause();
	}
	@Override
	public void onClick(View v) {
		if (v == btn_submit) {
			if (userName != null && userName.length() > 0 && password != null
					&& password.length() > 0) {

				if (CommonFunctions.isInternetConnected(this)) {
					if (!regid.isEmpty()) {
						progressDialog = ProgressDialog.show(this, "Authorizing",
								"Please wait while authorizing ......");
						progressDialog.setCancelable(true);
						checkUserCrentials();
					} else {
						progressDialog = ProgressDialog.show(this, "Authorizing",
								"Please wait while authorizing ......");
						progressDialog.setCancelable(true);
						//registerInBackground();
						
						if (checkPlayServices()) {
							gcm = GoogleCloudMessaging.getInstance(this);
							regid = getRegistrationId(this);

							if (regid.isEmpty()) {
								registerInBackground();
							}
						} else {
							Log.i(TAG, "No valid Google Play Services APK found.");
							Toast.makeText(
									this,
									"No valid Google Play Services APK found , Please install valid google play service apk file",
									Toast.LENGTH_LONG).show();
							if (progressDialog!=null) {
								progressDialog.dismiss();
							}
						}
					}
				} else {
					CommonFunctions
							.showAlert(
									Login.this,
									"Please check your internet connectivity",
									this.getResources().getString(
											R.string.warning));
				}

			} else {
				Toast.makeText(this, "All fields are required",
						Toast.LENGTH_LONG).show();
			}
		} else if (v == txtForgotPassword) {
			try {

				if (userName != null && userName.length() > 0) {
					List<String> nameList = new ArrayList<String>();
					nameList.add("email");
					nameList.add("api_key");
					List<String> valueList = new ArrayList<String>();
					valueList.add(userName.getText().toString());
					valueList.add(Constants.API_KEY);
					if (CommonFunctions.isInternetConnected(this)) {
						progressDialog = ProgressDialog
								.show(this, "Authorizing",
										"Please wait while sending your password link to your mail......");
						progressDialog.setCancelable(true);

						// new RestApiCall(this, CommonFunctions.DEMO_SERVER_URL
						// + TribeWireConstants.FORGOTPASSWORD_URL,
						// nameList, valueList).execute();
						new RestApiCall(this, CommonFunctions.DEMO_SERVER_URL
								+ TribeWireConstants.FORGOTPASSWORD_URL,
								nameList, valueList).start();
					} else {
						CommonFunctions
								.showAlert(
										Login.this,
										"Please check your internet connectivity",
										this.getResources().getString(
												R.string.warning));
					}

				} else {
					Toast.makeText(this,
							" Please enter your valid email address",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// Used to check the User Login Credentials
	private void checkUserCrentials() {

		String Email = userName.getText().toString().trim();
		if (Email == null || Email.length() < 1) {
			CommonFunctions.showAlert(Login.this,
					"Invalid Email. Please enter proper Email.", this
							.getResources().getString(R.string.warning));
			userName.setFocusable(true);
			return;
		}

		else {
			
			String userName = this.userName.getText().toString();
			String password = this.password.getText().toString();
			String registrationId = regid;
			Object[] params = new Object[3];
			params[0] = userName;
			params[1] = password;
			params[2] = registrationId;

			new CheckUserCrentials(this).execute(params);

		}

	}

	/**
	 * Response Type: json
	 * 
	 * error: Boolean true/false
	 * 
	 * error_msg: message string,
	 * 
	 * null string if error is false
	 * 
	 * user_id: base64 encoded
	 * 
	 * comp_id: base64 encoded
	 * 
	 * fname: string
	 * 
	 * lname: string
	 * 
	 * session: md5 string {"response": {"user_id":"MQ==",
	 * "fname":"Susan","lname":"Cross", "image":
	 * "http:\/\/eyesay.com\/uploads\/user_profiles\/u_1\/1402063118078eyesay.jpeg"
	 * , "comp_id":"NA==", "session":"bcc834d10466b63eda53de498a7db475"}}
	 */
	@Override
	public void onResponse(String response) {

		Log.e("", "The Response from the Server is " + response);
		try {
			if (response != null && response.length() > 0) {

				try {

					JSONObject jsonObject1 = new JSONObject(response);
					JSONObject jsonObject = jsonObject1
							.getJSONObject("response");
					// JSONObject jsonObject = new JSONObject(response);
					try {

						boolean error = jsonObject.getBoolean("error");
						if (error) {
							handler.sendEmptyMessage(5);

						}

					} catch (Exception e) {
						e.printStackTrace();
						String user_id = jsonObject.getString("user_id");
						CommonFunctions.setPref(this, CommonFunctions.REF_ID,
								user_id);
						String com_id = jsonObject.getString("comp_id");
						try {
							final String imagePath = jsonObject
									.getString("image");
							Log.e("", "======== The Image Path is " + imagePath);
							Thread thread = new Thread(new Runnable() {

								@Override
								public void run() {
									Bitmap bitmap = downloadBitmap(imagePath);
									saveBitmapToSDCard(bitmap);

								}
							});
							thread.start();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						try {
							// Log.e("", "Company Id "+Base64.decode(com_id,
							// Base64.DEFAULT)+" == User ID "+Base64.decode(user_id,
							// Base64.DEFAULT));
							Log.e("",
									"Company Id "
											+ new String(Base64.decode(com_id,
													Base64.DEFAULT))
											+ " == User ID "
											+ new String(Base64.decode(user_id,
													Base64.DEFAULT)));
						} catch (Exception e2) {
							e.printStackTrace();
						}
						CommonFunctions.setPref(this, Constants.COM_ID, com_id);
						String fName = jsonObject.getString("fname");
						CommonFunctions.setPref(this, Constants.FIRST_NAME,
								fName);
						String lName = jsonObject.getString("lname");
						CommonFunctions.setPref(this, Constants.LAST_NAME,
								lName);
						String sessionId = jsonObject.getString("session");
						CommonFunctions.setPref(this, Constants.SESSION,
								sessionId);

						Log.e("", "The User Id " + user_id + " ComId " + com_id
								+ " FirstName " + fName + " Lname " + lName
								+ " Session " + sessionId);
						handler.sendEmptyMessage(1);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String onError) {
		Log.e("", "Error " + onError);
		handler.sendEmptyMessage(0);
	}

	public static final int ADMIN_GROUP_RESPONSE = 6;
	public static final int USER_GROUP_RESPONSE = 7;
	public static final String KEY_USER_GROUP = "key_user_group";
	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {
				
				disMissProgress();

				new ContactSync(Login.this, Login.this).execute();
				progressDialog = ProgressDialog.show(Login.this,
						"Contact Sync",
						"Please wait while we fetch your contacts");
				progressDialog.setCancelable(true);
			} else if (msg.what == 0) {
				disMissProgress();
				CommonFunctions.showAlert(Login.this,
						"Error , Please try after some time", Login.this
								.getResources().getString(R.string.warning));

			} else if (msg.what == 2) {

				if (contactResponse != null && contactResponse.length() > 0) {
					SyncAddressBook.GSSyncContact(Login.this, contactResponse);
				}

				if (CommonFunctions.isInternetConnected(Login.this)) {
					// TribeWireUtil.getAdminGroups(Login.this);
					// TribeWireUtil.uploadProfilePic(Login.this);
					new GetGroupList(Login.this, getGroupList).start();

				} else {
					handler.sendEmptyMessage(ADMIN_GROUP_RESPONSE);
				}
				CommonFunctions.setPref(Login.this, CommonFunctions.IS_LOGIN,
						true);

			} else if (msg.what == 3) {
				disMissProgress();
				CommonFunctions.showAlert(Login.this,
						"Error while fetching contacts", Login.this
								.getResources().getString(R.string.warning));

			} else if (msg.what == 5) {
				disMissProgress();
				CommonFunctions
						.showAlert(
								Login.this,
								"Invalid username or password. Please check your credentials",
								Login.this.getResources().getString(
										R.string.warning));

			} else if (msg.what == ADMIN_GROUP_RESPONSE) {
				try {
					Bundle bundle = msg.getData();
					if (bundle != null) {
						String response = bundle.getString(KEY_GROUP_ADMIN);
						TribeWireUtil.parseUserGroups(Login.this, response);
						if (CommonFunctions.isInternetConnected(Login.this)) {
							TribeWireUtil.getAdminGroups(Login.this);
						} else {
							handler.sendEmptyMessage(USER_GROUP_RESPONSE);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();

				}

			} else if (msg.what == USER_GROUP_RESPONSE) {
				try {
					Bundle bundle = msg.getData();
					if (bundle != null) {
						String response = bundle.getString(KEY_USER_GROUP);

						TribeWireUtil.parseAdminGroup(response, Login.this);

					} else {
						handler.sendEmptyMessage(USER_GROUP_RESPONSE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					startGroupMemberService();
					pushHomeScreen();
				}
			}

		};

	};
	
	private void startGroupMemberService() {
		try {
			Intent intent  = new Intent(this, GroupMemberService.class);
			startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void pushHomeScreen() {
		try {
			// Commented as we have directly move the user to conversation
			// screen
			// CommonFunctions.checkMessagefromServer1(Login.this);
			Intent intent = new Intent(Login.this, MenuActivity.class);

			startActivity(intent);

			// CommonFunctions.startMessageService(Login.this);
			disMissProgress();
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Dismiss the Progress Dialog
	private static void disMissProgress() {

		try {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onContactResponse(String response) {
		Log.e("", "The Conatct Response is " + response);

		contactResponse = response;
		handler.sendEmptyMessage(2);

	}

	@Override
	public void onContactError(String onError) {
		Log.e("", "The Conatct Error  is " + onError);
		handler.sendEmptyMessage(3);
	}

	/**
	 * {"error":false,"error_msg":"","msg":
	 * "An email is just sent to gSingh at following email address gurdeep.singh180@gmail.com successfully!"
	 * }
	 */

	public IHttpRequest forgotPassWord = new IHttpRequest() {

		@Override
		public void onResponse(String response) {
			Log.e("", "========= Forgot Password response is " + response);

			try {
				if (response != null && response.length() > 0) {
					try {
						JSONObject jsonObject1 = new JSONObject(response);
						JSONObject jsonObject = jsonObject1
								.getJSONObject("response");
						String isError = jsonObject.getString("error");
						if (isError.equalsIgnoreCase("false")) {
							String message = jsonObject.getString("msg");
							if (progressDialog != null) {
								progressDialog.dismiss();
								progressDialog = null;

							}
							Toast.makeText(Login.this, message,
									Toast.LENGTH_LONG).show();

						} else {
							String message = jsonObject.getString("error_msg");
							if (progressDialog != null) {
								progressDialog.dismiss();
								progressDialog = null;

							}
							Toast.makeText(Login.this, message,
									Toast.LENGTH_LONG).show();

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onError(String onError) {
			Log.e("", "============ Error is " + onError);

		}
	};

	private static String KEY_GROUP_ADMIN = "admingroup";
	public IHttpRequest adminGroupRequest = new IHttpRequest() {

		@Override
		public void onResponse(String response) {
			Log.e("", "========== admin Group response \n" + response);

			Message message = new Message();
			message.what = USER_GROUP_RESPONSE;
			Bundle bundle = new Bundle();
			bundle.putString(KEY_USER_GROUP, response);

			message.setData(bundle);
			handler.sendMessage(message);

		}

		@Override
		public void onError(String onError) {
			Message message = new Message();
			message.what = USER_GROUP_RESPONSE;
			Bundle bundle = new Bundle();
			bundle.putString(KEY_USER_GROUP, onError);

			message.setData(bundle);
			handler.sendMessage(message);
		}
	};

	public IHttpRequest getGroupList = new IHttpRequest() {

		@Override
		public void onResponse(String response) {

			Log.e("",
					"========== The User Group Response from the server is \n"
							+ response);

			Message message = new Message();
			message.what = ADMIN_GROUP_RESPONSE;
			Bundle bundle = new Bundle();
			bundle.putString(KEY_GROUP_ADMIN, response);

			message.setData(bundle);
			handler.sendMessage(message);

		}

		@Override
		public void onError(String onError) {
			Message message = new Message();
			message.what = ADMIN_GROUP_RESPONSE;
			Bundle bundle = new Bundle();
			bundle.putString(KEY_GROUP_ADMIN, onError);

			message.setData(bundle);
			handler.sendMessage(message);
		}
	};

	Bitmap downloadBitmap(String url) {
		final int IO_BUFFER_SIZE = 4 * 1024;

		/* AndroidHttpClient is not allowed to be used from the main thread */
		/*
		 * final HttpClient client = (mode == Mode.NO_ASYNC_TASK) ? new
		 * DefaultHttpClient() : AndroidHttpClient.newInstance("Android");
		 */

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

					// return BitmapFactory.decodeStream(new FlushedInputStream(
					// inputStream));

					return CommonFunctions.decodeSampledBitmapFromResource1(
							inputStream, 300, 300, entity, url);
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
			Log.e("", "I/O error while retrieving bitmap from " + url);
		} catch (IllegalStateException e) {
			getRequest.abort();
			Log.e("", "Incorrect URL: " + url);
		} catch (Exception e) {
			getRequest.abort();
			Log.e("", "Error while retrieving bitmap from " + url);
		} finally {

			/*
			 * if ((client instanceof HttpClient)) { ((HttpClient)
			 * client).close(); }
			 */
		}
		return null;
	}

	private void saveBitmapToSDCard(Bitmap profile_bm) {
		try {
			String uploadFilePath = "";
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			boolean isImageWritten = profile_bm.compress(
					Bitmap.CompressFormat.JPEG, 100, bos);
			if (isImageWritten) {

			}

			byte bitmapImage[] = bos.toByteArray();
			File file1 = getTempFile(this);
			FileOutputStream fos = new FileOutputStream(file1);
			uploadFilePath = file1.toString();
			Log.e("", "The Compressed bitmap File path is " + uploadFilePath);
			CommonFunctions.setPref(Login.this, AddEditImage.LOCAL_PROFILE_PIC,
					"" + file1.toString());
			fos.write(bitmapImage);
			fos.close();

			// handler4.sendEmptyMessage(2);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static File getTempFile(Context context) {
		File path1 = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path1.exists()) {
			path1.mkdir();
			Log.e("", "Directory Created Again");
		}

		return new File(path1, System.currentTimeMillis() + "eyesay.jpeg");
	}
}
