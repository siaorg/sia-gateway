/*-
 * <<
 * sag
 * ==
 * Copyright (C) 2019 sia
 * ==
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
 * >>
 */


package com.creditease.gateway.helper;

import java.io.File;
import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
/***
 * 
 * @author admin
 *
 */

public class JvmToolHelper {

    public static final String OSNAME = System.getProperty("os.name").toLowerCase();
    public static final String JMX_CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";

    private static ClassLoader JVMToolClassloader = null;
    private static final Object LOCK = new Object();
    private static Class<?> virtualMachine;
    private static Class<?> virtualMachineDescriptor;
    private static Method method_VMList;
    private static Method method_AttachToVM;
    private static Method method_DetachFromVM;
    private static Method method_GetAgentProperties;
    private static Method method_VMId;
    private static Method method_GetSystemProperties;
    private static Method method_LoadAgent;

    private static Class<?> hostIdentifierClass;
    private static Class<?> monitoredVmClass;
    private static Class<?> vmIdentifierClass;

    private JvmToolHelper() {

    }

    public static String getJVMVendor() {

        return System.getProperty("java.vm.specification.vendor");
    }

    public static boolean isOracleJVM() {

        return "Sun Microsystems Inc.".equals(getJVMVendor()) || getJVMVendor().startsWith("Oracle");
    }

    public static boolean isVMAlive(String procId) {

        if (procId == null || "".equals(procId)) {
            return false;
        }

        initJVMToolJarClassLoader();

        try {
            Object vm = method_AttachToVM.invoke(null, procId);
            if (vm != null) {

                method_DetachFromVM.invoke(vm);
                return true;
            }
        }
        catch (Exception e) {
            // ignore
        }

        return false;
    }

