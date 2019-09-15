/**
 * @author Allen Ho
 */

//creates the framework
import javax.swing.JComponent;
import javax.swing.JFrame;

//graphics
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.RoundRectangle2D;

//listeners
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//other important stuff
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TetrisNewMechanics {
	//main method
	public static void main(String[] args) {
		//create the frame for the program
		JFrame frame = new tetrisJFrame();
		//set frame size
		frame.setSize(460,460);
		//set frame title
		frame.setTitle("Tetris");
		//make sure that the frame closes
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//make the frame size unable to be changed
		frame.setResizable(false);
		//make sure that the frame is visible
		frame.setVisible(true);
	}
	//initialize variables
	//for the JComponent and graphics
	static tetrisJComponent component = new tetrisJComponent();
	//initialize the timer
	static Timer timer = new Timer();
	static TimerTask task = new timerHelper();
	static boolean timerOn = false;
	//initialize the randomness
	public static Random generator = new Random(System.currentTimeMillis());
	//for the key inputs
	//array for the keys
	//by arrow key definition - 37
	//go figure what I mean by this
	static movementHelper[] keys = new movementHelper[] {new movementHelper(37), new movementHelper(38), new movementHelper(39), new movementHelper(40)};
	//JFrame class: implement MouseListener and KeyListener
	static class tetrisJFrame extends JFrame implements MouseListener, KeyListener{
		//create default location for JFrame
		private static final long serialVersionUID = 1L;
		//initialize tetrisJFrame
		/** 
		 * creates the JFrame
		 */
		public tetrisJFrame() {
			addKeyListener(this);
			addMouseListener(this);
			this.add(component);
		}
		/**
		 * Runs if the key is pressed
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			component.keyPressed(e.getKeyCode());
		}
		/**
		 * Runs if the key is released
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			component.keyReleased(e.getKeyCode());
		}
		/** 
		 * Runs if the key is typed
		 */
		@Override
		public void keyTyped(KeyEvent e) {
			component.keyTyped(e.getKeyChar());
		}
		/**
		 * Runs if the mouse is clicked
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			component.mouseClicked(e.getX(), e.getY());
		}
		/**
		 * Runs if the mouse enters the JFrame
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
			component.mouseEntered(e.getX(), e.getY());
		}
		/**
		 * Runs if the mouse exits the JFrame
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			component.mouseExited(e.getX(), e.getY());
		}
		/**
		 * Runs if the mouse is pressed
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			component.mousePressed(e.getX(), e.getY());
		}
		/** 
		 * Runs if the mouse is released
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			component.mouseReleased(e.getX(), e.getY());
		}
	}
	//create graphics and changes them
	static class tetrisJComponent extends JComponent {
		//stores the JComponent into a place
		private static final long serialVersionUID = 1L;
		//initialize the color array for the board
		public Color[][] colors = new Color[10][20];
		//initialize the screen number
		public int screen = 0;
		//initialize the default score, lines cleared and level
		public int score = 0, lines = 0, level = 1;
		//create a variable to start the timer
		private boolean startTimer = true;
		//create a variable to see if the game is playing
		public boolean playing = false;
		//see if the player lost
		public boolean lost = false;
		//create the starting level for the game
		public int startingLevel = 1;
		public int beforeLevel;
		//default colors -- will change later but looks great
		/**
		 * initializes the class
		 */
		public tetrisJComponent() {
			//creates the default colors
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 20; j++) {
					colors[i][j] = new Color(255,255,255);
				}
			}
		}
		/**
		 * draws the board
		 * @param graphics the graphics used to draw the JFrame
		 */
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			FontMetrics fontMetrics = g2.getFontMetrics();
			//game screen
			if(screen == 0) {
				g2.setColor(new Color(0,153,255));
				g2.setFont(new Font("TimesRoman",Font.BOLD,100));
				g2.drawString("TETRIS", 40, 100);
				g2.fill(new RoundRectangle2D.Double(100,200,250,100,15,15));
				g2.setColor(new Color(255,255,255));
				g2.setFont(new Font("TimesRoman",Font.BOLD,70));
				g2.drawString("PLAY", 130, 270);
			}
			if(screen == 1) {
				g2.setColor(new Color(0,153,255));
				g2.setFont(new Font("TimesRoman",Font.BOLD,100));
				g2.drawString("LEVEL", 60, 100);
				g2.setStroke(new BasicStroke(10));
				g2.setColor(new Color(0,102,204));
				g2.draw(new RoundRectangle2D.Double(300,200,100,100,5,5));
				g2.drawString(">", 320, 285);
				g2.draw(new RoundRectangle2D.Double(50,200,100,100,5,5));
				g2.drawString("<", 70, 285);
				g2.drawString(String.valueOf(level), 200, 285);
				g2.setColor(new Color(0,153,255));
				g2.draw(new RoundRectangle2D.Double(150,340,150,60,5,5));
				g2.setFont(new Font("TimesRoman",Font.BOLD,50));
				g2.setColor(new Color(0,102,204));
				g2.drawString("PLAY", 155, 385);
			}
			if(screen == 2) {
				//game screen blocks
				if(startTimer && !playing) {
					timer.schedule(task, 0, (long) (500*Math.pow(0.9,level) + 10)); 
					startTimer = false;
					timerOn = true;
					//timer.purge();
					//timer.cancel();
				}
				for(int i = 0; i < 10; i++) {
					for(int j = 0; j < 20; j++) {
						g2.setColor(colors[i][j]);
						g2.fill(new RoundRectangle2D.Double(10+20*i,10+20*j,20,20,5,5));
						g2.setColor(new Color(0,0,0));
						g2.draw(new RoundRectangle2D.Double(10+20*i,10+20*j,20,20,5,5));
					}
				}
				//drawing the next object rectangle
				g2.setColor(new Color(0,0,0));
				g2.draw(new Rectangle(230,10,200,100));
				switch(object2) {
				//line
				case 1:
					g2.setColor(new Color(102,255,255));
					g2.fill(new RoundRectangle2D.Double(250,40,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(290,40,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(330,40,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(370,40,40,40,5,5));
					g2.setColor(new Color(0,0,0));
					g2.draw(new RoundRectangle2D.Double(250,40,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(290,40,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(330,40,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(370,40,40,40,5,5));
				break;
				//backwards l
				case 2:
					g2.setColor(new Color(0,0,255));
					g2.fill(new RoundRectangle2D.Double(270,20,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(270,60,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(310,60,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(350,60,40,40,5,5));
					g2.setColor(new Color(0,0,0));
					g2.draw(new RoundRectangle2D.Double(270,20,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(270,60,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(310,60,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(350,60,40,40,5,5));
				break;
				//l shaped
				case 3:
					g2.setColor(new Color(255,153,51));
					g2.fill(new RoundRectangle2D.Double(270,60,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(310,60,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(350,20,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(350,60,40,40,5,5));
					g2.setColor(new Color(0,0,0));
					g2.draw(new RoundRectangle2D.Double(270,60,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(310,60,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(350,20,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(350,60,40,40,5,5));
				break;
				//square
				case 4:
					g2.setColor(new Color(255,255,51));
					g2.fill(new RoundRectangle2D.Double(290,20,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(290,60,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(330,20,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(330,60,40,40,5,5));
					g2.setColor(new Color(0,0,0));
					g2.draw(new RoundRectangle2D.Double(290,20,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(290,60,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(330,20,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(330,60,40,40,5,5));
				break;
				//s shaped
				case 5:
					g2.setColor(new Color(204,255,51));
					g2.fill(new RoundRectangle2D.Double(270,60,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(310,20,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(310,60,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(350,20,40,40,5,5));
					g2.setColor(new Color(0,0,0));
					g2.draw(new RoundRectangle2D.Double(270,60,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(310,20,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(310,60,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(350,20,40,40,5,5));
				break;
				//controller shaped
				case 6:
					g2.setColor(new Color(204,51,255));
					g2.fill(new RoundRectangle2D.Double(270,60,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(310,20,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(310,60,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(350,60,40,40,5,5));
					g2.setColor(new Color(0,0,0));
					g2.draw(new RoundRectangle2D.Double(270,60,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(310,20,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(310,60,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(350,60,40,40,5,5));
				break;
				//backwards s
				case 7:
					g2.setColor(new Color(255,0,0));
					g2.fill(new RoundRectangle2D.Double(270,20,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(310,20,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(310,60,40,40,5,5));
					g2.fill(new RoundRectangle2D.Double(350,60,40,40,5,5));
					g2.setColor(new Color(0,0,0));
					g2.draw(new RoundRectangle2D.Double(270,20,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(310,20,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(310,60,40,40,5,5));
					g2.draw(new RoundRectangle2D.Double(350,60,40,40,5,5));
				break;
				}
				//drawing the level, number of lines and the score
				g2.setColor(new Color(0,0,0));
				g2.setFont(new Font("TimesRoman",Font.PLAIN, 30));
				fontMetrics = g2.getFontMetrics();
				g2.drawString("Level", 230, 150);
				g2.drawString(String.valueOf(level), 430 - fontMetrics.stringWidth(String.valueOf(level)), 150);
				g2.drawString("Lines", 230, 200);
				g2.drawString(String.valueOf(lines), 430 - fontMetrics.stringWidth(String.valueOf(lines)), 200);
				g2.drawString("Score", 230, 250);
				g2.drawString(String.valueOf(score), 430 - fontMetrics.stringWidth(String.valueOf(score)), 250);
				//mouse from (287,300) to (389,353)
				g2.setColor(new Color(179,255,255));
				g2.fill(new RoundRectangle2D.Double(280,270,100,50,5,5));
				g2.setColor(Color.BLACK);
				g2.draw(new RoundRectangle2D.Double(280,270,100,50,5,5));
				g2.drawString("Pause",296,305);
				//if the player lost
				if(lost == true) {
					g2.setColor(new Color(0,0,0,170));
					g2.fill(new RoundRectangle2D.Double(50,50,350,350,10,10));
					g2.setColor(new Color(255,255,255));
					g2.setFont(new Font("TimesRoman",Font.PLAIN,30));
					g2.drawString("Oh no!", 180, 100);
					g2.setFont(new Font("TimesRoman",Font.PLAIN,15));
					g2.drawString("You lost!", 50, 150);
					g2.drawString("Press the button to restart.", 50, 165);
					g2.drawString("Score: " + score, 50, 180);
					g2.drawString("Lines: " + lines, 50, 195);
					g2.drawString("Level: " + level, 50, 210);
					g2.setColor(new Color(179,255,255));
					g2.fill(new RoundRectangle2D.Double(180,240,100,50,5,5));
					g2.setColor(new Color(0,0,0));
					g2.setFont(new Font("TimesRoman",Font.PLAIN,30));
					g2.drawString("Restart", 187, 275);
				}
				//if the game is paused
				if(timerOn == false && lost == false) {
					g2.setColor(new Color(0,0,0,170));
					g2.fill(new RoundRectangle2D.Double(20,20,180,140,10,10));
					g2.setColor(new Color(255,255,255));
					g2.setFont(new Font("TimesRoman",Font.PLAIN,30));
					g2.drawString("Paused",70,60);
					g2.setColor(new Color(0,153,255));
					g2.fill(new RoundRectangle2D.Double(70,80,90,30,5,5));
					g2.setFont(new Font("TimesRoman",Font.PLAIN,20));
					g2.setColor(new Color(0,255,255));
					g2.drawString("Restart", 88, 100);
					g2.setColor(new Color(0,153,255));
					g2.fill(new RoundRectangle2D.Double(70,120,90,30,5,5));
					g2.setFont(new Font("TimesRoman",Font.PLAIN,20));
					g2.setColor(new Color(0,255,255));
					g2.drawString("Unpause", 82, 140);
				}
			}
		}
		/**
		 * if the key is pressed
		 * @param keyCode the key code of the key pressed
		 */
		public void keyPressed(int keyCode) {
			//System.out.println("Key pressed: " + keyCode);
			if(screen == 2 && timerOn == true) {
				if(keyCode == 37 || keyCode == 65) {
					if(!keys[0].timerTask.running) 
						keys[0].start();
					return;
				}
				if(keyCode == 38 || keyCode == 87) {
					if(!keys[1].timerTask.running) {
						keys[1].start();
					}
					return;
				}
				if(keyCode == 39 || keyCode == 68) {
					if(!keys[2].timerTask.running) {
						keys[2].start();
					}
				}
				if(keyCode == 40 || keyCode == 83) {
					if(!keys[3].timerTask.running) {
						keys[3].start();
					}
				}
			}
		}
		/**
		 * if the key is released
		 * @param keyCode the code of the key pressed
		 */
		public void keyReleased(int keyCode) {
			//System.out.println("Key released: " + keyCode);
			if(screen == 2 && timerOn == true) {
				if(keyCode == 37 || keyCode == 65) {
					if(keys[0].timerTask.running) 
						keys[0].stop();
					return;
				}
				if(keyCode == 38 || keyCode == 87) {
					if(keys[1].timerTask.running) {
						keys[1].stop();
					}
					return;
				}
				if(keyCode == 39 || keyCode == 68) {
					if(keys[2].timerTask.running) {
						keys[2].stop();
					}
				}
				if(keyCode == 40 || keyCode == 83) {
					if(keys[3].timerTask.running) {
						keys[3].stop();
					}
				}
			}
		}
		/**
		 * if the key is typed
		 * @param character the character of the key pressed
		 */
		public void keyTyped(char character) {
			//System.out.println("Key typed: " + character);
		}
		/**
		 * if the mouse is clicked
		 * @param x the x coordinate of the mouse
		 * @param y the y coordinate of the mouse
		 */
		public void mouseClicked(int x, int y) {
			if(x > 107 && x < 357 && y > 231 && y < 331 && screen == 0) {
				screen = 1;
				component.repaint();
				return;
			}
			if(x > 48 && x < 157 && y > 223 && y < 329 && screen == 1) {
				if(level > 0) {
					level--;
					component.startingLevel = level;
					component.repaint();
					return;
				}
			}
			if(x > 297 && x < 407 && y > 221 && y < 331 && screen == 1) {
				if(level < 9) {
					level++;
					component.startingLevel = level;
					component.repaint();
					return;
				}
			}
			if(x > 149 && x < 307 && y > 361 && y < 431 && screen == 1) {
				screen = 2;
				component.repaint();
				return;
			}
			if(x > 287 && x < 389 && y > 300 && y < 353 && screen == 2 && lost == false) {
				if(timerOn) {
					timer.cancel();
					timer.purge();
					timerOn = false;
					component.repaint();
				} else {
					timer = new Timer();
					task = new timerHelper();
					timer.schedule(task, 0, (long) (500*Math.pow(0.9,level) + 10)); 
				}
				return;
			}
			if(x > 74 && x < 162 && y > 146 && y < 175 && screen == 2 && lost == false && !timerOn) {
				timer = new Timer();
				task = new timerHelper();
				timer.schedule(task, 0, (long) (500*Math.pow(0.9,level) + 10));
			}
			if(x > 188 && x < 287 && y > 271 && y < 321 && screen == 2 && lost == true) {
				lose();
				return;
			}
			if(x > 72 && x < 163 && y > 107 && y < 135 && timerOn == false && lost == false) {
				lose();
			}
		}
		/**
		 * if the mouse enters the JFrame
		 * @param x the x coordinate of the mouse
		 * @param y the y coordinate of the mouse
		 */
		public void mouseEntered(int x, int y) {
		}
		/**
		 * if the mouse exits the JFrame
		 * @param x the x coordinate of the mouse
		 * @param y the y coordinate of the mouse
		 */
		public void mouseExited(int x, int y) {
			if(timerOn && screen == 2 && !lost) {
				timer.cancel();
				timer.purge();
				timerOn = false;
				component.repaint();
			} 
		}
		/**
		 * if the mouse is pressed
		 * @param x the x coordinate of the mouse
		 * @param y the y coordinate of the mouse
		 */
		public void mousePressed(int x, int y) {
		}
		/**
		 * if the mouse is released
		 * @param x the x coordinate of the mouse
		 * @param y the y coordinate of the mouse
		 */
		public void mouseReleased(int x, int y) {
		}
		//finally! creating the actual game
		//creating the variables for the game
		//checks to see if the piece is movable
		public boolean movable = false;
		/* variable for the animation
		 * 0 means that there is no animation
		 * The animation goes all the way to 5
		 */
		public int animation = 0;
		/* for the two objects
		 * creates the shape and color
		 * 1 means that the shape is a line and is light blue
		 * 2 means that the shape is dark blue and is a reverse l
		 * 3 means that the shape is orange and is a l
		 * 4 means that the shape is yellow and is a square
		 * 5 means that the shape is s shaped and light green
		 * 6 means that the shape is like a controller and is light purple
		 * 7 means that the shape is red and is a reverse s
		 */
		public int object1;
		public int object2 = 4;
		/* for the orientation of the object
		 * 0 is the default orientation
		 * each consecutive orientation is rotated 90 degrees clockwise
		 */
		public int orientation = 0;
		/* for the color of the object
		 * the color of the object is matched with a specific shape
		 */
		public Color objectColor;
		/* for the position of the object
		 * made in the point class, see below
		 */
		public point objectPoint = new point(3,0);
		/* for all of the points of the object
		 * all of the points need to be translated by the objectPoint
		 */
		public ArrayList<point> objectPoints = new ArrayList<point>();
		/* for all of the points of the object, translated
		 * these points are already translated
		 */
		public ArrayList<point> objectPointTranslated = new ArrayList<point>();
		/* the size of the object
		 * in terms of the grid
		 */
		public int size;
		//for row elimination
		public Color[][] rowsEliminated = new Color[10][4];
		//for the specific rows eliminated
		public int[] rowNumber = new int[4];
		//for a very specific game mechanic
		public int slide = 0;
		/**
		 * this function animates the entire thing
		 */
		public void animate() {
			//if an animation is running
			if(animation != 0) {
				if(animation == 1) {
					rowNumber = new int[] {-1,-1,-1,-1};
					//sets the rows
					for(int i = 0; i < colors[0].length; i++) {
						size = 0;
						for(int j = 0; j < colors.length; j++) {
							if(!colors[j][i].equals(new Color(255,255,255))) {
								size++;
							}
						}
						if(size == 10) {
							size = 0;
							while(rowNumber[size] > 0) {
								size++;
							};
							rowNumber[size] = i;
							for(int j = 0; j < rowsEliminated.length; j++) {
								rowsEliminated[j][size] = colors[j][i];
							}
						}
					}
				}
				//if the animation is odd
				if(animation % 2 == 1) {
					//disappear
					for(int i: rowNumber) {
						if(i != -1) {
							for(int j = 0; j < colors.length; j++) {
								colors[j][i] = new Color(255,255,255);
							}
						}
					}
				} else {
					//make it appear
					for(int i = 0; i < rowNumber.length; i++) {
						if(rowNumber[i] != -1) {
							for(int j = 0; j < rowsEliminated.length; j++) {
								colors[j][rowNumber[i]] = rowsEliminated[j][i];
							}
						}
					}
				}
				animation++;
				//if the end of the animation has been reached
				if(animation == 6) {
					size = 0;
					for(int i = 0; i < rowNumber.length; i++) {
						if(rowNumber[i] != -1) {
							for(int j = rowNumber[i] - 1; j > 0; j--) {
								for(int k = 0; k < colors.length; k++) {
									colors[k][j+1] = colors[k][j];
								}
							}
							lines++;
							size++;
						}
					}
					if(size == 1) {
						if(level > 0 && level <= 1) {
							score+=100;
						} else if(level <= 3) {
							score+=200;
						} else if(level <= 5) {
							score+=300;
						} else if(level <= 7) {
							score+=400;
						} else {
							score+=500;
						}
					} else if(size == 2) {
						if(level > 0 && level <= 1) {
							score+=400;
						} else if(level <= 3) {
							score+=800;
						} else if(level <= 5) {
							score+=1200;
						} else if(level <= 7) {
							score+=1600;
						} else {
							score+=2000;
						}
					} else if(size == 3) {
						if(level > 0 && level <= 1) {
							score+=900;
						} else if(level <= 3) {
							score+=1800;
						} else if(level <= 5) {
							score+=2700;
						} else if(level <= 7) {
							score+=3600;
						} else {
							score+=4500;
						}
					} else {
						if(level > 0 && level <= 1) {
							score+=2000;
						} else if(level <= 3) {
							score+=4000;
						} else if(level <= 5) {
							score+=6000;
						} else if(level <= 7) {
							score+=8000;
						} else {
							score+=10000;
						}
					}
					animation = 0;
					beforeLevel = level;
					level = (int) (lines/10.0) + startingLevel;
					if(beforeLevel != level) {
						timer.cancel();
						timer = new Timer();
						task = new timerHelper();
						timer.schedule(task, 0, (long) (500*Math.pow(0.9,level) + 10)); 
					}
				}
				return;
			}
			//if a piece is not falling or not able to be animated
			//creates a new piece at the top
			if(!movable) {
				for(int i = 0; i < colors[0].length; i++) {
					size = 0;
					for(int j = 0; j < colors.length; j++) {
						if(!colors[j][i].equals(new Color(255,255,255))) {
							size++;
						}
					}
					if(size == 10) {
						animation = 1;
						animate();
						return;
					}
				}
				object1 = object2;
				object2 = generator.nextInt(7) + 1;
				orientation = 0;
				objectPointTranslated.clear();
				switch(object1) {
				//line
				case 1:
					objectColor = new Color(102,255,255);
					objectPoint = new point(3,-1);
					objectPoints.clear();
					objectPoints.add(new point(0,1));
					objectPoints.add(new point(1,1));
					objectPoints.add(new point(2,1));
					objectPoints.add(new point(3,1));
					size = 4;
				break;
				//backwards l
				case 2:
					objectColor = new Color(0,0,255);
					objectPoint = new point(3,0);
					objectPoints.clear();
					objectPoints.add(new point(0,0));
					objectPoints.add(new point(0,1));
					objectPoints.add(new point(1,1));
					objectPoints.add(new point(2,1));
					size = 3;
				break;
				//l shaped
				case 3:
					objectColor = new Color(255,153,51);
					objectPoint = new point(3,0);
					objectPoints.clear();
					objectPoints.add(new point(2,0));
					objectPoints.add(new point(0,1));
					objectPoints.add(new point(1,1));
					objectPoints.add(new point(2,1));
					size = 3;
				break;
				//square
				case 4:
					objectColor = new Color(255,255,51);
					objectPoint = new point(4,0);
					objectPoints.clear();
					objectPoints.add(new point(0,0));
					objectPoints.add(new point(0,1));
					objectPoints.add(new point(1,0));
					objectPoints.add(new point(1,1));
					size = 2;
				break;
				//s shape
				case 5:
					objectColor = new Color(204,255,51);
					objectPoint = new point(3,0);
					objectPoints.clear();
					objectPoints.add(new point(0,1));
					objectPoints.add(new point(1,1));
					objectPoints.add(new point(1,0));
					objectPoints.add(new point(2,0));
					size = 3;
				break;
				//controller shaped
				case 6:
					objectColor = new Color(204,51,255);
					objectPoint = new point(3,0);
					objectPoints.clear();
					objectPoints.add(new point(0,1));
					objectPoints.add(new point(1,0));
					objectPoints.add(new point(1,1));
					objectPoints.add(new point(2,1));
					size = 3;
				break;
				//backwards s
				case 7:
					objectColor = new Color(255,0,0);
					objectPoint = new point(3,0);
					objectPoints.clear();
					objectPoints.add(new point(0,0));
					objectPoints.add(new point(1,0));
					objectPoints.add(new point(1,1));
					objectPoints.add(new point(2,1));
					size = 3;
				break;
				}
				setObjectPoints();
				if(intersect(objectPointTranslated)) {
					playing = false;
					timerOn = false;
					timer.cancel();
					lost = true;
					repaint();
					return;
				} else {
					ArrayList<point> i = new ArrayList<point>(objectPointTranslated);
					for(point p: i) {
						colors[p.x][p.y] = objectColor;
					}
				}
				movable = true;
				return;
			}
			//finally, if the piece is movable
			//first remove the piece from the board
			ArrayList<point> i = new ArrayList<point>(objectPointTranslated);
			for(point p: i) {
				if(p.getX() < 0 || p.getX() >= 10 || p.getY() < 0 || p.getY() >= 20) {
					return;
				}
				colors[p.x][p.y] = new Color(255,255,255); 
			}
			//first move the piece down one
			//if the piece is not able to move down one, add one to the slide counter
			//if the slide counter reaches two, the piece is then immovable
			objectPoint.changeY(objectPoint.getY() + 1);
			setObjectPoints();
			if(intersect(objectPointTranslated)) {
				objectPoint.changeY(objectPoint.getY() - 1);
				slide++;
				if(slide == 2) {
					setObjectPoints();
					slide = 0;
					movable = false;
					i = new ArrayList<point>(objectPointTranslated);
					for(point p: i) {
						if(p.getX() < 0 || p.getX() >= 10 || p.getY() < 0 || p.getY() >= 20) {
							return;
						}
						colors[p.x][p.y] = objectColor; 
					}
					return;
				}
				for(point p: objectPointTranslated) {
					if(p.y >= 20) {
						movable = false;
						setObjectPoints();
						for(point Point: objectPointTranslated) {
							if(Point.getX() < 0 || Point.getX() >= 10 || Point.getY() < 0 || Point.getY() >= 20) {
								return;
							}
							colors[Point.x][Point.y] = objectColor; 
						}
						return;
					}
				}
				setObjectPoints();
			} else {
				slide = 0;
			}
			i = new ArrayList<point>(objectPointTranslated);
			for(point p: i) {
				if(p.getX() < 0 || p.getX() >= 10 || p.getY() < 0 || p.getY() >= 20) {
					return;
				}
				colors[p.x][p.y] = objectColor; 
			}
		}
		/**
		 * checks if any of the pieces intersect
		 * @param Point the ArrayList that needs to be analyzed
		 * @return if any of the pieces intersect
		 */
		public boolean intersect(ArrayList<point> Point) {
			ArrayList<point> i = new ArrayList<point>(Point);
			for(point p: i) {
				if(p.getX() < 0 || p.getX() >= 10 || p.getY() < 0 || p.getY() >= 20) {
					return true;
				}
				if(!colors[p.x][p.y].equals(new Color(255,255,255))) {
					return true;
				}
			}
			return false;
		}
		/**
		 * runs if the player lost
		 */
		public void lose() {
			level = startingLevel = 1;
			beforeLevel = 1;
			lines = 0;
			score = 0;
			lost = false;
			playing = false;
			movable = false;
			startTimer = true;
			timerOn = false;
			animation = 0;
			object1 = generator.nextInt(7) + 1;
			object2 = generator.nextInt(7) + 1;
			orientation = 0;
			slide = 0;
			objectPoint = new point(3,0);
			slide = 0;
			for(int i = 0; i < colors.length; i++) {
				for(int j = 0; j < colors[0].length; j++) {
					colors[i][j] = new Color(255,255,255);
				}
			}
			screen = 0;
			timer = new Timer();
			task = new timerHelper();
			repaint();
		}
		/** 
		 * creates the objectPointTranslated class
		 */
		public void setObjectPoints() {
			objectPointTranslated.clear();
			for(int i = 0; i < objectPoints.size(); i++) {
				objectPointTranslated.add(objectPoints.get(i).addToPoint(objectPoint));
			}
		}
	}
	//timer helper
	static class timerHelper extends TimerTask {
		public void run() {
			component.animate();
			component.repaint();
			timerOn = true;
		}
	}
	//it's a class for points!
	static class point {
		//the coordinate of the point
		int x;
		int y;
		/** 
		 * initializes the point class
		 * @param x the x coordinate of the point
		 * @param y the y coordinate of the point
		 */
		public point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		/** 
		 * gets the x value of the point
		 * @return the x coordinate of the point
		 */
		public int getX() {
			return x;
		}
		/**
		 * gets the y value of the point
		 * @return the y coordinate of the point
		 */
		public int getY() {
			return y;
		}
		/**
		 * changes the x value of the point
		 * @param x the x coordinate that will replace the initial value
		 */
		public void changeX(int x) {
			this.x = x;
		}
		/**
		 * changes the y value of the point
		 * @param y the y coordinate that will replace the initial value
		 */
		public void changeY(int y) {
			this.y = y;
		}
		/**
		 * changes the point completely
		 * @param x the x coordinate that will replace the initial value
		 * @param y the y coordinate that will replace the initial value
		 */
		public void changeCoordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}
		/**
		 * rotates the figure by the degree given, 
		 * @param degrees the degree that the figure needs to be rotated
		 * @param spaces the spaces that the object takes up
		 */
		//for this function only
		double originX, originY, tempX, tempY;
		public point rotate(int degrees, int spaces) {
			originX = x - spaces/2.0 + 0.5;
			originY = y - spaces/2.0 + 0.5;
			tempX = originX*Math.cos(degrees*Math.PI/180) - originY*Math.sin(degrees*Math.PI/180);
			tempY = originX*Math.sin(degrees*Math.PI/180) + originY*Math.cos(degrees*Math.PI/180);
			x = (int) Math.round(tempX + spaces/2.0 - 0.5);
			y = (int) Math.round(tempY + spaces/2.0 - 0.5);
			return(new point(x,y));
		}
		/**
		 * tests if the two points are the same
		 * @param p the point needed to be compared to
		 */
		public boolean testSame(point p) {
			if(this.x == p.x && this.y == p.y)
				return true;
			return false;
		}
		public point addToPoint(point p) {
			tempX = x + p.x;
			tempY = y + p.y;
			return new point((int) tempX,(int) tempY);
		}
		/**
		 * for the output (System.out.println)
		 */
		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}
	static class movementHelper implements Comparable<movementHelper>{
		public Timer time = new Timer();
		public int key = 39;
		public task timerTask = new task(key);
		public movementHelper(int key) {
			timerTask = new task(key);
			this.key = key;
		}
		public void start() {
			timerTask.running = true;
			time.schedule(timerTask, 0, (long) (500*Math.pow(0.9,component.level) + 10)); 
		}
		public void stop() {
			timerTask.running = false;
			time.cancel();
			time = new Timer();
			timerTask = new task(key);
		}
		static class task extends TimerTask {
			public boolean running;
			int key;
			public task(int key) {
				this.key = key;
			}
			public void run() {
				running = true;
				if(timerOn && component.movable && component.animation == 0) {
					ArrayList<point> k = new ArrayList<point>(component.objectPointTranslated);
					for(point p: k) {
						if(p.getX() < 0 || p.getX() >= 10 || p.getY() < 0 || p.getY() >= 20) {
							return;
						}
						component.colors[p.x][p.y] = new Color(255,255,255); 
					}
					//check rotation
					if(key == 38 || key == 87) {
						for(int i = 0; i < component.objectPoints.size(); i++) {
							component.objectPoints.set(i, component.objectPoints.get(i).rotate(90, component.size));
						}
						component.setObjectPoints();
						if(component.intersect(component.objectPointTranslated)) {
							for(int i = 0; i < component.objectPoints.size(); i++) {
								component.objectPoints.set(i, component.objectPoints.get(i).rotate(270, component.size));
							}
							component.setObjectPoints();
						}
						k = new ArrayList<point>(component.objectPointTranslated);
						for(point p: k) {
							if(p.x < 0 || p.x > 9 || p.y < 0 || p.y > 19) {
								for(int i = 0; i < component.objectPoints.size(); i++) {
									component.objectPoints.set(i, component.objectPoints.get(i).rotate(270, component.size));
								}
								component.setObjectPoints();
								break;
							}
						}
					}
					//check if the right/left key was pressed
					//first check right
					if(key == 39 || key == 87) {
						component.objectPoint.changeX(component.objectPoint.getX() + 1);
						component.setObjectPoints();
						if(component.intersect(component.objectPointTranslated)) {
							component.objectPoint.changeX(component.objectPoint.getX() - 1);
							component.setObjectPoints();
						}
					}
					//then check left
					if(key == 37 || key == 65) {
						component.objectPoint.changeX(component.objectPoint.getX() - 1);
						component.setObjectPoints();
						if(component.intersect(component.objectPointTranslated)) {
							component.objectPoint.changeX(component.objectPoint.getX() + 1);
							component.setObjectPoints();
						}
					}
					//finally check down
					if(key == 40 || key == 83) {
						component.objectPoint.changeY(component.objectPoint.getY() + 1);
						component.setObjectPoints();
						if(component.intersect(component.objectPointTranslated)) {
							component.objectPoint.changeY(component.objectPoint.getY() - 1);
							component.setObjectPoints();
						} else {
							if(component.level >= 0 && component.level <= 1) {
								component.score+=1;
							} else if(component.level<=3) {
								component.score+=2;
							} else if(component.level<=5) {
								component.score+=3;
							} else if(component.level<=7) {
								component.score+=4;
							} else {
								component.score+=5;
							}
						}
					}
					ArrayList<point> i = new ArrayList<point>(component.objectPointTranslated);
					for(point p: i) {
						if(p.getX() < 0 || p.getX() >= 10 || p.getY() < 0 || p.getY() >= 20) {
							return;
						}
						component.colors[p.x][p.y] = component.objectColor; 
					}
					component.repaint();
				}
			}
		}
		@Override
		public int compareTo(movementHelper o) {
			if(o.timerTask.key == timerTask.key) {
				return 1;
			}
			return 0;
		}
	}
}
