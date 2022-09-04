package games.miniBi;

import java.io.IOException;

import games.bi.Bi;
import games.classic.Classic;

public class MiniBi {

	public static void main(String[] args) throws IOException {
		
		Classic.bestFirstCalculation = "4*7=28";
		String[] answers = Bi.giveAnswers("mini");
		System.out.println("The answers are " + answers[0] + " and " + answers[1]);
	}

}
