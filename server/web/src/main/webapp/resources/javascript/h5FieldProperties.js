// Functions to create the Field properties

var inputCreator = function(type, id, value, name, size, styleClass) {
	var inputString = '<input ';
	inputString += attribCreator( 'type' , type);
	if(id!=null){
		inputString += attribCreator('id', id);
	}
	if(value!=null){
		inputString += attribCreator('value', value);
	}
	inputString += attribCreator('onchange', 'saveField()');

	if(styleClass!=null){
		inputString += attribCreator('class', styleClass);
	}
	switch (type) {
		case 'radio':
			inputString += attribCreator('name', name);
			if(value == true)
				inputString += attribCreator('checked', value);
			break;
		case 'number':
			inputString += attribCreator('size', size);
			break;
		default:
			break;
	}
	inputString += '/>';
	return inputString;
};

var  attribCreator = function (field, value){
	return field + '="' + value + '" ';
};

var  tagCreatorHTML = function (tag, value, styleClass){
	styleTag = "";
	if(styleClass!=null){
		styleTag = " class=\""+styleClass+"\" ";
	}
	
	return '<' + tag + styleTag + '>' + value + '</' + tag + '>';
};

var  tagCreatorXML = function (tag, value){
	return '<' + tag + '>' + replaceXMLEntityByCode(value) + '</' + tag + '>';
};

//general method to create a label
var createLabelProperty = function(id, title){
	var label = '<tr><td colspan="2">' + tagCreatorHTML('label', title) + '</td></tr>';
	return label;
};


//general method to create text component
var createTextProperty = function(id, value, title){
	var label = '<tr><td>' + tagCreatorHTML('label', title) + '</td>';
	label += '<td>' + inputCreator('text', id, value, null, null, null) + '</td></tr>';
	return label;
};

//general method to create number component
var createNumberProperty = function(id, value, title) {
	var label = '<tr><td>' + tagCreatorHTML('label', title) + '</td>';
	label += '<td>' + inputCreator('number', id, value, null, 4, 'numeric') + '</td></tr>';
	return label;
};

//general method to create date component
var createDateProperty = function(id, value, title) {
	var label = '<tr><td>' + tagCreatorHTML('label', title) + '</td>';
	label += '<td>' + inputCreator('date', id, value, null, null, 'datepicker') + '</td></tr>';
	return label;
};

var createRequiredProperty = function(value){
	var required = '<tr><td>' + tagCreatorHTML('label', jQuery.i18n.prop('msg_field_requiredProperty')) + '</td>';
	
	required += '<td>';
	required += inputCreator('radio', 'fieldRequiredTrue', (value == true) ? true : false, 'fieldRequired',   
			null, null);
	required += jQuery.i18n.prop('msg_true');
	
	required += inputCreator('radio', 'fieldRequiredFalse', (value == false) ? true : false, 'fieldRequired',
			null, null);
	required += jQuery.i18n.prop('msg_false');
	required +=  '</td></tr>';
	
	return required;
};


function replaceXMLEntityByCode(string) {
	string = string.replace(/&/g ,'&amp;');
	string = string.replace(/'/g,'&apos;');
	string = string.replace(/"/g ,'&quot;');	
	string = string.replace(/</g ,'&lt;');
	string = string.replace(/>/g ,'&gt;');
	return string; 
}

