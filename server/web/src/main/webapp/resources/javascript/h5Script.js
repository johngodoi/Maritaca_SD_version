var editElement = null;
var form = null;
var warning = false;
var actualForm = localStorage['actualForm'];

function initFormEditor(formtitle) {
	// This will initialize the plugin jQuery.i18n
	// The file properties is reference by path
	jQuery.i18n.properties({
	    name:'Messages', 
	    path:'../../resources/bundle/', 
	    mode:'both'
	});
	
	if(formtitle == undefined){
		formtitle = jQuery.i18n.prop('msg_form_title'); //'My New Form';
	}

	form = new FormClass();

	if (actualForm) {
		form.fromJSON(actualForm);
	} else {
		form.title = formtitle;
		form.container = 'xmlForm';
	}
	
	form.renderForm(true);

	window.onbeforeunload = function() {
		if (warning) {
			// internationalize
			return jQuery.i18n.prop('msg_warning_onbeforeunload');
		}
	};

	var field = document
			.querySelectorAll('#components ol > li');

	for ( var i = 0; i < field.length; i++) {
		var elem = field[i];
		elem.setAttribute('draggable', 'true');
		elem.addEventListener('dragstart', function(e) {
			e.dataTransfer.effectAllowed = 'copy';
			e.dataTransfer.setData('Text', $(this)
					.find('input').attr('type'));
		});
	}

	var destiny = document.querySelector('#xmlForm');

	destiny.addEventListener('dragover', function(e) {
		if (e.preventDefault) {
			e.preventDefault();
		}
		e.dataTransfer.dropEffect = 'copy';
		return false;
	});

	destiny.addEventListener('drop', function(e) {
		// TODO this stops the redirect, I don't know why, but
		// it works.
		if (e.preventDefault) {
			e.preventDefault();
		}
		type = e.dataTransfer.getData('Text');
		var elem = fieldFactory(type);
		if (elem == null) {
			return true;
		}
		// internationalize
//		elem.title = jQuery.i18n.prop('msg_form_fieldTitle');

		form.elements.push(elem);
		form.renderForm();

		$('li#field_' + (form.elements.length - 1)).click();

		return true;
	});

	$('#fieldSave').click(function() {
		var element = form.elements[editElement];
		element.saveProperties();

		form.renderForm();
		$('li#field_' + editElement).click();
	});

	$('#fieldDelete').click(function() {
		form.elements.splice(editElement, 1);
		$('#properties').hide();
		form.renderForm();
	});

	initForm();
};

function initForm() {
	$('#xmlForm ol li').click(function() {
		$('#xmlForm ol li').removeClass('editing');
		$(this).addClass('editing');

		var id = parseInt($(this).attr('id').substr(6));
		editField(id);
	});
}

function editField(id) {
	editElement = id;
	var element = form.elements[id];

	// showing the properties
	$('#properties').show();
	$('#propertiesSpecific ul li').children().hide();
	element.showProperties();
}

// move the component up and down
function move(value) {
	$('#save').click();

	// only values -1 and 1
	if (Math.abs(value) != 1) {
		return;
	}

	if (value == 1 && editElement == form.elements.length - 1) {
		return;
	} else if (value == -1 && editElement == 0) {
		return;
	}

	var currentField = form.elements[editElement];
	var newElement = editElement + value;

	form.elements[editElement] = form.elements[newElement];
	form.elements[newElement] = currentField;
	editElement = newElement;
	
	form.renderForm();
	$('li#field_' + editElement).click();
}

function sendFormToServer(){
	var xml = form.toXML();
	sendFormAjax(xml); //a4j:jsFunction
}

function updateTitle(value){
	form.updateTitle(value);
}

function loadFormFromXML(xml){
	try{
	var xmlDoc = $.parseXML( xml );
	}catch(err){
		console.log(err);
		return;
	}
    var $xml = $( xmlDoc );
    try{
    $xml.find('questions').each(function(){
    	 var elems = this.childNodes;
    	 for(var i = 0; i<elems.length; i++){
    		 var field = fieldFactory(elems[i].nodeName);
    		 if(field!=null){
    			field.setFromXMLDoc(elems[i]);
    			form.elements.push(field);
    		 }
    	 }
    });
    form.renderForm();
    }catch(err){
    	console.log(err);
    }
}
