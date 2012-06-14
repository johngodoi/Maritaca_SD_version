package br.unifesp.maritaca.business.answer.edit;

import java.util.HashMap;
import java.util.Map;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;
import br.unifesp.maritaca.persistence.entity.Answer;

public class AnswerEditorDAO extends BaseDAO {

	public void saveAnswer(Answer answer) {
		answer.setKey(TimeUUIDUtils.getUniqueTimeUUIDinMillis());

		EntityManagerFactory instance = EntityManagerFactory.getInstance();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cluster", "localhost:9160");
		params.put("keyspace", "Maritaca");

		EntityManager entityManager = instance.createEntityManager(
				EntityManagerFactory.HECTOR_MARITACA_EM, params);

		entityManager.persist(answer);
	}
}
