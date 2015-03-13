package com.eyesayapp.Utils;

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
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.tribewire.app.MessageSearchResult;

public class MessageSearch {

	int i = 0;
	private Context context;
	

	public MessageSearch(Context context) {
		this.context = context;
		
		
	}

	
	
	
	
	
	public MessageSearchResult messageSearch(String conV_id) {
		

		MessageSearchResult   messageSearchResult = new MessageSearchResult();

//		message = "Message " + i;
//		Log.e("", "==============The message is "+message);
//		i++;
//		
		
		messageSearchResult.addTweet(getMessage(conV_id));

		
		
		return messageSearchResult;

	}
	
	private static List<String> nameList;
	public  List<String> getNameList() {
		if (nameList == null) {
			nameList   = new ArrayList<String>();
			nameList.add(Constants.API_KEY_TAG);
			nameList.add(Constants.REF_ID_TAG);
			nameList.add(Constants.COMP_ID_TAG);
			nameList.add(Constants.SESSION_TAG);
			nameList.add(Constants.CONV_ID_TAG);
		} 
		
		return nameList;
		
		

	}
	private static List<String> valueList;
	public  List<String> getValueList(String conv_id) {
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
			valueList.add(conv_id);
			
		}
		return valueList;
		
	}

	/**
	 *  api_key:  user_id : base64 encoded  comp_id: base64 encoded conv_id
	 * : base64 encoded  session: you can get this from the login response
	 * 
	 * 
	 * 09-11 14:20:08.429: E/(21706): The Response from the Server is {"error":false,"error_msg":"","resp_msg":"Message Sent Successfully!","conv_id":"NA=="}
	 * 
	 * 
	 * 
	 * {"error":false,
	 * "error_msg":"",
	 * "conversation":
	 * [
	 * {"msg_id":"192",
	 * "sender":"Dave Brown",
	 * "text":"SGkgVGVzdGluZw==",
	 * "attachment_type":"0",
	 * "attached_file_name":"",
	 * "thumbnail":"",
	 * "date_time":"2013-09-12 07:29:08"},
	 * 
	 * {"msg_id":"193",
	 * "sender":"Dave Brown",
	 * "text":"aGk=",
	 * "attachment_type":"0",
	 * "attached_file_name":"",
	 * "thumbnail":"",
	 * "date_time":"2013-09-12 07:29:53"}
	 * ]}


	 */
	private String getMessage(String conV_id) {

		String responseResult = null;

		try {

			try {
				
				if (CommonFunctions.CheckNetConnectivity(context)) {

					HttpPost httpPost = new HttpPost(
							CommonFunctions.DEMO_SERVER_URL
									+ CommonFunctions.GET_NEW_MESSAGE);

					List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(
							5);
					nameValuePair.add(new BasicNameValuePair("api_key",
							Constants.API_KEY));
					nameValuePair.add(new BasicNameValuePair("user_id",
							CommonFunctions.getPref(context,
									CommonFunctions.REF_ID, "")));
					nameValuePair
							.add(new BasicNameValuePair("comp_id", CommonFunctions
									.getPref(context, Constants.COM_ID, "")));
					nameValuePair
							.add(new BasicNameValuePair("conv_id", conV_id));

					nameValuePair
							.add(new BasicNameValuePair("session", CommonFunctions
									.getPref(context, Constants.SESSION, "")));
					
					Log.e("", "The Api Key " + Constants.API_KEY);
					Log.e("",
							"The Api User_Id "
									+ CommonFunctions.getPref(context,
											CommonFunctions.REF_ID, ""));
					Log.e("",
							"The Api Com_Id "
									+ CommonFunctions.getPref(context,
											Constants.COM_ID, ""));
					Log.e("", "The Api Conv_Id "+conV_id );
					Log.e("",
							"The Api Session "
									+ CommonFunctions.getPref(context,
											Constants.SESSION, ""));
					
					

					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

					DefaultHttpClient client = new DefaultHttpClient();

					HttpResponse httpResponse = client.execute(httpPost);
					int statusCode = httpResponse.getStatusLine().getStatusCode();
					Log.e("", "The Status Code from the Server is " + statusCode);
					if (statusCode == HttpStatus.SC_OK) {
						responseResult = EntityUtils.toString(httpResponse
								.getEntity());
						
						try {
							JSONObject  jsonObject1  = new JSONObject(responseResult);
							      JSONObject jsonObject=        jsonObject1.getJSONObject("response");
							      String error  = jsonObject.getString("error");
							      if (error.equals("false")) {
							JSONArray jsonArray =		jsonObject.getJSONArray("conversation");
							
							
							if (jsonArray!=null && jsonArray.length()>0) {
	/**
	 * {"msg_id":"192",
		 * "sender":"Dave Brown",
		 * "text":"SGkgVGVzdGluZw==",
		 * "attachment_type":"0",
		 * "attached_file_name":"",
		 * "thumbnail":"",
		 * "date_time":"2013-09-12 07:29:08"},
	 */
								            
								
								
								
								String api_key = Constants.API_KEY;
								String ref_id = CommonFunctions
										.getPref(context, CommonFunctions.REF_ID, "");
								String com_id = CommonFunctions
										.getPref(context, Constants.COM_ID, "");
								String session = CommonFunctions
										.getPref(context, Constants.SESSION, "");
								
								List<String> nameList   = new ArrayList<String>();
								nameList.add(Constants.API_KEY_TAG);
								nameList.add(Constants.REF_ID_TAG);
								nameList.add(Constants.COMP_ID_TAG);
								nameList.add(Constants.SESSION_TAG);
								nameList.add(Constants.CONV_ID_TAG);
								List<String> valueList   = new ArrayList<String>();
								valueList.add(api_key);
								valueList.add(ref_id);
								valueList.add(com_id);
								valueList.add(session);
								valueList.add(conV_id);
						
								
								
								String response = CommonFunctions.postNameValueDataOnServer(context, nameList.size(), nameList, valueList, CommonFunctions.DEMO_SERVER_URL+CommonFunctions.UPDATE_READ_STATUS);
								Log.e("", " The Read Status Response is ========== "+response);
								
								
								
							}
								}
							
						} catch (Exception e) {
							// TODO: handle exception
						}
						
						
						Log.e("", "------------Get Message response from Server is -----------"+responseResult);
					}

					else {
						responseResult = null;
					}

				
				}
				else {
					
					Log.e("", "============== Can not fetch message because no internet connection================");
					
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseResult;
	}

}
