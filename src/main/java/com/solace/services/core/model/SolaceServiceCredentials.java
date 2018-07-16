package com.solace.services.core.model;

import java.util.List;

/**
 * A POJO to wrap the SolaceMessaging Cloud Foundry Service. This class provides easy access to all of the information
 * in SOLCAP_SERVICES without extra dependencies on any Solace Enterprise APIs.
 *
 * For more details see the GitHub project:
 *    - https://github.com/SolaceProducts/solace-services-info
 *
 */
public interface SolaceServiceCredentials {

    String getId();
    String getClientUsername();
    String getClientPassword();
    String getMsgVpnName();
    String getSmfHost();
    String getSmfTlsHost();
    String getSmfZipHost();
    String getJmsJndiUri();
    String getJmsJndiTlsUri();
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
    boolean isHA();
    }
