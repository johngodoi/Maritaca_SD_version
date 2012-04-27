package br.unifesp.maritaca.persistence.test.entitymanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.cassandra.BaseEmbededServerSetupTest;
import br.unifesp.maritaca.persistence.EntityManagerHectorImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class EntityManagerHectorImplTest extends BaseEmbededServerSetupTest {

	@Resource
	private EntityManagerHectorImpl emHectorImpl;

	@Test
	public void testCreateExistsDeleteColumnFamily() {
		assertFalse(emHectorImpl.columnFamilyExists(CFforTesting.class));
		assertTrue(emHectorImpl.createColumnFamily(CFforTesting.class));
		assertTrue(emHectorImpl.columnFamilyExists(CFforTesting.class));
		assertTrue(emHectorImpl.dropColumnFamily(CFforTesting.class));
		assertFalse(emHectorImpl.columnFamilyExists(CFforTesting.class));
	}
	
	@Test
	public void testJsonValue() {
		UUID uuid1 = new UUID(1, 2);
		UUID uuid2 = new UUID(1, 4);
		
		CFforTesting cf = new CFforTesting();
		List<UUID> uuids = new ArrayList<UUID>();
		uuids.add(uuid1);
		uuids.add(uuid2);		
		cf.setList(uuids);
		emHectorImpl.persist(cf);
		
		CFforTesting cfFound = emHectorImpl.find(cf.getClass(), cf.getKey());
		assertTrue(cfFound.getList().size()==2);
		assertTrue(cfFound.getList().get(0).equals(uuid1));
		assertTrue(cfFound.getList().get(1).equals(uuid2));
		
		emHectorImpl.delete(cf);	
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
