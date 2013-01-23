// JavaScript Document
var debug = false;
console.log('started msf-script');

//GLOBAL VARS
var numberRevso;
var numberDepens;
var numberAprodiff;
var numberRevsoSupply;
var fromSubmitPage;
var maxlength;
var showIllnessPage;

//Startup stuff
$(document).ready(function(e) { 
	console.log('started document.ready');
	//If no MSF object then we're debugging...
	if(window.MSF == undefined) {
		window.MSF = new MSF_debug();
	}
	 
	fromSubmitPage = new Boolean(false);
	showIllnessPage = new Boolean(false);
	
	var firstContent = $("div#intropage:jqmData(role='page') > div:jqmData(role='content') > .formintro");
	var footerNav = $("#footernav");
	$("div:jqmData(role='page')").not("#intropage").not("#submitpage").each(function(index, element) {
		
		//NICKDEBUG: Add links to intro page
		if (debug) {
			var lin = $("<p>").append($("<a>").html(element.id).attr('href', '#' + element.id));
			firstContent.append(lin);
		}
		
		//Add footer nav to all pages but first
		var footer = $(element).children("div:jqmData(role='footer')");
		var navtoinsert = footerNav.clone(true, false);
		navtoinsert.attr("id", navtoinsert.attr("id") + index);
		footer.append(navtoinsert);
		footer.trigger('create');
		
		$(element).live('pagebeforeshow', inactivateNavButtons);
	});
	
	footerNav.remove();
	
	//Add events to footer nav buttons
	$("a[action='next']").click(next);
	$("a[action='help']").click(help);
	$("a[action='prev']").click(prev);
	$("a[action='jumptostart']").click(jumptostart);
	$("a[action='submit']").click(submit);
	$("form").submit(function(e) {next(e); return false;});	
	
	//set up the form
	if(window.MSF != undefined && window.MSF != null) {	
		$("#suividat").val(MSF.getSurveyDate());
		$("#suividat").datebox('disable');
		
		$("#zone").val(MSF.getAreaName());
		$("#zone").textinput('disable');
		
		$("#enqnom").val(MSF.getIdPollster());
		$("#enqnom").textinput('disable');
		
		$("#nozone").val(MSF.getIdArea());
		$("#nozone").textinput('disable');
		
		$("#menagn").val(MSF.getHouseholdId());
		$("#menagn").textinput('disable');
		
		$("#mennom").val(MSF.getHouseholdChiefName());
		$("#mennom").textinput('disable');
		
		$("#novillage").val(MSF.getIdVillage());
		$("#novillage").textinput('disable');
		
		$("#villagen").val(MSF.getVillageName());
		$("#villagen").textinput('disable');
	}
	
	resolveJellyBeanIssueInput();
	loadAllStringValues();
});

function next(e) {
	//get the next page
	
	var currentPage;
	if (arguments.length > 0) {
		e.preventDefault();
		currentPage = $(e.srcElement).parents("div:jqmData(role='page')");
	} else {
		currentPage = $.mobile.activePage;
	}
	
	var next = currentPage.next("div:jqmData(role='page')");
	var pageform = $(currentPage).find("form");
	pageform = pageform.attr("id");	
	
	var pageOne = new Boolean(false);
	var pageTwo = new Boolean(false);
	var pageThree = new Boolean(false);
	var pageFour = new Boolean(false);
	var pageFive = new Boolean(false);
	
	switch(pageform){
		case "formpageone":
			pageOne = formPageOne(e);
			if(!pageOne)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;			
		case "formpagetwo":
			pageTwo = formPageTwo(e);
			if(!pageTwo)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;
		case "formpagethree":
			pageThree = formPageThree(e);
			if(!pageThree)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;
		case "formpagefour":
			pageFour = formPageFour(e);
			if(!pageFour)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;
		case "formpagefive":
			pageFive = formPageFive(e);
			if(!pageFive)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;
	}
	
	if (next && pageOne && pageTwo && pageThree && pageFour && pageFive) {
		if(fromSubmitPage==false){
			if(currentPage.attr('id')=='pagefour'){
				if(showIllnessPage==false){
					$.mobile.changePage( "#submitpage", { transition: "forward slide"} ); //transition: "forward slide" takes too long
					save(currentPage);					
				}
				else {
					$.mobile.changePage( "#" + next.attr('id'), { transition: "forward slide"} ); //transition: "forward slide" takes too long
					save(currentPage);
					
				}
			}
			else {
				$.mobile.changePage( "#" + next.attr('id'), { transition: "forward slide"} ); //transition: "forward slide" takes too long
				save(currentPage);
			}
			
		}
		else {
			if(fromSubmitPage){
				$.mobile.changePage( "#submitpage", { transition: "forward slide"} ); //transition: "forward slide" takes too long
				save(currentPage);
			}
			if(showIllnessPage){
				$.mobile.changePage( "#submitpage", { transition: "forward slide"} ); //transition: "forward slide" takes too long
				save(currentPage);
			}
		}
	}
	
}

