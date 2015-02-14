import java.util.*;

/*
 *This class contains methods which are used to
 *parse the input string, chech whether the input is a valid string
 *and computing the correct answer for valid expression
 */

public class ParseOperation{
	private String operaInput;                 //the input string
    private Stack<String> operatant = new Stack<String>();   //the stack used to store the store the operands in the input string
    private Stack<String> operator = new Stack<String>(); //the stack used to store the operators in the input string
    private Stack<String> operatantType = new Stack<String>(); //the stack used to store the type of operands in the input string
    int number; //the priority of every operator, samll number represent high priority
    
    private String calStepResult;             //the result of each calculation
    private String calStepResultType;         //the type of result of calculation
    private String newchar = new String();    //
	
	public ParseOperation(String input){
		operaInput = input;
	}

	public String parseInput(){
		String midResult = new String();
		String oldchar = new String();
		//String newchar = new String();
		String result = new String();
		int rightBra;
		int leftBra;
		
		result = operaInput;
		//System.out.println("input is "+result);
        
        /***************check1**********************/
        //check whether there are illegal character in the input string
        if(parseCharacter(result)==false){
            return null;
        }
        
        result = result.replace(":eq:","{");
        result = result.replace(":ne:","}");
        result = result.replace(":gt:","@");
        result = result.replace(":lt:","#");
        result = result.replace(":ge:","$");
        result = result.replace(":le:","%");
        
        /***************check2--check brackets****************/
		rightBra = result.indexOf(')');
		while (rightBra!=-1){
            //check whether the number of left brackets is equal to right bracket
			leftBra = result.lastIndexOf('(',rightBra);
			if (leftBra==-1){
				Parse.error("no enought left brackets");
				return null;
			}
			else {
                //pick the string in the first ()
				midResult = result.substring(leftBra+1,rightBra);
                //pick the string in the first () with ()
				oldchar = result.substring(leftBra,rightBra+1);
                //check whether the string in the () is valid
                /****************add a new check 3-1********************/
                //check whether the . is valid used
                if(parsePoint(midResult)==false){
                    return null;
                }
                /****************check3**************************/
                //first check whether there are space between number and character
                if (parseSpace(midResult)==false){
                    return null;
                }
                else {
                    midResult = midResult.replace(" ","");
                }
                //then check whether the operator has correct parameters
                //System.out.println(midResult);
                if(parseOperator(midResult)==false){
                    return null;
                }
                
				//calculate function according to midResult
                if(calculateStep(midResult)==false){
                    return null;
                }
                
                //System.out.println("Middle operation--->"+midResult);
                //System.out.println("the cal result of this midResult is: "+newchar);
				//newchar = "ROC";
				result = result.replace(oldchar,newchar);
				//System.out.println(result);
				rightBra = result.indexOf(')');
			}
		}
		leftBra = result.indexOf('(');
        ////check whether the number of left brackets is equal to right bracket
		if(leftBra!=-1){
			Parse.error("no enough right brackets");
			return null;
		}
		else{
            midResult = result;
            oldchar = result;
            if(parsePoint(midResult)==false){
                return null;
            }
            if (parseSpace(midResult)==false){
                return null;
            }
            else {
                midResult = midResult.replace(" ","");
            }
            //then check whether the operator has correct parameters
            if(parseOperator(midResult)==false){
                return null;
            }
			//calculate function according to midResult
            if(calculateStep(midResult)==false){
                return null;
            }
           // System.out.println("final operation--->"+midResult);
            //newchar = "ROC";
            result = result.replace(oldchar,newchar);
			//System.out.println(result);
			return result;
		}
	}
	//this method replace the following operators:
    //:eq: ---> {
    //:ne: ---> }
    //:gt: ---> @
    //:lt: ---> #
    //:ge: ---> $
    //:le: ---> %

