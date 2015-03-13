package com.tribewire.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.DataBase;
import com.eyesayapp.Utils.TribeWireConstants;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.asyn.IHttpRequest;
import com.eyesayapp.asyn.RestApiCall;
import com.eyesayapp.model.CompanyMessages;

import eyesay.adapter.CompanyFyiAdapter;
import eyesay.adapter.SimpleAdapter;

public class CompanyFYI extends Fragment implements OnItemClickListener,
		IHttpRequest, OnClickListener {

	ListView lstCompanyFYI;
	CompanyFyiAdapter companyFyiAdapter;
	ProgressDialog pd;
	List<CompanyMessages> companyMessages;
	Button btnBack;
	private Button btnSearch;
	private RelativeLayout rltSearchBar;
	private TextView txtCancelSearch;
	private EditText edtSearchText;
	private RelativeLayout rltMenuLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_companyfyi, null);
		// return super.onCreateView(inflater, container, savedInstanceState);

		lstCompanyFYI = (ListView) view.findViewById(R.id.list_companyFyi);
		btnBack = (Button) view.findViewById(R.id.btn_back);
		btnSearch = (Button) view.findViewById(R.id.btn_search);
		rltSearchBar = (RelativeLayout) view.findViewById(R.id.rlt_search_bar);
		txtCancelSearch = (TextView) view.findViewById(R.id.txt_search_cancel);
		edtSearchText = (EditText) view.findViewById(R.id.edt_search_contact);
		rltMenuLayout = (RelativeLayout) view
				.findViewById(R.id.rlt_menu_layout);
		btnSearch.setOnClickListener(searchListener);
		txtCancelSearch.setOnClickListener(searchListener);
		lstCompanyFYI.setOnItemClickListener(this);
		btnBack.setOnClickListener(this);
		lstCompanyFYI.setAdapter(companyFyiAdapter);
