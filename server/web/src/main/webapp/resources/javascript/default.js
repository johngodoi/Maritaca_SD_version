function showShareForm(thetitle){
	$('#dialogShareForm').dialog({
		autoOpen: false,
		modal: true,
		title: thetitle,
		width: 600,
		height: 300
	});
	$("#dialogShareForm").dialog('open');
}

function showImportForm(thetitle){
	$('#dialogImportForm').dialog({
		autoOpen: false,
		modal: true,
		title: thetitle,
		width: 600,
		height: 220
	});
	$("#dialogImportForm").dialog('open');
}

function closePopup(popupId) {
	$(popupId).dialog('close');
}
