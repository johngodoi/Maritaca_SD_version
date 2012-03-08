var MIN_PASSWORD_SIZE = 6;
var MAX_PASSWORD_SIZE = 20;

function saveAccountButton(){
	return $('input[id$=":saveAccountButton"]')[0];
}

function disableSaveAccount() {
	saveAccountButton().disabled = true;
}

function enableSaveAccount() {
	saveAccountButton().disabled = false;
}

window.onload = function(){
	if(saveAccountButton() != null){
		disableSaveAccount();
	}
};

function returnMinPasswordSize() {
	return MIN_PASSWORD_SIZE;
}

function returnMaxPasswordSize() {
	return MAX_PASSWORD_SIZE;
}

function checkPassword() {
	var password        = $('input[id$=":pass"]')[0].value;
	var passwordConfirm = $('input[id$=":passConfirm"]')[0].value;

	if (!checkPasswordSize(password)) {
		return;
	}

	if (!checkPasswordsMatches(password, passwordConfirm)) {
		return;
	}
}

function checkPasswordSize(password) {
	var message = $('span[id$=":passSizeError"]')[0];

	if (password.length < MIN_PASSWORD_SIZE
			|| password.length > MAX_PASSWORD_SIZE) {
		message.style.display = 'block';
		disableSaveAccount();
		return false;
	} else {
		message.style.display = 'none';
		enableSaveAccount();
		return true;
	}
}

function checkPasswordsMatches(password, passwordConfirm) {
	var message = $('span[id$=":passMatchError"]')[0];
	
	if (password == passwordConfirm && password != "") {
		message.style.display = 'none';
		enableSaveAccount();
		return true;
	} else {
		message.style.display = 'block';
		disableSaveAccount();
		return false;
	}
}

function encryptPassword(form, confirm) {
	var password = form[form.id + ":pass"].value;
	form[form.id + ":password"].value = $.sha1(password);

	if (confirm) {
		var passwordConfirm = form[form.id + ":passConfirm"].value;
		form[form.id + ":passwordConfirm"].value = $.sha1(passwordConfirm);
	}
}
