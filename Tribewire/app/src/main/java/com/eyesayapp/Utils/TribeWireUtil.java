package com.eyesayapp.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.eyesayapp.asyn.RestApiCall;
import com.eyesayapp.model.CompanyMessages;
import com.google.android.gms.internal.cu;
import com.tribewire.app.ConversationMessages;
import com.tribewire.app.Login;
import com.tribewire.app.R;

import eyesay.obj.Friend;

public class TribeWireUtil {

	/**
	 * 
	 * @param Initiate
	 *            a Web Call which will fetch Admin Groups from Tribewire Server
	 * 
	 */
	public static void getAdminGroups(Login login) {

		try {
			List<String> nameList = new ArrayList<String>();

			nameList = new ArrayList<String>();
			nameList.add(Constants.API_KEY_TAG);
			nameList.add(Constants.REF_ID_TAG);
			nameList.add(Constants.COMP_ID_TAG);
			nameList.add(Constants.SESSION_TAG);

			// new RestApiCall(login, CommonFunctions.DEMO_SERVER_URL
			// + TribeWireConstants.ADMIN_GROUP_URL, nameList,
			// Constants.getValueList(login)).execute();
			new RestApiCall(login, CommonFunctions.DEMO_SERVER_URL
					+ TribeWireConstants.ADMIN_GROUP_URL, nameList,
					Constants.getValueList(login)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Used to show notification alert whenevr user receives a emergency messages inside the app.
	public static void showNotificationAlert(final Context c, final String msg,final String title){		
		try {
			new Handler().post(new Runnable() {
				
				@Override
				public void run() {
					new AlertDialog.Builder(c)
					.setMessage(msg)
					.setTitle(title)
					.setPositiveButton(c.getResources().getString(R.string.ok),
							null).show();
					
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	

	public static void uploadProfilePic(Login login) {

		try {
			List<String> nameList = new ArrayList<String>();

			nameList = new ArrayList<String>();
			nameList.add(Constants.API_KEY_TAG);
			nameList.add(Constants.REF_ID_TAG);
			nameList.add(Constants.PROFILE_PIC);
			nameList.add(Constants.SESSION_TAG);
			String api_key = Constants.API_KEY;
			String ref_id = CommonFunctions.getPref(login,
					CommonFunctions.REF_ID, "");
			String com_id = "https://m.ak.fbcdn.net/profile.ak/hprofile-ak-prn2/t1/1780691_627345683979801_1994700623_n.jpg";

			String session = CommonFunctions.getPref(login, Constants.SESSION,
					"");
			ArrayList<String> valueList = null;
			if (valueList == null) {
				valueList = new ArrayList<String>();
				valueList.add(api_key);
				valueList.add(ref_id);
				valueList.add(com_id);
				valueList.add(session);
			}
			// new RestApiCall(login, CommonFunctions.DEMO_SERVER_URL
			// + TribeWireConstants.PROFILE_PIC_URL, nameList, valueList)
			// .execute();
			new RestApiCall(login, CommonFunctions.DEMO_SERVER_URL
					+ TribeWireConstants.PROFILE_PIC_URL, nameList, valueList)
					.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 * 
	 *         Parses the Admin Group Response and insert the records to group
	 *         table. If data parses without any error then it will return true
	 *         else it will return false
	 *         {"error":false,"error_msg":"","admin_groups"
	 *         :[{"group_id":"NzM=","group_name":"VGVzdCBBZG1pbiBHcm91cA=="}]}
	 */
	public static boolean parseAdminGroup(String response, Context context) {
		try {
			if (response != null && response.length() > 0) {
				try {

					JSONObject jsonObject1 = new JSONObject(response);
					JSONObject jsonObject = jsonObject1
							.getJSONObject("response");
					// JSONObject jsonObject = new JSONObject(response);
					String error = jsonObject.getString("error");
					if (error.equalsIgnoreCase("false")) {

						JSONArray jsonArray = jsonObject
								.getJSONArray("admin_groups");
						if (jsonArray != null && jsonArray.length() > 0) {

							DataBase db = new DataBase(context);
							db.open();

							try {
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject groupObj = jsonArray
											.getJSONObject(i);

									String groupId = groupObj
											.getString("group_id");
									String group_name = groupObj
											.getString("group_name");

									try {
										// groupId = new String(Base64.decode(
										// groupId.getBytes(),
										// Base64.DEFAULT));
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										group_name = new String(Base64.decode(
												group_name.getBytes(),
												Base64.DEFAULT));
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {
										int count = db.getCount(
												DataBase.Group_table,
												DataBase.Group_int,
												" sr_no = '" + groupId + "'");
										if (count > 0) {
											ContentValues values = new ContentValues();
											values.put("sr_no", groupId);
											values.put("groupname", group_name);
											// Type 1 indicates that it is admin
											values.put("type", 1);
											db.update(
													DataBase.Group_table,
													DataBase.Group_int,
													"sr_no = '" + groupId + "'",
													values);
											Log.e("",
													"======= Group Updated successfully=====");
										} else {
											ContentValues values = new ContentValues();
											values.put("sr_no", groupId);
											values.put("groupname", group_name);
											// Type 1 indicates that it is admin
											values.put("type", 1);
											db.insert_content(
													DataBase.Group_table,
													DataBase.Group_int, values);
											Log.e("",
													"======= Group Added  successfully=====");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									try {

										int count = db.getCount(
												DataBase.Friends_table,
												DataBase.Friends_int,
												Friend.GROUPID + " = '"
														+ groupId + "'");
										Log.e("",
												"The Nt In Db Count with Number "
														+ groupId + "=Count "
														+ count);

										if (!(count > 0)) {
											ContentValues contentValues = new ContentValues();
											contentValues.put(Friend.NAME,
													group_name.trim());
											contentValues.put(Friend.NUMBER,
													groupId.trim());
											contentValues.put("email", "");
											contentValues.put(Friend.ASSCID, 0);

											// Status 5 means it is a group
											contentValues.put(Friend.STATUS, 6);

											contentValues.put(
													Friend.STATUS_MESSAGE, " ");
											contentValues.put(Friend.GROUPID,
													groupId);
											contentValues.put(Friend.INVITE,
													false);
											db.insert_content(
													DataBase.Friends_table,
													DataBase.Friends_int,
													contentValues);

											Log.e("",
													"*****User Inserted Successfully *****");
										} else {
											Cursor c = null;
											try {
												c = db.fetch(
														DataBase.Friends_table,
														DataBase.Friends_int,
														Friend.GROUPID
																+ " like '%"
																+ groupId + "'");

												if (c != null
														&& c.getCount() > 0) {
													int sr_no = c.getInt(0);
													Log.e("",
															"Db User Serial Id is "
																	+ sr_no);
													try {
														ContentValues contentValues = new ContentValues();
														contentValues.put(
																Friend.NUMBER,
																groupId.trim());
														contentValues.put(
																"email", " ");

														// Status 5 means it is
														// a
														// group
														contentValues.put(
																Friend.STATUS,
																6);

														contentValues
																.put(Friend.STATUS_MESSAGE,
																		" ");
														Log.e("",
																"***************User Updated Successfully "
																		+ db.update(
																				DataBase.Friends_table,
																				DataBase.Friends_int,
																				"sr_no = "
																						+ sr_no,
																				contentValues)
																		+ "***************");

													} catch (Exception e) {
														e.printStackTrace();
														CommonFunctions
																.writeLog("498 Json Exception "
																		+ e.getMessage());
													}

												}

											} catch (Exception e) {
												e.printStackTrace();

											}

										}

									} catch (Exception e) {
										e.printStackTrace();
									}

								}

							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								db.close();
							}

						}
					} else {

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 
	 * @param context
	 * @param response
	 * 
	 *            parses the user defined groups information from server
	 * 
	 *            {"response": {"error":false,
	 *            "error_msg":"","groups":[{"group_id"
	 *            :"MTU=","group_name":"Z2FycnkgZ3JvdXA0"
	 *            },{"group_id":"Mjc=","group_name"
	 *            :"R3MgVGVzdCBnMg=="},{"group_id"
	 *            :"MTI=","group_name":"R1MgVGVzdCBncm91cA=="
	 *            },{"group_id":"MTg="
	 *            ,"group_name":"bXkgbmV3IGdyb3Vwcw=="},{"group_id"
	 *            :"MTc=","group_name"
	 *            :"TXlfVGVzdF9Hcm91cA=="},{"group_id":"MTIx"
	 *            ,"group_name":"TmV3IEdyb3VwIE5hbWU="
	 *            },{"group_id":"MTE5","group_name"
	 *            :"TmV3IEdyb3VwIE5hbWUgNzg="},{
	 *            "group_id":"MTIy","group_name":"TmV3IEdyb3VwIE5hbWUgODg="
	 *            },{"group_id"
	 *            :"ODg=","group_name":"cGNiYQ=="},{"group_id":"ODA="
	 *            ,"group_name"
	 *            :"c3VzYW5fZ3JvdXA="},{"group_id":"MTM=","group_name"
	 *            :"dGVzdCBncm91cA=="
	 *            },{"group_id":"MTIw","group_name":"dGVzdCBncm91cCBzdXNhbg=="
	 *            },{"group_id":"MTY=","group_name":"dGVzdGluZw=="},{"group_id":
	 *            "Mjg=","group_name":"dGVzdGluZyBncm91cCBmb3IgZnJhbmtpZQ=="},{
	 *            "group_id"
	 *            :"MjY=","group_name":"VGVzdG5ubg=="},{"group_id":"OTE="
	 *            ,"group_name"
	 *            :"dXNlcl9hZ3JvdXBfdGVzdGluZw=="},{"group_id":"ODc="
	 *            ,"group_name":"emFpbnphaW4="}]}}
	 */
	public static void parseUserGroups(Context context, String response) {

		try {
			if (response != null && response.length() > 0) {

				try {
					JSONObject jsonObject1 = new JSONObject(response);
					JSONObject jsonObject = jsonObject1
							.getJSONObject("response");
					// JSONObject jsonObject = new JSONObject(response);
					JSONArray groupArray = jsonObject.getJSONArray("groups");
					if (groupArray != null && groupArray.length() > 0) {
						DataBase db = new DataBase(context);
						db.open();

						try {
							for (int i = 0; i < groupArray.length(); i++) {
								JSONObject jsonObject2 = groupArray
										.getJSONObject(i);
								String groupId = jsonObject2
										.getString("group_id");
								String groupName = jsonObject2
										.getString("group_name");

								try {
									// groupId = new String(Base64.decode(
									// groupId.getBytes(),
									// Base64.DEFAULT));
								} catch (Exception e) {
									e.printStackTrace();
								}
								try {
									groupName = new String(Base64.decode(
											groupName.getBytes(),
											Base64.DEFAULT));
								} catch (Exception e) {
									e.printStackTrace();
								}
								try {
									int count = db.getCount(
											DataBase.Group_table,
											DataBase.Group_int, " sr_no = '"
													+ groupId + "'");
									if (count > 0) {
										ContentValues values = new ContentValues();
										values.put("sr_no", groupId);
										values.put("groupname",
												groupName.trim());
										// Type 0 refer to as User defined group
										values.put("type", 0);
										db.update(DataBase.Group_table,
												DataBase.Group_int, "sr_no = '"
														+ groupId + "'", values);
										Log.e("",
												"======= Group Updated successfully=====");
									} else {
										ContentValues values = new ContentValues();
										values.put("sr_no", groupId);
										values.put("groupname",
												groupName.trim());
										// Type 0 refer to as User defined group
										values.put("type", 0);
										db.insert_content(DataBase.Group_table,
												DataBase.Group_int, values);
										Log.e("",
												"======= Group Added  successfully=====");
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								try {

									int count = db.getCount(
											DataBase.Friends_table,
											DataBase.Friends_int,
											Friend.GROUPID + " = '" + groupId
													+ "'");
									Log.e("", "The Nt In Db Count with Number "
											+ groupId + "=Count " + count);

									if (!(count > 0)) {
										ContentValues contentValues = new ContentValues();
										contentValues.put(Friend.NAME,
												groupName.trim());
										contentValues.put(Friend.NUMBER,
												groupId.trim());
										contentValues.put("email", "");
										contentValues.put(Friend.ASSCID, 0);

										// Status 5 means it is a group
										contentValues.put(Friend.STATUS, 5);

										contentValues.put(
												Friend.STATUS_MESSAGE, " ");
										contentValues.put(Friend.GROUPID,
												groupId);
										contentValues.put(Friend.INVITE, false);
										db.insert_content(
												DataBase.Friends_table,
												DataBase.Friends_int,
												contentValues);

										Log.e("",
												"*****User Inserted Successfully *****");
									} else {
										Cursor c = null;
										try {
											c = db.fetch(
													DataBase.Friends_table,
													DataBase.Friends_int,
													Friend.GROUPID + " like '%"
															+ groupId + "'");

											if (c != null && c.getCount() > 0) {
												int sr_no = c.getInt(0);
												Log.e("",
														"Db User Serial Id is "
																+ sr_no);
												try {
													ContentValues contentValues = new ContentValues();
													contentValues.put(
															Friend.NUMBER,
															groupId.trim());
													contentValues.put("email",
															" ");

													// Status 5 means it is a
													// group
													contentValues.put(
															Friend.STATUS, 5);

													contentValues
															.put(Friend.STATUS_MESSAGE,
																	" ");
													Log.e("",
															"***************User Updated Successfully "
																	+ db.update(
																			DataBase.Friends_table,
																			DataBase.Friends_int,
																			"sr_no = "
																					+ sr_no,
																			contentValues)
																	+ "***************");

												} catch (Exception e) {
													e.printStackTrace();
													CommonFunctions
															.writeLog("498 Json Exception "
																	+ e.getMessage());
												}

											}

										} catch (Exception e) {
											e.printStackTrace();

										}

									}

								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (db != null) {
								db.close();
							}
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param context
	 * @param edtSearchText
	 * 
	 *            Display the Edit text automatically
	 * 
	 */
	public static void showKeyboard(Context context, EditText edtSearchText) {
		edtSearchText.setFocusableInTouchMode(true);
		edtSearchText.requestFocus();
		final InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(edtSearchText,
				InputMethodManager.SHOW_IMPLICIT);

	}

	/**
	 * Used to hide the System keyboard
	 * 
	 * @param context
	 * @param edtSearchText
	 */
	public static void hideKeyboard(Context context, EditText edtSearchText) {
		try {
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtSearchText.getWindowToken(), 0);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static Typeface tf = null;
	static Typeface tfb = null;

	/**
	 * 
	 */
	public static Typeface getArialTypeface(Context c) {

		try {
			tf = Typeface.createFromAsset(c.getAssets(), "fonts/arial.ttf");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return tf;
	}

	public static Typeface getArialBoldTypeface(Context c) {
		try {
			if (tfb == null) {
				tfb = Typeface.createFromAsset(c.getAssets(),
						"fonts/ArialBold.ttf");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tfb;

	}

	private static String getFriendName(Context context ,String friendId, DataBase db) {
		String name = "";
		try {

			Cursor cursor = db.fetch(
					DataBase.Friends_table,
					DataBase.Friends_int,
					Friend.NUMBER
							+ " = '"
							+ new String(Base64.decode(friendId.getBytes(),
									Base64.DEFAULT)) + "'");
			Log.e("", "========= Cursor Count is " + cursor.getCount()
					+ "=== Friend Id " + friendId);
			if (cursor != null && cursor.getCount() > 0) {
				String friendName = cursor.getString(2);
				name = friendName;
			}
			else {
		String refId   = 	CommonFunctions.getPref(context, CommonFunctions.REF_ID, "");
		if (refId.equals(friendId)) {
			
			String fName = CommonFunctions.getPref(context,
					Constants.FIRST_NAME, "");
			String lName = CommonFunctions.getPref(context,
					Constants.LAST_NAME, "");
			
			name  = fName +" "+lName;
		}
			}
			// Log.e("", "============ Is Status  updated " + isUpdated);
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;

	}

	/**
	 * 
	 * @param response
	 *            {"attached_file_name":"", "attachment_type":"0",
	 *            "conversation_id":"MzIx", "recipient_names":"ALL",
	 *            "last_message":"dGVzdCBieSBqaG9uZXk=",
	 *            "conversation_start_date":"2014-07-28 20:06:00",
	 *            "last_message_date":"2014-07-09 23:14:39",
	 *            "sender":"NA==","isread":"1"}]}}
	 */
	public static List<CompanyMessages> parseCompanyFyiResponse(Context ctx,
			String response) {
		List<CompanyMessages> companyMessages = null;

		try {

			DataBase db = null;
			if (response != null && response.length() > 0) {
				JSONObject jsonObject = new JSONObject(response);
				Log.e("", "===================== The Response " + response);
				JSONObject jsonObject2 = jsonObject.getJSONObject("response");
				String error = jsonObject2.getString("error");
				if (error.equalsIgnoreCase("false")) {
					JSONArray jsonArray = jsonObject2
							.getJSONArray("inbox_list");
					CommonFunctions.setPref(ctx, "count", jsonArray.length());
					if (jsonArray != null && jsonArray.length() > 0) {
						db = new DataBase(ctx);
						db.open();
						// db.cleanTable(DataBase.Company_Fyi_int);
						companyMessages = new ArrayList<CompanyMessages>();
						Log.e("",
								" = ===================== Json Array Length is "
										+ jsonArray.length()
										+ "============== Db Count "
										+ db.getCount(
												DataBase.Company_Fyi_table,
												DataBase.Company_Fyi_int, null));
						for (int i = 0; i < jsonArray.length(); i++) {
							CompanyMessages cm = new CompanyMessages();
							JSONObject infoObj = (JSONObject) jsonArray.get(i);
							String conversationId = infoObj
									.getString("conversation_id");
							String recipientName = infoObj
									.getString("recipient_names");
							String lastMessage = infoObj
									.getString("last_message");
							lastMessage = new String(Base64.decode(
									lastMessage.getBytes(), Base64.DEFAULT));
							String conversationDate = infoObj
									.getString("conversation_start_date");
							String senderId = infoObj.getString("sender");
							String isread = infoObj.getString("isread");
							String subject = infoObj.getString("subject");
							cm.setSubject(subject);
							try {
								String messageType = infoObj
										.getString("attachment_type");
								String file_Url = infoObj
										.getString("attached_file_name");
								if (messageType.equalsIgnoreCase("3")) {
									file_Url = Constants.IMAGE_BASE_URL
											+ file_Url;
								} else if (messageType.equalsIgnoreCase("2")) {
									file_Url = Constants.AUDIO_BASE_URL
											+ file_Url;
								}
								cm.setAttacheMessageType(messageType);
								cm.setAttachMessageUrl(file_Url);

							} catch (Exception e) {
								e.printStackTrace();
							}

							cm.setConversationId(conversationId);
							cm.setLastMessage(lastMessage);
							cm.setRecipientNames(recipientName);
							cm.setSenderName(getFriendName(ctx,senderId, db));
							// cm.setConversationStartDate(conversationDate);
							cm.setSenderId(senderId);
							try {

								String where = "" + DataBase.CONVERSATION_ID
										+ " = '" + conversationId + "'";
								Log.e("", "===================== Where "
										+ " i " + i + " " + where);
								int count = db.getCount(
										DataBase.Company_Fyi_table,
										DataBase.Company_Fyi_int, where);
								Log.e("",
										"=========== The Conversation Id Count is "
												+ count);
								if (count > 0) {

									ContentValues contentValues = new ContentValues();
									contentValues.put(DataBase.CONVERSATION_ID,
											conversationId);
									contentValues.put(DataBase.SENDER,
											recipientName);
									contentValues.put(DataBase.TEXT_MESSAGE,
											lastMessage);
									String newLongMillis = ""
											+ CommonFunctions
													.getLongMillisfromDate(conversationDate);
									cm.setConversationStartDate(newLongMillis);
									contentValues.put(DataBase.DATE_TIME_LONG,
											newLongMillis);
									contentValues.put(DataBase.SENDER_ID,
											senderId);
									try {
										Cursor cursor = db
												.fetch(DataBase.Company_Fyi_table,
														DataBase.Company_Fyi_int,
														where);
										if (cursor != null
												&& cursor.getCount() > 0) {
											String dbLongMillis = cursor
													.getString(3);
											Log.e("",
													"========= Db long millis "
															+ dbLongMillis
															+ " New Long Millis "
															+ newLongMillis);
											try {
												contentValues
														.put(DataBase.STATUS,
																Integer.parseInt(isread));

												cm.setReadStatus(Integer
														.parseInt(isread));
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

									boolean rowId = db.update(
											DataBase.Company_Fyi_table,
											DataBase.Company_Fyi_int,
											DataBase.CONVERSATION_ID + " = '"
													+ conversationId + "'",
											contentValues);
									Log.e("",
											"======== Data inserted successfully with Id "
													+ rowId + "===========");

								} else {
									ContentValues contentValues = new ContentValues();
									contentValues.put(DataBase.CONVERSATION_ID,
											conversationId);
									contentValues.put(DataBase.SENDER,
											recipientName);
									contentValues.put(DataBase.TEXT_MESSAGE,
											lastMessage);
									contentValues
											.put(DataBase.DATE_TIME_LONG,
													CommonFunctions
															.getLongMillisfromDate(conversationDate));
									cm.setConversationStartDate(""
											+ CommonFunctions
													.getLongMillisfromDate(conversationDate));
									contentValues.put(DataBase.SENDER_ID,
											senderId);
									// Status 0 means unread
									try {
										contentValues.put(DataBase.STATUS,
												Integer.parseInt(isread));

										cm.setReadStatus(Integer
												.parseInt(isread));
									} catch (Exception e) {
										e.printStackTrace();
									}
									// if (TribeWireConstants
									// .isFyiMenuFirstTime(ctx)) {
									// contentValues.put(DataBase.STATUS, 0);
									// cm.setReadStatus(0);
									// } else {
									// contentValues.put(DataBase.STATUS, 1);
									// cm.setReadStatus(1);
									// }

									long rowId = db.insert_content(
											DataBase.Company_Fyi_table,
											DataBase.Company_Fyi_int,
											contentValues);
									Log.e("",
											"======== Data inserted successfully with Id "
													+ rowId + "===========");
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

							companyMessages.add(cm);

						}
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return companyMessages;
	}

	// Returns the Company Fyi Count from Server
	public static int getCompanyFyiCount(String response) {
		int count = 0;
		try {

			if (response != null && response.length() > 0) {
				JSONObject jsonObject = new JSONObject(response);
				JSONObject jsonObject2 = jsonObject.getJSONObject("response");
				String error = jsonObject2.getString("error");
				if (error.equalsIgnoreCase("false")) {
					JSONArray jsonArray = jsonObject2
							.getJSONArray("inbox_list");
					if (jsonArray != null && jsonArray.length() > 0) {
						count = jsonArray.length();
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return count;
	}

	public static Bitmap rotateBitmap(String src, Bitmap bitmap) {
		try {
			int orientation = getExifOrientation(src);
			Log.e("", "************************* The Orientation "
					+ orientation + " ***************************");
			if (orientation == 1) {
				return bitmap;
			}

			Matrix matrix = new Matrix();
			switch (orientation) {

			case 2:
				matrix.setScale(-1, 1);
				break;
			case 3:
				matrix.setRotate(180);
				break;
			case 4:
				matrix.setRotate(180);
				matrix.postScale(-1, 1);
				break;
			case 5:
				matrix.setRotate(90);
				matrix.postScale(-1, 1);
				break;
			case 6:
				matrix.setRotate(90);
				break;
			case 7:
				matrix.setRotate(-90);
				matrix.postScale(-1, 1);
				break;
			case 8:
				matrix.setRotate(-90);
				break;
			default:
				return bitmap;
			}

			try {
				Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				bitmap.recycle();
				return oriented;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return bitmap;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public static Bitmap rotateBitmapZero(String src, Bitmap bitmap) {
		try {
			int orientation = getExifOrientation(src);
			Log.e("", "************************* The Orientation "
					+ orientation + " ***************************");
			if (orientation == 1) {
				return bitmap;
			}

			Matrix matrix = new Matrix();
			switch (orientation) {
			case 0:
				matrix.setRotate(90);
				// matrix.postRotate(90);

				break;
			case 2:
				matrix.setScale(-1, 1);
				break;
			case 3:
				matrix.setRotate(180);
				break;
			case 4:
				matrix.setRotate(180);
				matrix.postScale(-1, 1);
				break;
			case 5:
				matrix.setRotate(90);
				matrix.postScale(-1, 1);
				break;
			case 6:
				matrix.setRotate(90);
				break;
			case 7:
				matrix.setRotate(-90);
				matrix.postScale(-1, 1);
				break;
			case 8:
				matrix.setRotate(-90);
				break;
			default:
				return bitmap;
			}

			try {
				Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				bitmap.recycle();
				return oriented;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return bitmap;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	private static int getExifOrientation(String src) throws IOException {
		int orientation = 1;

		try {
			/**
			 * if your are targeting only api level >= 5 ExifInterface exif =
			 * new ExifInterface(src); orientation =
			 * exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
			 */
			if (Build.VERSION.SDK_INT >= 5) {
				Class<?> exifClass = Class
						.forName("android.media.ExifInterface");
				Constructor<?> exifConstructor = exifClass
						.getConstructor(new Class[] { String.class });
				Object exifInstance = exifConstructor
						.newInstance(new Object[] { src });
				Method getAttributeInt = exifClass.getMethod("getAttributeInt",
						new Class[] { String.class, int.class });
				Field tagOrientationField = exifClass
						.getField("TAG_ORIENTATION");
				String tagOrientation = (String) tagOrientationField.get(null);
				orientation = (Integer) getAttributeInt.invoke(exifInstance,
						new Object[] { tagOrientation, 1 });
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		return orientation;
	}

	public static Bitmap getBitmap(Context c, Uri uri) {

		// Uri uri = getImageUri(path);
		InputStream in = null;
		try {
			final int IMAGE_MAX_SIZE = 700000; // 1.2MP
			in = c.getContentResolver().openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
			// Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth
			// + ", orig-height: " + o.outHeight);

			Bitmap b = null;
			in = c.getContentResolver().openInputStream(uri);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				// Log.d(TAG, "1th scale operation dimenions - width: " + width
				// + ",height: " + height);

				double y = Math.sqrt(IMAGE_MAX_SIZE
						/ (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
						(int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();
			} else {
				b = BitmapFactory.decodeStream(in);
			}

			in.close();
			Log.e("", "");
			return b;
		} catch (IOException e) {
			// Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	public static String getNotificationMessage(String response) {
		String message = "";

		try {

			JSONObject jsonObject = new JSONObject(response);
			String encodedText = jsonObject.getString("text");
			String decodedMessage = new String(Base64.decode(
					encodedText.getBytes(), Base64.DEFAULT));
			message = decodedMessage;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return message;
	}

	public static String getNotificationConvId(String response) {
		String convId = "";

		try {

			JSONObject jsonObject = new JSONObject(response);
			String encodedText = jsonObject.getString("conversationid");
			// String decodedMessage = new
			// String(Base64.decode(encodedText.getBytes(), Base64.DEFAULT));
			convId = encodedText;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return convId;
	}

	public static boolean isEmergency(String response) {
		boolean isEmergency = false;

		try {

			JSONObject jsonObject = new JSONObject(response);
			boolean encodedText = jsonObject.getBoolean("is_emergency");
			// String decodedMessage = new
			// String(Base64.decode(encodedText.getBytes(), Base64.DEFAULT));
			if (encodedText) {
				isEmergency = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isEmergency;
	}

	/**
	 * 
	 * @param response
	 * @param ctx
	 * @return
	 * 
	 *         "sender_id":"NDkw", "msg_id":"Mzc2", "conversationid":"Mzc2",
	 *         "sender":"Been Chin", "text":"aGk=", "attachment_type":0,
	 *         "attached_file_name":"", "thumbnail":"", "msg_type":"1",
	 *         "date_time":"2015-01-08 12:30:14"
	 */
	public static void insertNewConversation(String response, Context ctx) {
		boolean messagePresent = false;
		try {
			if (response != null && response.length() > 0) {
				try {
					JSONObject jsonObject1 = new JSONObject(response);
					JSONObject inbox_List = jsonObject1;
					Log.e("",
							"================ The inbox array length is ======================= "
									+ inbox_List.length());

					DataBase db = null;
					db = new DataBase(ctx);
					db.open();
					try {

						JSONObject jsonObject2 = inbox_List;
						String conversation_Id = jsonObject2
								.getString("conversationid");
						int Convcount = db.getCount(DataBase.Receive_table,
								DataBase.Receive_int, "conversation_id = '"
										+ conversation_Id + "'");
						Log.e("",
								"===================== The Conversation Count in the Intent Service is "
										+ Convcount + " With Conversation Id "
										+ conversation_Id);
						if (Convcount > 0) {
							if (db != null) {
								db.close();
							}
							return;
						}

						String isEmergency = "0";
						
						boolean emergency = jsonObject2
								.getBoolean("is_emergency");
						if (emergency) {
							isEmergency = "1";
						}
						
						
						
						String isRead = jsonObject2.getString("is_read");
						int count = db.getCount(
								DataBase.Receive_table,
								DataBase.Receive_int,
								"conversation_Id ='"
										+ jsonObject2
												.getString("conversationid")
										+ "'");
						if (!(count > 0)) {
							ContentValues cv = new ContentValues();
							try {
								cv.put("message_id",
										jsonObject2.getString("msg_id"));
							} catch (Exception e) {
								e.printStackTrace();
							}
							// cv.put("sender", CommonFunctions
							// .getPref(ctx,
							// CommonFunctions.REF_ID,
							// ""));

							try {
								cv.put("sender",
										jsonObject2.getString("sender"));
							} catch (Exception e) {
								e.printStackTrace();
							}
							String receiver = CommonFunctions.getRecipientsId(
									db,
									jsonObject2.getString("receipient_names"));

							String receiverAll = jsonObject2
									.getString("receipient_names");

							cv.put("receiver", receiver);

							try {
								// attachment_type = 3 // Its an
								// image
								// attachment_type = 0 // Its an
								// text message only
								// attachment_type = 2 // Its an

								// attachment_type":0,
								// "attached_file_name":"",
								//
								// Audio File
								String messageType = jsonObject2
										.getString("attachment_type");
								String file_Url = jsonObject2
										.getString("attached_file_name");
								if (messageType.equalsIgnoreCase("3")) {
									file_Url = Constants.IMAGE_BASE_URL
											+ file_Url;
								} else if (messageType.equalsIgnoreCase("2")) {
									file_Url = Constants.AUDIO_BASE_URL
											+ file_Url;
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
																.getString("date_time")));

							} catch (Exception e) {
								e.printStackTrace();
							}
							cv.put("type", "1");
							try {
								String isread = jsonObject2.getString("is_read");
								cv.put("status", isread);

							} catch (Exception e) {
								e.printStackTrace();
							}

							cv.put("conversation_Id",
									jsonObject2.getString("conversationid"));
							cv.put("favourite", "56");
							cv.put("played", "0");

							cv.put("textmessage", jsonObject2.getString("text"));
							// if (!receiverAll
							// .equalsIgnoreCase("all")) {
							db.insert_content(DataBase.Receive_table,
									DataBase.Receive_int, cv);
							ContentValues values = new ContentValues();
							values.put(DataBase.conversationId,
									jsonObject2.getString("conversationid"));
							values.put(DataBase.isEmergency, isEmergency);
							values.put(DataBase.isRead, isRead);
							db.insert_content(DataBase.emergency_table,
									DataBase.emergency_int, values);
							// }

						} else {
							ContentValues cv = new ContentValues();
							cv.put("message_id",
									jsonObject2.getString("msg_id"));
							// cv.put("sender", CommonFunctions
							// .getPref(ctx,
							// CommonFunctions.REF_ID,
							// ""));
							cv.put("sender", jsonObject2.getString("sender"));
							String receiver = jsonObject2
									.getString("receipient_names");
							// String receiver = CommonFunctions
							// .getRecipientsId(
							// db,
							// jsonObject2
							// .getString("recipient_names"));
							//
							// cv.put("receiver", receiver);

							try {
								// attachment_type = 3 // Its an image
								// attachment_type = 0 // Its an text message
								// only
								// attachment_type = 2 // Its an Audio File
								String messageType = jsonObject2
										.getString("attachment_type");
								String file_Url = jsonObject2
										.getString("attached_file_name");
								if (messageType.equalsIgnoreCase("3")) {
									file_Url = Constants.IMAGE_BASE_URL
											+ file_Url;
								} else if (messageType.equalsIgnoreCase("2")) {
									file_Url = Constants.AUDIO_BASE_URL
											+ file_Url;
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
																.getString("date_time")));
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
																.getString("conversationid")
														+ "'");
								if (c != null && c.getCount() > 0) {
									String textMessage = c.getString(15);
									// if (textMessage
									// .equalsIgnoreCase(jsonObject2
									// .getString("last_message"))) {
									// // cv.put("status", "0");
									// } else {
									// cv.put("status", "1");
									// }
									try {
										String isread = jsonObject2
												.getString("is_read");
										cv.put("status", isread);

									} catch (Exception e) {
										e.printStackTrace();
									}
									// Log.e("",
									// "======== DB Text Message is ======"
									// + textMessage
									// + " === Real Message "
									// + jsonObject2
									// .getString("last_message"));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							cv.put("conversation_Id",
									jsonObject2.getString("conversationid"));
							cv.put("favourite", "56");
							cv.put("played", "0");

							cv.put("textmessage",
									jsonObject2.getString("text"));
							// if (!receiver.equalsIgnoreCase("all")) {
							db.update(
									DataBase.Receive_table,
									DataBase.Receive_int,
									" conversation_Id = '"
											+ jsonObject2
													.getString("conversationid")
											+ "'", cv);
							ContentValues values = new ContentValues();
							// values.put(DataBase.conversationId,
							// conversation_Id);
							values.put(DataBase.isEmergency, isEmergency);
							values.put(DataBase.isRead, isRead);
							String where = DataBase.conversationId + "= '"
									+ jsonObject2.getString("conversationid")
									+ "'";
							db.update(DataBase.emergency_table,
									DataBase.emergency_int, where, values);

							// }

							// db.insert_content(DataBase.Receive_table,
							// DataBase.Receive_int, cv);
						}

						int uncount = db.getCount(DataBase.emergency_table,
								DataBase.emergency_int, "isread  = 0");
						int readcount = db.getCount(DataBase.emergency_table,
								DataBase.emergency_int, "isread  = 1");
						int Emergencycount = db.getCount(
								DataBase.emergency_table,
								DataBase.emergency_int, null);
						int totalConvCount = db.getCount(
								DataBase.Receive_table, DataBase.Receive_int,
								null);
						Log.e("In the inboxlist class ", "The unread Count  "
								+ uncount + "\n=== The Read Count is "
								+ readcount + " \n======= Total ER Count "
								+ Emergencycount
								+ " \nTotal Conversation Count "
								+ totalConvCount);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						db.close();
					}

					messagePresent = true;

					// handler2.sendEmptyMessage(8);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void updateMessage(Context context, String message,
			boolean clearEdt) {
		String msgConversationId = "";

		try {

			try {
				if (message != null && message.length() > 0) {

					try {

						Log.e("",
								"================ The message inserted =============================== "
										+ message);

						JSONObject jsonObject1 = new JSONObject(message);
						// JSONObject jsonObject = jsonObject1
						// .getJSONObject("response");
						// String error = jsonObject.getString("error");
						// if (error.equals("false")) {
						// JSONArray jsonArray = jsonObject
						// .getJSONArray("conversation");

						// if (jsonArray != null && jsonArray.length() > 0) {
						DataBase db = null;
						try {

							db = new DataBase(context);
							db.open();
							/**
							 * {"msg_id":"192", "sender":"Dave Brown",
							 * "text":"SGkgVGVzdGluZw==", "attachment_type":"0",
							 * "attached_file_name":"", "thumbnail":"",
							 * "date_time":"2013-09-12 07:29:08"},
							 */
							// Log.e("",
							// "============= The Json Array Length is "
							// + jsonArray.length());
							// for (int i = 0; i < jsonArray.length(); i++) {
							String localMsgType = "2";
							String attached_file_name = "";
							String text = "";
							String senderName = "";

							try {
								// JSONObject jsonObject2 = jsonArray
								// .getJSONObject(i);
								JSONObject jsonObject2 = jsonObject1;

								// String msg_Id
								// =jsonObject2.getString("msg_id");
								// String sender =
								// jsonObject2.getString("sender");
								text = jsonObject2.getString("text");
								// Log.e("",
								// " ========== The Text Message is "
								// + text
								// + "=============");
								// Log.e("",
								// " ========== The Text Message is "
								// + text
								// + "=============");

								try {

									try {
										String message_type = jsonObject2
												.getString("attachment_type");
										msgConversationId = jsonObject2
												.getString("conversationid");
										Log.e("",
												"--------------- The Message Type "
														+ message_type
														+ " ----------------");
										if (message_type.equalsIgnoreCase("1")) {
											// Means Text
											// Message
											localMsgType = "0";
										} else if (message_type
												.equalsIgnoreCase("0")) {
											// Means Audio
											// Message
											localMsgType = "1";
										} else if (message_type
												.equalsIgnoreCase("2")) {
											// Means Audio
											// Message
											localMsgType = "2";
										} else if (message_type
												.equalsIgnoreCase("3")) {
											// Means Image
											// Message
											localMsgType = "3";
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									// String message_type =
									// jsonObject2
									// .getString("attachment_type");
									// if (message_type
									// .equalsIgnoreCase("1")) {
									// // Means Video Message
									// localMsgType = "3";
									// } else if (message_type
									// .equalsIgnoreCase("2")) {
									// // Means Audio Message
									// localMsgType = "4";
									// } else if (message_type
									// .equalsIgnoreCase("3")) {
									// // Means Image Message
									// localMsgType = "2";
									// }
								} catch (Exception e) {
									e.printStackTrace();
								}

								try {
									attached_file_name = jsonObject2
											.getString("attached_file_name");
									if (attached_file_name != null
											&& attached_file_name.length() > 0) {
										if (localMsgType.equalsIgnoreCase("2")) {
											attached_file_name = Constants.AUDIO_BASE_URL
													+ attached_file_name;
										} else if (localMsgType
												.equalsIgnoreCase("3")) {
											attached_file_name = Constants.IMAGE_BASE_URL
													+ attached_file_name;
										} else if (localMsgType
												.equalsIgnoreCase("4")) {
											attached_file_name = Constants.VIDEO_BASE_URL
													+ attached_file_name;
										}

									}

								} catch (Exception e) {
									e.printStackTrace();

								}
								try {
									senderName = jsonObject2
											.getString("sender");

								} catch (Exception e) {
									// TODO: handle exception
								}

								String thumbnail = "";
								try {
									thumbnail = jsonObject2
											.getString("thumbnail");
								} catch (Exception e) {
									e.printStackTrace();
								}
								String date_time = jsonObject2
										.getString("date_time");

							} catch (Exception e) {
								e.printStackTrace();
							}

							Log.e("",
									"============= After Decoding text message is "
											+ new String(Base64.decode(
													text.getBytes(),
													Base64.DEFAULT)));

							String name = "";
							try {
								Cursor cursor = db.fetch(
										DataBase.Receive_table,
										DataBase.Receive_int,
										"conversation_Id = '"
												+ msgConversationId + "'",
										null, null);
								if (cursor != null && cursor.getCount() > 0) {
									do {
										name = cursor.getString(3);
										Log.e("",
												"==================== The Name of the Receiver is "
														+ name);
									} while (cursor.moveToNext());

									cursor.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							// if (checkMessageCount()) {
							long rowId = db
									.insert(DataBase.Receive_table,
											DataBase.Receive_int,
											new String[] {
													"0",
													CommonFunctions
															.getPref(
																	context,
																	CommonFunctions.PHONE_NUMBER,
																	""),
													name,
													localMsgType,
													attached_file_name,
													""
															+ System.currentTimeMillis(),
													"1", "0",
													msgConversationId, "0",
													"" + "1", "ghgh", "0", "0",
													"" + text, "", "", "",
													senderName });

							// }

							try {

							} catch (Exception e) {
								// TODO: handle exception
							}

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								db.close();
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}

						// }
						// }

					} catch (JSONException e) {
						e.printStackTrace();

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
