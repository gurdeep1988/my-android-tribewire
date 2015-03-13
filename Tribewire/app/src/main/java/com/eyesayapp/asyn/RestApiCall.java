package com.eyesayapp.asyn;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.Contacts.GroupMembership;

import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.TribeWireConstants;
import com.tribewire.app.AddParticipants;
import com.tribewire.app.AllGroups;
import com.tribewire.app.CompanyFYI;
import com.tribewire.app.ComposeFyi;
import com.tribewire.app.ConversationMessages;
import com.tribewire.app.Emergency;
import com.tribewire.app.EyesaySendText;
import com.tribewire.app.GroupMembers;
import com.tribewire.app.Login;
import com.tribewire.app.MenuActivity;
import com.tribewire.app.TenFourConversations;

import eyesay.adapter.AddParticipantAdp;

//public class RestApiCall extends AsyncTask<Void, Void, String> {
public class RestApiCall extends Thread {
	List<String> nameList;
	List<String> valueList;
	String url;
	Context context;
	Fragment conversations;

	public RestApiCall(Context context, String url, List<String> nameList,
			List<String> valueList) {
		this.context = context;
		this.url = url;
		this.nameList = nameList;
		this.valueList = valueList;
	}

	public RestApiCall(Fragment context, String url, List<String> nameList,
			List<String> valueList) {
		this.conversations = context;
		this.url = url;
		this.nameList = nameList;
		this.valueList = valueList;
	}

	@Override
	public void run() {
		Looper.prepare();
		doInBackground();
		Looper.loop();
		super.run();
	}

	protected String doInBackground() {
		// @Override
		// protected String doInBackground(Void... params) {

		onPostExecute(CommonFunctions.postNameValueDataOnServer(context,
				nameList.size(), nameList, valueList, url));
//		return CommonFunctions.postNameValueDataOnServer(context,
//				nameList.size(), nameList, valueList, url);

		return "";
		// return CommonFunctions.postData(context,
		// nameList.size(), nameList, valueList, url);

	}

	protected void onPostExecute(String result) {
		if (context != null) {

			// @Override
			// protected void onPostExecute(String result) {
			if (context instanceof Login
					&& url.endsWith(TribeWireConstants.FORGOTPASSWORD_URL)) {
				((Login) context).forgotPassWord.onResponse(result);
			
			}
			
			else if (context instanceof EyesaySendText
					&& url.endsWith(CommonFunctions.DEMO_SERVER_URL)) {
				((EyesaySendText) context).readHttpRequest.onResponse(result);
			} 
			else if (context instanceof Login
					&& url.endsWith(TribeWireConstants.ADMIN_GROUP_URL)) {
				((Login) context).adminGroupRequest.onResponse(result);
			} 
			else if (context instanceof AddParticipants
					&& url.endsWith(TribeWireConstants.ADD_PARTICIPANTS)) {
				((AddParticipants) context).particpantsResponse.onResponse(result);
			}
			else if (context instanceof ConversationMessages
					&& url.endsWith(TribeWireConstants.ADD_PARTICIPANTS)) {
				((ConversationMessages) context).particpantsResponse.onResponse(result);
			} 
			else if (context instanceof AllGroups
					&& url.endsWith(TribeWireConstants.DELETE_GROUP_URL)) {
				((AllGroups) context).onResponse(result);
			}
			else if (context instanceof ComposeFyi
					&& url.endsWith(TribeWireConstants.COMPOSE_FYI_URL)) {
				((ComposeFyi) context).composeFyi.onResponse(result);
			} 
			
			else if (context instanceof Login
					&& url.endsWith(TribeWireConstants.PROFILE_PIC_URL)) {
				((Login) context).adminGroupRequest.onResponse(result);
			}
			else if (context instanceof ConversationMessages
					&& url.endsWith(CommonFunctions.READ_CONV)) {
				((ConversationMessages) context).onResponse(result);
			}
			else if (context instanceof ConversationMessages
					&& url.endsWith(CommonFunctions.DELETE_USER_MESSAGE)) {
				try {
					((ConversationMessages) context).ondeleteMessageResponse(result, valueList.get(3));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// else if (conversations instanceof TenFourConversations
			// && url.endsWith(TribeWireConstants.COMAPANY_FYI_URL)) {
			//
			// ((TenFourConversations) conversations)
			// .onCompanyFyiResponse(result);
			// }
			else if (context instanceof GroupMembers
					&& url.endsWith(CommonFunctions.GET_GROUP_MEMBER_LIST)) {
				((GroupMembers) context).onResponse(result);
			}

			// super.onPostExecute(result);

		} else if (conversations != null) {
			if (conversations instanceof Fragment
					&& conversations instanceof Emergency
					&& url.endsWith(TribeWireConstants.EMERGENCY_URL)) {
				Emergency emergency = (Emergency) conversations;
				emergency.onResponse(result);
			} else if (conversations instanceof Fragment
					&& conversations instanceof TenFourConversations
					&& url.endsWith(TribeWireConstants.COMAPANY_FYI_URL)) {
				TenFourConversations conversations1 = (TenFourConversations) conversations;
				conversations1.onCompanyFyiResponse(result);
			} else if (conversations instanceof CompanyFYI
					&& url.endsWith(TribeWireConstants.COMAPANY_FYI_URL)
					&& conversations instanceof CompanyFYI) {
				((CompanyFYI) conversations).onResponse(result);
			}

		}

	}

}
