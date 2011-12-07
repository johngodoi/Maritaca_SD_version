var elemento_em_edicao = null;
var form = null;
var warning = false;
var form_atual = localStorage['form_atual'];

$(document).ready(function() {

	//Instancia o formulário
	form = new FormClass();

	if(form_atual) {
		form.fromJSON(form_atual);
	}
	else {
		form.titulo = 'Meu formulário';
		form.container = "formulario";	
	}

	$('#titulo_form').val(form.titulo);
	form.desenhar(true);
	
	window.onbeforeunload = function() { 
		if (warning)
			return "As alterações feitas serão perdidas se você deixar esta página. \n\n\
					Para salvar o trabalho antes de sair, clique em \"Gravar\", na seção \"Opções do Formulário\".";
	}

	var campos = document.querySelectorAll('#elementos ol > li');

	for (var i = 0; i < campos.length; i++) {
		var el = campos[i];
		el.setAttribute('draggable', 'true');
		el.addEventListener('dragstart', function (e) {
			e.dataTransfer.effectAllowed = 'copy'; // only dropEffect='copy' will be dropable
			e.dataTransfer.setData('Text', $(this).find('input').attr('type')); // required otherwise doesn't work
		});
	}

	var destino = document.querySelector('#formulario');

	destino.addEventListener('dragover', function (e) {
		if (e.preventDefault) e.preventDefault(); // allows us to drop
		e.dataTransfer.dropEffect = 'copy';
		return false;
	});

	destino.addEventListener('drop', function (e) {
		if (e.stopPropagation) e.stopPropagation(); // stops the browser from redirecting...why???
		
		var elem = new FieldClass();
		elem.tipo = e.dataTransfer.getData('Text');
		elem.titulo = 'Título do campo';
		switch(elem.tipo){
			case 'radio':
			case 'checkbox':
				var opcao = new OptionClass();
				opcao.rotulo = 'Primeira opção';
				elem.opcoes.push(opcao);
				break;
		}

		form.elementos.push(elem);
		form.desenhar();

		$('li#campo_' + (form.elementos.length-1)).click();

		return false;
	});

	$('#gravar').click(function() {
		var elemento = form.elementos[elemento_em_edicao];

		elemento.titulo = $('#campo_titulo').val();
		elemento.nome = $('#campo_nome').val();
		elemento.obrigatorio = $('#campo_obrigatorio_sim').attr('checked');
		elemento.ajuda = $('#campo_ajuda').val();

		switch(elemento.tipo){
			case 'text':
				elemento.padrao = $('#campo_padrao').val();
				elemento.tamanho = $('#campo_tamanho').val();
				elemento.numerico = $('#campo_numerico_sim').attr('checked');
				break;
			case 'radio':
			case 'checkbox':
				elemento.opcoes = new Array();
				$('#ol_opcoes li').each(function() {
					var opcao = new OptionClass();
					opcao.rotulo = $(this).find('.campo_rotulo').val();
					opcao.valor = $(this).find('.campo_valor').val();
					elemento.opcoes.push(opcao);
				});
				break;
		}

		form.desenhar();
		$('li#campo_' + elemento_em_edicao).click();
	});

	$('#excluir').click(function() {
		form.elementos.splice(elemento_em_edicao, 1);
		$('#propriedades').hide();
		form.desenhar();
	});

	$('#gravar_form').click(function() {
		form.titulo = $('#titulo_form').val();
		form.desenhar();
		$('li#campo_' + elemento_em_edicao).click();
		localStorage['form_atual'] = form.toJSON();
		warning = false;
	});

	$('#limpar_form').click(function() {
		form = new FormClass();

		$('#titulo_form').val("Meu formulário");

		form.titulo = $('#titulo_form').val();
		form.container = "formulario";
		$('#propriedades').hide();
		form.desenhar();

		initFormulario();
	});

	$('#gerar_xml').click(function() {
		alert("here");
		var xml = form.toXML();
		$.ajax({
			type: 'POST',
			url: $('#url_envio').val(),
			data: {'xml': xml},
			success: function(d){
				if(d == '1') //Se voltar 1, o envio foi feito com sucesso
					alert('Formulário enviado com sucesso!');
				else //Caso contrario, algo deu errado
					alert('O servidor de destino recusou o envio. \n\nPor favor, tente novamente.')
			},
			error: function(){
				alert('Ocorreu um erro ao enviar o XML: \n\n' + xml);
			}
		});
	});

	initFormulario();

});

