package dev.wagnermoreira.projman.api.dto;

public class TaskDtos {
    public static class TaskCreateRequest {
        public String title;
        public String status; // optional; default PLANNED
        public Long projectId;
        public Long assigneeId;
    }

    public static class TaskResponse {
        public long id;
        public String title;
        public String status;
        public Long projectId;
        public Long assigneeId;
    }

    public static class TaskStatusUpdate {
        public String status;
    }
}
