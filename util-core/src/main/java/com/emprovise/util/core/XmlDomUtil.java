package com.emprovise.util.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML DOM Utility to parse the DOM Object and fetch the records.
 */
public class XmlDomUtil {

    private Document document;

    /**
     * Creates an instance of XmlDomUtil from the specified XML InputStream
     *
     * @param xmlStream The stream containing the XML Document as text.
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public XmlDomUtil(InputStream xmlStream) throws SAXException, IOException, ParserConfigurationException {

        // Fix for org.apache.xerces.impl.io.MalformedByteSequenceException: Invalid byte 1 of 1-byte UTF-8 sequence.
        Reader reader = new InputStreamReader(xmlStream, "UTF-8");
        InputSource inputSource = new InputSource(reader);
        inputSource.setEncoding("UTF-8");

        //get the factory
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        //Using factory get an instance of document builder
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        //parse using builder to get DOM representation of the XML file
        this.document = docBuilder.parse(inputSource);
    }

    /**
     * Creates an instance of XmlDomUtil from the specified XML Document String
     *
     * @param xmlDocument An XML Document String.
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public XmlDomUtil(String xmlDocument) throws SAXException, IOException, ParserConfigurationException {

        this(new ByteArrayInputStream(xmlDocument.getBytes()));
    }


    /**
     * Retrieves the value of the XML Element from the specified DOM Object
     *
     * @param tagName The name of the XML element in the DOM Object.
     * @return {@link String}
     * value of the element specified using the tag-name.
     */
    public String getElementValue(String tagName) {

        Element root = this.document.getDocumentElement();
        NodeList list = root.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }

    /**
     * Takes a XML element and the tag name, looks for the tag and gets
     * the text content i.e. for <item><rev>34</rev></item> xml snippet,
     * if the element points to item node and tagName is 'rev' I will return 34
     *
     * @param element
     * @param tagName The name of the XML element in the XML Document string.
     * @return {@link List}
     * unique values fetched from the XML element with the specified tag name.
     */
    public List<String> getElementValues(Element element, String tagName) {

        List<String> textValues = new ArrayList<String>();
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {

            for (int k = 0; k < nodeList.getLength(); k++) {
                Element el = (Element) nodeList.item(k);
                if (!textValues.contains(el.getFirstChild().getNodeValue())) {
                    textValues.add(el.getFirstChild().getNodeValue());
                }
            }
        }

        return textValues;
    }

    public <T> T getFirstElementValue(List<T> list) {
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Gets the Integer value for the specified tag name.
     * It calls the {@link #getFirstElementValue(List)}} and returns a integer value.
     *
     * @param element The root element to look from the DOM Object.
     * @param tagName The name of the XML element in the XML Document string.
     * @return {@link Integer}
     * value of the element specified using the tag-name.
     */
    public int getIntegerElementValue(Element element, String tagName) {

        String firstElementValue = getFirstElementValue(getElementValues(element, tagName));
        if (firstElementValue != null) {
            // Get first element only if this is an integer value.
            return Integer.parseInt(firstElementValue);
        } else {
            return 0;
        }
    }

    /**
     * Gets the Long value for the specified tag name.
     * It calls the {@link #getFirstElementValue(List)}  and returns a long value.
     *
     * @param element The root element to look from the DOM Object.
     * @param tagName The name of the XML element in the XML Document string.
     * @return {@link Long}
     * value of the element specified using the tag-name.
     */
    public long getLongElementValue(Element element, String tagName) {

        String firstElementValue = getFirstElementValue(getElementValues(element, tagName));
        if (firstElementValue != null) {
            // Get first element only if this is an long value.
            return Long.parseLong(firstElementValue);
        } else {
            return 0;
        }
    }

    public Document getDocument() {
        return document;
    }

    /**
     * Updates the XML Text Element tag value using the path expression.
     * The path expression is specified starting with root xml element,
     * going down each level to the final text element. Each xml element
     * is separated by a backslash '/', although no backslash is required
     * at the start of the root element or after the targeted text element.
     * For example: root/level1/level2/textnode
     * <p>
     * If the specified xml element is not present, it will be created and
     * updated with the specified value if its the targeted text element.
     *
     * @param pathExpression
     * @param value
     */
    public void updateElementValue(String pathExpression, String value) {

        String pathTag[] = pathExpression.split("/");

        //search nodes or create them if they do not exist
        Node node = this.document;

        for (int i = 0; i < pathTag.length; i++) {

            NodeList childNodes = node.getChildNodes();
            Node nodeFound = null;

            for (int j = 0; j < childNodes.getLength(); j++) {
                if (childNodes.item(j).getNodeName().equals(pathTag[i])) {
                    nodeFound = childNodes.item(j);
                    break;
                }
            }

            if (nodeFound == null) {
                nodeFound = document.createElement(pathTag[i]);
                node.appendChild(nodeFound);
                node.appendChild(document.createTextNode("\n"));
            }

            node = nodeFound;
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeType() == Node.TEXT_NODE) {
                // when text node exists override its value
                childNodes.item(i).setNodeValue(value);
                return;
            }
        }

        node.appendChild(document.createTextNode(value));
        //throw new RuntimeException("Unable to find any text tags inside tag " + node.getNodeName());
    }

    public String getElementByXpathExpression(String xpathExpression) throws XPathExpressionException {
        XPathExpression xp = XPathFactory.newInstance().newXPath().compile(xpathExpression);
        return xp.evaluate(document);
    }

    /**
     * Writes the Xml Document to the specified file.
     *
     * @param xmlFile {@link File} over which the Xml document is written.
     * @throws TransformerException
     */
    public void writeDocument(File xmlFile) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        DOMSource source = new DOMSource(this.document);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
    }
}
