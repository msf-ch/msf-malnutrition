package org.msf.android.fragments;

import org.msf.android.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EncounterCreationFragment extends Fragment{
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return (inflater.inflate(R.layout.patient_encounter_form, container, false));
	}
	
}
