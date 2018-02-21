package com.solace.services.loader.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class SolaceServiceInfoTest {

    @Test
    public void equalTest() {
        SolaceServiceInfo ssi = getTestSolaceServiceInfo();
        SolaceServiceInfo otherSsi = getTestSolaceServiceInfo();
        assertEquals(ssi, otherSsi);
    }

    @Test
    public void hashCodeTest() {
        SolaceServiceInfo ssi = getTestSolaceServiceInfo();
        SolaceServiceInfo otherSsi = getTestSolaceServiceInfo();
        assertEquals(ssi.hashCode(), otherSsi.hashCode());
    }

    @Test
    public void toStringTest() {
        SolaceServiceInfo ssi = getTestSolaceServiceInfo();
        SolaceServiceInfo otherSsi = getTestSolaceServiceInfo();
        assertEquals(ssi.toString(), otherSsi.toString());
    }

    private SolaceServiceInfo getTestSolaceServiceInfo() {
        SolaceServiceInfo ssi = new SolaceServiceInfo();
        ssi.setId("full-credentials-instance");
        ssi.setClientUsername("sample-client-username");
        ssi.setClientPassword("sample-client-password");
        ssi.setMsgVpnName("sample-msg-vpn");
        ssi.setSmfHost("tcp://192.168.1.50:7000");
        ssi.setSmfTlsHost("tcps://192.168.1.50:7003");
        ssi.setSmfZipHost("tcp://192.168.1.50:7001");
        ssi.setJmsJndiUri("smf://192.168.1.50:7000");
        ssi.setJmsJndiTlsUri("smfs://192.168.1.50:7003");
        ssi.setMqttUris(Collections.singletonList("tcp://192.168.1.50:7020"));
        ssi.setMqttTlsUris(Arrays.asList("ssl://192.168.1.50:7021", "ssl://192.168.1.51:7021"));
        ssi.setMqttWsUris(Collections.singletonList("ws://192.168.1.50:7022"));
        ssi.setMqttWssUris(Arrays.asList("wss://192.168.1.50:7023", "wss://192.168.1.51:7023"));
        ssi.setRestUris(Collections.singletonList("http://192.168.1.50:7018"));
        ssi.setRestTlsUris(Collections.singletonList("https://192.168.1.50:7019"));
        ssi.setAmqpUris(Collections.singletonList("http://192.168.1.50:7016"));
        ssi.setAmqpTlsUris(Collections.singletonList("https://192.168.1.50:7017"));
        ssi.setManagementHostnames(Collections.singletonList("vmr-Medium-VMR-0"));
        ssi.setManagementUsername("sample-mgmt-username");
        ssi.setManagementPassword("sample-mgmt-password");
        ssi.setActiveManagementHostname("vmr-medium-web");
        return ssi;
    }
}
