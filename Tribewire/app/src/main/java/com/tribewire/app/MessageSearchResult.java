package com.tribewire.app;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageSearchResult implements Parcelable{

	public static final Creator<MessageSearchResult> CREATOR = new Creator<MessageSearchResult>() {
		@Override
		public MessageSearchResult[] newArray(int size) {
			return new MessageSearchResult[size];
		}
		
		@Override 
		public MessageSearchResult createFromParcel(Parcel source) {
			return new MessageSearchResult(source);
		}
	};
	private List<String> messageLst;
	
	public MessageSearchResult() {
		messageLst = new ArrayList<String>();
	}
	
	@SuppressWarnings("unchecked")
	private MessageSearchResult(Parcel source) {
		messageLst = source.readArrayList(String.class.getClassLoader());
	}
	
	public void addTweet(String message) {
//		if (messageLst.size()>0) {
	//		messageLst.clear();
			messageLst.add(message);
		//}
	}
	
	public List<String> getMessages() {
		return messageLst;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(messageLst);
		
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
