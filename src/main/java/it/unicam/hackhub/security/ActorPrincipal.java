package it.unicam.hackhub.security;

import it.unicam.hackhub.auth.AccountRole;

public final class ActorPrincipal {
    private final Long accountId;
    private final Long subjectId;
    private final String username;
    private final AccountRole role;

    public ActorPrincipal(Long accountId, Long subjectId, String username, AccountRole role) {
        this.accountId = accountId;
        this.subjectId = subjectId;
        this.username = username;
        this.role = role;
    }

    public Long getAccountId() { return accountId; }
    public Long getSubjectId() { return subjectId; }
    public String getUsername() { return username; }
    public AccountRole getRole() { return role; }
}
