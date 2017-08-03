package demo.security.service;

import demo.security.models.Role;
import demo.security.models.User;

public interface SecurityService {
	String[] getGroups(String userId);
	
	Role[] getRolesByGroups(String[] groups);

	User[] getUsers();

	String[] getRoleIds(Role[] roles);

	String[] getRights(Role[] roles);
	
}
