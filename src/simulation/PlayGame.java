package simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import games.classic.Classic;

public class PlayGame {

	public static void main(String[] args) throws IOException {
		
		//playGame("classic");
		//playGame("mini");
	}
	
	public static void playGame(String gameMode) throws IOException {
		
		if (gameMode == "mini") {
			Classic.bestFirstCalculation = "4*7=28";
		}
		
		FileWriter fileResults = new FileWriter(new File("src/simulation/results.txt"));
	    BufferedWriter fileWriter = new BufferedWriter(fileResults);
	    StringBuilder contentFile = new StringBuilder();
	    
	    ArrayList<String> allowedCalculations = Classic.fetchCalculationsFromFile(gameMode);
		ArrayList<String> possibleCalculationsFile = new ArrayList<>(allowedCalculations);
		ArrayList<String> combinations = Classic.generateCombinations(new ArrayList<>(Arrays.asList("0", "1", "2")), 1, gameMode == "classic" ? 8 : 6);
		
		HashMap<String, String> bestSecondCalculations = Classic.fetchBestSecondCalculations(gameMode);
		String calculationFoundCombination = computeCombination(Classic.bestFirstCalculation, Classic.bestFirstCalculation);
			
		HashMap<Integer, Integer> results = new HashMap<>();
		for (int i = 1; i <= 9; i++) {
			results.put(i, 0);
		}
		for (String possibleCalculationFile : possibleCalculationsFile) {
			System.out.println(possibleCalculationFile);
			contentFile.append(possibleCalculationFile + " : ");
			ArrayList<String> possibleCalculations = new ArrayList<>(possibleCalculationsFile);
			String bestCalculation = "";
			String combination = "";
			int numberAttempts = 0;
			while (possibleCalculations.size() > 2) {
				if (bestCalculation == "") {
					bestCalculation = Classic.bestFirstCalculation;
				} else if (bestCalculation == Classic.bestFirstCalculation) {
					bestCalculation = bestSecondCalculations.get(combination);
				} else {
					bestCalculation = Classic.findBestCalculation(allowedCalculations, combinations, possibleCalculations);
				}
				combination = computeCombination(possibleCalculationFile, bestCalculation);
				possibleCalculations = Classic.fetchPossibleCalculations(bestCalculation, combination, possibleCalculations);
				contentFile.append(bestCalculation + "(" + combination + ") ");
				numberAttempts++;
			}
			if (possibleCalculations.size() == 1) {
				if (!bestCalculation.equals(possibleCalculationFile)) {
					contentFile.append(possibleCalculationFile + "(" + calculationFoundCombination + ")");
					numberAttempts++;
				}
			} else {
				bestCalculation = possibleCalculations.get(0);
				if (bestCalculation.equals(possibleCalculationFile)) {
					contentFile.append(possibleCalculationFile + "(" + calculationFoundCombination + ")");
					numberAttempts++;
				} else {
					combination = computeCombination(possibleCalculationFile, bestCalculation);
					contentFile.append(bestCalculation + "(" + combination + ") " + possibleCalculationFile + "(" + calculationFoundCombination + ")");
					numberAttempts+=2;
				}
			}
			results.put(numberAttempts, results.get(numberAttempts)+1);
			contentFile.append("\n");
		}
		contentFile.append("\n");
		
		double average = 0;
		for (int i = 1; i <= 9; i++) {
			int result = results.get(i);
			if (result > 0) {
				contentFile.append(i + " : " + result + " | ");
				average += i * result;
			}
		}
		average /= possibleCalculationsFile.size();
		contentFile.append("\nAverage = " + average);
		
		fileWriter.write(contentFile.toString());
		fileWriter.close();
	}
	
	public static String computeCombination(String answer, String attempt) {
		
		String combination = "";
		
		int calculationLength = answer.length();
		for (int i = calculationLength-1; i >= 0; i--) {
			if (answer.charAt(i) == attempt.charAt(i)) {
				combination = "2" + combination;
				answer = answer.substring(0, i) + answer.substring(i+1);
			} else {
				combination = "0" + combination;
			}
		}
		
		for (int i = 0; i < calculationLength; i++) {
			if (combination.charAt(i) == '0') {
				String character = Character.toString(attempt.charAt(i));
				if (answer.contains(character)) {
					combination = combination.substring(0, i) + "1" + combination.substring(i+1);
					if (character.equals("+") || character.equals("*")) {
						answer = answer.replaceFirst("\\" + character, "");
					} else {
						answer = answer.replaceFirst(character, "");
					}
				}
			}
		}
		
		return combination;
	}

}
