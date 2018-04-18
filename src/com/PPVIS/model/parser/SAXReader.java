package com.PPVIS.model.parser;

import com.PPVIS.model.Arc;
import com.PPVIS.model.Graph;
import com.PPVIS.model.Vertex;
import org.eclipse.swt.widgets.Canvas;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class SAXReader extends DefaultHandler {
    private Graph graph;
    private Canvas canvas;
    private Map<Long, Vertex> mapVertex = new HashMap();

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public Graph getGraph() {
        return graph;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("graph".equals(qName)) {
            graph = new Graph(attributes.getValue("name"), canvas);
            return;
        }
        if ("vertex".equals(qName)) {
            Vertex vertex = new Vertex(Integer.parseInt(attributes.getValue("x")), Integer.parseInt(attributes.getValue("y")), canvas);
            vertex.setName(attributes.getValue("name"));
            mapVertex.put(Long.parseLong(attributes.getValue("ID")), vertex);
            graph.addVertex(vertex);
            return;
        }
        if ("arc".equals(qName)) {
            Vertex outgoing = mapVertex.get(Long.parseLong(attributes.getValue("outgoing")));
            Vertex ingoing = mapVertex.get(Long.parseLong(attributes.getValue("ingoing")));
            if (outgoing != null && ingoing != null) {
                Arc arc = new Arc(outgoing, ingoing, canvas);
                arc.setWeight(Integer.parseInt(attributes.getValue("weight")));
                arc.setOriented(Boolean.parseBoolean(attributes.getValue("isOriented")));
                graph.addArc(arc);
            }
            return;
        }
    }

}
