package br.unifesp.maritaca.business.form.edit.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.business.base.dao.FormAccessibleByListDAO;
import br.unifesp.maritaca.business.parser.RandomAnswersCreator;
import br.unifesp.maritaca.persistence.entity.Answer;
import br.unifesp.maritaca.persistence.entity.Form;
import br.unifesp.maritaca.persistence.entity.FormAccessibleByList;
import br.unifesp.maritaca.persistence.entity.MaritacaDate;
import br.unifesp.maritaca.persistence.entity.MaritacaList;
import br.unifesp.maritaca.persistence.entity.User;

public class FormEditorDAO extends BaseDAO {

	@Inject private FormAccessibleByListDAO formAccessibleByListDAO;
	
	public void createOrUpdateFormAccessible(Form form, User owner, List<UUID> deletedLists) {
		if(form.getKey() != null && owner != null) {
			//form.getLists().remove(owner.getMaritacaList().getKey());//
			for(UUID uuid : form.getLists()) {
				FormAccessibleByList formsByList = formAccessibleByListDAO.findFormAccesibleByKey(uuid);
				if(formsByList == null) {//TODO:
					formsByList = new FormAccessibleByList();
					formsByList.setKey(uuid);
					formsByList.setMaritacaList(uuid);
						List<UUID> uuidsForm = new ArrayList<UUID>(1);
						uuidsForm.add(form.getKey());
					formsByList.setForms(uuidsForm);
					formAccessibleByListDAO.persist(formsByList);
				}
				else {
					if(!formsByList.getForms().isEmpty()) {
						if(!formsByList.getForms().contains(form.getKey())) {
							formsByList.getForms().add(form.getKey());
							formAccessibleByListDAO.persist(formsByList);
						}
					}
				}
			}
			//Delete Form from the List
            if(!deletedLists.isEmpty()) {
                removeLists(deletedLists, form.getKey());
            }
        }
    }
    
    private void removeLists(List<UUID> deletedLists, UUID formKey) {
        for(UUID uuid : deletedLists) {
            FormAccessibleByList formsByList = formAccessibleByListDAO.findFormAccesibleByKey(uuid);            
            if(formsByList != null) {
                if(formsByList.getForms().contains(formKey)) {
                    formsByList.getForms().remove(formKey);
                }
                formAccessibleByListDAO.persist(formsByList);
            }
        }
    }
	
	public void deleteFormAccessible(Form form, User owner) {
		if(form.getKey() != null ) {
			for(UUID uuid : form.getLists()) {
				FormAccessibleByList formsByList = formAccessibleByListDAO.findFormAccesibleByKey(uuid);
				if(formsByList != null) {
					if(formsByList.getForms().contains(uuid)) {
						List<UUID> temp = new ArrayList<UUID>(1);
						temp.add(uuid);
						formsByList.getForms().removeAll(temp);
						formAccessibleByListDAO.persist(formsByList);
					}
				}
			}
		}
	}
	
	//
	public FormAccessibleByListDAO getFormAccessibleByListDAO() {
		return formAccessibleByListDAO;
	}

	public void setFormAccessibleByListDAO(
			FormAccessibleByListDAO formAccessibleByListDAO) {
		this.formAccessibleByListDAO = formAccessibleByListDAO;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	
	public MaritacaList searchMaritacaListByName(String groupName) {
		if (entityManager == null || groupName == null)
			return null;

		List<MaritacaList> foundGroups = entityManager.cQuery(MaritacaList.class, "name",
				groupName);

		if (foundGroups.size() == 0) {
			return null;
		} else if (foundGroups.size() == 1) {
			return foundGroups.get(0);
		} else {
//			throw new InvalidNumberOfEntries(groupName, MaritacaList.class);
			return null;
		}
	}
}