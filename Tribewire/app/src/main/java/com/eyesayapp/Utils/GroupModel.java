package com.eyesayapp.Utils;

import java.io.Serializable;

public class GroupModel implements Serializable{

	private String groupName;
	private String groupMember;
	private String groupMemberNames;
	private String groupIds;
	
	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}
	
	public String getGroupIds() {
		return groupIds;
	}

	public void setGroupMember(String groupMember) {
		this.groupMember = groupMember;
	}

	public String getGroupMember() {
		return groupMember;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupMemberNames(String groupMemberNames) {
		this.groupMemberNames = groupMemberNames;
	}
	public String getGroupMemberNames() {
		return groupMemberNames;
	}
}
