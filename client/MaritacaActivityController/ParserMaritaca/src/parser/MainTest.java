package parser;

import java.io.File;

public class MainTest {
	
	public static void main(String[] args) {
		File file = new File ("/home/bruno/workspace/ParserMaritaca/arquivo.xml");
		XMLParser xml = new XMLParser(file);
		xml.getQuestions();
	}
}
