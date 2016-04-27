package recsys.notebooks.model;

import java.util.HashMap;
import java.util.Map;

public class NotebooksUserModel {
	
	private Map<Integer, NotebooksUser> users;

	public NotebooksUserModel() {
		users = new HashMap<Integer, NotebooksUser>();
	}

	public Map<Integer, NotebooksUser> getUsers() {
		return users;
	}

	public void setUsers(Map<Integer, NotebooksUser> users) {
		this.users = users;
	}

	public NotebooksUser get(int userID) {
		return users.get(userID);
	}

	public NotebooksUser getOrCreate(int userID) {
		if (users.get(userID) == null) {
			NotebooksUser newUser = new NotebooksUser();
			users.put(userID, newUser);
			return newUser;
		} else {
			return users.get(userID);
		}
	}
}
