package br.unifesp.maritaca.business.answer.edit;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hom.EntityManagerImpl;
import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.persistence.entity.Answer;

public class AnswerEditorDAO extends BaseDAO {

	private Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9160");
	private Keyspace keyspace = HFactory.createKeyspace("Maritaca", cluster);
	
	public void saveAnswer(Answer answer) throws Exception {
		answer.setKey(TimeUUIDUtils.getUniqueTimeUUIDinMillis());
		
		EntityManagerImpl em = new EntityManagerImpl(keyspace, "br.unifesp");
		
		em.persist(answer);
	}

}
