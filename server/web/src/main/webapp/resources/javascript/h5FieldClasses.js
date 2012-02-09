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
	this.component        = "checkBox";
	this.optionsTitles    = new Array();
	this.optionsTitles[0] = '';
	this.checkBoxId       = "checkBoxId";
		
	this.addXMLSpecificAttributes = function(){
		
	};
	
	this.addXMLElements = function(){
		
	};
	
	this.toHTMLSpecific = function(){
		var html = '';
		
		for(var i=0; i<this.optionsTitles.length; i++){
			html += '<input type="checkbox">';
			html += this.optionsTitles[i];
			html += '</input>';
		}
		
		return html;
	};
	
	/**
	 * Check box component layout:
	 * 
	 * Check Box:  
	 *    ________ [Remove]
	 *    ...
	 *    ________ [Remove]    
	 *    [Add]
	 */
	this.showSpecificProperties = function(){
		$('#propertiesSpecific').show();
						
		if($("li#"+this.checkBoxId).length==0){
			var checkBoxField = this.createField(this.component);

			for(var i=0; i<this.optionsTitles.length; i++){
				this.addInputField(checkBoxField,this.optionsTitles[i]);
			}		
			this.addAddCheckBoxButton(checkBoxField);
		} else {
			$("li#"+this.checkBoxId).children().show();
		}
	};
	
	this.addAddCheckBoxButton = function(field){
		var addCheckBoxText = "Add Item"; //TODO Internationalize this message
		var addCheckBoxId   = "addCheckBoxId";
		
		field.append('<button type="button" id="'+addCheckBoxId+'">'+
					'<img src="../../resources/images/add.png"/>'+
					  addCheckBoxText+
					 '</button>');
		
		var fAddInputField = this.addInputField;
		$('button#'+addCheckBoxId).bind("click",function(e){
			fAddInputField(field,"");			
		});
	};
	
	this.addInputField = function(field, value){
		var newDate            = new Date;
		var uuid               = newDate.getTime();
		var inputFieldId       = "inputField_" +uuid;
		var removeFieldId      = "removeField_"+uuid;
		var inputFieldClass    = "inputFieldClass";
		var checkBoxFieldId    = "checkBoxField";
		
		field.append('<tr id="'+inputFieldId+'" class="'+inputFieldClass+'"><td>'+
					 '<input type="text" id="'+checkBoxFieldId+'" value="'+value+'"/>'+
					 '<a id="'+removeFieldId+'">'+
					 '<img src="../../resources/images/delete.png"/>'+
					 '</a>'+
					 '</tr></td>');
		
		$('a#'+removeFieldId).bind("click", function(e){
			if($("tr."+inputFieldClass).size()>1){
				$('tr#'+inputFieldId).remove();				
			} else {
				//TODO Internationalize this message
				alert("Can't remove the last element.");
			}
		});
	};
	
	/**
	 * Creates the <li> tag inside the properties. 
	 * @param label
	 */
	this.createField = function(label){
		var ul = $('#propertiesSpecific > ul');
		var fieldContentId = "field_"+label+"_id";
		
		ul.append('<li id="'+this.checkBoxId+'">'+
				  '<label> Check Box Options: </label>'+ // TODO Internationalize
				  '<table id="'+fieldContentId+'"></table>'+
				  '</li>');
		
		return $("table#"+fieldContentId);
	};
	
	this.saveSpecificProperties = function(){
		var itensToSave = new Array();
		$('input#checkBoxField').each(function() {
			itensToSave.push( $(this).val() );
		});
		this.optionsTitles = itensToSave;
	};
};
CheckBox.prototype = new Field();


//// Number Field ////
var NumberField = function(){
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
	case 'checkbox':
		field = new CheckBox();
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
