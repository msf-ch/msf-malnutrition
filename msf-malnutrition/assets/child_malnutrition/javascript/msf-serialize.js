// JavaScript Document
window.formData = new FormData();

function pushFormValuesToModel(element) {
	
	//can't use these in malnutrition, the openmrs fields aren't defined!!
	//var radios = element.find("input[openmrs-fieldtype='radio']");
	//var texts = element.find("input[openmrs-fieldtype='text']");
	/*var allopenmrsfields = element.find("[openmrs-fieldtype]");
	
	allopenmrsfields.each(function(index, openmrselement) {
		var field = $(openmrselement);
		
		var concept = "";
		var value = "";
		var valueCoded = "";
		var openmrsClass = field.attr('openmrs-class');
		
		var fieldType = field.attr("openmrs-fieldtype");
		//figure out the field type
		if (fieldType == '') { //if there's no class info, improvise
			var type = field.attr('type');
		} else if (fieldType == 'radio') {
			var checkedelement = field.find(":checked");
			
			concept = field.attr('openmrs-concept');
			value = checkedelement.attr('value');
			valueCoded = checkedelement.attr('openmrs-valuecoded');
			
		} else if (fieldType == 'text') {
			concept = field.attr('openmrs-concept');
			value = field.val();
			valueCoded = "";
		} else if (fieldType == 'checkbox-boolean') {
			concept = field.attr('openmrs-concept');
			if(field.is(':checked')) {
				value = "true";
			} else {
				value = "false";
			}
			valueCoded = "";
		} else if (fieldType == 'checkbox-coded') {
			concept = field.attr('openmrs-concept');
			if(field.is(':checked')) {
				valueCoded = field.attr('openmrs-checked-concept');
			} else {
				valueCoded = field.attr('openmrs-unchecked-concept');
			}
			value = "";
		}
		
		//figure out where the data goes
		if(openmrsClass == 'patient') {
			if(formData.patient[concept] != undefined) {
				formData.patient[concept] = value;
			} else {
				console.log('undefined patient element' + openmrsClass);
			}
		} else if (openmrsClass == 'encounter') {
			if(formData.encounter[concept] != undefined) {
				formData.encounter[concept] = value;
			} else {
				console.log('undefined encounter element' + openmrsClass);
			}
		} else if (openmrsClass == 'obs') {
			var foundMatchingObs = false;
			for (var i = 0; i < formData.obs.length; i++) {
				var tempobs = formData.obs[i];
				if (tempobs.concept == concept) {
					tempobs.value = value;
					tempobs.valueCoded = valueCoded;
					foundMatchingObs = true;
					break;
				}
			}
			if (!foundMatchingObs) {
				var newObs = new Obs();
				newObs.concept = concept;
				newObs.value = value;
				newObs.valueCoded = valueCoded;
				formData.obs.push(newObs);
			}
		}
	});*/
	var textTrue = MSF.getStringValue("form_value_true");
	var textFalse = MSF.getStringValue("form_value_false");
	
	var inputfields = element.find("input:not([type='radio']):not(:jqmData(icon)),fieldset:jqmData(role='controlgroup'),select");
	inputfields.each(function(index, inputelement) {
		
		var field = $(inputelement);
		var type = field.attr('type');
		var fieldId = field.attr('id');
		
		var fieldValue = "";
		if (field.is("input")) {
			if (type == "checkbox") {
				if (field.is(":checked")) {
					fieldValue = textTrue;
				} else {
					fieldValue = textFalse;
				}
			} else {
				//Number, text, etc
				fieldValue = field.val();
			}
		} else if (field.is("fieldset")) {
			var checkboxes = field.find("[type='checkbox']");
			if (checkboxes.length == 0) {
				var fieldId = field.attr('id');
				var checkedField = field.find(":checked");
				
				if (checkedField != undefined && checkedField.length > 0) {
					fieldValue = checkedField.val();
				} else {
					return;
				}
			}
		} else if (field.is("select") || field.is("button")) {
			fieldValue = field.val();
		} 
		
		pushSingleObs(fieldId, fieldValue);
	});
}

function pushSingleObs(fieldId, fieldValue) {
	var found = false;
	
	for (var i = 0; i < formData.obs.length; i++) {
		var tempobs = formData.obs[i];
		if (tempobs.concept == fieldId) {
			tempobs.value = fieldValue;
			found = true;
		}
	}
	
	if (!found) {
		var newObs = new Obs();
		newObs.concept = fieldId;
		newObs.value = fieldValue;
		formData.obs.push(newObs);
	}
}

//objects that represent the serialized objects from the app
function FormData() {
	this.obs = [];
	this.patient = new Patient();
	this.encounter = new Encounter();
}

function Obs() {
	this.concept = "";
	this.value = "";
	this.valueCoded = "";
}

function Patient() {
	this.identifier = "";
	
	this.givenName = "";
	this.middleName = "";
	this.familyName = "";
	
	this.gender = "";
	
	this.birthDate = "";
	this.birthdateEstimated = false;
	this.age = "";
		
	this.deathDate = "";
	this.dead = false;
		
	this.city = "";
}

function Encounter() {
	this.encounterDatetime = "";
	this.location = "";
	this.provider = "";
	this.encounterType = "";
}