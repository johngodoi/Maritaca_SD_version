function showShareForm(thetitle){
	$('#dialogShareForm').dialog({
		autoOpen: false,
		modal: true,
		title: thetitle,
		width: 600,
	});
	$("#dialogShareForm").dialog('open');
}

function showImportForm(thetitle){
	$('#dialogImportForm').dialog({
		autoOpen: false,
		modal: true,
		title: thetitle,
		width: 600,
	});
	$("#dialogImportForm").dialog('open');
}

function closePopup(popupId) {
	$(popupId).dialog('close');
}

var MaritacaMessage = function(){
	this.message='';
	this.id=new Date().getTime();
	this.type='default';
	this.render = function(){
		var typeclass = '';
		switch(this.type){
			case 'error':
				typeclass = 'errMsg';
				break;
			case 'warn':
				typeclass = 'warnMsg';
				break;
			case 'info':
				typeclass = 'infoMsg';
			break;
		}
		var html = '<div id=' + this.id + ' class="maritacaMsg ' + typeclass + '">';
		html += '<p>' + this.message + '</p></div>';
		$('.maritacaMessages').append(html);
		$('#' + this.id).fadeIn(400).delay(4000).fadeOut(400);
		window.setTimeout("removeMessage('" + this.id + "')", 4800);
		
	};
};

function removeMessage(id){
	$('#' + id).remove();
}

//add a message to be shown for 4 seconds
var addMessage = function(msg, type){
	var marMsg = new MaritacaMessage();
	marMsg.message = msg;
	marMsg.type = type;
	marMsg.render();
};
