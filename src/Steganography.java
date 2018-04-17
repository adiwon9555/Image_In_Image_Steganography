 
/*
 *@author  Aditya Rathi
 *@version 2
 *Created: 22-Nov-2016
 */

/*
 *import list
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/*
 *Class Steganography
 */
public class Steganography
{
	
	/*
	 *Steganography Empty Constructor
	 */
	public Steganography()
	{
	}
	
	/*
	 *Encrypt an image with text, the output file will be of type .png
	 *@param path		 The path (folder) containing the image to modify
	 *@param original	The name of the image to modify
	 *@param ext1		  The extension type of the image to modify (jpg, png)
	 *@param stegan	  The output name of the file
	 *@param message  The text to hide in the image
	 *@param type	  integer representing either basic or advanced encoding
	 */
	public boolean encode(String path, String original, String ext1, String stegan, String epath,String ename,String eext, int old_key)
	{
		int key=key_modify(old_key);
		BufferedImage enc_image  = user_space(getImage(image_path(epath,ename,eext)));
		String			file_name 	= image_path(path,original,ext1);
		BufferedImage 	image_orig	= getImage(file_name);
		
		//user space is not necessary for Encrypting
		BufferedImage image_old = user_space(image_orig);
		BufferedImage image = add_text(image_old,enc_image,key);
		
		imageQuality(image_orig,image);
		
		return(setImage(image,new File(image_path(path,stegan,"png")),"png"));
	}
	public int key_modify(int old_key)
	{
		int temp=old_key;
		int key=0;
		for (int i = 1; i <=4; i++) 
		{	
			int a=temp%10;
			
			temp=temp/10;
			if(i==2 || i==4)
			{
				
				a=(a==9)?0:(a+1);
				
			}
			if(i==3 || i==1)
			{
				a=(a==0)?9:(a-1);
			}
			key=key*10+a;
			
		}
		
		System.out.println(key);
		
		return key;
		
	}
	public void imageQuality(BufferedImage image_old,BufferedImage image)
	{
		byte org_img[]=get_byte_data(image_old);
		byte new_img[]=get_byte_data(image);
		double mse=0;
		for(int i=0;i<org_img.length;i++)
		{
			mse=mse+Math.pow((org_img[i]-new_img[i]),2);
		}
		mse=mse/org_img.length;
		double psnr=10 * (Math.log10((Math.pow(255, 2))/mse));
		System.out.println("MSE = "+mse+ "\nPSNR = "+psnr );
		
	}
	
