package com.alakamandawalk.pkadmin.model;

public class StoryData {

    private String story, storyName, storyId, storyDate, storyImage;

    public StoryData() {

    }

    public StoryData(String story, String storyName, String storyId, String storyDate, String storyImage) {
        this.story = story;
        this.storyName = storyName;
        this.storyId = storyId;
        this.storyDate = storyDate;
        this.storyImage = storyImage;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getStoryDate() {
        return storyDate;
    }

    public void setStoryDate(String storyDate) {
        this.storyDate = storyDate;
    }

    public String getStoryImage() {
        return storyImage;
    }

    public void setStoryImage(String storyImage) {
        this.storyImage = storyImage;
    }
}
