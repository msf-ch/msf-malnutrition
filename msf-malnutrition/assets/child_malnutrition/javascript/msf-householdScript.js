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
var globalNumberPersonsCorrect;
var globalNumberChildCorrect;


//Startup stuff
$(document).ready(function(e) { 
	console.log('started document.ready');
	//If no MSF object then we're debugging...
	if(window.MSF == undefined) {
		console.log('window.MSF is NULL!!');
		window.MSF = new MSF_debug();
	} else {
		var msfString = JSON.stringify(MSF);
		console.log('window.MSF is: ' + msfString);
	}
	
	fromSubmitPage = new Boolean(false);
	
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
	var pageSix = new Boolean(false);
	var pageSeven = new Boolean(false);
	var pageEight = new Boolean(false);
	
	switch(pageform){
		case "basicform":
			pageOne = basicForm(e);
			if(!pageOne)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;
			
		case "householdorganisation":
			pageTwo = householdOrganisationForm(e);
			if(!pageTwo)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;
		case "householdlanguage":
			pageThree = householdLanguageForm(e);
			if(!pageThree){
				if(globalNumberPersonsCorrect==false)
					MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageNumberChildrenIncorrect"));
				else {
					if(globalNumberChildCorrect == false)
						MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageNumberChildrenSexIncorrect"));
					else
						MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
				}
			}
			break;
		case "householdpropertyland":
			pageFour = householdPropertyLandForm(e);
			if(!pageFour)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;
		case "householdcashexpenditures":
			pageFive = householdCashExpendituresForm(e);
			if(!pageFive)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;
		case "agriculturalproduction":
			pageSix = agriculturalProductionForm(e);
			if(!pageSix)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;
		case "householdagriincomecons":
			pageSeven = householdAgriIncomeconsForm(e);
			if(!pageSeven)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;
		case "householdissuesupplydebts":
			pageEight = householdIssueSupplyDebtsForm(e);
			if(!pageEight)
				MSF.showAlertMessage(MSF.getStringValue("titleError"),MSF.getStringValue("messageCorrectAnswers"));
			break;
	}
	
	if (next && pageOne && pageTwo && pageThree && pageFour && pageFive && pageSix && pageSeven && pageEight) {
		if(fromSubmitPage == false){
			$.mobile.changePage( "#" + next.attr('id'), { transition: "forward slide"} ); //transition: "forward slide" takes too long
			save(currentPage);
		}
		else {
			$.mobile.changePage( "#submitpage", { transition: "forward slide"} ); //transition: "forward slide" takes too long
			save(currentPage);
		}
	}
	
}

function basicForm(e){
	var suividatOk = new Boolean(false);
	var zoneOk = new Boolean(false);
	var villagenOk = new Boolean(false);
	var mennomOk = new Boolean(false);
	var eauorigiOk = new Boolean(false);
	var enqnomOk = new Boolean(false);
	var nozoneOk = new Boolean(false);
	var menagnOk = new Boolean(false);
	var novillageOk = new Boolean(false);
	var nenfmenOk = new Boolean(false);
	var puitnumOk = new Boolean(false);
	
	suividatOk = checkTextField("suividat", "a date");	
	zoneOk = checkTextField("zone", "the name of the responsability area");	
	villagenOk = checkTextField("villagen", "the name of the village");
	
	enqnomOk = checkIdField("enqnom", 2);
	novillageOk = checkIdField("novillage",2);
	nozoneOk = checkIdField("nozone", 2);
	menagnOk = checkIdField("menagn", 4);
	
	nenfmenOk = checkNumberField("nenfmen", "Enter a correct number of children");
	puitnumOk = checkNumberField("puitnum", "Enter a correct number of minutes");
	
	mennomOk = checkTextField("mennom", "the household chief's name");
	
	
	eauorigiOk = checkRadioButton("eauorigi");
	
	if(zoneOk && villagenOk && mennomOk && eauorigiOk && enqnomOk 
			&& nozoneOk && menagnOk && novillageOk && nenfmenOk && puitnumOk && suividatOk)
		return true;	
	else
		return false;
}

function householdOrganisationForm(e){	
	var cheffamOk = new Boolean(false);
	var csidistaOk = new Boolean(false);
	var educniveOk = new Boolean(false);
	var educenfOk = new Boolean(false);
	
	cheffamOk = checkRadioButton("cheffam");
	csidistaOk = checkRadioButton("csidista");
	educniveOk = checkRadioButton("educnive");
	educenfOk = checkRadioButton("educenf");
	
	if(cheffamOk && csidistaOk && educniveOk && educenfOk){
		return true;
	}
	else
		return false;
}

function householdLanguageForm(e){	
	var menalangOk = new Boolean(false);
	var menacaraOk = new Boolean(false);
	var lineOneOk = new Boolean(false);
	var lineTwoOk = new Boolean(false);
	//var lineThreeOk = new Boolean(false);
	var lineFourOk = new Boolean(false);
	var lineFiveOk = new Boolean(false);
	var lineSixOk = new Boolean (false);
	var lineSevenOk = new Boolean(false);
	
	var numberPersonCorrect =  new Boolean(false);
	var numberChildMaleCorrect = new Boolean(false);
	var numberChildFemaleCorrect = new Boolean(false);
	
	//LANGUAGE SPOKEN
	menalangOk = checkCheckboxField("menalang");
	
	//RESIDENTIAL STATUS
	menacaraOk = checkRadioButton("menacara");
	
	//HOUSEHOLD DEMOGRAPHICS
	lineOneOk = checkLineField("nbtotmale", "nbtotfemale");
	lineTwoOk = checkLineField("nbchildmaleunderfive", "nbchildfemaleunderfive");
	//lineThreeOk = checkLineField("nbchildmaleundertwo", "nbchildfemaleundertwo");
	lineFourOk =  checkLineField("nbmalecomehousehold", "nbfemalecomehousehold");
	lineFiveOk = checkLineField("nbmalelefthousehold", "nbfemalelefthousehold");
	lineSixOk = checkLineField("nbmalediedhousehold", "nbfemalediedhousehold");
	lineSevenOk = checkLineField("nbchildmalediedundertwentyfour", "nbchildfemalediedundertwentyfour");
	
	numberPersonCorrect = checkNumberPersonsCorrect();
	numberChildMaleCorrect = checkNumberChildCorrect("nbchildmaleunderfive");
	numberChildFemaleCorrect = checkNumberChildCorrect("nbchildfemaleunderfive");
	
	if(menalangOk && menacaraOk && lineOneOk && lineTwoOk /*&& lineThreeOk*/ && lineFourOk 
		&& lineFiveOk && lineSixOk && lineSevenOk){
		if(numberPersonCorrect){
		   globalNumberPersonsCorrect = true;
		   if(numberChildMaleCorrect == false || numberChildFemaleCorrect == false){
			   globalNumberChildCorrect = false;
			   return false;
		   }
		   else{
			   globalNumberChildCorrect = true;
			   return true;
		   }
		}
		else {
			globalNumberPersonsCorrect = false;
			return false;
		}
	}
	else
		return false;
}

function householdPropertyLandForm(e){
	var champOk = new Boolean(false);
	var surfcultOk = new Boolean(false);
	var cutlsmOk = new Boolean(false);
	var qicultOk = new Boolean(false);
	var emplchOk = new Boolean(false);
	var emptimOk = new Boolean(false);
	var outilsOk = new Boolean(false);
	var machineOk = new Boolean(false);
	var lineOneOk = new Boolean(false);
	var lineTwoOk = new Boolean(false);
	var lineThreeOk = new Boolean(false);
	var lineFourOk = new Boolean(false);
	var lineFiveOk = new Boolean(false);
	var lineSixOk = new Boolean (false);
	var lineSevenOk = new Boolean (false);
	var lineEightOk = new Boolean (false);
	var transpOk = new Boolean(false);
	var maisonOk = new Boolean(false);
	var stockOk = new Boolean(false);
	
	/**FIRST QUESTION **/
	champOk = checkRadioButton("champ");
	
	if($('input:radio[name=champ]:checked').val()=="yes"){		
		surfcultOk = checkNumberField("surfcult", "You have to specify a correct number");
	}
	else {
		$("#surfcult").val('');
		$("#select-choice-surfcult option:first").attr('selected','selected');
		$("#select-choice-surfcult").selectmenu('refresh');
		$("#lbl_surfcult").removeClass("red-text-bold");
		$("#lbl_under_surfcult").empty();
		surfcultOk = true;
	}
	
	/**SECOND QUESTION**/
	cutlsmOk = checkRadioButton("cutlsm");
	
	if($('input:radio[name=cutlsm]:checked').val()=="no"){		
		qicultOk = checkTextField("qicult", "who cultivates your land");
	}
	else {
		$("#qicult").val('');
		$("#lbl_qicult").removeClass("red-text-bold");
		$("#lbl_under_qicult").empty();
		qicultOk = true;
	}
	
	/**THIRD QUESTION**/
	emplchOk = checkRadioButton("emplch");
	
	if($('input:radio[name=emplch]:checked').val()=="yes"){		
		emptimOk = checkRadioButton("emptim");
	}
	else {
		var emptim_class = $(".emptim_class");
		$(emptim_class).prop('checked', false);
		$(emptim_class).checkboxradio("refresh")
		$("#lbl_emptim").removeClass("red-text-bold");
		$("#lbl_under_emptim").empty();
		emptimOk = true;
	}
	
	/** FOURTH QUESTION **/
	outilsOk = checkRadioButton("outils");
	
	/** FIFTH QUESTION **/
	machineOk = checkRadioButton("machine");
	
	/**SIXTH QUESTION **/
	lineOneOk = checkLineFieldUnique("nbcamel");
	lineTwoOk = checkLineFieldUnique("nbbull");
	lineThreeOk = checkLineFieldUnique("nbcow");
	lineFourOk = checkLineFieldUnique("nbsheep");
	lineFiveOk = checkLineFieldUnique("nbgoat");
	lineSixOk = checkLineFieldUnique("nbpoultry");
	lineSevenOk = checkLineFieldUnique("nbhorse");
	lineEightOk = checkLineFieldUnique("nbother");
	
	/**SEVENTH QUESTION **/	
	transpOk = checkCheckboxField("transp");
	/**EIGHT QUESTION **/
	maisonOk = checkRadioButton("maison");
	
	/**NINETH QUESTION **/
	stockOk = checkRadioButton("stock");
	
	if(champOk && surfcultOk && cutlsmOk && qicultOk && emplchOk && emptimOk && outilsOk && machineOk && 
		lineOneOk && lineTwoOk && lineThreeOk && lineFourOk && lineFiveOk && lineSixOk && lineSevenOk && lineEightOk &&
		transpOk && maisonOk && stockOk)
		return true;
	else
		return false;
}

function householdCashExpendituresForm(e){
	var revsoOk = new Boolean(false);
	var comercOk = new Boolean(false);
	var emploOk = new Boolean(false);
	var depensOk = new Boolean(false);
	
	/**FIRST QUESTION**/
	revsoOk = checkButtonsCount(numberRevso, "revso", 9);
	
	/**SECOND QUESTION **/
	comercOk = checkRadioButton("comerc");
	
	/**THIRD QUESTION **/
	emploOk = checkRadioButton("emplo");
	
	/**FOURTH QUESTION**/
	depensOk = checkButtonsCount(numberDepens, "depens", 12);
	
	
	if(comercOk && emploOk && revsoOk && depensOk)
		return true;
	else
		return false;			
}

function agriculturalProductionForm(e){
	var cultypOk = new Boolean(false);
	var recoltemOk = new Boolean(false);
	var rectypemOk = new Boolean(false);
	var cultjadOk = new Boolean(false);
	var recutiOk = new Boolean(false);
	
	/**FIRST QUESTION **/	
	cultypOk = checkCheckboxField("cultyp");
	
	/**SECOND QUESTION **/
	recoltemOk = checkRadioButton("recoltem");
	
	/**THIRD QUESTION **/
	if($('input:radio[name=recoltem]:checked').val()=="yes"){		
		rectypemOk = checkCheckboxInsideField("rectypem");
	}
	else {
		var rectypem_class = $(".rectypem_class");
		$(rectypem_class).prop('checked', false);
		$(rectypem_class).checkboxradio("refresh")
		$("#lbl_rectypem").removeClass("red-text-bold");
		$("#lbl_under_rectypem").empty();
		rectypemOk = true;
	}
	
	/**FOURTH QUESTION **/
	cultjadOk = checkRadioButton("cultjad");
	
	/**FIFTH QUESTION **/	
	recutiOk = checkCheckboxField("recuti");
	
	if(cultypOk && recoltemOk && rectypemOk && cultjadOk && recutiOk)
		return true;
	else
		return false;
}

function householdAgriIncomeconsForm(e){
	var consomaOk = new Boolean(false);
	var repnumOk = new Boolean(false);
	var ratgfdOk = new Boolean(false);
	var ratcompOk = new Boolean(false);
	var supnutOk = new Boolean(false);
	var supnutypOk = new Boolean(false);
	var ratventeOk = new Boolean(false);
	var ratupartOk = new Boolean(false);
	
	/**FIRST QUESTION **/	
	consomaOk = checkCheckboxField("consoma");
	
	/**SECOND QUESTION **/	
    repnumOk = checkNumberField("repnum", "Enter a correct number");
	
	/**THIRD QUESTION**/
	ratgfdOk = checkRadioButton("ratgfd");
	
	if($('input:radio[name=ratgfd]:checked').val()=="yes"){		
		ratcompOk = checkCheckboxInsideField("ratcomp");
	}
	else {
		var ratcomp_class = $(".ratcomp_class");
		$(ratcomp_class).prop('checked', false);
		$(ratcomp_class).checkboxradio("refresh")
		$("#lbl_ratcomp").removeClass("red-text-bold");
		$("#lbl_under_ratcomp").empty();
		ratcompOk = true;
	}
	
	/**FOURTH QUESTION**/
	supnutOk = checkRadioButton("supnut");
	
	if($('input:radio[name=supnut]:checked').val()=="yes"){		
		supnutypOk = checkCheckboxInsideField("supnutyp");
	}
	else {
		var supnutyp_class = $(".supnutyp_class");
		$(supnutyp_class).prop('checked', false);
		$(supnutyp_class).checkboxradio("refresh")
		$("#lbl_supnutyp").removeClass("red-text-bold");
		$("#lbl_under_supnutyp").empty();
		supnutypOk = true;
	}
	
	/**FIFTH QUESTION**/
	ratventeOk = checkRadioButton("ratvente");
	
	if($('input:radio[name=ratvente]:checked').val()=="yes"){		
		ratupartOk = checkCheckboxInsideField("ratupart");
	}
	else {
		var ratupart_class = $(".ratupart_class");
		$(ratupart_class).prop('checked', false);
		$(ratupart_class).checkboxradio("refresh")
		$("#lbl_ratupart").removeClass("red-text-bold");
		$("#lbl_under_ratupart").empty();
		ratupartOk = true;
	}
	
	
	if(consomaOk && repnumOk && ratgfdOk && ratcompOk && supnutOk && supnutypOk && ratventeOk && ratupartOk)
		return true;
	else
		return false;
}

function householdIssueSupplyDebtsForm(e){
	var mecadaptOk = new Boolean(false);
	var aprodiffOk = new Boolean(false);
	var revsoSupplyOk = new Boolean(false);
	var aprodiffMeansOk = new Boolean(false);
	var detteOk = new Boolean(false)
	
	/**FIRST QUESTION**/
	mecadaptOk = checkTextField("mecadapt", "this field");
	
	/**SECOND QUESTION**/
		aprodiffOk =  checkRadioButton("aprodiff");	
	
	if($('input:radio[name=aprodiff]:checked').val()=="yes"){	
		revsoSupplyOk = checkButtonsCount(numberRevsoSupply, "revsoSupply", 12);
	}
	else {
		$(".revsoSupplyButton").changeButtonText("No value");
		$(".revsoSupplyButton").button('enable');
		$("#lbl_revsoSupply").removeClass("red-text-bold");
	    $("#lbl_under_revsoSupply").empty();
		numberRevsoSupply = 1;
		revsoSupplyOk = true;
	}
	
	/**THIRD QUESTION**/
	aprodiffMeansOk = checkButtonsCount(numberAprodiff, "aprodiffMeans", 9);
	
	/**FOURTH QUESTION**/
	detteOk = checkRadioButton("dette");
	
	if(mecadaptOk && detteOk && aprodiffOk && revsoSupplyOk && aprodiffMeansOk)
		return true;
	else
		return false;
}



/**FUNCTIONS TO CHECK AND CLEAR FIELDS**/

//FUNCTION TO CHECK NUMBER OF PERSONS CORRECT
function checkNumberPersonsCorrect(){
	var numberMax = 0;
	var number = 0;
	
	var numberMax = parseInt($('#nbtotmale').val()) + 
	                parseInt($('#nbtotfemale').val());
	var number = parseInt($('#nbchildmaleunderfive').val()) + 
                 parseInt($('#nbchildfemaleunderfive').val());
	if(number <= numberMax)
		return true;
	else
		return false;
}

//FUNCTION TO CHECK NUMBER OF CHILDREN CORRECT
function checkNumberChildCorrect(id){
	var idNbTot;
	if(id=="nbchildmaleunderfive")
		idNbTot = "nbtotmale";
	else
		idNbTot = "nbtotfemale";
		
	var numberMax = 0;
	var number = 0;
	
	var numberMax = parseInt($('#'+idNbTot).val());
	
	var number = parseInt($('#'+id).val());
	
	if(number <= numberMax)
		return true;
	else
		return false;
}
//BASIC FUNCTIONS
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
	if(!theId.val() || theId.val() < 0 || isNaN(theId.val())){
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
	
	if(!theId.val() || theId.val() < 0 || isNaN(theId.val())){
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
	numberMax = 1;//[1]
	if(number <= numberMax){
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
	
	//[1] According to France, the user has to specify only the first value. Other aren't compulsory
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
	
	if (prev) 
			$.mobile.changePage( "#" + prev.attr('id'), { transition: "reverse slide"} ); //transition: "reverse slide" takes too long
	
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
			MSF.showAlertMessage(MSF.getStringValue("titleHelp"),message);
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
	var result = JSON.stringify(formData);
	if (result == undefined || result == null || result == '') {
		result = "{}";
	}
	console.log(result);
	MSF.storeData(result);
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
	

	//Loop for td
	$("td").each(function() {
		var id = $(this).attr('id');
		var text = $(this).text();
		text = $.trim(text);
		
		if(typeof(id)!="undefined"){
			if(id.substring(0,6) == "lbl_td"){
				text = text.toLowerCase();
				text = text.toLowerCase().replace(/ /g, '_');
				text = text.replace(/-/g, '_');
				text = text.replace(/,/g, '');
				text = text.replace(/\)/g, '');
				text = text.replace(/\(/g, '');
				$('#'+id).text(MSF.getStringValue(text));
			}
		}
	});
	
	//Loop for buttons
	//Loop for reset buttons
	$("input").each(function() {
		var type = $(this).attr("type");
		if(type=="button"){
			var id = $(this).attr('id');
			if(typeof(id)!="undefined"){
				$(this).attr('value',MSF.getStringValue("reset"));
			}
		}
	});

	//Loop for other buttons
	$(".revsoButton,.depensButton,.revsoSupplyButton,.aprodiffMeansButton").each(function() {
		$(this).attr('value',MSF.getStringValue("novalue"));
	});
	
	//Loop for radio buttons
	$("fieldset").each(function() {
		var id = $(this).attr('id');
		var text;
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
				text = text.replace(/%/g, '_percent');
				text = text.replace(/&/g, 'and');
				text = text.replace(/30/g, 'thirty');
				text = text.replace(/100/g, 'onehundred');
				text = text.replace(/0/g, 'zero');
				text = text.replace(/1/g, 'one');
				text = text.replace(/2/g, 'two');
				text = text.replace(/3/g, 'three');
				text = text.replace(/4/g, 'four');
				text = text.replace(/5/g, 'five');
				text = text.replace(/__/g, '_');
				
				var id = $(this).attr('id');
				if(text == "public" || text =="private")
					$('#'+id).text(MSF.getStringValue(text+"_school"));
				else {
					if(typeof(id)=="undefined")
						$(this).find(".ui-btn-text").text(MSF.getStringValue(text));
					else
						$('#'+id).text(MSF.getStringValue(text));
				}
			}
		});
	});
	
	//Loop for gridView
	$(".ui-block-a").each(function() {
		var text = $(this).text();
		text = $.trim(text);
		 if(text != "Previous" && text !=""){
			 text = text.toLowerCase().replace(/ /g, '_');
			 text = text.replace(/'/g, '');
			 text = text.replace(/-/g, '_');
			 text = text.replace(" - ", "_");
			 text = text.replace(/1/g, 'one');
			 text = text.replace(/2/g, 'two');
			 text = text.replace(/3/g, 'three');
			 text = text.replace(/4/g, 'four');
			 text = text.replace(/5/g, 'five');
			 $(this).find('.ui-bar').text(MSF.getStringValue(text));
		 }
	});
	
	$(".ui-block-b").each(function() {
		var text = $(this).text();
		text = $.trim(text);
		 if(text != "Help" && text !=""){
			    text = text.toLowerCase();
			    $(this).find('.ui-bar').text(MSF.getStringValue(text));
		 }
	});
	
	$(".ui-block-c").each(function() {
		var text = $(this).text();
		text = $.trim(text);
		 if(text != "Next" && text !=""){
				text = text.toLowerCase();
				$(this).find('.ui-bar').text(MSF.getStringValue(text));
		 }
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
	
	//Footer buttons
	$('.previousButton').find('.ui-btn-text').text(MSF.getStringValue("previousButton"));
	$('.helpButton').find('.ui-btn-text').text(MSF.getStringValue("helpButton"));
	$('.nextButton').find('.ui-btn-text').text(MSF.getStringValue("nextButton"));
	
	//SubmitPage
	$('#submitPagePreviousButton').text(MSF.getStringValue("previousButton"));
	$('#jumptostartButton').text(MSF.getStringValue("jumptostartButton"));
	$('#submithouseholdButton').text(MSF.getStringValue("submithouseholdButton"));	
	$('#lbl_submitpage_review_household').text(MSF.getStringValue("lbl_submitpage_review_household"));
}

/** PAGE 1 **/
$("#pageone").live('pageinit', function(e) {
	
	/** DATE **/
	clearDateErrorFields("suividat");
//	var today = new Date();
//	$('#suividat').data('datebox').options.defaultValue = [2011, 09, 08];//[today.getYear(), today.getMonth(), today.getDate()];
//	$('#suividat').datebox('refresh');
	
	/** ZONE **/
	clearTextErrorFields("zone");
	
	/** IDS **/
	clearIdError("enqnom", 2);	
	clearIdError("nozone", 2);
	clearIdError("novillage", 2);
	clearIdError("menagn",4);
	
	/**CHILDREN**/
	clearNumberError("nenfmen");
	
	/**MINUTES**/
	clearNumberError("puitnum");
	
	/**VILLAGE NAME **/
	clearTextErrorFields("villagen");
	
	/**HOUSEHOLD CHIEF'S NAME **/
	clearTextErrorFields("mennom");
	
	/**WATER ACCESS **/
	clearRadioButtonErrorFields("eauorigi");
	
	applyFocus("enqnom","nozone",2);
	applyFocus("nozone","menagn",2);
	applyFocus("menagn","villagen",4);
	applyFocus("novillage","nenfmen",2);
	applyFocus("nenfmen","mennom",2);
});

/** PAGE 2 **/
$("#pagetwo").live('pageinit', function(e) {	
	clearRadioButtonErrorFields("cheffam");
	clearRadioButtonErrorFields("csidista");
	clearRadioButtonErrorFields("educnive");
	clearRadioButtonErrorFields("educenf");
});

/** PAGE 3 **/
$("#pagethree").live('pageinit', function(e) {
	
	//LANGUAGE
	clearCheckboxField("menalang");
	
	//RESIDENTIAL STATUS
	clearRadioButtonErrorFields("menacara");
	
	//DEMOGRAPHICS
	clearLineField("nbtotmale");
	clearLineField("nbtotfemale");
	clearLineField("nbchildmaleunderfive");
	clearLineField("nbchildfemaleunderfive");
	clearLineField("nbchildmaleundertwo");
	clearLineField("nbchildfemaleundertwo");
	clearLineField("nbmalecomehousehold");
	clearLineField("nbfemalecomehousehold");
	clearLineField("nbmalelefthousehold");
	clearLineField("nbfemalelefthousehold");
	clearLineField("nbmalediedhousehold");
	clearLineField("nbfemalediedhousehold");
	clearLineField("nbchildmalediedundertwentyfour");
	clearLineField("nbchildfemalediedundertwentyfour");
});

/** PAGE 4 **/
$("#pagefour").live('pageinit', function(e) {
	
	$("#surfcultfield").hide();
	$("#qicultfield").hide();
	$("#emptimfields").hide();
	
	
	$(":input[@name='champ']").live('change',function(event) {  
        var val = $('input[name=champ]:checked').val();
        if(val == 'yes')
    		$("#surfcultfield").show("normal");
        else
    		$("#surfcultfield").hide("normal");
	});
	
	$(":input[@name='cutlsm']").live('change',function(event) {  
        var val = $('input[name=cutlsm]:checked').val();
        if(val == 'no')
    		$("#qicultfield").show("normal");
        else
    		$("#qicultfield").hide("normal");
	});
	
	$(":input[@name='emplch']").live('change',function(event) {  
        var val = $('input[name=emplch]:checked').val();
        if(val == 'yes')
    		$("#emptimfields").show("normal");
        else
    		$("#emptimfields").hide("normal");
	});
	
	//DATA CONTROLS
	
	/**FIRST QUESTION**/
	clearRadioButtonErrorFields("champ");
	
	$("#surfcult").live('keyup',function(event) {
		var val = $.trim($("#surfcult").val());
		if((val > 0 || !(isNan(val)))){
			$("#lbl_surfcult").removeClass("red-text-bold");
			$("#lbl_under_surfcult").empty();
		}
	});
	
	$("#surfcult").live('focusout',function(event) {
		var val = $.trim($("#surfcult").val());
		$("#surfcult").val(val);
	});
	
	/**SECOND QUESTION**/
	clearRadioButtonErrorFields("cutlsm");
	
	$("#qicult").live('keyup',function(event) {
		var val = $.trim($("#qicult").val());
		if(val!=""){
			$("#lbl_qicult").removeClass("red-text-bold");
			$("#lbl_under_qicult").empty();
		}
	});
	
	$("#qicult").live('focusout',function(event) {
		var val = $.trim($("#qicult").val());
		$("#qicult").val(val);
	});
	
	/**THIRD QUESTION**/
	clearRadioButtonErrorFields("emplch");
	clearRadioButtonErrorFields("emptim");
	
	/**FOURTH QUESTION**/
	clearRadioButtonErrorFields("outils");
	
	/**FIFTH QUESTION**/
	clearRadioButtonErrorFields("machine");
	
	/**SIXTH QUESTION **/
	clearLineField("nbcamel");
	clearLineField("nbbull");
	clearLineField("nbcow");
	clearLineField("nbsheep");
	clearLineField("nbgoat");
	clearLineField("nbpoultry");
	clearLineField("nbhorse");
	clearLineField("nbother");
	
	/**SEVENTH QUESTION**/
	clearCheckboxField("transp");
	
	$("#nonetransp").change(function(e) {
		  var notdoccheck = $("#nonetransp"); 
		  var checked = notdoccheck.is(":checked"); 
		  var otherCheckBoxes = notdoccheck.parents("fieldset").find("input").not("#nonetransp");
		  if(checked) {
			  otherCheckBoxes.prop("checked", false).change().checkboxradio('disable').checkboxradio("refresh");
	      } else {
				  otherCheckBoxes.checkboxradio('enable').checkboxradio("refresh");
		  }
	});
	
	$("#dontknow").change(function(e) {
		  var notdoccheck = $("#dontknow"); 
		  var checked = notdoccheck.is(":checked"); 
		  var otherCheckBoxes = notdoccheck.parents("fieldset").find("input").not("#dontknow");
		  if(checked) {
			  otherCheckBoxes.prop("checked", false).change().checkboxradio('disable').checkboxradio("refresh");
	      } else {
				  otherCheckBoxes.checkboxradio('enable').checkboxradio("refresh");
		  }
	});
	
	/**EIGHT QUESTION**/
	clearRadioButtonErrorFields("maison");
	
	/**NINETH QUESTION**/
	clearRadioButtonErrorFields("stock");
});

/** PAGE 5 **/
$("#pagefive").live('pageinit', function(e) {
	numberRevso = 1;
	numberDepens = 1;
	
	$(".revsoButton").click(function (){
			if(numberRevso <= 8){
				$(this).changeButtonText(numberRevso);
				$(this).button('disable');
				numberRevso++;
				//Only first value (2 instead of 9)
				if(numberRevso == 2){
					$("#lbl_revso").removeClass("red-text-bold");
				    $("#lbl_under_revso").empty();						
				}
			}
	});
	
	$("#revsoButtonReset").click(function (){
			$(".revsoButton").changeButtonText(MSF.getStringValue("novalue"));
			$(".revsoButton").button('enable');
			$("#lbl_revso").removeClass("red-text-bold");
		    $("#lbl_under_revso").empty();
				numberRevso = 1;
	});
	
	$(".depensButton").click(function (){
		if(numberDepens <= 12){
			$(this).changeButtonText(numberDepens);
			$(this).button('disable');
			numberDepens++;
			//Only first value (2 instead of 13)
			if(numberDepens == 2){
				$("#lbl_depens").removeClass("red-text-bold");
			    $("#lbl_under_depens").empty();						
			}
		}
	});

	$("#depensButtonReset").click(function (){
		$(".depensButton").changeButtonText(MSF.getStringValue("novalue"));
		$(".depensButton").button('enable');
		$("#lbl_depens").removeClass("red-text-bold");
	    $("#lbl_under_depens").empty();
			numberDepens = 1;
	});
	
	//DATA CONTROLS
	/**SECOND QUESTION**/
	clearRadioButtonErrorFields("comerc");
	/**THIRD QUESTION**/
    clearRadioButtonErrorFields("emplo");
});

/** PAGE 6 **/
$("#pagesix").live('pageinit', function(e) {
	
	$("#rectypemfields").hide();
	
	
	$(":input[@name='recoltem']").live('change',function(event) {  
        var val = $('input[name=recoltem]:checked').val();
        if(val == 'yes')
    		$("#rectypemfields").show("normal");
        else
    		$("#rectypemfields").hide("normal");
	});
	
	//DATA CONTROLS
	/**FIRST QUESTION**/
	clearCheckboxField("cultyp");
	
	/**SECOND QUESTION**/
	clearRadioButtonErrorFields("recoltem");
	
	/**THIRD QUESTION **/
	clearCheckBoxInsideField("rectypem");
	
	/**FOURTH QUESTION**/
	clearRadioButtonErrorFields("cultjad");
	
	/**FIFTH QUESTION **/
	clearRadioButtonErrorFields("recuti");
});

/** PAGE 7 **/
$("#pageseven").live('pageinit', function(e) {
	
	$("#ratcompfields").hide();
	$("#supnutypfields").hide();
	$("#ratupartfields").hide();
	
	$(":input[@name='ratgfd']").live('change',function(event) {  
        var val = $('input[name=ratgfd]:checked').val();
        if(val == 'yes')
    		$("#ratcompfields").show("normal");
        else
    		$("#ratcompfields").hide("normal");
	});
	
	$(":input[@name='supnut']").live('change',function(event) {  
        var val = $('input[name=supnut]:checked').val();
        if(val == 'yes')
    		$("#supnutypfields").show("normal");
        else
    		$("#supnutypfields").hide("normal");
	});
	
	$(":input[@name='ratvente']").live('change',function(event) {  
        var val = $('input[name=ratvente]:checked').val();
        if(val == 'yes')
    		$("#ratupartfields").show("normal");
        else
    		$("#ratupartfields").hide("normal");
	});
	
	//DATA CONTROLS
	/** FIRST QUESTION **/
	clearCheckboxField("consoma");
	
	$("#dontknowconsoma").change(function(e) {
		  var notdoccheck = $("#dontknowconsoma"); 
		  var checked = notdoccheck.is(":checked"); 
		  var otherCheckBoxes = notdoccheck.parents("fieldset").find("input").not("#dontknowconsoma");
		  if(checked) {
			  otherCheckBoxes.prop("checked", false).change().checkboxradio('disable').checkboxradio("refresh");
	      } else {
				  otherCheckBoxes.checkboxradio('enable').checkboxradio("refresh");
		  }
	});
	
	$("#dontknowratcomp").change(function(e) {
		  var notdoccheck = $("#dontknowratcomp"); 
		  var checked = notdoccheck.is(":checked"); 
		  var otherCheckBoxes = notdoccheck.parents("fieldset").find("input").not("#dontknowratcomp");
		  if(checked) {
			  otherCheckBoxes.prop("checked", false).change().checkboxradio('disable').checkboxradio("refresh");
	      } else {
				  otherCheckBoxes.checkboxradio('enable').checkboxradio("refresh");
		  }
	});
	/** SECOND QUESTION **/
	clearNumberError("repnum");
	
	/**THIRD QUESTION**/
	clearRadioButtonErrorFields("ratgfd");
	
	clearCheckBoxInsideField("ratcomp");
	
	$("#dontknowratcomp").change(function(e) {
		  var notdoccheck = $("#dontknowratcomp"); 
		  var checked = notdoccheck.is(":checked"); 
		  var otherCheckBoxes = notdoccheck.parents("fieldset").find("input").not("#dontknowratcomp");
		  if(checked) {
			  otherCheckBoxes.prop("checked", false).change().checkboxradio('disable').checkboxradio("refresh");
	      } else {
				  otherCheckBoxes.checkboxradio('enable').checkboxradio("refresh");
		  }
	});
	
	/**FOURTH QUESTION**/
	clearRadioButtonErrorFields("supnut");	
	clearCheckBoxInsideField("supnutyp");
	
	/**FIFTH QUESTION**/
	clearRadioButtonErrorFields("ratvente");	
	clearCheckBoxInsideField("ratupart");
	
	
});

/** PAGE 8 **/
$("#pageeight").live('pageinit', function(e) {	
	numberRevsoSupply = 1;
	numberAprodiff = 1;
	
	$("#revsoSupplyfields").hide();

	$(":input[@name='aprodiff']").live('change',function(event) {  
        var val = $('input[name=aprodiff]:checked').val();
        if(val == 'yes')
    		$("#revsoSupplyfields").show("normal");
        else
    		$("#revsoSupplyfields").hide("normal");
	});
	
	$(".revsoSupplyButton").click(function (){
			if(numberRevsoSupply <= 12){
				$(this).changeButtonText(numberRevsoSupply);
				$(this).button('disable');
				numberRevsoSupply++;
				//Only first value (2 instead of 13)
				if(numberRevsoSupply == 2){
					$("#lbl_revsoSupply").removeClass("red-text-bold");
				    $("#lbl_under_revsoSupply").empty();						
				}
			}
	});
	
	$("#revsoSupplyButtonReset").click(function (){
			$(".revsoSupplyButton").changeButtonText(MSF.getStringValue("novalue"));
			$(".revsoSupplyButton").button('enable');
			$("#lbl_revsoSupply").removeClass("red-text-bold");
		    $("#lbl_under_revsoSupply").empty();
				numberRevsoSupply = 1;
	});
	

	$(".aprodiffMeansButton").click(function (){
		if(numberAprodiff <= 9){
			$(this).changeButtonText(numberAprodiff);
			$(this).button('disable');
			numberAprodiff++;
			//Only first value (2 instead of 10)
			if(numberAprodiff == 2){
				$("#lbl_aprodiffMeans").removeClass("red-text-bold");
			    $("#lbl_under_aprodiffMeans").empty();						
			}
		}
	});

	$("#aprodiffMeansButtonReset").click(function (){
		$(".aprodiffMeansButton").changeButtonText(MSF.getStringValue("novalue"));
		$(".aprodiffMeansButton").button('enable');
		$("#lbl_aprodiffMeans").removeClass("red-text-bold");
	    $("#lbl_under_aprodiffMeans").empty();
		numberAprodiff = 1;
	});
	
	//DATA CONTROLS
	/**FIRST QUESTION**/
	clearTextErrorFields("mecadapt");
	
	/**SECOND QUESTION**/
	clearRadioButtonErrorFields("aprodiff");
	
	/**FOURTH QUESTION**/
	clearRadioButtonErrorFields("dette");
});

/** SUBMIT PAGE **/
$("#submitpage").live('pagebeforeshow', function() {
	fromSubmitPage = false;
	var list = $("#datareviewlist");
	list.children().remove("li");
	$("form").each(function() {
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