	/*
	 *Decrypt assumes the image being used is of type .png, extracts the hidden text from an image
	 *@param path   The path (folder) containing the image to extract the message from
	 *@param name The name of the image to extract the message from
	 *@param type integer representing either basic or advanced encoding
	 */
	public String decode(String path, String name,String stegan,int key)
	{
		BufferedImage image  = user_space(getImage(image_path(path,name,"png")));
		byte[] imageData;
		BufferedImage im;
		int width=getWidth(get_byte_data(image));
		int height=getHeight(get_byte_data(image));
		try
		{
			//user space is necessary for decrypting
			
			imageData = decode_text(get_byte_data(image),key);
			
	        

	        DataBuffer buffer = new DataBufferByte(imageData, imageData.length);
	        WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, 3 * width, 3, new int[]{2, 1, 0}, (Point) null);
	        ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	        im = new BufferedImage(cm, raster, true, null);
	        
			 setImage(im , new File(image_path(path,stegan,"png")),"png");
			 String a =path+"/"+stegan+".png";
			 System.out.println(a);
			 return a;
			
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
				"There is no hidden message in this image!","Error",
				JOptionPane.ERROR_MESSAGE);
			return "";
			
		}
	}
	
	/*
	 *Returns the complete path of a file, in the form: path\name.ext
	 *@param path   The path (folder) of the file
	 *@param name The name of the file
	 *@param ext	  The extension of the file
	 *@return A String representing the complete path of a file
	 */
	private String image_path(String path, String name, String ext)
	{
		return path + "/" + name + "." + ext;
	}
	
	/*
	 *Get method to return an image file
	 *@param f The complete path name of the image.
	 *@return A BufferedImage of the supplied file path
	 *@see	Steganography.image_path
	 */
	private BufferedImage getImage(String f)
	{
		BufferedImage 	image	= null;
		File 		file 	= new File(f);
		
		try
		{
			image = ImageIO.read(file);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, 
				"Image could not be read!","Error",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
	
	/*
	 *Set method to save an image file
	 *@param image The image file to save
	 *@param file	  File  to save the image to
	 *@param ext	  The extension and thus format of the file to be saved
	 *@return Returns true if the save is succesful
	 */
	private boolean setImage(BufferedImage image, File file, String ext)
	{
		try
		{
			file.delete(); //delete resources used by the File
			ImageIO.write(image,ext,file);
			return true;
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
				"File could not be saved!","Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	/*
	 *Handles the addition of text into an image
	 *@param image The image to add hidden text to
	 *@param text	 The text to hide in the image
	 *@return Returns the image with the text embedded in it
	 */
	private BufferedImage add_text(BufferedImage image, BufferedImage enc_image,int key)
	{
		//convert all items to byte arrays: image, message, message length
		byte img[]  = get_byte_data(image);
		byte msg[] = get_byte_data(enc_image);
		byte wid[]=bit_conversion(enc_image.getWidth());
		byte hit[] =bit_conversion(enc_image.getHeight());
		byte bkey[] =bit_conversion(key);
		byte len[]   = bit_conversion(msg.length);
		try
		{
			encode_text(img, bkey, 0,key);
			encode_text(img, len,  32,key); //0 first positiong
			encode_text(img, wid, 64,key);
			encode_text(img, hit, 96,key);//12 bytes of space for length: 4bytes*8bit = 32 bits , width and height
			encode_text(img, msg, 128,key); 
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
"Target File cannot hold message!", "Error",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
	
	/*
	 *Creates a user space version of a Buffered Image, for editing and saving bytes
	 *@param image The image to put into user space, removes compression interferences
	 *@return The user space version of the supplied image
	 */
	private BufferedImage user_space(BufferedImage image)
	{
		//create new_img with the attributes of image
		BufferedImage new_img  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D	graphics = new_img.createGraphics();
		graphics.drawRenderedImage(image, null);
		graphics.dispose(); //release all allocated memory for this image
		return new_img;
	}
	
	/*
	 *Gets the byte array of an image
	 *@param image The image to get byte data from
	 *@return Returns the byte array of the image supplied
	 *@see Raster
	 *@see WritableRaster
	 *@see DataBufferByte
	 */
	private byte[] get_byte_data(BufferedImage image)
	{
		/*byte[] imageInByte = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageInByte;*/
		WritableRaster raster   = image.getRaster();
		DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
		return buffer.getData();
	}
	
	/*
	 *Gernerates proper byte format of an integer
	 *@param i The integer to convert
	 *@return Returns a byte[4] array converting the supplied integer into bytes
	 */
	private byte[] bit_conversion(int i)
	{
		//originally integers (ints) cast into bytes
		//byte byte7 = (byte)((i & 0xFF00000000000000L) >>> 56);
		//byte byte6 = (byte)((i & 0x00FF000000000000L) >>> 48);
		//byte byte5 = (byte)((i & 0x0000FF0000000000L) >>> 40);
		//byte byte4 = (byte)((i & 0x000000FF00000000L) >>> 32);
		
		//only using 4 bytes
		byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
		byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
		byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
		byte byte0 = (byte)((i & 0x000000FF)	   );
		//{0,0,0,byte0} is equivalent, since all shifts >=8 will be 0
		return(new byte[]{byte3,byte2,byte1,byte0});
	}
	
	/*
	 *Encode an array of bytes into another array of bytes at a supplied offset
	 *@param image	 Array of data representing an image
	 *@param addition Array of data to add to the supplied image data array
	 *@param offset	  The offset into the image array to add the addition data
	 *@return Returns data Array of merged image and addition data
	 */
	private byte[] encode_text(byte[] image, byte[] addition, int offset,int key)
	{
		//check that the data + offset will fit in the image
		if(addition.length + offset> image.length)
		{
			throw new IllegalArgumentException("File not long enough!");
		}
		//loop through each addition byte
		if(offset==0 || offset==64) //only for key and width
		{
		for(int i=0; i<addition.length; ++i)
		{
			//loop through the 8 bits of each byte
			int add = addition[i];
			for(int bit=7; bit>=0; --bit, ++offset) //ensure the new offset value carries on through both loops
			{
				//assign an integer to b, shifted by bit spaces AND 1
				//a single bit of the current byte
				
				int b=	(add >>> bit) & 1;
				//assign the bit by taking: [(previous byte value) AND 0xfe] OR bit to add
				//changes the last bit of the byte in the image to be the bit of addition
				
				image[offset] = (byte)((image[offset] & 0xFE) | b );
				
			}
		}
		}
		if(offset==32 || offset==96) //only for length and height
		{
		for(int i=0; i<addition.length; ++i)
		{
			//loop through the 8 bits of each byte
			int add = addition[i];
			for(int bit=7; bit>=0; --bit, ++offset) //ensure the new offset value carries on through both loops
			{
				//assign an integer to b, shifted by bit spaces AND 1
				//a single bit of the current byte
				
				int b=	(add >>> bit) & 1;
				//assign the bit by taking: [(previous byte value) AND 0xfe] OR bit to add
				//changes the last bit of the byte in the image to be the bit of addition
				
				image[offset] = (byte)((image[offset] & 0xFD) | (b<<1) );
				
			}
		}
		}
		if(offset==128) //for message
		{
			int R[]=new int[4];
			R=keyCalculation(key);
			int ptr=0;
			for(int i=0; i<addition.length; ++i)
			{
				//loop through the 8 bits of each byte
				int add = addition[i];
				for(int bit=7; bit>=0; --bit, ++offset) //ensure the new offset value carries on through both loops
				{
					int b = (add >>> bit) & 1;
					//assign an integer to b, shifted by bit spaces AND 1
					//a single bit of the current byte
					if(R[ptr]==1)
					{
					
					image[offset] = (byte)((image[offset] & 0xFE) | b );
					}
					else
					{
						
						
						image[offset] = (byte)((image[offset] & 0xFD) | (b<<1) );
					}
					ptr=ptr==3?0:ptr+1;
				
				}
				
				
				
			}
			
			
		}
		return image;
	}
	
	public int authenticate_Key(String path, String name,int key)
	{
		byte[] imageBytes;
		BufferedImage image  = user_space(getImage(image_path(path,name,"png")));
		imageBytes=get_byte_data(image);
		int key_real=0;
		for(int i=0; i<32; ++i) 
		{
			key_real = (key_real << 1) | (imageBytes[i] & 1);
		
		}
			System.out.println(key_real);
			if(key_real==key)
			{
				return 1;
			}
			else
			{
				return 0;
			}
			
			
		
	}
	
	private int[] keyCalculation(int key)
	{
		int R[]=new int[4];
		for(int i=3; i>=0;i--)
		{
			R[i]=key%10;
			key=key/10;
		}
		for(int temp=0,next=0,i=0;i<=3;i++)
		{
			int pv,dif;
			next=(i==3?0:i+1);
			if(R[i]==0)
			{
			pv=1;
			}
			else
			{
				pv=R[i];
			}
			temp=(pv*10)+R[next];
			dif=Math.abs((temp/10)-(temp%10));
			switch(dif)
			{
			case 0:
					R[i]=2;
					break;
			case 1:
				R[i]=1;
				break;
			case 2:
				R[i]=2;
				break;
			case 3:
				R[i]=2;
				break;
			case 4:
				R[i]=1;
				break;
			case 5:
				R[i]=1;
				break;
			case 6:
				R[i]=2;
				break;
			case 7:
				R[i]=2;
				break;
			case 8:
				R[i]=1;
				break;
			case 9:
				R[i]=1;
				break;
			default:
				System.out.println("Something went wrong");
				break;
				
			}
			
			
			
		}
		
		return R;
	}
	/*
	 *Retrieves hidden text from an image
	 *@param image Array of data, representing an image
	 *@return Array of data which contains the hidden text
	 */
	private int getWidth(byte[] image)
	{
		int width=0;
		for(int i=64; i<96; ++i) //i=24 will also work, as only the 4th byte contains real data
		{
			width = (width << 1) | (image[i] & 1);
		}
		return width;
	}
	private int getHeight(byte[] image)
	{
		int height=0;
		for(int i=96; i<128; ++i) //i=24 will also work, as only the 4th byte contains real data
		{
			
			height = (height << 1) | ((image[i] & 2)>>>1);
		}
		return height;
	}
	private byte[] decode_text(byte[] image,int key)
	{
		int length = 0;
		
		int offset  = 128;
		int R[]=new int[4];
		R=keyCalculation(key);
		int ptr=0;
		//loop through 32 bytes of data to determine text length
		
		for(int i=32; i<64; ++i) 
		{
			length = (length << 1) | ((image[i] & 2)>>>1);
		
		}
		
		byte[] result = new byte[length];
		
		//loop through each byte of text
		for(int b=0; b<result.length; ++b )
		{
			
			//loop through each bit of particular position given by R[ptr] within a byte of text
			
			for(int i=0; i<8; ++i, ++offset)
			{
				if(R[ptr]==1)
				{
				//assign bit: [(new byte value) << 1] OR [(text byte) AND 1]
				result[b] = (byte)((result[b] << 1) | (image[offset] & 1));
				}
				else
				{
					result[b] = (byte)((result[b] << 1) | ((image[offset] & 2)>>>1));
				}
				ptr=ptr==3?0:ptr+1;
				
			}
			
		
		
		}
		
		
		return result;

	}
}
