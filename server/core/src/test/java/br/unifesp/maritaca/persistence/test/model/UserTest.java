package br.unifesp.maritaca.persistence.test.model;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Assert;
import org.junit.Test;

import br.unifesp.maritaca.core.User;

public class UserTest {

	//temporal
	@Test
	public void xmlTest() {
		User user = new User();
		user.setFirstname("myfirstname");
		user.setLastname("mylastname");
		user.setPassword("mypassword");
		user.setEmail("email@domain.com");
		user.setKey(UUID.randomUUID());
		String xmlout = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
				"<user>\n" +
				"    <key>"+ user.getKey() + "</key>\n" +
				"    <firstname>myfirstname</firstname>\n" +
				"    <lastname>mylastname</lastname>\n" +
				"    <email>email@domain.com</email>\n" +
				"</user>\n";
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(User.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			m.marshal(user, os);
//			System.out.println(xmlout);
//			System.out.println(os.toString("UTF8"));
			Assert.assertEquals(xmlout, os.toString("UTF8"));
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
