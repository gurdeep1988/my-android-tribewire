package com.eyesayapp.model;

public class NavDrawerItem {

	private String title;
	private int iconId;
	private boolean isBadge;

	public NavDrawerItem(String title, int iconId, boolean isBadge) {
		this.title = title;
		this.iconId = iconId;
		this.isBadge = isBadge;
	}

	public boolean isBadge() {
		return isBadge;
	}

	public void setBadge(boolean isBadge) {
		this.isBadge = isBadge;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public int getIconId() {
		return iconId;
	}

}
