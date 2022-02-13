package de.hagen.fernuni.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Graph;
import de.hagen.fernuni.model.Node;

/**
 * DialogChooseGraphFromMap stellt ein Dialog zur Verfügung, in welchem ein
 * Graph mithilfe einer Deutschlandkarte modelliert werden kann.
 * 
 * @author Mahmoud Machaka
 *
 */
public class DialogChooseGraphFromMap extends JDialog {

	private static final long serialVersionUID = 6839484583169456982L;

	private final JPanel contentPanel = new JPanel();
	private JTable tableNodes;

	private BufferedImage bufferedImage;
	private ImageIcon imageIcon;
	JLabel lblMap = new JLabel();

	private static int nodeCounter = 0;

	/**
	 * Create the dialog.
	 * 
	 * @param mainGUI         Main-Frame
	 * @param dialogLoadGraph Parent-Frame
	 */
	public DialogChooseGraphFromMap(MainGUI mainGUI, DialogLoadGraph dialogLoadGraph) {

		setResizable(false);
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Fernuni-Logo.png")));
		setTitle("Heuristische Algorithmen zur L\u00F6sung des Orientierungsproblems");
		setBounds(100, 100, 922, 845);
		setLocationRelativeTo(null); // Zentriert das Fenster
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(null);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblInstructions = new JLabel(
					"<html>F\u00FCgen Sie durch Klicken auf der Karte neue Knoten hinzu.<br />Stellen Sie sicher, dass mindestens zwei Knoten hinzugef\u00FCgt wurden.</html>");
			lblInstructions.setFont(new Font("Tahoma", Font.PLAIN, 16));
			lblInstructions.setBounds(12, 13, 596, 52);
			contentPanel.add(lblInstructions);
		}
		{
			try {
				bufferedImage = ImageIO.read(this.getClass().getClassLoader().getResource("GermanySimpleMap_50.png"));
				imageIcon = new ImageIcon(bufferedImage);
			} catch (IOException e1) {
				System.out.println("Karte konnte nicht geladen werden, da die Ressource nicht gefunden wurde.");
				e1.printStackTrace();
			}

			// Füge neuen Knoten in Liste hinzu und zeichne den Knoten auf der Karte ein
			lblMap.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int x = e.getX();
					int y = e.getY();
					// Ignoriere Mausklicks auf transparentem Bereich (Rand der Deutschlandkarte)
					if (bufferedImage.getRGB(x, y) == 0)
						return;

					DefaultTableModel model = (DefaultTableModel) tableNodes.getModel();

					// Verhindere das Hinzufügen von neuen Knoten in der Nähe bereits bestehender
					// Knoten
					int tableRowCount = model.getRowCount();
					for (int i = 0; i < tableRowCount; i++) {
						int xTableNode = (int) model.getValueAt(i, 1);
						int yTableNode = (int) model.getValueAt(i, 2);
						if ((x > xTableNode - 10 && x < xTableNode + 10)
								&& (y > yTableNode - 10 && y < yTableNode + 25)) {
							return;
						}

					}

					String nodeName = "K" + nodeCounter++;
					Object[] rowData;
					Random random = new Random();

					// Setze Profit des Start- und Endknotens auf 0. Setze den Profit weiterer
					// Knoten auf einen zufälligen Wert (Range 1-100)
					if (nodeCounter <= 2) {
						rowData = new Object[] { nodeName, x, y, 0 };
					} else {
						rowData = new Object[] { nodeName, x, y, random.nextInt(99) + 1 };
					}
					model.insertRow(0, rowData);
					drawNode(x, y, nodeName);
				}
			});
			lblMap.setIcon(imageIcon);
			lblMap.setBounds(32, 78, 506, 685);
			contentPanel.add(lblMap);
		}
		{
			JButton btnClear = new JButton("Reset");
			btnClear.setMargin(new Insets(1, 1, 1, 1));
			btnClear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					resetMap();
				}
			});
			btnClear.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnClear.setBounds(748, 666, 97, 25);
			contentPanel.add(btnClear);
		}

		JScrollPane scrollPaneForTable = new JScrollPane();
		scrollPaneForTable.setToolTipText("<html>In dieser Tabelle werden die Werte der hinzugef\u00FCgten Knoten angezeigt.<br />Sie k\u00F6nnen hier die Profite s\u00E4mtlicher Knoten (ausgenommen Start- und Endknoten) manuell editieren.</html>");
		scrollPaneForTable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneForTable.setBounds(550, 78, 295, 575);
		contentPanel.add(scrollPaneForTable);

		tableNodes = new JTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			// Ermögliche das Editieren der Profit-Spalte mit Ausnahme der Start- und
			// Endknoten
			@Override
			public boolean isCellEditable(int row, int column) {
				if (row == tableNodes.getRowCount() - 1 || row == tableNodes.getRowCount() - 2)
					return false;

				switch (column) {
				case 3:
					return true;
				default:
					return false;
				}
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Integer.class;
			};
		};
		;
		tableNodes.setRowSelectionAllowed(false);
		scrollPaneForTable.setViewportView(tableNodes);

		tableNodes.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Knoten", "x", "y", "Profit" }));
		tableNodes.getColumnModel().getColumn(0).setResizable(false);
		tableNodes.getColumnModel().getColumn(1).setResizable(false);
		tableNodes.getColumnModel().getColumn(2).setResizable(false);
		tableNodes.getColumnModel().getColumn(3).setResizable(false);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Abbrechen");
				cancelButton.setMargin(new Insets(1, 15, 1, 15));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						resetMap();
						dispose();
					}
				});
				{
					JButton btnLoad = new JButton("Speichern");
					btnLoad.setMargin(new Insets(1, 15, 1, 15));
					buttonPane.add(btnLoad);
					btnLoad.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							ArrayList<Node> nodeList = new ArrayList<Node>();

							for (int rowCounter = tableNodes.getRowCount() - 1; rowCounter >= 0; rowCounter--) {
								double x = Double.parseDouble(tableNodes.getValueAt(rowCounter, 1).toString());
								double y = Double.parseDouble(tableNodes.getValueAt(rowCounter, 2).toString());
								double profit = Double.parseDouble(tableNodes.getValueAt(rowCounter, 3).toString());
								nodeList.add(new Node(x, y, profit));
							}
							if (nodeList.size() >= 2) {
								mainGUI.graph = new Graph(nodeList, new ArrayList<Edge>());
								resetMap();
								dispose();
								dialogLoadGraph.dispose();
								mainGUI.mapChoosen = true;
								mainGUI.setTmaxToMinimum();
								mainGUI.lblLoadedGraph.setForeground(new Color(0, 100, 0));
								mainGUI.lblLoadedGraph.setText(
										"Graph mit " + mainGUI.graph.getV().size() + " Knoten erfolgreich geladen.");
							} else {
								JOptionPane.showMessageDialog(new JFrame(),
										"Der Graph konnte nicht gespeichert werden. Bitte stellen Sie sicher, dass mindestens zwei Knoten eingetragen wurden.",
										"Fehler beim Speichern des Graphen", JOptionPane.ERROR_MESSAGE);
							}
						}
					});
					btnLoad.setFont(new Font("Tahoma", Font.PLAIN, 13));
				}
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

	}

	/**
	 * Zeichnet einen Knoten auf der Deutschlandkarte ein.
	 * 
	 * @param x    X-Koordinate des Knotens
	 * @param y    Y-Koordinate des Knotens
	 * @param name Name des Knotens
	 */
	private void drawNode(int x, int y, String name) {
		Graphics2D g = bufferedImage.createGraphics();
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(hints);
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(3));
		int size = 3;
		g.drawLine(x - size, y + size, x + size, y - size);
		g.drawLine(x - size, y - size, x + size, y + size);
		g.drawString(name, x - size - 3, y + size + 15);
		lblMap.setIcon(new ImageIcon(bufferedImage));
		g.dispose();
	}

	/**
	 * Setzt die Deutschlandkarte und die Tabelle zurück.
	 */
	private void resetMap() {
		// Beende offene Zell-Editierung
		if (tableNodes.isEditing())
			tableNodes.getCellEditor().cancelCellEditing();

		// Entferne alle Elemente aus der Tabelle
		DefaultTableModel model = (DefaultTableModel) tableNodes.getModel();
		model.setRowCount(0);
		nodeCounter = 0;

		// Entferne alle Elemente von der Karte
		try {
			bufferedImage = ImageIO.read(this.getClass().getClassLoader().getResource("GermanySimpleMap_50.png"));
			imageIcon = new ImageIcon(bufferedImage);
			lblMap.setIcon(imageIcon);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
