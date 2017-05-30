/*
 * Copyright (c) 2010, ReportMill Software. All rights reserved.
 */
package studio.html;
import snap.parse.*;
import snap.util.*;

/**
 * A class to load an RXElement from aSource.
 */
public class HTMLParser extends Parser {
    
/**
 * Creates a new HTMLParser.
 */
public HTMLParser()
{
    // Install handlers: ParseUtils.installHandlers(getClass(), getRule());
    getRule("Document").setHandler(new DocumentHandler());
    getRule("Prolog").setHandler(new PrologHandler());
    getRule("Element").setHandler(new ElementHandler());
    getRule("Attribute").setHandler(new AttributeHandler());
}
    
/**
 * Kicks off xml parsing from given source and builds on this parser's element.
 */
public XMLElement parseXML(Object aSource) throws Exception
{
    String str = SnapUtils.getText(aSource);
    //WebURL url = WebURL.getURL(aSource);
    //String str = url!=null? url.getText() : null;
    //if(str==null && aSource instanceof byte[]) str = new String((byte[])aSource);
    return (XMLElement)parse(str).getCustomNode();
}

/**
 * Override to return XMLTokenizer.
 */
protected Tokenizer createTokenizerImpl()  { return new HTMLTokenizer(); }

/**
 * Document Handler.
 */
public static class DocumentHandler extends ParseHandler <XMLElement> {
    
    /** Returns the part class. */
    protected Class <XMLElement> getPartClass()  { return XMLElement.class; }

    /** ParseHandler method. */
    public void parsedOne(ParseNode aNode, String anId)
    {
        // Handle Element
        if(anId=="Element")
            _part = (XMLElement)aNode.getCustomNode();
    }
}

/**
 * Prolog Handler.
 */
public static class PrologHandler extends ParseHandler <XMLElement> {
    
    /** Returns the part class. */
    protected Class <XMLElement> getPartClass()  { return XMLElement.class; }

    /** ParseHandler method. */
    public void parsedOne(ParseNode aNode, String anId)
    {
        // Handle Attribute
        if(anId=="Attribute")
            getPart().addAttribute((XMLAttribute)aNode.getCustomNode());
    }
}

/**
 * Element Handler.
 */
public static class ElementHandler extends ParseHandler <XMLElement> {
    
    // Whether element has checked content
    boolean   _checkedContent;
    
    /** Returns the part class. */
    protected Class <XMLElement> getPartClass()  { return XMLElement.class; }

    /** ParseHandler method. */
    public void parsedOne(ParseNode aNode, String anId)
    {
        // Handle Name
        if(anId=="Name") {
            if(_part==null) { _part = new XMLElement(aNode.getString()); _checkedContent = false; }
            else if(!_part.getName().equals(aNode.getString())) {
                System.err.println("XMLParser: Expected closing tag " + _part.getName());
                throw new ParseException(aNode.getParser(), aNode.getRule());
            }
        }
            
        // Handle Attribute
        else if(anId=="Attribute")
            _part.addAttribute((XMLAttribute)aNode.getCustomNode());
            
        // Handle Element
        else if(anId=="Element") {
            _part.addElement((XMLElement)aNode.getCustomNode());
            
            // Read more content
            XMLElement htext = getHTMLText(aNode);
            if(htext!=null) _part.addElement(htext);
        }
            
        // Handle close: On first close, check for content
        else if(anId==">" && !_checkedContent) {
            XMLElement htext = getHTMLText(aNode);
            if(htext!=null) _part.addElement(htext);
        }
    }
    
    /** Returns an XML element for extra text. */
    XMLElement getHTMLText(ParseNode aNode)
    {
        HTMLTokenizer xt = (HTMLTokenizer)aNode.getParser().getTokenizer();
        String content = xt.getContent(); if(content==null) return null;
        content = content.trim(); if(content.length()==0) return null;
        XMLElement txml = new XMLElement("html_text"); txml.setValue(content);
        return txml;
    }
}

/**
 * Attribute Handler.
 */
public static class AttributeHandler extends ParseHandler <XMLAttribute> {
    
    // The attribute name
    String _name;
    
    /** Returns the part class. */
    protected Class <XMLAttribute> getPartClass()  { return XMLAttribute.class; }

    /** ParseHandler method. */
    public void parsedOne(ParseNode aNode, String anId)
    {
        // Handle Name
        if(anId=="Name")
            _name = aNode.getString();
            
        // Handle String
        else if(anId=="String") { String str = aNode.getString(); str = str.substring(1, str.length()-1);
            str = decodeXMLString(str);
            _part = new XMLAttribute(_name, str);
        }
    }
}

/** Converts an XML string to plain. This implementation is a bit bogus. */
private static String decodeXMLString(String aStr)
{
    // If no entity refs, just return
    if(aStr.indexOf('&')<0) return aStr;
    
    // Do common entity ref replacements
    aStr = aStr.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">");
    aStr = aStr.replace("&quot;", "\"").replace("&apos;", "'");
    
    // Iterate over string to find numeric/hex references and replace with char
    for(int start=aStr.indexOf("&#"); start>=0;start=aStr.indexOf("&#",start)) {
        int end = aStr.indexOf(";", start); if(end<0) continue;
        String str0 = aStr.substring(start, end+1), str1 = str0.substring(2,str0.length()-1);
        int val = Integer.valueOf(str1); String str2 = String.valueOf((char)val);
        aStr = aStr.replace(str0, str2);
    }
    
    // Return string
    return aStr;
}

/**
 * A Tokenizer subclass to read HTML contents.
 */
private static class HTMLTokenizer extends Tokenizer {
    
    /** Called to return the value of an element and update the char index. */
    protected String getContent()
    {
        // Mark content start and skip to next element-start char
        int start = _charIndex;
        while(!isNext("<") && _charIndex<length())
            _charIndex++;
        
        // Handle CDATA: Gobble until close and return string
        if(isNext("<![CDATA[")) {
            _charIndex += "<![CDATA[".length(); if(Character.isWhitespace(_charIndex)) _charIndex++;
            start = _charIndex;
            while(!isNext("]]>")) _charIndex++;
            String str = getInput().subSequence(start, _charIndex).toString();
            _charIndex += "]]>".length();
            return str;
        }
        
        // If next char isn't close tag, return null (assumes we hit child element instead of text content)
        if(!isNext("</"))
            return null;
        
        // Return string for content
        String str = getInput().subSequence(start, _charIndex).toString();
        return decodeXMLString(str);
    }

    /** Returns whether the given string is up next. */
    public boolean isNext(String aStr)
    {
        if(_charIndex+aStr.length()>length()) return false;
        for(int i=0,iMax=aStr.length();i<iMax;i++)
            if(charAt(_charIndex+i)!=aStr.charAt(i))
                return false;
        return true;
    }
}

/**
 * Test.
 */
public static void main(String args[]) throws Exception
{
    XMLParser parser = new XMLParser();
    XMLElement xml = parser.parseXML("/Temp/SnapCode/src/snap/app/AppPane.snp");
    System.err.println(xml);
}

}