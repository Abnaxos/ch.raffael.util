package ch.raffael.util.cli;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface CommandDispatcher {

    CommandDescriptor findCommand(String command);

    Iterable<CommandDescriptor> listCommands();

}
