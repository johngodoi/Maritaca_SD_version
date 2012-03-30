package br.unifesp.maritaca.auth;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class AnswerAuthorizationTests {
	
	@Resource
	private EntityManagerHectorImpl emHectorImpl;	
	
	private AuthorizationTestsSetUp env;
	
	@Before
	public void setUp(){
		env = new AuthorizationTestsSetUp(emHectorImpl);
	}

	@Test
	public void testFormWithAnswers(){
		
	}
	
	@Test
	public void testPublicForm(){
		
	}
}
