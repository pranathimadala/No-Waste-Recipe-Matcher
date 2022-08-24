package com.okayjava.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.okayjava.html.clone.ObjectCloner;
import com.okayjava.html.model.User;

@SpringBootApplication
public class CookingApplication {

	private static int individualCounter;

	private static void extractEverything(Scanner readingFile, HashSet<String> titles,
			HashMap<String, ArrayList<String>> bigMap, Queue<String> titlesQ) {

		String extract = "";

		while (readingFile.hasNextLine()) {
			String line = readingFile.nextLine();

			if (line.equals("* Exported from MasterCook *")) {
				readingFile.nextLine();
				extract = readingFile.nextLine();
				extract = extract.trim();
				titles.add(extract);
				titlesQ.add(extract);
				bigMap.put(extract, new ArrayList<String>());

			}

			if (line.equals("--------  ------------  --------------------------------")) {
				String currentLine = readingFile.nextLine();

				// while (!currentLine.isBlank()) {
				while (!(currentLine.length() == 0)) {

					bigMap.get(extract).add(currentLine);

					if (readingFile.hasNextLine()) {
						currentLine = readingFile.nextLine();
					}
				}

			}

		}

	}

	private static void createHTMLPage(BufferedWriter bw) throws IOException {

		bw.write(
				"<html><body style=\"background-color:PaleVioletRed; text-align: center;\"><h1 style = \"font-size:100px;\" >No Waste Recipe Generator</h1>");

	}

	// isolates ingredients, removes measurements/sizes
	/*
	 * private static String isolateIngredients(String ingredient) { int pos = 24;
	 * for (int i = 0; i < ingredient.length(); i++) { if (i == pos) { String ans =
	 * ingredient.substring(i, ingredient.length()); return ans; }
	 * 
	 * } return "";
	 * 
	 * }
	 */

	private static HashSet<String> filterIngredients(HashMap<String, ArrayList<String>> bigMap, HashSet<String> titles,
			Queue<String> titlesQ, List<String> getAll, Queue<String> titlesAgain) {

		HashSet<String> noRepeats = new HashSet<String>();
		// cut them out, send up into a queue
		int counter = 0;
		while (counter < bigMap.size()) {
			System.out.println("BigMap size " + bigMap.size());
			if (!titlesQ.isEmpty()) {
				String key = titlesQ.remove();
				titlesAgain.add(key);
				ArrayList<String> returned = bigMap.get(key);
				getAll.addAll(returned);
				int index = 0;
				while (index < returned.size()) {
					String store = returned.get(index);
					noRepeats.add(store);
					index++;
					counter++;
				}
			}

		}

		return noRepeats;
	}

	private static void createDropDownMenu(BufferedWriter bw, HashSet<String> titles,
			HashMap<String, ArrayList<String>> bigMap, Queue<String> titlesQ, HashSet<String> operate)
			throws IOException {

		bw.write("<h1>Check ingredients that you have!</h1>");

		// http://localhost:8080/examples/servlets/servlet/ColorGetServlet "\
		// method=\"get\">");

		bw.write("<form action=\"register\" method=\"post\">");
		bw.write("<input type=\"submit\" value=\"Submit\">");

		Iterator<String> itr = operate.iterator();

		while (itr.hasNext()) {
			String s = itr.next();
			if (s.length() % 2 == 0) {
				itr.remove();
				bw.write("<input type=\"checkbox\" id=\"ingredient\"" + " name=\"ingredient\" value=\"" + s + "\">"
						+ "<label>" + s + "</label><br>");
			}
		}
		bw.write("</form>");

	}

	private static void writeOutputFile(BufferedWriter pp) throws IOException {
		pp.write("<!DOCTYPE html> \n" + "<html xmlns:th=\"https://www.thymeLeaf.org\"> \n" + "<head>\n"
				+ "<meta charset = \"ISO-8859-1\"> \n" + "<title>Output Page</title>\n" + "</head>\n" + "<body>\n"
				+ "\n" + "<h1>Output Page!</h1>\n" + "<h2>You have picked the following ingredients:</h2>\n"
				+ "<table>\n" + "        <tr th:each=\"ingredient : ${ingredients}\"> \n"
				+ "        <td th:text=\"${ingredient}\"></td>\n" + "        </tr>\n" + "</table>\n" + "\n"
				+ "<h3>Based on the ingredients you have picked, you should make:</h3>\n" + "\n" + "\n");
	}

