var FormClass = function() {
	this.title = '';
	this.container = '';
	this.elements = new Array();
	
	this.renderForm = function(deleteAlert) {
		var html = '<legend>' + this.title + '</legend>';
			html += '<ol>';
		for(var i in this.elements){
			html += '<li id="field_' + i + '">';
			html += this.elements[i].toHTML();
			html += '</li>';
		}
		html += '</ol>';
		
		$('#' + this.container).html(html);
		
		initForm();
		
		if(warning != undefined && deleteAlert != true) {
			warning = false;
		}
	};
	
	this.toXML = function() {
		var xml = '<?xml version="1.0" encoding="UTF-8"?>';
			xml += '\n<form>';
			xml += '\n<title>' + this.title + '<\title>';
			xml += '\n<questions>';
		for(var i in this.elements) {
			xml += this.elements[i].toXML(i);
		}
		xml += '\n</questions>';
		xml += '\n</form>';
		return xml;
	};
	
	this.toJSON = function() {
		return '{"label":"' + this.title + '","container":"' + this.container +	
				'","elements":' + JSON.stringify(this.elements) + '}';
	};
	
	this.fromJSON = function(json) {
		var jsonObject = JSON.parse(json);
		this.title = jsonObject.title;
		this.container = jsonObject.container;
		for(var i in jsonObject.elements) {
			var elem = jsonObject.elements[i];
			var element = new Field();
			element.help = elem.help;
			
			this.elements.push(element);
		}
	};
};