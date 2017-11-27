/**
 * Parser based on the CYK algorithm.
 */

import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class Parser {


	class Pair {
		String left_Rule_Key;  //Key of rule from left
		String down_Rule_Key;  //Key of rule from down
		int[] left_back;
		int[] down_back;
		double pair_prob;
		boolean isTerminal;
	}

	public Grammar g;

	public HashMap<String, Pair> square[][];

	/**
	 * Constructor: read the grammar.
	 */
	public Parser(String grammar_filename) {
		g = new Grammar(grammar_filename);
	}

	/**
	 * Parse one sentence given in the array.
	 */
	public void parse(ArrayList<String> sentence) {

		//init the square using HashMap. So the value such as NP->NP NP, NP-> N
		// can be store in the map which contain the larger pair_prob.
		square = new HashMap[sentence.size()][sentence.size()];
		for(int i = 0; i < sentence.size(); ++i){
			for(int j = i; j < sentence.size(); ++j){
				square[i][j] = new HashMap<String, Pair>();
			}
		}

		for(int j = 0; j < sentence.size(); ++j){
			//Step 1 : All A| A->words[j], init in table
			String word = sentence.get(j);   //e.g. fish
			List<String> pre_Term = g.findPreTerminals(word); //e.g N-> fish,  X-> fish, normally it only have one?
			for(String LHS : pre_Term){
				List<RHS> RHSes = g.findProductions(LHS);
				for(RHS RHS : RHSes){
					if(RHS.first().equals(word)){
						Pair pair = new Pair();
						pair.isTerminal = true;
						pair.left_Rule_Key = word;
						pair.down_Rule_Key = null;
						pair.pair_prob = RHS.getProb();
						square[j][j].put(LHS, pair);
					}
				}
			}
//		}

		//step 2 handle unaries from NLP book
//		for(int j = 0; j < sentence.size(); ++j){
			for(int i = j - 1; i >= 0; --i){
				for(int k = i; k < j; ++k){

					HashMap<String, Pair> hash_left = square[i][k];
					HashMap<String, Pair> hash_down = square[k + 1][j];

					//use RHS search for LHS
					for(String left_Rules_Key : hash_left.keySet()){
						for(String down_Rules_Key : hash_down.keySet()){
							Pair left_Rule = hash_left.get(left_Rules_Key);
							Pair down_Rule = hash_down.get(down_Rules_Key);

							//given the RHS string (such as "VP NP")
							String rhsStr = left_Rules_Key + " " + down_Rules_Key;
							if(g.findLHS(rhsStr) == null) continue;
							for(String LHS : g.findLHS(rhsStr)){  //VP -> V NP get VP based on "V NP" and others.
								for(RHS RHS : g.findProductions(LHS)){
									//RHS.printProduction();
									String ruleTest = RHS.first()+ " " + RHS.second();
									if(ruleTest.equals(rhsStr)) {
										//find match!
										Pair curr_Pair = new Pair();
										curr_Pair.left_Rule_Key = left_Rules_Key;
										curr_Pair.down_Rule_Key = down_Rules_Key;
										curr_Pair.left_back = new int[]{i, k};
										curr_Pair.down_back = new int[]{k+1, j};
										curr_Pair.pair_prob = RHS.getProb() * left_Rule.pair_prob * down_Rule.pair_prob;
										curr_Pair.isTerminal = false;

										//put in to square hash
										if(!square[i][j].containsKey(curr_Pair) || square[i][j].get(LHS).pair_prob < curr_Pair.pair_prob){
											square[i][j].put(LHS, curr_Pair);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}


	private String DFS(String LHS, int[] back_trace){
		int i = back_trace[0];
		int j = back_trace[1];
//		if(i!= j) {
			Pair RHS = square[i][j].get(LHS);
			if (RHS.isTerminal) {
				return " (" + LHS + " " + RHS.left_Rule_Key + ")";
			} else {
				return " (" + LHS + DFS(RHS.left_Rule_Key, RHS.left_back) + DFS(RHS.down_Rule_Key, RHS.down_back) + ")";
			}
//		}
//		return "test";
	}

	/**
	 * Print the parse obtained after calling parse()
	 */
	public String PrintOneParse(int len) {
		int index = len - 1;
		int[] start_position = new int[]{0, index};

		//System.out.println(DFS("S", start_position));
		return DFS("S", start_position);
	}



	public static void main(String[] args) {
		// read the grammar in the file args[0]
		Parser parser = new Parser(args[0]);

		// read a parse tree from a bash pipe
		try {
			InputStreamReader isReader = new InputStreamReader(System.in);
			BufferedReader bufReader = new BufferedReader(isReader);
			while(true) {  //Line by line
				String line = null;
				if((line=bufReader.readLine()) != null) {
					ArrayList<String> sentence = new ArrayList<>(); //added by zhiheng, for store every incomming sentence

					String []words = line.split(" ");
					for (String word : words) {
						word = word.replaceAll("[^a-zA-Z]", "");
						if (word.length() == 0) {
							continue;
						}
						// use the grammar to filter out non-terminals and pre-terminals
						if (parser.g.symbolType(word) == 0 && (!word.equals(".") && !word.equals("!"))) {
							sentence.add(word);   //added by zhiheng example: a president ate the president!
						}
					}
					parser.parse(sentence);

					int len = sentence.size();
					String end = line.substring(line.length() - 2, line.length() - 1);
					System.out.println("(ROOT " + parser.PrintOneParse(len) + " " + end + ")"); //tmp delete end
					//System.out.println("(ROOT " + parser.PrintOneParse() + " " + end + ")");

				}
				else {
					break;
				}
			}
			bufReader.close();
			isReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//parser.parse(sentence);     __ encounter error???? by Zhiheng Liu move to loop
		//System.out.println("(ROOT " + parser.PrintOneParse() + " " + end + ")");
	}
}
