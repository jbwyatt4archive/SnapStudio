package studio.html;
import snap.view.ViewLayout;

/**
 * A HTMLElement for HTML body.
 */
public class HTMLBody extends HTMLElement {

/**
 * Creates the layout.
 */
protected ViewLayout createLayout()  { return new ViewLayout.VBoxLayout(this); }

}