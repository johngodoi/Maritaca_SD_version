package br.unifesp.maritaca.persistence.test.form;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.unifesp.maritaca.access.PrePolicy;
import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormPermissions;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.model.FormAnswerModel;
import br.unifesp.maritaca.model.ManagerModel;
import br.unifesp.maritaca.model.ModelFactory;
import br.unifesp.maritaca.model.UserModel;
import br.unifesp.maritaca.model.impl.ManagerModelImpl;
import br.unifesp.maritaca.persistence.EntityManager;

public class AuthorizationTestsSetUp {
		
	/* The default visibility is used here because all these
	 * variables are accessed by the testing classes.
	 */
	FormAnswerModel formAnswModel;
	
	User  user1grp1             = new User();
	User  user2grp1             = new User();
	User  userNoGrp             = new User();
	User  root                  = new User();
	
	MaritacaList grp1                  = new MaritacaList();		
	MaritacaList grpUser1              = new MaritacaList();				
	MaritacaList grpUser2              = new MaritacaList();
	MaritacaList grpUserNoGrp          = new MaritacaList();
	MaritacaList grpAllUsers           = new MaritacaList();
	
	Form  publicFormUser1       = new Form();
	Form  privateFormUser1      = new Form();
	Form  sharedHierFormUser1   = new Form();
	Form  sharedSocialFormUser1 = new Form();
	
	public AuthorizationTestsSetUp(EntityManager em){				
		user1grp1.setEmail("usr1");
		user2grp1.setEmail("usr2");
		userNoGrp.setEmail("usrNoGrp");
		root.setEmail(ManagerModel.CFG_ROOT);
		
		em.persist(user1grp1);
		em.persist(user2grp1);
		em.persist(userNoGrp);
		em.persist(root);
		
		grp1.setOwner(user1grp1);
		grpUser1.setOwner(user1grp1);
		grpUser2.setOwner(user2grp1);
		grpUserNoGrp.setOwner(userNoGrp);
		grpAllUsers.setOwner(root);
		grpAllUsers.setName(ManagerModel.ALL_USERS);
		
		em.persist(grp1);
		em.persist(grpUser1);
		em.persist(grpUser2);
		em.persist(grpUserNoGrp);
		em.persist(grpAllUsers);
		
//		user1grp1.setMaritacaList(grpUser1);
//		user2grp1.setMaritacaList(grpUser2);
//		userNoGrp.setMaritacaList(grpUserNoGrp);
		em.persist(user1grp1);
		em.persist(user2grp1);
		em.persist(userNoGrp);
		
		publicFormUser1.setUser(user1grp1);
		
		PrePolicy publicPolicy = PrePolicy.PUBLIC;		
		publicFormUser1.setPolicy("publicPolicy");//(publicPolicy);
		em.persist(publicFormUser1);
			
		FormPermissions fp;
		fp = publicPolicy.buildPublicFormPermission(publicFormUser1, grpAllUsers);
		em.persist(fp);
		fp = publicPolicy.buildOwnerFormPermission(publicFormUser1, grpUser1);
		em.persist(fp);
				
		privateFormUser1.setUser(user1grp1);
		
		PrePolicy privatePolicy = PrePolicy.PRIVATE;		
		privateFormUser1.setPolicy("privatePolicy");//(privatePolicy);
		em.persist(privateFormUser1);
			
		fp = privatePolicy.buildPublicFormPermission(privateFormUser1, grpAllUsers);
		em.persist(fp);
		fp = privatePolicy.buildOwnerFormPermission(privateFormUser1, grpUser1);
		em.persist(fp);
		
		sharedHierFormUser1.setUser(user1grp1);
		
		PrePolicy sharedHierPolicy = PrePolicy.SHARED_HIERARCHICAL;		
		sharedHierFormUser1.setPolicy("sharedHierPolicy");//(sharedHierPolicy);
		em.persist(sharedHierFormUser1);
			
		fp = sharedHierPolicy.buildPublicFormPermission(sharedHierFormUser1, grpAllUsers);
		em.persist(fp);
		fp = sharedHierPolicy.buildOwnerFormPermission(sharedHierFormUser1, grpUser1);
		em.persist(fp);
		fp = sharedHierPolicy.buildListFormPermission(sharedHierFormUser1, grp1);
		em.persist(fp);
		
		sharedSocialFormUser1.setUser(user1grp1);
		
		PrePolicy sharedSocialPolicy = PrePolicy.SHARED_SOCIAL;		
		sharedSocialFormUser1.setPolicy("sharedSocialPolicy");//(sharedSocialPolicy);
		em.persist(sharedSocialFormUser1);
			
		fp = sharedSocialPolicy.buildPublicFormPermission(sharedSocialFormUser1, grpAllUsers);
		em.persist(fp);
		fp = sharedSocialPolicy.buildOwnerFormPermission(sharedSocialFormUser1, grpUser1);
		em.persist(fp);
		fp = sharedSocialPolicy.buildListFormPermission(sharedSocialFormUser1, grp1);
		em.persist(fp);
				
		formAnswModel = ModelFactory.getInstance().createFormResponseModel();
		formAnswModel.setEntityManager(em);

		ManagerModel mm = mock(ManagerModelImpl.class);
		when(mm.getRootUser()).thenAnswer(new Answer<User>() {
			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				return root;
			}			
		});
		
		UserModel um =  ModelFactory.getInstance().createUserModel();
		um.setEntityManager(em);
		um.setManagerModel(mm);
		formAnswModel.setUserModel(um);
	}
}