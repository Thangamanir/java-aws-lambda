package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

public class JUnitStreamHandler implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

		String returnStr = "";

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		context.getLogger().log("Input stream==>" + inputStream.toString() + "\n");
		JSONObject responseJson = new JSONObject();
		JSONParser parser = new JSONParser();
		ResponseClass response = new ResponseClass();
		Gson gson = new Gson();
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		JSONObject responseJsonObject = new JSONObject();
		try {

			JSONObject event = (JSONObject) parser.parse(reader);

			if (event.get("httpMethod") != null && event.get("httpMethod").equals("GET")) {

				URL r3 = this.getClass().getClassLoader().getResource("resources/index.html");

				InputStream is = StreamHandler.class.getResourceAsStream("/index.html");

				JSONObject headerJson = new JSONObject();
				headerJson.put("x-custom-header", "my custom header value");
				headerJson.put("content-type", "text/html");
				responseJson.put("statusCode", 200);
				responseJson.put("headers", headerJson);
				responseJson.put("body", readFileAsString(r3.getPath()));
				context.getLogger().log("response from get==>" + responseJson.toJSONString());

			} else {
				context.getLogger().log("Body==>" + event.get("body"));
				if (event.get("body") != null) {
					RequestClass requestClass = new RequestClass(event.get("body").toString());
					String input = requestClass.getMethodCode();
					String testInput = requestClass.getTestCode();
					Random r = new Random();
					String fileName = "InputJavaFile";
					String testFileName = "JUnitTest";
					Path javaFile = null;
					Path testFile = null;
					input = constructCode(input);
					testInput = constructTestCode(testInput);

					context.getLogger().log("Input after java append: " + input + "\n");
					context.getLogger().log("Test after java append: " + testInput + "\n");
					javaFile = saveSource(input, fileName, context);
					testFile = saveSource(testInput, testFileName, context);
					Path classFile = compileSource(javaFile, testFile, context);
					context.getLogger().log("Printing Path==>" + classFile.toFile().getAbsolutePath() + "\t"
							+ Files.exists(classFile) + "\n");
					returnStr = (String) runClass(classFile, testFileName, context);

					JSONObject headerJson = new JSONObject();
					headerJson.put("x-custom-header", "my custom header value");
					responseJson.put("headers", headerJson);

					if (returnStr == null) {
						responseJson.put("statusCode", "200");
						response.setComplete(true);
						response.setTextFeedback("Success");
						response.setHtmlFeedback("<h1>Success</h1>");
						response.setJsonFeedback("{status:Success}");
						responseJson.put("body", gson.toJson(response));
					} else {
						responseJson.put("statusCode", "500");
						response.setComplete(false);
						response.setTextFeedback("Failed==>\n\t\t" + returnStr);
						response.setHtmlFeedback("<h1>Failed</h1>returnStr");
						response.setJsonFeedback("{status:Incorrect. Try again!}");
						responseJson.put("body", gson.toJson(response));
					}

				}
			}

		} catch (ParseException pex) {
			responseJson.put("statusCode", 500);
			responseJsonObject.put("htmlFeedback", "<h1>Failed</h1><h3>" + pex.getCause() + "</h3>");
			responseJsonObject.put("textFeedback", "Failed\n" + pex.getCause() + "\n");
			responseJsonObject.put("jsonFeedback","{status:Incorrect. Try again!}");
			responseJson.put("body", responseJsonObject.toString());			
		} catch (Exception e) {
			System.out.println("Cause==>" + e.getCause() + "\n" + e.getStackTrace());
			responseJson.put("statusCode", 500);
			responseJsonObject.put("htmlFeedback", "<h1>Failed</h1><h3>" + e.getCause() + "</h3>");
			responseJsonObject.put("textFeedback", "Failed\n" + e.getCause() + "\n");
			responseJsonObject.put("jsonFeedback","{status:Incorrect. Try again!}");
			responseJson.put("body", responseJsonObject.toString());
		}
		System.out.println("Response object==>" + gson.toJson(response));
		System.out.println("Returning==>" + responseJson.toJSONString());
		writer.write(responseJson.toString());
		writer.close();

	}

	private static String readFileAsString(String fileName) throws Exception {
		String data = "";
		data = new String(Files.readAllBytes(Paths.get(fileName)));
		return data;
	}

	private List<File> listFilePaths(File folder, Context context) {
		List<File> filePaths = new ArrayList<>();
		if (folder != null) {
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				File eachFile = listOfFiles[i];
				if (eachFile.getName().endsWith("jar") || eachFile.getName().endsWith("zip")) {
					if (eachFile.isFile()) {
						filePaths.add(Paths.get(folder + File.separator + eachFile.getName()).toFile());
					} else if (eachFile.isDirectory()) {
						filePaths.add(Paths.get(folder + File.separator + eachFile.getName()).toFile());
						listFilePaths(eachFile, context);
					}
				}
			}
			filePaths.add(Paths.get("/tmp").toFile());
			filePaths.add(Paths.get("/var/task").toFile());
		}
		return filePaths;
	}

	private String constructCode(String methodCode) throws FileNotFoundException {
		StringBuffer strMethod = new StringBuffer();
		strMethod.append("public class InputJavaFile {" + methodCode);
		strMethod.append("}");
		return strMethod.toString();
	}

	private String constructTestCode(String testMethodCode) throws Exception {
		StringBuffer strMethod = new StringBuffer();
		strMethod.append("import org.junit.Assert;\n");
		strMethod.append("import org.junit.Test;\n");

		strMethod.append("import java.io.ByteArrayOutputStream;\n");
		strMethod.append("import java.io.PrintStream;");

		strMethod.append("import org.junit.internal.TextListener;\n");
		strMethod.append("import org.junit.runner.JUnitCore;\n");
		strMethod.append("import org.junit.runner.Result;\n");
		strMethod.append("public class JUnitTest {\n");
		strMethod.append("@Test" + "\n");
		strMethod.append(testMethodCode);

		strMethod.append("}");
		return strMethod.toString();

	}

	private Path saveSource(String source, String fileName, Context context) throws IOException {
		Path sourcePath = Paths.get("/tmp/" + fileName + ".java");
		Files.write(sourcePath, source.getBytes());
		return sourcePath;
	}

	private Path compileSource(Path javaFile, Path testFile, Context context) throws Exception

	{
		// Files.readAllBytes
		String exceptionMsg = "";
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		File folder = new File("/var/task/lib");
		StringBuilder errorMsg = new StringBuilder();
		try {
				if (compiler == null) {
	
					Class<?> javacTool = Class.forName("com.sun.tools.javac.api.JavacTool");
					Method create = javacTool.getMethod("create");
					compiler = (JavaCompiler) create.invoke(null);
				}
	
				DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
				StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(diagnosticsCollector,
						null, null);
				standardJavaFileManager.setLocation(StandardLocation.CLASS_PATH, listFilePaths(folder, context));
	
				File[] javaFiles = new File[] { javaFile.toFile(), testFile.toFile() };
	
				Iterable<? extends JavaFileObject> compilationUnits1 = standardJavaFileManager
						.getJavaFileObjectsFromFiles(Arrays.asList(javaFiles));
				CompilationTask task = compiler.getTask(null, standardJavaFileManager, diagnosticsCollector, null, null,
						compilationUnits1);
				boolean success = task.call();
				standardJavaFileManager.close();
				if (!success) {
					List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticsCollector.getDiagnostics();
					for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
						// read error details from the diagnostic object
	
						if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
							exceptionMsg = String.format("Compilation error: Line %d - %s%n", diagnostic.getLineNumber(),
									diagnostic.getMessage(null));
	
							errorMsg.append(exceptionMsg);
						}
					}
					throw new Exception(errorMsg.toString());
				}

		} catch (Exception e) {
			context.getLogger().log("compilation Exception " + e + "\n");
			throw new Exception(e);
		}
		return javaFile.getParent().resolve("JUnitTest.class");
	}

	private Object runClass(Path javaClass, String fileName, Context context)
			throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException,
			NoSuchMethodException, InvocationTargetException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		URL classUrl = javaClass.getParent().toFile().toURI().toURL();
		File fileForClass = new File(
				org.junit.runner.JUnitCore.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { classUrl, fileForClass.toURI().toURL() });

		Class<?> clazz = Class.forName(fileName, true, classLoader);
		Object obj = clazz.newInstance();
		Method testReplaceMethod = obj.getClass().getDeclaredMethod("executeTest");

		Method[] allMethod = obj.getClass().getDeclaredMethods();
		for (Method m : allMethod) {
			context.getLogger().log("Method Name==" + m.getName() + "\n");
			context.getLogger().log("Method Return Type==" + m.getReturnType() + "\n");

		}
		try {
			Method m = obj.getClass().getDeclaredMethod("executeTest");
			return m.invoke(obj);
		} catch (InvocationTargetException e) {
			throw new InvocationTargetException(e);
		}

	}

}
