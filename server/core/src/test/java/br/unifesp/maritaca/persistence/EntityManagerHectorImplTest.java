package br.unifesp.maritaca.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import me.prettyprint.cassandra.BaseEmbededServerSetupTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.unifesp.maritaca.util.CFTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-context-test-v2.xml")
public class EntityManagerHectorImplTest extends BaseEmbededServerSetupTest {

	@Resource
	private EntityManagerHectorImpl emHectorImpl;

	@Test
	public void testCreateExistsDeleteColumnFamily() {
		assertFalse(emHectorImpl.tableExists(CFTest.class));
		assertTrue(emHectorImpl.createTable(CFTest.class));
		assertTrue(emHectorImpl.tableExists(CFTest.class));
		assertTrue(emHectorImpl.dropTable(CFTest.class));
		assertFalse(emHectorImpl.tableExists(CFTest.class));

	}

	@Test
	public void testInsertGetDelete() {
		CFTest cfTest = new CFTest();
		cfTest.setName("myname");

		emHectorImpl.persist(cfTest);// create a CF if doesn't exists
		CFTest cfTest2 = emHectorImpl.find(cfTest.getClass(), cfTest.getKey());
		assertEquals(cfTest.getKey(), cfTest2.getKey());
		assertEquals(cfTest.getName(), cfTest2.getName());
		emHectorImpl.delete(cfTest);
		cfTest = emHectorImpl.find(cfTest.getClass(), cfTest.getKey());
		assertNull(cfTest);

	}

}
