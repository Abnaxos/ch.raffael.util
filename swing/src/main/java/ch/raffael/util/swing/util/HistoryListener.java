package ch.raffael.util.swing.util;

import java.util.EventListener;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface HistoryListener extends EventListener {

    void historyChanged(HistoryEvent event);

}
