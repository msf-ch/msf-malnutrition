package org.msf.android.htmlforms.malnutrition;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.msf.android.R;
import org.msf.android.app.MSFClinicApp;
import org.msf.android.database.ClinicAdapter;
import org.msf.android.database.ClinicAdapterManager;
import org.msf.android.htmlforms.ReducedFormData;
import org.msf.android.htmlforms.ReducedObs;
import org.msf.android.managers.malnutrition.MalnutritionWorkflowManager;
import org.msf.android.openmrs.malnutrition.MalnutritionChild;
import org.msf.android.openmrs.malnutrition.MalnutritionHousehold;
import org.msf.android.openmrs.malnutrition.MalnutritionObservation;
import org.msf.android.rest.DefaultMapperFactory;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.dao.Dao;

public class ChildBridge {

	/* Relevant IDs for constructing household */
	public static String ID_GIVEN_NAME = "enfprenom"; // store as first name
	public static String ID_FAMILY_NAME = "enfnom";
	public static String ID_AGE = "enfage";
	public static String ID_ID_NUMBER = "enfantid";
	public static String ID_GENDER = "enfsex";

	public static String NO_FIELD_FOUND_ERROR_MESSAGE = "ERROR_NO_FIELD_FOUND";

	private ObjectMapper mapper;

	private ReducedFormData formData;
	private MalnutritionChild child;
	private Map<String, MalnutritionObservation> obs;

	private MalnutritionWorkflowManager workflowManager;
	
	private Toast toastMessage;
	
	private MalnutritionHousehold household;
	
	/* Values to return to the child form */
	private String areaName;
	private String idPollster;
	private String idArea;
	private String idVillage;

	/* Constructors */

	public ChildBridge(MalnutritionWorkflowManager workflowManager, MalnutritionHousehold household) {
		this(workflowManager, null, household);
		setValuesForChildForm();
	}

	public ChildBridge(MalnutritionWorkflowManager workflowManager,
			MalnutritionChild child, MalnutritionHousehold household) {
		mapper = DefaultMapperFactory.getDefaultMapper();

		this.setChild(child);
		this.workflowManager = workflowManager;

		this.household = household;
		setValuesForChildForm();
		setFormData(new ReducedFormData());
	}

	/* Interface Methods */

	@JavascriptInterface
	public void storeData(String data) throws JsonParseException,
			JsonMappingException, IOException {
		try {
			System.out.println("Form result string: " + data);

			ReducedFormData result = mapper.readValue(data,
					new TypeReference<ReducedFormData>() {
					});
			setFormData(result);

			parseFormToChild();
			storeChild(getChild());

			parseObs();
			storeObs();
		} catch (Exception ex) {
			ex.printStackTrace();

			Context context = MSFClinicApp.getAppContext();
			CharSequence text = "Error: " + ex.getMessage();
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}

		workflowManager.finishNewChildForm();
	}

	@JavascriptInterface
	public String getStringValue(String key) {
		try {
			int resId = R.string.class.getDeclaredField(key).getInt(null);
			String result = MSFClinicApp.getAppContext().getString(resId);
			return result;
		} catch (Exception e) {
			return NO_FIELD_FOUND_ERROR_MESSAGE;
		}
	}

	@JavascriptInterface
	public void showAlertMessage(String title, String message) {
		//This is not working
		/*AlertDialog.Builder adb = new AlertDialog.Builder(MSFClinicApp.getAppContext());
		adb.setTitle(title);
		adb.setMessage(message);
		adb.setPositiveButton("Ok", null);
        adb.show();*/
		//Prefer showing a Toast
		if(toastMessage !=null)
			toastMessage.cancel();
		
		toastMessage = Toast.makeText(MSFClinicApp.getAppContext(), message, Toast.LENGTH_LONG);
		toastMessage.show();
	}

	@JavascriptInterface
	public String serializeData() throws JsonGenerationException,
			JsonMappingException, IOException {
		return mapper.writeValueAsString(getFormData());
	}

