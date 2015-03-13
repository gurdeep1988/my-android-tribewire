/**
 * Class name : GetEventParams
 * version    : 0.1.1
 * Purpose    : creates calls & message event's parameters for sending to server
 */

package com.eyesayapp.Utils;

import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TimeUtils;

public class XMLRequestBuilder {

   public static final String REQUEST = "tfreq";
   public static final String REGNAME = "name";
   public static final String REGNUMBER = "phone";
   public static final String REGEAIL = "emailid";
   public static final String NUMBER = "phone";
   public static final String EMAIL = "emails";
   public static final String REF_ID = "refid";
   public static final String SENDER = "sender";
   public static final String RECEIVER = "receiver";
   public static final String TYPE = "message_type";
   public static final String FILE_URL = "file_url";
   public static final String TIME = "date_time";
   public static final String CON_ID = "conversation_Id";
   public static final String TEXT_MESSAGE = "textMessage";
   // Added By Gurdeep 
   public static final String PASSWORD = "password";
   
   
   /**
    * Creates XML parameters
    * 
    * @param
    * 
    * 
    * @param
    * 
    * @param
    * @return a byte array
    */
   public static byte[] RegistrationByteArray(String name, String mobileNo, String email,String password, Context context) {

	   //mobileNo = "00919878263647";
	    Log.i("", "================== The PHONE NUMBER IS "+mobileNo);
	   
	    
      StringBuffer sb = new StringBuffer();
      sb.append("str=");
      sb.append("<" + REQUEST + ">");
      sb.append("<" + REGNAME + ">" + name + "</" + REGNAME + ">");
      
      sb.append("<" + REGNUMBER + ">" + Base64.encode(CommonFunctions.convertHex(mobileNo).getBytes()) + "</" + REGNUMBER + ">");
      sb.append("<" + REGEAIL + ">" + email + "</" + REGEAIL + ">");
      sb.append("<device>android</device>");
      sb.append("<NetworkType>" + getNetworkTypeString(context) + "</NetworkType>");
      //sb.append("<OS>" + getOSVersion() + "</OS>");
      sb.append("<OS>"+getOSVersion()+"</OS>");
      
      sb.append("<"+PASSWORD+">"+password+"</"+PASSWORD+">");
      sb.append("<version_type>1</version_type>");
      //sb.append("<imei>" + getIMEINo(context) + "</imei>");
     sb.append("<imei>" + "98242251111122598799" + "</imei>");

         sb.append("<serviceprovider>" + getProviderName(context) + "</serviceprovider>");
    //  Random rand = new Random();
//      String rndNumber =Math.abs((long)((long)1000000 +  (long)(rand.nextInt(8999999))))+"";
//      String rndNumber1 =Math.abs((long)((long)1000000 +  (long)(rand.nextInt(8999999))))+"";
//     sb.append("<imei>" + rndNumber1+"2511"+rndNumber + "</imei>");
    //  sb.append("<serviceprovider>" + getProviderName(context) + "</serviceprovider>");
      sb.append("</" + REQUEST + ">");

      return sb.toString().getBytes();
   }

   static String getDeviceName() {
      return android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
   }

   public static String getProviderName(Context c) {
      TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
      return tm.getSimOperatorName();
   }

