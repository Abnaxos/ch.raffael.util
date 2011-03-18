package ch.raffael.util.swing.binding;

import java.awt.Component;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.Icon;

import com.google.common.collect.Maps;

import ch.raffael.util.binding.PresentationModel;
import ch.raffael.util.binding.validate.Message;
import ch.raffael.util.binding.validate.ValidationEvent;
import ch.raffael.util.binding.validate.ValidationListener;
import ch.raffael.util.common.UnreachableCodeException;
import ch.raffael.util.i18n.I18N;
import ch.raffael.util.swing.SwingUtil;
import ch.raffael.util.swing.components.feedback.FeedbackPanel;
import ch.raffael.util.swing.components.feedback.IconTextFeedback;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ValidationFeedbackManager implements ValidationListener {

    private final BindingResources res = I18N.getBundle(BindingResources.class);

    private final Map<Component, FeedbackEntry> feedbacks = new WeakHashMap<Component, FeedbackEntry>();

    @Override
    public void validationChanged(ValidationEvent event) {
        if ( !(event.getSubject() instanceof Component) ) {
            return;
        }
        Component component = (Component)event.getSubject();
        FeedbackPanel feedbackPanel = SwingUtil.findComponent(component, FeedbackPanel.class);
        if ( feedbackPanel == null ) {
            return;
        }
        FeedbackEntry entry = feedbacks.get(component);
        if ( entry == null ) {
            entry = new FeedbackEntry(component);
            feedbacks.put(component, entry);
        }
        if ( entry.currentMessage != null ) {
            for ( Message message : event.getRemovedMessages() ) {
                if ( entry.currentMessage.equals(message) ) {
                    entry.update(null);
                }
            }
        }
        for ( Message message : event.getMessages() ) {
            if ( entry.currentMessage == null ) {
                entry.update(message);
            }
            else if ( entry.currentMessage.getSeverity().compareTo(message.getSeverity()) > 0 ) {
                entry.update(message);
            }
        }
        Component feedback = entry.getFeedback();
        if ( feedback!=null && feedback.getParent() == null ) {
            feedbackPanel.add(feedback, component);
            feedbackPanel.revalidate();
            feedbackPanel.repaint();
        }
    }

    private class FeedbackEntry {
        private Component feedback;
        private Message currentMessage;

        private FeedbackEntry(Component subject) {
        }

        private void update(Message msg) {
            if ( msg == null ) {
                removeFeedback();
                currentMessage = null;
            }
            else {
                if ( currentMessage == null || currentMessage.getSeverity().compareTo(msg.getSeverity()) > 0 ) {
                    removeFeedback();
                    currentMessage = msg;
                }
            }
        }

        private Component getFeedback() {
            if ( currentMessage == null ) {
                removeFeedback();
            }
            else if ( feedback == null ) {
                Icon icon;
                switch ( currentMessage.getSeverity() ) {
                    case WARNING:
                        icon = res.warningFeedbackIcon();
                        break;
                    case ERROR:
                        icon = res.errorFeedbackIcon();
                        break;
                    default:
                        throw new UnreachableCodeException();
                }
                feedback = new IconTextFeedback(icon, currentMessage.getMessage());
            }
            return feedback;
        }

        private void removeFeedback() {
            if ( feedback != null ) {
                FeedbackPanel panel = SwingUtil.requireComponent(feedback, FeedbackPanel.class);
                panel.remove(feedback);
                panel.revalidate();
                panel.repaint();
            }
            feedback = null;
        }

    }

}
