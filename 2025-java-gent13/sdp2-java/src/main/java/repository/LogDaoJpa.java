package repository;

import domain.Log;

public class LogDaoJpa extends GenericDaoJpa<Log> implements LogDao {

	public LogDaoJpa() {
		super(Log.class);
	}

	// Implement any additional methods specific to Log here

}
