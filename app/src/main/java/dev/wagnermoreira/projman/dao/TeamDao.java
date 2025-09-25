package dev.wagnermoreira.projman.dao;

import dev.wagnermoreira.projman.domain.Team;

import java.util.List;

public interface TeamDao {
    long insert(Team team);
    List<Team> listAll();
}