    //this method check whether the character before and after . is correct
    public boolean parsePoint(String input){
        if(input.matches(".*[^0-9]\\..*")){
        	Parse.error("illegal character before decimal point");
            return false;
        }
        else if(input.matches(".*\\.[^0-9].*")){
        	Parse.error("illegal character behind decimal point");
            return false;
        }
        else if(input.matches(".*[.]")){
        	Parse.error("there shoulb be a number behind the decimal point");
            return false;
        }
        else if(input.matches("[.].*")){
        	Parse.error("there shoulb be a number before the decimal point");
            return false;
        }
        else
            return true;
    }
	//this method check whether there are space between number and character
	public boolean parseSpace(String input){
		if(input.matches(".*[a-z0-9A-Z]+[  ]+[a-z0-9A-Z]+.*")){
			Parse.error("illegal space");
            return false;
		}
        else{
            return true;
        }
	} 

	//remove the space in the input string
//	public void removeSpace(String input){
//		input = input.replace(" ","");
//	}

    public boolean parseCharacter(String input){
        if(input.compareTo("")==0){
        	Parse.error("the condition can not be empty");
            return false;
        }
        else if(input.matches(".*[{}@#$%].*")){
        	Parse.error("There are illegal characters");
            return false;
        }
        else{
            //this method replace the following operators:
            //:eq: ---> {
            //:ne: ---> }
            //:gt: ---> @
            //:lt: ---> #
            //:ge: ---> $
            //:le: ---> %
            input = input.replace(":eq:","{");
            input = input.replace(":ne:","}");
            input = input.replace(":gt:","@");
            input = input.replace(":lt:","#");
            input = input.replace(":ge:","$");
            input = input.replace(":le:","%");
            //System.out.println(input);
            if(input.matches(".*[^ .(){}@#$%_+\\-*/\\^|~a-z0-9A-Z].*")){
            	Parse.error("There are illegal characters");
                return false;
            }
            else if(input.matches(".*[^ \\^|~{}@#$%+\\-*/(][(].*")){
            	Parse.error("There are illegal characters before (");
                return false;
            }
            else if(input.matches(".*[)][^ \\^|~{}@#$%+\\-*/)].*")){
            	Parse.error("There are illegal characters after )");
                return false;
            }
                return true;
        }
    }
	//check whether the operator has correct parameters
    
	public boolean parseOperator(String input){
        //check whether the input is empty
        //System.out.println("input of parseOperator:"+input);
        if(input.compareTo("")==0){
        	Parse.error("can not be empty");
            return false;
        }
        //check whether the oprators are repeat
		if(input.matches(".*[+*/^|{}@#$%]{2,}.*")) {
			Parse.error("illegal repeat of operators");
            return false;
		}
		//check whether the character around "-" is valid
		else if(input.matches(".*[^+\\-*/{}@#$%a-z0-9A-Z_][\\-].*")) {
			Parse.error("the character before \"-\" is invalid");
            return false;
		}
		else if(input.matches(".*[\\-][^a-z0-9A-Z\\-].*")) {
			Parse.error("the character after \"-\" is invalid");
            return false;
		}
        //check whether the binary operator has enough operatant
        else if(input.matches("[+*/^|{}@#$%].*")){
        	Parse.error("no enough operant for the operator");
            return false;
        }
        else if(input.matches(".*[+\\-*/^|{}@#$%]")){
        	Parse.error("no enough operant for the operator");
            return false;
        }
        //check whether the binary operator has correct first operatant
		else if(input.matches(".*[^a-z0-9A-Z_`!][+*/^|{}@#$%].*")){
			Parse.error("first operatant is illegal1");
            return false;
		}
        //check whether the binary operator has correct second operatant
        else if(input.matches(".*[+\\-*/^|@#$%][^a-z0-9A-Z_`!\\-].*")){
        	Parse.error("operatant is illegal2");
            return false;
        }
        //check whether the "==" and "!=" has correct second operatant
        else if(input.matches(".*[{}][^\\-~a-z0-9A-Z`!].*")){
        	Parse.error("operatant is illegal3");
            return false;
        }
        //check whether the "~" has correct operatant
       // else if(input.matches(".*[^|\\^~{][~]+.*")){
        //    System.out.println("there should be no operatant before ~");
        //    return false;
       // }
        //the calculateSetp return boolean:
        //`-------->true
        //!-------->false
        else if(input.matches(".*[~]+[^`!~].*")){
       // else if((input.matches(".*[~]+.*")==true)&&(input.matches(".*[~]+[`!].*")==false)){
        	Parse.error("the operatant after ~ is illegal");
            return false;
        }
       
        else
            return true;
	}
    
