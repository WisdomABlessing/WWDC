package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import etc.Constants;
import etc.DPSGraphPanel;
import etc.TTKGraphPanel;
import etc.TTKNamePair;
import damage.Damage;
import damage.SurfaceDamage;
import etc.UIBuilder;
import mods.Mod;
import mods.ModManagerPanel;
import ttk.TTKManagerPanel;
import ttk.TTKTarget;
import weapons.ArcGunPanel;
import weapons.PistolPanel;
import weapons.RiflePanel;
import weapons.ShotgunPanel;
import weapons.WeaponManagerPanel;
import weapons.WeaponPanel;
import weapons.DPSPanel;
import options.ColorOptionsPanel;
import Maximizer.Maximizer;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main {

	/**
	 * ____________________________________________________________ GLOBAL VARIABLES
	 * ____________________________________________________________
	 */

	/** JFrames **/
	protected static JFrame mainFrame = new JFrame();
	protected static JFrame modManagerFrame = new JFrame();
	protected static JFrame targetManagerFrame = new JFrame();
	protected static JFrame weaponManagerFrame = new JFrame();
	protected static JFrame colorOptionsFrame = new JFrame();

	/** JButtons **/
	protected static JButton calculateButton = new JButton("Calculate");
	// protected static JButton statsButton = new JButton("Hide/Show Calculated
	// Stats");
	protected static JButton clearOutputButton = new JButton("Clear Output");
	protected static JButton maximizeButton = new JButton("Maximize");

	protected static JLabel TTKIterationsLabel = new JLabel("Iterations:");
	protected static JTextField TTKIterationsField = new JTextField(4);

	/** JTextAreas **/
	public static JTextArea output = new JTextArea();

	/** JTabbedPanes **/
	protected static JTabbedPane weaponPane = new JTabbedPane();
	protected static JTabbedPane graphPane = new JTabbedPane();

	/** JScrollPanes **/
	protected static JScrollPane outputScroll = new JScrollPane(output);

	/** JPanels **/
	protected static JPanel mainPanel = new JPanel();
	protected static JPanel secondaryPanel = new JPanel();
	protected static JPanel outerPanel = new JPanel();
	protected static JPanel deepsPanel = new JPanel();
	protected static JPanel topPanel = new JPanel();
	protected static JPanel bottomPanel = new JPanel();
	protected static RiflePanel riflePanel;
	protected static ShotgunPanel shotgunPanel;
	protected static PistolPanel pistolPanel;
	protected static ArcGunPanel arcGunPanel;
	protected static ModManagerPanel theModManager = null;
	public static TTKManagerPanel theTTKManager = null;
	protected static Maximizer theMaximizer = null;
	protected static WeaponManagerPanel theWeaponManager = null;
	protected static ColorOptionsPanel theColorPanel = null;
	protected static DPSGraphPanel dpsGraph = new DPSGraphPanel();
	protected static TTKGraphPanel ttkGraph = new TTKGraphPanel();
	protected static DPSPanel DPSPanel = new DPSPanel();

	/** JMenuBar **/
	protected static JMenuBar mainMenuBar = new JMenuBar();

	/** JMenuItems **/
	protected static JMenu fileMenu = new JMenu("File");
	protected static JMenuItem modMenu = new JMenuItem("Mod Manager");
	protected static JMenuItem TTKMenu = new JMenuItem("Target Manager");
	protected static JMenuItem weaponMenu = new JMenuItem("Weapon Manager");
	protected static JMenuItem saveItem = new JMenuItem("Save");
	protected static JMenuItem loadItem = new JMenuItem("Load");
	protected static JMenuItem colorOptionsItem = new JMenuItem("Color Options");

	/** JFileChoosers **/
	protected static JFileChooser chooser = new JFileChooser();

	/** Misc UI Components **/
	protected static MainActionListener action = new MainActionListener();
	protected static MainChangeListener change = new MainChangeListener();
	protected static MainWindowListener window = new MainWindowListener();
	protected static Boolean modManagerInit = false;
	protected static Boolean targetManagerInit = false;
	protected static Boolean weaponManagerInit = false;
	protected static Boolean colorOptionsInit = false;
	protected static JCheckBox TTKBox = new JCheckBox("TTK");
	protected static JCheckBox lightWeightTTKBox = new JCheckBox("Lightweight TTK");
	protected static JLabel targetGroupLabel = new JLabel("Group:");
	public static JComboBox targetGroupBox = new JComboBox();
	protected static JLabel corrosiveProjectionLabel = new JLabel("CP Count:");
	protected static JComboBox corrosiveProjectionBox = new JComboBox();
	protected static JCheckBox headShots = new JCheckBox("Headshots");

	/** Data **/

	/** Selected WeaponPanel **/
	public static WeaponPanel selectedWeapon = null;

	/** Mod Vectors **/
	protected static Vector<Mod> activeMods = new Vector<Mod>();
	protected static Vector<Double> modRanks = new Vector<Double>();

	/** Base Values **/
	protected static boolean useComplexTTK = true;
	public static int complexTTKIterations = 10000;
	public static int complexTTKCompletions = 0;
	public static String longestTTKName = "";
	// protected static int maxTTKTime = 300000;

	public static String weaponName = "";
	public static String weaponMode = "";
	protected static String damageType = "";
	protected static double chargeTime = 0.0;
	public static double fireRate = 0.0;
	protected static double reloadTime = 0.0;
	protected static double critChance = 0.0;
	protected static double critMult = 0.0;
	public static double projectileCount = 0.0;
	protected static double firstShotDamageMult = 1.0;
	protected static double lastShotDamageMult = 1.0;
	protected static double statusChance = 0.0;
	protected static double statusDuration = 1.0;
	protected static double damageMult = 1.0;
	protected static double deadAimMult = 1.0;
	protected static double flatDamageBonus = 0.0;
	protected static int mag = 0;
	public static int combo = 0;
	public static double startingCombo = 0;
	public static int burstCount = 0;
	public static double drain = 1;

	/** Calculated Values **/
	public static int finalMag = 0;
	protected static int finalAmmo = 0;
	protected static double finalIterationTime = 0.0;
	protected static double finalIterationsPerMinute = 0.0;
	protected static double averageCritMult = 0.0;
	public static double finalCritChance = 0.0;
	public static double finalCritMult = 0.0;
	public static double finalFireRate = 0.0;
	public static double finalReloadTime = 0.0;
	public static double finalProjectileCount = 0.0;
	public static double finalFirstShotDamageMult = 1.0;
	public static double finalLastShotDamageMult = 1.0;
	public static double finalStatusChance = 0.0;
	public static double finalStatusDuration = 1.0;
	public static double finalDamageMult = 1.0;
	public static double finalDeadAimMult = 1.0;
	protected static double finalFlatDamageBonus = 0.0;
	public static double finalCorpusMult = 1.0;
	public static double finalGrineerMult = 1.0;
	public static double finalInfestedMult = 1.0;
	public static double averageProjectileCount = 1;

	/** Damage/DPS Values **/
	// Misc Values
	protected static double procsPerSecond = 0.0;
	protected static double burstProcsPerSecond = 0.0;
	protected static double slashStacks = 0;
	protected static double fireStacks = 0;
	protected static double toxinStacks = 0;
	protected static double gasStacks = 0;

	public static Damage raw = new Damage();
	public static Damage impact = new Damage();
	public static Damage puncture = new Damage();
	public static Damage slash = new Damage();
	public static Damage fire = new Damage();
	public static Damage ice = new Damage();
	public static Damage electric = new Damage();
	public static Damage toxin = new Damage();
	public static Damage blast = new Damage();
	public static Damage magnetic = new Damage();
	public static Damage gas = new Damage();
	public static Damage radiation = new Damage();
	public static Damage corrosive = new Damage();
	public static Damage viral = new Damage();
	public static SurfaceDamage corpus = new SurfaceDamage();
	public static SurfaceDamage grineer = new SurfaceDamage();
	public static SurfaceDamage infested = new SurfaceDamage();
	public static SurfaceDamage cloneFlesh = new SurfaceDamage();
	public static SurfaceDamage ferrite = new SurfaceDamage();
	public static SurfaceDamage alloy = new SurfaceDamage();
	public static SurfaceDamage mechanical = new SurfaceDamage();
	public static SurfaceDamage corpusFlesh = new SurfaceDamage();
	public static SurfaceDamage shield = new SurfaceDamage();
	public static SurfaceDamage protoShield = new SurfaceDamage();
	public static SurfaceDamage robotic = new SurfaceDamage();
	public static SurfaceDamage infestedFlesh = new SurfaceDamage();
	public static SurfaceDamage fossilized = new SurfaceDamage();
	public static SurfaceDamage sinew = new SurfaceDamage();

	public static double globalToxin;
	public static double globalFire;
	public static double globalElectric;
	public static double fireRateModPower;
	public static double hunterMunitions;
	public static double vigilante;
	public static boolean headShot = false;
	public static Random rng = new Random();
	public static double bleedDoTDPS;
	public static double poisonDoTDPS;
	public static double heatDoTDPS;
	public static double cloudDoTDPS;
	public static double electricProcDPS;
	public static double gasProcDPS;
	public static boolean updateOutput;

	public static boolean setup = true;

	/**
	 * ____________________________________________________________ METHODS
	 * ____________________________________________________________
	 */

	/**
	 * Main Method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		UIBuilder.UIInit();
		riflePanel = new RiflePanel();
		shotgunPanel = new ShotgunPanel();
		pistolPanel = new PistolPanel();
		arcGunPanel = new ArcGunPanel();
		theModManager = new ModManagerPanel(riflePanel, shotgunPanel, pistolPanel, arcGunPanel);
		theTTKManager = new TTKManagerPanel();
		theWeaponManager = new WeaponManagerPanel(riflePanel, shotgunPanel, pistolPanel, arcGunPanel);
		theColorPanel = new ColorOptionsPanel();
		theMaximizer = new Maximizer();
		buildUI();
		mainFrame.setVisible(true);
		setup = false;
	}

	/**
	 * Builds the frame UI
	 */
	public static void buildUI() {
		UIBuilder.panelInit(mainPanel);
		UIBuilder.panelInit(secondaryPanel);
		UIBuilder.panelInit(outerPanel);
		UIBuilder.panelInit(deepsPanel);
		UIBuilder.panelInit(topPanel);
		UIBuilder.panelInit(bottomPanel);
		UIBuilder.panelInit(riflePanel);
		UIBuilder.panelInit(shotgunPanel);
		UIBuilder.panelInit(pistolPanel);
		UIBuilder.panelInit(arcGunPanel);
		UIBuilder.buttonInit(calculateButton);
		UIBuilder.buttonInit(maximizeButton);
		UIBuilder.labelInit(TTKIterationsLabel);
		UIBuilder.numberFieldInit(TTKIterationsField);
		// UIBuilder.buttonInit(statsButton);
		UIBuilder.buttonInit(clearOutputButton);
		UIBuilder.textAreaInit(output);
		UIBuilder.scrollPaneInit(outputScroll);
		UIBuilder.tabbedPaneInit(weaponPane);
		UIBuilder.tabbedPaneInit(graphPane);
		UIBuilder.menuBarInit(mainMenuBar);
		UIBuilder.menuInit(fileMenu);
		UIBuilder.menuItemInit(modMenu);
		UIBuilder.menuItemInit(TTKMenu);
		UIBuilder.menuItemInit(weaponMenu);
		UIBuilder.menuItemInit(saveItem);
		UIBuilder.menuItemInit(loadItem);
		UIBuilder.menuItemInit(colorOptionsItem);
		UIBuilder.fileChooserInit(chooser);
		UIBuilder.checkBoxInit(TTKBox);
		UIBuilder.checkBoxInit(headShots);
		UIBuilder.checkBoxInit(lightWeightTTKBox);
		UIBuilder.labelInit(corrosiveProjectionLabel);
		UIBuilder.labelInit(targetGroupLabel);
		UIBuilder.comboBoxInit(corrosiveProjectionBox);
		UIBuilder.comboBoxInit(targetGroupBox);

		corrosiveProjectionBox.setPrototypeDisplayValue("XX");
		targetGroupBox.setPrototypeDisplayValue("XX");

		for (int i = 0; i < 10; i++) {
			targetGroupBox.addItem("" + i);
		}

		for (int i = 0; i < 5; i++) {
			corrosiveProjectionBox.addItem("" + i);
		}

		try {
			File currentDirectory = new File(".");
			chooser.setCurrentDirectory(currentDirectory);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		calculateButton.addActionListener(action);
		maximizeButton.addActionListener(action);
		TTKBox.addActionListener(action);
		lightWeightTTKBox.addActionListener(action);
		// statsButton.addActionListener(action);
		clearOutputButton.addActionListener(action);
		saveItem.addActionListener(action);
		loadItem.addActionListener(action);
		modMenu.addActionListener(action);
		TTKMenu.addActionListener(action);
		weaponMenu.addActionListener(action);
		colorOptionsItem.addActionListener(action);
		weaponPane.addChangeListener(change);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		secondaryPanel.setLayout(new BoxLayout(secondaryPanel, BoxLayout.X_AXIS));
		outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
		deepsPanel.setLayout(new BoxLayout(deepsPanel, BoxLayout.Y_AXIS));
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

		weaponPane.add(riflePanel, Constants.RIFLE);
		weaponPane.add(shotgunPanel, Constants.SHOTGUN);
		weaponPane.add(pistolPanel, Constants.PISTOL);
		weaponPane.add(arcGunPanel, Constants.ARCGUN);

		graphPane.add(dpsGraph, "DPS");
		graphPane.add(ttkGraph, "TTK");

		JPanel buttonPanel = new JPanel();
		UIBuilder.panelInit(buttonPanel);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(corrosiveProjectionLabel);
		buttonPanel.add(corrosiveProjectionBox);
		buttonPanel.add(targetGroupLabel);
		buttonPanel.add(targetGroupBox);
		buttonPanel.add(TTKBox);
		buttonPanel.add(TTKIterationsLabel);
		buttonPanel.add(TTKIterationsField);
		buttonPanel.add(lightWeightTTKBox);
		buttonPanel.add(headShots);
		buttonPanel.add(calculateButton);
		buttonPanel.add(maximizeButton);
		buttonPanel.add(clearOutputButton);
		// buttonPanel.add(statsButton);

		headShots.setToolTipText("Calcualtes TTK as if you are getting only headshots. Not related to effects triggered by headshots.");
		corrosiveProjectionLabel.setToolTipText("Number of Corrosive Projection auras active.");
		corrosiveProjectionBox.setToolTipText("Number of Corrosive Projection auras active.");
		targetGroupLabel.setToolTipText("Target group to run calculations against.");
		targetGroupBox.setToolTipText("Target group to run calculations against.");
		TTKBox.setToolTipText("Warning: This will cause a significantly performance hit compared to not running TTK.");
		TTKIterationsField.setToolTipText("Set the number of TTK simulation iterations. 10000 by defautl, 1000 for lightweight TTK.");
		TTKIterationsLabel.setToolTipText("Set the number of TTK simulation iterations. 10000 by defautl, 1000 for lightweight TTK.");
		lightWeightTTKBox.setToolTipText("<HTML>This will be significantly faster, but will be far less accurate. No min/max TTK values.<br>Even less accurate on slow-firing weapons</HTML>");
		maximizeButton.setToolTipText("Test every combination of mods in empty mod slots for the best builds. Will take time to complete");
		TTKBox.setSelected(true);
		lightWeightTTKBox.setSelected(false);

		JPanel dataPanel = new JPanel();
		UIBuilder.panelInit(dataPanel);
		dataPanel.setLayout(new GridLayout(1, 2, 0, 0));
		dataPanel.add(graphPane);
		dataPanel.add(outputScroll);

		outputScroll.getViewport().setPreferredSize(new Dimension(400, 250));
		buttonPanel.setSize(new Dimension(200, 30));

		UIBuilder.createTitledLineBorder(DPSPanel, "Calculated Stats");

		topPanel.add(weaponPane);
		bottomPanel.add(dataPanel);
		mainPanel.add(topPanel);
		mainPanel.add(bottomPanel);

		// DPSPanel.setVisible(false);
		mainPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		DPSPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		secondaryPanel.add(mainPanel);
		secondaryPanel.add(DPSPanel);

		outerPanel.add(secondaryPanel);
		outerPanel.add(buttonPanel);

		fileMenu.add(colorOptionsItem);
		fileMenu.add(saveItem);
		fileMenu.add(loadItem);
		mainMenuBar.add(fileMenu);

		mainMenuBar.add(modMenu);
		mainMenuBar.add(TTKMenu);
		mainMenuBar.add(weaponMenu);

		mainFrame.setJMenuBar(mainMenuBar);
		mainFrame.add(outerPanel);
		mainFrame.pack();
		mainFrame.addWindowListener(window);
		mainFrame.setTitle(Constants.APP_TITLE + " " + Constants.APP_VERSION);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		TTKIterationsField.setText("10000");
	}

	/**
	 * Calculates and Displays the DPS
	 */
	public static void calculateDPS() {
		// Clear all the previous values
		clearValues();

		// Get the Selected Weapon Type and Active Mods
		selectedWeapon = (WeaponPanel) weaponPane.getSelectedComponent();
		selectedWeapon.parseActiveMods();

		// Get the base values from the selected weapon
		getBaseValues();

		// Calculate the final values based on weapon parameters and Active Mods
		calculateFinals();

		// Calculate miscellaneous values
		calculateMiscValues();

		// Calculate damage per shot
		calculateDamagePerShot();

		// Calculate the damage per magazine
		calculateDamagePerIteration();

		// Calculate the damage per minute
		calculateDamagePerMinute();

		// Calculate the damage per second
		calculateDamagePerSecond();

		// Calculate the burst damage per second
		calculateBurstDamagePerSecond();

		// Calculate Time To Kill Values
		if (TTKBox.isSelected()) {
			String iters = TTKIterationsField.getText();
			if (iters == null)
				iters = "10000";
			complexTTKIterations = Integer.parseInt(iters);
		} else if (lightWeightTTKBox.isSelected()) {
			complexTTKIterations = 1;
		}
		int targetGroup = Integer.parseInt((String) targetGroupBox.getSelectedItem());
		Vector<TTKTarget> groupTargets = new Vector<TTKTarget>();
		for (TTKTarget target : theTTKManager.targets) {
			if (target.group == targetGroup) {
				groupTargets.add(target);
			}
		}
		if (useComplexTTK && (raw.rawPerSecond > 100.0)) {
			complexTTKCompletions = 0;
			for (TTKTarget target : groupTargets) {
				target.runAdvancedTTK();
				String name = target.name + "[" + target.currentLevel + "]";
				if (name.length() > longestTTKName.length()) {
					longestTTKName = name;
				}
			}

			/*
			 * int ttkCount = groupTargets.size(); maxTTKTime = ttkCount * 60000; int
			 * ttkTimeout = 0; while(complexTTKCompletions < ttkCount && ttkTimeout <
			 * maxTTKTime){ try{ ttkTimeout += 1; Thread.sleep(1); }catch(Exception ex){
			 * ex.printStackTrace(); } }
			 * System.out.println("Advanced TTK Completed in "+(ttkTimeout/1000.0)
			 * +" seconds.");
			 */

		}
		// Print the data to the text area and render the graph
		if (updateOutput)
			updateOutput();
	}

	/**
	 * Clears all values
	 */
	protected static void clearValues() {
		selectedWeapon = null;
		activeMods = new Vector<Mod>();
		modRanks = new Vector<Double>();
		weaponName = "";
		weaponMode = "";
		damageType = "";
		chargeTime = 0.0;
		drain = 0;
		raw.clear();
		impact.clear();
		puncture.clear();
		slash.clear();
		fire.clear();
		ice.clear();
		electric.clear();
		toxin.clear();
		blast.clear();
		magnetic.clear();
		gas.clear();
		radiation.clear();
		corrosive.clear();
		viral.clear();
		corpus.clear();
		grineer.clear();
		infested.clear();
		cloneFlesh.clear();
		ferrite.clear();
		alloy.clear();
		mechanical.clear();
		corpusFlesh.clear();
		shield.clear();
		protoShield.clear();
		robotic.clear();
		infestedFlesh.clear();
		fossilized.clear();
		sinew.clear();
		fireRate = 0.0;
		reloadTime = 0.0;
		critChance = 0.0;
		critMult = 0.0;
		projectileCount = 0.0;
		firstShotDamageMult = 1.0;
		lastShotDamageMult = 1.0;
		statusChance = 0.0;
		statusDuration = 1.0;
		damageMult = 1.0;
		deadAimMult = 1.0;
		flatDamageBonus = 0.0;
		mag = 0;
		combo = 0;
		startingCombo = 1;
		burstCount = 0;
		finalMag = 0;
		finalAmmo = 0;
		finalIterationTime = 0.0;
		finalIterationsPerMinute = 0.0;
		averageCritMult = 0;
		finalCritChance = 0.0;
		finalCritMult = 0.0;
		finalFireRate = 0.0;
		finalReloadTime = 0.0;
		finalProjectileCount = 0.0;
		finalFirstShotDamageMult = 1.0;
		finalLastShotDamageMult = 1.0;
		finalStatusChance = 0.0;
		finalStatusDuration = 0.0;
		finalDamageMult = 1.0;
		finalDeadAimMult = 1.0;
		finalFlatDamageBonus = 0.0;
		finalCorpusMult = 1.0;
		finalGrineerMult = 1.0;
		finalInfestedMult = 1.0;
		procsPerSecond = 0.0;
		burstProcsPerSecond = 0.0;
		slashStacks = 0;
		fireStacks = 0;
		toxinStacks = 0;
		gasStacks = 0;
		complexTTKCompletions = 0;
		globalFire = 0;
		globalToxin = 0;
		globalElectric = 0;
		hunterMunitions = 0;
		vigilante = 0;
	}

	/**
	 * Gets the base values from the selected weapon
	 */
	protected static void getBaseValues() {
		// Base Values
		weaponName = selectedWeapon.getName();
		weaponMode = selectedWeapon.getWeaponMode();
		damageType = selectedWeapon.getDamageType();
		chargeTime = selectedWeapon.getChargeTime();
		fireRate = selectedWeapon.getFireRate();
		reloadTime = selectedWeapon.getReloadTime();
		critChance = selectedWeapon.getCritChance();
		critMult = selectedWeapon.getCritMultiplier();
		projectileCount = selectedWeapon.getProjectiles();
		firstShotDamageMult = 1;
		lastShotDamageMult = 1;
		statusChance = selectedWeapon.getStatusChance();
		mag = selectedWeapon.getMagSize();
		combo = selectedWeapon.getCombo();
		startingCombo = selectedWeapon.getStartingCombo();
		burstCount = selectedWeapon.getBurstCount();
		drain = selectedWeapon.getDrain();

		impact.base = selectedWeapon.getImpactDamage();
		puncture.base = selectedWeapon.getPunctureDamage();
		slash.base = selectedWeapon.getSlashDamage();
		if (damageType.equals(Constants.FIRE_WEAPON_DAMAGE)) {
			fire.base = selectedWeapon.getBaseDamage();
		} else if (damageType.equals(Constants.ICE_WEAPON_DAMAGE)) {
			ice.base = selectedWeapon.getBaseDamage();
		} else if (damageType.equals(Constants.ELECTRIC_WEAPON_DAMAGE)) {
			electric.base = selectedWeapon.getBaseDamage();
		} else if (damageType.equals(Constants.TOXIN_WEAPON_DAMAGE)) {
			toxin.base = selectedWeapon.getBaseDamage();
		} else if (damageType.equals(Constants.BLAST_WEAPON_DAMAGE)) {
			blast.base = selectedWeapon.getBaseDamage();
		} else if (damageType.equals(Constants.MAGNETIC_WEAPON_DAMAGE)) {
			magnetic.base = selectedWeapon.getBaseDamage();
		} else if (damageType.equals(Constants.GAS_WEAPON_DAMAGE)) {
			gas.base = selectedWeapon.getBaseDamage();
		} else if (damageType.equals(Constants.RADIATION_WEAPON_DAMAGE)) {
			radiation.base = selectedWeapon.getBaseDamage();
		} else if (damageType.equals(Constants.CORROSIVE_WEAPON_DAMAGE)) {
			corrosive.base = selectedWeapon.getBaseDamage();
		} else if (damageType.equals(Constants.VIRAL_WEAPON_DAMAGE)) {
			viral.base = selectedWeapon.getBaseDamage();
		}
		raw.base = impact.base + puncture.base + slash.base + fire.base + ice.base + electric.base + toxin.base + blast.base + magnetic.base + gas.base + radiation.base + corrosive.base + viral.base;

		// Factor for multiple projectiles per shot
		if (projectileCount > 1.0) {
			raw.base /= projectileCount;
			impact.base /= projectileCount;
			puncture.base /= projectileCount;
			slash.base /= projectileCount;
			fire.base /= projectileCount;
			ice.base /= projectileCount;
			electric.base /= projectileCount;
			toxin.base /= projectileCount;
			blast.base /= projectileCount;
			magnetic.base /= projectileCount;
			gas.base /= projectileCount;
			radiation.base /= projectileCount;
			corrosive.base /= projectileCount;
			viral.base /= projectileCount;
		}
		// Mod Vectors
		activeMods = selectedWeapon.getActiveMods();
		modRanks = selectedWeapon.getActiveModRanks();
	}

	/**
	 * Calculates the final modded values
	 */
	protected static void calculateFinals() {
		// Initialize mod vectors
		Vector<Mod> combinedMods = new Vector<Mod>();
		Vector<Double> magMods = new Vector<Double>();
		// Vector<Double> ammoMods = new Vector<Double>();
		Vector<Double> critChanceMods = new Vector<Double>();
		Vector<Double> critMultMods = new Vector<Double>();
		Vector<Double> fireRateMods = new Vector<Double>();
		Vector<Double> reloadTimeMods = new Vector<Double>();
		Vector<Double> damageMultMods = new Vector<Double>();
		Vector<Double> impactDamageMods = new Vector<Double>();
		Vector<Double> punctureDamageMods = new Vector<Double>();
		Vector<Double> slashDamageMods = new Vector<Double>();
		Vector<Double> fireDamageMods = new Vector<Double>();
		Vector<Double> iceDamageMods = new Vector<Double>();
		Vector<Double> electricDamageMods = new Vector<Double>();
		Vector<Double> toxinDamageMods = new Vector<Double>();
		Vector<Double> blastDamageMods = new Vector<Double>();
		Vector<Double> magneticDamageMods = new Vector<Double>();
		Vector<Double> gasDamageMods = new Vector<Double>();
		Vector<Double> radiationDamageMods = new Vector<Double>();
		Vector<Double> corrosiveDamageMods = new Vector<Double>();
		Vector<Double> viralDamageMods = new Vector<Double>();
		Vector<Double> projectileCountMods = new Vector<Double>();
		Vector<Double> firstShotDamageMods = new Vector<Double>();
		Vector<Double> lastShotDamageMods = new Vector<Double>();
		Vector<Double> statusChanceMods = new Vector<Double>();
		Vector<Double> statusDurationMods = new Vector<Double>();
		Vector<Double> corpusMods = new Vector<Double>();
		Vector<Double> grineerMods = new Vector<Double>();
		Vector<Double> infestedMods = new Vector<Double>();
		Vector<Double> flatDamageMods = new Vector<Double>();
		Vector<Double> deadAimMods = new Vector<Double>();
		Vector<Double> flatStatusMods = new Vector<Double>();
		Vector<Double> flatMagMods = new Vector<Double>();

		// Check for combined elements
		Mod primeMod = null;
		double primeModRanks = 0;
		String primeModType = "";

		for (int i = 0; i < activeMods.size(); i++) {
			Mod tempMod = activeMods.get(i);
			if (primeMod == null) {
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_FIRE_DAMAGE)) {
					double modPower = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_FIRE_DAMAGE)) * (1.0 + modRanks.get(i));
					if (blastDamageMods.size() > 0) {
						blastDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else if (radiationDamageMods.size() > 0) {
						radiationDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else if (gasDamageMods.size() > 0) {
						gasDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else {
						primeMod = tempMod;
						primeModRanks = modRanks.get(i);
						primeModType = Constants.MOD_TYPE_FIRE_DAMAGE;
					}
				}
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_ICE_DAMAGE)) {
					double modPower = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_ICE_DAMAGE)) * (1.0 + modRanks.get(i));
					if (blastDamageMods.size() > 0) {
						blastDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else if (magneticDamageMods.size() > 0) {
						magneticDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else if (viralDamageMods.size() > 0) {
						viralDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else {
						primeMod = tempMod;
						primeModRanks = modRanks.get(i);
						primeModType = Constants.MOD_TYPE_ICE_DAMAGE;
					}
				}
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
					double modPower = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) * (1.0 + modRanks.get(i));
					if (corrosiveDamageMods.size() > 0) {
						corrosiveDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else if (magneticDamageMods.size() > 0) {
						magneticDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else if (radiationDamageMods.size() > 0) {
						radiationDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else {
						primeMod = tempMod;
						primeModRanks = modRanks.get(i);
						primeModType = Constants.MOD_TYPE_LIGHTNING_DAMAGE;
					}
				}
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
					double modPower = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_TOXIN_DAMAGE)) * (1.0 + modRanks.get(i));
					if (corrosiveDamageMods.size() > 0) {
						corrosiveDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else if (viralDamageMods.size() > 0) {
						viralDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else if (gasDamageMods.size() > 0) {
						gasDamageMods.add(modPower);
						combinedMods.add(tempMod);
					} else {
						primeMod = tempMod;
						primeModRanks = modRanks.get(i);
						primeModType = Constants.MOD_TYPE_TOXIN_DAMAGE;
					}
				}
			} else {
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_FIRE_DAMAGE)) {
					if (primeModType.equals(Constants.MOD_TYPE_FIRE_DAMAGE)) {
						// Don't Combine
					} else if (primeModType.equals(Constants.MOD_TYPE_ICE_DAMAGE)) {
						if (magneticDamageMods.size() > 0) {
							// Don't Combine
						} else if (viralDamageMods.size() > 0) {
							// Don't Combine
						} else if (gasDamageMods.size() > 0) {
							// Don't Combine
						} else if (radiationDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_FIRE_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							blastDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					} else if (primeModType.equals(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
						if (corrosiveDamageMods.size() > 0) {
							// Don't Combine
						} else if (magneticDamageMods.size() > 0) {
							// Don't Combine
						} else if (blastDamageMods.size() > 0) {
							// Don't Combine
						} else if (gasDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_FIRE_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							radiationDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					} else if (primeModType.equals(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
						if (corrosiveDamageMods.size() > 0) {
							// Don't Combine
						} else if (viralDamageMods.size() > 0) {
							// Don't Combine
						} else if (blastDamageMods.size() > 0) {
							// Don't Combine
						} else if (radiationDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_FIRE_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							gasDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					}
				} else if (tempMod.effectTypes.contains(Constants.MOD_TYPE_ICE_DAMAGE)) {
					if (primeModType.equals(Constants.MOD_TYPE_FIRE_DAMAGE)) {
						if (gasDamageMods.size() > 0) {
							// Don't Combine
						} else if (radiationDamageMods.size() > 0) {
							// Don't Combine
						} else if (magneticDamageMods.size() > 0) {
							// Don't Combine
						} else if (viralDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_ICE_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							blastDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					} else if (primeModType.equals(Constants.MOD_TYPE_ICE_DAMAGE)) {
						// Don't Combine
					} else if (primeModType.equals(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
						if (corrosiveDamageMods.size() > 0) {
							// Don't Combine
						} else if (radiationDamageMods.size() > 0) {
							// Don't Combine
						} else if (blastDamageMods.size() > 0) {
							// Don't Combine
						} else if (viralDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_ICE_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							magneticDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					} else if (primeModType.equals(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
						if (corrosiveDamageMods.size() > 0) {
							// Don't Combine
						} else if (gasDamageMods.size() > 0) {
							// Don't Combine
						} else if (blastDamageMods.size() > 0) {
							// Don't Combine
						} else if (magneticDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_ICE_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							viralDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					}
				} else if (tempMod.effectTypes.contains(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
					if (primeModType.equals(Constants.MOD_TYPE_FIRE_DAMAGE)) {
						if (gasDamageMods.size() > 0) {
							// Don't Combine
						} else if (blastDamageMods.size() > 0) {
							// Don't Combine
						} else if (corrosiveDamageMods.size() > 0) {
							// Don't Combine
						} else if (magneticDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_LIGHTNING_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							radiationDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					} else if (primeModType.equals(Constants.MOD_TYPE_ICE_DAMAGE)) {
						if (blastDamageMods.size() > 0) {
							// Don't Combine
						} else if (viralDamageMods.size() > 0) {
							// Don't Combine
						} else if (corrosiveDamageMods.size() > 0) {
							// Don't Combine
						} else if (radiationDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_LIGHTNING_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							magneticDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					} else if (primeModType.equals(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
						// Don't Combine
					} else if (primeModType.equals(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
						if (viralDamageMods.size() > 0) {
							// Don't Combine
						} else if (gasDamageMods.size() > 0) {
							// Don't Combine
						} else if (radiationDamageMods.size() > 0) {
							// Don't Combine
						} else if (magneticDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_LIGHTNING_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							corrosiveDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					}
				} else if (tempMod.effectTypes.contains(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
					if (primeModType.equals(Constants.MOD_TYPE_FIRE_DAMAGE)) {
						if (blastDamageMods.size() > 0) {
							// Don't Combine
						} else if (radiationDamageMods.size() > 0) {
							// Don't Combine
						} else if (corrosiveDamageMods.size() > 0) {
							// Don't Combine
						} else if (viralDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_TOXIN_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							gasDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					} else if (primeModType.equals(Constants.MOD_TYPE_ICE_DAMAGE)) {
						if (blastDamageMods.size() > 0) {
							// Don't Combine
						} else if (magneticDamageMods.size() > 0) {
							// Don't Combine
						} else if (corrosiveDamageMods.size() > 0) {
							// Don't Combine
						} else if (gasDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_TOXIN_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							viralDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					} else if (primeModType.equals(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
						if (magneticDamageMods.size() > 0) {
							// Don't Combine
						} else if (radiationDamageMods.size() > 0) {
							// Don't Combine
						} else if (gasDamageMods.size() > 0) {
							// Don't Combine
						} else if (viralDamageMods.size() > 0) {
							// Don't Combine
						} else {
							combinedMods.add(primeMod);
							combinedMods.add(tempMod);
							double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
							double secondEffectPower = (tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_TOXIN_DAMAGE))) * (1.0 + modRanks.get(i));
							double jointPower = primeEffectPower + secondEffectPower;
							corrosiveDamageMods.add(jointPower);
							primeMod = null;
							primeModRanks = 0;
							primeModType = "";
						}
					} else if (primeModType.equals(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
						// Don't Combine
					}
				}
			}
		}
		// Combine the base
		boolean baseCombined = false;
		if (primeMod != null && !damageType.equals(Constants.PHYSICAL_WEAPON_DAMAGE)) {
			if (damageType.equals(Constants.FIRE_WEAPON_DAMAGE) && (blastDamageMods.size() == 0) && (radiationDamageMods.size() == 0) && (gasDamageMods.size() == 0)) {
				if (primeModType.equals(Constants.MOD_TYPE_FIRE_DAMAGE)) {
					// Don't Combine
				} else if (primeModType.equals(Constants.MOD_TYPE_ICE_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					blastDamageMods.add(jointPower);
					blast.base = fire.base;
					fire.base = 0.0;
					baseCombined = true;
				} else if (primeModType.equals(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					radiationDamageMods.add(jointPower);
					radiation.base = fire.base;
					fire.base = 0.0;
					baseCombined = true;
				} else if (primeModType.equals(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					gasDamageMods.add(jointPower);
					gas.base = fire.base;
					fire.base = 0.0;
					baseCombined = true;
				}
			} else if (damageType.equals(Constants.ICE_WEAPON_DAMAGE) && (blastDamageMods.size() == 0) && (magneticDamageMods.size() == 0) && (viralDamageMods.size() == 0)) {
				if (primeModType.equals(Constants.MOD_TYPE_FIRE_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					blastDamageMods.add(jointPower);
					blast.base = ice.base;
					ice.base = 0.0;
					baseCombined = true;
				} else if (primeModType.equals(Constants.MOD_TYPE_ICE_DAMAGE)) {
					// Don't Combine
				} else if (primeModType.equals(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					magneticDamageMods.add(jointPower);
					magnetic.base = ice.base;
					ice.base = 0.0;
					baseCombined = true;
				} else if (primeModType.equals(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					viralDamageMods.add(jointPower);
					viral.base = ice.base;
					ice.base = 0.0;
					baseCombined = true;
				}
			} else if (damageType.equals(Constants.ELECTRIC_WEAPON_DAMAGE) && (radiationDamageMods.size() == 0) && (magneticDamageMods.size() == 0) && (corrosiveDamageMods.size() == 0)) {
				if (primeModType.equals(Constants.MOD_TYPE_FIRE_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					radiationDamageMods.add(jointPower);
					radiation.base = electric.base;
					electric.base = 0.0;
					baseCombined = true;
				} else if (primeModType.equals(Constants.MOD_TYPE_ICE_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					magneticDamageMods.add(jointPower);
					magnetic.base = electric.base;
					electric.base = 0.0;
					baseCombined = true;
				} else if (primeModType.equals(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
					// Don't Combine
				} else if (primeModType.equals(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					corrosiveDamageMods.add(jointPower);
					corrosive.base = electric.base;
					electric.base = 0.0;
					baseCombined = true;
				}
			} else if (damageType.equals(Constants.TOXIN_WEAPON_DAMAGE) && (gasDamageMods.size() == 0) && (viralDamageMods.size() == 0) && (corrosiveDamageMods.size() == 0)) {
				if (primeModType.equals(Constants.MOD_TYPE_FIRE_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					gasDamageMods.add(jointPower);
					gas.base = toxin.base;
					toxin.base = 0.0;
					baseCombined = true;
				} else if (primeModType.equals(Constants.MOD_TYPE_ICE_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					viralDamageMods.add(jointPower);
					viral.base = toxin.base;
					toxin.base = 0.0;
					baseCombined = true;
				} else if (primeModType.equals(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
					combinedMods.add(primeMod);
					double primeEffectPower = (primeMod.effectStrengths.get(primeMod.effectTypes.indexOf(primeModType))) * (1.0 + primeModRanks);
					double jointPower = primeEffectPower;
					corrosiveDamageMods.add(jointPower);
					corrosive.base = toxin.base;
					toxin.base = 0.0;
					baseCombined = true;
				} else if (primeModType.equals(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
					// Don't Combine
				}
			}
		}
		if (!baseCombined) {
			if (damageType.equals(Constants.FIRE_WEAPON_DAMAGE)) {
				if (blastDamageMods.size() > 0) {
					blast.base = fire.base;
					fire.base = 0.0;
				} else if (radiationDamageMods.size() > 0) {
					radiation.base = fire.base;
					fire.base = 0.0;
				} else if (gasDamageMods.size() > 0) {
					gas.base = fire.base;
					fire.base = 0.0;
				}
			} else if (damageType.equals(Constants.ICE_WEAPON_DAMAGE)) {
				if (blastDamageMods.size() > 0) {
					blast.base = ice.base;
					ice.base = 0.0;
				} else if (magneticDamageMods.size() > 0) {
					magnetic.base = ice.base;
					ice.base = 0.0;
				} else if (viralDamageMods.size() > 0) {
					viral.base = ice.base;
					ice.base = 0.0;
				}
			} else if (damageType.equals(Constants.ELECTRIC_WEAPON_DAMAGE)) {
				if (radiationDamageMods.size() > 0) {
					radiation.base = electric.base;
					electric.base = 0.0;
				} else if (magneticDamageMods.size() > 0) {
					magnetic.base = electric.base;
					electric.base = 0.0;
				} else if (corrosiveDamageMods.size() > 0) {
					corrosive.base = electric.base;
					electric.base = 0.0;
				}
			} else if (damageType.equals(Constants.TOXIN_WEAPON_DAMAGE)) {
				if (gasDamageMods.size() > 0) {
					gas.base = toxin.base;
					toxin.base = 0.0;
				} else if (viralDamageMods.size() > 0) {
					viral.base = toxin.base;
					toxin.base = 0.0;
				} else if (corrosiveDamageMods.size() > 0) {
					corrosive.base = toxin.base;
					toxin.base = 0.0;
				}
			}
		}

		// Populate non-combined-element mod vectors
		for (int i = 0; i < activeMods.size(); i++) {
			Mod tempMod = activeMods.get(i);
			if (!combinedMods.contains(tempMod)) {
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_FIRE_DAMAGE)) {
					double modPower = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_FIRE_DAMAGE)) * (1.0 + modRanks.get(i));
					if (blastDamageMods.size() > 0) {
						blastDamageMods.add(modPower);
					} else if (radiationDamageMods.size() > 0) {
						radiationDamageMods.add(modPower);
					} else if (gasDamageMods.size() > 0) {
						gasDamageMods.add(modPower);
					} else {
						fireDamageMods.add(modPower);
					}
				}
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_ICE_DAMAGE)) {
					double modPower = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_ICE_DAMAGE)) * (1.0 + modRanks.get(i));
					if (blastDamageMods.size() > 0) {
						blastDamageMods.add(modPower);
					} else if (magneticDamageMods.size() > 0) {
						magneticDamageMods.add(modPower);
					} else if (viralDamageMods.size() > 0) {
						viralDamageMods.add(modPower);
					} else {
						iceDamageMods.add(modPower);
					}
				}
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
					double modPower = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) * (1.0 + modRanks.get(i));
					if (corrosiveDamageMods.size() > 0) {
						corrosiveDamageMods.add(modPower);
					} else if (magneticDamageMods.size() > 0) {
						magneticDamageMods.add(modPower);
					} else if (radiationDamageMods.size() > 0) {
						radiationDamageMods.add(modPower);
					} else {
						electricDamageMods.add(modPower);
					}
				}
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
					double modPower = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_TOXIN_DAMAGE)) * (1.0 + modRanks.get(i));
					if (corrosiveDamageMods.size() > 0) {
						corrosiveDamageMods.add(modPower);
					} else if (viralDamageMods.size() > 0) {
						viralDamageMods.add(modPower);
					} else if (gasDamageMods.size() > 0) {
						gasDamageMods.add(modPower);
					} else {
						toxinDamageMods.add(modPower);
					}
				}
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_IMPACT_DAMAGE)) {
					impactDamageMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_IMPACT_DAMAGE))) * (1.0 + modRanks.get(i)));
				}
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_PUNCTURE_DAMAGE)) {
					punctureDamageMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_PUNCTURE_DAMAGE))) * (1.0 + modRanks.get(i)));
				}
				if (tempMod.effectTypes.contains(Constants.MOD_TYPE_SLASH_DAMAGE)) {
					slashDamageMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_SLASH_DAMAGE))) * (1.0 + modRanks.get(i)));
				}
			}
		}

		// Populate non-elemental mod vectors
		for (int i = 0; i < activeMods.size(); i++) {
			Mod tempMod = activeMods.get(i);
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_MAG_CAP)) {
				magMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_MAG_CAP))) * (1.0 + modRanks.get(i)));
			}
			/*
			 * if (tempMod.effectTypes.contains(Constants.MOD_TYPE_AMMO_CAP)) {
			 * ammoMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(
			 * Constants.MOD_TYPE_AMMO_CAP))) * (1.0 + modRanks.get(i))); }
			 */
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_CRIT_CHANCE)) {
				critChanceMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_CRIT_CHANCE))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_CRIT_MULTIPLIER)) {
				critMultMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_CRIT_MULTIPLIER))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_FIRE_RATE)) {
				fireRateMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_FIRE_RATE))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_RELOAD_SPEED)) {
				reloadTimeMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_RELOAD_SPEED))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_DAMAGE_BONUS)) {
				damageMultMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_DAMAGE_BONUS))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_MULTISHOT)) {
				projectileCountMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_MULTISHOT))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_FIRST_SHOT_DAMAGE)) {
				firstShotDamageMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_FIRST_SHOT_DAMAGE))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_LAST_SHOT_DAMAGE)) {
				lastShotDamageMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_LAST_SHOT_DAMAGE))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_STATUS_CHANCE)) {
				statusChanceMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_STATUS_CHANCE))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_STATUS_DURATION)) {
				statusDurationMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_STATUS_DURATION))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_CORPUS_DAMAGE)) {
				corpusMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_CORPUS_DAMAGE))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_GRINEER_DAMAGE)) {
				grineerMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_GRINEER_DAMAGE))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_INFESTED_DAMAGE)) {
				infestedMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_INFESTED_DAMAGE))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_FLAT_DAMAGE)) {
				flatDamageMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_FLAT_DAMAGE))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_FLAT_STATUS)) {
				flatStatusMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_FLAT_STATUS))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_FLAT_MAG)) {
				flatMagMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_FLAT_MAG))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_DEAD_AIM)) {
				deadAimMods.add((tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_DEAD_AIM))) * (1.0 + modRanks.get(i)));
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_MUNITIONS)) {
				hunterMunitions = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_MUNITIONS)) * (1.0 + modRanks.get(i));
			}
			// Finding "mod power" for each element for status proc damage calculations
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) {
				double modPower = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_LIGHTNING_DAMAGE)) * (1.0 + modRanks.get(i));
				globalElectric += modPower;
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_FIRE_DAMAGE)) {
				double modPower = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_FIRE_DAMAGE)) * (1.0 + modRanks.get(i));
				globalFire += modPower;
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_TOXIN_DAMAGE)) {
				double modPower = tempMod.effectStrengths.get(tempMod.effectTypes.indexOf(Constants.MOD_TYPE_TOXIN_DAMAGE)) * (1.0 + modRanks.get(i));
				globalToxin += modPower;
			}
			if (tempMod.effectTypes.contains(Constants.MOD_TYPE_VIGILANTE)) {
				vigilante = 0.05 * selectedWeapon.getVigilanteEffects();
			}
		}

		// Calculate finals
		double tempMag = mag;
		for (int i = 0; i < magMods.size(); i++) {
			tempMag += mag * magMods.get(i);
		}
		finalMag = (int) Math.round(tempMag);
		for (int i = 0; i < flatMagMods.size(); i++) {
			finalMag += flatMagMods.get(i);
		}
		/*
		 * finalAmmo = ammoCap; for (int i = 0; i < ammoMods.size(); i++) { finalAmmo +=
		 * ammoCap * ammoMods.get(i); }
		 */

		finalCritChance = critChance;
		for (int i = 0; i < critChanceMods.size(); i++) {
			finalCritChance += critChance * critChanceMods.get(i);
		}
		finalCritChance += selectedWeapon.getAddCC();

		finalCritMult = critMult;
		for (int i = 0; i < critMultMods.size(); i++) {
			finalCritMult += critMult * critMultMods.get(i);
		}
		finalCritMult += selectedWeapon.getAddCD();

		finalFlatDamageBonus = flatDamageBonus;
		for (int i = 0; i < flatDamageMods.size(); i++) {
			finalFlatDamageBonus += flatDamageMods.get(i);
		}

		finalDeadAimMult = deadAimMult;
		for (int i = 0; i < deadAimMods.size(); i++) {
			finalDeadAimMult += deadAimMult * deadAimMods.get(i);
		}

		finalDamageMult = damageMult;
		for (int i = 0; i < damageMultMods.size(); i++) {
			finalDamageMult += damageMult * damageMultMods.get(i);
		}
		finalDamageMult += damageMult * selectedWeapon.getAddDam();

		if (weaponMode.equals(Constants.LANKA) || weaponMode.equals(Constants.SNIPER)) {
			if (startingCombo < 1.5)
				startingCombo = 1;
			finalDamageMult *= startingCombo;
		}

		finalFireRate = fireRate;
		fireRateModPower = 0;
		for (int i = 0; i < fireRateMods.size(); i++) {
			fireRateModPower += fireRateMods.get(i);
		}
		if (weaponMode.equals(Constants.AUTOBOW) || weaponMode.equals(Constants.SEMIBOW) || weaponMode.equals(Constants.CHARGEBOW)) {
			fireRateModPower *= 2;
		}
		if (weaponMode.equals(Constants.CHARGE) || weaponMode.equals(Constants.LANKA) || weaponMode.equals(Constants.CHARGEBOW)) {
			double finalChargeTime = chargeTime / (1 + fireRateModPower + selectedWeapon.getAddFR());
			if (fireRate > 0) {
				finalFireRate = 1 / ((1 / (fireRate * (1 + fireRateModPower))) + finalChargeTime);
			} else {
				finalFireRate = 1 / finalChargeTime;
			}
		} else {
			finalFireRate += fireRate * fireRateModPower;
			finalFireRate += fireRate * selectedWeapon.getAddFR();
		}

		// This is completely retarded, but also the current case
		if (weaponMode.equals(Constants.SEMI_AUTO) || weaponMode.equals(Constants.SNIPER) || weaponMode.equals(Constants.SEMIBOW)) {
			if (finalFireRate > 10.0) {
				finalFireRate = 10.0;
			}
		}

		finalReloadTime = reloadTime;
		double reloadSpeedMult = 1.0;
		for (int i = 0; i < reloadTimeMods.size(); i++) {
			// finalReloadTime -= reloadTime*reloadTimeMods.get(i);
			reloadSpeedMult += reloadTimeMods.get(i);
		}
		finalReloadTime /= reloadSpeedMult;

		finalProjectileCount = projectileCount;
		double multishot = 1;
		for (int i = 0; i < projectileCountMods.size(); i++) {
			multishot += projectileCount * projectileCountMods.get(i);
			finalProjectileCount += projectileCount * projectileCountMods.get(i);
		}
		if (weaponMode.equals(Constants.CONTINUOUS)) {
			finalProjectileCount = projectileCount;
			finalDamageMult *= multishot; // Beams don't get more projectiles, so I turned multishot into damage -o
		}
		finalFirstShotDamageMult = firstShotDamageMult;
		for (int i = 0; i < firstShotDamageMods.size(); i++) {
			finalFirstShotDamageMult += firstShotDamageMult * firstShotDamageMods.get(i);
		}

		finalLastShotDamageMult = lastShotDamageMult;
		for (int i = 0; i < lastShotDamageMods.size(); i++) {
			finalLastShotDamageMult += lastShotDamageMult * lastShotDamageMods.get(i);
		}

		finalStatusChance = statusChance;
		for (int i = 0; i < statusChanceMods.size(); i++) {
			finalStatusChance += statusChance * statusChanceMods.get(i);
		}
		for (int i = 0; i < flatStatusMods.size(); i++) {
			double localStatus = flatStatusMods.get(i);
			if (projectileCount > 1.0) {
				localStatus /= projectileCount;
			}
			finalStatusChance += localStatus;
		}
		finalStatusChance += selectedWeapon.getAddSC();

		if (finalStatusChance > 1) {
			finalStatusChance = 1;
		}
		finalStatusChance = (1 - Math.pow((1 - (finalStatusChance)), (1 / projectileCount))); // Correctly handling multi-projectile status -o

		finalStatusDuration = statusDuration;
		for (int i = 0; i < statusDurationMods.size(); i++) {
			finalStatusDuration += statusDuration * statusDurationMods.get(i);
		}

