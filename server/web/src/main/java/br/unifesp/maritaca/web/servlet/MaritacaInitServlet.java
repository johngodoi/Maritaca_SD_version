package br.unifesp.maritaca.web.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;

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
    }
}
