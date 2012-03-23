package br.unifesp.maritaca.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.unifesp.maritaca.access.operation.Operation;
import br.unifesp.maritaca.core.Answer;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.Group;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;

public interface FormAnswerModel extends GenericModel{
		
	boolean saveForm(Form form);
	
	Form getForm(UUID uid, boolean minimal);

	void setEntityManager(EntityManager em);

	Collection<Form> listAllForms();

	Collection<Form> listAllFormsMinimal();

	void deleteForm(Form form);
	
	String getFormIdFromUrl(String url);
	
	Collection<Form> listAllFormsMinimalByUser(User user);
	
	Collection<Form> listAllSharedForms(User user, boolean minimal);
	
	Collection<FormPermissions> getFormPermissionsByGroup(Group group);
	
	/************* ANSWER ***********/
	boolean saveAnswer(Answer answer);

	Answer getAnswer(UUID uuid);

	Collection<Answer> listAllAnswers(UUID formId);

	Collection<Answer> listAllAnswersMinimal(UUID formId);

	boolean urlForSharingExists(String url);

	void setUserModel(UserModel userModel);

	UserModel getUserModel();

	List<FormPermissions> getFormPermissions(Form form);

	FormPermissions getFormPermissionById(String formPermId);

	boolean saveFormPermission(FormPermissions fp);
	
	void deleteFormPermission(FormPermissions formPerm);

	<T> boolean currentUserHasPermission(T entity, Operation op);
	
	<T> boolean userHasPermission(User user, T entity, Operation op);
	
	Collection<Form> listAllFormsSortedbyName(User user);

	Collection<Form> listAllFormsSortedbyDate(User user);

	void close();

}
