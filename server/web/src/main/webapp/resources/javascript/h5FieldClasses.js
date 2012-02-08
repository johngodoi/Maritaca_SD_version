// Generic Field class
//
// This in an abstract class. The following methods must 
// be implemented in the subclasses: 
//		addXMLSpecificAttributes: return a string with the tag's attributes 
//		addXMLElements:	return a string with tag's XML elements
//		toHTMLSpecific: return a string with HTML corresponding component
//		showSpecificProperties: show the properties of a specific field 
//		saveSpecificProperties: updates the properties of a field
//	See TextBox class as example
//
var Field = function() {
	this.type = '';
	this.id = '';
	this.required = false;

	this.title = '';
	this.help = '';
	
	this.toHTML = function() {
		var html = '';
		html += '<label class="fieldLabel" for="label_' + this.id + '">';
		html += this.title;
		if(this.required)
			html += ' <span class="required">*</span>';
		html += '</label>';
		html += '<div class="order">' + 
						'<a href="#" onclick="move(-1); return false;">' + 
							'<img src="../../resources/images/arrow_up.png" alt="up" /></a>' + 
						'<a href="#" onclick="move(1); return false;">' + 
							'<img src="../../resources/images/arrow_down.png" alt="down" /></a></div>';
		html += this.toHTMLSpecific();
		
		return html;
	};
	
	this.toXML = function() {
		var xml = '';
		xml += '<' + this.type + ' id="' + this.id + '" required="' + this.required  + '" ';
		xml += this.addXMLSpecificAttributes();
		xml += '>';
		xml += tagCreator('label', this.title);
		xml += tagCreator('help', this.help);
		xml += this.addXMLElements();
		xml += '\n</' + this.type + '>';
		return xml;
	};
	
	this.showProperties = function(){
		$('#fieldLabel').val(this.title);
		$('#fieldHelp').val(this.help);
		$('#fieldRequiredTrue').prop('checked', this.required);
		$('#fieldRequiredFalse').prop('checked', !this.required);
		this.showSpecificProperties();
	};
	
	this.saveProperties = function(){
		this.title = $('#fieldLabel').val();
		this.help = $('#fieldHelp').val();
		this.required = $('#fieldRequiredTrue').is(':checked');
		this.saveSpecificProperties();
	};
};

// Class TextBox: Inherits from Field
var TextBox = function() {
	// TODO evaluate the size default
	this.size = '100';
	this.maxValue = '';
	this.bydefault = '';
	
	this.toHTMLSpecific = function() {
		var html = '';
		html +='<input ';
		html += attribCreator('type', this.type);
		html += attribCreator('id', 'field_' + this.id);
		html += attribCreator('maxlength', this.size);
		if(this.bydefault)
			html += attribCreator('value', this.bydefault);
		html += '/>';
		
		return html;
	};
	
	
	this.addXMLSpecificAttributes = function() {
		var xml = attribCreator('max', this.maxValue);
		return xml;
	};
	
	this.addXMLElements = function(){
		var xml = tagCreator('size', this.size);
		xml += tagCreator('defaultvalue', this.bydefault);
		return xml;

	};
	
	this.showSpecificProperties = function(){
		$('#fieldDefault').val(this.bydefault).parent().show();
		$('#fieldMaxValue').val(this.maxValue).parent().show();
		$('#fieldSize').val(this.size).parent().show();
		$('#propertiesSpecific').show();
	};
	
	this.saveSpecificProperties = function(){
		this.bydefault = $('#fieldDefault').val();
		this.maxValue = $('#fieldMaxValue').val();
		this.size = $('#fieldSize').val();
	};
};

// Inheritance to TextBox from Field
TextBox.prototype = new Field();

var CheckBox = function() {
	this.options = new Array();
};


//// Number Field ////
var NumberField = function(){
	this.bydefault = '';
	this.maxValue = '';
	this.minValue = '';
	
	this.toHTMLSpecific = function() {
		var html = '';
		
		html += '<input ';
		html += attribCreator( 'type' , this.type);
		html += attribCreator('id', 'field_' + this.id);
		html += this.specificAttributes();
		html += '/>';
		
		return html;
	};
	
	
	this.addXMLSpecificAttributes = function() {
		return this.specificAttributes();
	};
	
	this.addXMLElements = function(){
		//TODO: return IF comparisons
		return "";

	};
	
	this.showSpecificProperties = function(){
		$('#fieldNumericDefault').val(this.bydefault).parent().show();
		$('#fieldMaxValue').val(this.maxValue).parent().show();
		$('#fieldMinValue').val(this.minValue).parent().show();
		$('#propertiesSpecific').show();
	};
	
	this.saveSpecificProperties = function(){
		this.bydefault = $('#fieldNumericDefault').val();
		this.minValue = $('#fieldMinValue').val();
		this.maxValue = $('#fieldMaxValue').val();
	};
	
	this.specificAttributes = function(){
		var att = attribCreator('min', this.minValue);
		att += attribCreator('max', this.maxValue);
		if(this.bydefault)
			att += attribCreator('value', this.bydefault);
		return att;
	};
};
NumberField.prototype = new Field();

var DateField = function() {
	this.bydefault = '';
	this.maxValue = '';
	this.minValue = '';
	
	this.toHTMLSpecific = function() {
		var html = '';
		
		html += '<input ';
		html += attribCreator( 'type' , this.type);
		html += attribCreator('id', 'field_' + this.id);
		html += this.specificAttributes();
		html += '/>';
		
		return html;
	};
	
	this.addXMLSpecificAttributes = function() {
		return this.specificAttributes();
	};
	
	this.addXMLElements = function(){
		//TODO: return IF comparisons
		return "";

	};
	
	this.showSpecificProperties = function(){
		$('#fieldDateDefault').val(this.bydefault).parent().show();
		$('#fieldMaxValueDate').val(this.maxValue).parent().show();
		$('#fieldMinValueDate').val(this.minValue).parent().show();
		$('#propertiesSpecific').show();
	};

	this.saveSpecificProperties = function(){
		this.bydefault = $('#fieldDateDefault').val();
		this.maxValue = $('#fieldMaxValueDate').val();
		this.minValue = $('#fieldMinValueDate').val();
	};

	this.specificAttributes = function(){
		var att = attribCreator('min', this.minValue);
		att += attribCreator('max', this.maxValue);
		if(this.bydefault)
			att += attribCreator('value', this.bydefault);
		return att;
	};
};
DateField.prototype = new Field();

var fieldFactory = function(type){
	var field;
	switch (type) {
	case 'text':
		field = new TextBox();
		field.title = jQuery.i18n.prop('msg_form_textBoxTitle');
		break;
	case 'number':
		field = new NumberField();
		field.title = jQuery.i18n.prop('msg_form_numberTitle');
		break;
	case 'date':
		field = new DateField();
		field.title = jQuery.i18n.prop('msg_form_dateTitle');
		break;
	default:
		return null;
	}
	
	field.type = type;
	return field;
};

var  attribCreator = function (field, value){
	return field + '="' + value + '" ';
};

var  tagCreator = function (tag, value){
	return '\n<' + tag + '>' + value + '</' + tag + '>';
};