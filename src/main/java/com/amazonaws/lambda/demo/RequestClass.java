package com.amazonaws.lambda.demo;

import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class RequestClass {

	String methodCode;
	HashMap<String, Object> params = new HashMap<String, Object>();
	String methodName;
	String httpMethod;
	String testCode;
	List editable;
	List shown;
	
	public List getEditable() {
		return editable;
	}

	public List getShown() {
		return shown;
	}
	

	public void setEditable(List editable) {
		this.editable = editable;
	}

	public void setShown(List shown) {
		this.shown = shown;
	}

	

	
	public String getTestCode() {
		return testCode;
	}

	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public HashMap<String, Object> getParams() {
		return params;
	}

	public void setParams(HashMap<String, Object> params) {
		this.params = params;
	}

	public String getMethodCode() {
		return methodCode;
	}

	public void setMethodCode(String methodCode) {
		this.methodCode = methodCode;
	}

	public RequestClass(String methodCode, HashMap<String, Object> params) {
		this.methodCode = methodCode;
		this.params = params;
	}
	
	public RequestClass(String json) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(json);
        if(jsonTree.isJsonObject()) {
        	JsonObject obj = jsonTree.getAsJsonObject();
        	this.testCode=obj.get("shown").getAsJsonObject().get("0").getAsString();
            this.methodCode=obj.get("editable").getAsJsonObject().get("0").getAsString();
        }
        
        /*System.out.println("editable==>"+request.getEditable());
		System.out.println("shown=="+request.getShown());
        this.methodCode = request.getMethodCode();
        this.methodName=request.getMethodName();
        this.params = request.getParams();
        this.httpMethod = request.getHttpMethod();
        this.testCode=request.getTestCode();*/
        
    }
	
	public RequestClass(JSONObject json) {
		System.out.println("editable==>"+json.get("editable"));
		System.out.println("shown=="+json.get("shown"));
        this.methodCode = (String)json.get("methodCode");
        this.methodName=(String)json.get("methodName");
        this.params = (HashMap<String,Object>)json.get("params");
        this.httpMethod = (String)json.get("httpMethod");
        this.testCode = (String)json.get("testCode");
        
    }
	 
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

	public RequestClass() {
	}
}
