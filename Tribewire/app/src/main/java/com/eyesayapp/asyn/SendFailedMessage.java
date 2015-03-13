package com.eyesayapp.asyn;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.eyesayapp.Utils.CommonFunctions;

public class SendFailedMessage extends AsyncTask<String, Void, JSONObject>{
	
	
	
	private Context context;
	private String file_url;
	private String isdraft;
	private String conversationId;
	ISendFailedMessage sendFailedMessage;
	private int position;
	public SendFailedMessage(Context context,ISendFailedMessage sendFailedMessage, int position) {
		this.context  = context;
		this.sendFailedMessage = sendFailedMessage;
		this.position  = position;
	}
	@Override
	protected JSONObject doInBackground(String... params) {
		
		String response  = "";
		
		String baseFileUrl   = params[0];
		String number  = params[1];
		String names  = params[2];
		boolean reply  = false;
		String conversationId = params[3];
		String message   = params[4];
		try {
			message  = new String(Base64.decode(message, Base64.DEFAULT));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		String messageId   = params[5];
		boolean flag = false;
		
		JSONObject jsonObject = CommonFunctions.SendFailedTextMessage(context, flag, baseFileUrl, number, names, reply, conversationId, message,messageId);
		if (jsonObject!=null && jsonObject.length()>0) {
			
			try {
				jsonObject.put("position", ""+position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jsonObject;
		}
		else {
			
			return null;
		}
		
		
		//return false;
	}
	
	
	protected void onPostExecute(JSONObject result) {
		
		 try {
			if (result!=null && result.length()>0) {
				sendFailedMessage.isFailedMessageSent(true, result);
			}
			else {
				sendFailedMessage.isFailedMessageSent(true,null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	};

}
