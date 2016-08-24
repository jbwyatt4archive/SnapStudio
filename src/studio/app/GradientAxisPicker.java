package studio.app;
import snap.gfx.*;
import snap.gfx.GradientPaint.Stop;
import snap.view.*;

/**
 * GradientAxisPicker.
 */
public class GradientAxisPicker extends View {

    // Ivars
    Point                   _startPoint = Point.get(0.5,0.5);
    Point                   _endPoint = Point.get(1d, 0.5);
    Stop                    _stops[];
    boolean                 _dragging;
    ImagePaint              _background;

/**
 * Creates new GradientAxisPicker.
 */
public GradientAxisPicker()
{
    enableEvents(MousePressed, MouseDragged, MouseReleased, Action);
    setBorder(Border.createLoweredBevelBorder());
}

/**
 * Returns the starting point for gradient, defined as point in unit square where the first color stop is drawn.
 */
public Point getStartPoint()  { return _startPoint; }

/**
 * Sets the starting point for gradient.
 */
public void setStartPoint(double aX, double aY)  { _startPoint = new Point(aX,aY); }

/**
 * Returns the ending point for the gradient, defined as a point in the unit square where a circle centered at 
 * StartPoint and with the color of the last color stop, passes through.
 */
public Point getEndPoint()  { return _endPoint; }

/**
 * Sets the ending point for the gradient.
 */
public void setEndPoint(double aX, double aY)  { _endPoint = new Point(aX,aY); }

/**
 * Returns stops used for editing.
 */
public Stop[] getStops()  { return _stops; }

/**
 * Sets stops used for editing.
 */
public void setStops(Stop[] theStops)  { _stops = theStops; repaint(); }

/**
 * Paint node.
 */
protected void paintFront(Painter aPntr)
{
    // If no gradient, just return
    if(_stops==null) return;
    
    // Scale gradient points from unit square into this component's drawing area
    Rect r = getBoundsInset();
    Point sp = convertToBounds(_startPoint), ep = convertToBounds(_endPoint);
    double sx = sp.getX(), sy = sp.getY(), ex = ep.getX(), ey = ep.getY();
     
    // Draw a background under gradients with alpha
    if(GradientPaint.getStopsHaveAlpha(_stops)) {
        aPntr.setPaint(getBackgroundTexture()); aPntr.fill(r); }
    
    // Create/set awt Paint class to draw the gradient and fill
    aPntr.setPaint(new GradientPaint(GradientPaint.Type.RADIAL, sx, sy, ex, ey, getStops())); aPntr.fill(r);
    
    // If dragging, draw axis line
    if(_dragging) { int xsz = 3;
        aPntr.setPaint(Color.BLACK); aPntr.drawLine(sx,sy,ex,ey);
        aPntr.setPaint(Color.GREEN); aPntr.drawLine(sx-xsz,sy,sx+xsz,sy); aPntr.drawLine(sx,sy-xsz,sx,sy+xsz);
        aPntr.setPaint(Color.RED); aPntr.drawLine(ex-xsz,ey,ex+xsz,ey); aPntr.drawLine(ex,ey-xsz,ex,ey+xsz);
    }
}

/**
 * Process events.
 */
protected void processEvent(ViewEvent anEvent)
{
    // Handle MousePressed
    if(anEvent.isMousePressed()) {
        Point pt = anEvent.getPoint();
        _startPoint = convertToProportional(pt);
        _endPoint = convertToProportional(pt);
        _dragging = true;
        repaint();
    }
    
    // Handle MouseDragged
    else if(anEvent.isMouseDragged()) {
        _endPoint = convertToProportional(anEvent.getPoint());
        repaint();
    }
    
    // Handle MouseReleased: Reset dragging flag, send node event and repaint
    else if(anEvent.isMouseReleased()) {
        _dragging = false;
        fireActionEvent();
        repaint();
    }
}

// Returns bounds inset by border.
private Rect getBoundsInset()
{
    Insets ins = getInsetsAll();
    return new Rect(ins.left, ins.top, getWidth() - (ins.left+ins.right), getHeight()-(ins.top+ins.bottom));
}

// Converts a point in unit coords to bounds.
private Point convertToBounds(Point aPoint)
{
    Rect r = getBoundsInset();
    return Point.get(r.getX() + aPoint.getX()*r.getWidth(), r.getY() + aPoint.getY()*r.getHeight());
}

// Converts a point in bounds coords unit coords.
private Point convertToProportional(Point aPoint)
{
    Rect r = getBoundsInset();
    return Point.get((aPoint.getX()-r.getMinX())/r.getWidth(), (aPoint.getY()-r.getMinY())/r.getHeight());
}

/** Returns a texture to be used for the background of transparent gradients */
private ImagePaint getBackgroundTexture()
{
    if(_background!=null) return _background;
    int cs = 4, w = 2*cs;
    Image img = Image.get(w,w,true); Painter pntr = img.getPainter();
    pntr.setColor(Color.WHITE); pntr.fillRect(0, 0, w, w);
    pntr.setColor(new Color(168,193,255)); pntr.fillRect(0,0,cs,cs); pntr.fillRect(cs,cs,cs,cs);
    return _background = new ImagePaint(img);
}

}