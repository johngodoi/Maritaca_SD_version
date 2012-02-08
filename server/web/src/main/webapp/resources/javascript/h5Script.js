var editElement = null;
var form = null;
var warning = false;
var actualForm = localStorage['actualForm'];

// This will initialize the plugin jQuery.i18n
// The file properties is reference by path
jQuery.i18n.properties({
    name:'Messages', 
    path:'../../resources/bundle/', 
    mode:'both',
});

$(document).ready(function() {

	form = new FormClass();

	if (actualForm) {
		form.fromJSON(actualForm);
	} else {
		// internationalize
		form.title = jQuery.i18n.prop('msg_form_title'); //'My New Form';
		form.container = 'xmlForm';
	}

	$('#titleForm').val(form.title);
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
		elem.title = jQuery.i18n.prop('msg_form_fieldTitle');

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
});

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
	$('#propertiesSpecific').children('ul li').hide();
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
