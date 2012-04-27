package br.unifesp.maritaca.business.test;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import br.unifesp.maritaca.business.list.list.dto.MaritacaListDTO;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.core.MaritacaList;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.dto.UserDTO;

public class UtilBusinessTest {			
	private static final String LIST_NAME  = "List name";
	private static final String UUID_1     = "111dea60-146e-11e1-a7c0-d2b70b6d4d67";
	private static final String UUID_2     = "222dea60-146e-11e1-a7c0-d2b70b6d4d67";
	private static final String USER_EMAIL = "user@domain.com";

	@Test
	public void testDtoFromModel(){
		MaritacaList mList = new MaritacaList();
		mList.setName(LIST_NAME);
		mList.setKey(UUID_1);
		
		MaritacaListDTO mListDto;
		mListDto = UtilsBusiness.convertToClass(mList, MaritacaListDTO.class);
		
		assertEquals(mList.getName(),mListDto.getName());
		assertEquals(mList.getKey(),mListDto.getKey());
		
		User user = new User();
		user.setEmail(USER_EMAIL);
		
		UserDTO userDto = UtilsBusiness.convertToClass(user, UserDTO.class);		
		assertEquals(userDto.getEmail(),user.getEmail());
	}
	
	@Test
	public void testModelFromDto(){
		MaritacaListDTO mListDto = new MaritacaListDTO();
		mListDto.setName(LIST_NAME);
		mListDto.setKey(UUID_1);
		mListDto.setOwner(UUID.fromString(UUID_2));
		
		MaritacaList mList = UtilsBusiness.convertToClass(mListDto, MaritacaList.class);
		assertEquals(mList.getName(),mListDto.getName());
		assertEquals(mList.getKey(),mListDto.getKey());
		assertEquals(mList.getOwner().getKey(),mListDto.getOwner());
		
		UserDTO userDto = new UserDTO();
		userDto.setEmail(USER_EMAIL);
		User user = UtilsBusiness.convertToClass(userDto, User.class);
		assertEquals(userDto.getEmail(),user.getEmail());
	}
}
