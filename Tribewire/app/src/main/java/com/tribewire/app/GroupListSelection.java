package com.tribewire.app;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.eyesay.dialog.EyesayonWayDialog;
import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.GroupModel;
import com.eyesayapp.Utils.Utils;

import eyesay.obj.Friend;

public class GroupListSelection extends Activity {

	private Button btndone;
	private Button sendButton;
	// private ListView lvc;
	private CheckBox btnAddAll;
	public ProgressDialog pd;
	public String[] GroupName = null;
	public String[] DisplayGroup = null;
	public int[] totalMember = null;
	public String[] Groupid = null;
	public int[] status = null;
	public int[] contactid = null;
	public boolean flag[];

	public int position;
	public int size = 0;
	public String id[], names[], phonenumber[], invites[];
	// Group Member Variables
	public int MODE = 0;
	public int GMgroupid = 0;

	public String GMnames[], GMGroup[];
	public int GMstatus[];
	public String GMid[];
	public int GMcount = 0;

	int Activecount = 0;

	// Count Active User from Group...
	int TotalActiveFriend[] = null;
	final String TAG = "TenFourGroup";
	public String baseurl;

	private boolean reply;
	private int message_id;
	private String conversation_id;
	private String phonenumberr;
	private String contactName;
	private String media_message_url = "";
	private String draftsMessage = "";
	public EyesayonWayDialog eyesayonWayDialog;
	
	private LinearLayout linearLayout;
	View view;
	ArrayList<View> viewList = new ArrayList<View>();
	private String textMessage="";
    private String image_url="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_list_selection);
		

		try {
			media_message_url = getIntent().getExtras().getString(
					Utils.MEDIA_MESSAGE_KEY);
		} catch (Exception e) {

		}

		try {
			draftsMessage = getIntent().getExtras().getString(
					Utils.MESSAGE_DRAFTS);
		} catch (Exception e) {

		}

		try {
			 image_url  = getIntent().getExtras().getString(Utils.TEXT_IMAGE_URL_KEY);
			 Log.e("", "The Image Url is "+image_url);
		} catch (Exception e) {
			e.printStackTrace();

			
		}
		try {
			textMessage = this.getIntent().getExtras()
					.getString(Utils.TEXT_MESSAGE_KEY);
			Log.e("", "The text message  is "+textMessage);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			if (this.getIntent().getExtras() != null) {
				phonenumberr = this.getIntent().getStringExtra("numbers");
				contactName = this.getIntent().getExtras()
						.getString("friendName");
				if (this.getIntent().getExtras().containsKey("reply")) {
					reply = this.getIntent().getExtras().getBoolean("reply");
				}

				if (this.getIntent().getExtras().containsKey("message_id")) {
					message_id = this.getIntent().getExtras()
							.getInt("message_id");
				}
				if (this.getIntent().getExtras().containsKey("conversation_id")) {

					conversation_id = this.getIntent().getExtras()
							.getString("conversation_id");
				}
			}
		} catch (Exception e) {

			Debugger.debugE("Parameter received is wrong,....");
		}

