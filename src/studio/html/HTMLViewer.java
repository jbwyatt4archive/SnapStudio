package studio.html;
import snap.view.*;

/**
 * A viewer for HTML documents.
 */
public class HTMLViewer extends ViewOwner {
    
    // The source
    Object       _src;
    
    // The document
    HTMLDoc      _doc;
    
    // The document box
    BoxView          _docBox = new BoxView();

/**
 * Creates a new HTMLViewer.
 */
public HTMLViewer(Object aSource)
{
    HTMLDoc doc = HTMLDoc.getDoc(aSource);
    setDoc(doc);
}

/**
 * Creates the UI.
 */
protected View createUI()  { return _docBox; }

/**
 * Returns the document.
 */
public HTMLDoc getDoc()  { return _doc; }

/**
 * Sets the document.
 */
public void setDoc(HTMLDoc aDoc)
{
    _docBox.setContent(_doc = aDoc);
}

/**
 * Standard main method.
 */
public static void main(String args[])
{
    HTMLDoc doc = new HTMLDoc("/Temp/ReportMill!/index2.html");
    HTMLViewer viewer = new HTMLViewer(doc);
    viewer.getWindow().setTitle(viewer.getDoc().getTitle());
    viewer.setWindowVisible(true);
}

}