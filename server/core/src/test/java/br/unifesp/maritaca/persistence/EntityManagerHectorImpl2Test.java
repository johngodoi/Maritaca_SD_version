package br.unifesp.maritaca.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.unifesp.maritaca.util.CFforTesting;

public class EntityManagerHectorImpl2Test {
	EntityManagerHectorImpl em;

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	@Ignore
	public void test() {
		CFforTesting cfTest = new CFforTesting();
		cfTest.setName("myname");

		em.persist(cfTest);// create table if doesn't exists
		CFforTesting cFTest2 = em.find(cfTest.getClass(), cfTest.getKey());
		assertEquals(cfTest.getKey(), cFTest2.getKey());
		assertEquals(cfTest.getName(), cFTest2.getName());
		cfTest = em.find(cfTest.getClass(), cfTest.getKey());
		assertNull(cfTest.getName());

	}

}
