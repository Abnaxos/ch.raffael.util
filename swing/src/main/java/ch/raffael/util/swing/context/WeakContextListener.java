/*
 * Copyright 2010 Raffael Herzog
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

package ch.raffael.util.swing.context;

import java.lang.ref.WeakReference;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class WeakContextListener implements ContextListener {

    private final WeakReference<ContextListener> listener;

    public WeakContextListener(ContextListener listener) {
        this.listener = new WeakReference<ContextListener>(listener);
    }

    @Override
    public void contextChanged(ContextEvent event) {
        ContextListener listener = this.listener.get();
        if ( listener == null ) {
            event.getSource().removeContextListener(this);
        }
        else {
            listener.contextChanged(event);
        }
    }
}
