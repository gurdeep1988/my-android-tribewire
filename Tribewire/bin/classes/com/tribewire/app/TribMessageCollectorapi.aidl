package com.tribewire.app;

import com.tribewire.app.MessageSearchResult;

import com.tribewire.app.MessageCollectorListener;
interface TribMessageCollectorapi {

	
	MessageSearchResult getLatestSearchResult();
	void addListenerTrib(MessageCollectorListener listener);

	void removeListenerTrib(MessageCollectorListener listener);
}