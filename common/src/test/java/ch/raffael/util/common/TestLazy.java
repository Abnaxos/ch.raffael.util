package ch.raffael.util.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.testng.annotations.*;

import static org.testng.Assert.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestLazy {

    @Test
    public void testSerializeTransient() throws Exception {
        LazyUUID lazy = new LazyUUID(false);
        UUID uuid = lazy.get();
        lazy = writeRead(lazy);
        assertFalse(uuid.equals(lazy.get()), "UUID has been serialized");
    }

    @Test
    public void testSerializeNonTransient() throws Exception {
        LazyUUID lazy = new LazyUUID(true);
        UUID uuid = lazy.get();
        lazy = writeRead(lazy);
        assertEquals(lazy.get(), uuid, "UUID doesn't match");
    }

    private LazyUUID writeRead(LazyUUID lazy) throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(lazy);
        objOut.close();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
        return (LazyUUID)in.readObject();
    }
    
    private static class LazyUUID extends Lazy<UUID> {
        private LazyUUID(boolean transientInstance) {
            super(transientInstance);
        }
        @Override
        protected UUID createInstance() {
            return UUID.randomUUID();
        }
    }
    
}
