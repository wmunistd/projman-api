package dev.wagnermoreira.projman.domain;

import java.util.List;

public class Project {
    private Long id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String status; // PLANNED | IN_PROGRESS | DONE | CANCELLED
    private Long managerUserId; 
    private List<Long> teamIds;

    public Project(Long id, String name, String description, String startDate, String endDate, String status, Long managerUserId, List<Long> teamIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.managerUserId = managerUserId;
        this.teamIds = teamIds;
    }

    public static Project ofNew(String name, String description, String startDate, String endDate, String status, Long managerUserId, List<Long> teamIds) {
        return new Project(null, name, description, startDate, endDate, status, managerUserId, teamIds);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public Long getManagerUserId() { return managerUserId; }
    public List<Long> getTeamIds() { return teamIds; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setStatus(String status) { this.status = status; }
    public void setManagerUserId(Long managerUserId) { this.managerUserId = managerUserId; }
    public void setTeamIds(List<Long> teamIds) { this.teamIds = teamIds; }
}
