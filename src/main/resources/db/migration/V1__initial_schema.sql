-- Initial relational schema for PostgreSQL. Follow-up changes should be added as V2, V3, ...
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(254) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    team_id BIGINT
);

CREATE TABLE staff_profiles (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL
);

CREATE TABLE auth_accounts (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(254) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    account_role VARCHAR(20) NOT NULL,
    subject_id BIGINT NOT NULL
);
CREATE INDEX idx_auth_account_subject ON auth_accounts(account_role, subject_id);

CREATE TABLE teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    leader_id BIGINT NOT NULL
);
CREATE INDEX idx_teams_leader ON teams(leader_id);
CREATE TABLE team_members (
    team_id BIGINT NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL,
    UNIQUE(team_id, user_id)
);

CREATE TABLE hackathons (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    is_online BOOLEAN NOT NULL DEFAULT FALSE,
    prize DOUBLE PRECISION NOT NULL,
    max_team_size INTEGER NOT NULL,
    regulation VARCHAR(2000) NOT NULL,
    organizer_id BIGINT NOT NULL,
    judge_id BIGINT NOT NULL,
    sub_start_at TIMESTAMP,
    sub_end_at TIMESTAMP,
    delivery VARCHAR(255) NOT NULL,
    ranking_policy VARCHAR(80) NOT NULL,
    event_start_at TIMESTAMP,
    event_end_at TIMESTAMP,
    status VARCHAR(40) NOT NULL,
    winner_participating_team_id BIGINT,
    payout_status VARCHAR(30),
    paid_at TIMESTAMP,
    provider_ref VARCHAR(255),
    failure_reason VARCHAR(255)
);
CREATE INDEX idx_hackathon_status ON hackathons(status);
CREATE INDEX idx_hackathon_start ON hackathons(event_start_at);
CREATE TABLE hackathon_mentors (
    hackathon_id BIGINT NOT NULL REFERENCES hackathons(id) ON DELETE CASCADE,
    staff_profile_id BIGINT NOT NULL
);

CREATE TABLE participating_teams (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(id),
    hackathon_id BIGINT NOT NULL REFERENCES hackathons(id),
    contact_email VARCHAR(255) NOT NULL,
    payout_method VARCHAR(40),
    payout_value VARCHAR(255),
    registered_at TIMESTAMP NOT NULL,
    disqualified BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_part_team_hackathon UNIQUE(team_id, hackathon_id)
);
CREATE TABLE pt_active_members (
    participating_team_id BIGINT NOT NULL REFERENCES participating_teams(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL
);
CREATE TABLE pt_penalties (
    participating_team_id BIGINT NOT NULL REFERENCES participating_teams(id) ON DELETE CASCADE,
    points INTEGER NOT NULL,
    reason VARCHAR(500) NOT NULL,
    assigned_at TIMESTAMP NOT NULL,
    report_id BIGINT NOT NULL
);

CREATE TABLE invitations (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(id),
    invitee_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL
);
CREATE INDEX idx_invitations_invitee_status ON invitations(invitee_id, status);

CREATE TABLE submissions (
    id BIGSERIAL PRIMARY KEY,
    hackathon_id BIGINT NOT NULL REFERENCES hackathons(id),
    participating_team_id BIGINT NOT NULL UNIQUE REFERENCES participating_teams(id),
    response VARCHAR(10000) NOT NULL,
    response_url VARCHAR(500) NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE TABLE evaluations (
    id BIGSERIAL PRIMARY KEY,
    submission_id BIGINT NOT NULL UNIQUE REFERENCES submissions(id) ON DELETE CASCADE,
    score INTEGER NOT NULL,
    comment VARCHAR(2000)
);

CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,
    hackathon_id BIGINT NOT NULL REFERENCES hackathons(id),
    mentor_id BIGINT NOT NULL,
    participating_team_id BIGINT NOT NULL REFERENCES participating_teams(id),
    reason VARCHAR(500) NOT NULL,
    urgency VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    report_resolution VARCHAR(40)
);

CREATE TABLE support_requests (
    id BIGSERIAL PRIMARY KEY,
    hackathon_id BIGINT NOT NULL REFERENCES hackathons(id),
    participating_team_id BIGINT NOT NULL REFERENCES participating_teams(id),
    title VARCHAR(150) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    urgency VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL,
    reply_mentor_id BIGINT,
    reply_message VARCHAR(2000),
    reply_answered_at TIMESTAMP,
    call_mentor_id BIGINT,
    call_starts_at TIMESTAMP,
    call_duration NUMERIC(21),
    call_event_id VARCHAR(255),
    call_meeting_url VARCHAR(255),
    call_created_at TIMESTAMP
);

CREATE TABLE audit_events (
    id BIGSERIAL PRIMARY KEY,
    actor_id BIGINT,
    actor_role VARCHAR(20),
    http_method VARCHAR(10) NOT NULL,
    request_path VARCHAR(500) NOT NULL,
    operation VARCHAR(120) NOT NULL,
    success BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_audit_created_at ON audit_events(created_at);
CREATE INDEX idx_audit_actor ON audit_events(actor_id);
