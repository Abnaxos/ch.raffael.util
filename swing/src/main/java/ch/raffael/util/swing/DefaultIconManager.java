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

package ch.raffael.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultIconManager implements IconManager {

    public static final Icon MISSING_ICON = new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setColor(Color.BLACK);
            g2d.drawLine(x, y, x + 15, y + 15);
            g2d.drawLine(x, y + 15, y + 15, x);
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    };
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final Map<Object, Icon> cache = new HashMap<Object, Icon>();
    
    @NotNull
    @Override
    public synchronized Icon get(@NotNull Class<?> clazz, @NotNull String resource) {
        Key key = new Key(clazz, resource);
        Icon icon = get(key);
        if ( icon == null ) {
            try {
                URL url = clazz.getResource(resource);
                if ( url == null ) {
                    icon = MISSING_ICON;
                }
                else {
                    icon = new ImageIcon(url);
                }
            }
            catch ( Exception e ) {
                if ( logger.isLoggable(Level.WARNING) ) {
                    logger.log(Level.WARNING, "Error loading icon '" + resource + "' for class " + clazz, e);
                }
                icon = MISSING_ICON;
            }
            put(key, icon);
        }
        return icon;
    }

    @Override
    public synchronized Icon get(@NotNull Object key) {
        return cache.get(key);
    }

    @Override
    public synchronized void put(@NotNull Object key, @NotNull Icon icon) {
        cache.put(key, icon);
    }

    private final class Key {
        private final Class<?> clazz;
        private final String resource;
        private Key(Class<?> clazz, String resource) {
            this.clazz = clazz;
            this.resource = resource;
        }
        @Override
        public boolean equals(Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            Key that = (Key)o;
            if ( !clazz.equals(that.clazz) ) {
                return false;
            }
            return resource.equals(that.resource);
        }
        @Override
        public int hashCode() {
            int result = clazz.hashCode();
            result = 31 * result + resource.hashCode();
            return result;
        }
    }
    
}
