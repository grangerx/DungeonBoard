package paint;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;

import main.Main;
import main.Settings;

/**
 * Similar to {@code PicturePanel} this is used by the {@code ControlPaint} to handle user inputs
 * This is also the area that the user draws to update the {@code DisplayPaint}
 * @author McAJBen <McAJBen@gmail.com>
 * @since 1.0
 */
public class DrawPanel extends JComponent {
	
	private static final long serialVersionUID = -3142625453462827948L;
	
	/**
	 * the radius of the users Pen. Or in the case of a square, half the width
	 */
	private int radius;
	
	/**
	 * the diameter of the users Pen. Or in the case of a square this will be the width
	 */
	private int diameter;
	
	/**
	 * the drawing utensil in use (CIRCLE, SQUARE)
	 */
	private Pen penType;
	
	/**
	 * the actual {@code BufferedImage} that is being drawn onto and the mask is created from
	 */
	private BufferedImage drawingLayer;
	
	/**
	 * the {@code Graphics2D} for {@code drawingLayer}
	 */
	private Graphics2D g2;
	
	/**
	 * the size of the {@code DrawPanel}, used to find the size that the image needs to be drawn to
	 */
	private Dimension controlSize;
	
	/**
	 * the zoom of the {@code DisplayPaint} for the players to see
	 */
	private double displayZoom;
	
	/**
	 * the position of the previous click inside the {@code drawingLayer}
	 */
	private Point lastP;
	
	/**
	 * the current position of the mouse inside of (@code drawingLayer}
	 */
	private Point mousePos;
	
	/**
	 * true if the pen is drawing, false if you ignore it
	 */
	private boolean canDraw;
	
	/**
	 * true if an image is being loaded and the {@code drawingLayer} should not be displayed
	 */
	private boolean loading;
	
	/**
	 * true if the mouse has yet to be released
	 */
	private boolean dragging;
	
	/**
	 * the state of the vertical or horizontal lock on the pen
	 */
	private Direction styleLock;
	
	/**
	 * the state of the pen used for touch pads
	 */
	private DrawMode drawMode;
	
	/**
	 * the position of the previous click that changed the window position
	 */
	private Point lastWindowClick;
	
	/**
	 * the position of the actual window on the {@code drawingLayer}
	 */
	private Point windowPos;
	
	/**
	 * the position of the start of a click
	 */
	private Point startOfClick;
	
	/**
	 * the {@code JButton} that causes the mask to be displayed to the players.
	 * It only becomes enabled when the mask has been changed
	 */
	private JButton updateButton;
	
