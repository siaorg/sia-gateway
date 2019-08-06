package com.creditease.gateway.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reflection帮助类
 * 
 * @author peihua
 * 
 */


public class ReflectionHelper {

    private ReflectionHelper() {

    }

    /**
     * constructor by static method
     * 
     * @param className
     * @param paramTypes
     * @param params
     * @param clsLoaders
     * @return
     */
    public static Object newInstance(String className, String methodName, Class<?>[] paramTypes, Object[] params,
            ClassLoader... clsLoaders) {

        Object objInst = null;
        try {
            Class<?> capClass = null;

            if (null != clsLoaders && clsLoaders.length > 0) {
                capClass = tryLoadClass(className, clsLoaders);
            }

            if (null == capClass) {
                capClass = tryLoadClassFromThreadClassLoader(className);
            }

            if (capClass == null)
            {
            	return null;
            }

            objInst = capClass.getMethod(methodName, paramTypes).invoke(null, params);
        }
        catch (ClassCastException e) {
            // ignore
        }
        catch (IllegalAccessException e) {
            // ignore
        }
        catch (IllegalArgumentException e) {
            // ignore
        }
        catch (InvocationTargetException e) {
            // ignore
        }
        catch (NoSuchMethodException e) {
            // ignore
        }
        catch (SecurityException e) {
            // ignore
        }
        return objInst;
    }

    /**
     * newInstance which has Construct params
     * 
     * @param className
     * @param clsLoaders
     * @param params
     * @return
     */
    public static Object newInstance(String className, Class<?>[] paramtype, Object[] params,
            ClassLoader... clsLoaders) {

        Object objInst = null;
        try {
            Class<?> capClass = null;

            if (null != clsLoaders && clsLoaders.length > 0) {
                capClass = tryLoadClass(className, clsLoaders);
            }

            if (null == capClass) {
                capClass = tryLoadClassFromThreadClassLoader(className);
            }

            if (capClass == null)
            {
            	return null;
            }

            @SuppressWarnings("rawtypes")
            Constructor constructor = capClass.getConstructor(paramtype);

            objInst = constructor.newInstance(params);
        }
        catch (ClassCastException e) {
            // ignore
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            // ignore
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            // ignore
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            // ignore
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            // ignore
            e.printStackTrace();
        }
        return objInst;
    }

    /**
     * newInstance
     * 
     * @param className
     * @param clsLoaders
     * @return
     */
    public static Object newInstance(String className, ClassLoader... clsLoaders) {

        Object objInst = null;
        try {
            Class<?> capClass = null;

            if (null != clsLoaders && clsLoaders.length > 0) {
                capClass = tryLoadClass(className, clsLoaders);
            }

            if (null == capClass) {
                capClass = tryLoadClassFromThreadClassLoader(className);
            }

            if (capClass == null)
            {
            	return null;
            }

            objInst = capClass.newInstance();
        }
        catch (ClassCastException e) {
            // ignore
        }
        catch (InstantiationException e) {
            // ignore
        }
        catch (IllegalAccessException e) {
            // ignore
        }
        return objInst;
    }

    /**
     * @param className
     * @param capClass
     * @return
     */
    private static Class<?> tryLoadClassFromThreadClassLoader(String className) {

        Class<?> capClass = null;
        try {
            capClass = Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            capClass = reflectLoadClass(className);
        }
        return capClass;
    }

    /**
     * @param className
     * @param capClass
     * @return
     */
    private static Class<?> reflectLoadClass(String className) {

        Class<?> capClass = null;
        try {
            capClass = ReflectionHelper.class.getClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e1) {
            // ignore
        }
        return capClass;
    }

    /**
     * @param className
     * @param capClass
     * @param clsLoaders
     * @return
     */
    public static Class<?> tryLoadClass(String className, ClassLoader... clsLoaders) {

        Class<?> capClass = null;

        if (null == clsLoaders) {
            return null;
        }

        for (ClassLoader cl : clsLoaders) {
            try {
                capClass = cl.loadClass(className);

                if (null != capClass) {
                    break;
                }

            }
            catch (ClassNotFoundException e) {
                // ignore
                continue;
            }
        }
        return capClass;
    }

    /**
     * loops
     * 
     * @param beInvokeObjectRoot
     * @param methodNames
     * @return
     */
    public static Object invokes(Object beInvokeObjectRoot, LinkedList<String> methodNames,
            LinkedList<Class<?>[]> paramTypes, LinkedList<Object[]> params) {

        if (null == beInvokeObjectRoot || null == methodNames) {
            return null;
        }
        if (methodNames.isEmpty()) {
            return beInvokeObjectRoot;
        }

        Class<?>[] firstParamTypes = null;

        Object[] firstParams = null;
        if (null != paramTypes && null != params) {
            firstParamTypes = paramTypes.getFirst();
            firstParams = params.getFirst();
            paramTypes.removeFirst();
            params.removeFirst();
        }
        Object beInvokeObject = invoke(beInvokeObjectRoot.getClass().getName(), beInvokeObjectRoot,
                methodNames.getFirst(), firstParamTypes, firstParams);
        methodNames.removeFirst();

        return invokes(beInvokeObject, methodNames, paramTypes, params);
    }

