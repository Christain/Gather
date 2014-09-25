package com.gather.android.model;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ActList implements Serializable {
	
	private ArrayList<ActModel> acts;
	
	public ArrayList<ActModel> getActList() {
		return acts;
	}
	
	public void setActList(ArrayList<ActModel> list) {
		this.acts = list;
	}

}
