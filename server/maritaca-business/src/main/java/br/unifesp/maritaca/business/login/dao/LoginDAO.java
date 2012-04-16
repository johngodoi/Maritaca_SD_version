package br.unifesp.maritaca.business.login.dao;

import br.unifesp.maritaca.business.base.UserDAO;
import br.unifesp.maritaca.business.base.dao.BaseDAO;
import br.unifesp.maritaca.core.User;
import javax.inject.Inject;

public class LoginDAO extends BaseDAO {
    
    @Inject UserDAO userDAO;
    
    public User findUserByEmail(String email){
        return userDAO.findUserByEmail(email);
    }
}