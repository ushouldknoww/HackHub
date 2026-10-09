package it.unicam.hackhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evaluations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;

    private int score;
    @Column(length = 2000)
    private String comment;

    public Evaluation(int score, String comment) {
        if (score < 0 || score > 10) {
            throw new IllegalArgumentException("Score deve essere compreso tra 0 e 10");
        }
        this.score = score;
        this.comment = comment;
    }

    void assignSubmission(Submission submission) {
        this.submission = submission;
    }
}