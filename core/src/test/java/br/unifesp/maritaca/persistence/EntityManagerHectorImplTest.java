package br.unifesp.maritaca.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

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
	public void testCreateExistsDeleteColumnFamily(){
		try {
			assertFalse(emHectorImpl.tableExists(CFTest.class));
			assertTrue(emHectorImpl.createTable(CFTest.class));
			assertTrue(emHectorImpl.tableExists(CFTest.class));
			assertTrue(emHectorImpl.dropTable(CFTest.class));
			assertFalse(emHectorImpl.tableExists(CFTest.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void testInsertGetDelete() {
		CFTest CFTest = new CFTest();
		CFTest.setKey(UUID.randomUUID());
		CFTest.setName("myname");
		try {
			emHectorImpl.persist(CFTest);
			CFTest CFTest2 = emHectorImpl.find(CFTest.getClass(), CFTest.getKey());
			assertEquals(CFTest.getKey(), CFTest2.getKey());
			assertEquals(CFTest.getName(), CFTest2.getName());
			emHectorImpl.delete(CFTest);
			assertNull(emHectorImpl.find(CFTest.getClass(), CFTest.getKey()));
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
		}
	}

}
