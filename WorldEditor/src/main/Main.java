package main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;
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

import org.apache.commons.io.FileUtils;

@SuppressWarnings("serial")
public class Main extends JFrame{
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 900;
	
	//ALL JComponents//
	
	public static final String RES_LOC = "res/";
	public static final String ITEM_FOLDER_LOC = "items/";

	private String name = "NONAME";

	private String objFile = "";
	private String iconFile = "";
	private String textureFile = "";

	private JFileChooser objFC;
	private JFileChooser iconFC;
	private JFileChooser textureFC;

	private JButton openOBJ;
	private JButton openIcon;
	private JButton openTexture;
	
	private JTextField objTF;
	private JTextField iconTF;
	private JTextField textureTF;
	
	private JTextField nameTF;
	private JLabel nameLabel;
	
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
	int[] stats = {0, 0, 0, 0, 0, 0};
	String[] statNames = {"X Pos", "Y Pos", "Z Pos", "Rotation X", "Rotation Y", "Rotation Z", "Scale"};
	
	String[] itemTypes = {"Staff", "Sword", "Shield", "OffHand", "Helmet", "Torso", "Legs", "Arms", "Hands", "Shoes", "Accessory"};
	JComboBox<String> itemTypeBox;
	JMenuBar menuBar;
	JMenu file;
	JMenuItem newF, open, save;
	
	JDialog saved;
	
	//***************//
	
	String currentItemType = itemTypes[0];

	public static void main(String[] args){
		new Main();
	}
	