	/* Private methods */

	public void storeChild(MalnutritionChild mChild) throws SQLException {
		ClinicAdapter ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();
		try {
			mChild.household = workflowManager.getHousehold();
			
			Dao<MalnutritionChild, Long> cDao = ca.getChildDao();
			cDao.createOrUpdate(mChild);
		} finally {
			ClinicAdapterManager.unlock();
		}
	}

	private void parseFormToChild() {
		if (getChild() == null) {
			setChild(new MalnutritionChild());
		}

		List<ReducedObs> obs = formData.getObs();

		for (ReducedObs ob : obs) {
			if (ob == null) {
				System.out.println("Null ob!");
				continue;
			}

			if (ob.concept == null) {
				System.out.println("Null ob concept, value is: " + ob.value);
				continue;
			}

			if (ob.concept.equals(ID_AGE)) {
				getChild().age = ob.value;
			} else if (ob.concept.equals(ID_FAMILY_NAME)) {
				getChild().familyName = ob.value;
			} else if (ob.concept.equals(ID_GIVEN_NAME)) {
				getChild().givenName = ob.value;
			} else if (ob.concept.equals(ID_ID_NUMBER)) {
				getChild().idNumber = ob.value;
			} else if (ob.concept.equals(ID_GENDER)) {
				getChild().gender = ob.value;
			}
		}
	}

	private void parseObs() {
		if (obs == null) {
			obs = new HashMap<String, MalnutritionObservation>();
		}

		for (ReducedObs rObs : getFormData().getObs()) {
			if (obs.containsKey(rObs.concept)) {
				obs.get(rObs.concept).value = rObs.value;
			} else {
				MalnutritionObservation newObs = new MalnutritionObservation();
				newObs.concept = rObs.concept;
				newObs.value = rObs.value;
				obs.put(newObs.concept, newObs);
			}
		}
	}

	private void storeObs() throws SQLException {
		ClinicAdapter ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();

		Dao<MalnutritionObservation, Long> oDao = ca
				.getMalnutritionObservationDao();
		Iterator<MalnutritionObservation> obsIterator = obs.values().iterator();

		MalnutritionObservation currentObs;
		while (obsIterator.hasNext()) {
			currentObs = obsIterator.next();
			currentObs.child = getChild();
			oDao.createOrUpdate(currentObs);
		}

		ClinicAdapterManager.unlock();
	}

	/* Getters and setters */

	public ReducedFormData getFormData() {
		return formData;
	}

	private void setFormData(ReducedFormData formData) {
		this.formData = formData;
	}

	public MalnutritionChild getChild() {
		return child;
	}

	public void setChild(MalnutritionChild child) {
		this.child = child;
	}

	/* Getters for the household */
	
	public String getHouseholdChiefName(){
		return this.household.householdChief;
	}
	
	public String getHouseholdId(){
		return this.household.householdId;
	}
	
	public String getSurveyDate(){
		return this.household.surveyDate;
	}
	
	public String getVillageName(){
		return this.household.village;
	}
	
	public String getAreaName(){
		return this.areaName;
	}
	
	public String getIdArea(){
		return this.idArea;
	}
	
	public String getIdPollster(){
		return this.idPollster;
	}
	
	public String getIdVillage(){
		return this.idVillage;
	}
	
