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

package ch.raffael.util.i18n.impl;

import java.awt.Image;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.raffael.util.common.annotations.Singleton;
import ch.raffael.util.i18n.I18NException;
import ch.raffael.util.i18n.impl.handlers.ByteArrayHandler;
import ch.raffael.util.i18n.impl.handlers.IconHandler;
import ch.raffael.util.i18n.impl.handlers.ImageHandler;
import ch.raffael.util.i18n.impl.handlers.InputStreamHandler;
import ch.raffael.util.i18n.impl.handlers.KeyStrokeHandler;
import ch.raffael.util.i18n.impl.handlers.StringHandler;
import ch.raffael.util.i18n.impl.handlers.UrlHandler;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Singleton
public class HandlerManager {

    private final static HandlerManager INSTANCE = new HandlerManager();

    private final Map<Class<?>, Handler> handlers;

    private HandlerManager() {
        Map<Class<?>, Handler> h = new HashMap<Class<?>, Handler>();
        h.put(String.class, new StringHandler());
        h.put(Image.class, new ImageHandler());
        h.put(Icon.class, new IconHandler());
        h.put(ImageIcon.class, h.get(Icon.class));
        h.put(InputStream.class, new InputStreamHandler());
        h.put(byte[].class, new ByteArrayHandler());
        h.put(KeyStroke.class, new KeyStrokeHandler());
        h.put(URL.class, new UrlHandler());
        //h.put(Color.class, null);
        // FIXME: sound
        handlers = h; // JMM: assignment to final field guarantees visibility of the map's contents
    }

    public static HandlerManager getInstance() {
        return INSTANCE;
    }

    @Nullable
    public Handler getHandler(Class<?> type) {
        return handlers.get(type);
    }

    @NotNull
    public Handler requireHandler(Class<?> type) {
        Handler h = getHandler(type);
        if ( h == null ) {
            throw new I18NException("No handler for resource type " + type);
        }
        return h;
    }

}
