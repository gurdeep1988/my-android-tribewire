package com.tribewire.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.util.json.JSONObject;
import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.asyn.GroupAsyn;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.RestApiCall;

import eyesay.adapter.ConversationAdapterNew;
import eyesay.adapter.GroupMemberAdapter;
import eyesay.adapter.GroupMemberAdapter.GroupMemberWrapper;
import eyesay.adapter.SimpleAdapter;
import eyesay.obj.Friend;

/**
 * 
 * @author Garry Activity Used to show Members
 */
public class GroupMembers extends Activity implements OnClickListener,
		OnItemClickListener, IHttpRequest {

	String GMgroupid = "";

	// public String id[];//, names[], phonenumber[], invites[];
	// public boolean flag[];

	ProgressDialog pd;
	// ArrayList<String> updateMemberList ;

	String groupName = "";
	Button addEditGroup;
	private ListView lstGroupMembers;
	private EditText txtGroupName;
	private MyTouchListener mOnTouchListener;
	private int action_down_x = 0;
	private int action_up_x = 0;
	private int difference = 0;
	private Button btnbackToAllGroup;
	boolean isSelected[];
	boolean isAdmin = false;
	LinearLayout conConversationListeners;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_group_memeber);
		 this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		txtGroupName = (EditText) findViewById(R.id.txt_group_name);
		lstGroupMembers = (ListView) findViewById(R.id.lst_group_members);
		addEditGroup = (Button) findViewById(R.id.btn_add_member);
		btnbackToAllGroup = (Button) findViewById(R.id.btn_back_allgroup);
		conConversationListeners = (LinearLayout) findViewById(R.id.con_listener);
		addEditGroup.setOnClickListener(this);
		btnbackToAllGroup.setOnClickListener(this);
		TextView textView = (TextView) findViewById(R.id.group_name);

		try {
			GMgroupid = this.getIntent().getExtras().getString("groupid");
			groupName = this.getIntent().getExtras().getString("groupname");
			isAdmin = getIntent().getExtras().getBoolean("isadmin");
			Log.e("", "=============== is Admin  ========= " + isAdmin);
			textView.setText(groupName.toUpperCase());
			txtGroupName.setText(groupName);
			if (isAdmin) {
				txtGroupName.setEnabled(false);
			}
			Log.e("", "My Group Id is " + GMgroupid);
		} catch (Exception e) {
			e.printStackTrace();

		}
		lstGroupMembers.setOnItemClickListener(this);

		showProgressDialog();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new Thread(new Runnable() {

					@Override
					public void run() {
						Looper.prepare();
						fillGroupMembers();
						Looper.loop();

					}
				}).start();

			}
		});
		setResult(RESULT_OK);
		// if (isAdmin) {
		getGroupMemberList();
		// }
		super.onCreate(savedInstanceState);
		mOnTouchListener = new MyTouchListener();
	}

	

	public Handler handler2 = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {

				try {
				 String message   =  msg.getData().getString("message", "");
				 String emergencyMessage  =  TribeWireUtil.getNotificationMessage(message);
				 TribeWireUtil.showNotificationAlert(GroupMembers.this, emergencyMessage, "Emergency");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		};
	
	private void showProgressDialog() {
		pd = ProgressDialog.show(this, "Loading",
				"Please wait as we load your contacts");
		pd.setCancelable(true);

	}

	Handler gr_mem_handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {
				pd = ProgressDialog.show(GroupMembers.this, "Loading",
						"Please wait while getting group members");
				pd.setCancelable(true);
			} else if (msg.what == 2) {
				try {
					if (pd != null) {
						pd.dismiss();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
	};
	public List<String> FriendName = null;
	public List<String> FriendNumber = null;
	public List<String> friendImage = null;
	public List<Integer> status = null;
	public List<Integer> Friendid = null;
	public List<Model> models = null;

	private void fillGroupMembers() {

		DataBase db = new DataBase(GroupMembers.this);
		// Cursor contact = null;
		// Cursor c = null;
		db.open();

		//String where = "status !=5";
		String where = "status NOT IN (5,6)";
		Cursor contact = db.fetchAll(DataBase.Friends_table,
				DataBase.Friends_int, "name COLLATE NOCASE", where, null);

		try {
			if (contact.getCount() > 0) {

				int i = 0;
				int size = contact.getCount();
				FriendName = new ArrayList<String>();
				Friendid = new ArrayList<Integer>();
				FriendNumber = new ArrayList<String>();
				// Groupid = new String[size];
				friendImage = new ArrayList<String>();
				status = new ArrayList<Integer>();
				models = new ArrayList<Model>();
				isSelected = new boolean[size];
				while (contact.moveToNext()) {

					try {
						Model model = new Model();
						model.setVisible(false);
						FriendName.add(contact.getString(2));
						FriendNumber.add(contact.getString(3));
						Friendid.add(contact.getInt(0));
						status.add(contact.getInt(6));
						friendImage.add(contact.getString(4));
						models.add(model);

						if (isGroupMember(Integer.parseInt(contact
								.getString(Friend.INT_NUMBER)), db)) {
							isSelected[i] = true;
							addAvatar(i);
						} else {
							isSelected[i] = false;
						}
						i++;
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				if (FriendName.size() > 0) {
					handler.sendEmptyMessage(2);
				} else {
					handler.sendEmptyMessage(3);

				}
				// gr_mem_handler.sendEmptyMessage(1);

				pd.dismiss();
			} else {

				lstGroupMembers.setAdapter(new SimpleAdapter(GroupMembers.this,
						"No Member for this group"));
				lstGroupMembers.setOnCreateContextMenuListener(null);
				lstGroupMembers.setCacheColorHint(Color.TRANSPARENT);
				lstGroupMembers.setOnItemClickListener(null);

				pd.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (contact != null)
				contact.close();
			// if (c != null)
			// c.close();
			if (pd != null) {
				pd.dismiss();
			}
			db.close();
		}

	}

	private boolean isGroupMember(int friend_id, DataBase db) {

		try {

			int cursor = db.getCount(DataBase.Group_Member_table,
					DataBase.Group_member_int, "group_id ='" + GMgroupid + "'"
							+ " and friend_id=" + friend_id);
			Log.e("", "Cursor Count " + cursor + " And Friend Id is "
					+ friend_id);
			if (cursor > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {

			e.printStackTrace();
			return false;

		}

	}

	private boolean isValidGroupMember(int friend_id) {
		DataBase db = null;
		try {
			db = new DataBase(this);
			db.open();
			int cursor = db.getCount(DataBase.Group_Member_table,
					DataBase.Group_member_int, "group_id ='" + GMgroupid + "'"
							+ " and friend_id=" + friend_id);
			Log.e("", "Cursor Count " + cursor + " And Friend Id is "
					+ friend_id);
			if (cursor > 0) {

				return true;
			} else {
				return false;
			}
		} catch (Exception e) {

			e.printStackTrace();
			return false;

		} finally {
			db.close();
		}

	}

	@Override
	public void onClick(View v) {
		if (v == addEditGroup) {
			Intent intent = new Intent(GroupMembers.this,
					AddEditGroupMembers.class);
			intent.putExtra("groupid", "" + GMgroupid);
			intent.putExtra("groupname", "" + groupName);
			startActivityForResult(intent, 23);
		} else if (v == btnbackToAllGroup) {

			if (!isAdmin) {
				if (txtGroupName.getText().length() > 0) {
					updateGroupMembers();
				} else {
					Toast.makeText(this, "Please enter group name",
							Toast.LENGTH_LONG).show();
				}
			}
			else {
				finish();
			}
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	protected void onResume() {
		ActivityStatus.appStatus(this, true);
		ActivityStatus.setActivity(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		ActivityStatus.appStatus(null, false);
		ActivityStatus.setActivity(null);
		try {
			if (pd != null) {
				pd.dismiss();
				pd = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	GroupMemberAdapter groupMemberAdapter = null;
	private static final int SHOW_GROUP_MEMBERS = 19;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {

				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						Looper.prepare();
						fillGroupMembers();
						Looper.loop();

					}
				});
				thread.start();

			} else if (msg.what == 2) {
				groupMemberAdapter = new GroupMemberAdapter(GroupMembers.this,
						FriendName, status, Friendid, FriendNumber,
						friendImage, models, mOnTouchListener, isSelected);
				lstGroupMembers.setAdapter(groupMemberAdapter);
			} else if (msg.what == 3) {
				lstGroupMembers.setAdapter(new SimpleAdapter(GroupMembers.this,
						"No Member for this group"));
				lstGroupMembers.setOnCreateContextMenuListener(null);
				lstGroupMembers.setCacheColorHint(Color.TRANSPARENT);
				lstGroupMembers.setOnItemClickListener(null);
			} else if (msg.what == 9) {
				pd = ProgressDialog.show(GroupMembers.this, "Group Update",
						"Please while updating your group members");
				pd.setCancelable(true);
			} else if (msg.what == 10) {
				if (pd != null) {
					pd.dismiss();
					pd = null;
				}
			} else if (msg.what == SHOW_GROUP_MEMBERS) {
				fillGroupMembers();
				gr_mem_handler.sendEmptyMessage(2);
			}
		};
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		handler.sendEmptyMessage(1);
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class Model {

		private String list_item;
		private boolean isVisible;

		public String getList_item() {
			return list_item;
		}

		public void setList_item(String list_item) {
			this.list_item = list_item;
		}

		public boolean isVisible() {
			return isVisible;
		}

		public void setVisible(boolean isVisible) {
			this.isVisible = isVisible;
		}
	}

	public class MyTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			GroupMemberWrapper holder = (GroupMemberWrapper) v
					.getTag(R.layout.friendlist);
			int action = event.getAction();
			int position = (Integer) v.getTag();

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				action_down_x = (int) event.getX();
				Log.d("action", "ACTION_DOWN - ");
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d("action", "ACTION_MOVE - ");
				action_up_x = (int) event.getX();
				difference = action_down_x - action_up_x;
				break;
			case MotionEvent.ACTION_UP:
				Log.d("action", "ACTION_UP - ");
				calcuateDifference(holder, position, v);
				action_down_x = 0;
				action_up_x = 0;
				difference = 0;
				break;
			}
			return true;
		}
	}

	private void calcuateDifference(final GroupMemberWrapper holder,
			final int position, final View view) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (!isAdmin) {

					if (difference <= 15 && difference >= -15) {
						// Toast.makeText(AllGroups.this, position,
						// Toast.LENGTH_LONG)
						// .show();

						try {
							if (isSelected[position] == false) {
								Log.e("", "============ Selected Color blue");
								view.setBackgroundColor(getResources()
										.getColor(R.color.skyblue));
								isSelected[position] = true;
								addGroupMember(position);

								// groupMembers.add(Friendid[position]);
								groupMemberAdapter.changeData(
										GroupMembers.this, FriendName, status,
										Friendid, FriendNumber, friendImage,
										models, isSelected);
								addAvatar(position);
								
							} else {
								Log.e("", "============ Selected Color White");
								view.setBackgroundColor(Color.WHITE);

								isSelected[position] = false;
								// groupMembers.remove(Friendid[position]);
								deleteGroupMember(position);
								groupMemberAdapter.changeData(
										GroupMembers.this, FriendName, status,
										Friendid, FriendNumber, friendImage,
										models, isSelected);
								removeAvatar(position);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						Log.e("",
								"------------------------------------------------");
					}
					if (difference > 75) {
						if (isValidGroupMember(Friendid.get(position))) {
							holder.getBtnRemove().setVisibility(View.VISIBLE);
							models.get(position).setVisible(true);

							groupMemberAdapter.changeData(GroupMembers.this,
									FriendName, status, Friendid, FriendNumber,
									friendImage, models, isSelected);

						} else {
							Toast.makeText(
									GroupMembers.this,
									" Cannot remove as not a group member"
											+ position, Toast.LENGTH_LONG)
									.show();
						}

					}
					if (difference < -75) {
						if (holder.getBtnRemove().getVisibility() == View.VISIBLE) {
							holder.getBtnRemove().setVisibility(View.GONE);
							models.get(position).setVisible(false);
							groupMemberAdapter.changeData(GroupMembers.this,
									FriendName, status, Friendid, FriendNumber,
									friendImage, models, isSelected);

						}
						// items.get(position).setVisible(false);
						// mAdapter.changeData(items);
						// Toast.makeText(GroupMembers.this,
						// "Left to Right - " + position, Toast.LENGTH_LONG)
						// .show();
					}

				}

			}
		});
	}

	public void deleteMemberFromGroup(int position) {
		DataBase db = new DataBase(this);
		try {
			db.open();
			String groupId = GMgroupid;
			int friendId = Friendid.get(position);
			Log.e("", "=========== The Group Id is " + groupId + " Friend Id "
					+ friendId);
			String where = "group_id = '" + groupId + "' and friend_id ="
					+ friendId;
			Boolean isDelete = db.delete(DataBase.Group_Member_table,
					DataBase.Group_member_int, where);
			Log.e("", "============= If is deleted " + isDelete);
			if (isDelete) {
				// fillGroupMembers();
				friendImage.remove(position);
				Friendid.remove(position);
				FriendName.remove(position);
				FriendNumber.remove(position);
				status.remove(position);
				models.remove(position);
				groupMemberAdapter
						.changeData(this, FriendName, status, Friendid,
								FriendNumber, friendImage, models, isSelected);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

	}

	// Used to edit the groupName
	private void editGroupName() {
		try {

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void updateGroupMembers() {
		try {

			groupName = txtGroupName.getText().toString();
			DataBase db = null;
			Cursor cursor = null;

			try {
				db = new DataBase(this);
				db.open();

				cursor = db.fetch(DataBase.Group_Member_table,
						DataBase.Group_member_int, "group_id= '" + GMgroupid
								+ "'");
				Log.e("", "========== The Cursor Count is " + cursor.getCount());
				if (cursor != null && cursor.getCount() > 0) {

					String friendIds = "";
					//if (cursor.moveToFirst()) {
						do {

							try{
	                             Log.e("", "============ The Friend id "+cursor.getInt(2));
	 							Cursor c = db.fetch(DataBase.Friends_table,
	 									DataBase.Friends_int, Friend.NUMBER + " = "
	 											+ cursor.getInt(2));
	 							 Log.e("", "========== The SUb Cursor Count is -------------- "+c.getCount());

	 							friendIds += "" + c.getString(Friend.INT_NUMBER) + ",";
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						} while (cursor.moveToNext());

					//}

					try {
						if (friendIds.endsWith(",")) {
							friendIds = friendIds.substring(0,
									friendIds.length() - 1);
							Log.e("", "================ The fiendsIds are "
									+ friendIds);
							friendIds = Base64.encodeToString(
									friendIds.getBytes(), Base64.DEFAULT);
						}

						if (CommonFunctions
								.CheckNetConnectivity(GroupMembers.this)) {
							pd = ProgressDialog
									.show(GroupMembers.this, "Group Update",
											"Please wait while updating your group members");
							pd.setCancelable(true);
							String encodedGroupName = Base64
									.encodeToString(
											groupName.trim().getBytes(),
											Base64.DEFAULT);
							new GroupAsyn(this, "" + GMgroupid,
									encodedGroupName, friendIds, true,
									updateGroup).execute();
						} else {
							Toast.makeText(this,
									"Please check your internet connectivity",
									Toast.LENGTH_LONG).show();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						db.close();
						cursor.close();
					}
				} else {
					if (CommonFunctions.CheckNetConnectivity(GroupMembers.this)) {

						handler.sendEmptyMessage(9);
						String encodedGroupName = Base64.encodeToString(
								groupName.trim().getBytes(), Base64.DEFAULT);
						new GroupAsyn(this, "" + GMgroupid, encodedGroupName,
								"", true, updateGroup).execute();
					} else {
						Toast.makeText(this,
								"Please check your internet connectivity",
								Toast.LENGTH_LONG).show();
					}
					db.close();
					cursor.close();
					// handler.sendEmptyMessage(10);

					// finish();

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	IHttpRequest updateGroup = new IHttpRequest() {

		@Override
		public void onResponse(String response) {
			Log.e("", "========== The response " + response);
			if (response != null && response.length() > 0) {
				Toast.makeText(GroupMembers.this, "Group Updated successfully",
						Toast.LENGTH_LONG).show();
				DataBase db = null;
				try {
					db = new DataBase(GroupMembers.this);
					db.open();
					ContentValues cv = new ContentValues();

					cv.put("groupname", groupName);
					String where = "sr_no = '" + GMgroupid + "'";
					boolean isNameUpdated = db.update(DataBase.Group_table,
							DataBase.Group_int, where, cv);
					Log.e("",
							"========== Group Name is updated successfully ========="
									+ isNameUpdated);
					ContentValues contentValues = new ContentValues();
					contentValues.put(Friend.NAME, groupName);
					String where2 = "" + Friend.NUMBER + " = '" + GMgroupid
							+ "'";
					try {
						boolean isNameUpdated1 = db.update(
								DataBase.Friends_table, DataBase.Friends_int,
								where2, contentValues);
						Log.e("",
								"========== Group Name is updated successfully in Friend Table========="
										+ isNameUpdated1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (db != null) {
						db.close();

					}
				}
			}

			handler.sendEmptyMessage(10);

			finish();

		}

		@Override
		public void onError(String onError) {

			try {

			} catch (Exception e) {
				e.printStackTrace();
			}

			Toast.makeText(GroupMembers.this, "Error while Updating group",
					Toast.LENGTH_LONG).show();
			if (pd != null) {
				pd.dismiss();
				pd = null;
			}
		}
	};

	private void addGroupMember(int position) {
		try {
			DataBase db = new DataBase(GroupMembers.this);
			try {

				db.open();
				ContentValues values = new ContentValues();
				values.put("group_id", GMgroupid);
				// TODO Testing FriendId
				values.put("friend_id", FriendNumber.get(position));
				db.insert_content(DataBase.Group_Member_table,
						DataBase.Group_member_int, values);
				// updateMemberList.add(tv.id);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void deleteGroupMember(int position) {
		DataBase db = new DataBase(GroupMembers.this);
		try {

			db.open();
			ContentValues values = new ContentValues();
			values.put("group_id", GMgroupid);
			// TODO Testing FriendId
			values.put("friend_id", FriendNumber.get(position));
		boolean isDeleted =	db.delete(DataBase.Group_Member_table, DataBase.Group_member_int,
					"group_id='" + GMgroupid + "'" + " and friend_id="
							+ FriendNumber.get(position));

			// updateMemberList.remove(tv.id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

	}

	@Override
	public void onError(String onError) {
		gr_mem_handler.sendEmptyMessage(2);
	}

	/**
	 * {"response":{"error":false,"error_msg":"","group_members":
	 * "MzMsMjMsMzQsMzc="}}
	 */
	@Override
	public void onResponse(String response) {

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
				String[] memberArray = groupMembers.split(",");
				addmembersToGroup(memberArray);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			handler.sendEmptyMessage(SHOW_GROUP_MEMBERS);
			
		}

	}

	/*
	 * "group_id varchar(500),friend_id integer
	 */
	private void addmembersToGroup(String members[]) {
		DataBase db = null;
		try {
			if (members != null && members.length > 0) {
				try {
                     
					db = new DataBase(this);
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
					gr_mem_handler.sendEmptyMessage(2);

				} finally {
					
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

	private void getGroupMemberList() {
		try {
			gr_mem_handler.sendEmptyMessage(1);
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
			String ref_id = CommonFunctions.getPref(this,
					CommonFunctions.REF_ID, "");
			String com_id = CommonFunctions.getPref(this, Constants.COM_ID, "");

			String session = CommonFunctions.getPref(this, Constants.SESSION,
					"");

			if (valueList == null) {
				valueList = new ArrayList<String>();
				valueList.add(api_key);
				valueList.add(ref_id);
				valueList.add(com_id);
				valueList.add(session);
				valueList.add(GMgroupid);
			}
			//
			new RestApiCall(this, CommonFunctions.DEMO_SERVER_URL
					+ CommonFunctions.GET_GROUP_MEMBER_LIST, nameList,
					valueList).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	HashMap<Integer, ImageView> positionViewMap   = new HashMap<Integer, ImageView>();
	private void addAvatar(final int position){
		Log.e("", "======================= The Add Index is "+position);
	          try {
	        ImageView view	=positionViewMap.get(position);
	        	if (view!=null) {
					return;
					
				}
			} catch (Exception e) {
				//e.printStackTrace(); 
				
			}
		
		try {
			final ImageView imageView = new ImageView(this);
			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			final float scale = getResources().getDisplayMetrics().density;
			int pixels = (int) (55 * scale + 0.5f);
			layoutParams.width = pixels;
			layoutParams.height = pixels;
			layoutParams.setMargins(10, 10, 10, 10);
			imageView.setLayoutParams(layoutParams);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
              	Toast toast = Toast.makeText(GroupMembers.this, FriendName.get(position),Toast.LENGTH_LONG);
					
					int[] pos = new int[2];
					imageView.getLocationInWindow(pos);
					//tGravity(Gravity.TOP | Gravity.LEFT, pos[0] - ((contentDesc.length() / 2) * 12), pos[1] - 128);
					toast.setGravity(Gravity.TOP | Gravity.LEFT, pos[0] - ((FriendName.get(position).length() / 2) * 12), pos[1]+50);
					toast.show();   
					
				}
			});
			positionViewMap.put(position, imageView);
			
try {
	if ( friendImage!= null) {
		if (friendImage.get(position) != null
				&& friendImage.get(position).length() > 0) {
			// ImageDownloader1.getInstance().download(
			// frdm.getImgUrl(), imageView);

			ConversationAdapterNew.checkImage(
					friendImage.get(position), imageView);

		} else {
			imageView.setImageDrawable(this.getResources()
					.getDrawable(R.drawable.no_image));
		}
	} else {
		imageView.setImageDrawable(this.getResources()
				.getDrawable(R.drawable.no_image));
	}
} catch (Exception e) {
	e.printStackTrace();
}
			conConversationListeners.addView(imageView);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void removeAvatar(int position){
		try {
			Log.e("", "======================= The Remove Index is "+position);
			ImageView view	=positionViewMap.get(position);
        	if (view!=null) {
        		conConversationListeners.removeView(view);
        		positionViewMap.remove(position);
				
			}
			
		} catch (Exception e) {
		  e.printStackTrace();
		
		}
		
		
	}
	
	
	

}