	public void setValuesForChildForm(){
		ArrayList <MalnutritionObservation> obsList = new ArrayList<MalnutritionObservation>(this.household.obs);
		for(int i = 0; i<obsList.size();i++){
			MalnutritionObservation malobs = obsList.get(i);
			String concept = malobs.concept;
			String value = malobs.value;
			
			if(concept.equals("novillage"))
				this.idVillage = value;
			if(concept.equals("zone"))
				this.areaName = value;
			if(concept.equals("nozone"))
				this.idArea = value;
			if(concept.equals("enqnom"))
				this.idPollster = value;
		}
	}
	/*
	 * 
	 * 
	 * public void initHashMap(){
	 * System.out.println(formStringKeys.length+" "+formStringValues.length);
	 * for (int i=0;i<formStringValues.length;i++)
	 * this.hashmapStringValues.put(formStringKeys[i], formStringValues[i]); }
	 * private String[] formStringKeys = new String[] { "h1_pageone",
	 * "h1_pagetwo", "h1_pagethree", "h1_pagefour", "h1_pagefive",
	 * "h1_submitpage", "h1_submitpage_2", "h2_child_survey_basic_informations",
	 * "h2_informations_about_child_family",
	 * "h2_informations_about_breastfeeding", "h2_anthropometric_measurment",
	 * "h2_morbidity_illness", "h2_morbidity_illness_2", "lbl_suividat",
	 * "lbl_under_suividat", "lbl_zone", "lbl_under_zone", "lbl_enqnom",
	 * "lbl_under_enqnom", "lbl_nozone", "lbl_under_nozone", "lbl_menagn",
	 * "lbl_under_menagn", "lbl_villagen", "lbl_under_villagen",
	 * "lbl_novillage", "lbl_under_novillage", "lbl_enfantid",
	 * "lbl_under_enfantid", "lbl_enfnom", "lbl_under_enfnom",
	 * "lbl_enffirstnom", "lbl_under_enffirstnom", "lbl_enfage",
	 * "lbl_under_enfage", "lbl_naissor", "lbl_under_naissor", "lbl_enfsex",
	 * "lbl_under_enfsex", "lbl_mennom", "lbl_under_mennom", "lbl_localis",
	 * "lbl_under_localis", "lbl_vitavec", "lbl_under_vitavec", "lbl_orphan",
	 * "lbl_under_orphan", "lbl_coepouse", "lbl_under_coepouse",
	 * "lbl_maritstat", "lbl_under_maritstat", "lbl_allaitna",
	 * "lbl_under_allaitna", "lbl_allaitag", "lbl_under_allaitag",
	 * "lbl_laitcont", "lbl_under_laitcont", "lbl_vaccinat",
	 * "lbl_under_vaccinat", "lbl_typvacc", "lbl_under_typvacc", "lbl_pbmesure",
	 * "lbl_under_pbmesure", "lbl_oedem", "lbl_under_oedem", "lbl_poids",
	 * "lbl_under_poids", "lbl_taille", "lbl_under_taille", "lbl_malad",
	 * "lbl_under_malad", "lbl_epcause", "lbl_under_epcause","p_epcause",
	 * "lbl_eptemps", "lbl_under_eptemps", "lbl_epsoin", "lbl_under_epsoin",
	 * "lbl_epnorai", "lbl_under_epnorai", "lbl_eprecou", "lbl_under_eprecou",
	 * "lbl_admiss", "lbl_under_admiss", "lbl_progadm", "lbl_under_progadm",
	 * "lbl_epevol", "lbl_under_epevol", "lbl_eprest", "lbl_under_eprest",
	 * "lbl_decision_1", "lbl_under_decision_1", "lbl_decision_2",
	 * "lbl_under_decision_2", "lbl_progadm_2", "lbl_under_progadm_2",
	 * "lbl_typprog", "lbl_under_typprog", "birth_certificate",
	 * "calendar_local_events", "male", "female",
	 * "at_nutritionnal_center_of_massakorys_hospital",
	 * "at_nutritionnal_center_of_health_center",
	 * "buy_drugs_at_market_private_pharmacy",
	 * "buy_traditionnal_drugs_on_the_market", "community_healthcare_worker",
	 * "consult_community_health_worker", "consult_dr_choco", "consult_healer",
	 * "consult_health_center", "consult_marabut", "consult_private_doctor",
	 * "consulting_paramedical", "divorced", "dont_know", "father", "financial",
	 * "followed_by_community_healthcare_worker", "go_to_hospital",
	 * "grand_mother", "health_center", "health_center_too_far", "he_healed",
	 * "he_improved", "his_status_has_worsened", "hospital",
	 * "hospitalized_of_therapeutic_hospital_center_of_massakory", "maried",
	 * "measles", "monitoring_at_health_center",
	 * "monitoring_by_community_healthcare_worker", "mother", "no", "no_change",
	 * "no_time", "no_transportation_means", "not_serious_enough_a_sickness",
	 * "of_father", "of_mother", "one_two_days", "on_going_treatment", "other",
	 * "pentalavent_two_three_injections", "polio", "security_problems",
	 * "separated", "superior_five_days", "three_five_days",
	 * "too_serious_illness", "using_drugs_already_at_home", "widowed", "yes",
	 * "abdominal_pain", "appetite_loss", "bleeding", "breathing_difficulties",
	 * "burn", "constipation", "convulsion", "cough", "diarrhea",
	 * "ear_problems", "eye_problems", "fever", "headache", "not_specified",
	 * "problem_pain_to_urinate", "throat_problems", "trauma", "vomiting",
	 * "weight_loss", "previousButton", "exportCsvButton", "nextButton",
	 * "jumptostartButton", "submitchildButton", "lbl_submitpage_review_child",
	 * "messageCorrectAnswers", "messageHelp", "noanswergiven", "titleError",
	 * "titleHelp"};
	 * 
	 * private int[] formStringValues = new int[] { R.string.h1_pageone,
	 * R.string.h1_pagetwo, R.string.h1_pagethree, R.string.h1_pagefour,
	 * R.string.h1_pagefive, R.string.h1_submitpage, R.string.h1_submitpage_2,
	 * R.string.h2_child_survey_basic_informations,
	 * R.string.h2_informations_about_child_family,
	 * R.string.h2_informations_about_breastfeeding,
	 * R.string.h2_anthropometric_measurment, R.string.h2_morbidity_illness,
	 * R.string.h2_morbidity_illness_2, R.string.lbl_suividat,
	 * R.string.lbl_under_suividat, R.string.lbl_zone, R.string.lbl_under_zone,
	 * R.string.lbl_enqnom, R.string.lbl_under_enqnom, R.string.lbl_nozone,
	 * R.string.lbl_under_nozone, R.string.lbl_menagn,
	 * R.string.lbl_under_menagn, R.string.lbl_villagen,
	 * R.string.lbl_under_villagen, R.string.lbl_novillage,
	 * R.string.lbl_under_novillage, R.string.lbl_enfantid,
	 * R.string.lbl_under_enfantid, R.string.lbl_enfnom,
	 * R.string.lbl_under_enfnom, R.string.lbl_enffirstnom,
	 * R.string.lbl_under_enffirstnom, R.string.lbl_enfage,
	 * R.string.lbl_under_enfage, R.string.lbl_naissor,
	 * R.string.lbl_under_naissor, R.string.lbl_enfsex,
	 * R.string.lbl_under_enfsex, R.string.lbl_mennom,
	 * R.string.lbl_under_mennom, R.string.lbl_localis,
	 * R.string.lbl_under_localis, R.string.lbl_vitavec,
	 * R.string.lbl_under_vitavec, R.string.lbl_orphan,
	 * R.string.lbl_under_orphan, R.string.lbl_coepouse,
	 * R.string.lbl_under_coepouse, R.string.lbl_maritstat,
	 * R.string.lbl_under_maritstat, R.string.lbl_allaitna,
	 * R.string.lbl_under_allaitna, R.string.lbl_allaitag,
	 * R.string.lbl_under_allaitag, R.string.lbl_laitcont,
	 * R.string.lbl_under_laitcont, R.string.lbl_vaccinat,
	 * R.string.lbl_under_vaccinat, R.string.lbl_typvacc,
	 * R.string.lbl_under_typvacc, R.string.lbl_pbmesure,
	 * R.string.lbl_under_pbmesure, R.string.lbl_oedem,
	 * R.string.lbl_under_oedem, R.string.lbl_poids, R.string.lbl_under_poids,
	 * R.string.lbl_taille, R.string.lbl_under_taille, R.string.lbl_malad,
	 * R.string.lbl_under_malad, R.string.lbl_epcause,
	 * R.string.lbl_under_epcause,R.string.p_epcause, R.string.lbl_eptemps,
	 * R.string.lbl_under_eptemps, R.string.lbl_epsoin,
	 * R.string.lbl_under_epsoin, R.string.lbl_epnorai,
	 * R.string.lbl_under_epnorai, R.string.lbl_eprecou,
	 * R.string.lbl_under_eprecou, R.string.lbl_admiss,
	 * R.string.lbl_under_admiss, R.string.lbl_progadm,
	 * R.string.lbl_under_progadm, R.string.lbl_epevol,
	 * R.string.lbl_under_epevol, R.string.lbl_eprest,
	 * R.string.lbl_under_eprest, R.string.lbl_decision_1,
	 * R.string.lbl_under_decision_1, R.string.lbl_decision_2,
	 * R.string.lbl_under_decision_2, R.string.lbl_progadm_2,
	 * R.string.lbl_under_progadm_2, R.string.lbl_typprog,
	 * R.string.lbl_under_typprog, R.string.birth_certificate,
	 * R.string.calendar_local_events, R.string.male, R.string.female,
	 * R.string.at_nutritionnal_center_of_health_center,
	 * R.string.at_nutritionnal_center_of_massakorys_hospital,
	 * R.string.buy_drugs_at_market_private_pharmacy,
	 * R.string.buy_traditionnal_drugs_on_the_market,
	 * R.string.community_healthcare_worker,
	 * R.string.consult_community_health_worker, R.string.consult_dr_choco,
	 * R.string.consult_healer, R.string.consult_health_center,
	 * R.string.consult_marabut, R.string.consult_private_doctor,
	 * R.string.consulting_paramedical, R.string.divorced, R.string.dont_know,
	 * R.string.father, R.string.financial,
	 * R.string.followed_by_community_healthcare_worker,
	 * R.string.go_to_hospital, R.string.grand_mother, R.string.health_center,
	 * R.string.health_center_too_far, R.string.he_healed, R.string.he_improved,
	 * R.string.his_status_has_worsened, R.string.hospital,
	 * R.string.hospitalized_of_therapeutic_hospital_center_of_massakory,
	 * R.string.maried, R.string.measles, R.string.monitoring_at_health_center,
	 * R.string.monitoring_by_community_healthcare_worker, R.string.mother,
	 * R.string.no, R.string.no_change, R.string.no_time,
	 * R.string.no_transportation_means, R.string.not_serious_enough_a_sickness,
	 * R.string.of_father, R.string.of_mother, R.string.one_two_days,
	 * R.string.on_going_treatment, R.string.other,
	 * R.string.pentalavent_two_three_injections, R.string.polio,
	 * R.string.security_problems, R.string.separated,
	 * R.string.superior_five_days, R.string.three_five_days,
	 * R.string.too_serious_illness, R.string.using_drugs_already_at_home,
	 * R.string.widowed, R.string.yes, R.string.abdominal_pain,
	 * R.string.appetite_loss, R.string.bleeding,
	 * R.string.breathing_difficulties, R.string.burn, R.string.constipation,
	 * R.string.convulsion, R.string.cough, R.string.diarrhea,
	 * R.string.ear_problems, R.string.eye_problems, R.string.fever,
	 * R.string.headache, R.string.not_specified,
	 * R.string.problem_pain_to_urinate, R.string.throat_problems,
	 * R.string.trauma, R.string.vomiting, R.string.weight_loss,
	 * R.string.previousButton, R.string.helpButton, R.string.nextButton,
	 * R.string.jumptostartButton, R.string.submitchildButton,
	 * R.string.lbl_submitpage_review_child, R.string.messageCorrectAnswers,
	 * R.string.messageHelp, R.string.noanswergiven, R.string.titleError,
	 * R.string.titleHelp};
	 */
}
