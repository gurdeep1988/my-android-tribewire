package com.tribewire.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.SyncAddressBook;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.alphabetlist.IndexableListView;
import com.eyesayapp.asyn.ContactSync;
import com.eyesayapp.asyn.GroupAsyn;
import com.eyesayapp.asyn.IContactResult;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.model.SectionHeader;

import eyesay.adapter.AddGroupAdapter;
import eyesay.adapter.ConversationAdapterNew;
import eyesay.adapter.SimpleAdapter;
import eyesay.obj.Friend;

public class AddGroup extends Activity implements OnClickListener,
		IContactResult, OnItemClickListener {

	Button btnCreateGroup;
	TextView txtCancel;
	EditText edtGroupName;
	ProgressDialog pd;
	IndexableListView contacts_list;
	Cursor friends, c;
	public int size = 0;
	public String[] Groupid = null;
	public String[] FriendName = null;
	public String[] FriendNumber = null;
	public int[] Friendid = null;
	public int[] status = null;
	public int[] status_grp = null;
	public int position_contact;
	public int selectedGroup = -1;
	List<Integer> groupMembers = null;
	String currentGroupId = "";
	private Button btn_back;
	LinearLayout conConversationListeners;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_add_group);
		btnCreateGroup = (Button) findViewById(R.id.btn_create_group);
		txtCancel = (TextView) findViewById(R.id.txt_Cancel_group);
		edtGroupName = (EditText) findViewById(R.id.edt_create_group);
		btn_back = (Button) findViewById(R.id.btn_back_all_groups);
		conConversationListeners = (LinearLayout) findViewById(R.id.con_listener);
		txtCancel.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btnCreateGroup.setOnClickListener(this);
		contacts_list = (IndexableListView) findViewById(R.id.list_view);
		contacts_list.setFastScrollEnabled(true);
		contacts_list.setBackgroundColor(Color.WHITE);
		contacts_list.setOnItemClickListener(this);

		super.onCreate(savedInstanceState);
		CreateProgressBar();
		//new ContactSync(this, this).execute();

		groupMembers = new ArrayList<Integer>();
		//edtGroupName.setText("New Group Name");

		FillFriends();
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
              	Toast toast = Toast.makeText(AddGroup.this, FriendName[position],Toast.LENGTH_LONG);
					
					int[] pos = new int[2];
					imageView.getLocationInWindow(pos);
					//tGravity(Gravity.TOP | Gravity.LEFT, pos[0] - ((contentDesc.length() / 2) * 12), pos[1] - 128);
					toast.setGravity(Gravity.TOP | Gravity.LEFT, pos[0] - ((FriendName[position].length() / 2) * 12), pos[1]+50);
					toast.show();   
					
				}
			});
			positionViewMap.put(position, imageView);
			