	/**
	 * creates an instance of {@code DrawPanel}
	 */
	public DrawPanel() {
		setDoubleBuffered(false);
		setRadius(25);
		mousePos = new Point(-100, -100);
		displayZoom = 1;
		windowPos = new Point(0, 0);
		lastWindowClick = new Point(0, 0);
		penType = Pen.CIRCLE;
		styleLock = Direction.NONE;
		drawMode = DrawMode.ANY;
		updateButton = Settings.createButton("Update Screen");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (hasImage()) {
					try {
						Main.DISPLAY_PAINT.setMask(getMask());
					} catch (OutOfMemoryError error) {
						Settings.showError("Cannot update Image, file is probably large", error);
					}
					updateButton.setEnabled(false);
					updateButton.setBackground(Settings.CONTROL_BACKGROUND);
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (Settings.PAINT_IMAGE != null) {
					lastP = toDrawingPoint(e.getPoint());
					switch (drawMode) {
					case ANY:
						if (e.getButton() == MouseEvent.BUTTON2) {
							setWindowPos(lastP);
							Main.DISPLAY_PAINT.setWindowPos(getWindowPos());
							canDraw = false;
						}
						else {
							if (e.getButton() == MouseEvent.BUTTON1) {
								g2.setPaint(Settings.CLEAR);
								canDraw = true;
							}
							else if (e.getButton() == MouseEvent.BUTTON3) {
								g2.setPaint(Settings.OPAQUE);
								canDraw = true;
							}
							startOfClick = e.getPoint();
							dragging = true;
							addPoint(lastP);
						}
						break;
					case INVISIBLE:
					case VISIBLE:
						startOfClick = e.getPoint();
						dragging = true;
						addPoint(lastP);
						break;
					case WINDOW:
						setWindowPos(lastP);
						Main.DISPLAY_PAINT.setWindowPos(getWindowPos());
						break;
					}
					repaint();
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (Settings.PAINT_IMAGE != null && canDraw) {
					switch (penType) {
					case RECT:
						Point p = toDrawingPoint(e.getPoint());
						Point p2 = toDrawingPoint(startOfClick);
						g2.fillRect(
								Math.min(p.x, p2.x),
								Math.min(p.y, p2.y),
								Math.abs(p.x - p2.x),
								Math.abs(p.y - p2.y));
						break;
					default:
						break;
					}
				}
				dragging = false;
				repaint();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (Settings.PAINT_IMAGE != null) {
					if (canDraw) {
						addPoint(toDrawingPoint(e.getPoint()));
					}
					else {
						setWindowPos(toDrawingPoint(e.getPoint()));
						Main.DISPLAY_PAINT.setWindowPos(getWindowPos());
					}
					mousePos = e.getPoint();
					repaint();
				}
			}
			public void mouseMoved(MouseEvent e) {
				mousePos = e.getPoint();
				repaint();
			}
		});
		addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				controlSize = getSize();
				repaint();
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		repaint();
	}
	
	/**
	 * sets the {@code displayZoom}, moves the display window and changes the window position based on edges
	 * @param zoom the number of pixels in the image per the pixel on the output display.<br>
	 * - a higher number will zoom out<br>
	 * - a lower number will zoom in
	 */
	public void setZoom(double zoom) {
		displayZoom = zoom;
		setWindowPos(lastWindowClick);
		Main.DISPLAY_PAINT.setWindow(zoom, getWindowPos());
		repaint();
	}
	
	/**
	 * sets the zoom and position of the players view
	 * @param zoom the number of pixels in the image per pixel on the output display
	 * @param p the point of the click (middle point of window)
	 */
	public void setWindow(double zoom, Point p) {
		displayZoom = zoom;
		setWindowPos(p);
		Main.DISPLAY_PAINT.setWindow(zoom, getWindowPos());
		repaint();
	}
	
	/**
	 * creates the {@code drawingLayer} based on {@code Settings.PAINT_IMAGE}.
	 * Also sets {@code g2} and clears everything for a new image.
	 */
	public synchronized void setImage() {
		if (Settings.PAINT_IMAGE != null) {
			
			File maskFile = Settings.fileToMaskFile(Settings.PAINT_FOLDER);
			
			if (maskFile.exists() && maskFile.lastModified() > Settings.PAINT_FOLDER.lastModified()) {
				try {
					drawingLayer = ImageIO.read(maskFile);
					g2 = (Graphics2D) drawingLayer.getGraphics();
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.6f));
					g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				} catch (IOException e) {
					Settings.showError("Cannot load Mask, file is probably too large", e);
				}
			}
			else {
				drawingLayer = new BufferedImage(
						Settings.PAINT_IMAGE.getWidth() / Settings.PIXELS_PER_MASK,
						Settings.PAINT_IMAGE.getHeight() / Settings.PIXELS_PER_MASK,
						BufferedImage.TYPE_INT_ARGB);
				g2 = (Graphics2D) drawingLayer.getGraphics();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.6f));
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				hideAll();
			}
		}
	}

	/**
	 * changes the size of the user's pen
	 * @param value the radius of the pen
	 */
	public void setRadius(int value) {
		radius = value;
		diameter = radius * 2;
		repaint();
	}
	
	/**
	 * returns the {@code updateButton} for use in {@code ControlPaint}
	 * @return
	 */
	public JButton getUpdateButton() {
		return updateButton;
	}
	
	/**
	 * sets the paint image to null, and removes any settings for it
	 */
	public void resetImage() {
		Settings.PAINT_IMAGE = null;
		Settings.PAINT_CONTROL_IMAGE = null;
		g2 = null;
		drawingLayer = null;
		loading = false;
	}
	
	/**
	 * toggles the pen from Circle to Square or vice versa
	 */
	public void togglePen() {
		penType = Pen.values()[(penType.ordinal() + 1) % Pen.values().length];
		repaint();
	}

	/**
	 * toggles the style of vertical or horizontal lock
	 */
	public void toggleStyle() {
		styleLock = Direction.values()[(styleLock.ordinal() + 1) % Direction.values().length];
	}
	
	/**
	 * toggles the draw mode for touch pads
	 */
	public void toggleDrawMode() {
		drawMode = DrawMode.values()[(drawMode.ordinal() + 1) % DrawMode.values().length];
		if (g2 != null) {
			switch (drawMode) {
			case ANY:
				break;
			case VISIBLE:
				g2.setPaint(Settings.CLEAR);
				canDraw = true;
				break;
			case INVISIBLE:
				g2.setPaint(Settings.OPAQUE);
				canDraw = true;
				break;
			case WINDOW:
				canDraw = false;
				break;
			}
		}
	}
	
	/**
	 * sets if the {@code DrawPanel} is loading right now or not
	 * @param b <br>
	 * - true if loading<br>
	 * - false if done loading
	 */
	public void setImageLoading(boolean b) {
		loading = b;
		repaint();
	}
	
	/**
	 * gets the {@code penType} either Circle, or Square
	 * @return
	 */
	public int getPen() {
		return penType.ordinal();
	}
	
	/**
	 * gets the {@code styleLock} either vertical, horizontal, or none
	 * @return
	 */
	public int getStyle() {
		return styleLock.ordinal();
	}
	
	/**
	 * gets the {@code drawMode} for touch pad compatibility
	 * @return
	 */
	public int getDrawMode() {
		return drawMode.ordinal();
	}
	
	/**
	 * gets a black and transparent mask from the {@code drawingLayer}
	 * @return {@code BufferedImage} of type {@code TYPE_INT_ARGB} 
	 * with every pixel either being Color.BLACK or completely transparent.
	 * @throws OutOfMemoryError if the JVM runs out of memory
	 */
	public BufferedImage getMask() throws OutOfMemoryError {
		BufferedImage mask = new BufferedImage(
				drawingLayer.getWidth(),
				drawingLayer.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		
		for (int i = 0; i < drawingLayer.getWidth(); i++) {
			for (int j = 0; j < drawingLayer.getHeight(); j++) {
				int dl = drawingLayer.getRGB(i, j);
				if (dl == -1721434268) { // CLEAR
					mask.setRGB(i, j, 0);
				}
				else if (dl == -1711315868) { // OPAQUE
					mask.setRGB(i, j, -16777215);
				}
			}
		}
		return mask;
	}
	
	/**
	 * gets the window position based on {@code displayZoom}
	 * @return the position of the window by the top left corner of it in the {@code drawingLayer}
	 */
	public Point getWindowPos() {
		return new Point((int)(windowPos.x / displayZoom), (int) (windowPos.y / displayZoom));
	}
	
	/**
	 * tells if the {@code DrawPanel} currently has an image
	 * @return - true if there is an image<br>
	 * - false if there is not an image
	 */
	public boolean hasImage() {
		return drawingLayer != null;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if (loading) {
			g2d.drawString("Loading...", controlSize.width / 2, controlSize.height / 2);
		}
		else if (Settings.PAINT_CONTROL_IMAGE != null) {
			g2d.drawImage(Settings.PAINT_CONTROL_IMAGE, 0, 0, controlSize.width, controlSize.height, null);
			g2d.drawImage(drawingLayer, 0, 0, controlSize.width, controlSize.height, null);
			g2d.setColor(Settings.PINK);
			switch (penType) {
			case CIRCLE:
				g2d.drawOval(mousePos.x - radius, mousePos.y - radius, diameter, diameter);
				break;
			case SQUARE:
				g2d.drawRect(mousePos.x - radius, mousePos.y - radius, diameter, diameter);
				break;
			case RECT:
				if (dragging) {
					g2d.drawRect(
							Math.min(mousePos.x, startOfClick.x),
							Math.min(mousePos.y, startOfClick.y),
							Math.abs(mousePos.x - startOfClick.x),
							Math.abs(mousePos.y - startOfClick.y));
				}
				g2d.drawLine(mousePos.x, mousePos.y - 10, mousePos.x, mousePos.y + 10);
				g2d.drawLine(mousePos.x - 10, mousePos.y, mousePos.x + 10, mousePos.y);
				break;
			}
			
			drawPlayerView(g2d);
		}
		else if (controlSize != null) {
			g2d.drawString("No image loaded", controlSize.width / 2, controlSize.height / 2);
		}
	}
	
	/**
	 * Draws a rectangle on the area of a {@code DrawPanel} to tell what the players can see
	 * @param g2d the graphics component to draw to
	 */
	private void drawPlayerView(Graphics2D g2d) {
		int w = (int) (Settings.DISPLAY_SIZE.width * displayZoom * controlSize.width / Settings.PAINT_IMAGE.getWidth());
		int h = (int) (Settings.DISPLAY_SIZE.height * displayZoom * controlSize.height / Settings.PAINT_IMAGE.getHeight());
		int x, y;
		
		if (w > controlSize.width) {
			x = -(w - controlSize.width) / 2;
		}
		else {
			x = windowPos.x * controlSize.width / Settings.PAINT_IMAGE.getWidth();
		}
		if (h > controlSize.height) {
			y = -(h - controlSize.height) / 2;
		}
		else {
			y = windowPos.y * controlSize.height / Settings.PAINT_IMAGE.getHeight();
		}
		
		g2d.drawRect(x, y, w, h);
		g2d.drawLine(x, y, x + w, y + h);
		g2d.drawLine(x + w, y, x, y + h);
		g2d.setColor(Settings.PINK_CLEAR);
		g2d.fillRect(x, y, w, h);
	}

	/**
	 * converts a point on the {@code drawingLayer} to a point on the actual image
	 * @param p a point based on the placement in {@code drawingLayer}
	 * @return a point based on the placement in {@code Settings.PAINT_IMAGE}
	 */
	private Point toDrawingPoint(Point p) {
		return new Point(
				p.x * drawingLayer.getWidth() / controlSize.width,
				p.y * drawingLayer.getHeight() / controlSize.height);
	}
	
	/**
	 * sets the position of the window on {@code drawingLayer}
	 * @param p the point of the click (middle point of window)
	 */
	private void setWindowPos(Point p) {
		lastWindowClick = p;
		
		windowPos.x = (int) ((p.x * Settings.PIXELS_PER_MASK) - (Settings.DISPLAY_SIZE.width * displayZoom) / 2);
		windowPos.y = (int) ((p.y * Settings.PIXELS_PER_MASK) - (Settings.DISPLAY_SIZE.height * displayZoom) / 2);
		
		if (Settings.PAINT_IMAGE != null) {
			//if (windowPos.x > (Settings.PAINT_IMAGE.getWidth() - Settings.DISPLAY_SIZE.width * displayZoom)) {
			if (windowPos.x > (Settings.PAINT_IMAGE.getWidth() )) {
				windowPos.x = (int) (Settings.PAINT_IMAGE.getWidth() - Settings.DISPLAY_SIZE.width * displayZoom);
			}
			//if (windowPos.x < 0) {
			if (windowPos.x < (0 - (Settings.DISPLAY_SIZE.width * displayZoom) ) ) {
				windowPos.x = 0;
			}
			//if (windowPos.y > (Settings.PAINT_IMAGE.getHeight() - Settings.DISPLAY_SIZE.height * displayZoom)) {
			if (windowPos.y > (Settings.PAINT_IMAGE.getHeight() )) {
				windowPos.y = (int) (Settings.PAINT_IMAGE.getHeight() - Settings.DISPLAY_SIZE.height * displayZoom);
			}
			//if (windowPos.y < 0) {
			if (windowPos.y < (0 - (Settings.DISPLAY_SIZE.height * displayZoom) )) {
				windowPos.y = 0;
			}
		}
	}
	
	/**
	 * uses the pen to draw onto the {@code drawingLayer}
	 * @param newP a point based on the placement on {@code Settings.PAINT_IMAGE}<br>
	 * use {@code toDrawingPoint} to convert to the correct point
	 */
	private void addPoint(Point newP) {
		if (g2 != null) {
			switch (styleLock) {
				case HORIZONTAL:
					newP.y = lastP.y;
					break;
				case VERTICAL:
					newP.x = lastP.x;
					break;
				default:
					break;
			}
			final double widthMod = (double)drawingLayer.getWidth() / controlSize.width;
			final double heightMod = (double)drawingLayer.getHeight() / controlSize.height;
			final double rwidth = radius * widthMod;
			final double rheight = radius * heightMod;
			final int dwidth = (int) (diameter * widthMod);
			final int dheight = (int) (diameter * heightMod);
			switch (penType) {
			case CIRCLE:
				g2.fillPolygon(getCirclePolygon(newP, lastP, rwidth, rheight));
				g2.fillOval(
						newP.x - (int)rwidth,
						newP.y - (int)rheight,
						dwidth,
						dheight);
				break;
			case SQUARE:
				g2.fillPolygon(getSquarePolygon(newP, lastP, (int)rwidth, (int)rheight));
				g2.fillRect(
						newP.x - (int)rwidth,
						newP.y - (int)rheight,
						dwidth,
						dheight);
				break;
			case RECT:
				break;
			}
			lastP = newP;
			updateButton.setEnabled(true);
			updateButton.setBackground(Settings.ACTIVE);
		}
	}
	
	/**
	 * returns a polygon for a rectangle connecting two circles
	 * @param newP the center of one circle
	 * @param oldP the center of another circle<br>
	 * points based on the placement on {@code Settings.PAINT_IMAGE}<br>
	 * use {@code toDrawingPoint} to convert to the correct point
	 * @param rwidth the radius of the circle in the x direction
	 * @param rheight the radius of the circle in the y direction
	 * @return a {@code Polygon} with 4 points
	 */
	private Polygon getCirclePolygon(Point newP, Point oldP, double rwidth, double rheight) {
		final double angle = -Math.atan2(newP.getY() - oldP.getY(), newP.getX() - oldP.getX());
		final double anglePos = angle + Math.PI / 2;
		final double angleNeg = angle - Math.PI / 2;
		final int cosP = (int) (Math.cos(anglePos) * rwidth);
		final int cosN = (int) (Math.cos(angleNeg) * rwidth);
		final int sinP = (int) (Math.sin(anglePos) * rheight);
		final int sinN = (int) (Math.sin(angleNeg) * rheight);
		return new Polygon(
				new int[] {
						newP.x + cosP,
						newP.x + cosN,
						oldP.x + cosN,
						oldP.x + cosP},
				new int[] {
						newP.y - sinP,
						newP.y - sinN,
						oldP.y - sinN,
						oldP.y - sinP}, 4);
	}

	/**
	 * returns a polygon for a rectangle connecting two squares
	 * @param newP the center of one square
	 * @param oldP the center of another square<br>
	 * points based on the placement on {@code Settings.PAINT_IMAGE}<br>
	 * use {@code toDrawingPoint} to convert to the correct point
	 * @param rwidth the radius of the square in the x direction
	 * @param rheight the radius of the square in the y direction
	 * @return a {@code Polygon} with 4 points
	 */
	private Polygon getSquarePolygon(Point newP, Point oldP, int rwidth, int rheight) {
		if ((newP.x > oldP.x && newP.y > oldP.y) || (newP.x < oldP.x && newP.y < oldP.y)) {
			rheight *= -1;
		}
		return new Polygon(
				new int[] {
						newP.x - rwidth,
						newP.x + rwidth,
						oldP.x + rwidth,
						oldP.x - rwidth},
				new int[] {
						newP.y - rheight,
						newP.y + rheight,
						oldP.y + rheight,
						oldP.y - rheight}, 4);
	}
	
	/**
	 * fills all of the {@code drawingLayer} with a color
	 * @param c the color to paint with
	 */
	private void fillAll(Color c) {
		if (g2 != null) {
			g2.setPaint(c);
			g2.fillRect(0, 0, drawingLayer.getWidth(), drawingLayer.getHeight());
			repaint();
			updateButton.setEnabled(true);
			updateButton.setBackground(Settings.ACTIVE);
		}
	}
	
	/**
	 * sets all of {@code drawingLayer} to Opaque and unseen to players
	 */
	public void hideAll() {
		fillAll(Settings.OPAQUE);
	}
	
	/**
	 *  sets all of {@code drawingLayer} to Clear and seen to players
	 */
	public void showAll() {
		fillAll(Settings.CLEAR);
	}
	
	/**
	 * saves the mask to file
	 */
	public void saveMask() {
		File f = Settings.fileToMaskFile(Settings.PAINT_FOLDER);
		if (f != null) {
			try {
				ImageIO.write(drawingLayer, "png", f);
				
				File dataFile = new File(Settings.DATA_FOLDER + File.separator + "Paint" + File.separator + f.getName() + ".data");
				BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));
				writer.write(String.format("%f %d %d", displayZoom, lastWindowClick.x, lastWindowClick.y));
				writer.close();
				
			} catch (IOException e) {
				Settings.showError("Cannot save Mask", e);
			}
		}
	}
}