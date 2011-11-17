package br.unifesp.maritaca.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.unifesp.maritaca.util.CFTest;

public class EntityManagerHectorImpl2Test {
	EntityManagerHectorImpl em;
	

	@Before
	public void setUp() throws Exception {
		Cluster cl = HFactory.getOrCreateCluster("test", "localhost:9160");
		Keyspace k = HFactory.createKeyspace("Keyspace1", cl);
		HashMap params = new HashMap();
		params.put("cluster", cl);
		params.put("keyspace", k);
		em = (EntityManagerHectorImpl) EntityManagerFactory.getInstance()
				.createEntityManager(EntityManagerFactory.HECTOR_MARITACA_EM,
						params);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		CFTest cFTest = new CFTest();
		cFTest.setName("myname");
		
		try {
			em.createTable(CFTest.class);
			em.persist(cFTest);
			CFTest cFTest2 = em.find(cFTest.getClass(), cFTest.getKey());
			assertEquals(cFTest.getKey(), cFTest2.getKey());
			assertEquals(cFTest.getName(), cFTest2.getName());
			//em.delete(CFTest);
			//assertNull(em.find(CFTest.getClass(), CFTest.getKey()));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (SecurityException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (InstantiationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void testCreateExistsDeleteColumnFamily(){
//		try {
//			//assertFalse(em.tableExists(CFTest.class));
//			//assertTrue(em.createTable(CFTest.class));
//			//assertTrue(em.tableExists(CFTest.class));
//			//assertTrue(em.dropTable(CFTest.class));
//			//assertFalse(em.tableExists(CFTest.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//		
//	}
//
}
