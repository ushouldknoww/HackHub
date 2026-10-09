package it.unicam.hackhub.utils.strategy;

import it.unicam.hackhub.model.RankingCandidate;
import java.util.List;

public interface RankingStrategy {
    Long selectWinner(List<RankingCandidate> candidates);
}