    //calcalate a form-valid formula
    //the priority:
    //~
    //* /
    //+ -
    //@(:gt:), $(:ge:), #(:lt:), %(:le:)
    //{(:eq:), }(:ne:)
    //^
    //|
    public boolean calculateStep(String input){
        String[] operatorSet = new String[]{"+","*","/","{","}","@","#","$","%","^","|"};
        for(String oper:operatorSet){
            input = input.replace(oper," "+oper+" ");
        }
        int indexMinus = input.indexOf("-");
        while(indexMinus!=-1){
        	if(indexMinus==0){
        		indexMinus = input.indexOf("-",indexMinus+1);
        	}
        	else if(input.substring(indexMinus-1,indexMinus).matches("[\\w]")){
        		int length = input.length();
        		input = input.substring(0,indexMinus)+" + "+input.substring(indexMinus, length);
        		indexMinus = input.indexOf("-",indexMinus+4);
        	}
        	else{
        		indexMinus = input.indexOf("-",indexMinus+1);
        	}
        }
        //System.out.println("input of calculateStep is: "+input);
        String[] items = input.split("[ ]+");
        for(String item:items){
          //  System.out.println("item---->"+item);
            if(item.matches("[\\d]+")){
                operatant.push(item);
                operatantType.push("i");//this operatant is integer
           //     System.out.println("this is a int");
                continue;
            }
            else if(item.matches("[\\-]+[\\d]+")){
            	int lastindexMinus = item.lastIndexOf("-");
                int itemlength = item.length();
            	if (lastindexMinus!=-1){
                	if(lastindexMinus%2==0){
                		item = item.substring(lastindexMinus,itemlength);
                	}
                	else{
                		item = item.substring(lastindexMinus+1,itemlength);
                	}
                }
                operatant.push(item);
                operatantType.push("i");//this operatant is integer
           //     System.out.println("this is a int");
                continue;           	
            }
            else if(item.matches("[\\w]+"))
            {
                item = item.toLowerCase();
            	if(RudiInnerStructure.getAttributes_of_Var_From_PeekMap(item)==null){
                	Parse.error("no variable named "+item);
                	return false;
                }
                else{
                	String variableValue = RudiInnerStructure.getAttributes_of_Var_From_PeekMap(item)[1];
                	operatant.push(variableValue);
                	String variableTypeRaw = RudiInnerStructure.getAttributes_of_Var_From_PeekMap(item)[0];
                	switch(variableTypeRaw){
                	case "integer":{
                		operatantType.push("i");
                		break;
                	}
                	case "string":{
                		operatantType.push("s");
                		break;
                	}
                	case "float":{
                		operatantType.push("f");
                		break;
                	}
                	}
                }
            //    System.out.println("this is a variable");//this operatant is vrible
                continue;
            }
            else if(item.matches("[\\-]+[\\w]+"))
            {
            	int lastindexMinus = item.lastIndexOf("-");
            	String checkItem = item.toLowerCase().substring(lastindexMinus+1,item.length());
                if(RudiInnerStructure.getAttributes_of_Var_From_PeekMap(checkItem)==null){
                    Parse.error("no variable named"+checkItem);
                    return false;
                }
                else{
                	String variableValue = RudiInnerStructure.getAttributes_of_Var_From_PeekMap(checkItem)[1];
                	if (lastindexMinus!=-1){
                    	if(lastindexMinus%2==0){
                    		operatant.push("-"+variableValue);
                    	}
                    	else{
                    		operatant.push(variableValue);
                    	}
                    }
                	
                    String variableTypeRaw = RudiInnerStructure.getAttributes_of_Var_From_PeekMap(checkItem)[0];
                    switch(variableTypeRaw){
                        case "integer":{
                            operatantType.push("i");
                            break;
                        }
                        case "string":{
                            operatantType.push("s");
                            break;
                        }
                        case "float":{
                            operatantType.push("f");
                            break;
                        }
                    }
                }
                //    System.out.println("this is a variable");//this operatant is vrible
                continue;
            }
            else if(item.matches(".*\\..*"))
            {
                int lastindexMinus = item.lastIndexOf("-");
                int itemlength = item.length();
            	if (lastindexMinus!=-1){
                	if(lastindexMinus%2==0){
                		item = item.substring(lastindexMinus,itemlength);
                	}
                	else{
                		item = item.substring(lastindexMinus+1,itemlength);
                	}
                }
            	try{
            		System.out.println(item);
                    Float.parseFloat(item);
                    operatant.push(item);
                    operatantType.push("f");
              //      System.out.println("this is a float");//this operatant is float
                    continue;
                }
                catch(NumberFormatException e){
                	Parse.error("wrong use of \".\"");
                    return false;
                }
            }
            else if(item.matches("[~]*[`!]")){
             //   System.out.println("the result of the logic not is: "+logicNot(item));
             //   System.out.println("logic not");
                if(logicNot(item))
                    operatant.push("`");
                else
                    operatant.push("!");
                
                operatantType.push("b");//this operatant is boolean
                continue;
                
            }
            else if(item.matches("[*/+\\-@$#%{}\\^|]"))
            {
                if(operator.empty()==false){
                    String op1 = operator.peek();
                    int prio1 = priorityNum(op1);
                    int prio2 = priorityNum(item);
                    while(prio1<=prio2){
                        operator.pop();
                      //  operator.push(item);
                        String operatant2 = operatant.pop();
                        String operatant1 = operatant.pop();
                        String operatantType2 = operatantType.pop();
                        String operatantType1 = operatantType.pop();
                        if(calculateModule(operatant1,operatantType1,operatant2,operatantType2,op1)==false){
                            return false;
                        }
                        else{
                            operatant.push(calStepResult);
                            operatantType.push(calStepResultType);
                        }
                        if(operator.empty()){
                        	break;
                        }
                        else{
                        	op1  = operator.peek();
                        	prio1 = priorityNum(op1);
                            prio2 = priorityNum(item);
                        }
                    }
                    
                    operator.push(item);
                    
                }
                else{
                    operator.push(item);
                }
                continue;
            }
        }
        while(operator.empty()==false){
            String op = operator.pop();
            String operatant2 = operatant.pop();
            String operatant1 = operatant.pop();
            String operatantType2 = operatantType.pop();
            String operatantType1 = operatantType.pop();
            if(calculateModule(operatant1,operatantType1,operatant2,operatantType2,op)==false){
                return false;
            }
            else{
            //	System.out.println("before push: "+calStepResult);
                operatant.push(calStepResult);
                operatantType.push(calStepResultType);
            }
        }
        newchar = operatant.pop();
        return true;
    }
    
