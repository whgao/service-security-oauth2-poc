package demo.security.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class User {
	private String id;
	
	private String password;
	
	private final Set<String> groups = new HashSet<String>();

	public User() {
	}
	
	public User(String id, String password, String... groups) {
		setId(id);
		setPassword(password);
		addGroups(groups);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getGroups() {
		return groups.toArray(new String[groups.size()]);
	}
	
	public void addGroup(String group) {
		groups.add(group);
	}
	
	public void addGroups(String[] groups) {
		this.groups.addAll(Arrays.asList(groups));
	}

	public void removeGroup(String group) {
		groups.remove(group);
	}
	
	public void removeGroups(String[] groups) {
		this.groups.removeAll(Arrays.asList(groups));
	}
	
	public void clearGroups() {
		this.groups.clear();
	}
	
}
