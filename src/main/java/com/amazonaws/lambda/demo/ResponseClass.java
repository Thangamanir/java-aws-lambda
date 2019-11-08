package com.amazonaws.lambda.demo;

public class ResponseClass {	
	
	boolean isComplete;
	String jsonFeedback;
	String htmlFeedback;
	String textFeedback;

	
	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public String getJsonFeedback() {
		return jsonFeedback;
	}

	public void setJsonFeedback(String jsonFeedback) {
		this.jsonFeedback = jsonFeedback;
	}

	public String getHtmlFeedback() {
		return htmlFeedback;
	}

	public void setHtmlFeedback(String htmlFeedback) {
		this.htmlFeedback = htmlFeedback;
	}

	public String getTextFeedback() {
		return textFeedback;
	}

	public void setTextFeedback(String textFeedback) {
		this.textFeedback = textFeedback;
	}

	public ResponseClass(boolean isComplete, String textFeedback,String htmlFeedback,String jsonFeedback) {
		this.textFeedback = textFeedback;
		this.htmlFeedback = htmlFeedback;
		this.isComplete=isComplete;
		this.jsonFeedback=jsonFeedback;
	
	}

	public ResponseClass() {
	}

}