    public boolean calculateModule(String opt1,String type1,String opt2,String type2, String op){
        //check whether the operatant type is correct
        if(type1.matches("[i]")&&type2.matches("[i]")){
            int o1 = Integer.parseInt(opt1);
            int o2 = Integer.parseInt(opt2);
            switch(op){
				case "+": {
					calStepResult = o1 + o2 + "";
					calStepResultType = "i";
					return true;
				}
				case "-": {
					calStepResult = o1 - o2 + "";
					calStepResultType = "i";
					return true;
				}
				case "*": {
					calStepResult = o1 * o2 + "";
					calStepResultType = "i";
					return true;
				}
				case "/": {
					if (o2 == 0) {
						Parse.error("0 cannot be a divisor");
						return false;
					} else {
					//	System.out.println(o1 / o2);
						calStepResult = Integer.toString(o1 / o2);
					//	System.out.println("after calculate: "+calStepResult);
						calStepResultType = "i";
						return true;
					}
				}
				case "@": {
					calStepResult = ((o1 > o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "#": {
					calStepResult = ((o1 < o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "$": {
					calStepResult = ((o1 >= o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "%": {
					calStepResult = ((o1 <= o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "{": {
					calStepResult = ((o1 == o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "}": {
					calStepResult = ((o1 != o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "^": {
					Parse.error("type error");
					return false;
				}
				case "|": {
					Parse.error("type error");
					return false;
				}
				default:
					break;
			}
		}
        else if (type1.matches("[if]") && type2.matches("[if]")) {
			float o1 = Float.parseFloat(opt1);
			float o2 = Float.parseFloat(opt2);
			switch (op) {
				case "+": {
					calStepResult = o1 + o2 + "";
					calStepResultType = "f";
					return true;
				}
				case "-": {
					calStepResult = o1 - o2 + "";
					calStepResultType = "f";
					return true;
				}
				case "*": {
					calStepResult = o1 * o2 + "";
					calStepResultType = "f";
					return true;
				}
				case "/": {
					if (o2 == 0) {
						Parse.error("0 cannot be a divisor");
						return false;
					} else {
						calStepResult = o1 / o2 + "";
						calStepResultType = "f";
						return true;
					}
				}
				case "@": {
					calStepResult = ((o1 > o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "#": {
					calStepResult = ((o1 < o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "$": {
					calStepResult = ((o1 >= o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "%": {
					calStepResult = ((o1 <= o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "{": {
					calStepResult = ((o1 == o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "}": {
					calStepResult = ((o1 != o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "^": {
					Parse.error("type error");
					return false;
				}
				case "|": {
					Parse.error("type error");
					return false;
				}
				default:
					break;
			}
		}
        else if (type1.matches("[b]") && type2.matches("[b]")) {
			boolean o1 = (opt1.compareTo("`") == 0) ? true : false;
			boolean o2 = (opt2.compareTo("`") == 0) ? true : false;
			switch (op) {
				case "+":
				case "-":
				case "*":
				case "/":
				case "@":
				case "#":
				case "$":
				case "%": {
					Parse.error("type error");
					return false;
				}
				case "{": {
					calStepResult = ((o1 == o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "}": {
					calStepResult = ((o1 == o2) ? "`" : "!");
					calStepResultType = "b";
					return true;
				}
				case "^": {
					if ((o1 == false) || (o2 == false)) {
						calStepResult = "!";
						calStepResultType = "b";
					} else {
						calStepResult = "`";
						calStepResultType = "b";
					}
					return true;
				}
				case "|": {
					if ((o1 == false) && (o2 == false)) {
						calStepResult = "!";
						calStepResultType = "b";
					} else {
						calStepResult = "`";
						calStepResultType = "b";
					}
					return true;
				}
				default:
					break;
				}
		}
        else {
        	Parse.error("type error");
			return false;
		}
        return true;
    }
           
    public boolean logicNot(String notInput){
        if((notInput.length()%2)==0){
            if(notInput.charAt(notInput.length()-1)=='`'){
                return false;
            }
            else{
                return true;
            }
        }
        else{
            if(notInput.charAt(notInput.length()-1)=='`'){
                return true;
            }
            else{
                return false;
            }
        }
    }
    
    public int priorityNum(String op1)
    {
        switch(op1){
            case "!":{
                number = 0;
                break;
            }
            case "~":{
                number = 1;
                break;
            }
            case "*":
            case "/":
            {
                number = 2;
                break;
            }
            case "+":
            case "-":
            {
                number = 3;
                break;
            }
            case "@":
            case "#":
            case "$":
            case "%":
            {
                number = 4;
                break;
            }
            case "{":
            case "}":
            {
                number = 5;
                break;
            }
            case "^":
            {
                number = 6;
                break;
            }
            case "|":
            {
                number = 7;
                break;
            }
        }
        return number;
    }
}