package studio.app;
import snap.gfx.*;
import snap.view.*;
import snap.viewx.*;
import snap.web.*;

/**
 * Provides a WebPage version of EditorPane.
 */
public class EditorPage extends WebPage {

    // The editor pane
    EditorPane         _epane;

    // The editor pane
    static EditorPane  _epaneX;

/**
 * Override to wrap ReportPage in pane with EditButton.
 */
protected View createUI()
{
    // If EPaneUI available, return that
    if(_epaneX!=null) {
        _epane = _epaneX; _epaneX = null; //return epaneUI;
        ViewOwner mbarOwnr = _epane.getMenuBar();
        MenuBar mbar = mbarOwnr.getUI(MenuBar.class);
        mbarOwnr.getView("QuitMenuItem").setEnabled(false);
        BorderView bpane = new BorderView(); bpane.setTop(mbar); bpane.setCenter(_epane.getUI());
        bpane.addEventHandler(e -> handleKeyEvent(e), KeyPressed);
        return bpane;
    }
    
    // Otherwise, create normal UI and wrap inbox with edit button and text button
    Button ebtn = new Button(); ebtn.setText("Edit"); ebtn.setName("EditButton"); ebtn.setPrefWidth(100);
    Button tbtn = new Button(); tbtn.setText("Text"); tbtn.setName("TextButton"); tbtn.setPrefWidth(100);
    HBox hbox = new HBox(); hbox.setAlignment(Pos.CENTER_RIGHT); hbox.setPadding(3,3,3,3);
    hbox.setSpacing(5); hbox.addChild(ebtn); hbox.addChild(tbtn);
    View superUI = super.createUI();
    BorderView bpane = new BorderView(); bpane.setTop(hbox); bpane.setCenter(superUI);
    return bpane;
}

/**
 * Respond to UI.
 */
protected void respondUI(ViewEvent anEvent)
{
    // Handle EditButton
    if(anEvent.equals("EditButton")) {
        _epaneX = new PageEditorPane().open(getFile());
        getBrowser().reloadPage();
    }
    
    // Handle TextButton
    if(anEvent.equals("TextButton")) {
        WebFile file = getFile(); WebURL url = file.getURL();
        WebPage page = new TextPage(); page.setFile(file);
        WebBrowser browser = getBrowser(); browser.setPage(url, page);
        browser.setURL(file.getURL());
    }
}

/**
 * Called when BorderView around editor pane gets KeyPressed.
 */
private void handleKeyEvent(ViewEvent anEvent)
{
    if(anEvent.isKeyPressed() && anEvent.isShortcutDown()) {
        EditorPaneMenuBar mbar = _epane.getMenuBar();
        mbar.getUI().fireEvent(anEvent);
    }
}

/**
 * A custom editor pane for ReportEditor.
 */
public class PageEditorPane extends EditorPane {

    /** Called when editor is closed. */
    protected void editorClosed()
    {
        // If another open editor is available focus on it, otherwise run WelcomePanel
        EditorPane epane = WindowView.getOpenWindowOwner(EditorPane.class);
        if(epane!=null)
            epane.getEditor().requestFocus();
        getBrowser().reloadFile(getFile());
    }
}

/**
 * Creates a new file for use with showNewFilePanel method.
 */
protected WebFile createNewFile(String aPath)
{
    WebFile file = super.createNewFile(aPath);
    //file.setBytes(new RMDocument(792,612).getBytes());
    return file;
}

}
