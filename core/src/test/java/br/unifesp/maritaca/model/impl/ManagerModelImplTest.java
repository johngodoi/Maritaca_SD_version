package br.unifesp.maritaca.model.impl;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import javax.annotation.Resource;

import me.prettyprint.cassandra.BaseEmbededServerSetupTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Configuration;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.GroupUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class ManagerModelImplTest extends BaseEmbededServerSetupTest {

	@Resource
	private EntityManagerHectorImpl emHectorImpl;
	private EntityManager em;
	private ManagerModelImpl manModel;
	@SuppressWarnings("rawtypes")
	private Class[] classes = { Answer.class, Form.class, Configuration.class,
			FormPermissions.class, Group.class, GroupUser.class, User.class };

	@Before
	public void setUp() throws Exception {
		em = emHectorImpl;
		manModel = new ManagerModelImpl();
		manModel.setEntityManager(em);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void initMaritaca() {
		manModel.initMaritaca(new HashMap<String, String>());
		for (@SuppressWarnings("rawtypes")
		Class cl : classes) {
			assertTrue(em.tableExists(cl));
		}
	}

}
