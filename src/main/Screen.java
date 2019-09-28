package main;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;

/**
 * a container for a name and rectangle of a display
 * @author McAJBen <McAJBen@gmail.com>
 * @since 2.0
 */
public class Screen implements Comparable<Screen> {
	
	/**
	 * the dimensions and position of the {@code Screen}
	 */
	private final Rectangle rectangle;
	
	/**
	 * the {@code IDString} obtained from the {@code GraphicsDevice}
	 */
	private final String name;
	
	/**
	 * boolean - is this the default graphicdevice ?
	 */
	private final boolean defaultstatus;
	
	/**
	 * creates a {@code Screen} instance by using a {@code GraphicsDevice}
	 * @param graphicsDevice the {@code GraphicsDevice} related to the display
	 */
	public Screen(GraphicsDevice graphicsDevice, boolean blnDefaultParam) {
		rectangle = graphicsDevice.getDefaultConfiguration().getBounds();
		name = graphicsDevice.getIDstring();
		defaultstatus = blnDefaultParam;
	}
	
	/**
	 * Implements a compareTo interface for sort ordering on screens
	 */
	@Override
    public int compareTo(Screen that) {
		double thatx = that.getRectangle().getX();

        if(this.getRectangle().getX() == that.getRectangle().getX())
            return 0;
        else
        	return Double.compare(this.getRectangle().x, thatx);
    }
	
	@Override
	public String toString() {
		return name + " " + rectangle.width + "x" + rectangle.height + ((defaultstatus == true) ? " (Main)" : "") ;
	}

	/**
	 * gets the size of the {@code Screen} just like calling {@code getRectangle().getSize()}
	 * @return {@code Dimension} of the {@code Screen}
	 */
	public Dimension getSize() {
		return rectangle.getSize();
	}

	/**
	 * gets the {@code Rectangle} describing the {@code Screen's} position and size
	 * @return {@code Rectangle} of the {@code Screen}
	 */
	public Rectangle getRectangle() {
		return rectangle;
	}
	

}