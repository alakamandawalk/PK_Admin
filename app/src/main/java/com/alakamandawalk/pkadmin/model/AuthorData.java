package com.alakamandawalk.pkadmin.model;

public class AuthorData {

    String authorId, authorName, authorPost , authorDescription, authorCoverImage , authorProfileImage;

    public AuthorData() {
    }

    public AuthorData(String authorId, String authorName, String authorPost, String authorDescription, String authorCoverImage, String authorProfileImage) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorPost = authorPost;
        this.authorDescription = authorDescription;
        this.authorCoverImage = authorCoverImage;
        this.authorProfileImage = authorProfileImage;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorPost() {
        return authorPost;
    }

    public void setAuthorPost(String authorPost) {
        this.authorPost = authorPost;
    }

    public String getAuthorDescription() {
        return authorDescription;
    }

    public void setAuthorDescription(String authorDescription) {
        this.authorDescription = authorDescription;
    }

    public String getAuthorCoverImage() {
        return authorCoverImage;
    }

    public void setAuthorCoverImage(String authorCoverImage) {
        this.authorCoverImage = authorCoverImage;
    }

    public String getAuthorProfileImage() {
        return authorProfileImage;
    }

    public void setAuthorProfileImage(String authorProfileImage) {
        this.authorProfileImage = authorProfileImage;
    }
}
