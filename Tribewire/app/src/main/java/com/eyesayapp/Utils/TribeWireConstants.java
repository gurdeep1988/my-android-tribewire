package com.eyesayapp.Utils;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class TribeWireConstants {

	public static final String EMERGENCY_URL = "api/get_admin_emergency_group";
	public static final String FORGOTPASSWORD_URL = "api/request_password_reset_home";
	public static final String ADMIN_GROUP_URL = "api/get_admin_groups_list";
	public static final String PROFILE_PIC_URL = "api/set_profile_pic";
	public static final String COMAPANY_FYI_URL = "api/get_company_fyi";
	public static final String ADD_PARTICIPANTS  = "api/add_recipients_conversation";
	public static final String COMPOSE_FYI_URL = "api/send_fyi_message";
	
	public static final String DELETE_GROUP_URL = "api/delete_group";
	

	public static boolean getFyiReadStatus(Context context) {
		boolean isRead = false;
		try {
			isRead = CommonFunctions.getPref(context, "isread", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isRead;

	}

	public static boolean isFyiMenuFirstTime(Context context) {
		boolean isFyiFirst = false;
		try {
			isFyiFirst = CommonFunctions.getPref(context, "isfyifirst", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isFyiFirst;

	}

	public static void setFyiMenuFirstTimeTrue(Context context) {

		try {
			CommonFunctions.setPref(context, "isfyifirst", true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void setFyiMenuFirstTimeFalse(Context context) {

		try {
			CommonFunctions.setPref(context, "isfyifirst", false);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static void fyiRead(Context c) {
		try {
			CommonFunctions.setPref(c, "isread", true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void fyiUnread(Context c) {
		try {
			CommonFunctions.setPref(c, "isread", false);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void showFyiBadge(final Context context, final Button btnBadge) {
		try {
			new Handler().post(new Runnable() {

				@Override
				public void run() {
					if (!TribeWireConstants.getFyiReadStatus(context)) {
						btnBadge.setVisibility(View.VISIBLE);
					}
					else {
						btnBadge.setVisibility(View.GONE);
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
