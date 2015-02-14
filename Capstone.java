// The main class used to call parse and main program
public class Capstone {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String input;
		//interface to users to input rudi file and rudi command
		System.out.println("Rudi interpreter is running......");
		System.out.println("Plaase Enter \"rudi\" command with your file name.(\"e.g. rudi foobar.rudi\")");
		//if input command or file is not valid, throw a error and continue get input
		while(true){
			input = Action.keyInput();
			if(input.trim().matches("^rudi( +)[a-zA-Z_][a-zA-Z_0-9]*.rudi$")){
				break;
			}else{
				System.out.println("Your input is invalid, please re-enter.");
			}
		}
		//according to input, read from specified filename 
		readFile.readToFile(input.replaceAll("^rudi", "").trim());
		//read file from begin to end. read program and subroutine, save subroutine to HaspMap
		Parse.parse_Main_Sub();
		//push the variable hashmap of program into a varmapstack
		//all avriables in the haspmap can be accessed now
		RudiInnerStructure.pushTo_VarMapStack(RudiInnerStructure.Main_Var_Map());
		//begin the rudi program.
		Action.program();
	}
}