    /**
     * @param needLocalAttachSupport
     * @param vm
     * @param jvmProperties
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private static Properties fillJVMProperties(boolean needLocalAttachSupport, Object vm, Properties jvmProperties)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        if (needLocalAttachSupport) {
            startVMAgent(vm);
            jvmProperties = (Properties) method_GetAgentProperties.invoke(vm, (Object[]) null);
        }

        return jvmProperties;
    }

    private static void startVMAgent(Object vm) {

        try {
            Properties jvmProperties = (Properties) method_GetAgentProperties.invoke(vm, (Object[]) null);
            if (jvmProperties.get(JMX_CONNECTOR_ADDRESS) == null) {

                Properties systemProperties = (Properties) method_GetSystemProperties.invoke(vm, (Object[]) null);

                String agent = systemProperties.getProperty("java.home") + File.separator + "lib" + File.separator
                        + "management-agent.jar";
                method_LoadAgent.invoke(vm, new Object[] { agent });
            }
        }
        catch (Exception e) {
            // ignore
        }
    }

    private static void initJVMToolJarClassLoader() {

        String javaHome = System.getProperty("java.home");

        String tools = javaHome + File.separator + ".." + File.separator + "lib" + File.separator + "tools.jar";

        if (JVMToolClassloader == null) {
            synchronized (LOCK) {
                if (JVMToolClassloader == null) {
                    try {
                        JVMToolClassloader = new URLClassLoader(new URL[] { new File(tools).toURI().toURL() });

                        // virtual machine
                        virtualMachine = JVMToolClassloader.loadClass("com.sun.tools.attach.VirtualMachine");
                        virtualMachineDescriptor = JVMToolClassloader
                                .loadClass("com.sun.tools.attach.VirtualMachineDescriptor");

                        method_VMList = virtualMachine.getMethod("list", (Class[]) null);
                        method_AttachToVM = virtualMachine.getMethod("attach", String.class);
                        method_DetachFromVM = virtualMachine.getMethod("detach");
                        method_GetAgentProperties = virtualMachine.getMethod("getAgentProperties", (Class[]) null);
                        method_VMId = virtualMachineDescriptor.getMethod("id", (Class[]) null);
                        method_GetSystemProperties = virtualMachine.getMethod("getSystemProperties", (Class[]) null);
                        method_LoadAgent = virtualMachine.getMethod("loadAgent", new Class[] { String.class });

                        // java process
                        hostIdentifierClass = JVMToolClassloader.loadClass("sun.jvmstat.monitor.HostIdentifier");
                        monitoredVmClass = JVMToolClassloader.loadClass("sun.jvmstat.monitor.MonitoredVm");
                        vmIdentifierClass = JVMToolClassloader.loadClass("sun.jvmstat.monitor.VmIdentifier");

                    }
                    catch (Exception e) {
                        // ignore
                    }
                }
            }
        }
    }

    public static List<Map<String, String>> getAllJVMProcesses(String host) {

        initJVMToolJarClassLoader();

        // HostIdentifier localHostIdentifier = new HostIdentifier(host);
        Object localHostIdentifier = ReflectionHelper.newInstance("sun.jvmstat.monitor.HostIdentifier",
                new Class[] { String.class }, new Object[] { host }, JVMToolClassloader);

        // localObject1 = MonitoredHost.getMonitoredHost(localHostIdentifier);
        Object localObject1 = ReflectionHelper.invokeStatic("sun.jvmstat.monitor.MonitoredHost", "getMonitoredHost",
                new Class<?>[] { hostIdentifierClass }, new Object[] { localHostIdentifier }, JVMToolClassloader);

        // Set<Integer> localSet = ((MonitoredHost)localObject1).activeVms();
        @SuppressWarnings("unchecked")
        Set<Integer> localSet = (Set<Integer>) ReflectionHelper.invoke("sun.jvmstat.monitor.MonitoredHost", localObject1,
                "activeVms", new Class[] {}, new Object[] {}, JVMToolClassloader);

        if (localSet == null) {
            return Collections.emptyList();
        }

        List<Map<String, String>> vms = new ArrayList<Map<String, String>>();

        for (Integer localIterator : localSet) {

            int pid = localIterator.intValue();

            String str1 = "//" + pid + "?mode=r";

            try {

                // VmIdentifier localVmIdentifier = new VmIdentifier(str1);
                Object localVmIdentifier = ReflectionHelper.newInstance("sun.jvmstat.monitor.VmIdentifier",
                        new Class[] { String.class }, new Object[] { str1 }, JVMToolClassloader);
                // Object localMonitoredVm =
                // ((MonitoredHost)localObject1).getMonitoredVm(localVmIdentifier,
                // 0);
                Object localMonitoredVm = ReflectionHelper.invoke("sun.jvmstat.monitor.MonitoredHost", localObject1,
                        "getMonitoredVm", new Class<?>[] { vmIdentifierClass }, new Object[] { localVmIdentifier },
                        JVMToolClassloader);

                // String mainClass=
                // MonitoredVmUtil.mainClass(localMonitoredVm,true);
                String mainClass = (String) ReflectionHelper.invokeStatic("sun.jvmstat.monitor.MonitoredVmUtil",
                        "mainClass", new Class<?>[] { monitoredVmClass, boolean.class },
                        new Object[] { localMonitoredVm, true }, JVMToolClassloader);
                // String mainArgs= MonitoredVmUtil.mainArgs(localMonitoredVm);
                String mainArgs = (String) ReflectionHelper.invokeStatic("sun.jvmstat.monitor.MonitoredVmUtil", "mainArgs",
                        new Class<?>[] { monitoredVmClass }, new Object[] { localMonitoredVm }, JVMToolClassloader);
                // str3 = MonitoredVmUtil.jvmArgs(localMonitoredVm);
                String jvmArgs = (String) ReflectionHelper.invokeStatic("sun.jvmstat.monitor.MonitoredVmUtil", "jvmArgs",
                        new Class<?>[] { monitoredVmClass }, new Object[] { localMonitoredVm }, JVMToolClassloader);
                // str3 = MonitoredVmUtil.jvmFlags(localMonitoredVm);
                String jvmFlags = (String) ReflectionHelper.invokeStatic("sun.jvmstat.monitor.MonitoredVmUtil", "jvmFlags",
                        new Class<?>[] { monitoredVmClass }, new Object[] { localMonitoredVm }, JVMToolClassloader);

                Map<String, String> map = new LinkedHashMap<String, String>();

                map.put("pid", localIterator.toString());
                map.put("main", mainClass);
                map.put("margs", mainArgs);
                map.put("jargs", jvmArgs);
                map.put("jflags", jvmFlags);

                vms.add(map);

            }
            catch (Exception e) {
                // ignore
            }
        }

        return vms;
    }

    /**
     * isWindows
     * 
     * @return
     */
    public static boolean isWindows() {

        return (OSNAME.indexOf("win") > -1) ? true : false;
    }

