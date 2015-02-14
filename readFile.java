
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Stack;

// A class to read spcific rudi file into a hashmap
public class readFile {
	// attributes of the file 
	private static String filename = "test.txt";
	private static HashMap<Integer,String> codeMap = new HashMap<Integer,String>();
	private static Stack<String> comment_symbol_stack = new Stack<String>();
	private static int NumsofCodeLine = 0; 
	// give a constructor without reference, a default file is give named test.txt
	//put all file contents in to a hashmap
	public static void readToFile(){
		readFileByLines();
	}
	// create a read to read and put all contents into a hashmap
	public static void readToFile(String name){
		filename = name;
		readFileByLines();
	}
	//read codes by line, when a line has a &, that means this line continuew with the next line.
	public static String getCodeByLine(){
		int nowLineNum = RudiInnerStructure.getCodeLineNum();
		RudiInnerStructure.resetContinueLineNum();
		String onelinecode = codeMap.get(nowLineNum);
		//if there is a &, record a continue line number, then the next line will not be read
		while(onelinecode.matches(".*&$")){
			RudiInnerStructure.addContinueLineNum();
			//combine multiple lines into one string and return the entire string
			onelinecode = onelinecode.replaceAll("&$", "") + codeMap.get(nowLineNum+RudiInnerStructure.getContinueLineNum());
		}
		return onelinecode;
	}
	//get the number of code line. Interpreter use this functions to use code. 
	public static int getNumsofCodeLine(){
		return NumsofCodeLine;
	}
	//read all lines of code in file into a hash map.
	private static void readFileByLines() {
	    //create a file based on a filename or path
		File file = new File(filename);
		//create a input buffer
		LineNumberReader reader = null;
	    try{
	    	//read the file to the reader buffer
	        reader = new LineNumberReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
	        //create a variable to store temporary line data
	        String codeline = null;
	        Integer lineNum = reader.getLineNumber();
	        while ((codeline = reader.readLine()) != null) {
	        	//Add to LinkedList
	        	//System.out.println(codeline);
	        	codeline = removeComment(codeline);
	        	//remove all lines that are not encoded by utf-8 out of the hashmap
	        	if(isPureAscii(codeline)==false){
	        		Parse.error("Syntax error: Invalid character found.");
	        	}
	        	//if interpreter there is a line of comments out of the hashmap
	        	codeline = removeComment(codeline);
	        	//there comment format is invalid ,throw an error
	        	if(codeline==null){
	        		Parse.error("Syntax error: Comment statement error.");
	        	}
	        	//put the read line code into a hashmap. The key is line number and the value the code
	        	codeMap.put(lineNum,codeline.trim());
	        	lineNum = reader.getLineNumber();
	        	//after each read, the record number of line is added.
	        	NumsofCodeLine++;
	        }
	        //close the buffer
	        reader.close();
	    //throw an error if there is no such path or file
		} catch (IOException e) {
		    Parse.error("There is no such file in your path.");
		} finally {
		    try {
		        reader.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	// a function to check if it is a utf-8 format encoded
	private static boolean isPureAscii(String string){   
	    for(int i=0;i<string.length();i++){   
	        char ch=string.charAt(i);   
	        if(ch>=127||ch<0)return false;   
	    }   
	    return true;   
	} 
	//remove a comment from a file 
	private static String removeComment(String linecode){
		//use a stack to record left comment and right comment
		//more right comment will cause a downflow.
		if(comment_symbol_stack.empty()){
			return leftComment(linecode);
		}else{
			return rightComment(linecode);
		}		
	}
	//check if it is a left comment symbol
	private static String leftComment(String linecode){
		String tempString = linecode;
		String finalString = "";
		String parts[];
		// match with /*...*/ of one line
		if(linecode.matches("(/\\*)(.*)(\\*/)")){
			return "";
		// match with /*..... */ statement  of one line, get statement and return
		}else if(linecode.matches("(/\\*)(.*)(\\*/)(.+)")){
			parts= tempString.split("\\*/");
			finalString= parts[parts.length-1];
			return finalString.trim();
		// match with /*...... of one line, only left results in a symbol pushed to stack
		}else if(linecode.matches("(/\\*)(.*)")){
			comment_symbol_stack.push("/*");
			return "";
		// match with statement /*.....*/ of one line, get statement and return 
		}else if(linecode.matches("(.+)(/\\*)(.*)(\\*/)")){
			finalString= tempString.split("/\\*")[0];
			return finalString.trim();
		// match with ....../*...*/..... of one line, this is invalid 
		}else if(linecode.matches("(.+)(/\\*)(.*)(\\*/)(.+)")){
			Parse.error("Syntax error: Comment statement error.");
			return null;
		// match with statement /*..... of one line, get statement and return 
		}else if(linecode.matches("(.+)(/\\*)(.*)")){
			comment_symbol_stack.push("/*");
			finalString= tempString.split("/\\*")[0];
			return finalString.trim();
		// if there is no comment symbol, return the integral line. 
		}else{
			return linecode;
		}
	}
	//check if it is a right comment symbol
	private static String rightComment(String linecode){
		String tempString = linecode;
		String finalString = "";
		String parts[];
		//match with ......*/ , that means there is no code on this line
		if(linecode.matches("(.*)(\\*/)")){
			comment_symbol_stack.pop();
			return "";
		//match with .....*/ statement of one line, get the statement and return the statement as code
		}else if(linecode.matches("(.*)(\\*/)(.+)")){
			comment_symbol_stack.pop();
			parts= tempString.split("\\*/");
			finalString= parts[parts.length-1];
			return finalString.trim();
		//if there is no such comment line, return the integral line as code. 
		}else{
			return "";
		}
	}
	
}
