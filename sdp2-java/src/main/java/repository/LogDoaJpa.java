package repository;

import domain.Log;

public class LogDoaJpa extends GenericDaoJpa<Log> implements LogDoa {

	public LogDoaJpa() {
		super(Log.class);
	}

	// Implement any additional methods specific to Log here

}