function formPageOne(e){
	var suividatOk = new Boolean(false);
	var zoneOk = new Boolean(false);
	var villagenOk = new Boolean(false);
	var mennomOk = new Boolean(false);
	var naissorOk = new Boolean(false);
	var enqnomOk = new Boolean(false);
	var nozoneOk = new Boolean(false);
	var menagnOk = new Boolean(false);
	var novillageOk = new Boolean(false);
	var enfantidOk = new Boolean(false);
	var enfsexOk = new Boolean(false);
	var enfnomOk = new Boolean(false);
	var enfprenomOk = new Boolean(false);
	var enfageOk = new Boolean(false);
	
	//SURVEY DATE
	suividatOk = checkTextField("suividat", "the survey date");
	
	//AREA ZONE
	zoneOk = checkTextField("zone", "the area zone");
	
	//VILLAGE NAME
	villagenOk = checkTextField("villagen", "the village name");
	
	//POLLSTER ID
	enqnomOk = checkIdField("enqnom", 2);
	
	//CHILD AGE
	enfageOk = checkMeasurmentField("enfage", "The value must be between 5 and 25", 5,25);
	
	//VILLAGE ID
	novillageOk = checkIdField("novillage", 2);
	
	//ENFANT ID
	enfantidOk = checkIdField("enfantid", 1);
	
	//AREA ID
    nozoneOk = checkIdField("nozone", 2);
	
	//HOUSEHOLD ID
	menagnOk = checkIdField("menagn", 4);
	
	//HOUSEHOLD CHIEF'S NAME
	mennomOk = checkTextField("mennom", "the household chief's name");
	
	//CHILD'S NAME
	enfnomOk = checkTextField("enfnom","the child's name");
	
	//CHILD'S FIRST NAME
	enfprenomOk = checkTextField("enfprenom", "the child's first name");
	
	//HOW DID YOU GET THE AGE
	naissorOk = checkRadioButton("naissor");

	
	//CHILD SEX
	enfsexOk = checkRadioButton("enfsex");
	
	
	if(zoneOk && villagenOk && mennomOk && naissorOk && enqnomOk && 
	   nozoneOk && menagnOk && novillageOk && suividatOk && enfantidOk && 
	   enfsexOk && enfnomOk && enfprenomOk && enfageOk)
		return true;	
	else
		return false;
}



