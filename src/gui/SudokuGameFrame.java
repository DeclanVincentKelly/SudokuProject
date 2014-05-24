package gui;

//TODO Finish Javadoc

import game.SudokuGame;
import game.SudokuSolverToolkit;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.colorchooser.AbstractColorChooserPanel;
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
		KeyStroke forwardTab = KeyStroke.getKeyStroke("ctrl TAB");
		KeyStroke backwardTab = KeyStroke.getKeyStroke("ctrl shift TAB");

		// Remove ctrl-tab from normal focus traversal
		Set<AWTKeyStroke> forwardKeys = new HashSet<AWTKeyStroke>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		forwardKeys.remove(forwardTab);
		tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

		// Remove ctrl-shift-tab from normal focus traversal
		Set<AWTKeyStroke> backwardKeys = new HashSet<AWTKeyStroke>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
		backwardKeys.remove(backwardTab);
		tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

		// Add keys to the tab's input map
		InputMap inputMap = tabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(forwardTab, "navigateNext");
		inputMap.put(backwardTab, "navigatePrevious");
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
				if (temp == null)
					return;
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
				SudokuBoard temp = (SudokuBoard) gameTabs.getSelectedComponent();
				if (temp == null)
					return;
				SudokuGame g = temp.getGame();
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
				SudokuBoard temp = (SudokuBoard) gameTabs.getSelectedComponent();
				if (temp == null)
					return;
				SudokuGame g = temp.getGame();

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

		JMenuItem highlighting = new JMenuItem(new AbstractAction("Toggle Highlighting") {

			{
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_O);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuBoard current = (SudokuBoard) gameTabs.getSelectedComponent();
				if (current == null)
					return;
				current.getGame().toggleHighlighting();
				current.repaint();
			}

		});
		view.add(highlighting);

		JMenuItem changeColors = new JMenuItem(new AbstractAction("Change Color Scheme") {

			{
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_C);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuBoard board = (SudokuBoard) gameTabs.getSelectedComponent();
				if (board == null)
					return;
				final ArrayList<Color> colors = new ArrayList<Color>();
				String[] titles = { "Choose a base color", "Choose a region complete color", "Choose a duplicate color" };
				final JColorChooser chooser = new JColorChooser();
				chooser.setPreviewPanel(new JPanel());
				for (AbstractColorChooserPanel a : chooser.getChooserPanels()) {
					if (a.getDisplayName().equals("Swatches"))
						chooser.setChooserPanels(new AbstractColorChooserPanel[] { a });
				}
				for (String title : titles) {
					JDialog dialog = JColorChooser.createDialog(SudokuGameFrame.this, title, true, chooser, new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							colors.add(chooser.getColor());
						}

					}, new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							colors.add(Color.BLACK);
						}
					});
					dialog.setVisible(true);
				}

				board.getGame().setColors(colors.get(0), colors.get(1), colors.get(2));
				board.repaint();
			}

		});
		view.add(changeColors);

		JMenu game = new JMenu("Game");
		game.setMnemonic(KeyEvent.VK_G);

		JMenuItem undo = new JMenuItem(new AbstractAction("Undo") {

			{
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_U);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuBoard temp = (SudokuBoard) gameTabs.getSelectedComponent();
				if (temp == null)
					return;
				try {
					temp.getGame().undo();
				} catch (NoSuchElementException e1) {
				}
				temp.repaint();
			}

		});
		game.add(undo);

		JMenuItem redo = new JMenuItem(new AbstractAction("Redo") {

			{
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_R);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuBoard temp = (SudokuBoard) gameTabs.getSelectedComponent();
				if (temp == null)
					return;
				try {
					temp.getGame().redo();
				} catch (NoSuchElementException e1) {
				}
				temp.repaint();
			}

		});
		game.add(redo);

		game.addSeparator();

		JMenuItem setValues = new JMenuItem(new AbstractAction("Edit Constants") {

			{
				this.putValue(MNEMONIC_KEY, KeyEvent.VK_C);
				this.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SudokuBoard b = (SudokuBoard) gameTabs.getSelectedComponent();
				if (b == null)
					return;
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
				SudokuBoard temp = (SudokuBoard) gameTabs.getSelectedComponent();
				if (temp == null)
					return;
				SudokuGame current = temp.getGame();
				if (current.hasDuplicates()) {
					JOptionPane.showMessageDialog(null, "Remove all duplicates!");
					return;
				}
				if (!SudokuSolverToolkit.solveGame(current))
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
