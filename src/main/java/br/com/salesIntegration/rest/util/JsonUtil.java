package br.com.salesIntegration.rest.util;

import java.io.Serializable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

public class JsonUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	public static JSONObject objectToJson(Object object) throws ParseException{
		Gson gson = new Gson();
	    String jsonString = gson.toJson(object);
	    JSONParser parser = new JSONParser();
	    return (JSONObject) parser.parse(jsonString);	     
	}
}
