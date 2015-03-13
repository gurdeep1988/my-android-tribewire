package com.eyesayapp.asyn;

public interface IHttpRequest {

	public void onResponse(String response);

	public void onError(String onError);
}
