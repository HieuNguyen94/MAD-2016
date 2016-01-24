/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.khmt.restful;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
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
    public Response getMsg(@PathParam("param") int id) {

        String output = "UNKNOWN";
        ResultSet result =  database.getUser(id);
        String username = "";
        String email = "";
        if (result != null) {
            try {
                username = result.getString("username");
                email = result.getString("email");
                output = "name: " + username + "</br>" + "email: " + email;
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Response.status(200).entity(output).build();
    }
    
}
