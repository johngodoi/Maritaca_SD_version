function checkPassword(form) {
	var password        = form[form.id + ":password"].value;
	var passwordConfirm = form[form.id + ":passwordConfirm"].value;
	var message         = document.getElementById(form.id+":confirmPasswdError");
	var createAccount   = form[form.id + ":createAccountButton"];

	if (password == passwordConfirm && password != ""){
		message.style.visibility  = 'hidden';
	}else{
		message.style.visibility  = 'visible';
	}
}
