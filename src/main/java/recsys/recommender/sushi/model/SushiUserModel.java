package recsys.recommender.sushi.model;

import java.util.HashMap;
import java.util.Map;

public class SushiUserModel {
	
	private Map<Integer, SushiUser> users;

	public SushiUserModel() {
		users = new HashMap<Integer, SushiUser>();
	}

	public Map<Integer, SushiUser> getUsers() {
		return users;
	}

	public void setUsers(Map<Integer, SushiUser> users) {
		this.users = users;
	}

	public SushiUser get(int userID) {
		return users.get(userID);
	}

	public SushiUser getOrCreate(int userID) {
		if (users.get(userID) == null) {
			SushiUser newUser = new SushiUser();
			users.put(userID, newUser);
			return newUser;
		} else {
			return users.get(userID);
		}
	}
}
