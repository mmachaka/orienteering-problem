package de.hagen.fernuni.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * DialogLoadGraph stellt ein Dialog mit diversen Möglichkeiten zum Laden eines
 * Graphen bereit.
 * 
 * @author Mahmoud Machaka
 */
public class DialogLoadGraph extends JDialog {

	private static final long serialVersionUID = 6493333861328994600L;

	private final JPanel contentPanel = new JPanel();
	private final ButtonGroup buttonGroupLoadBenchmark = new ButtonGroup();

	/**
	 * Create the dialog.
	 * 
	 * @param mainGUI Main-Frame
	 */
	public DialogLoadGraph(MainGUI mainGUI) {
		DialogLoadGraph dialogLoadGraph = this;
		setResizable(false);
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Fernuni-Logo.png")));
		setTitle("Heuristische Algorithmen zur L\u00F6sung des Orientierungsproblems");
		setBounds(100, 100, 635, 492);
		setLocationRelativeTo(null); // Zentriert das Fenster
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JPanel panelLoadFromFile = new JPanel();
			panelLoadFromFile.setBounds(12, 13, 605, 78);
			contentPanel.add(panelLoadFromFile);
			panelLoadFromFile.setLayout(null);
			{
				JLabel lblOption1 = new JLabel("Option 1: Graph aus Datei laden");
				lblOption1.setBounds(0, 5, 267, 20);
				lblOption1.setFont(new Font("Tahoma", Font.BOLD, 16));
				panelLoadFromFile.add(lblOption1);
			}
			{
				JButton btnOpt1Open = new JButton("\u00D6ffne...");
				btnOpt1Open.setMargin(new Insets(1, 1, 1, 1));
				btnOpt1Open.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Lade Datei und erzeuge daraus einen Graphen. Übergebe Graphen an MainGUI.
						try {
							File file = FileIO.selectAndLoadFile(dialogLoadGraph);
							mainGUI.graph = removeDuplicateNodes(FileIO.parseAndLoadGraph(file));
							mainGUI.mapChoosen = false;
							mainGUI.setTmaxToMinimum();
							dispose();

							mainGUI.lblLoadedGraph.setForeground(new Color(0, 100, 0));
							mainGUI.lblLoadedGraph.setText(
									"Graph mit " + mainGUI.graph.getV().size() + " Knoten erfolgreich geladen.");

						} catch (NumberFormatException e1) {
							String fehlermeldung = "<p style=\"text-align: left;\">Die ausgew&auml;hlte Datei ist nicht korrekt formatiert.</p><p style=\"text-align: left;\">&nbsp;</p><p style=\"text-align: left;\">Jede Zeile muss die x-Koordinate, y-Koordinate und den Profit eines Knotens beinhalten, die jeweils durch einen Tabstopp getrennt sind.</p><p>Bitte stellen Sie au&szlig;erdem sicher, dass</p><ul><li>nur numerische Werte enthalten sind</li><li>Nachkommastellen mit einem Punkt getrennt sind</li><li>die ersten beiden Zeilen einen Profit von 0 haben (Start- und Endknoten)</li><li>keine leeren Zeilen enthalten sind</li></ul>";
							JOptionPane.showMessageDialog(new JFrame(), "<html>" + fehlermeldung + "</html>",
									"Fehler beim Laden des Graphen", JOptionPane.ERROR_MESSAGE);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(new JFrame(),
									"Die ausgewählte Datei konnte nicht geladen werden.",
									"Fehler beim Laden des Graphen", JOptionPane.ERROR_MESSAGE);
						} catch (NullPointerException e1) {
							// Keine Datei wurde geladen. Keine Aktion notwendig.
						}
					}
				});
				btnOpt1Open.setBounds(355, 3, 100, 25);
				btnOpt1Open.setFont(new Font("Tahoma", Font.PLAIN, 14));
				panelLoadFromFile.add(btnOpt1Open);
			}
			{
				JLabel lblOption1Info = new JLabel(
						"<html><p>Hinweis: Ausgenommen vom Start- und Endknoten d\u00FCrfen Knoten nicht mehrfach vorkommen, um Schleifen zu vermeiden. Duplikate werden daher automatisch entfernt.</p></html>");
				lblOption1Info.setBounds(0, 27, 581, 40);
				panelLoadFromFile.add(lblOption1Info);
			}
		}
		{
			JPanel panelGenerateFromMap = new JPanel();
			panelGenerateFromMap.setBounds(12, 104, 605, 39);
			contentPanel.add(panelGenerateFromMap);
			panelGenerateFromMap.setLayout(null);
			{
				JLabel lblOption2 = new JLabel("Option 2: Neuen Graph auf Karte zeichnen");
				lblOption2.setBounds(0, 2, 354, 20);
				lblOption2.setFont(new Font("Tahoma", Font.BOLD, 16));
				panelGenerateFromMap.add(lblOption2);
			}
			{
				JButton btnOpt2Open = new JButton("\u00D6ffne Karte");
				btnOpt2Open.setMargin(new Insets(1, 1, 1, 1));
				btnOpt2Open.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						DialogChooseGraphFromMap dialog = new DialogChooseGraphFromMap(mainGUI, dialogLoadGraph);
						dialog.setModalityType(ModalityType.APPLICATION_MODAL);
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);
					}
				});
				btnOpt2Open.setBounds(355, 0, 130, 25);
				btnOpt2Open.setFont(new Font("Tahoma", Font.PLAIN, 14));
				panelGenerateFromMap.add(btnOpt2Open);
			}
		}
		{
			JPanel panelBenchmarkGraph = new JPanel();
			panelBenchmarkGraph.setBounds(12, 156, 605, 107);
			contentPanel.add(panelBenchmarkGraph);
			panelBenchmarkGraph.setLayout(null);
			{
				JLabel lblOption3 = new JLabel("Option 3: Benchmark-Graphen laden");
				lblOption3.setBounds(0, 1, 307, 20);
				lblOption3.setFont(new Font("Tahoma", Font.BOLD, 16));
				panelBenchmarkGraph.add(lblOption3);
			}
			{
				JRadioButton rdbtnTsiligirides1 = new JRadioButton("Tsiligirides [32 Knoten]");
				rdbtnTsiligirides1.setFont(new Font("Tahoma", Font.PLAIN, 15));
				buttonGroupLoadBenchmark.add(rdbtnTsiligirides1);
				rdbtnTsiligirides1.setSelected(true);
				rdbtnTsiligirides1.setActionCommand("Tsiligirides1");
				rdbtnTsiligirides1.setBounds(10, 34, 180, 25);
				panelBenchmarkGraph.add(rdbtnTsiligirides1);
			}
			{
				JRadioButton rdbtnTsiligirides2 = new JRadioButton("Tsiligirides [21 Knoten]");
				rdbtnTsiligirides2.setFont(new Font("Tahoma", Font.PLAIN, 15));
				buttonGroupLoadBenchmark.add(rdbtnTsiligirides2);
				rdbtnTsiligirides2.setActionCommand("Tsiligirides2");
				rdbtnTsiligirides2.setBounds(194, 34, 180, 25);
				panelBenchmarkGraph.add(rdbtnTsiligirides2);
			}
			{
				JRadioButton rdbtnTsiligirides3 = new JRadioButton("Tsiligirides [33 Knoten]");
				rdbtnTsiligirides3.setFont(new Font("Tahoma", Font.PLAIN, 15));
				buttonGroupLoadBenchmark.add(rdbtnTsiligirides3);
				rdbtnTsiligirides3.setActionCommand("Tsiligirides3");
				rdbtnTsiligirides3.setBounds(378, 34, 180, 25);
				panelBenchmarkGraph.add(rdbtnTsiligirides3);
			}

			{
				JRadioButton rdbtnChao1 = new JRadioButton("Chao [64 Knoten]");
				rdbtnChao1.setFont(new Font("Tahoma", Font.PLAIN, 15));
				buttonGroupLoadBenchmark.add(rdbtnChao1);
				rdbtnChao1.setActionCommand("Chao1");
				rdbtnChao1.setBounds(194, 68, 180, 25);
				panelBenchmarkGraph.add(rdbtnChao1);
			}
			{
				JRadioButton rdbtnChao2 = new JRadioButton("Chao [66 Knoten]");
				rdbtnChao2.setFont(new Font("Tahoma", Font.PLAIN, 15));
				buttonGroupLoadBenchmark.add(rdbtnChao2);
				rdbtnChao2.setActionCommand("Chao2");
				rdbtnChao2.setBounds(10, 68, 180, 25);
				panelBenchmarkGraph.add(rdbtnChao2);
			}
			{
				JButton btnOpt3Load = new JButton("Lade");
				btnOpt3Load.setMargin(new Insets(1, 1, 1, 1));
				btnOpt3Load.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String selectedRadioButton = buttonGroupLoadBenchmark.getSelection().getActionCommand();
						String filename = "";

						// Lade Datei und erzeuge daraus einen Graphen. Übergebe Graphen an MainGUI.
						try {
							if (selectedRadioButton == "Tsiligirides1") {
								filename = "/Tsiligirides1.txt";
							}
							if (selectedRadioButton == "Tsiligirides2") {
								filename = "/Tsiligirides2.txt";
							}
							if (selectedRadioButton == "Tsiligirides3") {
								filename = "/Tsiligirides3.txt";
							}
							if (selectedRadioButton == "Chao1") {
								filename = "/Chao1.txt";
							}
							if (selectedRadioButton == "Chao2") {
								filename = "/Chao2.txt";
							}
							mainGUI.graph = removeDuplicateNodes(FileIO.parseAndLoadGraph(filename));
							mainGUI.mapChoosen = false;
							mainGUI.setTmaxToMinimum();
							dispose();

							mainGUI.lblLoadedGraph.setForeground(new Color(0, 100, 0));
							mainGUI.lblLoadedGraph.setText(
									"Graph mit " + mainGUI.graph.getV().size() + " Knoten erfolgreich geladen.");

						} catch (NumberFormatException e1) {
							String fehlermeldung = "<p>Die ausgew&auml;hlte Datei ist nicht korrekt formatiert. Jede Zeile muss wie folgt formatiert sein:</p><p style=\"text-align: center;font-size:13px\"><strong>x-Koordinate </strong><em>[Tabstopp] </em><strong>y-Koordinate </strong><em>[Tabstopp] </em><strong>Profit</strong></p><p>&nbsp;</p><p>Bitte stellen Sie au&szlig;erdem sicher, dass</p><ul><li>nur numerische Werte enthalten sind</li><li>Nachkommastellen mit einem Punkt getrennt sind</li><li>die ersten beiden Zeilen einen Profit von 0 haben (Start- und Endknoten)</li><li>keine leeren Zeilen enthalten sind</li></ul>";
							JOptionPane.showMessageDialog(new JFrame(), "<html>" + fehlermeldung + "</html>",
									"Fehler beim Laden des Graphen", JOptionPane.ERROR_MESSAGE);
						} catch (NullPointerException e1) {
							// Keine Datei wurde geladen. Keine Aktion notwendig.
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(new JFrame(),
									"Die ausgewählte Datei konnte nicht geladen werden.",
									"Fehler beim Laden des Graphen", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				btnOpt3Load.setFont(new Font("Tahoma", Font.PLAIN, 14));
				btnOpt3Load.setBounds(355, 0, 100, 25);
				panelBenchmarkGraph.add(btnOpt3Load);
			}
		}
		{
			JPanel panelRandomGraph = new JPanel();
			panelRandomGraph.setBounds(12, 276, 605, 140);
			contentPanel.add(panelRandomGraph);
			panelRandomGraph.setLayout(null);

			NumberFormat numberFormat = NumberFormat.getNumberInstance();
			numberFormat.setGroupingUsed(false);

			NumberFormatter numberNode = new NumberFormatter(numberFormat);
			numberNode.setValueClass(Integer.class);
			numberNode.setMinimum(2);
			numberNode.setMaximum(Integer.MAX_VALUE);

			JFormattedTextField formattedTextFieldOpt4NodeAmount = new JFormattedTextField(numberNode);
			formattedTextFieldOpt4NodeAmount.setHorizontalAlignment(SwingConstants.RIGHT);
			formattedTextFieldOpt4NodeAmount.setValue(new Integer(20));
			formattedTextFieldOpt4NodeAmount.setColumns(10);

			formattedTextFieldOpt4NodeAmount.setBounds(136, 35, 65, 22);
			panelRandomGraph.add(formattedTextFieldOpt4NodeAmount);

			NumberFormatter numberProfit = new NumberFormatter(numberFormat);
			numberProfit.setValueClass(Double.class);
			numberProfit.setMinimum(0.0);

			JFormattedTextField formattedTextFieldOpt4MinProfit = new JFormattedTextField(numberProfit);
			formattedTextFieldOpt4MinProfit.setValue(new Double(10.0));
			formattedTextFieldOpt4MinProfit.setHorizontalAlignment(SwingConstants.RIGHT);
			formattedTextFieldOpt4MinProfit.setColumns(10);

			formattedTextFieldOpt4MinProfit.setBounds(136, 65, 65, 22);
			panelRandomGraph.add(formattedTextFieldOpt4MinProfit);

			JFormattedTextField formattedTextFieldOpt4MaxProfit = new JFormattedTextField(numberProfit);
			formattedTextFieldOpt4MaxProfit.setValue(new Double(250.0));
			formattedTextFieldOpt4MaxProfit.setHorizontalAlignment(SwingConstants.RIGHT);
			formattedTextFieldOpt4MaxProfit.setColumns(10);
			formattedTextFieldOpt4MaxProfit.setBounds(136, 95, 65, 22);
			panelRandomGraph.add(formattedTextFieldOpt4MaxProfit);

			{
				JLabel lblOption4 = new JLabel("Option 4: Zuf\u00E4lligen Graphen erzeugen");
				lblOption4.setBounds(0, 2, 324, 20);
				lblOption4.setFont(new Font("Tahoma", Font.BOLD, 16));
				panelRandomGraph.add(lblOption4);
			}
			{
				JButton btnOpt4Generate = new JButton("Erzeuge...");
				btnOpt4Generate.setMargin(new Insets(1, 1, 1, 1));
				btnOpt4Generate
						.setToolTipText("Erzeugung eines zuf\u00E4lligen Graphen mit erh\u00F6hter Clusterbildung.");
				btnOpt4Generate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int anzahl = (int) formattedTextFieldOpt4NodeAmount.getValue();
						double minProfit = (double) formattedTextFieldOpt4MinProfit.getValue();
						double maxProfit = (double) formattedTextFieldOpt4MaxProfit.getValue();

						if (maxProfit < minProfit) {
							JOptionPane.showMessageDialog(new JFrame(),
									"'Min. Profit' darf nicht größer als 'Max. Profit' sein. Bitte überprüfen Sie Ihre Eingaben.",
									"Graph konnte nicht erzeugt werden", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if (anzahl < 2) {
							JOptionPane.showMessageDialog(new JFrame(),
									"Die Knotenanzahl muss mindestens 2 betragen. Bitte überprüfen Sie Ihre Eingaben.",
									"Graph konnte nicht erzeugt werden", JOptionPane.ERROR_MESSAGE);
							return;
						}

						double x, y, profit;
						ArrayList<Node> nodeList = new ArrayList<Node>();
						LinkedHashSet<Node> nodeHashMap = new LinkedHashSet<Node>();
						Random rng = new Random();

						// Füge Start- und Endknoten ein
						int i = 0;
						while (i < 2) {
							// Um Knoten mit gleichen Koordinaten zu vermeiden, wird der Zufallszahlenraum
							// entsprechend der Knotenanzahl vergrößert
							x = ((double) ((int) ((anzahl * rng.nextDouble()) * 10))) / 10;
							y = ((double) ((int) ((anzahl * rng.nextDouble()) * 10))) / 10;
							nodeList.add(new Node(x, y, 0));
							i++;
						}

						// Füge übrige Knoten gruppenweise hinzu
						while (i < anzahl) {
							int clusterSize;
							if (anzahl >= 25) {
								clusterSize = (int) (10 + rng.nextInt((anzahl / 25)));
							} else {
								clusterSize = 1;
							}

							int addedNodes = 0;
							double clusterCenterX = ((double) ((int) ((anzahl * rng.nextDouble()) * 20))) / 10;
							double clusterCenterY = ((double) ((int) ((anzahl * rng.nextDouble()) * 20))) / 10;

							while (i < anzahl && addedNodes < clusterSize) {
								// Verteilung um Cluster-Zentrum: min + (max - min) * rng.nextDouble()
								double minX = 0.9 * clusterCenterX;
								double maxX = 1.1 * clusterCenterX;
								double minY = 0.9 * clusterCenterY;
								double maxY = 1.1 * clusterCenterY;
								x = ((double) ((int) ((minX + (maxX - minX) * rng.nextDouble()) * 10))) / 10;
								y = ((double) ((int) ((minY + (maxY - minY) * rng.nextDouble()) * 10))) / 10;
								profit = ((double) ((int) ((minProfit + (maxProfit - minProfit) * rng.nextDouble())
										* 10))) / 10;

								if (nodeHashMap.add(new Node(x, y, profit))) {
									nodeList.add(new Node(x, y, profit));
									i++;
									addedNodes++;
								}
							}

						}
						mainGUI.graph = new Graph(nodeList, new ArrayList<Edge>());
						mainGUI.mapChoosen = false;
						mainGUI.setTmaxToMinimum();
						dispose();

						mainGUI.lblLoadedGraph.setForeground(new Color(0, 100, 0));
						mainGUI.lblLoadedGraph
								.setText("Graph mit " + mainGUI.graph.getV().size() + " Knoten erfolgreich geladen.");
					}
				});
				btnOpt4Generate.setFont(new Font("Tahoma", Font.PLAIN, 14));
				btnOpt4Generate.setBounds(355, 0, 100, 25);
				panelRandomGraph.add(btnOpt4Generate);
			}
			{
				JLabel lblOpt4NodeAmount = new JLabel("Knotenanzahl");
				lblOpt4NodeAmount.setHorizontalAlignment(SwingConstants.RIGHT);
				lblOpt4NodeAmount.setFont(new Font("Tahoma", Font.PLAIN, 14));
				lblOpt4NodeAmount.setBounds(10, 34, 114, 20);
				panelRandomGraph.add(lblOpt4NodeAmount);
			}
			{
				JLabel lblOpt4MinProfit = new JLabel("Min. Profit");
				lblOpt4MinProfit.setHorizontalAlignment(SwingConstants.RIGHT);
				lblOpt4MinProfit.setFont(new Font("Tahoma", Font.PLAIN, 14));
				lblOpt4MinProfit.setBounds(10, 65, 114, 20);
				panelRandomGraph.add(lblOpt4MinProfit);
			}
			{
				JLabel lblOpt4MaxProfit = new JLabel("Max. Profit");
				lblOpt4MaxProfit.setHorizontalAlignment(SwingConstants.RIGHT);
				lblOpt4MaxProfit.setFont(new Font("Tahoma", Font.PLAIN, 14));
				lblOpt4MaxProfit.setBounds(10, 95, 114, 20);
				panelRandomGraph.add(lblOpt4MaxProfit);
			}

		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Abbrechen");
				cancelButton.setMargin(new Insets(1, 15, 1, 15));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * Entfernt Knoten mit gleichen Koordinaten aus einem Graphen.
	 * <p>
	 * Mit dieser Methoden werden Knoten-Duplikate aus dem übergebenen Graphen
	 * entfernt, sodass nur Knoten mit einzigartigen Koordniaten enthalten bleiben.
	 * Ausgenommen von dieser Maßnahme ist der Start- und Endknoten des Graphen.
	 * 
	 * @param g Graph, aus welchem Knoten-Duplikate entfernt werden sollen.
	 * @return Gibt einen Graphen ohne Knoten-Duplikate zurück.
	 */
	private Graph removeDuplicateNodes(Graph g) {
		ArrayList<Node> nodeListNoDuplicates = new ArrayList<Node>();
		LinkedHashSet<Node> nodeHashMap = new LinkedHashSet<Node>();

		// Füge Start- und Endknoten hinzu
		nodeHashMap.add(g.getV().get(0));
		nodeListNoDuplicates.add(g.getV().get(0));
		nodeHashMap.add(g.getV().get(1));
		nodeListNoDuplicates.add(g.getV().get(1));

		if (g.getSize() > 2) {
			for (int i = 2; i < g.getSize(); i++) {
				if (nodeHashMap.add(g.getV().get(i)))
					nodeListNoDuplicates.add(g.getV().get(i));
			}
		}

		return new Graph(nodeListNoDuplicates, new ArrayList<Edge>());
	}
}
