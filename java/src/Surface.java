import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import javax.swing.Timer;

public class Surface extends JFrame {

	/**
	 * 
	 */
	// static variable single_instance of type Singleton 
    private static Surface single_instance = null; 
    
 // static method to create instance of Singleton class 
    public static Surface getInstance() 
    { 
        if (single_instance == null) {
            single_instance = new Surface(); 
        }
 
        return single_instance; 
    }
    
	public static double SURFACE_WIDTH =  800; // 30 m * 100 / 2.5 cm
	public static double SURFACE_HEIGHT = 255; // 20 m * 100 / 2.5 cm
    
	private static final long serialVersionUID = 5848795671507623896L;
	private Bereshit mySpaceship;
	private ArrayList <Point> points;
	private JLabel timeLabel;
	private JLabel surfaceLabel;
	private BufferedImage moon = null;
	public static final String MOON_IMAGE_NAME = "moon_surface2.png";
	private BufferedImage spaceship = null;
	public static final String SPACESHIP_IMAGE_NAME = "bereshit.png";
	
	private MyKeyListener myKeyListener;
	
	// Variables declaration - do not modify
    private MyPanel myPanel;
    /**
	 * @return the myPanel
	 */
	public MyPanel getMyPanel() {
		return myPanel;
	}

	
	public Surface() {
        initComponents();
        setFocusable(true);
    }

	public BufferedImage loadMoonImage() throws IOException {
		File file = new File(MOON_IMAGE_NAME);
        BufferedImage image = ImageIO.read(file);
        return image;
	}
	
	public BufferedImage loadSpaceshipImage() throws IOException {
		File file = new File(SPACESHIP_IMAGE_NAME);
        BufferedImage image = ImageIO.read(file);
        return image;
	}
	
    private void initComponents() {
        
    	try {
    		moon = loadMoonImage();
    		SURFACE_WIDTH = moon.getWidth();
			SURFACE_HEIGHT = moon.getHeight();
			
			spaceship = loadSpaceshipImage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
        // we want a custom panel, not a generic JPanel!
        myPanel = new MyPanel();

        myPanel.setBackground(Color.WHITE);
        myPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        myKeyListener = new MyKeyListener();
        this.addKeyListener(myKeyListener);
        
        mySpaceship = new Bereshit();
        points = mySpaceship.land();
        
        timeLabel = new JLabel();
        myPanel.add(timeLabel);
        
        surfaceLabel = new JLabel();
        myPanel.add(surfaceLabel);
        
        // add the component to the frame to see it!
        this.setContentPane(myPanel);
        // be nice to testers..
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        
    }
    

    public void KeyBindings() {
        myPanel.revalidate();
		myPanel.repaint();
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
	
	
	class MyPanel extends JPanel {

		private int x = 0;
	    private int y = 0;
	    private int index = 0;
	    
		/**
		 * 
		 */
		private static final long serialVersionUID = 4848795671507623896L;

		MyPanel() {
            // set a preferred size for the custom panel.
            setPreferredSize(new Dimension((int)SURFACE_WIDTH, (int)SURFACE_HEIGHT));
        }
		
		public Dimension getPreferredSize() {
			return new Dimension((int)SURFACE_WIDTH, (int)SURFACE_HEIGHT);
		}
		
		
		public void moveSpaceship() {
            if (index < points.size()) {
            	Point p = points.get(index);
            	this.x = p.x;
            	this.y = Math.abs(25000 - p.y);
            	System.out.println(x + ":" + y);
            	index++;
            }
            //repaint(0,x/500, y/50, 5, 5);
            repaint();
        }
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (moon != null) {
				Graphics2D g2 = (Graphics2D) g;
				g2.drawImage(moon, 0, 0, this);
				g2.finalize();
			}
			
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.RED);
			spaceship.setRGB(0, 0, Color.RED.getRGB());
			//g2d.fillRect(x/500, y/50, 5, 5);
			g2d.drawImage(spaceship, x/500, y/50 - 30, null);
			
			Graphics2D g2l1 = (Graphics2D) g;
			g2l1.setColor(Color.BLACK);
			g2l1.drawLine(0, 25000/50+5, 800000/500, 25000/50+5);
			
			Graphics2D g2l2 = (Graphics2D) g;
			g2l2.setColor(Color.BLACK);
			g2l2.drawLine(2, 2, 2, 25000/50+5);
			
			for (int i = 1; i <= 8; i++) {
				Graphics2D g2l3 = (Graphics2D) g;
				g2l3.setColor(Color.BLACK);
				int x = 800000/500/8 * i;
				g2l3.drawLine(x, 25000/50+10, x,25000/50);
			}
			
			for (int i = 1; i <= 25; i++) {
				Graphics2D g2l4 = (Graphics2D) g;
				g2l4.setColor(Color.BLACK);
				int y = 25000/50/25 * i;
				g2l4.drawLine(-5, y+5, 5,y+5);
			}
			
		}
    }
    
	class MyKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        	//System.out.println(e.getKeyChar());
        }

        @Override
        public void keyPressed(KeyEvent e) {
        	int keyCode = e.getKeyCode();

        	// LAND
        	if(keyCode == KeyEvent.VK_L) {
        		ActionListener timerAction = new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                    	myPanel.moveSpaceship();
                    }
                };
                Timer timer = new Timer(15, timerAction);
                timer.setRepeats(true);
                timer.start();
        	}
        	// RESET
        	if(keyCode == KeyEvent.VK_0) {
        		
        		return;
        	}
        	/*
        	// KEYS
        	if(keyCode == KeyEvent.VK_SPACE) {
        		if (flyThread != null) {
        			flyThread.interrupt();
        		}
        		Utils.log(Simulator.Keys);
        		return;
        	}
        	
        	// LAND
        	if(keyCode == KeyEvent.VK_L) {
        		if (flyThread != null) {
        			flyThread.interrupt();
        			//myDrone.land();
        			return;
        		}
        	}
        	
			
        	// SLEEP THREAD
        	if(keyCode == KeyEvent.VK_J) {
        		sleepThread += 2;
        		if (sleepThread > 1000) {
        			sleepThread = 300;
        		}
        		return;
        	}
        	
        	if(keyCode == KeyEvent.VK_K) {
        		sleepThread -= 2;
        		if (sleepThread <= 0) {
        			sleepThread = 0;
        		}
        		return;
        	}*/
        	
        }

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
}
