package com.gather.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gather.android.R;



public class MenuFragment extends Fragment {
	
	private View rootView;
	
	public View getCurrentView() {
		return rootView;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.menu_fragment, container, false);
		
		return rootView;
	}


}
