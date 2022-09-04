package games.speed;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import games.classic.Classic;

public class Speed {

	public static void main(String[] args) throws FileNotFoundException {
		
		System.out.println("The answer is " + giveAnswer("48+32=80", "10011100"));
	}
	
	public static String giveAnswer(String firstCalculation, String firstCombination) throws FileNotFoundException {
		
		Scanner scanner = new Scanner(System.in);
		
		ArrayList<String> allowedCalculations = Classic.fetchCalculationsFromFile("classic");
		ArrayList<String> possibleCalculations = Classic.fetchPossibleCalculations(firstCalculation, firstCombination, allowedCalculations);
		ArrayList<String> combinations = Classic.generateCombinations(new ArrayList<>(Arrays.asList("0", "1", "2")), 1, 8);
		
		while (possibleCalculations.size() > 2) {
			String bestCalculation = Classic.findBestCalculation(allowedCalculations, combinations, possibleCalculations);
			System.out.println(bestCalculation);
			String combination = scanner.next();
			possibleCalculations = Classic.fetchPossibleCalculations(bestCalculation, combination, possibleCalculations);
		}
		
		String bestCalculation = possibleCalculations.get(0);
		if (possibleCalculations.size() == 1) {
			scanner.close();
			return bestCalculation;
		}
		System.out.println(bestCalculation);
		String combination = scanner.next();
		scanner.close();
		if (!combination.contains("0") && !combination.contains("1")) {
			return bestCalculation;
		}
		return possibleCalculations.get(1);
	}

}
