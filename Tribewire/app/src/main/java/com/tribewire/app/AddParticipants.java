package com.tribewire.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.deser.FromStringDeserializer;
import org.json.JSONObject;

import android.app.ListActivity;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.http.JsonResponseHandler;
import com.app.eyesay.custommenu.CustomMenu;
import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.TribeWireConstants;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.alphabetlist.IndexableListView;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.RestApiCall;
import com.eyesayapp.model.FriendModel;
import com.eyesayapp.model.SectionHeader;
import com.google.android.gms.internal.cn;
import com.google.android.gms.internal.co;
import com.google.android.gms.internal.di;

import eyesay.adapter.AddParticipantAdp;
import eyesay.adapter.SimpleAdapter;

public class AddParticipants extends ListActivity implements OnClickListener,
		OnItemClickListener {

	public ProgressDialog pd;
	Cursor friends, c;
	public int size = 0;
	public String[] FriendName = null;
	public String[] FriendNumber = null;
	public int[] Friendid = null;
	public int[] status = null;
	public int[] status_grp = null;
	public int position_contact;
	public int selectedGroup = -1;
	public EditText txtnumber;
	public Button btnok, btncancel;
	// public static String friend=null;
	String friend;
	public static String Cont_No = null;
	public static String Cont_Name = null;

	// only for the icon representation
	public String[] Groupid = null;
	public String[] friendsMenu = null;

	// Group Selection
	public String[] GroupName = null;
	public int[] GMid = null;
	IndexableListView contacts_list;
	public String[] DisplayGroup = null;
	public int[] totalMember = null;
	int TotalActiveFriend[] = null;
	public int[] contactid = null;
	ListView group_lvc;
	public int position_gr;
	public int GMgroupid = 0;
	public int MODE = 0;
	int Activecount = 0;
	public String GMnames[], GMGroup[];
	public int GMstatus[];
	public String GMid_Str[];
	public int GMcount = 0;

	static int which_list = 1;

	private Button btnGoToGroup;
	private Button btnSearch;
	private RelativeLayout rltSearchBar;
	private TextView txtCancelSearch;
	private EditText edtSearchText;

	// Paramters for menu panel
	LinearLayout slidingPanel;
	RelativeLayout main_Layout;

	boolean isExapnd = false;
	LinearLayout conProfileData;
	ImageView imgProfilePic;
	Button btnBadge;

	ProgressDialog progressDialog;
	private RelativeLayout contactllayout;
	private String convId = "";
	List<String> participants = null;
	List<String> participantNames = null;
	List<Integer> positionList = null;
	private Button btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.group_contact_framelayout);

		contacts_list = (IndexableListView) findViewById(R.id.list_view);
		contacts_list.setFastScrollEnabled(true);
		contacts_list.setBackgroundColor(Color.WHITE);
		contacts_list.setOnItemClickListener(this);
		btnSearch = (Button) findViewById(R.id.btn_search);
		btnGoToGroup = (Button) findViewById(R.id.btn_goto_group);
		rltSearchBar = (RelativeLayout) findViewById(R.id.rlt_search_bar);
		txtCancelSearch = (TextView) findViewById(R.id.txt_search_cancel);
		edtSearchText = (EditText) findViewById(R.id.edt_search_contact);
		contactllayout = (RelativeLayout) findViewById(R.id.contactllayout);
		btnBack = (Button) findViewById(R.id.btn_menu);
		contactllayout.setVisibility(View.VISIBLE);
		btnGoToGroup.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnSearch.setOnClickListener(searchListener);
		txtCancelSearch.setOnClickListener(searchListener);

		contacts_list.setVisibility(View.VISIBLE);
		participants = new ArrayList<String>();
		participantNames = new ArrayList<String>();
		positionList  = new ArrayList<Integer>();
		CreateProgressBar();
		getReceipientData();
		FillFriends();
		super.onCreate(savedInstanceState);
	}

	ArrayList<FriendModel> conversationMessages;

	private void getReceipientData() {
		try {
			try {
				conversationMessages = (ArrayList<FriendModel>) getIntent()
						.getSerializableExtra("conListener");

				convId = (String) getIntent().getStringExtra("conid");

				Log.e("",
						"===== Size of Friend Model"
								+ conversationMessages.size());
				Log.e("", "=========== The conversation Id " + convId);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public Context getActivity() {
		return this;

	}

	OnClickListener searchListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v == btnSearch) {
				showSearchBar();
			} else if (v == txtCancelSearch) {
				hideSearchBar();
				FillFriends();
			}
		}
	};

	private void showSearchBar() {
		try {
			if (rltSearchBar.getVisibility() == View.GONE) {
				TribeWireUtil.showKeyboard(getActivity(), edtSearchText);
				rltSearchBar.setVisibility(View.VISIBLE);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void hideSearchBar() {
		try {

			if (rltSearchBar.getVisibility() == View.VISIBLE) {
				TribeWireUtil.hideKeyboard(getActivity(), edtSearchText);
				rltSearchBar.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {
				// lvc = this.getListView();

				addParticipantAdp = new AddParticipantAdp(getActivity(),
						FriendName, status, Groupid, FriendNumber, friendImage,
						sectionHeaders, isSelected);
				contacts_list.setAdapter(addParticipantAdp);
			} else if (msg.what == 2) {

				contacts_list.setAdapter(new SimpleAdapter(getActivity(),
						getString(R.string.no_contact)));
				contacts_list.setCacheColorHint(Color.WHITE);
				contacts_list.setOnItemClickListener(null);

			} else if (msg.what == 8) {
				try {
					String path = CommonFunctions.getPref(getActivity(),
							AddEditImage.LOCAL_PROFILE_PIC, "");
					if (path != null && path.length() > 0) {
						File image = new File(path);
						if (image.exists()) {

							// imgProfilePic.setImageBitmap(BitmapFactory.decodeFile(image
							// .getAbsolutePath()));
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (msg.what == 10) {
				// r

			}
		};

	};

	@Override
	public void onClick(View v) {

		if (v == btnBack) {
			addParticipantsToServer();

		}
	}

	// Method for contact starts
	public void CreateProgressBar() {

		pd = new ProgressDialog(getActivity());
		pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pd.setMessage(AddParticipants.this.getResources().getString(
				R.string.loading));
		pd.setMax(5000);

		pd.show();

	}

	public Runnable fill = new Runnable() {

		@Override
		public void run() {
			FillFriends();
		}
	};

	String friendImage[];
	ArrayList<SectionHeader> sectionHeaders;
	boolean isSelected[];
	AddParticipantAdp addParticipantAdp = null;

	public void FillFriends() {
		size = 0;
		DataBase db = new DataBase(getActivity());
		try {

			db.open();

			friends = db.fetchAll(DataBase.Friends_table, DataBase.Friends_int,
					"name COLLATE NOCASE ", null, null);

			size = friends.getCount();
			if (size != 0) {
				FriendName = new String[size];
				Friendid = new int[size];
				FriendNumber = new String[size];
				// Groupid = new String[size];
				friendImage = new String[size];
				status = new int[size];
				isSelected = new boolean[size];
				int i = 0;
				sectionHeaders = new ArrayList<SectionHeader>();
				String globaltempChar = "";
				while (friends.moveToNext()) {

					FriendName[i] = friends.getString(2);
					FriendNumber[i] = friends.getString(3);
					status[i] = friends.getInt(6);
					Friendid[i] = friends.getInt(0);
					if (isParticipant(Friendid[i],i)) {
						isSelected[i] = true;

					} else {
						isSelected[i] = false;
					}

					friendImage[i] = friends.getString(4);
					SectionHeader sectionHeader = new SectionHeader();
					String localChar = FriendName[i].substring(0, 1);
					if (i == 0) {
						sectionHeader.setIsShow(true);
						String alphabet = FriendName[i].substring(0, 1);
						sectionHeader.setAlphabet(alphabet.toUpperCase());
						sectionHeaders.add(sectionHeader);
						globaltempChar = alphabet;
					} else {
						if (globaltempChar.toUpperCase().equalsIgnoreCase(
								localChar.toUpperCase())) {
							SectionHeader sectionHeader2 = new SectionHeader();
							sectionHeader2.setAlphabet(null);
							sectionHeader2.setIsShow(false);
							sectionHeaders.add(sectionHeader2);
							globaltempChar = localChar;
						} else {
							SectionHeader sectionHeader2 = new SectionHeader();
							String alphabet = FriendName[i].substring(0, 1);
							sectionHeader2.setAlphabet(alphabet.toUpperCase());
							sectionHeader2.setIsShow(true);

							globaltempChar = localChar;
							sectionHeaders.add(sectionHeader2);
						}
					}

					i++;
				}

				// lvc = this.getListView();

				addParticipantAdp = new AddParticipantAdp(getActivity(),
						FriendName, status, Groupid, FriendNumber, friendImage,
						sectionHeaders, isSelected);
				contacts_list.setAdapter(addParticipantAdp);
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

				contacts_list.setAdapter(new SimpleAdapter(getActivity(),
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

	private boolean isParticipant(int friendId, int position) {
		boolean isPartcipent = false;
		try {

			for (int i = 0; i < conversationMessages.size(); i++) {
				FriendModel friendModel = conversationMessages.get(i);
				try {
					if (Integer.parseInt(friendModel.getFriendId()) == friendId) {
						isPartcipent = true;
						if (friendModel.getFriendStatus().equalsIgnoreCase("5")
								|| friendModel.getFriendStatus()
										.equalsIgnoreCase("6")) {
							try {
								String friendNumber = friendModel
										.getFriendNumber();
								String base64Number = new String(
										Base64.decode(friendNumber.getBytes(),
												Base64.DEFAULT));
								participants.add("-g_" + base64Number + "-");
								participantNames.add(friendModel
										.getFriendName());
								positionList.add(position);

							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							try {
								String friendNumber = friendModel
										.getFriendNumber();
								participants.add("-" + friendNumber + "-");
								participantNames.add(friendModel
										.getFriendName());
								positionList.add(position);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						break;
					}
				} catch (Exception e) {
					e.printStackTrace();

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isPartcipent;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 4) {
			Log.e("", "============= Back Pressed " + keyCode);

			addParticipantsToServer();

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		try {
			Log.e("", "===== The Friend Id " + Friendid[arg2]
					+ " ====   Status " + status[arg2]);
			
			try {
				Log.e("", "============= The Positin List  ==== "+positionList.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (addParticipantAdp != null) {
				if (isSelected[arg2] == true) {
					isSelected[arg2] = false;
					if (status[arg2] == 5 || status[arg2] == 6) {
						if (FriendNumber[arg2] != null) {
							String friendId = "-g_" + FriendNumber[arg2] + "-";
							String participantName = FriendName[arg2];
							participants.remove(friendId);
							participantNames.remove(participantName);
							positionList.remove(Integer.valueOf(arg2));
						}
					} else {
						if (FriendNumber[arg2] != null) {
							String friendId = "-" + FriendNumber[arg2] + "-";
							participants.remove(friendId);
							String participantName = FriendName[arg2];
							participantNames.remove(participantName);
							positionList.remove(Integer.valueOf(arg2));
						}
					}
				} else if (isSelected[arg2] == false) {
					isSelected[arg2] = true;
					if (status[arg2] == 5 || status[arg2] == 6) {
						if (FriendNumber[arg2] != null) {
							String friendId = "-g_" + FriendNumber[arg2] + "-";
							participants.add(friendId);
							String participantName = FriendName[arg2];
							participantNames.add(participantName);
							positionList.add(arg2);
						}
					} else {
						if (FriendNumber[arg2] != null) {
							String friendId = "-" + FriendNumber[arg2] + "-";
							participants.add(friendId);

							String participantName = FriendName[arg2];
							participantNames.add(participantName);
							positionList.add(arg2);
						}
					}

				}
				addParticipantAdp.notifyChange(this, FriendName, status,
						Groupid, FriendNumber, friendImage, sectionHeaders,
						isSelected);
			}

			Log.e("",
					"========== The List of participants "
							+ participants.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onResume() {
		// ActivityStatus.appStatus(this, true);
		ActivityStatus.setActivity(AddParticipants.this);
		handler.sendEmptyMessage(8);
		super.onResume();
	}

	@Override
	public void onPause() {
		ActivityStatus.appStatus(null, false);
		ActivityStatus.setActivity(null);
		super.onPause();
		try {
			if (CustomMenu.isShowing()) {
				CustomMenu.hide();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addParticipantsToServer() {
		try {
			String allParticipants = "";
			if (participants != null && participants.size() > 0) {
				for (int i = 0; i < participants.size(); i++) {
					allParticipants += participants.get(i) + ",";
				}

				if (allParticipants != null && allParticipants.length() > 0
						&& allParticipants.endsWith(",")) {
					allParticipants = allParticipants.substring(0,
							allParticipants.length() - 1);
					Log.e("", "=========== The All Participants are "
							+ allParticipants);
				}

				if (CommonFunctions.isInternetConnected(this)) {
					progressDialog = ProgressDialog.show(getActivity(),
							"Updating Recipients",
							"Please wait while update your information");
					String url = CommonFunctions.DEMO_SERVER_URL
							+ TribeWireConstants.ADD_PARTICIPANTS;
					List<String> nameList = new ArrayList<String>();
					nameList.add(Constants.API_KEY_TAG);
					nameList.add(Constants.REF_ID_TAG);
					nameList.add(Constants.CONV_ID_TAG);
					nameList.add(Constants.SESSION_TAG);
					nameList.add("recipients");
					Log.e("", "========= The Name List is "+nameList.toString());
					List<String> valueList = new ArrayList<String>();
					valueList.add(Constants.API_KEY);
					String userId = CommonFunctions.getPref(this,
							CommonFunctions.REF_ID, "");
					valueList.add("" + userId);
					valueList.add(convId);
					valueList.add(CommonFunctions.getPref(getActivity(),
							Constants.SESSION, ""));
					Log.e("", "========= The Value List is "+valueList.toString());

					// TODO need to add recipients
					valueList.add(Base64.encodeToString(
							allParticipants.getBytes(), Base64.DEFAULT));
					new RestApiCall(getActivity(), url, nameList, valueList)
							.start();

				} else {
					Toast.makeText(getActivity(),
							"Please check your internet connectivity",
							Toast.LENGTH_LONG).show();
				}
			}

			else {
				finish();
			}

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getActivity(),
					"Error while updating , Please try again later",
					Toast.LENGTH_LONG).show();
			dismissProgress();
			finish();
		}

	}

	private void dismissProgress() {
		try {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public IHttpRequest particpantsResponse = new IHttpRequest() {

		@Override
		public void onResponse(String response) {
			Log.e("", "============ The response from server is " + response);

			try {
				JSONObject jsonObject = new JSONObject(response);
				JSONObject jsonObject2 = jsonObject.getJSONObject("response");
				String error = jsonObject2.getString("error");
				if (error.equalsIgnoreCase("false")) {

					updateRecipients();
					prepareNewFriendData(positionList);
					dismissProgress();
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgress();
				Toast.makeText(getActivity(),
						" Error while updating recipient", Toast.LENGTH_LONG)
						.show();
			}

		}

		@Override
		public void onError(String onError) {
			Log.e("", "============ The Error  response from server  "
					+ onError);

			dismissProgress();
		}
	};

	private void updateRecipients() {
		DataBase db = null;
		try {
			String participantNames = "";
			if (this.participantNames != null
					&& this.participantNames.size() > 0) {
				for (int i = 0; i < this.participantNames.size(); i++) {
					participantNames += this.participantNames.get(i) + ",";
				}
			}

			if (participantNames != null && participantNames.length() > 0) {
				participantNames = participantNames.substring(0,
						participantNames.length() - 1);
			}
			Log.e("", "============ The Participant Names are "
					+ participantNames);

			if (participantNames != null && participantNames.length() > 0
					&& convId != null && convId.length() > 0) {
				db = new DataBase(this);
				db.open();
				ContentValues cv = new ContentValues();
				cv.put("receiver", participantNames);
				boolean isUpdate = db.update(DataBase.Receive_table,
						DataBase.Receive_int, "conversation_Id = '" + convId
								+ "'", cv);
				Log.e("", "======== is Receiver Updated " + isUpdate);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}

		}

	}

	public List<FriendModel> prepareNewFriendData(List<Integer> positinoList) {
		FriendModel friendModel = null;
		ArrayList<FriendModel> friendData  = null;
       if (positinoList!=null && positinoList.size()>0) {
		friendData  = new ArrayList<FriendModel>();
	}
		for (int position = 0; position < positinoList.size(); position++) {
			
			try {
				friendModel = new FriendModel();
				friendModel.setFriendId(""+Friendid[positinoList.get(position)]);
				friendModel.setFriendName(FriendName[positinoList.get(position)]);
				friendModel.setFriendNumber(FriendNumber[positinoList.get(position)]);
				friendModel.setFriendStatus(""+status[positinoList.get(position)]);
				friendModel.setImgUrl(friendImage[positinoList.get(position)]);
				friendData.add(friendModel);

			} catch (Exception e) {
				e.printStackTrace();
			}			
		}

		
		 try {
			 
			 Log.e("", "============== The size of the Friend Data is "+friendData.size()+" ========================== ");
			 Intent intent  = new Intent();
			 intent.putExtra("conlistener", friendData);
			  
			  setResult(RESULT_OK, intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return  friendData;

	}

}
