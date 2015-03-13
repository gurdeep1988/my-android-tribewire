package com.tribewire.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.map.deser.FromStringDeserializer;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.drm.DrmStore.Action;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.TribeWireConstants;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.model.NavDrawerItem;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.internal.fr;

import eyesay.adapter.NavDrawerListAdapter;

public class MenuActivity extends Activity implements IHttpRequest {

	private DrawerLayout drawerLayout;

	String[] navMenuTitles;
	TypedArray navMenuIcons;
	static NavDrawerListAdapter navDrawerListAdapter;
	ListView listView;
	ActionBarDrawerToggle actionBarDrawerToggle;
	CharSequence mTitle;
	private static int ALL_CONVERSATION = 1;
	private static int COMPANY_FYI = 2;
	private static int CONTACTS = 3;
	private static int EMERGENCY = 4;
	private static int EDIT_PROFILE = 5;
	public int currentFrangment = -1;
	int count = -1;
	String notificationConvId = "";
	boolean isEmergency = false;

	// private static boolean showCompanyFyiBadge= false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		mTitle = getTitle();
	        
		setContentView(R.layout.activity_menu);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		 try {
		        Integer titleId = (Integer) Class.forName("com.android.internal.R$id")
		                .getField("action_bar_title").get(null);
		        TextView title = (TextView) getWindow().findViewById(titleId);
		        title.setTextSize(14);
		         
		        // check for null and manipulate the title as see fit
		    } catch (Exception e) {
		        Log.e("", "Failed to obtain action bar title reference");
		    }
		 
		 try {
			 
			notificationConvId = getIntent().getStringExtra("conv_id");
			isEmergency  = getIntent().getBooleanExtra("is_emergency", false);
			// notificationConvId = GcmIntentService.convIDNOtify ;
			Log.e("", "================= The Notification Id is Menu screen"+notificationConvId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_item_names);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_icon_names);

