package com.solace.services.core.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SolaceServiceCredentialsImplTest {

    @Test
    public void testEqual() {
        SolaceServiceCredentialsImpl ssi = getTestSolaceServiceInfo();
        SolaceServiceCredentialsImpl otherSsi = getTestSolaceServiceInfo();
        assertEquals(ssi, otherSsi);
    }

    @Test
    public void testHashCode() {
        SolaceServiceCredentialsImpl ssi = getTestSolaceServiceInfo();
        SolaceServiceCredentialsImpl otherSsi = getTestSolaceServiceInfo();
        assertEquals(ssi.hashCode(), otherSsi.hashCode());
    }

    @Test
    public void testToString() {
        SolaceServiceCredentialsImpl ssi = getTestSolaceServiceInfo();
        SolaceServiceCredentialsImpl otherSsi = getTestSolaceServiceInfo();
        assertEquals(ssi.toString(), otherSsi.toString());
    }

    @Test
    public void testIsHA() {
        SolaceServiceCredentialsImpl ssi = getTestSolaceServiceInfo();
        ssi.setSmfHosts(Collections.singletonList("tcp://192.168.1.50:7000"));
        assertFalse(ssi.isHA());
        ssi.setSmfHosts(Arrays.asList("tcp://192.168.1.50:7000", "tcp://192.168.1.51:7000"));
        assertTrue(ssi.isHA());
    }

    private SolaceServiceCredentialsImpl getTestSolaceServiceInfo() {
        SolaceServiceCredentialsImpl ssi = new SolaceServiceCredentialsImpl();
        ssi.setId("full-credentials-instance");
        ssi.setClientUsername("sample-client-username");
        ssi.setClientPassword("sample-client-password");
        ssi.setMsgVpnName("sample-msg-vpn");
        ssi.setSmfHosts(Collections.singletonList("tcp://192.168.1.50:7000"));
        ssi.setSmfTlsHosts(Collections.singletonList("tcps://192.168.1.50:7003"));
        ssi.setSmfZipHosts(Collections.singletonList("tcp://192.168.1.50:7001"));
        ssi.setJmsJndiUris(Collections.singletonList("smf://192.168.1.50:7000"));
        ssi.setJmsJndiTlsUris(Collections.singletonList("smfs://192.168.1.50:7003"));
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
        ssi.setDmrClusterName("sample-dmr-cluster");
        ssi.setDmrClusterPassword("sample-dmr-cluster-password");
        return ssi;
    }
}
