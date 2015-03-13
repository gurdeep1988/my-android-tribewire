package com.eyesayapp.Utils;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase {

	
	/**
	 * "(_id integer primary key autoincrement," + "" + "sender text not null,"
	 * + "senderId text not null," + "" + "" + "date_time_long text not null," +
	 * "" + "status integer," + "conversation_Id text," + "" + "" +
	 * "textmessage varchar(500)," + "" + ");";
	 */
	public static final String FYI_ID="_id";
	public static final String SENDER="sender";
	public static final String SENDER_ID="senderId";
	public static final String DATE_TIME_LONG="date_time_long";
	public static final String STATUS="status";
	public static final String CONVERSATION_ID="conversation_Id";
	public static final String TEXT_MESSAGE="textmessage";
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_1_CREATE);
			db.execSQL(TABLE_2_CREATE);
			db.execSQL(TABLE_3_CREATE);
			db.execSQL(TABLE_4_CREATE);
			db.execSQL(TABLE_5_CREATE);
			db.execSQL(TABLE_6_CREATE);
			db.execSQL(TABLE_7_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			try {
				db.execSQL(TABLE_3_ALTER);
				db.execSQL(TABLE_4_ALTER);

				Log.e("",
						"====================================The Database Updated Succssfully=========================================");
			} catch (Exception e) {

			}
		}

	}

	private DatabaseHelper dbHelper;
	private SQLiteDatabase sqLiteDb;
	private Context HCtx = null;
	private static final String DATABASE_NAME = "tribewire";
	private static final int DATABASE_VERSION = 1;
	public static final String Friends_table = "Friends";
	public static final int Friends_int = 0;
	public static final String Group_table = "Groups";
	public static final int Group_int = 1;
	public static final String Receive_table = "message";
	public static final int Receive_int = 2;
	public static final String drafts_table = "drafts";
	public static final int drafts_int = 3;
	public static final String Group_Member_table = "groupmember";
	public static final int Group_member_int = 4;
	public static final String Company_Fyi_table = "companyfyi";
	public static final int Company_Fyi_int = 5;
    public static final String emergency_table     = "emergency";
    public static final int emergency_int   = 6;
    
    public static final String isEmergency ="is_emergency";
    public static final String isRead ="isread";
    public static final String conversationId ="conversation_Id";
    
    
    
	/**
	 * 
	 */
	public static final String[][] tables = new String[][] {
			{ "_id", "asscid", "name", "number", "email", "groupid", "status",
					"invite", "status_message" },
			{ "sr_no", "groupname", "type" },
			{ "_id", "message_id", "sender", "receiver", "message_type",
					"file_url", "date_time", "type", "status",
					"conversation_Id", "reply", "sender_status", "favourite",
					"played", "groupreceiver", "textmessage", "isdraft",
					"newdate", "thumbnail", "name" },
			{ "sr_no", "message_id", "sender", "receiver", "message_type",
					"file_url", "date_time", "phonenumber", "type",
					"textmessage" },
			{ "sr_no", "group_id", "friend_id" },
			{ "_id", "sender", "senderId", "date_time_long", "status",
					"conversation_Id", "textmessage" } ,
					{"_id", "conversation_Id, is_emergency", "isread"}				
	};

	// private static final String TABLE_1_CREATE = "create table " +
	// Friends_table + "(sr_no integer primary key autoincrement,"
	// + "asscid integer ," + "name text not null," + "number text not null," +
	// "email text not null," + "groupid text,"
	// + "status integer,invite boolean default false);";

	private static final String TABLE_1_CREATE = "create table "
			+ Friends_table
			+ "(_id integer primary key autoincrement,"
			+ "asscid integer ,"
			+ "name varchar(500) not null,"
			+ "number varchar(500) not null,"
			+ "email text not null,"
			+ "groupid text,"
			+ "status integer,invite boolean default false ,status_message varchar(100));";
	private static final String TABLE_2_CREATE = "create table " + Group_table
			+ "(sr_no varchar(500) not null,"
			+ "groupname text not null, type integer);";

	private static final String TABLE_3_CREATE = "create table "
			+ Receive_table
			+ "(_id integer primary key autoincrement,"
			+ "message_id varchar ,"
			+ "sender text not null,"
			+ "receiver text not null,"
			+ "message_type integer,"
			+ "file_url text not null,"
			+ "date_time text not null,"
			+ "type integer,"
			+ "status integer default 0,"
			+ "conversation_Id text,"
			+ "reply integer default 0,"
			+ "sender_status integer default 1,favourite integer,played integer default 0,groupreceiver varchar(500) default 0,"
			+ "textmessage varchar(500)," + "isdraft integer default 0,"
			+ "newdate varchar(500)," + " thumbnail varchar(500),"
			+ "name varchar(500)" + ");";

	private static final String TABLE_4_CREATE = "create table " + drafts_table
			+ "(sr_no integer primary key autoincrement,"
			+ "message_id integer ," + "sender text not null,"
			+ "receiver text not null," + "message_type integer,"
			+ "file_url text not null," + "date_time text not null,"
			+ "phonenumber text not null," + "type integer,"
			+ "textmessage varchar(500));";

	private static final String TABLE_5_CREATE = "create table "
			+ Group_Member_table + "(sr_no integer primary key autoincrement,"
			+ "group_id varchar(500),friend_id integer);";

	/**
	 * * "conversation_id" :"MTk1","recipient_names":"ALL","last_message"
	 * :"dGVzdA==","conversation_start_date"
	 * :"2014-05-09 11:20:36","last_message_date"
	 * :"0000-00-00 00:00:00","sender":"NA=="}
	 */
	private static final String TABLE_6_CREATE = "create table "
			+ Company_Fyi_table + "(_id integer primary key autoincrement," + ""
			+ "sender text not null," + "senderId text not null," + "" + ""
			+ "date_time_long text not null," + "" + "status integer,"
			+ "conversation_Id text," + "" + "" + "textmessage varchar(500)"
			+ "" + ");";
	
	private static final String TABLE_7_CREATE = "create table "
			+ emergency_table + "(_id integer primary key autoincrement," + ""
			+ "conversation_Id text not null," + "is_emergency integer," + "" + ""
			+ "isread integer)";
	

	private static final String TABLE_3_ALTER = "ALTER TABLE " + drafts_table
			+ " ADD textmessage varchar(500);";

	private static final String TABLE_4_ALTER = "ALTER TABLE " + Receive_table
			+ " ADD textmessage varchar(500);";

	/** Constructor */
	public DataBase(Context ctx) {
		HCtx = ctx;
	}

	public DataBase open() throws SQLException {
		dbHelper = new DatabaseHelper(HCtx);
		sqLiteDb = dbHelper.getWritableDatabase();
		return this;
	}

	public void clean() {
		sqLiteDb.delete(Friends_table, null, null);
		sqLiteDb.delete(Group_table, null, null);
		sqLiteDb.delete(Receive_table, null, null);
		sqLiteDb.delete(drafts_table, null, null);
		sqLiteDb.delete(Group_Member_table, null, null);
		sqLiteDb.delete(Company_Fyi_table, null, null);
		sqLiteDb.delete(emergency_table, null, null);
	}

	public void cleanTable(int tableNo) {
		switch (tableNo) {
		case Friends_int:
			sqLiteDb.delete(Friends_table, null, null);
			break;
		case Group_int:
			sqLiteDb.delete(Group_table, null, null);
			break;
		case Receive_int:
			sqLiteDb.delete(Receive_table, null, null);
			break;
		case drafts_int:
			sqLiteDb.delete(drafts_table, null, null);
			break;
		case Group_member_int:
			sqLiteDb.delete(Group_Member_table, null, null);
			break;
		case Company_Fyi_int:
			sqLiteDb.delete(Company_Fyi_table, null, null);
			break;
		case emergency_int:
			sqLiteDb.delete(emergency_table, null, null);
			break;
		default:
			break;
		}

	}

	public void close() {
		dbHelper.close();
	}

	public synchronized long insert(String DATABASE_TABLE, int tableNo,
			String[] values) {
		ContentValues vals = new ContentValues();
		for (int i = 0; i < values.length; i++) {
			Debugger.debugE("column : " + tables[tableNo][i + 1] + " : value :"
					+ values[i]);
			vals.put(tables[tableNo][i + 1], values[i]);
		}
		return sqLiteDb.insert(DATABASE_TABLE, null, vals);
	}

	public synchronized long insert_content(String DATABASE_TABLE, int tableNo,
			ContentValues cv) {
		ContentValues vals = cv;
		return sqLiteDb.insert(DATABASE_TABLE, null, vals);
	}

	public synchronized long insertWithSR_NO(String DATABASE_TABLE,
			int tableNo, String[] values, String srno) {
		ContentValues vals = new ContentValues();
		for (int i = 0; i < values.length; i++) {
			vals.put(tables[tableNo][i + 1], values[i]);
		}
		vals.put(tables[tableNo][0], srno);
		return sqLiteDb.insert(DATABASE_TABLE, null, vals);
	}

	public boolean delete(String DATABASE_TABLE, int tableNo, long rowId) {
		return sqLiteDb.delete(DATABASE_TABLE,
				tables[tableNo][0] + "=" + rowId, null) > 0;
	}

	public synchronized boolean delete(String DATABASE_TABLE, int tableNo,
			String whereCause) {
		return sqLiteDb.delete(DATABASE_TABLE, whereCause, null) > 0;
	}

	public synchronized boolean deleteAllContents(String DATABASE_TABLE) {
		return sqLiteDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	public synchronized Cursor fetch(String DATABASE_TABLE, int tableNo,
			long rowId) throws SQLException {

		Cursor ret = sqLiteDb.query(DATABASE_TABLE, tables[tableNo],
				tables[tableNo][0] + "=" + rowId, null, null, null, null);
		if (ret != null) {
			ret.moveToFirst();
		}
		return ret;
	}

	public synchronized String fetchConversationID(String names)
			throws SQLException {
		try {

			String str[] = names.split(",");

			if (str.length == 1) {
				Cursor ret = sqLiteDb.query(Receive_table,
						new String[] { "conversation_Id" }, "receiver='"
								+ names + "' or sender='" + names + "'", null,
						null, null, null);
				if (ret != null && ret.getCount() > 0) {
					ret.moveToLast();
					String tmp = ret.getString(0);
					ret.close();
					return tmp;
				}
				ret.close();
			} else {
				Vector<String> strs = new Vector<String>();
				for (int i = 0; i < str.length; i++) {
					strs.add(str[i]);
				}
				Cursor ret = sqLiteDb.query(Receive_table, new String[] {
						"conversation_Id", "receiver", "sender" },
						"receiver like'%" + str[0] + "%' or sender like '%"
								+ str[0] + "%'", null, null, null, null);
				if (ret != null && ret.getCount() > 0) {
					while (ret.moveToNext()) {
						String tmp = ret.getString(0);
						if (isMatching(strs, ret.getString(1))
								|| isMatching(strs, ret.getString(2))) {
							ret.close();
							return tmp;
						}
					}
				}
				ret.close();
			}
		} catch (Exception e) {
		}
		return "";
	}

	public static boolean isMatching(Vector<String> strs, String names) {
		String[] tmp = names.split(",");
		Log.e("TMP & strs", strs + " & " + names);

		if (tmp.length == strs.size()) {
			int matchCount = 0;
			for (int i = 0; i < tmp.length; i++) {
				if (strs.contains(tmp[i]))
					matchCount++;
			}
			if (matchCount == strs.size()) {
				return true;
			}
		}
		return false;
	}

	public synchronized Cursor fetch(String DATABASE_TABLE, int tableNo,
			String orderby, String groupby) throws SQLException {

		Cursor ret = sqLiteDb.query(DATABASE_TABLE, tables[tableNo], null,
				null, groupby, null, orderby);
		if (ret != null) {
			ret.moveToFirst();
		}
		return ret;
	}

	public synchronized Cursor fetch(String DATABASE_TABLE, int tableNo,
			String where, String orderby, String groupby) throws SQLException {
		Log.e("", " Where Clause \n " + where);
		Cursor ret = sqLiteDb.query(DATABASE_TABLE, tables[tableNo], where,
				null, groupby, null, orderby);
		if (ret != null) {
			ret.moveToFirst();
		}
		return ret;
	}

	public synchronized Cursor fetchContactGroup(String DATABASE_TABLE,
			int tableNo, String where, String orderby, String groupby)
			throws SQLException {
		Log.e("", " Where Clause \n " + where);
		Cursor ret = sqLiteDb.query(DATABASE_TABLE, tables[tableNo], where,
				null, groupby, null, orderby);
		if (ret != null) {
			// ret.moveToFirst();
		}
		return ret;
	}

	public synchronized Cursor fetch(String DATABASE_TABLE, int tableNo,
			String where) throws SQLException {
		Cursor ret = sqLiteDb.query(DATABASE_TABLE, tables[tableNo], where,
				null, null, null, null);
		// Log.e("", "=========== Where "+where
		// );
		if (ret != null) {
			ret.moveToFirst();
		}
		return ret;
	}

	public int getCount(String DATABASE_TABLE, int tableNo, String where)
			throws SQLException {
		int ret = 0;
		Cursor c = sqLiteDb.query(DATABASE_TABLE, tables[tableNo], where, null,
				null, null, null);
		if (c != null) {
			ret = c.getCount();
			c.close();
		}
		return ret;
	}

	public synchronized Cursor fetchContact(String DATABASE_TABLE, int tableNo,
			String word) throws SQLException {

		String selection = word + " MATCH ?";
		// String[] selectionArgs = new String[] {query+"*"};
		// Cursor c = sqLiteDb.query(DATABASE_TABLE, tables[tableNo], selection,
		// null, null, null, null);
		Cursor c = sqLiteDb.rawQuery("SELECT * FROM Friends where name LIKE '"
				+ word + "%'  order by name desc ;", null);
		if (c != null) {
			c.moveToFirst();

		}
		return c;
	}

	public int getCount(String DATABASE_TABLE, int tableNo, String where,
			String orderBy) throws SQLException {
		int ret = 0;
		Cursor c = sqLiteDb.query(DATABASE_TABLE, tables[tableNo], where, null,
				null, null, orderBy);
		if (c != null) {
			ret = c.getCount();
			c.close();
		}
		return ret;
	}

	public synchronized int fetchCounts(String DATABASE_TABLE, int tableNo,
			String[] cols, String where) throws SQLException {

		Cursor ret = sqLiteDb.query(DATABASE_TABLE, tables[tableNo], where,
				null, null, null, null);
		if (ret != null) {
			int i = ret.getCount();
			ret.close();
			return i;
		}
		return 0;
	}

	public synchronized Cursor fetch(String DATABASE_TABLE, int tableNo,
			int[] colindex, String where) throws SQLException {

		String[] cols = new String[colindex.length];
		for (int i = 0; i < colindex.length; i++)
			cols[i] = tables[tableNo][colindex[i]];

		Cursor ret = sqLiteDb.query(DATABASE_TABLE, cols, where, null, null,
				null, null);
		if (ret != null) {
			ret.moveToFirst();
		}
		return ret;
	}

	public synchronized Cursor fetch(String DATABASE_TABLE, int tableNo,
			String[] cols, String where) throws SQLException {

		Cursor ret = sqLiteDb.query(DATABASE_TABLE, cols, where, null, null,
				null, null);
		if (ret != null) {
			ret.moveToFirst();
		}
		return ret;
	}

	public synchronized Cursor fetch(String DATABASE_TABLE, int tableNo,
			int colIndex, String colval) throws SQLException {

		Cursor ret = sqLiteDb.query(DATABASE_TABLE, tables[tableNo],
				tables[tableNo][colIndex] + "='" + colval + "'", null, null,
				null, null);
		if (ret != null) {
			ret.moveToFirst();
		}
		return ret;
	}

	public synchronized Cursor fetch(String DATABASE_TABLE, int tableNo,
			int colIndex, String colval, int colIndex2, String colval2)
			throws SQLException {

		Cursor ret = sqLiteDb.query(DATABASE_TABLE, tables[tableNo],
				tables[tableNo][colIndex] + "='" + colval + "' and "
						+ tables[tableNo][colIndex2] + "='" + colval2 + "'",
				null, null, null, null);
		if (ret != null) {
			ret.moveToFirst();
		}
		return ret;
	}

	public synchronized Cursor fetch(String DATABASE_TABLE, int tableNo,
			int[] colIndex, String[] colval) throws SQLException {

		String strSelection = "";
		for (int i = 0; i < colIndex.length; i++) {
			strSelection = strSelection + tables[tableNo][colIndex[i]] + "='"
					+ colval[i] + "' and ";
		}
		strSelection = strSelection.substring(0, strSelection.length() - 5);
		Cursor ret = sqLiteDb.query(DATABASE_TABLE, tables[tableNo],
				strSelection, null, null, null, null);
		if (ret != null) {
			ret.moveToFirst();
		}
		return ret;
	}

	public synchronized Cursor fetch(String DATABASE_TABLE, int tableNo,
			int colIndex, String colval, String orderByval) throws SQLException {

		Cursor ret = sqLiteDb.query(DATABASE_TABLE, tables[tableNo],
				tables[tableNo][colIndex] + "='" + colval + "'", null, null,
				null, orderByval);
		if (ret != null) {
			ret.moveToFirst();
		}
		return ret;
	}

	public synchronized Cursor fetchAll(String DATABASE_TABLE, int tableNo) {
		try {
			return sqLiteDb.query(DATABASE_TABLE, tables[tableNo], null, null,
					null, null, null);

		} catch (Exception e) {
			// Log.e("yo", e.getMessage());
			return null;
		}
	}

	public synchronized Cursor fetchAll(String DATABASE_TABLE, int tableNo,
			String orderByval) {
		try {
			return sqLiteDb.query(DATABASE_TABLE, tables[tableNo], null, null,
					null, null, orderByval);

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("yo", e.getMessage());
			return null;
		}
	}

	public synchronized Cursor fetchAll(String DATABASE_TABLE, int tableNo,
			String orderByval, String where, String abc) {
		try {
			Debugger.debugE("Where clause+ " + where);
			return sqLiteDb.query(DATABASE_TABLE, tables[tableNo], where, null,
					null, null, orderByval);

		} catch (SQLiteException e) {
			Log.e("yo SQLiteException", e.getMessage());
			return null;
		} catch (Exception e) {
			Log.e("yo Exception", e.getMessage());
			return null;
		}
	}

	public synchronized Cursor fetchAll(String DATABASE_TABLE, int tableNo,
			String orderByval, String where) {
		try {
			Debugger.debugE("Where clause+ " + where);
			return sqLiteDb.query(DATABASE_TABLE, tables[tableNo], where, null,
					null, null, orderByval);

		} catch (SQLiteException e) {
			Log.e("yo SQLiteException", e.getMessage());
			return null;
		} catch (Exception e) {
			Log.e("yo Exception", e.getMessage());
			return null;
		}
	}

	public boolean update(String DATABASE_TABLE, int tableNo, long rowId,
			ContentValues vc) {
		return sqLiteDb.update(DATABASE_TABLE, vc, tables[tableNo][0] + "="
				+ rowId, null) > 0;
	}

	public boolean update(String DATABASE_TABLE, int tableNo, String where,
			ContentValues cv) {
		return sqLiteDb.update(DATABASE_TABLE, cv, where, null) > 0;
	}

	public boolean update(String DATABASE_TABLE, int tableNo, long rowId,
			int colIndex, int val) {
		ContentValues vals = new ContentValues();
		vals.put(tables[tableNo][colIndex], val);
		return sqLiteDb.update(DATABASE_TABLE, vals, tables[tableNo][0] + "="
				+ rowId, null) > 0;
	}

	public boolean update(String DATABASE_TABLE, int tableNo, long rowId,
			int colIndex, String val) {
		ContentValues vals = new ContentValues();
		vals.put(tables[tableNo][colIndex], val);
		return sqLiteDb.update(DATABASE_TABLE, vals, tables[tableNo][0] + "="
				+ rowId, null) > 0;
	}

}