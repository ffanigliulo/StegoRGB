import java.io.File;
import java.util.Base64;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

public class Stego_Controller {
		//Program Variables
		private Stego_View	view;
		private Stego_Model		model;
		
		//Panel Displays
		private JPanel		decode_panel;
		private JPanel		encode_panel;
		//Panel Variables
		private JTextArea 	input;
		private JButton		encodeButton,decodeButton;
		private JLabel		image_input;
		//Menu Variables
		private JMenuItem 	encode;
		private JMenuItem 	decode;
		private JMenuItem 	exit;
				
		//action event classes
		private Encode			enc;
		private Decode			dec;
		private EncodeButton	encButton;
		private DecodeButton	decButton;
				
		//decode variable
		private String			stat_path = "";
		private String			stat_name = "";
		
		/*
		 *Constructor to initialize view, model and environment variables
		 *@param aView  A GUI class, to be saved as view
		 *@param aModel A model class, to be saved as model
		 */
		public Stego_Controller(Stego_View aView, Stego_Model aModel)
		{
			//program variables
			view  = aView;
			model = aModel;
			
			//assign View Variables
			//2 views
			encode_panel	= view.getTextPanel();
			decode_panel	= view.getImagePanel();
			//2 data options
			input			= view.getText();
			image_input		= view.getImageInput();
			//2 buttons
			encodeButton	= view.getEButton();
			decodeButton	= view.getDButton();
			//menu
			encode			= view.getEncode();
			decode			= view.getDecode();
			exit			= view.getExit();
			
			//assign action events
			enc = new Encode();
			encode.addActionListener(enc);
			dec = new Decode();
			decode.addActionListener(dec);
			exit.addActionListener(new Exit());
			encButton = new EncodeButton();
			encodeButton.addActionListener(encButton);
			decButton = new DecodeButton();
			decodeButton.addActionListener(decButton);
			
			//encode view as default
			encode_view();
		}
		
		/*
		 *Updates the single panel to display the Encode View.
		 */
		protected void encode_view()
		{
			update();
			view.setContentPane(encode_panel);
			view.setVisible(true);
		}
		
		/*
		 *Updates the single panel to display the Decode View.
		 */
		private void decode_view()
		{
			update();
			view.setContentPane(decode_panel);
			view.setVisible(true);
		}
		
		/*
		 *Encode Class - handles the Encode menu item
		 */
		private class Encode implements ActionListener
		{
			/*
			 *handles the click event
			 *@param e The ActionEvent Object
			 */
			public void actionPerformed(ActionEvent e)
			{
				encode_view(); //show the encode view
			}
		}
		
		/*
		 *Decode Class - handles the Decode menu item
		 */
		private class Decode implements ActionListener
		{
			/*
			 *handles the click event
			 *@param e The ActionEvent Object
			 */
			public void actionPerformed(ActionEvent e)
			{
				decode_view(); //show the decode view
				
				//start path of displayed File Chooser
				JFileChooser chooser = new JFileChooser("./");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setFileFilter(new Image_Filter());
				int returnVal = chooser.showOpenDialog(view);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File directory = chooser.getSelectedFile();
					try{
						String image = directory.getPath();
						stat_name = directory.getName();
						stat_path = directory.getPath();
						stat_path = stat_path.substring(0,stat_path.length()-stat_name.length()-1);
						stat_name = stat_name.substring(0, stat_name.length()-4);
						image_input.setIcon(new ImageIcon(ImageIO.read(new File(image))));
					}
					catch(Exception except) {
					//msg if opening fails
					JOptionPane.showMessageDialog(view, "The File cannot be opened!", 
						"Error!", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}
		
		private class Exit implements ActionListener
		{
			/*
			 *handles the click event
			 *@param e The ActionEvent Object
			 */
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0); //exit the program
			}
		}
		
