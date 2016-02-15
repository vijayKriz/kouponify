package com.rest.kouponify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.rest.kouponify.common.GenericDaoImpl;
import com.rest.kouponify.dao.UserDao;
import com.rest.kouponify.entity.User;

/**
 *
 * @author vijay | User service to authenticate and basic user onboarding operation
 *
 */
@Service
public class UserService extends AbstractService<User> {

    @Autowired
    @Qualifier("userDao")
    UserDao dao;

    @Override
    protected GenericDaoImpl<User, Long> getDao() {
        return dao;
    }

    /**
     *
     * @param username
     * @return User
     */
    public User getUserByName(final String username) {
        return dao.loadUserByUsername(username);
    }

}
