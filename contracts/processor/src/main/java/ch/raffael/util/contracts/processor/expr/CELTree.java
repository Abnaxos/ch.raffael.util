package ch.raffael.util.contracts.processor.expr;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CELTree extends CommonTree {

    private Class<?> javaType;
    private Object data;

    public CELTree() {
        super();
    }

    public CELTree(CELTree node) {
        super(node);
    }

    public CELTree(Token t) {
        super(t);
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public Tree dupNode() {
        return new CELTree(this);
    }
}
