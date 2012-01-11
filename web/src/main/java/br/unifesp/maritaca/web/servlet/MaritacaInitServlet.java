package br.unifesp.maritaca.web.servlet;

import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import br.unifesp.maritaca.core.Form;
import br.unifesp.maritaca.core.FormShare;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.EntityManager;
import br.unifesp.maritaca.persistence.EntityManagerFactory;

/**
 * Servlet implementation class MaritacaInitServlet
 */
public class MaritacaInitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MaritacaInitServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("cluster", config.getInitParameter("cluster"));
    	params.put("keyspace", config.getInitParameter("keyspace"));
    	EntityManagerFactory.getInstance().setHectorParams(params);
    	
    	EntityManager em = EntityManagerFactory.getInstance().createEntityManager(EntityManagerFactory.HECTOR_MARITACA_EM);
    	
		User user = new User();
		user.setFirstname("admin");
		user.setPassword("123");
		
    	if(!em.tableExists(User.class)){
    		em.persist(user);
    	}
    	
    	if(!em.tableExists(Form.class)){
    		Form form = new Form();
    		form.setXml("form xml");
    		form.setTitle("test form");
    		form.setUser(user);
    		em.persist(form);
    	}
    	
    	em.createTable(FormShare.class);
    }
    
    @Override
    public void destroy() {
    	EntityManagerFactory.getInstance().closeEntityManager(EntityManagerFactory.HECTOR_MARITACA_EM);
    }
}
