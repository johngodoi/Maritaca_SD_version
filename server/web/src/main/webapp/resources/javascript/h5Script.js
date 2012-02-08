var editElement = null;
var form = null;
var warning = false;
var actualForm = localStorage['actualForm'];

$(document).ready(function() {

	form = new FormClass();

	if (actualForm) {
		form.fromJSON(actualForm);
	} else {
		// TODO internationalize
		form.title = 'My New Form';
		form.container = 'xmlForm';
	}

	$('#titleForm').val(form.title);
	form.renderForm(true);

	window.onbeforeunload = function() {
		if (warning) {
			// TODO internationalize
			return "Changes will be lost if you quit \n\n"
					+ "To save the work before to quit, click in Save, "
					+ "in section Form Options";
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
		// TODO internationalize
		elem.title = 'Field title';

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
