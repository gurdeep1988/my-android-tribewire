package com.eyesayapp.model;

import java.io.Serializable;

public class CompanyMessages implements Serializable{

	private String conversationId;
	private String recipientNames;
	private String lastMessage;
	private String conversationStartDate;
	private String senderId;
	private String senderName;
	private int readStatus;
	String attacheMessageType;
	String attachMessageUrl;
	private String subject;
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getSubject() {
		return subject;
	}
	public String getAttacheMessageType() {
		return attacheMessageType;
	}
	public String getAttachMessageUrl() {
		return attachMessageUrl;
	}
	
	public void setAttacheMessageType(String attacheMessageType) {
		this.attacheMessageType = attacheMessageType;
	}
	public void setAttachMessageUrl(String attachMessageUrl) {
		this.attachMessageUrl = attachMessageUrl;
	}
	
	
	
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	public void setReadStatus(int readStatus) {
		this.readStatus = readStatus;
	}
	
	public int getReadStatus() {
		return readStatus;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getRecipientNames() {
		return recipientNames;
	}

	public void setRecipientNames(String recipientNames) {
		this.recipientNames = recipientNames;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public String getConversationStartDate() {
		return conversationStartDate;
	}

	public void setConversationStartDate(String conversationStartDate) {
		this.conversationStartDate = conversationStartDate;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

}
