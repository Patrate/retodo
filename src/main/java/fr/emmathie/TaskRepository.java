package fr.emmathie;

import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<AbstractTask, Integer> {
}