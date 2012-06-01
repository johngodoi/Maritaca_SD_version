// Generic Field class
//
// This in an abstract class. The following methods must 
// be implemented in the subclasses: 
//		addXMLSpecificAttributes: return a string with the tag's attributes 
//		addXMLElements:	return a string with tag's XML elements
//		toHTMLSpecific: return a string with HTML corresponding component
//		showSpecificProperties: show the properties of a specific field 
//		saveSpecificProperties: updates the properties of a field
//      validateSpecific: checks if the fields were filled with valid information
//      setFromXMLDoc: loads the data from xml
//      setSpecificFromXMLDoc: parses the given document object setting the corresponding
//                             properties in the current field
//	See TextBox class as example
//
var Field = function() {
	this.type     = '';
	this.id       = '';
	this.next	  = '';
	this.required = false;
	
	this.title    = '';
	this.help     = '';
	
	this.lastCondTableId = 0;	
	this.conditionals    = new Array();
	
	this.toHTML = function(i) {
		var html = '';
		html += '<label class="fieldLabel" for="label_' + this.id + '"> ';
		html += idToQuestionNumber("field_"+i) + " - ";
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
		xml += '<' + this.type + ' id="' + this.id + '" next="' + this.next + '" required="' + this.required  + '" ';
		xml += this.addXMLSpecificAttributes();
		xml += '>';
		xml += tagCreator('label', this.title);
		xml += tagCreator('help', this.help);
		xml += this.addConditionalsXML();
		xml += this.addXMLElements();
		xml += '</' + this.type + '>';
		return xml;
	};
	
	this.addConditionalsXML = function(){
		var xml = '';
		
		for(var i=0; i<this.conditionals.length; i++){
			xml += '<if ';
			var comparator = this.symbolToString(this.conditionals[i].comparator);
			xml += 'comparison="'+comparator+'" ';
			xml += 'value="'+this.conditionals[i].value+'" ';
			xml += 'goto="'+this.conditionals[i].goTo+'" />';
		}
		
		return xml;
	};
	
	this.symbolToString = function(symbol){				
		switch(symbol){
			case "<":
				return "less";
			case "<=":
				return "lessequal";
			case ">":
				return "greater";
			case ">=":
				return "greaterequal";
			case "==":
				return "equal";
			default:
				addMessage(jQuery.i18n.prop('msg_error_form_parsing'), 'error');				
		}
	};
	
	this.stringToSymbol = function(string){
		switch(string){
			case "less":
				return "<";
			case "lessequal":
				return "<=";
			case "greater":
				return ">";
			case "greaterequal":
				return ">=";
			case "equal":
				return "==";
			default:
				addMessage(jQuery.i18n.prop('msg_error_form_parsing'), 'error');
		}		
	};
	
	this.setJSONValues = function(element){
		this.type = element.type;
		this.id = element.id;
		this.required = element.required;
		this.title = element.title;
		this.help = element.help;
		this.conditionals = element.conditionals;
		this.setJSONValuesSpecific(element);
	};
	
	this.showProperties = function(){
		// general properties are:
		var label = jQuery.i18n.prop('msg_field_labelProperty');
		var help  = jQuery.i18n.prop('msg_field_helpProperty');
		
		var html = '<table>';
		html += createTextProperty('fieldLabel', this.title, label);
		html += createTextProperty('fieldHelp', this.help, help);
		html += createRequiredProperty(this.required);
		
		if(nextQuestions = $('fieldset#xmlForm > ol > li.editing').nextAll().length<1){
			html += this.disabledConditions();
		} else {
			html += this.addConditions();
		}
		
		html += this.showSpecificProperties();
		html += '</table>';
		
		$('#properties').append(html);
		
		// initialize JQuery Datepicker;
		var locale = jQuery.i18n.prop('msg_datepicker_locale');
		$('.datepicker').datepicker( $.datepicker.regional[locale] );
	};
	
	this.disabledConditions = function(){
		this.conditionals = new Array();
		
		var msg = jQuery.i18n.prop('msg_field_conditional_error_last_question');
		return this.conditionalButton('addMessage(\''+msg+'\',\'error\');');
	};
	
	this.addConditions = function(){
		var html       = "";
		var htmlCondBt = this.addConditionalButton();
		
		for(var i=0; i<this.conditionals.length; i++){
			comparison = this.conditionals[i].comparator;
			value      = this.conditionals[i].value;
			goTo       = this.conditionals[i].goTo;
			
			html += this.createConditionalProperty(comparison,value,goTo);
		}
		
		html += htmlCondBt;						
		return html;
	};

	this.removeConditional = function(tableId){
		$("table.condPropTable#"+tableId).parent().parent().remove();
	};

	this.removeConditionalButton = function(tableId){
		return	'<img src="../../resources/img/delete.png" class="removeConditional"'+
				'onclick="new Field().removeConditional('+tableId+');saveField()"/>';
	};

	this.createConditionalProperty = function(comparison, value, goTo){		
		var ifHtml        = tagCreator('label',jQuery.i18n.prop('msg_field_if_conditional'));
		var comboHtml     = this.conditionalComboBox(comparison);
		var inputIfHtml   = inputCreator('text',null,value,null,null,'valueCond');
		var removeIfBtHtml= this.removeConditionalButton(this.lastCondTableId);
		
		var ifTdOneHtml   = tagCreator('td', ifHtml);
		var ifTdTwoHtml   = tagCreator('td', comboHtml+inputIfHtml+removeIfBtHtml);
		
		var rowOne        = ifTdOneHtml + ifTdTwoHtml;	
		var rowOneHtml    = tagCreator('tr', rowOne);
		
		var thenHtml      = tagCreator('label',jQuery.i18n.prop('msg_field_then_conditional'));
		var inputThenHtml = this.goToBox(goTo);
		
		var thenTdOneHtml = tagCreator('td', thenHtml,'alignRight');
		var thenTdTwoHtml = tagCreator('td', inputThenHtml);		
		
		var rowTwo        = thenTdOneHtml + thenTdTwoHtml; 
		var rowTwoHtml    = tagCreator('tr',rowTwo);
		
		var tableHtml     = '<table class="condPropTable" id="'+this.lastCondTableId+'">'; 
		tableHtml        += rowOneHtml + rowTwoHtml + '</table>';
		
		tableHtml         = '<tr><td colspan="2">'+tableHtml+'</td></tr>';
		
		this.lastTableId++;

		return tableHtml;
	};
	
	this.goToBox = function(goTo){
		var nextQuestions = $('fieldset#xmlForm > ol > li.editing').nextAll();
		
		var html = '<select class="goToCond" onchange="saveField();">';
		for(var i=0; i<nextQuestions.length; i++){
			var selected="";
			if(nextQuestions[i].id == goTo){
				selected = 'selected="selected"';
			}
			var questionOption = nextQuestions[i].textContent;
			html += '<option '+selected+'>' + questionOption + '</option>';
		}
		
		var endSelected = '';
		if(goTo=='-1'){
			endSelected = 'selected="selected"';
		}
		
		var formEnd = jQuery.i18n.prop('msg_field_end_form');
		html += '<option '+endSelected+'>' + formEnd + '</option>';
		html += '</select>';
				
		return html;
	};
	
	this.addConditionalProperty = function(){
		var tableHtml = this.createConditionalProperty('','','');				
		$("#addConditionalTable:first-child").prepend(tableHtml);
	};

	this.conditionalComboBox = function(selectedValue){
		var options = "";
		var conditionals = ["==", "<", "<=", ">", ">="];	
		for(var i=0; i<conditionals.length; i++){
			if(conditionals[i] == selectedValue){
				options += '<option selected="selected">' + conditionals[i] + '</option>';
			} else {
				options += tagCreator('option',conditionals[i]);
			}
		}
		var comboBox = '<select class="compCond">'+options+'</select>';

		return comboBox;
	};
		
	this.addConditionalButton = function(){		
		return this.conditionalButton('new Field().addConditionalProperty();');
	};
	
	this.conditionalButton = function(onclick){
		msgAdd = jQuery.i18n.prop('msg_field_add_conditional');
		
		var html =	'<tr><td colspan="2">'+
		    '<table id="addConditionalTable">'+
		    '<tr><td colspan="2">'+
			'<button type="button" onclick="'+onclick+'">'+						
				msgAdd+
			'</button>'+
			'</td></tr>'+
			'</table>'+
			'</td></tr>';
		
		return html;		
	};
	
	this.saveProperties = function(){
		if(!this.validateSpecific()){
			return;
		}		
		this.title = $('#fieldLabel').val();
		this.help  = $('#fieldHelp').val();
		this.required = $('#fieldRequiredTrue').is(':checked');
		this.conditionals = this.retrieveConditionals();
		this.saveSpecificProperties();
	};
	
	this.retrieveConditionals = function (){
		var localConditionals = new Array();
		
		var condComps  = $('select.compCond');
		var condValues = $('input.valueCond');
		var condGoTos  = $('select.goToCond > option:selected');
		var condIds    = $('table.condPropTable');
				
		for(var i=0; i<condValues.length; i++){
			var conditional = {};
			
			conditional.comparator=condComps[i].value;
			conditional.value=condValues[i].value;
			
			var formEnd = jQuery.i18n.prop('msg_field_end_form');
			if(condGoTos[i].value == formEnd){
				conditional.goTo='-1';
			} else {
				conditional.goTo=questionNumberToId(condGoTos[i].value);
			}
			conditional.id=condIds[i].attributes[1].value;
			
			localConditionals.push(conditional);
		};
		
		return localConditionals;
	};
	
	this.setFromXMLDoc = function(xmlDoc){
		var $xmlDoc = $( xmlDoc );
		if(xmlDoc.nodeName != this.type){
			console.error(xmlDoc.nodeName + " is not a compatible type for " + this.type);
			return;
		}
		this.id        = $xmlDoc.attr('id');
		var isRequired = $xmlDoc.attr('required');
		this.required  = isRequired == 'true'? true : false;

		this.title     = $xmlDoc.find('label').text();
		this.help      = $xmlDoc.find('help').text();
		
		this.conditionals = new Array();
		var listConditionals = $xmlDoc.find('if');
		for(var i=0; i<listConditionals.length; i++){
			var conditional = {};
			var conditionalElement = listConditionals[i];
			conditional.comparator=this.stringToSymbol(conditionalElement.attributes[0].value);
			conditional.value = conditionalElement.attributes[1].value;
			conditional.goTo  = conditionalElement.attributes[2].value;			
			this.conditionals.push(conditional);
		}
		
		this.setSpecificFromXMLDoc($xmlDoc);
	};
};

