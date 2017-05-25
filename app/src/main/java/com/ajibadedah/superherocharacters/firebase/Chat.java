package com.ajibadedah.superherocharacters.firebase;

/**
 * Created by ajibade on 5/24/17
 */

public class Chat {
    private String textReference;
    private String textChat;
    private String name;
    private String photoUrl;

    public Chat() {
        // Needed for Firebase
    }

    public Chat(String textReference, String textChat, String name, String photoUrl) {
        this.textReference = textReference;
        this.textChat = textChat;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getTextReference() {
        return textReference;
    }

    public void setTextReference(String textReference) {
        this.textReference = textReference;
    }

    public String getTextChat() {
        return textChat;
    }

    public void setTextChat(String textChat) {
        this.textChat = textChat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
