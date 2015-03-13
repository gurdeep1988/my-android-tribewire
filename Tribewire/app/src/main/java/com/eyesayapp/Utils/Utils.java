package com.eyesayapp.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Utils {

	public static final String RECORD_AUDIO_KEY = "record_audio";
	public static final String RECORD_VIDEO_KEY = "record_video";
	public static final boolean RECORD_AUDIO_VALUE = true;
	public static final boolean RECORD_VIDEO_VALUE = true;
	public static final boolean RECORD_TAB_AUDIO_VALUE = false;
	public static final boolean RECORD_TAB_VIDEO_VALUE = false;
	public static final int CONTACT_SUCCESSFULLY_ADDED = 456;
	public static final int RESULT_FILL_FRIENDS = 654;
	public static final int EYE_SAY_VIDEO_SAVE_RESULT = 333;
	public static final int EYE_SAY_AUDIO_SAVE_RESULT = 444;
	public static final String MEDIA_MESSAGE_KEY = "audio_video_message";
	public static final String MESSAGE_AUDIO = "audio";
	public static final String MESSAGE_VIDEO = "video";
	public static final String MESSAGE_IMAGE = "image";
	public static final String MESSAGE_DRAFTS = "drafts";
	public static final String DIALOG_KEY = "dialog";

	// TenfourConversation reply Constants

	public static final String REPLY_NUMBER = "number";
	public static final String REPLY_FRIEND_NAME = "friendName";
	public static final String REPLY_GR_DATA = "grdata";
	public static final String REPLY_STATUS = "reply";
	public static final String REPLY_STATUS_GROUP = "reply_group";
	public static final String REPLY_MESSAGE_ID = "message_id";
	public static final String REPLY_CONVERSATION_ID = "conversation_id";
	public static final String SEND_SINGLE_MESSAGE = "send_single_message";
	
	////////////////
	public static final int SINGLE_BACKTOTILE_RESULT = 784;
	public static final int GROUP_BACKTOTILE_RESULT = 785;
	public static final int DRAFTS_MESSAGE_SEND_RESULT = 786;
	
	public static final String TEXT_MESSAGE_KEY   = "textmessage";
	public static final String TEXT_IMAGE_URL_KEY   = "imageurl";
	
	public static final String FLURRY_API_KEY = "3Y56K6VX6Z5MJ8JD6JV8";
	/**
	 * {"success":"1","conversation": [{"message_id":"1346",
	 * "sender":"15555550104", "receiver":"19988339759",
	 * "conversation_Id":"(null)", "message_type":"0",
	 * "file_url":"http:\/\/is.gd\/jTL3wJ", "date_time":"376510298.250601"} ]}
	 */

	/**
	 * 
	 * @param response
	 * @param ctx
	 * 
	 *            private static final String TABLE_3_CREATE = "create table " +
	 *            Receive_table + "(sr_no integer primary key autoincrement," +
	 *            "message_id integer ," + "sender text not null," +
	 *            "receiver text not null," + "message_type integer," +
	 *            "file_url text not null," + "date_time text not null," +
	 *            "type integer," + "status integer," + "conversation_Id text,"
	 *            + "reply integer default 0," +
	 *            "sender_status integer default 1,favourite integer);";
	 */
	static String test_file_url = "";
	public static void parseSentReceiveMessage(String response, Context ctx) {
		// TODO Auto-generated method stub

		JSONObject jsonObject = null;
		DataBase db = null;
		try {
			jsonObject = new JSONObject(response);
			String success = jsonObject.getString("success");
			if (success.equalsIgnoreCase("1")) {
				JSONArray jsonArray = jsonObject.getJSONArray("conversation");
				if (jsonArray.length() > 0) {
					db = new DataBase(ctx);
					db.open();
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject msgDetail = jsonArray.getJSONObject(i);
					String sender = msgDetail.getString("sender").trim();
					sender = sender.replace("(", "");
					sender = sender.replace(")", "");
					sender = sender.replace("-", "");
					String receiverString = msgDetail.getString("receiver").trim();
					String message_type = msgDetail.getString("message_type").trim();
					String textmessage  = msgDetail.getString("textMessage").trim();
					textmessage   = textmessage.replace("\n", "");
					String file_url = msgDetail.getString("file_url").trim();
					String date_time = msgDetail.getString("date_time").trim();
					
					 if (!(test_file_url.equalsIgnoreCase(date_time))) {
							ContentValues cv = new ContentValues();
							
							String[] receiverArray = receiverString.split(",");
							for (int j = 0; j < receiverArray.length; j++) {
								boolean multipleCheck = false;
								String receiver = receiverArray[j];
								receiver=	receiver.replace("(", "");
								receiver=	receiver.replace(")", "");
								receiver=	receiver.replace("-", "");
								Log.e("Record Value", "Sender="+sender +",Receiver ="+receiver+",Type="+message_type+", Url="+file_url+", Date ="+date_time+" textmessage "+textmessage);
								if (receiverArray[j] != null) {
									
//									if (receiver.startsWith("+")||receiver.startsWith("0")) {
//										receiver =receiver.substring(1);
//									}
//									receiver = receiver.replace("-", "");
//									receiver  = receiver.replace("(", "");
//									receiver  = receiver.replace(")", "");
									String tempSender="";
									String tempReceiver="";
									if (sender.equalsIgnoreCase(CommonFunctions
											.getPref(ctx, CommonFunctions.PHONE_NUMBER,
													""))) {
									        // sender = receiver;
									         //receiver = sender;
										tempSender = sender;
						 				tempReceiver = receiver;
										// Outgoing
										cv.put("type", "0");
									} else {
										// Incoming
										tempSender=CommonFunctions
												.getPref(ctx, CommonFunctions.PHONE_NUMBER,
														"");
										tempReceiver = sender;
										//receiver = sender;
										//sender = receiver;
										cv.put("type", "1");
										multipleCheck = true;
									}
									cv.put("sender", tempSender);
								//	if (receiver.startsWith("+")) {
										//receiver = receiver.substring(1);
									//}
									
									String name = getName2(tempReceiver,db);
									cv.put("receiver", name);
									cv.put("message_type", message_type);
									cv.put("textmessage", textmessage);
									
									cv.put("file_url", file_url);
					
									try {
								    	if (date_time.contains(".")) {
								    	try {
								    		Log.e("", "The Original Date is  "+date_time);
								    		StringTokenizer stringTokenizer = new StringTokenizer(date_time,".");
								    		while (stringTokenizer
													.hasMoreElements()) {
												String iphoneDate = (String ) stringTokenizer
														.nextElement().toString();
												Log.e("", "Content Value  "+cv.toString());
												Log.e("", "************* Sender "+tempSender+" Receiver "+tempReceiver+" Message Type" +message_type); 
												Log.e("", "The iphone Date is ======="+iphoneDate);
												 
										         long datetime = method2(Long.parseLong(iphoneDate));
										        // datetime = ((datetime)-(12*60*60*1000));	
										         cv.put("date_time", datetime);
										    		break;
												
											}
								    		
								    		// Sender=19465383066,Receiver =18591741225,Type=0, Url=http://is.gd/4deHM7, Date =377857290.969243
                                            
										} catch (Exception e) {
										    e.printStackTrace();
										}
								    	
								    	}
								    	else {
								    		try {
								    			long datetime = method2(Long.parseLong(date_time));
									    		//datetime = ((datetime)-(12*60*60*1000));
									    		cv.put("date_time", datetime);
											} catch (Exception e) {
												e.printStackTrace();
											}
								    	}
								    	
										
										
										
									} catch (Exception e) {
										e.printStackTrace();
										cv.put("date_time", date_time);
									}
									

									cv.put("status", 1);
									try {

										if (name != null) {
											String conversationId = fetchConversationID(name,db);
											if (conversationId != null) {
												cv.put("conversation_Id", conversationId);
											} else {
												cv.put("conversation_Id", "tfc"
														+ System.currentTimeMillis());
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

									cv.put("sender_status", 1);
									cv.put("favourite", "0");
									
									
									try {
										long rowId = db.insert_content(
												DataBase.Receive_table,
												DataBase.Receive_int, cv);
										Log.e("", "Data Inserted with RowId " + rowId);
									} catch (Exception e) {
										e.printStackTrace();
									}                                                                                 
									
									
								}
								if (multipleCheck) {
									break;
								}
							}
							test_file_url = date_time;
					} 


					// insertToDb(ctx, msgDetail.getString("sender"),
					// msgDetail.getString("receiver"),
					// msgDetail.getString("message_type"),
					// msgDetail.getString("file_url"),
					// msgDetail.getString("date_time"));

				}
			} else if (success.equalsIgnoreCase("0")) {

			}

		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

	}

	
	private static String getName(DataBase db, String number) {
		String name = "";
		Cursor c = null;
		try {
			Log.e("", "Database Name for Phone Number " + number);
			c = db.fetch(DataBase.Friends_table, DataBase.Friends_int,
					"number ='"+number+"'");
			if (c!=null && c.getCount()>0) {
				if (c.moveToFirst()) {
					name = c.getString(2);	
					return name;
				}
				else {
					return number;
					
				}
			}
			else {
				return number;
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return name;
	}
	
	private static String getName2(String phoneNumber,DataBase db) {
		
		String senderName=null;
		Cursor findName =null;
		try {
             
			        if (phoneNumber.length()>4) {
			        	findName = db.fetch(DataBase.Friends_table,
				                 DataBase.Friends_int,
				                 "number like'%" + ("" +phoneNumber.substring(3)+ "'"));      
					}
			        else {
			        	findName = db.fetch(DataBase.Friends_table,
				                 DataBase.Friends_int,
				                 "number like'%" + ("" +phoneNumber+ "'"));
			        }			        

	        if (findName.getCount() > 0) {
	           findName.moveToFirst();
	           if (findName.getCount() == 1) {
	              senderName = findName.getString(2);

	          } else {
	              while (!findName.isAfterLast()) {
	                 if (checkForEqual(findName.getString(3), "" + phoneNumber)) {
	                    senderName = findName.getString(2);
	                 }
	                 findName.moveToNext();
	              }
	           }
	        }

	        


		
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			findName.close();
		}
		if (!(senderName!=null && senderName.length()>0)) {
			senderName = phoneNumber;
		}
		return senderName;
		
		
	}

	public static boolean checkForEqual(String cellno1, String cellno2) {

		if (cellno1.startsWith("0"))
			cellno1 = cellno1.substring(1);

		if (cellno2.startsWith("0"))
			cellno2 = cellno2.substring(1);

		if (cellno2.contains("-"))
			cellno2 = cellno2.replace("-", "");

		if (cellno1.contains("-"))
			cellno2 = cellno2.replace("-", "");

		if (cellno1.length() > cellno2.length()) {
			int startIndex = (cellno1.length() - cellno2.length());
			if (cellno2.equals(cellno1.substring(startIndex)))
				return true;
		} else {

			int startIndex = (cellno2.length() - cellno1.length());
			if (cellno1.equals(cellno2.substring(startIndex)))
				return true;

		}
		return false;
	}

	private static int determineDifferenceInDays(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		long diffInMillis = calendar2.getTimeInMillis()
				- calendar1.getTimeInMillis();
		return (int) (diffInMillis / (24 * 1000 * 60 * 60));
	}

	public static long method2(long date_time) {
		Calendar calendar = Calendar.getInstance();		
		calendar.set(2001, 0, 1, 00, 00, 00);
		
		long two_thou_millis = calendar.getTimeInMillis();		
		Calendar calendar2 = Calendar.getInstance();	
		Date nowDate = new Date(calendar2.getTimeInMillis());

		int dstOffset = (calendar2.get(Calendar.DST_OFFSET)/1000);
        Log.e("", "==== DST OFFSET======="+(calendar2.get(Calendar.DST_OFFSET)/1000));
        Log.e("", "==== ZONE OFFSET=========="+(calendar2.get(Calendar.ZONE_OFFSET)/1000));
		
        TimeZone tz = TimeZone.getDefault();	
		int offsetFromUtc = tz.getOffset(nowDate.getTime());
		long requiredOffset  = offsetFromUtc/1000;
		requiredOffset  = requiredOffset+dstOffset;
		
		//System.out.println(offsetFromUtc/1000);
		// Thread.sleep(5000);
	//	long required1 = (((diffInDays * 24 * 60 * 60) + ((nowDate.getHours()) * 60 * 60)) + ((nowDate.getMinutes()) + (nowDate.getSeconds())));
		long required1 = ((calendar2.getTimeInMillis()-two_thou_millis)/1000);
		//System.out.println("Required " + required1);
		required1 = (required1-requiredOffset);
		long difference = (required1 - date_time);
		// long difference = required1-377132111;
		long requiredMillis = (System.currentTimeMillis() - (difference*1000));
		//requiredMillis = ((requiredMillis)+(12*60*60*1000));
		Log.e("", "Required Millis  is " + requiredMillis);
		
		
		//System.out.println("Difference " + new Date(requiredMillis));

		return requiredMillis;

	}
	
	

	public static String fetchConversationID(String name, DataBase db) {
		String conversationId = null;
		Cursor c = null;
		try {
			Log.e("", "The Name of the Receiver is "+name);
			c = db.fetch(DataBase.Receive_table, DataBase.Receive_int,
					"receiver ='" + name + "'");
			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				conversationId = c.getString(9);
				Log.e("", "Name "+name+"and Conversation Id "+conversationId);
			}
			else {
				Log.e("", "Name "+name+"=====No Conversation Id ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			c.close();
		}
		return conversationId;
	}

}
