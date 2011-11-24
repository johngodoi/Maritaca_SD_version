package br.unifesp.maritaca.persistence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import me.prettyprint.cassandra.BaseEmbededServerSetupTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.util.CFforTesting;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class EntityManagerHectorImplTest extends BaseEmbededServerSetupTest {

	@Resource
	private EntityManagerHectorImpl emHectorImpl;

	@Test
	public void testCreateExistsDeleteColumnFamily() {
		assertFalse(emHectorImpl.tableExists(CFforTesting.class));
		assertTrue(emHectorImpl.createTable(CFforTesting.class));
		assertTrue(emHectorImpl.tableExists(CFforTesting.class));
		assertTrue(emHectorImpl.dropTable(CFforTesting.class));
		assertFalse(emHectorImpl.tableExists(CFforTesting.class));

	}

	@Test
	public void testInsertGetDelete() {
		CFforTesting cfTest = new CFforTesting();
		cfTest.setName("myname");

		emHectorImpl.persist(cfTest);// create a CF if doesn't exists
		CFforTesting cfTest2 = emHectorImpl.find(cfTest.getClass(),
				cfTest.getKey());
		assertEquals(cfTest.getKey(), cfTest2.getKey());
		assertEquals(cfTest.getName(), cfTest2.getName());
		emHectorImpl.delete(cfTest);
		cfTest = emHectorImpl.find(cfTest.getClass(), cfTest.getKey());
		assertNull(cfTest);
	}

	@Test
	public void testListAll() {
		ArrayList<CFforTesting> mylist = new ArrayList<CFforTesting>();
		CFforTesting cfTest = new CFforTesting();
		cfTest.setName("myname");
		cfTest.setBigData("bigdata");
		emHectorImpl.persist(cfTest);
		mylist.add(cfTest);

		cfTest = new CFforTesting();
		cfTest.setName("myname");
		cfTest.setBigData("bigdata");
		emHectorImpl.persist(cfTest);
		mylist.add(cfTest);

		Collection<CFforTesting> lresult = emHectorImpl
				.listAll(CFforTesting.class);

		assertNotNull(lresult);
		assertEquals(mylist.size(), lresult.size());
		for (CFforTesting cf : lresult) {
			assertNotNull(cf.getBigData());
		}

		lresult = null;
		// minimal data
		lresult = emHectorImpl.listAll(CFforTesting.class, true);

		assertNotNull(lresult);
		assertEquals(mylist.size(), lresult.size());
		for (CFforTesting cf : lresult) {
			assertNull(cf.getBigData());
		}
	}

}