    public static Object invokeStatic(String clsName, String methodName, Class<?>[] paramTypes, Object[] params,
            ClassLoader... cl) {

        return invoke(clsName, null, methodName, paramTypes, params, cl);
    }

    public static Object invoke(String clsName, Object clsObject, String methodName, Class<?>[] paramTypes,
            Object[] params, ClassLoader... clsLoaders) {

        if (null == clsName || null == methodName) {
            return null;
        }

        Class<?> cls = tryLoadClass(clsName, clsLoaders);

        if (cls == null) {
            try {
                cls = ReflectionHelper.class.getClassLoader().loadClass(clsName);
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }

        Method method = null;
        try {
            method = (null == paramTypes) ? cls.getDeclaredMethod(methodName)
                    : cls.getDeclaredMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException e) {

            try {
                method = cls.getMethod(methodName, paramTypes);
            }
            catch (NoSuchMethodException e1) {
                // ignore
                return null;
            }
            catch (SecurityException e1) {
                // ignore
                return null;
            }
        }
        catch (SecurityException e) {
            // ignore
            return null;
        }

        try {
            method.setAccessible(true);

            if (null != clsObject) {
                return method.invoke(clsObject, params);
            }
            else {
                return method.invoke(cls, params);
            }
        }
        catch (IllegalAccessException e) {
            // ignore
            return null;
        }
        catch (IllegalArgumentException e) {
            // ignore
            return null;
        }
        catch (InvocationTargetException e) {

            throw new RuntimeException(e.getTargetException());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getAnnotationValue(Class<?> c, Class annoCls, String annoField) {

        Object value = null;
        try {
            if (c.isAnnotationPresent(annoCls)) {
                Annotation anno = c.getAnnotation(annoCls);
                Method mtd = anno.getClass().getDeclaredMethod(annoField);
                value = mtd.invoke(anno);
            }
        }
        catch (Exception e) {
            // ignore
        }

        return value;
    }

    /**
     * @param method
     * @param annocls
     * @param annoField
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getAnnotationValue(Method method, Class annocls, String annoField) {

        Object value = null;
        Annotation anno = method.getAnnotation(annocls);
        if (anno == null)
        {
        	return value;
        }
        try {
            Method mtd = anno.getClass().getDeclaredMethod(annoField);
            value = mtd.invoke(anno);
        }
        catch (Exception e) {
            // ignore
        }

        // if value is ARRAY
        if (value.getClass().isArray()) {

            Object[] vArray = (Object[]) value;

            List<Object> vList = new ArrayList<Object>();

            for (Object vObj : vArray) {

                if (Annotation.class.isAssignableFrom(vObj.getClass())) {

                    Map<String, Object> subAnnofieldValues = new LinkedHashMap<String, Object>();

                    subAnnofieldValues = getAllFieldValuesOfAnnotation(subAnnofieldValues, (Annotation) vObj);

                    vList.add(subAnnofieldValues);
                }
                else {
                    vList.add(vObj);
                }
            }

            return vList;
        }
        // if value is not ARRAY
        else {

            return value;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, Object> getAnnotationAllFieldValues(Class c, Class annoCls) {

        Map<String, Object> fieldValues = new LinkedHashMap<String, Object>();

        Annotation anno = c.getAnnotation(annoCls);
        if (anno == null) {
            // -----xinyuzhou1 START
            Set<Class<?>> interfaces = ReflectionHelper.getAllInterfaces(c, false);
            for (Class intface : interfaces) {
                Annotation anno4Interface = intface.getAnnotation(annoCls);
                if (anno4Interface != null) {
                    return getAllFieldValuesOfAnnotation(fieldValues, anno4Interface);
                }
            }
            // -----xinyuzhou1 END
            return null;
        }

        return getAllFieldValuesOfAnnotation(fieldValues, anno);
    }

    /**
     * 
     * @param method
     * @param annoCls
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, Object> getAnnotationAllFieldValues(Class<?> cls, Method method, Class annoCls) {

        Map<String, Object> fieldValues = new LinkedHashMap<String, Object>();

        Annotation anno = method.getAnnotation(annoCls);
        if (anno == null) {
            // -----xinyuzhou1 START
            Set<Class<?>> interfaces = ReflectionHelper.getAllInterfaces(cls, false);
            for (Class intface : interfaces) {
                Method[] mthd = intface.getMethods();
                Set<Method> mthds = new HashSet<Method>(Arrays.asList(mthd));
                for (Method m : mthds) {
                    if (ifMethodEquals(m, method)) {
                        Annotation anno4Method = m.getAnnotation(annoCls);
                        return null == anno4Method ? null : getAllFieldValuesOfAnnotation(fieldValues, anno4Method);
                    }

                }

            }
            return null;
        }

        return getAllFieldValuesOfAnnotation(fieldValues, anno);
    }

    /**
     * 
     * @param Method
     * @param Method
     * @return boolean
     */
    private static boolean ifMethodEquals(Method m1, Method m2) {

        if (m1.getName().equals(m2.getName())) {
            if (!m1.getReturnType().equals(m2.getReturnType()))
            {   
            	return false;
            }
            Class<?>[] params1 = m1.getParameterTypes();
            Class<?>[] params2 = m2.getParameterTypes();
            if (params1.length == params2.length) {
                for (int i = 0; i < params1.length; i++) {
                    if (params1[i] != params2[i])
                    {
                    	return false;
                    }
                }
                return true;
            }
        }
        return false;

    }
    // -----xinyuzhou1 END

    private static Map<String, Object> getAllFieldValuesOfAnnotation(Map<String, Object> fieldValues, Annotation anno) {

        Method[] mtds = anno.getClass().getDeclaredMethods();

        for (Method mtd : mtds) {

            String name = mtd.getName();

            if ("annotationType".equalsIgnoreCase(name) || "hashCode".equalsIgnoreCase(name)
                    || "toString".equalsIgnoreCase(name)) {
                continue;
            }

            Object value = null;
            try {
                value = mtd.invoke(anno);
            }
            catch (Exception e) {
                // ignore
                continue;
            }

            // should consider Class array
            if (value.getClass().isArray()) {

                Object[] vArray = (Object[]) value;

                List<Object> vList = new ArrayList<Object>();

                for (Object vObj : vArray) {

                    if (Annotation.class.isAssignableFrom(vObj.getClass())) {

                        Map<String, Object> subAnnofieldValues = new LinkedHashMap<String, Object>();

                        subAnnofieldValues = getAllFieldValuesOfAnnotation(subAnnofieldValues, (Annotation) vObj);

                        if (null == subAnnofieldValues || subAnnofieldValues.size() == 0) {
                            continue;
                        }

                        vList.add(subAnnofieldValues);
                    }
                    else {
                        vList.add(vObj);
                    }
                }

                if (vList.size() == 0) {
                    continue;
                }

                fieldValues.put(name, vList);
            }
            else {

                fieldValues.put(name, value);
            }
        }

        return fieldValues;
    }

    /**
     * get static field
     * 
     * @param cls
     * @param fieldName
     * @return
     */
    public static Object getStaticField(Class<?> cls, String fieldName) {

        try {
            Field f = cls.getDeclaredField(fieldName);

            f.setAccessible(true);

            return f.get(null);
        }
        catch (NoSuchFieldException e) {
            // ignore
        }
        catch (SecurityException e) {
            // ignore
        }
        catch (IllegalArgumentException e) {
            // ignore
        }
        catch (IllegalAccessException e) {
            // ignore
        }

        return null;
    }

    public static void setField(Class<?> cls, Object obj, String fieldName, Object value) {

        try {
            Field f = cls.getDeclaredField(fieldName);

            f.setAccessible(true);

            f.set(obj, value);
        }
        catch (NoSuchFieldException e) {
            // ignore
        }
        catch (SecurityException e) {
            // ignore
        }
        catch (IllegalArgumentException e) {
            // ignore
        }
        catch (IllegalAccessException e) {
            // ignore
        }
    }

    public static Object getField(Class<?> cls, Object obj, String fieldName, boolean searchSupperClass) {

        Object res = getField(cls, obj, fieldName);

        if (res == null && cls.getSuperclass() != null && searchSupperClass == true) {
            return getField(cls.getSuperclass(), obj, fieldName, searchSupperClass);
        }

        return res;
    }

    public static Object getField(Class<?> cls, Object obj, String fieldName) {

        try {
            Field f = cls.getDeclaredField(fieldName);

            f.setAccessible(true);

            return f.get(obj);
        }
        catch (NoSuchFieldException e) {
            // ignore
        }
        catch (SecurityException e) {
            // ignore
        }
        catch (IllegalArgumentException e) {
            // ignore
        }
        catch (IllegalAccessException e) {
            // ignore
        }

        return null;
    }

    /**
     * getAllInterfaces
     * 
     * @param cls
     * @param needSuper
     * @return
     */
    public static Set<Class<?>> getAllInterfaces(Class<?> cls, boolean needSuper) {

        Set<Class<?>> ai = new HashSet<Class<?>>();

        Class<?>[] interfaces = cls.getInterfaces();

        if (interfaces != null) {
            ai.addAll(Arrays.asList(interfaces));
        }

        if (needSuper == false) {
            return ai;
        }

        Class<?> superCls = cls.getSuperclass();
        while (superCls != null) {

            Class<?>[] ifs = superCls.getInterfaces();

            if (ifs != null) {
                ai.addAll(Arrays.asList(ifs));
            }

            superCls = superCls.getSuperclass();
        }

        return ai;
    }
}
