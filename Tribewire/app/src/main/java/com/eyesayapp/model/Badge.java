package com.eyesayapp.model;

public class Badge {

	private static String allConvBadge;

	private static String companyBadge;

	public static void setAllConvBadge(String allConvBadge) {
		Badge.allConvBadge = allConvBadge;
	}

	public static String getAllConvBadge() {
		return allConvBadge;
	}

	public static void setCompanyBadge(String companyBadge) {
		Badge.companyBadge = companyBadge;
	}

	public static String getCompanyBadge() {
		return companyBadge;
	}

}