	public Main(){
		setTitle("Item Creator v1.0");
		add(new OpenGLView());
		
		//JComponent Stuff//
		menuBar = new JMenuBar();
		file = new JMenu("File");
		file.getAccessibleContext().setAccessibleDescription(
		        "Open files");
		menuBar.add(file);
		newF = new JMenuItem("New", KeyEvent.VK_N);
		file.add(newF);
		open = new JMenuItem("Open", KeyEvent.VK_O);
		open.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openFiles();
			}
		});
		file.add(open);
		save = new JMenuItem("Save and Export", KeyEvent.VK_S);
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAndExportFiles();
			}
		});
		file.add(save);
		setJMenuBar(menuBar);
		
		itemTypeBox = new JComboBox<String>(itemTypes);
		itemTypeBox.setSelectedIndex(0);
		itemTypeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        String newSelection = (String)itemTypeBox.getSelectedItem();
		        currentItemType = newSelection;
			}
		});
		setLayout(null);
		itemTypeBox.setBounds(20, 40, 200, 30);
		add(itemTypeBox);
		
		nameLabel=new JLabel("Item name: ");
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
		
		//****//
		
		//TODO: Add Camera controls
		
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
		
		/*timer = new Timer(1000, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println(currentItemType + ", " + stats[0] + ", " + stats[1] + ", " + stats[2] + ", " + stats[3] + ", " + stats[4] + ", " + stats[5]);
			}
		});
		timer.start();*/
		
		//*****************//
		
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

	public void saveAndExportFiles(){
		String dataFileLoc = RES_LOC + ITEM_FOLDER_LOC + name + "/" + name.replaceAll("\\s+","") + ".dat";
	    File f = new File(dataFileLoc);
	    f.getParentFile().mkdirs();
	    try {
			f.createNewFile();
		} catch (IOException e) {
			System.err.println("Could not make '.dat' file");
		    System.exit(-1);
		}
	    //save .dat
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(dataFileLoc, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String line = currentItemType +": "+ name +" "+ iconFile +" "+ objFile +" "+ 
				textureFile +" "+ stats[0] +" "+ stats[1] +" "+ stats[2] +" "+ 
				stats[3] +" "+ stats[4] +" "+ stats[5];
		writer.println(line);
		writer.close();
	    
	    //copy files
	    File dest = f.getParentFile();
	    File objSource = new File(objFile);
	    File iconSource = new File(iconFile);
	    File textureSource = new File(textureFile);
	    String message = "Finished Saving!";
	    try {
	    	FileUtils.copyFileToDirectory(objSource, dest);
	    	FileUtils.copyFileToDirectory(iconSource, dest);
	    	FileUtils.copyFileToDirectory(textureSource, dest);
	    } catch (IOException e) {
	        message = "ERROR: Could not save as there are not enough source files";
	        e.printStackTrace();
	    }
	    saved = new JDialog(this, message);
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

	public void openFiles(){
	    
	    JFrame openFrame = new JFrame();
	    JPanel openPane = new JPanel();
	    openPane.setSize(WIDTH-200, HEIGHT-200);
	    openPane.setLayout(null);

	    
	    
	    JLabel OBJLoc = new JLabel(".OBJ File: ");
	    OBJLoc.setBounds(100, 100, 100, 25);
	    openPane.add(OBJLoc);
	    objTF = new JTextField(20);
	    objTF.setBounds(200, 100, 200, 25);
	    objTF.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				objFile = ((JTextField)e.getSource()).getText();
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
	    });
	    openPane.add(objTF);
	    
	    JLabel iconLoc = new JLabel("Icon File: ");
	    iconLoc.setBounds(100, 150, 100, 25);
	    openPane.add(iconLoc);
	    iconTF = new JTextField(20);
	    iconTF.setBounds(200, 150, 200, 25);
	    iconTF.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				iconFile = ((JTextField)e.getSource()).getText();
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
	    });
	    openPane.add(iconTF);
	    
	    JLabel textureLoc = new JLabel("Texture File: ");
	    textureLoc.setBounds(100, 200, 100, 25);
	    openPane.add(textureLoc);
	    textureTF = new JTextField(20);
	    textureTF.setBounds(200, 200, 200, 25);
	    textureTF.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				textureFile = ((JTextField)e.getSource()).getText();
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
	    });
	    openPane.add(textureTF);
	    
	    
	    objFC = new JFileChooser(".");
	    iconFC = new JFileChooser(".");
	    textureFC = new JFileChooser(".");
	    FileFilter filter1 = new FileNameExtensionFilter("Obj File","obj");
	    objFC.setFileFilter(filter1);
	    FileFilter filter2 = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
	    iconFC.setFileFilter(filter2);
	    textureFC.setFileFilter(filter2);
	    objFC.addPropertyChangeListener(new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent e){
				try{
					//System.out.println(e.getPropertyName());
					if(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equalsIgnoreCase(e.getPropertyName())){
						File file = objFC.getSelectedFile();
						objFile= file.getCanonicalPath();
						//System.out.println(e.getPropertyName());
						objTF.setText(objFile);
					}
				}catch(Exception ex){}
			}
	    });
	    iconFC.addPropertyChangeListener(new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try{
					if(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equalsIgnoreCase(e.getPropertyName())){
						File file = iconFC.getSelectedFile();
						iconFile= file.getCanonicalPath();
						iconTF.setText(iconFile);
					}
				}catch(Exception ex){}
			}
	    });
	    textureFC.addPropertyChangeListener(new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try{
					if(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equalsIgnoreCase(e.getPropertyName())){
						File file = textureFC.getSelectedFile();
						textureFile= file.getCanonicalPath();
						textureTF.setText(textureFile);
					}
				}catch(Exception ex){}
			}
	    });
	    
	    
	    openOBJ = new JButton("...");
	    openOBJ.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
		        int returnVal = objFC.showOpenDialog(Main.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = objFC.getSelectedFile();
		            objFile = RES_LOC + ITEM_FOLDER_LOC + name + "/" +file.getName();
		        }	
			}
	    });
	    openOBJ.setBounds(400, 100, 25, 25);
	    openPane.add(openOBJ);
	    
	    
	    openIcon = new JButton("...");
	    openIcon.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
		        int returnVal = iconFC.showOpenDialog(Main.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = iconFC.getSelectedFile();
		            iconFile = RES_LOC + ITEM_FOLDER_LOC + name + "/" +file.getName();
		        }
			}
	    });
	    openIcon.setBounds(400, 150, 25, 25);
	    openPane.add(openIcon);
	    
	    
	    openTexture = new JButton("...");
	    openTexture.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
		        int returnVal = textureFC.showOpenDialog(Main.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = textureFC.getSelectedFile();
		            textureFile = RES_LOC + ITEM_FOLDER_LOC + name + "/" +file.getName();
		        }
			}
	    });
	    openTexture.setBounds(400, 200, 25, 25);
	    openPane.add(openTexture);
	    
	    
	    openFrame.add(openPane);
	     //If this doesnt work, try using JDialog
	    openFrame.setTitle("Open File");
	    openFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    openFrame.setResizable(false);
	    openFrame.setSize(WIDTH-200, HEIGHT-200);
	    openFrame.setLocationRelativeTo(null);
	    openFrame.setVisible(true);

	}

	public void newFiles(){



	}	
	
}
