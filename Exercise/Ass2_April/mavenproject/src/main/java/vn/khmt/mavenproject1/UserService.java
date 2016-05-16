/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.khmt.mavenproject1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
    @Path("/updateinfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUsername(String input) throws JSONException {
        
        JSONObject msgBody = new JSONObject(input);
        
        ResponseMessage<List<User>> resMsg = new ResponseMessage<List<User>>();
                
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

        resMsg.setSuccess("false");
        resMsg.setMessage("Update not done");
        
        if (!name.equals("")){
            query = "UPDATE public.user SET name = '" + name + "' WHERE username = '" + username + "';";
            res = database.executeSQL(query);
            if (!res) {                  
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();
            }
        }

        if ((!email.equals(""))){
            query = "UPDATE public.user SET email = '" + email + "' WHERE username = '" + username + "';";
            res = database.executeSQL(query);
            if (!res) {                    
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();
            }
        }

        if (!password.equals("")){
            query = "UPDATE public.user SET password = '" + password + "' WHERE username = '" + username + "';";
            res = database.executeSQL(query);
            if (!res) {                                            
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();
            }
        }

        if (!avatar.equals("")){
            query = "UPDATE public.user SET avatar = '" + avatar + "' WHERE username = '" + username + "';";
            res = database.executeSQL(query);
            if (!res) {                                            
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();
            }
        }

        if (!priority.equals("")){
            query = "UPDATE public.user SET priority = '" + priority + "' WHERE username = '" + username + "';";
            res = database.executeSQL(query);
            if (!res) {                                            
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();
            }
        }

        if (!profession.equals("")){
            query = "UPDATE public.user SET profession = '" + profession + "' WHERE username = '" + username + "';";
            res = database.executeSQL(query);
            if (!res) {                                            
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();
            }
        }

        if (!address.equals("")){
            query = "UPDATE public.user SET address = '" + address + "' WHERE username = '" + username + "';";
            res = database.executeSQL(query);
            if (!res) {                                            
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();
            }
        }

        if (!company.equals("")){
            query = "UPDATE public.user SET company = '" + company + "' WHERE username = '" + username + "';";
            res = database.executeSQL(query);
            if (!res) {                                            
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();
            }
        }

        resMsg.setSuccess("true");
        resMsg.setMessage("Update done");
        
        if (database.dbConnection != null){
            try {
                database.dbConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return Response.status(Response.Status.OK).entity(resMsg).build();
    }

    @POST
    @Path("/create")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createUser(String input) throws JSONException {
        
        JSONObject msgBody = new JSONObject(input);
        
        String username = msgBody.getString("username");        
        String password = msgBody.getString("password");
        String email = msgBody.getString("email");
        String name = msgBody.getString("name");
        String avatar = msgBody.getString("avatar");
        String profession = msgBody.getString("profession");
        String address = msgBody.getString("address");
        String company = msgBody.getString("company");        
        String priority = msgBody.getString("priority");
        if (priority.equals("")){
            priority = "2";
        }

        ResponseMessage<List<User>> resMsg = new ResponseMessage<List<User>>();
        
        if (database.checkUsername(username)) {
            System.out.println("Create done");
            String query = "INSERT INTO public.user(id, username, password, email, priority, name, avatar, profession, address, company) " + 
                    "SELECT MAX(t.id) + 1" + ",'" + username + "', '" + password + "','" + email + "','" + priority + "','" + name + "','" + avatar + "','" 
                    + profession + "','" + address + "','" + company + "' FROM public.user t;";
            boolean res = database.executeSQL(query);
            if (res) {
                resMsg.setSuccess("true");
                resMsg.setMessage("Create done");
                return Response.status(Response.Status.OK).entity(resMsg).build();
            }
        }
        
        resMsg.setSuccess("false");
        resMsg.setMessage("Create failed");
        
        if (database.dbConnection != null){
            try {
                database.dbConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return Response.status(Response.Status.OK).entity(resMsg).build();               
    }
    
    @POST
    @Path("/createsimple")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createUserSimple(String input) throws JSONException {
        
        JSONObject msgBody = new JSONObject(input);
        
        String username = msgBody.getString("username");
        String password = msgBody.getString("password");   
        
        ResponseMessage<List<User>> resMsg = new ResponseMessage<List<User>>();

        if (database.checkUsername(username)) {
            System.out.println("Create done");
            String query = "INSERT INTO public.user(id, username, password) " + "SELECT MAX(t.id) + 1" + ",'" + username + "', '" + password  + "' FROM public.user t;";
            boolean res = database.executeSQL(query);
            if (res) {
                resMsg.setSuccess("true");
                resMsg.setMessage("Create successfully");
                return Response.status(Response.Status.OK).entity(resMsg).build();
            }
        }
        
        resMsg.setSuccess("false");
        resMsg.setMessage("Username exist");
        
        if (database.dbConnection != null){
            try {
                database.dbConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return Response.status(Response.Status.OK).entity(resMsg).build();               
    }

    @POST
    @Path("/login")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response loginUser(String input) throws JSONException {
        
        JSONObject msgBody = new JSONObject(input);
        
        String username = msgBody.getString("username");
        String password = msgBody.getString("password");        

        ResponseMessage<String> resMsg = new ResponseMessage<String>();
        
        String avatar = database.login(username, password);
        if (!avatar.equals("")) {            
            resMsg.setSuccess("true");
            resMsg.setMessage("Login successfully");
            resMsg.setData(avatar);
            return Response.status(Response.Status.OK).entity(resMsg.create()).build();            
        }
        
        resMsg.setSuccess("false");
        resMsg.setMessage("Account not exist");                
        
        return Response.status(Response.Status.UNAUTHORIZED).entity(resMsg).build();               
    }
    
    @POST
    @Path("/checkusername")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response checkUsername(String input) throws JSONException {
        
        JSONObject msgBody = new JSONObject(input);
        
        String username = msgBody.getString("username");                

        ResponseMessage<List<User>> resMsg = new ResponseMessage<List<User>>();
        
        if (database.checkUsername(username)) {       
            resMsg.setSuccess("true");
            resMsg.setMessage("Username not exists");
            return Response.status(Response.Status.OK).entity(resMsg).build();            
        }
        
        resMsg.setSuccess("false");
        resMsg.setMessage("Username exists");
        
        if (database.dbConnection != null){
            try {
                database.dbConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return Response.status(Response.Status.UNAUTHORIZED).entity(resMsg).build();               
    }
    
    @POST
    @Path("/checkemail")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response checkEmail(String input) throws JSONException {
        
        JSONObject msgBody = new JSONObject(input);
        
        String email = msgBody.getString("email");                

        ResponseMessage<List<User>> resMsg = new ResponseMessage<List<User>>();        
        
        if (database.checkEmail(email)) {   
            resMsg.setSuccess("true");
            resMsg.setMessage("Email not exists");
            return Response.status(Response.Status.OK).entity(resMsg).build();            
        }
        
        resMsg.setSuccess("false");
        resMsg.setMessage("Email exists");
        
        if (database.dbConnection != null){
            try {
                database.dbConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return Response.status(Response.Status.UNAUTHORIZED).entity(resMsg).build();               
    }        
    
    @POST
    @Path("/getuserinfo")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserInfo(String input) throws JSONException {
        
        JSONObject msgBody = new JSONObject(input);
        
        String userName = msgBody.getString("username"); 
        
        ResultSet result = database.getUser(userName);

        ResponseMessage<User> resMsg = new ResponseMessage<User>();
        
        if (result != null) {
            try {         
                User user = new User();
                String avatar = result.getString("avatar");
                String email = result.getString("email");
                String password = result.getString("password");
                String address = result.getString("address");
                user.setAvatar(avatar);
                user.setEmail(email);
                user.setPassword(password);
                user.setAddress(address);
                resMsg.setSuccess("true");
                resMsg.setMessage("Get user info successfully");
                resMsg.setData(user);
                return Response.status(Response.Status.OK).entity(resMsg.create()).build();                
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        resMsg.setSuccess("false");
        resMsg.setMessage("Get user info failed");
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(resMsg).build();               
    }
}
