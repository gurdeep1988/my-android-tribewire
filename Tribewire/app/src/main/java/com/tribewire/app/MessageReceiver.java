package com.tribewire.app;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.Utils;

/**
 * Class for Message receiver
 * 
 * @author xtpl
 * 
 *         if message Present
 *         {"error":false,"error_msg":"","notifications_count"
 *         :1,"conv_ids":"NSw5LDgsNA=="}
 * 
 *         if message not present
 */
public class MessageReceiver extends Service {
	// private static final int APP_ID = 0;
	final String TAG = "MessageReceiver";
	String uniqueConversationId = "";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Debugger.debugE("On Start of Service");
		Log.v(TAG, "onStart is called");

		// Timer timer = new Timer();
		// timer.scheduleAtFixedRate(new TimerTask() {
		//
		// @Override
		// public void run() {}
		// }, 1000, 30*1000);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				int length = 0;

				boolean isActive = false;
				boolean isNotActive = false;
				try {
					if (CommonFunctions
							.CheckNetConnectivity(MessageReceiver.this)) {

						try {

							String api_key = Constants.API_KEY;
							String ref_id = CommonFunctions.getPref(
									MessageReceiver.this,
									CommonFunctions.REF_ID, "");
							String com_id = CommonFunctions.getPref(
									MessageReceiver.this, Constants.COM_ID, "");
							String session = CommonFunctions
									.getPref(MessageReceiver.this,
											Constants.SESSION, "");

							List<String> nameList = Constants.getNameList();

							List<String> valueList = Constants
									.getValueList(MessageReceiver.this);

							String response = CommonFunctions.postNameValueDataOnServer(
									MessageReceiver.this,
									nameList.size(),
									nameList,
									valueList,
									CommonFunctions.DEMO_SERVER_URL
											+ CommonFunctions.GET_CHAT_NOTIFICATION);

							try {
								if (response != null && response.length() > 0) {

									try {
										JSONObject jsonObject1 = new JSONObject(
												response);
										JSONObject jsonObject = jsonObject1
												.getJSONObject("response");

										String error = jsonObject
												.getString("error");
										if (error.equalsIgnoreCase("false")) {

											String conversationId = jsonObject
													.getString("conv_ids");

											if (conversationId != null
													&& conversationId.length() > 0) {

												try {

													StringTokenizer stringTokenizer = new StringTokenizer(
															conversationId, ",");
													length = stringTokenizer
															.countTokens();
													Log.e("",
															"===========The Number of Tokens are "
																	+ length
																	+ "=============");
													while (stringTokenizer
															.hasMoreElements()) {

														String conId = (String) stringTokenizer
																.nextElement();

														List<String> nameList1 = new ArrayList<String>();
														nameList1
																.add(Constants.API_KEY_TAG);
														nameList1
																.add(Constants.REF_ID_TAG);
														nameList1
																.add(Constants.COMP_ID_TAG);
														nameList1
																.add(Constants.SESSION_TAG);
														nameList1
																.add(Constants.CONV_ID_TAG);
														List<String> valueList1 = new ArrayList<String>();
														valueList1.add(api_key);
														valueList1.add(ref_id);
														valueList1.add(com_id);
														valueList1.add(session);
														valueList1.add(conId);

														try {
															Fragment activity = ActivityStatus
																	.getFragment();
															if (activity != null) {
																if (activity instanceof TenFourConversations) {
																	try {
																		TenFourConversations conversations = (TenFourConversations) activity;
																		if (conversations.THREAD != null
																				&& conversations.THREAD
																						.length() > 0) {

																			if (conversations.THREAD
																					.equalsIgnoreCase(conId)) {
																				Log.e("",
																						"============= User is Instant chattign with some one===========");

																				isActive = true;
																				try {
																					conversations.isOnCreate = true;
																				} catch (Exception e) {
																					e.printStackTrace();
																				}
																			} else {

																				conversations.isOnCreate = true;
																				isNotActive = true;
																				Log.e("",
																						"============= Its a new  conversation to inform ===========");
																				DataBase db = null;
																				try {
																					db = new DataBase(
																							MessageReceiver.this);
																					db.open();
																					int convIdCount = db
																							.getCount(
																									DataBase.Receive_table,
																									DataBase.Receive_int,
																									"conversation_Id='"
																											+ conId
																											+ "'");

																					Log.e("",
																							"The Conversation Id count is -------------"
																									+ convIdCount);
																					if (convIdCount > 0) {

																						try {

																							ContentValues cv = new ContentValues();
																							cv.put("status",
																									0);
																							db.update(
																									DataBase.Receive_table,
																									DataBase.Receive_int,
																									"conversation_Id='"
																											+ conId.trim()
																											+ "'",
																									cv);

																						} catch (Exception e) {
																							e.printStackTrace();
																						}

																					} else {

																					}
																				} catch (Exception e) {
																					e.printStackTrace();
																				} finally {
																					db.close();
																				}

																				String response1 = CommonFunctions
																						.postNameValueDataOnServer(
																								MessageReceiver.this,
																								nameList1
																										.size(),
																								nameList1,
																								valueList1,
																								CommonFunctions.DEMO_SERVER_URL
																										+ CommonFunctions.UPDATE_READ_STATUS);
																				Log.e("",
																						"---  Read Status In Message Receiver===========  "
																								+ response1);
																			}

																		}

																		else {

																			isNotActive = true;
																			Log.e("",
																					"============= Its a new  conversation to inform ===========");
																			DataBase db = null;
																			try {
																				db = new DataBase(
																						MessageReceiver.this);
																				db.open();
																				int convIdCount = db
																						.getCount(
																								DataBase.Receive_table,
																								DataBase.Receive_int,
																								"conversation_Id='"
																										+ conId
																										+ "'");

																				Log.e("",
																						"The Conversation Id count is -------------"
																								+ convIdCount);
																				if (convIdCount > 0) {

																					try {

																						ContentValues cv = new ContentValues();
																						cv.put("status",
																								0);
																						db.update(
																								DataBase.Receive_table,
																								DataBase.Receive_int,
																								"conversation_Id='"
																										+ conId.trim()
																										+ "'",
																								cv);

																					} catch (Exception e) {
																						e.printStackTrace();
																					}

																				} else {

																				}
																			} catch (Exception e) {
																				e.printStackTrace();
																			} finally {
																				db.close();
																			}

																			String response1 = CommonFunctions
																					.postNameValueDataOnServer(
																							MessageReceiver.this,
																							nameList1
																									.size(),
																							nameList1,
																							valueList1,
																							CommonFunctions.DEMO_SERVER_URL
																									+ CommonFunctions.UPDATE_READ_STATUS);
																			Log.e("",
																					"---  Read Status In Message Receiver===========  "
																							+ response1);
																			try {
																				conversations.isOnCreate = true;
																				conversations
																						.showMessageList2();
																			} catch (Exception e) {

																				e.printStackTrace();
																			}
																		}

																	} catch (Exception e) {
																		e.printStackTrace();
																	}

																} else {

																	Log.e("",
																			"============= ============================= Here  ================");
																	Activity conversationMessages = ActivityStatus
																			.getActivity();
																	if (conversationMessages != null

																			&& conversationMessages instanceof ConversationMessages) {
																		Log.e("",
																				"============= === Conversation Message instance ================");
																		ConversationMessages conversationMessages2 = (ConversationMessages) conversationMessages;
																		conversationMessages2.isNewConversation = true;
																		if (conversationMessages2.THREAD
																				.equalsIgnoreCase(conId)) {
																			isActive = true;
																		}

																	} else {
																		isNotActive = true;
																	}

																	Log.e("",
																			"============= Its a new  conversation to inform ===========");

																	DataBase db = null;
																	try {
																		db = new DataBase(
																				MessageReceiver.this);
																		db.open();

																		int convIdCount = db
																				.getCount(
																						DataBase.Receive_table,
																						DataBase.Receive_int,
																						"conversation_Id='"
																								+ conId
																								+ "'");
																		Log.e("",
																				"The Conversation Id count is -------------"
																						+ convIdCount);
																		if (convIdCount > 0) {

																			try {

																				ContentValues cv = new ContentValues();
																				cv.put("status",
																						0);
																				db.update(
																						DataBase.Receive_table,
																						DataBase.Receive_int,
																						"conversation_Id='"
																								+ conId.trim()
																								+ "'",
																						cv);

																			} catch (Exception e) {
																				e.printStackTrace();
																			} finally {
																				db.close();
																			}
																		} else {

																		}
																	} catch (Exception e) {
																		e.printStackTrace();
																	} finally {
																		db.close();
																	}

																	String response1 = CommonFunctions
																			.postNameValueDataOnServer(
																					MessageReceiver.this,
																					nameList1
																							.size(),
																					nameList1,
																					valueList1,
																					CommonFunctions.DEMO_SERVER_URL
																							+ CommonFunctions.UPDATE_READ_STATUS);
																	Log.e("",
																			"---  Read Status In Message Receiver===========  "
																					+ response1);

																}
															} else {

																isNotActive = true;
																Log.e("",
																		"============= Its a new  conversation to inform ===========");

																DataBase db = null;
																try {
																	db = new DataBase(
																			MessageReceiver.this);
																	db.open();

																	int convIdCount = db
																			.getCount(
																					DataBase.Receive_table,
																					DataBase.Receive_int,
																					"conversation_Id='"
																							+ conId
																							+ "'");
																	Log.e("",
																			"The Conversation Id count is -------------"
																					+ convIdCount);
																	if (convIdCount > 0) {

																		try {

																			ContentValues cv = new ContentValues();
																			cv.put("status",
																					0);
																			db.update(
																					DataBase.Receive_table,
																					DataBase.Receive_int,
																					"conversation_Id='"
																							+ conId.trim()
																							+ "'",
																					cv);

																		} catch (Exception e) {
																			e.printStackTrace();
																		} finally {
																			db.close();
																		}
																	} else {

																		// ContentValues
																		// cv =
																		// new
																		// ContentValues();
																		// cv.put("message_id",
																		// "0");
																		// cv.put("sender",
																		// CommonFunctions.getPref(MessageReceiver.this,
																		// CommonFunctions.REF_ID,
																		// ""));
																		//
																		// cv.put("receiver",
																		// "");
																		// cv.put("message_type",
																		// "2");
																		// cv.put("file_url",
																		// "");
																		//
																		//
																		// cv.put("date_time",
																		// ""+System.currentTimeMillis());
																		//
																		// cv.put("type",
																		// "1");
																		// cv.put("status",
																		// "0");
																		// cv.put("conversation_Id",conId);
																		// cv.put("favourite",
																		// "56");
																		// cv.put("played",
																		// "0");
																		//
																		// cv.put("textmessage",
																		// "");
																		// db.insert_content(DataBase.Receive_table,
																		// DataBase.Receive_int,
																		// cv);

																	}
																} catch (Exception e) {
																	e.printStackTrace();
																} finally {
																	db.close();
																}

																String response1 = CommonFunctions
																		.postNameValueDataOnServer(
																				MessageReceiver.this,
																				nameList1
																						.size(),
																				nameList1,
																				valueList1,
																				CommonFunctions.DEMO_SERVER_URL
																						+ CommonFunctions.UPDATE_READ_STATUS);
																Log.e("",
																		"---  Read Status In Message Receiver===========  "
																				+ response1);

															}

														} catch (Exception e) {
															e.printStackTrace();
														}

													}

												} catch (Exception e) {
													e.printStackTrace();
												}

											}

											if (length == 1 && isActive) {

											} else if (length == 1
													&& isNotActive) {

												String notify = "New Tribewire received ";
												String ns = Context.NOTIFICATION_SERVICE;
												NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
												long when = System
														.currentTimeMillis();

												Context context = getApplicationContext();
												CharSequence contentTitle = ":  New Tribewire  message";
												// Intent notificationIntent =
												// new
												// Intent(MessageReceiver.this,
												// EyeSayDefaultAudioVideo.class);
												Intent notificationIntent = new Intent(
														MessageReceiver.this,
														TenFourConversations.class);
												notificationIntent.putExtra(
														"show",
														uniqueConversationId);

												// Commented Bu Gurdeep on 16th
												// November 2012
												// Intent notificationIntent =
												// new
												// Intent(MessageReceiver.this,
												// TenFourTab.class);
												if (!false) {
													notificationIntent
															.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
																	| Intent.FLAG_ACTIVITY_SINGLE_TOP);
												}

												else {
													notificationIntent
															.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												}
												notificationIntent.putExtra(
														"tab", 3);
												PendingIntent contentIntent = PendingIntent
														.getActivity(
																MessageReceiver.this,
																0,
																notificationIntent,
																PendingIntent.FLAG_ONE_SHOT);

												Notification notification = new Notification(
														R.drawable.ic_launcher,
														notify, when);
												// notification.sound
												notification.defaults |= Notification.DEFAULT_SOUND;
												notification.defaults |= Notification.DEFAULT_VIBRATE;

												notification.flags = Notification.FLAG_AUTO_CANCEL;

												notification
														.setLatestEventInfo(
																context,
																contentTitle,
																notify,
																contentIntent);
												mNotificationManager
														.notify(CommonFunctions.NOTIFICATION_ID,
																notification);

												// Intent intent = new
												// Intent(MessageReceiver.this,NotificationBroadCast.class);
												// intent.putExtra("messagetype",
												// "2");
												// intent.putExtra("conversationId",
												// uniqueConversationId);
												// sendBroadcast(intent);

											} else if (length > 1
													&& isNotActive) {

												String notify = "New Eye Say Message received ";
												String ns = Context.NOTIFICATION_SERVICE;
												NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
												long when = System
														.currentTimeMillis();

												Context context = getApplicationContext();
												// CharSequence contentTitle =
												// (!CommonFunctions.isFullVersion(getBaseContext()))
												// ?
												// getString(R.string.app_name):getString(R.string.app_name_pre)
												// + ":  New Eye Say message";
												// Intent notificationIntent =
												// new
												// Intent(MessageReceiver.this,
												// EyeSayDefaultAudioVideo.class);
												Intent notificationIntent = new Intent(
														MessageReceiver.this,
														TenFourConversations.class);
												notificationIntent.putExtra(
														"show",
														uniqueConversationId);

												// Commented Bu Gurdeep on 16th
												// November 2012
												// Intent notificationIntent =
												// new
												// Intent(MessageReceiver.this,
												// TenFourTab.class);
												if (!false) {
													notificationIntent
															.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
																	| Intent.FLAG_ACTIVITY_SINGLE_TOP);
												}

												else {
													notificationIntent
															.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												}
												notificationIntent.putExtra(
														"tab", 3);
												PendingIntent contentIntent = PendingIntent
														.getActivity(
																MessageReceiver.this,
																0,
																notificationIntent,
																PendingIntent.FLAG_ONE_SHOT);

												Notification notification = new Notification(
														R.drawable.ic_launcher,
														notify, when);
												// notification.sound
												notification.defaults |= Notification.DEFAULT_SOUND;
												notification.defaults |= Notification.DEFAULT_VIBRATE;

												notification.flags = Notification.FLAG_AUTO_CANCEL;

												notification
														.setLatestEventInfo(
																context,
																"New Tribewire message received",
																notify,
																contentIntent);
												mNotificationManager
														.notify(CommonFunctions.NOTIFICATION_ID,
																notification);

												// Intent intent = new
												// Intent(MessageReceiver.this,NotificationBroadCast.class);
												// intent.putExtra("messagetype",
												// "2");
												// intent.putExtra("conversationId",
												// uniqueConversationId);
												// sendBroadcast(intent);

											}
										} else {

											Log.e("",
													"-------------------------- No New Notification----------------------------");
										}
									} catch (JSONException e) {
										e.printStackTrace();
									return;
									}

								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							Log.e("", " The Chat Notification Response is "
									+ response);

							Debugger.debugE("call the mesage reciever");
						} catch (Exception e) {
							Debugger.debugE("Error in Message Receiver");
							e.printStackTrace();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		thread.start();

		super.onStart(intent, startId);
	}

	/**
	 * Number comparison Function
	 * 
	 * @param cellno1
	 * @param cellno2
	 * @return
	 */
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

	private long parseDataTime(String date_time) {
		long datetime = System.currentTimeMillis();
		try {
			if (date_time.contains(".")) {
				try {
					Log.e("", "The Original Date is  " + date_time);
					StringTokenizer stringTokenizer = new StringTokenizer(
							date_time, ".");
					while (stringTokenizer.hasMoreElements()) {
						String iphoneDate = (String) stringTokenizer
								.nextElement().toString();
						// Log.e("", "Content Value  "+cv.toString());
						// Log.e("",
						// "************* Sender "+tempSender+" Receiver "+tempReceiver+" Message Type"
						// +message_type);
						Log.e("", "The iphone Date is =======" + iphoneDate);

						datetime = Utils.method2(Long.parseLong(iphoneDate));
						// datetime = ((datetime)-(12*60*60*1000));

						break;

					}

					// Sender=19465383066,Receiver =18591741225,Type=0,
					// Url=http://is.gd/4deHM7, Date =377857290.969243

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				try {
					datetime = Utils.method2(Long.parseLong(date_time));
					// datetime = ((datetime)-(12*60*60*1000));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return datetime;

	}
}
