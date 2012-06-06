package br.unifesp.maritaca.business.form.edit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.unifesp.maritaca.business.answer.edit.dto.DataCollectedDTO;
import br.unifesp.maritaca.business.base.AbstractEJB;
import br.unifesp.maritaca.business.base.dao.FormDAO;
import br.unifesp.maritaca.business.form.dto.FormDTO;
import br.unifesp.maritaca.business.form.dto.FormWSDTO;
import br.unifesp.maritaca.business.util.ConstantsBusiness;
import br.unifesp.maritaca.persistence.entity.Form;

public class ApkCreatorEJB extends AbstractEJB {
	
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ApkCreatorEJB.class);

	public FormWSDTO getUnmarshalledFormFromXML(FormDTO formDTO) {
		try {
			Form form = formDAO.getFormByKey(formDTO.getKey(), false);
		
			JAXBContext jaxbContext = JAXBContext.newInstance(FormWSDTO.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			InputStream is = new 
					ByteArrayInputStream(form.getXml().getBytes(ConstantsBusiness.ENCODING_UTF8));
			
			FormWSDTO formWSDTO = (FormWSDTO)unmarshaller.unmarshal(is);
			formWSDTO.setUrl(form.getUrl());
			formWSDTO.setKey(form.getKey());
			
			return formWSDTO;			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getMarshalledFormFromXML(FormDTO formDTO) {
		try {
			FormWSDTO formWSDTO = getUnmarshalledFormFromXML(formDTO);
			JAXBContext jaxbContext = JAXBContext.newInstance(FormWSDTO.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			StringWriter writer = new StringWriter(); 
			marshaller.marshal(formWSDTO, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void buildNewApkFromFormId(FormDTO formDTO) {			
		try {
			String scriptLocation	= configurationDAO.getValueByName(ConstantsBusiness.MOB_SCRIPT_LOCATION);
			String maritacaPath 	= configurationDAO.getValueByName(ConstantsBusiness.MOB_MARITACA_PATH);
			String projectsPath 	= configurationDAO.getValueByName(ConstantsBusiness.MOB_PROJECTS_PATH);
			if(!scriptLocation.equals("") && !maritacaPath.equals("") && !projectsPath.equals("")) {
				File formXMLFile = new File(maritacaPath+ConstantsBusiness.MOB_FORM_XML_PATH);
				if(formXMLFile.exists()) {	formXMLFile.delete(); }
				FileUtils.writeStringToFile(formXMLFile, getMarshalledFormFromXML(formDTO));
				//
				String packageName = "form_"+formDTO.getUrl();
				List<String> commands = new ArrayList<String>();
				commands.add(scriptLocation);
				commands.add(packageName);
				commands.add(formDTO.getTitle());
				commands.add(maritacaPath);
				commands.add(projectsPath);			
				ProcessBuilder processBuilder = new ProcessBuilder(commands);			
				Process process = processBuilder.start();
				process.waitFor();
				return;
			}
			log.error("Error running maritaca.sh");
		} catch (Exception e) {
			log.error(e.getMessage());
		}		 
	}
	
	public boolean downloadApkFromFormId(FacesContext facesContext, FormDTO formDTO) {
		boolean isValidApp = false;
		try {
			buildNewApkFromFormId(formDTO);
			String packageName = "form_"+formDTO.getUrl();
			String projectsPath = configurationDAO.getValueByName(ConstantsBusiness.MOB_PROJECTS_PATH);
			String filePath = projectsPath+packageName+ConstantsBusiness.MOB_BIN_PATH+"maritaca-mobile-release.apk";
			File file = new File(filePath);
			if(file.isFile()) {
				ExternalContext context = facesContext.getExternalContext();
				HttpServletResponse response = (HttpServletResponse) context.getResponse();  
		        response.setHeader("Content-Disposition", "attachment;filename=\"" + formDTO.getUrl() + ".apk\"");  
		        response.setContentLength((int) file.length());  
		        response.setContentType(ConstantsBusiness.MOB_MIMETYPE);			
				FileInputStream fis = new FileInputStream(file);			
				OutputStream os = response.getOutputStream();
				byte[] buf = new byte[(int)file.length()];  
	            int count;  
	            while ((count = fis.read(buf)) >= 0) {  
	                os.write(buf, 0, count);  
	            }	             
	            os.flush();
	            fis.close(); 
	            os.close();	            
	            isValidApp = true;
			}
		}
		catch (FileNotFoundException e) {
			log.error(e.getMessage());
		}
		catch (IOException e) {		
			log.error(e.getMessage());
		}
		return isValidApp;
	}
	
	@Override
	public void setFormDAO(FormDAO formDAO) {
		super.setFormDAO(formDAO);
	}
	
	@Override
	public FormDAO getFormDAO() {
		return super.getFormDAO();
	}
	

}
