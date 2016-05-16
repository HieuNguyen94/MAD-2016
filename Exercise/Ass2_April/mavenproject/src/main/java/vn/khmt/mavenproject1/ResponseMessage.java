/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.khmt.mavenproject1;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import org.codehaus.jettison.json.JSONException;

/**
 *
 * @author TRONGNGHIA
 */
public class ResponseMessage<T> {
    private String success;
    private String message;    
    private T data;    
    /**
     * @return the success
     */
    public String getSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(String success) {
        this.success = success;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }       

    /**
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }
    
    public String create() throws JSONException{
        Gson gson = new Gson();
        Type listType = new TypeToken<T>(){}.getType();
        JsonElement je = gson.toJsonTree(this.data, listType);        
        return ("{\"success\": \"" + this.success + "\", \"message\": \"" + this.message + "\", \"data\": " + je + "}");
    }
}
