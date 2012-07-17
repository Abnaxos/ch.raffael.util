package ch.raffael.util.contracts.processor.expr;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CELTreeAdaptor extends CommonTreeAdaptor {

    private Class<?> javaType;

    @Override
    public Object create(Token payload) {
        return new CELTree(payload);
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }
}
