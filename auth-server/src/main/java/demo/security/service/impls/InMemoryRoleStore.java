package demo.security.service.impls;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import demo.security.models.Role;
import demo.security.service.RoleStore;

public class InMemoryRoleStore implements RoleStore {

	private final AbstractInMemoryStore<String, Role> rolesStoreById = new AbstractInMemoryStore<String, Role>() {

		@Override
		protected String getKey(Role value) {
			return toKey(value.getId());
		}

		@Override
		protected String toKey(Object key) {
			return toLowerCase((String)key);
		}

		@Override
		protected Role[] buildStore() {
			return new Role[] {
				new Role("ROLE_ADMIN", "Admin Role", 
					new String[] {
						"RIGHT_ADMIN", "RIGHT_OTHER1"
					},
					new String[] {
						"GROUP_ADMIN", "GROUP_OTHER1"
					} )

				, new Role("ROLE_EDITOR", "Editor Role", 
					new String[] {
						"RIGHT_DEV", "RIGHT_OTHER2"
					},
					new String[] {
						"GROUP_DEV", "GROUP_OTHER2"
					} )
				
				, new Role("ROLE_READ", "Read only Role", 
					new String[] {
						"RIGHT_READ", "RIGHT_OTHER3"
					},
					new String[] {
						"GROUP_USER", "GROUP_OTHER3"
					} )
			};
		}
	};

	@Override
	public boolean addRole(Role role) {
		return rolesStoreById.addValue(role);
	}

	@Override
	public void updateRole(Role role) {
		rolesStoreById.updateValue(role);
		
	}

	@Override
	public Role getRole(String id) {
		return rolesStoreById.getValue(id);
	}

	@Override
	public boolean removeRole(String id) {
		return rolesStoreById.removeValue(id);
	}

	@Override
	public Role[] getRoles(String[] groups) {
		Set<String> gSet = new HashSet<String>(Arrays.asList(groups));
		
		List<Role> results = new ArrayList<Role>();
		Collection<Role> roles = rolesStoreById.getValues();
		boolean found = false;
		if (roles != null) {
			for(Role each : roles) {
				String[] rGroups = each.getGroups();
				for (int idx = 0; idx < rGroups.length; idx ++) {
					if (gSet.contains(rGroups[idx])) {
						found = true;
						results.add(each);
						break;
					}
				}
				
				if (found) {
					break;
				}
			}
		}
		return results.toArray(new Role[results.size()]);
	}

}
