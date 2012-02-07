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
		xml += '\n<label>' + this.title + '</label>';
		xml += '\n<help>' + this.help + '</help>';
		xml += this.addXMLElements();
		xml += '\n</' + this.type + '>';
		return xml;
	};
	
	this.showProperties = function(){
		$('#fieldLabel').val(this.title);
		$('#fieldHelp').val(this.help);
		$('#fieldRequiredTrue:checked').val(this.required);
		$('#fieldRequiredFalse:checked').val(!this.required);
		this.showSpecificProperties();
	};
	
	this.saveProperties = function(){
		this.title = $('#fieldLabel').val();
		this.help = $('#fieldHelp').val();
		this.required = $('#fieldRequiredTrue:checked').val() == 'on' ? true : false;
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
		
		html += '<input type="text" ';
		html += 'id="field_' + this.id + '" ';
		html += 'maxlength="' + this.size + '" ';
		if(this.bydefault)
			html += 'value="' + this.bydefault + '" ';
		html += '//>';
		
		return html;
	};
	
	
	this.addXMLSpecificAttributes = function() {
		var xml = 'max="' + this.maxValue + '"';
		return xml;
	};
	
	this.addXMLElements = function(){
		var xml = '\n<size>' + this.size + '</size>';
		xml += '\n<defaultvalue>' + this.bydefault + '</defaultvalue>';
		return xml;

	};
	
	this.showSpecificProperties = function(){
		$('#fieldDefault').show().val(this.bydefault);
		$('#fieldMaxValue').show().val(this.maxValue);
		$('#fieldSize').show().val(this.size);
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

var fieldFactory = function(type){
	var field;
	switch (type) {
	case 'text':
		field = new TextBox();
		break;

	default:
		return null;
	}
	
	field.type = type;
	return field
}