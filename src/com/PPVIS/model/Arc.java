package com.PPVIS.model;


import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Canvas;

import java.util.concurrent.atomic.AtomicLong;

public class Arc {
    private static AtomicLong atomicLong = new AtomicLong();
    private long ID;
    private int weight = 1;
    private Vertex ingoing;
    private Vertex outgoing;
    private boolean isOriented;
    private Canvas canvas;
    private Color defaultColor;
    private int xIn;
    private int yIn;
    private Color green = new Color(null, 29, 216, 23);
    private Color black = new Color(null, 0, 0, 0);
    private PaintListener paintListener;


    public Arc(Vertex outgoing, Vertex ingoing, Canvas canvas) {
        ID = atomicLong.incrementAndGet();
        this.ingoing = ingoing;
        this.outgoing = outgoing;
        outgoing.addOutgoing(this);
        ingoing.addIngoing(this);
        isOriented = true;
        this.canvas = canvas;
        defaultColor = black;
        draw();
        canvas.redraw();
    }

    public Arc(Vertex outgoing, int x, int y, Canvas canvas) {
        ID = atomicLong.incrementAndGet();
        this.outgoing = outgoing;
        outgoing.addOutgoing(this);
        isOriented = true;
        this.canvas = canvas;
        defaultColor = black;
        xIn = x;
        yIn = y;
        defaultColor = black;
        draw();
        canvas.redraw();
    }

    public void draw() {
        paintListener = new PaintListener() {
            @Override
            public void paintControl(PaintEvent paintEvent) {
                int x1 = outgoing.getX();
                int y1 = outgoing.getY();
                int x2 = ingoing == null ? xIn : ingoing.getX();
                int y2 = ingoing == null ? yIn : ingoing.getY();
                int r = Vertex.getRadius();
                paintEvent.gc.setForeground(defaultColor);
                paintEvent.gc.setLineWidth(3);
                int x3, y3;
                if (ingoing == null) {
                    if (x1 != x2) {
                        x3 = x1 < x2 ? (int) (x1 + r * Math.sqrt(1 / (Math.pow(((y1 - y2) / (x1 - x2)), 2) + 1))) : (int) (x1 - r * Math.sqrt(1 / (Math.pow(((y1 - y2) / (x1 - x2)), 2) + 1)));
                        y3 = y1 < y2 ? (int) (y1 + r * Math.sqrt(1 - 1 / (Math.pow((y1 - y2) / (x1 - x2), 2) + 1))) : (int) (y1 - r * Math.sqrt(1 - 1 / (Math.pow((y1 - y2) / (x1 - x2), 2) + 1)));
                    } else {
                        x3 = x1;
                        y3 = y1 > y2 ? y1 - r : y1 + r;
                    }
                    drawArrow(paintEvent.gc, x3, y3, xIn, yIn, 8, 120);
                } else {
                    int x4, y4;
                    double sqrtX, sqrtY;
                    if (x1 != x2) {
                        sqrtX = Math.sqrt(1 / (Math.pow(((y1 - y2) / (x1 - x2)), 2) + 1)) * r;
                        sqrtY = Math.sqrt(1 - 1 / (Math.pow((y1 - y2) / (x1 - x2), 2) + 1)) * r;
                        x3 = x1 < x2 ? (int) (x1 + sqrtX) : (int) (x1 - sqrtX);
                        y3 = y1 < y2 ? (int) (y1 + sqrtY) : (int) (y1 - sqrtY);
                        x4 = x1 < x2 ? (int) (x2 - sqrtX) : (int) (x2 + sqrtX);
                        y4 = y1 < y2 ? (int) (y2 - sqrtY) : (int) (y2 + sqrtY);
                    } else {
                        x3 = x1;
                        y3 = y1 > y2 ? y1 - r : y1 + r;
                        x4 = x2;
                        y4 = y1 > y2 ? y2 + r : y2 - r;
                    }
                    drawArrow(paintEvent.gc, x3, y3, x4, y4, 8, 120);
                }
            }
        };
        canvas.addPaintListener(paintListener);
    }

    public void drawArrow(GC gc, int x1, int y1, int x2, int y2, double arrowLength, double arrowAngle) {
        gc.drawLine(x1, y1, x2, y2);
        double theta = Math.atan2(y2 - y1, x2 - x1);
        if (isOriented) {
            Path path = new Path(gc.getDevice());
            path.moveTo((float) (x2 - arrowLength * Math.cos(theta - arrowAngle)), (float) (y2 - arrowLength * Math.sin(theta - arrowAngle)));
            path.lineTo((float) x2, (float) y2);
            path.lineTo((float) (x2 - arrowLength * Math.cos(theta + arrowAngle)), (float) (y2 - arrowLength * Math.sin(theta + arrowAngle)));
            path.close();
            gc.drawPath(path);
            path.dispose();
        }
        if (x1 > x2)
            gc.drawText(String.valueOf(weight), (int) ((x1 + x2) / 2 - 10 * Math.cos(theta + 90)), (int) ((y1 + y2) / 2 - 10 * Math.sin(theta + 90)));
        else
            gc.drawText(String.valueOf(weight), (int) ((x1 + x2) / 2 - 10 * Math.cos(theta - 90)), (int) ((y1 + y2) / 2 - 10 * Math.sin(theta - 90)));
    }

    public void select() {
        defaultColor = green;
        canvas.redraw();
    }

    public void deselect() {
        defaultColor = black;
        canvas.redraw();
    }

    public void setIngoing(Vertex ingoing) {
        this.ingoing = ingoing;
        ingoing.addIngoing(this);
        xIn = -1;
        yIn = -1;
        canvas.redraw();
    }

    public void delete() {
        canvas.removePaintListener(paintListener);
        canvas.redraw();
        if (ingoing != null)
            ingoing.delIngoing(this);
        outgoing.delOutgoing(this);
    }

    public void setXY(int xIn, int yIn) {
        this.xIn = xIn;
        this.yIn = yIn;
        canvas.redraw();
    }

    public Vertex getIngoing() {
        return ingoing;
    }

    public Vertex getOutgoing() {
        return outgoing;
    }

    public boolean isOriented() {
        return isOriented;
    }

    public void setOriented(boolean oriented) {
        isOriented = oriented;
        canvas.redraw();
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
        canvas.redraw();
    }

    public long getID() {
        return ID;
    }

    public void changeOrientation() {
        Vertex temp = ingoing;
        ingoing.delIngoing(this);
        outgoing.delOutgoing(this);
        ingoing = outgoing;
        outgoing = temp;
        ingoing.addIngoing(this);
        outgoing.addOutgoing(this);
        canvas.redraw();
    }

}
