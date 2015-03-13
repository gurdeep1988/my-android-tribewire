package com.eyesayapp.Utils;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import com.amazonaws.util.json.JSONObject;

public class GroupMemberService extends IntentService{

	private Context context;

	public GroupMemberService() {
		
		super("GroupMemberService");
		this.context = this;

	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		getGroups();
		
	}

	private void getGroups() {

		DataBase db = new DataBase(context);
		try {
			db.open();
			Cursor c = db.fetchAll(DataBase.Group_table, DataBase.Group_int,
					"groupname COLLATE NOCASE");
			int size = c.getCount();
			if (size != 0) {
				int i = 0;
				if (c.getCount() > 0) {
					while (c.moveToNext()) {
						String groupId = c.getString(0);
						
						getGroupMemberList(groupId);
						

					}
				}

			} else {

			}
			c.close();

		} catch (Exception e) {
			Debugger.debugE("Error in Fill Group " + e.getMessage());
		} finally {
			db.close();

		}
	}
	
	
	private void getGroupMemberList(String groupId) {
		try {
			
			List<String> nameList = null;
			List<String> valueList = null;
			nameList = new ArrayList<String>();
			nameList.add(Constants.API_KEY_TAG);
			nameList.add(Constants.REF_ID_TAG);
			nameList.add(Constants.COMP_ID_TAG);
			nameList.add(Constants.SESSION_TAG);
			nameList.add("group_id");
			// new RestApiCall(this, CommonFunctions.DEMO_SERVER_URL
			// + TribeWireConstants.COMAPANY_FYI_URL, nameList,
			// Constants.getValueList(this)).execute();

			String api_key = Constants.API_KEY;
			String ref_id = CommonFunctions.getPref(context,
					CommonFunctions.REF_ID, "");
			String com_id = CommonFunctions.getPref(context, Constants.COM_ID, "");

			String session = CommonFunctions.getPref(context, Constants.SESSION,
					"");

			if (valueList == null) {
				valueList = new ArrayList<String>();
				valueList.add(api_key);
				valueList.add(ref_id);
				valueList.add(com_id);
				valueList.add(session);
				valueList.add(groupId);
			}
			
		String groupListResponse = 	CommonFunctions.postNameValueDataOnServer(context,
					nameList.size(), nameList, valueList, CommonFunctions.DEMO_SERVER_URL
					+ CommonFunctions.GET_GROUP_MEMBER_LIST);
		
		parseGroupMemberResponse(groupListResponse, groupId);
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void parseGroupMemberResponse(String response,String GMgroupid) {
		Log.e("", "============== The Response from the Server is " + response);
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject jsonObject2 = jsonObject.getJSONObject("response");
			String error = jsonObject2.getString("error");
			if (error.equalsIgnoreCase("false")) {
				String groupMembers = jsonObject2.getString("group_members");
//				Log.e("",
//						"****************** The Group members are "
//								+ new String(
//										Base64.decode(groupMembers.getBytes(),
//												Base64.DEFAULT)));
				groupMembers = new String(Base64.decode(
						groupMembers.getBytes(), Base64.DEFAULT));
				Log.e("", "=============== The Group Members are after encoding ===== "+groupMembers);
				try {
					try {
						String[] memberArray = groupMembers.split(",");
						if (memberArray!=null && memberArray.length>0) {
							int groupMember = Integer.parseInt(memberArray[0]);	
						}
					} catch (Exception e) {
						e.printStackTrace();
						groupMembers = new String(Base64.decode(
								groupMembers.getBytes(), Base64.DEFAULT));	
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.e("", "=============== The Group Members are after encoding "+groupMembers);
				String[] memberArray = groupMembers.split(",");
				addmembersToGroup(memberArray, GMgroupid);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
	
	/*
	 * "group_id varchar(500),friend_id integer
	 */
	private void addmembersToGroup(String members[], String GMgroupid) {
		DataBase db = null;
		try {
			if (members != null && members.length > 0) {
				try {
                     
					db = new DataBase(context);
					db.open();

					for (int i = 0; i < members.length; i++) {
						int groupMember = Integer.parseInt(members[i]);
						Log.e("", "============== Group Member ID from Server "+groupMember);
						String where = "group_id = '" + GMgroupid
								+ "' and friend_id = " + groupMember;
						int count = db.getCount(DataBase.Group_Member_table,
								DataBase.Group_member_int, where);
						if (!(count > 0)) {
							ContentValues contentValues = new ContentValues();
							contentValues.put("group_id", "" + GMgroupid);
							contentValues.put("friend_id", groupMember);
							long rowId = db.insert_content(
									DataBase.Group_Member_table,
									DataBase.Group_int, contentValues);
							Log.e("",
									"=================== Group Added successfully with rowId "
											+ rowId);

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
	
}
