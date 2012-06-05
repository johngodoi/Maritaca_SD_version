package br.unifesp.maritaca.persistence;

import java.util.Map;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

public class EntityManagerFactory {
	public  static final int HECTOR_MARITACA_EM = 1;
	private static EntityManagerFactory instance;
	private static EntityManagerHectorImpl hectorEM;// singleton
	
	private Cluster cluster;
	private Keyspace keyspace;	

	private EntityManagerFactory() {
	}

	public static EntityManagerFactory getInstance() {
		if (instance == null) {
			instance = new EntityManagerFactory();
		}
		return instance;
	}
	
	public static void setHectorEntityManager(EntityManagerHectorImpl hectorEM){
		EntityManagerFactory.hectorEM = hectorEM;
	}

	public EntityManager createEntityManager(int type, Map<String,String> params) {
		EntityManager em = null;
		switch (type) {
		case HECTOR_MARITACA_EM:
			setHectorParams(params);
			return createEntityManager(type);
		}
		return em;
	}

	public EntityManager createEntityManager(int type) {
		EntityManager em = null;
		switch (type) {
		case HECTOR_MARITACA_EM:
			if (hectorEM == null) {
				if (cluster != null && keyspace != null)
					hectorEM = EntityManagerHectorImpl.getInstance(cluster,
							keyspace);
			}
			em = hectorEM;
			break;
		}
		return em;
	}

	public void setHectorParams(Map<String, String> params) {
		cluster = HFactory.getOrCreateCluster("cassandra",
				params.get("cluster"));
		keyspace = HFactory.createKeyspace(params.get("keyspace"), cluster);
	}

	public void closeEntityManager(int type) {
		switch (type) {
		case HECTOR_MARITACA_EM:
			if (hectorEM != null) {
				hectorEM.close();
			}
			break;
		}
	}
}