    /**
     * getLineSeperator
     * 
     * @return
     */
    public static String getLineSeperator() {

        String marker = "\r\n";
        if (!isWindows()) {
            marker = "\n";
        }

        return marker;
    }

    private static final Set<String> MINORGC = new HashSet<String>();
    private static final Set<String> FULLGC = new HashSet<String>();

    static {

        // Oracle (Sun) HotSpot
        // -XX:+UseSerialGC
        MINORGC.add("Copy");
        // -XX:+UseParNewGC
        MINORGC.add("ParNew");
        // -XX:+UseParallelGC
        MINORGC.add("PS Scavenge");
        // Oracle (BEA) JRockit
        // -XgcPrio:pausetime
        MINORGC.add("Garbage collection optimized for short pausetimes Young Collector");
        // -XgcPrio:throughput
        MINORGC.add("Garbage collection optimized for throughput Young Collector");
        // -XgcPrio:deterministic
        MINORGC.add("Garbage collection optimized for deterministic pausetimes Young Collector");

        // Oracle (Sun) HotSpot
        // -XX:+UseSerialGC
        FULLGC.add("MarkSweepCompact");
        // -XX:+UseParallelGC and (-XX:+UseParallelOldGC or -XX:+UseParallelOldGCCompacting)
        FULLGC.add("PS MarkSweep");
        // -XX:+UseConcMarkSweepGC
        FULLGC.add("ConcurrentMarkSweep");

        // Oracle (BEA) JRockit
        // -XgcPrio:pausetime
        FULLGC.add("Garbage collection optimized for short pausetimes Old Collector");
        // -XgcPrio:throughput
        FULLGC.add("Garbage collection optimized for throughput Old Collector");
        // -XgcPrio:deterministic
        FULLGC.add("Garbage collection optimized for deterministic pausetimes Old Collector");

    }

    public static Map<String, Object> readGCUsage(ObjectInstance oi, MBeanServerConnection mbsc) {

        Map<String, Object> m = new LinkedHashMap<String, Object>();

        ObjectName on = oi.getObjectName();

        String name = on.getKeyProperty("name");

        String gcName = null;

        if (MINORGC.contains(name)) {
            gcName = "mgc";

        }
        else if (FULLGC.contains(name)) {
            gcName = "fgc";
        }

        m.put(gcName + "_count", getMBeanAttrValue(mbsc, on, "CollectionCount"));
        m.put(gcName + "_time", getMBeanAttrValue(mbsc, on, "CollectionTime"));

        return m;
    }

    public static Map<String, Long> readGCUsage(List<GarbageCollectorMXBean> gcmbList) {

        Map<String, Long> m = new LinkedHashMap<String, Long>();

        for (GarbageCollectorMXBean gcmb : gcmbList) {

            String name = gcmb.getName();
            String gcName = null;
            if (MINORGC.contains(name)) {
                gcName = "mgc";

            }
            else if (FULLGC.contains(name)) {
                gcName = "fgc";
            }

            if (gcName == null) {
                continue;
            }

            m.put(gcName + "_count", gcmb.getCollectionCount());
            m.put(gcName + "_time", gcmb.getCollectionTime());
        }

        return m;
    }

    public static Map<String, Object> readHeapPoolUsage(ObjectInstance oi, MBeanServerConnection mbsc) {

        Map<String, Object> m = new LinkedHashMap<String, Object>();

        ObjectName on = oi.getObjectName();

        String jvmMemPoolName = getHeapPoolName(on.getKeyProperty("name"));

        m.put(jvmMemPoolName + "_use", getMBeanAttrValue(mbsc, on, "Usage", "used"));
        m.put(jvmMemPoolName + "_commit", getMBeanAttrValue(mbsc, on, "Usage", "committed"));
        m.put(jvmMemPoolName + "_max", getMBeanAttrValue(mbsc, on, "Usage", "max"));
        m.put(jvmMemPoolName + "_init", getMBeanAttrValue(mbsc, on, "Usage", "init"));

        return m;
    }

