package com.PPVIS.model;


import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;

import java.util.concurrent.atomic.AtomicLong;

public class Arc {
    private static AtomicLong atomicLong = new AtomicLong();
    private long ID;
    private int weight=1;
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


    protected Arc(Vertex outgoing, Vertex ingoing, Canvas canvas) {
        ID=atomicLong.incrementAndGet();
        this.ingoing = ingoing;
        this.outgoing = outgoing;
        isOriented = true;
        this.canvas = canvas;
        defaultColor = black;
        draw();
        canvas.redraw();
    }

    protected Arc(Vertex outgoing, int x, int y, Canvas canvas) {
        ID=atomicLong.incrementAndGet();
        this.outgoing = outgoing;
        isOriented = true;
        this.canvas = canvas;
        defaultColor = black;
        xIn = x;
        yIn = y;
        defaultColor=black;
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
                if (ingoing == null)
                    paintEvent.gc.drawLine(x1, y1, xIn, yIn);
                else
                    paintEvent.gc.drawLine(x1, y1, ingoing.getX(), ingoing.getY());
            }
        };
        canvas.addPaintListener(paintListener);
    }

    public void select() {
        defaultColor =green;
        canvas.redraw();
    }

    public void deselect() {
        defaultColor =black;
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

    public void deleteArc(){
        canvas.removePaintListener(paintListener);
        canvas.redraw();
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
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public long getID() {
        return ID;
    }
}
