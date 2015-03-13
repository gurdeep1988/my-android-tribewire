package com.eyesayapp.asyn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.tribewire.app.ConversationMessages;

public class InBoxList extends Thread {

	Context ctx;
	IInbox inbox;

	public InBoxList(Context ctx, IInbox inbox) {
		this.ctx = ctx;
		this.inbox = inbox;
	}

	@Override
	public void run() {
		Looper.prepare();
		doInBackground();
		Looper.loop();
		super.run();
	}

	protected Boolean doInBackground() {

		boolean islist = getInBoxList();
		onPostExecute(islist);
		return islist;
	}

	protected void onPostExecute(Boolean result) {

		if (result != null) {
			inbox.fetchData(result.booleanValue());
		}

	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 */
	public boolean getInBoxList() {

		boolean messagePresent = false;
		try {

			// try {
			// DataBase db = new DataBase(ctx);
			// db.open();
			// boolean table = db.deleteAllContents(DataBase.Receive_table);
			// db.close();
			//
			// Log.e("",
			// "================ Table has been deleted successfully "+table+"=============");
			//
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			String api_key = Constants.API_KEY;
			String ref_id = CommonFunctions.getPref(ctx,
					CommonFunctions.REF_ID, "");
			String com_id = CommonFunctions.getPref(ctx, Constants.COM_ID, "");
			String session = CommonFunctions
					.getPref(ctx, Constants.SESSION, "");
			List<String> nameList = new ArrayList<String>();
			nameList.add(Constants.API_KEY_TAG);
			nameList.add(Constants.REF_ID_TAG);
			nameList.add(Constants.COMP_ID_TAG);
			nameList.add(Constants.SESSION_TAG);
			List<String> valueList = new ArrayList<String>();
			valueList.add(api_key);
			valueList.add(ref_id);
			valueList.add(com_id);
			valueList.add(session);

			String response = CommonFunctions
					.postNameValueDataOnServer(ctx, nameList.size(), nameList,
							valueList, CommonFunctions.DEMO_SERVER_URL
									+ CommonFunctions.GET_INBOX);

//			CommonFunctions.writeLog("==========  Old Message Response is "
//					+ response);
			Log.e("", "The Response is " + response);
                 try {
					if (ActivityStatus.getActivity()!=null && ActivityStatus.getActivity() instanceof ConversationMessages) {
				    		 // Dont do any thing as Current Conversation Screen is in front
					}
					else {
						messagePresent   = parseData(response);	
					}
				} catch (Exception e) {
					e.printStackTrace();
				}      
			  
			  
		

		} catch (Exception e) {
			e.printStackTrace();
		}

		return messagePresent;

	}

	private boolean parseData (String response ){
		boolean messagePresent = false;
		try {
			if (response != null && response.length() > 0) {
				try {

					JSONObject jsonObject1 = new JSONObject(response);
					JSONObject jsonObject = jsonObject1
							.getJSONObject("response");
					// JSONObject jsonObject = new JSONObject(response);
					String error = jsonObject.getString("error");
					if (error.equalsIgnoreCase("false")) {
						JSONArray inbox_List = jsonObject
								.getJSONArray("inbox_list");
 Log.e("", "================ The inbox array length is ======================= "+inbox_List.length());
						if (inbox_List != null && inbox_List.length() > 0) {
							DataBase db = null;
							db = new DataBase(ctx);
							db.open();
							try {

								for (int i = 0; i < inbox_List.length(); i++) {
									JSONObject jsonObject2 = inbox_List
											.getJSONObject(i);

									/**
									 * "_id", "message_id", "sender",
									 * "receiver", "message_type", "file_url",
									 * "date_time", "type", "status",
									 * "conversation_Id", "reply",
									 * "sender_status",
									 * "favourite","played","groupreceiver"
									 * ,"textmessage"
									 */
									/*
									 * db.insert( DataBase.Receive_table,
									 * DataBase.Receive_int, new String[] {
									 * "0"//,"message_id" CommonFunctions
									 * .getPref( context,
									 * CommonFunctions.PHONE_NUMBER,
									 * ""),//"sender" name, // receiver Type, //
									 * message_type file_url,// file_url "" +
									 * d,//date_time "0", //type == whether
									 * incoming or outgoing "1",// status ==
									 * message read status converationId,//
									 * conversation_Id "0", // reply === Just
									 * neglect this paramter "" +
									 * sender_status,// sender_status // Just
									 * neglect this param , otherwise used for
									 * black status number, // favourite == Not
									 * in used as we get "0", // played == to
									 * check msg played or not "0", //
									 * groupreceiver == neglect this param
									 * message// textmessage == to store message
									 * });
									 */
									/*
									 * "{"msg_id":"MjkyOQ==",
									 * "sender_id":"MQ==",
									 * "sender":"Susan Cross",
									 * "date_time":"2014-07-01 15:58:30",
									 * "attachment_type":"3",
									 * "attached_file_name"
									 * :"1404230308_g5gaDxYPgq.jpeg",
									 * "conversation_id":"MzAw",
									 * "recipient_names":"Dave Brown",
									 * "last_message":"aW1hZ2UgdGVzdA==",
									 * "conversation_start_date"
									 * :"2014-06-22 15:19:57",
									 * "last_message_date"
									 * :"2014-07-01 15:58:30"}
									 */
									String conversation_Id  = jsonObject2
											.getString("conversation_id");
									String isEmergency   = jsonObject2.getString("is_emergency");
									String isRead  = jsonObject2.getString("isread");
									int count = db
											.getCount(
													DataBase.Receive_table,
													DataBase.Receive_int,
													"conversation_Id ='"
															+ jsonObject2
																	.getString("conversation_id")
															+ "'");
									if (!(count > 0)) {
										ContentValues cv = new ContentValues();
										try {
											cv.put("message_id",jsonObject2.getString("msg_id"));
										}
										catch (Exception e) {
											e.printStackTrace();
										}
//										cv.put("sender", CommonFunctions
//												.getPref(ctx,
//														CommonFunctions.REF_ID,
//														""));

										try {
											cv.put("sender", jsonObject2.getString("sender"));
										}
										catch (Exception e) {
											e.printStackTrace();
										}
										String receiver = CommonFunctions
												.getRecipientsId(
														db,
														jsonObject2
																.getString("recipient_names"));
										
										String receiverAll = jsonObject2
												.getString("recipient_names");
 
										cv.put("receiver", receiver);

										try {
											// attachment_type = 3 // Its an
											// image
											// attachment_type = 0 // Its an
											// text message only
											// attachment_type = 2 // Its an
											// Audio File
											String messageType = jsonObject2
													.getString("attachment_type");
											String file_Url = jsonObject2
													.getString("attached_file_name");
											if (messageType.equalsIgnoreCase("3")) {
											   file_Url  = Constants.IMAGE_BASE_URL +file_Url;	
											}
											else if (messageType.equalsIgnoreCase("2")) {
												file_Url  = Constants.AUDIO_BASE_URL +file_Url;
											}
											
											cv.put("message_type", messageType);
											cv.put("file_url", file_Url);

										} catch (Exception e) {
											e.printStackTrace();
											cv.put("message_type", "0");
											cv.put("file_url", "");
										}
										try {
											cv.put("date_time",
													""
															+ CommonFunctions
																	.getLongMillisfromDate(jsonObject2
																			.getString("last_message_date")));

											
										} catch (Exception e) {
											e.printStackTrace();
										}
										cv.put("type", "1");
										 try {
											String isread  =   jsonObject2.getString("isread");
											 cv.put("status", isread);
											 
										} catch (Exception e) {
											e.printStackTrace();
										}
										
										cv.put("conversation_Id", jsonObject2
												.getString("conversation_id"));
										cv.put("favourite", "56");
										cv.put("played", "0");

										cv.put("textmessage", jsonObject2
												.getString("last_message"));
										//if (!receiverAll
											//	.equalsIgnoreCase("all")) {
											db.insert_content(
													DataBase.Receive_table,
													DataBase.Receive_int, cv);
											ContentValues values  = new ContentValues();
											values.put(DataBase.conversationId, jsonObject2
													.getString("conversation_id"));
											values.put(DataBase.isEmergency, isEmergency);
											values.put(DataBase.isRead, isRead);
											db.insert_content(DataBase.emergency_table, DataBase.emergency_int, values);
										//}
										

									} else {
										ContentValues cv = new ContentValues();
										cv.put("message_id", jsonObject2.getString("msg_id"));
//										cv.put("sender", CommonFunctions
//												.getPref(ctx,
//														CommonFunctions.REF_ID,
//														""));
										cv.put("sender", jsonObject2.getString("sender"));
										String receiver = jsonObject2
												.getString("recipient_names");
										// String receiver = CommonFunctions
										// .getRecipientsId(
										// db,
										// jsonObject2
										// .getString("recipient_names"));
										//
										// cv.put("receiver", receiver);

										try {
											// attachment_type  = 3  // Its an image
											// attachment_type  = 0 // Its an text message only 
											//attachment_type  = 2 // Its an Audio File 
											String messageType  = jsonObject2.getString("attachment_type");
											String file_Url   = jsonObject2.getString("attached_file_name");
											if (messageType.equalsIgnoreCase("3")) {
												   file_Url  = Constants.IMAGE_BASE_URL +file_Url;	
												}
												else if (messageType.equalsIgnoreCase("2")) {
													file_Url  = Constants.AUDIO_BASE_URL +file_Url;
												}
											cv.put("message_type", messageType);
											cv.put("file_url", file_Url);
										} catch (Exception e) {
										e.printStackTrace();
										cv.put("message_type", "0");
										cv.put("file_url", "");
										}
										

										try {
											cv.put("date_time",
													""
															+ CommonFunctions
																	.getLongMillisfromDate(jsonObject2
																			.getString("last_message_date")));
										} catch (Exception e) {
											e.printStackTrace();
										}

										cv.put("type", "1");

										try {

											Cursor c = db
													.fetch(DataBase.Receive_table,
															DataBase.Receive_int,
															"conversation_id = '"
																	+ jsonObject2
																			.getString("conversation_id")
																	+ "'");
											if (c != null && c.getCount() > 0) {
												String textMessage = c
														.getString(15);
//												if (textMessage
//														.equalsIgnoreCase(jsonObject2
//																.getString("last_message"))) {
//													// cv.put("status", "0");
//												} else {
//													cv.put("status", "1");
//												}
												 try {
														String isread  =   jsonObject2.getString("isread");
														 cv.put("status", isread);
														 
													} catch (Exception e) {
														e.printStackTrace();
													}
//												Log.e("",
//														"======== DB Text Message is ======"
//																+ textMessage
//																+ " === Real Message "
//																+ jsonObject2
//																		.getString("last_message"));
											}
										} catch (Exception e) {
											e.printStackTrace();
										}

										cv.put("conversation_Id", jsonObject2
												.getString("conversation_id"));
										cv.put("favourite", "56");
										cv.put("played", "0");

										cv.put("textmessage", jsonObject2
												.getString("last_message"));
										//if (!receiver.equalsIgnoreCase("all")) {
											db.update(
													DataBase.Receive_table,
													DataBase.Receive_int,
													" conversation_Id = '"
															+ jsonObject2
																	.getString("conversation_id")
															+ "'", cv);
											ContentValues values  = new ContentValues();
											//values.put(DataBase.conversationId, conversation_Id);
											values.put(DataBase.isEmergency, isEmergency);
											values.put(DataBase.isRead, isRead);
											String where  = DataBase.conversationId+"= '"+jsonObject2
													.getString("conversation_id")+"'";
											db.update(DataBase.emergency_table, DataBase.emergency_int, where, values);
											
										//}

										// db.insert_content(DataBase.Receive_table,
										// DataBase.Receive_int, cv);
									}

								}
								
							int uncount  = 	db.getCount(DataBase.emergency_table, DataBase.emergency_int, "isread  = 0");
							int readcount  = 	db.getCount(DataBase.emergency_table, DataBase.emergency_int, "isread  = 1");
							int Emergencycount  = 	db.getCount(DataBase.emergency_table, DataBase.emergency_int, null);
							int totalConvCount  = 	db.getCount(DataBase.Receive_table, DataBase.Receive_int, null);
                 Log.e("In the inboxlist class ", "The unread Count  "+uncount+"\n=== The Read Count is "+readcount+" \n======= Total ER Count "+Emergencycount +" \nTotal Conversation Count "+totalConvCount);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								db.close();
							}

							messagePresent = true;
						} else {
							messagePresent = false;
						}

					}

					else {
						messagePresent = false;

					}
					// handler2.sendEmptyMessage(8);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messagePresent;
	}
	
}
