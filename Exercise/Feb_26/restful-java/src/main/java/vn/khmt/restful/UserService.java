/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.khmt.restful;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import jdk.nashorn.internal.parser.JSONParser;
import vn.khmt.db.ConnectToSQL;

/**
 *
 * @author HieuNguyen
 */
@Path("user")
public class UserService {
    
    ConnectToSQL database = new ConnectToSQL("postgresql", 
            "ec2-54-227-253-228.compute-1.amazonaws.com", 
            "d8viikojj42e3b",
            "uzufecmqojhnyx",
            "WPJGueUbd3npLKslU2BEUOmMHx"
    );
    
    @POST
    @Path("/add")
    public Response postMsg(@QueryParam("username") String username,
                    @QueryParam("password") String password,
                    @QueryParam("email") String email,
                    @QueryParam("status") String status,
                   @QueryParam("name") String name) {
        String query = "INSERT INTO public.user(id, username, password, email, status, name) " + "SELECT MAX(t.id) + 1" + ",'" + username + "', '" + password + "','" + email + "'," + status + ",'" + name + "' FROM public.user t;";
        database.executeSQL(query);
        return Response.status(201).entity("OK").build();
    }
    
    @GET
    @Path("/{param}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMsg(@PathParam("param") int id, @HeaderParam("Authorization") String auth){
        
        byte[] authBytes = Base64.getDecoder().decode(auth.substring(6));         
        String authString[] = new String(authBytes).split(":");
        
        boolean isAdmin = database.isAdmin(authString[0], authString[1]);
        
        User user = new User();
        ResultSet result =  database.getUser(id);        
        String username = "";
        String email = "";
        String password = "";
        if (result != null) {
            try {
                username = result.getString("username");
                email = result.getString("email");
                password = result.getString("password");                       
                if (isAdmin || (username.equals(authString[0]) && password.equals(authString[1]))){                    
                    user.setUsername(username);
                    user.setEmail(email);
                    return Response.status(Response.Status.OK).entity(user).build();  
                }                
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }   
        return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
    }
    
    @GET
    @Path("/all")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserList(@HeaderParam("Authorization") String auth) {
        
        byte[] authBytes = Base64.getDecoder().decode(auth.substring(6));         
        String authString[] = new String(authBytes).split(":");
                
        if (database.isAdmin(authString[0], authString[1])){
            List users = new ArrayList<User>();
            ResultSet result =  database.getAllUsers();
            String username = "";
            String email = "";                        
            try {
                while (result.next()) {                
                    username = result.getString("username");
                    email = result.getString("email");
                    User user =  new User();
                    user.setId(Integer.parseInt(result.getString("id")));
                    user.setUsername(username);
                    user.setPassword(result.getString("password"));
                    user.setEmail(email);
                    user.setStatus(result.getString("status"));
                    user.setName(result.getString("name"));
                    users.add(user);                                                
                }
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }                  

            GenericEntity<List<User>> entity = new GenericEntity<List<User>>(users) {};

            return Response.status(Response.Status.OK).entity(entity).build();
        }
        else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Only admin can view this information").build();
        }               
    }
    
    @PUT
    @Path("/rename")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateUsername(@PathParam("param") int id, JsonObject msgBody) {
                       
        String name = msgBody.getString("name");
        String username = msgBody.getString("username");
        String query = "UPDATE public.user SET name = '" + name + "' WHERE username = '" + username + "';";
        boolean res = database.executeSQL(query);
        if (res){
            return Response.status(Response.Status.OK).entity("Update Successfully").build();
        }
        else{
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Update Not Successfully").build();
        }
    }
    
}
