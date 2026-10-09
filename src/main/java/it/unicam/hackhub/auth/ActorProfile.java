package it.unicam.hackhub.auth;

public record ActorProfile(Long id, String username, AccountRole role, String name, String surname) { }
