/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.khmt.mavenproject1;

import com.google.gson.Gson;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author TRONGNGHIA
 */
@Path("post")
public class PostService {
    ConnectToSQL database = new ConnectToSQL("postgresql",
            "ec2-54-83-56-177.compute-1.amazonaws.com",
            "de13q0g083tfgb",
            "fqwsjkzcltggbh",
            "ekCZ_XgNomkrUbEufK-JjHwtDN"
    );         
    
    @GET
    @Path("/id/{param}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPost(@PathParam("param") int id) {       
        
        Post post = new Post();
        ResultSet result = database.getPost(id);        
        if (result != null) {
            try {                
                String content = result.getString("content");
                String time = result.getString("post_time");
                String userName = result.getString("user_name");
                post.setContent(content);
                post.setPostTime(time);
                post.setUserName(userName);
                return Response.status(Response.Status.OK).entity(post).build();                
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Failed").build();
    }
    
    @POST
    @Path("/getuserposts")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPostsFromUser(String input) throws JSONException {       
        
        JSONObject msgBody = new JSONObject(input);
        
        String userName = msgBody.getString("username");
        
        List posts = new ArrayList<Post>();        
        ResultSet result = database.getPostsFromUser(userName); 
        if (result != null){
        try {                
            do {
                Post post = new Post();
                String content = result.getString("content");
                String time = result.getString("post_time");
                String cardImage = result.getString("card_image");
                String id = result.getString("id");
                String numLike = result.getString("num_likes");
                String numComments = result.getString("num_comments");
                int likePost = result.getInt("like_post");
                if (likePost == 1){
                    post.setLikePost("true");
                }
                else{
                    post.setLikePost("false");
                }
                post.setContent(content);
                post.setPostTime(time);
                post.setCardImage(cardImage);
                post.setNumComments(numComments);
                post.setId(id);
                post.setNumLikes(numLike);
                posts.add(post);                
            }                
            while (result.next());
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        
//        GenericEntity<List<Post>> entity = new GenericEntity<List<Post>>(posts) {
//        };
        }
        
        ResponseMessage<List<Post>> resMsg = new ResponseMessage<List<Post>>();
        resMsg.setSuccess("true");
        resMsg.setMessage("Get posts from user successfully");
        resMsg.setData(posts);
              
        return Response.status(Response.Status.OK).entity(resMsg.create()).build();
    }
    
    @POST
    @Path("/create")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createPost(String input) throws JSONException {
        
        JSONObject msgBody = new JSONObject(input);
        
        String content = msgBody.getString("content");        
        String username = msgBody.getString("username");
        String cardimage = msgBody.getString("cardimage");
                
        System.out.println("Create done");
        String query = "INSERT INTO public.post(id, content, user_name, card_image) " + 
                "SELECT MAX(t.id) + 1" + ",'" + content + "', '" + username + "', '" + cardimage + " 'FROM public.post t;";
        
        ResponseMessage<List<Post>> resMsg = new ResponseMessage<List<Post>>();                
        
        boolean res = database.executeSQL(query);
        if (res) {
            resMsg.setSuccess("true");
            resMsg.setMessage("Create post successfully");
            return Response.status(Response.Status.CREATED).entity(resMsg).build();
        }        
        
        resMsg.setSuccess("false");
        resMsg.setMessage("Create post failed");
        
        if (database.dbConnection != null){
            try {
                database.dbConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return Response.status(Response.Status.NOT_IMPLEMENTED).entity(resMsg).build();               
    }
    
    @PUT
    @Path("/updatecontent")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updatePostContent(String input) throws JSONException {

        JSONObject msgBody = new JSONObject(input);
        
        String id = msgBody.getString("id");
        String content = msgBody.getString("content");        
        
        String query = "UPDATE public.post SET content = '" + content + "' WHERE id = '" + id + "';";
        
        ResponseMessage<List<Post>> resMsg = new ResponseMessage<List<Post>>();        
        
        boolean res = database.executeSQL(query);
        
        if (database.dbConnection != null){
            try {
                database.dbConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (res) {
            resMsg.setSuccess("true");
            resMsg.setMessage("Update post content successfully");           
            return Response.status(Response.Status.OK).entity(resMsg).build();
        } else {
            resMsg.setSuccess("false");
            resMsg.setMessage("Update post content failed"); 
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();
        }
    }
    
    @PUT
    @Path("/updatecardimage")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updatePostCard(String input) throws JSONException {

        JSONObject msgBody = new JSONObject(input);
        
        String id = msgBody.getString("id");
        String cardimage = msgBody.getString("cardimage");        
        
        String query = "UPDATE public.post SET card_image = '" + cardimage + "' WHERE id = '" + id + "';";
        
        ResponseMessage<List<Post>> resMsg = new ResponseMessage<List<Post>>();
        
        boolean res = database.executeSQL(query);
        
        if (database.dbConnection != null){
            try {
                database.dbConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (res) {
            resMsg.setSuccess("true");
            resMsg.setMessage("Update post card image successfully");
            return Response.status(Response.Status.OK).entity(resMsg).build();
        } else {
            resMsg.setSuccess("false");
            resMsg.setMessage("Update post card image failed");
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();
        }
    }
    
    @PUT
    @Path("/delpost")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response delPost(String input) throws JSONException {

        JSONObject msgBody = new JSONObject(input);
                
        String id = msgBody.getString("id");

        String query = "DELETE FROM public.post WHERE id = '" + id + "';";
        
        ResponseMessage<List<Post>> resMsg = new ResponseMessage<List<Post>>();

        boolean res = database.executeSQL(query);
        
        if (database.dbConnection != null){
            try {
                database.dbConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (res) {
            resMsg.setSuccess("true");
            resMsg.setMessage("Delete post successfully");
            return Response.status(Response.Status.OK).entity(resMsg).build();
        }   
        else{
            resMsg.setSuccess("false");
            resMsg.setMessage("Delete post failed");
            return Response.status(Response.Status.OK).entity(resMsg).build();
        }        
    }
    
    @POST
    @Path("/getnewsfeed")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getNewsFeed(String input) throws JSONException {

        JSONObject msgBody = new JSONObject(input);
                
        String userName = msgBody.getString("username");
        int pageIndex = Integer.valueOf(msgBody.getString("pageindex"));
        int pageSize = Integer.valueOf(msgBody.getString("pagesize"));                       
        
        ResponseMessage<List<Post>> resMsg = new ResponseMessage<List<Post>>();        
        
        List posts = new ArrayList<Post>();       
//        List<PostFeed> feedsSub = new ArrayList<PostFeed>();
        ResultSet result = database.getNewsFeed(userName, pageIndex, pageSize);  
        
        try {                
            do {
                Post post = new Post();
                String content = result.getString("content");
                String time = result.getString("post_time");
                String cardImage = result.getString("card_image");
                String userNameResponse = result.getString("user_name");
                String avatar = result.getString("avatar");
                String numLikes = result.getString("num_likes");
                String numComments = result.getString("num_comments");
                String id = result.getString("id"); 
                int likePost = result.getInt("like_post");
                if (likePost == 1){
                    post.setLikePost("true");
                }
                else{
                    post.setLikePost("false");
                }
                post.setContent(content);
                post.setPostTime(time);
                post.setUserName(userNameResponse);
                post.setUserAvatar(avatar);
                post.setCardImage(cardImage); 
                post.setNumLikes(numLikes);
                post.setNumComments(numComments);
                post.setId(id);
                posts.add(post);                
            }                
            while (result.next());
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }                

        resMsg.setSuccess("true");
        resMsg.setMessage("Get newsfeed successfully");
        resMsg.setData(posts);
              
        return Response.status(Response.Status.OK).entity(resMsg.create()).build();
    }
    
    @POST
    @Path("/test")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response test() throws JSONException{
        Gson gson = new Gson();        
        ResponseMessage res = new ResponseMessage<Post>();
        //BEGIN
        List posts = new ArrayList<Post>();
        Post post = new Post();
        post.setId("1");
        post.setNumLikes("1");
        post.setCardImage("1");        
        posts.add(post);
        posts.add(post);                
        res.setSuccess("true");
        res.setMessage("create successfully");        
        res.setData(post);
        //END
        return Response.status(Response.Status.OK).entity(res.create()).build();
    }    
}
