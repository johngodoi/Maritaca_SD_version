package br.unifesp.maritaca.persistence.entitymanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.persistence.entity.Answer;
import br.unifesp.maritaca.persistence.entity.QuestionAnswer;

import com.google.common.io.Files;

public class EntityManagerFactoryAnswerTest {

	private Answer answer;
	private EntityManager entityManager = this.getEntityManager();
	private final UUID answerId = UUID.fromString("35a978d0-ca03-4c58-94f5-8bae9dbfc17b");

	
	@Test
	public void testPersistAnswer() {	
		answer = createAnswer();
		Assert.assertTrue(this.entityManager.persist(answer));
	}
	
	@Test
	public void testFindAnswer() {
		this.answer = this.findAnswerByUUID();
		Assert.assertNotNull(this.answer);
		Assert.assertEquals(this.answer.getKey(), this.answerId);
		Assert.assertNotNull(this.answer.getCreationDate());
		Assert.assertNotNull(this.answer.getForm());
		Assert.assertNotNull(this.answer.getUser());
		Assert.assertNotNull(this.answer.getQuestions());
		Assert.assertEquals(6, this.answer.getQuestions().size());
	}

	private Answer findAnswerByUUID() {
		Answer answer = entityManager.find(Answer.class, this.answerId);
		return answer;
	}

	private Answer createAnswer() {
		this.answer = new Answer();
		this.answer.setKey(this.answerId);
		this.answer.setCreationDate(System.currentTimeMillis());
		this.answer.setForm(UUID.fromString("cbd88e90-a5e0-11e1-b177-005056c00008"));
		this.answer.setUser(UUID.fromString("00bbd2e0-a5ad-11e1-915a-005056c00008"));
		
		List<QuestionAnswer> answers = new ArrayList<QuestionAnswer>();
		QuestionAnswer answer1 = new QuestionAnswer();
		answer1.setId("1");
		answer1.setValue("eeessscvcvx");
		
		QuestionAnswer answer2 = new QuestionAnswer();
		answer2.setId("2");
		answer2.setValue(this.getImageXmlForTest());

		QuestionAnswer answer3 = new QuestionAnswer();
		answer3.setId("3");
		answer3.setValue(this.getImageXmlForTest());
		
		QuestionAnswer answer4 = new QuestionAnswer();
		answer4.setId("4");
		answer4.setValue(this.getImageXmlForTest());
		
		QuestionAnswer answer5 = new QuestionAnswer();
		answer5.setId("5");
		answer5.setValue(this.getImageXmlForTest());

		QuestionAnswer answer6 = new QuestionAnswer();
		answer6.setId("6");
		answer6.setValue("ndosandoqsa");
		
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);
		answers.add(answer4);
		answers.add(answer5);
		answers.add(answer6);
		
		answer.setQuestions(answers);
		return answer;
	}

	private EntityManager getEntityManager() {
		EntityManagerFactory instance = EntityManagerFactory.getInstance();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("cluster", "localhost:9160");
		params.put("keyspace", "Maritaca");
		
		EntityManager entityManager = instance.createEntityManager(EntityManagerFactory.HECTOR_MARITACA_EM, params);
		return entityManager;
	}	
	
	private String getImageXmlForTest() {
		try {
			File source = new File("src/test/resources/imagem.xml");
			String string = Files.toString(source, Charset.defaultCharset());
			return string;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
