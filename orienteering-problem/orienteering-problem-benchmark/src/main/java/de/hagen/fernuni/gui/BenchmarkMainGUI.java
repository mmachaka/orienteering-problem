package de.hagen.fernuni.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.NumberFormatter;

import de.hagen.fernuni.analysis.Analysis;

public class BenchmarkMainGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5773348660434959812L;
	private JPanel contentPane, panelConfiguration, panelBenchmarkSets, panelProgress;
	private JFormattedTextField fTxtField_Repetitions;
	private JCheckBox chckbxALNS, chckbxEA, chckbxSet100, chckbxSet500, chckbxSet1000, chckbxSet200, chckbxSet300,
			chckbxSet400, chckbxSet600, chckbxSet700, chckbxSet800, chckbxSet900;
	private DefaultListModel<String> listModel;
	private JButton btnStart, btnAbort;
	private Analysis benchmarkInstance;
	private JProgressBar progressBar;
	private String outputPath;
	private JLabel lblOutputPathTitle;
	private JLabel lblMaxTimeTitle;
	private JFormattedTextField fTxtField_MaxTime;
	private JLabel lblMaxTimeUnit;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BenchmarkMainGUI frame = new BenchmarkMainGUI();
					frame.setVisible(true);
					frame.setLocationRelativeTo(null); // Zentriert das Fenster

					// Falls, nicht-unterstützte Java-Version erkannt wird
					if (!System.getProperty("java.version").substring(0, 3).equals("1.8")) {
						final JEditorPane editorPane = new JEditorPane();
						editorPane.setEditable(false);
						editorPane.setVisible(true);
						editorPane.setOpaque(false);
						editorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
						editorPane.setText(
								"<html><p style=\"text-align: justify; font-size: 11px; font-family: Arial;\">Die von Ihnen verwendete Java-Version ("
										+ System.getProperty("java.version")
										+ ") wird nicht unterst&uuml;tzt und kann zu Fehlern in der Anwendung f&uuml;hren.</p>"
										+ "<p style=\"text-align: justify; font-size: 11px; font-family: Arial;\">Bitte installieren und verwenden Sie Java SE 8 (1.8):<br /></p>"
										+ "<p style=\"text-align: justify; font-size: 11px; font-family: Arial;\"><a href=\"https://www.oracle.com/java/technologies/downloads/archive/\">Java Archive - Downloads</a></p></html>");
						editorPane.addHyperlinkListener(new HyperlinkListener() {
							public void hyperlinkUpdate(HyperlinkEvent e) {
								if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
									if (Desktop.isDesktopSupported()) {
										try {
											Desktop.getDesktop().browse(e.getURL().toURI());
										} catch (IOException | URISyntaxException e1) {
											e1.printStackTrace();
										}
									}
								}
							}
						});
						JOptionPane.showMessageDialog(null, editorPane, "Nicht unterstüzte Java-Version",
								JOptionPane.INFORMATION_MESSAGE);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public BenchmarkMainGUI() {
		setResizable(false);
		BenchmarkMainGUI benchmarkMainGUI = this;
		setTitle("Benchmarking heuristischer Algorithmen");
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Fernuni-Logo.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 588, 693);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panelProgress = new JPanel();
		panelProgress.setBounds(12, 322, 558, 324);
		contentPane.add(panelProgress);
		panelProgress.setLayout(null);

		btnStart = new JButton("Starte Benchmarking");
		btnStart.setMargin(new Insets(1, 1, 1, 1));
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Integer.parseInt(fTxtField_Repetitions.getText());

					if (outputPath != null) {
						// GUI für Berechnung anpassen
						lockGUIForCalculation(true);

						// Berechnung im Swingworker starten
						benchmarkInstance = new Analysis(getParams(), benchmarkMainGUI);
						benchmarkInstance.execute();
					} else {
						JOptionPane.showMessageDialog(new JFrame(),
								"<html>Bitte geben Sie einen g&uuml;ltigen Ausgabepfad an.</html>",
								"Benchmarking konnte nicht gestartet werden!", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(new JFrame(),
							"<html>Bitte geben Sie f&uuml;r die Anzahl an Wiederholungen eine nat&uuml;rliche Zahl (gr&ouml;&szlig;er als 0) ein.</html>",
							"Benchmarking konnte nicht gestartet werden!", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnStart.setBounds(237, 283, 207, 27);
		panelProgress.add(btnStart);
		btnStart.setFont(new Font("Tahoma", Font.BOLD, 15));

		btnAbort = new JButton("Abbrechen");
		btnAbort.setMargin(new Insets(1, 1, 1, 1));
		btnAbort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (benchmarkInstance != null)
					benchmarkInstance.cancel(true);
			}
		});
		btnAbort.setBounds(114, 283, 111, 27);
		panelProgress.add(btnAbort);
		btnAbort.setEnabled(false);
		btnAbort.setFont(new Font("Tahoma", Font.BOLD, 15));

		listModel = new DefaultListModel<String>();
		JList<String> listProgress = new JList<String>(listModel);

		listProgress.setBounds(12, 0, 578, 200);
		panelProgress.add(listProgress);

		JScrollPane scrollPane = new JScrollPane(listProgress);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 0, 536, 234);
		panelProgress.add(scrollPane);

		progressBar = new JProgressBar();
		progressBar.setBounds(114, 245, 330, 25);
		progressBar.setStringPainted(true);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		panelProgress.add(progressBar);

		panelConfiguration = new JPanel();
		panelConfiguration.setBounds(12, 13, 558, 145);
		contentPane.add(panelConfiguration);
		panelConfiguration.setLayout(null);

		JLabel lblAlgorithmusTitle = new JLabel("Algorithmus w\u00E4hlen:");
		lblAlgorithmusTitle.setHorizontalAlignment(SwingConstants.LEFT);
		lblAlgorithmusTitle.setBounds(0, 0, 158, 25);
		lblAlgorithmusTitle.setFont(new Font("Tahoma", Font.BOLD, 15));
		panelConfiguration.add(lblAlgorithmusTitle);

		chckbxALNS = new JCheckBox("ALNS Algorithmus");
		chckbxALNS.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxALNS.setBounds(170, 0, 151, 25);
		chckbxALNS.setSelected(true);
		panelConfiguration.add(chckbxALNS);

		chckbxEA = new JCheckBox("Evolution\u00E4rer Algorithmus");
		chckbxEA.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxEA.setBounds(342, 0, 197, 25);
		chckbxEA.setSelected(true);
		panelConfiguration.add(chckbxEA);

		UIManager.put("ToolTip.background", new Color(255, 255, 153));
		UIManager.put("ToolTip.font", new Font("Arial", Font.BOLD, 14));
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

		JLabel lblRepetitionsTitle = new JLabel("Wiederholungen:");
		lblRepetitionsTitle.setHorizontalAlignment(SwingConstants.LEFT);
		lblRepetitionsTitle.setBounds(0, 38, 158, 25);
		lblRepetitionsTitle.setToolTipText(
				"Je \u00F6fter die Berechnungen wiederholt werden, desto genauer werden die Ergebnisse. Entsprechend erh\u00F6ht sich jedoch auch die Rechendauer.");
		lblRepetitionsTitle.setFont(new Font("Tahoma", Font.BOLD, 15));
		panelConfiguration.add(lblRepetitionsTitle);

		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setGroupingUsed(false);

		NumberFormatter integerFormatter = new NumberFormatter(numberFormat);
		integerFormatter.setValueClass(Integer.class);
		integerFormatter.setMinimum(1);
		integerFormatter.setMaximum(Integer.MAX_VALUE);

		NumberFormatter integerFormatterWithZero = new NumberFormatter(numberFormat);
		integerFormatterWithZero.setValueClass(Integer.class);
		integerFormatterWithZero.setMinimum(0);
		integerFormatterWithZero.setMaximum(Integer.MAX_VALUE);

		fTxtField_Repetitions = new JFormattedTextField(integerFormatter);
		fTxtField_Repetitions.setBounds(170, 40, 55, 22);
		fTxtField_Repetitions.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_Repetitions.setText("5");
		fTxtField_Repetitions.setToolTipText(
				"Je \u00F6fter die Berechnungen wiederholt werden, desto genauer werden die Ergebnisse. Entsprechend erh\u00F6ht sich jedoch auch die Rechendauer.");
		panelConfiguration.add(fTxtField_Repetitions);
		fTxtField_Repetitions.setColumns(10);

		JLabel lblOutputPath = new JLabel("<html>Kein Ausgabepfad ausgew\u00E4hlt.</html>");
		lblOutputPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblOutputPath.setForeground(Color.RED);
		lblOutputPath.setBounds(170, 112, 369, 20);
		panelConfiguration.add(lblOutputPath);

		JButton btnChooseOutputPath = new JButton("Speichern unter...");
		btnChooseOutputPath.setMargin(new Insets(1, 1, 1, 1));
		btnChooseOutputPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputPath = FileIO.chooseFolder(benchmarkMainGUI);
				boolean fileCanBeCreated = FileIO.fileCanBeCreated(outputPath);
				if (!fileCanBeCreated)
					outputPath = null;

				if (outputPath == null) {
					lblOutputPath.setText("<html>Kein Verzeichnis ausgewählt.</html>");
					lblOutputPath.setForeground(Color.RED);
					JOptionPane.showMessageDialog(new JFrame(),
							"Es wurde kein gültiges oder ein schreibgeschütztes Verzeichnis ausgewählt.",
							"Ungültiges Verzeichnis", JOptionPane.ERROR_MESSAGE);
				} else {
					lblOutputPath.setText(outputPath);
					lblOutputPath.setForeground(new Color(0, 100, 0));
				}
			}
		});
		btnChooseOutputPath.setBounds(170, 75, 151, 25);
		panelConfiguration.add(btnChooseOutputPath);

		lblOutputPathTitle = new JLabel("Ausgabepfad:");
		lblOutputPathTitle.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblOutputPathTitle.setBounds(0, 76, 158, 25);
		panelConfiguration.add(lblOutputPathTitle);

		lblMaxTimeTitle = new JLabel("Max. Laufzeit:");
		lblMaxTimeTitle.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMaxTimeTitle.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblMaxTimeTitle.setToolTipText(
				"<html>Beschr\u00E4nkt die maximale Laufzeit einer Wiederholung. Um die Algorithmen ohne zeitliche Beschr\u00E4nkung ausf\u00FChren zu k\u00F6nnen, geben Sie hier bitte den Wert '0' an.<br />{z \u2208 \u2115 | z \u2265 0}</html>");
		lblMaxTimeTitle.setBounds(263, 38, 124, 25);
		panelConfiguration.add(lblMaxTimeTitle);

		fTxtField_MaxTime = new JFormattedTextField(integerFormatterWithZero);
		fTxtField_MaxTime.setText("10");
		fTxtField_MaxTime.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_MaxTime.setToolTipText(
				"<html>Beschr\u00E4nkt die maximale Laufzeit einer Wiederholung. Um die Algorithmen ohne zeitliche Beschr\u00E4nkung ausf\u00FChren zu k\u00F6nnen, geben Sie hier bitte den Wert '0' an.<br />{z \u2208 \u2115 | z \u2265 0}</html>");
		fTxtField_MaxTime.setBounds(399, 40, 55, 22);
		panelConfiguration.add(fTxtField_MaxTime);

		lblMaxTimeUnit = new JLabel("Minute(n)");
		lblMaxTimeUnit.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblMaxTimeUnit.setBounds(460, 38, 73, 25);
		panelConfiguration.add(lblMaxTimeUnit);

		panelBenchmarkSets = new JPanel();
		panelBenchmarkSets.setBounds(12, 171, 558, 138);
		contentPane.add(panelBenchmarkSets);
		panelBenchmarkSets.setLayout(null);

		JLabel lblBenchmarkSetsTitle = new JLabel("Benchmark-Graphen w\u00E4hlen");
		lblBenchmarkSetsTitle.setBounds(0, 3, 210, 19);
		lblBenchmarkSetsTitle.setFont(new Font("Tahoma", Font.BOLD, 15));
		panelBenchmarkSets.add(lblBenchmarkSetsTitle);

		chckbxSet100 = new JCheckBox("100 Knoten");
		chckbxSet100.setBounds(10, 31, 93, 25);
		panelBenchmarkSets.add(chckbxSet100);
		chckbxSet100.setSelected(true);

		chckbxSet200 = new JCheckBox("200 Knoten");
		chckbxSet200.setBounds(107, 31, 93, 25);
		panelBenchmarkSets.add(chckbxSet200);
		chckbxSet200.setSelected(true);

		chckbxSet300 = new JCheckBox("300 Knoten");
		chckbxSet300.setBounds(204, 31, 93, 25);
		panelBenchmarkSets.add(chckbxSet300);
		chckbxSet300.setSelected(true);

		chckbxSet400 = new JCheckBox("400 Knoten");
		chckbxSet400.setBounds(301, 31, 93, 25);
		panelBenchmarkSets.add(chckbxSet400);
		chckbxSet400.setSelected(true);

		chckbxSet500 = new JCheckBox("500 Knoten");
		chckbxSet500.setBounds(398, 31, 93, 25);
		panelBenchmarkSets.add(chckbxSet500);
		chckbxSet500.setSelected(true);

		chckbxSet600 = new JCheckBox("600 Knoten");
		chckbxSet600.setBounds(10, 61, 93, 25);
		panelBenchmarkSets.add(chckbxSet600);

		chckbxSet700 = new JCheckBox("700 Knoten");
		chckbxSet700.setBounds(107, 61, 93, 25);
		panelBenchmarkSets.add(chckbxSet700);

		chckbxSet800 = new JCheckBox("800 Knoten");
		chckbxSet800.setBounds(204, 61, 93, 25);
		panelBenchmarkSets.add(chckbxSet800);

		chckbxSet900 = new JCheckBox("900 Knoten");
		chckbxSet900.setBounds(301, 61, 93, 25);
		panelBenchmarkSets.add(chckbxSet900);

		chckbxSet1000 = new JCheckBox("1000 Knoten");
		chckbxSet1000.setBounds(398, 61, 99, 25);
		panelBenchmarkSets.add(chckbxSet1000);

		JButton btnSelectAll = new JButton("Alle ausw\u00E4hlen");
		btnSelectAll.setBounds(301, 95, 93, 23);
		btnSelectAll.setMargin(new Insets(1, 1, 1, 1));
		panelBenchmarkSets.add(btnSelectAll);
		btnSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (Component cb : panelBenchmarkSets.getComponents()) {
					if (cb instanceof JCheckBox) {
						((JCheckBox) cb).setSelected(true);
					}
				}
			}
		});

		JButton btnDeselectAll = new JButton("Alle abw\u00E4hlen");
		btnDeselectAll.setBounds(404, 95, 87, 23);
		btnDeselectAll.setMargin(new Insets(1, 1, 1, 1));
		panelBenchmarkSets.add(btnDeselectAll);
		btnDeselectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (Component cb : panelBenchmarkSets.getComponents()) {
					if (cb instanceof JCheckBox) {
						((JCheckBox) cb).setSelected(false);
					}
				}
			}
		});

	}

	private HashMap<String, Object> getParams() {
		HashMap<String, Object> params = new HashMap<String, Object>();

		ArrayList<String> algorithms = new ArrayList<String>();
		if (chckbxALNS.isSelected()) {
			algorithms.add("ALNS");
		}
		if (chckbxEA.isSelected()) {
			algorithms.add("EA");
		}

		ArrayList<String> benchmarkSets = new ArrayList<String>();
		if (chckbxSet100.isSelected()) {
			benchmarkSets.add("/set_100.txt");
		}
		if (chckbxSet200.isSelected()) {
			benchmarkSets.add("/set_200.txt");
		}
		if (chckbxSet300.isSelected()) {
			benchmarkSets.add("/set_300.txt");
		}
		if (chckbxSet400.isSelected()) {
			benchmarkSets.add("/set_400.txt");
		}
		if (chckbxSet500.isSelected()) {
			benchmarkSets.add("/set_500.txt");
		}
		if (chckbxSet600.isSelected()) {
			benchmarkSets.add("/set_600.txt");
		}
		if (chckbxSet700.isSelected()) {
			benchmarkSets.add("/set_700.txt");
		}
		if (chckbxSet800.isSelected()) {
			benchmarkSets.add("/set_800.txt");
		}
		if (chckbxSet900.isSelected()) {
			benchmarkSets.add("/set_900.txt");
		}
		if (chckbxSet1000.isSelected()) {
			benchmarkSets.add("/set_1000.txt");
		}

		params.put("algorithms", algorithms);
		params.put("repetitions", Integer.parseInt(fTxtField_Repetitions.getText()));
		params.put("maxTime", Integer.parseInt(fTxtField_MaxTime.getText()));
		params.put("benchmarkSets", benchmarkSets);

		return params;
	}

	public void setListProgess(int position, List<String> chunks) {
		String s = chunks.remove(0);
		try {
			listModel.set(position, s);
		} catch (Exception e) {
			listModel.add(listModel.getSize(), s);
		}
	}

	public void setProgressbar(String s, int value) {
		progressBar.setString(s);
		progressBar.setValue(value);
	}

	public void lockGUIForCalculation(boolean lock) {
		if (lock) {
			for (Component c : panelConfiguration.getComponents()) {
				c.setEnabled(false);
			}
			btnStart.setEnabled(false);
			btnAbort.setEnabled(true);

			HashMap<String, Object> params = getParams();
			@SuppressWarnings("unchecked")
			ArrayList<String> algorithms = (ArrayList<String>) params.get("algorithms");
			@SuppressWarnings("unchecked")
			ArrayList<String> benchmarkSets = (ArrayList<String>) params.get("benchmarkSets");
			listModel.clear();
			for (String benchmarkSet : benchmarkSets) {
				String benchmarkSetName = benchmarkSet.substring(5, benchmarkSet.length() - 4);
				if (algorithms.contains("ALNS")) {
					listModel.addElement("<html><span style=\"color: #808080;\">Benchmark Graph " + benchmarkSetName
							+ " (ALNS): Warte...</span></html>");
				}
				if (algorithms.contains("EA")) {
					listModel.addElement("<html><span style=\"color: #808080;\">Benchmark Graph " + benchmarkSetName
							+ " (EA): Warte...</span></html>");
				}
			}
		} else {
			for (Component c : panelConfiguration.getComponents()) {
				c.setEnabled(true);
			}
			btnStart.setEnabled(true);
			btnAbort.setEnabled(false);
		}
	}

	public String getOutputPath() {
		return outputPath;
	}
}
