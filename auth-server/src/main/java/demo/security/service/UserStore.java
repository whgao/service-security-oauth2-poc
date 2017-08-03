package demo.security.service;

import demo.security.models.User;

public interface UserStore {
boolean addUser(User user);
	
	void updateUser(User user);
	
	User getUser(String id);
	
	boolean removeUser(String id);
	
	User[] getUsers();
}