	private static Integer calculate(HashMap<String, Integer> storeKeyCounter, String removed, Queue<String> titles,
			HashMap<String, ArrayList<String>> bigMap) {

		String w = null;
		int indivCounter = 0;
		while (!titles.isEmpty()) {
			w = titles.remove();
			ArrayList<String> values = bigMap.get(w);

			int index = (values.size() - 1);
			while (index >= 0) {
				String individual = values.remove(index);
				index--;
				if (individual.contains(removed)) {
					indivCounter++;
				}
			}

			System.out.println("Ind counter b4 map " + indivCounter);
			storeKeyCounter.put(w, indivCounter);
		}

		return indivCounter;
	}

	private static String fixIngredients(List<String> copy, HashMap<String, ArrayList<String>> bigMap,
			Queue<String> titlesAgain) throws Exception {

		Integer max = Integer.MIN_VALUE;
		String answer = "";
		ObjectCloner cloneClass = new ObjectCloner();
		HashMap<String, Integer> storeKeyCounter = new HashMap<String, Integer>();

		int counter = copy.size() - 1;
		while (counter >= 0) {

			String response = copy.remove(counter);

			Queue<String> titlesCopy = new LinkedList<String>(titlesAgain);
			titlesCopy = (Queue<String>) cloneClass.deepCopy(titlesAgain);
			HashMap<String, ArrayList<String>> bigMapCopy = new HashMap<String, ArrayList<String>>(bigMap);
			bigMapCopy = (HashMap<String, ArrayList<String>>) cloneClass.deepCopy(bigMap);

			int indCounter = calculate(storeKeyCounter, response, titlesCopy, bigMapCopy);
			System.out.println("ind counter " + indCounter);

			if (indCounter > max) {
				max = indCounter;

			}
			counter = counter - 1;

			if (storeKeyCounter.containsValue(max)) {
				for (Entry<String, Integer> entry : storeKeyCounter.entrySet()) {
					if (entry.getValue() == max) {
						answer = entry.getKey();
					}
				}
			}

		}

		if (max == 0) {
			answer = "None of the options match :<";
		}
		return answer;

	}

	private static void generateResponse(BufferedWriter hh, String answer) throws IOException {

		hh.write("<h4>" + answer + "</h4");
		hh.write("</body></html>");

	}

	public static void main(String[] args) throws Exception {

		User object1 = new User();
		SpringApplication.run(CookingApplication.class, args);

		File dataFile = new File(
				"/Users/pranathimadala/Downloads/workspace/cooking/src/main/java/com/okayjava/html/recipiess.txt");
		Scanner readingFile = new Scanner(dataFile);
		HashSet<String> titles = new HashSet<String>();
		HashMap<String, ArrayList<String>> bigMap = new HashMap<String, ArrayList<String>>();
		Queue<String> titlesQ = new LinkedList<String>();
		Queue<String> titlesAgain = new LinkedList<String>();
		extractEverything(readingFile, titles, bigMap, titlesQ);
		readingFile.close();

		File htmlFile = new File("src/main/resources/templates/index.html");
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(htmlFile));

		HashMap<String, ArrayList<String>> bigMapCopy = new HashMap<String, ArrayList<String>>(bigMap);
		createHTMLPage(bw);

		// transfer titles into a queue

		HashSet<String> forMenu = new HashSet<String>();
		List<String> getAll = new ArrayList<String>();
		forMenu = filterIngredients(bigMap, titles, titlesQ, getAll, titlesAgain);
		createDropDownMenu(bw, titles, bigMap, titlesQ, forMenu);

		bw.write("</body></html>");
		bw.close();

		TimeUnit.SECONDS.sleep(20);

		System.out.println("object values " + object1.getCopyValues());
		// fixIngredients(object1.getCopyValues());

		String ans = "";
		ans = fixIngredients(object1.getCopyValues(), bigMapCopy, titlesAgain);
		System.out.println("answer " + ans);

		File outputFile = new File("src/main/resources/templates/output.html");
		BufferedWriter pp;
		pp = new BufferedWriter(new FileWriter(outputFile));
		writeOutputFile(pp);
		generateResponse(pp, ans);
		pp.close();

	}

}
