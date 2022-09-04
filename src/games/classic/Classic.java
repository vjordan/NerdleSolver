package games.classic;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Classic {
	
	public static String bestFirstCalculation = "48-32=16";
	
	public static double LOG2 = Math.log(2);
	public static double bestEntropy = 0;
	public static String bestCalculation;

	public static void main(String[] args) throws IOException {
		
		//generatePossibleCalculations();
		//findBestFirstCalculation("classic");
		//saveBestSecondCalculations("classic");
		
		System.out.println("The answer is : " + giveAnswer("classic"));
	}
	
	public static String giveAnswer(String gameMode) throws FileNotFoundException {
		
		Scanner scanner = new Scanner(System.in);
		
		ArrayList<String> allowedCalculations = fetchCalculationsFromFile(gameMode);
		ArrayList<String> possibleCalculations = new ArrayList<String>(allowedCalculations);
		ArrayList<String> combinations = generateCombinations(new ArrayList<>(Arrays.asList("0", "1", "2")), 1, gameMode == "classic" ? 8 : 6);
		
		String bestCalculation = "";
		String combination = "";
		while (possibleCalculations.size() > 2) {
			if (bestCalculation == "") {
				bestCalculation = bestFirstCalculation;
			} else if (bestCalculation == bestFirstCalculation) {
				HashMap<String, String> bestSecondCalculations = fetchBestSecondCalculations(gameMode);
				bestCalculation = bestSecondCalculations.get(combination);
			} else {
				bestCalculation = findBestCalculation(allowedCalculations, combinations, possibleCalculations);
			}
			System.out.println(bestCalculation);
			combination = scanner.next();
			possibleCalculations = fetchPossibleCalculations(bestCalculation, combination, possibleCalculations);
		}
		
		bestCalculation = possibleCalculations.get(0);
		if (possibleCalculations.size() == 1) {
			scanner.close();
			return bestCalculation;
		}
		System.out.println(bestCalculation);
		combination = scanner.next();
		scanner.close();
		if (!combination.contains("0") && !combination.contains("1")) {
			return bestCalculation;
		}
		return possibleCalculations.get(1);
	}
	
	public static ArrayList<String> fetchPossibleCalculations(String calculation, String combination, ArrayList<String> possibleCalculations) {
		
		ArrayList<String> newPossibleCalculations = new ArrayList<>();
		
		for (String possibleCalculation : possibleCalculations) {
			if (checkIfCalculationIsValid(calculation, combination, possibleCalculation)) {
				newPossibleCalculations.add(possibleCalculation);
			}
		}
		
		return newPossibleCalculations;
	}
	
	public static String findBestCalculation(ArrayList<String> allowedCalculations, ArrayList<String> combinations, ArrayList<String> possibleCalculations) {
		
		int possibleCalculationsSize = possibleCalculations.size();
		allowedCalculations.parallelStream().forEach(allowedCalculation -> {
			double entropy = 0;
			for (String combination : combinations) {
				if (checkIfCombinationIsValid(allowedCalculation, combination)) {
					int numberValidCalculations = 0;
					for (String possibleCalculation : possibleCalculations) {
						if (checkIfCalculationIsValid(allowedCalculation, combination, possibleCalculation)) {
							numberValidCalculations++;
						}
					}
					if (numberValidCalculations > 0 && numberValidCalculations < possibleCalculationsSize) {
						double ratioValidCalculations = (double) numberValidCalculations / possibleCalculationsSize;
						entropy += -ratioValidCalculations * (Math.log(ratioValidCalculations)/LOG2);
					}
				}
			}
			if (entropy > bestEntropy || (entropy == bestEntropy && possibleCalculations.contains(allowedCalculation))) {
				bestEntropy = entropy;
				bestCalculation = allowedCalculation;
			}
		});
		bestEntropy = 0;
		
		return bestCalculation;
	}
	
	public static boolean checkIfCombinationIsValid(String calculation, String combination) {
		
		ArrayList<Character> charactersToCheck = new ArrayList<>();
		for (int i = 0; i < calculation.length(); i++) {
			char character = calculation.charAt(i);
			char type = combination.charAt(i);
			if (type == '0') {
				charactersToCheck.add(character);
			} else if (type == '1' && charactersToCheck.contains(character)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean checkIfCalculationIsValid(String allowedCalculation, String combination, String possibleCalculation) {
		
		int combinationLength = combination.length();
		for (int i = 0; i < combinationLength; i++) {
			char characterAllowedCalculation = allowedCalculation.charAt(i);
			char characterPossibleCalculation = possibleCalculation.charAt(i);
			if (combination.charAt(i) == '2') {
				if (characterAllowedCalculation != characterPossibleCalculation) {
					return false;
				}
			} else if (characterAllowedCalculation == characterPossibleCalculation) {
				return false;
			}
		}
		
		HashMap<Integer, ArrayList<Integer>> combinationDistribution = new HashMap<>();
		for (int i = combinationLength-1; i >= 0; i--) {
			int type = Integer.parseInt(Character.toString(combination.charAt(i)));
			ArrayList<Integer> previousPositions = combinationDistribution.get(type);
			if (previousPositions == null) {
				combinationDistribution.put(type, new ArrayList<>(Arrays.asList(i)));
			} else {
				previousPositions.add(i);
				combinationDistribution.put(type, previousPositions);
			}
		}
		
		for (int i = 2; i >= 0; i--) {
			ArrayList<Integer> positions = combinationDistribution.get(i);
			if (positions == null) {
				continue;
			}
			for (int position : positions) {
				switch (i) {
				case 2 :
					possibleCalculation = possibleCalculation.substring(0, position) + possibleCalculation.substring(position + 1);
					break;
				case 1 :
					String character = Character.toString(allowedCalculation.charAt(position));
					if (!possibleCalculation.contains(character)) {
						return false;
					}
					if (character.equals("+") || character.equals("*")) {
						possibleCalculation = possibleCalculation.replaceFirst("\\" + character, "");
					} else {
						possibleCalculation = possibleCalculation.replaceFirst(character, "");
					}
					break;
				case 0 :
					if (possibleCalculation.contains(Character.toString(allowedCalculation.charAt(position)))) {
						return false;
					}
					break;
				}
			}
		}
		
		return true;
	}
	
	public static HashMap<String, String> fetchBestSecondCalculations(String gameMode) throws FileNotFoundException {
		
		HashMap<String, String> bestSecondCalculationsByCombinations = new HashMap<>();
		
		Scanner secondCalculations = new Scanner(new File("src/games/" + gameMode + "/bestCalculations.txt"));
		while (secondCalculations.hasNextLine()) {
			String[] lineFile = secondCalculations.nextLine().split(" ");
			if (Integer.parseInt(lineFile[1]) > 2) {
				bestSecondCalculationsByCombinations.put(lineFile[0], lineFile[2]);
			}
        }
		secondCalculations.close();
		
		return bestSecondCalculationsByCombinations;
	}
	
	public static ArrayList<String> generateCombinations(ArrayList<String> combinations, int combinationLength, int expectedLength) {
		
		ArrayList<String> newCombinations = new ArrayList<>();
		for (String combination : combinations) {
			for (int i = 0; i <= 2; i++) {
				newCombinations.add(combination + i);
			}
		}
		
		combinationLength++;
		if (combinationLength == expectedLength) {
			return newCombinations;
		}
		return generateCombinations(newCombinations, combinationLength, expectedLength);
	}
	
	public static ArrayList<String> fetchCalculationsFromFile(String gameMode) throws FileNotFoundException {
		
		ArrayList<String> calculations = new ArrayList<>();
		
		Scanner calculationsFile = new Scanner(new File("src/games/" + gameMode + "/calculations.txt"));
        while (calculationsFile.hasNextLine()) {
        	calculations.add(calculationsFile.nextLine());
        }
        calculationsFile.close();
		
		return calculations;
	}
	
	// FIND BEST FIRST CALCULATION AND SAVE BEST SECOND CALCULATIONS
	
	public static void findBestFirstCalculation(String gameMode) throws FileNotFoundException {
		
		ArrayList<String> possibleCalculations = fetchCalculationsFromFile(gameMode);
		ArrayList<String> combinations = generateCombinations(new ArrayList<>(Arrays.asList("0", "1", "2")), 1, gameMode == "classic" ? 8 : 6);
		System.out.println(findBestCalculation(possibleCalculations, combinations, possibleCalculations));
	}
	
	public static void saveBestSecondCalculations(String gameMode) throws IOException {
		
		FileWriter fileBestSecondCalculations = new FileWriter(new File("src/games/" + gameMode + "/bestCalculations.txt"));
	    BufferedWriter fileWriter = new BufferedWriter(fileBestSecondCalculations);
	    StringBuilder contentFile = new StringBuilder();
		
		ArrayList<String> allowedCalculations = fetchCalculationsFromFile(gameMode);
		ArrayList<String> possibleCalculations = new ArrayList<>(allowedCalculations);
		ArrayList<String> combinations = generateCombinations(new ArrayList<>(Arrays.asList("0", "1", "2")), 1, gameMode == "classic" ? 8 : 6);	
		for (String combination : combinations) {
			if (checkIfCombinationIsValid(bestFirstCalculation, combination)) {
				ArrayList<String> newPossibleCalculations = fetchPossibleCalculations(bestFirstCalculation, combination, possibleCalculations);
				int newPossibleCalculationsSize = newPossibleCalculations.size();
				contentFile.append(combination + " " + newPossibleCalculationsSize);
				System.out.print(combination + " " + newPossibleCalculationsSize);
				if (newPossibleCalculationsSize > 2) {
					String bestCalculation = findBestCalculation(allowedCalculations, combinations, newPossibleCalculations);
					contentFile.append(" " + bestCalculation);
					System.out.print(" " + bestCalculation);
				}
			} else {
				contentFile.append(combination + " 0");
				System.out.print(combination + " 0");
			}
			contentFile.append("\n");
			System.out.println();
		}
		
		fileWriter.write(contentFile.deleteCharAt(contentFile.length()-1).toString());
		fileWriter.close();
	}
	
	// GENERATE POSSIBLE CALCULATIONS
	
	public static void generatePossibleCalculations() throws IOException {
		
		FileWriter fileCalculations = new FileWriter(new File("src/games/classic/calculations.txt"));
	    BufferedWriter fileWriter = new BufferedWriter(fileCalculations);
	    StringBuilder contentFile = new StringBuilder();
		
	    String inputs = "1234567890+-*/=";
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < inputs.length(); j++) {
				for (int k = 0; k < inputs.length(); k++) {
					for (int l = 0; l < inputs.length(); l++) {
						for (int m = 0; m < inputs.length(); m++) {
							for (int n = 0; n < inputs.length(); n++) {
								for (int o = 0; o < inputs.length(); o++) {
									for (int p = 0; p < inputs.length(); p++) {
										String calculation = Character.toString(inputs.charAt(i)) + Character.toString(inputs.charAt(j)) + Character.toString(inputs.charAt(k)) + Character.toString(inputs.charAt(l)) + Character.toString(inputs.charAt(m)) + Character.toString(inputs.charAt(n)) + Character.toString(inputs.charAt(o)) + Character.toString(inputs.charAt(p));
										if (containsOnlyOneEqualsAndAtPosition5Or6Or7(calculation)
												&& doNotContainsTwoConsecutiveOperators(calculation)
												&& hasOnlyANumberAfterTheEquals(calculation)
												&& hasOnlyOneOrTwoOperatorsBeforeTheEquals(calculation)
												&& hasNo0AsPrefixOfNumberAndNoLone0BeforeTheEquals(calculation)
												&& isCorrect(calculation)) {
											contentFile.append(calculation + "\n");
											System.out.println(calculation);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		fileWriter.write(contentFile.deleteCharAt(contentFile.length()-1).toString());
		fileWriter.close();
	}
	
	public static boolean containsOnlyOneEqualsAndAtPosition5Or6Or7(String calculation) {
		
		return calculation.split("=").length == 2 && calculation.charAt(7) != '=' && (calculation.indexOf("=") == 4 || calculation.indexOf("=") == 5 || calculation.indexOf("=") == 6);
	}
	
	public static boolean doNotContainsTwoConsecutiveOperators(String calculation) {
		
		return !(calculation.contains("++") || calculation.contains("+-") || calculation.contains("+*") || calculation.contains("+/")  || calculation.contains("+=")
				|| calculation.contains("-+") || calculation.contains("--") || calculation.contains("-*") || calculation.contains("-/") || calculation.contains("-=")
				|| calculation.contains("*+") || calculation.contains("*-") || calculation.contains("**") || calculation.contains("*/") || calculation.contains("*=")
				|| calculation.contains("/+") || calculation.contains("/-") || calculation.contains("/*") || calculation.contains("//") || calculation.contains("/="));
	}
	
	public static boolean hasOnlyANumberAfterTheEquals(String calculation) {
		
		String potentialNumber = calculation.split("=")[1];
		return !(potentialNumber.contains("+") || potentialNumber.contains("-") || potentialNumber.contains("*") || potentialNumber.contains("/"));
	}
	
	public static boolean hasOnlyOneOrTwoOperatorsBeforeTheEquals(String calculation) {
		
		String potentialCalculation = calculation.split("=")[0];
		int numberOperators = 0;
		for (int i = 0; i < potentialCalculation.length()-1; i++) {
			char character = potentialCalculation.charAt(i);
			if (character == '+' || character == '-' || character == '*' || character == '/') {
				numberOperators++;
				if (numberOperators == 3) {
					return false;
				}
			}
		}
		if (numberOperators == 0) {
			return false;
		}
		return true;
	}
	
	public static boolean hasNo0AsPrefixOfNumberAndNoLone0BeforeTheEquals(String calculation) {
		
		int positionEquals = calculation.indexOf("=");
		if (positionEquals != calculation.length()-2 && calculation.charAt(positionEquals+1) == '0') {
			return false;
		}
		for (int i = 1; i <= positionEquals - 2; i++) {
			char character = calculation.charAt(i);
			if ((character == '+' || character == '-' || character == '*' || character == '/')
					&& calculation.charAt(i+1) == '0') {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isCorrect(String calculation) {
		
		String beforeEquals = calculation.split("=")[0];
		double beforeEqualsCalculation = 0;
		if (beforeEquals.contains("+")) {
			if (!beforeEquals.contains("-") && !beforeEquals.contains("*") && !beforeEquals.contains("/")) {
				if (beforeEquals.split("\\+").length == 2) {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("\\+")[0]) + (double) Integer.parseInt(beforeEquals.split("\\+")[1]);
				} else {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("\\+")[0]) + (double) Integer.parseInt(beforeEquals.split("\\+")[1]) + (double) Integer.parseInt(beforeEquals.split("\\+")[2]);
				}
			} else if (beforeEquals.contains("-")) {
				if (beforeEquals.indexOf("+") < beforeEquals.indexOf("-")) {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("\\+")[0]) + (double) Integer.parseInt(beforeEquals.split("\\+")[1].split("-")[0]) - (double) Integer.parseInt(beforeEquals.split("\\+")[1].split("-")[1]);
				} else {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("-")[0]) - (double) Integer.parseInt(beforeEquals.split("-")[1].split("\\+")[0]) + (double) Integer.parseInt(beforeEquals.split("-")[1].split("\\+")[1]);
				}
			} else if (beforeEquals.contains("*")) {
				if (beforeEquals.indexOf("+") < beforeEquals.indexOf("*")) {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("\\+")[0]) + (double) Integer.parseInt(beforeEquals.split("\\+")[1].split("\\*")[0]) * (double) Integer.parseInt(beforeEquals.split("\\+")[1].split("\\*")[1]);
				} else {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("\\*")[0]) * (double) Integer.parseInt(beforeEquals.split("\\*")[1].split("\\+")[0]) + (double) Integer.parseInt(beforeEquals.split("\\*")[1].split("\\+")[1]);
				}
			} else if (beforeEquals.indexOf("+") < beforeEquals.indexOf("/")) {
				beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("\\+")[0]) + (double) Integer.parseInt(beforeEquals.split("\\+")[1].split("/")[0]) / (double) Integer.parseInt(beforeEquals.split("\\+")[1].split("/")[1]);
			} else {
				beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("/")[0]) / (double) Integer.parseInt(beforeEquals.split("/")[1].split("\\+")[0]) + (double) Integer.parseInt(beforeEquals.split("/")[1].split("\\+")[1]);
			}
		} else if (beforeEquals.contains("-")) {
			if (!beforeEquals.contains("*") && !beforeEquals.contains("/")) {
				if (beforeEquals.split("-").length == 2) {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("-")[0]) - (double) Integer.parseInt(beforeEquals.split("-")[1]);
				} else {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("-")[0]) - (double) Integer.parseInt(beforeEquals.split("-")[1]) - (double) Integer.parseInt(beforeEquals.split("-")[2]);
				}
			} else if (beforeEquals.contains("*")) {
				if (beforeEquals.indexOf("-") < beforeEquals.indexOf("*")) {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("-")[0]) - (double) Integer.parseInt(beforeEquals.split("-")[1].split("\\*")[0]) * (double) Integer.parseInt(beforeEquals.split("-")[1].split("\\*")[1]);
				} else {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("\\*")[0]) * (double) Integer.parseInt(beforeEquals.split("\\*")[1].split("-")[0]) - (double) Integer.parseInt(beforeEquals.split("\\*")[1].split("-")[1]);
				}
			} else if (beforeEquals.indexOf("-") < beforeEquals.indexOf("/")) {
				beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("-")[0]) - (double) Integer.parseInt(beforeEquals.split("-")[1].split("/")[0]) / (double) Integer.parseInt(beforeEquals.split("-")[1].split("/")[1]);
			} else {
				beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("/")[0]) / (double) Integer.parseInt(beforeEquals.split("/")[1].split("-")[0]) - (double) Integer.parseInt(beforeEquals.split("/")[1].split("-")[1]);
			}
		} else if (beforeEquals.contains("*")) {
			if (!beforeEquals.contains("/")) {
				if (beforeEquals.split("\\*").length == 2) {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("\\*")[0]) * (double) Integer.parseInt(beforeEquals.split("\\*")[1]);
				} else {
					beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("\\*")[0]) * (double) Integer.parseInt(beforeEquals.split("\\*")[1]) * (double) Integer.parseInt(beforeEquals.split("\\*")[2]);
				}
			} else if (beforeEquals.indexOf("*") < beforeEquals.indexOf("/")) {
				beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("\\*")[0]) * (double) Integer.parseInt(beforeEquals.split("\\*")[1].split("/")[0]) / (double) Integer.parseInt(beforeEquals.split("\\*")[1].split("/")[1]);
			} else {
				beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("/")[0]) / (double) Integer.parseInt(beforeEquals.split("/")[1].split("\\*")[0]) * (double) Integer.parseInt(beforeEquals.split("/")[1].split("\\*")[1]);
			}
		} else if (beforeEquals.split("/").length == 2) {
			beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("/")[0]) / (double) Integer.parseInt(beforeEquals.split("/")[1]);
		} else {
			beforeEqualsCalculation = (double) Integer.parseInt(beforeEquals.split("/")[0]) / (double) Integer.parseInt(beforeEquals.split("/")[1]) / (double) Integer.parseInt(beforeEquals.split("/")[2]);
		}
		
		int afterEquals = Integer.parseInt(calculation.split("=")[1]);
		if ((afterEquals == 0 || beforeEqualsCalculation % afterEquals == 0) && beforeEqualsCalculation == afterEquals) {
			return true;
		}
		return false;
	}

}
