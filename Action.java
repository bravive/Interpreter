import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

//This class is used to call functions
//It is the middleware between parse and execute operations
public class Action {
	//this a function to execute the code in program
	public static void program(){
		while(RudiInnerStructure.getCodeLineNum()<readFile.getNumsofCodeLine()){
			String code = readFile.getCodeByLine().trim();
			//not expected, throw an error
			if (Parse.line_Main_Section(code)==null){
				Parse.error("Statement or expression cannot be outside program or subroutine.");
			}else if (Parse.line_Main_Section(code).matches("")){
				//do nothing
			}else if (Parse.line_Main_Section(code).matches("program")){
				//do nothing
			}else if (Parse.line_Main_Section(code).matches("decs")){
				RudiInnerStructure.addCodeLineNum();
				//begin to execute the block of decs
				decs();
			}else if (Parse.line_Main_Section(code).matches("decs +\\[")){
				//begin to execute the block of decs
				decs();
			}else if (Parse.line_Main_Section(code).matches("begin")){
				RudiInnerStructure.addCodeLineNum();
				//begin to execute the block of begin
				begin();
			}else if (Parse.line_Main_Section(code).matches("end")){
				break;
			}
			RudiInnerStructure.addCodeLineNum();
		}
	}
	//this a function to execute the code in subroutine
	public static void subroutine(){
		while(RudiInnerStructure.getCodeLineNum()<readFile.getNumsofCodeLine()){
			String code = readFile.getCodeByLine().trim();
			//not expected, throw an error
			if (Parse.line_Sub_Section(code)==null){
				Parse.error("Statement or expression cannot be outside program or subroutine.");
			}else if (Parse.line_Sub_Section(code).matches("")){
				//do nothing
			}else if (Parse.line_Sub_Section(code).matches("subroutine")||  Parse.line_Sub_Section(code).matches("program")){
				Parse.error("Cannot define a subroutine or program in a subroutine.");
			}else if (Parse.line_Sub_Section(code).matches("decs")){
				//begin to execute the block of decs
				RudiInnerStructure.addCodeLineNum();
				decs();
			}else if (Parse.line_Sub_Section(code).matches("decs +\\[")){
				//begin to execute the block of decs
				decs();
			}else if (Parse.line_Sub_Section(code).matches("begin")){
				RudiInnerStructure.addCodeLineNum();
				//begin to execute the block of begin
				begin();
			}else if (Parse.line_Sub_Section(code).matches("return")){
				break;
			}
			RudiInnerStructure.addCodeLineNum();
		}
	}
	//this the third layer of functions execution in program or subroutine
	public static void begin(){
		while(RudiInnerStructure.getCodeLineNum()<readFile.getNumsofCodeLine()){
			String code = readFile.getCodeByLine().trim();
			//one line has many situations
			if (Parse.line_Begin_Fxn(code)==null){
				Parse.error("Statement or expression are not correct.");
			}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("")){
				//do nothing
			}else if(Parse.line_Begin_Fxn(code).equalsIgnoreCase("end")){
				System.out.println("<RUDI PROGRAM: Finish succesfully>");
				//there is end, program is over
				System.exit(0);
			}else if(Parse.line_Begin_Fxn(code).equalsIgnoreCase("return")){
				RudiInnerStructure.minusCodeLineNum();
				//there is return, subroutine is over
				break;
			}else if(Parse.line_Begin_Fxn(code).equalsIgnoreCase("stop")){
				System.out.println("<RUDI PROGRAM: Stop succesfully>");
				//there is a stop, the program is over
				System.exit(0);
			}else if(Parse.line_Begin_Fxn(code).equalsIgnoreCase("if")){
				//create a if object to call its function to execute.
				if_fxn newif = new if_fxn(code);
				newif.doif();
			}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("while")){
				//create a while object to call its function to execute.
				while_fxn newwhile = new while_fxn(code);
				newwhile.dowhile();
			}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("print")){
				// call print function to execute.
				Operation.callprint(code);
			}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("input")){
				// call input function to execute.
				Operation.callinput(code);
			}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("assignment")){
				Operation.callassign(code);
			}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("call_sub")){
				//create a subroutine object and call this subroutine function to execute.
				RudiInnerStructure.setMarkLineNum();
				sub_fxn newsub_call = new sub_fxn(code);
				newsub_call.dosub();
			}else{
				Parse.error("Unrecoginzed type or invalid syntax.");
			}
			RudiInnerStructure.addCodeLineNum();
		}
	}
	//this is a the same with begin, only call once function in begin block
	public static void begin_sub(String code){
		if (Parse.line_Begin_Fxn(code)==null){
			Parse.error("Sub Statement or expression are not correct.");
		}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("")){
			//do nothing
		}else if(Parse.line_Begin_Fxn(code).equalsIgnoreCase("end")){
			System.out.println("<RUDI PROGRAM: Finish succesfully>");
			System.exit(0);
		}else if(Parse.line_Begin_Fxn(code).equalsIgnoreCase("return")){
			//return to marked_line;
			//break;
		}else if(Parse.line_Begin_Fxn(code).equalsIgnoreCase("stop")){
			System.out.println("<RUDI PROGRAM: Stop succesfully>");
			System.exit(0);
		}else if(Parse.line_Begin_Fxn(code).equalsIgnoreCase("if")){
			if_fxn newif = new if_fxn(code);
			newif.doif();
		}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("while")){
		}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("print")){
			Operation.callprint(code);
		}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("input")){
			Operation.callinput(code);
		}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("assignment")){
			Operation.callassign(code);
		}else if (Parse.line_Begin_Fxn(code).equalsIgnoreCase("call_sub")){
		}else{
			Parse.error("Unrecoginzed type or invalid syntax.");
		}
	}
	//this the third layer about variable definition in program or subroutine
	public static void decs(){
		// record its occurrence time
		// must be once and
		int mark_left=0;
		int mark_right=0;
		while(RudiInnerStructure.getCodeLineNum()<readFile.getNumsofCodeLine()){
			String code = readFile.getCodeByLine().trim();
			if (Parse.line_decs_inner(code)==null){
				Parse.error("syntax error: Variable can be only defined in DECS.");
			}else if (Parse.line_decs_inner(code).matches("")){
				//do nothing
			//parse the decs
			}else if (Parse.line_decs_inner(code).matches("decs\\[")){
				//if there is more than onece, there is an error
				if(mark_left==0){
					mark_left++;
				}else{
					Parse.error("More than one \"[\" in DECS.");
				}
			//record the occurrence of [
			}else if (Parse.line_decs_inner(code).matches("\\[")){
				if(mark_left==0){
					mark_left++;
				}else{
					Parse.error("More than one \"[\" in DECS.");
				}
			//record the occurrence of [ and record the variable in map
			}else if (Parse.line_decs_inner(code).matches("\\[define")){
				if(mark_left==0){
					mark_left++;
				}else{
					Parse.error("More than one \"[\" in DECS.");
				}
				String parts[] = splictVarLine(code.trim().replaceAll("\\[", ""));
				putVarToMap(RudiInnerStructure.peekOf_VarMapStack(),parts);
			//record the occurrence of ] and record the variable in map
			}else if (Parse.line_decs_inner(code).matches("define\\]")){
				if(mark_left==0){
					Parse.error("There must be a \"[\" before definition");
				}
				if(mark_right==0){
					mark_right++;
				}else{
					Parse.error("More than one \"]\" in DECS.");
				}
				//get all attributes of a variable.
				String parts[] = splictVarLine(code.trim().replaceAll("\\]", ""));
				//put the var into map(k:part[1],v:part[0]part[2])
				putVarToMap(RudiInnerStructure.peekOf_VarMapStack(),parts);
				break;
			//only one variable in this decs block
			}else if (Parse.line_decs_inner(code).matches("\\[define\\]")){
				if(mark_left==0){
					mark_left++;
				}else{
					Parse.error("More than one \"[\" in DECS.");
				}
				if(mark_right==0){
					mark_right++;
				}else{
					Parse.error("More than one \"]\" in DECS.");
				}
				//remove"[" and "]", then split definition into parts
				String parts[] = splictVarLine(code.trim().replaceAll("\\[|\\]", ""));
				//put the var into map(k:part[1],v:part[0]part[2])
				putVarToMap(RudiInnerStructure.peekOf_VarMapStack(),parts);
				break;
			}else if (Parse.line_decs_inner(code).matches("define")){
				if(mark_left==0){
					Parse.error("There must be a \"[\" before definition");
				}
				//split without remove
				String parts[] = splictVarLine(code.trim());
				//put the var into map(k:part[1],v:part[0]part[2])
				putVarToMap(RudiInnerStructure.peekOf_VarMapStack(),parts);
			}else if (Parse.line_decs_inner(code).matches("]")){
				if(mark_right==0){
					mark_right++;
				}else{
					Parse.error("More than one \"]\" in DECS.");
				}
				break;
			}else if (Parse.line_decs_inner(code).matches("[.*]")){
				break;
			}
			RudiInnerStructure.addCodeLineNum();
		}
	}
	// a funtsion that is used to put variable into a specific map
	public static void putVarToMap(HashMap<String,String[]> peek_var_map, String parts[]){
		String var_name= parts[1].toLowerCase();
		String type_value[]= new String[2];
		type_value[0]=parts[0].toLowerCase();
		type_value[1]=parts[2];
		//variables cannot be defined twice
		if(peek_var_map.containsKey(var_name)){
			Parse.error("Cannot duplicate a same varialbe in a program or subroutine");
		}else{
			peek_var_map.put(var_name, type_value);
		}
	}
	//split variable definition code, return all attributes of it
	public static String[] splictVarLine(String code){
		String type_var_value[] = new String[3];
		String tempString[];
		//Considering different conditions and split it into three parts. 
		//If it is not initialized, its value is empty
		//integer ab_1
		//integer ab_1 = 4
		//float ab_1
		//float ab_1 = 5.41
		//string ab_1
		//string ab_1 = "sdfsdf sdlfsdf "
		if(code.trim().toLowerCase().matches("(integer)( +)([a-zA-Z_][a-zA-Z_0-9]*)")==true){
			tempString = code.trim().split(" +");
			type_var_value[0] = tempString[0];
			type_var_value[1] = tempString[1];
			type_var_value[2]="";
		}else if (code.trim().toLowerCase().matches("(integer)( +)([a-zA-Z_][a-zA-Z_0-9]*)( *)(=)( *)[-]*(([1-9][0-9]*)|[0])")==true){
			type_var_value = code.trim().split("( *= *)|( +)");
		}else if(code.trim().toLowerCase().matches("(float)( +)([a-zA-Z_][a-zA-Z_0-9]*)")==true){
			tempString = code.trim().split(" +");
			type_var_value[0] = tempString[0];
			type_var_value[1] = tempString[1];
			type_var_value[2]="";
		}else if (code.trim().toLowerCase().matches("(float)( +)([a-zA-Z_][a-zA-Z_0-9]*)( *)(=)( *)[-]*(([0-9]+\\.[0-9]+)|([0-9]+))")==true){
			type_var_value = code.trim().split("( *= *)|( +)");
		}else if(code.trim().toLowerCase().matches("(string)( +)([a-zA-Z_][a-zA-Z_0-9]*)")==true){
			tempString = code.trim().split(" +");
			type_var_value[0] = tempString[0];
			type_var_value[1] = tempString[1];
			type_var_value[2]="";
		}else if (code.trim().toLowerCase().matches("(string)( +)([a-zA-Z_][a-zA-Z_0-9]*)( *)(=)( *)\".*\"")==true){
			String temp1[] = code.trim().split("( +)",2);
			type_var_value[0] = temp1[0];
			String temp2[] = temp1[1].trim().split("( *= *)");
			temp2[1] = Parse.parseStringAndEcapse(temp2[1]);
			if (temp2[1]!=null){
				type_var_value[1] = temp2[0];
				type_var_value[2] = temp2[1];
			}
		}else{
			Parse.error("syntax error: variable definition is invalid.");
		}
		return type_var_value;
	}
	//it is a function to record subroutine name and its execution line nubmer
	public static void recordSubNameLine(){
		String code = readFile.getCodeByLine().trim();
		if(code.matches("subroutine +[a-zA-Z][a-zA-Z_0-9]*[ ]*\\(.*\\)")){
			String sub_name = code.replaceAll("((subroutine )|( *\\(.*\\)))", "").trim();
			Integer line_num = RudiInnerStructure.getCodeLineNum();
			Integer var_num = RudiInnerStructure.getCodeLineNum();
			Integer attributes[] = new Integer[2];
			String var = code.replaceAll("(^(subroutine .+\\()|(\\)$))", "").trim();
			//record reference number
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
			attributes[0]=line_num;
			attributes[1]=var_num;
			if(RudiInnerStructure.has_subName_Sub_Map(sub_name)){
				Parse.error("duplicate subroutine "+sub_name+"().");
			}else{
				RudiInnerStructure.add_to_Sub_Map(sub_name.toLowerCase(),attributes);
			}
		}else{
			Parse.error("Syntax error - definition of SUBROUTINE.");
		}
	}
	//it is a function used to wait for input from users. 
	public static String keyInput(){
		try{
			BufferedReader Buffer = new BufferedReader(new InputStreamReader(System.in));
			String tempString=Buffer.readLine();
			return tempString;
		}
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
	}
}
