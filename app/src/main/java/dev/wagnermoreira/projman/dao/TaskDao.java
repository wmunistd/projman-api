package dev.wagnermoreira.projman.dao;

import dev.wagnermoreira.projman.domain.Task;

import java.util.List;

public interface TaskDao {
    long insert(Task task);
    List<Task> listAll();
    List<Task> listByProject(Long projectId);
    boolean updateStatus(long id, String status);
}
