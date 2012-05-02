package br.unifesp.maritaca.business.test.form;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.business.base.dao.FormDAO;
import br.unifesp.maritaca.business.base.dao.UserDAO;
import br.unifesp.maritaca.business.form.list.FormListerEJB;
import br.unifesp.maritaca.cassandra.BaseEmbededServerSetupTest;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class FormListerTest extends BaseEmbededServerSetupTest {
	
	//@Resource
	private EntityManagerHectorImpl emHectorImpl;	
	private FormListerEJB formListerEJB;
	
	
	//@Before
	public void setUp() {
		EntityManagerFactory.setHectorEntityManager(emHectorImpl);
		formListerEJB = new FormListerEJB();
		formListerEJB.setFormDAO(new FormDAO());
		formListerEJB.setUserDAO(new UserDAO());
		
		emHectorImpl.createColumnFamily(User.class);
		emHectorImpl.createColumnFamily(MaritacaList.class);
	}

}