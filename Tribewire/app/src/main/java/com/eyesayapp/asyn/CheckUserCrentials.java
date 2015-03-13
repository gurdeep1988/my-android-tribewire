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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.tribewire.app.Login;

/**
 * 
 * A async call to Web-Server which checks the User Login credentials with the
 * help of username and password
 */
public class CheckUserCrentials extends AsyncTask<Object, Void, String> {

	private Context context;

	public CheckUserCrentials(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(Object... params) {

		String responseResult = null;

		if (params != null && params.length > 0) {
			String userName = (String) params[0];
			String password = (String) params[1];
			String registrationId   = (String)params[2];
			Log.e("", "User name === " + userName + " Password == " + password +" == Registration Id "+registrationId);
			if (userName != null && userName.length() > 0 && password != null
					&& password.length() > 0) {
				try {
					HttpPost httpPost = new HttpPost(
							CommonFunctions.DEMO_SERVER_URL
									+ CommonFunctions.LOGIN_URL);
										
					List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
					nameValuePair.add(new BasicNameValuePair("api_key", Constants.API_KEY));
					nameValuePair.add(new BasicNameValuePair("email", userName.trim()));
					nameValuePair.add(new BasicNameValuePair("password",  password.trim()));
					nameValuePair.add(new BasicNameValuePair("device_token", registrationId));
					
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));					
					DefaultHttpClient client = new DefaultHttpClient();	
					HttpResponse httpResponse = client.execute(httpPost);
					int statusCode = httpResponse.getStatusLine()
							.getStatusCode();
					Log.e("", "The Status Code from the Server is "
							+ statusCode);
					if (statusCode == HttpStatus.SC_OK) {
						responseResult = EntityUtils.toString(httpResponse
								.getEntity());
					}

					else {
						responseResult = statusCode
								+ " Server Error , Please try after some time ";
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				responseResult = "Invalid username and password ";
			}

		}

		return responseResult;
	}

	@Override
	protected void onPostExecute(String result) {

		try {
			if (result != null) {
				if (context instanceof Login) {
					Login login = (Login) context;
					if (result
							.contains(" Server Error , Please try after some time ")) {
						login.onError(result);
					} else if (result
							.contains("Invalid username and password ")) {
						login.onError(result);
					} else {
						login.onResponse(result);
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPostExecute(result);
	}

}
