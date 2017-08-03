package demo.security.service.impls;

import java.util.Collection;

import demo.security.models.User;
import demo.security.service.UserStore;

public class InMemoryUserStore implements UserStore {
	private final AbstractInMemoryStore<String, User> usersStoreById = new AbstractInMemoryStore<String, User>() {

		@Override
		protected String getKey(User value) {
			return toKey(value.getId());
		}

		@Override
		protected String toKey(Object key) {
			return toLowerCase((String)key);
		}

		@Override
		protected User[] buildStore() {
			return new User[] {
				new User("admin1", "testpass", "GROUP_ADMIN", "GROUP_USER", "GROUP_DEV")
				, new User("dev1", "testpass", "GROUP_USER", "GROUP_DEV")
				, new User("user1", "testpass", "GROUP_USER")
			};
		}
		
	};

	@Override
	public boolean addUser(User user) {
		return usersStoreById.addValue(user);
	}

	@Override
	public void updateUser(User user) {
		usersStoreById.updateValue(user);
	}

	@Override
	public User getUser(String id) {
		return usersStoreById.getValue(id);
	}

	@Override
	public boolean removeUser(String id) {
		return usersStoreById.removeValue(id);
	}

	@Override
	public User[] getUsers() {
		Collection<User> users = usersStoreById.getValues();
		return users.toArray( new User[users.size()]);
	}

}
