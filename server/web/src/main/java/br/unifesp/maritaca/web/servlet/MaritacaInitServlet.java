package br.unifesp.maritaca.web.servlet;

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import br.unifesp.maritaca.business.init.MaritacaInitEJB;

/**
 * Servlet implementation class MaritacaInitServlet
 */
public class MaritacaInitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String PARAM_NAME_CLUSTER = "cluster";
	private static final String PARAM_NAME_KEYSPACE = "keyspace";

	@Inject MaritacaInitEJB maritacaInitEJB; 
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MaritacaInitServlet() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		initLogger(config);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(PARAM_NAME_CLUSTER, config.getInitParameter(PARAM_NAME_CLUSTER));
		params.put(PARAM_NAME_KEYSPACE, config.getInitParameter(PARAM_NAME_KEYSPACE));

		maritacaInitEJB.initMaritaca(params);
	}

	private void initLogger(ServletConfig config) {
		String log4jLocation = config
				.getInitParameter("log4j-properties-location");

		ServletContext sc = config.getServletContext();

		if (log4jLocation != null) {
			String webAppPath = sc.getRealPath("/");
			String log4jProp = webAppPath + log4jLocation;
			File filePropLog4j = new File(log4jProp);
			if (filePropLog4j.exists()) {
				PropertyConfigurator.configure(log4jProp);
				return;
			}
		}

		System.err.println("Not possible to start the logging system from properties file");
		System.err.println("Logging basic configuration");
		BasicConfigurator.configure();

	}

	@Override
	public void destroy() {
		maritacaInitEJB.terminateMaritaca();
	}
}
