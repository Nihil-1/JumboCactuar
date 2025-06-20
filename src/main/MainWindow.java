package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class MainWindow extends JFrame {

	private final boolean DEBUG = false;

	private static final long serialVersionUID = 1L;
	private static final int ENCOUNTER_SIZE = 128;
	private static final int ENCOUNTERS_COUNT = 1024;
	private static final String TITLE = "Jumbo Cactuar - scene.out editor";

	private List<String> battleStageNames, enemyNames;
	private Encounter[] encs;
	private boolean suppressComponentChangedEvents = false, fileChanged = false;
	private String currentFile;
	private Encounter copiedEnc = null;
	private int copiedBattleStage;
	private byte copiedBattleFlags;
	private int copiedMainCam, copiedMainCamAnim;
	private int copiedSecCam, copiedSecCamAnim;
	private int[] copiedUnknowns = new int[56];
	private List<EnemyData> copiedEnemies;
	private EnemyData copiedEnemy;

	private JPanel contentPane;

	private JLabel encIdLbl;
	private JSpinner encIdSpinner;
	private JButton loadBtn;

	private JLabel battleStageLbl;
	private JLabel mainCamIdLbl, mainCamAnimLbl, secCamIdLbl, secCamAnimLbl;
	private JSpinner mainCamIdSpinner, mainCamAnimSpinner, secCamIdSpinner, secCamAnimSpinner;
	private JComboBox<String> battleStageComboBox;
	private JCheckBox[] battleFlagsChkBoxArr;
	private List<EnemySlotPanel> enemySlots;
	private UnknownsPanel unknownsPanel;
	private JButton copyEncounterBtn, pasteEncounterBtn;
	private JButton mainCamCopyBtn, mainCamPasteBtn, battleFlagsCopyBtn, battleFlagsPasteBtn, battleStageCopyBtn, battleStagePasteBtn;
	private JButton secCamPasteBtn;
	private JButton secCamCopyBtn;
	private JButton copySelectedEncountersBtn, pasteSelectedEncountersBtn;

	public static void main(String[] args) {
		final File draggedAndDroppedFile;

		if (args.length > 0) {
			String path = args[0];
			File file = new File(path);
			if (file.exists() && file.getName().endsWith(".out")) {
				draggedAndDroppedFile = file;
			} else {
				draggedAndDroppedFile = null;
			}
		} else {
			draggedAndDroppedFile = null;
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame;
					if (draggedAndDroppedFile != null) {
						frame = new MainWindow(draggedAndDroppedFile.getAbsolutePath());
					} else {
						frame = new MainWindow(null);
					}
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainWindow(String draggedAndDroppedFilePath) {
		try {
			battleStageNames = loadStringsFromFile("data/battle_stages.txt");
			enemyNames = loadStringsFromFile("data/enemy_names.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				tryToExit();
			}
		});

		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getRootPane().getActionMap();

		// Detect Esc key
		inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "escapePressed");
		actionMap.put("escapePressed", new AbstractAction() {

			private static final long serialVersionUID = -257796936417376333L;

			@Override
			public void actionPerformed(ActionEvent e) {
				tryToExit();
			}
		});

		// Detect Ctrl+S
		inputMap.put(KeyStroke.getKeyStroke("control S"), "ctrlSPressed");
		actionMap.put("ctrlSPressed", new AbstractAction() {

			private static final long serialVersionUID = 8103932125602944860L;

			@Override
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});

		// Detect Ctrl+O
		inputMap.put(KeyStroke.getKeyStroke("control O"), "ctrlOPressed");
		actionMap.put("ctrlOPressed", new AbstractAction() {

			private static final long serialVersionUID = 8153932125632944830L;

			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});

		new DropTarget(this, new DropTargetListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent event) {
				event.acceptDrop(DnDConstants.ACTION_COPY);
				try {
					Transferable transferable = event.getTransferable();
					List<File> droppedFiles = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
					if (!droppedFiles.isEmpty()) {
						File file = droppedFiles.get(0);
						if (file.getName().toLowerCase().endsWith(".out")) {
							readSceneOut(file.getAbsolutePath());
							System.out.println("Loaded file: " + file.getAbsolutePath());
						} else {
							JOptionPane.showMessageDialog(null, "Only .out files are supported.", "Invalid File", JOptionPane.WARNING_MESSAGE);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			public void dragEnter(DropTargetDragEvent dtde) {}

			public void dragOver(DropTargetDragEvent dtde) {}

			public void dropActionChanged(DropTargetDragEvent dtde) {}

			public void dragExit(DropTargetEvent dte) {}
		});

		setResizable(false);
		setSize(1610, 796);
		setLocationRelativeTo(null);
		setTitle(TITLE);
		ImageIcon imgIcon = new ImageIcon("data/jumbo_cactuar.png");
		setIconImage(imgIcon.getImage());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 76, 22);

		// File Menu
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem openItem = new JMenuItem("Open");
		fileMenu.add(openItem);

		JMenuItem saveItem = new JMenuItem("Save As...");
		fileMenu.add(saveItem);

		JMenuItem exitItem = new JMenuItem("Exit");
		fileMenu.add(exitItem);

		// About Menu
		JMenu aboutMenu = new JMenu("About");
		menuBar.add(aboutMenu);

		JMenuItem aboutItem = new JMenuItem("Information");
		aboutMenu.add(aboutItem);

		// Action Listeners
		openItem.addActionListener(e -> openFile());

		saveItem.addActionListener(e -> saveAs());

		exitItem.addActionListener(e -> tryToExit());

		aboutItem.addActionListener(e -> about());

		contentPane.add(menuBar);

		encIdSpinner = new JSpinner();
		encIdSpinner.setName("Encounter ID");
		encIdSpinner.setEnabled(false);
		JSpinner.NumberEditor encIdNumEditor = new JSpinner.NumberEditor(encIdSpinner, "#");
		encIdSpinner.setEditor(encIdNumEditor);
		encIdSpinner.setModel(new SpinnerNumberModel(0, 0, 1023, 1));
		encIdSpinner.setBounds(79, 32, 50, 20);
		enableSpinnerMouseWheel(encIdSpinner);
		enforceSpinnerBounds(encIdSpinner);
		contentPane.add(encIdSpinner);

		encIdLbl = new JLabel("Encounter ID:");
		encIdLbl.setEnabled(false);
		encIdLbl.setBounds(10, 35, 83, 14);
		contentPane.add(encIdLbl);

		loadBtn = new JButton("Load");
		loadBtn.setEnabled(false);
		loadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadEncounter(encs[(int) encIdSpinner.getValue()], true);
			}
		});
		loadBtn.setBounds(133, 31, 60, 23);
		contentPane.add(loadBtn);

		// Battle flags
		JPanel battleFlagsPanel = new JPanel();
		battleFlagsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Battle Flags", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		battleFlagsPanel.setBounds(10, 130, 183, 231);
		contentPane.add(battleFlagsPanel);
		battleFlagsPanel.setLayout(null);

		battleFlagsChkBoxArr = new JCheckBox[8];

		String[] battleFlagLabels = { "Disable Escape", "Disable Victory Fanfare", "Show Timer", "Disable EXP Gain", "Disable Post-Battle Screen", "Surprise Attack", "Back Attack",
				"Scripted Battle" };

		int checkboxYStart = 19;
		int checkboxHeight = 26; // Enough spacing between boxes
		int checkboxX = 6;
		int checkboxWidth = 160; // Wide enough for longest label

		for (int i = 0; i < battleFlagLabels.length; i++) {
			battleFlagsChkBoxArr[i] = new JCheckBox(battleFlagLabels[i]);
			battleFlagsChkBoxArr[i].setName("Battle Flags " + i);
			battleFlagsChkBoxArr[i].setEnabled(false);
			battleFlagsChkBoxArr[i].setBounds(checkboxX, checkboxYStart + (i * checkboxHeight), (i == 7) ? 100 : checkboxWidth, 23);
			battleFlagsPanel.add(battleFlagsChkBoxArr[i]);
		}

		battleStageLbl = new JLabel("Battle Stage:");
		battleStageLbl.setEnabled(false);
		battleStageLbl.setBounds(10, 90, 66, 14);
		contentPane.add(battleStageLbl);

		battleStageComboBox = new JComboBox<>();
		battleStageComboBox.setName("Battle Stage");
		battleStageComboBox.setEnabled(false);
		battleStageComboBox.setBounds(79, 86, 350, 22);

		for (String s : battleStageNames) {
			battleStageComboBox.addItem(s);
		}
		enableComboBoxMouseWheel(battleStageComboBox);
		contentPane.add(battleStageComboBox);

		battleFlagsPasteBtn = new JButton("P");
		battleFlagsPasteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadBattleFlags(copiedBattleFlags);
			}
		});
		battleFlagsPasteBtn.setMargin(new Insets(0, 0, 0, 0));
		battleFlagsPasteBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		battleFlagsPasteBtn.setEnabled(false);
		battleFlagsPasteBtn.setBounds(153, 200, 20, 20);
		battleFlagsPanel.add(battleFlagsPasteBtn);

		battleFlagsCopyBtn = new JButton("C");
		battleFlagsCopyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copiedBattleFlags = encs[(int) encIdSpinner.getValue()].getBattleFlags();
			}
		});
		battleFlagsCopyBtn.setMargin(new Insets(0, 0, 0, 0));
		battleFlagsCopyBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		battleFlagsCopyBtn.setEnabled(false);
		battleFlagsCopyBtn.setBounds(130, 200, 20, 20);
		battleFlagsPanel.add(battleFlagsCopyBtn);

		// Cameras
		JPanel mainCamPanel = new JPanel();
		mainCamPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Main Camera", TitledBorder.LEADING, TitledBorder.TOP,
				null, new Color(0, 0, 0)));
		mainCamPanel.setBounds(10, 372, 183, 110);
		contentPane.add(mainCamPanel);
		mainCamPanel.setLayout(null);

		mainCamIdLbl = new JLabel("Camera ID:");
		mainCamIdLbl.setEnabled(false);
		mainCamIdLbl.setBounds(10, 21, 63, 14);
		mainCamPanel.add(mainCamIdLbl);

		mainCamAnimLbl = new JLabel("Animation ID:");
		mainCamAnimLbl.setEnabled(false);
		mainCamAnimLbl.setBounds(10, 46, 75, 14);
		mainCamPanel.add(mainCamAnimLbl);

		mainCamIdSpinner = new JSpinner();
		mainCamIdSpinner.setModel(new SpinnerNumberModel(0, 0, 8, 1));
		enableSpinnerMouseWheel(mainCamIdSpinner);
		enforceSpinnerBounds(mainCamIdSpinner);
		mainCamIdSpinner.setName("Main Camera ID");
		mainCamIdSpinner.setEnabled(false);
		mainCamIdSpinner.setBounds(130, 18, 43, 20);
		mainCamPanel.add(mainCamIdSpinner);

		mainCamAnimSpinner = new JSpinner();
		mainCamAnimSpinner.setModel(new SpinnerNumberModel(0, 0, 7, 1));
		enableSpinnerMouseWheel(mainCamAnimSpinner);
		enforceSpinnerBounds(mainCamAnimSpinner);
		mainCamAnimSpinner.setName("Main Animation ID");
		mainCamAnimSpinner.setEnabled(false);
		mainCamAnimSpinner.setBounds(130, 43, 43, 20);
		mainCamPanel.add(mainCamAnimSpinner);

		mainCamCopyBtn = new JButton("C");
		mainCamCopyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copiedMainCam = encs[(int) encIdSpinner.getValue()].getMainCam();
				copiedMainCam = encs[(int) encIdSpinner.getValue()].getMainCamAnim();
			}
		});
		mainCamCopyBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		mainCamCopyBtn.setBounds(130, 79, 20, 20);
		mainCamCopyBtn.setMargin(new Insets(0, 0, 0, 0));
		mainCamPanel.add(mainCamCopyBtn);
		mainCamCopyBtn.setEnabled(false);

		mainCamPasteBtn = new JButton("P");
		mainCamPasteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadMainCamera(copiedMainCam, copiedMainCamAnim);
			}
		});
		mainCamPasteBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		mainCamPasteBtn.setEnabled(false);
		mainCamPasteBtn.setBounds(153, 79, 20, 20);
		mainCamPasteBtn.setMargin(new Insets(0, 0, 0, 0));
		mainCamPanel.add(mainCamPasteBtn);

		JPanel secCamPanel = new JPanel();
		secCamPanel.setLayout(null);
		secCamPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Secondary Camera", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		secCamPanel.setBounds(10, 493, 183, 110);
		contentPane.add(secCamPanel);

		secCamIdLbl = new JLabel("Camera ID:");
		secCamIdLbl.setEnabled(false);
		secCamIdLbl.setBounds(10, 21, 63, 14);
		secCamPanel.add(secCamIdLbl);

		secCamAnimLbl = new JLabel("Animation ID:");
		secCamAnimLbl.setEnabled(false);
		secCamAnimLbl.setBounds(10, 46, 75, 14);
		secCamPanel.add(secCamAnimLbl);

		secCamIdSpinner = new JSpinner();
		secCamIdSpinner.setModel(new SpinnerNumberModel(0, 0, 8, 1));
		enableSpinnerMouseWheel(secCamIdSpinner);
		enforceSpinnerBounds(secCamIdSpinner);
		secCamIdSpinner.setName("Secondary Camera ID");
		secCamIdSpinner.setEnabled(false);
		secCamIdSpinner.setBounds(130, 18, 43, 20);
		secCamPanel.add(secCamIdSpinner);

		secCamAnimSpinner = new JSpinner();
		secCamAnimSpinner.setModel(new SpinnerNumberModel(0, 0, 7, 1));
		enableSpinnerMouseWheel(secCamAnimSpinner);
		enforceSpinnerBounds(secCamAnimSpinner);
		secCamAnimSpinner.setName("Secondary Animation ID");
		secCamAnimSpinner.setEnabled(false);
		secCamAnimSpinner.setBounds(130, 43, 43, 20);
		secCamPanel.add(secCamAnimSpinner);

		secCamPasteBtn = new JButton("P");
		secCamPasteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSecondaryCamera(copiedSecCam, copiedSecCamAnim);
			}
		});
		secCamPasteBtn.setMargin(new Insets(0, 0, 0, 0));
		secCamPasteBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		secCamPasteBtn.setEnabled(false);
		secCamPasteBtn.setBounds(153, 79, 20, 20);
		secCamPanel.add(secCamPasteBtn);

		secCamCopyBtn = new JButton("C");
		secCamCopyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copiedSecCam = encs[(int) encIdSpinner.getValue()].getSecondaryCam();
				copiedSecCam = encs[(int) encIdSpinner.getValue()].getSecondaryCamAnim();
			}
		});
		secCamCopyBtn.setMargin(new Insets(0, 0, 0, 0));
		secCamCopyBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		secCamCopyBtn.setEnabled(false);
		secCamCopyBtn.setBounds(130, 79, 20, 20);
		secCamPanel.add(secCamCopyBtn);

		// Unknowns
		unknownsPanel = new UnknownsPanel(this);
		((AbstractDocument) unknownsPanel.unknown1TxFld.getDocument()).setDocumentFilter(new HexByteFilter(16));
		((AbstractDocument) unknownsPanel.unknown2TxFld.getDocument()).setDocumentFilter(new HexByteFilter(16));
		((AbstractDocument) unknownsPanel.unknown3TxFld.getDocument()).setDocumentFilter(new HexByteFilter(16));
		((AbstractDocument) unknownsPanel.unknown4TxFld.getDocument()).setDocumentFilter(new HexByteFilter(8));
		unknownsPanel.setAllEnabled(false);
		contentPane.add(unknownsPanel.panel);

		copyEncounterBtn = new JButton("Copy Encounter");
		copyEncounterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copiedEnc = encs[(int) encIdSpinner.getValue()];
			}
		});
		copyEncounterBtn.setEnabled(false);
		copyEncounterBtn.setBounds(203, 31, 111, 23);
		contentPane.add(copyEncounterBtn);

		pasteEncounterBtn = new JButton("Paste Encounter");
		pasteEncounterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (copiedEnc == null) return;
				loadEncounter(copiedEnc, false);
			}
		});
		pasteEncounterBtn.setEnabled(false);
		pasteEncounterBtn.setBounds(318, 31, 111, 23);
		contentPane.add(pasteEncounterBtn);

		battleStageCopyBtn = new JButton("C");
		battleStageCopyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copiedBattleStage = encs[(int) encIdSpinner.getValue()].getBattleStage();
			}
		});
		battleStageCopyBtn.setMargin(new Insets(0, 0, 0, 0));
		battleStageCopyBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		battleStageCopyBtn.setEnabled(false);
		battleStageCopyBtn.setBounds(439, 87, 20, 20);
		contentPane.add(battleStageCopyBtn);

		battleStagePasteBtn = new JButton("P");
		battleStagePasteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadBattleStage(copiedBattleStage);
			}
		});
		battleStagePasteBtn.setMargin(new Insets(0, 0, 0, 0));
		battleStagePasteBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		battleStagePasteBtn.setEnabled(false);
		battleStagePasteBtn.setBounds(462, 87, 20, 20);
		contentPane.add(battleStagePasteBtn);

		copySelectedEncountersBtn = new JButton("Copy Selected Encounters");
		copySelectedEncountersBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copiedEnemies = new ArrayList<>();
				for (int i = 0; i < enemySlots.size(); i++) {
					if (enemySlots.get(i).isSelected()) {
						enemySlots.get(i).selectedChkBox.setSelected(false);
						copiedEnemies.add(enemySlots.get(i).getEnemyData());
					}
				}
			}
		});
		copySelectedEncountersBtn.setEnabled(false);
		copySelectedEncountersBtn.setBounds(1249, 31, 162, 23);
		contentPane.add(copySelectedEncountersBtn);

		pasteSelectedEncountersBtn = new JButton("Paste Selected Encounters");
		pasteSelectedEncountersBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Integer> selectedSlots = new ArrayList<>();
				for (int i = 0; i < enemySlots.size(); i++) {
					if (enemySlots.get(i).isSelected()) selectedSlots.add(i);
				}

				int copiedCount = copiedEnemies.size();
				int selectedCount = selectedSlots.size();

				if (selectedCount != copiedCount) {
					JOptionPane.showMessageDialog(null, copiedCount + " slots were copied, please select " + copiedCount + " slots before pasting.", "Wrong amount of slots selected",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				for (int i = 0; i < copiedEnemies.size(); i++) {
					enemySlots.get(selectedSlots.get(i)).selectedChkBox.setSelected(false);
					loadEnemy(selectedSlots.get(i), copiedEnemies.get(i));
				}
			}
		});
		pasteSelectedEncountersBtn.setEnabled(false);
		pasteSelectedEncountersBtn.setBounds(1420, 31, 162, 23);
		contentPane.add(pasteSelectedEncountersBtn);

		// Enemy slots
		enemySlots = new ArrayList<>();
		int totalSlots = 8;
		int startX = 203;
		int startY = 130;
		int xSpacing = 348;
		int ySpacing = 242;

		for (int i = 0; i < totalSlots; i++) {
			int column = i % 4;
			int row = i / 4;
			int x = startX + column * xSpacing;
			int y = startY + row * ySpacing;

			EnemySlotPanel slot = new EnemySlotPanel(this, i, enemyNames);

			slot.slotPanel.setLocation(x, y);
			contentPane.add(slot.slotPanel);

			enableComboBoxMouseWheel(slot.enemyIdComboBox);
			enableSpinnerMouseWheel(slot.levelSpinner);
			enableSpinnerMouseWheel(slot.xSpinner);
			enableSpinnerMouseWheel(slot.ySpinner);
			enableSpinnerMouseWheel(slot.zSpinner);
			enforceSpinnerBounds(slot.levelSpinner);
			enforceSpinnerBounds(slot.xSpinner);
			enforceSpinnerBounds(slot.ySpinner);
			enforceSpinnerBounds(slot.zSpinner);

			slot.setAllEnabled(false); // Initially disabled
			enemySlots.add(slot); // Storing enemy slots for later use

			registerAllListeners(contentPane);
		}
		if (DEBUG) readSceneOut("data/scene.out");
		if (draggedAndDroppedFilePath != null) {
			currentFile = new File(draggedAndDroppedFilePath).getName();
			fileChanged = false;
			setTitle(TITLE + " | " + currentFile);
			readSceneOut(draggedAndDroppedFilePath);
		}
	}

	private void onComponentChanged(Component source) {
		String sourceName = source.getName();
		System.out.println(sourceName);
		Encounter enc = encs[(int) encIdSpinner.getValue()];

		if ("Encounter ID".equals(sourceName)) {
			loadEncounter(encs[(int) encIdSpinner.getValue()], true);
		} else if ("Battle Stage".equals(sourceName)) {
			fileChanged = true;
			enc.setBattleStage(battleStageComboBox.getSelectedIndex());
		} else if ("Main Camera ID".equals(sourceName)) {
			fileChanged = true;
			enc.setMainCam((int) mainCamIdSpinner.getValue());
		} else if ("Main Animation ID".equals(sourceName)) {
			fileChanged = true;
			enc.setMainCamAnim((int) mainCamAnimSpinner.getValue());
		} else if ("Secondary Camera ID".equals(sourceName)) {
			fileChanged = true;
			enc.setSecondaryCam((int) secCamIdSpinner.getValue());
		} else if ("Secondary Animation ID".equals(sourceName)) {
			fileChanged = true;
			enc.setSecondaryCamAnim((int) secCamAnimSpinner.getValue());
		} else if (sourceName.startsWith("Battle Flag")) {
			fileChanged = true;
			boolean[] flagsArr = new boolean[8];
			for (int i = 0; i < battleFlagsChkBoxArr.length; i++) {
				flagsArr[i] = battleFlagsChkBoxArr[i].isSelected();
			}
			enc.setBattleFlags(BitManipulator.packBooleansIntoByte(flagsArr));
		} else if (sourceName.startsWith("Enabled")) {
			fileChanged = true;
			int id = Integer.parseInt(sourceName.substring(sourceName.length() - 1, sourceName.length()));
			enc.setEnabledFlags(BitManipulator.setBit(enc.getEnabledFlags(), id, enemySlots.get(id).isEnabledFlag()));
		} else if (sourceName.startsWith("Not Targetable")) {
			fileChanged = true;
			int id = Integer.parseInt(sourceName.substring(sourceName.length() - 1, sourceName.length()));
			enc.setNotTargetableFlags(BitManipulator.setBit(enc.getNotTargetableFlags(), id, enemySlots.get(id).isNotTargetableFlag()));
		} else if (sourceName.startsWith("Not Visible")) {
			fileChanged = true;
			int id = Integer.parseInt(sourceName.substring(sourceName.length() - 1, sourceName.length()));
			enc.setNotVisibleFlags(BitManipulator.setBit(enc.getNotVisibleFlags(), id, enemySlots.get(id).isNotVisibleFlag()));
		} else if (sourceName.startsWith("Not Loaded")) {
			fileChanged = true;
			int id = Integer.parseInt(sourceName.substring(sourceName.length() - 1, sourceName.length()));
			enc.setNotLoadedFlags(BitManipulator.setBit(enc.getNotLoadedFlags(), id, enemySlots.get(id).isNotLoadedFlag()));
		} else if (sourceName.startsWith("ID")) {
			fileChanged = true;
			int index = Integer.parseInt(sourceName.substring(sourceName.length() - 1, sourceName.length()));
			enc.setEnemy(index, enemySlots.get(index).getEnemyId());
		} else if (sourceName.startsWith("Level")) {
			fileChanged = true;
			int index = Integer.parseInt(sourceName.substring(sourceName.length() - 1, sourceName.length()));
			enc.setLevel(index, enemySlots.get(index).getLevel());
		} else if (sourceName.startsWith("x") || sourceName.startsWith("y") || sourceName.startsWith("z")) {
			fileChanged = true;
			for (int i = 0; i < enemySlots.size(); i++) {
				enc.setPosition(i, enemySlots.get(i).getPosition());
			}
		} else if (sourceName.startsWith("Unknown")) {
			int unk1Length = unknownsPanel.unknown1TxFld.getText().length();
			int unk2Length = unknownsPanel.unknown2TxFld.getText().length();
			int unk3Length = unknownsPanel.unknown3TxFld.getText().length();
			int unk4Length = unknownsPanel.unknown4TxFld.getText().length();
			if (unk1Length == 47 && unk2Length == 47 && unk3Length == 47 && unk4Length == 23) {
				fileChanged = true;
				String[] unknownValues = { unknownsPanel.unknown1TxFld.getText(), unknownsPanel.unknown2TxFld.getText(), unknownsPanel.unknown3TxFld.getText(),
						unknownsPanel.unknown4TxFld.getText() };
				List<Integer> intValues = new ArrayList<>();
				for (String unknownVals : unknownValues) {
					String[] hexBytes = unknownVals.trim().split(" ");

					for (int i = 0; i < hexBytes.length; i++) {
						String hexByte = hexBytes[i];
						intValues.add(Integer.parseInt(hexByte, 16));
					}
				}
				int[] intArray = intValues.stream().mapToInt(Integer::intValue).toArray();
				enc.setUnknowns((intArray));
			}
		}
		if (fileChanged) {
			setTitle(TITLE + " | *" + currentFile);
		}
	}

	private void loadBattleFlags(byte battleFlags) {
		for (int i = 0; i < battleFlagsChkBoxArr.length; i++) {
			int mask = 1 << (7 - i); // Automatically calculate: 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01
			battleFlagsChkBoxArr[i].setSelected((battleFlags & mask) != 0);
		}
	}

	private void loadBattleStage(int battleStage) {
		battleStageComboBox.setSelectedIndex(battleStage);
	}

	private void loadMainCamera(int camId, int camAnim) {
		mainCamIdSpinner.setValue(camId);
		mainCamAnimSpinner.setValue(camAnim);
	}

	private void loadSecondaryCamera(int camId, int camAnim) {
		secCamIdSpinner.setValue(camId);
		secCamAnimSpinner.setValue(camAnim);
	}

	public void loadEnemy(int index, EnemyData enemyData) {
		EnemySlotPanel slot = enemySlots.get(index);

		// Set enemy ID and Level
		slot.setEnemyId(enemyData.getId());
		slot.setLevel(enemyData.getLevel());

		// Set flags
		slot.setEnabledFlag(enemyData.isEnabled());
		slot.setNotTargetableFlag(enemyData.isNotTargetable());
		slot.setNotVisibleFlag(enemyData.isNotVisible());
		slot.setNotLoadedFlag(enemyData.isNotLoaded());

		// Set position
		slot.setPosition(enemyData.getPosition().getX(), enemyData.getPosition().getY(), enemyData.getPosition().getZ());

	}

	public void loadUnknowns(int[] unknowns) {
		String unk1 = "";
		for (int i = 0; i < 16; i++) {
			unk1 += (unknowns[i] < 16) ? "0" + Integer.toHexString(unknowns[i]) : Integer.toHexString(unknowns[i]);
		}
		String unk2 = "";
		for (int i = 16; i < 32; i++) {
			unk2 += (unknowns[i] < 16) ? "0" + Integer.toHexString(unknowns[i]) : Integer.toHexString(unknowns[i]);
		}
		String unk3 = "";
		for (int i = 32; i < 48; i++) {
			unk3 += (unknowns[i] < 16) ? "0" + Integer.toHexString(unknowns[i]) : Integer.toHexString(unknowns[i]);
		}
		String unk4 = "";
		for (int i = 48; i < 56; i++) {
			unk4 += (unknowns[i] < 16) ? "0" + Integer.toHexString(unknowns[i]) : Integer.toHexString(unknowns[i]);
		}
		unknownsPanel.setUnknown1(unk1);
		unknownsPanel.setUnknown2(unk2);
		unknownsPanel.setUnknown3(unk3);
		unknownsPanel.setUnknown4(unk4);
	}

	private void loadEncounter(Encounter enc, boolean suppressEvents) {
		suppressComponentChangedEvents = suppressEvents;

		loadBattleFlags(enc.getBattleFlags());
		loadBattleStage(enc.getBattleStage());
		loadMainCamera(enc.getMainCam(), enc.getMainCamAnim());
		loadSecondaryCamera(enc.getSecondaryCam(), enc.getSecondaryCamAnim());
		for (int i = 0; i < enemySlots.size(); i++) {
			boolean[] enabledFlags = BitManipulator.unpackByteIntoBooleans(enc.getEnabledFlags());
			boolean[] notTargetableFlags = BitManipulator.unpackByteIntoBooleans(enc.getNotTargetableFlags());
			boolean[] notVisibleFlags = BitManipulator.unpackByteIntoBooleans(enc.getNotVisibleFlags());
			boolean[] notLoadedFlags = BitManipulator.unpackByteIntoBooleans(enc.getNotLoadedFlags());
			loadEnemy(i, new EnemyData(enc.getEnemy(i), enc.getLevel(i), enabledFlags[i], notTargetableFlags[i], notVisibleFlags[i], notLoadedFlags[i], enc.getPosition(i)));
		}
		loadUnknowns(enc.getUnknowns());

		suppressComponentChangedEvents = false;
	}

	private void readSceneOut(String fname) {
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(fname, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		encs = new Encounter[ENCOUNTERS_COUNT];
		for (int i = 0; i < ENCOUNTERS_COUNT; i++) {
			byte[] encData = new byte[ENCOUNTER_SIZE];
			try {
				file.seek(i * ENCOUNTER_SIZE);
				file.readFully(encData);
				encs[i] = new Encounter(encData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (JCheckBox b : battleFlagsChkBoxArr) {
			b.setEnabled(true);
		}

		currentFile = fname;
		fileChanged = false;
		setTitle(TITLE + " | " + currentFile);

		// Encounter ID stuff
		encIdLbl.setEnabled(true);
		encIdSpinner.setEnabled(true);
		loadBtn.setEnabled(true);

		// Battle Stage stuff
		battleStageLbl.setEnabled(true);
		battleStageComboBox.setEnabled(true);

		// Camera stuff
		mainCamIdLbl.setEnabled(true);
		mainCamAnimLbl.setEnabled(true);

		secCamIdLbl.setEnabled(true);
		secCamAnimLbl.setEnabled(true);

		mainCamIdSpinner.setEnabled(true);
		mainCamAnimSpinner.setEnabled(true);

		secCamIdSpinner.setEnabled(true);
		secCamAnimSpinner.setEnabled(true);

		// Slots
		for (EnemySlotPanel slot : enemySlots) {
			slot.setAllEnabled(true);
		}

		// Unknowns
		unknownsPanel.setAllEnabled(true);

		// Copy/Paste
		copyEncounterBtn.setEnabled(true);
		pasteEncounterBtn.setEnabled(true);
		battleStageCopyBtn.setEnabled(true);
		battleStagePasteBtn.setEnabled(true);
		battleFlagsCopyBtn.setEnabled(true);
		battleFlagsPasteBtn.setEnabled(true);
		mainCamCopyBtn.setEnabled(true);
		mainCamPasteBtn.setEnabled(true);
		secCamCopyBtn.setEnabled(true);
		secCamPasteBtn.setEnabled(true);
		copySelectedEncountersBtn.setEnabled(true);
		pasteSelectedEncountersBtn.setEnabled(true);

		loadEncounter(encs[(int) encIdSpinner.getValue()], true);
	}

	public static void writeAllUnknownsToFile(Encounter[] encs, String filename) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			for (int encId = 0; encId < encs.length; encId++) {
				Encounter enc = encs[encId];
				int[] values = enc.getUnknowns();

				writer.write("Encounter ID " + encId + ":");
				writer.newLine();

				for (int i = 0; i < values.length; i++) {
					writer.write(Integer.toString(values[i]));
					if ((i + 1) % 16 == 0 || (i + 1) % 56 == 0) {
						writer.newLine();
					} else {
						writer.write(", ");
					}
				}

				// New line between encs
				writer.newLine();
			}
			System.out.println("Unknowns successfully written to " + filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeAllCamsToFile(Encounter[] encs, String filename) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			for (int encId = 0; encId < encs.length; encId++) {
				Encounter enc = encs[encId];

				writer.write("Encounter ID " + encId + ":\n");
				writer.write("Main: " + enc.getMainCam() + ", " + enc.getMainCamAnim() + " | Secondary: " + enc.getSecondaryCam() + ", " + enc.getSecondaryCamAnim() + "\n");

				// New line between encs
				writer.newLine();
			}
			System.out.println("Unknowns successfully written to " + filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> loadStringsFromFile(String fname) throws IOException {
		ArrayList<String> strings = new ArrayList<String>();
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader(fname));
			int i = 0;
			String line = reader.readLine();

			while (line != null) {
				String id = "";
				if (i < 10) id = "  " + i;
				else if (i < 99) id += " " + i;
				else id = "" + i;
				strings.add(id + " - " + line);
				line = reader.readLine();
				i++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strings;
	}

	public static void enableComboBoxMouseWheel(JComboBox<?> comboBox) {
		comboBox.addMouseWheelListener(e -> {
			int index = comboBox.getSelectedIndex() + e.getWheelRotation();
			if (index >= 0 && index < comboBox.getItemCount()) comboBox.setSelectedIndex(index);
		});
	}

	public static void enableSpinnerMouseWheel(JSpinner spinner) {
		spinner.addMouseWheelListener(e -> {
			int rotation = e.getWheelRotation();
			SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
			int step = model.getStepSize().intValue();
			int newValue = ((Number) model.getValue()).intValue() - rotation * step;
			int min = ((Number) model.getMinimum()).intValue();
			int max = ((Number) model.getMaximum()).intValue();
			if (newValue >= min && newValue <= max) model.setValue(newValue);
		});
	}

	public static void enforceSpinnerBounds(JSpinner spinner) {
		SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
		JFormattedTextField textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();

		// When focus is lost or Enter is pressed, commit the value and clamp it
		textField.addActionListener(e -> validateAndClamp(spinner, model));
		textField.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusLost(java.awt.event.FocusEvent e) {
				validateAndClamp(spinner, model);
			}
		});
	}

	private static void validateAndClamp(JSpinner spinner, SpinnerNumberModel model) {
		try {
			spinner.commitEdit();
			Number val = (Number) model.getValue();
			int value = val.intValue();
			int min = ((Number) model.getMinimum()).intValue();
			int max = ((Number) model.getMaximum()).intValue();

			if (value < min) model.setValue(min);
			else if (value > max) model.setValue(max);
		} catch (ParseException ex) {
			// Revert to last valid value
			Toolkit.getDefaultToolkit().beep();
			Object lastValid = spinner.getValue();
			model.setValue(lastValid);
		}
	}

	private void registerAllListeners(Container container) {
		for (Component comp : container.getComponents()) {
			if (comp instanceof JCheckBox) {
				JCheckBox cb = (JCheckBox) comp;
				cb.addItemListener(e -> {
					if (!suppressComponentChangedEvents) {
						onComponentChanged(cb);
					}
				});
			} else if (comp instanceof JComboBox) {
				JComboBox<?> combo = (JComboBox<?>) comp;
				combo.addActionListener(e -> {
					if (!suppressComponentChangedEvents) {
						onComponentChanged(combo);
					}
				});
			} else if (comp instanceof JSpinner) {
				JSpinner spinner = (JSpinner) comp;
				spinner.addChangeListener(e -> {
					if (!suppressComponentChangedEvents) {
						onComponentChanged(spinner);
					}
				});
			} else if (comp instanceof JTextField) {
				JTextField text = (JTextField) comp;
				((AbstractDocument) text.getDocument()).addDocumentListener(new DocumentListener() {
					private void handle() {
						if (!suppressComponentChangedEvents) {
							onComponentChanged(text);
						}
					}

					public void insertUpdate(DocumentEvent e) {
						handle();
					}

					public void removeUpdate(DocumentEvent e) {
						handle();
					}

					public void changedUpdate(DocumentEvent e) {
						handle();
					}
				});
			} else if (comp instanceof Container) {
				Container childContainer = (Container) comp;
				registerAllListeners(childContainer); // Recurse into nested panels
			}
		}
	}

	// DocumentFilter for hex editing fields
	static class HexByteFilter extends DocumentFilter {

		private int bytes, maxLength;

		public HexByteFilter(int bytes) {
			this.bytes = bytes;
			maxLength = bytes * 2 + bytes - 1;
		}

		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			if (string == null) return;
			replace(fb, offset, 0, string, attr);
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder sb = new StringBuilder(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text.toUpperCase());

			String formatted = formatHexInput(sb.toString());

			if (formatted != null && formatted.length() <= maxLength) {
				fb.replace(0, doc.getLength(), formatted, attrs);
			}
		}

		private String formatHexInput(String input) {
			input = input.replaceAll("[^0-9A-Fa-f]", ""); // Strip non-hex
			input = input.toUpperCase();

			if (input.length() > bytes * 2) return null; // A byte is 2 chars

			StringBuilder result = new StringBuilder();
			for (int i = 0; i < input.length(); i += 2) {
				if (i > 0) result.append(" ");
				result.append(input.charAt(i));
				if (i + 1 < input.length()) result.append(input.charAt(i + 1));
			}
			return result.toString();
		}
	}

	private void saveAs() {
		if (currentFile == null) return;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(new File(currentFile));

		// Set the file selection mode to allow saving files (not directories)
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // Only allow selecting files for saving

		// Create and set a file filter for .out files
		FileNameExtensionFilter outFilter = new FileNameExtensionFilter("FF8 .out Files (*.out)", "out");
		fileChooser.addChoosableFileFilter(outFilter);
		fileChooser.setFileFilter(outFilter); // Set as the default filter

		int userSelection = fileChooser.showSaveDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			String filePath = fileToSave.getAbsolutePath();

			if (!filePath.toLowerCase().endsWith(".out")) {
				filePath += ".out";
				fileToSave = new File(filePath); // Update fileToSave with the new path
			}

			// Check if the file already exists and prompt the user
			if (fileToSave.exists()) {
				int confirmOverwrite = JOptionPane.showConfirmDialog(this, "File '" + fileToSave.getName() + "' already exists.\nDo you want to overwrite it?", "Confirm Overwrite",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

				if (confirmOverwrite == JOptionPane.NO_OPTION) {
					System.out.println("Save operation cancelled by user (did not overwrite).");
					return;
				}
			}
			byte[] dataToSave = new byte[ENCOUNTERS_COUNT * ENCOUNTER_SIZE];
			int offset = 0; // To keep track of the current position in dataToSave

			for (Encounter enc : encs) {
				byte[] encounterBytes = enc.toBytes(); // Get the 128 bytes for the current encounter
				System.arraycopy(encounterBytes, 0, dataToSave, offset, ENCOUNTER_SIZE);
				offset += ENCOUNTER_SIZE; // Move the offset to the next available position
			}
			try (FileOutputStream out = new FileOutputStream(fileToSave)) {
				out.write(dataToSave);
				fileChanged = false;
				setTitle(TITLE + " | " + currentFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void tryToExit() {
		if (!fileChanged) System.exit(0);
		else {
			int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit without saving?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}

	private void about() {
		// Create a JEditorPane to display HTML content
		JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html"); // Crucial: tell it to render HTML
		editorPane.setEditable(false); // Make it read-only
		editorPane.setBackground(UIManager.getColor("OptionPane.background")); // Match dialog background

		// The HTML content with your link
		String htmlMessage = "<html><head><style>" + //
				"body { " + //
				"  font-family: Arial, sans-serif; /* Preferred font, fallback to generic sans-serif */" + //
				"  font-size: 13pt; /* Adjust font size */" + //
				"  color: #333333; /* Dark grey text color */" + //
				"} " + //
				"a { " + //
				"  color: #0000FF; /* Link color */" + //
				"  text-decoration: underline; /* Underline links */" + //
				"} " + //
				"</style></head><body>" + //
				"<h3>Made by Nihil</h3>" + //
				"CTRL+O: Open<br>" + //
				"CTRL+S: Save As<br>" + //
				"ESC: Exit<br><br>" + //
				"Dragging and dropping to load .out files is supported,<br>" + //
				"both in window and directly on the .exe when opening the program.<br><br>" + //
				"Use the C buttons to copy parts of encounter data,<br>" + //
				"then use the P buttons to paste it into another encounter.<br><br>" + //
				"<a href=\"https://hobbitdur.github.io/FF8ModdingWiki/technical-reference/battle/encounter-codes/\">Encounters list</a><br>" + //
				"<a href=\"https://hobbitdur.github.io/FF8ModdingWiki/technical-reference/battle/battle-structure-sceneout/\">Info on scene.out's format</a>" + //
				"</body></html>";//
		editorPane.setText(htmlMessage);

		// Add a HyperlinkListener to handle clicks on the links
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						// Attempt to open the link in the default browser
						if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} else {
							// Fallback for systems where Desktop is not supported
							JOptionPane.showMessageDialog(editorPane, "Cannot open link: " + e.getURL(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					} catch (IOException | URISyntaxException ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(editorPane, "Error opening link: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		// Show the JEditorPane in the JOptionPane
		JOptionPane.showMessageDialog(this, editorPane, "About", JOptionPane.INFORMATION_MESSAGE);
	}

	private void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(new File("scene.out"));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		FileNameExtensionFilter outFilter = new FileNameExtensionFilter("FF8 .out Files (*.out)", "out");
		fileChooser.addChoosableFileFilter(outFilter);
		fileChooser.setFileFilter(outFilter); // Set it as the default filter

		int userSelection = fileChooser.showOpenDialog(this); // Adjust this line based on 'this' context

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			// Check if the selected item is a directory or a file
			if (selectedFile.isDirectory()) {
				JOptionPane.showMessageDialog(null, "Please select an .out file, not a directory.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
			} else if (selectedFile.getName().toLowerCase().endsWith(".out")) {
				readSceneOut(selectedFile.getAbsolutePath());
			} else {
				JOptionPane.showMessageDialog(null, "Only .out files are supported.", "Invalid File", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public Encounter getCopiedEnc() {
		return copiedEnc;
	}

	public int[] getCopiedUnknowns() {
		return copiedUnknowns;
	}

	public void setCopiedEnc(Encounter copiedEnc) {
		this.copiedEnc = copiedEnc;
	}

	public void setCopiedMainCamAnim(int copiedMainCamAnim) {
		this.copiedMainCamAnim = copiedMainCamAnim;
	}

	public Encounter getCurrentEncounter() {
		return encs[(int) encIdSpinner.getValue()];
	}

	public void setCopiedUnknowns(int[] copiedUnknowns) {
		this.copiedUnknowns = copiedUnknowns;
	}

	public EnemyData getCopiedEnemy() {
		return copiedEnemy;
	}

	public void setCopiedEnemy(EnemyData copiedEnemy) {
		this.copiedEnemy = copiedEnemy;
	}

}