function initFormulario() {
	//Cria listener para clique nos campos
	$('#formulario ol li').click(function() {
		$('#formulario ol li').removeClass('em_edicao');
		$(this).addClass('em_edicao');
		
		var id = parseInt($(this).attr('id').substr(6));
		editaCampo(id);
	});

	//Torna os campos numéricos apenas
	$('input[type=text].numerico').numeric();
}

function editaCampo(id) {
	elemento_em_edicao = id;
	var elemento = form.elementos[id];
	
	$('#propriedades').show().children('div').hide();
	$('#campo_titulo').val(elemento.titulo);
	$('#campo_nome').val(elemento.nome);
	$('#campo_obrigatorio_sim').attr('checked', elemento.obrigatorio);
	$('#campo_obrigatorio_nao').attr('checked', !elemento.obrigatorio);
	$('#campo_ajuda').val(elemento.ajuda);

	switch(elemento.tipo){
		case 'text':
			$('#propriedades_text').show();
			$('#campo_padrao').val(elemento.padrao);
			$('#campo_tamanho').val(elemento.tamanho);
			$('#campo_numerico_sim').attr('checked', elemento.numerico);
			$('#campo_numerico_nao').attr('checked', !elemento.numerico);
			break;
		case 'radio':
		case 'checkbox':
			$('#propriedades_option').show();
			var html = '';
			for(var i in elemento.opcoes) {
				var opcao = elemento.opcoes[i];
				html += '<li>';
				html += '<label>Rótulo: <input type="text" class="campo_rotulo" value="' + opcao.rotulo + '" /></label>';
				html += '<label>Valor: <input type="text" class="campo_valor" value="' + opcao.valor + '" /></label>';
				html += '<label>Ordem: <a href="#" onclick="subir_opcao(' + i + '); return false;"><img src="img/arrow_up.png" alt="subir" /></a> <a href="#" onclick="descer_opcao(' + i + '); return false;"><img src="img/arrow_down.png" alt="descer" /></label></a>';
				html += '</li>';
			}
			$('#ol_opcoes').html(html);
			break;
	}

}

function subir() {
	$('#gravar').click();
	
	if(elemento_em_edicao == 0)
		return;
	
	var campo_atual = form.elementos[elemento_em_edicao];
	var novo_elemento = elemento_em_edicao-1;
	
	form.elementos[elemento_em_edicao] = form.elementos[novo_elemento];
	form.elementos[novo_elemento] = campo_atual;

	elemento_em_edicao = novo_elemento;
	form.desenhar();
	$('li#campo_' + elemento_em_edicao).click();
}

function descer() {
	$('#gravar').click();
	
	if(elemento_em_edicao == form.elementos.length-1)
		return;

	var campo_atual = form.elementos[elemento_em_edicao];
	var novo_elemento = elemento_em_edicao+1;

	form.elementos[elemento_em_edicao] = form.elementos[novo_elemento];
	form.elementos[novo_elemento] = campo_atual;

	elemento_em_edicao = novo_elemento;
	form.desenhar();
	$('li#campo_' + elemento_em_edicao).click();
}

function subir_opcao(opcao) {
	$('#gravar').click();
	
	if(opcao == 0)
		return;

	var elemento = form.elementos[elemento_em_edicao];
	var opcao_atual = elemento.opcoes[opcao];
	var nova_opcao = opcao-1;

	elemento.opcoes[opcao] = elemento.opcoes[nova_opcao];
	elemento.opcoes[nova_opcao] = opcao_atual;

	form.desenhar();
	$('li#campo_' + elemento_em_edicao).click();
}

function descer_opcao(opcao) {
	$('#gravar').click();
	
	var elemento = form.elementos[elemento_em_edicao];

	if(opcao == elemento.opcoes.length-1)
		return;
	
	var opcao_atual = elemento.opcoes[opcao];
	var nova_opcao = opcao+1;

	elemento.opcoes[opcao] = elemento.opcoes[nova_opcao];
	elemento.opcoes[nova_opcao] = opcao_atual;

	form.desenhar();
	$('li#campo_' + elemento_em_edicao).click();
}

function acrescentar() {
	$('#gravar').click();
	
	var elemento = form.elementos[elemento_em_edicao];
	var opcao = new OptionClass();
	opcao.rotulo = 'Outra opção';
	elemento.opcoes.push(opcao);

	form.desenhar();
	$('li#campo_' + elemento_em_edicao).click();
}

function remover() {
	$('#gravar').click();
	
	var elemento = form.elementos[elemento_em_edicao];
	if(elemento.opcoes.length <= 1)
		return;
	
	elemento.opcoes.pop();

	form.desenhar();
	$('li#campo_' + elemento_em_edicao).click();
}