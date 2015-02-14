
import java.util.HashMap;
import java.util.Stack;

//This is a base class. 
//It has some attribute for future extending if, else, while and subroutine
public class ComplexFxn {
	Stack<String> brackets_stack = new Stack<String>();
	Boolean is_valid = true;
	Boolean condition = false;
	String code;
	public ComplexFxn(String code){
		this.code = code.trim();
	}
} 
//a while class to create a new while
//each while process has its owe attributes. Only when its loop is over, its attributes will be expired
//That is the way how to implement loop, iteration and nested
class while_fxn extends ComplexFxn{
	//used to mark the beginning position of the while
	private int markline = -1;
	//initialize its super attributes
	public while_fxn(String code){
		super(code);
	}
	public void dowhile(){
		setMarkline(RudiInnerStructure.getCodeLineNum());
		//continue execute until the loop is out of range
		while(true){
			this.code=readFile.getCodeByLine().trim();
			String tempcode = this.code.trim();
			//mark the while position
			tempcode = Operation.removeCaseInsentive(tempcode,"while",true).trim();
			if(tempcode.matches("\\(.*\\)")){
				//get condition part of code
				String condition_code = tempcode.replaceAll("\\(|\\)", "").trim();
				//user parseOperation API to parse true of false of condition
				ParseOperation math = new ParseOperation(condition_code);
				String tempString  = math.parseInput();
				//check its condition
				if(tempString.equals("`")){
					this.condition = true;
				}else{
					this.condition = false;
				}
			//
			}else if(tempcode.matches("\\(.*\\).+")){
				Parse.error("There can not be anything behind WHILE condition.");
			}else{
				Parse.error("Missing condition in WHILE statement");
			}
			RudiInnerStructure.addCodeLineNum();
			this.code=readFile.getCodeByLine().trim();
			while(this.is_valid==true){
				Operation.morefxn_oneline(this);
				RudiInnerStructure.addCodeLineNum();
				this.code=readFile.getCodeByLine().trim();
			}
			if(this.condition==false){
				break;
			}
			this.is_valid=true;
			RudiInnerStructure.setCodeLineNum(getMarkline());
		}
		RudiInnerStructure.minusCodeLineNum();
	}
	public int getMarkline() {
		return this.markline;
	}
	public void setMarkline(int markline) {
		this.markline = markline;
	}
	public boolean hasMarkLindSet(){
		if(this.markline<0){
			return false;
		}else{
			return true;
		}
	}
	public void resetMarkLindSet(){
		this.markline = -1;
	}
}
class if_fxn extends ComplexFxn{
	public if_fxn(String code){
		super(code);
	}
	public void doif(){
		String tempcode = this.code.trim();
		tempcode = Operation.removeCaseInsentive(tempcode,"if",true).trim();
		if(tempcode.matches("\\(.*\\) +then")){
			//get condition code
			String condition_code = tempcode.replaceAll("\\(|\\).*", "").trim();
			//get true or false
			ParseOperation math = new ParseOperation(condition_code);
			String tempString  = math.parseInput();
			if(tempString.equals("`")){
				this.condition = true;
			}
		}else if(tempcode.matches("\\(.*\\) +then.+")){
			Parse.error("There can not be anything behind IF-THEN condition.");
		}else{
			Parse.error("Missing condition in IF statement");
		}
		RudiInnerStructure.addCodeLineNum();
		this.code=readFile.getCodeByLine().trim();
		while(this.is_valid==true){
			Operation.morefxn_oneline(this);
			RudiInnerStructure.addCodeLineNum();
			this.code=readFile.getCodeByLine().trim();
		}
		//check and call if-else
		//the same line with if
		if(this.code.trim().matches("else")){
			else_fxn newelse = new else_fxn(this.code);
			if(this.condition==false){
				newelse.condition=true;
			}
			newelse.doelse();
		}else if (this.code.trim().matches(".+else.*")){
			Parse.error("Syntax error, ELSE can be a single line.");
		}else if (this.code.trim().matches(".*else.+")){
			Parse.error("Syntax error, ELSE can be a single line.");
		}
		RudiInnerStructure.minusCodeLineNum();
	}
}
class else_fxn extends ComplexFxn{
	public else_fxn(String code){
		super(code);
	}
	public void doelse(){
		RudiInnerStructure.addCodeLineNum();
		this.code=readFile.getCodeByLine().trim();
		while(this.is_valid==true){
			Operation.morefxn_oneline(this);
			RudiInnerStructure.addCodeLineNum();
			this.code=readFile.getCodeByLine().trim();
		}
		if(this.brackets_stack.empty()==false){
			Parse.error("Brackets do not match.");
		}
		//RudiInnerStructure.addCodeLineNum();
		//this.code=readFile.getCodeByLine().trim();
	}
}
class sub_fxn extends ComplexFxn{
	/*----------------------important elements in sub-------------*/
	private HashMap<String,String[]> Sub_Var_Map;
	private int sub_line_num = 0;
	private int sub_ref_num = 0;
	private int parent_line = 0;
	private String parent_ref_name[];
	private String parent_ref_type[];
	private String parent_ref_value[];
	private int parent_ref_num = 0;
	/*-------------------------------------------------------------*/
	public sub_fxn(String code){
		super(code);
		Sub_Var_Map = new HashMap<String,String[]>();
		RudiInnerStructure.pushTo_VarMapStack(Sub_Var_Map);
		this.initialize_Var();
		this.parent_line = RudiInnerStructure.getCodeLineNum();
	}
	public void dosub(){
		String sub_ref_name[];
		String sub_ref_type[];
		String sub_ref_value[];
		//mark original position.
		
		//go-to sub line//set codeline to this.code
		RudiInnerStructure.setCodeLineNum(sub_line_num);
		this.code=readFile.getCodeByLine().trim();
		if(this.sub_ref_num != this.parent_ref_num){
			Parse.error("References number do not match.");
		}
		/*--begin to get sub_var_name---*/
		/*--begin to set sub_var_type_value---*/
		String var_names = this.code.replaceAll("(^(subroutine .+\\()|(\\)$))", "").trim();
		String parts[] = var_names.split(" *, *");
		sub_ref_name = parts;
		sub_ref_type = parent_ref_type;
		sub_ref_value = parent_ref_value;
		
		int i = 0;
		String tempname = null;
		
		while(i<this.sub_ref_num){
			String tempattr[] = new String[2];
			tempname = sub_ref_name[i];
			tempattr[0] = sub_ref_type[i];
			tempattr[1] = sub_ref_value[i];
			this.Sub_Var_Map.put(tempname, tempattr);
			
			i++;
		}
		
		//decs-beign-continue
		RudiInnerStructure.addCodeLineNum();
		this.code=readFile.getCodeByLine().trim();
		
		Action.subroutine();
		/*System.out.println("----------");
		System.out.println(sub_ref_name[0]+":"+Arrays.toString(this.Sub_Var_Map.get(sub_ref_name[0])));
		System.out.println("----------");
		*/
		
		//call update parent var:based no sub_ref_name[] and sub_ref_value[]====>this.parent_ref_name[] this.parent_ref_value[]
		if(this.sub_ref_num!=0){
			update_parent_var(sub_ref_name,this.parent_ref_name);
		}
		
		//done pop var_map from stack(last sentence of dosub())
		RudiInnerStructure.popFrom_VarMapStack();
		//go back to original position
		RudiInnerStructure.setCodeLineNum(this.parent_line);
	}
	private void update_parent_var(String[] sub_ref_name,String[] parent_ref_name){
		int i = 0;
		while(i<sub_ref_num){
			RudiInnerStructure.update_Peek2VarMap(parent_ref_name[i], Sub_Var_Map.get(sub_ref_name[i]));
			i++;
		}
		
	}
	public void initialize_Var(){
		//pasre call syntax
		if (this.code.toLowerCase().matches("(.+)( *)(\\(.*\\))")){
			// check sub_name in Sub_Map
			String sub_name=this.code.replaceAll("\\(.*\\)","").trim();
			if(!RudiInnerStructure.has_subName_Sub_Map(sub_name)){
				Parse.error("This subroutine has not been defined.");
			}else{
				this.sub_line_num = RudiInnerStructure.get_lineNum_from_Sub_Map(sub_name)[0];
				this.sub_ref_num = RudiInnerStructure.get_lineNum_from_Sub_Map(sub_name)[1];
			}
			// initial parent_ref_attr & sub_ref_attr
			this.parent_ref_num = Parse.parseSubVarNum(this.code);
			String var = code.replaceAll("(^(.+\\()|(\\)$))", "").trim();
			if(var.equals("")){
				this.parent_ref_num = 0;
			}else{
				if(var.matches("^,.*")||var.matches(".*,$")){
					Parse.error("Reference syntax is invalid");
				}else{
					String parts[] = var.split(" *, *");
					this.parent_ref_num = parts.length;
					this.parent_ref_name = new String[this.parent_ref_num];
					this.parent_ref_type = new String[this.parent_ref_num];
					this.parent_ref_value = new String[this.parent_ref_num];
					int i=0;
					while(i <  this.parent_ref_num){
						if(RudiInnerStructure.has_Var_From_Peek2Map(parts[i].trim())){
							this.parent_ref_name[i]=parts[i].trim();
							this.parent_ref_type[i]=RudiInnerStructure.getAttributes_of_Var_From_Peek2Map(parts[i].trim())[0].trim();
							this.parent_ref_value[i]=RudiInnerStructure.getAttributes_of_Var_From_Peek2Map(parts[i].trim())[1].trim();
							
							i++;
						}else{
							Parse.error("The called variable has not been defined in your PROGRAM DECS");
						}		
					}
				}
			}

			
		}else{
			Parse.error("This call syntax is invalid.");
		}
	}
	
	public HashMap<String,String[]> Sub_Var_Map(){
		return Sub_Var_Map;
	}
	public void add_to_SubVarMap(String var_name,String parts[]){
		Sub_Var_Map.put(var_name, parts);
	}
	public void update_SubVarMap(String var_name,String parts[]){
		Sub_Var_Map.put(var_name, parts);
	}
	public boolean has_var_in_SubVarMap(String var_name){
		return Sub_Var_Map.containsKey(var_name.toLowerCase());
	}
	public String[] get_value_from_SubVarMap(String var_name){
		return Sub_Var_Map.get(var_name);
	}
}

