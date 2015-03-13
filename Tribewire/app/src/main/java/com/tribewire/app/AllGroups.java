package com.tribewire.app;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.TribeWireConstants;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.Utils.Utils;
import com.eyesayapp.asyn.GetGroupList;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.RestApiCall;
import com.google.android.gms.internal.fi;
import com.tribewire.app.AllGroups.GroupsAdapter.ViewHolder;

import eyesay.obj.Friend;

public class AllGroups extends Activity implements OnItemClickListener,
		OnClickListener, TextWatcher, IHttpRequest {

	ListView group_lvc;
	public ProgressDialog pd;
	Button btnAddGroup;
	Button btnBack;
	private MyTouchListener mOnTouchListener;
	private int action_down_x = 0;
	private int action_up_x = 0;
	private int difference = 0;
	private RelativeLayout rltSearchBar;
	private EditText edtSearchText;
	private Button btnSearch;

	private TextView txtCancelSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_all_group);
		group_lvc = (ListView) findViewById(R.id.lst_all_groups);

		btnAddGroup = (Button) findViewById(R.id.btn_add_group);
		edtSearchText = (EditText) findViewById(R.id.edt_search_contact);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnSearch = (Button) findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(searchListener);
		txtCancelSearch = (TextView) findViewById(R.id.txt_search_cancel);
		txtCancelSearch.setOnClickListener(searchListener);
		btnAddGroup.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		group_lvc.setOnItemClickListener(this);
		edtSearchText.addTextChangedListener(this);
		rltSearchBar = (RelativeLayout) findViewById(R.id.rlt_search_bar);
		super.onCreate(savedInstanceState);
		mOnTouchListener = new MyTouchListener();

		FillGroup();
		getAllGroups();
	}

	OnClickListener searchListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v == btnSearch) {
				showSearchBar();
			} else if (v == txtCancelSearch) {
				hideSearchBar();
				FillGroup();
			}
		}
	};

	@Override
	public void onResume() {
		ActivityStatus.setActivity(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		ActivityStatus.setActivity(null);
		super.onPause();
	}
	
	

	private void showSearchBar() {
		try {
			if (rltSearchBar.getVisibility() == View.GONE) {
				TribeWireUtil.showKeyboard(this, edtSearchText);
				rltSearchBar.setVisibility(View.VISIBLE);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void hideSearchBar() {
		try {

			if (rltSearchBar.getVisibility() == View.VISIBLE) {
				TribeWireUtil.hideKeyboard(this, edtSearchText);
				rltSearchBar.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	

	public Handler handler2 = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {

				try {
				 String message   =  msg.getData().getString("message", "");
				 String emergencyMessage  =  TribeWireUtil.getNotificationMessage(message);
				 TribeWireUtil.showNotificationAlert(AllGroups.this, emergencyMessage, "Emergency");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		};
	@Override
	public void onClick(View v) {
		if (v == btnAddGroup) {
			Intent intent = new Intent(this, AddGroup.class);
			startActivityForResult(intent, 23);

		} else if (v == btnBack) {
			finish();
		}

	}

	private void getAllGroups() {
		try {
			if (CommonFunctions.CheckNetConnectivity(AllGroups.this)) {

				pd = ProgressDialog.show(AllGroups.this, "Group Detail",
						"Please wait while getting group list");
				pd.setCancelable(true);
				new GetGroupList(AllGroups.this, getGroupList).start();

			} else {
				Toast.makeText(AllGroups.this,
						"Please check your internet connectivity",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public IHttpRequest getGroupList = new IHttpRequest() {

		@Override
		public void onResponse(String response) {

			try {
				if (response != null && response.length() > 0) {

					try {
						JSONObject jsonObject1 = new JSONObject(response);
						JSONObject jsonObject = jsonObject1
								.getJSONObject("response");
						// JSONObject jsonObject = new JSONObject(response);
						JSONArray groupArray = jsonObject
								.getJSONArray("groups");
						if (groupArray != null && groupArray.length() > 0) {
							DataBase db = new DataBase(AllGroups.this);
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
										groupName = new String(Base64.decode(
												groupName.getBytes(),
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
											values.put("groupname", groupName);
											values.put("type", 0);
											db.update(
													DataBase.Group_table,
													DataBase.Group_int,
													"sr_no = '" + groupId + "'",
													values);
											try {
												// Add this group to Friend
												// table Also
												ContentValues cv = new ContentValues();
												cv.put(Friend.NAME,
														groupName.trim());
												cv.put(Friend.NUMBER,
														groupId.trim());
												cv.put("email", "");
												cv.put(Friend.ASSCID, 0);

												// Status 5 means it is a group
												cv.put(Friend.STATUS, 5);

												cv.put(Friend.STATUS_MESSAGE,
														" ");
												cv.put(Friend.GROUPID, groupId);
												cv.put(Friend.INVITE, false);
												db.update(
														DataBase.Friends_table,
														DataBase.Friends_int,
														""
																+ Friend.NUMBER
																+ "= '"
																+ groupId
																		.trim()
																+ "'", cv);

											} catch (Exception e) {
												e.printStackTrace();
											}
											Log.e("",
													"======= Group Updated successfully=====");

										} else {
											ContentValues values = new ContentValues();
											values.put("sr_no", groupId);
											values.put("groupname", groupName);
											values.put("type", 0);
											db.insert_content(
													DataBase.Group_table,
													DataBase.Group_int, values);
											try {
												// Add this group to Friend
												// table Also
												ContentValues cv = new ContentValues();
												cv.put(Friend.NAME,
														groupName.trim());
												cv.put(Friend.NUMBER,
														groupId.trim());
												cv.put("email", "");
												cv.put(Friend.ASSCID, 0);

												// Status 5 means it is a group
												cv.put(Friend.STATUS, 5);

												cv.put(Friend.STATUS_MESSAGE,
														" ");
												cv.put(Friend.GROUPID, groupId);
												cv.put(Friend.INVITE, false);
												db.insert_content(
														DataBase.Friends_table,
														DataBase.Friends_int,
														cv);

											} catch (Exception e) {
												e.printStackTrace();
											}
											Log.e("",
													"======= Group Added  successfully=====");
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

						else {
							Toast.makeText(
									AllGroups.this,
									"Group list is empty, Go ahead and create an group",
									Toast.LENGTH_LONG).show();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			FillGroup();

			if (pd != null) {
				pd.dismiss();
			}
		}

		@Override
		public void onError(String onError) {

			if (pd != null) {
				pd.dismiss();
			}
		}
	};

	// Method for contact ends

	// Method for group starts
	public String[] Groupid = null;
	public String[] friendsMenu = null;

	// Group Selection
	public String[] GroupName = null;
	public int[] GMid = null;
	public String[] DisplayGroup = null;
	public int[] totalMember = null;
	public int size = 0;
	public int[] contactid = null;
	int TotalActiveFriend[] = null;
	public int[] status_grp = null;
	public int[] groupType = null;
	public Model modelArray[] = null;

	private void FillGroup() {

		DataBase db = new DataBase(AllGroups.this);
		try {
			db.open();
			Cursor c = db.fetchAll(DataBase.Group_table, DataBase.Group_int,
					"groupname COLLATE NOCASE");
			size = c.getCount();
			if (size != 0) {

				GroupName = new String[size];
				Groupid = new String[size];
				DisplayGroup = new String[size];
				totalMember = new int[size];
				status_grp = new int[size];
				contactid = new int[size];
				TotalActiveFriend = new int[size];
				groupType = new int[size];
				modelArray = new Model[size];
				int i = 0;
				if (c.getCount() > 0) {
					while (c.moveToNext()) {
						// Cursor count = db.fetchAll(DataBase.Friends_table,
						// DataBase.Friends_int, "name", "groupid like '%"
						// + c.getInt(0) + "%'");

						Model model = new Model();
						model.setVisible(false);
						modelArray[i] = model;
						Cursor count = db.fetch(DataBase.Group_Member_table,
								DataBase.Group_member_int,
								"group_id='" + c.getString(0) + "'");
						GroupName[i] = c.getString(1);
						DisplayGroup[i] = "" + (count.getCount()) + "";
						Groupid[i] = "" + c.getString(0);

						Log.e("", "The Group Id is " + Groupid[i]);
						totalMember[i] = count.getCount();

						status_grp[i] = -2;
						contactid[i] = 0;
						try {
							groupType[i] = c.getInt(2);
							Log.e("", "========= Group Type " + groupType[i]
									+ "== Group Count " + DisplayGroup[i]
									+ " group Name " + GroupName[i]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						TotalActiveFriend[i] = db.fetchCounts(
								DataBase.Friends_table, DataBase.Friends_int,
								new String[] { "sr_no" },
								"groupid like '%" + c.getInt(0)
										+ "%' and status!=-1");
						i++;
						count.close();
					}
				}

				handler.sendEmptyMessage(2);
				// group_lvc.setOnItemClickListener(this);
				// registerForContextMenu(group_lvc);
				// pd.dismiss();

			} else {
				handler.sendEmptyMessage(3);

			}
			c.close();

		} catch (Exception e) {
			Debugger.debugE("Error in Fill Group " + e.getMessage());
		} finally {
			db.close();
			// pd.dismiss();

		}
	}

	GroupsAdapter groupsAdapter;
	private static int SHOW_PROGRESS = 10;
	private static int DISMISS_PROGRESS = 11;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				FillGroup();
			} else if (msg.what == 2) {
				groupsAdapter = new GroupsAdapter(AllGroups.this, Groupid,
						GroupName, DisplayGroup, groupType, modelArray);
				group_lvc.setAdapter(groupsAdapter);
				// group_lvc.setAdapter(new
				// ArrayAdapter<String>(AddressBook.this,
				// R.layout.simple_list_item_1, DisplayGroup));
				group_lvc.setCacheColorHint(Color.TRANSPARENT);

			} else if (msg.what == 3) {
				// group_lvc.setBackgroundResource(R.drawable.eyesay_create_groups);
				group_lvc.setBackgroundResource(R.drawable.groupbgwtext);
				// group_lvc.setAdapter(new SimpleAdapter(this,
				// getString(R.string.no_group)));

				group_lvc.setCacheColorHint(Color.TRANSPARENT);
				group_lvc.setOnItemClickListener(null);
				group_lvc.setOnItemLongClickListener(null);
			} else if (msg.what == 4) {
				groupsAdapter.changeData(Groupid, GroupName, DisplayGroup,
						groupType, modelArray);
				Toast.makeText(AllGroups.this, "Group Deleted Successfully",
						Toast.LENGTH_LONG).show();
			} else if (msg.what == SHOW_PROGRESS) {
				try {
					pd = ProgressDialog.show(AllGroups.this, "Delete Group",
							"Deleting Group Please wait ........");
					pd.setCancelable(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == DISMISS_PROGRESS) {
				try {
					if (pd != null) {
						pd.dismiss();
					}
					FillGroup();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		try {

			Log.e("", "========== The Group Type " + groupType[arg2]);
			if (groupType[arg2] == 0) {
				Intent intent = new Intent(AllGroups.this, GroupMembers.class);
				intent.putExtra("groupid", "" + Groupid[arg2]);
				intent.putExtra("groupname", "" + GroupName[arg2]);
				startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		handler.sendEmptyMessage(1);
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class MyTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			ViewHolder holder = (ViewHolder) v
					.getTag(R.layout.listitem_all_groups);
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
				calcuateDifference(holder, position);
				action_down_x = 0;
				action_up_x = 0;
				difference = 0;
				break;
			}
			return true;
		}
	}

	private void calcuateDifference(final ViewHolder holder, final int position) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Log.e("", "============ The difference " + difference);
				if (difference <= 15 && difference >= -15) {
					// Toast.makeText(AllGroups.this, position,
					// Toast.LENGTH_LONG)
					// .show();
					Log.e("", "========== The Group Type "
							+ groupType[position]);
					boolean isAdmin = false;
					if (groupType[position] == 0) {
						isAdmin = false;

					} else {
						isAdmin = true;
					}
					Intent intent = new Intent(AllGroups.this,
							GroupMembers.class);
					intent.putExtra("groupid", "" + Groupid[position]);
					intent.putExtra("groupname", "" + GroupName[position]);
					intent.putExtra("isadmin", isAdmin);
					startActivityForResult(intent, 45);
					Log.e("",
							"------------------------------------------------");
				}
				if (difference > 65) {
					holder.btnRemove.setVisibility(View.VISIBLE);
					modelArray[position].setVisible(true);
					groupsAdapter.changeData(Groupid, GroupName, DisplayGroup,
							groupType, modelArray);
					// items.get(position).setVisible(true);
					//
					// mAdapter.changeData(items);

					// Toast.makeText(AllGroups.this,
					// "Right to Left - " + position, Toast.LENGTH_LONG)
					// .show();
				}
				if (difference < -65) {
					holder.btnRemove.setVisibility(View.GONE);
					modelArray[position].setVisible(false);
					groupsAdapter.changeData(Groupid, GroupName, DisplayGroup,
							groupType, modelArray);
					// items.get(position).setVisible(false);
					// mAdapter.changeData(items);
					// Toast.makeText(AllGroups.this,
					// "Left to Right - " + position, Toast.LENGTH_LONG)
					// .show();
				}
			}
		});
	}

	// Delete the group from server
	private void deleteGroupFromServer(String groupId) {

		try {
			if (CommonFunctions.isInternetConnected(this)) {
				handler.sendEmptyMessage(SHOW_PROGRESS);
				List<String> nameList = Constants.getNameList();
				nameList.add("group_id");
				List<String> valueList = Constants.getValueList(this);
				valueList.add(groupId);
				new RestApiCall(this, CommonFunctions.DEMO_SERVER_URL
						+ TribeWireConstants.DELETE_GROUP_URL, nameList,
						valueList).start();

			} else {
				Toast.makeText(AllGroups.this,
						"Please check your internet connection ",
						Toast.LENGTH_LONG).show();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void deleteGroup(String groupId, int index) {

		deleteGroupFromServer(groupId);
		DataBase db = null;
		try {
			db = new DataBase(this);
			db.open();

			Log.e("", "========== Group Id =========" + groupId);
			boolean isDeleted = db.delete(DataBase.Group_table,
					DataBase.Group_int, "sr_no = '" + groupId + "'");
			Log.e("", "=========== Is Deleted " + isDeleted);
			boolean isDeletedFromFriend = db.delete(DataBase.Friends_table,
					DataBase.Friends_int, "" + Friend.NUMBER + " = '" + groupId
							+ "'");
			Log.e("", "=========== Is Deleted from friend "
					+ isDeletedFromFriend);

			int count = db.getCount(DataBase.Group_Member_table,
					DataBase.Group_member_int, "group_id = '" + groupId + "'");
			Log.e("", "========================== Count is " + count);
			if (count > 0) {
				boolean isDeletedMember = db.delete(
						DataBase.Group_Member_table, DataBase.Group_member_int,
						"group_id = '" + groupId + "'");
				Log.e("", "=========== Is Deleted Member " + isDeletedMember);

			}

			if (isDeleted) {

				try {

					// FillGroup();
					// handler.sendEmptyMessage(4);
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

	public class GroupsAdapter extends BaseAdapter {

		private String groupId[];
		private String groupName[];
		private String groupCount[];
		private int groupType[];

		Model modelArray[];
		Typeface tf;

		public GroupsAdapter(Context context, String groupId[],
				String groupName[], String groupCount[], int groupType[],
				Model modelArray[]) {
			this.groupId = groupId;
			this.groupName = groupName;
			this.groupCount = groupCount;
			this.groupType = groupType;
			this.modelArray = modelArray;
			tf = TribeWireUtil.getArialTypeface(AllGroups.this);
		}

		public void changeData(String groupId[], String groupName[],
				String groupCount[], int groupType[], Model modelArray[]) {
			this.groupId = groupId;
			this.groupName = groupName;
			this.groupCount = groupCount;
			this.groupType = groupType;
			this.modelArray = modelArray;

			notifyDataSetChanged();
		}

		@Override
		public int getCount() {

			return groupId.length;
		}

		@Override
		public Object getItem(int position) {

			return groupId[position];
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {

				convertView = getLayoutInflater().inflate(
						R.layout.listitem_all_groups, null);

				holder.btnRemove = (Button) convertView
						.findViewById(R.id.btn_remove);
				holder.txtGroupName = (TextView) convertView
						.findViewById(R.id.txt_group_name);
				holder.txtGroupCount = (TextView) convertView
						.findViewById(R.id.txt_group_count);
				holder.relativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.rlt_group_bg);
				final int finalPositon = position;
				holder.txtGroupName.setTypeface(tf);
				holder.btnRemove.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.e("", "==========================================");
						modelArray[finalPositon].setVisible(false);
						v.setVisibility(View.GONE);
						deleteGroup(groupId[finalPositon], finalPositon);

					}
				});
				convertView.setTag(R.layout.listitem_all_groups, holder);

			} else {
				holder = (ViewHolder) convertView
						.getTag(R.layout.listitem_all_groups);
			}
			convertView.setTag(position);
			convertView.setOnTouchListener(mOnTouchListener);

			holder.btnRemove.setTag(position);
			if (this.modelArray[position] != null) {
				try {
					if (modelArray[position].isVisible())
						holder.btnRemove.setVisibility(View.VISIBLE);
					else
						holder.btnRemove.setVisibility(View.GONE);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					holder.txtGroupName.setText(groupName[position]);
					if (groupType[position] == 1) {
						holder.txtGroupName.setTextColor(getResources()
								.getColor(R.color.tribewire_grey));
						holder.relativeLayout.setBackgroundColor(getResources()
								.getColor(R.color.lighgrey));

					} else {
						holder.txtGroupName.setTextColor(getResources()
								.getColor(R.color.green_user_group));

						holder.relativeLayout.setBackgroundColor(getResources()
								.getColor(R.color.white));

					}
					holder.txtGroupCount.setText(groupCount[position]);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			return convertView;
		}

		public class ViewHolder {
			public TextView txtGroupName;
			public TextView txtGroupCount;
			public RelativeLayout relativeLayout;

			public Button btnRemove;
		}

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence searchText, int start, int before,
			int count) {
		Log.e("", "The Text Change sequence is ======" + searchText);

		searchGroup(searchText);

	}

	private void searchGroup(CharSequence searchKeyword) {

		DataBase db = new DataBase(AllGroups.this);
		Cursor c = null;
		try {
			db.open();

			if (searchKeyword.length() > 0) {
				// friends = db.fetchContact(DataBase.Friends_table,
				// DataBase.Friends_int, ""
				// + ""+Friend.NAME+"" + " LIKE '" + searchText + "%'", null);

				// friends = db.fetchContact(DataBase.Friends_table,
				// DataBase.Friends_int, searchText.toString());

				c = db.fetchContactGroup(DataBase.Group_table,
						DataBase.Group_int, " groupname like '" + searchKeyword
								+ "%'", null, null);
			} else {
				c = db.fetchAll(DataBase.Group_table, DataBase.Group_int,
						"groupname");
			}
			size = c.getCount();
			Log.e("", "============= Group Search Count " + size);
			if (size != 0) {

				GroupName = new String[size];
				Groupid = new String[size];
				DisplayGroup = new String[size];
				totalMember = new int[size];
				status_grp = new int[size];
				contactid = new int[size];
				TotalActiveFriend = new int[size];
				groupType = new int[size];
				modelArray = new Model[size];
				int i = 0;
				if (c.getCount() > 0) {
					while (c.moveToNext()) {
						// Cursor count = db.fetchAll(DataBase.Friends_table,
						// DataBase.Friends_int, "name", "groupid like '%"
						// + c.getInt(0) + "%'");

						if (c.getString(0) != null
								&& c.getString(0).length() > 0) {

							Model model = new Model();
							model.setVisible(false);
							modelArray[i] = model;
							Cursor count = db.fetch(
									DataBase.Group_Member_table,
									DataBase.Group_member_int,
									"group_id='" + c.getString(0) + "'");
							GroupName[i] = c.getString(1);
							DisplayGroup[i] = "" + (count.getCount()) + "";
							Groupid[i] = "" + c.getString(0);

							Log.e("", "The Group Id is " + Groupid[i]);
							totalMember[i] = count.getCount();

							status_grp[i] = -2;
							contactid[i] = 0;
							try {
								groupType[i] = c.getInt(2);
								Log.e("", "========= Group Type "
										+ groupType[i] + "== Group Count "
										+ DisplayGroup[i]);
							} catch (Exception e) {
								e.printStackTrace();
							}
							TotalActiveFriend[i] = db
									.fetchCounts(DataBase.Friends_table,
											DataBase.Friends_int,
											new String[] { "sr_no" },
											"groupid like '%" + c.getInt(0)
													+ "%' and status!=-1");
							i++;
							count.close();
						} else {
							Log.e("", "========= One null value=======");

						}

					}
				}

				handler.sendEmptyMessage(2);
				// group_lvc.setOnItemClickListener(this);
				// registerForContextMenu(group_lvc);
				// pd.dismiss();

			} else {
				handler.sendEmptyMessage(3);

			}
			c.close();

		} catch (Exception e) {
			Debugger.debugE("Error in Fill Group " + e.getMessage());
		} finally {
			db.close();
			// pd.dismiss();

		}

	}

	@Override
	public void onResponse(String response) {
		Log.e("",
				"===================== The Delete Group Response from Server is "
						+ response);

		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject jsonObject2 = jsonObject.getJSONObject("response");
			String error = jsonObject2.getString("error");
			if (error.equalsIgnoreCase("false")) {
				handler.sendEmptyMessage(DISMISS_PROGRESS);
			}
			else {
				Toast.makeText(AllGroups.this, "Error  while deleting group , Please try again later", Toast.LENGTH_LONG).show();
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onError(String onError) {
		Log.e("",
				"===================== The Delete Group Error from Server is "
						+ onError);
		handler.sendEmptyMessage(DISMISS_PROGRESS);

	}

}
