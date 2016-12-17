import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.lang.Double.isFinite;
import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import javax.swing.JPanel;

import exception.ProgramException;
import program.Bytecode;
import program.Program;
import statement.AssignStatement;
import statement.Number;

/**
 * A {@link JPanel} that draws shapes according to a program. Uses an {@link Interpreter} to execute
 * the programs given with {@link #setProgram(String)}.
 */
public class DrawingPanel extends JPanel {
    
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    
    private String program;
    private int iterations;
    
    /**
     * Creates a drawing panel that displays the area given by the mins and maxes.
     */
    public DrawingPanel(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        setBackground(Color.WHITE);
    }
    
    /**
     * Sets the program to be "drawn" on the panel.
     */
    public void setProgram(String program) {
        if(!Objects.equals(this.program, program)) {
            this.program = program;
            repaint();
        }
    }
    
    /**
     * Sets the number of repetitions <i>n</i>. The program will be executed <i>n + 1</i> times and
     * hence <i>n</i> lines will be drawn.
     */
    public void setRepetitions(int repetitions) {
        if(repetitions < 1)
            throw new IllegalArgumentException("repetitions must be positive");
        
        this.iterations = repetitions + 1; // need at least two points to draw something
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        
        // enable anti-aliasing to make lines look smoother:
        ((Graphics2D) graphics).setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        
        try {
            long startTime = System.nanoTime();
            
            double[] xs = new double[iterations];
            double[] ys = new double[iterations];
            Color[] colors = new Color[iterations];
            
            boolean useBytecode = true;
			if(useBytecode) {
				Program p = new Program(program);
				Bytecode b = p.compileToByteCodeTopDown();

				Random r = new Random(73);
				int red=r.nextInt(255),green=r.nextInt(255),blue=r.nextInt(255), tred=red,tgreen=green,tblue=blue;
				for(int i = 0; i < iterations; ++i) {
					HashMap<String, Double> predefinedVariables = new HashMap<String,Double>();
					predefinedVariables.put("n", (double) iterations);
					predefinedVariables.put("i", (double) i);
					predefinedVariables = b.evaluate(predefinedVariables, false);
					xs[i] = predefinedVariables.get("x");
					ys[i] = predefinedVariables.get("y");
					if(red == tred && green == tgreen && blue == tblue) {
						tred = r.nextInt(255);
						tgreen = r.nextInt(255);
						tblue = r.nextInt(255);
					}
					if(red != tred) red += tred < red ? -1 : 1;
					if(green != tgreen) green += tgreen < green ? -1 : 1;
					if(blue != tblue) blue += tblue < blue ? -1 : 1;

					colors[i] = new Color(red, green, blue);
				}
			}
			else {
				Random r = new Random(73);
				int red=r.nextInt(255),green=r.nextInt(255),blue=r.nextInt(255), tred=red,tgreen=green,tblue=blue;
				for(int i = 0; i < iterations; ++i) {
	            	String newProgram = "n="+iterations+"; \n i="+i+";\n" + program;
	            	Program p = new Program(newProgram);
	            	p.topDownParser(false);
	            	HashMap<String, AssignStatement> states = p.getUserdefinedStatements();
	            	xs[i] = ((Number)states.get("x").getExpression()).getValue();
	            	ys[i] = ((Number)states.get("y").getExpression()).getValue();
	            	
	            	if(red != tred) red += tred < red ? -1 : 1;
					if(green != tgreen) green += tgreen < green ? -1 : 1;
					if(blue != tblue) blue += tblue < blue ? -1 : 1;

					colors[i] = new Color(red, green, blue);
	            }

			}
        
            // TODO: Interpret program "iterations" times and retrieve xs, ys and colors.
            //if(program != null)
            //    throw new ProgramException("not implemented yet");
            
            
            
            drawAxes(graphics);
            drawLines(graphics, xs, ys, colors);
            
            long drawTime = System.nanoTime() - startTime;
            drawFps(graphics, 1000000000.0 / drawTime);
        } catch(ProgramException e) {
            graphics.setColor(Color.RED);
            graphics.drawString(e.getMessage(), 10, getHeight() - 10);
        }
    }
    
    /**
     * Draws the axes of the coordinate system and ticks for every unit.
     */
    private void drawAxes(Graphics g) {
        int tickHalfLength = 3;
        
        g.setColor(new Color(0.75f, 0.75f, 0.75f));
        int zeroGuiX = toGuiX(0);
        int zeroGuiY = toGuiY(0);
        g.drawLine(0, zeroGuiY, getWidth(), zeroGuiY);
        g.drawLine(zeroGuiX, 0, zeroGuiX, getHeight());
        
        for(long x = round(minX); x < maxX; x++)
            if(x != 0) {
                int guiX = toGuiX(x);
                g.drawLine(guiX, zeroGuiY - tickHalfLength, guiX, zeroGuiY + tickHalfLength);
            }
        
        for(long y = round(minY); y < maxY; y++)
            if(y != 0) {
                int guiY = toGuiY(y);
                g.drawLine(zeroGuiX - tickHalfLength, guiY, zeroGuiX + tickHalfLength, guiY);
            }
    }
    
    /**
     * Draws the lines given by the x and y coordinates and the colors. Note that the first color in
     * the array is ignored, since the color of each line is determined by the end point.
     */
    private void drawLines(Graphics g, double[] xs, double[] ys, Color[] colors) {
        for(int i = 1; i < xs.length; i++) {
            if(isFinite(xs[i - 1]) && isFinite(ys[i - 1]) && isFinite(xs[i]) && isFinite(ys[i])) {
                g.setColor(colors[i]);
                g.drawLine(toGuiX(xs[i - 1]), toGuiY(ys[i - 1]), toGuiX(xs[i]), toGuiY(ys[i]));
            }
        }
    }
    
    /**
     * Draws the number of frames per second that the panel could draw (the actual number of frames
     * that are drawn is determined by how often {@link #repaint()} is called) in the lower right
     * corner.
     */
    private void drawFps(Graphics g, double fps) {
        g.setColor(new Color(0.6f, 0.6f, 0.6f));
        String text = String.format("FPS: %.2f", fps);
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, getWidth() - textWidth - 10, getHeight() - 10);
    }
    
    /**
     * Converts an x value in the "program" coordinate system to a GUI x coordinate.
     */
    private int toGuiX(double x) {
        return (int) ((x - minX) / (maxX - minX) * getWidth());
    }
    
    /**
     * Converts a y value in the "program" coordinate system to a GUI y coordinate.
     */
    private int toGuiY(double y) {
        return (int) ((1 - (y - minY) / (maxY - minY)) * getHeight());
    }
}
