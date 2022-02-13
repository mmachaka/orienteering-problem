package de.hagen.fernuni.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.hagen.fernuni.model.Edge;
import de.hagen.fernuni.model.Node;

/**
 * DialogShowResultsOnMap bietet ein Dialog, in welchem das Ergebnis der
 * Berechnung optisch dargestellt wird, falls der Graph mithilfe der
 * Deutschlandkarte erzeugt wurde.
 * 
 * @author Mahmoud Machaka
 *
 */
public class DialogShowResultsOnMap extends JDialog {

	private static final long serialVersionUID = 2712023239322067722L;

	private final JPanel panelMap = new JPanel();

	private BufferedImage bufferedImage;
	private ImageIcon imageIcon;
	JLabel lblMap = new JLabel();

	/**
	 * Create the dialog.
	 * 
	 * @param mainGUI Main-Frame
	 * @param results Anzuzeigende Ergebnisse
	 */
	public DialogShowResultsOnMap(MainGUI mainGUI, String results) {
		setResizable(false);
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Fernuni-Logo.png")));
		setTitle("Heuristische Algorithmen zur L\u00F6sung des Orientierungsproblems");
		setBounds(100, 100, 879, 804);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		panelMap.setBounds(12, 13, 531, 706);
		panelMap.setBorder(null);
		getContentPane().add(panelMap);
		panelMap.setLayout(null);

		JLabel lblMap = new JLabel();
		lblMap.setBounds(12, 13, 506, 685);
		panelMap.add(lblMap);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(12, 725, 852, 35);
			getContentPane().add(buttonPane);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
			{
				JButton okButton = new JButton("OK");
				okButton.setMargin(new Insets(1, 15, 1, 15));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		{
			try {
				bufferedImage = ImageIO.read(this.getClass().getClassLoader().getResource("GermanySimpleMap_50.png"));
				imageIcon = new ImageIcon(bufferedImage);
				int size = mainGUI.solvedGraph.getSize();
				for (int i = 0; i < size; i++) {
					Node v = mainGUI.solvedGraph.getV().get(i);
					String nodeName = "K" + i;
					drawNode((int) v.getX(), (int) v.getY(), nodeName);
				}
				drawEdges(mainGUI.solvedGraph.getE());

			} catch (IOException e1) {
				System.out.println("Karte konnte nicht geladen werden, da die Ressource nicht gefunden wurde.");
				e1.printStackTrace();
			}
			lblMap.setIcon(imageIcon);
			panelMap.add(lblMap);
		}
		{
			JPanel panelResults = new JPanel();
			panelResults.setBounds(555, 13, 309, 706);
			getContentPane().add(panelResults);
			panelResults.setLayout(null);

			JLabel lblResults = new JLabel(results);
			lblResults.setVerticalAlignment(SwingConstants.TOP);
			lblResults.setBounds(12, 23, 285, 670);
			panelResults.add(lblResults);
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
	 * Zeichnet eine Kante auf der Deutschlandkarte ein.
	 * 
	 * @param edges Einzuzeichnende Kante
	 */
	private void drawEdges(ArrayList<Edge> edges) {
		Graphics2D g = bufferedImage.createGraphics();
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(hints);
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(3));
		Node start, end;
		for (Edge e : edges) {
			start = e.getStartNode();
			end = e.getEndNode();
			g.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
		}
		lblMap.setIcon(new ImageIcon(bufferedImage));
		g.dispose();
	}
}
