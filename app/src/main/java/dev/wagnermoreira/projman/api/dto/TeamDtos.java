package dev.wagnermoreira.projman.api.dto;

import java.util.List;

public class TeamDtos {
    public static class TeamCreateRequest {
        public String name;
        public String description;
        public List<Long> memberIds;
    }

    public static class TeamResponse {
        public long id;
        public String name;
        public String description;
        public List<Long> memberIds;
    }
}
