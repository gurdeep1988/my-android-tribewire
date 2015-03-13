package com.eyesayapp.asyn;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.tribewire.app.Emergency;
import com.tribewire.app.FrameActivity;
import com.tribewire.app.MenuActivity;
import com.tribewire.app.TenFourConversations;

/**
 * 
 * A async call to Web-Server which checks the User Login credentials with the
 * help of username and password
 */
public class SignOut extends Thread {

	private Context context;

	private Fragment conversations;

	public SignOut(Context context) {
		this.context = context;
	}

	public SignOut(Fragment context) {
		this.conversations = context;
	}

	@Override
	public void run() {
		Looper.prepare();
		doInBackground();
		Looper.loop();
		super.run();
	}

	String errorResponse   = "";
	protected String doInBackground() {

		String responseResult = null;

		try {
			HttpPost httpPost = new HttpPost(CommonFunctions.DEMO_SERVER_URL
					+ CommonFunctions.LOGOUT_URL);

			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
			nameValuePair.add(new BasicNameValuePair("api_key",
					Constants.API_KEY));
			String session = CommonFunctions.getPref(context,
					Constants.SESSION_TAG, "");
			Log.e("", " The Api Key " + Constants.API_KEY + " == Session "
					+ session);
			session = session.trim();
			nameValuePair.add(new BasicNameValuePair(Constants.SESSION_TAG,
					session));

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse = client.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.e("", "The Status Code from the Server is " + statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				responseResult = EntityUtils.toString(httpResponse.getEntity());
				Log.e("", "=======  The Response from Server is "
						+ responseResult);
				try {
					JSONObject jsonObject1 = new JSONObject(responseResult);
					JSONObject jsonObject = jsonObject1
							.getJSONObject("response");
					String error = jsonObject.getString("error");
					if (error.equalsIgnoreCase("false")) {
						responseResult = "false";
					} else {
						 errorResponse   = responseResult;
						responseResult = "true";
						
					}
				} catch (Exception e) {
					e.printStackTrace();
					responseResult = "true";
				}
			}

			else {
				responseResult = statusCode
						+ " Server Error , Please try after some time ";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		onPostExecute(responseResult);
		return responseResult;
	}

	protected void onPostExecute(String result) {

		try {
			if (result != null) {

				if (context != null) {
					if (context instanceof MenuActivity) {
						MenuActivity login = (MenuActivity) context;
						if (result
								.contains(" Server Error , Please try after some time ")) {
							login.onError(result);
						} else if (result
								.contains("Invalid username and password ")) {
							login.onError(result);
						} else if (result.contains("true")) {
						 login.onError(errorResponse);
						//	login.onResponse(result);
						} else if (result.contains("false")) {
							login.onResponse(result);

						} else {
							login.onError(result);
						}

					}

				} 

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
