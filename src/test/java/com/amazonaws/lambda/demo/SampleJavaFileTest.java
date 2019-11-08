package com.amazonaws.lambda.demo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class SampleJavaFileTest {
	String EXPECTED_OUTPUT_STRING="r3plac3";
	
	
	public void testReplaceString() {
		SampleJavaFile sample = new SampleJavaFile();
		
		String outputString = sample.replaceString("replace","e","3");
		Assert.assertEquals(EXPECTED_OUTPUT_STRING, outputString);
	}
	
	public void test() {
		Assert.assertEquals("1","1");
	}
	@Test
	public void execute() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try 
		{	
			JUnitCore junit = new JUnitCore();
			junit.addListener(new TextListener(new PrintStream(outputStream)));
			Result result = junit.run(SampleJavaFileTest.class);
			
			System.out.println(result.wasSuccessful());
			System.out.println(result.getFailureCount());
			System.out.println(result.getFailures());
			System.out.println("***********************");
			System.out.println(outputStream.toString());
		}catch(Exception e) {
			System.out.println("Failure==>"+e);
		}
		//return outputStream;
	}
	/*public static void main(String ...args) {
		System.out.println(new SampleJavaFileTest().execute().toString());
		new SampleJavaFileTest().testReplaceString();
		new SampleJavaFileTest().test();
		
	}*/
}


/*
 * @Test public void testReplaceStringMapInput() { SampleJavaFile sample = new
 * SampleJavaFile(); HashMap<String,Object> h1=new HashMap<String,Object>();
 * h1.put("param1", "replace"); h1.put("param2", "e"); h1.put("param3","3");
 * String outputString = sample.replaceStringMapInput(h1);
 * 
 * Assert.assertEquals(EXPECTED_OUTPUT_STRING, outputString); }
 */