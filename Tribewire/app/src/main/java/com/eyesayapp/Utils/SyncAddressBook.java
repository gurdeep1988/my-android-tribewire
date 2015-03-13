package com.eyesayapp.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;
import eyesay.obj.Friend;

/**
 * Shared class for sync the contacts to server
 * 
 * @author xtpl
 * 
 */ 
public class SyncAddressBook {

	

	public static int getIndex(String commaSaperatednumbers, String number) {
		Debugger.debugE("SDB : GetIndex : phone book cont"
				+ commaSaperatednumbers + "acctive cont  : " + number);
		String numbers[] = commaSaperatednumbers.split(",");
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i].equals(number)) {
				Debugger.debugE("SDB : Number Found : " + i);
				return i;
			}
		}
		return -1;
	}

	public final static int LIMIT = 100;

	

	//public static void AddPhoneBook(Context context) {}

	private void trimUsNumber(String number) {
		Log.e("", "Before Trimming Number is  " + number);
		if (number.length() > 10) {
			if (number.startsWith("1") || number.startsWith("2")) {
				String requiredNumber = number.substring(1);
				Log.e("", "Now Required Number is " + requiredNumber);
			}
		}

	}

	/**
	 * {"error":false,"error_msg":"",
	 * "contact_list":[{"username":"Bill Harris","user_id":"NA==","online":false}]
	 * }
	 * 
	 * @param context
	 */
	public static void GSSyncContact(Context context, String jsonResponse) {
		if (CommonFunctions.isInternetConnected(context)) {

			String response = jsonResponse;

			DataBase db = null;

			if (response != null && response.length() > 0) {

				String number  = "";
				try {
				        JSONObject responseObj1  = new JSONObject(response);
				            JSONObject responseObj =  responseObj1.getJSONObject("response");
		//		            JSONObject responseObj = new JSONObject(response);
				        boolean error   = responseObj.getBoolean("error");
				        Log.e("", "The Error Value is "+error);
				        if (!error) {
							 JSONArray contactArray  =    responseObj.getJSONArray("contact_list");
							 if (contactArray!=null && contactArray.length()>0) {
								 db = new DataBase(context);
									db.open();
									for (int i = 0; i < contactArray.length(); i++) {
										
										JSONObject contactObj  = contactArray.getJSONObject(i);
										String userName = contactObj.getString("username");
										String user_id   = contactObj.getString("user_id");
										String statusType  = contactObj.getString("status_type");
										String statusMessage  = contactObj.getString("status");
										
										String profile_image  = "";
										
										try {
											profile_image  = contactObj.getString("profile_photo");
										} catch (Exception e) {
											e.printStackTrace();
										}
										
									try {
										number = new String(Base64.decode(user_id.getBytes(), Base64.DEFAULT));
									} catch (Exception e) {
									      e.printStackTrace();
									      Log.e("", "Error while encoding base64 String "+user_id);
									}
										
										int count = db.getCount(DataBase.Friends_table,
												DataBase.Friends_int, Friend.NUMBER + " = '" + number
														+ "'");
										Log.e("", " Db Count + "+count +"  Number " + number +"Name "+userName +" \n Image "+profile_image
												);
										
										if (!(count > 0)) {
											ContentValues contentValues = new ContentValues();
											contentValues.put(Friend.NAME, userName
													.trim());
											contentValues.put(Friend.NUMBER, number.trim());
											contentValues.put("email", profile_image);
											contentValues.put(Friend.ASSCID, 0);
											if (statusType.equalsIgnoreCase("0")) {
												contentValues.put(Friend.STATUS, 0);
											}
											else if(statusType.equalsIgnoreCase("1")) {
												contentValues.put(Friend.STATUS, 1);
											}
											else if(statusType.equalsIgnoreCase("2")) {
												contentValues.put(Friend.STATUS, 2);
											}
											else if(statusType.equalsIgnoreCase("3")) {
												contentValues.put(Friend.STATUS, 3);
											}
											else if(statusType.equalsIgnoreCase("4")) {
												contentValues.put(Friend.STATUS, 4);
											}
											contentValues.put(Friend.STATUS_MESSAGE, statusMessage);
											contentValues.put(Friend.GROUPID, 0);
											contentValues.put(Friend.INVITE, false);
											db.insert_content(DataBase.Friends_table,
													DataBase.Friends_int, contentValues);
											
											Log.e("",
													"*****User Inserted Successfully *****");
										} else {
											Cursor c = null;
											try {
												c = db.fetch(DataBase.Friends_table,
														DataBase.Friends_int, Friend.NUMBER
																+ " = '" + number
																+ "'");

												if (c != null && c.getCount() > 0) {
													int sr_no = c.getInt(0);
													Log.e("", "Db User Serial Id is " + sr_no);
													try {
										 				ContentValues contentValues = new ContentValues();
														contentValues.put(Friend.NUMBER, number.trim());
														contentValues.put("email", profile_image);			
														if (statusType.equalsIgnoreCase("0")) {
															contentValues.put(Friend.STATUS, 0);
														}
														else if(statusType.equalsIgnoreCase("1")) {
															contentValues.put(Friend.STATUS, 1);
														}
														else if(statusType.equalsIgnoreCase("2")) {
															contentValues.put(Friend.STATUS, 2);
														}
														else if(statusType.equalsIgnoreCase("3")) {
															contentValues.put(Friend.STATUS, 3);
														}
														else if(statusType.equalsIgnoreCase("4")) {
															contentValues.put(Friend.STATUS, 4);
														}
														contentValues.put(Friend.STATUS_MESSAGE, statusMessage);
														Log.e("",
																"***************User Updated Successfully "
																		+ db.update(
																				DataBase.Friends_table,
																				DataBase.Friends_int,
																				"_id = " + sr_no,
																				contentValues)+"***************");

													} catch (Exception e) {
														e.printStackTrace();
														CommonFunctions.writeLog("498 Json Exception "
																+ e.getMessage());
													}

												}
												else {
													Log.e("",
															"**********Unable to  Update User Record********** ");
												}
											} catch (Exception e) {
												e.printStackTrace();
//												CommonFunctions.writeLog("504 Json Exception "
//														+ e.getMessage());
											} finally {
												if (c != null) {
													c.close();
												}
											}
										} 
									}	
									
									
								 
							}
							 else {
								 Log.e("", " Either contact Array is Null or Size equal to Zero So Length is  "+contactArray.length());
								 
							 }
						}
				        else {
				        	try {
				        		Log.e("", " Error Value is true with message "+responseObj.getString("error_msg"));
							} catch (Exception e) {
								e.printStackTrace();
							}
				        	
				        }
				        
				        
				} catch (JSONException e) {
					e.printStackTrace();
					if (db!=null) {
						db.close();
					}
				}
				

				// Numbers that are in server DB

			}

		}

	}

}
