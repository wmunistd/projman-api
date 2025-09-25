package dev.wagnermoreira.projman.domain;

public class Task {
    private Long id;
    private String title;
    private String status; // PLANNED | IN_PROGRESS | DONE | CANCELLED
    private Long projectId; // nullable
    private Long assigneeId; // nullable

    public Task(Long id, String title, String status, Long projectId, Long assigneeId) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
    }

    public static Task ofNew(String title, String status, Long projectId, Long assigneeId) {
        return new Task(null, title, status, projectId, assigneeId);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public Long getProjectId() { return projectId; }
    public Long getAssigneeId() { return assigneeId; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setStatus(String status) { this.status = status; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
}
