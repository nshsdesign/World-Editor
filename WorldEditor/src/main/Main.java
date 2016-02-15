package main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import entities.World;
import worldParser.WorldFileLoader;

@SuppressWarnings("serial")
public class Main extends JFrame{
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 900;
	
	//ALL JComponents//
	
	public static final String RES_LOC = "res/";
	public static final String WORLD_FOLDER_LOC = "worlds/";

	private String name = "NONAME";
	private String worldFile = "";
	
	private JFileChooser worldFC;
	private JButton openWorld;
	private JTextField worldTF;
	private JTextField nameTF;
	private JLabel nameLabel;
	
	private World world;
	
	public static final int STAT_MIN = -100;
	public static final int STAT_MAX = 100;
	public static final int STAT_INIT = 0;
	JSlider posX;
	JSlider posY;
	JSlider posZ;
	JSlider rotX;
	JSlider rotY;
	JSlider rotZ;
	JSlider scale;
	JSlider[] statSliders = {posX, posY, posZ, rotX, rotY, rotZ, scale};
	public static int[] stats = {0, 0, 0, 0, 0, 0, 0};
	String[] statNames = {"X Pos", "Y Pos", "Z Pos", "Rotation X", "Rotation Y", "Rotation Z", "Scale"};
	String[] objectTypes;
	JComboBox<String> objectTypeBox;
	JMenuBar menuBar;
	JMenu file;
	JMenuItem newF, open, save;
	JDialog saved;
	
	//***************//

	public static void main(String[] args){
		new Main();
	}
	
