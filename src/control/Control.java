package control;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import main.Settings;

import java.awt.Desktop;
import java.io.IOException;

/**
 * a {@code JPanel} for all Controls
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public abstract class Control extends JPanel {

	private static final long serialVersionUID = 7062093943678033069L;
	
	/**
	 * creates an instance of the {@code Control} class
	 */
	public Control() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Settings.BACKGROUND, 5));
	}
	
	/**
	 * creates a top panel with a refresh button and the correct layout
	 * @return a {@code JPanel} with a refresh button
	 */
	protected JPanel getNorthPanel() {
		JPanel northPanel = getEmptyNorthPanel();
		JButton folderopenButton = Settings.createButton(Settings.ICON_FOLDEROPEN);
		folderopenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(Settings.FOLDER);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		JButton refreshButton = Settings.createButton(Settings.ICON_REFRESH);
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		

		northPanel.add(folderopenButton);
		northPanel.add(refreshButton);
		
		northPanel.repaint();
		
		return northPanel;
	}
	
	/**
	 * creates a top panel without anything in it
	 * @return a {@code JPanel}
	 */
	protected JPanel getEmptyNorthPanel() {
		JPanel northPanel = new JPanel();
		northPanel.setBackground(Settings.CONTROL_BACKGROUND);
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
		northPanel.repaint();
		return northPanel;
	}
	
	/**
	 * loads all of the image from the file
	 */
	protected abstract void load();

	/**
	 * tells the {@code ControlPanel} if it is the currently displayed control
	 * @param b true if it is displayed, false if it is not
	 */
	public void setMainControl(boolean b) {
		
	}
}