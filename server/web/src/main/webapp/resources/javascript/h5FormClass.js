var FormClass = function() {
	this.title = '';
	this.container = '';
	this.elements = new Array();
	
	this.renderForm = function(deleteAlert) {
		var html = '<legend>' + this.title + '</legend>';
			html += '<ol>';
		for(var i in this.elements){
			html += '<li id="field_' + i + '">';
			html += this.elements[i].toHTML(i);
			html += '</li>';
		}
		html += '</ol>';
		
		$('#' + this.container).html(html);
		
		initForm();
		
		if(warning != undefined && deleteAlert != true) {
			warning = true;
		}
	};
	
	this.toXML = function() {
		var usrId = $('.loggedUserPanel a').first().text();
		var xml = '<?xml version="1.0" encoding="UTF-8"?>';
			xml += '<form>';
			xml += '<userid>'+ usrId + '</userid>';
			xml += '<formid>'+ new Date().getTime()+'</formid>';
			xml += '<title>' + this.title + '</title>';
			xml += '<questions>';

			xml+= '<timestamp id="0" show="';
			if($('#timestampCheck').attr('checked') == 'checked')
				xml += 'yes';
			else
				xml += 'no';
			xml += '"></timestamp>';
			
			xml+= '<location id="1" show="';
			if($('#locationCheck').attr('checked') == 'checked')
				xml += 'yes';
			else
				xml += 'no';
			xml += '"></location>';
			
			for(var i in this.elements) {
				this.elements[i].id		= parseInt(i)+2;
				// add the next_id value
				if(i < (this.elements.length-1))
					this.elements[i].next	= parseInt(i)+1;
				else 
					this.elements[i].next	= -1;
				
				xml += this.elements[i].toXML(i);
			}
		xml += '</questions>';
		xml += '</form>';
		return xml;
	};
	
	this.toJSON = function() {
		return '{"title":"' + this.title + '","container":"' + this.container +	
				'","elements":' + JSON.stringify(this.elements) + '}';
	};
	
	this.fromJSON = function(json) {
		var jsonObject = JSON.parse(json);
		this.title = jsonObject.title;
		this.container = jsonObject.container;
		for(var i in jsonObject.elements) {
			var elem = jsonObject.elements[i];
			var element = fieldFactory(elem.type);
			if (element == null) {
				return true;
			}
			element.setJSONValues(elem);
			
			this.elements.push(element);
		}
	};
	
	this.updateTitle = function(newtitle){
		this.title = newtitle;
		$('#' + this.container + ' legend').html(newtitle);
	};
};