		initView();
		initListener();
		initData();
	}

	@Override
	protected void onStart() {
	
		super.onStart();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
	}
	private void initView() {
		btndone = (Button) findViewById(R.id.group_btndone);
		sendButton = (Button) findViewById(R.id.select_group_btn_send);
		
		// lvc = (ListView) findViewById(R.id.select_group_list_view);
		linearLayout = (LinearLayout) findViewById(R.id.linear_layout_group);
		btnAddAll = (CheckBox) this.findViewById(R.id.group_btnAddAll);
	}

	private void initListener() {
		btnAddAll.setOnClickListener(click);
		btndone.setOnClickListener(click);
		sendButton.setOnClickListener(click);
		
		// lvc.setOnItemClickListener(click_list);
	}

	private void initData() {
		// TODO Auto-generated method stub
		CreateProgressBar();

		// h.post(mpost);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {

						FillGroup();

					}
				});
				thread.start();

			}
		});
	}

	public void CreateProgressBar() {
		pd = new ProgressDialog(GroupListSelection.this);
		pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pd.setMessage(" Please Wait..");
		pd.setCancelable(false);
		pd.show();
	}

	private void createMyProgressBar() {
		eyesayonWayDialog = new EyesayonWayDialog(this);
		eyesayonWayDialog.setCancelable(true);
		eyesayonWayDialog.show();

	}

	Handler h = new Handler();
	Runnable mpost = new Runnable() {
		@Override
		public void run() {
			FillGroup();
			pd.dismiss();
		}
	};
	private Cursor count;
	private Cursor c;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try {
				if (viewList != null) {
					for (int i = 0; i < viewList.size(); i++) {
						linearLayout.addView(viewList.get(i));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				viewList.clear();
				viewList = null;
			}

		};
	};

	public void FillGroup() {
		// try {
		// TenFourTab.loadAds(getBaseContext());
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		DataBase db = new DataBase(GroupListSelection.this);
		try {
			db.open();
			c = db.fetchAll(DataBase.Group_table, DataBase.Group_int,
					"groupname");
			size = c.getCount();

			if (size != 0) {
				LayoutInflater inflater = this.getLayoutInflater();
				GroupName = new String[size];
				Groupid = new String[size];
				DisplayGroup = new String[size];
				totalMember = new int[size];
				status = new int[size];
				contactid = new int[size];
				TotalActiveFriend = new int[size];
				flag = new boolean[size];
				int i = 0;
				if (c.getCount() > 0) {
					while (c.moveToNext()) {
						// count = db.fetchAll(DataBase.Friends_table,
						// DataBase.Friends_int, "name", "groupid like '%"
						// + c.getInt(0) + "%'");
						count = db.fetch(DataBase.Group_Member_table,
								DataBase.Group_member_int,
								"group_id='"+c.getString(0)+"'");
						GroupName[i] = c.getString(1);
						DisplayGroup[i] = c.getString(1) + " ("
								+ (count.getCount()) + ")";
						Groupid[i] = "" + c.getString(0);
						totalMember[i] = count.getCount();
						status[i] = -2;
						contactid[i] = 0;
						TotalActiveFriend[i] = db.fetchCounts(
								DataBase.Friends_table, DataBase.Friends_int,
								new String[] { "sr_no" },
								"groupid like '%" + c.getInt(0)
										+ "%' and status!=-1");
						flag[i] = false;

						view = inflater.inflate(R.layout.contacts, null);
						TextView textView = (TextView) view
								.findViewById(R.id.text);
						textView.setText(DisplayGroup[i]);
						ImageView imageView = (ImageView) view
								.findViewById(R.id.imgperson);
						imageView.setVisibility(View.GONE);

						view.setTag(new TagValue(i, false));

						viewList.add(view);

						view.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								TagValue tv = (TagValue) v.getTag();

								Log.e("", " Selector " + tv.selector + " Pos "
										+ tv.pos);
								if (tv.selector == false) {
									v.setBackgroundColor(Color
											.parseColor("#2AAAFF"));
									tv.selector = true;
									v.setTag(tv);
									flag[tv.pos] = true;
								} else {
									v.setBackgroundColor(Color.WHITE);

									tv.selector = false;
									v.setTag(tv);
									flag[tv.pos] = false;
									// flag[pos] = true;
								}

							}
						});

						i++;
						count.close();

					}
					handler.sendEmptyMessage(1);
				}

				// lvc = this.getListView();
				// lvc.setAdapter(new GroupListAdapter(GroupListSelection.this,
				// DisplayGroup, status, Groupid, null));
				// lvc.setAdapter(new ArrayAdapter<String>(AddressBook.this,
				// R.layout.simple_list_item_1, DisplayGroup));
				// lvc.setCacheColorHint(Color.TRANSPARENT);
				// lvc.getCount();
				// registerForContextMenu(lvc);
				pd.dismiss();

			} else {
				// lvc.setAdapter(new SimpleAdapter(this,
				// getString(R.string.no_group)));
				// lvc.setCacheColorHint(Color.TRANSPARENT);
				// lvc.setOnItemClickListener(null);
				// lvc.setOnItemLongClickListener(null);

			}
			c.close();

		} catch (Exception e) {
			Debugger.debugE("Error in Fill Group " + e.getMessage());
			e.printStackTrace();
		} finally {
			db.close();
			pd.dismiss();

		}
	}

	public OnClickListener click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			 
			 if (v.getId() == R.id.group_btnAddAll) {
			} else if (v.getId() == R.id.select_group_btn_send) {
				boolean isSelected=false;
				if (flag!=null && flag.length>0) {
					for (int i = 0; i < flag.length; i++) {
						if (flag[i]) {
							isSelected = true;
							break;
						}
					}
				}
				if (isSelected) {
					if (CommonFunctions.isInternetConnected(GroupListSelection.this)) {
						new Handler().post(new Runnable() {

							@Override
							public void run() {
								createMyProgressBar();

							}
						});

						Thread thread = new Thread(sendRecordedMessage);
						thread.start();						
					}
					
					else {
						CommonFunctions.showAlert(GroupListSelection.this, "Please check your internet connectivity", "Network Error");
					}
					

				}
				else {
					CommonFunctions.showAlert(GroupListSelection.this, "Please select some group in order to send", "Select Recipient");
				}
			} else if (v.getId() == R.id.group_btndone) {
				/*
				 * CheckBox iv=null; if(!iv.isChecked()){
				 * Toast.makeText(getApplicationContext(),
				 * "no group is slected ", 3000).show(); } else{ // }
				 */
				addDataToMultipleBucket();
				finish();
			}

		}
	};

	Runnable sendRecordedMessage = new Runnable() {

		public void run() {
			Looper.prepare();
			String mediaMessage = "";
			try {
				baseurl = CommonFunctions.getBaseURL();
			} catch (Exception e) {
				e.printStackTrace();
			}
			boolean messageType = false;
			if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_AUDIO)) {
				mediaMessage = baseurl + "temporary/android.wav";
				messageType = true;
			} else if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_VIDEO)) {
				mediaMessage = baseurl + "temporary/android.3gpp";
				messageType = false;
			} 
			else if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_IMAGE)) {
				mediaMessage = image_url;
				Log.e("", "The Media Message is ================== "+mediaMessage);
				messageType = false;
			}
			else if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_DRAFTS)) {
				if (draftsMessage != null && draftsMessage.length() > 0) {
					mediaMessage = draftsMessage;
					Log.e("", "==== Drafts Message True " + draftsMessage);
					setResult(RESULT_OK);
				} else {
					CommonFunctions
							.showAlert(
									GroupListSelection.this,
									"Unable to send draft message, Invalid file format",
									"Error");
					return;
				}
			}

			try {

				Debugger.debugE("in sending...");
				String selectedPhoneNumbers = "";
				String selectedNames = "";
				int friendSelectedIndex = 0;
                ArrayList<String> grList = new ArrayList<String>();  
                
                ArrayList<GroupModel> grModel = new ArrayList<GroupModel>();
				for (int j = 0; j < flag.length; j++) {
					if (flag[j]) {
						//
						 String groupMembers="";
						 GroupModel  groupModel = new GroupModel();
						 String groupId = Groupid[j];
						 String groupName = GroupName[j];
						 groupModel.setGroupName(groupName);
						 groupModel.setGroupIds(groupId);
						 grList.add(groupName) ;
						 
						 DataBase db = new DataBase(GroupListSelection.this);
						 //String groupIds = "";
						 try {
							db.open();
							Cursor c = db.fetchAll(DataBase.Group_Member_table,
									DataBase.Group_member_int, null,
									"group_id = '"+groupId+"' " );
							
							if (c.getCount() > 0) {
								try {
							//		groupIds +="-g_"+groupId+"-";
									
									if (c.moveToFirst()) {
										do {

											int friendId = c.getInt(2);
											Cursor frd_cur = null;
											try {
												frd_cur = db.fetch(
														DataBase.Friends_table,
														DataBase.Friends_int,
														"sr_no = " + friendId);
												frd_cur.moveToFirst();
												// frd_cur
												// .getString(Friend.INT_SR_NO);
												//
												//
												// frd_cur
												// .getString(Friend.INT_INVITE);

												groupMembers +=frd_cur
														.getString(Friend.INT_NUMBER)
														+ ",";
//												Log.e("",
//														"Selected Number "
//																+ frd_cur
//																		.getString(Friend.INT_NUMBER)
//																+ "and Name "
//																+ frd_cur
//																		.getString(Friend.INT_NAME));
												selectedPhoneNumbers += "-"+frd_cur
														.getString(Friend.INT_NUMBER)
														+ "-,";
												
												selectedNames += frd_cur
														.getString(Friend.INT_NAME)
														+ ",";
												friendSelectedIndex++;

											} catch (Exception e) {
												e.printStackTrace();
											} finally {
												frd_cur.close();
											}
										
										} while (c.moveToNext());
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									c.close();
								}
								
							}

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							// if (contactGID != null)
							// contactGID.close();

							db.close();
						}
						// for (int i = 0; i < contactGID.getCount(); i++) {

						// }

						//
						try {
							if (groupMembers.endsWith(",")) {
							groupMembers =	groupMembers.substring(0, groupMembers.length()-1);
							}
							
//							if (groupIds.endsWith(",")) {
//								groupIds =	groupIds.substring(0, groupIds.length()-1);
//								}
						} catch (Exception e) {
							e.printStackTrace();
						}
						groupModel.setGroupMember(groupMembers);
						//groupModel.setGroupIds(groupIds);
						grModel.add(groupModel);	
					}
				}
				if (friendSelectedIndex > 0) {

					if (selectedPhoneNumbers.endsWith(","))
						selectedPhoneNumbers = selectedPhoneNumbers.substring(
								0, selectedPhoneNumbers.length() - 1);
					if (selectedNames.endsWith(","))
						selectedNames = selectedNames.substring(0,
								selectedNames.length() - 1);
					try {
						if (eyesayonWayDialog != null) {
							eyesayonWayDialog.getHandlerObj().sendEmptyMessage(
									9);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if (mediaMessage.length()<=2 ||  mediaMessage.endsWith(".jpg")|| mediaMessage.endsWith(".gif")|| mediaMessage.endsWith(".jpeg") || mediaMessage.endsWith(".mp4")||mediaMessage.toLowerCase().endsWith(".3gpp")||mediaMessage.toLowerCase().endsWith(".mov")|| mediaMessage.endsWith(".wav") ) {

						if (CommonFunctions.SendTextGroupMessage(GroupListSelection.this,
								messageType, mediaMessage, selectedPhoneNumbers,
								selectedNames, reply, conversation_id,grList,grModel, textMessage)) {
							try {
								if (eyesayonWayDialog != null) {
									eyesayonWayDialog.getHandlerObj()
											.sendEmptyMessage(11);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							Debugger.debugE("in mesage id ... + " + message_id);
							Log.e("", "The Commma Separated Numbers are "
									+ selectedPhoneNumbers + "\n Names "
									+ selectedNames);

						}
						
						else {
							insertGroupData();		
						}
						
					}
					
					else{ 
					if (CommonFunctions.SendGroupMessage(GroupListSelection.this,
							messageType, mediaMessage, selectedPhoneNumbers,
							selectedNames, reply, conversation_id,grList,grModel)) {
						try {
							if (eyesayonWayDialog != null) {
								eyesayonWayDialog.getHandlerObj()
										.sendEmptyMessage(11);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						Debugger.debugE("in mesage id ... + " + message_id);
						Log.e("", "The Commma Separated Numbers are "
								+ selectedPhoneNumbers + "\n Names "
								+ selectedNames);

					}
					
					else {
						
					}
				}
				}
			}

			catch (Exception e) {
				e.printStackTrace();
			} finally {

				if (eyesayonWayDialog != null) {
					eyesayonWayDialog.dismiss();
				} // startActivity(intent);
				try {
					if (media_message_url
							.equalsIgnoreCase(Utils.MESSAGE_DRAFTS)) {
						if (draftsMessage != null && draftsMessage.length() > 0) {

							Log.e("", "==== Drafts Message True "
									+ draftsMessage);

						}
						// Intent intent = new Intent(GroupListSelection.this,
						// EyeSayDefaultAudioVideo.class);
						// startActivity(intent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				setResult(777);
				finish();
			}
			Looper.loop();
		}
	};

	@Override
	protected void onResume() {
		ActivityStatus.appStatus(this, true);
		super.onResume();
	}

	@Override
	protected void onPause() {
		ActivityStatus.appStatus(null, false);
		super.onPause();
	}

	private class TagValue {
		Integer pos;
		Boolean selector;

		public TagValue(Integer pos, Boolean selector) {
			this.pos = pos;
			this.selector = selector;
		}
	}
	
	private void addDataToMultipleBucket() {

		Intent  data = null;
		 try {
       	  baseurl = CommonFunctions.getBaseURL();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String selectedPhoneNumbers = "";
			String selectedNames="";
			int friendSelectedIndex = 0;
			String mediaMessage="";
			ArrayList<String>  grList = new ArrayList<String>();
			ArrayList<GroupModel> grModel = new ArrayList<GroupModel>();
			for (int j = 0; j < flag.length; j++) {
			
				if (flag[j]) {
					//
					GroupModel groupModel = new GroupModel();
					String groupId = Groupid[j];
					Log.e("", "============== The Group Id is -----------------------"+groupId);
					String groupName = GroupName[j];
					grList.add(groupName);
					groupModel.setGroupName(groupName);
					groupModel.setGroupIds(groupId);
					DataBase db = new DataBase(GroupListSelection.this);
					String groupMembers="";
					String groupMemberNames = "";
					//String groupIds  = "";
					try {
						db.open();
						Cursor c = db.fetchAll(DataBase.Group_Member_table,
								DataBase.Group_member_int, null,
								"group_id = '"+groupId+"'"  );
						//groupIds +="-g_"+groupId+"-,";
						if (c.getCount() > 0) {
							try {
								if (c.moveToFirst()) {
									do {
										
										int friendId = c.getInt(2);
										Cursor frd_cur = null;
										try {
											frd_cur = db.fetch(
													DataBase.Friends_table,
													DataBase.Friends_int,
													"sr_no = " + friendId);
											frd_cur.moveToFirst();
											// frd_cur
											// .getString(Friend.INT_SR_NO);
											//
											//
											// frd_cur
											// .getString(Friend.INT_INVITE);

											Log.e("",
													"Selected Number "
															+ frd_cur
																	.getString(Friend.INT_NUMBER)
															+ "and Name "
															+ frd_cur
																	.getString(Friend.INT_NAME));
											//selectedPhoneNumbers += "-"+phonenumber[i]+"-,";
											selectedPhoneNumbers += "-"+frd_cur
													.getString(Friend.INT_NUMBER)
													+ "-,";
											selectedNames += frd_cur
													.getString(Friend.INT_NAME)
													+ ",";
											groupMembers +="-"+frd_cur
													.getString(Friend.INT_NUMBER)
													+ "-,";
											groupMemberNames +=""+frd_cur
													.getString(Friend.INT_NAME)
													+ ",";
											friendSelectedIndex++;

										} catch (Exception e) {
											e.printStackTrace();
										} finally {
											frd_cur.close();
										}
									} while (c.moveToNext());
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								c.close();
							}

						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// if (contactGID != null)
						// contactGID.close();

						db.close();
					}
					// for (int i = 0; i < contactGID.getCount(); i++) {

					// }

					//
					
					try {
						if (groupMembers.endsWith(",")) {
						groupMembers =	groupMembers.substring(0, groupMembers.length()-1);
						}
						if (groupMemberNames.endsWith(",")) {
							groupMemberNames =	groupMemberNames.substring(0, groupMemberNames.length()-1);
							}
//						if (groupIds.endsWith(",")) {
//							groupIds =	groupIds.substring(0, groupIds.length()-1);
//							}
					} catch (Exception e) {
						e.printStackTrace();
					}
					groupModel.setGroupMember(groupMembers);
					groupModel.setGroupMemberNames(groupMemberNames);
					//groupModel.setGroupIds(groupIds);
	                grModel.add(groupModel);
				}
				
				

			}		if (friendSelectedIndex>0) {
			       
				if (selectedPhoneNumbers.endsWith(","))
					selectedPhoneNumbers = selectedPhoneNumbers.substring(0, selectedPhoneNumbers.length() - 1);
				if (selectedNames.endsWith(","))
					selectedNames = selectedNames.substring(0, selectedNames.length() - 1);
			
		} 
			
			if (selectedNames.length()>0 && selectedPhoneNumbers.length()>0) {
				data = new Intent();
				data.putExtra("names", selectedNames);
				data.putExtra("receiver", selectedPhoneNumbers);
				boolean messageType = false;
				
				if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_AUDIO)) {
					mediaMessage = baseurl+"temporary/android.wav";
					messageType = true;
				} else if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_VIDEO)) {
					mediaMessage = baseurl+"temporary/android.3gpp";
					messageType = false;
				}
				else if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_DRAFTS)){
					if (draftsMessage!=null && draftsMessage.length()>0) {
						mediaMessage  = draftsMessage;
						if (mediaMessage.endsWith(".3gpp")) {
							messageType = false;		
						}
						else {
							messageType = true;
						}
						Log.e("", "==== Drafts Message True "+draftsMessage);
						setResult(RESULT_OK);
					}	
			}
				if (mediaMessage.length()>0) {
					data.putExtra("msg_url", mediaMessage);	
				}
				
					data.putExtra("messageType", messageType);
				    data.putStringArrayListExtra("grlist", grList);
				    
				    data.putExtra("grdata", grModel);
		}	
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	    setResult(Utils.GROUP_BACKTOTILE_RESULT,data);

	

	}
	
	
	private void insertGroupData() {


		Intent  data = null;
		 try {
       	  baseurl = CommonFunctions.getBaseURL();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String selectedPhoneNumbers = "";
			String selectedNames="";
			int friendSelectedIndex = 0;
			String mediaMessage="";
			ArrayList<String>  grList = new ArrayList<String>();
			ArrayList<GroupModel> grModel = new ArrayList<GroupModel>();
			for (int j = 0; j < flag.length; j++) {
			
				if (flag[j]) {
					//
					GroupModel groupModel = new GroupModel();
					String groupId = Groupid[j];
					String groupName = GroupName[j];
					grList.add(groupName);
					groupModel.setGroupName(groupName);
					DataBase db = new DataBase(GroupListSelection.this);
					String groupMembers="";
					String groupMemberNames = "";
					try {
						db.open();
						Cursor c = db.fetchAll(DataBase.Group_Member_table,
								DataBase.Group_member_int, null,
								"group_id = " + groupId);
						if (c.getCount() > 0) {
							try {
								if (c.moveToFirst()) {
									do {
										
										int friendId = c.getInt(2);
										Cursor frd_cur = null;
										try {
											frd_cur = db.fetch(
													DataBase.Friends_table,
													DataBase.Friends_int,
													"sr_no = " + friendId);
											frd_cur.moveToFirst();
											// frd_cur
											// .getString(Friend.INT_SR_NO);
											//
											//
											// frd_cur
											// .getString(Friend.INT_INVITE);

											Log.e("",
													"Selected Number "
															+ frd_cur
																	.getString(Friend.INT_NUMBER)
															+ "and Name "
															+ frd_cur
																	.getString(Friend.INT_NAME));
											//selectedPhoneNumbers += "-"+phonenumber[i]+"-,";
											selectedPhoneNumbers += "-"+frd_cur
													.getString(Friend.INT_NUMBER)
													+ "-,";
											selectedNames += frd_cur
													.getString(Friend.INT_NAME)
													+ ",";
											groupMembers +="-"+frd_cur
													.getString(Friend.INT_NUMBER)
													+ "-,";
											groupMemberNames +=""+frd_cur
													.getString(Friend.INT_NAME)
													+ ",";
											friendSelectedIndex++;

										} catch (Exception e) {
											e.printStackTrace();
										} finally {
											frd_cur.close();
										}
									} while (c.moveToNext());
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								c.close();
							}

						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// if (contactGID != null)
						// contactGID.close();

						db.close();
					}
					// for (int i = 0; i < contactGID.getCount(); i++) {

					// }

					//
					
					try {
						if (groupMembers.endsWith(",")) {
						groupMembers =	groupMembers.substring(0, groupMembers.length()-1);
						}
						if (groupMemberNames.endsWith(",")) {
							groupMemberNames =	groupMemberNames.substring(0, groupMemberNames.length()-1);
							}
					} catch (Exception e) {
						e.printStackTrace();
					}
					groupModel.setGroupMember(groupMembers);
					groupModel.setGroupMemberNames(groupMemberNames);
	                grModel.add(groupModel);
	                
	             //   EyesaySendText.sendFailMessageToDrafts(GroupListSelection.this, groupMembers, groupMemberNames, "", media_message_url, false);
	                
	                
				}
				
				

			}		if (friendSelectedIndex>0) {
			       
				if (selectedPhoneNumbers.endsWith(","))
					selectedPhoneNumbers = selectedPhoneNumbers.substring(0, selectedPhoneNumbers.length() - 1);
				if (selectedNames.endsWith(","))
					selectedNames = selectedNames.substring(0, selectedNames.length() - 1);
			
		} 
			
			if (selectedNames.length()>0 && selectedPhoneNumbers.length()>0) {
				data = new Intent();
				data.putExtra("names", selectedNames);
				data.putExtra("receiver", selectedPhoneNumbers);
				boolean messageType = false;
				
				if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_AUDIO)) {
					mediaMessage = baseurl+"temporary/android.wav";
					messageType = true;
				} else if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_VIDEO)) {
					mediaMessage = baseurl+"temporary/android.3gpp";
					messageType = false;
				}
				else if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_DRAFTS)){
					if (draftsMessage!=null && draftsMessage.length()>0) {
						mediaMessage  = draftsMessage;
						if (mediaMessage.endsWith(".3gpp")) {
							messageType = false;		
						}
						else {
							messageType = true;
						}
						Log.e("", "==== Drafts Message True "+draftsMessage);
						
					}	
			}
				
		}	
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	   

	

	

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	          if (keyCode==4) {
				return true;
			}
		return super.onKeyDown(keyCode, event);
	}
	

}
