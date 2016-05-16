package vn.khmt.mavenproject1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author LUONG The Nhan
 * @version 0.1
 */
public class ConnectToSQL {

    public static final String ERROR = "Error";
    public static final String NOTMATCH = "NotMatch";
    public static final String SQLSERVER = "sqlserver";
    public static final String SQLSERVERDRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String MYSQL = "mysql";
    public static final String MYSQLDRIVER = "com.mysql.jdbc.Driver";
    public static final String POSTGRESQL = "postgresql";
    public static final String POSTGRESQLDRIVER = "org.postgresql.Driver";

    Connection dbConnection = null;

    public ConnectToSQL(String type, String host, String dbname, String user, String pwd) {
        this.dbConnection = getDBConnection(type, host, dbname, user, pwd);
    }

    private Connection getDBConnection(String type, String host, String dbname, String user, String pwd) {
        if (type != null && !type.isEmpty()) {
            try {
                if (type.equalsIgnoreCase(SQLSERVER)) {
                    Class.forName(SQLSERVERDRIVER);
                    dbConnection = DriverManager.getConnection(host + ";database=" + dbname + ";sendStringParametersAsUnicode=true;useUnicode=true;characterEncoding=UTF-8;", user, pwd);
                } else if (type.equalsIgnoreCase(MYSQL)) {
                    Class.forName(MYSQLDRIVER);
                    dbConnection = DriverManager.getConnection(host + "/" + dbname, user, pwd);
                } else if (type.equalsIgnoreCase(POSTGRESQL)) {
                    Class.forName(POSTGRESQLDRIVER);
                    dbConnection = DriverManager.getConnection("jdbc:postgresql://" + host + ":5432/" + dbname + "?sslmode=require&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", user, pwd);
                }
                return dbConnection;
            } catch (ClassNotFoundException | SQLException ex) {
                String mess = ex.getMessage();
                System.err.println(ex.getMessage());
            }
        }
        return dbConnection;
    }
    
