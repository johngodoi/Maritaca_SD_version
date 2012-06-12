package br.unifesp.maritaca.persistence.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import br.unifesp.maritaca.persistence.entity.QuestionAnswer;

public class MaritacaEntityUtils {
	public static List<QuestionAnswer> parseStringToAnswerList(String value) {
		if(value == null || !(value.contains("id") && value.contains("value"))) {
			return null;
		}
		List<QuestionAnswer> questions = new ArrayList<QuestionAnswer>();
		String questionStr[] = value.split("},");
		questionStr[0] = questionStr[0].substring(1);
		for(int i = 0; i < questionStr.length; i++) {
			String attributes[] = questionStr[i].split("value=");
			QuestionAnswer question = new QuestionAnswer();
			String idStr = attributes[0].trim();
			question.setId(idStr.substring(4, idStr.length() - 1));
			String valueStr = attributes[1].trim();
			String resultValue = i < questionStr.length - 1 ? valueStr : valueStr.substring(0, valueStr.length() - 2);
			question.setValue(resultValue);
			questions.add(question);
		}
		return questions;
	}

	public static String compress(String expression) {
		//TODO ver meio de adicionar essa funcionalidade na persistência de imagens
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream(expression.length());
			GZIPOutputStream gos;
			gos = new GZIPOutputStream(os);
			gos.write(expression.getBytes());
			gos.close();
			byte[] compressed = os.toByteArray();
			os.close();
			return bytesToStringUTF(compressed);
		} catch (IOException e) {
			return expression;
		}
	}


	public static String decompress(String expression) {
		//TODO ver meio de adicionar essa funcionalidade na persistência de imagens
		try {
			byte[] bytearray = stringToBytesUTF(expression);
			final int BUFFER_SIZE = 32;
			ByteArrayInputStream is = new ByteArrayInputStream(bytearray);
			GZIPInputStream gis;
			gis = new GZIPInputStream(is, BUFFER_SIZE);
			StringBuilder string = new StringBuilder();
			byte[] data = new byte[BUFFER_SIZE];
			int bytesRead;
			while ((bytesRead = gis.read(data)) != -1) {
				string.append(new String(data, 0, bytesRead));
			}
			gis.close();
			is.close();
			return string.toString();
		} catch (IOException e) {
			return expression;
		}
	}

	private static String bytesToStringUTF(byte[] bytes) {
		char[] buffer = new char[bytes.length >> 1];
		for(int i = 0; i < buffer.length; i++) {
			int bpos = i << 1;
			char c = (char)(((bytes[bpos]&0x00FF)<<8) + (bytes[bpos+1]&0x00FF));
			buffer[i] = c;
		}
		return new String(buffer);
	}

	private static byte[] stringToBytesUTF(String str) {
		char[] buffer = str.toCharArray();
		byte[] b = new byte[buffer.length << 1];
		for(int i = 0; i < buffer.length; i++) {
			int bpos = i << 1;
			b[bpos] = (byte) ((buffer[i]&0xFF00)>>8);
			b[bpos + 1] = (byte) (buffer[i]&0x00FF);
		}
		return b;
	}
}
