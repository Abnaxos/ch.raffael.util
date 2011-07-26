package ch.raffael.util.swing.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Holder for another component. Used in GUI designers to provide a container for custom
 * components or components, that will be created programmatically.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ComponentHolder extends JPanel {

    private String description;
    private JLabel placeholder;

    public ComponentHolder(String description) {
        this();
        setDescription(description);
    }

    public ComponentHolder() {
        super(new BorderLayout());
        setComponent(null);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        if ( placeholder != null ) {
            placeholder.setText(labelText(description));
        }
    }

    private static String labelText(String description) {
        if ( description == null ) {
            return "Component Placeholder";
        }
        else {
            return description;
        }
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        throw new UnsupportedOperationException("Use setComponent()");
    }

    public Component getComponent() {
        if ( getComponentCount() == 0 ) {
            return null;
        }
        else if ( getComponent(0) == placeholder ) {
            return null;
        }
        else {
            return getComponent(0);
        }
    }

    public void setComponent(Component component) {
        removeAll();
        if ( component == null ) {
            if ( placeholder == null ) {
                placeholder = new JLabel();
                placeholder.setOpaque(true);
                placeholder.setBackground(Color.ORANGE);
                placeholder.setText(labelText(description));
            }
            super.addImpl(placeholder, BorderLayout.CENTER, 0);
        }
        else {
            placeholder = null;
            super.addImpl(component, BorderLayout.CENTER, 0);
        }
    }

}
