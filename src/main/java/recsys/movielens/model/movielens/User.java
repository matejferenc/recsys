package recsys.movielens.model.movielens;

import recsys.model.SetPreference;

public class User {

	private SetPreference genrePreferences;

	private SetPreference directorPreferences;

	private SetPreference actorPreferences;

	private SetPreference actressPreferences;
	
	private SetPreference keywordsPreferences;

	public User() {
		genrePreferences = new SetPreference();
		directorPreferences = new SetPreference();
		actorPreferences = new SetPreference();
		actressPreferences = new SetPreference();
		keywordsPreferences = new SetPreference();
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
	
	public SetPreference getKeywordsPreferences() {
		return keywordsPreferences;
	}

}
