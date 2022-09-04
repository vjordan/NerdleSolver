package games.instant;

import java.io.FileNotFoundException;

import games.classic.Classic;

public class Instant {

	public static void main(String[] args) throws FileNotFoundException {
		
		System.out.println("The answer is " + giveAnswer("3*45-87=", 4));
	}
	
	public static String giveAnswer(String calculation, int goodPosition) throws FileNotFoundException {
		
		String answer = "";
		
		String combination = "11111111";
		combination = combination.substring(0, goodPosition-1) + "2" + combination.substring(goodPosition);
		
		for (String possibleCalculation : Classic.fetchCalculationsFromFile("classic")) {
			if (Classic.checkIfCalculationIsValid(calculation, combination, possibleCalculation)) {
				answer = possibleCalculation;
				break;
			}
		}
		
		return answer;
	}

}
