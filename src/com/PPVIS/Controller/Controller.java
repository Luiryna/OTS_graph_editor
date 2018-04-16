package com.PPVIS.Controller;

import com.PPVIS.model.Graph;
import com.PPVIS.model.parser.WriterXML;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;

public class Controller {
    private static Controller controller;
    private WriterXML writerXML;

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
                writerXML.write();
                return true;
            } catch (TransformerException | ParserConfigurationException e) {
                return false;
            }
        }
        return false;
    }
}
