import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// Abstract class representing a generic shape
abstract class Shape {
    private int x1, y1, x2, y2;
    private Color color;
    private boolean isSolid;

    // Constructor for initializing common attributes of a shape
    public Shape(Color color, boolean isSolid, int x1, int y1, int x2, int y2) {
        this.color = color;
        this.isSolid = isSolid;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public Color getColor() {
        return color;
    }

    public boolean isSolid() {
        return isSolid;
    }

	// Abstract method to be implemented by subclasses for drawing the shape
    public abstract void draw(Graphics g);
}

class Rectangle extends Shape {
	// Constructor calling the superclass constructor
    public Rectangle(Color color, boolean isSolid, int x1, int y1, int x2, int y2) {
        super(color, isSolid, x1, y1, x2, y2);
    }

    public void draw(Graphics g) {
        g.setColor(getColor());
        if (isSolid()) {
            g.fillRect(getX1(), getY1(), getX2(), getY2());
        } else {
            g.drawRect(getX1(), getY1(), getX2(), getY2());
        }
    }
}

class Oval extends Shape {
    public Oval(Color color, boolean isSolid, int x1, int y1, int x2, int y2) {
        super(color, isSolid, x1, y1, x2, y2);
    }

    public void draw(Graphics g) {
        g.setColor(getColor());
        if (isSolid()) {
            g.fillOval(getX1(), getY1(), getX2(), getY2());
        } else {
            g.drawOval(getX1(), getY1(), getX2(), getY2());
        }
    }
}

class Line extends Shape {
    public Line(Color color, boolean isSolid, int x1, int y1, int x2, int y2) {
        super(color, isSolid, x1, y1, x2, y2);
    }

    public void draw(Graphics g) {
        g.setColor(getColor());
        g.drawLine(getX1(), getY1(), getX2(), getY2());
    }
}

public class PaintBrush extends Applet implements MouseListener, MouseMotionListener {
    private Color currentColor = Color.BLACK;
    private boolean isSolid = false;

    private ArrayList<Shape> shapes = new ArrayList<>();
	private ArrayList<Shape> redoShapes = new ArrayList<>();

    private int startX, startY, endX, endY;
    private int currentShape;

    private static final int Rectangle = 1;
    private static final int Oval = 2;
    private static final int Line = 3;
    private static final int Pencil = 4;
    private static final int Eraser = 5;
    private static final int Clear_All = 6;
	private static final int Undo = 7;
	private static final int Redo = 8;

    private Checkbox solidCheckbox;
	private CheckboxGroup colorCheckboxGroup;

	
    public void init() {
        addMouseListener(this);
        addMouseMotionListener(this);

        createButton("Rectangle", Rectangle, Color.LIGHT_GRAY);
        createButton("Oval", Oval, Color.LIGHT_GRAY);
        createButton("Line", Line, Color.LIGHT_GRAY);
        createButton("Pencil", Pencil, Color.LIGHT_GRAY);
        createButton("Eraser", Eraser, Color.LIGHT_GRAY);
		createButton("Undo", Undo, Color.LIGHT_GRAY);
		createButton("Redo", Redo, Color.LIGHT_GRAY);
        createButton("Clear_All", Clear_All, Color.LIGHT_GRAY);

        colorCheckboxGroup = new CheckboxGroup();
        createColorCheckbox("Red", Color.RED);
        createColorCheckbox("Green", new Color(8, 124, 0));
        createColorCheckbox("Blue", Color.BLUE);
        createColorCheckbox("Black", Color.BLACK);

        solidCheckbox = new Checkbox("Solid", false);
        solidCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                isSolid = solidCheckbox.getState();
            }
        });

        add(solidCheckbox);
    }

    private void createButton(String label, int shapeType, Color buttonColor) {
		Button button = new Button(label);
		button.setBackground(buttonColor);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleButtonClick(shapeType);
			}
		});
		add(button);
	}

    private void handleButtonClick(int shapeType) {
        currentShape = shapeType;

        // enter the body of this condition just if I click on Clear_All button
        if (currentShape == Clear_All) {
            shapes.clear();
            repaint();
        } else if (currentShape == Undo && shapes.size() > 0){
			 int x = shapes.size() - 1;
			 redoShapes.add(shapes.get(x));
			 shapes.remove(x);
			 repaint();
		} else if (currentShape == Redo && redoShapes.size() > 0){
				int x = redoShapes.size() - 1;
				shapes.add(redoShapes.get(x));
				redoShapes.remove(x);
				repaint();
		}
	}

    private void createColorCheckbox(String label, Color color) {
        Checkbox checkbox = new Checkbox(label, colorCheckboxGroup, false);
        checkbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                currentColor = color;
            }
        });
        add(checkbox);
    }


    public void mousePressed(MouseEvent e) {
        startX = e.getX();
		startY = e.getY();
		
		// Update end coordinates for the next iteration
		endX = startX; 
		endY = startY; 
    }
	
	public void mouseDragged(MouseEvent e) {
		endX = e.getX();
        endY = e.getY();

		switch(currentShape){
            case Pencil:
                int pencilEndX = e.getX();
                int pencilEndY = e.getY();
                shapes.add(new Line(currentColor, isSolid, startX, startY, pencilEndX, pencilEndY));
				
				// Update start coordinates for the next iteration
                startX = pencilEndX;
                startY = pencilEndY;
                break;
            case Eraser:
				int eraserEndX = e.getX();
				int eraserEndY = e.getY();
				
				// Create a rectangle from the start coordinates to the current mouse coordinates
				shapes.add(new Oval(Color.WHITE, isSolid, startX, startY, 25, 25));

				// Update start coordinates for the next iteration
				startX = eraserEndX;
				startY = eraserEndY;
				
				break;

        }

        repaint(); // Repaint to show the changing shape
    }

    public void mouseReleased(MouseEvent e) {
        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.abs(endX - startX);
        int height = Math.abs(endY - startY);

        switch (currentShape) {
            case Rectangle:
                shapes.add(new Rectangle(currentColor, isSolid, x, y, width, height));
                break;
            case Oval:
                shapes.add(new Oval(currentColor, isSolid, x, y, width, height));
                break;
			case Line:
                shapes.add(new Line(currentColor, isSolid, startX, startY, endX, endY));
                break;
            default:
                break;
        }

        repaint();
    }


    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

    // Add paint method to draw shapes based on the ArrayList
    public void paint(Graphics g) {
        for (Shape shape : shapes) {
            shape.draw(g);
        }
			
    // Draw the current shape being dragged
		int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.abs(endX - startX);
        int height = Math.abs(endY - startY);

		switch (currentShape) {
            case Rectangle:
                (new Rectangle(currentColor, isSolid, x, y, width, height)).draw(g);
                break;
            case Oval:
                (new Oval(currentColor, isSolid, x, y, width, height)).draw(g);
                break;
			case Line:
                (new Line(currentColor, isSolid, startX, startY, endX, endY)).draw(g);
                break;
            default:
                break;
    }
}
}

/*All rights reserved © 28/12/2023 --> Hassan Hosny Hassan*/