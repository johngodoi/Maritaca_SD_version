// Generic Field class
//
// This in an abstract class. The following methods must 
// be implemented in the subclasses: 
//		addXMLSpecificAttributes: return a string with the tag's attributes 
//		addXMLElements:	return a string with tag's XML elements
//		toHTMLSpecific: return a string with HTML corresponding component
//		showSpecificProperties: show the properties of a specific field 
//		saveSpecificProperties: updates the properties of a field
//      setFromXMLDoc: loads the data from xml
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
		xml += '</' + this.type + '>';
		return xml;
	};
	
	this.setJSONValues = function(element){
		this.type = element.type;
		this.id = element.id;
		this.required = element.required;
		this.title = element.title;
		this.help = element.help;
		this.setJSONValuesSpecific(element);
	};
	
	this.showProperties = function(){
		$('#fieldLabel').val(this.title);
		$('#fieldHelp').val(this.help);
		if(this.required)
			$('#fieldRequiredTrue').attr('checked', true);
		else
			$('#fieldRequiredFalse').attr('checked', true);
		this.showSpecificProperties();
	};
	
	this.saveProperties = function(){
		this.title = $('#fieldLabel').val();
		this.help = $('#fieldHelp').val();
		this.required = $('#fieldRequiredTrue').is(':checked');
		this.saveSpecificProperties();
	};
	
	this.setFromXMLDoc = function(xmlDoc){
		var $xmlDoc = $( xmlDoc );
		if(xmlDoc.nodeName != this.type){
			console.error(xmlDoc.nodeName + "is not a compatible type for " + this.type);
			return;
		}
		this.id = $xmlDoc.attr('id');
		var isRequired = $xmlDoc.attr('required');
		this.required = isRequired == 'true'? true : false;

		this.title = $xmlDoc.find('label').text();
		this.help = $xmlDoc.find('help').text();
		this.setSpecificFromXMLDoc($xmlDoc);
	};
};

