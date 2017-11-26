/**
 * Generate sentences from a CFG
 * 
 * @author sihong
 *	added generater,  DFS  and RandomRHS function by Zhiheng
 */

import java.io.*;
import java.util.*;

public class Generator {
	
	private Grammar grammar;

	/**
	 * Constructor: read the grammar.
	 */
	public Generator(String grammar_filename) {
		grammar = new Grammar(grammar_filename);
	}

	/**
	 * Generate a number of sentences.
	 */
	public ArrayList<String> generate(int numSentences) {
		ArrayList<String> sentences_List = new ArrayList<>();
		String Starter = "ROOT";
		int Tree_depth = 0;
		for(int i = 0; i < numSentences; ++i){
			String sentence = DFS_helper(Tree_depth, Starter);  //DFS or try backtracking??
			if(sentence != ""){
				sentences_List.add(sentence);
			}
		}
		return sentences_List;
	}



	private String DFS_helper(int DFS_depth, String LHS){

		//if the current node is a terminal, end the DFS. from symbolType() 0 = terminal, 1 = pre-terminal, 2 = non-terminal
		if(grammar.symbolType(LHS) == 0) return LHS;
		//if(DFS_depth == 0)

		//directly define left and right instead of a internal Tree Class
		String tree_Left;
		String tree_Right;

		RHS random_RHS = get_Weighted_Random(LHS);

		int go_Deeper = DFS_depth + 1;

		tree_Left = DFS_helper(go_Deeper, random_RHS.first());
		tree_Right = random_RHS.second() != null ? " " + DFS_helper(go_Deeper, random_RHS.second()) : "";

		return "(" + LHS + " " + tree_Left + tree_Right + ")";
	}

	//return Weighted_Random RHS for LHS
	public RHS get_Weighted_Random(String LHS){

		//use Grammer findProductions() to get List of RHSes for a LHS
		//then use RHS getProb() to return the prob of a RHS

		ArrayList<RHS> RHSes = grammar.findProductions(LHS);

		//find one RHS from the RHSes
		//reference: Weighted Random Sampling using Reservoir
		//https://en.wikipedia.org/wiki/Reservoir_sampling#Weighted_Random_Sampling_using_Reservoir

		RHS current = RHSes.get(0);
		double total_prob = current.getProb();
		Random random = new Random();

		//for(int i = 0; i < RHSes.size(); ++i){
		for(int i = 1; i < RHSes.size(); ++i){
			double current_prob = RHSes.get(i).getProb();
			total_prob += current_prob;
			double prob = current_prob/total_prob;

			//Random random = new Random();

			if(prob >= random.nextDouble()) current = RHSes.get(i);
		}
		return current;
	}

	//able to change the number in g.generate to get multiple sentences.
	public static void main(String[] args) {
		// the first argument is the path to the grammar file.
		Generator g = new Generator(args[0]);
		ArrayList<String> res = g.generate(1);
		for (String s : res) {
			System.out.println(s);
		}
	}
}
