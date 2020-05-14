package com.alakamandawalk.pkadmin.model;

public class MessageData {

    private String message, messageId, messageTime, storyId, storyName;

    public MessageData() {
    }

    public MessageData(String message, String messageId, String messageTime, String storyId, String storyName) {
        this.message = message;
        this.messageId = messageId;
        this.messageTime = messageTime;
        this.storyId = storyId;
        this.storyName = storyName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }
}
