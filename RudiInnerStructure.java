import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
// This class is a core controller of this redi interpreter.
// there are several api offered to programmer to access the interpreter's inner functions.
public class RudiInnerStructure {
	//the stack is used to store variable maps, it is used in subroutine iteration calls.
	//The peek map is always the variable map the subroutine can use
	//When there is a subroutine object is released, the map on the peak is popped
	private static LinkedList<HashMap<String,String[]>> Var_MapStack = new LinkedList<HashMap<String,String[]>>();
	//the map is used to store program variables
	//the content structure is <variable name, <variable type, variable value>>
	private static HashMap<String,String[]> Main_Var_Map = new HashMap<String,String[]>();
	//the map is used to store subroutine's name and its variables' attributes
	//the content structure is <subroutine name, its variable numbers>
	private static HashMap<String,Integer[]> Sub_Map = new HashMap<String,Integer[]>();
	//record executed line
	private static int codeLineNum = 0;
	//when use while, the line number is hold
	private static int markLineNum = 0;
	//record continue line number when reading happens to a &
	private static int continueLineNum = 0;
	
	//it is function used put the variable map into the stack.
	public static void pushTo_VarMapStack(HashMap<String,String[]> var_map){
		Var_MapStack.push(var_map);
	}
	//pop the peak map of the stack
	public static HashMap<String,String[]> popFrom_VarMapStack(){
		return Var_MapStack.pop();
	}
	//get the peek map of the stack
	public static HashMap<String,String[]> peekOf_VarMapStack(){
		return Var_MapStack.peek();
	}
	//get the parent map when a subroutine return values to its parent reference
	public static HashMap<String,String[]> peek2Of_VarMapStack(){
		ListIterator<HashMap<String,String[]>> tempIterator = Var_MapStack.listIterator(1);
		return tempIterator.next();
	}
	//update values that exists in the map of the peek of stack
	public static void update_PeekVarMap(String var_name,String parts[]){
		if(Var_MapStack.peek().containsKey(var_name)){
			Var_MapStack.peek().put(var_name, parts);
		}
		//if there is no,the update functions cannot be used
		else{
			Parse.error("There is no such variable in the range to update");
		}
		
	}
	//update values that exists in the map of the parent of subroutine
	public static void update_Peek2VarMap(String var_name,String parts[]){
		ListIterator<HashMap<String,String[]>> tempIterator = Var_MapStack.listIterator(1);
		HashMap<String,String[]> peek2map= tempIterator.next();
		if(peek2map.containsKey(var_name)){
			peek2map.put(var_name, parts);
		}
		//if there is no,the update functions cannot be used
		else{
			Parse.error("There is no such variable in the range to update");
		}
		
	}
	//get all attributes (type and value) of a variable in the peak map of the stack
	public static String[] getAttributes_of_Var_From_PeekMap(String var_name){
		if(Var_MapStack.peek().containsKey(var_name)){
			return Var_MapStack.peek().get(var_name);
		}
		//if there is no such variable, the map return a null string
		else {
			return null;
		}
	}
	//get all attributes (type and value) of a variable in the parent map of the stack
	public static String[] getAttributes_of_Var_From_Peek2Map(String var_name){
		ListIterator<HashMap<String,String[]>> tempIterator = Var_MapStack.listIterator(1);
		HashMap<String,String[]> peek2map= tempIterator.next();
		if(peek2map.containsKey(var_name)){
			return peek2map.get(var_name);
		}
		//if there is no such variable, the map return a null string
		else{
			return null;
		}
	}
	//check if there is a variable in the peak map of the stack
	//if there is such one, return true, or false
	public static Boolean has_Var_From_PeekMap(String var_name){
		if(Var_MapStack.peek().containsKey(var_name)){
			return true;
		}
		else{
			return false;
		}
	}
	//check if there is a variable in the parent map of the stack
	//if there is such one, return true, or false
	public static Boolean has_Var_From_Peek2Map(String var_name){
		ListIterator<HashMap<String,String[]>> tempIterator = Var_MapStack.listIterator(1);
		HashMap<String,String[]> peek2map= tempIterator.next();
		if(peek2map.containsKey(var_name)){
			return true;
		}
		else{
			return false;
		}
	}
	//get the only one map of variable in program
	public static HashMap<String,String[]> Main_Var_Map(){
		return Main_Var_Map;
	}
	//add new variable into the main variable map
	public static void add_to_MainVarMap(String var_name,String parts[]){
		Main_Var_Map.put(var_name, parts);
	}
	//update the variable attributes in the main variable map
	public static void update_MainVarMap(String var_name,String parts[]){
		Main_Var_Map.put(var_name, parts);
	}
	//check if there there is such variable with name in the main variable map
	public static boolean has_var_in_MainVarMap(String var_name){
		return Main_Var_Map.containsKey(var_name.toLowerCase());
	}
	//get all attributes of this main variable name.
	public static String[] get_value_from_MainVarMap(String var_name){
		return Main_Var_Map.get(var_name);
	}
	//get subroutine's variable hashmap
	public static HashMap<String,Integer[]> Sub_Map(){
		return Sub_Map;
	}
	//put variables into subroutine variable map
	public static void add_to_Sub_Map(String sub_name,Integer[] line_num){
		Sub_Map.put(sub_name, line_num);
	}
	//get value according to name
	public static Integer[] get_lineNum_from_Sub_Map(String sub_name){
		return Sub_Map.get(sub_name);
	}
	//check if there is such subroutine in this map
	public static boolean has_subName_Sub_Map(String sub_name){
		return Sub_Map.containsKey(sub_name.toLowerCase());
	}
	//get the number of code line that is being executed.
	public static int getCodeLineNum(){
		return codeLineNum;
	}
	//set a specific line number to the code line record.
	public static void setCodeLineNum(int number){
		codeLineNum = number;
	}
	//automatically add code line number.
	public static void addCodeLineNum(){
		codeLineNum = codeLineNum+1+getContinueLineNum();
		resetContinueLineNum();
	}
	//automatically minus conde line number
	public static void minusCodeLineNum(){
		codeLineNum--;
	}
	//get the marked line number when you want read the marked line
	public static int getMarkLineNum(){
		return markLineNum;
	}
	//set mark to a specific line number
	public static void setMarkLineNum(int number){
		markLineNum = number;
	}
	// set mark to the line that is being executed
	public static void setMarkLineNum(){
		markLineNum = getCodeLineNum();
	}
	//reset the mark line of number to cancel the last set
	public static void resetMarkLineNum(){
		markLineNum = 0;
	}
	//add mark line number automatically
	public static void addMarkLineNum(int number){
		markLineNum++;
	}
	//initialized code line number recorder
	public static void initalizeCodeLineNum(){
		codeLineNum=0;
	}
	//initialized mark line number recorder
	public static void initalizeMarkLineNum(){
		markLineNum=0;
	}
	//get the number of lines that should be regared one line to be executed
	public static int getContinueLineNum() {
		return continueLineNum;
	}
	//add continue line number of a recorder automatically
	public static void addContinueLineNum() {
		continueLineNum = continueLineNum+1;
	}
	//after the entire line read, the continue line number should be clear
	public static void resetContinueLineNum(){
		continueLineNum = 0 ;
	}
	

}
