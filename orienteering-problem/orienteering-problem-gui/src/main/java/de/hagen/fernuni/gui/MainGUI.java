package de.hagen.fernuni.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.text.NumberFormat;

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
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import de.hagen.fernuni.factory.HeuristicAlgorithmFactory;
import de.hagen.fernuni.logic.IHeuristicAlgorithm;
import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.strategy.Context;

/**
 * MainGUI stellt das Hauptfenster der Anwendung dar und ermöglicht die
 * Konfiguration der auszuführenden Algorithmen.
 * 
 * @author Mahmoud Machaka
 *
 */
public class MainGUI extends JFrame {

	private static final long serialVersionUID = -2265543291662223869L;

	private JPanel contentPane, panelLoadGraph, panelOutputPath, panelAlgorithm, panelCompute, panelStatus;
	private final ButtonGroup buttonGroupSelectAlgorithm = new ButtonGroup();

	private DialogLoadGraph dialogLoadGraph;

	protected JLabel lblLoadedGraph;
	protected JLabel lblOutputPath;
	private JLabel lblStatus;

	private JButton btnAbortButton;

	private JFormattedTextField fTxtField_ALNS_Param_Tmax;
	private JFormattedTextField fTxtField_ALNS_Param_iterations;
	private JFormattedTextField fTxtField_ALNS_Param_alpha;
	private JFormattedTextField fTxtField_ALNS_Param_decay;
	private JFormattedTextField fTxtField_ALNS_Param_maxTime;

	private JFormattedTextField fTxtField_EA_Param_Tmax;
	private JFormattedTextField fTxtField_EA_Param_d2d;
	private JFormattedTextField fTxtField_EA_Param_npop;
	private JFormattedTextField fTxtField_EA_Param_ncand;
	private JFormattedTextField fTxtField_EA_Param_p;
	private JFormattedTextField fTxtField_EA_Param_maxTime;

	private IHeuristicAlgorithm alns;
	private IHeuristicAlgorithm ea;

	protected Graph graph, solvedGraph;
	protected boolean mapChoosen;
	private String outputPath, filename;
	private long totalTime;

	private SwingWorker<Graph, Graph> worker;

	/**
	 * Launch the application.
	 */
	public void displayMainGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI frame = new MainGUI();
					frame.setVisible(true);
					frame.setLocationRelativeTo(null); // Zentriert das Fenster
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainGUI() {
		MainGUI mainGUI = this;
		setResizable(false);
		setTitle("Heuristische Algorithmen zur L\u00F6sung des Orientierungsproblems");
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Fernuni-Logo.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 625, 567);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panelLoadGraph = new JPanel();
		panelLoadGraph.setBounds(32, 27, 548, 78);
		contentPane.add(panelLoadGraph);

