package com.gather.android.model;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ActModel implements Serializable {

	private int id; // 活动ID
	private String title; // 活动标题
	private String addr_city; // 城市ID
	private String addr_area; // 地址(区)
	private String addr_road; // 地址(路)
	private String addr_num; // 地址(号)
	private String addr_route; //地址（路线）
	private String b_time; // 开始时间
	private String e_time; // 结束时间
	private int t_status; // 时间状态：1即将开始，2进行中，3筹备中，4已结束
	private String head_img_url; // 图片地址
	private int is_loved; // 是否已添加感兴趣：-1不可再添加，0未添加，1已添加
	private int loved_num; // 感兴趣的用户数
	private int shared_num; // 已分享的用户数
	private ArrayList<InterestStringModel> act_tags; // 活动标签
	
	private String organizer; //活动组织者
	private String detail; //活动详情
	private String intro; //活动简述

	public int getId() {
		return id;
	}

	public String getTitle() {
		if (title != null) {
			return title;
		} else {
			return "";
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAddr_city() {
		if (addr_city != null) {
			return addr_city;
		} else {
			return "";
		}
	}

	public void setAddr_city(String addr_city) {
		this.addr_city = addr_city;
	}

	public String getAddr_area() {
		if (addr_area != null) {
			return addr_area;
		} else {
			return "";
		}
	}

	public void setAddr_area(String addr_area) {
		this.addr_area = addr_area;
	}

	public String getAddr_road() {
		if (addr_road != null) {
			return addr_road;
		} else {
			return "";
		}
	}

	public void setAddr_road(String addr_road) {
		this.addr_road = addr_road;
	}

	public String getAddr_num() {
		if (addr_num != null) {
			return addr_num;
		} else {
			return "";
		}
	}

	public void setAddr_num(String addr_num) {
		this.addr_num = addr_num;
	}

	public String getB_time() {
		if (b_time != null) {
			return b_time;
		} else {
			return "";
		}
	}

	public void setB_time(String b_time) {
		this.b_time = b_time;
	}

	public String getE_time() {
		if (e_time != null) {
			return e_time;
		} else {
			return "";
		}
	}

	public void setE_time(String e_time) {
		this.e_time = e_time;
	}

	public int getT_status() {
		return t_status;
	}

	public void setT_status(int t_status) {
		this.t_status = t_status;
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

	public int getIs_loved() {
		return is_loved;
	}

	public void setIs_loved(int is_loved) {
		this.is_loved = is_loved;
	}

	public int getLoved_num() {
		return loved_num;
	}

	public void setLoved_num(int loved_num) {
		this.loved_num = loved_num;
	}

	public int getShared_num() {
		return shared_num;
	}

	public void setShared_num(int shared_num) {
		this.shared_num = shared_num;
	}

	public ArrayList<InterestStringModel> getAct_tags() {
		return act_tags;
	}

	public void setAct_tags(ArrayList<InterestStringModel> act_tags) {
		this.act_tags = act_tags;
	}

	public String getAddr_route() {
		return addr_route;
	}

	public void setAddr_route(String addr_route) {
		this.addr_route = addr_route;
	}

	public String getOrganizer() {
		if (organizer != null) {
			return organizer;
		} else {
			return "";
		}
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public String getDetail() {
		if (detail != null) {
			return detail;
		} else {
			return "";
		}
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getIntro() {
		if (intro != null) {
			return intro;
		} else {
			return "";
		}
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

}
