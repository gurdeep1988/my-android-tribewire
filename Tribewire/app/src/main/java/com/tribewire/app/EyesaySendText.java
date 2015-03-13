package com.tribewire.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.eyesay.custommenu.CustomMenu;
import com.app.eyesay.custommenu.CustomMenu.OnMenuItemSelectedListener;
import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.ExtAudioRecorder;
import com.eyesayapp.Utils.GroupModel;
import com.eyesayapp.Utils.ImageDownloader1;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.Utils.Utils;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.RestApiCall;
import com.eyesayapp.model.CompanyMessages;
import com.eyesayapp.model.ContactList;

import eyesay.obj.Friend;

public class EyesaySendText extends Activity implements OnClickListener,
		TextWatcher {

	public static final String IMAGE_EXTENSION = ".jpeg";
	private Button msg_txt_back;
	private Button msg_txt_send;
	private boolean draft_dialog = false;
	private String drafts_message_url = "";
	private String reply_number, reply_name, reply_conversation_id;
	private boolean reply_status;
	private int reply_message_id;
	private boolean send_single_msg = false;
	private boolean isGroup = false;

	private Dialog dialog;
	private Button btn_group_cont, btn_single_cont;
	private boolean single_cont_sel = true;
	private boolean group_cont_sel = true;
	private boolean facebook_sel = true;
	private boolean twitter_sel = true;
	private boolean youtube_sel = true;
	private boolean tumbler_sel = true;
	private Intent backToTilescontactData;
	private Intent backToTilesGroupData;
	final Context context = this;
	private Timer timer = null;
	private int timerCount = 0;
	private String baseurl;
	//public EyesayonWayDialog eyesayonWayDialog;
	private static EditText msg_edittext;

	// private ImageView msg_pic;
	Bitmap profile_bm;
	public static final int ACTIVITY_SELECT_IMAGE = 211;
	public static final int ACTIVITY_CAPTURE_IMAGE = 212;
	public static final int ACTIVITY_SELECT_VIDEO = 311;
	public static final int ACTIVITY_CAPTURE_VIDEO = 312;
	public static final int ACTIVITY_CAPTURE_AUDIO = 313;
	String uploadFilePath = "";

	private RelativeLayout eyesay_txt_camera;
	InputMethodManager inputManager = null;

	private String myVideoFilePath = "";
	private ImageButton btn_openMedia;
	private MultiAutoCompleteTextView txt_contact_Names;
	private Button addToContact;
	public static int EYESAY_ADD_MEDIA_CODE = 989;
	private List<String> phoneNumberList;
	private List<ContactList> nameList;
	private String autoCompleteNumbers;
	private String autoCompleteNames = "";
	private boolean isAutoCompleteFocus = false;
	private boolean isToContactSelected = true;
	private ImageView imgCapture;
	private RelativeLayout rltAudio;
	private ProgressBar progressBar;
	private TextView txtAudioCount;
	private ImageView btnPlayPause;
	private TextView txt_Title;
    private String THREAD   = "";
    private boolean isSending  = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_send_text_message);
		msg_txt_back = (Button) findViewById(R.id.text_cancel);
		msg_txt_send = (Button) findViewById(R.id.text_send);
		eyesay_txt_camera = (RelativeLayout) findViewById(R.id.eyesay_txt_camera);
		imgCapture = (ImageView) findViewById(R.id.img_capture);
		rltAudio = (RelativeLayout) findViewById(R.id.rlt_audio);
		progressBar = (ProgressBar) findViewById(R.id.progressBarAudio);
		txtAudioCount = (TextView) findViewById(R.id.audio_seconds);
		btnPlayPause = (ImageView) findViewById(R.id.img_audio_message);
		txt_Title   = (TextView)findViewById(R.id.txt_title);
		btnPlayPause.setOnClickListener(this);
		eyesay_txt_camera.setOnClickListener(this);
		Button btnBack = (Button) findViewById(R.id.text_back);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		btn_openMedia = (ImageButton) findViewById(R.id.open_media);
		btn_openMedia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(EyesaySendText.this,
				// EyesayAddMedia.class);
				// startActivityForResult(intent, EYESAY_ADD_MEDIA_CODE);
				showMenu();
			}
		});
		msg_txt_send.setOnClickListener(this);
		msg_txt_back.setOnClickListener(this);
		msg_edittext = (EditText) findViewById(R.id.msg_editText1);
		msg_edittext.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		msg_edittext.addTextChangedListener(this);
		initializeRecordingUi();
		inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// ----------------------Starting of handling Reply Message from Message
		// Conversation Screen-----------------------//
		ContactList contactListS[] = null;
		DataBase db = null;
		Cursor c = null;
		
		try {
			db = new DataBase(this);
			db.open();
			c = db.fetchAll(DataBase.Friends_table, DataBase.Friends_int);
			Log.e("", "============== The Cursor Count is " + c.getCount());
			contactListS    = new ContactList[c.getCount()];
			if (c.moveToFirst()) {
				phoneNumberList = new ArrayList<String>();
				nameList = new ArrayList<ContactList>();
				do {
					ContactList contactList = new ContactList();
					String friendName = c.getString(2);
					contactList.setName(friendName);
					String friendNumber = c.getString(3);
					String imageUrl   = c.getString(4);
					contactList.setImageUrl(imageUrl);
					 int status = c.getInt(6);
					 contactList.setStatus(status);
					contactList.setPhoneNumber(friendNumber);
					contactListS[c.getPosition()] = contactList;
					Log.e("", "====== Friend Name " + friendName
							+ " Friend Number " + friendNumber);
					phoneNumberList.add(friendNumber);
					nameList.add(contactList);

				} while (c.moveToNext());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
			db.close();
		}
		txt_contact_Names = (MultiAutoCompleteTextView) findViewById(R.id.contactNames);
        
		
		
		try {
			try {

			CompanyMessages	companyMessages = (CompanyMessages) getIntent()
						.getSerializableExtra("receiver");
			
			getFriendName(companyMessages.getSenderId());
			isToContactSelected = false;
			txt_Title.setText("REPLY TO SENDER");
			} catch (Exception e) {
				e.printStackTrace();
				isToContactSelected = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			 String receiver   = getIntent().getStringExtra("receiver");
			 txt_contact_Names.setText(receiver);
			 isToContactSelected = false;
			 THREAD   = getIntent().getStringExtra("conid");
			 updateReadValue();
		} catch (Exception e) {
			e.printStackTrace();
			isToContactSelected = true;
			
		}  
		
		
		final ContactList contactList[] = contactListS;
		final ArrayAdapter<ContactList> multiAdapter = new ArrayAdapter<ContactList>(
				this, android.R.layout.simple_dropdown_item_1line, nameList)
				
		 {
		                 
		
			
		
			   
//			   @Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				
//				return super.getView(position, convertView, parent);
//			}
			
			      
			 @Override
			public int getPosition(ContactList item) {
				// TODO Auto-generated method stub
				return super.getPosition(item);
			}
			 
			 @Override
			public int getCount() {
				// TODO Auto-generated method stub
				return super.getCount();
			}
			 
			 @Override
			public ContactList getItem(int position) {
				// TODO Auto-generated method stub
				return super.getItem(position);
			}
			 
			 @Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return super.getItemId(position);
			}
			 
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {


				ViewHolder holder;
				// TODO COMMENTED on 15 Dec 2012 Temporary Need to change
				if (convertView == null) {
					convertView = LayoutInflater.from(EyesaySendText.this).inflate(R.layout.friendlist, null);
					// convertView.setBackgroundColor(Color.WHITE);
					holder = new ViewHolder();
					holder.icon = (ImageView) convertView.findViewById(R.id.imgstatus);
					holder.name = (TextView) convertView.findViewById(R.id.text);
                  

					// CommonFunctions.setFont(c, holder.name, true, false);

					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				
		ContactList contactList=		 getItem(position);
		 try {
			 holder.name.setText(contactList.getName());
		} catch (Exception e) {
			e.printStackTrace();
		} 
				if (contactList.getStatus() == 3) {
					holder.name.setTextColor(Color.BLACK);
					holder.icon.setBackgroundResource(R.drawable.icons_status_online);
					convertView.setBackgroundColor(EyesaySendText.this.getResources().getColor(R.color.white));
				}
				if (contactList.getStatus() == 4) {
					holder.name.setTextColor(Color.BLACK);
					holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
					convertView.setBackgroundColor(EyesaySendText.this.getResources().getColor(R.color.white));
				}
				if (contactList.getStatus() == 0) {
					holder.name.setTextColor(Color.BLACK);
					//holder.icon.setBackgroundResource(R.drawable.unavailable);
					holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
					convertView.setBackgroundColor(EyesaySendText.this.getResources().getColor(R.color.white));
				} else if (contactList.getStatus() == 1) {
					holder.name.setTextColor(Color.BLACK);
					holder.icon.setBackgroundResource(R.drawable.icons_status_online);
					convertView.setBackgroundColor(EyesaySendText.this.getResources().getColor(R.color.white));
				} else if (contactList.getStatus() == 2) {
					holder.name.setTextColor(Color.BLACK);
					//holder.icon.setBackgroundResource(R.drawable.unavailable);
					holder.icon.setBackgroundResource(R.drawable.icons_status_offline);
					convertView.setBackgroundColor(EyesaySendText.this.getResources().getColor(R.color.white));

				}
				else if (contactList.getStatus() == 5) {
					holder.name.setTextColor(EyesaySendText.this.getResources().getColor(R.color.green_user_group));
					holder.icon.setBackgroundResource(R.drawable.group_arrow_next_24x24);
					convertView.setBackgroundColor(EyesaySendText.this.getResources().getColor(R.color.white));
				}
				else if ( contactList.getStatus() == 6) {
					holder.name.setTextColor(EyesaySendText.this.getResources().getColor(R.color.tribewire_grey));
					holder.icon.setBackgroundResource(R.drawable.group_arrow_next_24x24);
		            convertView.setBackgroundColor(EyesaySendText.this.getResources().getColor(R.color.white));
				}		

				
				


				try {

					holder.contactPic = (ImageView) convertView
							.findViewById(R.id.contact_pic);


					
					// Leave this because no image is coming from Server right now 
					// will be uncommented in future   24-08-2013  15:43
					
					
					if (contactList.getImageUrl()!=null) {
						if (contactList.getImageUrl()!=null && contactList.getImageUrl().length()>0) {
//							ImageDownloader1.getInstance().download(
//									imageUrl[position].trim(),holder.contactPic);	
							checkImage(contactList.getImageUrl(), holder.contactPic);
						}
						else {
							
							 if (contactList.getStatus() == 5) {
						
								 holder.contactPic.setImageDrawable(EyesaySendText.this.getResources().getDrawable(R.drawable.group));

							}
							 else if (contactList.getStatus() == 6) {
									
								 holder.contactPic.setImageDrawable(EyesaySendText.this.getResources().getDrawable(R.drawable.tab_group_n));

							}
							 else {
//										holder.name.setTextColor(Color.BLACK);
//									holder.icon.setBackgroundResource(R.drawable.messagebluearrow);

								 holder.contactPic.setImageDrawable(EyesaySendText.this.getResources().getDrawable(R.drawable.no_image));
							 }

						}
					}
					else {
//						holder.contactPic.setImageDrawable(c.getResources().getDrawable(R.drawable.no_image));
						 if (contactList.getStatus() == 5) {
						
							 holder.contactPic.setImageDrawable(EyesaySendText.this.getResources().getDrawable(R.drawable.group));

						}
						 else if (contactList.getStatus() == 6) {
								
							 holder.contactPic.setImageDrawable(EyesaySendText.this.getResources().getDrawable(R.drawable.tab_group_n));

						}
						 else {
							 holder.contactPic.setImageDrawable(EyesaySendText.this.getResources().getDrawable(R.drawable.no_image));

						 }
					}
					
//					holder.phone_number.setText(""+phoneNumner[position]);
//					Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
//							Uri.encode(phoneNumner[position]));
//					LocalImageDownloader.getInstance().download(uri.toString(),
//							holder.contactPic, c);
				
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				// holder.icon.setImageBitmap(BitmapFactory.decodeResource(c.getResources(),
				// ICON[position]));

				return convertView;

			
				
			
			}
			
		};
		
		
		//final MultiAutoAdapter multiAdapter  = new MultiAutoAdapter(this, android.R.id.text1, contactListS);
		
		txt_contact_Names.setAdapter(multiAdapter);
		MultiAutoCompleteTextView.CommaTokenizer commaTokenizer = new MultiAutoCompleteTextView.CommaTokenizer();
		txt_contact_Names.setTokenizer(commaTokenizer);
		txt_contact_Names.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.e("", "============= Text Changesd Called");

				if (s != null && s.length() > 3) {
					isToContactSelected = false;
				} else {
					isToContactSelected = true;
				}
				multiAdapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				

			}
		});
		txt_contact_Names.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					isAutoCompleteFocus = true;
					// Toast.makeText(EyesaySendText.this, "got the focus",
					// Toast.LENGTH_LONG).show();
				} else {
					isAutoCompleteFocus = false;
					// Toast.makeText(EyesaySendText.this, "lost the focus",
					// Toast.LENGTH_LONG).show();
				}
			}
		});
		txt_contact_Names.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adpaterView, View arg1,
					int position, long arg3) {
				// Toast.makeText(EyesaySendText.this,
				// "  Item Position "+((ContactList)arg0.getItemAtPosition(arg2)).getPhoneNumber(),
				// Toast.LENGTH_SHORT).show();
				if (adpaterView != null) {
					ContactList contactList = (ContactList) adpaterView
							.getItemAtPosition(position);
					if (contactList != null) {
						autoCompleteNumbers += "-"
								+ contactList.getPhoneNumber() + "-,";
						autoCompleteNames += contactList.getName() + ",";

						// Toast.makeText(EyesaySendText.this,
						// "  Item Position "+autoCompleteNames,
						// Toast.LENGTH_SHORT).show();
						isToContactSelected = false;
					}
				}

			}

		});
		
		try {
//			 txt_contact_Names.setOnKeyListener(new OnKeyListener() {
//
//			        public boolean onKey(View v, int keyCode, KeyEvent event) {
//			            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
//			                    || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
//			                    || keyCode == KeyEvent.KEYCODE_DPAD_UP
//			                    || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//
//			                String t = ((TextView) v).getText().toString();
//			                String f = txt_contact_Names.getText().toString();
//
//			                int s = txt_contact_Names.getSelectionStart();
//			                int i = s;
//
//			                while (i > 0 && f.charAt(i - 1) != ' ') {
//			                    i--;
//			                }
//
//			                multiAdapter.getFilter().filter(t.substring(i, s));
//
//			                return false;
//			            }
//
//			            return false;
//			        }
//			    });
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			reply_number = getIntent().getStringExtra(Utils.REPLY_NUMBER);
			reply_name = getIntent().getStringExtra(Utils.REPLY_FRIEND_NAME);

			if (reply_name != null && reply_name.length() > 0) {
				txt_contact_Names.setText(reply_name);
			}

			reply_message_id = getIntent().getIntExtra(Utils.REPLY_MESSAGE_ID,
					0);
			reply_status = getIntent()
					.getBooleanExtra(Utils.REPLY_STATUS, true);
			reply_conversation_id = getIntent().getStringExtra(
					Utils.REPLY_CONVERSATION_ID);

			try {

				send_single_msg = getIntent().getBooleanExtra(
						Utils.SEND_SINGLE_MESSAGE, false);

				isGroup = getIntent().getBooleanExtra(Utils.REPLY_STATUS_GROUP,
						false);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		try {
			addToContact = (Button) findViewById(R.id.add_contact);
			addToContact.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					/* showSendVideoDialog(); */

					Intent test_intent = new Intent(EyesaySendText.this,
							ContactListSelection.class);
					test_intent.putExtra(Utils.MEDIA_MESSAGE_KEY,
							Utils.MESSAGE_IMAGE);
					test_intent.putExtra(Utils.TEXT_MESSAGE_KEY, msg_edittext
							.getText().toString());

					test_intent.putExtra(Utils.TEXT_IMAGE_URL_KEY,
							uploadFilePath);
					startActivityForResult(test_intent, REQUEST_CANCEL);

					single_cont_sel = false;

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onCreate(savedInstanceState);

	}
	
	public IHttpRequest readHttpRequest  = new  IHttpRequest() {
		
		@Override
		public void onError(String onError) {

		}

		@Override
		public void onResponse(String response) {
			Log.e("", "============= The read response is " + response);
			DataBase db = null;
			try {
				db = new DataBase(EyesaySendText.this);
				db.open();
				String where = "conversation_Id = '" + THREAD+"'";
				Log.e("", "======== Where " + where);
				ContentValues cv = new ContentValues();
				cv.put(DataBase.isRead, "1");
				boolean isUpdate = db.update(DataBase.emergency_table,
						DataBase.emergency_int, where, cv);
				Log.e("", "=========== Status update " + isUpdate);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (db != null) {
					db.close();
				}
			}

		}
	};
	
	
	static class ViewHolder {

		TextView name;

		ImageView icon;
		ImageView contactPic;

	}
	private boolean checkImage(String url , ImageView imageView) {
		boolean isExists  = false;
		try {
			final String fileName = url.substring(url.lastIndexOf("/") + 1);
			
			String anotherurl = url.substring(0, url.lastIndexOf("/"));
			System.out.println("Another Url " + anotherurl);
			String anothernewUrl = anotherurl.substring(anotherurl.lastIndexOf("/") + 1);
			System.out.println("Another new Url "+anothernewUrl);
			
			String requiredFileUrl  = anothernewUrl+fileName;
			System.out.println("My required Url "+requiredFileUrl);

			final File f = new File(CommonFunctions.getImageRL() + requiredFileUrl);
			Log.e("", "=============The file Path is " + f.toString());
			if (f.exists()) {
				isExists  = true;
				
				Log.e("", "=========== File exists " + f.exists());
				imageView.setImageURI(Uri.parse(f.toString()));
			}
			else {
				isExists    = false;
				ImageDownloader1.getInstance().download(
						url.trim(),imageView,EyesaySendText.this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
           return isExists ;
	}
	
	private void updateReadValue() {
		try {
			List<String> nameList = new ArrayList<String>();
			nameList.add("api_key");
			nameList.add("conv_id");
			List<String> valueList = new ArrayList<String>();
			valueList.add(Constants.API_KEY);
			valueList.add(THREAD);
			new RestApiCall(
					EyesaySendText.this,
					CommonFunctions.DEMO_SERVER_URL + CommonFunctions.READ_CONV,
					nameList, valueList).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private LinearLayout conRecordingLayout;
	// private TextView txtSecondsLeft;
	private TextView txtSecondsPlayed;
	private ProgressBar progressRecording;
	private Button txtPlay;
	private Button txtRecord;
	private Button txtCancelRecording;
	String recordingPath = "";
	boolean isOngoingRecording = false;
	public int count = 0;
	public int seconds = 60;
	public boolean isPlayingCurrently = false;
	private int audioPlayCount = 0;
	private ExtAudioRecorder recorder;
	volatile boolean running;
	private Thread recordThread;
	private int audioDuration;
	
	private void initializeRecordingUi() {
		conRecordingLayout = (LinearLayout) findViewById(R.id.con_audio_recording);

		txtSecondsPlayed = (TextView) findViewById(R.id.second_played);
		progressRecording = (ProgressBar) findViewById(R.id.progressBarRecord);
		txtPlay = (Button) findViewById(R.id.btn_play);

		txtRecord = (Button) findViewById(R.id.btn_record);
		txtCancelRecording = (Button) findViewById(R.id.txt_cancel);
		txtPlay.setOnClickListener(recordingListener);

		txtRecord.setOnClickListener(recordingListener);
		txtCancelRecording.setOnClickListener(recordingListener);
	}
	
	
	
	OnClickListener recordingListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == txtPlay) {
				if (!isPlayingCurrently && !isOngoingRecording) {
					try {
						isOngoingRecording = false;
						// playAudioFile(CommonFunctions.getBaseURL()
						// + "temporary/" + "android.wav");

						if (recordingPath != null && recordingPath.length() > 0) {
							playAudioFile(recordingPath);
						} else {
							Toast.makeText(EyesaySendText.this,
									"Please record some file before play",
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {

						e.printStackTrace();
					}
				} else if (isPlayingCurrently) {
					Toast.makeText(EyesaySendText.this,
							"File is currently playing ", Toast.LENGTH_SHORT)
							.show();
				} else if (isOngoingRecording) {
					Toast.makeText(
							EyesaySendText.this,
							"Before playing please stop recording and then play ",
							Toast.LENGTH_SHORT).show();
				}
			}

			// else if (v == txtStop) {
			// if (isOngoingRecording) {
			// stopRecording();
			// } else if (isPlayingCurrently) {
			// stopPlaying();
			// }
			// }
			else if (v == txtRecord) {
				if (!isOngoingRecording) {
					StartRecording();
				} else {
					// Toast.makeText(ConversationMessages.this,
					// "Recording in progress ", Toast.LENGTH_LONG).show();
					if (isOngoingRecording) {
						stopRecording();
					} else if (isPlayingCurrently) {
						stopPlaying();
					}
				}
			} else if (v == txtCancelRecording) {
				conRecordingLayout.setVisibility(View.GONE);
				if (isOngoingRecording) {
					stopRecording();
				} else if (isPlayingCurrently) {
					stopPlaying();
				}
			}

		}
	};
	
	public void StartRecording() {

		if (recordThread != null) {
			try {
				// recordThread.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			CommonFunctions.writeLog("Audio Recording Started ");
		} catch (Exception e) {

		}
		txtRecord.setBackgroundResource(R.drawable.record_btn_on);
		// txtPlay.setBackgroundResource(R.drawable.videopause);
		// txtPlay.setTextColor(Color.GRAY);
		// txtStop.setTextColor(Color.GRAY);
		// startRecordingBtn.setEnabled(true);
		// btnCancel.setEnabled(false);
		progressRecording.setProgress(0);
		recorder = ExtAudioRecorder.getInstanse(false);
		running = true;
		try {
			baseurl = CommonFunctions.getBaseURL();
		} catch (IOException e1) {
			e1.printStackTrace();
			try {
				CommonFunctions.writeLog("Base Url Null Line No 781 "
						+ e1.getMessage());
			} catch (Exception e) {

			}
		}
		// recorder.setMaxDuration(30000);

		recorder.setOutputFile(baseurl + "temporary/" + "android.wav");

		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			try {
				CommonFunctions
						.writeLog("Unable to Prepare Audio Recorder IllegalStateException 794"
								+ e.getMessage());
			} catch (Exception e2) {

			}
		} catch (Exception e) {
			e.printStackTrace();
			CommonFunctions.writeLog("Unable to Prepare Audio Recorder 800"
					+ e.getMessage());
		}
		recorder.start();
		recordingPath = baseurl + "temporary/" + "android.wav";

		try {
			CommonFunctions.writeLog("Audio Recorder Start Called 804");
		} catch (Exception e) {
			e.printStackTrace();

		}
		isOngoingRecording = true;

		count = 0;
		try {
			recordThread = new Thread() {
				public void run() {
					Looper.prepare();
					try {
						while (count++ <= seconds && isOngoingRecording) {
							progressHandler.sendMessage(progressHandler
									.obtainMessage());
							sleep(1000);
						}
						if (count >= seconds && isOngoingRecording) {
							Debugger.debugE("if count" + count);
							endRecording();
						}
					} catch (Exception e) {
						Debugger.debugE("Exception" + e.getMessage());
					}
					Looper.loop();
				}
			};
			recordThread.start();
		} catch (Exception e) {
			CommonFunctions
					.writeLog("  Exception In the Audio Recorder Recording Thread "
							+ e.getMessage());
		}

	}

	Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			updateUICount();
		}
	};

	Handler audioPlayProgressHandler = new Handler() {
		public void handleMessage(Message msg) {
			updateUIAudioCountCount();
		}
	};

	void updateUICount() {
		try {

			if (isOngoingRecording) {
				int displaycount = 60 - count + 1;
				try {
					progressRecording
							.setProgress((int) (((count + 1) * 100) / seconds));
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Log.e("", "===============Count ++"+(count*1.65));
				if (displaycount - 1 >= 0) {
					if (displaycount - 1 <= 9) {
						// txtSeconds.setText("00:0" + (displaycount - 1));
						txtSecondsPlayed.setText("00:" + (count));
					} else {
						// txtSeconds.setText("00:" + (displaycount - 1));
						if (count > 9) {
							txtSecondsPlayed.setText("00:" + (count));
						} else {
							txtSecondsPlayed.setText("00:0" + (count));
						}
					}
				}
				if (displaycount == 0) {

					endRecording();
				}

				CommonFunctions
						.writeLog("Updating Audio Recording UI Count with count Value "
								+ count);
			}

		} catch (Exception e) {
			CommonFunctions.writeLog("Error while updating UI count "
					+ e.getMessage());
		}

	}

	void updateUIAudioCountCount() {

		try {

			if (isPlayingCurrently) {

				int displayAudiocount = audioDuration - audioPlayCount;

				try {
					progressRecording
							.setProgress((int) ((audioPlayCount * 100) / audioDuration));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (displayAudiocount >= 0) {
					if (displayAudiocount <= 9) {
						txtSecondsPlayed.setText("00:0" + displayAudiocount);
					} else {
						txtSecondsPlayed.setText("00:" + displayAudiocount);
					}
				}
				CommonFunctions.writeLog("Updating audio playing UI Count"
						+ audioPlayCount);
			}

		} catch (Exception e) {
			CommonFunctions
					.writeLog("Error while updating Audio PLaying UI Count "
							+ e.getMessage());
		}
	}

	private void stopRecording() {
		if (isOngoingRecording) {
			endRecording();
		}
	}

	public void endRecording() {
		// txtRecording.setText("Stopped...");
		try {
			running = false;
			recorder.stop();
			recorder.release();
			recorder = null;
			isOngoingRecording = false;

			isOngoingRecording = false;
			isPlayingCurrently = false;
			progressRecording.setProgress(100);
			// btnCancel.setEnabled(true);
			// txtRecord.setTextColor(Color.GRAY);

			txtRecord.setBackgroundResource(R.drawable.record_btn_off);
			// txtStop.setTextColor(Color.BLUE);
			// txtPlay.setTextColor(Color.GRAY);
			// txtPlay.setBackgroundResource(R.drawable.videopause);
			uploadFilePath = recordingPath;
			// Intent intent = new Intent(this, EyesayPlayAudio.class);
			// intent.putExtra("messagetype", 0);
			// intent.putExtra("type", 0);
			// intent.putExtra("url", baseurl + "temporary/" + "android.wav");
			//
			// intent.putExtra("receiver", "");
			// startActivityForResult(intent, REQUEST_CODE_USE_RETAKE);
			// CommonFunctions.writeLog("Audio Recording ended successfully ");

		} catch (Exception e) {
			e.printStackTrace();
			// Debugger.debugE(e.getMessage());
			CommonFunctions.writeLog("Error while Stoping Audio Recording "
					+ e.getMessage());
		}

	}

	MediaPlayer mp = null;

	public void playAudioFile(String fileName) {

		File file = new File(fileName);
		if (file.exists()) {

			try {
				// btnCancel.setEnabled(false);
				// txtPlay.setTextColor(Color.BLUE);
				txtPlay.setBackgroundResource(R.drawable.videopause);
				// txtRecord.setTextColor(Color.GRAY);
				txtRecord.setBackgroundResource(R.drawable.record_btn_off);
				// txtStop.setTextColor(Color.GRAY);

				mp = new MediaPlayer();
				mp.setDataSource(fileName);
				mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mp.setVolume(15, 15);
				mp.prepare();
				mp.start();
				isPlayingCurrently = true;

				audioDuration = mp.getDuration() / 1000;
				audioPlayCount = 0;

				progressRecording.setProgress(0);
				try {
					new Thread() {
						public void run() {
							Looper.prepare();
							try {
								while (audioPlayCount <= audioDuration
										&& isPlayingCurrently) {
									audioPlayProgressHandler
											.sendMessage(audioPlayProgressHandler
													.obtainMessage());
									sleep(1000);
									audioPlayCount++;
								}
								if (audioPlayCount >= audioDuration
										&& isPlayingCurrently) {
									Debugger.debugE("if audioPlayCount"
											+ audioPlayCount);
								}
							} catch (Exception e) {
								e.printStackTrace();
								Debugger.debugE("Exception" + e.getMessage());
							}
							Looper.loop();
						}
					}.start();
				} catch (Exception e) {
					CommonFunctions
							.writeLog("Error while starting Audio Playing Thread===="
									+ e.getMessage());
				}

				// h.post(playfile);
				mp.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {

						stopPlaying();

						// btnCancel.setEnabled(true);
						//
					}
				});
			} catch (Exception e) {
				// Debugger.debugE("error " + e.getMessage());
				mp.release();
				mp = null;
				isPlayingCurrently = false;

				CommonFunctions
						.writeLog("Error while Playing Recorded Audio File "
								+ e.getMessage());
			}

		} else {
			// txtRecording.setText("File Does Not Exist");
			Toast.makeText(getBaseContext(), "Audio File Does Not Exist", 5000)
					.show();
			// Alert dialog if file dose not exit

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					EyesaySendText.this);

			// set title
			alertDialogBuilder.setTitle("Tribewire");

			// set dialog message
			alertDialogBuilder
					.setMessage("Before you play, it's time to record!")
					.setCancelable(false)
					.setPositiveButton("ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									dialog.cancel();

								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();

			CommonFunctions
					.writeLog("File Does Not exists so cannot play this audio File");
		}
	}

	private void stopPlaying() {

		try {
			if (mp != null && mp.isPlaying()) {
				mp.stop();
				mp.release();

				mp = null;
				// txtRecord.setTextColor(Color.GRAY);
				txtRecord.setBackgroundResource(R.drawable.record_btn_off);
				// txtStop.setTextColor(Color.BLUE);
				// txtPlay.setTextColor(Color.GRAY);
				isPlayingCurrently = false;
				// txtRecording.setText("Stopped...");
				// playRecordingBtn
				// .setBackgroundResource(R.drawable.button_play_inactive);
				// btnCancel.setEnabled(true);
				progressRecording.setProgress(0);
				Log.e("", "Playing Stopped");
			}
		} catch (Exception e) {
			CommonFunctions
					.writeLog("Error while Stop Playing the Audio Recording 180 "
							+ e.getMessage());
		}
	}
	
	private void getFriendName(String friendId) {
		DataBase db = null;
		try {
			db = new DataBase(this);
			db.open();
			
			Cursor cursor  = db.fetch(DataBase.Friends_table, DataBase.Friends_int, Friend.NUMBER+" = '"+new String(Base64.decode(friendId.getBytes(), Base64.DEFAULT))+"'");
			Log.e("", "========= Cursor Count is "+cursor.getCount()+"=== Friend Id "+friendId);
			 if (cursor!=null && cursor.getCount()>0) {
				   String friendName  = cursor.getString(2);
	//			   mcV.setEnabled(false);
				   txt_contact_Names.setText(""+friendName+",");
				   
			}
			//Log.e("", "============ Is Status  updated " + isUpdated);
			 cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

	}

	ProgressDialog progressDialog;
	// It Download the company tile

	boolean delPrevText = false;
	int startIdx = 0;

	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("", "The Key Code is ==================== " + keyCode);
		if (keyCode == 67) {
			if (isAutoCompleteFocus) {
				// deleteToUser();
			}

		}

		return super.onKeyDown(keyCode, event);
	}

	OnClickListener dialog_Click = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v.getId() == R.id.record_dialog_btn_send) {

				if (msg_edittext.getText().toString().length() > 0) {

					if (!single_cont_sel || !group_cont_sel || !facebook_sel
							|| !twitter_sel) {
						if (CommonFunctions
								.isInternetConnected(EyesaySendText.this)) {

							if (dialog != null) {
								dialog.dismiss();
							}

						} else {
							try {
								Toast.makeText(
										getApplicationContext(),
										"Please check your internet connectivity",
										Toast.LENGTH_LONG).show();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						try {
							Toast.makeText(
									getApplicationContext(),
									"Please select at least one tile to enjoy tribewire",
									Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				} else {
//					if (eyesayonWayDialog != null) {
//						eyesayonWayDialog.dismiss();
//					}

					handler2.sendEmptyMessage(10);

				}

			}

		}
	};

	private void sendMultipleMessages() {
		if (!single_cont_sel || !group_cont_sel || !isToContactSelected) {
			if (dialog != null) {
				dialog.dismiss();
			}
			handler2.sendEmptyMessage(5);
		}

		try {
			if (CommonFunctions.isInternetConnected(EyesaySendText.this)) {

				startTimer();

				if (!single_cont_sel || !isToContactSelected) {

					if (send_single_msg) {
						try {
							baseurl = CommonFunctions.getBaseURL();
						} catch (Exception e) {
							e.printStackTrace();
						}
						// message type false means it is a video message
						boolean messageType = false;

						String media_message_url = uploadFilePath;

					}

					if (backToTilescontactData != null) {
						String numbers = backToTilescontactData
								.getStringExtra("receiver");
						String names = backToTilescontactData
								.getStringExtra("names");

						try {
							final String file_url = backToTilescontactData
									.getStringExtra("msg_url");
						} catch (Exception e) {

						}
						final Boolean messageType = backToTilescontactData
								.getBooleanExtra("messageType", false);
						Log.e("",
								"==========================================Single Contact ====================================================================");
						Log.e("", "Name " + names);
						Log.e("", "Numbers " + numbers);
						Log.e("", "File Url" + uploadFilePath);
						Log.e("", "messageType " + messageType);
						Log.e("",
								"=======================================================================================================");

						if (numbers != null && names != null
								&& numbers.length() > 0 && names.length() > 0) {

							Set<String> nameSet = new HashSet<String>();
							Set<String> numberSet = new HashSet<String>();

							String allRecipients = txt_contact_Names.getText()
									.toString();
							Log.e("",
									"============== All Recipients Names are =============="
											+ allRecipients);
							DataBase db = null;
							Cursor c = null;
							try {
								db = new DataBase(EyesaySendText.this);
								db.open();
								if (allRecipients != null
										&& allRecipients.length() > 0) {

									String nameArray[] = allRecipients
											.split(",");
									for (int i = 0; i < nameArray.length; i++) {
										String userName = nameArray[i].trim();
										if (userName != null
												&& userName.length() > 0) {
											try {
												c = db.fetch(
														DataBase.Friends_table,
														DataBase.Friends_int,
														"name='"
																+ userName
																		.trim()
																+ "'");

												numberSet.add(c.getString(3));
												nameSet.add(userName);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}

									}

									// Log.e("", "========= The name set is "
									// + nameSet);
									// Log.e("", "========= The Phone set is "
									// + numberSet);

								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								db.close();
								c.close();
							}

							try {
								String finalNames[] = nameSet
										.toArray(new String[nameSet.size()]);
								String finalNumber[] = numberSet
										.toArray(new String[nameSet.size()]);
								if (finalNames != null && finalNames.length > 0
										&& finalNumber != null
										&& finalNumber.length > 0) {
									names = "";
									numbers = "";
									db = new DataBase(this);
									db.open();
								}

								for (int i = 0; i < finalNumber.length; i++) {
									names += finalNames[i] + ",";
									c = db.fetch(DataBase.Friends_table,
											DataBase.Friends_int, "name='"
													+ finalNames[i].trim()
													+ "'");

									Log.e("",
											"Name "
													+ finalNames[i]
													+ " Count "
													+ c.getCount()
													+ " Status "
													+ (c.getInt(Friend.INT_STATUS))
													+ " Number "
													+ c.getString(3));

									if (c != null && c.getCount() > 0) {
										if ((c.getInt(Friend.INT_STATUS)) == 5 || (c.getInt(Friend.INT_STATUS)) == 6) {
											numbers += "-g_"
													+ new String(
															Base64.decode(
																	c.getString(
																			3)
																			.getBytes(),
																	Base64.DEFAULT))
													+ "-,";
											// Log.e("",
											// "====== The Number \n "+numbers);
										} else {

											numbers += "-" + c.getString(3)
													+ "-,";
											// Log.e("",
											// "====== The Number \n "+numbers);
										}

									}
								}
								Log.e("", "============ The names are " + names);
								if (names.endsWith(",")) {
									names = names.substring(0,
											names.length() - 1);
									numbers = numbers.substring(0,
											numbers.length() - 1);
								}

							} catch (Exception e) {
								e.printStackTrace();

							} finally {
								c.close();
								db.close();

							}

							boolean isBroadCast   = false;
							
							if (txt_contact_Names.getText().toString().equalsIgnoreCase("all")) {
								isBroadCast = true;
							}
						//	isSending    = false;
							if (CommonFunctions.SendTextMessage(context,
									messageType, uploadFilePath, numbers,
									names, false, null, msg_edittext.getText()
											.toString(), isBroadCast )) {
//								eyesayonWayDialog.getHandlerObj()
//										.sendEmptyMessage(90);

								// }
								// handler2.sendEmptyMessage(3);

							} else {
								String dest = "";
								try {
									CommonFunctions.getBaseURL();
									dest = CommonFunctions.getImageRL()
											+ System.currentTimeMillis()
											+ EyesaySendText.IMAGE_EXTENSION;

									Log.e("", "================== dest==="
											+ dest);

									handler2.sendEmptyMessage(1);
								} catch (IOException e1) {

									e1.printStackTrace();
								}

							}

						}
					} else {

						Set<String> nameSet = new HashSet<String>();
						Set<String> numberSet = new HashSet<String>();

						String allRecipients = txt_contact_Names.getText()
								.toString();
						Log.e("",
								"============== All Recipients Names are =============="
										+ allRecipients);
						DataBase db = null;
						Cursor c = null;
						try {
							db = new DataBase(EyesaySendText.this);
							db.open();
							if (allRecipients != null
									&& allRecipients.length() > 0) {

								String names[] = allRecipients.split(",");
								for (int i = 0; i < names.length; i++) {
									String userName = names[i].trim();
									if (userName != null
											&& userName.length() > 0) {

										try {
											c = db.fetch(
													DataBase.Friends_table,
													DataBase.Friends_int,
													"name='" + userName.trim()
															+ "'");
											if (c != null && c.getCount() > 0) {
												try {
													numberSet.add(c
															.getString(3));
												} catch (Exception e) {
													e.printStackTrace();
												}
												nameSet.add(userName);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
								Log.e("", "========= The name set is "
										+ nameSet);
								Log.e("", "========= The Phone set is "
										+ numberSet);

							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (c != null && db != null) {
								db.close();
								c.close();
							}
						}
						// try {
						if ((nameSet != null && nameSet.size() > 0
								&& numberSet != null && numberSet.size() > 0)) {

							try {

								db = new DataBase(this);
								db.open();
								String numbers = "";
								String names = "";

								final Boolean messageType = false;
								String finalNames[] = nameSet
										.toArray(new String[nameSet.size()]);
								String finalNumber[] = numberSet
										.toArray(new String[nameSet.size()]);
								for (int i = 0; i < finalNumber.length; i++) {
									names += finalNames[i] + ",";
									c = db.fetch(DataBase.Friends_table,
											DataBase.Friends_int, "name='"
													+ finalNames[i].trim()
													+ "'");
									Log.e("",
											"Name "
													+ finalNames[i]
													+ " Count "
													+ c.getCount()
													+ " Status "
													+ (c.getInt(Friend.INT_STATUS))
													+ " Number "
													+ c.getString(3));
									if (c != null && c.getCount() > 0) {
										if ((c.getInt(Friend.INT_STATUS)) == 5 || (c.getInt(Friend.INT_STATUS)) == 6) {
											numbers += "-g_"
													+ new String(
															Base64.decode(
																	c.getString(
																			3)
																			.getBytes(),
																	Base64.DEFAULT))
													+ "-,";

										} else {
											numbers += "-" + c.getString(3)
													+ "-,";
										}

									}
								}
								if (names.endsWith(",")) {
									names = names.substring(0,
											names.length() - 1);
									numbers = numbers.substring(0,
											numbers.length() - 1);
								}

								boolean isBroadCast  = false;
								if (txt_contact_Names.getText().toString().equalsIgnoreCase("all")) {
									isBroadCast = true;
								}		
							//	isSending  = false;
								if (CommonFunctions.SendTextMessage(context,
										messageType, uploadFilePath, numbers,
										names, false, null, msg_edittext
												.getText().toString(),isBroadCast)) {
//									eyesayonWayDialog.getHandlerObj()
//											.sendEmptyMessage(90);
								} else {
									String dest = "";
									try {
										CommonFunctions.getBaseURL();
										dest = CommonFunctions.getImageRL()
												+ System.currentTimeMillis()
												+ EyesaySendText.IMAGE_EXTENSION;

										Log.e("", "================== dest==="
												+ dest);

										handler2.sendEmptyMessage(1);
									} catch (IOException e1) {

										e1.printStackTrace();
									}

								}

							} catch (Exception e) {
								e.printStackTrace();

							} finally {
								if (c != null && db != null) {
									c.close();
									db.close();
								}
							}

						}
						else if (txt_contact_Names.getText().toString().equalsIgnoreCase("all")){
							try {
								boolean isBroadCast  = false;
								if (txt_contact_Names.getText().toString().equalsIgnoreCase("all")) {
									isBroadCast = true;
								}						
								if (CommonFunctions.SendTextMessage(context,
										false, uploadFilePath, "",
										"", false, null, msg_edittext
												.getText().toString(),isBroadCast)) {
//									eyesayonWayDialog.getHandlerObj()
//											.sendEmptyMessage(90);
								} else {
									String dest = "";
									try {
										CommonFunctions.getBaseURL();
										dest = CommonFunctions.getImageRL()
												+ System.currentTimeMillis()
												+ EyesaySendText.IMAGE_EXTENSION;

										Log.e("", "================== dest==="
												+ dest);

										handler2.sendEmptyMessage(1);
									} catch (IOException e1) {

										e1.printStackTrace();
									}

								}

							} catch (Exception e) {
								e.printStackTrace();

							} 

							
						}
						
						else {

							if (!(backToTilesGroupData != null)) {
								Toast.makeText(
										EyesaySendText.this,
										"Recipient does not exists , Please enter valid recipient name",
										Toast.LENGTH_LONG).show();
							}

						}

					}
				}
				if (!group_cont_sel) {
					if (backToTilesGroupData != null) {
						String numbers = backToTilesGroupData
								.getStringExtra("receiver");
						final String names = backToTilesGroupData
								.getStringExtra("names");
						final String file_url = backToTilesGroupData
								.getStringExtra("msg_url");
						final Boolean messageType = backToTilesGroupData
								.getBooleanExtra("messageType", false);
						final ArrayList<String> grlist = backToTilesGroupData
								.getStringArrayListExtra("grlist");

						final ArrayList<GroupModel> grModel = (ArrayList<GroupModel>) backToTilesGroupData
								.getSerializableExtra("grdata");
						Log.e("",
								"==========================================Group data ================================================");
						Log.e("", "Name " + names);
						Log.e("", "Numbers " + numbers);
						Log.e("", "File Url" + uploadFilePath);
						Log.e("", "messageType " + messageType);
						Log.e("", "Model ArrayList size is " + grModel.size());
						Log.e("",
								"=======================================================================================================");

						for (int i = 0; i < grModel.size(); i++) {
							numbers = "-g_"
									+ new String(Base64.decode(grModel.get(i)
											.getGroupIds().getBytes(),
											Base64.DEFAULT)) + "-,";
						}
						if (numbers.endsWith(",")) {
							numbers = numbers
									.substring(0, numbers.length() - 1);
						}
						Log.e("",
								"=========================== Finally The numbers are "
										+ numbers + " ==============");
						if (numbers != null && names != null
								&& numbers.length() > 0 && names.length() > 0) {
							if (CommonFunctions.SendTextGroupMessage(context,
									messageType, uploadFilePath, numbers,
									names, false, null, grlist, grModel,
									msg_edittext.getText().toString())) {
								// if (single_cont_sel && group_cont_sel) {
//								eyesayonWayDialog.getHandlerObj()
//										.sendEmptyMessage(95);
								// }
								// else {
								// if (eyesayonWayDialog!=null) {
								// eyesayonWayDialog.dismiss();
								// }
								// finish();
								// }
								handler2.sendEmptyMessage(2);

							} else {
								String dest = " ";
								try {
									CommonFunctions.getBaseURL();
									dest = CommonFunctions.getImageRL()
											+ System.currentTimeMillis()
											+ ".jpeg";

									Log.e("", "================== dest==="
											+ dest);
									for (int i = 0; i < grlist.size(); i++) {

										String groupName = grlist.get(i);
										String groupMembers = grModel.get(i)
												.getGroupMember().trim();
										String groupMemberNames = grModel
												.get(i).getGroupMemberNames()
												.trim();
										groupMembers = groupMembers.replace(
												"-", "");
										try {
											groupMembers = CommonFunctions
													.sortStringArray(groupMembers);
										} catch (Exception e) {
											e.printStackTrace();
										}
										Log.e("",
												"===================== The group Members are "
														+ groupMembers);
										Log.e("",
												"===================== The group Members Names are "
														+ groupMemberNames);

									}
									handler2.sendEmptyMessage(1);
								} catch (IOException e1) {

									e1.printStackTrace();
								}
							}

						}
					}
				}

			}

			// / No Internet Connection
			else {
//				if (eyesayonWayDialog != null) {
//					eyesayonWayDialog.dismiss();
//				}
				CommonFunctions
						.showAlert(
								EyesaySendText.this,
								"Please Check your internet connection, You can save your message in drafts by clicking save &  can send later when network avilable ",
								"Network Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			if (eyesayonWayDialog != null) {
//				eyesayonWayDialog.dismiss();
//
//			}
			if (dialog != null) {
				dialog.dismiss();
			}
			isSending  = true;
			stopTimer();
			finish();

		}

	}

	public Handler handler2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				Toast.makeText(
						getApplicationContext(),
						"Looks like there was a problem sending your message.",
						9000).show();
			} else if (msg.what == 2) {
				Toast.makeText(EyesaySendText.this,
						"Message Sent to Group successfully",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == 3) {
				Toast.makeText(EyesaySendText.this,
						"Message Sent successfully", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 5) {
//				eyesayonWayDialog = new EyesayonWayDialog(EyesaySendText.this);
//				eyesayonWayDialog.setCancelable(true);
//				eyesayonWayDialog.show();
//				eyesayonWayDialog.getHandlerObj().sendEmptyMessage(9);
			} else if (msg.what == 6) {
				if (!facebook_sel) {

				}
			} else if (msg.what == 7) {
				if (!twitter_sel) {

				}

			} else if (msg.what == 8) {
				try {
//					Toast.makeText(
//							getApplicationContext(),
//							"Would you like to save your message to drafts so you can try sending later?",
//							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == 10) {
				try {
					Toast.makeText(
							getApplicationContext(),
							"Please enter some text or select media to send message ",
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == 15) {
				Toast.makeText(
						getApplicationContext(),
						"No  Member exists in Group, Please add at least one member & then reply again",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(
						getApplicationContext(),
						"Looks like there was a problem sending your message to groups.Please try again later",
						9000).show();
			}
		};

	};

	private void startTimer() {
		try {
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					timerCount++;
					if (timerCount >= 45) {
						stopTimer();
					}
				}
			}, 50, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void stopTimer() {
		try {
			if (timer != null) {
				timer.cancel();
				timer = null;
				if (timerCount >= 45) {
					handler2.sendEmptyMessage(8);
				}
				timerCount = 0;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean checkmessageToSend() {
		boolean isFile = false;
		try {

			if (msg_edittext.getText().toString().length() > 0) {

				isFile = true;

			} else {

				isFile = false;

			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return isFile;

	}

	@Override
	public void onClick(View v) {

		if (v == msg_txt_back) {

			// Intent go_to_tabActivity = new Intent(getApplicationContext(),
			// TenFourTab.class);
			// startActivity(go_to_tabActivity);
			try {
				setResult(RESULT_OK);
			} catch (Exception e) {
				e.printStackTrace();
			}
			finish();
			

			// finish();

		} else if (v == msg_txt_send) {

			   if (isSending) {
				   isSending  = false;
					if (txt_contact_Names.getText().length() > 3 || txt_contact_Names.getText().toString().equalsIgnoreCase("all")) {
						if (msg_edittext.getText().length() > 0
								|| uploadFilePath.length() > 0) {
							single_cont_sel = true;
							if (txt_contact_Names.getText().toString().equalsIgnoreCase("all")) {
								isToContactSelected = false;
							}
							try {
								if (conRecordingLayout.getVisibility() == View.VISIBLE) {
									conRecordingLayout
											.setVisibility(View.GONE);
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
							Thread thread = new Thread(new Runnable() {

								@Override
								public void run() {
									Looper.prepare();

									sendMultipleMessages();
									Looper.loop();

								}
							});
							thread.start();
						} else {
							isSending  = true;
							Toast.makeText(this,
									"Please enter message or select media ",
									Toast.LENGTH_LONG).show();
						}

					} else {
						isSending  = true;
						Toast.makeText(this,
								"Please select atleast one contact or group",
								Toast.LENGTH_LONG).show();
					}
			      }
			  
			


		}

		else if (v == btn_single_cont) {
			if (single_cont_sel) {
				Intent test_intent = new Intent();
				test_intent.setClass(EyesaySendText.this,
						ContactListSelection.class);

				test_intent.putExtra(Utils.MEDIA_MESSAGE_KEY,
						Utils.MESSAGE_IMAGE);
				test_intent.putExtra(Utils.TEXT_MESSAGE_KEY, msg_edittext
						.getText().toString());

				test_intent.putExtra(Utils.TEXT_IMAGE_URL_KEY, uploadFilePath);
				startActivityForResult(test_intent, REQUEST_CANCEL);

				single_cont_sel = false;
			} else {
				btn_single_cont
						.setBackgroundResource(R.drawable.button_sharetocontacts_active);
				single_cont_sel = true;
			}

		}

		else if (v == eyesay_txt_camera) {

		} else if (v == btnPlayPause) {

			if (playflag == false) {
				Log.e("", "==Starting of new media");
				handler3.sendEmptyMessage(3);
				playMyAudioFile(uploadFilePath, false);

			} else if (playflag == true && pauseFlag == false) {
				Log.e("", "=================== Media paused");
				try {
					if (mp != null && mp.isPlaying()) {
						mp.pause();
						threadPP.pauseThread();
						length = mp.getCurrentPosition();
						pauseFlag = true;
						handler3.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (pauseFlag == true) {
				Log.e("",
						"=================== Resuming the Media Player if else case ");
				pauseFlag = false;
				playflag = true;
				handler3.sendEmptyMessage(2);

				mp.seekTo(length);
				mp.start();

				threadPP = new ThreadPP();

				// threadPP.resumeThread();

			}

		}
	}

	
	@Override
	protected void onResume() {
		try {
			
			ActivityStatus.setActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		try {
			if (mp != null) {
				mp.stop();
				mp.release();
				move = 0;
				temp = 0;
				playflag = false;
				if (threadPP != null) {
					threadPP.pauseThread();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			ActivityStatus.setActivity(null);
		

		super.onPause();
	}
	
	
	public Handler handler5= new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {

				try {
				 String message   =  msg.getData().getString("message", "");
				 String emergencyMessage  =  TribeWireUtil.getNotificationMessage(message);
				 TribeWireUtil.showNotificationAlert(EyesaySendText.this, emergencyMessage, "Emergency");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		};
	Handler handler4 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				try {
					if (profile_bm!=null) {
						showImageContainer();
						imgCapture.setImageBitmap(profile_bm);
					}
					
					//if (imageUri != null) {
						//showImageContainer();
						//imgCapture.setImageURI(imageUri);
					//}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (msg.what == 2) {
				try {
					if (profile_bm != null) {
						showImageContainer();
						imgCapture.setImageURI(selectedImage);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == 3) {
				showAudioContainer();
			} else if (msg.what == 4) {
				showAudioContainer();
			}

		}

	};

	private void showImageContainer() {
		try {
			if (imgCapture.getVisibility() == View.GONE) {
				imgCapture.setVisibility(View.VISIBLE);
			}
			if (rltAudio.getVisibility() == View.VISIBLE) {
				rltAudio.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void showAudioContainer() {
		try {
			if (imgCapture.getVisibility() == View.VISIBLE) {
				imgCapture.setVisibility(View.GONE);
			}
			if (rltAudio.getVisibility() == View.GONE) {
				rltAudio.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	int message_id = 0;

	private int REQUEST_CANCEL = 909;
	public static final String IMAGE_DATA = "image_data";
	public static final String AUDIO_DATA = "audio_data";
	Uri selectedImage = null;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("", "The Request Code " + requestCode
				+ " ======= Result Code is  " + resultCode);
		if (requestCode == 601 && resultCode == RESULT_OK) {
			setResult(RESULT_FIRST_USER);

		} else if (requestCode == 610 && resultCode == 777) {
			try {
				if (dialog != null) {
					dialog.dismiss();
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (requestCode == 610 && resultCode == RESULT_OK) {
			try {
				// btn_single_cont
				// .setBackgroundResource(R.drawable.button_sharetocontacts_active);
				single_cont_sel = true;
				Log.e("", "=================Here");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		else if (requestCode == 610 && resultCode == 779) {
			try {
				// btn_group_cont
				// .setBackgroundResource(R.drawable.button_sharetogroups_active);
				group_cont_sel = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else if (requestCode == 610
				&& resultCode == Utils.SINGLE_BACKTOTILE_RESULT) {
			try {
				backToTilescontactData = data;
				if (backToTilescontactData == null) {
					try {
						// btn_single_cont
						// .setBackgroundResource(R.drawable.button_sharetocontacts_active);
						single_cont_sel = true;
						Log.e("", "=================Here");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (requestCode == 610
				&& resultCode == Utils.GROUP_BACKTOTILE_RESULT) {
			try {
				backToTilesGroupData = data;
				if (backToTilesGroupData == null) {

					try {
						// btn_group_cont
						// .setBackgroundResource(R.drawable.button_sharetogroups_active);
						group_cont_sel = true;
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		else if (requestCode == REQUEST_CANCEL && resultCode == RESULT_OK) {
			try {
				// btn_single_cont
				// .setBackgroundResource(R.drawable.button_sharetocontacts_active);
				single_cont_sel = true;
				Log.e("", "=================Here");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (requestCode == REQUEST_CANCEL && resultCode == 779) {
			try {
				// btn_group_cont
				// .setBackgroundResource(R.drawable.button_sharetogroups_active);
				group_cont_sel = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else if (requestCode == REQUEST_CANCEL && resultCode == 777) {
			try {
				if (dialog != null) {
					dialog.dismiss();
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (requestCode == REQUEST_CANCEL
				&& resultCode == Utils.SINGLE_BACKTOTILE_RESULT) {
			try {
				backToTilescontactData = data;
				if (backToTilescontactData == null) {
					try {
						// btn_single_cont
						// .setBackgroundResource(R.drawable.button_sharetocontacts_active);
						single_cont_sel = true;
						Log.e("", "=================Here");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				else {
					try {
						String names = backToTilescontactData
								.getStringExtra("names");

						txt_contact_Names.append("" + names + ",");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (requestCode == REQUEST_CANCEL
				&& resultCode == Utils.GROUP_BACKTOTILE_RESULT) {
			try {
				backToTilesGroupData = data;
				if (backToTilesGroupData == null) {
					try {
						// btn_group_cont
						// .setBackgroundResource(R.drawable.button_sharetogroups_active);
						group_cont_sel = true;
						Log.e("", "=================Here");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				else {
					try {

						if (backToTilesGroupData != null) {
							final ArrayList<String> grlist = backToTilesGroupData
									.getStringArrayListExtra("grlist");
							String names = "";
							for (int i = 0; i < grlist.size(); i++) {
								names += grlist.get(i) + ",";
							}

							txt_contact_Names.append("" + names + ",");
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (requestCode == ACTIVITY_CAPTURE_IMAGE && resultCode == RESULT_OK) {
			if (resultCode == RESULT_OK) {

				Uri selectedImage = imageUri;
				try {

				//	getContentResolver().notifyChange(selectedImage, null);
					Log.e("",
							"-------------------------------------------------------------"
									+ selectedImage);

					uploadFilePath = selectedImage.toString();

					if (uploadFilePath.startsWith("file://")) {
						uploadFilePath = uploadFilePath.replace("file://", "");
						Log.e("",
								"After-------------------------------------------------------------"
										+ uploadFilePath);
						
						Bitmap bitmap = getBitmap(selectedImage);
						profile_bm = TribeWireUtil.rotateBitmapZero(
								selectedImage.toString(), bitmap);

					}
					try {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();

						boolean isImageWritten = profile_bm.compress(
								Bitmap.CompressFormat.JPEG, 100, bos);
						if (isImageWritten) {

						}

						byte bitmapImage[] = bos.toByteArray();
						File file1 = getTempFile(this);
						FileOutputStream fos = new FileOutputStream(file1);
						uploadFilePath = file1.toString();
						Log.e("", "The Compressed bitmap File path is "
								+ uploadFilePath);
						fos.write(bitmapImage);
						fos.close();

						// handler.sendEmptyMessage(2);
					} catch (Exception e) {
						e.printStackTrace();
					}
					handler4.sendEmptyMessage(1);
				} catch (Exception e) {
					e.printStackTrace();

				}

			}
		}

		else if (requestCode == ACTIVITY_SELECT_IMAGE
				&& resultCode == RESULT_OK) {
			if (resultCode == RESULT_OK) {

				try {
					selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					Log.e("", "File Path is " + filePath);
					uploadFilePath = filePath;
					cursor.close();
					profile_bm = getBitmap(selectedImage);
					try {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();

						boolean isImageWritten = profile_bm.compress(
								Bitmap.CompressFormat.JPEG, 100, bos);
						if (isImageWritten) {

						}

						byte bitmapImage[] = bos.toByteArray();
						File file1 = getTempFile(this);
						FileOutputStream fos = new FileOutputStream(file1);
						uploadFilePath = file1.toString();
						Log.e("", "The Compressed bitmap File path is "
								+ uploadFilePath);
						fos.write(bitmapImage);
						fos.close();

						handler4.sendEmptyMessage(2);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}

		else if (requestCode == ACTIVITY_CAPTURE_AUDIO
				&& resultCode == RESULT_OK) {

			if (resultCode == RESULT_OK) {

				try {

					uploadFilePath = data.getStringExtra(Emergency.AUDIO_DATA);
					Log.e("",
							"-------------------------------------------------------------"
									+ uploadFilePath + " ");

					handler4.sendEmptyMessage(3);
				} catch (Exception e) {
					e.printStackTrace();

				}

			}

		}

		// finish();
	}

	Uri imageUri;

	int length = 0;

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	public static File getTempFile(Context context) {
		File path1 = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path1.exists()) {
			path1.mkdir();
			Log.e("", "Directory Created Again");
		}

		return new File(path1, System.currentTimeMillis() + "eyesay.jpeg");
	}

	// --------------------------------------------//

	private String TAG = this.getClass().getSimpleName();

	private Bitmap getBitmap(Uri uri) {

		// Uri uri = getImageUri(path);
		InputStream in = null;
		try {
			final int IMAGE_MAX_SIZE = 700000; // 1.2MP
			in = getContentResolver().openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
			Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth
					+ ", orig-height: " + o.outHeight);

			Bitmap b = null;
			in = getContentResolver().openInputStream(uri);
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
				Log.d(TAG, "1th scale operation dimenions - width: " + width
						+ ",height: " + height);

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
			// FileOutputStream fileOutputStream = new
			// FileOutputStream(getTempFile(this));
			// while (in.available()!=-1) {
			// //in.read();
			//
			// try {
			// fileOutputStream.write(in.read());
			// fileOutputStream.close();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			in.close();

			// Log.d(TAG, "bitmap size - width: " +b.getWidth() + ", height: " +
			// b.getHeight());

			Log.e("", "");
			return b;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	private boolean checkFileToSend() {
		boolean isFile = false;
		try {
			try {
				baseurl = CommonFunctions.getBaseURL();
			} catch (Exception e) {
				e.printStackTrace();
			}

			File file = new File(baseurl + "temporary/" + "android.3gpp");

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

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	private void showMenu() {
		try {
			HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
			hashMap.put(0, "Take Photo");
			hashMap.put(1, "Use Existing Photo");
			hashMap.put(2, "Record Voice Message");
			hashMap.put(3, "Cancel");
			CustomMenu.show(this, hashMap, new OnMenuItemSelectedListener() {

				@Override
				public void MenuItemSelectedEvent(Integer selection) {
					if (selection == 0) {

						takePhoto();
					} else if (selection == 1) {
						chooseExisting();
					} else if (selection == 2) {
						addVoice();
					} else if (selection == 3) {
						CustomMenu.hide();
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Records Voice Using Eyesay Voice UI
	private void addVoice() {
		try {
			// Intent intent = new Intent(this, TenFourRecordVoice.class);
			// startActivityForResult(intent, ACTIVITY_CAPTURE_AUDIO);
			conRecordingLayout.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	// Takes Photo Using Default Camera
	private void takePhoto() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		File photo = getTempFile(this);
		photo.delete();
		photo = getTempFile(this);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		imageUri = Uri.fromFile(photo);
		startActivityForResult(intent, ACTIVITY_CAPTURE_IMAGE);

	}

	// choose Existing Image and Video from Library
	private void chooseExisting() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
	}

	Handler handler1 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				try {
					if (imageUri != null) {
						// showContainer();
						// imgCaptureImage.setImageURI(imageUri);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (msg.what == 2) {
				try {
					if (profile_bm != null) {
						// showContainer();
						// imgCaptureImage.setImageBitmap(profile_bm);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}

	};

	boolean downlodflag = true;
	boolean playflag = false;
	boolean pauseFlag = false;

	private boolean mediaCompleted = false;
	private boolean mediaFirstTime = true;
	
	int move = 0;
	int temp = 0;
	int sec = 0;

	final Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (move < 10) {
				txtAudioCount.setText("00:0" + move);
			}

			else {
				txtAudioCount.setText("00:" + move);
			}
			temp++;
			progressBar.setProgress((int) (((temp) * 100) / sec));
		};
	};

	File f = null;
	ThreadPP threadPP;

	private void playMyAudioFile(String fileName, final boolean autodownlod) {

		Log.e("", "========== The File name is " + fileName);
		try {
			mp = new MediaPlayer();
			try {
				mp.setDataSource(fileName);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.setVolume(15, 15);
			try {
				mp.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mp.start();
			playflag = true;
			sec = mp.getDuration() / 1000;
			if (sec > 60)
				sec = 60;
			// CommonFunctions.setFont(EyesayPlayAudio.this, txt_audio_seconds,
			// false, false);
			// txt_audio_seconds.setText("Duration: 00:"
			// + ((sec <= 9) ? "0" + sec : sec));

			move = sec;
			Log.e("", "===================" + move);
			// new Handler().post(runnable);

			// Thread t = new Thread() {
			// public void run() {
			// while (move > 0) {
			// move--;
			//
			// handler.sendEmptyMessage(1);
			// try {
			// sleep(1000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			//
			// }
			// }
			// };
			// t.start();
			threadPP = new ThreadPP();
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					Debugger.debugE("on completion method..");
					move = 0;
					temp = 0;
					if (mp != null) {
						mp.stop();
						mp.release();
					}

					playflag = false;
					mediaCompleted = true;
					mp = null;
					EyesaySendText.this.mp = null;
					handler3.sendEmptyMessage(4);
					if (autodownlod) {
						// TODO TOAST HAS TO ADD
						// callMenu(type, Receiver, autodownlod);
					}
					// finish();
				}

			});

		} catch (Exception e) {
			CommonFunctions.showAlert(EyesaySendText.this,
					"Error whle Playing Media", "Error");

			Debugger.debugE("error " + e.getMessage());

			mp.release();
			mp = null;

			playflag = false;

		}

	}

	Handler handler3 = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {
				btnPlayPause.setBackgroundResource(R.drawable.videoplay);
			} else if (msg.what == 2) {
				btnPlayPause.setBackgroundResource(R.drawable.videopause);
			} else if (msg.what == 3) {
				btnPlayPause.setBackgroundResource(R.drawable.videopause);
			} else if (msg.what == 4) {
				btnPlayPause.setBackgroundResource(R.drawable.videoplay);
				progressBar.setProgress(0);
			}
		};
	};

	boolean isRunning = true;

	class ThreadPP implements Runnable {

		public ThreadPP() {
			Thread thread = new Thread(this);
			thread.start();

			isRunning = true;
			;
		}

		public synchronized void pauseThread() {
			isRunning = false;
			try {
				// this.wait();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public synchronized void resumeThread() {
			isRunning = true;
			try {
				// this.notify();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {

			Log.e("", "=========== The move Count is =============" + move);
			while (move > 0 && isRunning) {
				move--;
				Log.e("", "=========== The move Count is " + move);
				// handler.post(new Runnable() {
				// public void run() {
				// if (move >= 0) {
				// ((TextView) findViewById(R.id.audio_seconds))
				// .setText("Duration: 00:"
				// + ((move <= 9) ? "0" + move
				// : move));
				//
				// }
				// }
				// });
				handler.sendEmptyMessage(1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

	}

}
