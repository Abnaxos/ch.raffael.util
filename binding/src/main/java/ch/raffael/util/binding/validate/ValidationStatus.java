package ch.raffael.util.binding.validate;

import java.util.Set;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ValidationStatus {

    Set<Message> getMessages();

    Message.Severity getMaxSeverity();

    boolean containsSeverity(@NotNull Message.Severity severity);

}
