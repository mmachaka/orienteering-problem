package de.hagen.fernuni.gui;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

public class FileIO {

	/**
	 * Öffnet Dialog zur Auswahl von Ordnern und gibt den Pfad zum ausgewählten
	 * Verzeichnis zurück.
	 * 
	 * @param c Aufrufende Komponente
	 * @return Ausgabepfad (Verzeichnis) als String
	 */
	public static String chooseFolder(Component c) {
		String path = "";
		LookAndFeel oldLookAndFeel = UIManager.getLookAndFeel();

		// Natives Design für das Fenster wählen
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fileChooser.setDialogTitle("Ausgabeverzeichnis wählen");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		int result = fileChooser.showOpenDialog(c);
		if (result == JFileChooser.APPROVE_OPTION) {
			if (fileChooser.getSelectedFile().isDirectory())
				path = fileChooser.getSelectedFile().getAbsolutePath();
			else
				path = null;
		} else
			path = null;

		// Fenster-Design wieder zurücksetzen
		try {
			UIManager.setLookAndFeel(oldLookAndFeel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * Gibt im angegebenen Verzeichnis einen einzigartigen Dateinamen zurück.
	 * <p>
	 * Diese Methode liefert einen einzigartigen Dateinamen innerhalb eines Ordners
	 * zurück, um zu vermeiden, dass existierende Dateien überschrieben werden. Wird
	 * als Parameter beispielsweise C:\Users\Mahmoud\result.txt angegeben, würde als
	 * Dateiname result.txt zurückgegeben werden. Falls diese Datei jedoch bereits
	 * existiert, würde als Dateiname result_1.txt zurückgegeben werden. Falls auch
	 * diese Datei existieren sollte, würde die Zahl im Namen solange inkrementiert
	 * werden, bis ein unbenutzer Dateiname gefunden wird.
	 * 
	 * @param filename Pfad samt Dateiname, der abgewandelt wird
	 * @return Gibt im angegebenen Verzeichnis einen einzigartigen Dateinamen
	 *         zurück.
	 */
	public static String generateFileName(String filename) {
		Pattern regexPattern = Pattern.compile("(.*?)(?:\\((\\d+)\\))?(\\.[^.]*)?");
		if (new File(filename).isFile()) {
			Matcher m = regexPattern.matcher(filename);
			if (m.matches()) {
				String prefix = m.group(1);
				String last = m.group(2);
				String suffix = m.group(3);
				if (suffix == null)
					suffix = "";

				int count = last != null ? Integer.parseInt(last) : 0;

				do {
					count++;
					filename = prefix + "_" + count + suffix;
				} while (new File(filename).isFile());
			}
		}
		return filename;
	}

	/**
	 * Erzeugt einen einzigartigen Ordner-Namen im angegebenen Verzeichnis
	 * 
	 * @param path       Verzeichnispfad
	 * @param foldername Ordner-Name der abgewandelt werden soll
	 * @return Einigartiger Ordner-Name
	 */
	public static String generateFolderName(String path, String foldername) {
		String newFoldername = path + foldername;
		int counter = 1;
		while (new File(newFoldername).exists()) {
			newFoldername = path + foldername + "_" + counter;
			counter++;
		}
		return newFoldername;
	}

	/**
	 * Speichert die Lösung und eine Zusammenfassung der Ergebnisse in einer Datei
	 * im angegebenen Speicherort.
	 * 
	 * @param path        Absoluter Pfad zum Speicherort
	 * @param filename    Name der Ergebnisdatei
	 * @param solvedGraph Heuristische Lösung des Orientierungsproblems
	 * @param params      Beinhaltet folgende Informationen für die Zusammenfassung:
	 *                    Verwendeter Algorithmus, gewählte Kostenobergrenze,
	 *                    Parametereinstellungen
	 * @param runtime     Laufzeit der durchgeführten Berechnung
	 * 
	 * @return Gibt den einzigartigen Namen der Ergebnisdatei im Verzeichnis zurück.
	 */
	public static String saveResultsToFile(String path, String filename, Graph solvedGraph, String[] params,
			long runtime) {

		File folder = new File(path);
		folder.mkdir();

		String uniqueFilename = generateFileName(path + File.separator + filename);
		File file = new File(uniqueFilename);

		FileWriter fw;
		try {
			fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("### Zusammenfassung der Ergebnisse ###\n");
			bw.write("Verwendeter Algorithmus: " + params[0] + "\n");
			bw.write("Kostenobergrenze (Tmax) = " + params[1] + "\n");
			bw.write("Parametereinstellungen: ");
			for (int i = 2; i < params.length - 1; i++) {
				bw.write(params[i] + ", ");
			}
			bw.write(params[params.length - 1] + "\n");

			bw.write("Laufzeit: " + runtime + " ms\n");
			bw.write("Erzielter Profit: " + solvedGraph.getProfitOfTour() + "\n");
			bw.write("Kosten der Tour: " + solvedGraph.getCostOfTour() + "\n");
			bw.write("Größe des Graphen: " + solvedGraph.getSize() + " Knoten\n");

			bw.write("\n### Lösung des Orientierungsproblems ###\n");
			bw.write("--- Knoten (x, y, Profit) ---\n");
			for (Node v : solvedGraph.getV()) {
				bw.write(v.toString());
				bw.newLine();
			}

			bw.write("\n--- Kanten der Tour ---\n");
			for (Edge e : solvedGraph.getE()) {
				bw.write(e.toString());
				bw.newLine();
			}

			bw.close();
			fw.close();
		} catch (IOException e1) {
			// Keine Aktion notwendig
		}
		return uniqueFilename;
	}

	/**
	 * Speichert Daten in eine Datei im angegebenen Speicherort. Falls die Datei
	 * bereits existiert, werden die Daten am Ende der Datei angehangen.
	 * 
	 * @param path     Absoluter Pfad zum Speicherort
	 * @param filename Name der Ergebnisdatei
	 * @param data     Zu speichernde Daten
	 * 
	 * @return Gibt den einzigartigen Namen der Ergebnisdatei im Verzeichnis zurück.
	 */
	public static String saveDataToFile(String path, String filename, String data) {
		File folder = new File(path);
		folder.mkdir();

		String absolutePath = path + File.separator + filename;
		File file = new File(absolutePath);

		FileWriter fw;
		try {
			fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.append(data);

			bw.close();
			fw.close();
		} catch (IOException e1) {
			// Keine Aktion notwendig
		}
		return absolutePath;
	}

	/**
	 * Liefert einen HTML-String, der die Ergebnisse der Berechnung zusammenfasst.
	 * 
	 * @param totalTime       Laufzeit der durchgeführten Berechnung
	 * @param solution        Heuristisch berechnete Lösung des
	 *                        Orientierungsproblems
	 * @param params          Zur Berechnung verwendete Parameter
	 * @param pathAndFilename Absoluter Pfad samt Name der Ergebnisdatei
	 * @return Ein String, der die Ergebnisse der Berechnung im HTML-Format
	 *         zusammenfasst.
	 */
	public static String summarizeResults(double totalTime, Graph solution, String[] params, String pathAndFilename) {
		String results = "<html><h1 style=\"text-align: center;\"><strong>Zusammenfassung der Ergebnisse</strong></h1><ul>";
		results += "<li>Verwendeter Algorithmus: " + params[0] + "</li>";
		results += "<li>Kostenobergrenze (Tmax) =  " + params[1] + "</li>";
		results += "<li>Parametereinstellungen:<ul>";
		for (int i = 2; i < params.length - 1; i++) {
			results += "<li>" + params[i] + "</li>";
		}
		results += "<li>" + params[params.length - 1] + "</li></ul></il>";

		results += "<li>Laufzeit: " + totalTime + " ms</li>";
		results += "<li>Erzielter Profit: " + solution.getProfitOfTour() + "</li>";
		results += "<li>Kosten der Tour: " + solution.getCostOfTour() + "</li>";
		results += "<li>Gr&ouml;&szlig;e des Graphen: " + solution.getSize() + " Knoten</li></ul>";
		results += "<p>Die Ergebnisse wurden in folgender Datei gespeichert:</p>";
		results += "<p>" + pathAndFilename + "</p></html>";
		return results;
	}

	/**
	 * Öffnet Dialog zur Auswahl von Textdateien.
	 * 
	 * @param c Aufrufende Komponente
	 * 
	 * @return Ausgewählte Datei
	 */
	public static File selectAndLoadFile(Component c) {
		File selectedFile = null;
		LookAndFeel oldLookAndFeel = UIManager.getLookAndFeel();

		// Natives Design für das Fenster wählen
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFileChooser fileChooser = new JFileChooser();

		// Erlaube nur das Laden von Text-Dateien
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Textdateien (.txt)", "txt", "text");
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setDialogTitle("Graph-Datei wählen");
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(c);
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
		}

		// Fenster-Design wieder zurücksetzen
		try {
			UIManager.setLookAndFeel(oldLookAndFeel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return selectedFile;
	}

	/**
	 * Erzeugt aus einer übergebenen Datei einen Graphen.
	 * <p>
	 * Erzeugt aus einer übergebenen Datei einen Graphen. Sollte die Datei nicht
	 * korrekt formatiert sein, wird eine NumberFormatException geworfen. Die
	 * Prüfung der korrekten Formatierung wird durch die Methode
	 * isCorrectlyFormatted(File f) realisiert.
	 * 
	 * @param f Datei die eingelesen
	 * @return Graph
	 * @throws NumberFormatException Wird geworfen, wenn Datei nicht korrekt
	 *                               formatiert ist.
	 * @throws IOException           Wird geworfen, wenn Datei nicht lesbar ist.
	 */
	public static Graph parseAndLoadGraph(File f) throws NumberFormatException, IOException {
		// Abbruch, falls Datei inkorrekt formatiert ist
		if (!isCorrectlyFormatted(f)) {
			throw new NumberFormatException();
		}

		ArrayList<Node> nodeList = new ArrayList<Node>();
		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		BufferedReader br = new BufferedReader(new FileReader(f));
		String nextLine;

		// Lese Datei zeilenweise aus und erzeuge jeweils einen Knoten mit den
		// entsprechenden Werten
		while ((nextLine = br.readLine()) != null) {
			String[] element = nextLine.split("\t");

			double x = Double.parseDouble(element[0]);
			double y = Double.parseDouble(element[1]);
			double profit = Double.parseDouble(element[2]);

			nodeList.add(new Node(x, y, profit));
		}
		br.close();

		return new Graph(nodeList, edgeList);
	}

	/**
	 * Liest eine Datei als Stream ein und erzeugt daraus einen Graphen.
	 * <p>
	 * Mit dem übergebenen String wird die Datei als Stream eingelesen und aus ihr
	 * ein Graphen erzeugt. Sollte die Datei nicht korrekt formatiert sein, wird
	 * eine NumberFormatException geworfen. Die Prüfung der korrekten Formatierung
	 * wird durch die Methode isCorrectlyFormatted(String filename) realisiert.
	 * 
	 * @param filename Name der zu lesenden Datei
	 * @return Eingelesener Graph
	 * @throws NumberFormatException Wird geworfen, wenn Datei nicht korrekt
	 *                               formatiert ist.
	 * @throws IOException           Wird geworfen, wenn Datei nicht lesbar ist.
	 */
	public static Graph parseAndLoadGraph(String filename) throws NumberFormatException, IOException {
		// Abbruch, falls Datei inkorrekt formatiert ist
		if (!isCorrectlyFormatted(filename)) {
			throw new NumberFormatException();
		}
		InputStream in = FileIO.class.getResourceAsStream(filename);
		ArrayList<Node> nodeList = new ArrayList<Node>();
		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		String nextLine;

		InputStreamReader streamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(streamReader);
		// Lese Datei zeilenweise aus und erzeuge jeweils einen Knoten mit den
		// entsprechenden Werten
		while ((nextLine = br.readLine()) != null) {
			String[] element = nextLine.split("\t");

			double x = Double.parseDouble(element[0]);
			double y = Double.parseDouble(element[1]);
			double profit = Double.parseDouble(element[2]);

			nodeList.add(new Node(x, y, profit));
		}
		br.close();
		streamReader.close();
		return new Graph(nodeList, edgeList);

	}

	/**
	 * Prüft, ob die übergebene Datei korrekt formatiert ist.
	 * 
	 * @param f Die zu prüfende Datei
	 * @return Gibt true zurück, wenn die Formatierung korrekt ist.
	 * @throws IOException Wird geworfen, wenn Datei nicht lesbar ist.
	 */
	private static boolean isCorrectlyFormatted(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		String nextLine;
		int lineCounter = 0;

		while ((nextLine = br.readLine()) != null) {
			String[] elements = nextLine.split("\\t+");

			// Prüfe, ob jede Zeile drei numerische Einträge (x, y und Profit) besitzt
			if (elements.length != 3) {
				br.close();
				return false;
			}
			if (!isDouble(elements[0]) || !isDouble(elements[1]) || !isDouble(elements[2])) {
				br.close();
				return false;
			}

			// Prüfe, ob die ersten beiden Knoten einen Profit von 0 haben
			if (lineCounter < 2) {
				if (!elements[2].equals("0") && !elements[2].equals("0.0")) {
					br.close();
					return false;
				}
			}
			lineCounter++;
		}
		br.close();

		// Prüfe, ob mindestens Start- und Endknoten vorhanden sind
		if (lineCounter < 2)
			return false;

		return true;
	}

	/**
	 * Liest eine Datei als Stream ein und prüft, ob diese korrekt formatiert ist.
	 * 
	 * @param filename Name der zu lesenden Datei
	 * @return Gibt true zurück, wenn die Formatierung korrekt ist.
	 */
	private static boolean isCorrectlyFormatted(String filename) {
		InputStream in = FileIO.class.getResourceAsStream(filename);

		try (InputStreamReader streamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
				BufferedReader br = new BufferedReader(streamReader)) {
			String nextLine;
			int lineCounter = 0;

			while ((nextLine = br.readLine()) != null) {
				String[] elements = nextLine.split("\\t+");

				// Prüfe, ob jede Zeile drei numerische Einträge (x, y und Profit) besitzt
				if (elements.length != 3) {
					br.close();
					streamReader.close();
					return false;
				}
				if (!isDouble(elements[0]) || !isDouble(elements[1]) || !isDouble(elements[2])) {
					br.close();
					streamReader.close();
					return false;
				}

				// Prüfe, ob die ersten beiden Knoten einen Profit von 0 haben
				if (lineCounter < 2) {
					if (!elements[2].equals("0") && !elements[2].equals("0.0")) {
						br.close();
						streamReader.close();
						return false;
					}
				}
				lineCounter++;
			}

			br.close();
			streamReader.close();
			// Prüfe, ob mindestens Start- und Endknoten vorhanden sind
			if (lineCounter < 2)
				return false;

			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Hilfsmethode mit der geprüft wird, ob ein String als Double geparsed werden
	 * kann.
	 * 
	 * @param s Der zu prüfende String
	 * @return Gibt true zurück, wenn der String als Double geparsed werden kann.
	 */
	private static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Prüft, ob eine Datei im übergebenen Verzeichnis angelegt werden kann.
	 * @param outputPath Pfad zum Verzeichnis
	 * @return Gibt true zurück, wenn eine Datei im angegebenen Verzeichnis erzeugt werden kann.
	 */
	public static boolean fileCanBeCreated(String outputPath) {
		try {
			File testFile = new File(outputPath, "testFileCanBeCreated.txt");
			FileOutputStream testFileOutputStream = new FileOutputStream(testFile);
			testFileOutputStream.close();
			testFile.delete();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
