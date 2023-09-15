package com.daqem.limitedlife.forge;

import com.daqem.limitedlife.LimitedLife;

public class SideProxyForge {

    SideProxyForge() {
        LimitedLife.init();
    }

    public static class Server extends SideProxyForge {
        Server() {

        }
    }

    public static class Client extends SideProxyForge {

        Client() {
        }
    }
}
