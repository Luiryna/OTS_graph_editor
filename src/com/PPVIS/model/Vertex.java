package com.PPVIS.model;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Vertex {
    private static AtomicLong atomicLong = new AtomicLong();
    private long ID;
    private Canvas canvas;
    private static int radius = 10;
    private String name = "";
    private int x;
    private int y;
    private int distance = -1;
    private List<Arc> ingoing;
    private List<Arc> outgoing;
    private Color defaultColor;
    private Color green = new Color(null, 29, 216, 23);
    private Color black = new Color(null, 0, 0, 0);
    private PaintListener paintListener;

    public Vertex(int x, int y, Canvas canvas) {
        ID = atomicLong.incrementAndGet();
        this.x = x;
        this.y = y;
        this.canvas = canvas;
        ingoing = new ArrayList<>();
        outgoing = new ArrayList<>();
        defaultColor = black;
        draw();
        canvas.redraw();
    }

    private void draw() {
        paintListener = new PaintListener() {
            @Override
            public void paintControl(PaintEvent paintEvent) {
                paintEvent.gc.setForeground(defaultColor);
                paintEvent.gc.setLineWidth(5);
                paintEvent.gc.drawOval(x - radius, y - radius, radius * 2, radius * 2);
                paintEvent.gc.drawText(name, x + radius, y + radius);
                if (distance != -1) {
                    paintEvent.gc.setForeground(new Color(null, 10,10,255));
                    paintEvent.gc.drawText(String.valueOf(distance), x + 2*radius, y - 2*radius);
                }
            }
        };
        canvas.addPaintListener(paintListener);
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
        canvas.redraw();
    }

    public void move(int x, int y) {
        defaultColor = green;
        this.x = x;
        this.y = y;
        canvas.redraw();
    }

    public void select() {
        defaultColor = green;
        canvas.redraw();
    }

    public void setDistance(int distance) {
        this.distance = distance;
        canvas.redraw();
    }

    public void deselect() {
        defaultColor = black;
        canvas.redraw();
    }

    public static int getRadius() {
        return radius;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void delete() {
        ingoing.clear();
        outgoing.clear();
        canvas.removePaintListener(paintListener);
        canvas.redraw();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        canvas.redraw();
    }

    public List<Arc> getIngoing() {
        return ingoing;
    }

    public List<Arc> getOutgoing() {
        return outgoing;
    }

    public void addIngoing(Arc arc) {
        ingoing.add(arc);
    }

    public void addOutgoing(Arc arc) {
        outgoing.add(arc);
    }

    public void delIngoing(Arc arc) {
        ingoing.remove(arc);
    }

    public void delOutgoing(Arc arc) {
        outgoing.remove(arc);
    }

    public long getID() {
        return ID;
    }

}
