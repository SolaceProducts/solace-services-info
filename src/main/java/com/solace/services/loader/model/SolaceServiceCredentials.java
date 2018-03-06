package com.solace.services.loader.model;

import java.util.List;

/**
 * A POJO to wrap the SolaceMessaging Cloud Foundry Service. This class provides easy access to all of the information
 * in SOLCAP_SERVICES without extra dependencies on any Solace Enterprise APIs.
 *
 * For more details see the GitHub project:
 *    - https://github.com/SolaceProducts/sl-solace-messaging-service-info
 *
 */
public interface SolaceServiceCredentials {

    String getId();
    String getClientUsername();
    String getClientPassword();
    String getMsgVpnName();
    List<String> getSmfHosts();
    List<String> getSmfTlsHosts();
    List<String> getSmfZipHosts();
    List<String> getJmsJndiUris();
    List<String> getJmsJndiTlsUris();
    List<String> getRestUris();
    List<String> getRestTlsUris();
    List<String> getAmqpUris();
    List<String> getAmqpTlsUris();
    List<String> getMqttUris();
    List<String> getMqttTlsUris();
    List<String> getMqttWsUris();
    List<String> getMqttWssUris();
    List<String> getManagementHostnames();
    String getManagementPassword();
    String getManagementUsername();
    String getActiveManagementHostname();
    }
