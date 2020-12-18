package photoshop;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class Photoshop extends Component {

	//VARIABLES
    private String outputName;
    private Color[][] pixels;
    private int w,h;
    
   
    //METHODS
    public void brighten(int amount) {
    	
        for(int i = 0; i < pixels.length; i++) {
        	for(int j = 0; j < pixels[i].length; j++) {
        		Color c = pixels[i][j];  
        		pixels[i][j] = new Color(Math.min(c.getRed() + amount, 255), 
        									Math.min(c.getGreen() + amount, 255), 
        									Math.min(c.getBlue() + amount, 255));
        	}
        }
  
        outputName = "brightened_" + outputName;
    }
    
    public void darken(int amount) {
    	
    	 for(int i = 0; i < pixels.length; i++) {
    		 for(int j = 0; j < pixels[i].length; j++) {
    			Color c = pixels[i][j];  
         		pixels[i][j] = new Color(Math.max(c.getRed() - amount, 0), 
         									Math.max(c.getGreen() - amount, 0), 
         									Math.max(c.getBlue() - amount, 0));
    		 }
    	 }
    	 
    	 outputName = "darkened_" + outputName;
    }
  
    public void negate() {
        
        for(int i = 0; i < pixels.length; i++) {
        	for(int j = 0; j < pixels[i].length; j++) {
        		Color c = pixels[i][j];  
        		pixels[i][j] = new Color(255 - c.getRed(), 
        								255 - c.getGreen(), 
        								255 - c.getBlue());
        	}
        }
        
        outputName = "negated_" + outputName;
    }
    
    public void blur() {
		
		for(int i = 0; i < pixels.length; i++) {
			for(int j = 0; j < pixels[i].length; j++) {
				
				//check its not an edge pixel
				if(i != 0 && i != pixels.length-1 && j != 0 && j != pixels.length-1) {
					
					int avgRed = (pixels[i-1][j-1].getRed() + pixels[i][j-1].getRed() + pixels[i+1][j-1].getRed() +
								pixels[i-1][j].getRed() + pixels[i][j].getRed() + pixels[i+1][j].getRed() +
								pixels[i-1][j+1].getRed() + pixels[i][j+1].getRed() + pixels[i+1][j+1].getRed()) / 9;
					
					int avgGreen = (pixels[i-1][j-1].getGreen() + pixels[i][j-1].getGreen() + pixels[i+1][j-1].getGreen() +
							pixels[i-1][j].getGreen() + pixels[i][j].getGreen() + pixels[i+1][j].getGreen() +
							pixels[i-1][j+1].getGreen() + pixels[i][j+1].getGreen() + pixels[i+1][j+1].getGreen()) / 9;
					
					int avgBlue = (pixels[i-1][j-1].getBlue() + pixels[i][j-1].getBlue() + pixels[i+1][j-1].getBlue() +
							pixels[i-1][j].getBlue() + pixels[i][j].getBlue() + pixels[i+1][j].getBlue() +
							pixels[i-1][j+1].getBlue() + pixels[i][j+1].getGreen() + pixels[i+1][j+1].getBlue()) / 9;
					
					pixels[i][j] = new Color(avgRed, avgGreen, avgBlue);
				}
			}
		}
		
		outputName = "blurred_" + outputName;
	}
    

    public void run() throws IOException {
    	JFileChooser fc = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+ "Images");
		fc.setCurrentDirectory(workingDirectory);
		fc.showOpenDialog(null);
		File my_file = fc.getSelectedFile();
		if (my_file == null)
			System.exit(-1);
		
		// reads the image file and creates our 2d array
        BufferedImage image = ImageIO.read(my_file);
        BufferedImage new_image = new BufferedImage(image.getWidth(),
                        image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        create_pixel_array(image);
		outputName = my_file.getName();
		
		// runs the manipulations determined by the user
		System.out.println("Enter the manipulations you would like to run on the image.\nYour "
				+ "choices are: brighten, darken, negate, or blur.\nEnter each "
				+ "manipulation you'd like to run, then type in 'done'.");
		Scanner in = new Scanner(System.in);
		String action = in.next().toLowerCase();
		while (!action.equals("done")) {
    			try {
	    			if (action.equals("brighten")) {
	    				System.out.println("enter an amount to increase the brightness by");
	    				int brightness = in.nextInt();
	        			Method m = getClass().getDeclaredMethod(action, int.class);
	        			m.invoke(this, brightness);
	    			}
	    			else if (action.equals("darken")) {
	    				System.out.println("enter an amount to decrease the brightness by");
	    				int darkness = in.nextInt();
	    				Method m = getClass().getDeclaredMethod(action, int.class);
	    				m.invoke(this, darkness);
	    			}
	    			else {
	        			Method m = getClass().getDeclaredMethod(action);
	        			m.invoke(this, new Object[0]);
	    			}
	    			System.out.println("done. enter another action, or type 'done'");
    			}
    			catch (NoSuchMethodException e) {
    				System.out.println("not a valid action, try again");
    			} catch (IllegalAccessException e) {} 
    			catch (IllegalArgumentException e) {}
    			catch (InvocationTargetException e) {}
    			
    			action = in.next().toLowerCase();
    		} 
        in.close();
        
        // turns our 2d array of colors into a new png file
        create_new_image(new_image);
        File output_file = new File("Images/" + outputName);
        ImageIO.write(new_image, "png", output_file);
    }
    
    public void create_pixel_array(BufferedImage image) {
        w = image.getWidth();
        h = image.getHeight();
        pixels = new Color[h][];
        for (int i = 0; i < h; i++) {
            pixels[i] = new Color[w];
            for (int j = 0; j < w; j++) {
                pixels[i][j] = new Color(image.getRGB(j,i));
            }
        }
    }

    public void create_new_image(BufferedImage new_image) {
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
            		new_image.setRGB(j, i, pixels[i][j].getRGB());
            }
        }
    }

    public static void main(String[] args) {
		new Photoshop();
	}

    public Photoshop() {
        try {
			run();
		} catch (IOException e) {
			System.out.println("Image does not exist :(");}
    }
}