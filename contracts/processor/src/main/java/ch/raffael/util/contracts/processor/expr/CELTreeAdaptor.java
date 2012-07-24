package ch.raffael.util.contracts.processor.expr;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
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

    @Override
    public Object errorNode(TokenStream input, Token start, Token stop, RecognitionException e) {
        // FIXME: Not implemented
        return null;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }
}
