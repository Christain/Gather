package com.gather.android.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserInfoModel implements Serializable {

	private int uid; // 用户ID
	private String birth; // 生日1999-09-09 00:00:00
	private int sex; // 性别1男2女
	private String nick_name; // 昵称
	private String real_name; // 真实姓名
	private String head_img_url; // 头像
	private String pho_num; // 电话号码
	private String email; // 邮箱
	private String address; // 地址
	private int is_regist; // 是否已完成注册：0未完成，1已完成
	private String contact_phone; // 联系电话
	private String sina_expires_in; // 新浪过期时间
	private String sina_token; // 新浪token
	private String sina_openid; // 新浪openId
	private String qq_expires_in; // 腾讯过期时间
	private String qq_openid; // 腾讯openId
	private String qq_token; // 腾讯token
	private String contact_qq; // 联系QQ
	private String last_login_time; // 最后登录时间

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getBirth() {
		if (birth != null) {
			return birth;
		} else {
			return "";
		}
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getNick_name() {
		if (nick_name != null) {
			return nick_name;
		} else {
			return "";
		}
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getReal_name() {
		if (real_name != null) {
			return real_name;
		} else {
			return "";
		}
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getHead_img_url() {
		if (head_img_url != null) {
			return head_img_url;
		} else {
			return "";
		}
	}

	public void setHead_img_url(String head_img_url) {
		this.head_img_url = head_img_url;
	}

	public String getPho_num() {
		if (pho_num != null) {
			return pho_num;
		} else {
			return "";
		}
	}

	public void setPho_num(String pho_num) {
		this.pho_num = pho_num;
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

	public int getIs_regist() {
		return is_regist;
	}

	public void setIs_regist(int is_regist) {
		this.is_regist = is_regist;
	}

	public String getContact_phone() {
		if (contact_phone != null) {
			return contact_phone;
		} else {
			return "";
		}
	}

	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}

	public String getSina_expires_in() {
		if (sina_expires_in != null) {
			return sina_expires_in;
		} else {
			return "";
		}
	}

	public void setSina_expires_in(String sina_expires_in) {
		this.sina_expires_in = sina_expires_in;
	}

	public String getSina_token() {
		if (sina_token != null) {
			return sina_token;
		} else {
			return "";
		}
	}

	public void setSina_token(String sina_token) {
		this.sina_token = sina_token;
	}

	public String getSina_openid() {
		if (sina_openid != null) {
			return sina_openid;
		} else {
			return "";
		}
	}

	public void setSina_openid(String sina_openid) {
		this.sina_openid = sina_openid;
	}

	public String getQq_expires_in() {
		if (qq_expires_in != null) {
			return qq_expires_in;
		} else {
			return "";
		}
	}

	public void setQq_expires_in(String qq_expires_in) {
		this.qq_expires_in = qq_expires_in;
	}

	public String getQq_openid() {
		if (qq_openid != null) {
			return qq_openid;
		} else {
			return "";
		}
	}

	public void setQq_openid(String qq_openid) {
		this.qq_openid = qq_openid;
	}

	public String getQq_token() {
		if (qq_token != null) {
			return qq_token;
		} else {
			return "";
		}
	}

	public void setQq_token(String qq_token) {
		this.qq_token = qq_token;
	}

	public String getContact_qq() {
		if (contact_qq != null) {
			return contact_qq;
		} else {
			return "";
		}
	}

	public void setContact_qq(String contact_qq) {
		this.contact_qq = contact_qq;
	}

	public String getLast_login_time() {
		if (last_login_time != null) {
			return last_login_time;
		} else {
			return "";
		}
	}

	public void setLast_login_time(String last_login_time) {
		this.last_login_time = last_login_time;
	}

}
