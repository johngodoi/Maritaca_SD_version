package br.unifesp.maritaca.business.login.dao;

import javax.inject.Inject;

import br.unifesp.maritaca.business.base.UserDAO;
import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.business.util.UtilsBusiness;
import br.unifesp.maritaca.core.User;
import br.unifesp.maritaca.persistence.dto.UserDTO;

public class LoginDAO extends BaseDAO {
    
    @Inject
    private UserDAO userDAO;
    
    public UserDTO findUserByEmail(String email){
    	User user = userDAO.findUserByEmail(email);
        return UtilsBusiness.convertToClass(user,UserDTO.class);
    }
}