var MIN_PASSWORD_SIZE = 8;
var MAX_PASSWORD_SIZE = 20;

window.onload = function(){
	var saveAccountId     = window.document.forms[0].id+":createAccountButton";
	var saveAccountButton = document.getElementById(saveAccountId);	
	saveAccountButton.disabled=true;
};

function returnMinPasswordSize() {
	return MIN_PASSWORD_SIZE;
}

function returnMaxPasswordSize() {
	return MAX_PASSWORD_SIZE;
}

function checkPassword(form) {
	var password = form[form.id + ":password"].value;
	var passwordConfirm = form[form.id + ":passwordConfirm"].value;

	if (!checkPasswordSize(form, password)) {
		return;
	}

	if (!checkPasswordsMatches(form, password, passwordConfirm)) {
		return;
	}
}

function checkPasswordSize(form, password) {
	var message = document.getElementById(form.id + ":passwdLengthError");

	if (password.length < MIN_PASSWORD_SIZE
			|| password.length > MAX_PASSWORD_SIZE) {
		message.style.visibility = 'visible';
		disableSaveAccount(form);
		return false;
	} else {
		message.style.visibility = 'hidden';
		enableSaveAccount(form);
		return true;
	}
}

function checkPasswordsMatches(form, password, passwordConfirm) {
	var message = document.getElementById(form.id + ":confirmPasswdError");

	if (password == passwordConfirm && password != "") {
		message.style.visibility = 'hidden';
		enableSaveAccount(form);
		return true;
	} else {
		message.style.visibility = 'visible';
		disableSaveAccount(form);
		return false;
	}
}

function disableSaveAccount(form) {
	form[form.id + ":createAccountButton"].disabled = true;
}

function enableSaveAccount(form) {
	form[form.id + ":createAccountButton"].disabled = false;
}

function encryptPassword(form, confirm) {
	var password = form[form.id + ":password"].value;
	form[form.id + ":password"].value = $.sha1(password);

	if (confirm) {
		var passwordConfirm = form[form.id + ":passwordConfirm"].value;
		form[form.id + ":passwordConfirm"].value = $.sha1(passwordConfirm);
	}
}