//		if (!TribeWireConstants.isFyiMenuFirstTime(getActivity())) {
//			TribeWireConstants.setFyiMenuFirstTimeTrue(getActivity());
//		}
		getAllMessages();
		return view;
	}

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	//
	// setContentView(R.layout.activity_companyfyi);
	//
	// lstCompanyFYI = (ListView) findViewById(R.id.list_companyFyi);
	// btnBack = (Button) findViewById(R.id.btn_back);
	// btnSearch = (Button) findViewById(R.id.btn_search);
	// rltSearchBar = (RelativeLayout) findViewById(R.id.rlt_search_bar);
	// txtCancelSearch = (TextView) findViewById(R.id.txt_search_cancel);
	// edtSearchText = (EditText) findViewById(R.id.edt_search_contact);
	// rltMenuLayout = (RelativeLayout) findViewById(R.id.rlt_menu_layout);
	// btnSearch.setOnClickListener(searchListener);
	// txtCancelSearch.setOnClickListener(searchListener);
	// lstCompanyFYI.setOnItemClickListener(this);
	// btnBack.setOnClickListener(this);
	// lstCompanyFYI.setAdapter(companyFyiAdapter);
	//
	// if (!TribeWireConstants.isFyiMenuFirstTime(this)) {
	// TribeWireConstants.setFyiMenuFirstTimeTrue(CompanyFYI.this);
	// }
	//
	// getAllMessages();
	// super.onCreate(savedInstanceState);
	// }

	OnClickListener searchListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v == btnSearch) {
				showSearchBar();
			} else if (v == txtCancelSearch) {
				hideSearchBar();
				handler.sendEmptyMessage(SHOW_MESSAGE);
			}
		}
	};

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

	@Override
	public void onClick(View v) {
		try {
			if (v == btnBack) {
				// finish();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	String selectedConv = "";

	private void updateReadValue(String conID) {
		try {
			List<String> nameList = new ArrayList<String>();
			nameList.add("api_key");
			nameList.add("conv_id");
			List<String> valueList = new ArrayList<String>();
			valueList.add(Constants.API_KEY);
			valueList.add(conID);
			new RestApiCall(
					getActivity(),
					CommonFunctions.DEMO_SERVER_URL + CommonFunctions.READ_CONV,
					nameList, valueList).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	String conID   = "";
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {

		if (companyMessages != null && companyMessages.size() > 0) {

			setStatusOne(companyMessages.get(arg2).getConversationId());

			CompanyMessages messages = companyMessages.get(arg2);
			messages.setReadStatus(1);

			companyFyiAdapter.changeData(companyMessages);
//			
//			Intent intent = new Intent(getActivity(), EyesaySendText.class);
//
//			intent.putExtra("receiver", companyMessages.get(arg2));
//
//			startActivity(intent);

			try {
				Intent intent = new Intent(getActivity(), ConversationMessages.class);
				selectedConv   = companyMessages.get(arg2).getConversationId();
				intent.putExtra("conid", companyMessages.get(arg2).getConversationId());
				intent.putExtra("subject", companyMessages.get(arg2).getSubject());
				intent.putExtra("fyireply", true);
				
				startActivityForResult(intent, 11);
			} catch (Exception e) {
				e.printStackTrace();
			}
			 conID  = companyMessages.get(arg2).getConversationId();
			updateReadValue(conID);
			
		}

	}

	private void setStatusOne(String conversationId) {
		DataBase db = null;
		try {
			db = new DataBase(getActivity());
			db.open();
			ContentValues cv = new ContentValues();
			cv.put(DataBase.STATUS, 1);
			boolean isUpdated = db.update(DataBase.Company_Fyi_table,
					DataBase.Company_Fyi_int, DataBase.CONVERSATION_ID + " = '"
							+ conversationId + "'", cv);
			Log.e("", "============ Is Status  updated " + isUpdated);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

	}

  public Handler  handler2   = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			getAllMessages();
		};
		
	};
	
	public void getAllMessages() {
		try {
			if (CommonFunctions.isInternetConnected(getActivity())) {
//				pd = ProgressDialog.show(getActivity(), "Loading...",
//						"Please wait while fetching your messages");
//				pd.setCancelable(true);
				List<String> nameList = null;
				nameList = new ArrayList<String>();
				nameList.add(Constants.API_KEY_TAG);
				nameList.add(Constants.REF_ID_TAG);
				nameList.add(Constants.COMP_ID_TAG);
				nameList.add(Constants.SESSION_TAG);
				// new RestApiCall(this, CommonFunctions.DEMO_SERVER_URL
				// + TribeWireConstants.COMAPANY_FYI_URL, nameList,
				// Constants.getValueList(this)).execute();
				
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

	private static final int SHOW_MESSAGE = 1;
	private static final int SHOW_SEARCH_MESSAGE = 2;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == SHOW_MESSAGE) {
				if (companyMessages != null && companyMessages.size() > 0) {
					companyFyiAdapter = new CompanyFyiAdapter(getActivity(),
							companyMessages);
					// TribeWireConstants.setFyiMenuFirstTimeFalse(CompanyFYI.this);
					TribeWireConstants.fyiRead(getActivity());
					lstCompanyFYI.setAdapter(companyFyiAdapter);
				}
			}

			else if (msg.what == SHOW_SEARCH_MESSAGE) {
				if (tempCompanyMsgList != null && tempCompanyMsgList.size() > 0) {
					companyFyiAdapter = new CompanyFyiAdapter(getActivity(),
							tempCompanyMsgList);
					// TribeWireConstants.setFyiMenuFirstTimeFalse(CompanyFYI.this);
					TribeWireConstants.fyiRead(getActivity());
					lstCompanyFYI.setAdapter(companyFyiAdapter);
				} else {
//					lstCompanyFYI.setAdapter(new SimpleAdapter(getActivity(),
//							"No FYi Found"));
				}
			}

		};

	};

public	IHttpRequest  httpRequest   = new IHttpRequest() {
		
		@Override
		public void onResponse(String response) {
			Log.e("", "============= The read response is " + response);
			DataBase db = null;
			try {
				db = new DataBase(getActivity());
				db.open();
				String where = "conversation_Id = '" + conID+"'";
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
		
		@Override
		public void onError(String onError) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public void onResponse(String response) {
		Log.e("", "== Response" + response);
		companyMessages = TribeWireUtil.parseCompanyFyiResponse(getActivity(),
				response);
		Log.e("", "============== The COmpany FYI Count is " + companyMessages);
		handler.sendEmptyMessage(SHOW_MESSAGE);
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}

	}

	@Override
	public void onError(String onError) {

	}

	ArrayList<CompanyMessages> tempCompanyMsgList = null;

	public  void searchFyi(String value) {
		try {
			if (companyMessages != null && companyMessages.size() > 0) {
				tempCompanyMsgList = new ArrayList<CompanyMessages>();
				for (int i = 0; i < companyMessages.size(); i++) {
					CompanyMessages cmpMsg = companyMessages.get(i);
					Log.e("", "==================Recipient Name "+cmpMsg.getSenderName());
					if (cmpMsg.getSenderName().toLowerCase().startsWith(value.toLowerCase())) {
						tempCompanyMsgList.add(cmpMsg);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			handler.sendEmptyMessage(SHOW_SEARCH_MESSAGE);
		}

	}
	
	
	@Override
	public void onPause() {
		ActivityStatus.setActivity(null);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		try {
			 if (selectedConv!=null && selectedConv.length()>0) {
				deleteFyiMessages();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ActivityStatus.setActivity(getActivity());
		super.onResume();
	}
	
	private void deleteFyiMessages() {
		try {
			DataBase db = new DataBase(getActivity());
			db.open();
			boolean isDelete = db.delete(
					DataBase.Receive_table,
					DataBase.Receive_int,
					"conversation_Id ='"
							+ selectedConv + "'");
			Log.e("",
					"============== Table Deleted "
							+ isDelete);
		} catch (Exception e) {
			// TODO: handle exceptionresend

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		   if (resultCode ==Activity.RESULT_OK) {
			   getAllMessages();
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
