package br.unifesp.maritaca.business.test.answer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.business.answer.edit.AnswerEditorDAO;
import br.unifesp.maritaca.business.answer.edit.AnswerEditorEJB;
import br.unifesp.maritaca.business.answer.edit.dto.AnswerListDTO;
import br.unifesp.maritaca.business.answer.edit.dto.AnswerWSDTO;
import br.unifesp.maritaca.business.answer.edit.dto.DataCollectedDTO;
import br.unifesp.maritaca.business.answer.edit.dto.QuestionAnswerDTO;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;
import br.unifesp.maritaca.persistence.cassandra.BaseEmbededServerSetupTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class SaveAnswerTest  extends BaseEmbededServerSetupTest {

	@Resource
	private EntityManagerHectorImpl emHectorImpl;
	
	private AnswerEditorEJB         answEditorEjb;
	
	private String uuid = "111dea60-146e-11e1-a7c0-d2b70b6d4d67";
	
	@Before
	public void setUpTets(){
		EntityManagerFactory.setHectorEntityManager(emHectorImpl);
		
		answEditorEjb = new AnswerEditorEJB();
		
		AnswerEditorDAO answEditorDao = new AnswerEditorDAO();
		
		answEditorEjb.setAnswerEditorDAO(answEditorDao);
	}
	
	@Test
	public void saveAnswerTest(){
		DataCollectedDTO        collectedDTO = new DataCollectedDTO();
		AnswerListDTO           answListDTO  = new AnswerListDTO();
		
		List<AnswerWSDTO>       answers      = new ArrayList<AnswerWSDTO>();		
		AnswerWSDTO             answWsDto    = new AnswerWSDTO();

		List<QuestionAnswerDTO> questionAnsw = new ArrayList<QuestionAnswerDTO>();
		QuestionAnswerDTO       qAnsw1       = new QuestionAnswerDTO();
		
		/*
		qAnsw1.setId("0");
		qAnsw1.setValue("Simple answer");
		
		questionAnsw.add(qAnsw1);
		answWsDto.setQuestions(questionAnsw);
		
		answers.add(answWsDto);
		answListDTO.setAnswers(answers);		
		
		collectedDTO.setAnswerList(answListDTO);
		
		collectedDTO.setFormId(uuid);
		collectedDTO.setUserId(uuid);
							
		answEditorEjb.saveAnswers(collectedDTO);
		*/						
	}

}