   public static String getNetworkTypeString(Context c) {
      TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
      if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_GSM)
         return "CDMA";
      else
         return "GSM";
   }

   public static String getIMEINo(Context context) {
      TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      return tm.getDeviceId().toString();
   }

   public static String getOSVersion() {
      switch (Integer.parseInt(Build.VERSION.SDK)) {
         case 3:
            return "1.5";
         case 4:
            return "1.6";
         case 5:
            return "2.0";
         case 6:
            return "2.0.1";
         case 7:
            return "2.1";
         case 8:
            return "2.2";
         case 9:
            return "2.3";
         case 10:
            return "2.3.3";
         case 11:
            return "3.0";
         case 12:
            return "3.2";
         case 15:
             return "4.0.3";
         case 17:
             return "4.2";

         default:
            return "2.0";
      }
   }

   static String getApplicationVersion(Context context) {
      PackageInfo pinfo;
      try {
         pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
         return pinfo.versionName;

      } catch (NameNotFoundException e) {
         e.printStackTrace();
      }
      return "0";
   }

   public static byte[] ActiveEmailsByteArray(String email, Context context) {
      StringBuffer sb = new StringBuffer();
      Integer id = CommonFunctions.getPref(context, CommonFunctions.REF_ID, 0);
      sb.append("str=");
      sb.append("<" + REQUEST + ">");
      sb.append("<" + REF_ID + ">" + id + "</" + REF_ID + ">");
      sb.append("<" + EMAIL + ">" + email + "</" + EMAIL + ">");
      sb.append("</" + REQUEST + ">");
      return sb.toString().getBytes();
   }

   public static byte[] ActiveFriendsByteArray(String number, Context context) {
      StringBuffer sb = new StringBuffer();
      Integer id = CommonFunctions.getPref(context, CommonFunctions.REF_ID, 0);
      sb.append("str=");
      sb.append("<" + REQUEST + ">");
      sb.append("<" + REF_ID + ">" + id + "</" + REF_ID + ">");
      sb.append("<" + NUMBER + ">" + Base64.encode(CommonFunctions.convertHex(number).getBytes()) + "</" + NUMBER + ">");
      sb.append("</" + REQUEST + ">");
      return sb.toString().getBytes();
   }
   public static byte[] GsActiveFriendsByteArray(String nameNumJsonArray, Context context) {
	      StringBuffer sb = new StringBuffer();
	      Integer id = CommonFunctions.getPref(context, CommonFunctions.REF_ID, 0);
	      sb.append("str=");
	      sb.append(nameNumJsonArray);
	      
	      return sb.toString().getBytes();
	   }

   /**
    * 
    * @param password
    * @param email
    * @param context
    * @return
    * 
    * <tfreq><email>%@</email><password>%@</password></tfreq>
    */
   public static  byte[]  sendForgotPassword (String password,String email, Context context) {
	      StringBuffer sb = new StringBuffer();	      
	      sb.append("str=");
	      sb.append("<" + REQUEST + ">");
	      sb.append("<email>" + email + "</email>");
	      sb.append("<password>" + password +"</password>");
	      sb.append("</" + REQUEST + ">");
	      return sb.toString().getBytes();
	   }
   /**
    * 
    * @param useid
    * @param sender
    * @param receiver
    * @param message_type
    * @param file_url
    * @param date_time
    *           11/03/2010 02:15:00
    * 
    */
   public static byte[] SendTextMessageByteArray(String sender, String Receiver, String MessageType, String File_Url, String conversationId,String textmessage,
            Context context) 
   {      
      //String date= (String) DateFormat.format("kk:mm:ss MM/dd/yyyy", new Date());


		Calendar calendar = Calendar.getInstance();
		calendar.set(2001, 0, 1, 00, 00, 00);
		long two_thou_millis = calendar.getTimeInMillis();		
		Log.e("", "The 2000 date is "+new Date(two_thou_millis));
	
		Calendar calendar2  = Calendar.getInstance();		
		Date nowDate = new Date(calendar2.getTimeInMillis());
        long today_millis = calendar2.getTimeInMillis();
		
        long required = (today_millis-two_thou_millis)/1000;
        Log.e(""," Here Required Value is  ===="+required);
		//int diffInDays = determineDifferenceInDays(new Date(two_thou_millis), nowDate);
		//System.out.println("Number of days betweens dates: " + diffInDays);
		
       int dstOffset = (calendar2.get(Calendar.DST_OFFSET)/1000);
       Log.e("", "==== DST OFFSET======="+(calendar2.get(Calendar.DST_OFFSET)/1000));
       Log.e("", "==== ZONE OFFSET=========="+(calendar2.get(Calendar.ZONE_OFFSET)/1000));
	   TimeZone tz = TimeZone.getDefault();
	   int offsetFromUtc = tz.getOffset(nowDate.getTime());
	   long requiredOffset  = offsetFromUtc/1000;
		
		requiredOffset  = requiredOffset+dstOffset;
		System.out.println("Offset=========="+requiredOffset);
		required = (required-requiredOffset);	   
	   
	   StringBuffer sb = new StringBuffer();
      
      String id = CommonFunctions.getPref(context, CommonFunctions.REF_ID,"");
      sb.append("str=");
      sb.append("<" + REQUEST + ">");
      sb.append("<" + REF_ID + ">" + id + "</" + REF_ID + ">");
      sb.append("<" + SENDER + ">" + Base64.encode(CommonFunctions.convertHex(sender).getBytes()) + "</" + SENDER + ">");
   // Code added by Gurdeep Singh on 7th November   
      
      try {
    	  
    		
			if (Receiver.endsWith(",")) {
				Receiver = Receiver.substring(0, Receiver.length()-1);
			} 
			
			sb.append("<" + RECEIVER + ">");
			
			sb.append(Base64.encode(CommonFunctions.convertHex(Receiver).getBytes()));
			
			sb.append("</" + RECEIVER + ">");
//			  while (stringTokenizer.hasMoreElements()) {
//                  name  = stringTokenizer
//							.nextElement().toString();
//                  sb.append("<" + RECEIVER + ">" + Base64.encode(CommonFunctions.convertHex(name).getBytes()) + "</" + RECEIVER + ">");
//                  }
	} catch (Exception e) {
		sb.append("<" + RECEIVER + ">" + Base64.encode(CommonFunctions.convertHex(Receiver).getBytes()) + "</" + RECEIVER + ">");
		e.printStackTrace();
	}
      sb.append("<" + TEXT_MESSAGE + ">" + textmessage + "</" + TEXT_MESSAGE + ">");
      sb.append("<" + TYPE + ">" + MessageType + "</" + TYPE + ">");
      sb.append("<" + FILE_URL + ">" + File_Url + "</" + FILE_URL + ">");
      sb.append("<" + CON_ID + ">" + conversationId + "</" + CON_ID + ">");
      sb.append("<" + TIME + ">" + required + "</" + TIME + ">");
      sb.append("</" + REQUEST + ">");
      return sb.toString().getBytes();
   }
   
   
   /**
    * 
    * @param useid
    * @param sender
    * @param receiver
    * @param message_type
    * @param file_url
    * @param date_time
    *           11/03/2010 02:15:00
    * 
    */
   public static byte[] SendMessageByteArray(String sender, String Receiver, String MessageType, String File_Url, String conversationId,
            Context context) 
   {      
      //String date= (String) DateFormat.format("kk:mm:ss MM/dd/yyyy", new Date());

	   
		/*Calendar calendar = Calendar.getInstance();
		calendar.set(2001, 0, 1, 00, 00, 00);
		long two_thou_millis = calendar.getTimeInMillis();
		
		Calendar calendar2  = Calendar.getInstance();
		Date nowDate = new Date(calendar2.getTimeInMillis());
        long today_millis = calendar2.getTimeInMillis();
        
		
        long required = (today_millis-two_thou_millis)/1000;
        Log.e(""," Here Required Value is  ===="+required+"The Date is "+nowDate);
        int dstOffset = (calendar2.get(Calendar.DST_OFFSET)/1000);
        Log.e("", "==== DST OFFSET======="+(calendar2.get(Calendar.DST_OFFSET)/1000));
        Log.e("", "==== ZONE OFFSET=========="+(calendar2.get(Calendar.ZONE_OFFSET)/1000));
 	   
        
        
		TimeZone tz = TimeZone.getDefault();		
		int offsetFromUtc = tz.getOffset(today_millis);
		long requiredOffset  = offsetFromUtc/1000;
		requiredOffset  = offsetFromUtc+dstOffset;
		System.out.println("Offset=========="+requiredOffset);
		required = (required-requiredOffset);*/
		Calendar calendar = Calendar.getInstance();
		calendar.set(2001, 0, 1, 00, 00, 00);
		long two_thou_millis = calendar.getTimeInMillis();		
		Log.e("", "The 2000 date is "+new Date(two_thou_millis));
	
		Calendar calendar2  = Calendar.getInstance();		
		Date nowDate = new Date(calendar2.getTimeInMillis());
        long today_millis = calendar2.getTimeInMillis();
		
        long required = (today_millis-two_thou_millis)/1000;
        Log.e(""," Here Required Value is  ===="+required);
		//int diffInDays = determineDifferenceInDays(new Date(two_thou_millis), nowDate);
		//System.out.println("Number of days betweens dates: " + diffInDays);
		
       int dstOffset = (calendar2.get(Calendar.DST_OFFSET)/1000);
       Log.e("", "==== DST OFFSET======="+(calendar2.get(Calendar.DST_OFFSET)/1000));
       Log.e("", "==== ZONE OFFSET=========="+(calendar2.get(Calendar.ZONE_OFFSET)/1000));
	   TimeZone tz = TimeZone.getDefault();
	   int offsetFromUtc = tz.getOffset(nowDate.getTime());
	   long requiredOffset  = offsetFromUtc/1000;
		
		requiredOffset  = requiredOffset+dstOffset;
		System.out.println("Offset=========="+requiredOffset);
		required = (required-requiredOffset);	   
		
//		Log.e(""," Date for 2001 Millis is ===="+new Date(two_thou_millis));
//		Log.e(""," Date for Current  Millis is ===="+new Date(today_millis));
//		Log.e(""," ============================================================ ");
//		
//		Log.e("","2001 Millis is ========================"+ two_thou_millis);
//		Log.e(""," Current Time Millis are  ===="+today_millis);
//		Log.e(""," Required Millis are    Millis is ===="+required);
//		
		    //(X days * 24 * 60 * 60) + (current hours in current time * 60 * 60) +  (current mins in current time * 60 ) +  (current seconds in current time)
		 //Log.e("", "Calender Hour "+calendar2.get(Calendar.HOUR)+" Minute "+calendar2.get(Calendar.MINUTE)+" second "+calendar2.get(Calendar.SECOND));
		 //Log.e("", "NowDate Hour "+nowDate.getHours()+" NowDate Minutes"+nowDate.getMinutes()+"NowDate second "+nowDate.getSeconds());
		 //long required =(((diffInDays*24*60*60)+(((nowDate.getHours()-5))*(60*60)))+((nowDate.getMinutes()-30)*60)+(nowDate.getSeconds()));
		 //long required =(((diffInDays*24*60*60)+(((nowDate.getHours()))*(60*60)))+((nowDate.getMinutes())*60)+(nowDate.getSeconds()));
		 //System.out.println("=========================================== ");
		 //System.out.println("Required Before "+required);
		 //required = (required-requiredOffset);
		 //System.out.println("Required After"+required);
		 //System.out.println("=========================================== ");
		 //Log.e("", "Date Sending to Server is "+new Date(((System.currentTimeMillis())+(required*1000))));
	   StringBuffer sb = new StringBuffer();
      
      Integer id = CommonFunctions.getPref(context, CommonFunctions.REF_ID, 0);
      sb.append("str=");
      sb.append("<" + REQUEST + ">");
      sb.append("<" + REF_ID + ">" + id + "</" + REF_ID + ">");
      sb.append("<" + SENDER + ">" + Base64.encode(CommonFunctions.convertHex(sender).getBytes()) + "</" + SENDER + ">");
   // Code added by Gurdeep Singh on 7th November   
      
      try {
    	  
    		
			if (Receiver.endsWith(",")) {
				Receiver = Receiver.substring(0, Receiver.length()-1);
			} 
			
			sb.append("<" + RECEIVER + ">");
			
			sb.append(Base64.encode(CommonFunctions.convertHex(Receiver).getBytes()));
			
			sb.append("</" + RECEIVER + ">");
//			  while (stringTokenizer.hasMoreElements()) {
//                  name  = stringTokenizer
//							.nextElement().toString();
//                  sb.append("<" + RECEIVER + ">" + Base64.encode(CommonFunctions.convertHex(name).getBytes()) + "</" + RECEIVER + ">");
//                  }
	} catch (Exception e) {
		sb.append("<" + RECEIVER + ">" + Base64.encode(CommonFunctions.convertHex(Receiver).getBytes()) + "</" + RECEIVER + ">");
		e.printStackTrace();
	}
      
      sb.append("<" + TYPE + ">" + MessageType + "</" + TYPE + ">");
      sb.append("<" + FILE_URL + ">" + File_Url + "</" + FILE_URL + ">");
      sb.append("<" + CON_ID + ">" + conversationId + "</" + CON_ID + ">");
      sb.append("<" + TIME + ">" + required + "</" + TIME + ">");
      sb.append("</" + REQUEST + ">");
      return sb.toString().getBytes();
   }

   public static void writeDataOnFile(String fileName, String str) {
      try {
         FileWriter f = new FileWriter(fileName, true);
         f.write(str);
         f.write("\n\n");
         f.flush();
         f.close();
      } catch (Exception e) {
         debug("ERROR IN WRITE FILE");
      }
   }

   public static void debug(String msg) {
      Debugger.debugE("GetEventParams " + msg);
   }
   private static int determineDifferenceInDays(Date date1, Date date2) {
	    Calendar calendar1 = Calendar.getInstance();
	    calendar1.setTime(date1);
	    Calendar calendar2 = Calendar.getInstance();
	    calendar2.setTime(date2);
	    long diffInMillis = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
	    return (int) (diffInMillis / (24* 1000 * 60 * 60));
	}
}