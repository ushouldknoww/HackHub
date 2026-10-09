package it.unicam.hackhub.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "leader_id", nullable = false)
    private Long leader;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id")
    )
    @Column(name = "user_id", nullable = false)
    private List<Long> members = new ArrayList<>();

    public Team(String name, Long leaderId) {
        this.name = name;
        this.leader = leaderId;
        this.members.add(leaderId);
    }

    public void addMember(Long userId) {
        if (userId != null && !this.members.contains(userId)) {
            this.members.add(userId);
        }
    }

    public List<Long> getMemberIdsSnapshot() {
        return new ArrayList<>(this.members);
    }

    public int getTeamSize() {
        return this.members.size();
    }
}