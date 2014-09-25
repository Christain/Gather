package com.gather.android.model;

import java.io.Serializable;

public class RegisterDataModel implements Serializable {

	private String nickname;
	private String password;
	private int sex;
	private String birthday;
	private String address;
	private String email;
	private String photoPath;

	public String getNickname() {
		if (nickname != null) {
			return nickname;
		} else {
			return "";
		}
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		if (password != null) {
			return password;
		} else {
			return "";
		}
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		if (birthday != null) {
			return birthday;
		} else {
			return "";
		}
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		if (address != null) {
			return address;
		} else {
			return "";
		}
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		if (email != null) {
			return email;
		} else {
			return "";
		}
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhotoPath() {
		if (photoPath != null) {
			return photoPath;
		} else {
			return "";
		}
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

}
