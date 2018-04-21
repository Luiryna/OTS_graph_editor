package com.PPVIS.algoritm;

import com.PPVIS.model.Arc;
import com.PPVIS.model.Graph;
import com.PPVIS.model.Vertex;

import java.util.*;

public class Algorithm {

    public static Map<Vertex, Integer> findDistance(Graph graph, Vertex start) {
        Map<Vertex, Integer> mapDistance = new HashMap<>();
        List<Vertex> nonUsedVertex = new ArrayList<>(graph.getVertices());
        Vertex minVertex = start;
        for (Vertex vertex : graph.getVertices()) {
            mapDistance.put(vertex, 2147483647);
        }
        mapDistance.replace(start, 0);
        nonUsedVertex.remove(start);
        while (true) {
            Vertex minVertStep = null;
            for (Arc arc : minVertex.getOutgoing()) {
                if (mapDistance.get(arc.getIngoing()) > mapDistance.get(minVertex) + arc.getWeight()) {
                    mapDistance.replace(arc.getIngoing(), mapDistance.get(minVertex) + arc.getWeight());
                }
            }
            for (Arc arc : minVertex.getIngoing()) {
                if (!arc.isOriented()) {
                    if (mapDistance.get(arc.getOutgoing()) > mapDistance.get(minVertex) + arc.getWeight()) {
                        mapDistance.replace(arc.getOutgoing(), mapDistance.get(minVertex) + arc.getWeight());
                    }
                }
            }
            for (Map.Entry<Vertex, Integer> entry : mapDistance.entrySet()) {
                if (minVertStep == null) {
                    if (nonUsedVertex.contains(entry.getKey()))
                        minVertStep = entry.getKey();
                } else {
                    Vertex vertex = entry.getKey();
                    if (nonUsedVertex.contains(vertex) && mapDistance.get(minVertStep) > mapDistance.get(vertex))
                        minVertStep = vertex;
                }
            }
            if (minVertStep != null) {
                minVertex = minVertStep;
                nonUsedVertex.remove(minVertex);
            } else break;
        }
        return mapDistance;
    }
}