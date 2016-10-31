package studio.app;
import java.util.List;
import snap.view.*;

/**
 * An inspector for general shape attributes, like property keys, name, text wrap around, etc.
 */
public class ShapeGeneral extends EditorPane.SupportPane {
    
    // The bindings table
    TableView <String>  _bindingsTable;

/**
 * Creates a new ShapeGeneral pane.
 */
public ShapeGeneral(EditorPane anEP)  { super(anEP); }

/**
 * Initialize UI panel for this inspector.
 */
protected void initUI()
{
    // Get bindings table
    _bindingsTable = getView("BindingsTable", TableView.class); _bindingsTable.setRowHeight(18);
    _bindingsTable.setCellConfigure(this :: configureBindingsTable);
    enableEvents(_bindingsTable, MouseRelease, DragDrop);
}

/**
 * Updates UI controsl from current selection.
 */
public void resetUI()
{
    // Get currently selected shape
    View shape = getSelectedShape();

    // Reset table model shape
    _bindingsTable.setItems(new String[] { "X", "Y", "Width", "Height" }); //shape.getPropNames());
    if(_bindingsTable.getSelectedIndex()<0) _bindingsTable.setSelectedIndex(0);
    _bindingsTable.updateItems();
    
    // Reset BindingsText
    String pname = _bindingsTable.getSelectedItem();
    Binding binding = shape.getBinding(pname);
    setViewValue("BindingsText", binding!=null? binding.getKey() : null);
}

/**
 * Updates current selection from UI controls.
 */
public void respondUI(ViewEvent anEvent)
{
    // Get the current editor and selected shape (just return if null) and selected shapes
    View shape = getSelectedShape(); if(shape==null) return;
    List <View> shapes = getEditor().getSelectedOrSuperSelectedShapes();
    
    // Handle BindingsTable
    if(anEvent.equals("BindingsTable")) {
        
        // Handle MouseClick
        if(anEvent.getClickCount()==2)
            requestFocus(getView("BindingsText"));
            
        // Handle DragDrop
        if(anEvent.isDragDrop()) {
            Dragboard dboard = anEvent.getDragboard(); anEvent.acceptDrag();
            if(dboard.hasString()) {
                int row = _bindingsTable.getRowAt(anEvent.getX(), anEvent.getY()); if(row<0) return;
                //String pname = shape.getPropNames()[row];
                //String bkey = KeysPanel.getDragKey();
                //shape.addBinding(pname, bkey);
            }
            anEvent.dropComplete();
        }
    }
    
    // Handle BindingsText
    if(anEvent.equals("BindingsText")) {
        
        // Get selected PropertyName and Key
        String pname = _bindingsTable.getSelectedItem(); if(pname==null) return;
        String key = getViewStringValue("BindingsText"); if(key!=null && key.length()==0) key = null;
        
        // Remove previous binding and add new one (if valid)
        for(View shp : shapes)
            if(key!=null) shp.addBinding(pname, key);
            else shp.removeBinding(pname);
    }
}

/**
 * Returns the current selected shape for the current editor.
 */
public View getSelectedShape()
{
    Editor e = getEditor(); if(e==null) return null;
    return e.getSelectedOrSuperSelectedShape();
}

/**
 * Called to configure BindingsTable.
 */
private void configureBindingsTable(ListCell <String> aCell)
{
    if(aCell.getCol()==0) return;
    String pname = aCell.getItem(); if(pname==null) return;
    View shape = getSelectedShape(); if(shape==null) return;
    Binding binding = getSelectedShape().getBinding(pname);
    aCell.setText(binding!=null? binding.getKey() : null);
}

/**
 * Returns the name to be used in the inspector's window title.
 */
public String getWindowTitle()  { return "General Inspector"; }

}