package dev.wagnermoreira.projman.domain;

import java.util.List;

public class Team {
    private Long id;
    private String name;
    private String description;
    private List<Long> memberIds;

    public Team(Long id, String name, String description, List<Long> memberIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.memberIds = memberIds;
    }

    public static Team ofNew(String name, String description, List<Long> memberIds) {
        return new Team(null, name, description, memberIds);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Long> getMemberIds() { return memberIds; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }
}
