// This class is used to define all static functionality funtions.
public class Operation {
	//call print function: three situations
	public static void callprint(String code){
		String tempcode = code.trim();
		//1. print "string here"
		if (tempcode.toLowerCase().matches("(print)( +)(\".*\")")){
			tempcode=tempcode.replaceAll("^(?i)(print)","").trim();
			tempcode=Parse.parseStringAndEcapse(tempcode);
			if (tempcode!=null){
				System.out.print(tempcode);
			}
		//2. print cr
		}else if(code.toLowerCase().matches("(print)( +)(cr)")){
			System.out.println("\r");
		//2. print var
		}else if(code.toLowerCase().matches("(print)( +)([a-zA-Z_][a-zA-Z_0-9]*)")){
			tempcode=tempcode.replaceAll("^(?i)(print)","").trim();
			//get the value from hashmap and check if it is initizlized
			if(RudiInnerStructure.has_Var_From_PeekMap(tempcode)==false){
				Parse.error("Unrecogonized Variable name.(Define variable in DECS first)");
			}else if(RudiInnerStructure.getAttributes_of_Var_From_PeekMap(tempcode)[1].equals("")){
				Parse.error("Variable has not been initialized.");
			}else{
				System.out.print(RudiInnerStructure.getAttributes_of_Var_From_PeekMap(tempcode)[1]);
			}
		}else{
			Parse.error("syntax error: Print funtion.");
		}
	}
	//call input functions: check input valid
	public static void callinput(String code){
		String tempcode = code.trim();
		//parse it the input string is valid
		if (tempcode.toLowerCase().matches("(input)( +)(\".*\")")){
			tempcode=tempcode.replaceAll("^(?i)(input)","").trim();
			tempcode=Parse.parseStringAndEcapse(tempcode);
			if (tempcode!=null){
				System.out.print(tempcode);
			}
		//when input happens, it will match input and variable, they match then call the assignment function. 
		}else if(code.toLowerCase().matches("(input)( +)([a-zA-Z_][a-zA-Z_0-9]*)")){
			tempcode=tempcode.replaceAll("^(?i)(input)","").trim();
			String input = Action.keyInput();
			MatchThenAssign(tempcode,input);
		}else{
			Parse.error("syntax error: Input funtion.");
		}
	}
	//call assignment functions: check assignment valid
	public static void callassign(String code){
		String tempcode = code.trim();
		String parts[];
		String var;
		String express;
		//there is "=" that is an assignment
		if (tempcode.toLowerCase().matches("([a-zA-Z_])([a-zA-Z_0-9]*)( *)=( *)(.+)")){
			parts=tempcode.split(" *= *",2);
			var = parts[0];
			express = parts[1];
			//check if the variable is defined in decs
			if(RudiInnerStructure.has_Var_From_PeekMap(var)==false){
				Parse.error("Unrecogonized Variable name.(Define variable in DECS first)");
			}else{
				String type_value[] = RudiInnerStructure.getAttributes_of_Var_From_PeekMap(var);
				try{
					//check if the value matches to the type of variable
					if(type_value[0].equalsIgnoreCase("string")){
						if (express.trim().toLowerCase().matches("( *)\".*\"")==true){
							express = Parse.parseStringAndEcapse(express.trim());
							if (express!=null){
								type_value[1]=express;
								//update a related variable map
								RudiInnerStructure.update_PeekVarMap(var, type_value);
							}
						}else{
							Parse.error("String matches error.");
						}
					}else{
						//do calculate when assignment is a function
						ParseOperation math = new ParseOperation(express);
						Float value = Float.parseFloat(math.parseInput());
						if(type_value[0].equalsIgnoreCase("float")){
							type_value[1]=Float.toString(value);
							//update a related variable map
							RudiInnerStructure.update_PeekVarMap(var, type_value);
						}else if (type_value[0].equalsIgnoreCase("integer")){
							type_value[1]=Integer.toString(Math.round(value));
							//update a related variable map
							RudiInnerStructure.update_PeekVarMap(var, type_value);
						}
					}
				}catch(NumberFormatException e){
					Parse.error("Variable can only receieve a valid float or int number.");
				}
			}
		}else{
			Parse.error("Unrecoginzed error.");
		}
	}
	//remove some contained string in a string with case insensitive situation.
	//check if the remove only happen at the beginning of  a string
	public static String removeCaseInsentive(String code, String remove, Boolean head){
		if (head == false){
			return code.replaceAll("(?i)"+remove,"");
		}else{
			return code.replaceAll("^(?i)"+remove,"");
		}
		
	}
	//remove some contained string in a string with case insensitive situation.
	public static String removeCaseInsentive(String code, String remove){
		return code.replaceAll("(?i)"+remove,"");
	}
	//check it a assignment match (value and variable)
	public static void MatchThenAssign(String var_name, String express){
		if(RudiInnerStructure.has_Var_From_PeekMap(var_name)==false){
			Parse.error("Unrecogonized Variable name.(Define variable in DECS first)");
		}else{
			String type_value[] = RudiInnerStructure.getAttributes_of_Var_From_PeekMap(var_name);
			try{
				if(type_value[0].equalsIgnoreCase("string")){
					type_value[1]=express;
					RudiInnerStructure.update_PeekVarMap(var_name, type_value);
				}else{
					//do calculate when assignment is a function
					Float value = Float.parseFloat(express);
					if(type_value[0].equalsIgnoreCase("float")){
						type_value[1]=Float.toString(value);
						RudiInnerStructure.update_PeekVarMap(var_name, type_value);
					}else if (type_value[0].equalsIgnoreCase("integer")){
						type_value[1]=Integer.toString(Math.round(value));
						RudiInnerStructure.update_PeekVarMap(var_name, type_value);
					}
				}
			}catch(NumberFormatException e){
				Parse.error("Variable can only receieve a valid float or int number.");
			}
		}
	}
	//this is a parse function is operating process
	// if check if there is a [ or ] exists in the same line with special symbol. That is not allowed in this interpreter
	//Also this functions is used to manage complex functions like ,if ,while and subroutine.
	//condition is used to decide if next few lines is executed
	public static void morefxn_oneline(ComplexFxn fxn){
		String tempcode = fxn.code.trim();
		//if condition is false, that means next few lines cannot be execuate. Only record [ and ]
		if(fxn.condition==false){
			if (tempcode.toLowerCase().matches("\\[")){
				fxn.brackets_stack.push("[");
			}else if(tempcode.toLowerCase().matches("\\]")){
				if(fxn.brackets_stack.empty()){
					Parse.error("Brackets do not match.");
				}else{
					fxn.brackets_stack.pop();
					if(fxn.brackets_stack.empty()){
						fxn.is_valid=false;
					}
				}
			}
		}else if (Parse.isEmptyLine(tempcode)){
			//do nothing
		}else if (tempcode.toLowerCase().matches("\\[.+\\]")){//--------------[statement]
			Parse.error("\"]\" can not follow by other statements or expressions ");
		}else if (tempcode.toLowerCase().matches("\\[.+")){//-----------------[statement
			Parse.error("\"]\" can not follow by other statements or expressions ");
		}else if (tempcode.toLowerCase().matches("\\].+")){//-----------------]statement
			Parse.error("\"]\" can not follow by other statements or expressions ");
		}else if (tempcode.toLowerCase().matches(".+\\]")){//-----------------statement]
			Parse.error("\"]\" can not follow by other statements or expressions ");
		}else if (tempcode.toLowerCase().matches("\\[")){//-------------------[
			if(fxn.brackets_stack.empty()==false){
				Parse.error("Brackets do not match.");
			}else{
				fxn.brackets_stack.push("[");
			}
		}else if (tempcode.toLowerCase().matches("\\]")){//-------------------]
			if(fxn.brackets_stack.empty()){
				Parse.error("Brackets do not match.");
			}else{
				fxn.brackets_stack.pop();
				fxn.is_valid=false;
			}
		}
		else if(Parse.line_Begin_Fxn(tempcode).equalsIgnoreCase("stop")){
			System.out.println("<RUDI PROGRAM: Stop succesfully>");
			System.exit(0);
		//when if there is a "if", create a new object and do its functions
		}else if(Parse.line_Begin_Fxn(tempcode).equalsIgnoreCase("if")){
			if(fxn.brackets_stack.empty()){
				Parse.error("Brackets do not match.");
			}else{
				if_fxn newif = new if_fxn(tempcode);
				newif.doif();
			}
		//when if there is a "while", create a new object and do its functions
		}else if (Parse.line_Begin_Fxn(tempcode).equalsIgnoreCase("while")){
			if(fxn.brackets_stack.empty()){
				Parse.error("Brackets do not match.");
			}else{
				while_fxn newwhile = new while_fxn(tempcode);
				newwhile.dowhile();
			}
		//other if there are other operations, the process it the same in begin block
		}else if (Parse.line_Begin_Fxn(tempcode).equalsIgnoreCase("print")){
			Operation.callprint(tempcode);
		}else if (Parse.line_Begin_Fxn(tempcode).equalsIgnoreCase("input")){
			Operation.callinput(tempcode);
		}else if (Parse.line_Begin_Fxn(tempcode).equalsIgnoreCase("assignment")){
			Operation.callassign(tempcode);
		//when if there is a "subroutine call", create a new object and do its functions
		}else if (Parse.line_Begin_Fxn(tempcode).equalsIgnoreCase("call_sub")){
			
			RudiInnerStructure.setMarkLineNum();
			sub_fxn newsub_call = new sub_fxn(tempcode);
			newsub_call.dosub();
		}else if (Parse.line_Begin_Fxn(tempcode)==null){
			Parse.error("Statement or expression cannot be outside block.");
		}else{
			Parse.error("Unrecoginzed type or invalid syntax.");
		}
	}
}