// Class TextBox: Inherits from Field
var TextBox = function() {
	this.type = 'text';
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
	
	this.setJSONValuesSpecific = function(element) {
		this.size = element.size;
		this.maxValue = element.maxValue;
		this.bydefault = element.bydefault;
	};
	
	this.addXMLSpecificAttributes = function() {
		var xml = attribCreator('max', this.maxValue);
		if(this.bydefault)
			xml += attribCreator('value', this.bydefault);
		return xml;
	};
	
	this.addXMLElements = function(){
		var xml = tagCreator('size', this.size);
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
	
	this.setSpecificFromXMLDoc = function($xmlDoc){
		this.maxValue = $xmlDoc.attr('max');
		this.size = $xmlDoc.find('size').text();
		this.bydefault = $xmlDoc.attr('value');
	};
};
// Inheritance to TextBox from Field
TextBox.prototype = new Field();


/// Box ///
var globalId = 0;
var generateUUID = function(){
	globalId++;
	if(globalId >= 1000000){
		globalId = 0;
	}
	return globalId;
};

var Box = function(type) {
	this.component          = type;
	this.optionsTitles      = new Array();
	this.optionsTitles[0]   = '';
	this.boxId              = '';
	this.optionLabelInputId = this.boxId+"_optionLabelId";
	this.type               = type;
	this.boxClass           = "boxClass";
	this.msgAddBox          = null; 
	this.msgRemoveError     = null;
	this.msgBoxLabel        = null;
		
	this.setJSONValuesSpecific = function(element) {
		this.type          = element.type;
		this.optionsTitles = element.optionsTitles;
	};
	
	this.addXMLSpecificAttributes = function(){
		return "";
	};
	
	this.addXMLElements = function(){
		var xml = '';
		for(var i=0; i<this.optionsTitles.length; i++){
			xml += '<option>';
			xml += this.optionsTitles[i];
			xml += '</option>';
		}		
		return xml;
	};
	
	this.toHTMLSpecific = function(){		
		var html = '';		
		for(var i=0; i<this.optionsTitles.length; i++){
			html += '<input type="'+this.type+'" name="'+this.boxId+'">';
			html += this.optionsTitles[i];
			html += '</input>';
		}		
		return html;
	};
	
	this.showSpecificProperties = function(){
		$('#propertiesSpecific').show();
		
		if($("li."+this.boxClass).length>0){
			$("li."+this.boxClass).remove();
		}
		
		var boxField = this.createField(this.component);

		for(var i=0; i<this.optionsTitles.length; i++){
			this.addInputField(boxField,this.optionsTitles[i],this);
		}		
		this.addAddBoxButton(boxField);
	};
	
	this.addAddBoxButton = function(field){
		var addBoxId   = "add"+this.boxId;
		
		field.append('<button type="button" id="'+addBoxId+'">'+
					'<img src="../../resources/images/add.png"/>'+
					  this.msgAddBox+
					 '</button>');
		
		var myAddInputField = this.addInputField;
		var myBox           = this;
		$('button#'+addBoxId).bind("click",function(e){
			myAddInputField(field,"",myBox);			
		});
	};
	
	this.addInputField = function(field, value, myBox){
		var uuid               = generateUUID();
		var rowId              = myBox.boxId+"_row" +uuid;
		var removeFieldId      = myBox.boxId+"_removeField"+uuid;
		var rowClass           = myBox.boxId+"_rowClass";
		var msgRemoveError     = myBox.msgRemoveError;
		
		field.append('<tr id="'+rowId+'" class="'+rowClass+'"><td>'+
					 '<input type="text" id="'+myBox.optionLabelInputId+'" value="'+value+'"/>'+
					 '<a id="'+removeFieldId+'">'+
					 '<img src="../../resources/images/delete.png"/>'+
					 '</a>'+
					 '</tr></td>');
		
		$('a#'+removeFieldId).bind("click", function(e){
			if($("tr."+rowClass).size()>1){
				$('tr#'+rowId).remove();				
			} else {
				alert(msgRemoveError);
			}
		});
	};
	
	/**
	 * Creates the <li> tag inside the properties. 
	 * @param label
	 */
	this.createField = function(label){
		var ul             = $('#propertiesSpecific > ul');
		var contentTableId = this.boxId+"contentTableId";
		var msgBoxLabel    = this.msgBoxLabel;
		
		ul.append('<li class="'+this.boxClass+'">'+
				  '<label> '+msgBoxLabel+': </label>'+
				  '<table id="'+contentTableId+'"></table>'+
				  '</li>');
		
		return $("table#"+contentTableId);
	};
	
	this.saveSpecificProperties = function(){
		var itensToSave = new Array();
		$('input#'+this.optionLabelInputId).each(function() {
			itensToSave.push( $(this).val() );
		});
		this.optionsTitles = itensToSave;
	};
	
	this.setSpecificFromXMLDoc = function($xmlDoc){
		var parsedOptions = new Array();		
		var foundOptions  = $xmlDoc.find('option');
		
		for(var i=0; i<foundOptions.length; i++){
			parsedOptions.push(foundOptions[i].textContent);
		}
		this.optionsTitles = parsedOptions;
	};
};
Box.prototype = new Field();

////CheckBox Field ////
var CheckBox  = function(){
	this.boxId            = this.component+generateUUID();
	this.msgAddBox        = jQuery.i18n.prop('msg_box_add'); 
	this.msgRemoveError   = jQuery.i18n.prop('msg_box_remove_error');
	this.msgBoxLabel      = jQuery.i18n.prop('msg_box_label');
}; 
CheckBox.prototype = new Box("checkbox");

////RadioBox Field ////
var RadioBox  = function(){
	this.boxId            = this.component+generateUUID();
	this.msgAddBox        = jQuery.i18n.prop('msg_box_add'); 
	this.msgRemoveError   = jQuery.i18n.prop('msg_box_remove_error');
	this.msgBoxLabel      = jQuery.i18n.prop('msg_box_label');
}; 
RadioBox.prototype = new Box("radio");

////ComboBox Field ////
var ComboBox  = function(){
	this.boxId            = this.component+generateUUID();
	this.msgAddBox        = jQuery.i18n.prop('msg_box_add'); 
	this.msgRemoveError   = jQuery.i18n.prop('msg_box_remove_error');
	this.msgBoxLabel      = jQuery.i18n.prop('msg_box_label');
	
	this.toHTMLSpecific = function(){
		var html = "<select>";
		
		for(var i=0; i<this.optionsTitles.length; i++){
			html += '<option value="'+this.optionsTitles[i]+'">';
			html += this.optionsTitles[i];
			html += '</option>';
		}		
		
		html += "</select>";		
		return html;
	};
}; 
ComboBox.prototype = new Box("combobox");


//// Number Field ////
var NumberField = function(){
	this.type = 'number';
	this.bydefault = '';
	this.maxValue  = '';
	this.minValue  = '';
	
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
	
	this.setJSONValuesSpecific = function(element) {
		this.bydefault = element.bydefault;
		this.maxValue = element.maxValue;
		this.minValue = element.minValue;
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
	
	this.setSpecificFromXMLDoc = function($xmlDoc){
		this.maxValue = $xmlDoc.attr('max');
		this.minValue = $xmlDoc.attr('min');
		this.bydefault = $xmlDoc.attr('value');
	};
};
NumberField.prototype = new Field();

var DateField = function() {
	this.type = 'date';
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
	
	this.setJSONValuesSpecific = function(element) {
		this.bydefault = element.bydefault;
		this.maxValue = element.maxValue;
		this.minValue = element.minValue;
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
	
	this.setSpecificFromXMLDoc = function($xmlDoc){
		this.maxValue = $xmlDoc.attr('max');
		this.minValue = $xmlDoc.attr('min');
		this.bydefault = $xmlDoc.attr('value');
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
	case 'checkbox':
		field = new CheckBox();
		field.title = jQuery.i18n.prop('msg_form_checkBoxTitle');
		break;
	case 'radio':
		field = new RadioBox();
		field.title = jQuery.i18n.prop('msg_form_radioBoxTitle');
		break;
	case 'combobox':
		field = new ComboBox();
		field.title = jQuery.i18n.prop('msg_form_comboBoxTitle');
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
	return '<' + tag + '>' + value + '</' + tag + '>';
};
