package ch.raffael.util.contracts.processor.expr

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Category(CELTree)
class CELTreeExtensions {

    static CELTree getAt(CELTree self, int index) {
        if ( self.children == null || index >= self.children.size() ) {
            AST.NONE
        }
        else {
            self.children[index]
        }
    }

    static List getTok(CELTree self) {
        [ self.type, self.text ]
    }
}
