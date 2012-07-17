package ch.raffael.contracts.agent;

import java.lang.instrument.Instrumentation;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class ContractsManager implements ContractsManagerMBean {

    private static final ContractsManager INSTANCE = new ContractsManager();

    private Instrumentation instrumentation = null;

    public static void premain(String args, Instrumentation instrumentation) {
        getInstance().instrumentation = instrumentation;
    }

    public static ContractsManager getInstance() {
        return INSTANCE;
    }

}
