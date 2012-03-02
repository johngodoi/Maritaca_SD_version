// Functions to create the Field properties

var inputCreator = function(type, id, value, name, size, styleClass) {
	var inputString = '<input ';
	inputString += attribCreator( 'type' , type);
	inputString += attribCreator('id', id);
	inputString += attribCreator('value', value);
	inputString += attribCreator('onchange', 'fieldSave()');
	switch (type) {
		case 'radio':
			inputString += attribCreator('name', name);
			if(value == true)
				inputString += attribCreator('checked', value);
			break;
		case 'number':
			inputString += attribCreator('size', size);
			inputString += attribCreator('class', styleClass);
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

var  tagCreator = function (tag, value){
	return '<' + tag + '>' + value + '</' + tag + '>';
};

//general method to create text component
var createTextProperty = function(id, value, title){
	var label = '<tr><td>' + tagCreator('label', title) + '</td>';
	label += '<td>' + inputCreator('text', id, value, null, null, null) + '</td></tr>';
	return label;
};

//general method to create number component
var createNumberProperty = function(id, value, title) {
	var label = '<tr><td>' + tagCreator('label', title) + '</td>';
	label += '<td>' + inputCreator('number', id, value, null, 4, 'numeric') + '</td></tr>';
	return label;
};

//general method to create number component
var createDateProperty = function(id, value, title) {
	var label = '<tr><td>' + tagCreator('label', title) + '</td>';
	label += '<td>' + inputCreator('date', id, value, null, null, null) + '</td></tr>';
	return label;
};

var createRequiredProperty = function(value){
	var required = '<tr><td>' + tagCreator('label', jQuery.i18n.prop('msg_field_requiredProperty')) + '</td>';
	
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