function formPageTwo(e){	
	var localisOk = new Boolean(false);
	var vitavecOk = new Boolean(false);
	var orphanOk = new Boolean(false);
	var coepouseOk = new Boolean(false);
	var maritstatOk = new Boolean(false);
	
	localisOk = checkTextField("localis", "the location of child's house");
	
	vitavecOk = checkRadioButton("vitavec");
	orphanOk = checkRadioButton("orphan");
	
	coepouseOk = checkNumberField("coepouse", "Enter a correct number of concubines");
	
	maritstatOk = checkRadioButton("maritstat");
	
	if(localisOk && vitavecOk && coepouseOk && orphanOk && maritstatOk){
		return true;
	}
	else
		return false;
}
function formPageThree(e){
	var allaitnaOk = new Boolean(false);
	var allaitagOk = new Boolean(false);
	var laitcontOk = new Boolean(false);
	var vaccinatOk = new Boolean(false);
	var typvaccOk = new Boolean(false);
	
	allaitnaOk = checkRadioButton("allaitna");
	
	allaitagOk = checkNumberField("allaitag", "Enter a correct number of months please");
	
	laitcontOk = checkRadioButton("laitcont");
	vaccinatOk = checkRadioButton("vaccinat");
	
	if($('input:radio[name=vaccinat]:checked').val()=="yes"){		
		typvaccOk = checkCheckboxInsideField("typvacc");
	}
	else {
		uncheckField("typvacc_class");
		$("#lbl_typvacc").removeClass("red-text-bold");
		$("#lbl_under_typvacc").empty();
		typvaccOk = true;
	}
	
	if(allaitnaOk && allaitagOk && laitcontOk && vaccinatOk && typvaccOk)
		return true;
	else
		return false;
}
function formPageFour(e){
	var pbmesureOk = new Boolean(false);
	var oedemOk = new Boolean(false);
	var poidsOk = new Boolean(false);
	var tailleOk = new Boolean(false);
	var maladOk = new Boolean(false);
	
	pbmesureOk = checkMeasurmentField("pbmesure", "The value must be between 50 and 180",50,180);
	oedemOk = checkRadioButton("oedem");
	poidsOk = checkMeasurmentField("poids", "The value must be between 2 and 20",2,20);
	tailleOk = checkMeasurmentField("taille","The value must be between 60 and 90",60,90);
	maladOk = checkRadioButton("malad");
	
	if($('input:radio[name=malad]:checked').val()=="yes"){		
		showIllnessPage = true;
	}
	else {
		showIllnessPage = false;
	}
	
	if(pbmesureOk && oedemOk && poidsOk && tailleOk && maladOk){
		if (showIllnessPage == false){
			
			//Clear all values of the next page
			$("#epcause").val('');			
			uncheckField("epcause_class");			
			uncheckField("eptemps_class");
			uncheckField("epsoin_class");			
			uncheckField("epnorai_class");
			uncheckField("eprecou_class");			
			uncheckField("admiss_class");
			uncheckField("progadmepevol_class");
			uncheckField("eprest_class");
			uncheckField("decisions_class");
			uncheckField("progadm_2_class");
			uncheckField("typprog_class");	
		}
		return true;
	}
	else
		return false;
}
function formPageFive(e){
	var epcauseOk = new Boolean(false);
	var epcauseCheckboxOk = new Boolean(false);
	var eptempsOk = new Boolean(false);
	var epsoinOk = new Boolean(false);
	var epnoraiOk = new Boolean(false);
	var eprecouOk = new Boolean(false);
	var admissOk = new Boolean(false);
	var progadmOk = new Boolean(false);
	var epevolOk = new Boolean(false);
	var eprestOk = new Boolean(false);
	var decision_1Ok = new Boolean(false);
	var decision_2Ok = new Boolean(false);
	var progadm_2Ok = new Boolean(false);
	var typprogOk = new Boolean(false);
	
	epcauseOk = checkTextField("epcause", "the symptoms");
	epcauseCheckboxOk = checkCheckboxEpcauseField("epcause");
	eptempsOk = checkRadioButton("eptemps");
	epsoinOk = checkRadioButton("epsoin");
	
	if($('input:radio[name=epsoin]:checked').val()=="no"){		
		epnoraiOk = checkCheckboxInsideField("epnorai");
	}
	else {
		uncheckField("epnorai_class");
		$("#lbl_epnorai").removeClass("red-text-bold");
		$("#lbl_under_epnorai").empty();
		epnoraiOk = true;
	}
	
	if($('input:radio[name=epsoin]:checked').val()=="yes"){		
		eprecouOk = checkCheckboxInsideField("eprecou");
	}
	else {
		uncheckField("eprecou_class");
		$("#lbl_eprecou").removeClass("red-text-bold");
		$("#lbl_under_eprecou").empty();
		eprecouOk = true;
	}
	
	admissOk = checkRadioButton("admiss");
	
	if($('input:radio[name=admiss]:checked').val()=="yes"){		
		progadmOk = checkRadioButton("progadm");
		epevolOk = checkRadioButton("epevol");
	}
	else {
		uncheckField("progadmepevol_class");
		$("#lbl_progadm").removeClass("red-text-bold");
		$("#lbl_under_progadm").empty();
		$("#lbl_epevol").removeClass("red-text-bold");
		$("#lbl_under_epevol").empty();
		progadmOk = true;
		epevolOk = true;
	}
	
	eprestOk = checkRadioButton("eprest");
	
	if($('input:radio[name=eprest]:checked').val()=="yes"){		
		decision_1Ok = checkRadioButton("decision_1");
		decision_2Ok = checkRadioButton("decision_2");
	}
	else {
		uncheckField("decisions_class");
		$("#lbl_decision_1").removeClass("red-text-bold");
		$("#lbl_under_decision_1").empty();
		$("#lbl_decision_2").removeClass("red-text-bold");
		$("#lbl_under_decision_2").empty();
		decision_1Ok = true;
		decision_2Ok = true;
	}
	
	progadm_2Ok = checkRadioButton("progadm_2");
	
	if($('input:radio[name=progadm_2]:checked').val()=="yes"){		
		typprogOk = checkRadioButton("typprog");
	}
	else {
		uncheckField("typprog_class");
		$("#lbl_typprog").removeClass("red-text-bold");
		$("#lbl_under_typprog").empty();
		typprogOk = true;
	}
	
	if(epcauseOk && epcauseCheckboxOk && eptempsOk && epsoinOk && epnoraiOk && eprecouOk && admissOk &&
	   progadmOk && epevolOk && eprestOk && decision_1Ok && decision_2Ok && progadm_2Ok && typprogOk)
		return true;
	else
		return false;
}
/**FUNCTIONS TO CHECK AND CLEAR FIELDS**/
//FUNCTIONS FOR UNCHECKED FIELDS
function uncheckField(fieldClass){
	var theField = "."+fieldClass;
	if($(theField).is(':checked')==true){
		$(theField).prop('checked', false);
		$(theField).checkboxradio("refresh");
	}
}

//BASICS FUNCTIONS
function checkRadioButton(id){
	if(!$("input:radio[name="+id+"]:checked").val()){
		$("#lbl_under_"+id).remove();
		$("#lbl_"+id+" br").remove();
		$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
		$("#lbl_"+id).addClass("red-text-bold");		
		return false;
	}
	else 
		return true;
}

function clearRadioButtonErrorFields(id){
	$("#"+id).change(function(event) {
		$("#lbl_"+id).removeClass("red-text-bold");
		$("#lbl_under_"+id).empty();
	});
}