// Class TextBox: Inherits from Field
var TextBox = function() {
	this.type = 'text';
	// TODO evaluate the size default
	this.size = '100';
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
		this.bydefault = element.bydefault;
	};
	
	this.addXMLSpecificAttributes = function() {
		var xml = '';
		return xml;
	};
	
	this.addXMLElements = function(){
		var xml = tagCreator('size', this.size);
		xml += tagCreator('default', this.bydefault);
		return xml;

	};
	
	this.showSpecificProperties = function(){
		var defaultTitle = jQuery.i18n.prop('msg_field_defaultProperty');
		var sizeTitle = jQuery.i18n.prop('msg_field_sizeProperty');
		
		var html = createTextProperty('fieldDefault', this.bydefault, defaultTitle) ;
		html += createNumberProperty('fieldSize', this.size, sizeTitle);
		
		return html;
	};
	
	this.saveSpecificProperties = function(){
		this.bydefault = $('#fieldDefault').val();
		this.size = $('#fieldSize').val();
	};
	
	this.validateSpecific = function(){
		bydefault = $('#fieldDefault').val();
		size      = $('#fieldSize').val();
		
		if( bydefault!='' && size!='' ){
			if(bydefault.length > size){
				addMessage(jQuery.i18n.prop("msg_text_validation_error_size"),"error");
				return false;
			}
		}
		return true;
	};
	
	this.setSpecificFromXMLDoc = function($xmlDoc){
		this.size = $xmlDoc.find('size').text();
		this.bydefault = $xmlDoc.find('default').text();
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
	this.byDefault	        = '';

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
		xml += tagCreator('default', this.bydefault);
		for(var i=0; i<this.optionsTitles.length; i++){
			xml += '<option>';
			xml += this.optionsTitles[i];
			xml += '</option>';
		}		
		return xml;
	};
	
	this.validateSpecific = function(){
		var itensToSave  = new Array();
		var repeatedItem = null; 
		$('input.'+this.optionLabelInputClass).each(function() {
			var item = $(this).val();
			if(item != null && item != ''){
				if(itensToSave.indexOf(item)!=-1){
					repeatedItem = $(this)[0];
				} else {
					itensToSave.push(item);
				}
			}
		});

		if(repeatedItem !=null){
			var rowId = repeatedItem.parentNode.parentNode.id;
			this.deleteBoxField(rowId);
			var errorMsg = jQuery.i18n.prop("msg_box_validation_error_repeated_item",repeatedItem.value); 
			addMessage(errorMsg,"error");
			return false;
		}
		
		return true;
	};
	
	this.showSpecificProperties = function(){
		var defaultTitle = jQuery.i18n.prop('msg_field_defaultProperty');
		var html = createTextProperty('fieldDefault', this.bydefault, defaultTitle) ;
		html += createLabelProperty("",this.msgBoxLabel);
		
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
		this.bydefault = $('#fieldDefault').val();
		var itensToSave = new Array();
		$('input.'+this.optionLabelInputClass).each(function() {
			var item = $(this).val();
			if(item==null){
				item='';				
			}
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
		this.bydefault = $xmlDoc.find('default').text();
	};
};
Box.prototype = new Field();

//// CheckBox Field ////
var CheckBox  = function(){
	this.boxId            = this.component+generateUUID();
	this.msgAddBox        = jQuery.i18n.prop('msg_box_add'); 
	this.msgRemoveError   = jQuery.i18n.prop('msg_box_remove_error');
	this.msgBoxLabel      = jQuery.i18n.prop('msg_box_label');
	
	this.toHTMLSpecific = function(){
		var html = '';
		for(var i=0; i<this.optionsTitles.length; i++){
			html +='<input ';
			html += attribCreator('type', this.type);
			html += attribCreator('name', this.boxId);
			html += attribCreator('readonly', 'readonly');
			if(this.optionsTitles[i] == this.bydefault) {
				html += 'checked';
			}
			html += '>';
			html += this.optionsTitles[i];
			html += '</input>';	
		}				
		return html;
	};
}; 
CheckBox.prototype = new Box("checkbox");

//// RadioBox Field ////
var RadioBox  = function(){
	this.boxId            = this.component+generateUUID();
	this.msgAddBox        = jQuery.i18n.prop('msg_box_add'); 
	this.msgRemoveError   = jQuery.i18n.prop('msg_box_remove_error');
	this.msgBoxLabel      = jQuery.i18n.prop('msg_box_label');
	
	this.toHTMLSpecific = function(){
		var html = '';
		for(var i=0; i<this.optionsTitles.length; i++){
			html +='<input ';
			html += attribCreator('type', this.type);
			html += attribCreator('name', this.boxId);
			html += attribCreator('readonly', 'readonly');
			if(this.optionsTitles[i] == this.bydefault) {
				html += 'checked';
			}
			html += '>';
			html += this.optionsTitles[i];
			html += '</input>';	
		}				
		return html;
	};
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
			html += '<option value="' + this.optionsTitles[i]+'"';
			if(this.optionsTitles[i] == this.bydefault) {
				html += 'selected';
			}
			html += '>';
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
		if(this.bydefault)
			html += attribCreator('value', this.bydefault);
		html += this.specificAttributes();
		html += '/>';
		
		return html;
	};
	
	this.addXMLSpecificAttributes = function() {
		return this.specificAttributes();
	};
	
	this.addXMLElements = function(){
		var xml = tagCreator('default', this.bydefault);
		return xml;
	};
	
	this.setJSONValuesSpecific = function(element) {
		this.bydefault = element.bydefault;
		this.maxValue = element.maxValue;
		this.minValue = element.minValue;
	};
	
	this.showSpecificProperties = function(){
		var defaultTitle  = jQuery.i18n.prop('msg_field_defaultProperty');
		var maxValueTitle = jQuery.i18n.prop('msg_field_maxValueProperty');
		var minValueTitle = jQuery.i18n.prop('msg_field_minValueProperty');
		
		var html = createNumberProperty('fieldDefault', this.bydefault, defaultTitle) ;		
		html    += createNumberProperty('fieldMaxValue', this.maxValue, maxValueTitle) ;		
		html    += createNumberProperty('fieldMinValue', this.minValue, minValueTitle);
		
		return html;
	};
	
	this.validateSpecific = function(){
		maxValue  = parseInt( $('#fieldMaxValue').val() );
		minValue  = parseInt( $('#fieldMinValue').val() );
		bydefault = parseInt( $('#fieldDefault').val() );
		
		if(maxValue != '' && minValue != ''){
			if(maxValue <= minValue){
				addMessage(jQuery.i18n.prop("msg_number_validation_error_max_min"),"error");
				return false;
			}
		}
		
		if(maxValue != '' && bydefault != ''){
			if(maxValue < bydefault){
				addMessage(jQuery.i18n.prop("msg_number_validation_error_max_default"),"error");
				return false;
			}
		}
		
		if(minValue != '' && bydefault != ''){
			if(minValue > bydefault){
				addMessage(jQuery.i18n.prop("msg_number_validation_error_min_default"),"error");
				return false;
			}
		}
		
		return true;
	};
	
	this.saveSpecificProperties = function(){
		this.bydefault = $('#fieldDefault').val();
		this.minValue = $('#fieldMinValue').val();
		this.maxValue = $('#fieldMaxValue').val();
	};
	
	this.specificAttributes = function(){
		var att = attribCreator('min', this.minValue);
		att += attribCreator('max', this.maxValue);
		return att;
	};
	
	this.setSpecificFromXMLDoc = function($xmlDoc){
		this.maxValue = $xmlDoc.attr('max');
		this.minValue = $xmlDoc.attr('min');
		this.bydefault = $xmlDoc.find('default').text();
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
		if(this.bydefault)
			html += attribCreator('value', this.bydefault);
		html += this.specificAttributes();
		html += '/>';
		
		return html;
	};
	
	this.addXMLSpecificAttributes = function() {
		return this.specificAttributes();
	};
	
	this.addXMLElements = function(){
		var xml = tagCreator('default', this.bydefault);
		// get date format from JQuery datepicker
		var dateFormat = $( ".datepicker" ).datepicker( "option", "dateFormat" );
		xml += tagCreator('format', dateFormat);
		return xml;
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
	
	this.validateSpecific= function(){				
		maxValue  = new Date( $('#fieldMaxValue').val() );
		minValue  = new Date( $('#fieldMinValue').val() );
		bydefault = new Date( $('#fieldDefault').val() );
		
		if(maxValue != '' && minValue != ''){
			if(maxValue <= minValue){
				addMessage(jQuery.i18n.prop("msg_date_validation_error_max_min"),"error");
				return false;
			}
		}
		
		if(maxValue != '' && bydefault != ''){
			if(maxValue < bydefault){
				addMessage(jQuery.i18n.prop("msg_date_validation_error_max_default"),"error");
				return false;
			}
		}
		
		if(minValue != '' && bydefault != ''){
			if(minValue > bydefault){
				addMessage(jQuery.i18n.prop("msg_date_validation_error_min_default"),"error");
				return false;
			}
		}
				
		$("#fieldDefault").mask("99/99/9999");
		$("#fieldMaxValue").mask("99/99/9999");
		$("#fieldMinValue").mask("99/99/9999");
							
		return true;
	};

	this.saveSpecificProperties = function(){
		this.bydefault = $('#fieldDefault').val();
		this.maxValue = $('#fieldMaxValue').val();
		this.minValue = $('#fieldMinValue').val();
	};

	this.specificAttributes = function(){
		var att = attribCreator('min', this.minValue);
		att += attribCreator('max', this.maxValue);
		return att;
	};
	
	this.setSpecificFromXMLDoc = function($xmlDoc){
		this.maxValue = $xmlDoc.attr('max');
		this.minValue = $xmlDoc.attr('min');
		this.bydefault = $xmlDoc.find('default').text();
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