try {
	if ( friendImage!= null) {
		if (friendImage[position] != null
				&& friendImage[position].length() > 0) {
			// ImageDownloader1.getInstance().download(
			// frdm.getImgUrl(), imageView);

			ConversationAdapterNew.checkImage(
					friendImage[position], imageView);

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

	public Handler handler2 = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {

				try {
				 String message   =  msg.getData().getString("message", "");
				 String emergencyMessage  =  TribeWireUtil.getNotificationMessage(message);
				 TribeWireUtil.showNotificationAlert(AddGroup.this, emergencyMessage, "Emergency");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		};
	

	Handler h = new Handler();
	public Runnable fill = new Runnable() {

		@Override
		public void run() {
			FillFriends();
		}
	};
	String friendImage[];

	boolean isSelected[];
	AddGroupAdapter addGroupAdapter;
	ArrayList<SectionHeader> sectionHeaders;
	public void FillFriends() {
		size = 0;
		DataBase db = new DataBase(AddGroup.this);
		try {

			db.open();

			friends = db.fetchAll(DataBase.Friends_table, DataBase.Friends_int,
					"name COLLATE NOCASE", "status NOT IN (5,6)", null);
			size = friends.getCount();
			if (size != 0) {
				FriendName = new String[size];
				Friendid = new int[size];
				FriendNumber = new String[size];
				// Groupid = new String[size];
				sectionHeaders = new ArrayList<SectionHeader>();
				friendImage = new String[size];
				status = new int[size];
				isSelected = new boolean[size];
				int i = 0;
				String globaltempChar = "";
				while (friends.moveToNext()) {

					FriendName[i] = friends.getString(2);
					FriendNumber[i] = friends.getString(3);
					// Groupid[i] = friends.getString(5);
					status[i] = friends.getInt(6);
					Friendid[i] = friends.getInt(0);
					friendImage[i] = friends.getString(4);
					isSelected[i] = false;
					SectionHeader sectionHeader = new SectionHeader();
					String localChar = FriendName[i].substring(0, 1);
					try {
						if (i == 0) {
							sectionHeader.setIsShow(true);
							String alphabet = FriendName[i].substring(0, 1);
							sectionHeader.setAlphabet(alphabet.toUpperCase());
							sectionHeaders.add(sectionHeader);
							globaltempChar = alphabet;
						} else {
							if (globaltempChar.toUpperCase().equalsIgnoreCase(localChar.toUpperCase())) {
								SectionHeader sectionHeader2 = new SectionHeader();
								sectionHeader2.setAlphabet(null);
								sectionHeader2.setIsShow(false);
								sectionHeaders.add(sectionHeader2);
								globaltempChar   = localChar;
							} else {
	                            SectionHeader sectionHeader2     = new SectionHeader();
	                            String alphabet = FriendName[i].substring(0, 1);
	                            sectionHeader2.setAlphabet(alphabet.toUpperCase());
	                            sectionHeader2.setIsShow(true);
								
	                            globaltempChar  = localChar;
	                            sectionHeaders.add(sectionHeader2);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					i++;
				}

				// lvc = this.getListView();
				addGroupAdapter = new AddGroupAdapter(AddGroup.this,
						FriendName, status, Groupid, FriendNumber, friendImage,
						isSelected,sectionHeaders);
				contacts_list.setAdapter(addGroupAdapter);
				contacts_list.setCacheColorHint(Color.WHITE);

				contacts_list.setRecyclerListener(new RecyclerListener() {

					@Override
					public void onMovedToScrapHeap(View view) {

						try {
							ImageView imageView = (ImageView) view
									.findViewById(R.id.contact_pic);
							imageView.setImageDrawable(null);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

			} else {

				contacts_list.setAdapter(new SimpleAdapter(AddGroup.this,
						getString(R.string.no_contact)));
				contacts_list.setCacheColorHint(Color.WHITE);
				contacts_list.setOnItemClickListener(null);

			}
			friends.close();
		} catch (Exception e) {
			if (pd != null)
				pd.dismiss();
			Debugger.debugE("Error in Fill Group " + e.getMessage());
		} finally {
			db.close();
			if (pd != null)
				pd.dismiss();

		}
		// TenFourTab.loadAds(getBaseContext());
	}

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
	
	// Method for contact starts
	public void CreateProgressBar() {

		pd = new ProgressDialog(AddGroup.this);
		pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pd.setMessage(AddGroup.this.getResources().getString(R.string.loading));
		pd.setMax(5000);

		pd.show();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Log.e("", "============ The Key Code is " + keyCode);
		if (keyCode == 4) {
			Log.e("", "======== So it means back key is pressed =========== ");
			if (edtGroupName.getText().toString().length() > 0) {
				createNewGroup();
			} else {
				Toast.makeText(this, "Please enter group name ",
						Toast.LENGTH_LONG).show();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		if (v == btnCreateGroup) {

		} else if (v == txtCancel) {
			searchandHideKeyboard();

		} else if (v == btn_back) {

			if (edtGroupName.getText().toString().length() > 0) {
				createNewGroup();
			} else {
//				Toast.makeText(this, "Please enter group name ",
//						Toast.LENGTH_LONG).show();
				finish();
			}

		}

	}

	private void addMembersToGroup() {
		try {
			if (groupMembers.size() > 0) {

				if (currentGroupId != null && currentGroupId.length() > 0) {
					DataBase db = null;
					try {
						db = new DataBase(this);
						db.open();

						for (int i = 0; i < groupMembers.size(); i++) {
							int friendId = groupMembers.get(i);
							ContentValues contentValues = new ContentValues();
							contentValues.put("group_id", currentGroupId);
							contentValues.put("friend_id", friendId);
							long rowId = db.insert_content(
									DataBase.Group_Member_table,
									DataBase.Group_member_int, contentValues);
							Log.e("", "======== New Record Row id is " + rowId);
						}

					} catch (Exception e) {
						e.printStackTrace();

					} finally {
						db.close();

					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finish();
		}

	}

	private void createNewGroup() {

		try {

			String groupname = edtGroupName.getText().toString().trim();
			if (groupname.length() != 0
					&& !groupname.toString().trim().equals("")
					&& !groupname.toString().trim().equals(" ")) {

				if (!groupname.matches("^[a-zA-Z0-9][a-zA-Z0-9 ]*[a-zA-Z0-9]$")) {
					CommonFunctions
							.showAlert(
									AddGroup.this,
									"Enter a Valid Group Name",
									AddGroup.this.getResources().getString(
											R.string.warning));
					return;
				}
				// DataBase db = new DataBase(FrameActivity.this);
				// db.open();
				if (!checkGroupName("Add", groupname)) {
					CommonFunctions
							.showAlert(
									AddGroup.this,
									AddGroup.this.getResources().getString(
											R.string.group_exist),

									(!CommonFunctions
											.isFullVersion(getBaseContext())) ? getString(R.string.app_name)
											: getString(R.string.app_name));
					// db.close();
					return;
				}

				if (CommonFunctions.CheckNetConnectivity(AddGroup.this)) {
					pd = ProgressDialog.show(AddGroup.this, "New Group",
							"Please wait while creating your group");
                 pd.setCancelable(true);
             	String groupMemberId = "";
                 try {
					if (groupMembers!=null && groupMembers.size()>0) {
					
						for (int i = 0; i < groupMembers.size(); i++) {
							groupMemberId  += groupMembers.get(i)+",";
							
						}
						if (groupMemberId.endsWith(",")) {
							groupMemberId = groupMemberId.substring(0,
									groupMemberId.length() - 1);
							Log.e("", "================ The fiendsIds are "
									+ groupMemberId);
							groupMemberId = Base64.encodeToString(
									groupMemberId.getBytes(), Base64.DEFAULT);
						}
						
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
                 
                 
					new GroupAsyn(AddGroup.this, "", Base64.encodeToString(
							groupname.getBytes(), Base64.DEFAULT),groupMemberId, false,
							createGrouprequest).execute();
				} else {
					Toast.makeText(AddGroup.this,
							"Please check your internet connectivity",
							Toast.LENGTH_LONG).show();
				}
			} else {
				CommonFunctions.showAlert(AddGroup.this,
						"Please enter proper name to create group",
						AddGroup.this.getResources()
								.getString(R.string.warning));
			}
			//

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	IHttpRequest createGrouprequest = new IHttpRequest() {

		@Override
		public void onError(String onError) {
			try {
				if (pd != null) {
					pd.dismiss();
					Toast.makeText(
							AddGroup.this,
							"Error while creating group , Please try after some time",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onResponse(String response) {

			if (response != null && response.length() > 0) {
				DataBase db = new DataBase(AddGroup.this);
				db.open();
				String groupName = "";
				String groupId = "";
				try {

					try {
						JSONObject jsonObject1 = new JSONObject(response);
						JSONObject jsonObject = jsonObject1
								.getJSONObject("response");
//						groupName = jsonObject.getString("groupname");

						groupId = jsonObject.getString("group_id");
						groupName =edtGroupName.getText().toString();

					} catch (Exception e) {
						e.printStackTrace();
					}

					ContentValues contentValues = new ContentValues();
					contentValues.put("sr_no", groupId);
					contentValues.put("groupname", groupName);
					contentValues.put("type", 0);
					// Type 0 refer to as User defined group

				long rowId =	db.insert_content(DataBase.Group_table, DataBase.Group_int,
							contentValues);

				if (rowId>0) {
					try {
						// Add this group to Friend table Also
						ContentValues cv = new ContentValues();
						cv.put(Friend.NAME, groupName.trim());
						cv.put(Friend.NUMBER, groupId.trim());
						cv.put("email", "");
						cv.put(Friend.ASSCID, 0);

						// Status 5 means it is a group
						cv.put(Friend.STATUS, 5);

						cv.put(Friend.STATUS_MESSAGE, " ");
						cv.put(Friend.GROUPID, groupId);
						cv.put(Friend.INVITE, false);
						db.insert_content(DataBase.Friends_table,
								DataBase.Friends_int, cv);
						currentGroupId = groupId;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					

					db.close();
					addMembersToGroup();

					Toast.makeText(AddGroup.this, "Group Created",
							Toast.LENGTH_LONG).show();

					// FillFriends();
					try {
						// group_lvc.setBackgroundColor(Color.WHITE);
					} catch (Exception e) {
						e.printStackTrace();
					}

					edtGroupName.setText("");

				} catch (Exception e) {
					e.printStackTrace();
				} finally {

					db.close();
					if (pd != null) {
						pd.dismiss();
						pd = null;
					}
				}

			}

			if (pd != null) {
				pd.dismiss();
				pd = null;
			}

		}
	};
	public int position_gr;

	public boolean checkGroupName(String value, String groupname) {
		DataBase db = new DataBase(AddGroup.this);
		try {
			db.open();
			if (value.equals("Add")) {
				// Commented By Gurdeep on 17 November 2012 at 16:29 pm
				String tempGroupName = groupname;
				Log.e("", "Group Name entered By User is ==" + tempGroupName);
				// Cursor cursor = db.fetch(DataBase.Group_table,
				// DataBase.Group_int, "groupname ='"+tempGroupName+"'");
				// Log.e("", "Required Group Count is ==="+cursor.getCount());
				Cursor c = db
						.fetchAll(DataBase.Group_table, DataBase.Group_int);

				Log.e("", "Noida Total  Group Count is ===" + c.getCount());
				if (c.getCount() > 0) {
					c.moveToFirst();
					do {
						// This If condition Commented by Gurdeep on 17th Nov
						// 2012 at 16:50
						// txtnumber.getText().toString().trim() is changes to
						// groupname local param
						// if (c.getString(1).equalsIgnoreCase(
						// txtnumber.getText().toString().trim())) {
						if (c.getString(1).equalsIgnoreCase(groupname.trim())) {
							Log.e("", "Noida Says Group Exists ===");
							c.close();
							db.close();
							return false;
						}
					} while (c.moveToNext());
				} else {
					Log.e("", "Noida Says No Group exists in app ===");
					c.close();
					db.close();
					return true;
				}
				c.close();
				db.close();
			} else {
				// TODO Need to check working of position_gr Field
				Cursor c = db.fetch(DataBase.Group_table, DataBase.Group_int,
						"sr_no!=" + Groupid[position_gr]);
				if (c.getCount() > 0) {
					c.moveToFirst();
					do {
						if (c.getString(1).equalsIgnoreCase(
								edtGroupName.getText().toString().trim())) {
							c.close();
							db.close();
							return false;
						}
					} while (c.moveToNext());
				} else {
					c.close();
					db.close();
					return true;
				}
				c.close();
				db.close();
			}
		} catch (Exception e) {
			Debugger.debugE("Exception in checking Group " + e.getMessage());
		} finally {
			try {
				db.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return true;
	}

	@Override
	public void onContactResponse(String response) {
		try {
			SyncAddressBook.GSSyncContact(AddGroup.this, response);
			//h.post(fill);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onContactError(String onError) {
		Log.e("",
				"======================= Error while refreshing contact ===============");

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		try {
			if (isSelected[position] == false) {
				Log.e("", "============ Selected Color blue");
				arg1.setBackgroundColor(Color.BLUE);
				isSelected[position] = true;
               addAvatar(position);
				addGroupAdapter.changeData(isSelected);
				groupMembers.add(Integer.valueOf(FriendNumber[position]));
				Log.e("", "=========== The Selected Group Members when added"+groupMembers);
			} else {
				Log.e("", "============ Selected Color White");
				arg1.setBackgroundColor(Color.WHITE);
				isSelected[position] = false;
				groupMembers.remove(Integer.valueOf(FriendNumber[position]));
				//groupMembers.remove(Friendid[position]);
				removeAvatar(position);
				Log.e("", "=========== The Selected Group Members after remove"+groupMembers);
				addGroupAdapter.changeData(isSelected);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// try {
		// Log.e("", "=========== Item Clicked " + arg2);
		// if (status != null && status.length > 0) {
		// int groupStatus = status[arg2];
		// if (groupStatus == 5) {
		// Log.e("", "=========== Item Clicked friend id"
		// + Friendid[arg2] + " Friend Number "
		// + FriendNumber[arg2]);
		// Intent intent = new Intent(AddGroup.this,
		// GroupMembers.class);
		// intent.putExtra("groupid", "" + FriendNumber[arg2]);
		// intent.putExtra("groupname", "" + FriendName[arg2]);
		// startActivity(intent);
		//
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	private void searchandHideKeyboard() {
		try {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtGroupName.getWindowToken(), 0);
			edtGroupName.setText("");

			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
	}
}