function checkTextField(id, message){
	if(!$("#"+id).val()){
		$("#lbl_under_"+id).remove();
		$("#lbl_"+id+" br").remove();
		$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
		$("#lbl_"+id).addClass("red-text-bold");		
		return false;
	}
	else 
		return true;
}

function clearTextErrorFields(id){
	$("#"+id).live('keyup',function(event) {
		var val = $("#"+id).val();
		if(val!=""){
			$("#lbl_"+id).removeClass("red-text-bold");
			$("#lbl_under_"+id).empty();
		}
	});
	
	$("#"+id).live('focusout',function(event) {
		var val = $.trim($("#"+id).val());
		$("#"+id).val(val);
	});
}

function clearDateErrorFields(id){
	$("#"+id).live('change',function(event) {
		var val = $("#"+id).val();
		if(val!=""){
			$("#lbl_"+id).removeClass("red-text-bold");
			$("#lbl_under_"+id).empty();
		}
	});
	
	$("#"+id).live('focusout',function(event) {
		var val = $.trim($("#"+id).val());
		$("#"+id).val(val);
	});
}

function checkIdField(id,theLength){
	var theId = $("#"+id);
	if(!theId.val() ||theId.val() <= 0 || theId.val().length < theLength || isNaN(theId.val())){
		$("#lbl_under_"+id).remove();
		$("#lbl_"+id+" br").remove();
		$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
		$("#lbl_"+id).addClass("red-text-bold");
		return false;
	}
	else
		return true;
}

function clearIdError(id, theLength){
	var theId = $("#"+id);
	theId.live('keyup',function(event) {
		var val = $.trim(theId.val());
		if((val > 0 || !(isNan(val)))&& val.length == theLength){
			$("#lbl_"+id).removeClass("red-text-bold");
			$("#lbl_under_"+id).empty();
		}
	});
	
	theId.live('focusout',function(event) {
		var val = $.trim(theId.val());
		theId.val(val);
	});
}

function checkNumberField(id, message){
	var theId = $("#"+id);
	if(!theId.val() || theId.val() <= 0 || isNaN(theId.val())){
		$("#lbl_under_"+id).remove();
		$("#lbl_"+id+" br").remove();
		$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
		$("#lbl_"+id).addClass("red-text-bold");
		return false;
	}	
	else
		return true;
}

function clearNumberError(id){
	var theId = $("#"+id);
	theId.live('keyup',function(event) {
		var val = $.trim(theId.val());
		if((val > 0 || !(isNan(val)))&& val.length > 0){
			$("#lbl_"+id).removeClass("red-text-bold");
			$("#lbl_under_"+id).empty();
		}
	});

	theId.live('focusout',function(event) {
		var val = $.trim(theId.val());
		theId.val(val);
	});
}

function checkCheckboxField(id){
	if($("#"+id+"_checkbox input[type=checkbox]:checked").length < 1){
		$("#lbl_under_"+id).remove();
		$("#lbl_"+id+" br").remove();
		$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
		$("#lbl_"+id).addClass("red-text-bold");		
		return false;
	}
	else 
		return true;
}

function clearCheckboxField(id){
	$("#"+id).change(function(event) { 
		if($("#"+id+"_checkbox input[type=checkbox]:checked").length < 1){
			$("#lbl_under_"+id).remove();
			$("#lbl_"+id+" br").remove();
			$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
			$("#lbl_"+id).addClass("red-text-bold");
		}
		else {
			$("#lbl_"+id).removeClass("red-text-bold");
			$("#lbl_under_"+id).empty();	
		}
});
}

function checkCheckboxInsideField(id){
	if($("#"+id+"fields input[type=checkbox]:checked").length < 1){
		$("#lbl_under_"+id).remove();
		$("#lbl_"+id+" br").remove();
		$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
		$("#lbl_"+id).addClass("red-text-bold");		
		return false;
	}
	else 
		return true;
}

function clearCheckBoxInsideField(id){
	$("#"+id).change(function(event) { 
		if($("#"+id+"fields input[type=checkbox]:checked").length < 1){
			$("#lbl_under_"+id).remove();
			$("#lbl_"+id+" br").remove();
			$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
			$("#lbl_"+id).addClass("red-text-bold");
		}
		else {
			$("#lbl_"+id).removeClass("red-text-bold");
			$("#lbl_under_"+id).empty();	
		}
	});
}

function checkLineField(id1,id2){
	var theId1 = $("#"+id1);
	var theId2 = $("#"+id2);
	
	if((!theId1.val() || theId1.val() < 0 || isNaN(theId1.val()))
			||(!theId2.val() || theId2.val() < 0 || isNaN(theId2.val()))){
			
			if(!theId1.val())
				theId1.addClass("error");  
			if(!theId2.val())
				theId2.addClass("error");  
			return false;
		}
		else
			return true;
}

