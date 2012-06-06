package br.unifesp.maritaca.mobile.model;

import java.io.InputStream;

import br.unifesp.maritaca.mobile.util.XMLFormParser;

import android.util.Log;

/**
 * @author Arlindo
 */
public class Model {

	private Integer currentIdQuestion;
	
	private Question[] questions;
	//XMLParser xml;

	public Model(InputStream is) {
		//this.questions 			= new XMLFormParser().getQuestions(is);
		XMLFormParser xml = new XMLFormParser(is);
		questions = xml.getQuestions();
		this.currentIdQuestion 	= 0;
	}
	
	public Question getCurrentQuestion(){
		return questions[currentIdQuestion];
	}
	
	public boolean next(){
		int currentId = currentIdQuestion;
		int nextId = questions[currentIdQuestion].getNext();
		
		if (nextId == -1){
			return false;
		}
		else{
			currentIdQuestion = nextId;
			questions[currentIdQuestion].setPrevious(currentId);
			return true;
		}
	}
	
	public void previous(){
		currentIdQuestion = questions[currentIdQuestion].getPrevious();
	}
	
	public Question[] save2() {
		String output = new String();
		for(int i = 0 ; i < questions.length; i++){
			output += i + ": " + questions[i].getValue() + "\n";
		}
		Log.i("info", output);
		return questions;
	}
	
	public Question[] save() {
		return questions;
	}
	
	public boolean validate(){
		return questions[currentIdQuestion].validate();
	}

	public boolean hasPrevious() {
		return questions[currentIdQuestion].getPrevious() != null ?  true : false;
	}

	public boolean isQuestionsEmpty() {		
		return questions != null ? (questions.length > 0 ? false : true) : true;
	}
}