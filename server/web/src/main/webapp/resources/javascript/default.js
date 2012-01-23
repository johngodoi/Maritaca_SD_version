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