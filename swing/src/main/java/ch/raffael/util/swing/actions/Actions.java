package ch.raffael.util.swing.actions;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Actions {

    private Actions() {
    }

    public static void bind(Action action, ActionMap actionMap, InputMap inputMap) {
        bind(action, defaultKey(action), actionMap, inputMap);
    }

    public static void bind(Action action, Object actionKey, InputMap inputMap) {
        bind(action, actionKey, null, inputMap);
    }

    public static void bind(Action action, Object actionKey, JComponent component, int condition) {
        bind(action, actionKey, component.getActionMap(), component.getInputMap(condition));
    }

    public static void bind(Action action, JComponent component, int condition) {
        bind(action, defaultKey(action), component.getActionMap(), component.getInputMap(condition));
    }

    public static void bindWhenAncestor(Action action, Object actionKey, JComponent component) {
        bind(action, actionKey, component, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void bindWhenAncestor(Action action, JComponent component) {
        bind(action, defaultKey(action), component, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void bindWhenFocused(Action action, Object actionKey, JComponent component) {
        bind(action, actionKey, component, JComponent.WHEN_FOCUSED);
    }

    public static void bindWhenFocused(Action action, JComponent component) {
        bind(action, defaultKey(action), component, JComponent.WHEN_FOCUSED);
    }

    public static void bindWhenWindowFocused(Action action, Object actionKey, JComponent component) {
        bind(action, actionKey, component, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public static void bindWhenWindowFocused(Action action, JComponent component) {
        bind(action, defaultKey(action), component, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public static void bind(Action action, Object actionKey, ActionMap actionMap, InputMap inputMap) {
        if ( actionMap != null ) {
            actionMap.put(actionKey, action);
        }
        Object key = action.getValue(Action.ACCELERATOR_KEY);
        if ( key instanceof KeyStroke ) {
            inputMap.put((KeyStroke)key, actionKey);
        }
    }

    @SuppressWarnings( { "UnusedParameters" })
    private static Object defaultKey(Action action) {
        return new Object();
    }

}
