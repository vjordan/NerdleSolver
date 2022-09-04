package games.bi;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import games.classic.Classic;

public class Bi {
	
	public static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws FileNotFoundException {
		
		String[] answers = giveAnswers("classic");
		System.out.println("The answers are " + answers[0] + " and " + answers[1]);
	}
	
	public static String[] giveAnswers(String gameMode) throws FileNotFoundException {
		
		ArrayList<String> allowedCalculations = Classic.fetchCalculationsFromFile(gameMode);
		ArrayList<String> possibleCalculations1 = new ArrayList<String>(allowedCalculations);
		ArrayList<String> possibleCalculations2 = new ArrayList<String>(allowedCalculations);
		ArrayList<String> combinations = Classic.generateCombinations(new ArrayList<>(Arrays.asList("0", "1", "2")), 1, gameMode == "classic" ? 8 : 6);
		
		boolean calculationFound = false;
		String bestCalculation = "";
		String combination1 = "";
		String combination2 = "";
		while (possibleCalculations1.size() != 1 && possibleCalculations2.size() != 1) {
			if (bestCalculation == "") {
				bestCalculation = Classic.bestFirstCalculation;
			} else if (bestCalculation == Classic.bestFirstCalculation && combination1.equals(combination2)) {
				HashMap<String, String> bestSecondCalculations = Classic.fetchBestSecondCalculations(gameMode);
				bestCalculation = bestSecondCalculations.get(combination1);
			} else {
				bestCalculation = findBestCalculation(allowedCalculations, combinations, possibleCalculations1, possibleCalculations2);
			}
			System.out.println("    " + bestCalculation);
			System.out.print("[1] ");
			combination1 = scanner.next();
			System.out.print("[2] ");
			combination2 = scanner.next();
			if (bestCalculation == Classic.bestFirstCalculation && combination1.equals(combination2)) {
				possibleCalculations1 = possibleCalculations2 = Classic.fetchPossibleCalculations(bestCalculation, combination1, possibleCalculations1);
			} else {
				possibleCalculations1 = Classic.fetchPossibleCalculations(bestCalculation, combination1, possibleCalculations1);
				possibleCalculations2 = Classic.fetchPossibleCalculations(bestCalculation, combination2, possibleCalculations2);
			}
			if ((possibleCalculations1.size() == 1 && !combination1.contains("0") && !combination1.contains("1"))
					|| (possibleCalculations2.size() == 1 && !combination2.contains("0") && !combination2.contains("1"))) {
				calculationFound = true;
			}
		}
		
		int possibleCalculations1Size = possibleCalculations1.size();
		if (possibleCalculations1Size != 1 || possibleCalculations2.size() != 1) {
			boolean hasPossibleCalculations1OnlyOneElement = possibleCalculations1Size == 1;
			ArrayList<String> possibleCalculations = hasPossibleCalculations1OnlyOneElement ? possibleCalculations2 : possibleCalculations1;
			if (!calculationFound) {
				bestCalculation = hasPossibleCalculations1OnlyOneElement ? possibleCalculations1.get(0) : possibleCalculations2.get(0);
				possibleCalculations = updatePossibleCalculations(bestCalculation, possibleCalculations);
			}
			while (possibleCalculations.size() > 2) {
				bestCalculation = Classic.findBestCalculation(allowedCalculations, combinations, possibleCalculations);
				possibleCalculations = updatePossibleCalculations(bestCalculation, possibleCalculations);
			}
			if (possibleCalculations.size() == 2) {
				bestCalculation = possibleCalculations.get(0);
				System.out.println("    " + bestCalculation);
				System.out.print("    ");
				String combination = scanner.next();
				if (combination.contains("0") || combination.contains("1")) {
					possibleCalculations = new ArrayList<>(Collections.singletonList(possibleCalculations.get(1)));
				} else {
					possibleCalculations = new ArrayList<>(Collections.singletonList(bestCalculation));
				}
			}
			scanner.close();
			if (hasPossibleCalculations1OnlyOneElement) {
				return new String[]{possibleCalculations1.get(0), possibleCalculations.get(0)};
			} else {
				return new String[]{possibleCalculations.get(0), possibleCalculations2.get(0)};
			}
		}
		
		scanner.close();	
		return new String[]{possibleCalculations1.get(0), possibleCalculations2.get(0)};
	}
	
	public static ArrayList<String> updatePossibleCalculations(String bestCalculation, ArrayList<String> possibleCalculations) {
		
		System.out.println("    " + bestCalculation);
		System.out.print("    ");
		String combination = scanner.next();
		return Classic.fetchPossibleCalculations(bestCalculation, combination, possibleCalculations);
	}
	
	public static String findBestCalculation(ArrayList<String> allowedCalculations, ArrayList<String> combinations, ArrayList<String> possibleCalculations1, ArrayList<String> possibleCalculations2) {
		
		allowedCalculations.parallelStream().forEach(allowedCalculation -> {
			double entropy = 0;
			for (String combination : combinations) {
				if (Classic.checkIfCombinationIsValid(allowedCalculation, combination)) {
					entropy += computeEntropy(allowedCalculation, combination, possibleCalculations1);
					entropy += computeEntropy(allowedCalculation, combination, possibleCalculations2);
				}
			}
			if (entropy > Classic.bestEntropy || (entropy == Classic.bestEntropy && (possibleCalculations1.contains(allowedCalculation) || possibleCalculations2.contains(allowedCalculation)))) {
				Classic.bestEntropy = entropy;
				Classic.bestCalculation = allowedCalculation;
			}
		});
		Classic.bestEntropy = 0;
		
		return Classic.bestCalculation;
	}
	
	public static double computeEntropy(String allowedCalculation, String combination, ArrayList<String> possibleCalculations) {
		
		int possibleCalculationsSize = possibleCalculations.size();
		int numberValidCalculations = 0;
		for (String possibleCalculation : possibleCalculations) {
			if (Classic.checkIfCalculationIsValid(allowedCalculation, combination, possibleCalculation)) {
				numberValidCalculations++;
			}
		}
		
		if (numberValidCalculations > 0 && numberValidCalculations < possibleCalculationsSize) {
			double ratioValidCalculations = (double) numberValidCalculations / possibleCalculationsSize;
			return -ratioValidCalculations * (Math.log(ratioValidCalculations)/Classic.LOG2);
		}
		return 0;
	}

}
