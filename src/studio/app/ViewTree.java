package studio.app;
import snap.gfx.Color;
import snap.view.*;

/**
 * An inspector to show ViewTree.
 */
public class ViewTree extends EditorPane.SupportPane {
    
    // The ViewTree
    TreeView       _viewTree;

/**
 * Creates a new ViewTree.
 */
public ViewTree(EditorPane anEP)  { super(anEP); }

/**
 * Returns the ViewTree.
 */
protected View createUI()
{
    TreeView tview = new TreeView(); tview.setName("ViewTree"); tview.setGrowHeight(true);
    tview.setResolver(new ViewTreeResolver()); tview.setBorder(Color.GRAY, 1);
    ScrollView scroll = new ScrollView(_viewTree = tview);
    return scroll;
}

/**
 * Initialize UI.
 */
protected void initUI()
{
    enableEvents(_viewTree, MouseRelease);
}

/**
 * Reset UI.
 */
protected void resetUI()
{
    _viewTree.setItems(getEditor().getContent());
    _viewTree.expandAll();
    _viewTree.setSelectedItem(getEditor().getSelectedOrSuperSelectedShape());
}

/**
 * Respond UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle ViewTree
    if(anEvent.equals("ViewTree") && anEvent.isActionEvent())
        getEditor().setSelectedShape((View)anEvent.getSelectedItem());
        
    // Handle MouseClick
    if(anEvent.isMouseClick() && anEvent.getClickCount()==2)
        getEditor().setSuperSelectedShape((View)anEvent.getSelectedItem());
}

/**
 * A resolver for Views.
 */
public class ViewTreeResolver extends TreeResolver <View> {
    
    /** Returns the parent of given item. */
    public View getParent(View anItem)  { return anItem!=getEditor().getContent()? anItem.getParent() : null; }

    /** Whether given object is a parent (has children). */
    public boolean isParent(View anItem)
    {
        if(!(anItem instanceof ParentView)) return false;
        if(anItem instanceof Label || anItem instanceof ButtonBase || anItem instanceof Spinner) return false;
        if(anItem instanceof ComboBox || anItem instanceof ListView) return false;
        return ((ParentView)anItem).getChildCount()>0;
    }

    /** Returns the children. */
    public View[] getChildren(View aParent)
    {
        ParentView par = (ParentView)aParent;
        if(par instanceof ScrollView) { ScrollView sp = (ScrollView)par;
            return sp.getContent()!=null? new View[] { sp.getContent() } : new View[0]; }
        return par.getChildren();
    }

    /** Returns the text to be used for given item. */
    public String getText(View anItem)
    {
        String str = anItem.getClass().getSimpleName();
        String name = anItem.getName(); if(name!=null) str += " - " + name;
        String text = anItem.getText(); if(text!=null) str += " \"" + text + "\" ";
        return str;
    }

    /** Return the image to be used for given item. */
    public View getGraphic(View anItem)  { return null; }
}

}