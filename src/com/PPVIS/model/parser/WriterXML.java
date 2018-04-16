package com.PPVIS.model.parser;

import com.PPVIS.model.Arc;
import com.PPVIS.model.Graph;
import com.PPVIS.model.Vertex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class WriterXML {
    private File file;
    private Graph graph;
    private Document document;

    public WriterXML(File file, Graph graph){
        this.file = file;
        this.graph = graph;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void write() throws ParserConfigurationException, TransformerException {
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element elementGraph = document.createElement("graph");
        elementGraph.setAttribute("name",graph.getName());

        Element elementVertices = document.createElement("vertices");

        for(Vertex vertex: graph.getVertices()){
            Element elementVertex = document.createElement("vertex");
            elementVertex.setAttribute("ID",String.valueOf(vertex.getID()));
            elementVertex.setAttribute("name", vertex.getName());
            elementVertex.setAttribute("x",String.valueOf(vertex.getX()));
            elementVertex.setAttribute("y",String.valueOf(vertex.getY()));

            elementVertices.appendChild(elementVertex);
        }

        Element elementArcs = document.createElement("arcs");

        for(Arc arc: graph.getArcs()){
            Element elementArc = document.createElement("arc");
            elementArc.setAttribute("ID",String.valueOf(arc.getID()));
            elementArc.setAttribute("weight", String.valueOf(arc.getWeight()));
            elementArc.setAttribute("outgoing",String.valueOf(arc.getOutgoing().getID()));
            elementArc.setAttribute("ingoing",String.valueOf(arc.getIngoing().getID()));
            elementArc.setAttribute("isOriented",String.valueOf(arc.isOriented()));

            elementArcs.appendChild(elementArc);
        }

        elementGraph.appendChild(elementVertices);
        elementGraph.appendChild(elementArcs);
        document.appendChild(elementGraph);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(file);
        transformer.transform(domSource, streamResult);
    }
}
