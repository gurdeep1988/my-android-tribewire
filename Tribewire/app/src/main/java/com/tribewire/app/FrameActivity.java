package com.tribewire.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.eyesayapp.Utils.GroupMemberService;
import com.eyesayapp.Utils.InValidResponseException;
import com.eyesayapp.Utils.NoInternetConnectionException;
import com.eyesayapp.Utils.SyncAddressBook;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.Utils.Utils;
import com.eyesayapp.alphabetlist.IndexableListView;
import com.eyesayapp.asyn.ContactSync;
import com.eyesayapp.asyn.IContactResult;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.SignOut;
import com.eyesayapp.model.SectionHeader;

import eyesay.adapter.SimpleAdapter;
import eyesay.parser.FriendsAdapter;

public class FrameActivity extends ListFragment implements OnClickListener,
		OnItemClickListener, IContactResult, TextWatcher, IHttpRequest {
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

	/*
	 * String[] contacts = {"ankur","naveet","sir"}; String[] groups =
	 * {"friend","home","company"};
	 */

	IndexableListView contacts_list;

	// Button contacts_btn;
	// Button group_btn;
	// Button add;

	// varible for grroups
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

	// private Button frame_layout_group, frame_layout_video, frame_layout_msg;
	private LinearLayout show_group_layout;
	private String uniqueGroupId = "";
	private String uniqueGroupName = null;

	private Button btnGoToGroup;
	private Button btnSearch;
	private RelativeLayout rltSearchBar;
	private TextView txtCancelSearch;
	private EditText edtSearchText;

	// Paramters for menu panel
	LinearLayout slidingPanel;
	RelativeLayout main_Layout;
	private int panelWidth;
	boolean isExapnd = false;
	private DisplayMetrics metrics;
	LinearLayout.LayoutParams menuPanelParameters;
	FrameLayout.LayoutParams slidingPanelParameters;
	boolean isExpanded = false;
	private GestureDetector gestureDetector;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	View.OnTouchListener gestureListener;
	private Button slide_menu_icon;
	RelativeLayout conAllConversation;
	RelativeLayout conCompanyFYi;
	RelativeLayout con_Contacts;
	RelativeLayout con_Groups;
	RelativeLayout con_Emergency;
	private TextView txtAllConversation;
	private TextView txtUsername;
	LinearLayout conProfileData;
	ImageView imgProfilePic;
	Button btnBadge;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.group_contact_framelayout, null);
		contacts_list = (IndexableListView) view.findViewById(R.id.list_view);
		contacts_list.setFastScrollEnabled(true);
		contacts_list.setBackgroundColor(Color.WHITE);
		btnSearch = (Button) view.findViewById(R.id.btn_search);
		btnGoToGroup = (Button) view.findViewById(R.id.btn_goto_group);
		rltSearchBar = (RelativeLayout) view.findViewById(R.id.rlt_search_bar);
		txtCancelSearch = (TextView) view.findViewById(R.id.txt_search_cancel);
		edtSearchText = (EditText) view.findViewById(R.id.edt_search_contact);
		edtSearchText.addTextChangedListener(this);
		btnGoToGroup.setOnClickListener(this);
		btnSearch.setOnClickListener(searchListener);
		txtCancelSearch.setOnClickListener(searchListener);
		contacts_list.setOnItemClickListener(click_list);
		contacts_list.setVisibility(View.VISIBLE);
		CreateProgressBar();
		new ContactSync(getActivity(), this).execute();
		startGroupMemberService();
		
		return view;
	}

	private void startGroupMemberService() {
		try {
			Intent intent  = new Intent(getActivity(), GroupMemberService.class);
			getActivity().startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void setFontforMenu() {
		// try {
		// Typeface tf = TribeWireUtil.getArialTypeface(this);
		// TextView txtAllCon = (TextView)
		// findViewById(R.id.txt_all_conversation);
		// TextView txtCompanyFyi = (TextView)
		// findViewById(R.id.txt_company_fyi);
		// TextView txtContact = (TextView)
		// findViewById(R.id.txt_contact_groups);
		// TextView txtEmergency = (TextView) findViewById(R.id.txt_emergency);
		// TextView allConversation = (TextView)
		// findViewById(R.id.txt_static_allconv);
		// TextView txtSignout = (TextView) findViewById(R.id.txt_sign_out);
		// txtSignout.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// //Toast.makeText(FrameActivity.this, "======== Toast Clicked",
		// Toast.LENGTH_SHORT).show();
		// signOut();
		// }
		// });
		// TextView txtStatus = (TextView) findViewById(R.id.txt_status);
		// TextView txtOnline = (TextView) findViewById(R.id.txt_status_online);
		// txtSignout.setTypeface(tf);
		// txtStatus.setTypeface(tf);
		// txtOnline.setTypeface(tf);
		// txtAllCon.setTypeface(tf);
		// txtCompanyFyi.setTypeface(tf);
		// txtContact.setTypeface(tf);
		// txtEmergency.setTypeface(tf);
		// allConversation.setTypeface(TribeWireUtil
		// .getArialBoldTypeface(this));
		//
		// } catch (Exception e) {
		// // TODO: handle exception
		// }

	}

	private void setuserName() {
		// try {
		// String fName = CommonFunctions.getPref(getActivity(),
		// Constants.FIRST_NAME,
		// "");
		// String lName = CommonFunctions.getPref(getActivity(),
		// Constants.LAST_NAME,
		// "");
		// txtUsername.setText(fName + " " + lName);
		// txtUsername.setTypeface(TribeWireUtil.getArialBoldTypeface(this));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	private void setMenuPanel() {
		// slidingPanel = (LinearLayout) findViewById(R.id.slidingpanel);
		// slidingPanel.setVisibility(View.INVISIBLE);
		// main_Layout = (RelativeLayout) findViewById(R.id.mainLayout);
		// slide_menu_icon = (Button) findViewById(R.id.btn_menu);
		// conAllConversation = (RelativeLayout)
		// findViewById(R.id.con_allconversation);
		// conCompanyFYi = (RelativeLayout) findViewById(R.id.con_company_fyi);
		// con_Contacts = (RelativeLayout) findViewById(R.id.con_contacts);
		// con_Groups = (RelativeLayout) findViewById(R.id.con_groups);
		// con_Emergency = (RelativeLayout) findViewById(R.id.con_emergency);
		// txtAllConversation = (TextView)
		// findViewById(R.id.txt_all_conversation);
		// txtUsername = (TextView) findViewById(R.id.txt_username);
		// slide_menu_icon.setOnClickListener(this);
		// conAllConversation.setOnTouchListener(onSwipeAllConversation);
		// conCompanyFYi.setOnTouchListener(onSwipeCompanyFYi);
		// con_Contacts.setOnTouchListener(onSwipeContact);
		//
		// con_Emergency.setOnTouchListener(onSwipeEmergency);
		// initiateMenuPanel();
	}

	/*
	 * OnSwipeTouchListener onSwipeAllConversation = new OnSwipeTouchListener()
	 * {
	 * 
	 * @Override public void onSwipeRight() { if (isExpanded) {
	 * hideMenuAnother(); isExpanded = false; } Log.e("",
	 * "========= Swipe right to left =========="); super.onSwipeRight();
	 * 
	 * }
	 * 
	 * @Override public void onClick() { Log.e("", "  Swipe Confirm 100 %"); //
	 * finish(); if (isExpanded) { hideMenuAnother(); isExpanded = false; } new
	 * Handler().postDelayed(new Runnable() {
	 * 
	 * @Override public void run() { Intent intent = new
	 * Intent(FrameActivity.this, TenFourConversations.class);
	 * startActivity(intent); finish(); } }, 650);
	 * 
	 * 
	 * super.onClick(); }
	 * 
	 * @Override public void onSwipeLeft() { if (isExpanded) {
	 * hideMenuAnother(); isExpanded = false; } Log.e("",
	 * "========= Swipe right to left ==========");
	 * 
	 * super.onSwipeLeft(); } };
	 * 
	 * OnSwipeTouchListener onSwipeCompanyFYi = new OnSwipeTouchListener() {
	 * 
	 * @Override public void onSwipeRight() { if (isExpanded) {
	 * hideMenuAnother(); isExpanded = false; } Log.e("",
	 * "========= Swipe right to left =========="); super.onSwipeRight();
	 * 
	 * }
	 * 
	 * @Override public void onClick() { Log.e("", "  Swipe Confirm 100 %"); if
	 * (isExpanded) { hideMenuAnother(); isExpanded = false; } new
	 * Handler().postDelayed(new Runnable() {
	 * 
	 * @Override public void run() { Intent intent = new
	 * Intent(FrameActivity.this, CompanyFYI.class); startActivity(intent);
	 * 
	 * } }, 650);
	 * 
	 * super.onClick(); }
	 * 
	 * @Override public void onSwipeLeft() { if (isExpanded) {
	 * hideMenuAnother(); isExpanded = false; } Log.e("",
	 * "========= Swipe right to left ==========");
	 * 
	 * super.onSwipeLeft(); }
	 * 
	 * };
	 * 
	 * OnSwipeTouchListener onSwipeContact = new OnSwipeTouchListener() {
	 * 
	 * @Override public void onSwipeRight() { if (isExpanded) {
	 * hideMenuAnother(); isExpanded = false; } Log.e("",
	 * "========= Swipe right to left =========="); super.onSwipeRight();
	 * 
	 * }
	 * 
	 * @Override public void onClick() { Log.e("", "  Swipe Confirm 100 %"); if
	 * (isExpanded) { hideMenuAnother(); isExpanded = false; }
	 * 
	 * 
	 * super.onClick(); }
	 * 
	 * @Override public void onSwipeLeft() { if (isExpanded) {
	 * hideMenuAnother(); isExpanded = false; } Log.e("",
	 * "========= Swipe right to left ==========");
	 * 
	 * super.onSwipeLeft(); }
	 * 
	 * };
	 * 
	 * OnSwipeTouchListener onSwipeProfile = new OnSwipeTouchListener() {
	 * 
	 * @Override public void onSwipeRight() { if (isExpanded) {
	 * hideMenuAnother(); isExpanded = false; } Log.e("",
	 * "========= Swipe right to left =========="); super.onSwipeRight();
	 * 
	 * }
	 * 
	 * @Override public void onClick() { Log.e("", "  Swipe Confirm 100 %"); if
	 * (isExpanded) { hideMenuAnother(); isExpanded = false; } new
	 * Handler().postDelayed(new Runnable() {
	 * 
	 * @Override public void run() { Intent intent = new
	 * Intent(FrameActivity.this, AddEditImage.class); startActivity(intent);
	 * 
	 * } }, 650);
	 * 
	 * super.onClick(); }
	 * 
	 * @Override public void onSwipeLeft() { if (isExpanded) {
	 * hideMenuAnother(); isExpanded = false; } Log.e("",
	 * "========= Swipe right to left ==========");
	 * 
	 * super.onSwipeLeft(); } };
	 * 
	 * OnSwipeTouchListener onSwipeEmergency = new OnSwipeTouchListener() {
	 * 
	 * @Override public void onSwipeRight() { if (isExpanded) {
	 * hideMenuAnother(); isExpanded = false; } Log.e("",
	 * "========= Swipe right to left =========="); super.onSwipeRight();
	 * 
	 * }
	 * 
	 * @Override public void onClick() { Log.e("", "  Swipe Confirm 100 %"); if
	 * (isExpanded) { hideMenuAnother(); isExpanded = false; }
	 * 
	 * new Handler().postDelayed(new Runnable() {
	 * 
	 * @Override public void run() { Intent intent = new
	 * Intent(FrameActivity.this, Emergency.class); startActivity(intent);
	 * finish(); } }, 650);
	 * 
	 * super.onClick(); }
	 * 
	 * @Override public void onSwipeLeft() { if (isExpanded) {
	 * hideMenuAnother(); isExpanded = false; } Log.e("",
	 * "========= Swipe right to left ==========");
	 * 
	 * super.onSwipeLeft(); } };
	 */

	private void initiateMenuPanel() {
		try {
			metrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay()
					.getMetrics(metrics);

			Log.e("", "Height is ========== " + metrics.widthPixels);

			panelWidth = (int) ((metrics.widthPixels) * 0.83);
			int slidingpanelMargin = (int) ((metrics.widthPixels) * 0.17);
			// Here Main_Layout refer to as Sliding panel
			slidingPanelParameters = (FrameLayout.LayoutParams) main_Layout
					.getLayoutParams();
			slidingPanelParameters.width = metrics.widthPixels;
			main_Layout.setLayoutParams(slidingPanelParameters);

			menuPanelParameters = (LinearLayout.LayoutParams) slidingPanel
					.getLayoutParams();
			menuPanelParameters.width = metrics.widthPixels;
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) slidingPanel
					.getLayoutParams();
			params.rightMargin = slidingpanelMargin;
			Log.e("", "=========== The Panel width is=================== "
					+ panelWidth);
			slidingPanel.setLayoutParams(params);
			slidingPanel.requestLayout();

			gestureDetector = new GestureDetector(new MyGestureDetector());
			gestureListener = new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if (gestureDetector.onTouchEvent(event)) {
						return true;
					}
					return false;
				}
			};
			slidingPanel.setOnTouchListener(gestureListener);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					Log.e("", "===Swipe Right to left ");
					if (isExpanded) {
						hideMenuAnother();
						isExpanded = false;
					}

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					if (isExpanded) {
						hideMenuAnother();
						isExpanded = false;
					}

					Log.e("", "===Swipe Left to right ");

				}
			} catch (Exception e) {

			}
			return false;
		}
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// if (gestureDetector.onTouchEvent(event))
	// return true;
	// else
	// return false;
	// }

	private void showMenuAnother() {
		slidingPanel.setVisibility(View.VISIBLE);

		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_right_in);

		LayoutParams params = (LayoutParams) main_Layout.getLayoutParams();
		// params.rightMargin = panelWidth;
		// params.gravity = Gravity.RIGHT;

		params.leftMargin = panelWidth;
		params.gravity = Gravity.LEFT;

		// main_Layout.clearAnimation();
		main_Layout.setLayoutParams(params);
		main_Layout.requestLayout();
		main_Layout.setAnimation(animation);
		isExapnd = true;

	}

	private void hideMenuAnother() {

		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_left_in);

		LayoutParams params = (LayoutParams) main_Layout.getLayoutParams();
		params.leftMargin = 0;
		// params.leftMargin = 0;

		// main_Layout.clearAnimation();
		main_Layout.setLayoutParams(params);
		main_Layout.requestLayout();
		main_Layout.setAnimation(animation);
		isExapnd = false;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				slidingPanel.setVisibility(View.INVISIBLE);

			}
		}, 650);
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
				contacts_list
						.setAdapter(new FriendsAdapter(getActivity(),
								FriendName, status, Groupid, FriendNumber,
								friendImage,sectionHeaders));
			} else if (msg.what == 2) {

				contacts_list.setAdapter(new SimpleAdapter(getActivity(),
						getString(R.string.no_contact)));
				contacts_list.setCacheColorHint(Color.WHITE);
				contacts_list.setOnItemClickListener(null);

			} else if (msg.what == 8) {
				try {
					FillFriends();
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
			
			else if (msg.what==10) {
				//r
				
				 new ContactSync(getActivity(), FrameActivity.this).execute();
			}
		};

	};

	public void searchFriends(CharSequence searchText) {

		size = 0;
		DataBase db = new DataBase(getActivity());
		Cursor friends = null;
		try {

			db.open();
			Log.e("", "========= Search Text Length " + searchText.length());
			if (searchText.length() > 0) {
				// friends = db.fetchContact(DataBase.Friends_table,
				// DataBase.Friends_int, ""
				// + ""+Friend.NAME+"" + " LIKE '" + searchText + "%'", null);

				// friends = db.fetchContact(DataBase.Friends_table,
				// DataBase.Friends_int, searchText.toString());

				friends = db.fetchContactGroup(DataBase.Friends_table,
						DataBase.Friends_int, " name like '" + searchText
								+ "%'", null, null);

			} else {
				friends = db.fetchAll(DataBase.Friends_table,
						DataBase.Friends_int, "name  COLLATE NOCASE", null,
						null);
			}

			size = friends.getCount();
			if (size != 0) {
				FriendName = new String[size];
				Friendid = new int[size];
				FriendNumber = new String[size];
				// Groupid = new String[size];
				friendImage = new String[size];
				status = new int[size];

				int i = 0;
				sectionHeaders = new ArrayList<SectionHeader>();
				String globaltempChar = "";
				while (friends.moveToNext()) {

					FriendName[i] = friends.getString(2);
					FriendNumber[i] = friends.getString(3);
					// Groupid[i] = friends.getString(5);
					status[i] = friends.getInt(6);
					Friendid[i] = friends.getInt(0);
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
					i++;
				}

				handler.sendEmptyMessage(1);
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

				handler.sendEmptyMessage(2);
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

		searchFriends(searchText);

	}

	@Override
	public void onContactError(String onError) {

		Log.e("",
				"======================= Error while refreshing contact ===============");
	}

	@Override
	public void onContactResponse(String response) {

		try {
			SyncAddressBook.GSSyncContact(getActivity(), response);
			h.post(fill);
			Log.e("",
					"======================= **********************************===============");
			//FillFriends();
              //h.sendEmptyMessage(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == btnGoToGroup) {
			Intent intent = new Intent(getActivity(), AllGroups.class);
			startActivityForResult(intent, 45);

		}
		if (v == slide_menu_icon) {
			Log.e("", "======== If expanded " + isExpanded);
			if (!isExpanded) {
				isExpanded = true;

				showMenuAnother();
			} else {
				isExpanded = false;

				hideMenuAnother();

			}

		}

		// switch (v.getId()) {

		/*
		 * case R.id.btnAddFriend:
		 * 
		 * switch (which_list) { case 1: // Toast.makeText(FrameActivity.this,
		 * "YOU ARE IN CONTACT", // Toast.LENGTH_LONG).show(); // contacts_btn
		 * // .setBackgroundResource(R.drawable.button_contact_topbar_inactive);
		 * // add.setBackgroundResource(R.drawable.addgroupbutton); //
		 * contacts_btn //
		 * .setBackgroundResource(R.drawable.button_contact_topbar_active); //
		 * add.setBackgroundResource(R.drawable.addgroupbutton);
		 * 
		 * 
		 * break;
		 * 
		 * case 2: // Toast.makeText(FrameActivity.this, "YOU ARE IN GROUP", //
		 * Toast.LENGTH_LONG).show(); group_btn
		 * .setBackgroundResource(R.drawable.button_groups_inactive);
		 * add.setBackgroundResource(R.drawable.addgroupbutton);
		 * 
		 * if (contacts_list != null) { if (contacts_list.getVisibility() ==
		 * View.VISIBLE) { contacts_list.setVisibility(View.INVISIBLE); } }
		 * 
		 * if (groups_list != null) { if (groups_list.getVisibility() ==
		 * View.VISIBLE) { groups_list.setVisibility(View.INVISIBLE); } } if
		 * (group_lvc != null) { if (group_lvc.getVisibility() == View.VISIBLE)
		 * { group_lvc.setVisibility(View.INVISIBLE); } } if
		 * (group_member_list.getVisibility()==View.VISIBLE) { Intent intent =
		 * new Intent(FrameActivity.this,GroupMembers.class);
		 * intent.putExtra("groupid", ""+uniqueGroupId);
		 * intent.putExtra("groupname", uniqueGroupName);
		 * startActivityForResult(intent,231); } else { Animation swipeLeft =
		 * AnimationUtils.loadAnimation(this, R.anim.swipeleft);
		 * show_group_layout.startAnimation(swipeLeft);
		 * show_group_layout.setVisibility(View.VISIBLE); createNewGroup(); }
		 * //createGroup(); break; } break;
		 * 
		 * // TODO COMMENTED ADDED BY GURDEEP ON 12th October
		 * 
		 * case R.id.frame_layout_video:
		 * 
		 * 
		 * 
		 * 
		 * // //
		 * frame_layout_group.setBackgroundResource(R.drawable.contact_nav_inactive
		 * ); //
		 * frame_layout_msg.setBackgroundResource(R.drawable.messages_nav_inactive
		 * ); finish(); break;
		 * 
		 * case R.id.frame_layout_msg: //
		 * frame_layout_msg.setBackgroundResource(
		 * R.drawable.messages_nav_active); // //
		 * frame_layout_group.setBackgroundResource
		 * (R.drawable.contact_nav_inactive); //
		 * Toast.makeText(this,"=======Message", Toast.LENGTH_SHORT).show();
		 * Intent go_to_msg = new Intent(getApplicationContext(),
		 * TenFourConversations.class); startActivityForResult(go_to_msg,123);
		 * break;
		 * 
		 * 
		 * }
		 */

	}

	// Method for contact starts
	public void CreateProgressBar() {

		pd = new ProgressDialog(getActivity());
		pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pd.setMessage(FrameActivity.this.getResources().getString(
				R.string.loading));
		pd.setMax(5000);

		pd.show();

	}

	public void SynchronizeContact() {
		new Thread() {
			public void run() {
				try {
					sleep(200);
					// SyncAddressBook.AddPhoneBook(FrameActivity.this);
					// SyncAddressBook.GSSyncContact(FrameActivity.this);
					// sleep(2000);
					// if (getActiveFriends()) {
					h.post(fill);
					// }
				} catch (Exception e) {
					Debugger.debugE("Sync with address book");
				} finally {
					// pd.dismiss();

				}

			}
		}.start();

	}

	public boolean getActiveFriends() throws NoInternetConnectionException,
			InValidResponseException {

		return false;
	}

	Handler h = new Handler();
//		public void handleMessage(android.os.Message msg) {
//			FillFriends();
//		};
	
	public Runnable fill = new Runnable() {

		@Override
		public void run() {
			FillFriends();
		}
	};

	String friendImage[];
	ArrayList<SectionHeader> sectionHeaders;

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

				int i = 0;
				sectionHeaders = new ArrayList<SectionHeader>();
				String globaltempChar = "";
				while (friends.moveToNext()) {

					FriendName[i] = friends.getString(2);
					FriendNumber[i] = friends.getString(3);
					// Groupid[i] = friends.getString(5);
					status[i] = friends.getInt(6);
					Friendid[i] = friends.getInt(0);
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

					i++;
				}

				// lvc = this.getListView();
				contacts_list
						.setAdapter(new FriendsAdapter(getActivity(),
								FriendName, status, Groupid, FriendNumber,
								friendImage,sectionHeaders));
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

	String selectedCountryCode = "1";

	public OnItemClickListener click_list = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			position_contact = arg2;
			try {
				Log.e("", "======== Status " + status[arg2]);

				// if (groupType[position] == 0) {
				// Intent intent = new Intent(AllGroups.this,
				// GroupMembers.class);
				//
				// intent.putExtra("groupid", "" + Groupid[position]);
				// intent.putExtra("groupname", "" + GroupName[position]);
				// startActivityForResult(intent, 45);
				// }

				if (status[arg2] == 5 || status[arg2] == 6) {
					DataBase db = null;
					try {
						db = new DataBase(getActivity());
						db.open();
						int count = db
								.getCount(DataBase.Group_table,
										DataBase.Group_int, "sr_no ='"
												+ FriendNumber[arg2]
												+ "' and type = 0");
						if (count > 0) {
							Intent intent = new Intent(getActivity(),
									GroupMembers.class);

							intent.putExtra("groupid", "" + FriendNumber[arg2]);
							intent.putExtra("groupname", "" + FriendName[arg2]);
							intent.putExtra("isadmin", false);
							startActivityForResult(intent, 45);
						}

						else {
							// Toast.makeText(FrameActivity.this,
							// "You can not edit admin group",
							// Toast.LENGTH_LONG).show();
							Intent intent = new Intent(getActivity(),
									GroupMembers.class);

							intent.putExtra("groupid", "" + FriendNumber[arg2]);
							intent.putExtra("groupname", "" + FriendName[arg2]);
							intent.putExtra("isadmin", true);
							startActivityForResult(intent, 45);

						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						db.close();
					}

				}
				// TODO Commented has to take decision acccording to future
				// response
				// if (status!=null && status.length>0) {
				// int groupStatus = status[arg2] ;
				// if (groupStatus==5) {
				// Intent intent = new
				// Intent(FrameActivity.this,GroupMembers.class);
				// intent.putExtra("groupid", ""+FriendNumber[arg2]);
				// intent.putExtra("groupname", ""+FriendName[arg2]);
				// startActivityForResult(intent,231);
				// }
				// }
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			/*
			 * Debugger.debugE("Position of selected friend on click" +
			 * position_contact+" and Status Array Length "+status.length); if
			 * (status[position_contact] == 1 || status[position_contact] == 0)
			 * { friendsMenu = new String[FrameActivity.this.getResources()
			 * .getStringArray(R.array.friendsmenu).length]; friendsMenu =
			 * FrameActivity.this.getResources().getStringArray(
			 * R.array.friendsmenu);
			 * 
			 * friend = Friendid[position_contact] + ":" + "friend" + ";" +
			 * FriendName[position_contact] + FriendNumber[position_contact];
			 * Cont_Name = FriendName[position_contact]; Cont_No =
			 * FriendNumber[position_contact]; showActionSheet(Cont_No,
			 * Cont_Name,Friendid[position_contact]);
			 * 
			 * } else if (status[position_contact] == -1) {
			 * 
			 * new AlertDialog.Builder(FrameActivity.this).setItems( new
			 * String[] { "UnBlock Contact" }, new
			 * DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * { switch (which) { case 0: { // UnBlockContact(); break; } case
			 * 1: { DeleteContact();
			 * 
			 * }
			 * 
			 * default: break; }
			 * 
			 * } }).show();
			 * 
			 * }
			 */

		}
	};

	public void AddTOGroup(final int friendId) {
		final DataBase db = new DataBase(getActivity());

		try {
			db.open();
			c = db.fetchAll(DataBase.Group_table, DataBase.Group_int,
					"upper(groupname)");
			selectedGroup = -1;
			// Add to group if exist.
			if (c.getCount() > 0) {
				Vector<ContentValues> cvector = new Vector<ContentValues>();
				while (c.moveToNext()) {
					// if (!Groupid[position_contact].contains("" +
					// c.getInt(0))) {
					ContentValues cv = new ContentValues();
					cv.put("groupid", c.getInt(0));
					cv.put("groupname", c.getString(1));
					cvector.add(cv);
					// }
				}
				// c.close();
				if (cvector.size() > 0) {
					GroupName = new String[cvector.size()];
					GMid = new int[cvector.size()];
					for (int i = 0; i < cvector.size(); i++) {
						GroupName[i] = cvector.get(i).get("groupname")
								.toString();
						GMid[i] = Integer.parseInt(cvector.get(i)
								.get("groupid").toString());
					}

					new AlertDialog.Builder(getActivity())
							.setTitle("Group Name")
							.setSingleChoiceItems(GroupName, selectedGroup,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											selectedGroup = which;

											Debugger.debugE("Selected group id"
													+ GMid[selectedGroup]
													+ " and position_contact "
													+ selectedGroup);

										}
									})
							.setPositiveButton(
									this.getResources().getString(R.string.ok),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (selectedGroup == -1) {
												Toast.makeText(
														getActivity(),
														"For adding contact to group, you must need to select a group",
														1).show();
												return;
											}

											try {
												DataBase db1 = new DataBase(
														getActivity());
												try {
													db1.open();
													Log.e("",
															"group id"
																	+ GMid[selectedGroup]
																	+ "Postion "
																	+ selectedGroup
																	+ " Friend Id "
																	+ friendId);
													int count = db1
															.getCount(
																	DataBase.Group_Member_table,
																	DataBase.Group_member_int,
																	"group_id="
																			+ GMid[selectedGroup]
																			+ " and friend_id="
																			+ friendId);
													if (count > 0) {
														CommonFunctions
																.showAlert(
																		getActivity(),
																		"This friend is already a member of this group, Please select another group",
																		"Already Exist");
													} else {
														ContentValues values = new ContentValues();
														values.put(
																"group_id",
																GMid[selectedGroup]);
														values.put("friend_id",
																friendId);
														db1.insert_content(
																DataBase.Group_Member_table,
																DataBase.Group_member_int,
																values);
														Toast.makeText(
																getActivity(),
																"Memeber added to Group Successfully",
																Toast.LENGTH_SHORT)
																.show();
													}

												} catch (Exception e) {
													db1.close();
													e.printStackTrace();
												} finally {
													db1.close();
												}

											} catch (Exception e) {
												e.printStackTrace();
											}

										}
									}).setNegativeButton(R.string.cancel, null)
							.show();
				} else {
					// CommonFunctions
					// .showAlert(
					// FrameActivity.this,
					// "Selected Contact is already exist in all the group",
					// (!CommonFunctions
					// .isFullVersion(getBaseContext())) ?
					// getString(R.string.app_name)
					// : getString(R.string.app_name_pre));
				}
			} else {
				CommonFunctions.showAlert(getActivity(),
						"Please first create group in order to add", "");

			}
		}

		catch (Exception e) {
			c.close();
			db.close();
		} finally {
			// FillFriends();
			c.close();
			db.close();
		}

	}

	public void DeleteContact() {

		new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.delete))
				.setMessage("Are you sure to delete this contact ?")
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								DataBase db = new DataBase(getActivity());
								try {
									db.open();
									db.delete(DataBase.Friends_table,
											DataBase.Friends_int,
											Friendid[position_contact]);
									db.close();
									FillFriends();
								} catch (Exception e) {
									// TODO: handle exception
									db.close();
								}
							}

						}).setNegativeButton(R.string.no, null).create().show();

	}

	// Method for contact ends

	// Method for group starts

	public AlertDialog showOptionDialog(final int Group_position) {
		String arr[] = null;
		if (TotalActiveFriend[Group_position] > 0) {
			arr = new String[] { "Edit Group", "Delete Group", "Send Message" };
		} else {
			arr = new String[] { "Edit Group", "Delete Group" };
		}

		// final int id = Integer.parseInt(position);

		return new AlertDialog.Builder(getActivity())// .setTitle("Select Option")
				.setItems(arr, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						switch (which) {

						case 0: {
							EditGroupDetail();
							break;
						}
						case 1: {
							DeleteGroup();
							break;
						}
						case 2: {
							if (totalMember[Group_position] > 0) {
								Debugger.debugE("..group id"
										+ Groupid[Group_position]);
								// Intent i = new Intent(FrameActivity.this,
								// TenFourTab.class);
								// i.putExtra("tab", 2);
								// i.putExtra("group", Groupid[Group_position]
								// + ":group;" + GroupName[Group_position]);
								// startActivity(i);
								// finish();
							} else {
								CommonFunctions
										.showAlert(
												getActivity(),
												FrameActivity.this
														.getResources()
														.getString(
																R.string.no_group_memeber),
												FrameActivity.this
														.getResources()
														.getString(
																R.string.warning));
							}
							break;
						}

						default:
							break;
						}
					}

				}).create();
	}

	public void EditGroupDetail() {
		/*
		 * final Dialog a = new Dialog(FrameActivity.this);
		 * a.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		 * a.setContentView(R.layout.add_group); ((TextView)
		 * a.findViewById(R.id.title)).setText("Edit group");
		 * CommonFunctions.setFont(getBaseContext(), ((TextView)
		 * a.findViewById(R.id.title)), true, false); ;
		 * a.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
		 * LayoutParams.WRAP_CONTENT); Window w = a.getWindow(); // txtLable =
		 * (TextView) w.findViewById(R.id.name); //
		 * txtLable.setText("Group Name"); txtnumber = (EditText)
		 * w.findViewById(R.id.txtname);
		 * CommonFunctions.setFont(getBaseContext(), txtnumber, false, false);
		 * txtnumber.setHint("Enter Group Name");
		 * txtnumber.setText(GroupName[position_gr]); btnok = (Button)
		 * w.findViewById(R.id.btn_submit_reg);
		 * btnok.setBackgroundResource(R.drawable.btn_save); //
		 * btnok.setText("Edit"); btnok.setOnClickListener(new OnClickListener()
		 * {
		 * 
		 * public void onClick(View v) { if (txtnumber.length() != 0) { String
		 * groupname = txtnumber.getText().toString().trim(); if (!groupname
		 * .matches("^[a-zA-Z0-9][a-zA-Z0-9 ]*[a-zA-Z0-9]$")) {
		 * CommonFunctions.showAlert( FrameActivity.this,
		 * "Enter a Valid Group Name",
		 * FrameActivity.this.getResources().getString( R.string.warning));
		 * return; } DataBase db = new DataBase(FrameActivity.this); db.open();
		 * if (!checkGroupName("Edit","")) { CommonFunctions .showAlert(
		 * FrameActivity.this, FrameActivity.this .getResources()
		 * .getString(R.string.group_exist), (!CommonFunctions
		 * .isFullVersion(getBaseContext())) ? getString(R.string.app_name) :
		 * getString(R.string.app_name_pre)); db.close(); return; }
		 * db.update(DataBase.Group_table, DataBase.Group_int,
		 * Integer.parseInt(Groupid[position_gr]), 1,
		 * txtnumber.getText().toString()); db.close();
		 * Toast.makeText(FrameActivity.this, "Group Edited",
		 * Toast.LENGTH_LONG).show(); a.dismiss(); FillGroup(); } else {
		 * CommonFunctions .showAlert(FrameActivity.this,
		 * FrameActivity.this.getResources() .getString(R.string.All_fields),
		 * FrameActivity.this.getResources() .getString(R.string.warning)); } //
		 * contacts_btn //
		 * .setBackgroundResource(R.drawable.button_contact_topbar_inactive); //
		 * group_btn // .setBackgroundResource(R.drawable.button_groups_active);
		 * // add.setBackgroundResource(R.drawable.button_addedit_inactive);
		 * 
		 * }
		 * 
		 * }); btncancel = (Button) w.findViewById(R.id.btn_exit_reg);
		 * btncancel.setOnClickListener(new OnClickListener() {
		 * 
		 * public void onClick(View v) {
		 * 
		 * a.dismiss(); // contacts_btn //
		 * .setBackgroundResource(R.drawable.button_contact_topbar_inactive); //
		 * group_btn // .setBackgroundResource(R.drawable.button_groups_active);
		 * // add.setBackgroundResource(R.drawable.button_addedit_inactive); }
		 * }); a.show();
		 */

	}

	public void DeleteGroup() {
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.delete)
				.setMessage("Do you want to delete the Group?")
				.setPositiveButton(this.getResources().getString(R.string.yes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								DataBase db = new DataBase(getActivity());
								db.open();
								Debugger.debugE("Group id "
										+ Groupid[position_gr]);
								db.delete(DataBase.Group_table,
										DataBase.Group_int,
										Integer.parseInt(Groupid[position_gr]));
								ContentValues cv = new ContentValues();

								Debugger.debugE("Group id "
										+ Groupid[position_gr]);

								Cursor count = db.fetchAll(
										DataBase.Friends_table,
										DataBase.Friends_int, "name",
										"groupid like '%"
												+ Groupid[position_gr] + "%'");
								Debugger.debugE("count records "
										+ count.getCount());

								if (count.getCount() != 0) {
									count.moveToFirst();
									do {
										Debugger.debugE("" + count.getString(5));
										String groupid = count.getString(5);

										groupid = groupid.replace(","
												+ Groupid[position_gr], "");
										cv.put("groupid", groupid);
										db.update(DataBase.Friends_table,
												DataBase.Friends_int,
												count.getInt(0), cv);
										Debugger.debugE("replace .." + groupid);
									} while (count.moveToNext());
								}
								count.close();

								db.close();
								Toast.makeText(getActivity(), "Group Deleted",
										Toast.LENGTH_LONG).show();
								// FillGroup();
							}
						})
				.setNegativeButton(this.getResources().getString(R.string.no),
						null).show();
	}

	void createGroup() {
		/*
		 * final Dialog a = new Dialog(FrameActivity.this);
		 * a.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		 * a.setContentView(R.layout.add_group); ((TextView)
		 * a.findViewById(R.id.title)).setText("Create group");
		 * CommonFunctions.setFont(getBaseContext(), ((TextView)
		 * a.findViewById(R.id.title)), true, false);
		 * a.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
		 * LayoutParams.WRAP_CONTENT); Window w = a.getWindow(); // txtLable =
		 * (TextView) w.findViewById(R.id.name); //
		 * txtLable.setText("Group Name"); txtnumber = (EditText)
		 * w.findViewById(R.id.txtname);
		 * CommonFunctions.setFont(getBaseContext(), txtnumber, false, false); ;
		 * txtnumber.setHint("Enter Group Name"); btnok = (Button)
		 * w.findViewById(R.id.btn_submit_reg); btnok.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * public void onClick(View v) {
		 * 
		 * String groupname = txtnumber.getText().toString().trim(); if
		 * (groupname.length() != 0 && !groupname.toString().trim().equals("")
		 * && !groupname.toString().trim().equals(" ")) {
		 * 
		 * if (!groupname .matches("^[a-zA-Z0-9][a-zA-Z0-9 ]*[a-zA-Z0-9]$")) {
		 * CommonFunctions.showAlert( FrameActivity.this,
		 * "Enter a Valid Group Name",
		 * FrameActivity.this.getResources().getString( R.string.warning));
		 * return; } DataBase db = new DataBase(FrameActivity.this); db.open();
		 * if (!checkGroupName("Add", groupname)) { CommonFunctions .showAlert(
		 * FrameActivity.this, FrameActivity.this .getResources()
		 * .getString(R.string.group_exist), (!CommonFunctions
		 * .isFullVersion(getBaseContext())) ? getString(R.string.app_name) :
		 * getString(R.string.app_name_pre)); db.close(); return; }
		 * db.insert(DataBase.Group_table, DataBase.Group_int, new String[] {
		 * groupname }); db.close(); Toast.makeText(FrameActivity.this,
		 * "Group Created", Toast.LENGTH_LONG).show(); a.dismiss(); FillGroup();
		 * } else { CommonFunctions.showAlert( FrameActivity.this,
		 * "Please enter proper name to create group",
		 * FrameActivity.this.getResources().getString( R.string.warning)); } //
		 * contacts_btn //
		 * .setBackgroundResource(R.drawable.button_contact_topbar_inactive); //
		 * group_btn // .setBackgroundResource(R.drawable.button_groups_active);
		 * // add.setBackgroundResource(R.drawable.button_addedit_inactive);
		 * 
		 * } }); btncancel = (Button) w.findViewById(R.id.btn_exit_reg);
		 * btncancel.setOnClickListener(new OnClickListener() {
		 * 
		 * public void onClick(View v) { a.dismiss(); // contacts_btn //
		 * .setBackgroundResource(R.drawable.button_contact_topbar_inactive); //
		 * group_btn // .setBackgroundResource(R.drawable.button_groups_active);
		 * // add.setBackgroundResource(R.drawable.button_addedit_inactive); }
		 * }); a.show();
		 */

	}

	public void FillGroupMembers() {
		/*
		 * DataBase db = new DataBase(FrameActivity.this); try { db.open();
		 * TextView gName = (TextView) findViewById(R.id.groupName);
		 * gName.setText("Group : " +
		 * this.getIntent().getExtras().getString("name"));
		 * CommonFunctions.setFont(getBaseContext(), gName, true, false);
		 * Debugger.debugE("Group id + " + GMgroupid); Cursor contact =
		 * db.fetchAll(DataBase.Friends_table, DataBase.Friends_int, "name",
		 * "groupid like '%" + GMgroupid + "%'"); Activecount = 0; if
		 * (contact.getCount() > 0) { GMcount = contact.getCount(); GMid_Str =
		 * new String[contact.getCount()]; GMnames = new
		 * String[contact.getCount()]; // GMstatus =new int[contact.getCount()];
		 * String[] GMphonenumber = new String[contact.getCount()]; GMstatus =
		 * new int[contact.getCount()]; GMGroup = new
		 * String[contact.getCount()]; int i = 0; while (contact.moveToNext()) {
		 * GMid_Str[i] = "" + contact.getInt(0); GMnames[i] =
		 * contact.getString(2); GMphonenumber[i] = contact.getString(3);
		 * GMstatus[i] = contact.getInt(6); GMGroup[i] = contact.getString(5);
		 * if (contact.getInt(Friend.INT_STATUS) != -1) { Activecount++; } i++;
		 * }
		 * 
		 * group_lvc = this.getListView(); group_lvc.setAdapter(new
		 * FriendsAdapter(FrameActivity.this, GMnames, GMstatus,
		 * GMid_Str,GMphonenumber,null));
		 * group_lvc.setCacheColorHint(Color.TRANSPARENT);
		 * group_lvc.setOnItemClickListener(this);
		 * 
		 * } else { GMcount = 0; group_lvc = this.getListView();
		 * 
		 * group_lvc.setAdapter(new SimpleAdapter(this,
		 * getString(R.string.no_group_memeber)));
		 * group_lvc.setOnCreateContextMenuListener(null);
		 * group_lvc.setCacheColorHint(Color.TRANSPARENT);
		 * group_lvc.setOnItemClickListener(null); } contact.close(); } catch
		 * (Exception e) { Debugger.debugE("in group membres");
		 * 
		 * } finally { db.close(); pd.dismiss(); }
		 */
	}

	private void showGroupMembers(String groupId, String groupName) {
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		final int gid = arg2;
		position_gr = arg2;
		Log.e("", "===========GOD HERE IS " + gid + "and ====" + Groupid[gid]);
		if (MODE == 0) {
			uniqueGroupId = Groupid[gid];
			uniqueGroupName = GroupName[gid];
			showGroupMembers(Groupid[gid], GroupName[gid]);
		}
	}

	// Method for group ends
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		try {
			// frame_layout_group
			// .setBackgroundResource(R.drawable.contact_nav_active);
			// frame_layout_msg
			// .setBackgroundResource(R.drawable.messages_nav_inactive);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (requestCode == 123 && resultCode == Activity.RESULT_FIRST_USER) {
			int gid = data.getIntExtra("groupid", 0);
			if (gid > 0) {
				String name = "";
				try {
					DataBase db = new DataBase(getActivity());
					db.open();
					Cursor c = db.fetch(DataBase.Group_table,
							DataBase.Group_int, (long) gid);
					c.moveToFirst();
					name = c.getString(1);
					c.close();
					db.close();

					// FillGroup();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					// if (group_lvc!=null) {
					// group_lvc.removeAllViews();
					// FillGroupMembers();
					// FillFriends();
					// FillGroup();
					// }
				} catch (Exception e) {
					// TODO: handle exception
				}
				// Commented By Gurdeep Singh on 10th November 2012
				// Intent i = new Intent(FrameActivity.this, TenFourTab.class);
				// i.putExtra("MODE", 1);
				// i.putExtra("tab", 1);
				// i.putExtra("groupid", gid);
				// i.putExtra("name", name);
				// startActivity(i);
				// finish();
			}
		}

		else if (requestCode == Utils.CONTACT_SUCCESSFULLY_ADDED
				&& resultCode == Utils.RESULT_FILL_FRIENDS) {
			Log.e("", "=========Filling the Friends ==============");
			FillFriends();
		} else if (requestCode == 231 && resultCode == Activity.RESULT_OK) {
			Log.e("", "=========Filling the Friends ==============");
			// FillGroup();
			FillFriends();
			// showGroupMembers(""+uniqueGroupId, uniqueGroupName);

		}

		else if (requestCode == 45) {
			Log.e("", "=========Filling the Friends ==============45 45 45 ");
			// FillGroup();
			FillFriends();
			// showGroupMembers(""+uniqueGroupId, uniqueGroupName);

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// @Override
	// protected void onDestroy() {
	// try {
	// CommonFunctions.clearActivityStack(FrameActivity.this);
	// } catch (Exception e) {
	// }
	// super.onDestroy();
	// }
	// @Override
	// public void finish() {
	// Log.e("", "On Finish Called");
	// try {
	// CommonFunctions.clearActivityStack(FrameActivity.this);
	// } catch (Exception e) {
	//
	// }
	//
	// super.finish();
	// }
	private void showActionSheet(final String phoneNumber, final String name,
			final int friendId) {
		try {

			HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
			menuItems.put(0, "Send Message");
			menuItems.put(1, "Add to Group");
			menuItems.put(2, "Cancel");

			CustomMenu.show(getActivity(), menuItems,
					new OnMenuItemSelectedListener() {

						@Override
						public void MenuItemSelectedEvent(Integer selection) {

							switch (selection) {
							case 0:
								// Intent intent = new
								// Intent(FrameActivity.this,EyeSayDefaultAudioVideo.class);
								// intent.putExtra("phone", phoneNumber);
								// intent.putExtra("name", name);
								// startActivity(intent);
								String conversionId = "";
								DataBase db = null;
								try {
									db = new DataBase(getActivity());
									db.open();
									conversionId = db.fetchConversationID(name);
									Log.e("", "Db Conversation Id"
											+ conversionId);
									if (!(conversionId != null && conversionId
											.length() > 0)) {
										// conversionId =
										// "tfc"+System.currentTimeMillis();
										Log.e("",
												"New Generated Conversation Id"
														+ conversionId);
									}

								} catch (Exception e) {
									e.printStackTrace();

								} finally {
									db.close();
								}

								Intent i = new Intent();
								i.setClass(getActivity(),
										EyeSayDefaultMessage.class);
								i.putExtra(Utils.REPLY_NUMBER, "-"
										+ phoneNumber + "-");
								i.putExtra(Utils.REPLY_FRIEND_NAME, name);
								i.putExtra(Utils.REPLY_STATUS, true);
								i.putExtra(Utils.REPLY_MESSAGE_ID, 0);
								i.putExtra(Utils.REPLY_CONVERSATION_ID,
										conversionId);
								i.putExtra(Utils.SEND_SINGLE_MESSAGE, true);
								startActivity(i);
								// finish();
								break;
							case 1:
								AddTOGroup(friendId);
								break;
							case 2:
								CustomMenu.hide();
								break;
							default:
								break;

							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showGroupActionSheet(final String phoneNumber,
			final String name) {

		HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
		menuItems.put(0, "Edit Group");
		menuItems.put(1, "Delete Group");
		menuItems.put(2, "Cancel");

		CustomMenu.show(getActivity(), menuItems,
				new OnMenuItemSelectedListener() {

					@Override
					public void MenuItemSelectedEvent(Integer selection) {

						switch (selection) {
						case 0:
							break;
						case 1:
							break;
						case 2:
							CustomMenu.hide();
							break;
						default:
							break;
						}
					}
				});

	}

	@Override
	public void onResume() {
		// ActivityStatus.appStatus(this, true);
              ActivityStatus.setActivity(getActivity());
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

	private void signOut() {
		progressDialog = ProgressDialog.show(getActivity(), "Logging out",
				"Please wait while sign out from eyesay ......");
		progressDialog.setCancelable(true);

		new SignOut(this).start();

	}

	ProgressDialog progressDialog;

	@Override
	public void onResponse(String response) {

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		CommonFunctions.CancelAlarm(getActivity());
		CommonFunctions.setPref(getActivity(), CommonFunctions.IS_LOGIN, false);
		CommonFunctions.setPref(getActivity(), Constants.SESSION, "");
		CommonFunctions.setPref(getActivity(), CommonFunctions.REF_ID, "");
		try {
			DataBase db = new DataBase(getActivity());
			db.open();
			db.clean();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// finish();

	}

	@Override
	public void onError(String onError) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		Toast.makeText(
				getActivity(),
				"Some thing went wrong while logout , Please try after some time",
				Toast.LENGTH_LONG).show();

	}

}
