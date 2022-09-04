package games.mini;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import games.classic.Classic;

public class Mini {
	
	public static String inputs = "1234567890+-*/=";

	public static void main(String[] args) throws IOException {
		
		Classic.bestFirstCalculation = "4*7=28";
		
		//generatePossibleCalculations();
		//Classic.findBestFirstCalculation("mini");
		//Classic.saveBestSecondCalculations("mini");
		
		System.out.println("The answer is " + Classic.giveAnswer("mini"));
	}
	
	public static void generatePossibleCalculations() throws IOException {
		
		FileWriter fileCalculations = new FileWriter(new File("src/games/mini/calculations.txt"));
	    BufferedWriter fileWriter = new BufferedWriter(fileCalculations);
	    StringBuilder contentFile = new StringBuilder();
		
		ArrayList<String> allCalculations = generateAllCalculations(new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9")), 1, 6);
		for (String calculation : allCalculations) {
			if (containsOnlyOneEqualsAndAtPosition4Or5(calculation)
					&& doNotContainsAnOperatorJustBeforeTheEquals(calculation)
					&& Classic.hasOnlyANumberAfterTheEquals(calculation)
					&& hasOnlyOneOperatorBeforeTheEquals(calculation)
					&& Classic.hasNo0AsPrefixOfNumberAndNoLone0BeforeTheEquals(calculation)
					&& isCorrect(calculation)) {
				contentFile.append(calculation + "\n");
				System.out.println(calculation);
			}
		}
		
		fileWriter.write(contentFile.deleteCharAt(contentFile.length()-1).toString());
		fileWriter.close();
	}
	
	public static boolean containsOnlyOneEqualsAndAtPosition4Or5(String calculation) {

		return calculation.split("=").length == 2 && calculation.charAt(5) != '=' && (calculation.indexOf("=") == 3 || calculation.indexOf("=") == 4);
	}
	
	public static boolean doNotContainsAnOperatorJustBeforeTheEquals(String calculation) {

		return !(calculation.contains("+=") || calculation.contains("-=") || calculation.contains("*=") || calculation.contains("/="));
	}
	
	public static boolean hasOnlyOneOperatorBeforeTheEquals(String calculation) {

		String potentialCalculation = calculation.split("=")[0];
		boolean operatorFound = false;
		for (int i = 0; i < potentialCalculation.length()-1; i++) {
			char character = potentialCalculation.charAt(i);
			if (character == '+' || character == '-' || character == '*' || character == '/') {
				if (operatorFound) {
					return false;
				}
				operatorFound = true;
			}
		}
		return operatorFound;
	}
	
	public static boolean isCorrect(String calculation) {
		
		String beforeEquals = calculation.split("=")[0];
		int resultAfterEquals = Integer.parseInt(calculation.split("=")[1]);
		if (calculation.contains("+")) {
			if (Integer.parseInt(beforeEquals.split("\\+")[0]) + Integer.parseInt(beforeEquals.split("\\+")[1]) == resultAfterEquals) {
				return true;
			}
		} else if (calculation.contains("-")) {
			if (Integer.parseInt(beforeEquals.split("-")[0]) - Integer.parseInt(beforeEquals.split("-")[1]) == resultAfterEquals) {
				return true;
			}
		} else if (calculation.contains("*")) {
			if (Integer.parseInt(beforeEquals.split("\\*")[0]) * Integer.parseInt(beforeEquals.split("\\*")[1]) == resultAfterEquals) {
				return true;
			}
		} else {
			int firstNumber = Integer.parseInt(beforeEquals.split("/")[0]);
			int secondNumber = Integer.parseInt(beforeEquals.split("/")[1]);
			if (firstNumber / secondNumber == resultAfterEquals && firstNumber % secondNumber == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static ArrayList<String> generateAllCalculations(ArrayList<String> calculations, int calculationLength, int expectedLength) {
		
		ArrayList<String> newCalculations = new ArrayList<>();
		for (String calculation : calculations) {
			for (int i = 0; i < inputs.length(); i++) {
				newCalculations.add(calculation + Character.toString(inputs.charAt(i)));
			}
		}
		
		calculationLength++;
		if (calculationLength == expectedLength) {
			return newCalculations;
		}
		return generateAllCalculations(newCalculations, calculationLength, expectedLength);
	}

}
