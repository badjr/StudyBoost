package com.badjr.studyboost.dao;

import java.util.List;
import java.util.Optional;

/**
 * From https://baeldung.com/java-dao-pattern
 */
public interface Dao<T> {
	Optional<T> get(long id);

	List<T> getAll();

	void save(T t);

	void saveAll(List<T> t);

	void update(T t, String[] params);

	void delete(T t);
}
