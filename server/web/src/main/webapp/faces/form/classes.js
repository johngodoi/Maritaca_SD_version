//Classe do formulário que conterá os campos
var FormClass = function() {
	this.titulo = '';				//Título que será mostrado no topo do formulário
	this.container = ''; 			//ID do elemento que contera o form
	this.elementos = new Array();	//Elementos do form, da classe InputClass

	//Desenha o formulário no container selecionado
	this.desenhar = function(suprime_alerta) {
		var html = '<legend>' + this.titulo + '</legend>';
			html += '<ol>';
			for(var i in this.elementos){
				html += '<li id="campo_' + i + '">';
				html += this.elementos[i].toHTML();
				html += '</li>';
			}
			html += '</ol>';
		//Adiciona ao container selecionado
		$('#' + this.container).html(html);

		//Reinicializa os handlers
		initFormulario();
		
		//Alerta o usuário sobre perda de dados
		if(warning != undefined && suprime_alerta != true)
			warning = true;
	}

	//Gera um XML com os campos do formulário
	this.toXML = function() {
		var xml = '<?xml version="1.0" encoding="UTF-8"?>';
		xml += '\n<ficha>';
		xml += '\n<nome>' + this.titulo + '</nome>';
		for(var i in this.elementos){
			xml += this.elementos[i].toXML(i);
		}
		xml += '\n</ficha>';
		return xml;
	}

	//Retorna o elemento em formato JSON para ser amazenado no localStorage
	this.toJSON = function() {
		return '{"titulo":"'+this.titulo+'","container":"'+this.container+'","elementos":'+JSON.stringify(this.elementos)+'}';
	}

	//Define as propriedades com base em uma string JSON
	this.fromJSON = function(json) {
		var obj = JSON.parse(json);
		this.titulo = obj.titulo;
		this.container = obj.container;
		for(var i in obj.elementos) {
			var elem = obj.elementos[i];
			var elemento = new FieldClass();
			elemento.nome = elem.nome;
			elemento.tipo = elem.tipo;
			elemento.obrigatorio = elem.obrigatorio;
			elemento.numerico = elem.numerico;
			elemento.tamanho = elem.tamanho;
			elemento.titulo = elem.titulo;
			elemento.padrao = elem.padrao;
			elemento.ajuda = elem.ajuda;
			for(var j in elem.opcoes) {
				var opc = elem.opcoes[j];
				var opcao = new OptionClass();
				opcao.rotulo = opc.rotulo;
				opcao.valor = opc.valor;
				elemento.opcoes.push(opcao);
			}
			this.elementos.push(elemento);
		}
	}
}

//Classe dos campos que serão adicionados
var FieldClass = function() {
	this.nome = '';				//Nome do elemento na página (name)
	this.tipo = 'text'; 		//Pode ser text, checkbox ou radio
	this.opcoes = new Array(); 	//Vetor de opções para Checkbox ou Option
	this.obrigatorio = false; 	//TRUE ou FALSE para se o preenchimento eh obrigatorio
	this.numerico = false;		//TRUE ou FALSE para se é numerico (apenas para tipo 'text')
	this.tamanho = 100;			//Tamanho máximo do campo, aplicável para tipo 'text'
	this.titulo = '';			//Rótulo do campo do formulário
	this.padrao = '';			//Valor padrão do campo
	this.ajuda = '';			//Texto de ajuda para o preenchimento

	//Método para renderizar um campo
	this.toHTML = function() {
		var retorno = '';
		switch(this.tipo){
			case 'text':
					retorno += '<label class="label_campo" for="campo_' + this.id + '">';
					retorno += this.titulo;
					if(this.obrigatorio)
						retorno += ' <span class="required">*</span>';
					retorno += '</label>';
					retorno += '<div class="ordem"><a href="#" onclick="subir(); return false;"><img src="img/arrow_up.png" alt="subir" /></a> <a href="#" onclick="descer(); return false;"><img src="img/arrow_down.png" alt="descer" /></a></div>';
					retorno += '<input type="text" ';
					retorno += 'id="campo_' + this.id + '" ';
					retorno += 'maxlength="' + this.tamanho + '" ';
					if(this.numerico)
						retorno += 'class="numerico" ';
					if(this.padrao)
						retorno += 'value="' + this.padrao + '" ';
				break;
			case 'checkbox':
			case 'radio':
					retorno += '<label class="label_campo">';
					retorno += this.titulo;
					if(this.obrigatorio)
						retorno += ' <span class="required">*</span>';
					retorno += '</label>';
					retorno += '<div class="ordem"><a href="#" onclick="subir(); return false;"><img src="img/arrow_up.png" alt="subir" /></a> <a href="#" onclick="descer(); return false;"><img src="img/arrow_down.png" alt="descer" /></a></div>';
					for(var i in this.opcoes){
						var opcao = this.opcoes[i];
						retorno += '<label><input type="' + this.tipo + '" name="' + this.nome + '" ';
						if(this.padrao == opcao.valor)
							retorno += 'checked="checked" ';
						retorno += '/>';
						retorno += opcao.rotulo;
						retorno += '</label>';
					}
					
				break;
		}
		return retorno;
	}

	//Gera um XML com os dados do campo
	this.toXML = function(ordem) {
		var xml = '\n<campo ';
		switch(this.tipo){
			case 'text':
				xml += 'tipo="texto" ';
				break;
			case 'checkbox':
				xml += 'tipo="opcao_multipla" ';
				break;
			case 'radio':
				xml += 'tipo="opcao_unica" ';
				break;
		}
		xml += 'nome="' + this.nome + '" ';
		xml += 'obrigatorio="' + (this.obrigatorio?'sim':'nao') + '" ';
		xml += 'ordem="' + ordem + '"';
		xml += '>';

		xml += '\n<rotulo>' + this.titulo + '</rotulo>';
		xml += '\n<ajuda>' + this.ajuda + '</ajuda>';

		switch(this.tipo){
			case 'text':
				xml += '\n<tipo_dados>' + (this.numerico?'numerico':'alfanumerico') + '</tipo_dados>';
				xml += '\n<tamanho>' + this.tamanho + '</tamanho>';
				break;
			case 'checkbox':
			case 'radio':
				xml += '\n<itens>';
				for(var i in this.opcoes){
					var opcao = this.opcoes[i];
					xml += '\n\t<item valor="' + opcao.valor + '" ordem="' + i + '">' + opcao.rotulo + '</item>';
				}
				xml += '\n</itens>';
				break;
		}

		xml += '\n<valor_padrao>' + this.padrao + '</valor_padrao>';

		xml += '\n</campo>';
		return xml;
	}
	
}

//Classe para as opções
var OptionClass = function() {
	this.rotulo = '';			//Texto da opção
	this.valor = '';			//Valor da opção
}