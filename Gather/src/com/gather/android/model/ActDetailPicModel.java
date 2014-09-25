package com.gather.android.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ActDetailPicModel implements Serializable {

	private int id;
	private String img_url;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImg_url() {
		if (img_url != null) {
			return img_url;
		} else {
			return "";
		}
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

}
