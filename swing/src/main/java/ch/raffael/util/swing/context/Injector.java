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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.raffael.util.beans.BeanException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Injector<T> {

    private static final Map<Class<?>, Object> defaults = Collections.unmodifiableMap(new HashMap<Class<?>, Object>() {{
        put(int.class, 0);
        put(short.class, (short)0);
        put(byte.class, (byte)0);
        put(long.class, 0L);
        put(double.class, 0.0);
        put(float.class, 0f);
        put(boolean.class, false);
        put(char.class, '\0');
    }});
    private static final Map<Class<?>, Class<?>> wrappers = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>() {{
        put(int.class, Integer.class);
        put(short.class, Short.class);
        put(byte.class, Byte.class);
        put(long.class, Long.class);
        put(double.class, Double.class);
        put(float.class, Float.class);
        put(boolean.class, Boolean.class);
        put(char.class, Character.class);
    }});
    private final Class<T> clazz;
    private final List<MemberInfo<Constructor<?>>> constructors;
    private final List<MemberInfo<Method>> methods;

    public Injector(Class<T> clazz) {
        this.clazz = clazz;
        constructors = scanConstructors();
        methods = scanMethods();
    }

    @SuppressWarnings({ "unchecked" })
    private List<MemberInfo<Constructor<?>>> scanConstructors() {
        Constructor<?>[] ctors = clazz.getConstructors();
        Arrays.sort(ctors, new Comparator<Constructor<?>>() {
            @Override
            public int compare(Constructor<?> a, Constructor<?> b) {
                if ( a.getParameterTypes().length > b.getParameterTypes().length ) {
                    return 1;
                }
                else if ( a.getParameterTypes().length < b.getParameterTypes().length ) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        List<MemberInfo<Constructor<?>>> result = new ArrayList<MemberInfo<Constructor<?>>>(ctors.length);
        for ( Constructor<?> ctor : ctors ) {
            if ( ctor.getAnnotation(NoInject.class) == null ) {
                result.add(this.<Constructor<?>>readMemberInfo(ctor, true, ctor.getParameterTypes(), ctor.getParameterAnnotations()));
            }
        }
        return Collections.unmodifiableList(result);
    }

    private List<MemberInfo<Method>> scanMethods() {
        List<MemberInfo<Method>> result = new ArrayList<MemberInfo<Method>>();
        for ( Method method : clazz.getMethods() ) {
            Init init = method.getAnnotation(Init.class);
            if ( init != null ) {
                MemberInfo<Method> info = readMemberInfo(method, init.optional(), method.getParameterTypes(), method.getParameterAnnotations());
                info.priority = init.pri();
                result.add(info);
            }
        }
        Collections.sort(result, new Comparator<MemberInfo<Method>>() {
            @Override
            public int compare(MemberInfo<Method> a, MemberInfo<Method> b) {
                // FIXME: would be nice to also sort according to depth (super class first)
                if ( a.priority > b.priority ) {
                    return 1;
                }
                else if ( a.priority < b.priority ) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        return Collections.unmodifiableList(result);
    }

    private <M extends Member> MemberInfo<M> readMemberInfo(M member, boolean optional, Class<?>[] paramTypes, Annotation[][] paramAnnotations) {
        MemberInfo<M> memberInfo = new MemberInfo<M>();
        memberInfo.member = member;
        memberInfo.optional = optional;
        memberInfo.params = new InjectionInfo[paramTypes.length];
        for ( int i = 0; i < paramTypes.length; i++ ) {
            InjectionInfo info = new InjectionInfo();
            info.type = paramTypes[i];
            for ( Annotation annotation : paramAnnotations[i] ) {
                if ( annotation instanceof Inject ) {
                    Inject inject = (Inject)annotation;
                    if ( !inject.type().equals(Inject.USE_PARAM_CLASS) ) {
                        info.type = inject.type();
                    }
                    info.key = decodeKey(inject.key());
                    info.optional = inject.optional();
                    break;
                }
            }
            memberInfo.params[i] = info;
        }
        return memberInfo;
    }

    private Object decodeKey(String keyString) {
        // FIXME: add some way to use more complex keys?
        if ( keyString.isEmpty() ) {
            return null;
        }
        else {
            return keyString;
        }
    }

    public T instantiate(Context ctx) {
        Object[] args = null;
        nextConstructor:
        for ( MemberInfo<Constructor<?>> ctor : constructors ) {
            if ( args == null ) {
                // We're going to reuse that array, instead of creating a new one for
                // each try. Because the most eager constructor will always be the first
                // one, it will be large enough to hold all arguments for any constructor.
                // We'll copy the array before invoking the constructor if needed.
                args = new Object[ctor.params.length];
            }
            for ( int i = 0; i < ctor.params.length; i++ ) {
                InjectionInfo info = ctor.params[i];
                args[i] = getParameter(ctx, info, null, null);
                if ( args[i] == null ) {
                    if ( !info.optional ) {
                        continue nextConstructor;
                    }
                    else {
                        args[i] = defaults.get(info.type);
                    }
                }
            }
            // looks like we could match all arguments, let's use that constructor
            assert args.length <= ctor.params.length;
            if ( args.length != ctor.params.length ) {
                args = Arrays.copyOf(args, ctor.params.length);
            }
            return instantiate(ctor.member, args);
        }
        // no constructor matched
        throw new ContextException("No constructor found to instantiate " + clazz + " using " + ctx);
    }

    private Object getParameter(Context ctx, InjectionInfo info, Object target, Method targetMethod) {
        if ( info.type.isPrimitive() ) {
            return ctx.find(wrappers.get(info.type), info.key);
        }
        else if ( info.type.isAssignableFrom(Context.class) && !info.type.equals(Object.class) ) {
            return ctx;
        }
        Object param = ctx.find(info.type, info.key);
        if ( param == null ) {
            InjectionProvider provider = ctx.find(InjectionProvider.class, info.type);
            if ( provider != null ) {
                param = provider.getInjection(ctx, info.type, info.key, clazz, target, targetMethod);
            }
        }
        return param;
    }

    @SuppressWarnings({ "unchecked" })
    private T instantiate(Constructor<?> ctor, Object[] args) {
        try {
            return (T)ctor.newInstance(args);
        }
        catch ( InstantiationException e ) {
            throw new BeanException("Error calling constructor " + ctor, e);
        }
        catch ( IllegalAccessException e ) {
            throw new BeanException("Error calling constructor " + ctor, e);
        }
        catch ( InvocationTargetException e ) {
            throw new BeanException("Error calling constructor " + ctor, e);
        }
    }

    public void initialize(Context ctx, T object) {
        nextMethod:
        for ( MemberInfo<Method> method : methods ) {
            Object[] args = new Object[method.params.length];
            for ( int i = 0; i < method.params.length; i++ ) {
                InjectionInfo info = method.params[i];
                args[i] = getParameter(ctx, info, object, method.member);
                if ( args[i] == null ) {
                    if ( !info.optional ) {
                        if ( !method.optional ) {
                            throw new ContextException("Cannot inject paramter " + i + " of init-method " + method.member);
                        }
                        else {
                            // the whole method is optional, so just skip it
                            continue nextMethod;
                        }
                    }
                    else {
                        args[i] = defaults.get(info.type);
                    }
                }
            }
            try {
                method.member.invoke(object, args);
            }
            catch ( IllegalAccessException e ) {
                throw new BeanException("Error invoking " + method.member + " on " + object, e);
            }
            catch ( InvocationTargetException e ) {
                throw new BeanException("Error invoking " + method.member + " on " + object, e);
            }
        }
    }

    private final static class InjectionInfo {
        private Class<?> type;
        private Object key;
        private boolean optional;
    }

    private final static class MemberInfo<T extends Member> {
        private T member;
        private boolean optional;
        private int priority;
        private InjectionInfo[] params;
    }

}
