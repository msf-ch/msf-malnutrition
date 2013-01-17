/**
 * Script to make sure we don't have all those nasty MSF not found errors when
 * debugging
 */

function MSF_debug() {
	this.getStringValue = getStringValue;

	this.storeData = storeData;

	this.showAlertMessage = showAlertMessage;
	this.getSurveyDate = getSurveyDate;
	this.getHouseholdId = getHouseholdId;
	this.getHouseholdChiefName = getHouseholdChiefName;
	this.getVillageName = getVillageName;
}

function getStringValue(key) {
	return "DEBUG_VALUE";
}

function storeData(dataToStore) {
}

function showAlertMessage(title, message) {
	alert(title + "... " + message);
}

function getSurveyDate() {
	return "04/28/1983";
}

function getHouseholdId() {
	return "9912";
}

function getHouseholdChiefName() {
	return "Chief's name";
}

function getVillageName() {
	return "Village name";
}

function forceNextPage() {
	var activePage = $.mobile.activePage;
	if (activePage.attr('id') == $("div:jqmData(role='page')").last().attr('id')) {
		//if we're at the last page, submit
		submit(activePage);
	} else {
		var nextPage = $.mobile.activePage.next("div:jqmData(role='page')");
		$.mobile.changePage( "#" + nextPage.attr('id'));
	}
}