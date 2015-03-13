package com.eyesayapp.asyn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;

public class GroupAsyn extends AsyncTask<Void, Void, Boolean> {

	private Context context;
	private String groupName;
	private String groupMembers;
	private boolean isUpdate;
	private IHttpRequest iHttpRequest;
	private String responseData = "";
	private String groupId;

	public GroupAsyn(Context context,String groupId, String groupName, String groupMembers,
			boolean isUpdate, IHttpRequest iHttpRequest) {
		this.context = context;
		this.groupName = groupName;
		this.groupMembers = groupMembers;
		this.isUpdate = isUpdate;
		this.iHttpRequest = iHttpRequest;
		this.groupId = groupId;
	}

	@Override
	protected void onPreExecute() {

		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean isGroupCreated = false;
		try {
			String api_key = Constants.API_KEY;
			String ref_id = CommonFunctions.getPref(context,
					CommonFunctions.REF_ID, "");

			String session = CommonFunctions.getPref(context,
					Constants.SESSION, "");

			List<String> nameList1 = new ArrayList<String>();
			nameList1.add(Constants.API_KEY_TAG);
			nameList1.add(Constants.REF_ID_TAG);
			nameList1.add(Constants.GROUP_NAME);
			if (isUpdate) {
				nameList1.add("group_id");
			}
			nameList1.add(Constants.GROUP_MEMBERS);
			nameList1.add(Constants.SESSION_TAG);

			List<String> valueList1 = new ArrayList<String>();
			valueList1.add(api_key);
			valueList1.add(ref_id);
			valueList1.add(groupName);
			if (isUpdate) {
				valueList1.add(groupId);
			}
			valueList1.add(groupMembers);
			valueList1.add(session);

			if (isUpdate) {
				String response1 = CommonFunctions.postNameValueDataOnServer(
						context, nameList1.size(), nameList1, valueList1,
						CommonFunctions.DEMO_SERVER_URL
								+ CommonFunctions.UPDATE_GROUP);
				if (response1 != null && response1.length() > 0) {
					try {

						JSONObject jsonObject1 = new JSONObject(response1);
                                JSONObject jsonObject=      jsonObject1.getJSONObject("response");						
						String error = jsonObject.getString("error");
						if (error.equalsIgnoreCase("false")) {
							responseData    = response1;
							isGroupCreated = true;
						}
						else {
							isGroupCreated = false;
						}

					} catch (JSONException e) {
						e.printStackTrace();
						iHttpRequest.onError("");
					}

				}
				Log.e("", "========= Group Create Reponse is " + response1);

			} else {
				String response1 = CommonFunctions.postNameValueDataOnServer(
						context, nameList1.size(), nameList1, valueList1,
						CommonFunctions.DEMO_SERVER_URL
								+ CommonFunctions.CREATE_GROUP);
				if (response1 != null && response1.length() > 0) {
					try {

						JSONObject jsonObject1 = new JSONObject(response1);
						JSONObject jsonObject = jsonObject1.getJSONObject("response");
						String error = jsonObject.getString("error");
						if (error.equalsIgnoreCase("false")) {
							isGroupCreated = true;
							responseData    = response1;
						}
						
						else {
							isGroupCreated = false;
						}

					} catch (JSONException e) {
						e.printStackTrace();
						iHttpRequest.onError("");
					}

				}

				Log.e("", "========= Group Update Reponse is " + response1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			iHttpRequest.onError("");
		}

		return isGroupCreated;
	}

	@Override
	protected void onPostExecute(Boolean result) {

		if (result.booleanValue()) {
			JSONObject jsonObject=null;
			try {
				 jsonObject   = new JSONObject(responseData);
				jsonObject.put("groupname", groupName);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			iHttpRequest.onResponse(jsonObject.toString());
		}
		else {
			iHttpRequest.onError("");
		}

		super.onPostExecute(result);
	}
}