	public Main(){
		world = new World();
		
		setTitle("World Editor v1.0");
		add(new OpenGLView(world));
		
		WorldFileLoader.loadObjectTypes("objectTypes");
		objectTypes = WorldFileLoader.getObjectTypesArray();
		System.out.println(objectTypes[0]);
		world.setCurrentObjectType(objectTypes[0]);
		 
		//---------------JComponent Stuff--------------------//
		menuBar = new JMenuBar();
		file = new JMenu("File");
		file.getAccessibleContext().setAccessibleDescription(
		        "Open file");
		menuBar.add(file);
		newF = new JMenuItem("New", KeyEvent.VK_N);
		file.add(newF);
		open = new JMenuItem("Open", KeyEvent.VK_O);
		open.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		file.add(open);
		save = new JMenuItem("Save and Export", KeyEvent.VK_S);
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAndExportFile();
			}
		});
		file.add(save);
		setJMenuBar(menuBar);
		
		objectTypeBox = new JComboBox<String>(objectTypes);
		objectTypeBox.setSelectedIndex(0);
		objectTypeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        String newSelection = (String)objectTypeBox.getSelectedItem();
		        world.setCurrentObjectType(newSelection);
			}
		});
		setLayout(null);
		objectTypeBox.setBounds(20, 40, 200, 30);
		add(objectTypeBox);
		
		nameLabel=new JLabel("World name: ");
		nameLabel.setBounds(275,40,100,30);
		add(nameLabel);
		
	    nameTF = new JTextField(20);
	    nameTF.setBounds(350, 45, 200, 25);
	    nameTF.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				name = ((JTextField)e.getSource()).getText();//TODO: TEST
				//System.out.println(name);
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
	    });
	    add(nameTF);
		
		//********************************************//
		
		for(int i=0; i<statSliders.length; i++){
			JLabel sliderLabel = new JLabel(statNames[i]+":");
			sliderLabel.setBounds(950 + 10, 18 + (i*100), 300, 50);
			add(sliderLabel);
			
			statSliders[i] = new JSlider(JSlider.HORIZONTAL,
	                STAT_MIN, STAT_MAX, STAT_INIT);
			statSliders[i].addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					changeValue((JSlider)e.getSource());
				}
			});
			statSliders[i].setMajorTickSpacing((STAT_MAX-STAT_MIN)/10);
			statSliders[i].setMinorTickSpacing(2);
			statSliders[i].setPaintTicks(true);
			statSliders[i].setPaintLabels(true);
			statSliders[i].setPaintTrack(false);
			statSliders[i].setBounds(950, 50 + (i*100), 300, 50);
			add(statSliders[i]);
		}
		
		//********************************************//
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
		
	public void changeValue(JSlider slider){
		for(int i=0; i<statSliders.length; i++){
			if(slider.equals(statSliders[i])) stats[i] = slider.getValue();
		}
	}
	
	
	
	


	    //TODO: ADD AN INPUT FOR THE NAME OF THE ITEM

	public void saveAndExportFile(){
		String dataFileLoc = RES_LOC + WORLD_FOLDER_LOC + name + "/" + name.replaceAll("\\s+","") + ".world";
		
		//Double check that file and folder exists
	    File f = new File(dataFileLoc);
	    f.getParentFile().mkdirs();
	    try {
			f.createNewFile();
		} catch (IOException e) {
			System.err.println("Could not make '.world' file");
		    System.exit(-1);
		}
	    
	    //------- save -------
	    WorldFileLoader.saveWorldFile(dataFileLoc, world);
	    
//	    //copy files
//	    File dest = f.getParentFile();
//	    File worldSource = new File(worldFile);
//	    String message = "Finished Saving!";
//	    try {
//	    	FileUtils.copyFileToDirectory(worldSource, dest);
//	    } catch (IOException e) {
//	        message = "ERROR: Could not save as there are not enough source files";
//	        e.printStackTrace();
//	    }
	    
	    saved = new JDialog(this, "Finished Saving!");
	    saved.setSize(400, 200);
	    saved.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    JPanel pane = new JPanel();
	    JButton close = new JButton();
	    close.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeWindow();
			}	    	
	    });
	    pane.add(close);
	    saved.getContentPane().add(pane, BorderLayout.PAGE_END);
	    saved.setVisible(true);
	    saved.setLocation(WIDTH/2, HEIGHT/2);
	    
	}
	
	public void closeWindow(){
		saved.setVisible(false);
		saved.dispose();
	}

	public void openFile(){
	    
	    JFrame openFrame = new JFrame();
	    JPanel openPane = new JPanel();
	    openPane.setSize(WIDTH-200, HEIGHT-200);
	    openPane.setLayout(null);

	    
	    
	    JLabel worldLoc = new JLabel("World File: ");
	    worldLoc.setBounds(100, 100, 100, 25);
	    openPane.add(worldLoc);
	    worldTF = new JTextField(20);
	    worldTF.setBounds(200, 100, 200, 25);
	    worldTF.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				worldFile = ((JTextField)e.getSource()).getText();
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
	    });
	    openPane.add(worldTF);
	    
	    worldFC = new JFileChooser(".");
	    FileFilter filter1 = new FileNameExtensionFilter("World File","world");
	    worldFC.setFileFilter(filter1);
	    worldFC.addPropertyChangeListener(new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent e){
				try{
					//System.out.println(e.getPropertyName());
					if(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equalsIgnoreCase(e.getPropertyName())){
						File file = worldFC.getSelectedFile();
						worldFile= file.getCanonicalPath();
						//System.out.println(e.getPropertyName());
						worldTF.setText(worldFile);
					}
				}catch(Exception ex){}
			}
	    });
	    
	    openWorld = new JButton("...");
	    openWorld.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
		        int returnVal = worldFC.showOpenDialog(Main.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = worldFC.getSelectedFile();
		            worldFile = RES_LOC + WORLD_FOLDER_LOC + name + "/" +file.getName();
		        }	
			}
	    });
	    openWorld.setBounds(400, 100, 25, 25);
	    openPane.add(openWorld);
	    
	    
	    
	    openFrame.add(openPane);
	     //If this doesnt work, try using JDialog
	    openFrame.setTitle("Open File");
	    openFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    openFrame.setResizable(false);
	    openFrame.setSize(WIDTH-200, HEIGHT-200);
	    openFrame.setLocationRelativeTo(null);
	    openFrame.setVisible(true);

	}

	public void newFile(){



	}	
	
}
