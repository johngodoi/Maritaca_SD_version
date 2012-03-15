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
//      setSpecificFromXMLDoc: parses the given document object setting the corresponding
//                             properties in the current field
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
		
		var up = jQuery.i18n.prop('msg_up');
		var down = jQuery.i18n.prop('msg_down');
		var deleteField = jQuery.i18n.prop('msg_delete');
		
		html += '<div class="order">' + 
					'<a href="#" onclick="move(-1); return false;">' + 
						'<img src="../../resources/img/arrow_up.png" alt="up"' +
						'title="' + up + '"/></a>' + 
					'<a href="#" onclick="move(1); return false;">' + 
						'<img src="../../resources/img/arrow_down.png" alt="down"' +
						'title="' + down + '"/></a>' +
					'<a href="#" onclick="deleteField(); return false;">' + 
						'<img src="../../resources/img/delete.png" alt="down"' +
						'title="' + deleteField + '"/></a>' +
				'</div>';
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
		// general properties are:
		var label = jQuery.i18n.prop('msg_field_labelProperty');
		var help = jQuery.i18n.prop('msg_field_helpProperty');
		
		var html = '<table>';
		html += createTextProperty('fieldLabel', this.title, label);
		html += createTextProperty('fieldHelp', this.help, help);
		html += createRequiredProperty(this.required);
		
		html += this.showSpecificProperties();

		html += '</table>';
		
		$('#properties').append(html);
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
		html += attribCreator('readonly', 'readonly');
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
		var defaultTitle = jQuery.i18n.prop('msg_field_defaultProperty');
		var maxValueTitle = jQuery.i18n.prop('msg_field_maxValueProperty');
		var sizeTitle = jQuery.i18n.prop('msg_field_sizeProperty');
		
		var html = createTextProperty('fieldDefault', this.bydefault, defaultTitle) ;
		html += createNumberProperty('fieldMaxValue', this.maxValue, maxValueTitle) ;
		html += createNumberProperty('fieldSize', this.size, sizeTitle);
		
		return html;
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
	this.optionLabelInputClass = this.boxId+"_optionLabel";
	this.type               = type;
	this.boxClass           = "boxClass";
	this.msgAddBox          = null; 
	this.msgRemoveError     = null;
	this.msgBoxLabel        = null;

	this.toHTMLSpecific = function(){
		var html = '';
		for(var i=0; i<this.optionsTitles.length; i++){
			html +='<input ';
			html += attribCreator('type', this.type);
			html += attribCreator('name', this.boxId);
			html += attribCreator('readonly', 'readonly');
			html += '>';
			html += this.optionsTitles[i];
			html += '</input>';	
		}				
		return html;
	};
	
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
	
	
	this.showSpecificProperties = function(){
		var html = createLabelProperty("",this.msgBoxLabel);
		
		html += this.openTable();
		for(var i=0; i<this.optionsTitles.length; i++){
			html += this.createOptionRow(this.optionsTitles[i],i);
		}
		html += this.closeTable();		
		html += this.addItemButton("");
		
		return html;
	};
	
	this.addItemButton = function(value){		
		var html =	'<tr><td colspan="2">'+
						'<button type="button" onclick="new Box().addBoxOptionField();">'+						
						'<img src="../../resources/img/add.png"/>'+
							this.msgAddBox+
						'</button>'+
					'</td></tr>';
		return html;
	};
	
	this.addBoxOptionField = function(){
		var html     = this.createOptionRow("");
		$("table#boxOptionsTable").append(html);
	};
	
	this.openTable = function(value){
		var htmlTable ='<tr><td colspan="2">';		
		htmlTable    += '<table id="boxOptionsTable">';		
		return htmlTable;
	};
	
	this.closeTable = function(value){
		var htmlTable = '</table>';
		htmlTable    += '</td></tr>';
		return htmlTable;
	};
	
	this.createOptionRow = function(value,rowNumber){
		var uuid = generateUUID();
		var htmlRow = '<tr id="'+uuid+'">';
		htmlRow    += '   <td colspan="2">';
		htmlRow    += inputCreator("text","",value,"","",this.optionLabelInputClass);
		
		if(rowNumber != 0){
			htmlRow+= '      <a  onclick="new Box().deleteBoxField(\''+uuid+'\');saveField();">'; 
	        htmlRow+= '         <img src="../../resources/img/delete.png"/>'; 
	        htmlRow+= '      </a>';	
		}
		
		htmlRow    += '   </td>';
		htmlRow    += '</tr>';
		return htmlRow;
	};
	
	this. deleteBoxField = function(id){
		$("tr#"+id).remove();
	};
	
	this.saveSpecificProperties = function(){
		var itensToSave = new Array();
		$('input.'+this.optionLabelInputClass).each(function() {
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

//// CheckBox Field ////
var CheckBox  = function(){
	this.boxId            = this.component+generateUUID();
	this.msgAddBox        = jQuery.i18n.prop('msg_box_add'); 
	this.msgRemoveError   = jQuery.i18n.prop('msg_box_remove_error');
	this.msgBoxLabel      = jQuery.i18n.prop('msg_box_label');
}; 
CheckBox.prototype = new Box("checkbox");

//// RadioBox Field ////
var RadioBox  = function(){
	this.boxId            = this.component+generateUUID();
	this.msgAddBox        = jQuery.i18n.prop('msg_box_add'); 
	this.msgRemoveError   = jQuery.i18n.prop('msg_box_remove_error');
	this.msgBoxLabel      = jQuery.i18n.prop('msg_box_label');
}; 
RadioBox.prototype = new Box("radio");

//// ComboBox Field ////
var ComboBox  = function(){
	this.boxId            = this.component+generateUUID();
	this.msgAddBox        = jQuery.i18n.prop('msg_box_add'); 
	this.msgRemoveError   = jQuery.i18n.prop('msg_box_remove_error');
	this.msgBoxLabel      = jQuery.i18n.prop('msg_box_label');
	
	this.toHTMLSpecific = function(){
		var html = "<select>";
		for(var i = 0; i < this.optionsTitles.length; i++){
			html += '<option value="' + this.optionsTitles[i] + '">';
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
		html += attribCreator('readonly', 'readonly');
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
		var defaultTitle = jQuery.i18n.prop('msg_field_defaultProperty');
		var maxValueTitle = jQuery.i18n.prop('msg_field_maxValueProperty');
		var minValueTitle = jQuery.i18n.prop('msg_field_minValueProperty');
		
		var html = createNumberProperty('fieldDefault', this.bydefault, defaultTitle) ;
		html += createNumberProperty('fieldMaxValue', this.maxValue, maxValueTitle) ;
		html += createNumberProperty('fieldMinValue', this.minValue, minValueTitle);
		
		return html;
	};
	
	this.saveSpecificProperties = function(){
		this.bydefault = $('#fieldDefault').val();
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

//// Date Field ////
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
		html += attribCreator('readonly', 'readonly');
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
		var defaultTitle = jQuery.i18n.prop('msg_field_defaultProperty');
		var maxValueTitle = jQuery.i18n.prop('msg_field_maxValueProperty');
		var minValueTitle = jQuery.i18n.prop('msg_field_minValueProperty');
		
		var html = createDateProperty('fieldDefault', this.bydefault, defaultTitle) ;
		html += createDateProperty('fieldMaxValue', this.maxValue, maxValueTitle) ;
		html += createDateProperty('fieldMinValue', this.minValue, minValueTitle);
		
		return html;
	};

	this.saveSpecificProperties = function(){
		this.bydefault = $('#fieldDefault').val();
		this.maxValue = $('#fieldMaxValue').val();
		this.minValue = $('#fieldMinValue').val();
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

//// Field Factory Method ////
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



