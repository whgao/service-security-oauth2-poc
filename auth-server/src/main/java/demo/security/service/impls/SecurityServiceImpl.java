package demo.security.service.impls;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import demo.security.models.Role;
import demo.security.models.User;
import demo.security.service.RoleStore;
import demo.security.service.SecurityService;
import demo.security.service.UserStore;


public class SecurityServiceImpl implements SecurityService {

	private final RoleStore roleStore;
	
	private final UserStore userStore;
	
	
	public SecurityServiceImpl(RoleStore roleStore, UserStore userStore) {
		super();
		this.roleStore = roleStore;
		this.userStore = userStore;
	}

	@Override
	public String[] getGroups(String userId) {
		User user = userStore.getUser(userId);
		return (user == null) ? new String[0] : user.getGroups();
	}

	@Override
	public Role[] getRolesByGroups(String[] groups) {
		return roleStore.getRoles(groups);
	}


	@Override
	public String[] getRoleIds(Role[] roles) {
		Set<String> results = new HashSet<String>();
		for (int idx = 0; idx < roles.length; idx ++) {
			results.add(roles[idx].getId());
		}
		return results.toArray( new String[results.size()] );
	}

	@Override
	public String[] getRights(Role[] roles) {
		Set<String> results = new HashSet<String>();
		for (int idx = 0; idx < roles.length; idx ++) {
			results.addAll(Arrays.asList(roles[idx].getRights()));
		}
		return results.toArray( new String[results.size()] );
	}

	@Override
	public User[] getUsers() {
		return userStore.getUsers();
	}

}
