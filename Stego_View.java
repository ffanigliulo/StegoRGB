import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

/*
 *Class Stego_View
 */
public class Stego_View extends JFrame {
	
	//variables for window
	private static int WIDTH  = 800;
	private static int HEIGHT = 600;
	
	//elements for JPanel
	private JTextArea 	input;
	private JScrollBar 	scroll,scroll2;
	private JButton		encodeButton,decodeButton;
	private JLabel		image_input;
			
	//elements for Menu
	private JMenu 		file;
	private JMenuItem 	encode;
	private JMenuItem 	decode;
	private JMenuItem 	exit;
	
	/*
	 *Constructor for Steganography_View class
	 *@param name Used to set the title on the JFrame
	 */
	public Stego_View(String name)
	{
		//set the title of the JFrame
		super(name);
		
		//Menubar
		JMenuBar menu = new JMenuBar();
		
		JMenu file = new JMenu("Menu");
		encode = new JMenuItem("Encode"); file.add(encode);
		decode = new JMenuItem("Decode"); file.add(decode);
		file.addSeparator();
		exit = new JMenuItem("Exit"); file.add(exit);
		
		menu.setOpaque(true);
		menu.setBackground(new Color(59, 89, 152));
		menu.setForeground(Color.white);
		file.setOpaque(true);
		file.setBackground(new Color(59, 89, 152));
		file.setForeground(Color.white);
		encode.setOpaque(true);
		encode.setBackground(new Color(59, 89, 152));
		encode.setForeground(Color.white);
		decode.setOpaque(true);
		decode.setBackground(new Color(59, 89, 152));
		decode.setForeground(Color.white);
		exit.setOpaque(true);
		exit.setBackground(new Color(59, 89, 152));
		exit.setForeground(Color.white);
		
		file.setPreferredSize(new Dimension(80, 32));
		encode.setPreferredSize(new Dimension(80, 24));
		decode.setPreferredSize(new Dimension(80, 24));
		exit.setPreferredSize(new Dimension(80, 24));
		menu.add(file);
		setJMenuBar(menu);
		
		
		// display rules
		setResizable(true);						//allow window to be resized: true?false
		setBackground(Color.darkGray);			//background color of window: Color(int,int,int) or Color.name
		setLocation(100,100);					//location on the screen to display window
        setDefaultCloseOperation(EXIT_ON_CLOSE);//what to do on close operation: exit, do_nothing, etc
        setSize(WIDTH,HEIGHT);					//set the size of the window
        setVisible(true);						//show the window: true?false
        
	}
	
	/*
	 *@return The menu item 'Encode'
	 */
	public JMenuItem	getEncode()		{ return encode;			}
	/*
	 *@return The menu item 'Decode'
	 */
	public JMenuItem	getDecode()		{ return decode;			}
	/*
	 *@return The menu item 'Exit'
	 */
	public JMenuItem	getExit()		{ return exit;				}
	/*
	 *@return The TextArea containing the text to encode
	 */
	public JTextArea	getText()		{ return input;				}
	/*
	 *@return The JLabel containing the image to decode text from
	 */
	public JLabel		getImageInput()	{ return image_input;		}
	/*
	 *@return The JPanel displaying the Encode View
	 */
	public JPanel		getTextPanel()	{ return new Text_Panel();	}
	/*
	 *@return The JPanel displaying the Decode View
	 */
	public JPanel		getImagePanel()	{ return new Image_Panel();	}
	/*
	 *@return The Encode button
	 */
	public JButton		getEButton()	{ return encodeButton;		}
	/*
	 *@return The Decode button
	 */
	public JButton		getDButton()	{ return decodeButton;		}
	
