package ch.raffael.util.binding.validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultValidationResult implements ValidationResult {

    private Set<Message> messages = null;

    @Override
    public void add(Message message) {
        if ( messages == null ) {
            messages = Sets.newLinkedHashSet();
        }
        messages.add(message);
    }

    @Override
    public void addError(String message) {
        add(Message.error(message));
    }

    @Override
    public void addError(String message, Object details) {
        add(Message.error(message, details));
    }

    @Override
    public void addWarning(String message) {
        add(Message.warning(message));
    }

    @Override
    public void addWarning(String message, Object details) {
        add(Message.warning(message, details));
    }

    public Set<Message> getMessages() {
        if ( messages == null ) {
            return Collections.emptySet();
        }
        else {
            return Collections.unmodifiableSet(messages);
        }
    }

    @Nullable
    public ValidationEvent createEvent(@Nullable Set<Message> previousMessages, @NotNull Object source, @NotNull Object subject) {
        Set<Message> current;
        Set<Message> added;
        Set<Message> removed;
        if ( Objects.equal(messages, previousMessages) ) {
            return null;
        }
        current = getMessages();
        if ( current.isEmpty() ) {
            removed = previousMessages;
            added = Collections.emptySet();
        }
        else if ( previousMessages == null || previousMessages.isEmpty() ) {
            added = current;
            removed = Collections.emptySet();
        }
        else {
            added = Sets.difference(current, previousMessages);
            removed = Sets.difference(previousMessages, current);
        }
        return new ValidationEvent(source, subject, current, added, removed);
    }

    @Nullable
    public ValidationEvent createEvent(@Nullable DefaultValidationResult previousResult, @NotNull Object source, @NotNull Object subject) {
        return createEvent(previousResult == null ? null : previousResult.getMessages(), source, subject);
    }

    public void fireEvent(@NotNull ValidationListener target, @Nullable Set<Message> previousMessages, @NotNull Object source, @NotNull Object subject) {
        ValidationEvent event = createEvent(previousMessages, source, subject);
        if ( event != null ) {
            target.validationChanged(event);
        }
    }

    public void fireEvent(@NotNull ValidationListener target, @Nullable DefaultValidationResult previousResult, @NotNull Object source, @NotNull Object subject) {
        ValidationEvent event = createEvent(previousResult, source, subject);
        if ( event != null ) {
            target.validationChanged(event);
        }
    }

}
