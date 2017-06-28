import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Stego_Model {
	
	static final int TYPE=BufferedImage.TYPE_3BYTE_BGR;
	
	//Stego_Model Empty Constructor (could be omitted because the default constructor is empty)
	public Stego_Model() {
		
	}


	/*
	 *Encode an image with text, the output file will be of type .png
	 *@param path		 The path (folder) containing the image to modify
	 *@param original	The name of the image to modify
	 *@param ext1		  The extension type of the image to modify (jpg, png)
	 *@param stegan	  The output name of the file
	 *@param message  The text to hide in the image
	 *@param secretKey The secret key for encoding
	 */
	public boolean encode(String path, String original, String ext1, String stegan, String message, boolean[] secretKey)
	{
		boolean result = false;
		String			file_name 	= image_path(path,original,ext1);
		BufferedImage 	image_orig	= getImage(file_name);
		
		//get width and height of the original image
		int w = image_orig.getWidth();
	    int h = image_orig.getHeight();
	    
	    //N.B.: user space is not necessary for Encoding
	  	BufferedImage image = user_space(image_orig);
	    
	    //create 3 byte 2D arrays for each RGB matrix
	    byte[][] redMatrix = new byte[h][w];
	    byte[][] greenMatrix = new byte[h][w];
	    byte[][] blueMatrix = new byte[h][w];
	    
	    //create 3 byte 1D arrays for each RGB matrix
	    byte[] redArray = new byte[w*h];
	    byte[] greenArray = new byte[w*h];
	    byte[] blueArray = new byte[w*h];
	    
	    //split RGB planes
	    splitRGB(image, redMatrix, greenMatrix, blueMatrix);
	
	    //convert 2D arrays into 1D arrays
	    matrixToArray(redArray, redMatrix, h, w);
	    matrixToArray(greenArray, greenMatrix, h, w);
	    matrixToArray(blueArray, blueMatrix, h, w);
	    
	    //convert byte[] array to boolean[] array
	    boolean boolRedArray[] = new boolean[w*h*8];
	    boolRedArray = bytesToBooleans(redArray);
	    boolean boolGreenArray[] = new boolean[w*h*8];
	    boolGreenArray = bytesToBooleans(greenArray);
	    boolean boolBlueArray[] = new boolean[w*h*8];
	    boolBlueArray = bytesToBooleans(blueArray);
	    
	    //add text to the three boolean arrays
	    try {
	    	result = add_text(message, boolRedArray, boolGreenArray, boolBlueArray, secretKey);
	    } catch (Exception e1) {
	    	JOptionPane.showMessageDialog(null, 
					"There is no text to be encoded!", "Error",JOptionPane.ERROR_MESSAGE);
	    }
	    
	    if (result) {
	    	//convert the modified boolean[] arrays into three byte[] arrays
		    byte[] byteRedArray = convertToByteArray(boolRedArray);
		    byte[] byteGreenArray = convertToByteArray(boolGreenArray);
		    byte[] byteBlueArray = convertToByteArray(boolBlueArray);
		    
		    //set and return the new stego image
		    byte[] cover_image = get_byte_data(image);
		    for (int i=0, pixel=0; pixel<cover_image.length/3; pixel++, i+=3) {
		    	cover_image[i] = (byteBlueArray[pixel]);
		    	cover_image[i+1] = (byteGreenArray[pixel]);
		    	cover_image[i+2] = (byteRedArray[pixel]);
		    }
	    	return(setImage(image,new File(image_path(path,stegan,"png")),"png"));
	    } else {
	    	return false;
	    }
	}
	
	/*
	 *Gets the byte array of an image
	 *@param image The image to get byte data from
	 *@return Returns the byte array of the image
	 */
	private byte[] get_byte_data(BufferedImage image)
	{
		WritableRaster raster   = image.getRaster();
		DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
		return buffer.getData();
	}
	
	/*
	 *Decoding, that is extracts the hidden text from an image .png
	 *@param path   The path (folder) containing the image to extract the message from
	 *@param name The name of the image to extract the message from
	 *@param byteSecretKey byte[] array representing the secret key
	 *@return String which contains the hidden text
	 */
	public String decode(String path, String name, byte[] byteSecretKey)
	{
		byte[] decode;
		try
		{
			//user space is necessary for decrypting
			BufferedImage image  = user_space(getImage(image_path(path,name,"png")));
			int w = image.getWidth();
		    int h = image.getHeight();
		    
			byte[][] redMatrix = new byte[h][w];
		    byte[][] greenMatrix = new byte[h][w];
		    byte[][] blueMatrix = new byte[h][w];
		    byte[] redArray = new byte[w*h];
		    byte[] greenArray = new byte[w*h];
		    byte[] blueArray = new byte[w*h];
		    
			splitRGB(image, redMatrix, greenMatrix, blueMatrix);
			
			matrixToArray(redArray, redMatrix, h, w);
		    matrixToArray(greenArray, greenMatrix, h, w);
		    matrixToArray(blueArray, blueMatrix, h, w);
		    boolean boolRedArray[] = new boolean[w*h*8];
		    boolRedArray = bytesToBooleans(redArray);
		    boolean boolGreenArray[] = new boolean[w*h*8];
		    boolGreenArray = bytesToBooleans(greenArray);
		    boolean boolBlueArray[] = new boolean[w*h*8];
		    boolBlueArray = bytesToBooleans(blueArray);
		    
		    decode = extract_text(boolRedArray, boolGreenArray, boolBlueArray, byteSecretKey);
		    
			return(new String(decode));
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
				"The decoding operation is failed!","Error",
				JOptionPane.ERROR_MESSAGE);
			return "";
		}
	}
	
	/*
	 *Retrieves hidden text from an image
	 *@param boolRed Array of booleans, representing the red matrix of the image
	 *@param boolGreen Array of booleans, representing the green matrix of the image
	 *@param boolBlue Array of booleans, representing the blue matrix of the image
	 *@param SecretKey byte[] array, representing the secret key
	 *@return byte[] array representing the hidden text
	 */
	private byte[] extract_text(boolean[] boolRed, boolean[] boolGreen, boolean[] boolBlue, byte[] SecretKey)
	{
		
		int length = 0;
		
		boolean[] lengthArray = new boolean[32];
		boolean[] boolSecretKey = bytesToBooleans(SecretKey);
	
		for (int i=0, bit=7; i<32; i+=3, bit+=24) {
			if(boolSecretKey[i] ^ boolRed[bit]) {
				lengthArray[i] = boolGreen[bit];
			} else {
				lengthArray[i] = boolBlue[bit];
			}
			if (boolSecretKey[i+1] ^ boolGreen[bit+8]) {
				lengthArray[i+1] = boolRed[bit+8];
			} else {
				lengthArray[i+1] = boolBlue[bit+8];
			}
			if (i==30) {break;}
			if (boolSecretKey[i+2] ^ boolBlue[bit+16]) {
				lengthArray[i+2] = boolRed[bit+16];
			} else {
				lengthArray[i+2] = boolGreen[bit+16];
			}
		}
		byte[] byteLength = convertToByteArray(lengthArray);
		length = fromByteArray(byteLength);
		
		byte[] result = new byte[length];
		boolean[] boolResult = new boolean[length*8];
		
		for (int i=0, bit=263, dim=0; dim<(length*8); i+=3, bit+=24, dim+=3) {
			if(i>=boolSecretKey.length){i=0;}
			if(boolSecretKey[i] ^ boolRed[bit]) {
				boolResult[dim] = boolGreen[bit];
			} else {
				boolResult[dim] = boolBlue[bit];
			}
			if ((dim+1)==(length*8)) {break;}
			if((i+1)>=boolSecretKey.length){i=-1;}
			if (boolSecretKey[i+1] ^ boolGreen[bit+8]) {
				boolResult[dim+1] = boolRed[bit+8];
			} else {
				boolResult[dim+1] = boolBlue[bit+8];
			}
			if ((dim+2)==(length*8)) {break;}
			if((i+2)>=boolSecretKey.length){i=-2;}
			if (boolSecretKey[i+2] ^ boolBlue[bit+16]) {
				boolResult[dim+2] = boolRed[bit+16];
			} else {
				boolResult[dim+2] = boolGreen[bit+16];
			}
		}
		
		result = convertToByteArray(boolResult);
		return result;
	}
	
	/*
	 *Returns the complete path of a file, in the form: path\name.ext
	 *@param path   The path (folder) of the file
	 *@param name The name of the file
	 *@param ext	  The extension of the file
	 *@return A String representing the complete path of a file
	 */
	
	private static String image_path(String path, String name, String ext)
	{
		return path + "\\" + name + "." + ext;
	}
	
	/*
	 *Get method to return an image file
	 *@param f The complete path name of the image
	 *@return A BufferedImage of the supplied file path
	 */
	
	private BufferedImage getImage(String f)
	{
		BufferedImage image = null;
		File file = new File(f);
		
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
	 *Convert a 2D array into 1D array
	 *@param array The destination array
	 *@param matrix The matrix that has to be converted
	 *@param height The height of the matrix
	 *@param width The width of the matrix
	*/
	private void matrixToArray(byte[] array, byte[][] matrix, int height, int width) {
		for(int y = 0; y < height; ++y) {
	        for(int x = 0; x < width; ++x) {
	            byte number = matrix[y][x];
	            array[y*width+x] = number;
	        }
		}
	}
	
	/*
	 *Convert a 1D array into 2D array
	 *@param arr The array that has to be converted
	 *@param h The height of the destination matrix
	 *@param w The width of the destination matrix
	 *@return The destination matrix
	*/
	static int[][] arrayToMatrix(int[] arr, int w, int h) {
		int matrix[][] = new int[w][h];
		
		for(int i=0; i<w; ++i) {
			for(int j=0;j<h; ++j) {
				matrix[i][j] = arr[j%h+i*h];
			}
		}
		return matrix;
	}
	
	/*
	 *Convert a byte[] array into boolean[] array
	 *@param arr The byte[] array that has to be converted
	 *@return The destination boolean[] array
	*/
	public boolean [] bytesToBooleans(byte [] bytes){
	    boolean [] bools = new boolean[bytes.length*8];
	    byte [] pos = new byte[]{(byte)0x80, 0x40, 0x20, 0x10, 0x8, 0x4, 0x2, 0x1};
	
	    for(int i = 0; i < bytes.length; ++i){
	        for(int j = i * 8, k = 0; k < 8; ++j, ++k){
	            bools[j] = (bytes[i] & pos[k]) != 0; //se è diverso da 0 restituisce true altrimenti 0, e infatti questo risulta proprio il valore del bit.
	        }
	    }
	
	    return bools;
	}
	
	/*
	 * Convert a boolean[] array into byte[] array
	 * This will round down to the nearest number of bytes.  So it will chop off the last few booleans.
	 * Eg: If there are 9 booleans, then that will be 1 byte, and it will lose the last boolean.
	 *@param booleans The boolean[] array that has to be converted
	 *@return The destination byte[] array
	*/
	
	private static byte[] convertToByteArray(boolean[] booleans) {
		  byte[] result = new byte[booleans.length/8];
	
		  for (int i=0; i<result.length; ++i) {
		      int index = i*8;
		      byte b = (byte)(
		              (booleans[index+0] ? 1<<7 : 0) +
		              (booleans[index+1] ? 1<<6 : 0) +
		              (booleans[index+2] ? 1<<5 : 0) + 
		              (booleans[index+3] ? 1<<4 : 0) +
		              (booleans[index+4] ? 1<<3 : 0) +
		              (booleans[index+5] ? 1<<2 : 0) + 
		              (booleans[index+6] ? 1<<1 : 0) +
		              (booleans[index+7] ? 1 : 0));
		      result[i] = (byte) b;
		  }
	
		  return result;
		}
	
	/*
	 *Creates a user space version of a Buffered Image, for editing and saving bytes
	 *@param image The image to put into user space, removes compression interferences
	 *@return The user space version of the image
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
	 *Handles the addition of text into the image
	 *@param text The text to hide in the image
	 *@param boolRed The boolean[] array representing the red matrix
	 *@param boolgreen The boolean[] array representing the green matrix
	 *@param boolBlue The boolean[] array representing the blue matrix
	 *@param key The boolean[] array representing the secret key
	 */
	private boolean add_text(String text, boolean[] boolRed, boolean[] boolGreen, boolean[] boolBlue, boolean[] key)
	{
		//convert message and message length to byte arrays
		if (text.length()==0) {
			throw new IllegalArgumentException("There is no text!");
		} else {
			byte msg[] = text.getBytes();
			byte len[]   = bit_conversion(msg.length);
			boolean boolMsg[] = bytesToBooleans(msg);
			boolean boolLen[] = bytesToBooleans(len);
			
			try
			{
				encode_text(boolRed, boolGreen, boolBlue, boolLen, boolMsg, key);
				return true;
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null, 
						"Target File not long enough! Can't hold your text!", "Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
	}
	
	/*
	*Split the image into three (red, green and blue) matrices
	*@param imageToSplit The image to split
	*@param RedMat The byte[][] matrix representing the red matrix
	*@param GreenMat The byte[][] matrix representing the green matrix
	*@param BlueMat The byte[][] matrix representing the blue matrix
	*/
	private void splitRGB(BufferedImage imageToSplit, byte[][] RedMat, byte[][] GreenMat, byte[][] BlueMat) {
		
		for (int y=0;y<imageToSplit.getHeight();++y)
			for (int x=0;x<imageToSplit.getWidth();++x)
			{
				int pixel=imageToSplit.getRGB(x,y);
	
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;
	
				RedMat[y][x] = (byte) red;
				GreenMat[y][x] = (byte) green;
				BlueMat[y][x] = (byte) blue;
			}
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
	 *Encode bits of the message into the three matrices (red, green and blue)
	 *@param red boolean[] array representing the bits of the red matrix
	 *@param green boolean[] array representing the bits of the green matrix
	 *@param blue boolean[] array representing the bits of the blue matrix
	 *@param length boolean[] array representing the length of the text
	 *@param text boolean[] array representing the data to add to the matrices
	 *@param secretKey boolean[] array representing the secret key
	 */
	private void encode_text(boolean[] red, boolean[] green, boolean[] blue, boolean[] length, boolean[] text, boolean[] secretKey)
	{
			//check that the data will fit in the image
			if((length.length + 0 > (red.length)/8) || (text.length + 32 > (red.length)/8)) //un bit salvato in ogni pixel (red.length/8 = w*h (numero di pixels)
			{
				throw new IllegalArgumentException("File not long enough!");
			} else {
				
					for (int i=0, bit=7; i<length.length; i+=3, bit+=24) {
						if(secretKey[i] ^ red[bit]) {
							green[bit] = length[i];
						} else {
							blue[bit] = length[i];
						}
						if (secretKey[i+1] ^ green[bit+8]) {
							red[bit+8] = length[i+1];
						} else {
							blue[bit+8] = length[i+1];
						}
						if (i==30) {break;}
						if (secretKey[i+2] ^ blue[bit+16]) {
							red[bit+16] = length[i+2];
						} else {
							green[bit+16] = length[i+2];
						}
					}
					for (int i=0, bit=263, dim=0; dim<text.length; i+=3, bit+=24, dim+=3) {
						if(i>=secretKey.length){i=0;}
						if(secretKey[i] ^ red[bit]) {
							green[bit] = text[dim];
						} else {
							blue[bit] = text[dim];
						}
						if ((dim+1)==text.length) {break;}
						if((i+1)>=secretKey.length){i=-1;}
						if (secretKey[i+1] ^ green[bit+8]) {
							red[bit+8] = text[dim+1];
						} else {
							blue[bit+8] = text[dim+1];
						}
						if ((dim+2)==text.length) {break;}
						if((i+2)>=secretKey.length){i=-2;}
						if (secretKey[i+2] ^ blue[bit+16]) {
							red[bit+16] = text[dim+2];
						} else {
							green[bit+16] = text[dim+2];
						}
					}
				}
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
		
		return(new byte[]{byte3,byte2,byte1,byte0});
	}
	
	/*
	 * Packing an array of 4 bytes to an int, big endian
	 * @param bytes byte[] array to convert
	 * @return Int representing the byte[] array
	 */
	int fromByteArray(byte[] bytes) {
		return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}
}