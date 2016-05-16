/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.khmt.mavenproject1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author TRONGNGHIA
 */
@Path("comment")
public class CommentService {
    ConnectToSQL database = new ConnectToSQL("postgresql",
            "ec2-54-83-56-177.compute-1.amazonaws.com",
            "de13q0g083tfgb",
            "fqwsjkzcltggbh",
            "ekCZ_XgNomkrUbEufK-JjHwtDN"
    );         
    
    @POST
    @Path("/getcommentsatpost")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCommentsAtPost(String input) throws JSONException {       
        
        JSONObject msgBody = new JSONObject(input);
        
        String postId = msgBody.getString("postid");        
        ResponseMessage<List<Comment>> resMsg = new ResponseMessage<List<Comment>>();
        
        List comments = new ArrayList<Comment>();        
        ResultSet result = database.getCommentsAtPost(postId);
        if (result != null){
        try {                
            do {
                Comment comment = new Comment();
                String id = result.getString("id");
                String userName = result.getString("user_name");                
                String content = result.getString("content");
                String time = result.getString("post_time");    
                String avatar = result.getString("avatar");
                comment.setId(id);                
                comment.setUserName(userName);
                comment.setContent(content);
                comment.setPostTime(time);     
                comment.setUserAvatar(avatar);
                comments.add(comment);                
            }                
            while (result.next());
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        
//        GenericEntity<List<Comment>> entity = new GenericEntity<List<Comment>>(comments) {
//        };
        }
        resMsg.setSuccess("true");
        resMsg.setMessage("Get comments successfully");
        resMsg.setData(comments);
              
        return Response.status(Response.Status.OK).entity(resMsg.create()).build();
    }
    
    @POST
    @Path("/create")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createComment(String input) throws JSONException {
        
        JSONObject msgBody = new JSONObject(input);
        
        String postId = msgBody.getString("postid");
        String content = msgBody.getString("content");        
        String username = msgBody.getString("username");        

        ResponseMessage<List<Comment>> resMsg = new ResponseMessage<List<Comment>>();
        
        boolean res = database.createComment(username, postId, content);
        if (res){
            resMsg.setSuccess("true");
            resMsg.setMessage("Create comment done");
            return Response.status(Response.Status.CREATED).entity(resMsg).build();
        }
        else{
            resMsg.setSuccess("false");
            resMsg.setMessage("Create comment failed");            
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(resMsg).build();
        }               
    }
    
    @PUT
    @Path("/update")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updatePost(String input) throws JSONException {

        JSONObject msgBody = new JSONObject(input);
        
        String id = msgBody.getString("id");        
        String content = msgBody.getString("content");        
        String query = "UPDATE public.comment SET content = '" + content + "' WHERE id = '" + id + "';";
        
        ResponseMessage<List<Comment>> resMsg = new ResponseMessage<List<Comment>>();
        
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
            resMsg.setMessage("Update comment successfully");
            return Response.status(Response.Status.OK).entity(resMsg).build();
        } else {
            resMsg.setSuccess("false");
            resMsg.setMessage("Update comment failed");
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();
        }
    }
    
    @PUT
    @Path("/delcomment")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response delComment(String input) throws JSONException {

        JSONObject msgBody = new JSONObject(input);
                        
        String id = msgBody.getString("id");       
        
        ResponseMessage<String> resMsg = new ResponseMessage<String>();
        
        boolean res = database.delComment(id);
        if (res){
            resMsg.setSuccess("true");
            resMsg.setMessage("Delete comment done");
            return Response.status(Response.Status.CREATED).entity(resMsg).build();
        }
        else{
            resMsg.setSuccess("false");
            resMsg.setMessage("Delete comment failed");            
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(resMsg).build();
        }        
    }
}
