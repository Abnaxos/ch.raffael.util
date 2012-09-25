package ch.raffael.contracts.agent;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ContractsManagerMBean {

    String MBEAN_NAME = "ch.raffael.util.contracts:type=ContractsManager";

    public void enable(String name);
    public void disable(String name);

    public void isEnabled(String name);

}
