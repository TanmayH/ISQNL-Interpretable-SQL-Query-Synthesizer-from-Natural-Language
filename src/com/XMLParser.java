package com;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
This class parses the xml file(maps.xml) and loads the select items from the xml file against which  checks are made

INPUT: XML root word word(from instance.input=rootword);

OUTPUT: Set of alternate words from the xml file for that root word(instane.xmlparse())

*/


public class XMLParser {

    public String input;
    public List<String> xmlParser(){
        List<String> res=new ArrayList<String>();
        try {
            File inputFile = new File("resources/maps.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName(input);


            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    NodeList itemList = eElement.getElementsByTagName("item");

                    for (int count = 0; count < itemList.getLength(); count++) {
                        Node node1 = itemList.item(count);

                        if (node1.getNodeType() == node1.ELEMENT_NODE) {
                            Element item = (Element) node1;

                            res.add(item.getTextContent());
                        }
                    }
                }
            }


        }


        catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }



}
