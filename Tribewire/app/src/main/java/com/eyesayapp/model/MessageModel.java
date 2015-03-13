package com.eyesayapp.model;


/* {"msg_id":"192",
	 * "sender":"Dave Brown",
	 * "text":"SGkgVGVzdGluZw==",
	 * "attachment_type":"0",
	 * "attached_file_name":"",
	 * "thumbnail":"",
	 * "date_time":"2013-09-12 07:29:08"},*/
public class MessageModel {

private String msg_id ;
private String sender ;
private String text ;
private String attachment_type ;
private String attached_file_name ;
private String thumbnail ;
private String date_time ;
public String getMsg_id() {
	return msg_id;
}
public void setMsg_id(String msg_id) {
	this.msg_id = msg_id;
}
public String getSender() {
	return sender;
}
public void setSender(String sender) {
	this.sender = sender;
}
public String getText() {
	return text;
}
public void setText(String text) {
	this.text = text;
}
public String getAttachment_type() {
	return attachment_type;
}
public void setAttachment_type(String attachment_type) {
	this.attachment_type = attachment_type;
}
public String getAttached_file_name() {
	return attached_file_name;
}
public void setAttached_file_name(String attached_file_name) {
	this.attached_file_name = attached_file_name;
}
public String getThumbnail() {
	return thumbnail;
}
public void setThumbnail(String thumbnail) {
	this.thumbnail = thumbnail;
}
public String getDate_time() {
	return date_time;
}
public void setDate_time(String date_time) {
	this.date_time = date_time;
}


	
}
