package demo.security.service;


import demo.security.models.Role;

public interface RoleStore {
	boolean addRole(Role role);
	
	void updateRole(Role role);
	
	Role getRole(String id);
	
	boolean removeRole(String id);
	
	Role[] getRoles(String[] groups);
}
