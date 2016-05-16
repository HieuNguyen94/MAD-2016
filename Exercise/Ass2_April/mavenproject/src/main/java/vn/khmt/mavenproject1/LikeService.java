/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.khmt.mavenproject1;

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
@Path("like")
public class LikeService {
    ConnectToSQL database = new ConnectToSQL("postgresql",
            "ec2-54-83-56-177.compute-1.amazonaws.com",
            "de13q0g083tfgb",
            "fqwsjkzcltggbh",
            "ekCZ_XgNomkrUbEufK-JjHwtDN"
    );         
    
    @POST
    @Path("/create")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createLike(String input) throws JSONException {
        
        JSONObject msgBody = new JSONObject(input);
                        
        String username = msgBody.getString("username");
        String postId = msgBody.getString("postid");                
        
        ResponseMessage<String> resMsg = new ResponseMessage<String>();               
        
        boolean res = database.likePost(username, postId);
        if (res){
            resMsg.setSuccess("true");
            resMsg.setMessage("Create like done");
            return Response.status(Response.Status.CREATED).entity(resMsg).build();
        }
        else{
            resMsg.setSuccess("false");
            resMsg.setMessage("Create like failed");            
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(resMsg).build();
        }                                       
    }        
    
    @PUT
    @Path("/dellike")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dislike(String input) throws JSONException {

        JSONObject msgBody = new JSONObject(input);
                
        String username = msgBody.getString("username");
        String postId = msgBody.getString("postid");
        
        ResponseMessage<String> resMsg = new ResponseMessage<String>();          
        
        boolean res = database.disLikePost(username, postId);
        if (res){
            resMsg.setSuccess("true");
            resMsg.setMessage("Delete like done");
            return Response.status(Response.Status.CREATED).entity(resMsg).build();
        }
        else{
            resMsg.setSuccess("false");
            resMsg.setMessage("Delete like failed");            
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(resMsg).build();
        } 
    }
}
