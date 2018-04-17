
/*
 *@author Aditya Rathi
 *Created March 24, 2017
 */

/*
 *Import List
 */
import java.io.File;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
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

/*
 *Steganography_Controller Class
 */
public class Steganography_Controller
{
	//Program Variables
	private Steganography_View	view;
	private Steganography		model;
	
	//Panel Displays
	private JPanel		decode_panel;
	private JPanel		encode_panel;
	//Panel Variables
	private JLabel 	input;
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
	private String			stat_epath = "";
	private String			stat_ename = "";
	private String			stat_eext = "";
	/*
	 *Constructor to initialize view, model and environment variables
	 *@param aView  A GUI class, to be saved as view
	 *@param aModel A model class, to be saved as model
	 */
	public Steganography_Controller(Steganography_View aView, Steganography aModel)
	{
		//program variables
		view  = aView;
		model = aModel;
		
		//assign View Variables
		//2 views
		encode_panel	= view.getEncodeImagePanel();
		decode_panel	= view.getImagePanel();
		//2 data options
		input			= view.getInput();
		image_input		= view.getImageInput();
		//2 buttons
		encodeButton	= view.getEButton();
		decodeButton	= view.getDButton();
		//menu
		encode			= view.getEncode();
		decode			= view.getDecode();
		exit			= view.getExit();
		
		//assign action events
		
		dec = new Decode();
		decode.addActionListener(dec);
		enc = new Encode();
		encode.addActionListener(enc);
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
	private void encode_view()
	{
		input.setText("Select Encode / Decode Option From Menu");
    	input.setFont(input.getFont().deriveFont(25f));
		update();
		view.setContentPane(encode_panel);
		view.setVisible(true);
	}
	
	/*
	 *Updates the single panel to display the Decode View.
	 */
	private void decode_view()
	{
		image_input.setText("Select Encode / Decode Option From Menu");
    	image_input.setFont(input.getFont().deriveFont(25f));
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
			encodeButton.setEnabled(false);
			encodeButton.setText("Encode Now");
			//start path of displayed File Chooser
			JFileChooser chooser = new JFileChooser("./");
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setFileFilter(new Image_Filter());
			int returnVal = chooser.showOpenDialog(view);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File directory = chooser.getSelectedFile();
				try{
					String image = directory.getPath();
					stat_ename = directory.getName();
					stat_epath = directory.getPath();
					stat_eext = Image_Filter.getExtension(directory);
					stat_epath = stat_epath.substring(0,stat_epath.length()-stat_ename.length()-1);
					stat_ename = stat_ename.substring(0, stat_ename.length()-4);
					input.setText("");
					input.setIcon(new ImageIcon(ImageIO.read(new File(image))));
				}
				catch(Exception except) {
				//msg if opening fails
				JOptionPane.showMessageDialog(view, "The File cannot be opened!", 
					"Error!", JOptionPane.INFORMATION_MESSAGE);
				}
				encodeButton.setEnabled(true);
			}
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
			decodeButton.setEnabled(false);
			decodeButton.setText("Decode Now");
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
					image_input.setText("");
					image_input.setIcon(new ImageIcon(ImageIO.read(new File(image))));
				}
				catch(Exception except) {
				//msg if opening fails
				JOptionPane.showMessageDialog(view, "The File cannot be opened!", 
					"Error!", JOptionPane.INFORMATION_MESSAGE);
				}
				decodeButton.setEnabled(true);
			}
		}
	}
	
	/*
	 *Exit Class - handles the Exit menu item
	 */
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
					
					String ext  = Image_Filter.getExtension(directory);
					String name = directory.getName();
					String path = directory.getPath();
					path = path.substring(0,path.length()-name.length()-1);
					name = name.substring(0, name.length()-4);
					
					String stegan = JOptionPane.showInputDialog(view,
									"Enter output file name:", "File name",
									JOptionPane.PLAIN_MESSAGE);
					
					int key=0,f=1;
					try {
						key=Integer.parseInt(JOptionPane.showInputDialog(view, "Enter 4 digit key!", "Key", 
								JOptionPane.PLAIN_MESSAGE));
					} catch (Exception e1) {
						
						f=0;
					}
					if(!(key>=1000 && key<=9999)&&f==1)
					{
						f=2;
						
					}
					int c=0;
					while(f!=1&&c==0)
					{
					try {
						
						
							c=1;
							System.out.println(f);
							key=getkey(f);
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						c=0;
						f=0;
					}
					}
					if(model.encode(path,name,ext,stegan,stat_epath,stat_ename,stat_eext,key))
					{
						JOptionPane.showMessageDialog(view, "The Image was encoded Successfully! To continue Select option from menu", 
							"Success!", JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						JOptionPane.showMessageDialog(view, "The Image could not be encoded!", 
							"Error!", JOptionPane.INFORMATION_MESSAGE);
					}
					//display the new image
					decode_view();
					decodeButton.setEnabled(false);
					decodeButton.setText("Your Encoded Image");
					image_input.setText("");
					image_input.setIcon(new ImageIcon(ImageIO.read(new File(path + "/" + stegan + ".png"))));
				}
				catch(Exception except) {
				//msg if opening fails
				JOptionPane.showMessageDialog(view, "The File cannot be opened!", 
					"Error!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			
		}
		public int getkey(int f)
		{
			int key=0;
			
			
			while(f==0)
			{
				f=1;
				try {
					key=Integer.parseInt(JOptionPane.showInputDialog(view, "4 digit NUMBERS are only allowed...Enter 4 digit key!", "Key", 
							JOptionPane.ERROR_MESSAGE));
				} catch (Exception e1) {
					
					f=0;
				}
				
			}
			
			while(!(key>=1000 && key<=9999)||f==2)
			{
				f=1;
				key=Integer.parseInt(JOptionPane.showInputDialog(view, "Idiot!!Only 4 digits.... Enter 4 digit key!", "Key", 
						JOptionPane.ERROR_MESSAGE));
			}
			return key;
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
			int auth=1,key=0,count=0;
			do
			{
					if(count>3)
						break;
				
			int f=1;
			
			try {
				if(auth==1)
				{
				key=Integer.parseInt(JOptionPane.showInputDialog(view, "Enter 4 digit key!", "Key", 
						JOptionPane.PLAIN_MESSAGE));
				}
				else
				{
					key=Integer.parseInt(JOptionPane.showInputDialog(view, "Wrong key...Enter 4 digit key!", "Key", 
							JOptionPane.ERROR_MESSAGE));
				}
			} catch (NumberFormatException e1) {
				
				f=0;
			}
			
			while(f==0)
			{
				f=1;
				try {
					key=Integer.parseInt(JOptionPane.showInputDialog(view, "4 digit NUMBERS are only allowed...Enter 4 digit key!", "Key", 
							JOptionPane.ERROR_MESSAGE));
				} catch (NumberFormatException e1) {
					
					f=0;
				}
			
			
			
			}
			
			
			auth=model.authenticate_Key(stat_path, stat_name,key);
			if(auth==1)
			{
				break;
			}
			count++;

			}
			while(auth==0);
			if(count<=3)
			{
			try{
			
			String stegan = JOptionPane.showInputDialog(view,
					"Enter output file name:", "File name",
					JOptionPane.PLAIN_MESSAGE);
	
				String image=model.decode(stat_path,stat_name,stegan,key);
					
			if(image != "")
			{
				encode_view();
				input.setText("");
				encodeButton.setEnabled(false);
				encodeButton.setText("Your Output Image");
				JOptionPane.showMessageDialog(view, "The Image was decoded Successfully!", 
							"Success!", JOptionPane.INFORMATION_MESSAGE);
				input.setIcon(new ImageIcon(ImageIO.read(new File(image))));
			}
			else
			{
				JOptionPane.showMessageDialog(view, "The Image could not be decoded!", 
							"Error!", JOptionPane.INFORMATION_MESSAGE);
			}
			}
			catch(Exception except) {
				
				JOptionPane.showMessageDialog(view, "The File cannot be decoded!", 
					"Error!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(view, "Maximum tries exceeded",
						"Dont forget Your Key",JOptionPane.ERROR_MESSAGE);
			}
		}
			
	}
	
	/*
	 *Updates the variables to an initial state
	 */
	public void update()
	{
		input.setIcon(null);			//clear image
		image_input.setIcon(null);	//clear image
		stat_path = "";				//clear path
		stat_name = "";				//clear name
	}
	
	/*
	 *Main Method for testing
	 */
	public static void main(String args[])
	{
		new Steganography_Controller(
									new Steganography_View("Image_In_Image"),
									new Steganography()
									);
	}
}