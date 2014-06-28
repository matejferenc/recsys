package recsys.recommender.sushi.model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
	
	private Map<Integer, User> users;

	public UserModel() {
		users = new HashMap<Integer, User>();
	}

	public Map<Integer, User> getUsers() {
		return users;
	}

	public void setUsers(Map<Integer, User> users) {
		this.users = users;
	}

	public User get(int userID) {
		return users.get(userID);
	}

	public User getOrCreate(int userID) {
		if (users.get(userID) == null) {
			User newUser = new User();
			users.put(userID, newUser);
			return newUser;
		} else {
			return users.get(userID);
		}
	}
}
