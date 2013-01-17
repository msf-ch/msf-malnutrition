package org.msf.android.htmlforms.malnutrition;

import java.io.IOException;
import java.sql.SQLException;
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

public class HouseholdBridge {

	/* Relevant IDs for constructing household */
	public static String ID_HOUSEHOLD_CHIEF = "mennom"; // store as first name
	public static String ID_VILLAGE_NAME = "villagen";
	public static String ID_SURVEY_DATE = "suividat";
	public static String ID_HOUSEHOLD_ID = "menagn";

	private double longitude = Double.MAX_VALUE;
	private double latitude = Double.MAX_VALUE;

	public static String NO_FIELD_FOUND_ERROR_MESSAGE = "ERROR_NO_FIELD_FOUND";

	private ObjectMapper mapper;

	private ReducedFormData formData;
	private MalnutritionHousehold household;
	private Map<String, MalnutritionObservation> obs;

	private MalnutritionWorkflowManager workflowManager;

	private Toast toastMessage;

	/* Constructors */

	public HouseholdBridge(MalnutritionWorkflowManager workflowManager) {
		this(workflowManager, null);
	}

	public HouseholdBridge(MalnutritionWorkflowManager workflowManager,
			MalnutritionHousehold household) {
		mapper = DefaultMapperFactory.getDefaultMapper();

		this.setHousehold(household);
		this.workflowManager = workflowManager;

		setFormData(new ReducedFormData());
	}

	/* Interface Methods */
	@JavascriptInterface
	public void storeData(final String data) throws JsonParseException,
			JsonMappingException, IOException {
		Thread t = new Thread() {
			public void run() {
				storeDataInternal(data);
			}
		};
		t.start();
	}
	