    public static Map<String, Long> readHeapPoolUsage(List<MemoryPoolMXBean> pmbList) {

        Map<String, Long> m = new LinkedHashMap<String, Long>();

        for (MemoryPoolMXBean mpmb : pmbList) {

            String jvmMemPoolName = getHeapPoolName(mpmb.getName().trim());

            MemoryUsage mu = mpmb.getUsage();

            m.put(jvmMemPoolName + "_use", mu.getUsed());
            m.put(jvmMemPoolName + "_commit", mu.getCommitted());
            m.put(jvmMemPoolName + "_max", mu.getMax());
            m.put(jvmMemPoolName + "_init", mu.getInit());
        }

        return m;
    }

    private static String getHeapPoolName(String poolName) {

        String jvmMemPoolName = poolName.toLowerCase();

        if (jvmMemPoolName.indexOf("code") > -1) {
            return "code";
        }
        else if (jvmMemPoolName.indexOf("old") > -1 || jvmMemPoolName.indexOf("tenured") > -1) {
            return "old";
        }
        else if (jvmMemPoolName.indexOf("eden") > -1) {
            return "eden";
        }
        else if (jvmMemPoolName.indexOf("survivor") > -1) {
            return "surv";
        }
        else if (jvmMemPoolName.indexOf("perm") > -1 || jvmMemPoolName.indexOf("metaspace") > -1) {
            return "perm";
        }
        else if (jvmMemPoolName.indexOf("compressed") > -1 && jvmMemPoolName.indexOf("class") > -1) {
            return "compressed";
        }

        return jvmMemPoolName;
    }

    public static Map<String, Object> readClassLoadUsage(ObjectInstance oi, MBeanServerConnection mbsc) {

        Map<String, Object> m = new LinkedHashMap<String, Object>();

        ObjectName on = oi.getObjectName();

        m.put("class_total", getMBeanAttrValue(mbsc, on, "TotalLoadedClassCount"));
        m.put("class_load", getMBeanAttrValue(mbsc, on, "LoadedClassCount"));
        m.put("class_unload", getMBeanAttrValue(mbsc, on, "UnloadedClassCount"));

        return m;
    }

    public static Map<String, Long> readClassLoadUsage(ClassLoadingMXBean clmb) {

        Map<String, Long> m = new LinkedHashMap<String, Long>();

        m.put("class_total", clmb.getTotalLoadedClassCount());
        m.put("class_load", new Long(clmb.getLoadedClassCount()));
        m.put("class_unload", clmb.getUnloadedClassCount());

        return m;
    }

    public static Map<String, Object> readHeapUsage(ObjectInstance oi, MBeanServerConnection mbsc) {

        Map<String, Object> m = new LinkedHashMap<String, Object>();

        ObjectName on = oi.getObjectName();

        m.put("heap_use", getMBeanAttrValue(mbsc, on, "HeapMemoryUsage", "used"));
        m.put("heap_commit", getMBeanAttrValue(mbsc, on, "HeapMemoryUsage", "committed"));
        m.put("heap_max", getMBeanAttrValue(mbsc, on, "HeapMemoryUsage", "max"));
        m.put("heap_init", getMBeanAttrValue(mbsc, on, "HeapMemoryUsage", "init"));

        return m;
    }

    public static Map<String, Long> readHeapUsage(MemoryUsage mu) {

        Map<String, Long> m = new LinkedHashMap<String, Long>();

        m.put("heap_use", mu.getUsed());
        m.put("heap_commit", mu.getCommitted());
        m.put("heap_max", mu.getMax());
        m.put("heap_init", mu.getInit());

        return m;
    }

