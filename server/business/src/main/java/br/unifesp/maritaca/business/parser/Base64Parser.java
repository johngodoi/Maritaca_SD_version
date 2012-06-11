package br.unifesp.maritaca.business.parser;

import javax.xml.bind.DatatypeConverter;

public class Base64Parser {

	public byte[] decodeBase64CodeToByteArray(String base64Code) {
		return DatatypeConverter.parseBase64Binary(base64Code);
	}
}
