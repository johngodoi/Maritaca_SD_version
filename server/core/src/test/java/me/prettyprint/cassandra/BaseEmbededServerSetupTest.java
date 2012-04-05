package me.prettyprint.cassandra;

import java.io.IOException;
import java.util.Arrays;

import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.testutils.EmbeddedServerHelper;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for test cases that need access to EmbeddedServerHelper
 * 
 * @author Nate McCall (nate@vervewireless.com)
 * 
 */
public abstract class BaseEmbededServerSetupTest {
	private static Logger log = LoggerFactory
			.getLogger(BaseEmbededServerSetupTest.class);
	private static EmbeddedServerHelper embedded;

	protected HConnectionManager connectionManager;
	protected CassandraHostConfigurator cassandraHostConfigurator;
	protected String clusterName  = "TestCluster";
	protected String keyspaceName = "Keyspace1";
	protected String dabaseAddr   = "127.0.0.1:9170";

	/**
	 * Set embedded cassandra up and spawn it in a new thread.
	 * 
	 * @throws TTransportException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@BeforeClass
	public static void setup() throws TTransportException, IOException,
			InterruptedException, ConfigurationException {
		log.info("in setup of BaseEmbedded.Test");
		embedded = new EmbeddedServerHelper();
		embedded.setup();
	}

	@AfterClass
	public static void teardown() throws IOException {
		EmbeddedServerHelper.teardown();
		embedded = null;
	}

	protected void setupClient() {
		cassandraHostConfigurator = new CassandraHostConfigurator(dabaseAddr);
		connectionManager = new HConnectionManager(clusterName,cassandraHostConfigurator);
	}

	public void cleanUp() {
		Cluster cl = HFactory.getOrCreateCluster(clusterName, dabaseAddr);
		cl.dropKeyspace(keyspaceName);
		
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(keyspaceName,                              
                															 "ColumnFamilyName", 
                															 ComparatorType.BYTESTYPE);		
		KeyspaceDefinition k = HFactory.createKeyspaceDefinition(keyspaceName,
																 ThriftKsDef.DEF_STRATEGY_CLASS,
																 1,
																 Arrays.asList(cfDef));		
		cl.addKeyspace(k, true);
	}
}
