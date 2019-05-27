package com.PPVIS.model;//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.util.Pair;
//import model.Arc;
//import model.Graph;
//import model.Node;

import com.PPVIS.model.Arc;
import com.PPVIS.model.Graph;
import com.PPVIS.model.Vertex;


import java.util.ArrayList;

import javax.naming.NameClassPair;
import java.security.KeyPair;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class Product {
    private Graph first;
    private Graph second;

    public Product(Graph first, Graph second) {
        this.first = first;
        this.second = second;
    }
    public Graph cartesianProduct(Graph g, Graph h) {
        List<Vertex[]> nodePairs = new ArrayList();
        Map<Vertex[], Vertex> nodePairsMatching = new HashMap<>();


        List<Vertex> gNodes = g.getVertices();
        List<Vertex> hNodes = h.getVertices();

        Graph product = new Graph("product");

        for (Vertex u : gNodes) {
            for (Vertex v : hNodes) {
                Vertex[] uv = new Vertex[2];
                uv[0] = u;
                uv[1] = v;
                Vertex uvMatching = new Vertex();
                nodePairs.add(uv);
                nodePairsMatching.put(uv, uvMatching);
                product.getVertices().add(uvMatching);
            }
        }

        for (Vertex[] uv : nodePairs) {
            for (Vertex[] u1v1 : nodePairs) {
                if ((uv[0].equals(u1v1[0]) && (h.getArc(uv[1], u1v1[1]) != null))
                        || (uv[1].equals(u1v1[1]) && (g.getArc(uv[0], u1v1[0]) != null))) {
                    product.addArc(nodePairsMatching.get(uv), nodePairsMatching.get(u1v1));
                }
            }
        }

        return product;
    }

    public Graph createCopyGraph() {
        int times = second.getVertices().size();
        int amountOfVertices = first.getVertices().size();
        for (int i = 1; i <= times; i++) {
            for (int j = 0; j < amountOfVertices; j++) {
                first.addVertex(first.getVertices().get(j).getX() + (i * 10), first.getVertices().get(j).getY());
            }
        }
        return first;
    }

//    public static Graph tensorProduct(Graph g, Graph h) {
//        ObservableList<Pair<Vertex, Vertex>> nodePairs = FXCollections.observableArrayList();
//        Map<Pair<Vertex, Vertex>, Vertex> nodePairsMatching = new HashMap<>();
//
//        ObservableList<Vertex> gNodes = g.getNodes();
//        ObservableList<Vertex> hNodes = h.getNodes();
//
//        Graph product = new Graph();
//
//        for (Vertex u : gNodes) {
//            for (Vertex v : hNodes) {
//                Pair<Vertex, Vertex> uv = new Pair<>(u, v);
//                Vertex uvMatching = new Vertex('<' + u.getName() + ", " + v.getName() + '>');
//                nodePairs.add(uv);
//                nodePairsMatching.put(uv, uvMatching);
//                product.getNodes().add(uvMatching);
//            }
//        }
//
//        for (Pair<Vertex, Vertex> uv : nodePairs) {
//            for (Pair<Vertex, Vertex> u1v1 : nodePairs) {
//                if ((h.getArc(uv.getValue(), u1v1.getValue()) != null || h.getArc(u1v1.getValue(), uv.getValue()) != null)
//                        && (g.getArc(uv.getKey(), u1v1.getKey()) != null || g.getArc(u1v1.getKey(), uv.getKey()) != null)) {
//
//                    if (product.getArc(nodePairsMatching.get(u1v1), nodePairsMatching.get(uv)) == null) {
//                        product.getArcs().add(new Arc(nodePairsMatching.get(uv), nodePairsMatching.get(u1v1), false));
//                    }
//                }
//            }
//        }
//
//        return product;
//    }
//
//    public static Graph toTree(Graph g) {
//        ObservableList<Vertex> gNodes = g.getNodes();
//        Graph product = new Graph();
//
//        for(int i = 0; i < gNodes.size(); i++){
//            product.getNodes().add(gNodes.get(i));
//        }
//
//        ObservableList<Vertex> hNodes = product.getNodes();
//
//        for(int i = 0; i < hNodes.size(); i++){
//            if (i * 2 + 1 >= hNodes.size()) {
//                continue;
//            } else {
//                product.getArcs().add(new Arc(hNodes.get(i), hNodes.get(i * 2 + 1)));
//            }
//            if (i * 2 + 2 >= hNodes.size()){
//                continue;
//            } else {
//                product.getArcs().add(new Arc(hNodes.get(i), hNodes.get(i * 2 + 2)));
//            }
//        }
//        return product;
//    }
}