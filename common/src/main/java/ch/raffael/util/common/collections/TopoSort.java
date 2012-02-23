/*
 * Copyright 2012 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.raffael.util.common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TopoSort<T> {

    private final Set<T> nodes;
    private final Set<Arc<T>> arcs = new HashSet<Arc<T>>();

    public TopoSort() {
        nodes = new LinkedHashSet<T>();
    }

    public TopoSort(@NotNull Collection<T> contents) {
        nodes = new LinkedHashSet<T>(contents);
    }

    public void add(@NotNull T left, @NotNull T right) {
        arcs.add(new Arc<T>(left, right));
        nodes.add(left);
        nodes.add(right);
    }

    public void add(@NotNull T node) {
        nodes.add(node);
    }

    public void addAll(@NotNull Collection<T> node) {
        nodes.addAll(node);
    }

    @NotNull
    public List<T> sort() throws CircularGraphException {
        return sort(new ArrayList<T>(nodes.size()));
    }

    @NotNull
    public List<T> sort(@NotNull List<T> target) throws CircularGraphException {
        while ( !nodes.isEmpty() ) {
            T node = findFreeNode();
            target.add(node);
            removeOutgoingArcs(node);
        }
        return target;
    }

    /**
     * Find the first node that has no incoming arcs.
     * @return The first free node in the list.
     * @throws CircularGraphException If there's no free node.
     */
    private T findFreeNode() throws CircularGraphException {
        Iterator<T> iter = nodes.iterator();
        nextNode:
        while ( iter.hasNext() ) {
            T node = iter.next();
            for ( Arc arc : arcs ) {
                if ( arc.right.equals(node) ) {
                    continue nextNode;
                }
            }
            // yay, we've found a node
            iter.remove();
            return node;
        }
        // no free node found, the graph is circular
        throw new CircularGraphException(); // TODO: give a list of nodes
    }

    private void removeOutgoingArcs(T node) {
        Iterator<Arc<T>> iter = arcs.iterator();
        while ( iter.hasNext() ) {
            Arc<T> arc = iter.next();
            if ( arc.left.equals(node) ) {
                iter.remove();
            }
        }
    }

    public static class CircularGraphException extends Exception {

        public CircularGraphException() {
        }

        public CircularGraphException(String message) {
            super(message);
        }

        public CircularGraphException(String message, Throwable cause) {
            super(message, cause);
        }

        public CircularGraphException(Throwable cause) {
            super(cause);
        }
    }

    protected static class Arc<T> {
        private final T left;
        private final T right;
        protected Arc(T left, T right) {
            this.left = left;
            this.right = right;
        }
        @Override
        public String toString() {
            return left + " => " + right;
        }
        @Override
        public boolean equals(Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            Arc that = (Arc)o;
            if ( !left.equals(that.left) ) {
                return false;
            }
            return right.equals(that.right);
        }
        @Override
        public int hashCode() {
            int result = left.hashCode();
            result = 31 * result + right.hashCode();
            return result;
        }
        protected T getLeft() {
            return left;
        }
        protected T getRight() {
            return right;
        }
    }

}
