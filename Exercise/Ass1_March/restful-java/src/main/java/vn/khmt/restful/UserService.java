/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.khmt.restful;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import vn.khmt.db.ConnectToSQL;

/**
 *
 * @author HieuNguyen
 */
@Path("user")
public class UserService {

    ConnectToSQL database = new ConnectToSQL("postgresql",
            "ec2-54-83-56-177.compute-1.amazonaws.com",
            "de13q0g083tfgb",
            "fqwsjkzcltggbh",
            "ekCZ_XgNomkrUbEufK-JjHwtDN"
    ); 
    
    @GET
    @Path("/{param}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMsg(@PathParam("param") int id, @HeaderParam("Authorization") String auth) {

        byte[] authBytes = Base64.getDecoder().decode(auth.substring(6));
        String authString[] = new String(authBytes).split(":");

        boolean isAdmin = database.isAdmin(authString[0], authString[1]);

        User user = new User();
        ResultSet result = database.getUser(id);
        String username = "";
        String email = "";
        String password = "";
        if (result != null) {
            try {
                username = result.getString("username");
                email = result.getString("email");
                password = result.getString("password");
                if (isAdmin || (username.equals(authString[0]) && password.equals(authString[1]))) {                    
                    user.setId(Integer.parseInt(result.getString("id")));                    
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(result.getString("password"));                    
                    user.setPriority(result.getInt("priority"));
                    user.setName(result.getString("name"));
                    user.setAvatar(result.getString("avatar"));                    
                    return Response.status(Response.Status.OK).entity(user).build();
                }
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
    }

    @GET
    @Path("/all-old")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserListOld(@HeaderParam("Authorization") String auth) {

        byte[] authBytes = Base64.getDecoder().decode(auth.substring(6));
        String authString[] = new String(authBytes).split(":");

        if (database.isAdmin(authString[0], authString[1])) {
            List users = new ArrayList<User>();
            ResultSet result = database.getAllUsers();            
            try {
                do {                                        
                    User user = new User();
                    user.setId(Integer.parseInt(result.getString("id")));
                    user.setUsername(result.getString("username"));
                    user.setEmail(result.getString("email"));
                    user.setPassword(result.getString("password"));                    
                    user.setPriority(result.getInt("priority"));
                    user.setName(result.getString("name"));
                    user.setAvatar(result.getString("avatar"));
                    users.add(user);
                } while (result.next());
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }

            GenericEntity<List<User>> entity = new GenericEntity<List<User>>(users) {
            };

            return Response.status(Response.Status.OK).entity(entity).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Only admin can view this information").build();
        }
    }
    
    @GET
    @Path("/all")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserList() {       
        
        List users = new ArrayList<User>();
        ResultSet result = database.getAllUsers();            
        try {
            do {                                        
                User user = new User();
                user.setId(Integer.parseInt(result.getString("id")));
                user.setUsername(result.getString("username"));
                user.setEmail(result.getString("email"));
                user.setPassword(result.getString("password"));                    
                user.setPriority(result.getInt("priority"));
                user.setName(result.getString("name"));
                user.setAvatar(result.getString("avatar"));
                user.setProfession(result.getString("profession"));
                user.setAddress(result.getString("address"));
                user.setCompany(result.getString("company"));
                users.add(user);
            } while (result.next());
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }

        GenericEntity<List<User>> entity = new GenericEntity<List<User>>(users) {
        };

        return Response.status(Response.Status.OK).entity(entity).build();        
    }

    @PUT
    @Path("/rename")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateUsernameOld(@PathParam("param") int id, JsonObject msgBody) {

        String name = msgBody.getString("name");
        String username = msgBody.getString("username");
        String query = "UPDATE public.user SET name = '" + name + "' WHERE username = '" + username + "';";
        boolean res = database.executeSQL(query);
        if (res) {
            return Response.status(Response.Status.OK).entity("Update Successfully").build();
        } else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Update Not Successfully").build();
        }
    }
    
    @PUT
    @Path("/updateinfo")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateUsername(@HeaderParam("Authorization") String auth, JsonObject msgBody) {

        byte[] authBytes = Base64.getDecoder().decode(auth.substring(6));
        String authString[] = new String(authBytes).split(":");
        
        if (database.isAdmin(authString[0], authString[1])){
            String username = msgBody.getString("username"); 
            
            String name = msgBody.getString("name");    
            String email = msgBody.getString("email");
            String priority = msgBody.getString("priority");
            String password = msgBody.getString("password");
            String avatar = msgBody.getString("avatar");
            String profession = msgBody.getString("profession");
            String address = msgBody.getString("address");
            String company = msgBody.getString("company");
            
            String query;
            boolean res;
            
            if (!name.equals("")){
                query = "UPDATE public.user SET name = '" + name + "' WHERE username = '" + username + "';";
                res = database.executeSQL(query);
                if (!res) {                            
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Update Not Successfully").build();
                }
            }
            
            if ((!email.equals("") && database.checkEmail(email))){
                query = "UPDATE public.user SET email = '" + email + "' WHERE username = '" + username + "';";
                res = database.executeSQL(query);
                if (!res) {                            
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Update Not Successfully").build();
                }
            }
            
            if (!password.equals("")){
                query = "UPDATE public.user SET password = '" + password + "' WHERE username = '" + username + "';";
                res = database.executeSQL(query);
                if (!res) {                            
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Update Not Successfully").build();
                }
            }
            
            if (!avatar.equals("")){
                query = "UPDATE public.user SET avatar = '" + avatar + "' WHERE username = '" + username + "';";
                res = database.executeSQL(query);
                if (!res) {                            
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Update Not Successfully").build();
                }
            }
            
            if (!priority.equals("")){
                query = "UPDATE public.user SET priority = '" + priority + "' WHERE username = '" + username + "';";
                res = database.executeSQL(query);
                if (!res) {                            
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Update Not Successfully").build();
                }
            }
            
            if (!profession.equals("")){
                query = "UPDATE public.user SET profession = '" + profession + "' WHERE username = '" + username + "';";
                res = database.executeSQL(query);
                if (!res) {                            
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Update Not Successfully").build();
                }
            }
            
            if (!address.equals("")){
                query = "UPDATE public.user SET address = '" + address + "' WHERE username = '" + username + "';";
                res = database.executeSQL(query);
                if (!res) {                            
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Update Not Successfully").build();
                }
            }
            
            if (!company.equals("")){
                query = "UPDATE public.user SET company = '" + company + "' WHERE username = '" + username + "';";
                res = database.executeSQL(query);
                if (!res) {                            
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Update Not Successfully").build();
                }
            }
            
            return Response.status(Response.Status.OK).entity("Update Successfully").build();
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("Only admin can edit this information").build(); 
        }
    }

    @POST
    @Path("/create")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createUser(JsonObject msgBody) {
        
        String username = msgBody.getString("username");
        String password = msgBody.getString("password");
        String email = msgBody.getString("email");
        String name = msgBody.getString("name");
        String avatar = msgBody.getString("avatar");
        String profession = msgBody.getString("profession");
        String address = msgBody.getString("address");
        String company = msgBody.getString("company");

        if (database.createVerify(username, email)) {
            System.out.println("Create done");
            String query = "INSERT INTO public.user(id, username, password, email, priority, name, avatar, profession, address, company) " + 
                    "SELECT MAX(t.id) + 1" + ",'" + username + "', '" + password + "','" + email + "'," + 2 + ",'" + name + "','" + avatar + "','" 
                    + profession + "','" + address + "','" + company + "' FROM public.user t;";
            boolean res = database.executeSQL(query);
            if (res) {
                return Response.status(Response.Status.OK).entity("Y").build();
            }
        }
        
        return Response.status(Response.Status.OK).entity("N").build();               
    }
    
    @POST
    @Path("/createsimple")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createUserSimple(JsonObject msgBody) {
        
        String username = msgBody.getString("username");
        String password = msgBody.getString("password");   

        if (database.checkUsername(username)) {
            System.out.println("Create done");
            String query = "INSERT INTO public.user(id, username, password) " + "SELECT MAX(t.id) + 1" + ",'" + username + "', '" + password  + "' FROM public.user t;";
            boolean res = database.executeSQL(query);
            if (res) {
                return Response.status(Response.Status.OK).entity("Y").build();
            }
        }
        
        return Response.status(Response.Status.OK).entity("N").build();               
    }

    @POST
    @Path("/login")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response loginUser(JsonObject msgBody) {
        
        String username = msgBody.getString("username");
        String password = msgBody.getString("password");        

        if (database.accountExist(username, password)) {                                                
            return Response.status(Response.Status.OK).entity("OK").build();            
        }
        
        return Response.status(Response.Status.UNAUTHORIZED).entity("FAILED").build();               
    }
    
    @POST
    @Path("/checkusername")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response checkUsername(JsonObject msgBody) {
        
        String username = msgBody.getString("username");                

        if (database.checkUsername(username)) {                                                
            return Response.status(Response.Status.OK).entity("OK").build();            
        }
        
        return Response.status(Response.Status.UNAUTHORIZED).entity("Username exists").build();               
    }
    
    @POST
    @Path("/checkemail")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response checkEmail(JsonObject msgBody) {
        
        String email = msgBody.getString("email");                

        if (database.checkEmail(email)) {                                                
            return Response.status(Response.Status.OK).entity("OK").build();            
        }
        
        return Response.status(Response.Status.UNAUTHORIZED).entity("Email exists").build();               
    }
    
    @PUT
    @Path("/deluser")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response delUser(@HeaderParam("Authorization") String auth, JsonObject msgBody) {

        byte[] authBytes = Base64.getDecoder().decode(auth.substring(6));
        String authString[] = new String(authBytes).split(":");

        if (database.isAdmin(authString[0], authString[1])) {
            String username = msgBody.getString("username");
            
            String query = "DELETE FROM public.user WHERE username = '" + username + "';";

            boolean res = database.executeSQL(query);
            if (res) {
                return Response.status(Response.Status.OK).entity("Y").build();
            }   
            else{
                return Response.status(Response.Status.OK).entity("N").build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Only admin can do this").build();
        }
    }
}
