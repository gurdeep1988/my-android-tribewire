package com.eyesayapp.asyn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;

//public class GetGroupList extends AsyncTask<Void, Void, Boolean> {
public class GetGroupList extends Thread {
	private Context context;
    private IHttpRequest iHttpRequest;
    private String responseData="";
	
	public GetGroupList(Context context, IHttpRequest iHttpRequest) {
		this.context = context;
		this.iHttpRequest   = iHttpRequest;
	}

	
	@Override
	public void run() {
		Looper.prepare();
		doInBackground();
		Looper.loop();
		super.run();
	}
	protected Boolean doInBackground() {
		boolean isGroupListRetreived = false;

		String api_key = Constants.API_KEY;
		String ref_id = CommonFunctions.getPref(context,
				CommonFunctions.REF_ID, "");

		String session = CommonFunctions
				.getPref(context, Constants.SESSION, "");

		List<String> nameList1 = new ArrayList<String>();
		nameList1.add(Constants.API_KEY_TAG);
		nameList1.add(Constants.REF_ID_TAG);

		nameList1.add(Constants.SESSION_TAG);

		List<String> valueList1 = new ArrayList<String>();
		valueList1.add(api_key);
		valueList1.add(ref_id);

		valueList1.add(session);

		try {
			String response = CommonFunctions.postNameValueDataOnServer(context,
					nameList1.size(), nameList1, valueList1,
					CommonFunctions.DEMO_SERVER_URL + CommonFunctions.GET_GROUP_LIST);
           Log.e("", "================ The Group List response from the Server is "+response);
           
           if (response!=null && response.length()>0) {
			try {
				
				JSONObject jsonObject1  = new JSONObject(response);
				                 JSONObject jsonObject  = jsonObject1.getJSONObject("response");
				String error  =    jsonObject.getString("error");
				if (error.equalsIgnoreCase("false")) {
					isGroupListRetreived = true;
					
					responseData  = response;
				}
				else {
					isGroupListRetreived = false;
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
				iHttpRequest.onError("Response");
			}
        	   
		}
           
		} catch (Exception e) {
			e.printStackTrace();
			iHttpRequest.onError("Response");
		}
		
		
	       onPostExecute(isGroupListRetreived);	
		return isGroupListRetreived;
	}

	
	protected void onPostExecute(Boolean result) {
		if (result.booleanValue()) {
			iHttpRequest.onResponse(responseData);
		}
		else {
			iHttpRequest.onError("Response");
		}
		//super.onPostExecute(result);
	}
}