	/*
	 *Class Text_Panel
	 */
	private class Text_Panel extends JPanel
	{
		/*
		 *Constructor to enter text to be encoded
		 */
		public Text_Panel()
		{
			//setup GridBagLayout
			GridBagLayout layout = new GridBagLayout(); 
			GridBagConstraints layoutConstraints = new GridBagConstraints(); 
			setLayout(layout);
			
			input = new JTextArea();
			layoutConstraints.gridx 	= 0; layoutConstraints.gridy = 0; 
			layoutConstraints.gridwidth = 1; layoutConstraints.gridheight = 1; 
			layoutConstraints.fill 		= GridBagConstraints.BOTH; 
			layoutConstraints.insets 	= new Insets(40,20,40,20); 
			layoutConstraints.anchor 	= GridBagConstraints.CENTER; 
			layoutConstraints.weightx 	= 1.0; layoutConstraints.weighty = 50.0;
			JScrollPane scroll = new JScrollPane(input,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
			layout.setConstraints(scroll,layoutConstraints);
			scroll.setBorder(BorderFactory.createLineBorder(Color.black,1));
	    	add(scroll);
	    	
	    	encodeButton = new JButton("Encode Text");
	    	layoutConstraints.gridx 	= 0; layoutConstraints.gridy = 1; 
			layoutConstraints.gridwidth = 1; layoutConstraints.gridheight = 1; 
			layoutConstraints.fill 		= GridBagConstraints.VERTICAL; 
			layoutConstraints.insets 	= new Insets(0,-5,40,-5); 
			layoutConstraints.anchor 	= GridBagConstraints.CENTER; 
			layoutConstraints.weightx 	= 1.0; layoutConstraints.weighty = 1.0;
			layout.setConstraints(encodeButton,layoutConstraints);
	    	add(encodeButton);
	    	encodeButton.setBackground(new Color(59, 89, 152));
	    	encodeButton.setForeground(Color.white);
	    	encodeButton.setPreferredSize(new Dimension(140, 40));
	    	
	    	//set basic display
			setBackground(new Color(232, 232, 232));
		}
	}
	
	/*
	 *Class Image_Panel
	 */
	private class Image_Panel extends JPanel
	{
		/*
		 *Constructor for displaying an image to be decoded
		 */
		public Image_Panel()
		{
			//setup GridBagLayout
			GridBagLayout layout = new GridBagLayout(); 
			GridBagConstraints layoutConstraints = new GridBagConstraints(); 
			setLayout(layout);
			
			image_input = new JLabel();
			layoutConstraints.gridx 	= 0; layoutConstraints.gridy = 0; 
			layoutConstraints.gridwidth = 1; layoutConstraints.gridheight = 1; 
			layoutConstraints.fill 		= GridBagConstraints.BOTH; 
			layoutConstraints.insets 	= new Insets(20,10,20,10); 
			layoutConstraints.anchor 	= GridBagConstraints.CENTER; 
			layoutConstraints.weightx 	= 1.0; layoutConstraints.weighty = 50.0;
			JScrollPane scroll2 = new JScrollPane(image_input,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
			layout.setConstraints(scroll2,layoutConstraints);
			scroll2.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
			image_input.setHorizontalAlignment(JLabel.CENTER);
	    	add(scroll2);
	    	image_input.setOpaque(true);
	    	image_input.setBackground(new Color(175, 199, 216));
	    	
	    	decodeButton = new JButton("Decode Now");
	    	layoutConstraints.gridx 	= 0; layoutConstraints.gridy = 1; 
			layoutConstraints.gridwidth = 1; layoutConstraints.gridheight = 1; 
			layoutConstraints.fill 		= GridBagConstraints.VERTICAL; 
			layoutConstraints.insets 	= new Insets(0,-5,20,-5); 
			layoutConstraints.anchor 	= GridBagConstraints.CENTER; 
			layoutConstraints.weightx 	= 1.0; layoutConstraints.weighty = 1.0;
			layout.setConstraints(decodeButton,layoutConstraints);
	    	add(decodeButton);
	    	decodeButton.setBackground(new Color(59, 89, 152));
	    	decodeButton.setForeground(Color.white);
	    	decodeButton.setPreferredSize(new Dimension(140, 40));
	    	
	    	//set basic display
			setBackground(new Color(232, 232, 232));
	    }
	 }
}
