package org.msf.android.htmlforms;

import java.util.ArrayList;
import java.util.List;

public class ReducedFormData {
	private List<ReducedObs> obs;
	private ReducedPatient patient;
	private ReducedEncounter encounter;
	
	public ReducedFormData() {
		setObs(new ArrayList<ReducedObs>());
		setPatient(new ReducedPatient());
		setEncounter(new ReducedEncounter());
	}

	public List<ReducedObs> getObs() {
		return obs;
	}

	public void setObs(List<ReducedObs> obs) {
		this.obs = obs;
	}

	public ReducedPatient getPatient() {
		return patient;
	}

	public void setPatient(ReducedPatient patient) {
		this.patient = patient;
	}

	public ReducedEncounter getEncounter() {
		return encounter;
	}

	public void setEncounter(ReducedEncounter encounter) {
		this.encounter = encounter;
	}
}