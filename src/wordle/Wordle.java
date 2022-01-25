package wordle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Wordle {
	public static final int[] primes = new int[] { 2, 3, 5, 7, 11, 1 };
	// 1 is the spookiest prime
	
	public static final String PATH_TO_DICT = "src/wordle/words.txt";
	public static final String PATH_TO_DATA = "src/wordle/data.txt";
	
	
	public static void main(String[] args) throws FileNotFoundException {
		List<String> dict = new ArrayList<>();
		Scanner sc = new Scanner(new File(PATH_TO_DICT));
		while (sc.hasNext()) {
			String word = sc.nextLine();
			dict.add(word);
		}
		sc.close();
		
		Map<Character, LetterInfo> godelData = new HashMap<>();
		sc = new Scanner(new File(PATH_TO_DATA));
		
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			line = line.replace('_', '5');
			
			char c = line.charAt(0);
			String[] params = line.substring(1).split(", ");
			if (params.length == 1) {
				LetterInfo info = new LetterInfo(1, primes[0], 0, true);
				godelData.put(c, info);
				continue;
			}
			
			int green = 1;
			for (String num : params[0].split(" ")) {
				if (!"".equals(num)) {
					green *= primes[Integer.parseInt(num)];
				}
			}
			
			int orangeAndGray = 1;
			for (String num : params[1].split(" ")) {
				if (!"".equals(num)) {
					orangeAndGray *= primes[Integer.parseInt(num)];	
				}
			}
			
			boolean excess = false;
			for (String num : params[2].split(" ")) {
				if (!"".equals(num) && !"5".equals(num)) {
					orangeAndGray *= primes[Integer.parseInt(num)];
					excess = true;
				}
			}
			int min = Integer.parseInt(params[3]);
			LetterInfo info = new LetterInfo(green, orangeAndGray, min, excess);
			godelData.put(c, info);
		}
		sc.close();
		List<String> words = getPossibleWords(godelData, dict);
		for (String word : words) {
			System.out.println(word);
		}
	}
	
	private static List<String> getPossibleWords(Map<Character, LetterInfo> knownValues, List<String> dict) {
		List<String> ret = new ArrayList<>();
		for (String word : dict) {
			Map<Character, Integer> candidateValues = computeGodelValues(word);
			boolean possible = true;
			for (char c : knownValues.keySet()) {
				LetterInfo info = knownValues.get(c);
				Integer candidateNum = candidateValues.get(c);
				candidateNum = (candidateNum != null) ? candidateNum : 1;
				if (candidateNum % info.green != 0) { // not all greens of this letter show up in the same spot in candidate
					possible = false;
					break;
				}
				candidateNum /= info.green;
				int min = info.min - numFactors(info.green, 1);
				int actual = numFactors(candidateNum, info.orangeAndGray);
				int candidateExcess = numFactors(candidateNum, 1) - actual;
		
				if (
					actual < min || // not enough copies of this letter to account for all oranges
					candidateExcess > 0 || // word has copies of this letter in orange / grey squares
					(info.excess && actual > min) // more copies of this letter in candidate than # of oranges and letter is known to be gray
				) {
					possible = false;
					break;
				}
			}
			if (possible) {
				ret.add(word);
			}
		}
		return ret;
	}
	
	private static Map<Character, Integer> computeGodelValues(String word) {
		Map<Character, Integer> ret = new HashMap<>();
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (ret.containsKey(c)) {
				ret.put(c, ret.get(c) * primes[i]);
			} else {
				ret.put(c,  primes[i]);
			}
		}
		return ret;
	}
	
	private static int numFactors(int num, int ignoring) {
		int count = 0;
		for (Integer p : primes) {
			if (num % p == 0 && ignoring % p != 0) {
				count++;
			}
		}
		return count;
	}
}
