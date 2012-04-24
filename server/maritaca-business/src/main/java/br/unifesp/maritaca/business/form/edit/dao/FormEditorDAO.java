package br.unifesp.maritaca.business.form.edit.dao;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.business.parser.RandomAnswersCreator;
import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.MaritacaDate;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;

public class FormEditorDAO extends BaseDAO {
	
	public boolean verifyIfUserExist(UUID userKey) {
		return entityManager.rowDataExists(User.class, userKey);
	}
	
	public boolean verifyIfUrlExist(String url) {
		// TODO: improve this
		// look for url in the Form columnFamily
		List<Form> fsList = entityManager.cQuery(Form.class, "url", url, true);
		return fsList.size() > 0;
	}

	public void persistForm(Form form) {
		entityManager.persist(form);
		
		createRandownAnswer(form);
	}

	/**
	 * This is a temporary method used to test the answers visualization while
	 * the mobile answers collector is being developed.
	 * @param form
	 */
	public void createRandownAnswer(Form form) {
		Random  rand = new Random();
		Integer numAnswers = rand.nextInt(10)+2;
		for(int i=0; i < numAnswers;i++){
			Answer answer = new Answer();
			answer.setForm(form);
			answer.setXml(RandomAnswersCreator.createRandomAnswersFromForm(form.getXml()));
			answer.setUser(form.getUser());
			answer.setCollectionDate(new MaritacaDate());
			entityManager.persist(answer);
		}
	}

	public Form getForm(UUID key, boolean minimal) {
		if (key == null ) {
			throw new IllegalArgumentException("Incomplete parameters");
		}
		Form form = entityManager.find(Form.class, key, minimal);
//		if(userHasPermission(getCurrentUser(), form, Operation.READ)){
//			return form;	
//		} else {
//			throw new AuthorizationDenied(Form.class, form.getKey(), getCurrentUser().getKey(), Operation.READ);
//		}	
		return form;
	}

	public void delete(Form form) {
		entityManager.delete(form);
	}
	
	public User getOwnerOfMaritacaList(MaritacaList gr) {
		if (gr.getOwner() != null) {
			return getUser(gr.getOwner().getKey());
		} else {
			MaritacaList group = entityManager.find(MaritacaList.class, gr.getKey(), true);
			if (group != null) {
				return getOwnerOfMaritacaList(group);
			} else {
				return null;
			}
		}
	}
	
	public User getUser(UUID uuid) {
		return entityManager.find(User.class, uuid);
	}
}
