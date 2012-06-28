/*
 * Copyright 2012 Piratenpartei Schweiz
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
package ch.raffael.util.common;

import java.io.File;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public enum OS {

    WINDOWS(false, false) {
        @Override
        public File findAppDataDir() {
            String directory = System.getenv("APPDATA");
            if ( directory == null || directory.isEmpty() ) {
                directory = System.getProperty("user.home");
            }
            return new File(directory);
        }
    },
    MAC(true, true) {
        @Override
        protected File findAppDataDir() {
            return new File(System.getProperty("user.home"), "Library/Application Support");
        }
    },
    LINUX(true, true) {
        @Override
        protected File findAppDataDir() {
            return freedesktopAppData;
        }
    },
    UNIX(true, true) {
        @Override
        protected File findAppDataDir() {
            return freedesktopAppData;
        }
    };

    public static final String LINE_SEPARATOR;
    static {
        String ls = System.getProperty("line.separator");
        if ( ls == null || ls.isEmpty() ) {
            // you never know ... ;)
            ls = "\n";
        }
        LINE_SEPARATOR = ls;
    }

    private static final OS current;
    static {
        String os = System.getProperty("os.name").toLowerCase();
        if ( os.startsWith("windows") ) {
            current = WINDOWS;
        }
        else if ( os.startsWith("mac os x") ) {
            current = MAC;
        }
        else if ( os.startsWith("linux") ) {
            current = LINUX;
        }
        else {
            // for now, we're simply assuming UNIX
            current = UNIX;
        }
    }

    private static volatile boolean preferFreedesktop = false;
    private static final File freedesktopAppData = findFreedesktopAppdata();

    private final File applicationDataDir;
    private final boolean freedesktopApplicable;
    private final boolean unix;

    private OS(boolean unix, boolean freedesktopApplicable) {
        this.freedesktopApplicable = freedesktopApplicable;
        this.unix = unix;
        applicationDataDir = findAppDataDir();
    }

    public static void preferFreedesktop(boolean preferFreedesktop) {
        OS.preferFreedesktop = preferFreedesktop;
    }

    public static boolean preferFreedesktop() {
        return preferFreedesktop;
    }

    public static OS current() {
        return current;
    }

    public boolean freedesktopApplicable() {
        return freedesktopApplicable;
    }

    public boolean unix() {
        return unix;
    }

    public File applicationDataDir() {
        if ( freedesktopApplicable && preferFreedesktop ) {
            return freedesktopAppData;
        }
        else {
            return applicationDataDir;
        }
    }

    public File applicationDataDir(String name) {
        return new File(applicationDataDir(), name);
    }

    protected abstract File findAppDataDir();

    private static File findFreedesktopAppdata() {
        String directory = System.getenv("XDG_CONFIG_HOME");
        if ( directory == null || directory.isEmpty() ) {
            return new File(System.getProperty("user.home"), ".config");
        }
        else {
            return new File(directory);
        }
    }

}
