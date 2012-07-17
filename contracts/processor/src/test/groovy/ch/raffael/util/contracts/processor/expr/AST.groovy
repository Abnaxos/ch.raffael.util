package ch.raffael.util.contracts.processor.expr

import org.antlr.runtime.Token

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class AST {

    static final CELTree NONE = new CELTree(Token.INVALID_TOKEN)

    CELTree root
    @Delegate CELTree tree

    private stack = []

    def AST(CELTree tree) {
        this.tree = tree
        root = tree
    }

    CELTree getAt(int index) {
        CELTreeExtensions.getAt(tree, index)
    }

    AST down(int... indexes) {
        for ( i in indexes ) {
            stack.push(tree)
            tree = getAt(i)
        }
        if ( tree.type == 0 ) {
            null
        }
        else {
            this
        }
    }

    AST up(int count=1) {
        if ( count <= 0 ) {
            this
        }
        else if ( up(count - 1) && stack ) {
            tree = stack.pop()
            this
        }
        else {
            null
        }
    }

    List getTok() {
        CELTreeExtensions.getTok(tree)
    }

}
