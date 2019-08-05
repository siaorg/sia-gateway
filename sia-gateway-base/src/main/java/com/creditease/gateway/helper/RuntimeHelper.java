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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/***
 * 
 * @author admin
 *
 */
public class RuntimeHelper {

	private static ExecutorService runtimeHelperExcutor = new ThreadPoolExecutor(10, 10,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue(10),
            new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "RuntimeHelper");
               thread.setDaemon(true);
               return thread;
             }
	       }
	);
    /**
     * 
     * RunTimeTask description: execution runtime
     *
     */
    private static class RunTimeTask implements Callable<String> {

        String[] cmd;

        public RunTimeTask(String... cmds) {
            cmd = cmds;
        }

        @Override
        public String call() throws Exception {

            StringBuilder output = new StringBuilder();
            BufferedReader br = null;
            BufferedReader ebr = null;
            InputStream in = null;
            InputStream ein = null;

            Process ps = null;

            try {

                if (cmd.length == 1) {
                    ps = Runtime.getRuntime().exec(cmd[0]);
                }
                else {
                    ps = Runtime.getRuntime().exec(cmd);
                }

                in = ps.getInputStream();

                ein = ps.getErrorStream();
                
                ps.getOutputStream().close();

                br = new BufferedReader(new InputStreamReader(in));
                String line;

                while ((line = br.readLine()) != null) {
                    output.append(line + "\n");
                }

                ebr = new BufferedReader(new InputStreamReader(ein));

                while ((line = ebr.readLine()) != null) {
                    output.append(line + "\n");
                }

                return output.toString();
            }
            catch (Exception e) {
                return output.toString();

            }
            finally

            {
                if (br != null) {
                    br.close();
                }
                if (ebr != null) {
                    ebr.close();
                }
                ps.destroy();
            }
        }
    }

    private static String writeCmdFile(String cmd, String shellParentPath) throws Exception {

        String fileExt = ".bat";

//        boolean isWindows = JVMToolHelper.isWindows();
//
//        if (!isWindows) {
//            fileExt = ".sh";
//        }

        String fileName = "s" + cmd.hashCode() + ".uav" + fileExt;

        String filePath = shellParentPath + "/" + fileName;

        if (!IoHelper.exists(filePath)) {

            if (!IoHelper.exists(shellParentPath)) {
                IoHelper.createFolder(shellParentPath);
            }

            StringBuilder sb = new StringBuilder();

//            if (!isWindows) {
//                sb.append("#!/bin/bash" + System.lineSeparator());
//            }

            sb.append(cmd);

            String data = sb.toString();

            IoHelper.writeTxtFile(filePath, data, "utf-8", false);

//            if (!isWindows) {
//                // we need get the execute auth
//                RuntimeHelper.exec("sh -c \"chmod u+x " + filePath + "\"");
//            }
        }

        return filePath;
    }

    private static String exePrefix() {

        //boolean isWindows = JVMToolHelper.isWindows();

        String exePrefix = "";

//        if (!isWindows) {
//            exePrefix = "sh ";
//        }
        return exePrefix;
    }

    /**
     * 自动生成并执行脚本，bat或shell
     * 
     * @param cmd
     * @param shellParentPath
     * @return
     * @throws Exception
     */
    public static String exeShell(String cmd, String shellParentPath) throws Exception {

        String exePrefix = exePrefix();

        String output = exec(5000, exePrefix + writeCmdFile(cmd, shellParentPath));

        return output;
    }

    /**
     * 自动生成并执行脚本，bat或shell
     * 
     * @param cmd
     * @param shellParentPath
     * @return
     * @throws Exception
     */
    public static String exeShell(String cmd, String shellParentPath, long timeout) throws Exception {

        String exePrefix = exePrefix();

        String output = exec(timeout, exePrefix + writeCmdFile(cmd, shellParentPath));

        return output;
    }

    /**
     * execute cmd / shell command
     * 
     * @param cmd
     * @return
     * @throws Exception
     */
    public static String exec(String cmd) throws Exception {

        return exec(5000, cmd);
    }

    /**
     * exec
     * 
     * @param timeout
     * @param cmd
     * @return
     * @throws Exception
     */

    
    public static String exec(long timeout, String... cmd) throws Exception {

        String output = new String();

        RunTimeTask task = new RunTimeTask(cmd);

        FutureTask<String> futureTask = new FutureTask<String>(task);

        runtimeHelperExcutor.submit(futureTask);

        try {
            output = futureTask.get(timeout, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e) {
            futureTask.cancel(true);
        }
        catch (Exception e) {
            futureTask.cancel(true);
        }
        finally {
            if (!futureTask.isCancelled()) {
                futureTask.cancel(true);
            }
            runtimeHelperExcutor.shutdownNow();
        }

        return output;
    }

}
