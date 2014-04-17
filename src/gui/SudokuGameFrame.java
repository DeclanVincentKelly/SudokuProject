package gui;

//TODO Finish Javadoc

import game.SudokuGame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import library.SudokuLibrary;

@SuppressWarnings("serial")
public class SudokuGameFrame extends JFrame implements Runnable {

	private JTabbedPane tabs = new JTabbedPane();
	private JPanel libraryTab = new JPanel();
	private JTabbedPane gameTabs = new JTabbedPane(JTabbedPane.BOTTOM);
	private JMenuBar menu = new JMenuBar();
	private SudokuRegister<SudokuGame> gameReg = new SudokuRegister<SudokuGame>();
	private SudokuRegister<SudokuLibrary> libReg = new SudokuRegister<SudokuLibrary>();
	private ArrayList<Component> gameMenuItems = new ArrayList<Component>();
	private ArrayList<Component> libraryMenuItems = new ArrayList<Component>();

	private static final String prevFileGame = "prevGameState.ser";
	private static final String prevFileLib = "prevLibState.ser";

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new SudokuGameFrame());
	}

	public SudokuGameFrame() {
		super("Sudoku");

		if (!loadPrevious())
			addGame(new SudokuGame("New Game"));

		Image img = null;
		try {
			img = ImageIO.read(SudokuGameFrame.class.getResourceAsStream("/res/main.png"));
		} catch (IOException e) {
			img = getIconImage();
			e.printStackTrace();
		}
		setIconImage(img);

		add(tabs);
		try {
			tabs.addTab("Game Board", new ImageIcon(ImageIO.read(SudokuGameFrame.class.getResourceAsStream("/res/board.png"))), gameTabs);
			tabs.addTab("Game Library", new ImageIcon(ImageIO.read(SudokuGameFrame.class.getResourceAsStream("/res/library.png"))), libraryTab);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		tabs.setFocusable(false);

		gameTabs.setFocusable(false);

		// TODO Insert arrangements for Game Library JPanel (Possible a JTree
		// and a search boxs)

		setJMenuBar(menu);
		addMenuItems(menu);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				gameReg.saveState(prevFileGame);
			}

		});

		gameTabs.addContainerListener(new ContainerListener() {

			@Override
			public void componentAdded(ContainerEvent e) {
				SudokuGameFrame.this.pack();
			}

			@Override
			public void componentRemoved(ContainerEvent e) {
				if (gameTabs.getTabCount() > 0)
					SudokuGameFrame.this.pack();
			}

		});

		tabs.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				toggleMenuItems();
			}

		});
		
		toggleMenuItems();
	}

	private void toggleMenuItems() {
		if(tabs.getSelectedIndex() == 0) {
			for(Component c: gameMenuItems)
				c.setEnabled(true);
			
			for(Component c: libraryMenuItems)
				c.setEnabled(false);
		} else {
			for(Component c: gameMenuItems)
				c.setEnabled(false);
			
			for(Component c: libraryMenuItems)
				c.setEnabled(true);
		}
		
	}

	public void run() {
		setResizable(false);
		setLocation(100, 100);
		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private boolean loadPrevious() {
		gameReg.loadState(prevFileGame);
		if (gameReg.isEmpty())
			return false;
		else {
			for (SudokuGame g : gameReg.values())
				addGame(g);
			return true;
		}
	}

	private void addGame(SudokuGame g) {
		if (!isUnique(g.getName()))
			g.setName(g.getName() + " (1)");

		int count = 2;
		while (!isUnique(g.getName())) {
			g.setName(g.getName().substring(0, g.getName().length() - (g.getName().length() - g.getName().lastIndexOf("("))) + "(" + count + ")");
			count++;
		}

		gameTabs.addTab(g.getName(), new SudokuBoard(g));
		gameReg.register(g.getName(), g);

	}

	private boolean isUnique(String name) {
		for (int i = 0; i < gameTabs.getTabCount(); i++)
			if (((SudokuBoard) gameTabs.getComponentAt(i)).getGame().getName().equals(name))
				return false;

		return true;
	}

	// TODO Add actions
	private void addMenuItems(JMenuBar bar) {
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);

		JMenuItem newGame = new JMenuItem(new AbstractAction("New Game") {

			{
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_N);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(SudokuGameFrame.this, "Enter the name for the new game", "New Game", JOptionPane.QUESTION_MESSAGE);
				if (name == null || name.equals(""))
					name = "New Game";

				SudokuGame g = new SudokuGame(name);
				addGame(g);

				repaint();
			}

		});
		file.add(newGame);

		JMenuItem openGame = new JMenuItem(new AbstractAction("Open Game") {

			{
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_O);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Sudoku Games", "game");
				fc.setFileFilter(filter);
				int returnVal = fc.showOpenDialog(SudokuGameFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					SudokuGame g = (SudokuGame) SudokuRegister.load(fc.getSelectedFile());
					g.setName(fc.getSelectedFile().getName().substring(0, fc.getSelectedFile().getName().indexOf(".")));
					addGame(g);
					g.saveAt(fc.getSelectedFile());
				}
			}

		});
		file.add(openGame);

		file.addSeparator();

		JMenuItem close = new JMenuItem(new AbstractAction("Close Current Game") {

			{
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_C);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuBoard temp = (SudokuBoard) gameTabs.getSelectedComponent();
				gameReg.deregister(temp.getGame().getName());
				gameTabs.remove(temp);
				repaint();
			}

		});
		file.add(close);

		JMenuItem closeAll = new JMenuItem(new AbstractAction("Close All Games") {

			{
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_L);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				gameReg.clear();
				gameTabs.removeAll();
				repaint();
			}

		});
		file.add(closeAll);

		file.addSeparator();

		JMenuItem save = new JMenuItem(new AbstractAction("Save") {

			{
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_S);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuGame g = ((SudokuBoard) gameTabs.getSelectedComponent()).getGame();
				if (g.isSaved()) {
					SudokuRegister.save(g);
				} else {
					JFileChooser fc = new JFileChooser();
					fc.setSelectedFile(new File(g.getName() + "." + g.getSuffix()));
					fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
					int result = fc.showSaveDialog(SudokuGameFrame.this);
					if (result == JFileChooser.APPROVE_OPTION) {
						g.saveAt(fc.getSelectedFile());
						SudokuRegister.save(g);
					}
				}
			}

		});
		file.add(save);

		JMenuItem saveAs = new JMenuItem(new AbstractAction("Save As") {
			
			{
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_A);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuGame g = ((SudokuBoard) gameTabs.getSelectedComponent()).getGame();
				JFileChooser fc = new JFileChooser();
				fc.setSelectedFile(new File(g.getName() + "." + g.getSuffix()));
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int result = fc.showSaveDialog(SudokuGameFrame.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					g.saveAt(fc.getSelectedFile());
					SudokuRegister.save(g);
				}				
			}
			
		});
		file.add(saveAs);

		JMenuItem saveAll = new JMenuItem(new AbstractAction("Save All") {

			{
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_V);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				for(SudokuGame g: gameReg.values()) {
					if (g.isSaved()) {
						SudokuRegister.save(g);
					} else {
						JFileChooser fc = new JFileChooser();
						fc.setSelectedFile(new File(g.getName() + "." + g.getSuffix()));
						fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
						int result = fc.showSaveDialog(SudokuGameFrame.this);
						if (result == JFileChooser.APPROVE_OPTION) {
							g.saveAt(fc.getSelectedFile());
							SudokuRegister.save(g);
						}
					}
				}
			}

		});
		file.add(saveAll);

		file.addSeparator();

		JMenuItem exit = new JMenuItem(new AbstractAction("Exit") {

			{
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_E);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuGameFrame.this.dispose();
			}

		});
		file.add(exit);

		JMenu view = new JMenu("View");
		view.setMnemonic(KeyEvent.VK_V);

		JMenuItem highlighting = new JMenuItem("Turn Off Highlighting", KeyEvent.VK_O);
		view.add(highlighting);

		JMenuItem changeColors = new JMenuItem("Change Color Scheme", KeyEvent.VK_C);
		view.add(changeColors);

		view.addSeparator();

		JMenuItem nextTab = new JMenuItem("Next Tab", KeyEvent.VK_N);
		nextTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.CTRL_MASK));
		view.add(nextTab);

		JMenuItem prevTab = new JMenuItem("Previous Tab", KeyEvent.VK_P);
		prevTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.CTRL_MASK));
		view.add(prevTab);

		JMenu game = new JMenu("Game");
		game.setMnemonic(KeyEvent.VK_G);

		JMenuItem undo = new JMenuItem("Undo", KeyEvent.VK_U);
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		game.add(undo);

		JMenuItem redo = new JMenuItem("Redo", KeyEvent.VK_R);
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		game.add(redo);

		game.addSeparator();

		JMenuItem renameGame = new JMenuItem("Rename Game", KeyEvent.VK_R);
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		game.add(renameGame);

		JMenuItem setValues = new JMenuItem(new AbstractAction("Edit Constant Values") {

			{
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_V);
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuBoard b = (SudokuBoard) gameTabs.getComponentAt(gameTabs.getSelectedIndex());
				if(!b.getEditConstant())
					b.setBackground(Color.GRAY);
				else
					b.setBackground(new Color(238, 238, 238));
				b.setEditConstant(!b.getEditConstant());
				b.repaint();
			}

		});
		game.add(setValues);

		JMenu library = new JMenu("Library");
		library.setMnemonic(KeyEvent.VK_L);

		JMenuItem openLib = new JMenuItem("Open Library", KeyEvent.VK_O);
		library.add(openLib);

		JMenuItem closeLib = new JMenuItem("Close Library", KeyEvent.VK_C);
		library.add(closeLib);

		JMenuItem saveLib = new JMenuItem("Save Library", KeyEvent.VK_S);
		library.add(saveLib);

		library.addSeparator();

		JMenuItem changeActive = new JMenuItem("Change Active Library", KeyEvent.VK_A);
		library.add(changeActive);

		bar.add(file);
		bar.add(view);
		bar.add(game);
		bar.add(library);

		gameMenuItems.addAll(Arrays.asList(file.getMenuComponents()));
		gameMenuItems.addAll(Arrays.asList(view.getMenuComponents()));
		gameMenuItems.addAll(Arrays.asList(game.getMenuComponents()));

		libraryMenuItems.addAll(Arrays.asList(library.getMenuComponents()));
		
		ArrayList<Component> toRemove = new ArrayList<Component>();
		
		for (Component c : gameMenuItems)
			if (c instanceof JSeparator)
				toRemove.add(c);
		gameMenuItems.removeAll(toRemove);
		toRemove.clear();
		
		for (Component c : libraryMenuItems)
			if (c instanceof JSeparator)
				toRemove.add(c);
		libraryMenuItems.removeAll(toRemove);
		toRemove.clear();
	}

}
