package recsys.recommender.movielens.model.movielens;

public class User {

	private SetPreference genrePreferences;

	private SetPreference directorPreferences;

	private SetPreference actorPreferences;

	private SetPreference actressPreferences;

	public User() {
		genrePreferences = new SetPreference();
		directorPreferences = new SetPreference();
		actorPreferences = new SetPreference();
		actressPreferences = new SetPreference();
	}

	public SetPreference getGenrePreferences() {
		return genrePreferences;
	}

	public SetPreference getDirectorPreferences() {
		return directorPreferences;
	}

	public SetPreference getActorPreferences() {
		return actorPreferences;
	}

	public SetPreference getActressPreferences() {
		return actressPreferences;
	}

}
