package demo.security.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Role {
	private String id;
	
	private String name;
	
	private final Set<String> groups = new HashSet<>();
	
	private final Set<String> rights = new HashSet<>();

	
	
	public Role(String id, String name, String[] rights, String[] groups) {
		super();
		setId(id);
		setName(name);
		addRights(rights);
		addGroups(groups);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public String[] getRights() {
		return rights.toArray(new String[rights.size()]);
	}
	
	public void addRight(String right) {
		rights.add(right);
	}
	
	public void addRights(String[] rights) {
		this.rights.addAll(Arrays.asList(rights));
	}

	public void removeRight(String right) {
		rights.remove(right);
	}
	
	public void removeRights(String[] rights) {
		this.rights.removeAll(Arrays.asList(rights));
	}
	
	public void clearRights() {
		this.rights.clear();
	}
	
}
