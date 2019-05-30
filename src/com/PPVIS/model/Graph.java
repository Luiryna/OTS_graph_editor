package com.PPVIS.model;

import org.eclipse.swt.widgets.Canvas;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Graph {
    private Canvas canvas;
    private String name;
    private List<Arc> arcs = new ArrayList<>();
    private List<Vertex> vertices = new ArrayList<>();
    private Vertex selectVertex;
    private Arc selectArc;


    public Graph(String name) {
        this.name = name;
    }

    public Graph(String name, Canvas canvas) {
        this.name = name;
        this.canvas = canvas;
    }

    public void addVertex(int x, int y) {
        if (findVertex(x, y) == null)
            vertices.add(new Vertex(x, y, canvas));
    }

    public void addVertex(Vertex vertex) {
        if (vertex != null)
            vertices.add(vertex);
    }

    public void addArc(Vertex outgoing, Vertex ingoing) {
        if (outgoing != null && ingoing != null) {
            Arc arc = new Arc(outgoing, ingoing, canvas);
            arcs.add(arc);
        }
    }

    public void addArc(Arc arc) {
        if (arc != null)
            arcs.add(arc);
    }

    public Arc addArc(Vertex outgoing, int xIn, int yIn) {
        if (outgoing != null) {
            Arc arc = new Arc(outgoing, xIn, yIn, canvas);
            arcs.add(arc);
            return arc;
        }
        return null;
    }

    public void delete(Arc arc) {
        arcs.remove(arc);
        arc.delete();
    }

    public void delete(Vertex vertex) {
        vertices.remove(vertex);
        for (Arc arc : new ArrayList<>(vertex.getIngoing())) {
            arc.delete();
            arcs.remove(arc);

        }
        for (Arc arc : new ArrayList<>(vertex.getOutgoing())) {
            arc.delete();
            arcs.remove(arc);
        }
        vertex.delete();
    }

    public Vertex findVertex(int x, int y) {
        for (Vertex vertex : vertices) {
            int x1 = vertex.getX() - x;
            int y1 = vertex.getY() - y;
            int r = vertex.getRadius();
            if (r >= Math.sqrt(x1 * x1 + y1 * y1)) {
                return vertex;
            }
        }
        return null;
    }

    public void select(Vertex vertex) {
        if (selectVertex != null) {
            selectVertex.deselect();
        }
        if (selectArc != null)
            selectArc.deselect();
        selectVertex = vertex;
        if (vertex != null)
            vertex.select();
    }

    public Arc findArc(int x, int y) {
        for (Arc arc : arcs) {
            int x1 = arc.getOutgoing().getX();
            int y1 = arc.getOutgoing().getY();
            int x2 = arc.getIngoing().getX();
            int y2 = arc.getIngoing().getY();
            double len1 = Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
            double len2 = Math.sqrt((x2 - x) * (x2 - x) + (y2 - y) * (y2 - y));
            double len = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            if (len - 0.5 < len1 + len2 && len1 + len2 < len + 0.5) {
                return arc;
            }
        }
        return null;
    }

    public void select(Arc arc) {
        if (selectArc != null) {
            selectArc.deselect();
        }
        if (selectVertex != null)
            selectVertex.deselect();
        selectArc = arc;
        if (arc != null)
            arc.select();
    }

    public Vertex getSelectVertex() {
        return selectVertex;
    }

    public Arc getSelectArc() {
        return selectArc;
    }

    public void deselectArc() {
        selectArc.deselect();
        selectArc = null;
    }

    public void deselectVertex() {
        if (selectVertex != null)
            selectVertex.deselect();
        selectVertex = null;
    }

    public void deleteSelected() {
        if (selectArc != null) {
            delete(selectArc);
        }
        if (selectVertex != null) {
            delete(selectVertex);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Arc> getArcs() {
        return arcs;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public int[][] getAdjacencyMatrix() {
        int[][] matrix = new int[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                final int indexJ = j;
                final int indexI = i;
                if(arcs.stream().anyMatch(t -> (t.getOutgoing().getID() == vertices.get(indexI).getID()) && (t.getIngoing().getID() == vertices.get(indexJ).getID()))) {
                    matrix[i][j] = 1;
                }
                else { matrix[i][j] = 0; }
            }
        }
        return matrix;
    }
    
     public int getRadius() {
        int[][] matrix = new int[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                final int indexJ = j;
                final int indexI = i;
                if(arcs.stream().anyMatch(t -> (t.getOutgoing().getID() == vertices.get(indexI).getID()) && (t.getIngoing().getID() == vertices.get(indexJ).getID()))) {
                    matrix[i][j] = 1;
                }
                else { matrix[i][j] = 0; }
            }
        }

        int[][] edge = new int[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size(); i++)
            for (int j = 0; j < vertices.size(); j++) {
                edge[i][j] = matrix[i][j];
                if (edge[i][j] == 0)
                    edge[i][j] = 100000;//машинная бесконечность
            }//Находим кратчайшие расстояния между всеми парами вершин
        for (int k = 0; k < vertices.size(); k++)           //алгоритм Флойда
            for (int i = 0; i < vertices.size(); i++)
                for (int j = 0; j < vertices.size(); j++)
                    if (i != j)
                        edge[i][j] = Math.min(edge[i][j], edge[i][k] + edge[k][j]);


        int radius = 100000, max, diameter = -1;
        int[] ecc = new int[vertices.size()];
        int[] rad = new int[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            rad[i] = ecc[i] = 0;
            max = -1;
            for (int j = 0; j < vertices.size(); j++) {
                if (i!=j)
                {
                    if(edge[i][j] > max)//если ребро > max
                    {
                        max = edge[i][j];//то max присваиваем значение ребра
                    }
                    if(edge[i][j] > diameter)//если ребро > диаметра
                        diameter = edge[i][j];//то диаметру присваиваем значение ребра
                }
            }
            ecc[i] = max;    //расстояние от этой (т.е. от центра) вершины до самой удаленной
            if (max<radius)
            {
                radius  = max; // присваиваем значение радиусу
            }
        }//Если эксцентриситет вершины равен радиусу графа то эта вершина центр графа, выводим её.
        int j = 0;   //кол-во вершин центра
        for(int i = 0; i < vertices.size(); i++)
        {
            if(radius == ecc[i])
                rad[j++] = i;
        }
        System.out.println(radius);

        return radius;
    }

    public int getDiameter() {
        int[][] matrix = new int[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                final int indexJ = j;
                final int indexI = i;
                if(arcs.stream().anyMatch(t -> (t.getOutgoing().getID() == vertices.get(indexI).getID()) && (t.getIngoing().getID() == vertices.get(indexJ).getID()))) {
                    matrix[i][j] = 1;
                }
                else { matrix[i][j] = 0; }
            }
        }

        int[][] edge = new int[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size(); i++)
            for (int j = 0; j < vertices.size(); j++) {
                edge[i][j] = matrix[i][j];
                if (edge[i][j] == 0)
                    edge[i][j] = 100000;//машинная бесконечность
            }//Находим кратчайшие расстояния между всеми парами вершин
        for (int k = 0; k < vertices.size(); k++)           //алгоритм Флойда
            for (int i = 0; i < vertices.size(); i++)
                for (int j = 0; j < vertices.size(); j++)
                    if (i != j)
                        edge[i][j] = Math.min(edge[i][j], edge[i][k] + edge[k][j]);


        int radius = 100000, max, diameter = -1;
        int[] ecc = new int[vertices.size()];
        int[] rad = new int[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            rad[i] = ecc[i] = 0;
            max = -1;
            for (int j = 0; j < vertices.size(); j++) {
                if (i!=j)
                {
                    if(edge[i][j] > max)//если ребро > max
                    {
                        max = edge[i][j];//то max присваиваем значение ребра
                    }
                    if(edge[i][j] > diameter)//если ребро > диаметра
                        diameter = edge[i][j];//то диаметру присваиваем значение ребра
                }
            }
            ecc[i] = max;    //расстояние от этой (т.е. от центра) вершины до самой удаленной
            if (max<radius)
            {
                radius  = max; // присваиваем значение радиусу
            }
        }//Если эксцентриситет вершины равен радиусу графа то эта вершина центр графа, выводим её.
        int j = 0;   //кол-во вершин центра
        for(int i = 0; i < vertices.size(); i++)
        {
            if(radius == ecc[i])
                rad[j++] = i;
        }
        System.out.println(diameter);

        return diameter;
    }

    public Arc getArc(Vertex ingoing, Vertex outgoing) {
        for (int i = 0; i < arcs.size(); i++) {
            if ((arcs.get(i).getIngoing().getID() == ingoing.getID()) && (arcs.get(i).getOutgoing().getID() == outgoing.getID())) {
                return arcs.get(i);
            }
        }
        return null;
    }
}
