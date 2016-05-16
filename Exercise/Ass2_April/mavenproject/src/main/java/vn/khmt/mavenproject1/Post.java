/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.khmt.mavenproject1;

/**
 *
 * @author TRONGNGHIA
 */
public class Post {
    private String id;
    private String content;
    private String postTime;
    private String cardImage;
    private String userName;
    private String numLikes;
    private String numComments;
    private String userAvatar;
    private String likePost;
    
    public Post(){
        
    }
    
    public Post(String id, String content, String postTime, String userName){
        this.id = id;
        this.content = content;
        this.postTime = postTime;
        this.userName = userName;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the postTime
     */
    public String getPostTime() {
        return postTime;
    }

    /**
     * @param postTime the postTime to set
     */
    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    /**
     * @return the user_id
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the cardImage
     */
    public String getCardImage() {
        return cardImage;
    }

    /**
     * @param cardImage the cardImage to set
     */
    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }

    /**
     * @return the numLikes
     */
    public String getNumLikes() {
        return numLikes;
    }

    /**
     * @param numLikes the numLikes to set
     */
    public void setNumLikes(String numLikes) {
        this.numLikes = numLikes;
    }

    /**
     * @return the userAvatar
     */
    public String getUserAvatar() {
        return userAvatar;
    }

    /**
     * @param userAvatar the userAvatar to set
     */
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    /**
     * @return the likePost
     */
    public String getLikePost() {
        return likePost;
    }

    /**
     * @param likePost the likePost to set
     */
    public void setLikePost(String likePost) {
        this.likePost = likePost;
    }

    /**
     * @return the numComments
     */
    public String getNumComments() {
        return numComments;
    }

    /**
     * @param numComments the numComments to set
     */
    public void setNumComments(String numComments) {
        this.numComments = numComments;
    }
}
