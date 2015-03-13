package com.app.eyesay.custommenu;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.tribewire.app.R;

public class CustomMenu {
   private static volatile CustomMenu mMenu = null;

   private ArrayList<CustomMenuItem> mMenuItems;
   private OnMenuItemSelectedListener mListener = null;
   private Activity mContext = null;
   private static volatile PopupWindow mPopupWindow = null;
   private static boolean mIsShowing = false;

   /**
    * Menu item selected listener
    */
   public interface OnMenuItemSelectedListener {
           public void MenuItemSelectedEvent(Integer selection);
   }

   /**
    * Is the menu opened
    * 
    * @return boolean isShowing
    */
   public static boolean isShowing() {
           return mIsShowing;
   }

   /**
    * Constructor
    * 
    * @param activity
    *            Activity where the menu will be shown
    * @param OnMenuItemSelectedListener
    *            listener Listener
    * @param LayoutInflater
    *            items Items list
    * @return void
    */
   public CustomMenu(Activity activity, OnMenuItemSelectedListener listener,
                   HashMap<Integer, String> items) {

           mListener = listener;
           mMenuItems = new ArrayList<CustomMenuItem>();
           mContext = activity;

           // Add items to the menu
           for (Integer id : items.keySet()) {

                   String name = items.get(id);
                   CustomMenuItem cmi = new CustomMenuItem();
                   cmi.setCaption(name);
                   cmi.setId(id);
                   mMenuItems.add(cmi);

           }

   }


   public void show(View v) {

           // The menu is shown
           mIsShowing = true;
           int itemCount = mMenuItems.size();

           if (itemCount < 1)
                   return; // Nothing to show

           if (mPopupWindow != null)
                   return; // The menu is opened

           // Display settings
           Display display = ((WindowManager) mContext
                           .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

           // The view to show
           View mView = ((Activity) mContext).getLayoutInflater().inflate(
                           R.layout.custom_menu, null);

           // Create popup window to show
           mPopupWindow = new PopupWindow(mView, LayoutParams.FILL_PARENT,
                           LayoutParams.WRAP_CONTENT, false);
           mPopupWindow.setAnimationStyle(R.style.Animations_MenuAnimation);
           mPopupWindow.setWidth(display.getWidth());
           mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);

           // Add menu items
           TableLayout table = (TableLayout) mView
                           .findViewById(R.id.custom_menu_table);
           table.removeAllViews();

           for (int i = 0; i < itemCount; i++) {

                   TableRow row = null;
                   Button btn = null;
                   // create headers
                   row = new TableRow(mContext);
                   row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                   LayoutParams.WRAP_CONTENT));

                   final CustomMenuItem cmi = mMenuItems.get(i);
                   View itemLayout = ((Activity) mContext).getLayoutInflater()
                                   .inflate(R.layout.custom_menu_item, null);
                   btn = (Button) itemLayout
                                   .findViewById(R.id.custom_menu_item_caption);
                   btn.setText(cmi.getCaption());

                   btn.setOnClickListener(new OnClickListener() {
                           @Override
                           public void onClick(View v) {
                                   mListener.MenuItemSelectedEvent(cmi.getId());
                                   hide();
                           }
                   });

                   row.addView(itemLayout);
                   table.addView(row);
           }

   }

   /**
    * Hide your menu.
    * 
    * @return void
    */
   public static void hide() {
           mIsShowing = false;
           if (mPopupWindow != null) {
                   mPopupWindow.dismiss();
                   mPopupWindow = null;
                   mMenu = null;
           }
           return;
   }

   public static synchronized void show(Activity context, HashMap<Integer, String> items,
                   OnMenuItemSelectedListener listener) {

           if (mMenu != null && CustomMenu.isShowing()) {

                   hide();
                   return;

           }

           CustomMenu menu = new CustomMenu(context, listener, items);

           mMenu = menu;
           menu.show(context.findViewById(android.R.id.content).getRootView());

   }

   /**
    * Menu item class
    * 
    * @author horribile
    * 
    */
   private class CustomMenuItem {

           private String mCaption = null;
           private int mId = -1;

           public void setCaption(String caption) {
                   mCaption = caption;
           }

           public String getCaption() {
                   return mCaption;
           }

           public void setId(int id) {
                   mId = id;
           }

           public int getId() {
                   return mId;
           }

   }
}
