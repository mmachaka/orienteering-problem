package de.hagen.fernuni.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Die WelcomePage ist das erste Fenster der Benutzeroberfläche, welches nach
 * dem Starten der Anwendung angezeigt wird. Sie beinhaltet auch die
 * Main-Methode der Anwendung.
 *
 * @author Mahmoud Machaka
 * @version 1.0
 */
public class WelcomePage extends JFrame {

	private static final long serialVersionUID = 8553738970213151013L;

	private JPanel contentPane;

	/**
	 * Launch the application.
	 * 
	 * @param args Startparameter der Anwendung
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WelcomePage frame = new WelcomePage();
					frame.setVisible(true);
					frame.setLocationRelativeTo(null); // Zentriert das Fenster

					// Falls, nicht-unterstützte Java-Version erkannt wird
					if (!System.getProperty("java.version").substring(0, 3).equals("1.8")) {
						System.out.println("A");
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
									System.out.println("B");
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
	public WelcomePage() {

		setIconImage(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Fernuni-Logo.png")));
		setResizable(false);
		setTitle("Heuristische Algorithmen zur L\u00F6sung des Orientierungsproblems");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 737);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panelTextPane = new JPanel();
		contentPane.add(panelTextPane, BorderLayout.CENTER);
		panelTextPane.setLayout(null);

		JTextPane textPaneTitle = new JTextPane();
		textPaneTitle.setBounds(28, 13, 732, 120);
		textPaneTitle.setBackground(SystemColor.control);
		textPaneTitle.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textPaneTitle.setEditable(false);
		textPaneTitle.setContentType("text/html");
		textPaneTitle.setText(
				"<h1 style=\"text-align: center;\"><strong><span style=\"font-size: xx-large;\">Heuristische Algorithmen zur L&ouml;sung des Orientierungsproblems<br /></span></strong></h1>");
		panelTextPane.add(textPaneTitle);

		JTextPane textPaneMessage = new JTextPane();
		textPaneMessage.setEditable(false);
		textPaneMessage.setContentType("text/html");
		textPaneMessage.setText(
				"<p style=\"text-align: justify; font-size: 14px;\">Beim Orientierungsproblem werden ungerichtete Graphen mit gewichteten Knoten und Kanten betrachtet. Die Gewichte der Knoten stellen Profite dar, w&auml;hrend die Gewichte der Kanten als Kosten verstanden werden k&ouml;nnen.</p><p style=\"text-align: justify; font-size: 14px;\">Als Spezialisierung der Tourenplanung besch&auml;ftigt sich das Orientierungsproblem mit der Suche nach einer Tour im Graphen, bei der unter Ber&uuml;cksichtigung einer Kostenobergrenze der Profit maximiert werden kann.</p><p style=\"text-align: justify; font-size: 14px;\">Mit dieser Applikation k&ouml;nnen zwei heuristische Algorithmen zur effizienten L&ouml;sung des Orientierungsproblems auf geeignete Graphen angewendet werden. Zu beachten ist, dass diese heuristischen Algorithmen die exakte L&ouml;sung nur approximieren k&ouml;nnen. Jedoch ben&ouml;tigen sie zur Berechnung im Gegensatz zu exakten Algorithmen nur einen Bruchteil der Zeit.</p><p style=\"text-align: justify; font-size: 14px;\">Hinweise zur Anwendung finden Sie in der beigef&uuml;gten Readme-Datei oder wenn Sie &uuml;ber die entsprechenden Elemente der Anwendung mit der Maus fahren.</p><p style=\"text-align: justify; font-size: 13px;\"><em>Diese Anwendung wurde im Rahmen einer Master-Abschlussarbeit von Mahmoud Machaka an der FernUniversit&auml;t in Hagen entwickelt. Fragen oder Anmerkungen k&ouml;nnen Sie gerne per Mail an <a href=\"mailto:mahmoud.machaka@studium.fernuni-hagen.de\">mahmoud.machaka@studium.fernuni-hagen.de</a> richten.<br /></em></p>");

		textPaneMessage.setBounds(28, 159, 732, 387);
		textPaneMessage.setMargin(new Insets(0, 15, 5, 15)); // Abstand des Textes zu den Rändern des Textfeldes
		panelTextPane.add(textPaneMessage);
		
		JScrollPane scrollPane = new JScrollPane(textPaneMessage);
		scrollPane.setBounds(28, 159, 732, 472);
		panelTextPane.add(scrollPane);

		JPanel panelButtons = new JPanel();
		FlowLayout fl_panelButtons = (FlowLayout) panelButtons.getLayout();
		fl_panelButtons.setAlignment(FlowLayout.RIGHT);
		contentPane.add(panelButtons, BorderLayout.SOUTH);

		// Close WelcomePage and open MainGUI
		JButton btnContinue = new JButton("Weiter");
		btnContinue.setMargin(new Insets(1, 15, 1, 15));
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				new MainGUI().displayMainGUI();
			}
		});
		panelButtons.add(btnContinue);
	}
}
