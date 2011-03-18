package ch.raffael.util.swing.binding;

import javax.swing.Icon;

import ch.raffael.util.i18n.Default;
import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface BindingResources extends ResourceBundle {

    @Default("feedback-error.png")
    Icon errorFeedbackIcon();
    @Default("feedback-warning.png")
    Icon warningFeedbackIcon();

}