function checkLineFieldUnique(id){
	var theId = $("#"+id);
	
	if(!theId.val() || theId.val() < 0 || isNaN(theId1.val())){
			if(!theId.val())
				theId.addClass("error");  
			return false;
		}
		else
			return true;
}

function clearLineField(id){

	$("#"+id).live('keyup',function(event) {
		var val = $.trim($("#"+id).val());
		if((val >= 0 || !(isNan(val))) && val.length > 0){
			$("#"+id).removeClass("error");
		}
	});
	
	$("#"+id).live('focusout',function(event) {
		var val = $.trim($("#"+id).val());
		$("#"+id).val(val);
	});
}

function checkButtonsCount(number, id, numberMax){
	if(number < 9){
		$("#lbl_under_"+id).remove();
		$("#lbl_"+id+" br").remove();
		$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
		$("#lbl_"+id).addClass("red-text-bold");
		return false;
	}
	else {
		$("#lbl_"+id).removeClass("red-text-bold");
	    $("#lbl_under_"+id).empty();
		 return true;
	}
}

function checkCheckboxEpcauseField(id){
	if($("#"+id+"_checkbox input[type=checkbox]:checked").length < 1){
		$("#lbl_under_"+id).remove();
		$("#lbl_"+id+" br").remove();
		$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
		$("#lbl_"+id).addClass("red-text-bold");		
		return false;
	}
	else 
		return true;
}
function clearCheckboxEpcauseField(id){
	$("#"+id+"_checkbox").change(function(event) { 
		if($("#"+id+"_checkbox input[type=checkbox]:checked").length < 1){
			$("#lbl_under_"+id).remove();
			$("#lbl_"+id+" br").remove();
			$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
			$("#lbl_"+id).addClass("red-text-bold");
		}
		else {
			$("#lbl_"+id).removeClass("red-text-bold");
			$("#lbl_under_"+id).empty();	
		}
	});
}

//SPECIFIC FUNCTION FOR MEASURMENT
function checkMeasurmentField(id,message,valueInf,valueSup){
	var theId = $("#"+id);
	if(!theId.val() || theId.val() < valueInf || theId.val() > valueSup || isNaN(theId.val())){
		$("#lbl_under_"+id).remove();
		$("#lbl_"+id+" br").remove();
		$("#lbl_"+id).append("<br/><label id='lbl_under_"+id+"' class='red-text'>"+MSF.getStringValue('lbl_under_'+id)+"</label>");
		$("#lbl_"+id).addClass("red-text-bold");
		return false;
	}	
	else
		return true;
}

function clearMeasurmentField(id, valueInf,valueSup){
	var theId = $("#"+id);
	theId.live('keyup',function(event) {
		var val = $.trim(theId.val());
		if((val >= valueInf && val <= valueSup) && val.length > 0){
			$("#lbl_"+id).removeClass("red-text-bold");
			$("#lbl_under_"+id).empty();
		}
	});

	theId.live('focusout',function(event) {
		var val = $.trim(theId.val());
		theId.val(val);
	});
}

/**END FUNCTIONS**/

function prev(e) {	
	//get the previous page
	var currentPage;
	if (arguments.length > 0) {
	e.preventDefault();
		currentPage = $(e.srcElement).parents("div:jqmData(role='page')");
	} else {
		currentPage = $.mobile.activePage;
	}
	var prev = currentPage.prev("div:jqmData(role='page')");
	
	if(prev){
		if(currentPage.attr('id')=="submitpage"){
				if(showIllnessPage==false)
					$.mobile.changePage( "#pagefour", { transition: "reverse slide"} ); 
				else
					$.mobile.changePage( "#" + prev.attr('id'), { transition: "reverse slide"} );
		}
		else 
			$.mobile.changePage( "#" + prev.attr('id'), { transition: "reverse slide"} );
	}
}

function help(e) {
	e.preventDefault();
	var tabHelpMsg =new Array;
	tabHelpMsg["idform"]="ZOUZOU";
	
	var pageform = $.mobile.activePage.find('form').attr('id');

	var filtre = new Boolean();
	
	for(var i in tabHelpMsg){
		if(pageform==i){
			message = tabHelpMsg[i];
			  MSF.showAlertMessage(MSF.getStringValue("titleHelp"),MSF.getStringValue("messageHelp"));
			  filtre = true;
		}
		else if(filtre != true){
			MSF.showAlertMessage(MSF.getStringValue("titleHelp"),MSF.getStringValue("messageHelp"));
		}
		
	}
	
	inactivateNavButtons(e);
}

function save(page) {
	var currentPage = $(page); //make sure we're dealing with JQuery
	var currentPageContent = currentPage.find(":jqmData(role='content')");
	
	pushFormValuesToModel(currentPageContent);
	
	var result = JSON.stringify(formData);
	//MSF.storeFormData(result);
	
	console.log('save() finished');
	
	return result;
}

function load() {
	
}

function submit(e) {
	MSF.storeData(JSON.stringify(formData));
}