//		if (damageType.equals(Constants.PHYSICAL_WEAPON_DAMAGE)) {

		impact.finalBase = impact.base;
		for (int i = 0; i < impactDamageMods.size(); i++) {
			impact.finalBase += impact.base * impactDamageMods.get(i);
		}
		impact.finalBase *= finalDamageMult;

		puncture.finalBase = puncture.base;
		for (int i = 0; i < punctureDamageMods.size(); i++) {
			puncture.finalBase += puncture.base * punctureDamageMods.get(i);
		}
		puncture.finalBase *= finalDamageMult;

		slash.finalBase = slash.base;
		for (int i = 0; i < slashDamageMods.size(); i++) {
			slash.finalBase += slash.base * slashDamageMods.get(i);
		}
		slash.finalBase *= finalDamageMult;
//		}

		fire.finalBase = fire.base;
		for (int i = 0; i < fireDamageMods.size(); i++) {
			fire.finalBase += raw.base * fireDamageMods.get(i);
		}
		fire.finalBase *= finalDamageMult;

		ice.finalBase = ice.base;
		for (int i = 0; i < iceDamageMods.size(); i++) {
			ice.finalBase += raw.base * iceDamageMods.get(i);
		}
		ice.finalBase *= finalDamageMult;

		electric.finalBase = electric.base;
		for (int i = 0; i < electricDamageMods.size(); i++) {
			electric.finalBase += raw.base * electricDamageMods.get(i);
		}
		electric.finalBase *= finalDamageMult;

		toxin.finalBase = toxin.base;
		for (int i = 0; i < toxinDamageMods.size(); i++) {
			toxin.finalBase += raw.base * toxinDamageMods.get(i);
		}
		toxin.finalBase *= finalDamageMult;

		blast.finalBase = blast.base;
		for (int i = 0; i < blastDamageMods.size(); i++) {
			blast.finalBase += raw.base * blastDamageMods.get(i);
		}
		blast.finalBase *= finalDamageMult;

		magnetic.finalBase = magnetic.base;
		for (int i = 0; i < magneticDamageMods.size(); i++) {
			magnetic.finalBase += raw.base * magneticDamageMods.get(i);
		}
		magnetic.finalBase *= finalDamageMult;

		gas.finalBase = gas.base;
		for (int i = 0; i < gasDamageMods.size(); i++) {
			gas.finalBase += raw.base * gasDamageMods.get(i);
		}
		gas.finalBase *= finalDamageMult;

		radiation.finalBase = radiation.base;
		for (int i = 0; i < radiationDamageMods.size(); i++) {
			radiation.finalBase += raw.base * radiationDamageMods.get(i);
		}
		radiation.finalBase *= finalDamageMult;

		corrosive.finalBase = corrosive.base;
		for (int i = 0; i < corrosiveDamageMods.size(); i++) {
			corrosive.finalBase += raw.base * corrosiveDamageMods.get(i);
		}
		corrosive.finalBase *= finalDamageMult;

		viral.finalBase = viral.base;
		for (int i = 0; i < viralDamageMods.size(); i++) {
			viral.finalBase += raw.base * viralDamageMods.get(i);
		}
		viral.finalBase *= finalDamageMult;

		raw.finalBase = impact.finalBase + puncture.finalBase + slash.finalBase + fire.finalBase + ice.finalBase + electric.finalBase + toxin.finalBase + blast.finalBase + magnetic.finalBase + gas.finalBase + radiation.finalBase + corrosive.finalBase + viral.finalBase;

		finalCorpusMult = 1.0;
		for (int i = 0; i < corpusMods.size(); i++) {
			finalCorpusMult += corpusMods.get(i);
		}

		finalGrineerMult = 1.0;
		for (int i = 0; i < grineerMods.size(); i++) {
			finalGrineerMult += grineerMods.get(i);
		}

		finalInfestedMult = 1.0;
		for (int i = 0; i < infestedMods.size(); i++) {
			finalInfestedMult += infestedMods.get(i);
		}

		if (headShots.isSelected()) {
			headShot = true;
		} else {
			headShot = false;
		}

		if (weaponMode.equals(Constants.BURST)) {
			if (selectedWeapon.isRefireCanceled()) {
				finalFireRate += fireRate;
			}
			double burstDelay = 0.05;
			double burstMS = (1 / finalFireRate);
			double burstIterationMS = (burstMS * burstCount) + burstDelay;
			finalFireRate = (1 / burstIterationMS);
			if (finalFireRate > 10.0) {
				finalFireRate = 10.0;
			}
			double numBursts = finalMag / burstCount;
			double rawFireTime = numBursts / finalFireRate;
			finalIterationTime = rawFireTime + finalReloadTime;
		} else if (weaponMode.equals(Constants.FULL_AUTO_RAMP_UP) || weaponMode.equals(Constants.FULL_AUTO_BULLET_RAMP)) {
			double baseFireDelay = ((1 / finalFireRate));
			double firstFireDelay = baseFireDelay * 5 / 2;
			double secondFireDelay = baseFireDelay * 5 / 3;
			double thirdFireDelay = baseFireDelay * 5 / 4;
			if (weaponMode.equals(Constants.FULL_AUTO_BULLET_RAMP)) { // Kohm's effective magazine size -o
				finalMag = (int) Math.round(projectileCount + (finalMag - (projectileCount / 3 - 1) - (Main.projectileCount * (Main.projectileCount + 1) / 2) / 3) / (projectileCount / 3));
			}
			finalIterationTime = (firstFireDelay + secondFireDelay + thirdFireDelay + ((finalMag - 4) * baseFireDelay)) + finalReloadTime;
		} else if (weaponMode.equals(Constants.CONTINUOUS)) {
			finalMag /= drain;
			finalIterationTime = ((finalMag) / finalFireRate) + finalReloadTime;
		} else if (weaponMode.equals(Constants.CHARGE) || weaponMode.equals(Constants.CHARGEBOW) || weaponMode.equals(Constants.LANKA)) {
			finalIterationTime = ((finalMag) / finalFireRate) + finalReloadTime;
		} else {
			finalIterationTime = ((finalMag - 1) / finalFireRate) + finalReloadTime;
		}
		finalIterationsPerMinute = 60.0 / finalIterationTime;

		averageCritMult = 1 - Math.min(1, finalCritChance) + (finalCritChance + vigilante) * finalCritMult;

	}

	/**
	 * Calculates miscellaneous values
	 */
	protected static void calculateMiscValues() {

		double totalPhysical = Main.impact.finalBase + Main.puncture.finalBase + Main.slash.finalBase;
		double totalElemental = Main.raw.finalBase - totalPhysical;
		double slashProcRate = (4 * Main.slash.finalBase) / ((4 * totalPhysical) + totalElemental);
		double fireProcRate = (Main.fire.finalBase / ((4 * totalPhysical) + totalElemental));
		double toxinProcRate = (Main.toxin.finalBase / ((4 * totalPhysical) + totalElemental));
		double gasProcRate = (Main.gas.finalBase / ((4 * totalPhysical) + totalElemental));
		averageProjectileCount = finalProjectileCount;
		if (weaponMode.equals(Constants.FULL_AUTO_BULLET_RAMP)) { // kohm's average projectile count
			averageProjectileCount = finalProjectileCount * ((((projectileCount * (projectileCount + 1) / 2) + projectileCount * (finalMag - projectileCount)) / finalMag) / projectileCount);
		}
		procsPerSecond = ((averageProjectileCount * finalMag) * finalStatusChance) * (1 / finalIterationTime);
		burstProcsPerSecond = ((averageProjectileCount * finalMag) * finalStatusChance) * (1 / (finalMag / finalFireRate));

		if (slash.finalBase > 0.0 || hunterMunitions > 0) {
			slashStacks = procsPerSecond * slashProcRate * 6 * finalStatusDuration;
			slashStacks += hunterMunitions * Math.max(1, finalCritChance) * ((averageProjectileCount * finalMag) * (1 / finalIterationTime)) * 6 * finalStatusDuration;
		}
		if (fire.finalBase > 0.0) {
			fireStacks = 1 - Math.pow((1 - fireProcRate), (((averageProjectileCount * finalMag) * (1 / finalIterationTime)) * 6 * finalStatusDuration));
		}
		if (toxin.finalBase > 0.0) {
			toxinStacks = procsPerSecond * toxinProcRate * 8 * finalStatusDuration;
		}
		if (weaponName.equals("Hystrix (Poison)") || weaponName.equals("Acrid")) {
			toxinStacks += ((averageProjectileCount * finalMag) * (1 / finalIterationTime)) * 8 * finalStatusDuration;
		}
		if (gas.finalBase > 0.0) {
			gasStacks = procsPerSecond * gasProcRate * 8 * finalStatusDuration;
		}

	}

	/**
	 * Calculates the damage per shot values
	 */
	protected static void calculateDamagePerShot() {

		// Calculate base damage per shot values
		impact.perShot = (impact.finalBase * averageProjectileCount) * finalDeadAimMult;
		puncture.perShot = (puncture.finalBase * averageProjectileCount) * finalDeadAimMult;
		slash.perShot = (slash.finalBase * averageProjectileCount) * finalDeadAimMult;
		fire.perShot = (fire.finalBase * averageProjectileCount) * finalDeadAimMult;
		ice.perShot = (ice.finalBase * averageProjectileCount) * finalDeadAimMult;
		electric.perShot = (electric.finalBase * averageProjectileCount) * finalDeadAimMult;
		toxin.perShot = (toxin.finalBase * averageProjectileCount) * finalDeadAimMult;
		blast.perShot = (blast.finalBase * averageProjectileCount) * finalDeadAimMult;
		magnetic.perShot = (magnetic.finalBase * averageProjectileCount) * finalDeadAimMult;
		gas.perShot = (gas.finalBase * averageProjectileCount) * finalDeadAimMult;
		radiation.perShot = (radiation.finalBase * averageProjectileCount) * finalDeadAimMult;
		corrosive.perShot = (corrosive.finalBase * averageProjectileCount) * finalDeadAimMult;
		viral.perShot = (viral.finalBase * averageProjectileCount) * finalDeadAimMult;
		raw.perShot = impact.perShot + puncture.perShot + slash.perShot + fire.perShot + ice.perShot + electric.perShot + toxin.perShot + blast.perShot + magnetic.perShot + gas.perShot + radiation.perShot + corrosive.perShot + viral.perShot;

		// Surface-specific
		corpus.perShot = raw.perShot * finalCorpusMult;
		grineer.perShot = raw.perShot * finalGrineerMult;

		infested.perShot += impact.perShot;
		infested.perShot += puncture.perShot;
		infested.perShot += slash.perShot * 1.25;
		infested.perShot += fire.perShot * 1.25;
		infested.perShot += ice.perShot;
		infested.perShot += electric.perShot;
		infested.perShot += toxin.perShot;
		infested.perShot += blast.perShot;
		infested.perShot += magnetic.perShot;
		infested.perShot += gas.perShot * 1.75;
		infested.perShot += radiation.perShot * 0.5;
		infested.perShot += corrosive.perShot;
		infested.perShot += viral.perShot * 0.5;
		infested.perShot *= finalInfestedMult;

		cloneFlesh.perShot += impact.perShot * 0.75;
		cloneFlesh.perShot += puncture.perShot;
		cloneFlesh.perShot += slash.perShot * 1.25;
		cloneFlesh.perShot += fire.perShot * 1.25;
		cloneFlesh.perShot += ice.perShot;
		cloneFlesh.perShot += electric.perShot;
		cloneFlesh.perShot += toxin.perShot;
		cloneFlesh.perShot += blast.perShot;
		cloneFlesh.perShot += magnetic.perShot;
		cloneFlesh.perShot += gas.perShot * 0.5;
		cloneFlesh.perShot += radiation.perShot;
		cloneFlesh.perShot += corrosive.perShot;
		cloneFlesh.perShot += viral.perShot * 1.75;
		cloneFlesh.perShot *= finalGrineerMult;

		ferrite.perShot += impact.perShot;
		ferrite.perShot += puncture.perShot * 1.5;
		ferrite.perShot += slash.perShot * 0.85;
		ferrite.perShot += fire.perShot;
		ferrite.perShot += ice.perShot;
		ferrite.perShot += electric.perShot;
		ferrite.perShot += toxin.perShot * 1.25;
		ferrite.perShot += blast.perShot * 0.75;
		ferrite.perShot += magnetic.perShot;
		ferrite.perShot += gas.perShot;
		ferrite.perShot += radiation.perShot;
		ferrite.perShot += corrosive.perShot * 1.75;
		ferrite.perShot += viral.perShot;

		alloy.perShot += impact.perShot;
		alloy.perShot += puncture.perShot * 1.15;
		alloy.perShot += slash.perShot * 0.5;
		alloy.perShot += fire.perShot;
		alloy.perShot += ice.perShot * 1.25;
		alloy.perShot += electric.perShot * 0.5;
		alloy.perShot += toxin.perShot;
		alloy.perShot += blast.perShot;
		alloy.perShot += magnetic.perShot * 0.5;
		alloy.perShot += gas.perShot;
		alloy.perShot += radiation.perShot * 1.75;
		alloy.perShot += corrosive.perShot;
		alloy.perShot += viral.perShot;

		mechanical.perShot += impact.perShot * 1.25;
		mechanical.perShot += puncture.perShot;
		mechanical.perShot += slash.perShot;
		mechanical.perShot += fire.perShot;
		mechanical.perShot += ice.perShot;
		mechanical.perShot += electric.perShot * 1.5;
		mechanical.perShot += toxin.perShot * 0.75;
		mechanical.perShot += blast.perShot * 1.75;
		mechanical.perShot += magnetic.perShot;
		mechanical.perShot += gas.perShot;
		mechanical.perShot += radiation.perShot;
		mechanical.perShot += corrosive.perShot;
		mechanical.perShot += viral.perShot * 0.75;
		mechanical.perShot *= finalGrineerMult;

		corpusFlesh.perShot += impact.perShot * 0.75;
		corpusFlesh.perShot += puncture.perShot;
		corpusFlesh.perShot += slash.perShot * 1.25;
		corpusFlesh.perShot += fire.perShot;
		corpusFlesh.perShot += ice.perShot;
		corpusFlesh.perShot += electric.perShot;
		corpusFlesh.perShot += toxin.perShot * 1.5;
		corpusFlesh.perShot += blast.perShot;
		corpusFlesh.perShot += magnetic.perShot;
		corpusFlesh.perShot += gas.perShot * 0.75;
		corpusFlesh.perShot += radiation.perShot;
		corpusFlesh.perShot += corrosive.perShot;
		corpusFlesh.perShot += viral.perShot * 1.5;
		corpusFlesh.perShot *= finalCorpusMult;

		shield.perShot += impact.perShot * 1.5;
		shield.perShot += puncture.perShot * 0.85;
		shield.perShot += slash.perShot;
		shield.perShot += fire.perShot;
		shield.perShot += ice.perShot * 1.5;
		shield.perShot += electric.perShot;
		shield.perShot += toxin.perShot;
		shield.perShot += blast.perShot;
		shield.perShot += magnetic.perShot * 1.75;
		shield.perShot += gas.perShot;
		shield.perShot += radiation.perShot * 0.75;
		shield.perShot += corrosive.perShot;
		shield.perShot += viral.perShot;
		shield.perShot *= finalCorpusMult;

		protoShield.perShot += impact.perShot * 1.15;
		protoShield.perShot += puncture.perShot * 0.5;
		protoShield.perShot += slash.perShot;
		protoShield.perShot += fire.perShot * 0.5;
		protoShield.perShot += ice.perShot;
		protoShield.perShot += electric.perShot;
		protoShield.perShot += toxin.perShot * 1.25;
		protoShield.perShot += blast.perShot;
		protoShield.perShot += magnetic.perShot * 1.75;
		protoShield.perShot += gas.perShot;
		protoShield.perShot += radiation.perShot;
		protoShield.perShot += corrosive.perShot * 0.5;
		protoShield.perShot += viral.perShot;
		protoShield.perShot *= finalCorpusMult;

		robotic.perShot += impact.perShot;
		robotic.perShot += puncture.perShot * 1.25;
		robotic.perShot += slash.perShot * 0.75;
		robotic.perShot += fire.perShot;
		robotic.perShot += ice.perShot;
		robotic.perShot += electric.perShot * 1.5;
		robotic.perShot += toxin.perShot * 0.75;
		robotic.perShot += blast.perShot;
		robotic.perShot += magnetic.perShot;
		robotic.perShot += gas.perShot;
		robotic.perShot += radiation.perShot * 1.25;
		robotic.perShot += corrosive.perShot;
		robotic.perShot += viral.perShot;
		robotic.perShot *= finalCorpusMult;

		infestedFlesh.perShot += impact.perShot;
		infestedFlesh.perShot += puncture.perShot;
		infestedFlesh.perShot += slash.perShot * 1.5;
		infestedFlesh.perShot += fire.perShot * 1.5;
		infestedFlesh.perShot += ice.perShot * 0.5;
		infestedFlesh.perShot += electric.perShot;
		infestedFlesh.perShot += toxin.perShot;
		infestedFlesh.perShot += blast.perShot;
		infestedFlesh.perShot += magnetic.perShot;
		infestedFlesh.perShot += gas.perShot * 1.5;
		infestedFlesh.perShot += radiation.perShot;
		infestedFlesh.perShot += corrosive.perShot;
		infestedFlesh.perShot += viral.perShot;
		infestedFlesh.perShot *= finalInfestedMult;

		fossilized.perShot += impact.perShot;
		fossilized.perShot += puncture.perShot;
		fossilized.perShot += slash.perShot * 1.15;
		fossilized.perShot += fire.perShot;
		fossilized.perShot += ice.perShot * 0.75;
		fossilized.perShot += electric.perShot;
		fossilized.perShot += toxin.perShot * 0.5;
		fossilized.perShot += blast.perShot * 1.5;
		fossilized.perShot += magnetic.perShot;
		fossilized.perShot += gas.perShot;
		fossilized.perShot += radiation.perShot * 0.25;
		fossilized.perShot += corrosive.perShot * 1.75;
		fossilized.perShot += viral.perShot;
		fossilized.perShot *= finalInfestedMult;

		sinew.perShot += impact.perShot;
		sinew.perShot += puncture.perShot * 1.25;
		sinew.perShot += slash.perShot;
		sinew.perShot += fire.perShot;
		sinew.perShot += ice.perShot * 1.25;
		sinew.perShot += electric.perShot;
		sinew.perShot += toxin.perShot;
		sinew.perShot += blast.perShot * 0.5;
		sinew.perShot += magnetic.perShot;
		sinew.perShot += gas.perShot;
		sinew.perShot += radiation.perShot * 1.5;
		sinew.perShot += corrosive.perShot;
		sinew.perShot += viral.perShot;
		sinew.perShot *= finalInfestedMult;

		// Calculate crit damage per shot values
		raw.critPerShot = raw.perShot * finalCritMult;
		impact.critPerShot = impact.perShot * finalCritMult;
		puncture.critPerShot = puncture.perShot * finalCritMult;
		slash.critPerShot = slash.perShot * finalCritMult;
		fire.critPerShot = fire.perShot * finalCritMult;
		ice.critPerShot = ice.perShot * finalCritMult;
		electric.critPerShot = electric.perShot * finalCritMult;
		toxin.critPerShot = toxin.perShot * finalCritMult;
		blast.critPerShot = blast.perShot * finalCritMult;
		magnetic.critPerShot = magnetic.perShot * finalCritMult;
		gas.critPerShot = gas.perShot * finalCritMult;
		radiation.critPerShot = radiation.perShot * finalCritMult;
		corrosive.critPerShot = corrosive.perShot * finalCritMult;
		viral.critPerShot = viral.perShot * finalCritMult;
		corpus.critPerShot = corpus.perShot * finalCritMult;
		grineer.critPerShot = grineer.perShot * finalCritMult;
		infested.critPerShot = infested.perShot * finalCritMult;
		cloneFlesh.critPerShot = cloneFlesh.perShot * finalCritMult;
		ferrite.critPerShot = ferrite.perShot * finalCritMult;
		alloy.critPerShot = alloy.perShot * finalCritMult;
		mechanical.critPerShot = mechanical.perShot * finalCritMult;
		corpusFlesh.critPerShot = corpusFlesh.perShot * finalCritMult;
		shield.critPerShot = shield.perShot * finalCritMult;
		protoShield.critPerShot = protoShield.perShot * finalCritMult;
		robotic.critPerShot = robotic.perShot * finalCritMult;
		infestedFlesh.critPerShot = infestedFlesh.perShot * finalCritMult;
		fossilized.critPerShot = fossilized.perShot * finalCritMult;
		sinew.critPerShot = sinew.perShot * finalCritMult;

		finalFirstShotDamageMult -= 1;
		// Calculate first-shot damage
		raw.firstShot = raw.perShot * averageCritMult * finalFirstShotDamageMult;
		impact.firstShot = impact.perShot * averageCritMult * finalFirstShotDamageMult;
		puncture.firstShot = puncture.perShot * averageCritMult * finalFirstShotDamageMult;
		slash.firstShot = slash.perShot * averageCritMult * finalFirstShotDamageMult;
		fire.firstShot = fire.perShot * averageCritMult * finalFirstShotDamageMult;
		ice.firstShot = ice.perShot * averageCritMult * finalFirstShotDamageMult;
		electric.firstShot = electric.perShot * averageCritMult * finalFirstShotDamageMult;
		toxin.firstShot = toxin.perShot * averageCritMult * finalFirstShotDamageMult;
		blast.firstShot = blast.perShot * averageCritMult * finalFirstShotDamageMult;
		magnetic.firstShot = magnetic.perShot * averageCritMult * finalFirstShotDamageMult;
		gas.firstShot = gas.perShot * averageCritMult * finalFirstShotDamageMult;
		radiation.firstShot = radiation.perShot * averageCritMult * finalFirstShotDamageMult;
		corrosive.firstShot = corrosive.perShot * averageCritMult * finalFirstShotDamageMult;
		viral.firstShot = viral.perShot * averageCritMult * finalFirstShotDamageMult;
		corpus.firstShot = corpus.perShot * averageCritMult * finalFirstShotDamageMult;
		grineer.firstShot = grineer.perShot * averageCritMult * finalFirstShotDamageMult;
		infested.firstShot = infested.perShot * averageCritMult * finalFirstShotDamageMult;
		cloneFlesh.firstShot = cloneFlesh.perShot * averageCritMult * finalFirstShotDamageMult;
		ferrite.firstShot = ferrite.perShot * averageCritMult * finalFirstShotDamageMult;
		alloy.firstShot = alloy.perShot * averageCritMult * finalFirstShotDamageMult;
		mechanical.firstShot = mechanical.perShot * averageCritMult * finalFirstShotDamageMult;
		corpusFlesh.firstShot = corpusFlesh.perShot * averageCritMult * finalFirstShotDamageMult;
		shield.firstShot = shield.perShot * averageCritMult * finalFirstShotDamageMult;
		protoShield.firstShot = protoShield.perShot * averageCritMult * finalFirstShotDamageMult;
		robotic.firstShot = robotic.perShot * averageCritMult * finalFirstShotDamageMult;
		infestedFlesh.firstShot = infestedFlesh.perShot * averageCritMult * finalFirstShotDamageMult;
		fossilized.firstShot = fossilized.perShot * averageCritMult * finalFirstShotDamageMult;
		sinew.firstShot = sinew.perShot * averageCritMult * finalFirstShotDamageMult;

		finalLastShotDamageMult -= 1;
		// Calculate last-shot damage
		raw.lastShot = raw.perShot * averageCritMult * finalLastShotDamageMult;
		impact.lastShot = impact.perShot * averageCritMult * finalLastShotDamageMult;
		puncture.lastShot = puncture.perShot * averageCritMult * finalLastShotDamageMult;
		slash.lastShot = slash.perShot * averageCritMult * finalLastShotDamageMult;
		fire.lastShot = fire.perShot * averageCritMult * finalLastShotDamageMult;
		ice.lastShot = ice.perShot * averageCritMult * finalLastShotDamageMult;
		electric.lastShot = electric.perShot * averageCritMult * finalLastShotDamageMult;
		toxin.lastShot = toxin.perShot * averageCritMult * finalLastShotDamageMult;
		blast.lastShot = blast.perShot * averageCritMult * finalLastShotDamageMult;
		magnetic.lastShot = magnetic.perShot * averageCritMult * finalLastShotDamageMult;
		gas.lastShot = gas.perShot * averageCritMult * finalLastShotDamageMult;
		radiation.lastShot = radiation.perShot * averageCritMult * finalLastShotDamageMult;
		corrosive.lastShot = corrosive.perShot * averageCritMult * finalLastShotDamageMult;
		viral.lastShot = viral.perShot * averageCritMult * finalLastShotDamageMult;
		corpus.lastShot = corpus.perShot * averageCritMult * finalLastShotDamageMult;
		grineer.lastShot = grineer.perShot * averageCritMult * finalLastShotDamageMult;
		infested.lastShot = infested.perShot * averageCritMult * finalLastShotDamageMult;
		cloneFlesh.lastShot = cloneFlesh.perShot * averageCritMult * finalLastShotDamageMult;
		ferrite.lastShot = ferrite.perShot * averageCritMult * finalLastShotDamageMult;
		alloy.lastShot = alloy.perShot * averageCritMult * finalLastShotDamageMult;
		mechanical.lastShot = mechanical.perShot * averageCritMult * finalLastShotDamageMult;
		corpusFlesh.lastShot = corpusFlesh.perShot * averageCritMult * finalLastShotDamageMult;
		shield.lastShot = shield.perShot * averageCritMult * finalLastShotDamageMult;
		protoShield.lastShot = protoShield.perShot * averageCritMult * finalLastShotDamageMult;
		robotic.lastShot = robotic.perShot * averageCritMult * finalLastShotDamageMult;
		infestedFlesh.lastShot = infestedFlesh.perShot * averageCritMult * finalLastShotDamageMult;
		fossilized.lastShot = fossilized.perShot * averageCritMult * finalLastShotDamageMult;
		sinew.lastShot = sinew.perShot * averageCritMult * finalLastShotDamageMult;

	}

	/**
	 * Calculates the total damage done over an entire magazine
	 */
	protected static void calculateDamagePerIteration() {
		raw.perIteration = raw.perShot * finalMag * averageCritMult + raw.firstShot + raw.lastShot;
		impact.perIteration = impact.perShot * finalMag * averageCritMult + impact.firstShot + impact.lastShot;
		puncture.perIteration = puncture.perShot * finalMag * averageCritMult + puncture.firstShot + puncture.lastShot;
		slash.perIteration = slash.perShot * finalMag * averageCritMult + slash.firstShot + slash.lastShot;
		fire.perIteration = fire.perShot * finalMag * averageCritMult + fire.firstShot + fire.lastShot;
		ice.perIteration = ice.perShot * finalMag * averageCritMult + ice.firstShot + ice.lastShot;
		electric.perIteration = electric.perShot * finalMag * averageCritMult + electric.firstShot + electric.lastShot;
		toxin.perIteration = toxin.perShot * finalMag * averageCritMult + toxin.firstShot + toxin.lastShot;
		blast.perIteration = blast.perShot * finalMag * averageCritMult + blast.firstShot + blast.lastShot;
		magnetic.perIteration = magnetic.perShot * finalMag * averageCritMult + magnetic.firstShot + magnetic.lastShot;
		gas.perIteration = gas.perShot * finalMag * averageCritMult + gas.firstShot + gas.lastShot;
		radiation.perIteration = radiation.perShot * finalMag * averageCritMult + radiation.firstShot + radiation.lastShot;
		corrosive.perIteration = corrosive.perShot * finalMag * averageCritMult + corrosive.firstShot + corrosive.lastShot;
		viral.perIteration = viral.perShot * finalMag * averageCritMult + viral.firstShot + viral.lastShot;
		corpus.perIteration = corpus.perShot * finalMag * averageCritMult + corpus.firstShot + corpus.lastShot;
		grineer.perIteration = grineer.perShot * finalMag * averageCritMult + grineer.firstShot + grineer.lastShot;
		infested.perIteration = infested.perShot * finalMag * averageCritMult + infested.firstShot + infested.lastShot;
		cloneFlesh.perIteration = cloneFlesh.perShot * finalMag * averageCritMult + cloneFlesh.firstShot + cloneFlesh.lastShot;
		ferrite.perIteration = ferrite.perShot * finalMag * averageCritMult + ferrite.firstShot + ferrite.lastShot;
		alloy.perIteration = alloy.perShot * finalMag * averageCritMult + alloy.firstShot + alloy.lastShot;
		mechanical.perIteration = mechanical.perShot * finalMag * averageCritMult + mechanical.firstShot + mechanical.lastShot;
		corpusFlesh.perIteration = corpusFlesh.perShot * finalMag * averageCritMult + corpusFlesh.firstShot + corpusFlesh.lastShot;
		shield.perIteration = shield.perShot * finalMag * averageCritMult + shield.firstShot + shield.lastShot;
		protoShield.perIteration = protoShield.perShot * finalMag * averageCritMult + protoShield.firstShot + protoShield.lastShot;
		robotic.perIteration = robotic.perShot * finalMag * averageCritMult + robotic.firstShot + robotic.lastShot;
		infestedFlesh.perIteration = infestedFlesh.perShot * finalMag * averageCritMult + infestedFlesh.firstShot + infestedFlesh.lastShot;
		fossilized.perIteration = fossilized.perShot * finalMag * averageCritMult + fossilized.firstShot + fossilized.lastShot;
		sinew.perIteration = sinew.perShot * finalMag * averageCritMult + sinew.firstShot + sinew.lastShot;
	}

	/**
	 * Calculates the total damage dealt over a given minute.
	 */
	protected static void calculateDamagePerMinute() {
		raw.perMinute = raw.perIteration * finalIterationsPerMinute;
		impact.perMinute = impact.perIteration * finalIterationsPerMinute;
		puncture.perMinute = puncture.perIteration * finalIterationsPerMinute;
		slash.perMinute = slash.perIteration * finalIterationsPerMinute;
		fire.perMinute = fire.perIteration * finalIterationsPerMinute;
		ice.perMinute = ice.perIteration * finalIterationsPerMinute;
		electric.perMinute = electric.perIteration * finalIterationsPerMinute;
		toxin.perMinute = toxin.perIteration * finalIterationsPerMinute;
		blast.perMinute = blast.perIteration * finalIterationsPerMinute;
		magnetic.perMinute = magnetic.perIteration * finalIterationsPerMinute;
		gas.perMinute = gas.perIteration * finalIterationsPerMinute;
		radiation.perMinute = radiation.perIteration * finalIterationsPerMinute;
		corrosive.perMinute = corrosive.perIteration * finalIterationsPerMinute;
		viral.perMinute = viral.perIteration * finalIterationsPerMinute;
		corpus.perMinute = corpus.perIteration * finalIterationsPerMinute;
		grineer.perMinute = grineer.perIteration * finalIterationsPerMinute;
		infested.perMinute = infested.perIteration * finalIterationsPerMinute;
		cloneFlesh.perMinute = cloneFlesh.perIteration * finalIterationsPerMinute;
		ferrite.perMinute = ferrite.perIteration * finalIterationsPerMinute;
		alloy.perMinute = alloy.perIteration * finalIterationsPerMinute;
		mechanical.perMinute = mechanical.perIteration * finalIterationsPerMinute;
		corpus.perMinute = corpusFlesh.perIteration * finalIterationsPerMinute;
		shield.perMinute = shield.perIteration * finalIterationsPerMinute;
		protoShield.perMinute = protoShield.perIteration * finalIterationsPerMinute;
		robotic.perMinute = robotic.perIteration * finalIterationsPerMinute;
		infestedFlesh.perMinute = infestedFlesh.perIteration * finalIterationsPerMinute;
		fossilized.perMinute = fossilized.perIteration * finalIterationsPerMinute;
		sinew.perMinute = sinew.perIteration * finalIterationsPerMinute;
	}

	protected static void calculateDamagePerSecond() {
		// Calculate base DPS values
		raw.perSecond = raw.perMinute / 60.0;
		impact.perSecond = impact.perMinute / 60.0;
		puncture.perSecond = puncture.perMinute / 60.0;
		slash.perSecond = slash.perMinute / 60.0;
		fire.perSecond = fire.perMinute / 60.0;
		ice.perSecond = ice.perMinute / 60.0;
		electric.perSecond = electric.perMinute / 60.0;
		toxin.perSecond = toxin.perMinute / 60.0;
		blast.perSecond = blast.perMinute / 60.0;
		magnetic.perSecond = magnetic.perMinute / 60.0;
		gas.perSecond = gas.perMinute / 60.0;
		radiation.perSecond = radiation.perMinute / 60.0;
		corrosive.perSecond = corrosive.perMinute / 60.0;
		viral.perSecond = viral.perMinute / 60.0;
		corpus.perSecond = corpus.perMinute / 60.0;
		grineer.perSecond = grineer.perMinute / 60.0;
		infested.perSecond = infested.perMinute / 60.0;
		cloneFlesh.perSecond = cloneFlesh.perMinute / 60.0;
		ferrite.perSecond = ferrite.perMinute / 60.0;
		alloy.perSecond = alloy.perMinute / 60.0;
		mechanical.perSecond = mechanical.perMinute / 60.0;
		corpusFlesh.perSecond = corpus.perMinute / 60.0;
		shield.perSecond = shield.perMinute / 60.0;
		protoShield.perSecond = protoShield.perMinute / 60.0;
		robotic.perSecond = robotic.perMinute / 60.0;
		infestedFlesh.perSecond = infestedFlesh.perMinute / 60.0;
		fossilized.perSecond = fossilized.perMinute / 60.0;
		sinew.perSecond = sinew.perMinute / 60.0;

		// Add in DoTs

		double totalPhysical = Main.impact.finalBase + Main.puncture.finalBase + Main.slash.finalBase;
		double totalElemental = Main.raw.finalBase - totalPhysical;
		double ElectricProcRate = (Main.electric.finalBase / ((4 * totalPhysical) + totalElemental)) * finalStatusChance;
		double GasProcRate = (Main.gas.finalBase / ((4 * totalPhysical) + totalElemental)) * finalStatusChance;
		double SlashProcRate = (4 * Main.slash.finalBase) / ((4 * totalPhysical) + totalElemental) * finalStatusChance;

		double hunterMult = 1;
		if (hunterMunitions > 0) { // Need to fix because hunter munitions stacks are always on crit
			double hunterRatio = (Math.min(1, finalCritChance) * 0.3 / (Math.min(1, finalCritChance) * 0.3 + SlashProcRate));
			hunterMult = (hunterRatio * finalCritMult + (1 - hunterRatio) * averageCritMult) / averageCritMult;
		}
		double rawBase = (raw.base * finalDamageMult) * finalDeadAimMult * (1 + (finalFirstShotDamageMult + finalLastShotDamageMult) / finalMag);
		double DoTBase = rawBase * averageCritMult * (1 + (finalFirstShotDamageMult + finalLastShotDamageMult) / finalMag);
		double electricBase = DoTBase * (1 + globalElectric) * 0.5 * (1 + (finalFirstShotDamageMult + finalLastShotDamageMult) / finalMag);
		double bleedDamage = DoTBase * 0.35 * hunterMult * (1 + (finalFirstShotDamageMult + finalLastShotDamageMult) / finalMag);
		double poisonDamage = (DoTBase * (1 + globalToxin)) * 0.5 * (1 + (finalFirstShotDamageMult + finalLastShotDamageMult) / finalMag);
		double heatDamage = (DoTBase * (1 + globalFire)) * 0.5 * (1 + (finalFirstShotDamageMult + finalLastShotDamageMult) / finalMag);
		double cloudDamage = DoTBase * (0.25 * (1 + globalToxin) * (1 + globalToxin)) * (1 + (finalFirstShotDamageMult + finalLastShotDamageMult) / finalMag);
		bleedDoTDPS = slashStacks * bleedDamage * (7 / 6);
		poisonDoTDPS = toxinStacks * poisonDamage * (9 / 8);
		heatDoTDPS = fireStacks * heatDamage * (7 / 6);
		cloudDoTDPS = gasStacks * cloudDamage * (9 / 8);
		electricProcDPS = ElectricProcRate * electricBase * averageProjectileCount * finalFireRate;
		gasProcDPS = GasProcRate * poisonDamage * averageProjectileCount * finalFireRate;

		raw.perSecond += (bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS + electricProcDPS + gasProcDPS);
		corpus.perSecond += (bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS * finalCorpusMult * finalCorpusMult + electricProcDPS + gasProcDPS * finalCorpusMult);
		grineer.perSecond += (bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS * finalGrineerMult * finalGrineerMult + electricProcDPS + gasProcDPS * finalGrineerMult);
		infested.perSecond += (bleedDoTDPS + poisonDoTDPS + (heatDoTDPS * 1.25) + cloudDoTDPS * finalInfestedMult * finalInfestedMult + electricProcDPS + gasProcDPS * finalInfestedMult);
		cloneFlesh.perSecond += (bleedDoTDPS + poisonDoTDPS + (heatDoTDPS * 1.25) + cloudDoTDPS * finalGrineerMult * finalGrineerMult + electricProcDPS + gasProcDPS * finalGrineerMult);
		ferrite.perSecond += (bleedDoTDPS + (poisonDoTDPS * 1.25) + heatDoTDPS + (cloudDoTDPS * 1.25) + electricProcDPS + gasProcDPS * 1.25);
		alloy.perSecond += (bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS + (electricProcDPS * 0.5) + gasProcDPS);
		mechanical.perSecond += (bleedDoTDPS + (poisonDoTDPS * 0.75) + heatDoTDPS + (cloudDoTDPS * 0.75 * finalGrineerMult * finalGrineerMult) + (electricProcDPS * 1.5) + gasProcDPS * 0.75 * finalGrineerMult);
		corpusFlesh.perSecond += (bleedDoTDPS + (poisonDoTDPS * 1.5) + heatDoTDPS + (cloudDoTDPS * 1.5 * finalCorpusMult * finalCorpusMult) + electricProcDPS + gasProcDPS * 1.5 * finalCorpusMult);
		shield.perSecond += (heatDoTDPS + electricProcDPS);
		protoShield.perSecond += ((heatDoTDPS * 0.5) + electricProcDPS);
		robotic.perSecond += (bleedDoTDPS + (poisonDoTDPS * 0.75) + heatDoTDPS + (cloudDoTDPS * 0.75 * finalCorpusMult * finalCorpusMult) + (electricProcDPS * 1.5) + gasProcDPS * 0.75 * finalCorpusMult);
		infestedFlesh.perSecond += (bleedDoTDPS + poisonDoTDPS + (heatDoTDPS * 1.5) + cloudDoTDPS * finalInfestedMult * finalInfestedMult + electricProcDPS + gasProcDPS * finalInfestedMult);
		fossilized.perSecond += (bleedDoTDPS + (poisonDoTDPS * 0.5) + heatDoTDPS + (cloudDoTDPS * finalInfestedMult * finalInfestedMult * 0.5) + electricProcDPS + gasProcDPS * finalInfestedMult * 0.5);
		sinew.perSecond += (bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS * finalInfestedMult * finalInfestedMult + electricProcDPS + gasProcDPS * finalInfestedMult);
	}

	protected static void calculateBurstDamagePerSecond() {
		// Calculate base Burst DPS values
		double burstTime = (1 / (finalIterationTime - finalReloadTime));
		raw.rawPerSecond = raw.perIteration * burstTime;
		impact.rawPerSecond = impact.perIteration * burstTime;
		puncture.rawPerSecond = puncture.perIteration * burstTime;
		slash.rawPerSecond = slash.perIteration * burstTime;
		fire.rawPerSecond = fire.perIteration * burstTime;
		ice.rawPerSecond = ice.perIteration * burstTime;
		electric.rawPerSecond = electric.perIteration * burstTime;
		toxin.rawPerSecond = toxin.perIteration * burstTime;
		blast.rawPerSecond = blast.perIteration * burstTime;
		magnetic.rawPerSecond = magnetic.perIteration * burstTime;
		gas.rawPerSecond = gas.perIteration * burstTime;
		radiation.rawPerSecond = radiation.perIteration * burstTime;
		corrosive.rawPerSecond = corrosive.perIteration * burstTime;
		viral.rawPerSecond = viral.perIteration * burstTime;
		corpus.rawPerSecond = corpus.perIteration * burstTime;
		grineer.rawPerSecond = grineer.perIteration * burstTime;
		cloneFlesh.rawPerSecond = cloneFlesh.perIteration * burstTime;
		infested.rawPerSecond = infested.perIteration * burstTime;
		ferrite.rawPerSecond = ferrite.perIteration * burstTime;
		alloy.rawPerSecond = alloy.perIteration * burstTime;
		mechanical.rawPerSecond = mechanical.perIteration * burstTime;
		corpusFlesh.rawPerSecond = corpusFlesh.perIteration * burstTime;
		shield.rawPerSecond = shield.perIteration * burstTime;
		protoShield.rawPerSecond = protoShield.perIteration * burstTime;
		robotic.rawPerSecond = robotic.perIteration * burstTime;
		infestedFlesh.rawPerSecond = infestedFlesh.perIteration * burstTime;
		fossilized.rawPerSecond = fossilized.perIteration * burstTime;
		sinew.rawPerSecond = sinew.perIteration * burstTime;

		// Add in DoTs
		raw.rawPerSecond += (bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS + electricProcDPS + gasProcDPS);
		corpus.rawPerSecond += (bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS * finalCorpusMult * finalCorpusMult + electricProcDPS + gasProcDPS * finalCorpusMult);
		grineer.rawPerSecond += (bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS * finalGrineerMult * finalGrineerMult + electricProcDPS + gasProcDPS * finalGrineerMult);
		infested.rawPerSecond += (bleedDoTDPS + poisonDoTDPS + (heatDoTDPS * 1.25) + cloudDoTDPS * finalInfestedMult * finalInfestedMult + electricProcDPS + gasProcDPS * finalInfestedMult);
		cloneFlesh.rawPerSecond += (bleedDoTDPS + poisonDoTDPS + (heatDoTDPS * 1.25) + cloudDoTDPS * finalGrineerMult * finalGrineerMult + electricProcDPS + gasProcDPS * finalGrineerMult);
		ferrite.rawPerSecond += (bleedDoTDPS + (poisonDoTDPS * 1.25) + heatDoTDPS + (cloudDoTDPS * 1.25) + electricProcDPS + gasProcDPS * 1.25);
		alloy.rawPerSecond += (bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS + (electricProcDPS * 0.5) + gasProcDPS);
		mechanical.rawPerSecond += (bleedDoTDPS + (poisonDoTDPS * 0.75) + heatDoTDPS + (cloudDoTDPS * 0.75 * finalGrineerMult * finalGrineerMult) + (electricProcDPS * 1.5) + gasProcDPS * 0.75 * finalGrineerMult);
		corpusFlesh.rawPerSecond += (bleedDoTDPS + (poisonDoTDPS * 1.5) + heatDoTDPS + (cloudDoTDPS * 1.5 * finalCorpusMult * finalCorpusMult) + electricProcDPS + gasProcDPS * 1.5 * finalCorpusMult);
		shield.rawPerSecond += (heatDoTDPS + electricProcDPS);
		protoShield.rawPerSecond += ((heatDoTDPS * 0.5) + electricProcDPS);
		robotic.rawPerSecond += (bleedDoTDPS + (poisonDoTDPS * 0.75) + heatDoTDPS + (cloudDoTDPS * 0.75 * finalCorpusMult * finalCorpusMult) + (electricProcDPS * 1.5) + gasProcDPS * 0.75 * finalCorpusMult);
		infestedFlesh.rawPerSecond += (bleedDoTDPS + poisonDoTDPS + (heatDoTDPS * 1.5) + cloudDoTDPS * finalInfestedMult * finalInfestedMult + electricProcDPS + gasProcDPS * finalInfestedMult);
		fossilized.rawPerSecond += (bleedDoTDPS + (poisonDoTDPS * 0.5) + heatDoTDPS + (cloudDoTDPS * finalInfestedMult * finalInfestedMult * 0.5) + electricProcDPS + gasProcDPS * finalInfestedMult * 0.5);
		sinew.rawPerSecond += (bleedDoTDPS + poisonDoTDPS + heatDoTDPS + cloudDoTDPS * finalInfestedMult * finalInfestedMult + electricProcDPS + gasProcDPS * finalInfestedMult);
	}

	/**
	 * Appends the weapon information to the output text area
	 */
	protected static void updateOutput() {
		// Append to Output
		DecimalFormat f = new DecimalFormat("#.###");
		output.append("\n");
		output.append("\n_____________________________________________________________");
		output.append("\nName :: " + weaponName);
		output.append("\nMagazine Size :: " + finalMag);
		output.append("\nTotal Ammo :: " + (finalMag + finalAmmo));
		output.append("\nCrit Chance :: " + f.format(finalCritChance * 100.0) + "%");
		output.append("\nCrit Damage Multiplier :: " + f.format(finalCritMult * 100.0) + "%");
		String delimiter = "rounds";
		String mode = selectedWeapon.getWeaponMode();
		if (mode.equals(Constants.BURST)) {
			delimiter = "bursts";
		} else if (mode.equals(Constants.CONTINUOUS)) {
			delimiter = "ticks";
		}
		output.append("\nFire Rate :: " + f.format(finalFireRate) + " " + delimiter + " per second");
		output.append("\nReload Time :: " + f.format(finalReloadTime) + " seconds");
		output.append("\nStatus Chance :: " + f.format(finalStatusChance * 100.0) + "%");
		output.append("\nAverage Projectiles Per Shot :: " + f.format(averageProjectileCount));
		output.append("\nStatus Procs Per Second :: " + f.format(procsPerSecond));
		output.append("\nBurst Status Procs Per Second :: " + f.format(burstProcsPerSecond));
		output.append("\nTime to Empty magazine :: " + f.format(finalIterationTime - finalReloadTime) + " seconds");
		if (slashStacks > 0) {
			output.append("\nAverage Bleed Stacks :: " + slashStacks);
		}
		if (toxinStacks > 0) {
			output.append("\nAverage Poison Stacks :: " + toxinStacks);
		}
		if (gasStacks > 0) {
			output.append("\nAverage Poison Cloud Stacks :: " + gasStacks);
		}
		if (fireStacks > 0) {
			output.append("\nAverage Burning Stacks :: " + fireStacks);
		}
		output.append("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		output.append("\nRaw Damage Per Shot :: " + f.format(raw.perShot));
		if (impact.perShot > 0.0) {
			output.append("\nImpact Damage Per Shot :: " + f.format(impact.perShot));
		}
		if (puncture.perShot > 0.0) {
			output.append("\nPuncture Damage Per Shot :: " + f.format(puncture.perShot));
		}
		if (slash.perShot > 0.0) {
			output.append("\nSlash Damage Per Shot :: " + f.format(slash.perShot));
		}
		if (fire.perShot > 0.0) {
			output.append("\nFire Damage Per Shot :: " + f.format(fire.perShot));
		}
		if (ice.perShot > 0.0) {
			output.append("\nIce Damage Per Shot :: " + f.format(ice.perShot));
		}
		if (electric.perShot > 0.0) {
			output.append("\nElectric Damage Per Shot :: " + f.format(electric.perShot));
		}
		if (toxin.perShot > 0.0) {
			output.append("\nToxin Damage Per Shot :: " + f.format(toxin.perShot));
		}
		if (blast.perShot > 0.0) {
			output.append("\nBlast Damage Per Shot :: " + f.format(blast.perShot));
		}
		if (magnetic.perShot > 0.0) {
			output.append("\nMagnetic Damage Per Shot :: " + f.format(magnetic.perShot));
		}
		if (gas.perShot > 0.0) {
			output.append("\nGas Damage Per Shot :: " + f.format(gas.perShot));
		}
		if (radiation.perShot > 0.0) {
			output.append("\nRadiation Damage Per Shot :: " + f.format(radiation.perShot));
		}
		if (corrosive.perShot > 0.0) {
			output.append("\nCorrosive Damage Per Shot :: " + f.format(corrosive.perShot));
		}
		if (viral.perShot > 0.0) {
			output.append("\nViral Damage Per Shot :: " + f.format(viral.perShot));
		}
		output.append("\nDamage Per Shot to Clone Flesh :: " + f.format(cloneFlesh.perShot));
		output.append("\nDamage Per Shot to Ferrite Armor :: " + f.format(ferrite.perShot));
		output.append("\nDamage Per Shot to Alloy Armor :: " + f.format(alloy.perShot));
		output.append("\nDamage Per Shot to Mechanical :: " + f.format(mechanical.perShot));
		output.append("\nDamage Per Shot to Corpus Flesh :: " + f.format(corpusFlesh.perShot));
		output.append("\nDamage Per Shot to Shield :: " + f.format(shield.perShot));
		output.append("\nDamage Per Shot to Proto Shield :: " + f.format(protoShield.perShot));
		output.append("\nDamage Per Shot to Robotic :: " + f.format(robotic.perShot));
		output.append("\nDamage Per Shot to Infested Flesh :: " + f.format(infestedFlesh.perShot));
		output.append("\nDamage Per Shot to Fossilized :: " + f.format(fossilized.perShot));
		output.append("\nDamage Per Shot to Sinew :: " + f.format(sinew.perShot));
		output.append("\nDamage Per Shot to Corpus :: " + f.format(corpus.perShot));
		output.append("\nDamage Per Shot to Grineer :: " + f.format(grineer.perShot));
		output.append("\nDamage Per Shot to Infested :: " + f.format(infested.perShot));
		output.append("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		output.append("\nRaw Crit Damage Per Shot :: " + f.format(raw.critPerShot));
		if (impact.critPerShot > 0.0) {
			output.append("\nImpact Crit Damage Per Shot :: " + f.format(impact.critPerShot));
		}
		if (puncture.critPerShot > 0.0) {
			output.append("\nPuncture Crit Damage Per Shot :: " + f.format(puncture.critPerShot));
		}
		if (slash.critPerShot > 0.0) {
			output.append("\nSlash Crit Damage Per Shot :: " + f.format(slash.critPerShot));
		}
		if (fire.critPerShot > 0.0) {
			output.append("\nFire Crit Damage Per Shot :: " + f.format(fire.critPerShot));
		}
		if (ice.critPerShot > 0.0) {
			output.append("\nIce Crit Damage Per Shot :: " + f.format(ice.critPerShot));
		}
		if (electric.critPerShot > 0.0) {
			output.append("\nElectric Crit Damage Per Shot :: " + f.format(electric.critPerShot));
		}
		if (toxin.critPerShot > 0.0) {
			output.append("\nToxin Crit Damage Per Shot :: " + f.format(toxin.critPerShot));
		}
		if (blast.critPerShot > 0.0) {
			output.append("\nBlast Crit Damage Per Shot :: " + f.format(blast.critPerShot));
		}
		if (magnetic.critPerShot > 0.0) {
			output.append("\nMagnetic Crit Damage Per Shot :: " + f.format(magnetic.critPerShot));
		}
		if (gas.critPerShot > 0.0) {
			output.append("\nGas Crit Damage Per Shot :: " + f.format(gas.critPerShot));
		}
		if (radiation.critPerShot > 0.0) {
			output.append("\nRadiation Crit Damage Per Shot :: " + f.format(radiation.critPerShot));
		}
		if (corrosive.critPerShot > 0.0) {
			output.append("\nCorrosive Crit Damage Per Shot :: " + f.format(corrosive.critPerShot));
		}
		if (viral.critPerShot > 0.0) {
			output.append("\nViral Crit Damage Per Shot :: " + f.format(viral.critPerShot));
		}
		output.append("\nCrit Damage Per Shot to Clone Flesh :: " + f.format(cloneFlesh.critPerShot));
		output.append("\nCrit Damage Per Shot to Ferrite Armor :: " + f.format(ferrite.critPerShot));
		output.append("\nCrit Damage Per Shot to Alloy Armor :: " + f.format(alloy.critPerShot));
		output.append("\nCrit Damage Per Shot to Mechanical :: " + f.format(mechanical.critPerShot));
		output.append("\nCrit Damage Per Shot to Corpus Flesh :: " + f.format(corpusFlesh.critPerShot));
		output.append("\nCrit Damage Per Shot to Shield :: " + f.format(shield.critPerShot));
		output.append("\nCrit Damage Per Shot to Proto Shield :: " + f.format(protoShield.critPerShot));
		output.append("\nCrit Damage Per Shot to Robotic :: " + f.format(robotic.critPerShot));
		output.append("\nCrit Damage Per Shot to Infested Flesh :: " + f.format(infestedFlesh.critPerShot));
		output.append("\nCrit Damage Per Shot to Fossilized :: " + f.format(fossilized.critPerShot));
		output.append("\nCrit Damage Per Shot to Sinew :: " + f.format(sinew.critPerShot));
		output.append("\nCrit Damage Per Shot to Corpus :: " + f.format(corpus.critPerShot));
		output.append("\nCrit Damage Per Shot to Grineer :: " + f.format(grineer.critPerShot));
		output.append("\nCrit Damage Per Shot to Infested :: " + f.format(infested.critPerShot));
		if (finalFirstShotDamageMult > 0) {
			output.append("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			output.append("\nRaw First Shot Damage :: " + f.format(raw.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			if (impact.firstShot > 0.0) {
				output.append("\nImpact First Shot Damage :: " + f.format(impact.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (puncture.firstShot > 0.0) {
				output.append("\nPuncture First Shot Damage :: " + f.format(puncture.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (slash.firstShot > 0.0) {
				output.append("\nSlash First Shot Damage :: " + f.format(slash.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (fire.firstShot > 0.0) {
				output.append("\nFire First Shot Damage :: " + f.format(fire.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (ice.firstShot > 0.0) {
				output.append("\nIce First Shot Damage :: " + f.format(ice.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (electric.firstShot > 0.0) {
				output.append("\nElectric First Shot Damage :: " + f.format(electric.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (toxin.firstShot > 0.0) {
				output.append("\nToxin First Shot Damage :: " + f.format(toxin.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (blast.firstShot > 0.0) {
				output.append("\nBlast First Shot Damage :: " + f.format(blast.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (magnetic.firstShot > 0.0) {
				output.append("\nMagnetic First Shot Damage :: " + f.format(magnetic.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (gas.firstShot > 0.0) {
				output.append("\nGas First Shot Damage :: " + f.format(gas.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (radiation.firstShot > 0.0) {
				output.append("\nRadiation First Shot Damage :: " + f.format(radiation.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (corrosive.firstShot > 0.0) {
				output.append("\nCorrosive First Shot Damage :: " + f.format(corrosive.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			if (viral.firstShot > 0.0) {
				output.append("\nViral First Shot Damage :: " + f.format(viral.firstShot * (1 + finalFirstShotDamageMult) / finalFirstShotDamageMult));
			}
			output.append("\nFirst Shot Damage to Clone Flesh :: " + f.format(cloneFlesh.firstShot));
			output.append("\nFirst Shot Damage to Ferrite Armor :: " + f.format(ferrite.firstShot));
			output.append("\nFirst Shot Damage to Alloy Armor :: " + f.format(alloy.firstShot));
			output.append("\nFirst Shot Damage to Mechanical :: " + f.format(mechanical.firstShot));
			output.append("\nFirst Shot Damage to Corpus Flesh :: " + f.format(corpusFlesh.firstShot));
			output.append("\nFirst Shot Damage to Shield :: " + f.format(shield.firstShot));
			output.append("\nFirst Shot Damage to Proto Shield :: " + f.format(protoShield.firstShot));
			output.append("\nFirst Shot Damage to Robotic :: " + f.format(robotic.firstShot));
			output.append("\nFirst Shot Damage to Infested Flesh :: " + f.format(infestedFlesh.firstShot));
			output.append("\nFirst Shot Damage to Fossilized :: " + f.format(fossilized.firstShot));
			output.append("\nFirst Shot Damage to Sinew :: " + f.format(sinew.firstShot));
			output.append("\nFirst Shot Damage to Corpus :: " + f.format(corpus.firstShot));
			output.append("\nFirst Shot Damage to Grineer :: " + f.format(grineer.firstShot));
			output.append("\nFirst Shot Damage to Infested :: " + f.format(infested.firstShot));
		}
		if (finalLastShotDamageMult > 0) {
			output.append("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			output.append("\nRaw Last Shot Damage :: " + f.format(raw.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			if (impact.lastShot > 0.0) {
				output.append("\nImpact Last Shot Damage :: " + f.format(impact.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (puncture.lastShot > 0.0) {
				output.append("\nPuncture Last Shot Damage :: " + f.format(puncture.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (slash.lastShot > 0.0) {
				output.append("\nSlash Last Shot Damage :: " + f.format(slash.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (fire.lastShot > 0.0) {
				output.append("\nFire Last Shot Damage :: " + f.format(fire.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (ice.lastShot > 0.0) {
				output.append("\nIce Last Shot Damage :: " + f.format(ice.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (electric.lastShot > 0.0) {
				output.append("\nElectric Last Shot Damage :: " + f.format(electric.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (toxin.lastShot > 0.0) {
				output.append("\nToxin Last Shot Damage :: " + f.format(toxin.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (blast.lastShot > 0.0) {
				output.append("\nBlast Last Shot Damage :: " + f.format(blast.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (magnetic.lastShot > 0.0) {
				output.append("\nMagnetic Last Shot Damage :: " + f.format(magnetic.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (gas.lastShot > 0.0) {
				output.append("\nGas Last Shot Damage :: " + f.format(gas.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (radiation.lastShot > 0.0) {
				output.append("\nRadiation Last Shot Damage :: " + f.format(radiation.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (corrosive.lastShot > 0.0) {
				output.append("\nCorrosive Last Shot Damage :: " + f.format(corrosive.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			if (viral.lastShot > 0.0) {
				output.append("\nViral Last Shot Damage :: " + f.format(viral.lastShot * (1 + finalLastShotDamageMult) / finalLastShotDamageMult));
			}
			output.append("\nLast Shot Damage to Clone Flesh :: " + f.format(cloneFlesh.lastShot));
			output.append("\nLast Shot Damage to Ferrite Armor :: " + f.format(ferrite.lastShot));
			output.append("\nLast Shot Damage to Alloy Armor :: " + f.format(alloy.lastShot));
			output.append("\nLast Shot Damage to Mechanical :: " + f.format(mechanical.lastShot));
			output.append("\nLast Shot Damage to Corpus Flesh :: " + f.format(corpusFlesh.lastShot));
			output.append("\nLast Shot Damage to Shield :: " + f.format(shield.lastShot));
			output.append("\nLast Shot Damage to Proto Shield :: " + f.format(protoShield.lastShot));
			output.append("\nLast Shot Damage to Robotic :: " + f.format(robotic.lastShot));
			output.append("\nLast Shot Damage to Infested Flesh :: " + f.format(infestedFlesh.lastShot));
			output.append("\nLast Shot Damage to Fossilized :: " + f.format(fossilized.lastShot));
			output.append("\nLast Shot Damage to Sinew :: " + f.format(sinew.lastShot));
			output.append("\nLast Shot Damage to Corpus :: " + f.format(corpus.lastShot));
			output.append("\nLast Shot Damage to Grineer :: " + f.format(grineer.lastShot));
			output.append("\nLast Shot Damage to Infested :: " + f.format(infested.lastShot));
		}
		output.append("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		output.append("\nRaw Damage Per Second :: " + f.format(raw.perSecond));
		output.append("\nDamage Per Second to Clone Flesh :: " + f.format(cloneFlesh.perSecond));
		output.append("\nDamage Per Second to Ferrite Armor :: " + f.format(ferrite.perSecond));
		output.append("\nDamage Per Second to Alloy Armor :: " + f.format(alloy.perSecond));
		output.append("\nDamage Per Second to Mechanical :: " + f.format(mechanical.perSecond));
		output.append("\nDamage Per Second to Corpus Flesh :: " + f.format(corpusFlesh.perSecond));
		output.append("\nDamage Per Second to Shield :: " + f.format(shield.perSecond));
		output.append("\nDamage Per Second to Proto Shield :: " + f.format(protoShield.perSecond));
		output.append("\nDamage Per Second to Robotic :: " + f.format(robotic.perSecond));
		output.append("\nDamage Per Second to Infested Flesh :: " + f.format(infestedFlesh.perSecond));
		output.append("\nDamage Per Second to Fossilized :: " + f.format(fossilized.perSecond));
		output.append("\nDamage Per Second to Sinew :: " + f.format(sinew.perSecond));
		output.append("\nDamage Per Second to Corpus :: " + f.format(corpus.perSecond));
		output.append("\nDamage Per Second to Grineer :: " + f.format(grineer.perSecond));
		output.append("\nDamage Per Second to Infested :: " + f.format(infested.perSecond));
		output.append("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		output.append("\nRaw Burst Damage Per Second :: " + f.format(raw.rawPerSecond));
		output.append("\nBurst Damage Per Second to Clone Flesh :: " + f.format(cloneFlesh.rawPerSecond));
		output.append("\nBurst Damage Per Second to Ferrite Armor :: " + f.format(ferrite.rawPerSecond));
		output.append("\nBurst Damage Per Second to Alloy Armor :: " + f.format(alloy.rawPerSecond));
		output.append("\nBurst Damage Per Second to Mechanical :: " + f.format(mechanical.rawPerSecond));
		output.append("\nBurst Damage Per Second to Corpus Flesh :: " + f.format(corpusFlesh.rawPerSecond));
		output.append("\nBurst Damage Per Second to Shield :: " + f.format(shield.rawPerSecond));
		output.append("\nBurst Damage Per Second to Proto Shield :: " + f.format(protoShield.rawPerSecond));
		output.append("\nBurst Damage Per Second to Robotic :: " + f.format(robotic.rawPerSecond));
		output.append("\nBurst Damage Per Second to Infested Flesh :: " + f.format(infestedFlesh.rawPerSecond));
		output.append("\nBurst Damage Per Second to Fossilized :: " + f.format(fossilized.rawPerSecond));
		output.append("\nBurst Damage Per Second to Sinew :: " + f.format(sinew.rawPerSecond));
		output.append("\nBurst Damage Per Second to Corpus :: " + f.format(corpus.rawPerSecond));
		output.append("\nBurst Damage Per Second to Grineer :: " + f.format(grineer.rawPerSecond));
		output.append("\nBurst Damage Per Second to Infested :: " + f.format(infested.rawPerSecond));

		output.append("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		output.append(selectedWeapon.getModsOutput());
		output.append("\nCorrosive Projections: " + corrosiveProjectionBox.getSelectedItem());

		output.append("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::"); // showing stuff people care about without scrolling
		if (poisonDoTDPS > 0)
			output.append("\nDPS From Poison Procs :: " + f.format(poisonDoTDPS));
		if (electricProcDPS > 0)
			output.append("\nDPS From Electric Procs :: " + f.format(electricProcDPS));
		if (heatDoTDPS > 0)
			output.append("\nDPS From Fire Procs :: " + f.format(heatDoTDPS));
		if (cloudDoTDPS > 0)
			output.append("\nDPS From Gas Procs :: " + f.format(cloudDoTDPS + gasProcDPS));
		if (bleedDoTDPS > 0)
			output.append("\nDPS From Bleeds :: " + f.format(bleedDoTDPS));
		output.append("\nTotal Damage Per Second :: " + f.format(raw.perSecond));
		output.append("\nTotal Burst Damage Per Second :: " + f.format(raw.rawPerSecond));

		if (useComplexTTK) {
			output.append("\n::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			String ttkTableHeader = "\nTarget Name";
			Font font = Main.output.getFont();
			FontMetrics metric = Main.output.getFontMetrics(font);
			int spaceWidth = metric.stringWidth(".");
			int nameFieldWidth = metric.stringWidth(longestTTKName);
			double nameDiff = (nameFieldWidth - metric.stringWidth("Target Name")) / spaceWidth;
			nameDiff = Math.ceil(nameDiff);
			nameDiff += 2;
			for (int i = 0; i < nameDiff; i++) {
				ttkTableHeader += ".";
			}
			ttkTableHeader += "|........TTK.......|....MinTTK.....|....MaxTTK";
			output.append(ttkTableHeader);
			String ttkTableSep = "\n";
			spaceWidth = metric.stringWidth("-");
			int headerWidth = metric.stringWidth(ttkTableHeader);
			int headerLength = headerWidth / spaceWidth;
			for (int i = 0; i < headerLength; i++) {
				ttkTableSep += "-";
			}
			output.append(ttkTableSep);
			int targetGroup = Integer.parseInt((String) targetGroupBox.getSelectedItem());
			Vector<TTKTarget> groupTargets = new Vector<TTKTarget>();
			for (TTKTarget target : theTTKManager.targets) {
				if (target.group == targetGroup) {
					groupTargets.add(target);
				}
			}
			Vector<TTKNamePair> TTKGraphVec = new Vector<TTKNamePair>();
			for (TTKTarget target : groupTargets) {
				output.append(target.printAdvancedData());
				TTKGraphVec.add(target.getTTKNamePair());
			}
			// Update the TTK Graph
			ttkGraph.updateGraph(TTKGraphVec);
		}

		// Update the DPS Graph
		dpsGraph.updateDPS(raw.perSecond, cloneFlesh.perSecond, ferrite.perSecond, alloy.perSecond, mechanical.perSecond, corpusFlesh.perSecond, shield.perSecond, protoShield.perSecond, robotic.perSecond, infestedFlesh.perSecond, fossilized.perSecond, sinew.perSecond, infested.perSecond,
				grineer.perSecond, corpus.perSecond);
	}

	/**
	 * Update the instant stats
	 */
	public static void updateStats() {
		if (!setup) {
			useComplexTTK = false;
			updateOutput = false;
			calculateDPS();
			updateOutput = true;

			DecimalFormat f = new DecimalFormat("#.###");

			DPSPanel.impactField.setText(f.format(finalProjectileCount * averageCritMult * impact.finalBase));
			DPSPanel.punctureField.setText(f.format(finalProjectileCount * averageCritMult * puncture.finalBase));
			DPSPanel.slashField.setText(f.format(finalProjectileCount * averageCritMult * slash.finalBase));
			DPSPanel.fireField.setText(f.format(finalProjectileCount * averageCritMult * fire.finalBase));
			DPSPanel.iceField.setText(f.format(finalProjectileCount * averageCritMult * ice.finalBase));
			DPSPanel.electricField.setText(f.format(finalProjectileCount * averageCritMult * electric.finalBase));
			DPSPanel.toxinField.setText(f.format(finalProjectileCount * averageCritMult * toxin.finalBase));
			DPSPanel.blastField.setText(f.format(finalProjectileCount * averageCritMult * blast.finalBase));
			DPSPanel.magneticField.setText(f.format(finalProjectileCount * averageCritMult * magnetic.finalBase));
			DPSPanel.gasField.setText(f.format(finalProjectileCount * averageCritMult * gas.finalBase));
			DPSPanel.radiationField.setText(f.format(finalProjectileCount * averageCritMult * radiation.finalBase));
			DPSPanel.corrosiveField.setText(f.format(finalProjectileCount * averageCritMult * corrosive.finalBase));
			DPSPanel.viralField.setText(f.format(finalProjectileCount * averageCritMult * viral.finalBase));
			DPSPanel.projectilesField.setText(f.format(finalProjectileCount));
			DPSPanel.FRField.setText(f.format(finalFireRate));
			DPSPanel.CCField.setText(f.format(100 * finalCritChance) + "%");
			DPSPanel.CDField.setText(f.format(finalCritMult));
			DPSPanel.SCField.setText(f.format(100 * finalStatusChance) + "%");
			DPSPanel.modifiedSCField.setText(f.format(100 * (1 - Math.pow(1 - finalStatusChance, finalProjectileCount))) + "%");
			DPSPanel.magField.setText(f.format(finalMag));
			DPSPanel.reloadField.setText(f.format(finalReloadTime));
			DPSPanel.damageField.setText(f.format(finalProjectileCount * averageCritMult * raw.finalBase));
			DPSPanel.slashProcField.setText(f.format(bleedDoTDPS));
			DPSPanel.toxinProcField.setText(f.format(poisonDoTDPS));
			DPSPanel.gasProcField.setText(f.format(cloudDoTDPS + gasProcDPS));
			DPSPanel.electricProcField.setText(f.format(electricProcDPS));
			DPSPanel.fireProcField.setText(f.format(heatDoTDPS));
			DPSPanel.burstField.setText(f.format(raw.rawPerSecond));
			DPSPanel.sustainedField.setText(f.format(raw.perSecond));

			if (impact.finalBase > 0) {
				DPSPanel.impactPanel.setVisible(true);
			} else {
				DPSPanel.impactPanel.setVisible(false);
			}
			if (puncture.finalBase > 0) {
				DPSPanel.puncturePanel.setVisible(true);
			} else {
				DPSPanel.puncturePanel.setVisible(false);
			}
			if (slash.finalBase > 0) {
				DPSPanel.slashPanel.setVisible(true);
			} else {
				DPSPanel.slashPanel.setVisible(false);
			}
			if (fire.finalBase > 0) {
				DPSPanel.firePanel.setVisible(true);
			} else {
				DPSPanel.firePanel.setVisible(false);
			}
			if (ice.finalBase > 0) {
				DPSPanel.icePanel.setVisible(true);
			} else {
				DPSPanel.icePanel.setVisible(false);
			}
			if (electric.finalBase > 0) {
				DPSPanel.electricPanel.setVisible(true);
			} else {
				DPSPanel.electricPanel.setVisible(false);
			}
			if (toxin.finalBase > 0) {
				DPSPanel.toxinPanel.setVisible(true);
			} else {
				DPSPanel.toxinPanel.setVisible(false);
			}
			if (blast.finalBase > 0) {
				DPSPanel.blastPanel.setVisible(true);
			} else {
				DPSPanel.blastPanel.setVisible(false);
			}
			if (magnetic.finalBase > 0) {
				DPSPanel.magneticPanel.setVisible(true);
			} else {
				DPSPanel.magneticPanel.setVisible(false);
			}
			if (gas.finalBase > 0) {
				DPSPanel.gasPanel.setVisible(true);
			} else {
				DPSPanel.gasPanel.setVisible(false);
			}
			if (radiation.finalBase > 0) {
				DPSPanel.radiationPanel.setVisible(true);
			} else {
				DPSPanel.radiationPanel.setVisible(false);
			}
			if (corrosive.finalBase > 0) {
				DPSPanel.corrosivePanel.setVisible(true);
			} else {
				DPSPanel.corrosivePanel.setVisible(false);
			}
			if (viral.finalBase > 0) {
				DPSPanel.viralPanel.setVisible(true);
			} else {
				DPSPanel.viralPanel.setVisible(false);
			}
			if (bleedDoTDPS > 0) {
				DPSPanel.slashProcPanel.setVisible(true);
			} else {
				DPSPanel.slashProcPanel.setVisible(false);
			}
			if (poisonDoTDPS > 0) {
				DPSPanel.toxinProcPanel.setVisible(true);
			} else {
				DPSPanel.toxinProcPanel.setVisible(false);
			}
			if (cloudDoTDPS > 0) {
				DPSPanel.gasProcPanel.setVisible(true);
			} else {
				DPSPanel.gasProcPanel.setVisible(false);
			}
			if (electricProcDPS > 0) {
				DPSPanel.electricProcPanel.setVisible(true);
			} else {
				DPSPanel.electricProcPanel.setVisible(false);
			}
			if (heatDoTDPS > 0) {
				DPSPanel.fireProcPanel.setVisible(true);
			} else {
				DPSPanel.fireProcPanel.setVisible(false);
			}
		}
		if (TTKBox.isSelected()) {
			useComplexTTK = true;
		}
	}

	/**
	 * Method to display the mod manager
	 */
	protected static void displayModManager() {

		if (!modManagerInit) {
			modManagerInit = true;
			theModManager.Init("mods.db");
			modManagerFrame.add(theModManager);
			modManagerFrame.pack();
			modManagerFrame.addWindowListener(new ModWindowListener());
			modManagerFrame.setTitle("Mod Manager");
			modManagerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		modManagerFrame.setVisible(true);
	}

	/**
	 * Method to display the weapon manager
	 */
	protected static void displayWeaponManager() {

		if (!weaponManagerInit) {
			weaponManagerInit = true;
			weaponManagerFrame.add(theWeaponManager);
			weaponManagerFrame.pack();
			weaponManagerFrame.addWindowListener(new WeaponWindowListener());
			weaponManagerFrame.setTitle("Weapon Manager");
			weaponManagerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		weaponManagerFrame.setVisible(true);
	}

	/**
	 * Method to display the target manager
	 */
	protected static void displayTargetManager() {
		if (!targetManagerInit) {
			targetManagerInit = true;
			targetManagerFrame.add(theTTKManager);
			targetManagerFrame.pack();
			targetManagerFrame.addWindowListener(new TTKWindowListener());
			targetManagerFrame.setTitle("Target Manager");
			targetManagerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		targetManagerFrame.setVisible(true);
	}

	/**
	 * Method to display the color options manager
	 */
	protected static void displayColorOptions() {
		if (!colorOptionsInit) {
			colorOptionsInit = true;
			colorOptionsFrame.add(theColorPanel);
			colorOptionsFrame.pack();
			colorOptionsFrame.setTitle("Color Options");
			colorOptionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		colorOptionsFrame.setVisible(true);
	}

	/**
	 * Method to toggle the enabled state of the mod manager menu item
	 */
	protected static void updateModMenuState(boolean enabled) {
		modMenu.setEnabled(enabled);
	}

	/**
	 * Method to toggle the enabled state of the target manager menu item
	 */
	protected static void updateTTKMenuState(boolean enabled) {
		TTKMenu.setEnabled(enabled);
	}

	/**
	 * Method to toggle the enabled state of the weapon manager menu item
	 */
	protected static void updateWeaponMenuState(boolean enabled) {
		weaponMenu.setEnabled(enabled);
	}

	/**
	 * ____________________________________________________________ INTERNAL CLASSES
	 * ____________________________________________________________
	 */

	/**
	 * change Listener Local Class
	 */
	protected static class MainChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			updateStats();
		}
	}

	/**
	 * Action Listener Local Class
	 * 
	 * @author GottFuast
	 *
	 */
	protected static class MainActionListener implements ActionListener {
		/**
		 * Default CTOR
		 */
		public MainActionListener() {
			// Do Nothing
		}

		/**
		 * Action Listener Callback
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(calculateButton)) {
				updateStats();
				updateOutput = true;
				if (lightWeightTTKBox.isSelected() || TTKBox.isSelected()) {
					useComplexTTK = true;
				}
				calculateDPS();
			} else if (e.getSource().equals(maximizeButton)) {
				selectedWeapon = (WeaponPanel) weaponPane.getSelectedComponent();
				selectedWeapon.parseActiveMods();
				useComplexTTK = true;
				updateOutput = false;
				setup = true;
				theMaximizer.Maximizer();
				setup = false;
			} else if (e.getSource().equals(TTKBox) || e.getSource().equals(lightWeightTTKBox)) {
				useComplexTTK = (TTKBox.isSelected() || lightWeightTTKBox.isSelected());
				if (e.getSource().equals(TTKBox)) {
					if (lightWeightTTKBox.isSelected()) {
						lightWeightTTKBox.setSelected(false);
					}
				} else {
					if (TTKBox.isSelected()) {
						TTKBox.setSelected(false);
					}
				}
			} else if (e.getSource().equals(targetGroupBox)) {
				ttkGraph.clear();
			}
			/*
			 * else if (e.getSource().equals(statsButton)) { if (DPSPanel.isVisible()) {
			 * DPSPanel.setVisible(false); } else { DPSPanel.setVisible(true); } repack(); }
			 */
			else if (e.getSource().equals(clearOutputButton)) {
				output.setText("");
				dpsGraph.clear();
				ttkGraph.clear();
			} else if (e.getSource().equals(loadItem)) {
				int returnVal = chooser.showOpenDialog(mainPanel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					riflePanel.setting = true;
					shotgunPanel.setting = true;
					pistolPanel.setting = true;
					riflePanel.clear();
					shotgunPanel.clear();
					pistolPanel.clear();
					// output.setText("");
					// graph.clear();
					clearValues();
					File file = chooser.getSelectedFile();
					try {
						BufferedReader reader = new BufferedReader(new FileReader(file));
						String header = reader.readLine();
						reader.close();
						if (header.equals(Constants.RIFLE)) {
							weaponPane.setSelectedIndex(weaponPane.indexOfTab(Constants.RIFLE));
							riflePanel.loadFromFile(file);
						} else if (header.equals(Constants.PISTOL)) {
							weaponPane.setSelectedIndex(weaponPane.indexOfTab(Constants.PISTOL));
							pistolPanel.loadFromFile(file);
						} else if (header.equals(Constants.SHOTGUN)) {
							weaponPane.setSelectedIndex(weaponPane.indexOfTab(Constants.SHOTGUN));
							shotgunPanel.loadFromFile(file);
						} else if (header.equals(Constants.ARCGUN)) {
							weaponPane.setSelectedIndex(weaponPane.indexOfTab(Constants.ARCGUN));
							arcGunPanel.loadFromFile(file);
						}
					} catch (Exception ex) {
						// Do Nothing
					}
					riflePanel.setting = false;
					shotgunPanel.setting = false;
					pistolPanel.setting = false;
					updateStats();
				} else {
					// Do Nothing
				}
			} else if (e.getSource().equals(saveItem)) {
				int returnVal = chooser.showSaveDialog(mainPanel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					if (!file.getAbsolutePath().endsWith(".wdc")) {
						file = new File(file.getAbsolutePath() + ".wdc");
					}
					WeaponPanel selected = (WeaponPanel) weaponPane.getSelectedComponent();
					selected.saveToFile(file);
				} else {
					// Do Nothing
				}
			} else if (e.getSource().equals(modMenu)) {
				displayModManager();
				updateModMenuState(false);
			} else if (e.getSource().equals(TTKMenu)) {
				displayTargetManager();
				updateTTKMenuState(false);
			} else if (e.getSource().equals(weaponMenu)) {
				displayWeaponManager();
				updateWeaponMenuState(false);
			} else if (e.getSource().equals(colorOptionsItem)) {
				displayColorOptions();
			}
		}
	}

	public static double getCorrosiveProjectionMult() {
		double mult = 1.0 - (0.3 * Double.parseDouble((String) corrosiveProjectionBox.getSelectedItem()));
		if (mult < 0.0) {
			mult = 0.0;
		}
		return mult;
	}

	public static void repack() {
		mainFrame.pack();
	}

	/**
	 * Window Listener Local Class
	 * 
	 * @author GottFuast
	 *
	 */
	protected static class MainWindowListener implements WindowListener {

		/**
		 * Default CTOR
		 */
		public MainWindowListener() {
			// Do Nothing
		}

		/**
		 * Event to indicate that the window has closed
		 */
		public void windowClosed(WindowEvent e) {
			System.exit(0);
		}

		/**
		 * Event to indicate taht the window is closing
		 */
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}

		/**
		 * Unused
		 */
		public void windowActivated(WindowEvent e) {
		}

		public void windowDeactivated(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {
		}

		public void windowOpened(WindowEvent e) {
		}
	}

	/**
	 * Window Listener Local Class
	 * 
	 * @author GottFaust
	 *
	 */
	protected static class ModWindowListener implements WindowListener {

		/**
		 * Default CTOR
		 */
		public ModWindowListener() {
			// Do Nothing
		}

		/**
		 * Event to indicate that the window has closed
		 */
		public void windowClosed(WindowEvent e) {
			updateModMenuState(true);
		}

		/**
		 * Unused
		 */
		public void windowActivated(WindowEvent e) {
		}

		public void windowClosing(WindowEvent e) {
		}

		public void windowDeactivated(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {
		}

		public void windowOpened(WindowEvent e) {
		}
	}

	/**
	 * Window Listener Local Class
	 * 
	 * @author GottFaust
	 *
	 */
	protected static class TTKWindowListener implements WindowListener {

		/**
		 * Default CTOR
		 */
		public TTKWindowListener() {
			// Do Nothing
		}

		/**
		 * Event to indicate that the window has closed
		 */
		public void windowClosed(WindowEvent e) {
			updateTTKMenuState(true);
		}

		/**
		 * Unused
		 */
		public void windowActivated(WindowEvent e) {
		}

		public void windowClosing(WindowEvent e) {
		}

		public void windowDeactivated(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {
		}

		public void windowOpened(WindowEvent e) {
		}
	}

	/**
	 * Window Listener Local Class
	 * 
	 * @author GottFaust
	 *
	 */
	protected static class WeaponWindowListener implements WindowListener {

		/**
		 * Default CTOR
		 */
		public WeaponWindowListener() {
			// Do Nothing
		}

		/**
		 * Event to indicate that the window has closed
		 */
		public void windowClosed(WindowEvent e) {
			updateWeaponMenuState(true);
		}

		/**
		 * Unused
		 */
		public void windowActivated(WindowEvent e) {
		}

		public void windowClosing(WindowEvent e) {
		}

		public void windowDeactivated(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {
		}

		public void windowOpened(WindowEvent e) {
		}
	}

	public static class DoTPair {
		public double damage = 0.0;
		public int duration = 0;

		public DoTPair(double damage, int duration) {
			this.damage = damage;
			this.duration = duration;
		}
	}

}