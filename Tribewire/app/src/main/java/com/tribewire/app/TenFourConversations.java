package com.tribewire.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import com.app.eyesay.dialog.EyesayonWayDialog;
import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.Debugger;
import com.eyesayapp.Utils.OnSwipeTouchListener;
import com.eyesayapp.Utils.TribeWireConstants;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.IInbox;
import com.eyesayapp.asyn.InBoxList;
import com.eyesayapp.asyn.RestApiCall;
import com.eyesayapp.asyn.SignOut;
import com.eyesayapp.model.Badge;
import com.eyesayapp.model.FriendModel;
import com.google.android.gms.internal.cu;

import eyesay.adapter.MessageFolderAdapter;
import eyesay.adapter.SimpleAdapter;
import eyesay.obj.Friend;

/**
 * Class for managing message Conversation list.
 * 
 * @author xtpler
 * 
 */

public class TenFourConversations extends Fragment implements OnClickListener,
		TextWatcher, OnItemClickListener, IInbox, RecyclerListener,
		IHttpRequest {

	public EyesayonWayDialog eyesayonWayDialog;

	LinearLayout slidingPanel;
	LinearLayout main_Layout;
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
	private Button btnNewMessage;

	private boolean messageReceived = false;
	public boolean isOnCreate = false;
	String conversationId = "";
	static TenFourConversations obj;
	private TextView txtAllConversation;
	private TextView txtUsername;
	RelativeLayout rltSearchBar;
	EditText edtSearchText;
	TextView txtCancelSearch;
	Button btnSearch;
	RelativeLayout rltMenuLayout;
	LinearLayout conProfileData;
	ImageView imgProfilePic;

	private Button btnBadge;

	Context context = getActivity();

	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.activity_conversation, null);

		// btnBadge = (Button) view.findViewById(R.id.btn_badge);
		// TribeWireConstants.showFyiBadge(context, btnBadge);
		// slidingPanel = (LinearLayout) view.findViewById(R.id.slidingpanel);
		// slidingPanel.setVisibility(View.INVISIBLE);
		// main_Layout = (LinearLayout) view.findViewById(R.id.mainLayout);
		// slide_menu_icon = (Button) view.findViewById(R.id.btn_menu);
		// conAllConversation = (RelativeLayout) view
		// .findViewById(R.id.con_allconversation);
		// conCompanyFYi = (RelativeLayout) view
		// .findViewById(R.id.con_company_fyi);
		// con_Contacts = (RelativeLayout) view.findViewById(R.id.con_contacts);
		// con_Groups = (RelativeLayout) view.findViewById(R.id.con_groups);
		rltSearchBar = (RelativeLayout) view.findViewById(R.id.rlt_search_bar);
		edtSearchText = (EditText) view.findViewById(R.id.edt_search_contact);
		txtCancelSearch = (TextView) view.findViewById(R.id.txt_search_cancel);
		// con_Emergency = (RelativeLayout)
		// view.findViewById(R.id.con_emergency);
		btnNewMessage = (Button) view.findViewById(R.id.btn_newmessage);
		// txtAllConversation = (TextView) view
		// .findViewById(R.id.txt_all_conversation);
		// txtUsername = (TextView) view.findViewById(R.id.txt_username);
		btnSearch = (Button) view.findViewById(R.id.btn_search);
		rltMenuLayout = (RelativeLayout) view
				.findViewById(R.id.rlt_menu_layout);
		// imgProfilePic = (ImageView)
		// view.findViewById(R.id.img_profile_local);
		btnSearch.setOnClickListener(searchListener);
		txtCancelSearch.setOnClickListener(searchListener);
		// conProfileData = (LinearLayout) view
		// .findViewById(R.id.con_profile_data);
		edtSearchText.addTextChangedListener(this);
		btnNewMessage.setOnClickListener(this);
		// slide_menu_icon.setOnClickListener(this);
		// conAllConversation.setOnTouchListener(onSwipeAllConversation);
		// conCompanyFYi.setOnTouchListener(onSwipeCompanyFYi);
		// con_Contacts.setOnTouchListener(onSwipeContact);
		// conProfileData.setOnTouchListener(onSwipeProfileData);
		// con_Emergency.setOnTouchListener(onSwipeEmergency);
		// initiateMenuPanel();
		getActivity().getActionBar()
				.setTitle("ALL CONVERSATIONS".toUpperCase());
		initialKeys();
		// setuserName();
		// setFontforMenu();
		// setDefaultImage();

		// new InBoxList(getActivity(), getActivity()).start();
		// getAdminGroups();

		Log.e("", "============ Oncreate Called ================");
		// return super.onCreateView(inflater, container, savedInstanceState);
		// FillMessageFolder(view);
           moveToConversationScreen();

		
		return view;
	}

	
	private void moveToConversationScreen() {
		try {

			
			String convId = getArguments().getString("conv_id");
		boolean isEmergency  = 	getArguments().getBoolean("is_emergency");
		if (isEmergency) {
			return;
		}
			//String convId = GcmIntentService.convIDNOtify;
			
			Log.e("",
					"=============== The Conversation Id in TenfourConversation "
							+ convId);
			if (convId != null && convId.length() > 0) {
				String name = "";
				DataBase db = null;
				try {
					 db = new DataBase(getActivity());
					db.open();
					Cursor cursor = db.fetch(DataBase.Receive_table,
							DataBase.Receive_int, "conversation_Id = '"
									+ convId + "'", null, null);
					if (cursor != null && cursor.getCount() > 0) {
						do {
							name = cursor.getString(3);
							Log.e("",
									"==================== The Name of the Receiver is "
											+ name);
						} while (cursor.moveToNext());

						cursor.close();
						ArrayList<FriendModel>  friendModels =		separateFriends(name, db);
						friendModelArray  = new ArrayList<ArrayList<FriendModel>>();
						friendModelArray.add(friendModels);
					}

				} catch (Exception e) {

					e.printStackTrace();
				}
				finally {
					if (db!=null ) {
					db.close();	
					}
				}
 
								
				Intent intent = new Intent(getActivity(),
						ConversationMessages.class);
				intent.putExtra("conid", convId);
				try {
					intent.putExtra("conListener", friendModelArray.get(position));
				} catch (Exception e) {
					e.printStackTrace();
				}
				startActivityForResult(intent, 11);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	//
	// setContentView(R.layout.activity_conversation);
	// btnBadge = (Button) findViewById(R.id.btn_badge);
	// TribeWireConstants.showFyiBadge(getActivity(), btnBadge);
	// slidingPanel = (LinearLayout) findViewById(R.id.slidingpanel);
	// slidingPanel.setVisibility(View.INVISIBLE);
	// main_Layout = (LinearLayout) findViewById(R.id.mainLayout);
	// slide_menu_icon = (Button) findViewById(R.id.btn_menu);
	// conAllConversation = (RelativeLayout)
	// findViewById(R.id.con_allconversation);
	// conCompanyFYi = (RelativeLayout) findViewById(R.id.con_company_fyi);
	// con_Contacts = (RelativeLayout) findViewById(R.id.con_contacts);
	// con_Groups = (RelativeLayout) findViewById(R.id.con_groups);
	// rltSearchBar = (RelativeLayout) findViewById(R.id.rlt_search_bar);
	// edtSearchText = (EditText) findViewById(R.id.edt_search_contact);
	// txtCancelSearch = (TextView) findViewById(R.id.txt_search_cancel);
	// con_Emergency = (RelativeLayout) findViewById(R.id.con_emergency);
	// btnNewMessage = (Button) findViewById(R.id.btn_newmessage);
	// txtAllConversation = (TextView) findViewById(R.id.txt_all_conversation);
	// txtUsername = (TextView) findViewById(R.id.txt_username);
	// btnSearch = (Button) findViewById(R.id.btn_search);
	// rltMenuLayout = (RelativeLayout) findViewById(R.id.rlt_menu_layout);
	// imgProfilePic = (ImageView) findViewById(R.id.img_profile_local);
	// btnSearch.setOnClickListener(searchListener);
	// txtCancelSearch.setOnClickListener(searchListener);
	// conProfileData = (LinearLayout) findViewById(R.id.con_profile_data);
	// edtSearchText.addTextChangedListener(getActivity());
	// btnNewMessage.setOnClickListener(getActivity());
	// slide_menu_icon.setOnClickListener(getActivity());
	// conAllConversation.setOnTouchListener(onSwipeAllConversation);
	// conCompanyFYi.setOnTouchListener(onSwipeCompanyFYi);
	// con_Contacts.setOnTouchListener(onSwipeContact);
	// conProfileData.setOnTouchListener(onSwipeProfileData);
	// con_Emergency.setOnTouchListener(onSwipeEmergency);
	// initiateMenuPanel();
	// initialKeys();
	// setuserName();
	// setFontforMenu();
	// setDefaultImage();
	//
	// // new InBoxList(getActivity(), getActivity()).start();
	// // getAdminGroups();
	// super.onCreate(savedInstanceState);
	// }

	private void setDefaultImage() {
		try {
			String path = CommonFunctions.getPref(getActivity(),
					AddEditImage.LOCAL_PROFILE_PIC, "");
			if (path != null && path.length() > 0) {
				File image = new File(path);
				if (image.exists()) {

					imgProfilePic.setImageBitmap(BitmapFactory.decodeFile(image
							.getAbsolutePath()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	TextView txtCompanyFyi;

	private void setFontforMenu() {
		try {

			// Typeface tf = TribeWireUtil.getArialTypeface(getActivity());
			// TextView txtAllCon = (TextView)
			// findViewById(R.id.txt_all_conversation);
			// txtCompanyFyi = (TextView) findViewById(R.id.txt_company_fyi);
			// TextView txtContact = (TextView)
			// findViewById(R.id.txt_contact_groups);
			// TextView txtEmergency = (TextView)
			// findViewById(R.id.txt_emergency);
			// TextView allConversation = (TextView)
			// findViewById(R.id.txt_static_allconv);
			// TextView txtSignout = (TextView) findViewById(R.id.txt_sign_out);
			// txtSignout.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // Toast.makeText(TenFourConversations.getActivity(),
			// // "======== Toast Clicked", Toast.LENGTH_SHORT).show();
			// signOut();
			// }
			// });
			// TextView txtStatus = (TextView) findViewById(R.id.txt_status);
			// TextView txtOnline = (TextView)
			// findViewById(R.id.txt_status_online);
			// txtSignout.setTypeface(tf);
			// txtStatus.setTypeface(tf);
			// txtOnline.setTypeface(tf);
			// txtAllCon.setTypeface(tf);
			// txtCompanyFyi.setTypeface(tf);
			// txtContact.setTypeface(tf);
			// txtEmergency.setTypeface(tf);
			// allConversation.setTypeface(TribeWireUtil
			// .getArialBoldTypeface(getActivity()));

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void signOut() {
		progressDialog = ProgressDialog.show(getActivity(), "Logging out",
				"Please wait while sign out  ......");
		progressDialog.setCancelable(true);

		new SignOut(this).start();

	}

	private void showSearchBar() {
		try {
			if (rltSearchBar.getVisibility() == View.GONE) {
				TribeWireUtil.showKeyboard(getActivity(), edtSearchText);
				rltMenuLayout.setVisibility(View.GONE);
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
				rltMenuLayout.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	OnClickListener searchListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v == btnSearch) {
				showSearchBar();
			} else if (v == txtCancelSearch) {
				hideSearchBar();
				FillMessageFolder(view);
			}
		}
	};

	private void setuserName() {
		try {
			String fName = CommonFunctions.getPref(getActivity(),
					Constants.FIRST_NAME, "");
			String lName = CommonFunctions.getPref(getActivity(),
					Constants.LAST_NAME, "");
			txtUsername.setText(fName + " " + lName);
			txtUsername.setTypeface(TribeWireUtil
					.getArialBoldTypeface(getActivity()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static final int SHOW_FYI_BADEG = 7;
	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == 8) {

				try {
					String path = CommonFunctions.getPref(getActivity(),
							AddEditImage.LOCAL_PROFILE_PIC, "");
					if (path != null && path.length() > 0) {
						File image = new File(path);
						if (image.exists()) {

							// imgProfilePic.setImageBitmap(BitmapFactory
							// .decodeFile(image.getAbsolutePath()));
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (msg.what == SHOW_FYI_BADEG) {
				// btnBadge.setVisibility(View.VISIBLE);

			} else if (msg.what == 10) {
				// isOnCreate = true;
				try {
					NotificationManager mNotificationManager = (NotificationManager) getActivity()
							.getSystemService(Context.NOTIFICATION_SERVICE);
					mNotificationManager.cancel(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				showMessageList();
			}
		};

	};

	@Override
	public void onResume() {
		try {
			NotificationManager mNotificationManager = (NotificationManager) getActivity()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isOnCreate) {
			FillMessageFolder(view);
			getCompanyFyiMessages();
		} else {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {

							try {
								if (THREAD != null && THREAD.length() > 0) {
									getTopMessage();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							getCompanyFyiMessages();

							try {
								ActivityStatus.setActivity(getActivity());

								handler.sendEmptyMessage(8);
							} catch (Exception e) {
								e.printStackTrace();
							}

							FillMessageFolder(view);

						}
					});

				}
			}, 1000);
		}

		ActivityStatus.setActivity(getActivity());

		super.onResume();
	}

	@Override
	public void onPause() {
		ActivityStatus.setActivity(null);
		super.onPause();
	}

	private void initialKeys() {
		try {
			obj = this;
			try {
				messageReceived = getActivity().getIntent().getBooleanExtra(
						CommonFunctions.MSG_RECEIVED, false);

			} catch (Exception e) {
				e.printStackTrace();
			}

			isOnCreate = true;

			try {
				// "show", "conversation"
				conversationId = getActivity().getIntent().getStringExtra(
						"show");
				// 04-09 11:18:07.330: E/(23575): The Conversation Value is
				// conversation

				Log.e("", "The Conversation Value is " + conversationId);
				if (conversationId != null) {
					Log.e("", "I am Inside");
					// fillConversation();
					THREAD = conversationId;
				} else {
					Log.e("", "I am Outside");
					// FillMessageFolder();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

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

			// gestureDetector = new GestureDetector(new MyGestureDetector());
			// gestureListener = new View.OnTouchListener() {
			// public boolean onTouch(View v, MotionEvent event) {
			// if (gestureDetector.onTouchEvent(event)) {
			// return true;
			// }
			// return false;
			// }
			// };
			slidingPanel.setOnTouchListener(gestureListener);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// class MyGestureDetector extends SimpleOnGestureListener {
	// @Override
	// public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	// float velocityY) {
	// try {
	// if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
	// return false;
	// // right to left swipe
	// if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
	// && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	// Log.e("", "===Swipe Right to left ");
	// if (isExpanded) {
	// hideMenuAnother();
	// isExpanded = false;
	// }
	//
	// } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
	// && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	// if (isExpanded) {
	// hideMenuAnother();
	// isExpanded = false;
	// }
	//
	// Log.e("", "===Swipe Left to right ");
	//
	// }
	// } catch (Exception e) {
	//
	// }
	// return false;
	// }
	// }
	//
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

	@Override
	public void onClick(View v) {
		if (v == slide_menu_icon) {
			Log.e("", "======== If expanded " + isExpanded);
			if (!isExpanded) {
				isExpanded = true;

				showMenuAnother();
			} else {
				isExpanded = false;

				hideMenuAnother();

			}

		} else if (v == btnNewMessage) {
			Intent intent = new Intent(getActivity(), EyesaySendText.class);
			startActivity(intent);

		}
	}

	OnSwipeTouchListener onSwipeAllConversation = new OnSwipeTouchListener() {

		@Override
		public void onSwipeRight() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");
			super.onSwipeRight();

		}

		@Override
		public void onClick() {
			Log.e("", "  Swipe Confirm 100 %");
			// finish();
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			super.onClick();
		}

		@Override
		public void onSwipeLeft() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");

			super.onSwipeLeft();
		}
	};

	OnSwipeTouchListener onSwipeCompanyFYi = new OnSwipeTouchListener() {

		@Override
		public void onSwipeRight() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");
			super.onSwipeRight();

		}

		@Override
		public void onClick() {
			Log.e("", "  Swipe Confirm 100 %");
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(getActivity(), CompanyFYI.class);
					startActivity(intent);

				}
			}, 650);

			super.onClick();
		}

		@Override
		public void onSwipeLeft() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");

			super.onSwipeLeft();
		}

	};

	OnSwipeTouchListener onSwipeContact = new OnSwipeTouchListener() {

		@Override
		public void onSwipeRight() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");
			super.onSwipeRight();

		}

		@Override
		public void onClick() {
			Log.e("", "  Swipe Confirm 100 %");
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(getActivity(),
							FrameActivity.class);
					startActivity(intent);
					// finish();
				}
			}, 650);

			super.onClick();
		}

		@Override
		public void onSwipeLeft() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");

			super.onSwipeLeft();
		}

	};

	OnSwipeTouchListener onSwipeProfileData = new OnSwipeTouchListener() {

		@Override
		public void onSwipeRight() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");
			super.onSwipeRight();

		}

		@Override
		public void onClick() {
			Log.e("", "  Swipe Confirm 100 %");
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(getActivity(),
							AddEditImage.class);
					startActivity(intent);

				}
			}, 650);

			super.onClick();
		}

		@Override
		public void onSwipeLeft() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");

			super.onSwipeLeft();
		}
	};

	OnSwipeTouchListener onSwipeEmergency = new OnSwipeTouchListener() {

		@Override
		public void onSwipeRight() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");
			super.onSwipeRight();

		}

		@Override
		public void onClick() {
			Log.e("", "  Swipe Confirm 100 %");
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(getActivity(), Emergency.class);
					startActivity(intent);
					getActivity().finish();
				}
			}, 650);

			//
			super.onClick();
		}

		@Override
		public void onSwipeLeft() {
			if (isExpanded) {
				hideMenuAnother();
				isExpanded = false;
			}
			Log.e("", "========= Swipe right to left ==========");

			super.onSwipeLeft();
		}
	};

	String[] conversationIDs;
	ListView lv;
	int position = 0;
	Dialog a = null;

	public String THREAD = "";
	boolean playflag, downloadflag;
	private int EYESAY_DRAFTS = 989;
	String myTempConversationId = "";

	ProgressDialog progressDialog = null;

	int[] state;
	ProgressDialog pd;
	private EditText message_search;

	Button global_reply;

	public int messageOrConversation = -1;
	boolean viewExists = false;

	boolean isServiceBind = false;
	List<Integer> MessageId = null;
	private int isread[] = null;

	MessageFolderAdapter messageFolderAdapter;
	String[] messageType = null;
	String[] fileUrl = null;

	public void FillMessageFolder(View view) {

		// { "sr_no", "message_id", "sender", "receiver", "message_type",
		// "file_url", "date_time", "type", "status",
		// "conversation_Id", "reply", "sender_status" ,"favourite" }
		Log.e("",
				"=============================== Fill Message folder==========");
		// setContentView(R.layout.message_folderlist);
		viewExists = false;
		messageOrConversation = 1;
		// message_search = (EditText) findViewById(R.id.message_search);
		lv = (ListView) view.findViewById(R.id.lst_msg_folder);

		// message_search.addTextChangedListener(getActivity());

		String[] display;
		int[] msgCount;
		String[] date;

		conversationIDs = null;
		String topMessage[] = null;
		// THREAD = "";
		MessageId = null;

		DataBase db = new DataBase(getActivity());
		try {
			db.open();

			Cursor message = db.fetch(DataBase.Receive_table,
					DataBase.Receive_int, "date_time desc,status",
					"conversation_Id");

			if (message.getCount() > 0 && message != null) {

				int totalcount = message.getCount();
				try {
					int unreadCount = db.getCount(DataBase.emergency_table,
							DataBase.emergency_int, DataBase.isRead + " = 0");
					Badge.setAllConvBadge("" + unreadCount);
					MenuActivity.notifyAdapter();
				} catch (Exception e) {
					e.printStackTrace();
				}

				display = new String[totalcount];
				msgCount = new int[totalcount];
				date = new String[totalcount];
				state = new int[totalcount];
				conversationIDs = new String[totalcount];
				topMessage = new String[totalcount];
				friendModelArray = new ArrayList<ArrayList<FriendModel>>();
				isread = new int[totalcount];
				messageType = new String[totalcount];
				receiverArray = new String[totalcount];
				senderName = new String[totalcount];
				fileUrl = new String[totalcount];
				isEmergency = new Integer[totalcount];
				int count = 0;
				do {
					conversationIDs[message.getPosition()] = message
							.getString(9);
					date[message.getPosition()] = CommonFunctions
							.getdateAno(message.getLong(6));// date
					// Log.e("",
					// "Milliseconds " + message.getLong(6)
					// + "===Milliseconds Date is "
					// + date[message.getPosition()]);

					// Log.e("",
					// "========== The date is "
					// + new Date(message.getLong(6)).toString());

					count = db.fetchCounts(
							DataBase.Receive_table,
							DataBase.Receive_int,
							new String[] { "_id" },
							"conversation_Id='"
									+ conversationIDs[message.getPosition()]
									+ "'");
					senderName[message.getPosition()] = message.getString(2);
					state[message.getPosition()] = message.getInt(7);// state
					isread[message.getPosition()] = message.getInt(8);
					messageType[message.getPosition()] = message.getString(4);
					fileUrl[message.getPosition()] = message.getString(5);

					// Log.e("", "======================== tHE MESSAGE TYPE "
					// + message.getString(4));
					// String append = "";
					msgCount[message.getPosition()] = count;// state
					try {
						topMessage[message.getPosition()] = message
								.getString(15);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (state[message.getPosition()] == 0) {
						display[message.getPosition()] = message.getString(3);
					} else {
						display[message.getPosition()] = message.getString(3);
					}

					receiverArray[message.getPosition()] = message.getString(3);
					// Log.e("", "Sender " + message.getString(2) +
					// "Receiver = "
					// + message.getString(3));

					// count = db.fetchCounts(DataBase.Receive_table,
					// DataBase.Receive_int, new String[] { "_id" },
					// "conversation_Id='" + message.getString(9)
					// + "' and status=1");
					// int newcount = db.getCount(DataBase.Receive_table,
					// DataBase.Receive_int,
					// "conversation_Id='" + message.getString(9)
					// + "' and status=0");
					// state[message.getPosition()] = (newcount > 0) ? 1 : 0;//
					// state
					fetchIsEmergency(db,
							conversationIDs[message.getPosition()],
							isEmergency, state, message.getPosition());
					// String receiver = message.getString(3);
					// ArrayList<FriendModel> subLstFriendMo = separateFriends(
					// receiver, db);
					// friendModelArray.add(subLstFriendMo);
					String receiver = message.getString(3);
					ArrayList<FriendModel> subLstFriendMo = null;
					if (receiver.equalsIgnoreCase("all")) {
						String sender = message.getString(2);
						subLstFriendMo = separateFriends(sender, db);
						Log.e("",
								"--------------------------------------  The Sender Name "
										+ sender);
					} else {
						subLstFriendMo = separateFriends(receiver, db);
					}

					try {

						String receiverArray[] = receiver.split(",");
						if (receiverArray != null && receiverArray.length > 1) {
							String receiverNames = "";
							int restFriends = receiverArray.length - 1;
							if (restFriends > 1) {
								receiverNames = receiverArray[0] + " and "
										+ restFriends + " Others";
							} else {
								receiverNames = receiverArray[0] + " and "
										+ restFriends + " Other";
							}
							if (isEmergency[message.getPosition()] == 1) {
								display[message.getPosition()] = message
										.getString(2);
							} else {
								display[message.getPosition()] = receiverNames;
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					friendModelArray.add(subLstFriendMo);

				} while (message.moveToNext());
				lv.setCacheColorHint(Color.TRANSPARENT);
				messageFolderAdapter = new MessageFolderAdapter(getActivity(),
						display, date, state, msgCount, topMessage,
						this.friendModelArray, messageType, fileUrl,
						isEmergency);
				lv.setAdapter(messageFolderAdapter);
				lv.setOnItemClickListener(this);
               lv.setSelection(selectedPosition);
			} else {
				// lv = getActivity().getListView();

				// lv.setCacheColorHint(Color.TRANSPARENT);
				//
				// lv.setAdapter(new SimpleAdapter(getActivity(),
				// "Conversations not available"));
				// lv.setOnItemClickListener(null);
				// lv.setOnItemLongClickListener(null);
			}
			message.close();
			Debugger.debugE(".." + message.getCount());
		} catch (Exception e) {
			Debugger.debugE("error in fill Message" + e.getMessage());
		} finally {
			db.close();

		}
		showMessageList();

	}

	// TODO need it replace with User Id
	private ArrayList<FriendModel> separateFriends(String receiver, DataBase db) {
		ArrayList<FriendModel> lstFriendModel = null;
		FriendModel friendModel;
		try {
			// Log.e("", "============== full receiver name is " + receiver);
			String receiverArray[] = receiver.split(",");
			if (receiverArray.length > 0) {
				lstFriendModel = new ArrayList<FriendModel>();
			}
			// Log.e("", "===== Receiver Array length is " +
			// receiverArray.length);
			for (int i = 0; i < receiverArray.length; i++) {
				String receiverName = receiverArray[i];
				// Log.e("", "=============After tokenizing === " +
				// receiverName);
				friendModel = getFriendData(receiverName, db);
				lstFriendModel.add(friendModel);
			}
		} catch (Exception e) {
			e.printStackTrace();
			friendModel = getFriendData(receiver, db);
			lstFriendModel.add(friendModel);
		}

		return lstFriendModel;
	}

	private FriendModel getFriendData(String receiverName, DataBase db) {
		FriendModel friendModel = null;
		try {
			String where = Friend.NAME + " = '" + receiverName + "'";

			Cursor cursor = db.fetch(DataBase.Friends_table,
					DataBase.Friends_int, where);
			// Log.e("", "============= The Cursor Count is " +
			// cursor.getCount()
			// + "=============== With Friend Name " + receiverName);

			if (cursor != null && cursor.getCount() > 0) {
				friendModel = new FriendModel();
				String userImage = cursor.getString(4);
				String friendId = "" + cursor.getInt(0);
				String friendNumber = cursor.getString(3);
				String friendName = cursor.getString(2);
				String friendStatus = "" + cursor.getInt(6);
				friendModel.setFriendId(friendId);
				friendModel.setFriendName(friendName);
				friendModel.setFriendNumber(friendNumber);
				friendModel.setImgUrl(userImage);
				friendModel.setFriendStatus(friendStatus);
				// Log.e("", "=============== the User Image is " + userImage);
			}

			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return friendModel;

	}

	public static boolean isProgressToShow = true;

	public void showMessageList() {
		try {
			Log.e("", "=============  Show message list called ===========");
			// if (isOnCreate) {

			if (CommonFunctions.CheckNetConnectivity(getActivity())) {

				if (isOnCreate) {
					if (isProgressToShow) {
						pd = ProgressDialog.show(getActivity(),
								"Fetching Inbox",
								"Please wait while fetching inbox list");
						pd.setCancelable(true);
						pd.show();
					}
				}

				new InBoxList(getActivity(), this).start();
				isOnCreate = false;
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void showMessageList2() {
		try {
			Log.e("",
					"=============  Show message list 2 called 222===========");
			if (isOnCreate) {

				if (CommonFunctions.CheckNetConnectivity(getActivity())) {
					Looper.prepare();
					if (isProgressToShow) {
						pd = ProgressDialog.show(getActivity(),
								"Fetching Inbox",
								"Please wait while fetching inbox list");
						pd.setCancelable(true);
						pd.show();
					}
					Looper.loop();
					new InBoxList(getActivity(), this).start();
					isOnCreate = false;

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void afterTextChanged(Editable arg0) {

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		searchThread(s);
	}

	int selectedPosition = 0;
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		// position = position - 1;
		// if (CommonFunctions.CheckNetConnectivity(getActivity())) {
		// DataBase db = new DataBase(getActivity());
		// try {
		// db.open();
		// ContentValues cv = new ContentValues();
		// cv.put("status", 0);
		// boolean isUpdate = db.update(DataBase.Receive_table,
		// DataBase.Receive_int, "conversation_Id ='"
		// + conversationIDs[position]
		// + "' and status = 1",
		//
		// cv);
		// Log.e("", "=============== Is update " + isUpdate
		// + " With Conv id " + conversationIDs[position]);
		// } catch (Exception e) {
		// Debugger.debugE("Exception :+" + conversationIDs[position]
		// + ".." + e.getMessage());
		// } finally {
		// db.close();
		// }
		// }
		selectedPosition  = position;
		Log.e("", "=============== The receiver Array "
				+ receiverArray[position]);
		Log.e("", "=============== Is Emergency " + isEmergency[position]);

		if ((receiverArray[position].equalsIgnoreCase("all"))
				|| isEmergency[position] == 1) {
			try {
				Intent intent = new Intent(getActivity(), EyesaySendText.class);
				intent.putExtra("receiver", senderName[position]);
				intent.putExtra("conid", conversationIDs[position]);
				startActivity(intent);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			THREAD = conversationIDs[position];
			Log.e("", "============= conversationIDs[position] "
					+ conversationIDs[position] + " ======= THREAD " + THREAD);
			myTempConversationId = conversationIDs[position];

			Intent intent = new Intent(getActivity(),
					ConversationMessages.class);
			intent.putExtra("conid", THREAD);
			try {
				intent.putExtra("conListener", friendModelArray.get(position));
			} catch (Exception e) {
				e.printStackTrace();
			}
			startActivityForResult(intent, 11);

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.e("", "============================== is on create  ========="
				+ isOnCreate + " Result Code " + resultCode
				+ " Default result Code " + Activity.RESULT_OK);

		// THREAD = "";
		if (resultCode == Activity.RESULT_OK) {
			isOnCreate = true;
			// showMessageList2();

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * private static final String TABLE_3_CREATE = "create table " +
	 * Receive_table + "(_id integer primary key autoincrement," +
	 * "message_id integer ," + "sender text not null," +
	 * "receiver text not null," + "message_type integer," +
	 * "file_url text not null," + "date_time text not null," + "type integer,"
	 * + "status integer default 0," + "conversation_Id text," +
	 * "reply integer default 0," +
	 * "sender_status integer default 1,favourite integer,played integer default 0,groupreceiver varchar(500) default 0,"
	 * + "textmessage varchar(500)," + "isdraft integer default 0," +
	 * "newdate varchar(500)," + " thumbnail varchar(500)," +
	 * "name varchar(500)" + ");";
	 */
	private void getTopMessage() {
		DataBase db = null;
		try {
			db = new DataBase(getActivity());
			db.open();
			Cursor c = db.fetch(DataBase.Receive_table, DataBase.Receive_int,
					"conversation_Id  = '" + THREAD.trim() + "'",
					"date_time desc", null);
			Log.e("",
					"-------------------------------------The Cursor Count is ----------------"
							+ c.getCount());
			if (c != null && c.getCount() > 0) {
				ContentValues cv = new ContentValues();
				cv.put("message_id", c.getString(1));
				cv.put("sender", c.getString(2));
				cv.put("receiver", c.getString(3));
				// cv.put("message_type", c.getString(4));
				cv.put("file_url", c.getString(5));
				try {
					String file_Url = c.getString(5);
					if (file_Url != null
							&& (file_Url.endsWith(".mp3") || file_Url
									.endsWith(".wav"))) {
						cv.put("message_type", 2);
					} else if (file_Url != null
							&& (file_Url.endsWith(".jpg")
									|| file_Url.endsWith(".png")
									|| file_Url.endsWith(".jpeg")
									|| file_Url.endsWith(".gif") || file_Url
										.endsWith(".bmp"))) {
						cv.put("message_type", "3");
					} else {
						cv.put("message_type", "0");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				cv.put("date_time", c.getString(6));
				cv.put("type", c.getString(7));
				// cv.put("status", c.getString(8));
				// cv.put("conversation_Id", c.getString(9));
				cv.put("reply", c.getString(10));
				cv.put("sender_status", c.getString(11));
				cv.put("favourite", c.getString(12));
				cv.put("played", c.getString(13));
				cv.put("groupreceiver", c.getString(14));
				cv.put("textmessage", c.getString(15));
				cv.put("isdraft", c.getString(16));
				cv.put("newdate", c.getString(17));
				cv.put("thumbnail", c.getString(18));
				cv.put("name", c.getString(19));

				boolean isUpdate = db.update(DataBase.Receive_table,
						DataBase.Receive_int, "conversation_Id='" + THREAD
								+ "'", cv);
				Log.e("",
						"================ Is Updated =============== "
								+ isUpdate
								+ " ======== Message "
								+ new String(Base64.decode(c.getString(15)
										.getBytes(), Base64.DEFAULT))
								+ "  = message type " + c.getString(5));

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	Handler handler2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			fillNewInbox();

		};
	};

	@Override
	public void fetchData(boolean fetch) {
		if (fetch) {
			handler2.sendEmptyMessage(2);

		}

		try {
			if (pd != null) {
				pd.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	ArrayList<ArrayList<FriendModel>> friendModelArray = null;
	Integer isEmergency[];
	String receiverArray[];
	String senderName[];

	public void fillNewInbox() {
		Log.e("", "============= Fill New Inbox called");
		String[] display;
		int[] msgCount;
		String[] date;

		conversationIDs = null;
		String topMessage[] = null;
		// THREAD = "";
		MessageId = null;

		DataBase db = new DataBase(getActivity());
		try {
			db.open();

			Cursor message = db.fetch(DataBase.Receive_table,
					DataBase.Receive_int, "date_time desc,status",
					"conversation_Id");

			if (message.getCount() > 0 && message != null) {
				int totalcount = message.getCount();
				try {
					// txtAllConversation.setText("All Conversations("
					// + totalcount + ")");
					int unreadCount = db.getCount(DataBase.emergency_table,
							DataBase.emergency_int, DataBase.isRead + " = 0 ");
					Badge.setAllConvBadge("" + unreadCount);
					MenuActivity.notifyAdapter();
				} catch (Exception e) {
					e.printStackTrace();
				}
				isEmergency = new Integer[totalcount];
				messageType = new String[totalcount];
				fileUrl = new String[totalcount];
				display = new String[totalcount];
				msgCount = new int[totalcount];
				date = new String[totalcount];
				state = new int[totalcount];
				conversationIDs = new String[totalcount];
				topMessage = new String[totalcount];
				receiverArray = new String[totalcount];
				senderName = new String[totalcount];
				friendModelArray = new ArrayList<ArrayList<FriendModel>>();
				int count = 0;
				do {
					conversationIDs[message.getPosition()] = message
							.getString(9);
					date[message.getPosition()] = CommonFunctions
							.getdateAno(message.getLong(6));// date
					// Log.e("",
					// "Milliseconds " + message.getLong(6)
					// + "===Milliseconds Date is "
					// + date[message.getPosition()]);
					count = db.fetchCounts(
							DataBase.Receive_table,
							DataBase.Receive_int,
							new String[] { "_id" },
							"conversation_Id='"
									+ conversationIDs[message.getPosition()]
									+ "'");
					state[message.getPosition()] = message.getInt(7);// state

					// String append = "";
					msgCount[message.getPosition()] = count;// state
					try {
						topMessage[message.getPosition()] = message
								.getString(15);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// msgCount
					// if (count > 1) {
					// append = " (" + count + ") ";
					// }

					if (state[message.getPosition()] == 0) {
						display[message.getPosition()] = message.getString(3);
					} else {
						display[message.getPosition()] = message.getString(3);
					}
					// Log.e("", "Sender " + message.getString(2) +
					// "Receiver = "
					// + message.getString(3));
					// count = db.fetchCounts(DataBase.Receive_table,
					// DataBase.Receive_int, new String[] { "_id" },
					// "conversation_Id='" + message.getString(9)
					// + "' and status=1");
					senderName[message.getPosition()] = message.getString(2);
					messageType[message.getPosition()] = message.getString(4);
					fileUrl[message.getPosition()] = message.getString(5);
					int newcount = db.getCount(DataBase.Receive_table,
							DataBase.Receive_int,
							"conversation_Id='" + message.getString(9)
									+ "' and status=0");
					// Log.e("", "============ Read Unread Count " + newcount
					// + " == Conversation id " + message.getString(9));
					// Log.e("", "============ Read Unread Count "+count);
					// state[message.getPosition()] = (newcount > 0) ? 1 : 0;//
					// state

					fetchIsEmergency(db,
							conversationIDs[message.getPosition()],
							isEmergency, state, message.getPosition());

					// String receiver = message.getString(3);
					String receiver = message.getString(3);

					receiverArray[message.getPosition()] = message.getString(3);
					// ArrayList<FriendModel> subLstFriendMo = separateFriends(
					// receiver, db);

					ArrayList<FriendModel> subLstFriendMo = null;
					if (receiver.equalsIgnoreCase("all")) {
						String sender = message.getString(2);

						Log.e("",
								"--------------------------------------  The Sender Name "
										+ sender);
						subLstFriendMo = separateFriends(sender, db);
					} else {
						subLstFriendMo = separateFriends(receiver, db);
					}

					try {

						String receiverArray[] = receiver.split(",");
						if (receiverArray != null && receiverArray.length > 1) {
							String receiverNames = "";
							int restFriends = receiverArray.length - 1;
							if (restFriends > 1) {
								receiverNames = receiverArray[0] + " and "
										+ restFriends + " Others";
							} else {
								receiverNames = receiverArray[0] + " and "
										+ restFriends + " Other";
							}

							if (isEmergency[message.getPosition()] == 1) {
								display[message.getPosition()] = message
										.getString(2);
							} else {
								display[message.getPosition()] = receiverNames;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					friendModelArray.add(subLstFriendMo);
				} while (message.moveToNext());

				// lv = getActivity().getListView();
				lv.setCacheColorHint(Color.TRANSPARENT);
				lv.setRecyclerListener(this);
				lv.setAdapter(new MessageFolderAdapter(getActivity(), display,
						date, state, msgCount, topMessage, friendModelArray,
						messageType, fileUrl, isEmergency));
				lv.setOnItemClickListener(this);
				lv.setSelection(selectedPosition);
			} else {
				// lv = getActivity().getListView();
				lv.setCacheColorHint(Color.TRANSPARENT);

				lv.setAdapter(new SimpleAdapter(getActivity(),
						"Conversations not available"));
				lv.setOnItemClickListener(null);
				lv.setOnItemLongClickListener(null);
			}
			message.close();
			Debugger.debugE(".." + message.getCount());
		} catch (Exception e) {
			Debugger.debugE("error in fill Message" + e.getMessage());
		} finally {
			db.close();

		}

	}

	private void fetchIsEmergency(DataBase db, String conversationId,
			Integer[] isEmergencys, int[] isReadStatus, int position) {
		try {

			if (db != null) {
				String where = DataBase.conversationId + " = '"
						+ conversationId + "'";
				Cursor c = db.fetch(DataBase.emergency_table,
						DataBase.emergency_int, where);
				if (c != null && c.getCount() > 0) {
					int isEmgergency = c.getInt(2);
					isEmergencys[position] = isEmgergency;
					int isRead = c.getInt(3);
					isReadStatus[position] = isRead;
				}

				c.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void searchThread(CharSequence searchText) {
		String[] display;
		int[] msgCount;
		String[] date;
		int[] state;
		conversationIDs = null;
		String topMessage[] = null;
		// THREAD = "";
		MessageId = null;

		DataBase db = new DataBase(getActivity());
		Cursor message = null;
		try {
			db.open();
			Log.e("", "================The Search text is "
					+ searchText.toString().trim() + "===============");
			if (searchText != null && searchText.length() > 0) {

				message = db.fetch(DataBase.Receive_table,
						DataBase.Receive_int, " receiver LIKE '" + searchText
								+ "%'", "date_time desc,status",
						"conversation_Id");
			} else {
				message = db.fetch(DataBase.Receive_table,
						DataBase.Receive_int, "date_time desc,status",
						"conversation_Id");
			}
			Log.e("", "The Message Count is " + message.getCount());
			if (message.getCount() > 0 && message != null) {

				int totalcount = message.getCount();
				messageType = new String[totalcount];
				fileUrl = new String[totalcount];
				display = new String[totalcount];
				msgCount = new int[totalcount];
				date = new String[totalcount];
				state = new int[totalcount];
				conversationIDs = new String[totalcount];
				topMessage = new String[totalcount];
				isEmergency = new Integer[totalcount];
				senderName = new String[totalcount];
				receiverArray[message.getPosition()] = message.getString(3);
				friendModelArray = new ArrayList<ArrayList<FriendModel>>();
				int count = 0;
				try {
					// txtAllConversation.setText("All Connversation("
					// + totalcount + ")");
				} catch (Exception e) {
					e.printStackTrace();
				}
				do {
					conversationIDs[message.getPosition()] = message
							.getString(9);
					date[message.getPosition()] = CommonFunctions
							.getdateAno(message.getLong(6));// date
					count = db.fetchCounts(
							DataBase.Receive_table,
							DataBase.Receive_int,
							new String[] { "_id" },
							"conversation_Id='"
									+ conversationIDs[message.getPosition()]
									+ "'");
					senderName[message.getPosition()] = message.getString(2);
					messageType[message.getPosition()] = message.getString(4);
					fileUrl[message.getPosition()] = message.getString(5);
					state[message.getPosition()] = message.getInt(7);// state
					msgCount[message.getPosition()] = count;// state
					if (state[message.getPosition()] == 0) {
						display[message.getPosition()] = message.getString(3);
					} else {
						display[message.getPosition()] = message.getString(3);
					}

					receiverArray[message.getPosition()] = message.getString(3);
					count = db.fetchCounts(DataBase.Receive_table,
							DataBase.Receive_int, new String[] { "_id" },
							"conversation_Id='" + message.getString(9)
									+ "' and status=0");
					// state[message.getPosition()] = (count > 0) ? 1 : 0;//
					// state
					fetchIsEmergency(db,
							conversationIDs[message.getPosition()],
							isEmergency, state, message.getPosition());
					topMessage[message.getPosition()] = message.getString(15);
					String receiver = message.getString(3);
					ArrayList<FriendModel> subLstFriendMo = separateFriends(
							receiver, db);
					friendModelArray.add(subLstFriendMo);
					try {

						String receiverArray[] = receiver.split(",");
						if (receiverArray != null && receiverArray.length > 1) {
							String receiverNames = "";
							int restFriends = receiverArray.length - 1;
							if (restFriends > 1) {
								receiverNames = receiverArray[0] + " and "
										+ restFriends + " Others";
							} else {
								receiverNames = receiverArray[0] + " and "
										+ restFriends + " Other";
							}

							display[message.getPosition()] = receiverNames;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} while (message.moveToNext());

				lv = (ListView) view.findViewById(R.id.lst_msg_folder);
				lv.setRecyclerListener(this);
				lv.setCacheColorHint(Color.TRANSPARENT);
				lv.setAdapter(new MessageFolderAdapter(getActivity(), display,
						date, state, msgCount, topMessage, friendModelArray,
						messageType, fileUrl, isEmergency));
				lv.setOnItemClickListener(this);
				lv.setSelection(selectedPosition);

			} else {
				lv = (ListView) view.findViewById(R.id.lst_msg_folder);
				lv.setCacheColorHint(Color.TRANSPARENT);

				lv.setAdapter(new SimpleAdapter(getActivity(),
						"Conversations not available"));
				lv.setOnItemClickListener(null);
				lv.setOnItemLongClickListener(null);
			}
			message.close();
			Debugger.debugE(".." + message.getCount());
		} catch (Exception e) {
			Debugger.debugE("error in fill Message" + e.getMessage());
		} finally {
			db.close();

		}

	}

	@Override
	public void onMovedToScrapHeap(View view) {
		try {
			ImageView imgUserOne = (ImageView) view
					.findViewById(R.id.img_user_one);
			ImageView imgUserTwo = (ImageView) view
					.findViewById(R.id.img_user_two);
			ImageView imgUserThree = (ImageView) view
					.findViewById(R.id.img_user_three);
			ImageView imgUserFour = (ImageView) view
					.findViewById(R.id.img_user_four);
			ImageView imgUserPic = (ImageView) view
					.findViewById(R.id.img_user_pic);

			ImageView imageView = (ImageView) view
					.findViewById(R.id.img_message);
			imageView.setImageDrawable(null);
			ImageView imageView1 = (ImageView) view
					.findViewById(R.id.img_audio_message);
			imageView1.setImageDrawable(null);
			imgUserTwo.setImageBitmap(null);
			imgUserFour.setImageBitmap(null);
			imgUserOne.setImageBitmap(null);
			imgUserThree.setImageBitmap(null);
			imgUserPic.setImageBitmap(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

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

	public void getCompanyFyiMessages() {
		try {
			if (CommonFunctions.isInternetConnected(getActivity())) {
				// pd = ProgressDialog.show(getActivity(), "Loading...",
				// "Please wait while fetching your messages");
				List<String> nameList = null;

				nameList = new ArrayList<String>();
				nameList.add(Constants.API_KEY_TAG);
				nameList.add(Constants.REF_ID_TAG);
				nameList.add(Constants.COMP_ID_TAG);
				nameList.add(Constants.SESSION_TAG);
				// new RestApiCall(getActivity(),
				// CommonFunctions.DEMO_SERVER_URL
				// + TribeWireConstants.COMAPANY_FYI_URL, nameList,
				// Constants.getValueList(getActivity())).execute();

				//
				new RestApiCall(this, CommonFunctions.DEMO_SERVER_URL
						+ TribeWireConstants.COMAPANY_FYI_URL, nameList,
						Constants.getValueList(getActivity())).start();
			} else {
				CommonFunctions.showAlert(getActivity(),
						"Please check your internet connectivity",
						"Network Error");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	Handler handler3 = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {
				// btnBadge.setVisibility(View.VISIBLE);
				try {
					Activity activity = TenFourConversations.this.getActivity();
					if (activity instanceof MenuActivity) {
						int count = 0;
						try {
							count = serverCount;
						} catch (Exception e) {
							e.printStackTrace();
						}

						MenuActivity menuActivity = (MenuActivity) activity;
						menuActivity.invalidateMenu(count);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (msg.what == 2) {
				try {
					Activity activity = TenFourConversations.this.getActivity();
					if (activity instanceof MenuActivity) {
						MenuActivity menuActivity = (MenuActivity) activity;
						menuActivity.invalidateMenu(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// btnBadge.setVisibility(View.GONE);
			} else if (msg.what == 3) {
				// txtCompanyFyi.setText("Company FYIs (" + serverCount + ")");
			}

		};
	};

	// Parses the count for Company Fyi if Server count is greater then Local
	// Database count
	// then we have to show Red Badger
	int serverCount = 0;
	int dbCount = 0;

	public void onCompanyFyiResponse(String response) {
		try {

			TribeWireUtil.parseCompanyFyiResponse(getActivity(), response);

			DataBase db = new DataBase(getActivity());
			db.open();

			dbCount = CommonFunctions.getPref(getActivity(), "count", 0);
			Log.e("", "============ The Db Count is " + dbCount
					+ "========= Server Count " + serverCount);

			try {
				Badge.setCompanyBadge("" + (serverCount - dbCount));
			} catch (Exception e) {
				e.printStackTrace();
			}
			serverCount = getFyiUnreadCount(db);
			MenuActivity.notifyAdapter();
			if (serverCount > 0) {
				handler3.sendEmptyMessage(1);
				TribeWireConstants.fyiUnread(getActivity());
			} else {
				handler3.sendEmptyMessage(2);

			}
			try {
				handler3.sendEmptyMessage(3);

			} catch (Exception e) {
				e.printStackTrace();

			}

			db.close();
			TribeWireUtil.parseCompanyFyiResponse(getActivity(), response);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int getFyiUnreadCount(DataBase db) {
		int unreadCount = 0;

		try {

			unreadCount = db.getCount(DataBase.Company_Fyi_table,
					DataBase.Company_Fyi_int, "status  = 0");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return unreadCount;
	}

}
