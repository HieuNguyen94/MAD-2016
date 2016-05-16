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
public class Comment {
    
    private String id;
    private String userName;
    private String postId;
    private String content;
    private String postTime;    
    private String userAvatar;
    
    public Comment(){
        
    }
    
    public Comment(String id, String userName, String postId, String content, String postTime, String dislike){
        this.id = id;
        this.userName = userName;
        this.postId = postId;
        this.content = content;
        this.postTime = postTime;        
    }

    /**
     * @return the userName
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
     * @return the postId
     */
    public String getPostId() {
        return postId;
    }

    /**
     * @param postId the postId to set
     */
    public void setPostId(String postId) {
        this.postId = postId;
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
}
