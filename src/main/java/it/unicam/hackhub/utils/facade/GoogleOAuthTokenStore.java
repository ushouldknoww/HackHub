package it.unicam.hackhub.utils.facade;

public interface GoogleOAuthTokenStore {
    String findAccessTokenByStaffProfileId(Long staffProfileId);
}