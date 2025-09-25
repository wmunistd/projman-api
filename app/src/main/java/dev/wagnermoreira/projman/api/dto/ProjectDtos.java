package dev.wagnermoreira.projman.api.dto;

import java.util.List;

public class ProjectDtos {
    public static class ProjectCreateRequest {
        public String name;
        public String description;
        public String startDate;
        public String endDate;
        public String status;
        public Long managerUserId;
        public List<Long> teamIds;
    }

    public static class ProjectResponse {
        public long id;
        public String name;
        public String description;
        public String startDate;
        public String endDate;
        public String status;
        public Long managerUserId;
        public List<Long> teamIds;
    }
}
