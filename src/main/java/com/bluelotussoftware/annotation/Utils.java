/*
 * Copyright 2016 Blue Lotus Software, LLC.
 * Copyright 2015 John Yeary <jyeary@bluelotussoftware.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bluelotussoftware.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import javax.annotation.PreDestroy;

/**
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 2.0
 */
public class Utils {

    /**
     * This method reflectively examines the provided object looking for
     * {@link PreDestroy} annotations, invokes the first method with the
     * annotation found, and returns.
     *
     * @param object The object to examine.
     * @return {@code true} if successful, and {@code false} otherwise.
     * @throws SecurityException If a security manager, <i>s</i>, is present and
     * any of the following conditions is met:
     * <ul>
     * <li> the caller's class loader is not the same as the class loader of
     * this class and invocation of
     * {@link SecurityManager#checkPermissions#checkPermission(java.security.Permission)}
     * method with {@code RuntimePermission("accessDeclaredMembers")} denies
     * access to the declared methods within this class.</li>
     *
     * <li> the caller's class loader is not the same as or an ancestor of the
     * class loader for the current class and invocation of
     * {@link SecurityManager#checkPackageAccess#checkPackageAccess(java.lang.String)}
     * denies access to the package of this class.</li>
     * </ul>
     * @throws IllegalArgumentException if the method is an instance method and
     * the specified object argument is not an instance of the class or
     * interface declaring the underlying method (or of a subclass or
     * implementor thereof); if the number of actual and formal parameters
     * differ; if an unwrapping conversion for primitive arguments fails; or if,
     * after possible unwrapping, a parameter value cannot be converted to the
     * corresponding formal parameter type by a method invocation conversion.
     * @throws IllegalAccessException if this {@code Method} object is enforcing
     * Java language access control and the underlying method is inaccessible.
     * @throws InvocationTargetException if the underlying method throws an
     * exception.
     */
    public static boolean destroy(final Object object) throws SecurityException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class clazz = object.getClass();
        String methodName = null;

        synchronized (object) {
            boolean success = false;
            try {
                Method[] methods = clazz.getDeclaredMethods();

                done:// Break for multiple for loops.
                for (Method method : methods) {
                    Annotation[] annotations = method.getDeclaredAnnotations();

                    for (Annotation annotation : annotations) {

                        if (annotation instanceof PreDestroy) {
                            methodName = method.getName();

                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }

                            method.invoke(object, (Object[]) null);
                            success = true;
                            break done;
                        }
                    }
                }
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                System.err.println(MessageFormat.format("An exception occurred while trying to process @PreDestroy on : {0}.{1}(). The message follows: {2}", clazz.getName(), methodName, ex.getMessage()));
                throw ex;
            }
            return success;
        }
    }

    /**
     *
     * This method reflectively examines the provided map looking for objects
     * with {@link PreDestroy} annotations, invokes the first method with the
     * annotation found, and returns.
     *
     * @param map A map of objects to examine for {@link PreDestroy}
     * annotations.
     * @throws SecurityException If a security manager, <i>s</i>, is present and
     * any of the following conditions is met:
     * <ul>
     * <li> the caller's class loader is not the same as the class loader of
     * this class and invocation of
     * {@link SecurityManager#checkPermissions#checkPermission(java.security.Permission)}
     * method with {@code RuntimePermission("accessDeclaredMembers")} denies
     * access to the declared methods within this class.</li>
     *
     * <li> the caller's class loader is not the same as or an ancestor of the
     * class loader for the current class and invocation of
     * {@link SecurityManager#checkPackageAccess#checkPackageAccess(java.lang.String)}
     * denies access to the package of this class.</li>
     * </ul>
     * @throws IllegalArgumentException if the method is an instance method and
     * the specified object argument is not an instance of the class or
     * interface declaring the underlying method (or of a subclass or
     * implementor thereof); if the number of actual and formal parameters
     * differ; if an unwrapping conversion for primitive arguments fails; or if,
     * after possible unwrapping, a parameter value cannot be converted to the
     * corresponding formal parameter type by a method invocation conversion.
     * @throws IllegalAccessException if this {@code Method} object is enforcing
     * Java language access control and the underlying method is inaccessible.
     * @throws InvocationTargetException if the underlying method throws an
     * exception.
     * @see Utils#destroy(java.lang.Object)
     * @since 2.0
     */
    public static void destroy(final Map<String, Object> map) throws SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (map != null) {

            Map<String, Object> synchronizedMap = Collections.synchronizedMap(map);

            if (!synchronizedMap.isEmpty()) {

                for (Object o : synchronizedMap.values()) {
                    destroy(o);
                }
            }
        }
    }
}