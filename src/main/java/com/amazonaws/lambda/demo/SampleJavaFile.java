package com.amazonaws.lambda.demo;

public class SampleJavaFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World");

	}

	public String appendString(String inputStr) {
		String outputStr = inputStr.concat("Here");
		return outputStr;
	}

	public String replaceStringMapInput(java.util.HashMap<String, Object> map) {
		String outputStr = ((String) map.get("param1")).replaceAll((String) map.get("param2"),
				(String) map.get("param3"));
		return outputStr;
	}
	
	public String replaceString(String inputStr, String oldChar,String newChar) {
		String outputStr = inputStr.replaceAll(oldChar,newChar);
		return outputStr;
	}
	
	/*

    {"params":{"param1":"replace","param2":"e","param3":"3"} ,
    "methodName":"replaceString",
    "methodCode":"public String replaceString(java.util.HashMap<String, Object> map) {\r\n\t\tString outputStr = ((String) map.get(\"param1\")).replaceAll((String) map.get(\"param2\"),\r\n\t\t\t\t(String) map.get(\"param3\"));\r\n\t\treturn outputStr;\r\n\t}"
    }*/

}
