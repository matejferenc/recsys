package recsys.recommender.sushi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderModel {
	
	private Map<Integer, List<Integer>> order;

	public OrderModel() {
		order = new HashMap<Integer, List<Integer>>();
	}

	public Map<Integer, List<Integer>> getUsers() {
		return order;
	}

	public void setUsers(Map<Integer, List<Integer>> users) {
		this.order = users;
	}

	public List<Integer> get(int userID) {
		return order.get(userID);
	}

	public List<Integer> getOrCreate(int userID) {
		if (order.get(userID) == null) {
			List<Integer> newOrder = new ArrayList<Integer>();
			order.put(userID, newOrder);
			return newOrder;
		} else {
			return order.get(userID);
		}
	}
}
