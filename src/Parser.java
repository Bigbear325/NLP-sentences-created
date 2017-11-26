/**
 * Parser based on the CYK algorithm.
 */

import java.io.*;
import java.util.*;

public class Parser {

	public Grammar g;

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
	}

	/**
	 * Print the parse obtained after calling parse()
	 */
	public String PrintOneParse() {
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
					System.out.println("(ROOT " + parser.PrintOneParse() + " " + ")");
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
