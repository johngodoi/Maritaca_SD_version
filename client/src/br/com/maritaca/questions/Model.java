/**
 * 
 */
package br.com.maritaca.questions;

import java.io.InputStream;

import br.com.maritaca.parser.XMLParser;

/**
 * @author Arlindo
 *
 *
 */
public class Model {
	
	Question[] questions;
	XMLParser xml;
	
	int size;
	int current;
	int last;

	/**
	 * 
	 */
	public Model(InputStream is) {
		xml = new XMLParser(is);
		questions = xml.getQuestions();
		size = questions.length;
		last = size - 1;
		current = 0;
	}
	
	public Question getCurrent(){
		return questions[current];
	}
	
	public void next(){
		questions[questions[current].getNext()].setPrevious(current);
		current = questions[current].getNext();
	}
	
	public void previous(){
		current = questions[current].getPrevious();
	}
	
	public void save(){
		//TODO Walkirya
	}
	
	public boolean validate(){
		//questions[current].validate();
		return true; //TODO
	}

	public boolean hasNext() {
		if (questions[current].getNext() != null){
			return true;
		}	
		return false;
	}
	
	public boolean hasPrevious() {
		if (questions[current].getPrevious() != null){
			return true;
		}	
		return false;
	}

	public int getSize() {
		return size;
	}
}