    public static Map<String, Object> readNonHeapUsage(ObjectInstance oi, MBeanServerConnection mbsc) {

        Map<String, Object> m = new LinkedHashMap<String, Object>();

        ObjectName on = oi.getObjectName();

        m.put("noheap_use", getMBeanAttrValue(mbsc, on, "NonHeapMemoryUsage", "used"));
        m.put("noheap_commit", getMBeanAttrValue(mbsc, on, "NonHeapMemoryUsage", "committed"));
        m.put("noheap_max", getMBeanAttrValue(mbsc, on, "NonHeapMemoryUsage", "max"));
        m.put("noheap_init", getMBeanAttrValue(mbsc, on, "NonHeapMemoryUsage", "init"));

        return m;
    }

    public static Map<String, Long> readNonHeapUsage(MemoryUsage mu) {

        Map<String, Long> m = new LinkedHashMap<String, Long>();

        m.put("noheap_use", mu.getUsed());
        m.put("noheap_commit", mu.getCommitted());
        m.put("noheap_max", mu.getMax());
        m.put("noheap_init", mu.getInit());

        return m;
    }

    public static Map<String, Object> readThreadUsage(ObjectInstance oi, MBeanServerConnection mbsc) {

        Map<String, Object> m = new LinkedHashMap<String, Object>();

        ObjectName on = oi.getObjectName();

        m.put("thread_live", getMBeanAttrValue(mbsc, on, "ThreadCount"));
        m.put("thread_daemon", getMBeanAttrValue(mbsc, on, "DaemonThreadCount"));
        m.put("thread_peak", getMBeanAttrValue(mbsc, on, "PeakThreadCount"));
        m.put("thread_started", getMBeanAttrValue(mbsc, on, "TotalStartedThreadCount"));

        return m;
    }

    public static Map<String, Long> readThreadUsage(ThreadMXBean tmb) {

        Map<String, Long> m = new LinkedHashMap<String, Long>();

        m.put("thread_live", new Long(tmb.getThreadCount()));
        m.put("thread_daemon", new Long(tmb.getDaemonThreadCount()));
        m.put("thread_peak", new Long(tmb.getPeakThreadCount()));
        m.put("thread_started", tmb.getTotalStartedThreadCount());

        return m;
    }

    public static Map<String, Object> readCPUUsage(ObjectInstance oi, MBeanServerConnection mbsc) {

        Map<String, Object> m = new LinkedHashMap<String, Object>();

        ObjectName on = oi.getObjectName();

        Object procCPU = getMBeanAttrValue(mbsc, on, "ProcessCpuLoad");
        Object systemCPU = getMBeanAttrValue(mbsc, on, "SystemCpuLoad");

        if (procCPU == null) {
            procCPU = new Long(-1);
            systemCPU = new Long(-1);
        }
        else {
            DecimalFormat formatter = new DecimalFormat("00.0");

            procCPU = Double.valueOf(formatter.format((Double) procCPU * 100));
            systemCPU = Double.valueOf(formatter.format((Double) systemCPU * 100));
        }

        m.put("cpu_p", procCPU);
        m.put("cpu_s", systemCPU);

        return m;
    }


    /**
     * obtain current process cpu utilization if jdk version is 1.6 by-hongqiangwei
     */


    private static Object getMBeanAttrValue(MBeanServerConnection mbsc, ObjectName on, String attr, String key) {

        Object o = getMBeanAttrValue(mbsc, on, attr);

        if (o == null) {
            return null;
        }

        if (CompositeData.class.isAssignableFrom(o.getClass())) {

            CompositeData cd = (CompositeData) o;

            return cd.get(key);
        }

        return null;
    }

    private static Object getMBeanAttrValue(MBeanServerConnection mbsc, ObjectName on, String attr) {

        try {
            return mbsc.getAttribute(on, attr);
        }
        catch (AttributeNotFoundException e) {
            // ignore
        }
        catch (InstanceNotFoundException e) {
            // ignore
        }
        catch (MBeanException e) {
            // ignore
        }
        catch (ReflectionException e) {
            // ignore
        }
        catch (IOException e) {
            // ignore
        }

        return null;
    }

    /**
     * 获取当前进程id
     * 
     * @return
     */
    public static String getCurrentProcId() {

        String name = ManagementFactory.getRuntimeMXBean().getName();

        String pid = name.split("@")[0];

        return pid;
    }

}
