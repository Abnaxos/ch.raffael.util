package ch.raffael.util.binding.validate;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.beans.Event;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ValidationEvent extends Event<Object> {

    private final Object subject;
    private final Set<Message> messages;
    private final Set<Message> addedMessages;
    private final Set<Message> removedMessages;

    public ValidationEvent(@NotNull Object source, Object subject, Set<Message> messages, Set<Message> addedMessages, Set<Message> removedMessages) {
        super(source);
        this.addedMessages = addedMessages;
        this.messages = messages;
        this.removedMessages = removedMessages;
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public Set<Message> getAddedMessages() {
        return addedMessages;
    }

    public Set<Message> getRemovedMessages() {
        return removedMessages;
    }
}
