package ch.raffael.util.contracts.processor.expr;

import org.antlr.runtime.tree.TreeVisitorAction;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class CELVisitorAction implements TreeVisitorAction {

    @Override
    public Object pre(Object t) {
        return pre((CELTree)t);
    }

    @Override
    public Object post(Object t) {
        return post((CELTree)t);
    }

    protected abstract CELTree pre(CELTree tree);
    protected abstract CELTree post(CELTree tree);

}
