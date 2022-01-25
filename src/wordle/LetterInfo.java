package wordle;

public class LetterInfo {
	int green;
	int orangeAndGray;
	int min;
	boolean excess;
	
	LetterInfo(int green, int orangeAndGray, int min, boolean excess) {
		this.green = green;
		this.orangeAndGray = orangeAndGray;
		this.min = min;
		this.excess = excess;
	}
}