function jumptostart(e) {
	e.preventDefault();	
	$.mobile.changePage("#pageone", { transition: "reverse slide"});
}

function inactivateNavButtons(e, data) {
	//$(e.target).find("div:jqmData(role='navbar')").find("a").removeClass("ui-btn-active"); footer is not embedded in page in 1.1, possible bug. workaround.
	$(document).find("div:jqmData(role='footer')").find("a").removeClass("ui-btn-active");
}

function applyFocus(firstField, secondField, lengthFirstField){
	$("#"+firstField).keyup(function(e){
		if($(this).val().length == lengthFirstField)
			$("#"+secondField).focus();
		
	});
}

function resolveJellyBeanIssueInput(){
	var ver = window.navigator.appVersion;
    ver = ver.toLowerCase();

	if (ver.indexOf("android 4.1") >= 0){  
	    var idMaxLengthMap = {};
	
	    //loop through all input-text and textarea element
	    $.each($(':input'), function () {  
	    	
	    	var type = $(this).attr('type');  
	    	
	    	if(type=="number"){
			        var id = $(this).attr('id');
			        maxlength = $(this).attr('maxlength');
			        if ((typeof id !== 'undefined') && (typeof maxlength !== 'undefined')) {
			        	idMaxLengthMap[id] = maxlength;
			            $(this).removeAttr('maxlength');
			        }
	    	}
	    });
	
	    $(':input').bind('change keyup', function() {
            var type = $(this).attr('type');	    	
			 if(type=="number"){
			        var id = $(this).attr('id'),
			            maxlength = '';
			        if ((typeof id !== 'undefined') && idMaxLengthMap.hasOwnProperty(id)) {
			            maxlength = idMaxLengthMap[id];
			            if ($(this).val().length > maxlength) {
			            	$(this).val($(this).val().slice(0, maxlength));
			            }
			        }
	    	}
	    });
	}
}

