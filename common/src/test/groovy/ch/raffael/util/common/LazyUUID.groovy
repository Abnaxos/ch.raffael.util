package ch.raffael.util.common

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class LazyUUID extends Lazy<UUID> {

    LazyUUID(boolean serializeValue) {
        super(serializeValue)
    }

    @Override
    protected UUID createInstance() {
        return UUID.randomUUID()
    }
}
