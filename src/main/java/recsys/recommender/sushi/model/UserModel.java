package recsys.recommender.sushi.model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
	
	private Map<Long, User> users;

	public UserModel() {
		users = new HashMap<Long, User>();
	}

	public Map<Long, User> getUsers() {
		return users;
	}

	public void setUsers(Map<Long, User> users) {
		this.users = users;
	}

	public User get(long userID) {
		return users.get(userID);
	}

	public User getOrCreate(long userID) {
		if (users.get(userID) == null) {
			User newUser = new User();
			users.put(userID, newUser);
			return newUser;
		} else {
			return users.get(userID);
		}
	}
}