function loadAllStringValues(){
	//Loop for each h1
	$("h1").each(function() {
		var id = $(this).attr('id');
		if(typeof(id)== "string"){
			$('#'+id).html(MSF.getStringValue(id));
		}
	});
	
	//Loop for each h2
	$("h2").each(function() {
		var id = $(this).attr('id');
		$('#'+id).html(MSF.getStringValue(id));
	});
	
	//Loop for radio buttons
	$("fieldset").each(function() {
		var id = $(this).attr('id');
		var text;
		if(id !="epcause_choice"){
				$(this).children().each(function(){
					text = $(this).text();
					if(text.length > 0 || text !="" ) {
						text = $.trim(text);
						text = text.toLowerCase().replace(/ /g, '_');
						text = text.replace(/'/g, '');
						text = text.replace(/-/g, '_');
						text = text.replace("/", "_");
						text = text.replace(/\)/g, '');
						text = text.replace(/\(/g, '');
						text = text.replace(/1/g, 'one');
						text = text.replace(/2/g, 'two');
						text = text.replace(/3/g, 'three');
						text = text.replace(/4/g, 'four');
						text = text.replace(/5/g, 'five');
						text = text.replace(/>/g, 'superior');
						
						var id = $(this).attr('id');
						
						if(typeof(id)=="undefined")
							$(this).find(".ui-btn-text").text(MSF.getStringValue(text));
						else
							$('#'+id).text(MSF.getStringValue(text));
						}
		
				});
		}
	});
	
	/*//Loop for radio buttons who are bugging
	$("fieldset").each(function() {
		var id = $(this).attr('id');
		if(id=="naissor" || id=="enfsex"){
			var text;
			$(this).children().each(function(){
				text = $(this).text();
				if(text.length > 0) {
					text = $.trim(text);
					text = text.toLowerCase().replace(/ /g, '_');
					$(this).find(".ui-btn-text").text(MSF.getStringValue(text));
				}
			});
		}
	});*/
	
	//Loop for gridView
	$(".epcause_class").each(function() {
		var id = $(this).attr('id');
		var text = $('label[for="'+id+'"]').text();
		text = $.trim(text);
		text = text.toLowerCase().replace(/ /g, '_');
		text = text.replace("/", "_");
		$('label[for="'+id+'"]').text(MSF.getStringValue(text));
	});
	
	//Loop for each labels
	$("label").each(function() {
		var id = $(this).attr('id');
		if(typeof(id)== "string"){
			var label = id.substring(0,8);
			if(label != "lbl_rbtn"){
				var under_id = id.split("_");
				$('#'+id).html(MSF.getStringValue(id));
				if(under_id.length==3){
					$('#lbl_under_'+under_id[1]+'_'+under_id[2]).html(MSF.getStringValue('lbl_under_'+under_id[1]+'_'+under_id[2]));
				}
				else{
				 	$('#lbl_under_'+under_id[1]).html(MSF.getStringValue('lbl_under_'+under_id[1]));
				}
			}
		}
	});
	
	//Id child label
	$("#lbl_enfantid").text(MSF.getStringValue("lbl_enfantid")+" : "+MSF.getHouseholdId());
	
	//P label
	$("#p_epcause").text(MSF.getStringValue("p_epcause"));
	//Footer buttons
	$('.previousButton').find('.ui-btn-text').text(MSF.getStringValue("previousButton"));
	$('.helpButton').find('.ui-btn-text').text(MSF.getStringValue("helpButton"));
	$('.nextButton').find('.ui-btn-text').text(MSF.getStringValue("nextButton"));
	
	//SubmitPage
	$('#submitPagePreviousButton').text(MSF.getStringValue("previousButton"));
	$('#jumptostartButton').text(MSF.getStringValue("jumptostartButton"));
	$('#submitchildButton').text(MSF.getStringValue("submitchildButton"));	
	$('#lbl_submitpage_review_child').text(MSF.getStringValue("lbl_submitpage_review_child"));
}
/** PAGE 1 **/
$("#pageone").live('pageinit', function(e) {
	/** DATE **/
	clearDateErrorFields("suividat");
	
	/** ZONE **/
	clearTextErrorFields("zone");
	
	/** IDS **/
	clearIdError("enqnom", 2);
	clearIdError("nozone",2);
	clearIdError("novillage",2);
	clearIdError("menagn",4);
	clearIdError("enfantid",1);
	
	applyFocus("enqnom","nozone",2);
	applyFocus("nozone","menagn",2);
	applyFocus("menagn","villagen",4);
	applyFocus("novillage","enfantid",2);
	applyFocus("enfantid","enfnom",1);
	
	/**CHILD'S AGE**/
	clearNumberError("enfage");
	
	/**VILLAGE NAME **/
	clearTextErrorFields("villagen");
	
	/**HOUSEHOLD CHIEF'S NAME **/
	clearTextErrorFields("mennom");
	
	/**CHILD NAME **/
	clearTextErrorFields("enfnom");
	
	/**CHILD FIRST NAME **/
	clearTextErrorFields("enfprenom");
	
	/**GET THE CHILD AGE **/
	clearRadioButtonErrorFields("naissor");
	clearRadioButtonErrorFields("enfsex");
});

/** PAGE 2 **/
$("#pagetwo").live('pageinit', function(e) {
	
	clearTextErrorFields("localis");
	
	clearRadioButtonErrorFields("vitavec");
	clearRadioButtonErrorFields("orphan");
	
	clearNumberError("coepouse");
	
	clearRadioButtonErrorFields("maritstat");
	
});

/** PAGE 3 **/
$("#pagethree").live('pageinit', function(e) {
	$("#typvaccfields").hide();
	
	$("input[name='vaccinat']").live('change',function(event) {  
        var val = $('input[name=vaccinat]:checked').val();
        if(val == 'yes')
    		$("#typvaccfields").show("normal");
        else
    		$("#typvaccfields").hide("normal");
	});
	
	clearRadioButtonErrorFields("allaitna");
	
	clearNumberError("allaitag");
	
	clearRadioButtonErrorFields("laitcont");
	clearRadioButtonErrorFields("vaccinat");
	
	clearCheckBoxInsideField("typvacc");
});

/** PAGE 4 **/
$("#pagefour").live('pageinit', function(e) {
	
	clearMeasurmentField("pbmesure",50,180);	
	clearRadioButtonErrorFields("oedem");
	clearMeasurmentField("poids",2,20);
	clearMeasurmentField("taille",60,90);
	clearRadioButtonErrorFields("malad");
	
	$('#poids').focusout(function(e){
		var val;
		var text = $(this).val().toString();
		if(text.substring(text.length-1,text.length)=="."){
			text = text.substring(0,text.length-1);
			val = parseFloat(text);
			$(this).val(val);
		}		
	});
	
});

/** PAGE 5 **/
$("#pagefive").live('pageinit', function(e) {
	$("#epnoraifields").hide();
	$("#eprecoufields").hide();
	$("#progadmepevolfields").hide();
	$("#decisionsfields").hide();
	$("#typprogfields").hide();
	
	$("input[name='epsoin']").live('change',function(event) {  
        var val = $('input[name=epsoin]:checked').val();
        if(val == 'no'){
    		$("#epnoraifields").show("normal");
    		$("#eprecoufields").hide("normal");
        }
        if(val == 'yes') {
    		$("#epnoraifields").hide("normal");
    	    $("#eprecoufields").show("normal");
        }
        if(val == 'dontknow') {
    		$("#epnoraifields").hide("normal");
    	    $("#eprecoufields").hide("normal");
        }
	});
	
	$("input[name='admiss']").live('change',function(event) {  
        var val = $('input[name=admiss]:checked').val();
        if(val == 'yes')
        	$("#progadmepevolfields").show("normal");
        else
        	$("#progadmepevolfields").hide("normal");
	});
	
	$("input[name='eprest']").live('change',function(event) {  
        var val = $('input[name=eprest]:checked').val();
        if(val == 'yes')
        	$("#decisionsfields").show("normal");
        else
        	$("#decisionsfields").hide("normal");
	});
	
	$("input[name='progadm_2']").live('change',function(event) {  
        var val = $('input[name=progadm_2]:checked').val();
        if(val == 'yes')
        	$("#typprogfields").show("normal");
        else
        	$("#typprogfields").hide("normal");
	});
	
	
	clearTextErrorFields("epcause");
	clearCheckboxEpcauseField("epcause");
	clearRadioButtonErrorFields("eptemps");
	clearRadioButtonErrorFields("epsoin");
	
	clearCheckBoxInsideField("epnorai");
	clearCheckBoxInsideField("eprecou");
	
	clearRadioButtonErrorFields("admiss");
	clearRadioButtonErrorFields("progadm");
	clearRadioButtonErrorFields("epevol");
	
	clearRadioButtonErrorFields("eprest");
	clearRadioButtonErrorFields("decision_1");
	clearRadioButtonErrorFields("decision_2");
	
	clearRadioButtonErrorFields("progadm_2");
	clearRadioButtonErrorFields("typprog");
});

/** SUBMIT PAGE **/
$("#submitpage").live('pagebeforeshow', function() {
	fromSubmitPage = false;
	var list = $("#datareviewlist");
	list.children().remove("li");
	$("form").each(function() {
		if(showIllnessPage==false){
			    if($(this).attr('id')!="formpagefive"){
					var page = $(this).parents(":jqmData(role='page')");
					var separator = $("<li>").attr('data-role', 'list-divider').html(page.attr('name'));
					var pageid = page.attr('id');
					list.append(separator);
					var data = $(this).serializeArray();
					for (var i = 0; i < data.length; i++) {
						var li = $("<li>");
						var a = $("<a>").attr('href', '#' + pageid);
						var nameElements = $(this).find("label[id='" + data[i].id + "']");
						var name = data[i].name;
						if (nameElements.length > 0 && nameElements.html() != undefined && nameElements.html() != '') {
							name = nameElements.html();
							while (name.substr(-1) == ':' || name.substr(-1) == ' ') {
								name = name.substr(0, name.length - 1);
							}
						}
						var value = data[i].value;
						if (value == undefined || value == '' && data[i].name !="namelocationquarantine") {
								a.append($("<h2>").html(name));
								a.append($("<p>").html(MSF.getStringValue("noanswergiven").toUpperCase()).addClass('red-text'));	
						} else {
								a.append($("<h2>").html(name));
								a.append($("<p>").html(value));
						}
						if(data[i].name !="namelocationquarantine"){
							li.append(a);
							list.append(li);
						}
						
					}
			    }
		}
		else {
			var page = $(this).parents(":jqmData(role='page')");
			var separator = $("<li>").attr('data-role', 'list-divider').html(page.attr('name'));
			var pageid = page.attr('id');
			list.append(separator);
			var data = $(this).serializeArray();
			for (var i = 0; i < data.length; i++) {
				var li = $("<li>");
				var a = $("<a>").attr('href', '#' + pageid);
				var nameElements = $(this).find("label[id='" + data[i].id + "']");
				var name = data[i].name;
				if (nameElements.length > 0 && nameElements.html() != undefined && nameElements.html() != '') {
					name = nameElements.html();
					while (name.substr(-1) == ':' || name.substr(-1) == ' ') {
						name = name.substr(0, name.length - 1);
					}
				}
				var value = data[i].value;
				if (value == undefined || value == '' && data[i].name !="namelocationquarantine") {
						a.append($("<h2>").html(name));
						a.append($("<p>").html(MSF.getStringValue("noanswergiven").toUpperCase()).addClass('red-text'));	
				} else {
						a.append($("<h2>").html(name));
						a.append($("<p>").html(value));
				}
				if(data[i].name !="namelocationquarantine"){
					li.append(a);
					list.append(li);
				}
				
			}
		}
		
	});
	list.listview('refresh');
	
});

$("#submitpage").live('pagebeforeshow', function() {
	$("#datareviewlist li a").bind('click', function(e) {	
		fromSubmitPage = true;
	});
});


//methods for serializing and deserializing data from the app
function getData() {
	var serializedData = MSF.serializeData();
	
	window.formData = JSON.parse(serializedData);
	return window.formData;
}

(function($) {
    /*
     * Changes the displayed text for a jquery mobile button.
     * Encapsulates the idiosyncracies of how jquery re-arranges the DOM
     * to display a button for either an <a> link or <input type="button">
     */
    $.fn.changeButtonText = function(newText) {
        return this.each(function() {
            $this = $(this);
            if( $this.is('a') ) {
                $('span.ui-btn-text',$this).text(newText);
                return;
            }
            if( $this.is('input') ) {
                $this.val(newText);
                // go up the tree
                var ctx = $this.closest('.ui-btn');
                $('span.ui-btn-text',ctx).text(newText);
                return;
            }
        });
    };
})(jQuery);
