package vn.khmt.db;

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
//                System.out.println("User exists");
//                String ans = rs.getString("username");
//                System.out.println(ans);
//                System.out.println("asdf");
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
//                System.out.println("User exists");
//                String ans = rs.getString("username");
//                System.out.println(ans);
//                System.out.println("asdf");
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
        return false;
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
            // execute insert SQL stetement
            if (preparedStatement != null) {
                int res = preparedStatement.executeUpdate();
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
        }
        return false;
    }

}
