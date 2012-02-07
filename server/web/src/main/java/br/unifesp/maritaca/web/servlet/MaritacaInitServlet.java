package br.unifesp.maritaca.web.servlet;

import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import br.unifesp.maritaca.model.ManagerModel;
import br.unifesp.maritaca.model.ModelFactory;
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
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	HashMap<String, String> params = new HashMap<String, String>();
    	params.put("cluster", config.getInitParameter("cluster"));
    	params.put("keyspace", config.getInitParameter("keyspace"));
    	
    	ManagerModel mm = ModelFactory.getInstance().createManagerModel();
    	mm.initMaritaca(params);
    	
    }
    
    @Override
    public void destroy() {
    	EntityManagerFactory.getInstance().closeEntityManager(EntityManagerFactory.HECTOR_MARITACA_EM);
    }
}
