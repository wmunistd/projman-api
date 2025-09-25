package dev.wagnermoreira.projman.dao;

import dev.wagnermoreira.projman.domain.Project;

import java.util.List;

public interface ProjectDao {
    long insert(Project project);
    List<Project> listAll();
}
