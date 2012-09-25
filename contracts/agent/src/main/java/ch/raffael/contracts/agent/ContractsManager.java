package ch.raffael.contracts.agent;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.WeakHashMap;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import javassist.ClassPool;

import ch.raffael.util.contracts.ContractViolation;
import ch.raffael.util.contracts.NotNull;
import ch.raffael.util.contracts.internal.ContractsContext;
import ch.raffael.util.contracts.internal.Log;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("ConstantConditions")
public final class ContractsManager implements ContractsManagerMBean {

    private static final ContractsManager INSTANCE = new ContractsManager();

    private final Log log = Log.getInstance();
    private Instrumentation instrumentation = null;
    private final WeakHashMap<ClassLoader, ClassPool> classPools = new WeakHashMap<ClassLoader, ClassPool>();

    private ContractsManager() {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            server.registerMBean(this, ObjectName.getInstance(MBEAN_NAME));
            log.info("Registered MBean %s", MBEAN_NAME);
        }
        catch ( Exception e ) {
            log.error("Error registering MBean %s", e, MBEAN_NAME);
        }
    }

    public static void premain(String args, Instrumentation instrumentation) {
        getInstance().instrumentation = instrumentation;
        instrumentation.addTransformer(new Transformer());
    }

    public static ContractsManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void enable(@NotNull String name) {
        if ( name == null ) throw new NullPointerException("name");
        ContractsContext.getContext(name).enable();
    }

    @Override
    public void disable(@NotNull String name) {
        if ( name == null ) throw new NullPointerException("name");
        ContractsContext.getContext(name).disable();
    }

    @Override
    public void isEnabled(@NotNull String name) {
        if ( name == null ) throw new NullPointerException("name");
        ContractsContext.getContext(name).isEnabled();
    }

    public void reportViolation(@NotNull ContractsContext context, @NotNull ContractViolation violation) {
        if ( context == null ) throw new NullPointerException("context");
        if ( violation == null ) throw new NullPointerException("violation");
        // FIXME: not implemented
    }

    @NotNull
    ClassPool getClassPool(@NotNull ClassLoader loader) {
        return null;
    }

}
