package com.rest.kouponify.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.rest.kouponify.common.GenericDaoImpl;
import com.rest.kouponify.entity.User;

/**
 * Created by vijay on 2/15/16.
 */

@Component("userDao")
public class UserDao extends GenericDaoImpl<User, Long> {

    public UserDao() {
        super("kouponify.user", "user_id");
    }

    @Override
    public User rowMapper(final ResultSet rs) throws SQLException {
        final User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFName(rs.getString("fname"));
        user.setLName(rs.getString("lname"));
        user.setShop(rs.getString("shop"));
        user.setCreatedBy(rs.getLong("created_by"));
        user.setUpdatedBy(rs.getLong("updated_by"));
        user.setCreatedAt(rs.getLong("created_at"));
        user.setUpdatedAt(rs.getLong("updated_at"));
        user.setActive(rs.getBoolean("active"));
        return user;
    }

    @Override
    public Map<String, Object> rowUnmapper(final User user) {
        final Map<String, Object> mapping = new LinkedHashMap<String, Object>();
        mapping.put("user_id", user.getId());
        mapping.put("username", user.getUsername());
        mapping.put("password", user.getPassword());
        mapping.put("email", user.getEmail());
        mapping.put("fname", user.getFName());
        mapping.put("lname", user.getLName());
        mapping.put("shop", user.getShop());
        mapping.put("created_by", user.getCreatedBy());
        mapping.put("updated_by", user.getUpdatedBy());
        mapping.put("created_at", user.getCreatedAt());
        mapping.put("updated_at", user.getUpdatedAt());
        setIfPresent(mapping, user.getActive(), "active");
        return mapping;
    }

    public User loadUserByUsername(final String username) throws UsernameNotFoundException {
        return findObjByCustomQuery("username='" + StringEscapeUtils.escapeSql(username) + "'");
    }

}
