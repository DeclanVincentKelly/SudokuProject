package gui;

//TODO Finish Javadoc

import game.SudokuGame;
import game.SudokuSolver;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("serial")
public class SudokuGameFrame extends JFrame implements Runnable {

	private JTabbedPane gameTabs = new JTabbedPane(JTabbedPane.BOTTOM);
	private JMenuBar menu = new JMenuBar();
	private SudokuRegister<SudokuGame> gameReg = new SudokuRegister<SudokuGame>();
	private JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));

	private static final String prevFileGame = "prevGameState.ser";

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new SudokuGameFrame());
	}

	public SudokuGameFrame() {
		super("Sudoku");

		// Reload previous games if any
		if (!loadPrevious())
			addGame(new SudokuGame("Unsaved Game"));

		// Add icons
		Image img = null;
		try {
			img = ImageIO.read(SudokuGameFrame.class.getResourceAsStream("/res/main.png"));
		} catch (IOException e) {
			img = getIconImage();
			e.printStackTrace();
		}
		setIconImage(img);

		gameTabs.setBackground(Color.WHITE);
		gameTabs.setFocusable(false);
		add(gameTabs);

		setJMenuBar(menu);
		addMenuItems(menu);

		// Add listener for saving the game(s) after the window has closed
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				gameReg.saveState(prevFileGame);
			}

		});

		// Add listeners for dynamic removal of game tabs
		// and for the toggling between the two main tabs
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

		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".game") || f.isDirectory();
			}

			@Override
			public String getDescription() {
				return "Sudoku Games (*.game)";
			}

		});

		setupTabTraversalKeys(gameTabs);
	}

	// Add actions for changing game tabs with CTRL+TAB or CTRL+SHIFT+TAB
	private static void setupTabTraversalKeys(JTabbedPane tabbedPane) {
		KeyStroke ctrlTab = KeyStroke.getKeyStroke("ctrl TAB");
		KeyStroke ctrlShiftTab = KeyStroke.getKeyStroke("ctrl shift TAB");

		// Remove ctrl-tab from normal focus traversal
		Set<AWTKeyStroke> forwardKeys = new HashSet<AWTKeyStroke>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		forwardKeys.remove(ctrlTab);
		tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

		// Remove ctrl-shift-tab from normal focus traversal
		Set<AWTKeyStroke> backwardKeys = new HashSet<AWTKeyStroke>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
		backwardKeys.remove(ctrlShiftTab);
		tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

		// Add keys to the tab's input map
		InputMap inputMap = tabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(ctrlTab, "navigateNext");
		inputMap.put(ctrlShiftTab, "navigatePrevious");
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
		gameReg.register(g);
		gameTabs.addTab(g.getName(), new SudokuBoard(g));
	}

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
				SudokuGame g = new SudokuGame("Unsaved Game");
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
				int returnVal = chooser.showOpenDialog(SudokuGameFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					SudokuGame g = (SudokuGame) SudokuRegister.load(chooser.getSelectedFile());
					g.setName(chooser.getSelectedFile().getName().substring(0, chooser.getSelectedFile().getName().indexOf(g.getSuffix()) - 1));
					addGame(g);
					g.setSave(chooser.getSelectedFile());
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
				gameReg.deregister(gameReg.indexOf(temp.getGame()));
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
					chooser.setSelectedFile(new File(g.getName()));
					int result = chooser.showSaveDialog(SudokuGameFrame.this);
					if (result == JFileChooser.APPROVE_OPTION) {
						g.setName(chooser.getSelectedFile().getName().substring(0, chooser.getSelectedFile().getName().indexOf(g.getSuffix()) - 1));
						g.setSave(chooser.getSelectedFile());
						SudokuRegister.save(g);
						gameTabs.setTitleAt(gameTabs.getSelectedIndex(), g.getName());
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

				chooser.setSelectedFile(new File(g.getName() + "." + g.getSuffix()));
				int result = chooser.showSaveDialog(SudokuGameFrame.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					g.setName(chooser.getSelectedFile().getName().substring(0, chooser.getSelectedFile().getName().indexOf(g.getSuffix()) - 1));
					g.setSave(chooser.getSelectedFile());
					SudokuRegister.save(g);
					gameTabs.setTitleAt(gameTabs.getSelectedIndex(), g.getName());
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
				for (SudokuGame g : gameReg.values()) {
					if (g.isSaved()) {
						SudokuRegister.save(g);
					} else {

						chooser.setSelectedFile(new File(g.getName() + "." + g.getSuffix()));
						int result = chooser.showSaveDialog(SudokuGameFrame.this);
						if (result == JFileChooser.APPROVE_OPTION) {
							g.setName(chooser.getSelectedFile().getName().substring(0, chooser.getSelectedFile().getName().indexOf(g.getSuffix()) - 1));
							g.setSave(chooser.getSelectedFile());
							SudokuRegister.save(g);
							gameTabs.setTitleAt(gameTabs.getSelectedIndex(), g.getName());
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

		// TODO Finish menu item
		JMenuItem highlighting = new JMenuItem("Turn Off Highlighting", KeyEvent.VK_O);
		view.add(highlighting);

		// TODO Finish menu item
		JMenuItem changeColors = new JMenuItem("Change Color Scheme", KeyEvent.VK_C);
		view.add(changeColors);

		view.addSeparator();

		JMenu game = new JMenu("Game");
		game.setMnemonic(KeyEvent.VK_G);

		// TODO Finish menu item
		JMenuItem undo = new JMenuItem("Undo", KeyEvent.VK_U);
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		game.add(undo);

		// TODO Finish menu item
		JMenuItem redo = new JMenuItem("Redo", KeyEvent.VK_R);
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		game.add(redo);

		game.addSeparator();

		// TODO Finish menu item
		JMenuItem renameGame = new JMenuItem("Rename Game", KeyEvent.VK_R);
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		game.add(renameGame);

		JMenuItem setValues = new JMenuItem(new AbstractAction("Edit Constants") {

			{
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_V);
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuBoard b = (SudokuBoard) gameTabs.getComponentAt(gameTabs.getSelectedIndex());
				if (!b.getEditConstant())
					b.setBackground(new Color(120, 120, 120));
				else
					b.setBackground(Color.WHITE);
				b.setEditConstant(!b.getEditConstant());
				b.repaint();
			}

		});
		game.add(setValues);

		game.addSeparator();

		JMenuItem solve = new JMenuItem(new AbstractAction("Solve Game") {

			{
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_S);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuGame current = ((SudokuBoard) gameTabs.getSelectedComponent()).getGame();
				if (current.hasDuplicates()) {
					JOptionPane.showMessageDialog(null, "Remove all duplicates!");
					return;
				}
				if (!SudokuSolver.solveGame(current))
					JOptionPane.showMessageDialog(null, "This game can't be solved!");
				current.refresh();
				repaint();
			}

		});
		game.add(solve);

		bar.add(file);
		bar.add(view);
		bar.add(game);
	}

}
