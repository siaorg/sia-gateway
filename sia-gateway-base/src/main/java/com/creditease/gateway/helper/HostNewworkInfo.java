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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
/***
 * 
 * @author admin
 *
 */
public class HostNewworkInfo {

    private static List<InetAddress> ips;

    private List<InetAddress> readInfo(String name) {

        List<InetAddress> ipList = new ArrayList<InetAddress>();

        InetAddress ip = null;
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();

            while (netInterfaces.hasMoreElements()) {

                NetworkInterface ni = netInterfaces.nextElement();

                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    ip = ips.nextElement();

                    if (!ni.isVirtual() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {

                        if (name != null) {

                            if (ni.getName().equalsIgnoreCase(name)) {
                                ipList.add(ip);
                            }
                        }
                        else {

                            ipList.add(ip);
                        }

                    }
                }

            }
        }
        catch (SocketException e) {
            // ignore
        }

        return ipList;
    }

    public List<InetAddress> getIPs(String name) {

        return this.readInfo(name);
    }

    public List<InetAddress> getIPs() {

        if (ips == null) {
            ips = this.readInfo(null);
        }
        return ips;
    }

    public String getNetCardName(String ipString) {

        Enumeration<NetworkInterface> netInterfaces;
        try {
            InetAddress ipAddr;
            ipAddr = InetAddress.getByName(ipString);
            netInterfaces = NetworkInterface.getNetworkInterfaces();

            while (netInterfaces.hasMoreElements()) {

                NetworkInterface ni = netInterfaces.nextElement();

                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ip = ips.nextElement();

                    if (!ni.isVirtual() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {

                        if (ip.equals(ipAddr)) {

                            return ni.getName();
                        }

                    }
                }

            }
        }
        catch (SocketException e) {
            // ignore
        }
        catch (UnknownHostException e) {
            // ignore
        }

        return null;
    }
}
