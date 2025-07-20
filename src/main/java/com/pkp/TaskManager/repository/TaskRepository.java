package com.pkp.TaskManager.repository;

import com.pkp.TaskManager.model.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {
}
