package com.eyesayapp.asyn;

import org.json.JSONObject;

public interface ISendFailedMessage {

	
	public boolean isFailedMessageSent(boolean isSent, JSONObject jsonObject);
}
