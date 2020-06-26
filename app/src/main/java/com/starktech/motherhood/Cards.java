package com.starktech.motherhood;

public class Cards {
    private String userId;
    private String cardname;
    private String profileImageUrl;
    private String about;


    public Cards(String userId, String cardname,String profileImageUrl, String about) {
        this.userId = userId;
        this.cardname = cardname;
        this.profileImageUrl=profileImageUrl;
        this.about=about;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardname() {
        return cardname;
    }

    public void setCardname(String cardname) {
        this.cardname = cardname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
