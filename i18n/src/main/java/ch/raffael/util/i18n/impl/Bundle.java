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

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.raffael.util.common.logging.LogUtil;
import ch.raffael.util.i18n.Default;
import ch.raffael.util.i18n.Forward;
import ch.raffael.util.i18n.I18N;
import ch.raffael.util.i18n.I18NException;
import ch.raffael.util.i18n.NotFoundException;
import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Bundle implements InvocationHandler {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private final Class<? extends ResourceBundle> bundleClass;
    private final ResourceBundle proxy;

    private final List<Bundle> parents = new LinkedList<Bundle>();
    private final Map<ResourcePointer, ResourceHolder> resources = new HashMap<ResourcePointer, ResourceHolder>();

    private final Map<ResourceIndicator, MethodSignature> methods = new HashMap<ResourceIndicator, MethodSignature>();
    private final Map<ResourceIndicator, Bundle> forwards = new HashMap<ResourceIndicator, Bundle>();
    private final Map<ResourceIndicator, String> defaults = new HashMap<ResourceIndicator, String>();

    private final Map<Method, MethodSignature> signatures = new HashMap<Method, MethodSignature>();

    private ResourceResolver resolver;

    private MetaImpl meta = null;
    private final Map<ResourceIndicator, ResourceImpl<Object>> dynamicsCache = new HashMap<ResourceIndicator, ResourceImpl<Object>>();


    public Bundle(@NotNull Class<? extends ResourceBundle> bundleClass) {
        if ( !validateBundleClass(bundleClass) ) {
            throw new IllegalArgumentException("Invalid bundle class: " + bundleClass);
        }
        this.bundleClass = bundleClass;
        proxy = (ResourceBundle)Proxy.newProxyInstance(bundleClass.getClassLoader(), new Class<?>[] { bundleClass }, this);

    }

    public static boolean validateBundleClass(Class<?> bundleClass) {
        return ResourceBundle.class.isAssignableFrom(bundleClass) && bundleClass.isInterface() && !bundleClass.isAnnotation();
    }

    @SuppressWarnings({ "unchecked" })
    public synchronized void init() {
        //Map<ResourceIndicator, MethodSignature> methods = new HashMap<ResourceIndicator, MethodSignature>();
        for ( Class<?> cls : bundleClass.getInterfaces() ) {
            if ( bundleClass.equals(ResourceBundle.class) ) {
                continue; // skip
            }
            if ( !validateBundleClass(cls) ) {
                throw new I18NException(bundleClass + ": Invalid superinterface " + cls);
            }
            Bundle parentBundle = BundleManager.getInstance().getOrLoad((Class<? extends ResourceBundle>)cls);
            for ( MethodSignature signature : parentBundle.methods.values() ) {
                checkCompatibility(signature);
                methods.put(signature.getIndicator(), signature);
            }
            signatures.putAll(parentBundle.signatures);
            parents.add(parentBundle);
        }
        for ( Method method : bundleClass.getDeclaredMethods() ) {
            if ( isMetaMethod(method) ) {
                // check compatibility
                if ( !method.getReturnType().equals(ResourceBundle.Meta.class) ) {
                    throw new I18NException(method + ": Incompatible meta() method");
                }
                // skip
                continue;
            }
            Handler handler = HandlerManager.getInstance().getHandler(method.getReturnType());
            if ( handler == null ) {
                throw new I18NException(method + ": Invalid return type");
            }
            MethodSignature signature = new MethodSignature(method);
            checkCompatibility(signature);
            handler.validateSignature(bundleClass, signature);
            Forward fwd = method.getAnnotation(Forward.class);
            if ( fwd != null ) {
                Bundle fwdBundle = BundleManager.getInstance().getOrLoad(fwd.value());
                MethodSignature fwdSignature = fwdBundle.methods.get(signature.getIndicator());
                try {
                    signature.checkCompatibility(fwdSignature);
                }
                catch ( I18NException e ) {
                    throw new I18NException(method + ": Forward to incompatible method: " + Util.resourceString(fwdBundle.getBundleClass(), fwdSignature), e);
                }
                forwards.put(signature.getIndicator(), fwdBundle);
            }
            Default def = method.getAnnotation(Default.class);
            if ( def != null ) {
                defaults.put(signature.getIndicator(), def.value());
            }
            methods.put(signature.getIndicator(), signature);
            signatures.put(method, signature);
        }
        // load the implementation
        resolver = new PropertyResourceResolver(bundleClass);
    }

    private void checkCompatibility(MethodSignature signature) {
        MethodSignature existing = methods.get(signature.getIndicator());
        if ( existing != null ) {
            existing.checkCompatibility(signature);
        }
    }

    @Override
    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ( isMetaMethod(method) ) {
            if ( meta == null ) {
                meta = new MetaImpl();
            }
            return meta;
        }
        MethodSignature signature = signatures.get(method);
        if ( signature == null ) {
            // just in case, shouldn't happen, actually, after init()
            signature = new MethodSignature(method);
            signatures.put(method, signature);
        }
        return getResource(signature, args);
    }

    private Object getResource(MethodSignature signature, Object[] args) {
        Object[] selectors = null;
        Object[] parameters = null;
        List<MethodSignature.Argument> arguments = signature.getArguments();
        if ( arguments.size() > 0 ) {
            if ( signature.getSelectorCount() > 0 ) {
                selectors = new Object[signature.getSelectorCount()];
            }
            int paramCount = args.length - signature.getSelectorCount();
            if ( paramCount > 0 ) {
                parameters = new Object[paramCount];
            }
            int selIdx = 0;
            int paramIdx = 0;
            for ( int i = 0; i < args.length; i++ ) {
                if ( arguments.get(i).isSelector() ) {
                    assert selectors != null;
                    selectors[selIdx++] = args[i];
                }
                else {
                    assert parameters != null;
                    parameters[paramIdx++] = args[i];
                }
            }
        }
        ResourcePointer ptr = new ResourcePointer(signature, selectors, I18N.getLocaleSearch());
        ResourceHolder holder = lookup(ptr);
        try {
            if ( holder == null ) {
                if ( I18N.isLenient() ) {
                    Handler handler = HandlerManager.getInstance().getHandler(signature.getReturnType());
                    assert handler != null; // has been checked in init()
                    return handler.notFound(bundleClass, ptr, resolver.getBaseUrl());
                }
                else {
                    throw new NotFoundException("Resource " + Util.resourceString(bundleClass, signature) + " not found");
                }
            }
            else {
                if ( parameters != null ) {
                    return holder.handler.parametrize(holder.get(), parameters);
                }
                else {
                    return holder.get();
                }
            }
        }
        catch ( I18NException e ) {
            throw e;
        }
        catch ( Exception e ) {
            throw new I18NException("Error getting resource " + signature + " with arguments " + (args == null ? "[]" : Arrays.asList(args)), e);
        }
    }

    @Nullable
    private ResourceHolder lookup(@NotNull ResourcePointer ptr) {
        if ( resources.containsKey(ptr) ) {
            return resources.get(ptr);
        }
        // check for forward
        Bundle fwd = forwards.get(ptr.getSignature().getIndicator());
        if ( fwd != null ) {
            ResourceHolder res = fwd.lookup(ptr);
            resources.put(ptr, res);
            return res;
        }
        // defined here?
        String strValue = resolver.getValue(ptr);
        if ( strValue == null ) {
            strValue = defaults.get(ptr.getSignature().getIndicator());
        }
        if ( strValue != null ) {
            Handler handler = HandlerManager.getInstance().getHandler(ptr.getSignature().getReturnType());
            assert handler != null; // should have been checked in init()
            ResourceHolder res = new ResourceHolder(bundleClass, ptr, handler, strValue);
            resources.put(ptr, res);
            return res;
        }
        // check inherited
        ResourceHolder inherited = null;
        for ( Bundle p : parents ) {
            ResourceHolder r = p.lookup(ptr);
            if ( r != null ) {
                if ( inherited != null && !inherited.source.equals(r.source) ) {
                    // ambiguous
                    throw new I18NException("Ambiguousity at " + ptr + ": Both " + inherited.source + " and " + r.source + " match");
                }
                inherited = r;
            }
        }
        resources.put(ptr, inherited);
        return inherited;
    }

    private static boolean isMetaMethod(Method method) {
        return method.getName().equals("meta") && method.getParameterTypes().length == 0;
    }

    @NotNull
    public Class<? extends ResourceBundle> getBundleClass() {
        return bundleClass;
    }

    @SuppressWarnings({ "unchecked" })
    @NotNull
    public <T extends ResourceBundle> T getResourceBundle(@NotNull Class<T> getAs) {
        if ( !getAs.equals(bundleClass) ) {
            throw new IllegalArgumentException("Invalid bundle class " + getAs + ": Expected " + bundleClass);
        }
        return (T)proxy;
    }

    private final class ResourceHolder {
        private final Class<? extends ResourceBundle> source;
        private final ResourcePointer pointer;
        private final Handler handler;
        private final String strValue;
        private SoftReference<Object> value;
        private ResourceHolder(Class<? extends ResourceBundle> source, ResourcePointer pointer, Handler handler, String strValue) {
            this.source = source;
            this.handler = handler;
            this.pointer = pointer;
            this.strValue = strValue;
        }
        @NotNull
        private synchronized Object get() {
            Object value = null;
            if ( this.value != null ) {
                value = this.value.get();
            }
            if ( value == null ) {
                try {
                    value = handler.resolve(source, pointer, resolver.getBaseUrl(), strValue);
                }
                catch ( Exception e ) {
                    throw new I18NException("Error loading resource " + pointer, e);
                }
                if ( value == null ) {
                    if ( I18N.isLenient() ) {
                        return handler.notFound(bundleClass, pointer, resolver.getBaseUrl());
                    }
                    else {
                        throw new NotFoundException("Cannot load resource " + pointer);
                    }
                }
                this.value = new SoftReference<Object>(value);
            }
            return value;
        }
    }

    private class MetaImpl implements ResourceBundle.Meta {

        @NotNull
        @Override
        public <T> ResourceBundle.Resource<T> resource(Class<T> type, String name) {
            return resource(type, name, (Class<?>[])null);
        }

        @SuppressWarnings({ "unchecked" })
        @NotNull
        @Override
        public <T> ResourceBundle.Resource<T> resource(Class<T> type, String name, Class<?>... paramTypes) {
            ResourceBundle.Resource result = resource(name, paramTypes);
            if ( !type.isAssignableFrom(result.type()) ) {
                throw new I18NException("Return type mismatch at resource " + ((ResourceImpl)result).signature + " <-> " + type);
            }
            return (ResourceBundle.Resource<T>)result;
        }

        @NotNull
        @Override
        public ResourceBundle.Resource<Object> resource(String name) {
            return resource(name, (Class<?>[])null);
        }

        @NotNull
        @Override
        public ResourceBundle.Resource<Object> resource(String name, Class<?>... paramTypes) {
            synchronized ( Bundle.this ) {
                ResourceIndicator indicator = new ResourceIndicator(name, paramTypes);
                ResourceImpl<Object> result = dynamicsCache.get(indicator);
                if ( result == null ) {
                    MethodSignature signature = methods.get(indicator);
                    if ( signature == null ) {
                        Method method;
                        try {
                            method = bundleClass.getMethod(name, paramTypes);
                        }
                        catch ( NoSuchMethodException e ) {
                            throw new I18NException("Bundle " + bundleClass.getName() + " has no method " + indicator);
                        }
                        methods.put(indicator, signature);
                        signatures.put(method, signature);
                    }
                    result = new ResourceImpl<Object>(signature);
                    dynamicsCache.put(indicator, result);
                }
                return result;
            }
        }

    }

    private class ResourceImpl<T> implements ResourceBundle.Resource<T> {
        private final MethodSignature signature;
        private ResourceImpl(MethodSignature signature) {
            this.signature = signature;
        }
        @NotNull
        @Override
        public T get() {
            return get((Object[])null);
        }
        @SuppressWarnings({ "unchecked" })
        @NotNull
        @Override
        public T get(Object... args) {
            synchronized ( Bundle.this ) {
                int argCount = 0;
                if ( args != null ) {
                    argCount = args.length;
                }
                if ( signature.getArguments().size() != argCount ) {
                    throw new IllegalArgumentException("Invalid argument count " + argCount + " for resource " + signature);
                }
                if ( args != null ) {
                    for ( int i = 0; i < args.length; i++ ) {
                        if ( args[i] != null && !signature.getArguments().get(i).getType().isInstance(args[i]) ) {
                            throw new IllegalArgumentException("Argument " + i + " for resource " + signature + ": " + args[i] + " is not an instance of " + signature.getArguments().get(i).getType());
                        }
                    }
                }
                return (T)getResource(signature, args);
            }
        }
        @SuppressWarnings({ "unchecked" })
        @NotNull
        @Override
        public Class<T> type() {
            return (Class<T>)signature.getReturnType();
        }
    }

}
