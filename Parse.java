//Parse is a class used to parse mostly all code line. 
//check if this line is valid, if it is not, the interpreter will throw an error.
public class Parse {
	//parse syntax of program and subroutine structure.
	//put subroutine into a hashmap for future call.
	public static void parse_Main_Sub(){
		//record if there is a program statement read ever.
		//if it is over 1, there will be an error, because there is only one program in one file
		int mark_main=0;
		//read file from begin to the end until line number is out of range.
		while(RudiInnerStructure.getCodeLineNum()<readFile.getNumsofCodeLine()){
			//read a line code to pasre
			String code = readFile.getCodeByLine().trim();
			if (code.toLowerCase().trim().matches("program")){
				mark_main++;
				//if there is a program, check if there is decs, begin and end in its structure
				find_Main_Section();
			}else if (code.toLowerCase().trim().matches("subroutine +.+")){
				//if there is a subroutine, call the functions to record its attributes.
				Action.recordSubNameLine();
				//if there is a subroutine, check if there is decs, begin and continue in its structure
				find_Sub_Section();
			}
			// pointer of executed line code moving
			RudiInnerStructure.addCodeLineNum();
		}
		//after a file is read, initialize its pointer to original
		RudiInnerStructure.initalizeCodeLineNum();
		//more than one program statement, throw an error
		if(mark_main==0){
			error("There is no PROGRAM entry in your code file.");
		}else if (mark_main >1){
			error("Can not define two PROGRAMs in your code file.");
		}
	}
	//check if there is a valid structure in program
	public static void find_Main_Section(){
		boolean mark_main=false;
		//record how many times, decs, begin and end happen in the program
		int mark_decs=0;
		int mark_begin=0;
		int mark_end=0;
		while(RudiInnerStructure.getCodeLineNum()<readFile.getNumsofCodeLine()){
			String code = readFile.getCodeByLine();
			if (line_Main_Section(code)==null && mark_decs == 0 && mark_begin == 0){
				error("Cannot exist statement, assignment or other symbol ouside DECS or BEGIN block");
				//just be sensitive to decs, begin and end
			}else if (line_Main_Section(code)==null || line_Main_Section(code).equalsIgnoreCase("")){
				RudiInnerStructure.addCodeLineNum();
				continue;
				//when there is a pragram, another one cannot exists.
			}else if (line_Main_Section(code).matches("program")){
				if(mark_main==false){
					mark_main=true;
				}else{
					error("Cannot define one program in another program.");
				}
			}
			//subroutine definition is not allowed in a program
			else if(Parse.line_Main_Section(code).equalsIgnoreCase("subroutine")){
				error("Cannot exist subroutine in your program code");
				//parse the block of decs
			}else if (line_Main_Section(code).matches("decs.*") && mark_main==true){
				if(mark_decs==0){
					mark_decs++;
				}else{
					error("There is more than one DECS block found in your program.");
				}
				//TODO
			//parse the block of begin
			}else if (line_Main_Section(code).matches("begin") && mark_main==true){
				if(mark_begin==0){
					mark_begin++;
				}else{
					error("There is more than one BEGIN block found in your program.");
				}
				//TODO
				//System.out.println("begin exists.");
			//parse the end symbol in program
			}else if (line_Main_Section(code).matches("end")){
				//there muct be a begin block in a program
				if (mark_begin!=0){
					if(mark_end==0){
						mark_end++;
					}else{
						error("There is more than one END block found in your program.");
					}
					break;
				}else{
					error("There is no BEGIN in before END");
				}
				
			}
			RudiInnerStructure.addCodeLineNum();
		}
		//check if the structure of the program is valid
		if(mark_begin==0){
			error("There is no BEGIN block found in your program ");
		}else if(mark_end==0){
			error("There is no END block found in your program ");
		}
	}
	//parse the structure of subroutine.
	public static void find_Sub_Section(){
		boolean mark_sub=false;
		int mark_decs=0;
		int mark_begin=0;
		int mark_return=0;
		while(RudiInnerStructure.getCodeLineNum()<readFile.getNumsofCodeLine()){
			String code[] = readFile.getCodeByLine().trim().split(" ");
			//any statement cannot exist outside decs and begin block
			if (line_Sub_Section(code[0])==null && mark_decs == 0 && mark_begin == 0){
				error("Cannot exist statement, assignment or other symbol ouside DECS or BEGIN block");
			//only be sensitive to decs, begin and continue
			}else if (line_Sub_Section(code[0])==null || line_Sub_Section(code[0]).equals("")){
				RudiInnerStructure.addCodeLineNum();
				continue;
			//subroutine cannot be allowed defined in a subroutine
			}else if (line_Sub_Section(code[0]).matches("subroutine")){
				if(mark_sub==false){
					mark_sub=true;
				}else{
					error("Cannot define one subroutine in another subroutine.");
				}
			//program cannot be defined in subroutine.
			}else if(line_Sub_Section(code[0]).matches("program")){
				error("Cannot exist program or other subroutine in your subroutine code");
			//parse if the code line is the block of decs
			}else if (line_Sub_Section(code[0]).matches("decs.*") && mark_sub==true){
				if(mark_decs==0){
					mark_decs++;
				}else{
					error("There is more than one DECS block found in your subroutine.");
				}
				//parse if the code line is the block of begin
			}else if (line_Sub_Section(code[0]).matches("begin") && mark_sub==true){
				if(mark_begin==0){
					mark_begin++;
				}else{
					error("There is more than one BEGIN block found in your subroutine.");
				}
				//parse if the code line is return 
			}else if (line_Sub_Section(code[0]).matches("return") && mark_begin!=0){
				if(mark_return==0){
					mark_return++;
				}else{
					error("There is more than one RETURN block found in your subroutine.");
				}
				break;
			}
			RudiInnerStructure.addCodeLineNum();
		}
		if(mark_begin==0){
			error("There is no BEGIN block found in your subroutine ");
		}else if(mark_return==0){
			error("There is no RETURN block found in your subroutine ");
		}
	}
	//parse two symbol words program and subroutine.
	public static String line_Main_Sub(String string){
		if(isEmptyLine(string)){return "";}
		String program_subroutine = string.trim();
		if(program_subroutine.equalsIgnoreCase("program") ||
			program_subroutine.equalsIgnoreCase("subroutine")) {
			return program_subroutine;
		}else{
			return null;
		}
	}
	//parse2 each that may be in the first layer in a program
	public static String line_Main_Section(String string){
		if(isEmptyLine(string)){return "";}
		String section = string.trim().toLowerCase();
		if(section.toLowerCase().matches("program") ||
				section.matches("subroutine")||//cannot exist sub in program
				section.matches("decs.*") || 
				section.matches("begin") ||
				section.matches("end")) {
			return section;
		}else{
			return null;
		}
	}
	//parse each that be in the first layer in a subroutine
	public static String line_Sub_Section(String string){
		if(isEmptyLine(string)){return "";}
		String section = string.trim();
		if(section.matches("subroutine") || 
				section.toLowerCase().matches("program")||//cannot exist program in subroutine
				section.toLowerCase().matches("decs.*") ||
				section.toLowerCase().matches("begin") ||
				section.toLowerCase().matches("return")) {
			return section;
		}else{
			return null;
		}
	}
	//parse the second layer of program or subroutine
	//parse that may exist in decs block
	public static String line_decs_inner(String code){
		code = code.trim();
		if(isEmptyLine(code)){
			return "";
		}else if(code.toLowerCase().matches("decs +\\[")){
			return "decs[";
		}else if(code.toLowerCase().matches("\\[( *\t*)*(integer|float|string)( +).+\\]")) {
			return "[define]";
		}else if(code.toLowerCase().matches("\\[( *\t*)*]")){
			return "[]";
		}else if(code.toLowerCase().matches("\\[( *\t*)*(integer|float|string)( +).+[^]]*")){
			return "[define";
		}else if(code.toLowerCase().matches("(integer|float|string)( +).*\\]")){
			return "define]";
		}else if(code.toLowerCase().matches("\\[")){
			return "[";
		}else if(code.toLowerCase().matches("(integer|float|string)( +).*")){
			return "define";
		}else if(code.toLowerCase().matches("\\]")){
			return "]";
		}else{
			return null;
		}
	}
	//parse the second layer of program or subroutine
	//parse the code that may be in begin block "if" "while" "statement" "call sub" "print" "empty" "null" 
	public static String line_Begin_Fxn(String string){
		if(isEmptyLine(string)){
			return "";
		}
		String code = string.trim();
		if (code.toLowerCase().matches("end")||code.toLowerCase().matches("return")||code.toLowerCase().matches("stop")){
			return code;
		}else if (code.toLowerCase().matches("if *\\(.+\\).*")){
			//if(statement)
			return "if";
		}else if (code.toLowerCase().matches("while *\\(.+\\).*")){
			//while(statement)
			return "while";
		}else if (code.toLowerCase().matches("print[ \"]+.+")){
			//print statement || print""
			return "print";
		}else if (code.toLowerCase().matches("input[ \"]+.+")){
			//print statement || print""
			return "input";
		}else if (code.toLowerCase().matches("([a-zA-Z_])([a-zA-Z_0-9]*)( *)=( *)(.+)")){
			//a=234;a="b"
			return "assignment";
		}else if (!code.toLowerCase().matches("((if)|(stop)|(while)|(print)|(float)|(integer)|(string)) *\\(.*\\)")){
			if(code.toLowerCase().matches("(.+)( *)(\\()(.*)(\\))")){
				return "call_sub";
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	//parse variable valid
	public static boolean isValidVar(String string){
		//first char is a letter.
		return string.trim().matches("[a-zA-Z_][a-zA-Z_0-9]*");
	}
	//parse if there is a valid string input. if there is double quota, it much has be escaped
	public static String parseStringAndEcapse(String code){
		String tempcode=code.trim();
		if (tempcode.toLowerCase().matches("(\".*\")")){
			tempcode=tempcode.replaceAll("^\"|\"$","");
			//there is a no \ with "
			if(tempcode.matches(".*([^\\\\]\").*")){
				Parse.error("Invalid string: you should escape your double quota.");
				return null;
			//if there is a \", then turn to "
			}else if(tempcode.matches(".*(\\\\\").*")){
				return tempcode.replaceAll("\\\\\"", "\"");
			}else{
				return tempcode;
			}
		}else{
			Parse.error("Input must be a valid stiring");
			return null;
		}
	}
	//parse a line with no valid executable code
	// an empty line
	public static boolean isEmptyLine(String string){
		return string.trim().equalsIgnoreCase("");
	}
	//general error function, throw error with line number
	public static void error(String string){
		System.out.println("Error (Line:"+(RudiInnerStructure.getCodeLineNum()+1)+"): "+string);
		System.exit(1);
	}
	//based on regex, check the occurrence numbers in a string
	public static int countOccurrence(String string, String regex){
		int count = string.length() - string.replaceAll(regex, "").length();
		return count;
	}
	//parse it the number is a even number
	public static boolean parseEven(int num){
		if (num % 2 == 0){ 
			return true;
		}else{ 
			return false; 
		}
	}
	//parse and get the number of variables of subroutine call or subroutine definition
	public static int parseSubVarNum(String name_brackets_code){
		int var_num = 0;
		String var = name_brackets_code.replaceAll("(^.+(\\()|(\\)$))", "").trim();
		if(var.equals("")){
			var_num = 0;
		}else{
			if(var.matches("^,.*")||var.matches(".*,$")){
				Parse.error("Reference definition is invalid");
			}else{
				String parts[] = var.split(" *, *");
				var_num = parts.length;
			}
		}
		return var_num;
	}
}