		/*
		 *Encode Button Class - handles the Encode Button item
		 */
		private class EncodeButton implements ActionListener
		{
			/*
			 *handles the click event
			 *@param e The ActionEvent Object
			 */
			public void actionPerformed(ActionEvent e)
			{
				//start path of displayed File Chooser
				JFileChooser chooser = new JFileChooser("./");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setFileFilter(new Image_Filter());
				int returnVal = chooser.showOpenDialog(view);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File directory = chooser.getSelectedFile();
					try{
						String text = input.getText();
						String ext  = Image_Filter.getExtension(directory);
						String name = directory.getName();
						String path = directory.getPath();
						path = path.substring(0,path.length()-name.length()-1);
						name = name.substring(0, name.length()-4);
						
						String stegan = JOptionPane.showInputDialog(view,
										"Enter output file name:", "File name",
										JOptionPane.PLAIN_MESSAGE);
						
						boolean[] boolSecretKey = new boolean[128];
						int reply = JOptionPane.showConfirmDialog(null,
								"Do you want to encode with your own key? If NO, a new key will be generated.", "Encoding", JOptionPane.YES_NO_CANCEL_OPTION);
						if (reply == JOptionPane.YES_OPTION) {
							String stringKey = new String();
							while (stringKey.length() != 24) {
									stringKey = JOptionPane.showInputDialog(null,
										"Enter your own secret key:", "Secret Key",
										JOptionPane.PLAIN_MESSAGE);
									if (stringKey.length() != 24) {
										JOptionPane.showMessageDialog(view, "The key must be 24 characters!", 
												"Error!", JOptionPane.ERROR_MESSAGE);
									}
							}
							byte[] byteKey = Base64.getDecoder().decode(stringKey);
							boolSecretKey = model.bytesToBooleans(byteKey);
							
							if(model.encode(path,name,ext,stegan,text, boolSecretKey))
							{
								JOptionPane.showMessageDialog(view, "The Image was encoded Successfully!", 
									"Success!", JOptionPane.INFORMATION_MESSAGE);
							}
							else
							{
								JOptionPane.showMessageDialog(view, "The Image could not be encoded!", 
									"Error!", JOptionPane.INFORMATION_MESSAGE);
							}
							encode_view();
						} else {
							if (reply == JOptionPane.NO_OPTION) {
								boolSecretKey = Key_Generator.generateSecretKey();
								
								if(model.encode(path,name,ext,stegan,text, boolSecretKey))
								{
									JOptionPane.showMessageDialog(view, "The Image was encoded Successfully!", 
										"Success!", JOptionPane.INFORMATION_MESSAGE);
								}
								else
								{
									JOptionPane.showMessageDialog(view, "The Image could not be encoded!", 
										"Error!", JOptionPane.INFORMATION_MESSAGE);
								}
								encode_view();
							}
						}
					}
					catch(Exception except) {
					//msg if opening fails
					JOptionPane.showMessageDialog(view, "The File cannot be opened!", 
						"Error!", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			
		}
		
		/*
		 *Decode Button Class - handles the Decode Button item
		 */
		private class DecodeButton implements ActionListener
		{
			/*
			 *handles the click event
			 *@param e The ActionEvent Object
			 */
			public void actionPerformed(ActionEvent e)
			{
				String stringKey = new String();
				while (stringKey.length() != 24) {
						stringKey = JOptionPane.showInputDialog(null,
							"Enter the secret key:", "Secret Key",
							JOptionPane.PLAIN_MESSAGE);
						if (stringKey.length() != 24) {
							JOptionPane.showMessageDialog(view, "The key must be 24 characters!", 
									"Error!", JOptionPane.ERROR_MESSAGE);
						}
				}
				byte[] byteKey = Base64.getDecoder().decode(stringKey);
				String message = model.decode(stat_path, stat_name, byteKey);
				System.out.println(stat_path + ", " + stat_name);
				if(message != "")
				{
					encode_view();
					JOptionPane.showMessageDialog(view, "The Image was decoded Successfully!", 
								"Success!", JOptionPane.INFORMATION_MESSAGE);
					input.setText(message);
				}
				else
				{
					JOptionPane.showMessageDialog(view, "The Image could not be decoded!", 
								"Error!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		
		/*
		 *Updates the variables to an initial state
		 */
		public void update()
		{
			input.setText("");			//clear textarea
			image_input.setIcon(null);	//clear image
			stat_path = "";				//clear path
			stat_name = "";				//clear name
		}
		
		/*
		 *Main Method for testing
		 */
		public static void main(String args[])
		{
			new Stego_Controller(
										new Stego_View("StegoRGB"),
										new Stego_Model()
										);
		}
}
