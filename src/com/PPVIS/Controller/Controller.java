package com.PPVIS.Controller;

import com.PPVIS.model.Graph;
import com.PPVIS.model.parser.SAXReader;
import com.PPVIS.model.parser.WriterXML;
import org.eclipse.swt.widgets.Canvas;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

public class Controller {
    private static Controller controller;
    private WriterXML writerXML;
    private SAXReader saxReader;

    public static synchronized Controller getInstance(){
        if(controller==null)
            controller= new Controller();
        return controller;
    }

    public boolean save(File file, Graph graph){
        if (writerXML==null)
            writerXML = new WriterXML(file,graph);
        if(file!=null && graph!=null){
            try {
                writerXML.setFile(file);
                writerXML.setGraph(graph);
                String name = file.getName().substring(0, file.getName().indexOf('.'));
                if(!name.equals(graph.getName()))
                    graph.setName(name);
                writerXML.write();
                return true;
            } catch (TransformerException | ParserConfigurationException e) {
                return false;
            }
        }
        return false;
    }

    public Graph open(File file, Canvas canvas){
        if (saxReader == null) saxReader = new SAXReader();
        try {
            saxReader.setCanvas(canvas);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(file, saxReader);
            canvas.redraw();
            return saxReader.getGraph();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
