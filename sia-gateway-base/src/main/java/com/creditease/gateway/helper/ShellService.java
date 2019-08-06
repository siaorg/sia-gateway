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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
/***
 * 
 * @author admin
 *
 */
@Service
public class ShellService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShellService.class);

    /**
     * 通过 ChannelExec 交互，上下文没有关联
     * 
     * @param host
     * @param port
     * @param user
     * @param password
     * @param commands
     * @return
     */
    public String[] channelExecCommand(String host, int port, String user, String password, String[] commands) {

        String[] responses = new String[commands.length];
        JSch jsch = new JSch();
        Session session = null;
        String reponse = "FAIL";
        try {
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            for (int i = 0; i < commands.length; i++) {
                ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
                try {
                    InputStream in = channelExec.getInputStream();
                    channelExec.setCommand(commands[i]);
                    LOGGER.info("command:[" + commands[i] + "]");
                    channelExec.setErrStream(System.err);
                    channelExec.connect();
                    reponse = getStringFromStream(in);

                }
                catch (Exception e) {
                    LOGGER.error("run command fail:", e);
                    reponse = exception2String(e);
                }
                finally {
                    channelExec.disconnect();
                    responses[i] = reponse;
                    LOGGER.info("response:[" + reponse + "]");
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("run command fail:", e);
        }
        finally {
            if (session != null) {
                session.disconnect();
            }
        }
        return responses;
    }

    /**
     * 通过 ChannelShell 交互，上下文语义关联
     * 
     * @param host
     * @param port
     * @param user
     * @param password
     * @param commands
     * @return
     */
    public String channelShellCommand(String host, int port, String user, String password, String[] commands) {

        String reponse = "FAIL";
        JSch jsch = new JSch();
        Session session = null;

        try {
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            ChannelShell channel = (ChannelShell) session.openChannel("shell");
            channel.connect();
            // 从远程端到达的所有数据都能从这个流中读取到
            InputStream inputStream = channel.getInputStream();
            // 写入该流的所有数据都将发送到远程端。
            OutputStream outputStream = channel.getOutputStream();
            // 使用PrintWriter流的目的就是为了使用println这个方法
            // 好处就是不需要每次手动给字符串加\n
            PrintWriter printWriter = new PrintWriter(outputStream);

            for (int i = 0; i < commands.length; i++) {
                LOGGER.info("command:[" + commands[i] + "]");
                printWriter.println(commands[i]);

            }
            // 加上个就是为了，结束本次交互
            printWriter.println("exit");
            printWriter.flush();
            // 获取结果
            reponse = getStringFromStream(inputStream);

        }
        catch (Exception e) {
            LOGGER.error("run command fail:", e);
        }
        finally {
            if (session != null) {
                session.disconnect();
            }
        }
        LOGGER.info("response:[" + reponse + "]");
        return reponse;
    }

    /**
     * 将本地文件名为src的文件上传到目标服务器，目标文件名为dst，若dst为目录，则目标文件名将与src文件名相同。 采用默认的传输模式：OVERWRITE
     * 
     * @param host
     * @param port
     * @param user
     * @param password
     * @param src
     * @param dst
     *            如果 dst 是目录（注意，目录必须存在），则目标文件名与源文件名相同，文件的绝对路径为 dst/源文件名。 如果 dst 是文件名，文件的绝对路径为 dst。
     * @return
     */
    public String channelSftpCommand(String host, int port, String user, String password, String src, String dst) {

        String reponse = "FAIL";
        JSch jsch = new JSch();
        Session session = null;

        try {
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            // 设置登陆超时时间
            session.connect(10000);
            // 创建sftp通信通道
            ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect(10000);
            /**
             * @see http://www.cnblogs.com/longyg/archive/2012/06/25/2556576.html
             */
            sftp.put(src, dst);
            reponse = "SUCCESS";
        }
        catch (Exception e) {
            LOGGER.error("run command fail:", e);
        }
        finally {
            if (session != null) {
                session.disconnect();
            }
        }
        LOGGER.info("response:[" + reponse + "]");
        return reponse;
    }

    private String getStringFromStream(InputStream in) throws IOException, UnsupportedEncodingException {

        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        try {
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in2b = swapStream.toByteArray();
            return new String(in2b, "UTF-8");
        }
        catch (IOException e) {
            // ignore
        }
        return "";
    }

    private String exception2String(Throwable e) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

}
