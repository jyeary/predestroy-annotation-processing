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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 2.0
 */
public class UtilsTest {

    public UtilsTest() {
    }

    @Test
    public void testDestroy() throws IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException {
        class A {

            @PreDestroy
            private void destroy() {
                System.out.println("@PreDestroy invoked!");
            }
        }
        System.out.println("destroy()");
        Object object = new A();
        boolean result = Utils.destroy(object);
        assertTrue(result);
    }

    @Test
    public void testDestroyMultiples() throws IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException {
        class A {

            @PreDestroy
            private void destroy() {
                System.out.println("@PreDestroy invoked!");
            }

            @PreDestroy
            private void destroy$1() {
                System.out.println("Oops!");
            }
        }
        System.out.println("destroy() multiple annotations.");
        Object object = new A();
        boolean result = Utils.destroy(object);
        assertTrue(result);
    }

    @Test(expected = InvocationTargetException.class)
    public void testDestroyInvocationTargetException() throws IllegalArgumentException,
            SecurityException, IllegalAccessException, InvocationTargetException {
        class A {

            @PreDestroy
            private void destroy() {
                throw new RuntimeException();
            }
        }
        System.out.println("destroy() throws InvocationTargetException");
        Object object = new A();
        Utils.destroy(object);
    }

    @Test
    public void testDestroyUnannotated() throws IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException {
        class A {

            public void destroy() {
                System.out.println("destroy() invoked!");
            }
        }
        System.out.println("destroy() called on unannotated object");
        Object object = new A();
        boolean result = Utils.destroy(object);
        assertFalse(result);
    }

    @Test
    public void testDestroyWrongAnnotation() throws IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException {
        class A {

            @PostConstruct
            public void init() {
                System.out.println("@PostConstruct invoked!");
            }
        }
        System.out.println("destroy() called on @PostConstruct object");
        Object object = new A();
        boolean result = Utils.destroy(object);
        assertFalse(result);
    }

    @Test
    public void testDestroyMap() throws SecurityException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        System.out.println("destroy() called on Map<String,Object>()");

        Map<String, Object> map = new HashMap<>();

        class A {

            @PreDestroy
            private void destroy() {
                System.out.println("@PreDestroy invoked!");
            }
        }

        class B {

            public void destroy() {
                System.out.println("destroy() invoked!");
            }
        }

        map.put("A", new A());
        map.put("B", new B());
        Utils.destroy(map);
    }

    @Test
    public void testDestroyNullMap() throws SecurityException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        System.out.println("destroy() called on a null Map<String,Object>()");
        Map<String, Object> map = null;
        Utils.destroy(map);
    }

    @Test
    public void testDestroyEmptyMap() throws SecurityException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        System.out.println("destroy() called on an empty Map<String,Object>()");
        Map<String, Object> map = new HashMap<>();
        Utils.destroy(map);
    }

}