    public ResultSet getUser(int id) {
        try {
            String SQL = "SELECT * FROM public.user WHERE id = " + id + ";";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            if (rs.next()) {
                return rs;
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return null;
    }
    
    public ResultSet getUser(String username) {
        try {
            String SQL = "SELECT avatar, email, address, password FROM public.user WHERE username = '" + username + "';";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            if (rs.next()) {
                return rs;
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return null;
    }
    
    public ResultSet getAllUsers() {
        try {
            String SQL = "SELECT * FROM public.user";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            if (rs.next()) {
                return rs;
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return null;
    }

    public boolean isAdmin(String username, String password) {
        try {
            String SQL = "SELECT * FROM public.user WHERE priority = '1'";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            while (rs.next()) {
                if (rs.getString("username").equals(username) && rs.getString("password").equals(password)){
                    return true;
                }
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return false;
    }
    
    public boolean accountExist(String username, String password) {
        try {
            String SQL = "SELECT * FROM public.user";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            while (rs.next()) {
                if (rs.getString("username").equals(username) && rs.getString("password").equals(password)){
                    return true;
                }
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } 
//        finally {
//            if (this.dbConnection != null) {
//                try {
//                    this.dbConnection.close();
//                } catch (SQLException sqle) {
//                    System.err.println(sqle.getMessage());
//                }
//            }
//        }
        return false;
    }
    
    public String getAvatar(String userName){
        try {
            String SQL = "SELECT avatar FROM public.user WHERE username = '" + userName + "';";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            while (rs.next()) {
                return rs.getString("avatar");
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return "";
    }
    
    public boolean createVerify(String username, String email) {
        try {
            String SQL = "SELECT username, email FROM public.user";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            while (rs.next()) {
                if (rs.getString("username").equals(username) || rs.getString("email").equals(email)){
                    return false;
                }
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return true;
    }
    
    public boolean checkUsername(String username) {
        try {
            String SQL = "SELECT username FROM public.user";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            while (rs.next()) {
                if (rs.getString("username").equals(username)){
                    return false;
                }
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } 
//        finally {
//            if (this.dbConnection != null) {
//                try {
//                    this.dbConnection.close();
//                } catch (SQLException sqle) {
//                    System.err.println(sqle.getMessage());
//                }
//            }
//        }
        return true;
    }
    
    public boolean checkEmail(String email) {
        try {
            String SQL = "SELECT email FROM public.user";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            while (rs.next()) {
                if (rs.getString("email").equals(email)){
                    return false;
                }
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } 
//        finally {
//            if (this.dbConnection != null) {
//                try {
//                    this.dbConnection.close();
//                } catch (SQLException sqle) {
//                    System.err.println(sqle.getMessage());
//                }
//            }
//        }
        return true;
    }
    
    public String checkUser(String username, String password) {
        try {
            if (username != null && password != null) {
                String SQL = "SELECT u.name, u.priority FROM user u WHERE u.username = '" + username + "' AND u.password = '" + password + "';";
                Statement stmt = this.dbConnection.createStatement();
                ResultSet rs = stmt.executeQuery(SQL);

                // Iterate through the data in the result set and display it.  
                if (rs.next()) {
                    if (rs.getInt(2) == 1) {
                        return rs.getString(1);
                    } else {
                        return ERROR;
                    }
                } else {
                    return NOTMATCH;
                }
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return null;
    }

    private static Timestamp getTimeStampOfDate(Date date) {
        if (date != null) {
            return new Timestamp(date.getTime());
        }
        return null;
    }

    public boolean executeSQL(String sql) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = this.dbConnection.prepareStatement(sql);            
            if (preparedStatement != null) {
                int res = preparedStatement.executeUpdate();                
                // execute insert SQL stetement                
                return res == 1;                
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
//            if (this.dbConnection != null) {
//                try {
//                    this.dbConnection.close();
//                } catch (SQLException sqle) {
//                    System.err.println(sqle.getMessage());
//                }
//            }
        }
        return false;
    }        
    
    public boolean likePost(String username, String postId){    
        boolean ret = false;
        String query = "INSERT INTO public.likes(username, post_id) VALUES ('" + 
                username + "', " + postId + ");";
        boolean res = executeSQL(query);
        if (res) {            
            query = "UPDATE public.post SET num_likes = num_likes + 1 WHERE id = '" + postId + "';";
            res = executeSQL(query);
            if (res){
                ret = true;
            }            
        }                
        if (this.dbConnection != null) {
            try {
                this.dbConnection.close();
            } catch (SQLException sqle) {
                System.err.println(sqle.getMessage());
            }
        }
        return ret;
    }
    
    public boolean disLikePost(String username, String postId){    
        boolean ret = false;
        String query = "DELETE FROM public.likes WHERE post_id = '" + postId + "' AND username = '" + username + "';";
        boolean res = executeSQL(query);
        if (res) {            
            query = "UPDATE public.post SET num_likes = num_likes - 1 WHERE id = '" + postId + "';";
            res = executeSQL(query);
            if (res){
                ret = true;
            }            
        }                
        if (this.dbConnection != null) {
            try {
                this.dbConnection.close();
            } catch (SQLException sqle) {
                System.err.println(sqle.getMessage());
            }
        }
        return ret;
    }
    
    public ResultSet getPost(int id) {
        try {
            String SQL = "SELECT * FROM public.post WHERE id = " + id + ";";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            if (rs.next()) {
                return rs;
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return null;
    }
    
    public ResultSet getPostsFromUser(String userName) {        
        try {
            String SQL0 = "UPDATE public.post SET like_post = 0 WHERE true;";
            executeSQL(SQL0);
            String SQL1 = "SELECT post_id FROM public.likes" + " WHERE username = '" + userName + "';";                       
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs1 = stmt.executeQuery(SQL1);
            while (rs1.next()){
                String SQL3 = "UPDATE public.post SET like_post = 1 WHERE id = '" + rs1.getString("post_id") + "';";
                executeSQL(SQL3);
            }            
            String SQL2 = "SELECT * FROM public.post WHERE user_name = '" + userName + "' ORDER BY post_time DESC;";
            
            ResultSet rs = stmt.executeQuery(SQL2);
            // Iterate through the data in the result set and display it.  
            if (rs.next()) {
                return rs;
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } 
        finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return null;
    }
    
    public ResultSet getNewsFeed(String userName, int pageIndex, int pageSize) {
        try {
            String SQL0 = "UPDATE public.post SET like_post = 0 WHERE true;";
            executeSQL(SQL0);
            String SQL1 = "SELECT post_id FROM public.likes" + " WHERE username = '" + userName + "';";                       
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs1 = stmt.executeQuery(SQL1);
            while (rs1.next()){
                String SQL3 = "UPDATE public.post SET like_post = 1 WHERE id = '" + rs1.getString("post_id") + "';";
                executeSQL(SQL3);
            }            
            String SQL2 = "SELECT p.id, p.num_likes, p.content, p.post_time, p.card_image, p.user_name, p.like_post, p.num_comments, u.avatar FROM public.post p, public.user u " + " WHERE p.user_name = u.username ORDER BY p.post_time DESC LIMIT " + pageSize + "OFFSET " + pageIndex * pageSize + ";";
            
            ResultSet rs = stmt.executeQuery(SQL2);
            // Iterate through the data in the result set and display it.  
            if (rs.next()) {
                return rs;
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } 
        finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return null;
    }

    public boolean checkLikePost(String userName, String postId){
        try{
            String SQL = "SELECT username FROM public.likes" + " WHERE username = '" + userName + "' AND post_id = '" + postId + "';";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            if (rs.next()){
                return true;
            }            
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } 
        return false;
    }
    
    public ResultSet getCommentsAtPost(String postId) {
        try {
            String SQL = "SELECT c.id, c.post_time, c.user_name, c.content, u.avatar FROM public.comment c, public.user u WHERE c.user_name = u.username AND post_id = '" + postId + "' ORDER BY c.post_time DESC;";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            if (rs.next()) {
                return rs;
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return null;
    }

    public boolean createComment(String username, String postId, String content){    
        boolean ret = false;
        String query = "INSERT INTO public.comment(id, user_name, post_id, content) " + "SELECT MAX(t.id) + 1, " + 
                "'" + username + "', " + postId + ", '" + content + "' FROM public.comment t;";
        boolean res = executeSQL(query);
        if (res) {            
            query = "UPDATE public.post SET num_comments = num_comments + 1 WHERE id = '" + postId + "';";
            res = executeSQL(query);
            if (res){
                ret = true;
            }            
        }                
        if (this.dbConnection != null) {
            try {
                this.dbConnection.close();
            } catch (SQLException sqle) {
                System.err.println(sqle.getMessage());
            }
        }
        return ret;
    }
    
    public boolean delComment(String id){    
        boolean ret = false;
        String query = "UPDATE public.post SET num_comments = num_comments - 1 WHERE id = " 
                    + "(SELECT post_id FROM comment WHERE id = '" + id + "');";
        boolean res = executeSQL(query);
        if (res) {            
            query = "DELETE FROM public.comment WHERE id = '" + id + "';";
            res = executeSQL(query);
            if (res){
                ret = true;
            }            
        }                
        if (this.dbConnection != null) {
            try {
                this.dbConnection.close();
            } catch (SQLException sqle) {
                System.err.println(sqle.getMessage());
            }
        }
        return ret;
    }

    public String login(String username, String password) {        
        try {
            String SQL = "SELECT * FROM public.user";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            
            // Iterate through the data in the result set and display it.  
            while (rs.next()) {
                if (rs.getString("username").equals(username) && rs.getString("password").equals(password)){                    
                    return rs.getString("avatar");
                }
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } 
        finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return "";
    }
    
}
