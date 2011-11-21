package br.unifesp.maritaca.persistence;

import java.util.Map;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

public class EntityManagerFactory {
	public static final int HECTOR_MARITACA_EM=1;
	private static EntityManagerFactory instance;
	
	private Cluster cluster;
	private Keyspace keyspace;
	
	private EntityManagerFactory(){}
	
	public static EntityManagerFactory getInstance(){
		if(instance == null){
			instance = new EntityManagerFactory();
		}
		return instance;
	}
	
	public EntityManager createEntityManager(int type, Map params){
		EntityManager em=null;
		switch (type) {
		case HECTOR_MARITACA_EM:
			em = new EntityManagerHectorImpl((Cluster)(params.get("cluster")), (Keyspace)(params.get("keyspace")));
			break;
		}
		return em;
	}
	
	public EntityManager createEntityManager(int type){
		EntityManager em=null;
		switch (type) {
		case HECTOR_MARITACA_EM:
			if(cluster !=null && keyspace != null)
			em = new EntityManagerHectorImpl(cluster, keyspace);
			break;
		}
		return em;
	}
	
	public void setHectorParams(Map<String, String> params)throws IllegalArgumentException{
		cluster = HFactory.getOrCreateCluster("cassandra", params.get("cluster"));
		keyspace = HFactory.createKeyspace(params.get("keyspace"), cluster);
	}
}
