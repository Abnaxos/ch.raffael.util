/*
 * Copyright (c) 2009, Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.raffael.util.classloader;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.raffael.util.classloader.Dispatcher.Type.*;


/**
 * A class loader that is capable of delegating 'horizontally' to other class loaders.
 * It allows to map packages or package hierarchies to {@link Dispatcher}s which in turn
 * may change the class loader to be used to load the class. This is useful to implement
 * OSGi-like class loader isolation and delegation.
 *
 * @see PackageMap
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DelegatingClassLoader extends URLClassLoader implements DelegatingClassLoaderMBean {

    private static final Logger log = LoggerFactory.getLogger(DelegatingClassLoader.class);

    private final String id = null;
    private final PackageMap<Dispatcher> dispatchers = new PackageMap<Dispatcher>();

    private boolean reporting = true;
    private final List<ClassLoaderReportEntry> report = new ArrayList<ClassLoaderReportEntry>();
    private int internalCount = 0;
    private int importedCount = 0;
    private int failedCount = 0;

    static {
        Method registerMethod = null;
        try {
            registerMethod = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable");
        }
        catch ( NoSuchMethodException e ) {
            log.warn("Cannot register class loader as parallel capable. It's strongly recommended to use JDK7 or higher to avoid dead-locks.");
            log.warn("See Java bug #4670071 (http://bugs.sun.com/view_bug.do?bug_id=4670071) for details.");
        }
        if ( registerMethod != null ) {
            try {
                registerMethod.setAccessible(true);
                Boolean result = (Boolean)registerMethod.invoke(null);
                if ( !result ) {
                    log.error("Could not register class loader as parallel capable. This may cause dead-locks while class-loading.");
                }
            }
            catch ( Exception e ) {
                log.error("Error registering class loader as parallel capable. This may cause dead-locks while class-loading.", e);
            }
        }
    }

    public DelegatingClassLoader(URL[] classpath, ClassLoader parent, Map<String, Dispatcher> dispatchers) {
        super(classpath, parent);
        this.dispatchers.putAll(dispatchers);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "DelegatingClassLoader{" + id + "}";
    }

    /**
     * Load a class, delegating to another class loader if necessary. This implementation
     * first looks up a dispatcher for the package. If no dispatcher is found, the
     * original implementation of URLClassLoader will be used, otherwise, the dispatcher
     * is asked for a class loader for the class. If the dispatcher returns
     * <code>null</code>, it as if no dispatcher had been found: The class loader will
     * try to load the class itself.
     *
     * @param name The name of the class to load.
     *
     * @return The loaded class.
     *
     * @throws ClassNotFoundException
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        int pos = name.lastIndexOf('.');
        if ( pos < 0 ) {
            try {
                return super.findClass(name);
            }
            catch ( ClassNotFoundException e ) {
                throw report(name, e);
            }
        }
        String pkg = name.substring(0, pos).replace('.', '/');
        String className = name.substring(pos + 1);
        ClassLoader exporter = findLoader(pkg, className, CLASS);
        if ( exporter == null ) {
            // there was an import or the package but no exporter found
            throw report(name, new ClassNotFoundException(name + " (no exporting module)"));
        }
        else if ( exporter == this ) {
            if ( doLog() ) {
                log("Loading class " + name + " internally");
            }
            try {
                return report(super.findClass(name), this);
            }
            catch ( ClassNotFoundException e ) {
                throw report(name, e);
            }
        }
        else {
            if ( doLog() ) {
                log("Importing class " + name + " from " + exporter);
            }
            try {
                return report(exporter.loadClass(name), exporter);
            }
            catch ( ClassNotFoundException e ) {
                throw report(name, new ClassNotFoundException(name + " (delegated to " + exporter + ")", e));
            }
        }
    }

    /**
     * Determines the ClassLoader for a class or resource. Returns <code>this</code> to
     * handle the class or resource internally, or a class loader to delegate to.
     *
     * @param pkg      The name of the package (using '/'-notation, e.g. "ch/raffael").
     *
     * @return The ClassLoader which loads classes from this package or
     *         <code>null</code>.
     */
    protected ClassLoader findLoader(String pkg, String name, Dispatcher.Type type) {
        Dispatcher dispatcher = dispatchers.get(pkg);
        if ( dispatcher == null ) {
            return this;
        }
        ClassLoader loader = dispatcher.getClassLoaderFor(pkg, name, type);
        if ( loader != null ) {
            return loader;
        }
        else {
            return this;
        }
    }

    /**
     * Find a resource. Finding resource follows the rules of loading classes described
     * in {@link #findClass(String)}.
     *
     * @param name The name of the resource.
     * @return The URL of the resource or <code>null</code>.
     */
    @Override
    public URL findResource(String name) {
        int pos = name.lastIndexOf('/');
        if ( pos < 0 ) {
            return super.findResource(name);
        }
        String pkg = name.substring(0, pos);
        String resName = name.substring(pos + 1);
        ClassLoader exporter = findLoader(pkg, resName, RESOURCE);
        if ( exporter == null ) {
            return null;
        }
        else if ( exporter == this ) {
            if ( doLog() ) {
                log("Finding resource " + name + " locally");
            }
            return super.findResource(name);
        }
        else {
            if ( doLog() ) {
                log("Importing resource " + name + " from " + exporter);
            }
            return exporter.getResource(name);
        }
    }

    /**
     * Find a resource. Finding resource follows the rules of loading classes described
     * in {@link #findClass(String)}.
     *
     * @param name The name of the resource.
     * @return The URL of the resource or <code>null</code>.
     */
    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        int pos = name.lastIndexOf('/');
        if ( pos < 0 ) {
            return super.findResources(name);
        }
        String pkg = name.substring(0, pos);
        String resName = name.substring(pos + 1);
        ClassLoader exporter = findLoader(pkg, resName, RESOURCE);
        if ( exporter == null ) {
            return null;
        }
        else if ( exporter == this ) {
            if ( doLog() ) {
                log("Finding all resources " + name + " locally");
            }
            return super.findResources(name);
        }
        else {
            if ( doLog() ) {
                log("Importing all resources " + name + " from " + exporter);
            }
            return exporter.getResources(name);
        }
    }

    /**
     * Get the ID of this ClassLoader.
     *
     * @return The ID of this ClassLoader.
     */
    public String getId() {
        return id;
    }

    protected boolean doLog() {
        return log.isTraceEnabled();
    }

    protected void log(String msg) {
        if ( log.isTraceEnabled() ) {
            log.trace(id + ": " + msg);
        }
    }

    public boolean isReporting() {
        synchronized ( report ) {
            return reporting;
        }
    }

    public void setReporting(boolean reporting) {
        synchronized ( report ) {
            this.reporting = reporting;
        }
    }

    protected Class<?> report(Class<?> clazz, ClassLoader source) {
        if ( reporting ) {
            synchronized ( report ) {
                String loaderName;
                if ( source == this ) {
                    internalCount++;
                    loaderName = null;
                }
                else {
                    importedCount++;
                    loaderName = source.toString();
                }
                ClassLoaderReportEntry entry = new ClassLoaderReportEntry(report.size(), clazz.getName(), loaderName, null);
                report.add(entry);
            }
        }
        return clazz;
    }

    protected ClassNotFoundException report(String name, ClassNotFoundException exception) {
        if ( reporting ) {
            synchronized ( report ) {
                failedCount++;
                ClassLoaderReportEntry entry = new ClassLoaderReportEntry(report.size(), name, null, exception);
                report.add(entry);
            }
        }
        return exception;
    }

    // JMX

    public int getLoadedCount() {
        synchronized ( report ) {
            return importedCount + internalCount;
        }
    }

    public int getImportedCount() {
        synchronized ( report ) {
            return importedCount;
        }
    }

    public int getFailedCount() {
        synchronized ( report ) {
            return failedCount;
        }
    }

    public int getInternalCount() {
        synchronized ( report ) {
            return internalCount;
        }
    }

    public int getTotalCount() {
        synchronized ( report ) {
            return importedCount + internalCount + failedCount;
        }
    }

    public TabularData getReport(int type, int startIndex) throws OpenDataException {
        CompositeType rowType = new CompositeType("Class Report", "Class Report",
                                                  new String[] { "Index", "Timestamp", "Class", "Loader", "Error" },
                                                  new String[] { "Index", "Timestamp", "Class", "Loader", "Error" },
                                                  new OpenType[] { SimpleType.INTEGER, SimpleType.LONG, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING});
        TabularType resultType = new TabularType("ClassLoaderReport",
                                                 "Class Loader Report",
                                                 rowType,
                                                 new String[] { "Index" });
        TabularDataSupport result = new TabularDataSupport(resultType);
        synchronized ( report ) {
            List<ClassLoaderReportEntry> r = report;
            if ( startIndex > 0 ) {
                r = report.subList(startIndex, report.size());
            }
            for ( ClassLoaderReportEntry entry : r ) {
                boolean doReport;
                switch ( type ) {
                    case REPORT_ALL:
                        doReport = true;
                        break;
                    case REPORT_INTERNAL:
                        doReport = entry.exception == null && entry.loaderName == null;
                        break;
                    case REPORT_IMPORTED:
                        doReport = entry.exception == null && entry.loaderName != null;
                        break;
                    case REPORT_FAILED:
                        doReport = entry.exception != null;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown report type (supportet reports: 0=all, 1=internal, 2=imported, 3=failed");
                }
                if ( doReport ) {
                    String error = null;
                    if ( entry.exception != null ) {
                        StringWriter str = new StringWriter();
                        PrintWriter print = new PrintWriter(str);
                        entry.exception.printStackTrace(print);
                        print.flush();
                        error = str.toString();
                    }
                    result.put(new CompositeDataSupport(
                            rowType,
                            new String[] { "Index", "Timestamp", "Class", "Loader", "Error" },
                            new Object[] { entry.index, entry.timestamp, entry.className, entry.loaderName, error }));
                }
            }
        }
        return result;
    }

    protected static class ClassLoaderReportEntry {
        private final int index;
        private final long timestamp;
        private final String className;
        private final String loaderName;
        private final Throwable exception;


        public ClassLoaderReportEntry(int index, String className, String loaderName, Throwable exception) {
            this.index = index;
            this.timestamp = System.currentTimeMillis();
            this.className = className;
            this.loaderName = loaderName;
            this.exception = exception;
        }
    }

    public static final class Builder {

        private final Map<String, Dispatcher> dispatchers = new HashMap<String, Dispatcher>();
        private final List<URL> urls = new ArrayList<URL>();
        private String id = null;
        private boolean reporting = Boolean.getBoolean("ch.raffael.util.classloader.reportingByDefault");
        private boolean mbean = false;

        private Builder() {
        }

        public Builder add(URL url) {
            urls.add(url);
            return this;
        }

        public Builder add(URL... urls) {
            this.urls.addAll(Arrays.asList(urls));
            return this;
        }

        public Builder add(Collection<? extends URL> urls) {
            this.urls.addAll(urls);
            return this;
        }

        public Builder add(Iterable<? extends URL> urls) {
            if ( urls instanceof Collection ) {
                add((Collection<? extends URL>)urls);
            }
            else {
                for ( URL url : urls ) {
                    this.urls.add(url);
                }
            }
            return this;
        }

        public Builder add(Iterator<? extends URL> urls) {
            while ( urls.hasNext() ) {
                this.urls.add(urls.next());
            }
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder reporting() {
            reporting = true;
            return this;
        }

        public Builder reporting(boolean reporting) {
            this.reporting = reporting;
            return this;
        }

        public Builder mbean() {
            this.mbean = true;
            this.reporting = true;
            return this;
        }

        public Builder mbean(boolean mbean) {
            this.mbean = mbean;
            if ( mbean ) {
                this.reporting = true;
            }
            return this;
        }

        public Builder dispatch(String packageName, ClassLoader classLoader) {
            return dispatch(packageName, new FixedDispatcher(classLoader));
        }

        public Builder dispatch(String packageName, Dispatcher dispatcher) {
            boolean start = true;
            for ( int i = 0; i < packageName.length(); i++ ) {
                char c = packageName.charAt(i);
                if ( start ) {
                    if ( !Character.isJavaIdentifierStart(c) ) {
                        throw new IllegalArgumentException("Invalid package name: " + packageName);
                    }
                    start = false;
                }
                else if ( c == '/' || c == '.' ) {
                    start = true;
                }
                else if ( !Character.isJavaIdentifierPart(c) ) {
                    throw new IllegalArgumentException("Invalid package name: " + packageName);
                }
            }
            dispatchers.put(packageName.replace('.', '/'), dispatcher);
            return this;
        }

        public DelegatingClassLoader build(ClassLoader parent) {
            if ( id == null ) {
                id = UUID.randomUUID().toString();
            }
            DelegatingClassLoader classLoader = new DelegatingClassLoader(
                    urls.toArray(new URL[urls.size()]), parent, dispatchers);
            classLoader.setReporting(reporting);
            if ( mbean ) {
                String name = "ch.raffael.util:type=DelegatingClassLoader,id=";
                boolean quote = false;
                for ( int i = 0; i < id.length(); i++ ) {
                    char c = id.charAt(i);
                    if ( c == ',' || c == '=' || c == ':' || c == '"' || c == '*' || c == '?' ) {
                        quote = true;
                        break;
                    }
                }
                if ( quote ) {
                    name += ObjectName.quote(id);
                }
                else {
                    name += id;
                }
                try {
                    ManagementFactory.getPlatformMBeanServer().registerMBean(classLoader, new ObjectName(name));
                }
                catch ( Exception e ) {
                    log.warn("Cannot register MBean {}", name, e);
                }
            }
            return classLoader;
        }

    }

}
