package com.eyesayapp.model;

import java.io.Serializable;
import java.util.Set;

public class FriendModel implements Serializable{

	private String friendId;
	private String imgUrl;
	private String friendNumber;
	private String friendName;
	private String friendStatus;
	
	public String getFriendStatus() {
		return friendStatus;
	}
	
	public void setFriendStatus(String friendStatus) {
		this.friendStatus = friendStatus;
	}
	
	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}
	
	public String getFriendNumber() {
		return friendNumber;
	}
	
	public void setFriendNumber(String friendNumber) {
		this.friendNumber = friendNumber;
	}
	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

}
