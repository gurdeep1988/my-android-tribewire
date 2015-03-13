package com.tribewire.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.eyesay.dialog.EyesayonWayDialog;
import com.eyesayapp.Utils.ActivityObj;
import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.Utils.Utils;
import com.eyesayapp.alphabetlist.IndexableListView;
import com.eyesayapp.model.SectionHeader;

import eyesay.adapter.ContactAdapter;
import eyesay.adapter.ContactListAdapter1;
import eyesay.adapter.SimpleAdapter;
import eyesay.obj.Friend;

public class ContactListSelection extends Activity implements TextWatcher{

	private Button sendButton;
	
	public String id[], names[], phonenumber[], invites[];
	public int status[];
	private ProgressDialog pd = null;
	public String where = "status=0 and invite=0";
	public String PhoneNumbers = "";
	public boolean flag[];
	private Button btndone;
	private CheckBox btnAddAll;
	// private ListView lvc;
	// private LinearLayout linearLayout;
	public boolean group = false;
	public boolean invite = false;
	public static boolean fromContactListActivity = false;
	String intentData = "";
	private String contactName;
	private boolean reply;
	private int message_id;
	private String conversation_id;
	private String phonenumberr;
	public String baseurl;
	final Context context = this;
	private String media_message_url = "";
	private String draftsMessage = "";
	public EyesayonWayDialog eyesayonWayDialog;
	private String mediaMessage = "";
	private IndexableListView contac_list;
	private String textMessage = "";
	private String image_url = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_list_selection);

		/*
		 * if (this.getIntent().getExtras() != null) { if
		 * (this.getIntent().getExtras().containsKey("ids")) { intentData =
		 * this.getIntent().getExtras().getString("ids"); } }
		 */

		try {
			media_message_url = getIntent().getExtras().getString(
					Utils.MEDIA_MESSAGE_KEY);
		} catch (Exception e) {

		}

		try {
			draftsMessage = getIntent().getExtras().getString(
					Utils.MESSAGE_DRAFTS);
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			image_url = getIntent().getExtras().getString(
					Utils.TEXT_IMAGE_URL_KEY);
		} catch (Exception e) {
			e.printStackTrace();

		}
		try {
			textMessage = this.getIntent().getExtras()
					.getString(Utils.TEXT_MESSAGE_KEY);
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

				// if
				// (this.getIntent().getExtras().containsKey(Utils.TEXT_MESSAGE_KEY))
				// {
				//
				//
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Debugger.debugE("Parameter received is wrong,....");
			CommonFunctions.writeLog("Error in Contact List Selection");
		}
		
		
		initView();
		setListener();
		initData();
	}

	@Override
	protected void onStart() {

		super.onStart();
	}
	private RelativeLayout rltSearchBar;
	private EditText edtSearchText;
	private Button btnSearch;
	private TextView txtCancelSearch;
	private Button btndone1;
	private void initView() {
		btndone = (Button) findViewById(R.id.contacts_btndone);
		btndone1 = (Button) findViewById(R.id.contacts_btndone1);
		btndone1.setOnClickListener(click);
		sendButton = (Button) findViewById(R.id.select_contacts_btn_send);
		
		// lvc = (ListView) findViewById(R.id.select_contact_list_view);
		// linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
		contac_list = (IndexableListView) findViewById(R.id.contact_list_view);
		contac_list.setFastScrollEnabled(true);
		contac_list.setBackgroundColor(Color.WHITE);
		btnAddAll = (CheckBox) this.findViewById(R.id.contacts_btnAddAll);
		
		edtSearchText = (EditText) findViewById(R.id.edt_search_contact);		
		btnSearch = (Button) findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(searchListener);
		txtCancelSearch = (TextView) findViewById(R.id.txt_search_cancel);
		txtCancelSearch.setOnClickListener(searchListener);			
		edtSearchText.addTextChangedListener(this);
		rltSearchBar = (RelativeLayout) findViewById(R.id.rlt_search_bar);
	}

	OnClickListener searchListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v == btnSearch) {
				showSearchBar();
			} else if (v == txtCancelSearch) {
				hideSearchBar();
				fillContactsList();
			}
		}
	};
	
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
	private void setListener() {
		btnAddAll.setOnClickListener(click);
		btndone.setOnClickListener(click);
		sendButton.setOnClickListener(click);
		
		contac_list.setOnItemClickListener(click_list);
		// lvc.setOnItemClickListener(click_list);
	}

	private void initData() {
		id = null;
		names = null;
		phonenumber = null;
		flag = null;
		CreateProgressBar();
		// h.post(mpost);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {

						Looper.prepare();
						fillContactsList();
						Looper.loop();

					}
				});
				thread.start();

			}
		});

	}

	public void CreateProgressBar() {

		pd = new ProgressDialog(ContactListSelection.this);
		pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pd.setMessage(" Please Wait..");
		pd.setCancelable(false);
		pd.show();

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try {
				// if (viewList!=null) {
				// for (int i = 0; i < viewList.size(); i++) {
				// // linearLayout.addView(viewList.get(i));
				// }
				// }

				if (msg.what == 2) {
					String Message = "";
					Message = ContactListSelection.this.getResources()
							.getString(R.string.no_contact);
					contac_list.setAdapter(new SimpleAdapter(
							ContactListSelection.this, Message));
					contac_list.setOnCreateContextMenuListener(null);
					contac_list.setCacheColorHint(Color.TRANSPARENT);
					contac_list.setOnItemClickListener(null);
					btnAddAll.setFocusable(false);
					btnAddAll.setVisibility(View.GONE);
					btnAddAll.setOnClickListener(null);
				} else {

					contactListAdapter = new ContactListAdapter1(
							ContactListSelection.this, id, names, phonenumber,
							flag,status,sectionHeaders,friendImage);
					contac_list.setAdapter(contactListAdapter);
				}
				// contac_list.setCacheColorHint(Color.TRANSPARENT);
			} catch (Exception e) {
				e.printStackTrace();
				// viewList.clear();
				// viewList=null;
			}

		};
	};
	Handler h = new Handler();
	Runnable mpost = new Runnable() {
		@Override
		public void run() {
			fillContactsList();
			pd.dismiss();
		}
	};
	ContactAdapter adapter;
	ContactListAdapter1 contactListAdapter;
	String friendImage[];
	// View view;
	ArrayList<View> viewList = new ArrayList<View>();
	ArrayList<SectionHeader> sectionHeaders;
	private void fillContactsList() {
		
		DataBase db = new DataBase(ContactListSelection.this);
		// Cursor contact = null;
		// Cursor c = null;
		db.open();

		Cursor contact = db.fetchAll(DataBase.Friends_table,
				DataBase.Friends_int, "name COLLATE NOCASE" , null, null);
		// LayoutInflater inflater = this.getLayoutInflater();

		try {
			if (contact.getCount() > 0) {
				names = new String[contact.getCount()];
				id = new String[contact.getCount()];
				phonenumber = new String[contact.getCount()];
				status = new int[contact.getCount()];
				invites = new String[contact.getCount()];
				flag = new boolean[contact.getCount()];
				friendImage   = new String[contact.getCount()];
				int i = 0;
				SectionHeader sectionHeader = new SectionHeader();
				sectionHeaders  = new ArrayList<SectionHeader>();
				String globaltempChar = "";
				while (contact.moveToNext()) {
					id[i] = contact.getString(Friend.INT_SR_NO);
					names[i] = contact.getString(Friend.INT_NAME);
					phonenumber[i] = contact.getString(Friend.INT_NUMBER);
					invites[i] = contact.getString(Friend.INT_INVITE);
					flag[i] = false;
					status[i] = contact.getInt(Friend.INT_STATUS);
					friendImage[i] = contact.getString(4);
					String localChar = names[i].substring(0, 1);
					if (i == 0) {
						sectionHeader.setIsShow(true);
						String alphabet = names[i].substring(0, 1);
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
                            String alphabet = names[i].substring(0, 1);
                            sectionHeader2.setAlphabet(alphabet.toUpperCase());
                            sectionHeader2.setIsShow(true);
							
                            globaltempChar  = localChar;
                            sectionHeaders.add(sectionHeader2);
						}
					}
					i++;

				}
				handler.sendEmptyMessage(1);

				pd.dismiss();
			} else {

				handler.sendEmptyMessage(2);

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

	
	
	public void searchFriends(CharSequence searchText) {

		
		DataBase db = new DataBase(ContactListSelection.this);
		// Cursor contact = null;
		// Cursor c = null;
		db.open();
		Cursor contact =null;
		if (searchText.length() > 0) {
			// friends = db.fetchContact(DataBase.Friends_table,
			// DataBase.Friends_int, ""
			// + ""+Friend.NAME+"" + " LIKE '" + searchText + "%'", null);

			// friends = db.fetchContact(DataBase.Friends_table,
			// DataBase.Friends_int, searchText.toString());

			contact = db.fetchContactGroup(DataBase.Friends_table,
					DataBase.Friends_int, " name like '" + searchText
							+ "%'", null, null);

		} else {
			contact = db.fetchAll(DataBase.Friends_table,
					DataBase.Friends_int, "name  COLLATE NOCASE", null,
					null);
		}
		
		// LayoutInflater inflater = this.getLayoutInflater();

		try {
			if (contact.getCount() > 0) {
				names = new String[contact.getCount()];
				id = new String[contact.getCount()];
				phonenumber = new String[contact.getCount()];
				status = new int[contact.getCount()];
				invites = new String[contact.getCount()];
				flag = new boolean[contact.getCount()];
				friendImage   = new String[contact.getCount()];
				int i = 0;
				SectionHeader sectionHeader = new SectionHeader();
				sectionHeaders  = new ArrayList<SectionHeader>();
				String globaltempChar = "";
				while (contact.moveToNext()) {
					id[i] = contact.getString(Friend.INT_SR_NO);
					names[i] = contact.getString(Friend.INT_NAME);
					phonenumber[i] = contact.getString(Friend.INT_NUMBER);
					invites[i] = contact.getString(Friend.INT_INVITE);
					flag[i] = false;
					status[i] = contact.getInt(Friend.INT_STATUS);
					friendImage[i] = contact.getString(4);
					String localChar = names[i].substring(0, 1);
					if (i == 0) {
						sectionHeader.setIsShow(true);
						String alphabet = names[i].substring(0, 1);
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
                            String alphabet = names[i].substring(0, 1);
                            sectionHeader2.setAlphabet(alphabet.toUpperCase());
                            sectionHeader2.setIsShow(true);
							
                            globaltempChar  = localChar;
                            sectionHeaders.add(sectionHeader2);
						}
					}
					i++;

				}
				handler.sendEmptyMessage(1);

				pd.dismiss();
			} else {

				handler.sendEmptyMessage(2);

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
	public OnClickListener click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.contacts_btnAddAll) {
			} else if (v.getId() == R.id.contacts_btndone) {
				backToTileData();
				finish();
			} 
			else if (v.getId() == R.id.contacts_btndone1) {
				backToTileData();
				finish();
			} 
			else if (v.getId() == R.id.select_contacts_btn_send) {
				boolean isSelected = false;
				if (flag != null && flag.length > 0) {
					for (int i = 0; i < flag.length; i++) {
						if (flag[i]) {
							isSelected = true;
							break;
						}
					}
				}
				if (isSelected) {
					new Handler().post(new Runnable() {

						@Override
						public void run() {
							createMyProgressBar();

						}
					});

					Thread thread = new Thread(sendRecordedMessage);
					thread.start();
				} else {
					CommonFunctions.showAlert(ContactListSelection.this,
							"Please select some contact in order to send",
							"Select Recipient");
				}
			} 
		}
	};

	private void createMyProgressBar() {
		eyesayonWayDialog = new EyesayonWayDialog(ContactListSelection.this);
		eyesayonWayDialog.setCancelable(true);
		eyesayonWayDialog.show();

	}

	// Running a thread for message sending...

	final Runnable sendRecordedMessage = new Runnable() {

		public void run() {
			try {

				Looper.prepare();
				try {
					baseurl = CommonFunctions.getBaseURL();
				} catch (Exception e) {
					e.printStackTrace();
				}
				boolean messageType = false;

				if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_AUDIO)) {
					mediaMessage = baseurl + "temporary/android.wav";
					messageType = true;
				} else if (media_message_url
						.equalsIgnoreCase(Utils.MESSAGE_VIDEO)) {
					mediaMessage = baseurl + "temporary/android.3gpp";
					messageType = false;
				} else if (media_message_url
						.equalsIgnoreCase(Utils.MESSAGE_IMAGE)) {
					mediaMessage = image_url;
					messageType = false;
				}
				// This condition in that case when user wants to send message
				// to friends that is saved in drafts
				else if (media_message_url
						.equalsIgnoreCase(Utils.MESSAGE_DRAFTS)) {
					if (draftsMessage != null && draftsMessage.length() > 0) {
						mediaMessage = draftsMessage;
						Log.e("", "==== Drafts Message True " + draftsMessage);
						setResult(RESULT_OK);
					} else {
						CommonFunctions
								.showAlert(
										ContactListSelection.this,
										"Unable to send draft message, Invalid file format",
										"Error");
						return;
					}
				}
				try {

					Debugger.debugE("in sending...");

					// Intent i = new Intent(ContactListSelection.this,
					// MessageReceiver.class);

					// startService(getIntent());
					String selectedPhoneNumbers = "";
					String selectedNames = "";
					int friendSelectedIndex = 0;
					for (int i = 0; i < flag.length; i++) {

						if (flag[i]) {

							if (status[i]==5) {
								Log.e("", "Selected Number which is group" + phonenumber[i]
										+ "and Name " + names[i]);
								selectedPhoneNumbers += "-g_" + new String(Base64.decode(phonenumber[i].getBytes(), Base64.DEFAULT))+ "-,";
								selectedNames += names[i] + ",";
								friendSelectedIndex++;
							}
							else{
								Log.e("", "Selected Number " + phonenumber[i]
										+ "and Name " + names[i]);
								selectedPhoneNumbers += "-" + phonenumber[i] + "-,";
								selectedNames += names[i] + ",";
								friendSelectedIndex++;
							}
							
						}

					}
					if (friendSelectedIndex > 0) {

						if (selectedPhoneNumbers.endsWith(","))
							selectedPhoneNumbers = selectedPhoneNumbers
									.substring(0,
											selectedPhoneNumbers.length() - 1);
						if (selectedNames.endsWith(","))
							selectedNames = selectedNames.substring(0,
									selectedNames.length() - 1);

						Log.e("", "The Commma Separated Numbers are "
								+ selectedPhoneNumbers + "\n Names "
								+ selectedNames);
						try {
							if (eyesayonWayDialog != null) {
								eyesayonWayDialog.getHandlerObj()
										.sendEmptyMessage(9);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						// ------------------------- Start of Eyesay Send Tex
						// -------------------------------//
						try {
							if (mediaMessage.length() <= 2
									|| mediaMessage.endsWith(".jpg")
									|| mediaMessage.endsWith(".gif")
									|| mediaMessage.endsWith(".jpeg")
									|| mediaMessage.endsWith(".mp4")
									|| mediaMessage.toLowerCase().endsWith(
											".3gpp")
									|| mediaMessage.toLowerCase().endsWith(
											".mov")
									|| mediaMessage.endsWith(".wav")) {

								boolean isBroadCast  = false;
								if (CommonFunctions.SendTextMessage(
										ContactListSelection.this, messageType,
										mediaMessage, selectedPhoneNumbers,
										selectedNames, reply, conversation_id,
										textMessage,isBroadCast)) {

									try {
										if (eyesayonWayDialog != null) {
											eyesayonWayDialog.getHandlerObj()
													.sendEmptyMessage(11);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									Debugger.debugE("in mesage id ... + "
											+ message_id);
									if (message_id != 0) {

										DataBase db = new DataBase(
												ContactListSelection.this);
										try {
											ContentValues cv = new ContentValues();
											cv.put("reply", 1);
											db.open();
											db.update(DataBase.Receive_table,
													DataBase.Receive_int,
													"sr_no=" + message_id, cv);
											db.close();
										} catch (Exception e) {

										}
									}

								} else {

									// Unable to Send message to To friends due
									// to network problem or server problem
									// so put in database
									DataBase db = null;
									String dest = "null";
									String sender = CommonFunctions.getPref(
											ContactListSelection.this,
											CommonFunctions.PHONE_NUMBER, "");
									try {
										if (flag.length > 0) {
											db = new DataBase(
													ContactListSelection.this);
											db.open();
											if (checkFileToSend(media_message_url)) {

												try {
													if (mediaMessage != null
															&& mediaMessage
																	.length() > 0
															&& mediaMessage
																	.endsWith(".jpeg")
															|| mediaMessage
																	.endsWith(".mp4")) {
														CommonFunctions
																.getBaseURL();
														if (mediaMessage
																.endsWith(".mp4")
																|| mediaMessage
																		.toLowerCase()
																		.endsWith(
																				".3gpp")
																|| mediaMessage
																		.toLowerCase()
																		.endsWith(
																				".mov")) {
															dest = CommonFunctions
																	.getImageRL()
																	+ System.currentTimeMillis()
																	+ ".mp4";
														} else {
															dest = CommonFunctions
																	.getImageRL()
																	+ System.currentTimeMillis()
																	+ ".jpeg";
														}
														try {
															Log.e("",
																	"================== "
																			+ CommonFunctions
																					.getImageRL());
															CommonFunctions
																	.copy(new File(
																			media_message_url),
																			new File(
																					dest));
														} catch (Exception e) {
															e.printStackTrace();
														}
													}

													Log.e("",
															"================== dest==="
																	+ dest);
												} catch (Exception e1) {

													e1.printStackTrace();
												}

												for (int i = 0; i < flag.length; i++) {

													if (flag[i]) {

														Log.e("",
																"Saved Draft Number "
																		+ phonenumber[i]
																		+ "Saved Draft Name "
																		+ names[i]);

														try {
															saveMessageToDrafts(
																	db,
																	sender,
																	names[i],
																	dest,
																	phonenumber[i]);
														} catch (Exception e) {
															e.printStackTrace();
														}
													}

												}
												handler2.sendEmptyMessage(5);

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

							else {

								if (CommonFunctions.SendMessage(
										ContactListSelection.this, messageType,
										mediaMessage, selectedPhoneNumbers,
										selectedNames, reply, conversation_id)) {

									try {
										if (eyesayonWayDialog != null) {
											eyesayonWayDialog.getHandlerObj()
													.sendEmptyMessage(11);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									Debugger.debugE("in mesage id ... + "
											+ message_id);
									if (message_id != 0) {

										DataBase db = new DataBase(
												ContactListSelection.this);
										try {
											ContentValues cv = new ContentValues();
											cv.put("reply", 1);
											db.open();
											db.update(DataBase.Receive_table,
													DataBase.Receive_int,
													"sr_no=" + message_id, cv);
											db.close();
										} catch (Exception e) {

										}
									}

								} else {
									// Unable to Send message to To friends due
									// to network problem or server problem
									// so put in database
									DataBase db = null;
									String dest = "";
									String sender = CommonFunctions.getPref(
											ContactListSelection.this,
											CommonFunctions.PHONE_NUMBER, "");
									try {
										if (flag.length > 0) {
											db = new DataBase(
													ContactListSelection.this);
											db.open();
											if (checkFileToSend(mediaMessage)) {

												try {
													if (mediaMessage
															.endsWith(".wav")) {
														dest = CommonFunctions
																.getAudioURL()
																+ System.currentTimeMillis()
																+ ".wav";
													} else {
														dest = CommonFunctions
																.getVideoURL()
																+ System.currentTimeMillis()
																+ ".3gpp";
													}

													Log.e("",
															"================== dest==="
																	+ dest);
												} catch (IOException e1) {

													e1.printStackTrace();
												}
												try {
													Log.e("",
															"================== "
																	+ CommonFunctions
																			.getAudioURL());

													CommonFunctions
															.copy(new File(
																	mediaMessage),
																	new File(
																			dest));
												} catch (Exception e) {
													e.printStackTrace();
												}
												for (int i = 0; i < flag.length; i++) {

													if (flag[i]) {

														Log.e("",
																"Saved Draft Number "
																		+ phonenumber[i]
																		+ "Saved Draft Name "
																		+ names[i]);

														try {
															saveMessageToDrafts(
																	db,
																	sender,
																	names[i],
																	dest,
																	phonenumber[i]);
														} catch (Exception e) {
															e.printStackTrace();
														}
													}

												}
												handler2.sendEmptyMessage(5);

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

						} catch (Exception e) {
							e.printStackTrace();
						}

						// ---------------------------------- End of Send Text
						// Message-----------------------------------------//
					} else {
						if (eyesayonWayDialog != null) {
							eyesayonWayDialog.dismiss();
						}
						CommonFunctions
								.showAlert(
										ContactListSelection.this,
										"You have not selected any contact, Please selet some contact",
										"Error");
						return;
						// CommonFunctions.showAlert(ContactListSelection.this,
						// "Please select single or multiple contact in order to send message",
						// "Send Message");

					}
				}

				catch (Exception e) {
					CommonFunctions
							.writeLog("error in Contact List Selection when Sending message 1"
									+ e.getMessage());
				} finally {

					// Commented By GURDEEP SINGH 30th October 2012 at 16:29 PM
					// Intent intent = new Intent(ContactListSelection.this,
					// TenFourTab.class);
					// setResult(RESULT_FIRST_USER);
					if (eyesayonWayDialog != null)
						eyesayonWayDialog.dismiss();
					// startActivity(intent);
					if (media_message_url
							.equalsIgnoreCase(Utils.MESSAGE_DRAFTS)) {
						if (draftsMessage != null && draftsMessage.length() > 0) {

							Log.e("", "==== Drafts Message True "
									+ draftsMessage);
							try {
								List<Activity> activities = ActivityObj
										.getActivityInstance();
								int size = activities.size();
								if (activities != null && size > 0) {
									for (int i = 0; i < size; i++) {
										try {
											activities.get(i).finish();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									activities.clear();
								}
							} catch (Exception e) {

							}
						}
						// Intent intent = new Intent(ContactListSelection.this,
						// EyeSayDefaultAudioVideo.class);
						// startActivity(intent);
					}

					setResult(777);
					finish();

				}
				Looper.loop();

			} catch (Exception e) {
				e.printStackTrace();
				CommonFunctions
						.writeLog("error in Contact List Selection when Sending message "
								+ e.getMessage());
			}

		}
	};

	public OnItemClickListener click_list = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

//			int pos = (int) arg3;
//			contactListAdapter.setSelectedPosition(arg2);
//			int First = contac_list.getFirstVisiblePosition();
//			int current = pos;
//			if (First > 0) {
//				current = pos - First;
//				Debugger.debugE("Current Postition" + current);
//
//			}
//			RelativeLayout rl = (RelativeLayout) contac_list
//					.getChildAt(current);
//			Log.e("", "Current Position=" + current + " Relative " + rl);
//			
//			CheckBox iv = (CheckBox) rl.getChildAt(1);
			if (flag[arg2]) {
				//arg1.setBackgroundColor(Color.WHITE);
			//	iv.setChecked(false);

				flag[arg2] = false;
				try {
					contactListAdapter.notifyChange(flag);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

				arg1.setBackgroundColor(getResources().getColor(R.color.white
						));
				//iv.setChecked(true);
				flag[arg2] = true;
				try {
					contactListAdapter.notifyChange(flag);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			
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
		
			ActivityStatus.setActivity(null);
		
		super.onPause();
	}

	private void saveMessageToDrafts(DataBase db, String sender,
			String receiver, String dest, String phoneNumber) {

		try {

			ContentValues cv = new ContentValues();
			cv.put("message_id", "");
			cv.put("sender", sender);
			cv.put("receiver", receiver);
			if (dest.endsWith(".3gpp")) {
				cv.put("message_type", 1);
			} else if (dest.endsWith(".3gpp")) {
				cv.put("message_type", 0);
			} else if (dest.length() < 2 || dest.endsWith(".jpeg")) {
				cv.put("message_type", 2);
				cv.put("textmessage", textMessage);
			}
			try {
				cv.put("file_url", dest);
			} catch (Exception e) {
				e.printStackTrace();
			}
			cv.put("date_time", "" + System.currentTimeMillis());
			cv.put("type", 1);
			cv.put("phonenumber", phoneNumber);
			db.insert_content(DataBase.drafts_table, DataBase.drafts_int, cv);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// finally{
		// if (db!=null) {
		// db.close();
		// }
		// }

	}

	private boolean checkFileToSend(String source) {
		boolean isFile = false;
		try {
			File file = new File(source);

			Log.e("", "================File Exists " + file.exists()
					+ "========= File Length " + file.length());
			if (file.exists() && file.length() > 0) {

				isFile = true;

			} else {

				isFile = false;

			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return isFile;

	}

	Handler handler2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Toast.makeText(
					getApplicationContext(),
					"Looks like there was a problem sending your EyeSay. Not to worry. It's saved in your drafts so you can send it later!",
					9000).show();
		};

	};

	private void backToTileData() {
		Intent data = null;
		try {
			baseurl = CommonFunctions.getBaseURL();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String selectedPhoneNumbers = "";
			String selectedNames = "";
			int friendSelectedIndex = 0;
			for (int i = 0; i < flag.length; i++) {

				if (flag[i]) {

					if (status[i] ==5) {
						Log.e("", "Selected Number " + phonenumber[i] + "and Name "
								+ names[i]);
						selectedPhoneNumbers += "-g_" +new String(Base64.decode(phonenumber[i].getBytes(), Base64.DEFAULT)) + "-,";
						selectedNames += names[i] + ",";
						friendSelectedIndex++;
					}
					else {
					Log.e("", "Selected Number " + phonenumber[i] + "and Name "
							+ names[i]);
					selectedPhoneNumbers += "-" + phonenumber[i] + "-,";
					selectedNames += names[i] + ",";
					friendSelectedIndex++;
					}
				}

			}
			if (friendSelectedIndex > 0) {

				if (selectedPhoneNumbers.endsWith(","))
					selectedPhoneNumbers = selectedPhoneNumbers.substring(0,
							selectedPhoneNumbers.length() - 1);
				if (selectedNames.endsWith(","))
					selectedNames = selectedNames.substring(0,
							selectedNames.length() - 1);

			}

			if (selectedNames.length() > 0 && selectedPhoneNumbers.length() > 0) {
				data = new Intent();
				data.putExtra("names", selectedNames);
				data.putExtra("receiver", selectedPhoneNumbers);
				boolean messageType = false;

				if (media_message_url.equalsIgnoreCase(Utils.MESSAGE_AUDIO)) {
					mediaMessage = baseurl + "temporary/android.wav";
					messageType = true;
				} else if (media_message_url
						.equalsIgnoreCase(Utils.MESSAGE_VIDEO)) {
					mediaMessage = baseurl + "temporary/android.3gpp";
					messageType = false;
				} else if (media_message_url
						.equalsIgnoreCase(Utils.MESSAGE_DRAFTS)) {
					if (draftsMessage != null && draftsMessage.length() > 0) {
						mediaMessage = draftsMessage;
						if (mediaMessage.endsWith(".3gpp")) {
							messageType = false;
						} else {
							messageType = true;
						}
						Log.e("", "==== Drafts Message True " + draftsMessage);
						setResult(RESULT_OK);
					}
				}
				if (mediaMessage.length() > 0) {
					data.putExtra("msg_url", mediaMessage);
				}

				data.putExtra("messageType", messageType);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		setResult(Utils.SINGLE_BACKTOTILE_RESULT, data);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 4) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onDestroy();
	}

	public static Bitmap decodeSampledBitmapFromResource(InputStream is,
			int reqWidth, int reqHeight) {
		try {
			is.reset();
		} catch (Exception e) {
			// TODO: handle exception
		}

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeStream(is, null, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		try {
			is.reset();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return BitmapFactory.decodeStream(is, null, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
		searchFriends(s);
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

}
