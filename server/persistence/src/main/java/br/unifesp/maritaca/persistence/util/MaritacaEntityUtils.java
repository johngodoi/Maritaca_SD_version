package br.unifesp.maritaca.persistence.util;

import java.util.ArrayList;
import java.util.List;

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
			String resultValue = i < questionStr.length - 1 ? valueStr : valueStr.substring(0, valueStr.length() - 1);
			question.setValue(resultValue);
			questions.add(question);
		}
		return questions;
	}
}