		ArrayList<NavDrawerItem> drawerItems = new ArrayList<NavDrawerItem>();
		drawerItems.add(new NavDrawerItem(navMenuTitles[0], -2, false));
		drawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(0, -1), true));
		drawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(1, -1), true));
		drawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
				.getResourceId(2, -1), false));
		drawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
				.getResourceId(3, -1), false));
		navMenuIcons.recycle();
		listView = (ListView) findViewById(R.id.list_slidermenu);
		navDrawerListAdapter = new NavDrawerListAdapter(this, drawerItems);
		listView.setAdapter(navDrawerListAdapter);
		listView.setOnItemClickListener(new SlideMenuClickListener());
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.blue)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle("ALL CONVERSATIONS".toUpperCase());
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		
		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.drawer_30x30, R.string.app_name, R.string.app_name) {
			@Override
			public void onDrawerClosed(View drawerView) {
				// TODO Auto-generated method stub
				// getActionBar().setTitle("All Conversation");
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle("All Conversation");
				// calling onPrepareOptionsMenu() to hide action bar icons
				try {
					navDrawerListAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (currentFrangment == EMERGENCY) {
					Emergency emergency = (Emergency) fragment;
					TribeWireUtil.hideKeyboard(MenuActivity.this,
							emergency.edtMessage);
				}

				// invalidateOptionsMenu();
				super.onDrawerOpened(drawerView);
			}

		};
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		if (savedInstanceState == null) {
			//displayView(1);
			displayAllConversation();
			currentFrangment = ALL_CONVERSATION;

			
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		try {
//			getActionBar().setBackgroundDrawable(
//					new ColorDrawable(getResources().getColor(R.color.nav_allconversation)));
			ActivityStatus.setActivity(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResume();
	}
	
	
	
	public Handler handler2 = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {

				try {
				 String message   =  msg.getData().getString("message", "");
				 String emergencyMessage  =  TribeWireUtil.getNotificationMessage(message);
				 TribeWireUtil.showNotificationAlert(MenuActivity.this, emergencyMessage, "Emergency");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		};
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.e("", "========== oN CREATE OPTIONS MENU ");
		if (currentFrangment == ALL_CONVERSATION) {
			getMenuInflater().inflate(R.menu.main_menu, menu);
			try {
				SearchView searchView = (SearchView) menu.findItem(
						R.id.action_search).getActionView();
				searchView.setOnQueryTextListener(onAllConQueryTextListener);

                if (this.count>0) {
    				if (!TribeWireConstants.getFyiReadStatus(MenuActivity.this)) {

    					try {
    						View view = (View) menu.findItem(R.id.companyFyiBadge)
    								.getActionView();
    						Button button = (Button) view
    								.findViewById(R.id.btn_badge_value);
    						if (count != -1 && count != 0) {
    							button.setText("" + count);
    							menu.findItem(R.id.companyFyiBadge).setVisible(true);
    							menu.findItem(R.id.companyFyiBadge).getActionView()
    									.setOnClickListener(new OnClickListener() {

    										@Override
    										public void onClick(View v) {
    											currentFrangment = COMPANY_FYI;

    											fragment = new CompanyFYI();
    											getActionBar().setBackgroundDrawable(
    													new ColorDrawable(getResources()
    															.getColor(R.color.blue)));
    											invalidateOptionsMenu();
    											if (fragment != null) {
    												android.app.FragmentManager fragmentManager = getFragmentManager();
    												fragmentManager
    														.beginTransaction()
    														.replace(R.id.frame_container,
    																fragment).commit();

    												// update selected item and title, then
    												// close the drawer

    												setTitle("Company FYi");
    												drawerLayout.closeDrawer(listView);
    											}

    										}
    									});
    						}
    					} catch (Exception e) {
    						e.printStackTrace();
    					}

    				} else {
    					menu.findItem(R.id.companyFyiBadge).setVisible(false);
    				}
				}
                else {
                	menu.findItem(R.id.companyFyiBadge).setVisible(false);
                }
				// if (showCompanyFyiBadge) {
				// menu.findItem(R.id.companyFyiBadge).setVisible(true);
				// }
				// else {
				// menu.findItem(R.id.companyFyiBadge).setVisible(false);
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}

			// mSearchView.setOnQueryTextListener(this);
		} else if (currentFrangment == COMPANY_FYI) {
			getMenuInflater().inflate(R.menu.menu_companyfyi, menu);
			try {
				SearchView searchView = (SearchView) menu.findItem(
						R.id.action_search).getActionView();
				searchView.setOnQueryTextListener(onCompanyFyiListener);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (currentFrangment == CONTACTS) {
			getMenuInflater().inflate(R.menu.menu_contacts, menu);
			try {
				SearchView searchView = (SearchView) menu.findItem(
						R.id.action_search).getActionView();
				searchView.setOnQueryTextListener(onContactsQueryTextListener);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (currentFrangment == EMERGENCY) {

		} else if (currentFrangment == EDIT_PROFILE) {

		}
		return true;
	}

	OnQueryTextListener onAllConQueryTextListener = new OnQueryTextListener() {

		@Override
		public boolean onQueryTextSubmit(String query) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			Log.e("", "========= The String is " + newText);
			if (currentFrangment == ALL_CONVERSATION) {
				 if (newText!=null && newText.length()>0) {
				TenFourConversations tenFourConversations = (TenFourConversations) fragment;
				tenFourConversations.searchThread(newText);
				 }
			}
			return false;
		}
	};
	OnQueryTextListener onCompanyFyiListener = new OnQueryTextListener() {

		@Override
		public boolean onQueryTextSubmit(String query) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			Log.e("", "========= The String is " + newText);
			if (currentFrangment == COMPANY_FYI) {
				// if (newText!=null && newText.length()>0) {
				CompanyFYI companyFyi = (CompanyFYI) fragment;
				companyFyi.searchFyi(newText);
				// }
			}
			return false;
		}
	};

	OnQueryTextListener onContactsQueryTextListener = new OnQueryTextListener() {

		@Override
		public boolean onQueryTextSubmit(String query) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			Log.e("", "========= The String is " + newText);
			try {
				if (currentFrangment == CONTACTS) {
					FrameActivity tenFourConversations = (FrameActivity) fragment;
					tenFourConversations.searchFriends(newText);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return false;
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		if (item.getItemId() == R.id.action_settings) {
			return true;
		} else if (item.getItemId() == R.id.newconversation) {
			Intent intent = new Intent(MenuActivity.this, EyesaySendText.class);
			startActivityForResult(intent, 8);
			return super.onOptionsItemSelected(item);
		} 
		else if (item.getItemId() == R.id.composeFyi) {
			Intent intent = new Intent(MenuActivity.this, ComposeFyi.class);
			startActivityForResult(intent, 2);
			return super.onOptionsItemSelected(item);
		} 
		
		else if (item.getItemId() == R.id.btn_groups) {
			Intent groupIntent = new Intent(MenuActivity.this, AllGroups.class);
			startActivity(groupIntent);
			return super.onOptionsItemSelected(item);
		} else if (item.getItemId() == R.id.companyFyiBadge) {
			// Intent companyFYi = new Intent(MenuActivity.this,
			// CompanyFYI.class);
			// startActivity(companyFYi);
			// TribeWireConstants.showFyiBadge(context, btnBadge)
			return super.onOptionsItemSelected(item);
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = drawerLayout.isDrawerOpen(listView);
		// menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item

			displayView(position);

		}
	}

	static android.app.Fragment fragment = null;

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	public  void displayView(int position) {
		// update the main content by replacing fragments

		switch (position) {
		case 0:
			currentFrangment = EDIT_PROFILE;
			fragment = new AddEditImage();
			getActionBar().setBackgroundDrawable(
					new ColorDrawable(getResources().getColor(R.color.blue)));
			invalidateOptionsMenu();
			break;
		case 1:
			currentFrangment = ALL_CONVERSATION;

			fragment = new TenFourConversations();
			
			try {
				Bundle bundle  = new Bundle();
				bundle.putString("conv_id", notificationConvId);
				bundle.putBoolean("is_emergency", isEmergency);
				fragment.setArguments(bundle);
			} catch (Exception e) {
				e.printStackTrace();
			}
			getActionBar().setBackgroundDrawable(
					new ColorDrawable(getResources().getColor(R.color.nav_allconversation)));
			getActionBar().setTitle("ALL CONVERSATIONS".toUpperCase());
             if (fragment instanceof TenFourConversations) {
				TenFourConversations conversations   = (TenFourConversations)fragment;
				TenFourConversations.isProgressToShow  = false;
			}
			invalidateOptionsMenu();
			break;
		case 2:
			currentFrangment = COMPANY_FYI;

			fragment = new CompanyFYI();
			getActionBar().setBackgroundDrawable(
					new ColorDrawable(getResources().getColor(R.color.blue)));
			invalidateOptionsMenu();
			break;
		case 3:
			currentFrangment = CONTACTS;
			fragment = new FrameActivity();
			getActionBar().setBackgroundDrawable(
					new ColorDrawable(getResources().getColor(R.color.nav_allconversation)));
			invalidateOptionsMenu();

			break;

		case 4:
			currentFrangment = EMERGENCY;
			fragment = new Emergency();
			getActionBar().setBackgroundDrawable(
					new ColorDrawable(getResources().getColor(R.color.red)));
			invalidateOptionsMenu();
			break;
		case 5:

			break;

		default:
			break;
		}

		if (fragment != null) {
			android.app.FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			listView.setItemChecked(position, true);
			listView.setSelection(position);

			setTitle(navMenuTitles[position].toUpperCase());
			drawerLayout.closeDrawer(listView);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
		notificationConvId = null;
		isEmergency = false;
		
	}
	
	private void displayAllConversation() {
		try {
			currentFrangment = ALL_CONVERSATION;
			TenFourConversations.isProgressToShow  = true;
			fragment = new TenFourConversations();
			try {
				Bundle bundle  = new Bundle();
				bundle.putString("conv_id", notificationConvId);
				bundle.putBoolean("is_emergency", isEmergency);
				fragment.setArguments(bundle);
			} catch (Exception e) {
				e.printStackTrace();
			}

	
			getActionBar().setBackgroundDrawable(
					new ColorDrawable(getResources().getColor(R.color.nav_allconversation)));
			getActionBar().setTitle("ALL CONVERSATIONS".toUpperCase());
			invalidateOptionsMenu();
			if (fragment != null) {
				android.app.FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();

				// update selected item and title, then close the drawer
				listView.setItemChecked(1, true);
				listView.setSelection(1);

				setTitle("all conversations".toUpperCase());
				drawerLayout.closeDrawer(listView);
			} else {
				// error in creating fragment
				Log.e("MainActivity", "Error in creating fragment");
			}
			notificationConvId = null;
			isEmergency = false;
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static final int SHOW_PROGRESS = 1;
	public static final int DISMISS_PROGRESS = 2;
	public static final int NO_INTERNET_CONNECTION = 3;
	ProgressDialog progressDialog;
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == SHOW_PROGRESS) {
				progressDialog = ProgressDialog.show(MenuActivity.this,
						"Logging out",
						"Please wait while sign out ......");
				progressDialog.setCancelable(true);
			} else if (msg.what == DISMISS_PROGRESS) {

				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			} else if (msg.what == NO_INTERNET_CONNECTION) {
				Toast.makeText(MenuActivity.this,
						"Please check your internet connectivity",
						Toast.LENGTH_LONG).show();
			}

		};
	};

	/**
	 * 07-31 11:10:21.484: E/(27166):
	 * {"response":{"error":true,"error_msg":"WEB LOGGED IN"}}
	 */

	@Override
	public void onError(String onError) {
		Log.e("", "on Error Response is " + onError);
		try {
			JSONObject jsonObject = new JSONObject(onError);
			JSONObject jsonObject2 = jsonObject.getJSONObject("response");
			String error = jsonObject2.getString("error");
			if (error.equalsIgnoreCase("true")) {
				String errorMsg = jsonObject2.getString("error_msg");
				if (errorMsg.equalsIgnoreCase("WEB LOGGED IN")) {
					handler.sendEmptyMessage(DISMISS_PROGRESS);
					CommonFunctions.CancelAlarm(this);
					CommonFunctions.setPref(this, CommonFunctions.IS_LOGIN,
							false);
					CommonFunctions.setPref(this, Constants.SESSION, "");
					CommonFunctions.setPref(this, CommonFunctions.REF_ID, "");
					CommonFunctions.setPref(this, "count", 0);
					Constants.setValueListNull();
					deleteOldImage();
					try {
						DataBase db = new DataBase(this);
						db.open();
						db.clean();
						db.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(this, Login.class);
					startActivity(intent);
					removeFromPrefrence();
					finish();
				//	unregisterInBackground();
					
				} else {
					Toast.makeText(this,
							"Error while sign out , Please try again later",
							Toast.LENGTH_LONG).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void deleteOldImage() {
		try {
			try {
				String path = CommonFunctions.getPref(this,
						AddEditImage.LOCAL_PROFILE_PIC, "");
				if (path != null && path.length() > 0) {
					File image = new File(path);
					if (image.exists()) {
						image.delete();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onResponse(String response) {
		handler.sendEmptyMessage(DISMISS_PROGRESS);
		CommonFunctions.CancelAlarm(this);
		CommonFunctions.setPref(this, CommonFunctions.IS_LOGIN, false);
		CommonFunctions.setPref(this, Constants.SESSION, "");
		CommonFunctions.setPref(this, CommonFunctions.REF_ID, "");
		CommonFunctions.setPref(this, "count", 0);
		Constants.setValueListNull();
		deleteOldImage();
		try {
			DataBase db = new DataBase(this);
			db.open();
			db.clean();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//unregisterInBackground();
		removeFromPrefrence();
		Intent intent = new Intent(this, Login.class);
		startActivity(intent);
		finish();

	}

	private SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return this.getSharedPreferences(DemoActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	public static final String PROPERTY_REG_ID = "registration_id";

	private void removeFromPrefrence() {
		final SharedPreferences prefs = getGcmPreferences(MenuActivity.this);

		try {
			SharedPreferences.Editor editor = prefs.edit().putString(
					PROPERTY_REG_ID, "");
			// editor.putInt(PROPERTY_APP_VERSION, appVersion);
			editor.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void notifyAdapter() {

		 //navDrawerListAdapter.notifyDataSetChanged();

	}

	public void invalidateMenu(int count) {
		// showCompanyFyiBadge = true;
		this.count = count;
		invalidateOptionsMenu();
	}

	String SENDER_ID = "965404619941";
	GoogleCloudMessaging gcm;

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void unregisterInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					try {
						Log.e("",
								"=============== Device unregistering ------- ==================");
						GoogleCloudMessaging.getInstance(MenuActivity.this)
								.unregister();
						Log.e("",
								"=============== Device unregistered ==================");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {

				Log.e("",
						"=============================== The Registration Id "
								+ msg);
			}
		}.execute(null, null, null);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		   if (requestCode==2 && resultCode == RESULT_OK) {
			   try {
				        if (fragment instanceof CompanyFYI) {
							CompanyFYI  companyFYI  = (CompanyFYI)fragment;
							companyFYI.getAllMessages();
						}
				   
			} catch (Exception e) {
				e.printStackTrace();
				
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
