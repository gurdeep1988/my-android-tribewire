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

public class ContactSync extends AsyncTask<Void, Void, String> {

	private Context context;
    private IContactResult iContactResult;
	public ContactSync(Context context, IContactResult  iContactResult) {
		this.context = context;
		this.iContactResult  = iContactResult;
	}

	@Override
	protected String doInBackground(Void... params) {

		String responseResult = null;



		try {

			try {
				HttpPost httpPost = new HttpPost(
						CommonFunctions.DEMO_SERVER_URL
								+ CommonFunctions.CONTACT_URL);

				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(
						4);
				nameValuePair.add(new BasicNameValuePair("api_key",
						Constants.API_KEY));
				nameValuePair.add(new BasicNameValuePair("user_id",
						CommonFunctions.getPref(context,
								CommonFunctions.REF_ID, "")));
				nameValuePair.add(new BasicNameValuePair("comp_id",
						CommonFunctions.getPref(context, Constants.COM_ID,
								"")));
				nameValuePair.add(new BasicNameValuePair("session",
						CommonFunctions.getPref(context, Constants.SESSION,
								"")));

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

		} catch (Exception e) {
			e.printStackTrace();
		}

	

		return responseResult;
	}

	@Override
	protected void onPostExecute(String result) {

		try {
			if (result != null) {
				
					if (result
							.contains(" Server Error , Please try after some time ")) {
						iContactResult.onContactError(result);
					} else if (result
							.contains("Invalid username and password ")) {
						iContactResult.onContactError(result);
					} else {
						iContactResult.onContactResponse(result);
					}

				

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPostExecute(result);
	}

}
