package it.unicam.hackhub.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hackathon_id", nullable = false)
    private Long hackathon;

    @Column(name = "participating_team_id", nullable = false, unique = true)
    private Long participatingTeam;

    @Column(name = "response", nullable = false, length = 10000)
    private String response;

    @Column(name = "response_url", nullable = false, length = 500)
    private String responseURL;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Evaluation evaluation;

    public Submission(Long hackathonId, Long participatingTeamId, String response, String responseURL) {
        this.hackathon = hackathonId;
        this.participatingTeam = participatingTeamId;
        this.response = response;
        this.responseURL = responseURL;
        this.updatedAt = LocalDateTime.now();
    }

    public Evaluation addEvaluation(int score, String comment) {
        if (this.hasEvaluation()) {
            throw new IllegalStateException("Submission già valutata");
        }
        this.evaluation = new Evaluation(score, comment);
        this.evaluation.assignSubmission(this);
        return this.evaluation;
    }

    public boolean hasEvaluation() {
        return this.evaluation != null;
    }

    public int getScore() {
        return this.evaluation.getScore();
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
}