		JButton btnLoadGraph = new JButton("Lade Graphen");
		btnLoadGraph.setMargin(new Insets(1, 1, 1, 1));
		btnLoadGraph.setBounds(345, 1, 175, 30);
		btnLoadGraph.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnLoadGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Singleton: Erzeuge dialogLoadGraph nur, wenn es noch nicht existiert
				if (dialogLoadGraph == null) {
					dialogLoadGraph = new DialogLoadGraph(mainGUI);
				}
				dialogLoadGraph.setModalityType(ModalityType.APPLICATION_MODAL);
				dialogLoadGraph.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialogLoadGraph.setVisible(true);
			}
		});
		panelLoadGraph.setLayout(null);

		JLabel lblStep_1 = new JLabel("Schritt 1: W\u00E4hle einen Graphen");
		lblStep_1.setBounds(0, 2, 330, 25);
		lblStep_1.setFont(new Font("Tahoma", Font.BOLD, 20));
		panelLoadGraph.add(lblStep_1);
		panelLoadGraph.add(btnLoadGraph);

		lblLoadedGraph = new JLabel("Kein Graph ausgew\u00E4hlt.");
		lblLoadedGraph.setBounds(0, 40, 536, 17);
		lblLoadedGraph.setForeground(Color.RED);
		lblLoadedGraph.setFont(new Font("Tahoma", Font.ITALIC, 14));
		panelLoadGraph.add(lblLoadedGraph);

		panelOutputPath = new JPanel();
		panelOutputPath.setBounds(32, 118, 548, 78);
		contentPane.add(panelOutputPath);
		panelOutputPath.setLayout(null);

		JLabel lblStep_2 = new JLabel("Schritt 2: W\u00E4hle Speicherort");
		lblStep_2.setBounds(0, 2, 314, 25);
		lblStep_2.setFont(new Font("Tahoma", Font.BOLD, 20));
		panelOutputPath.add(lblStep_2);

		JButton btnChooseOutputPath = new JButton("Speichern unter...");
		btnChooseOutputPath.setMargin(new Insets(1, 1, 1, 1));
		btnChooseOutputPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputPath = FileIO.chooseFolder(mainGUI);

				boolean fileCanBeCreated = FileIO.fileCanBeCreated(outputPath);

				if (!fileCanBeCreated)
					outputPath = null;

				if (outputPath == null) {
					lblOutputPath.setText("Es wurde kein gültiges oder ein schreibgeschütztes Verzeichnis ausgewählt.");
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
		btnChooseOutputPath.setBounds(345, 1, 175, 37);
		btnChooseOutputPath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelOutputPath.add(btnChooseOutputPath);

		lblOutputPath = new JLabel("Kein Ausgabepfad ausgew\u00E4hlt.");
		lblOutputPath.setBounds(0, 40, 536, 17);
		lblOutputPath.setFont(new Font("Tahoma", Font.ITALIC, 14));
		lblOutputPath.setForeground(Color.RED);
		panelOutputPath.add(lblOutputPath);

		panelAlgorithm = new JPanel();
		panelAlgorithm.setBounds(32, 209, 548, 207);
		contentPane.add(panelAlgorithm);
		panelAlgorithm.setLayout(null);

		panelCompute = new JPanel();
		panelCompute.setBounds(32, 429, 548, 39);
		contentPane.add(panelCompute);
		panelCompute.setLayout(null);

		panelStatus = new JPanel();
		panelStatus.setBounds(32, 481, 548, 39);
		contentPane.add(panelStatus);
		panelStatus.setLayout(null);

		JLabel lblStep_4 = new JLabel("Schritt 4: Berechne L\u00F6sung");
		lblStep_4.setBounds(0, 2, 291, 25);
		lblStep_4.setFont(new Font("Tahoma", Font.BOLD, 20));
		panelCompute.add(lblStep_4);

		JButton btnCompute = new JButton("Berechne");
		btnCompute.setMargin(new Insets(1, 1, 1, 1));
		btnCompute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (inputIsValid()) {
					worker = getNewSwingWorker(mainGUI);
					worker.execute();
				} else {
					String fehlermeldung = "<html><p>Die Berechnung konnte nicht durchgef&uuml;hrt werden, da</p><ul>";
					if (graph == null) {
						fehlermeldung += "<li>kein Graph ausgew&auml;hlt wurde</li>";
					}
					if (outputPath == null) {
						fehlermeldung += "<li>kein Ausgabepfad ausgew&auml;hlt wurde</li>";
					}
					if (!paramsAreValid()) {
						fehlermeldung += "<li>ung&uuml;ltige Parameterwerte eingegeben wurden. Bitte &uuml;berpr&uuml;fen Sie die Wertebereiche der Parameter, indem Sie mit der Maus &uuml;ber die entsprechenden Eingabefelder fahren.</li>";
					}

					fehlermeldung += "</ul></html>";
					JOptionPane.showMessageDialog(new JFrame(), fehlermeldung,
							"Berechnung konnte nicht durchgeführt werden!", JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		btnCompute.setBounds(345, 1, 135, 30);
		btnCompute.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelCompute.add(btnCompute);

		JPanel panelParams = new JPanel();
		panelParams.setBounds(0, 85, 548, 122);
		panelAlgorithm.add(panelParams);
		panelParams.setLayout(new CardLayout(0, 0));

		JPanel panelALNSParams = new JPanel();
		panelParams.add(panelALNSParams, "CardALNS");
		panelALNSParams.setLayout(null);

		UIManager.put("ToolTip.background", new Color(255, 255, 153));
		UIManager.put("ToolTip.font", new Font("Arial", Font.BOLD, 14));
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

		JLabel lblALNSParam1 = new JLabel("Tmax");
		lblALNSParam1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblALNSParam1.setToolTipText(
				"<html>Kostenobergrenze, die von einer L&ouml;sung nicht &uuml;berschritten werden darf.<br />{t &isin; \u211D | t &ge; 0}</html>");
		lblALNSParam1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblALNSParam1.setBounds(0, 15, 80, 20);
		panelALNSParams.add(lblALNSParam1);

		JLabel lblALNSParam2 = new JLabel("Iterationen");
		lblALNSParam2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblALNSParam2.setToolTipText(
				"<html>Maximale Anzahl durchzuf&uuml;hrender Iterationen. In jeder Iteration werden aus der aktuellen L&ouml;sung Knoten entfernt und neue Knoten hinzugef&uuml;gt, um neue und bessere L&ouml;sungen zu finden.<br />{i &isin; \u2115 | i &ge; 0}</html>");
		lblALNSParam2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblALNSParam2.setBounds(0, 54, 80, 20);
		panelALNSParams.add(lblALNSParam2);

		JLabel lblALNSParam3 = new JLabel("<html>&alpha;</html>");
		lblALNSParam3.setToolTipText(
				"<html>Aggressivit&auml;tsfaktor, der die Anzahl zu entfernender Knoten durch eine Zerst&ouml;rmethode beeinflusst.<br />Je h&ouml;her der Wert, desto mehr Knoten werden durch eine Zerst&ouml;rmethode entfernt.<br />{&alpha; &isin; \u211D | 0 &lt; &alpha; &lt; 1}</html>");
		lblALNSParam3.setHorizontalAlignment(SwingConstants.RIGHT);
		lblALNSParam3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblALNSParam3.setBounds(200, 15, 95, 20);
		panelALNSParams.add(lblALNSParam3);

		JLabel lblALNSParam4 = new JLabel("h");
		lblALNSParam4.setToolTipText(
				"<html>Decay-Faktor, der die Bewertung eingesetzter Reparatur- und Zerst&ouml;rmethoden beeeinflusst.<br />Je h\u00F6her der Wert, desto h\u00F6her der Einfluss einer Bewertung auf die Gesamtbewertung einer Methode.<br />{h \u2208 \u211D | 0 \u2264 h \u2264 1}</html>");
		lblALNSParam4.setHorizontalAlignment(SwingConstants.RIGHT);
		lblALNSParam4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblALNSParam4.setBounds(200, 54, 95, 20);
		panelALNSParams.add(lblALNSParam4);

		JButton btnALNSDefaultParams = new JButton(
				"<html><p style=\"text-align: center;\">Parameter<br />zur&uuml;cksetzen</p></html>");
		btnALNSDefaultParams.setMargin(new Insets(1, 1, 1, 1));
		btnALNSDefaultParams.setToolTipText(
				"<html>Setzt alle Parameter auf ihre Default-Werte zur&uuml;ck.<br /><em>Tmax wird auf den Wert gesetzt, der wenigstens die Verbindung von Start- und Endknoten einer Tour erlaubt.</em></html>");
		btnALNSDefaultParams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetParamsALNS();
			}
		});
		btnALNSDefaultParams.setBounds(429, 12, 107, 41);
		panelALNSParams.add(btnALNSDefaultParams);

		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setGroupingUsed(false);

		NumberFormatter doubleFormatter = new NumberFormatter(numberFormat);
		doubleFormatter.setValueClass(Double.class);
		doubleFormatter.setMinimum(0.0);

		NumberFormatter integerFormatter = new NumberFormatter(numberFormat);
		integerFormatter.setValueClass(Integer.class);
		integerFormatter.setMinimum(1);
		integerFormatter.setMaximum(Integer.MAX_VALUE);

		NumberFormatter integerFormatterWithZero = new NumberFormatter(numberFormat);
		integerFormatterWithZero.setValueClass(Integer.class);
		integerFormatterWithZero.setMinimum(0);
		integerFormatterWithZero.setMaximum(Integer.MAX_VALUE);

		NumberFormat numberFormatZeroToOne = NumberFormat.getNumberInstance();
		numberFormatZeroToOne.setGroupingUsed(false);
		numberFormatZeroToOne.setRoundingMode(RoundingMode.UP);
		numberFormatZeroToOne.setMaximumFractionDigits(12);

		NumberFormatter zeroToOneInclusiveFormatter = new NumberFormatter(numberFormatZeroToOne);
		zeroToOneInclusiveFormatter.setValueClass(Double.class);
		zeroToOneInclusiveFormatter.setMinimum(0.0);
		zeroToOneInclusiveFormatter.setCommitsOnValidEdit(true); // Prüft Korrektheit der Eingabe erst nach Fokuswechsel
		zeroToOneInclusiveFormatter.setMaximum(1.0);

		NumberFormatter zeroToOneExclusiveFormatter = new NumberFormatter(numberFormatZeroToOne);
		zeroToOneExclusiveFormatter.setValueClass(Double.class);
		zeroToOneExclusiveFormatter.setMinimum(0.000000000001);
		zeroToOneExclusiveFormatter.setMaximum(0.999999999999);

		fTxtField_ALNS_Param_Tmax = new JFormattedTextField(doubleFormatter);
		fTxtField_ALNS_Param_Tmax.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_ALNS_Param_Tmax.setToolTipText(
				"<html>Kostenobergrenze, die von einer L&ouml;sung nicht &uuml;berschritten werden darf.<br />{t &isin; \u211D | t &ge; 0}</html>");
		fTxtField_ALNS_Param_Tmax.setBounds(85, 15, 75, 22);
		panelALNSParams.add(fTxtField_ALNS_Param_Tmax);

		fTxtField_ALNS_Param_iterations = new JFormattedTextField(integerFormatter);
		fTxtField_ALNS_Param_iterations.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_ALNS_Param_iterations.setToolTipText(
				"<html>Maximale Anzahl durchzuf&uuml;hrender Iterationen. In jeder Iteration werden aus der aktuellen L&ouml;sung Knoten entfernt und neue Knoten hinzugef&uuml;gt, um neue und bessere L&ouml;sungen zu finden.<br />{i &isin; \u2115 | i &ge; 0}</html>");
		fTxtField_ALNS_Param_iterations.setBounds(85, 54, 75, 22);
		panelALNSParams.add(fTxtField_ALNS_Param_iterations);

		fTxtField_ALNS_Param_alpha = new JFormattedTextField(zeroToOneExclusiveFormatter);
		fTxtField_ALNS_Param_alpha.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_ALNS_Param_alpha.setToolTipText(
				"<html>Aggressivit&auml;tsfaktor, der die Anzahl zu entfernender Knoten durch eine Zerst&ouml;rmethode beeinflusst.<br />Je h&ouml;her der Wert, desto mehr Knoten werden durch eine Zerst&ouml;rmethode entfernt.<br />{&alpha; &isin; \u211D | 0 &lt; &alpha; &lt; 1}</html>");
		fTxtField_ALNS_Param_alpha.setBounds(300, 15, 75, 22);
		panelALNSParams.add(fTxtField_ALNS_Param_alpha);

		fTxtField_ALNS_Param_decay = new JFormattedTextField(zeroToOneInclusiveFormatter);
		fTxtField_ALNS_Param_decay.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_ALNS_Param_decay.setToolTipText(
				"<html>Decay-Faktor, der die Bewertung eingesetzter Reparatur- und Zerst&ouml;rmethoden beeeinflusst.<br />Je h\u00F6her der Wert, desto h\u00F6her der Einfluss einer Bewertung auf die Gesamtbewertung einer Methode.<br />{h \u2208 \u211D | 0 \u2264 h \u2264 1}</html>");
		fTxtField_ALNS_Param_decay.setBounds(300, 54, 75, 22);
		panelALNSParams.add(fTxtField_ALNS_Param_decay);

		fTxtField_ALNS_Param_maxTime = new JFormattedTextField(integerFormatterWithZero);
		fTxtField_ALNS_Param_maxTime.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_ALNS_Param_maxTime.setToolTipText(
				"<html>Beschr\u00E4nkt die maximale Laufzeit des Algorithmus. Um den Algorithmus ohne zeitliche Beschr\u00E4nkung ausf\u00FChren zu k\u00F6nnen, geben Sie hier bitte den Wert '0' an.<br />{z \u2208 \u2115 | z \u2265 0}</html>");
		fTxtField_ALNS_Param_maxTime.setBounds(300, 93, 75, 22);
		panelALNSParams.add(fTxtField_ALNS_Param_maxTime);

		JLabel lblALNSParam5 = new JLabel("Max. Laufzeit");
		lblALNSParam5.setHorizontalAlignment(SwingConstants.RIGHT);
		lblALNSParam5.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblALNSParam5.setToolTipText(
				"<html>Beschr\u00E4nkt die maximale Laufzeit des Algorithmus. Um den Algorithmus ohne zeitliche Beschr\u00E4nkung ausf\u00FChren zu k\u00F6nnen, geben Sie hier bitte den Wert '0' an.<br />{z \u2208 \u2115 | z \u2265 0}</html>");
		lblALNSParam5.setBounds(188, 93, 107, 20);
		panelALNSParams.add(lblALNSParam5);

		JLabel lblALNSMaxTime = new JLabel("Minute(n)");
		lblALNSMaxTime.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblALNSMaxTime.setBounds(382, 93, 86, 20);
		panelALNSParams.add(lblALNSMaxTime);

		JPanel panelEAParams = new JPanel();
		panelParams.add(panelEAParams, "CardEA");
		panelEAParams.setLayout(null);

		JLabel lblEAParam1 = new JLabel("Tmax");
		lblEAParam1.setToolTipText(
				"<html>Kostenobergrenze, die von einer L&ouml;sung nicht &uuml;berschritten werden darf.<br />{t &isin; \u211D | t &ge; 0}</html>");
		lblEAParam1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEAParam1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblEAParam1.setBounds(0, 15, 80, 20);
		panelEAParams.add(lblEAParam1);

		JLabel lblEAParam3 = new JLabel("d2d");
		lblEAParam3.setToolTipText(
				"<html>Bestimmt die H&auml;ufigkeit, mit welcher die Optimierungsoperatoren auf die Population angewendet werden.<br />Je kleiner d2d, desto h&auml;ufiger werden die Optimierungsoperatoren ausgef&uuml;hrt.<br />{d2d &isin; \u2115 | d2d &ge; 2 }</html>");
		lblEAParam3.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEAParam3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblEAParam3.setBounds(215, 15, 80, 20);
		panelEAParams.add(lblEAParam3);

		JLabel lblEAParam4 = new JLabel("npop");
		lblEAParam4.setToolTipText(
				"<html>Anzahl der L&ouml;sungen in der Population.<br />{npop &isin; \u2115 | npop &ge; 1 }</html>");
		lblEAParam4.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEAParam4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblEAParam4.setBounds(0, 54, 80, 20);
		panelEAParams.add(lblEAParam4);

		JLabel lblEAParam5 = new JLabel("ncand");
		lblEAParam5.setToolTipText(
				"<html>Anzahl der Kandidaten aus denen regelm&auml;&szlig;ig zwei L&ouml;sungen (Eltern) ausgew&auml;hlt werden.<br />{ncand &isin; \u2115 | ncand &ge; 1, ncand &le; npop}</html>");
		lblEAParam5.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEAParam5.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblEAParam5.setBounds(215, 54, 80, 20);
		panelEAParams.add(lblEAParam5);

		JButton btnEADefaultParams = new JButton(
				"<html><p style=\"text-align: center;\">Parameter<br />zur&uuml;cksetzen</p></html>");
		btnEADefaultParams.setMargin(new Insets(1, 1, 1, 1));
		btnEADefaultParams.setToolTipText(
				"<html>Setzt alle Parameter auf ihre Default-Werte zur&uuml;ck.<br /><em>Tmax wird auf den Wert gesetzt, der wenigstens die Verbindung von Start- und Endknoten einer Tour erlaubt.</em></html>");
		btnEADefaultParams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetParamsEA();
			}
		});
		btnEADefaultParams.setBounds(429, 12, 107, 41);
		panelEAParams.add(btnEADefaultParams);

		JLabel lblEAParam6 = new JLabel("p");
		lblEAParam6.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblEAParam6.setToolTipText(
				"<html>Wahrscheinlichkeit, mit der ein Knoten in eine initiale L&ouml;sung aufgenommen wird.<br />{p &isin; \u211D | 0 \u2264 p \u2264 1}</html>");
		lblEAParam6.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEAParam6.setBounds(0, 93, 80, 20);
		panelEAParams.add(lblEAParam6);

		fTxtField_EA_Param_Tmax = new JFormattedTextField(doubleFormatter);
		fTxtField_EA_Param_Tmax.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_EA_Param_Tmax.setToolTipText(
				"<html>Kostenobergrenze, die von einer L&ouml;sung nicht &uuml;berschritten werden darf.<br />{t &isin; \u211D | t &ge; 0}</html>");
		fTxtField_EA_Param_Tmax.setBounds(85, 15, 75, 22);
		panelEAParams.add(fTxtField_EA_Param_Tmax);

		fTxtField_EA_Param_d2d = new JFormattedTextField(integerFormatter);
		fTxtField_EA_Param_d2d.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_EA_Param_d2d.setToolTipText(
				"<html>Bestimmt die H&auml;ufigkeit, mit welcher die Optimierungsoperatoren auf die Population angewendet werden.<br />Je kleiner d2d, desto h&auml;ufiger werden die Optimierungsoperatoren ausgef&uuml;hrt.<br />{d2d &isin; \u2115 | d2d &ge; 2 }</html>");
		fTxtField_EA_Param_d2d.setBounds(300, 15, 75, 22);
		panelEAParams.add(fTxtField_EA_Param_d2d);

		fTxtField_EA_Param_npop = new JFormattedTextField(integerFormatter);
		fTxtField_EA_Param_npop.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_EA_Param_npop.setToolTipText(
				"<html>Anzahl der L&ouml;sungen in der Population.<br />{npop &isin; \u2115 | npop &ge; 1 }</html>");
		fTxtField_EA_Param_npop.setBounds(85, 54, 75, 22);
		panelEAParams.add(fTxtField_EA_Param_npop);

		fTxtField_EA_Param_ncand = new JFormattedTextField(integerFormatter);
		fTxtField_EA_Param_ncand.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_EA_Param_ncand.setToolTipText(
				"<html>Anzahl der Kandidaten aus denen regelm&auml;&szlig;ig zwei L&ouml;sungen (Eltern) ausgew&auml;hlt werden.<br />{ncand &isin; \u2115 | ncand &ge; 1, ncand &le; npop}</html>");
		fTxtField_EA_Param_ncand.setBounds(300, 54, 75, 22);
		panelEAParams.add(fTxtField_EA_Param_ncand);

		fTxtField_EA_Param_p = new JFormattedTextField(zeroToOneInclusiveFormatter);
		fTxtField_EA_Param_p.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_EA_Param_p.setToolTipText(
				"<html>Wahrscheinlichkeit, mit der ein Knoten in eine initiale L&ouml;sung aufgenommen wird.<br />{p &isin; \u211D | 0 \u2264 p \u2264 1}</html>");
		fTxtField_EA_Param_p.setBounds(85, 93, 75, 22);
		panelEAParams.add(fTxtField_EA_Param_p);

		fTxtField_EA_Param_maxTime = new JFormattedTextField(integerFormatterWithZero);
		fTxtField_EA_Param_maxTime.setHorizontalAlignment(SwingConstants.CENTER);
		fTxtField_EA_Param_maxTime.setToolTipText(
				"<html>Beschr\u00E4nkt die maximale Laufzeit des Algorithmus. Um den Algorithmus ohne zeitliche Beschr\u00E4nkung ausf\u00FChren zu k\u00F6nnen, geben Sie hier bitte den Wert '0' an.<br />{z \u2208 \u2115 | z \u2265 0}</html>");
		fTxtField_EA_Param_maxTime.setBounds(300, 93, 75, 22);
		panelEAParams.add(fTxtField_EA_Param_maxTime);

		JLabel lblEAParam7 = new JLabel("Max. Laufzeit");
		lblEAParam7.setToolTipText(
				"<html>Beschr\u00E4nkt die maximale Laufzeit des Algorithmus. Um den Algorithmus ohne zeitliche Beschr\u00E4nkung ausf\u00FChren zu k\u00F6nnen, geben Sie hier bitte den Wert '0' an.<br />{z \u2208 \u2115 | z \u2265 0}</html>");
		lblEAParam7.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEAParam7.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblEAParam7.setBounds(188, 93, 107, 20);
		panelEAParams.add(lblEAParam7);

		JLabel lblEAMaxTime = new JLabel("Minute(n)");
		lblEAMaxTime.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblEAMaxTime.setBounds(382, 93, 86, 20);
		panelEAParams.add(lblEAMaxTime);

		// Setze Default-Werte der formatierten Textfelder
		resetParamsALNS();
		resetParamsEA();

		JLabel lblStep_3 = new JLabel("Schritt 3: W\u00E4hle Algorithmus und Parameter");
		lblStep_3.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblStep_3.setBounds(0, 0, 462, 25);
		panelAlgorithm.add(lblStep_3);

		JRadioButton rdbtnALNS = new JRadioButton("<html>Adaptive Large Neighbourhood Search<br />(ALNS)</html>");
		rdbtnALNS.setMargin(new Insets(1, 1, 1, 1));
		rdbtnALNS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelParams.getLayout();
				cl.show(panelParams, "CardALNS");
			}
		});
		buttonGroupSelectAlgorithm.add(rdbtnALNS);
		rdbtnALNS.setSelected(true);
		rdbtnALNS.setFont(new Font("Tahoma", Font.PLAIN, 14));
		rdbtnALNS.setBounds(0, 35, 311, 40);
		rdbtnALNS.setActionCommand("ALNS");
		panelAlgorithm.add(rdbtnALNS);

		JRadioButton rdbtnEA = new JRadioButton("<html>Evolutionary Algorithm<br />(EA)</html>");
		rdbtnEA.setMargin(new Insets(1, 1, 1, 1));
		rdbtnEA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelParams.getLayout();
				cl.show(panelParams, "CardEA");
			}
		});
		buttonGroupSelectAlgorithm.add(rdbtnEA);
		rdbtnEA.setFont(new Font("Tahoma", Font.PLAIN, 14));
		rdbtnEA.setActionCommand("EA");
		rdbtnEA.setBounds(332, 34, 197, 40);
		panelAlgorithm.add(rdbtnEA);

		lblStatus = new JLabel("Aktuell wird keine Berechnung durchgeführt.");
		lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatus.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblStatus.setForeground(Color.GRAY);
		lblStatus.setBounds(0, 5, 333, 23);
		panelStatus.add(lblStatus);

		btnAbortButton = new JButton("Abbrechen");
		btnAbortButton.setMargin(new Insets(1, 1, 1, 1));
		btnAbortButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnAbortButton.setEnabled(false);
		btnAbortButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (worker != null)
					worker.cancel(true);
				if (alns != null)
					alns.cancel();
				if (ea != null) {
					ea.cancel();
				}
			}
		});
		btnAbortButton.setBounds(345, 1, 135, 30);
		panelStatus.add(btnAbortButton);
	}

	/**
	 * Prüft, ob alle zur Berechnung erforderlichen Benutzereingaben getätigt
	 * wurden.
	 * 
	 * @return Gibt true zurück, wenn ein Graph, Ausgabepfad und valide
	 *         Parametereinstellungen gewählt wurden
	 */
	private boolean inputIsValid() {
		return (graph != null) && (outputPath != null) && paramsAreValid();
	}

	/**
	 * Setzt die Parametereinstellungen für den ALNS-Algorithmus in der GUI zurück.
	 */
	private void resetParamsALNS() {
		setTmaxToMinimum();
		fTxtField_ALNS_Param_iterations.setValue(100);
		fTxtField_ALNS_Param_alpha.setValue(0.5);
		fTxtField_ALNS_Param_decay.setValue(0.5);
		fTxtField_ALNS_Param_maxTime.setValue(0);
	}

	/**
	 * Setzt die Parametereinstellungen für den Evolutionären Algorithmus (EA) in
	 * der GUI zurück.
	 */
	private void resetParamsEA() {
		setTmaxToMinimum();
		fTxtField_EA_Param_d2d.setValue(50);
		fTxtField_EA_Param_npop.setValue(100);
		fTxtField_EA_Param_ncand.setValue(10);
		fTxtField_EA_Param_p.setValue(0.01);
		fTxtField_EA_Param_maxTime.setValue(0);
	}

	/**
	 * Gibt für den ausgewählten Algorithmus die eingegebenen Parameterwerte als
	 * String-Liste zurück.
	 * 
	 * @return Eine String-Liste der Parameterwerte für den ausgewählten Algorithmus
	 */
	private String[] getParams() {
		String[] params = null;
		String choosenAlgorithm;
		if (buttonGroupSelectAlgorithm.getSelection().getActionCommand().equals("ALNS")) {
			choosenAlgorithm = "Adaptive Large Neighbourhood Search Algorithm";
			params = new String[6];
			params[0] = choosenAlgorithm;
			params[1] = fTxtField_ALNS_Param_Tmax.getValue().toString();
			params[2] = fTxtField_ALNS_Param_iterations.getValue().toString();
			params[3] = fTxtField_ALNS_Param_alpha.getValue().toString();
			params[4] = fTxtField_ALNS_Param_decay.getValue().toString();
			params[5] = fTxtField_ALNS_Param_maxTime.getValue().toString();
		}
		if (buttonGroupSelectAlgorithm.getSelection().getActionCommand().equals("EA")) {
			choosenAlgorithm = "Evolutionary Algorithm";
			params = new String[7];
			params[0] = choosenAlgorithm;
			params[1] = fTxtField_EA_Param_Tmax.getValue().toString();
			params[2] = fTxtField_EA_Param_d2d.getValue().toString();
			params[3] = fTxtField_EA_Param_npop.getValue().toString();
			params[4] = fTxtField_EA_Param_ncand.getValue().toString();
			params[5] = fTxtField_EA_Param_p.getValue().toString();
			params[6] = fTxtField_EA_Param_maxTime.getValue().toString();
		}
		return params;
	}

	/**
	 * Gibt für den ausgewählten Algorithmus die eingegebenen Parameterwerte und
	 * Parameternamen als String-Liste zurück.
	 * 
	 * @return Eine String-Liste der Parameterwerte und -namen für den ausgewählten
	 *         Algorithmus
	 */
	private String[] getPrintableParams() {
		String[] params = getParams();
		if (buttonGroupSelectAlgorithm.getSelection().getActionCommand().equals("ALNS")) {
			params[2] = "Iterations = " + params[2];
			params[3] = "alpha = " + params[3];
			params[4] = "h = " + params[4];
			params[5] = "maxTime = " + params[5];
		}
		if (buttonGroupSelectAlgorithm.getSelection().getActionCommand().equals("EA")) {
			params[2] = "d2d = " + params[2];
			params[3] = "npop = " + params[3];
			params[4] = "ncand = " + params[4];
			params[5] = "p = " + params[5];
			params[6] = "maxTime = " + params[6];
		}
		return params;
	}

	/**
	 * Prüft, ob die eingegebenen Parameter für den ausgewählten Algorithmus
	 * zulässig sind.
	 * <p>
	 * Prüft, ob die eingegebenen Parameter für den ausgewählten Algorithmus
	 * zulässig sind. Die tatsächliche Prüfung wird entsprechend an
	 * paramsALNSAreValid() bzw. paramsEAAreValid() delegiert.
	 * 
	 * @return Gibt true zurück, wenn die Parameter für den ausgewählten Algorithmus
	 *         gültig sind.
	 */
	private boolean paramsAreValid() {
		boolean isValid = false;
		if (buttonGroupSelectAlgorithm.getSelection().getActionCommand().equals("ALNS"))
			isValid = paramsALNSAreValid();
		if (buttonGroupSelectAlgorithm.getSelection().getActionCommand().equals("EA"))
			isValid = paramsEAAreValid();
		return isValid;
	}

	/**
	 * Prüft, ob die Parameter für den ALNS-Algorithmus zulässig sind.
	 * 
	 * @return Gibt true zurück, wenn die Parameter für den ALNS-Algorithmus gültig
	 *         sind
	 */
	private boolean paramsALNSAreValid() {
		String[] params = getParams();
		boolean isValid = true;
		try {
			double tmax = Double.parseDouble(params[1]);
			int iterations = Integer.parseInt(params[2]);
			double alpha = Double.parseDouble(params[3]);
			double h = Double.parseDouble(params[4]);
			int maxTime = Integer.parseInt(params[5]);

			if (tmax < 0 || iterations < 1)
				return false;
			if (alpha == 0)
				fTxtField_ALNS_Param_alpha.setValue(0.000001);
			if (alpha == 1)
				fTxtField_ALNS_Param_alpha.setValue(0.999999);
			if (alpha < 0 || alpha > 1)
				return false;
			if (h < 0 || h > 1)
				return false;
			if (maxTime < 0)
				return false;

		} catch (NumberFormatException ex) {
			return false;
		}
		return isValid;
	}

	/**
	 * Prüft, ob die Parameter für den Evolutionären Algorithmus (EA) zulässig sind.
	 * 
	 * @return Gibt true zurück, wenn die Parameter für den Evolutionären
	 *         Algorithmus (EA) gültig sind
	 */
	private boolean paramsEAAreValid() {
		String[] params = getParams();
		boolean isValid = true;
		try {
			double tmax = Double.parseDouble(params[1]);
			int d2d = Integer.parseInt(params[2]);
			int npop = Integer.parseInt(params[3]);
			int ncand = Integer.parseInt(params[4]);
			double p = Double.parseDouble(params[5]);
			int maxTime = Integer.parseInt(params[6]);

			if (tmax < 0 || d2d < 1)
				return false;
			if ((p < 0 || p > 1) || (ncand > npop))
				return false;
			if (maxTime < 0)
				return false;

		} catch (NumberFormatException ex) {
			return false;
		}
		return isValid;
	}

	/**
	 * Setzt in der GUI die Kostenobergrenze (Tmax) auf den Wert, sodass mindestens
	 * die Kante vom Startknoten zum Endknoten in die Lösung aufgenommen werden
	 * kann.
	 */
	protected void setTmaxToMinimum() {
		if (graph != null) {
			double tmax = Math.round(10 * Edge.getDistance(graph.getV().get(0), graph.getV().get(1)));
			tmax = tmax / 10;
			fTxtField_ALNS_Param_Tmax.setValue(tmax);
			fTxtField_EA_Param_Tmax.setValue(tmax);
		} else {
			fTxtField_ALNS_Param_Tmax.setValue(100);
			fTxtField_EA_Param_Tmax.setValue(100);
		}
	}

	private SwingWorker<Graph, Graph> getNewSwingWorker(MainGUI mainGUI) {
		SwingWorker<Graph, Graph> worker = new SwingWorker<Graph, Graph>() {
			@Override
			protected Graph doInBackground() throws Exception {
				setPanelEnabled(panelCompute, false);
				setPanelEnabled(panelAlgorithm, false);
				setPanelEnabled(panelOutputPath, false);
				setPanelEnabled(panelLoadGraph, false);
				btnAbortButton.setEnabled(true);
				lblStatus.setText("Berechnung wird durchgeführt...");
				lblStatus.setForeground(new Color(0, 100, 0));

				totalTime = 0;
				String selectedRadioButton = buttonGroupSelectAlgorithm.getSelection().getActionCommand();

				if (selectedRadioButton.equals("ALNS")) {
					long startTime = System.currentTimeMillis();
					String[] params = getParams();
					Graph g = new Graph(graph);
					alns = HeuristicAlgorithmFactory.getInstance().createALNS(g, Double.parseDouble(params[1]),
							Integer.parseInt(params[2]), Double.parseDouble(params[3]), Double.parseDouble(params[4]),
							Integer.parseInt(params[5]));
					Context context = new Context(alns);

					solvedGraph = context.execute();
					long endTime = System.currentTimeMillis();
					totalTime = endTime - startTime;
				}

				if (selectedRadioButton.equals("EA")) {
					long startTime = System.currentTimeMillis();
					String[] params = getParams();
					Graph g = new Graph(graph);
					ea = HeuristicAlgorithmFactory.getInstance().createEvolutionaryAlgorithm(g,
							Double.parseDouble(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]),
							Integer.parseInt(params[4]), Double.parseDouble(params[5]), Integer.parseInt(params[6]));
					Context context = new Context(ea);
					solvedGraph = context.execute();
					long endTime = System.currentTimeMillis();
					totalTime = endTime - startTime;
				}

				setPanelEnabled(panelLoadGraph, true);
				setPanelEnabled(panelOutputPath, true);
				setPanelEnabled(panelAlgorithm, true);
				setPanelEnabled(panelCompute, true);
				btnAbortButton.setEnabled(false);
				lblStatus.setText("Aktuell wird keine Berechnung durchgeführt.");
				lblStatus.setForeground(Color.GRAY);
				return solvedGraph;
			}

			@Override
			protected void done() {
				if (isCancelled())
					return;

				try {
					filename = FileIO.saveResultsToFile(outputPath, "result.txt", solvedGraph, getPrintableParams(),
							totalTime);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(new JFrame(),
							"Die Datei konnte nicht erstellt werden. Womöglich wurde der Ordner während des Schreibvorgangs entfernt, verschoben oder umbenannt oder es fehlen Schreibrechte.",
							"Fehler beim Schreiben der Datai", JOptionPane.ERROR_MESSAGE);
				}

				// Zusammenfassung der Ergebnisse im Popup-Fenster
				String results = FileIO.summarizeResults(totalTime, solvedGraph, getPrintableParams(), filename);
				if (mapChoosen) {
					DialogShowResultsOnMap dialog = new DialogShowResultsOnMap(mainGUI, results);
					dialog.setModalityType(ModalityType.APPLICATION_MODAL);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(new JFrame(), results, "Berechnung erfolgreich!",
							JOptionPane.INFORMATION_MESSAGE);
				}

			}
		};
		return worker;
	}

	/**
	 * Aktiviert oder deaktiviert alle Komponenten eines Panels.
	 * 
	 * @param panel     Panel, dessen Komponenten aktiviert oder deaktiviert werden
	 *                  sollen.
	 * @param isEnabled Bestimmt, ob die Komponenten aktiviert oder deaktiviert
	 *                  werden sollen.
	 */
	void setPanelEnabled(JPanel panel, Boolean isEnabled) {
		panel.setEnabled(isEnabled);
		Component[] components = panel.getComponents();

		for (Component component : components) {
			if (component instanceof JPanel) {
				setPanelEnabled((JPanel) component, isEnabled);
			}
			component.setEnabled(isEnabled);
		}
	}
}
