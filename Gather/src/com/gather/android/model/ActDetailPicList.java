package com.gather.android.model;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ActDetailPicList implements Serializable {

	private ArrayList<ActDetailPicModel> imgs;

	public ArrayList<ActDetailPicModel> getImgs() {
		return imgs;
	}

	public void setImgs(ArrayList<ActDetailPicModel> imgs) {
		this.imgs = imgs;
	}
}
