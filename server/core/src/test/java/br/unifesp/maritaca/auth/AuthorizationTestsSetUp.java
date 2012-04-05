package br.unifesp.maritaca.auth;

import java.util.ArrayList;
import java.util.List;

import br.unifesp.maritaca.access.Policy;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.MaritacaListUser;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ManagerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.persistence.EntityManager;

public class AuthorizationTestsSetUp {
		
	/* The default visibility is used here because all these
	 * variables are accessed by the testing classes.
	 */
	EntityManager   em;
	
	FormAnswerModel formAnswModel;
	UserModel       userModel;
	
	User  user1grp1             = new User();
	User  user2grp1             = new User();
	User  userNoGrp             = new User();
	
	MaritacaList grp1           = new MaritacaList();		
	
	Form  publicFormUser1       = new Form();
	Form  privateFormUser1      = new Form();
	Form  sharedHierFormUser1   = new Form();
	Form  sharedSocialFormUser1 = new Form();
	
	public AuthorizationTestsSetUp(EntityManager em){
		this.em = em;
		
		initModels(em);
		
		publicFormUser1.setTitle("publicFormUser1");
		privateFormUser1.setTitle("privateFormUser1");
		sharedHierFormUser1.setTitle("sharedHierFormUser1");
		sharedSocialFormUser1.setTitle("sharedSocialFormUser1");
		
		user1grp1.setEmail("usr1");
		user2grp1.setEmail("usr2");
		userNoGrp.setEmail("usrNoGrp");
		
		userModel.saveUser(user1grp1);
		userModel.saveUser(user2grp1);
		userModel.saveUser(userNoGrp);
				
		grp1.setOwner(user1grp1);
		grp1.setName("grp1");
		userModel.saveMaritacaList(grp1);
			
		MaritacaListUser listUser = new MaritacaListUser(grp1,user2grp1);
		userModel.saveMaritacaListUser(listUser);
						
		publicFormUser1.setUser(user1grp1);		
		publicFormUser1.setPolicy(Policy.PUBLIC);
		formAnswModel.saveForm(publicFormUser1);
				
		privateFormUser1.setUser(user1grp1);		
		privateFormUser1.setPolicy(Policy.PRIVATE);
		formAnswModel.saveForm(privateFormUser1);
		
		List<MaritacaList> formLists = new ArrayList<MaritacaList>();
		formLists.add(grp1);
		
		sharedHierFormUser1.setUser(user1grp1);				
		sharedHierFormUser1.setPolicy(Policy.SHARED_HIERARCHICAL);
		formAnswModel.saveForm(sharedHierFormUser1,formLists);
		
		sharedSocialFormUser1.setUser(user1grp1);				
		sharedSocialFormUser1.setPolicy(Policy.SHARED_SOCIAL);
		formAnswModel.saveForm(sharedSocialFormUser1,formLists);
	}
	
	private void initModels(EntityManager em){		
		formAnswModel = ModelFactory.getInstance().createFormResponseModel();
		formAnswModel.setEntityManager(em);
		
		userModel     =  ModelFactory.getInstance().createUserModel();
		userModel.setEntityManager(em);
		
		ManagerModel managerModel =  ModelFactory.getInstance().createManagerModel();
		managerModel.setEntityManager(em);
		managerModel.setUserModel(userModel);
		managerModel.initMaritaca(null);
		
		formAnswModel.setUserModel(userModel);
	}
}