	private void storeDataInternal(final String data) {
			try {
				System.out.println("Form result string: " + data);

				ReducedFormData result = mapper.readValue(data,
						new TypeReference<ReducedFormData>() {
						});
				setFormData(result);

				parseFormToHousehold();
				storeHousehold(getHousehold());

				parseObs();
				storeObs();
			} catch (Exception ex) {
				ex.printStackTrace();

				Context context = MSFClinicApp.getApplication();
				CharSequence text = "Error: " + ex.getMessage();
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
			workflowManager.finishHouseholdForm();
	}

	@JavascriptInterface
	public String getStringValue(String key) {
		try {
			int resId = R.string.class.getDeclaredField(key).getInt(null);
			String result = MSFClinicApp.getApplication().getString(resId);
			return result;
		} catch (Exception e) {
			return NO_FIELD_FOUND_ERROR_MESSAGE;
		}
	}

	@JavascriptInterface
	public void showAlertMessage(String title, String message) {
		// This is not working
		/*
		 * AlertDialog.Builder adb = new
		 * AlertDialog.Builder(MSFClinicApp.getAppContext());
		 * adb.setTitle(title); adb.setMessage(message);
		 * adb.setPositiveButton("Ok", null); adb.show();
		 */

		// Prefer showing a Toast
		if (toastMessage != null)
			toastMessage.cancel();

		toastMessage = Toast.makeText(MSFClinicApp.getApplication(), message,
				Toast.LENGTH_LONG);
		toastMessage.show();
	}

	@JavascriptInterface()
	public String serializeData() throws JsonGenerationException,
			JsonMappingException, IOException {
		return mapper.writeValueAsString(getFormData());
	}

	/* Worker methods */

	public static void storeHousehold(MalnutritionHousehold mHousehold)
			throws SQLException {
		ClinicAdapter ca = ClinicAdapterManager.lockAndRetrieveClinicAdapter();

		try {
			Dao<MalnutritionHousehold, Long> hDao = ca.getHouseholdDao();
			hDao.createOrUpdate(mHousehold);
		} finally {
			ClinicAdapterManager.unlock();
		}
	}

	private void parseFormToHousehold() {
		if (getHousehold() == null) {
			setHousehold(new MalnutritionHousehold());
		}

		getHousehold().latitude = latitude;
		getHousehold().longitude = longitude;

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

			if (ob.concept.equals(ID_HOUSEHOLD_CHIEF)) {
				getHousehold().householdChief = ob.value;
			} else if (ob.concept.equals(ID_VILLAGE_NAME)) {
				getHousehold().village = ob.value;
			} else if (ob.concept.equals(ID_SURVEY_DATE)) {
				getHousehold().surveyDate = ob.value;
			} else if (ob.concept.equals(ID_HOUSEHOLD_ID)) {
				getHousehold().householdId = ob.value;
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
			currentObs.household = household;
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

	public MalnutritionHousehold getHousehold() {
		return household;
	}

	public void setHousehold(MalnutritionHousehold household) {
		this.household = household;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/*
	 * Archived
	 * 
	 * 
	 * private String[] formStringKeys = new String[] { "h1_householdpageone",
	 * "h1_householdpagetwo", "h1_householdpagethree", "h1_householdpagefour",
	 * "h1_householdpagefive", "h1_householdpagesix", "h1_householdpageseven",
	 * "h1_householdpageeight", "h1_householdsubmitpage",
	 * "h1_householdsubmitpage_2", "h2_household_survey_basic_informations",
	 * "h2_household_water_access", "h2_household_organisation",
	 * "h2_household_language", "h2_household_demographics",
	 * "h2_property_land_ownership", "h2_cash_income", "h2_expenditures",
	 * "h2_agricultural_production", "h2_agricultural_income_and_consumption",
	 * "h2_alimentary_rations",
	 * "h2_issues_regarding_supplying_debts","lbl_suividat",
	 * "lbl_under_suividat", "lbl_zone", "lbl_under_zone", "lbl_enqnom",
	 * "lbl_under_enqnom", "lbl_nozone", "lbl_under_nozone", "lbl_menagn",
	 * "lbl_under_menagn", "lbl_villagen", "lbl_under_villagen",
	 * "lbl_novillage", "lbl_under_novillage", "lbl_mennom", "lbl_under_mennom",
	 * "lbl_nenfmen", "lbl_under_nenfmen", "lbl_eauorigi", "lbl_under_eauorigi",
	 * "lbl_puitnum", "lbl_under_puitnum", "lbl_cheffam", "lbl_under_cheffam",
	 * "lbl_csidista", "lbl_under_csidista", "lbl_educnive",
	 * "lbl_under_educnive", "lbl_educenf", "lbl_under_educenf", "lbl_menalang",
	 * "lbl_under_menalang", "lbl_menacara", "lbl_under_menacara", "lbl_champ",
	 * "lbl_under_champ", "lbl_surfcult", "lbl_under_surfcult", "lbl_cutlsm",
	 * "lbl_under_cutlsm", "lbl_qicult", "lbl_under_qicult", "lbl_emplch",
	 * "lbl_under_emplch", "lbl_emptim", "lbl_under_emptim", "lbl_outils",
	 * "lbl_under_outils", "lbl_machine", "lbl_under_machine", "lbl_cheptel",
	 * "lbl_under_cheptel", "lbl_transp", "lbl_under_transp", "lbl_maison",
	 * "lbl_under_maison", "lbl_stock", "lbl_under_stock", "lbl_revso",
	 * "lbl_under_revso", "lbl_comerc", "lbl_under_comerc", "lbl_emplo",
	 * "lbl_under_emplo", "lbl_depens", "lbl_under_depens", "lbl_cultyp",
	 * "lbl_under_cultyp", "lbl_recoltem", "lbl_under_recoltem", "lbl_rectypem",
	 * "lbl_under_rectypem", "lbl_cultjad", "lbl_under_cultjad", "lbl_recuti",
	 * "lbl_under_recuti", "lbl_consoma", "lbl_under_consoma", "lbl_repnum",
	 * "lbl_under_repnum", "lbl_ratgfd", "lbl_under_ratgfd", "lbl_ratcomp",
	 * "lbl_under_ratcomp", "lbl_supnut", "lbl_under_supnut", "lbl_supnutyp",
	 * "lbl_under_supnutyp", "lbl_ratvente", "lbl_under_ratvente",
	 * "lbl_ratupart", "lbl_under_ratupart", "lbl_mecadapt",
	 * "lbl_under_mecadapt", "lbl_aprodiff", "lbl_under_aprodiff",
	 * "lbl_revsoSupply", "lbl_under_revsoSupply", "lbl_aprodiffMeans",
	 * "lbl_under_aprodiffMeans", "lbl_dette", "lbl_under_dette", "male",
	 * "female", "total_number_of_persons_in_the_household",
	 * "number_of_children_under_five_years_old_present_in_the_household",
	 * "number_of_persons_who_arrived_in_the_household_last_three_months",
	 * "number_of_persons_who_left_the_household_last_three_months",
	 * "number_of_persons_who_died_in_the_household_last_three_months",
	 * "number_of_children_under_twofour_months_who_died_last_three_months",
	 * "camel", "bull", "cow", "sheep", "goat", "poultry", "horse",
	 * "drilled_well_with_manual_pomp", "tip_well_or_protected",
	 * "not_protected_well", "river__oxbow_lake__pond", "rainwater",
	 * "other_source", "in_the_village", "less_than_thirty_minutes",
	 * "between_thirty_minutes_and_one_hour", "between_one_and_two_hours",
	 * "more_than_two_hours", "primary", "secondary", "koranic_school",
	 * "university", "none", "private_school", "public_school", "arab",
	 * "kanenmbu", "goran", "french", "fulbe", "nomad_onehundred_percent",
	 * "nomad_and_sedentary", "sedentary_onehundred_percent",
	 * "from_time_to_time", "all_the_time", "motorbike", "vehicle", "truck",
	 * "owner", "tenant", "mil", "niebe", "other_peas", "corn_maize",
	 * "vegetables", "rice", "peanuts", "bean", "bere_bere", "consumption",
	 * "conservation", "sale_exchange_on_the_market", "repayment_of_debt",
	 * "ceremonial_contribution", "cereals", "root_tubers", "peas_lentils",
	 * "milk_dairy", "eggs", "meat_gut_offal", "fish_lake_products",
	 * "oil_lipid", "sugar_honey", "fruits_vegetables", "nuts", "sorghum",
	 * "csb", "oil", "sugar", "salt", "ppdoz", "supplumpy_spp", "dont_know",
	 * "father", "grand_mother", "mother", "no", "other", "yes",
	 * "previousButton", "exportCsvButton", "nextButton", "jumptostartButton",
	 * "submithouseholdButton", "lbl_submitpage_review_household",
	 * "messageNumberChildrenIncorrect", "messageCorrectAnswers", "messageHelp",
	 * "noanswergiven", "titleError", "titleHelp"};
	 * 
	 * private int[] formStringValues = new int[] {
	 * R.string.h1_householdpageone, R.string.h1_householdpagetwo,
	 * R.string.h1_householdpagethree, R.string.h1_householdpagefour,
	 * R.string.h1_householdpagefive, R.string.h1_householdpagesix,
	 * R.string.h1_householdpageseven, R.string.h1_householdpageeight,
	 * R.string.h1_householdsubmitpage, R.string.h1_householdsubmitpage_2,
	 * R.string.h2_household_survey_basic_informations,
	 * R.string.h2_household_water_access, R.string.h2_household_organisation,
	 * R.string.h2_household_language, R.string.h2_household_demographics,
	 * R.string.h2_property_land_ownership, R.string.h2_cash_income,
	 * R.string.h2_expenditures, R.string.h2_agricultural_production,
	 * R.string.h2_agricultural_income_and_consumption,
	 * R.string.h2_alimentary_rations,
	 * R.string.h2_issues_regarding_supplying_debts, R.string.lbl_suividat,
	 * R.string.lbl_under_suividat, R.string.lbl_zone, R.string.lbl_under_zone,
	 * R.string.lbl_enqnom, R.string.lbl_under_enqnom, R.string.lbl_nozone,
	 * R.string.lbl_under_nozone, R.string.lbl_menagn,
	 * R.string.lbl_under_menagn, R.string.lbl_villagen,
	 * R.string.lbl_under_villagen, R.string.lbl_novillage,
	 * R.string.lbl_under_novillage, R.string.lbl_mennom,
	 * R.string.lbl_under_mennom, R.string.lbl_nenfmen,
	 * R.string.lbl_under_nenfmen, R.string.lbl_eauorigi,
	 * R.string.lbl_under_eauorigi, R.string.lbl_puitnum,
	 * R.string.lbl_under_puitnum, R.string.lbl_cheffam,
	 * R.string.lbl_under_cheffam, R.string.lbl_csidista,
	 * R.string.lbl_under_csidista, R.string.lbl_educnive,
	 * R.string.lbl_under_educnive, R.string.lbl_educenf,
	 * R.string.lbl_under_educenf, R.string.lbl_menalang,
	 * R.string.lbl_under_menalang, R.string.lbl_menacara,
	 * R.string.lbl_under_menacara, R.string.lbl_champ,
	 * R.string.lbl_under_champ, R.string.lbl_surfcult,
	 * R.string.lbl_under_surfcult, R.string.lbl_cutlsm,
	 * R.string.lbl_under_cutlsm, R.string.lbl_qicult,
	 * R.string.lbl_under_qicult, R.string.lbl_emplch,
	 * R.string.lbl_under_emplch, R.string.lbl_emptim,
	 * R.string.lbl_under_emptim, R.string.lbl_outils,
	 * R.string.lbl_under_outils, R.string.lbl_machine,
	 * R.string.lbl_under_machine, R.string.lbl_cheptel,
	 * R.string.lbl_under_cheptel, R.string.lbl_transp,
	 * R.string.lbl_under_transp, R.string.lbl_maison,
	 * R.string.lbl_under_maison, R.string.lbl_stock, R.string.lbl_under_stock,
	 * R.string.lbl_revso, R.string.lbl_under_revso, R.string.lbl_comerc,
	 * R.string.lbl_under_comerc, R.string.lbl_emplo, R.string.lbl_under_emplo,
	 * R.string.lbl_depens, R.string.lbl_under_depens, R.string.lbl_cultyp,
	 * R.string.lbl_under_cultyp, R.string.lbl_recoltem,
	 * R.string.lbl_under_recoltem, R.string.lbl_rectypem,
	 * R.string.lbl_under_rectypem, R.string.lbl_cultjad,
	 * R.string.lbl_under_cultjad, R.string.lbl_recuti,
	 * R.string.lbl_under_recuti, R.string.lbl_consoma,
	 * R.string.lbl_under_consoma, R.string.lbl_repnum,
	 * R.string.lbl_under_repnum, R.string.lbl_ratgfd,
	 * R.string.lbl_under_ratgfd, R.string.lbl_ratcomp,
	 * R.string.lbl_under_ratcomp, R.string.lbl_supnut,
	 * R.string.lbl_under_supnut, R.string.lbl_supnutyp,
	 * R.string.lbl_under_supnutyp, R.string.lbl_ratvente,
	 * R.string.lbl_under_ratvente, R.string.lbl_ratupart,
	 * R.string.lbl_under_ratupart, R.string.lbl_mecadapt,
	 * R.string.lbl_under_mecadapt, R.string.lbl_aprodiff,
	 * R.string.lbl_under_aprodiff, R.string.lbl_revsoSupply,
	 * R.string.lbl_under_revsoSupply, R.string.lbl_aprodiffMeans,
	 * R.string.lbl_under_aprodiffMeans, R.string.lbl_dette,
	 * R.string.lbl_under_dette, R.string.male, R.string.female,
	 * R.string.total_number_of_persons_in_the_household,
	 * R.string.number_of_children_under_five_years_old_present_in_the_household
	 * ,
	 * R.string.number_of_persons_who_arrived_in_the_household_last_three_months
	 * , R.string.number_of_persons_who_left_the_household_last_three_months,
	 * R.string.number_of_persons_who_died_in_the_household_last_three_months,
	 * R.
	 * string.number_of_children_under_twofour_months_who_died_last_three_months
	 * , R.string.camel, R.string.bull, R.string.cow, R.string.sheep,
	 * R.string.goat, R.string.poultry, R.string.horse,
	 * R.string.drilled_well_with_manual_pomp, R.string.tip_well_or_protected,
	 * R.string.not_protected_well, R.string.river__oxbow_lake__pond,
	 * R.string.rainwater, R.string.other_source, R.string.in_the_village,
	 * R.string.less_than_thirty_minutes,
	 * R.string.between_thirty_minutes_and_one_hour,
	 * R.string.between_one_and_two_hours, R.string.more_than_two_hours,
	 * R.string.primary, R.string.secondary, R.string.koranic_school,
	 * R.string.university, R.string.none, R.string.private_school,
	 * R.string.public_school, R.string.arab, R.string.kanenmbu, R.string.goran,
	 * R.string.french, R.string.fulbe, R.string.nomad_onehundred_percent,
	 * R.string.nomad_and_sedentary, R.string.sedentary_onehundred_percent,
	 * R.string.from_time_to_time, R.string.all_the_time, R.string.motorbike,
	 * R.string.vehicle, R.string.truck, R.string.owner, R.string.tenant,
	 * R.string.mil, R.string.niebe, R.string.other_peas, R.string.corn_maize,
	 * R.string.vegetables, R.string.rice, R.string.peanuts, R.string.bean,
	 * R.string.bere_bere, R.string.consumption, R.string.conservation,
	 * R.string.sale_exchange_on_the_market, R.string.repayment_of_debt,
	 * R.string.ceremonial_contribution, R.string.cereals, R.string.root_tubers,
	 * R.string.peas_lentils, R.string.milk_dairy, R.string.eggs,
	 * R.string.meat_gut_offal, R.string.fish_lake_products, R.string.oil_lipid,
	 * R.string.sugar_honey, R.string.fruits_vegetables, R.string.nuts,
	 * R.string.sorghum, R.string.csb, R.string.oil, R.string.sugar,
	 * R.string.salt, R.string.ppdoz, R.string.supplumpy_spp,
	 * R.string.dont_know, R.string.father, R.string.grand_mother,
	 * R.string.mother, R.string.no, R.string.other, R.string.yes,
	 * R.string.previousButton, R.string.helpButton, R.string.nextButton,
	 * R.string.jumptostartButton, R.string.submithouseholdButton,
	 * R.string.lbl_submitpage_review_household,
	 * R.string.messageNumberChildrenIncorrect, R.string.messageCorrectAnswers,
	 * R.string.messageHelp, R.string.noanswergiven, R.string.titleError,
	 * R.string.titleHelp};/*
	 * 
	 * 
	 * public void initHashMap() { System.out.println(formStringKeys.length +
	 * " " + formStringValues.length); for (int i = 0; i <
	 * formStringValues.length; i++) this.hashmapStringValues
	 * .put(formStringKeys[i], formStringValues[i]); }
	 */
}
