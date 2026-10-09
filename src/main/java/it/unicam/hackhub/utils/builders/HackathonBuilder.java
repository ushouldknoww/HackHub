package it.unicam.hackhub.utils.builders;

import it.unicam.hackhub.model.Hackathon;
import it.unicam.hackhub.model.enums.RankingPolicy;
import it.unicam.hackhub.model.valueobjs.Period;

import java.util.ArrayList;
import java.util.List;

public class HackathonBuilder implements IHackathonBuilder {

    private String name;
    private String type;
    private String location;
    private boolean online;
    private double prize;
    private int maxTeamSize;
    private String regulation;

    private Long organizerId;
    private Long judgeId;
    private List<Long> mentorsId;

    private Period subscriptionDates;
    private Period dates;
    private String delivery;
    private RankingPolicy rankingPolicy;

    public HackathonBuilder() {
        this.reset();
    }

    @Override
    public void reset() {
        this.name = null;
        this.type = null;
        this.location = null;
        this.online = false;
        this.prize = 0.0;
        this.maxTeamSize = 0;
        this.regulation = null;
        this.organizerId = null;
        this.judgeId = null;
        this.mentorsId = new ArrayList<>();
        this.subscriptionDates = null;
        this.dates = null;
        this.delivery = null;
        this.rankingPolicy = null;
    }

    @Override
    public IHackathonBuilder buildName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public IHackathonBuilder buildType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public IHackathonBuilder buildLocation(String location) {
        this.location = location;
        return this;
    }

    @Override
    public IHackathonBuilder buildOnline(boolean online) {
        this.online = online;
        return this;
    }

    @Override
    public IHackathonBuilder buildPrize(double prize) {
        this.prize = prize;
        return this;
    }

    @Override
    public IHackathonBuilder buildMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
        return this;
    }

    @Override
    public IHackathonBuilder buildRegulation(String regulation) {
        this.regulation = regulation;
        return this;
    }

    @Override
    public IHackathonBuilder buildOrganizer(Long organizerId) {
        this.organizerId = organizerId;
        return this;
    }

    @Override
    public IHackathonBuilder buildJudge(Long judgeId) {
        this.judgeId = judgeId;
        return this;
    }

    @Override
    public IHackathonBuilder buildMentors(List<Long> mentorsId) {
        this.mentorsId = mentorsId != null ? mentorsId : new ArrayList<>();
        return this;
    }

    @Override
    public IHackathonBuilder buildSubscriptionDates(Period subscriptionDates) {
        this.subscriptionDates = subscriptionDates;
        return this;
    }

    @Override
    public IHackathonBuilder buildDates(Period dates) {
        this.dates = dates;
        return this;
    }

    @Override
    public IHackathonBuilder buildDelivery(String delivery) {
        this.delivery = delivery;
        return this;
    }

    @Override
    public IHackathonBuilder buildRankingPolicy(RankingPolicy rankingPolicy) {
        this.rankingPolicy = rankingPolicy;
        return this;
    }

    @Override
    public Hackathon build() {
        Hackathon hackathon = new Hackathon(
                name,
                type,
                location,
                online,
                prize,
                maxTeamSize,
                regulation,
                organizerId,
                judgeId,
                mentorsId,
                delivery,
                rankingPolicy,
                subscriptionDates,
                dates
        );

        this.reset();
        return hackathon;
    }
}