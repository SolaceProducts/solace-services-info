package com.solace.services.loader.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/*
 * A POJO to wrap the SolaceMessaging Cloud Foundry Service. This class provides easy access to all of the information
 * in the SCLOUD_SERVICES without extra dependencies on any Solace Enterprise APIs.
 *
 * For more details see the GitHub project:
 *    - https://github.com/SolaceProducts/sl-solace-messaging-service-info
 *
 */
public class SolaceServiceInfo {

    private String id;
    private String clientUsername;
    private String clientPassword;
    private String msgVpnName;
    private String smfHost;
    private String smfTlsHost;
    private String smfZipHost;
    private String jmsJndiUri;
    private String jmsJndiTlsUri;
    private List<String> restUris;
    private List<String> restTlsUris;
    private List<String> amqpUris;
    private List<String> amqpTlsUris;
    private List<String> mqttUris;
    private List<String> mqttTlsUris;
    private List<String> mqttWsUris;
    private List<String> mqttWssUris;
    private List<String> managementHostnames;
    private String managementPassword;
    private String managementUsername;
    private String activeManagementHostname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public String getMsgVpnName() {
        return msgVpnName;
    }

    public void setMsgVpnName(String msgVpnName) {
        this.msgVpnName = msgVpnName;
    }

    public String getSmfHost() {
        return smfHost;
    }

    public void setSmfHost(String smfHost) {
        this.smfHost = smfHost;
    }

    public String getSmfTlsHost() {
        return smfTlsHost;
    }

    public void setSmfTlsHost(String smfTlsHost) {
        this.smfTlsHost = smfTlsHost;
    }

    public String getSmfZipHost() {
        return smfZipHost;
    }

    public void setSmfZipHost(String smfZipHost) {
        this.smfZipHost = smfZipHost;
    }

    public String getJmsJndiUri() {
        return jmsJndiUri;
    }

    public void setJmsJndiUri(String jmsJndiUri) {
        this.jmsJndiUri = jmsJndiUri;
    }

    public String getJmsJndiTlsUri() {
        return jmsJndiTlsUri;
    }

    public void setJmsJndiTlsUri(String jmsJndiTlsUri) {
        this.jmsJndiTlsUri = jmsJndiTlsUri;
    }

    public List<String> getRestUris() {
        return restUris;
    }

    public void setRestUris(List<String> restUris) {
        this.restUris = restUris;
    }

    public List<String> getRestTlsUris() {
        return restTlsUris;
    }

    public void setRestTlsUris(List<String> restTlsUris) {
        this.restTlsUris = restTlsUris;
    }

    public List<String> getAmqpUris() {
        return amqpUris;
    }

    public void setAmqpUris(List<String> amqpUris) {
        this.amqpUris = amqpUris;
    }

    public List<String> getAmqpTlsUris() {
        return amqpTlsUris;
    }

    public void setAmqpTlsUris(List<String> amqpTlsUris) {
        this.amqpTlsUris = amqpTlsUris;
    }

    public List<String> getMqttUris() {
        return mqttUris;
    }

    public void setMqttUris(List<String> mqttUris) {
        this.mqttUris = mqttUris;
    }

    public List<String> getMqttTlsUris() {
        return mqttTlsUris;
    }

    public void setMqttTlsUris(List<String> mqttTlsUris) {
        this.mqttTlsUris = mqttTlsUris;
    }

    public List<String> getMqttWsUris() {
        return mqttWsUris;
    }

    public void setMqttWsUris(List<String> mqttWsUris) {
        this.mqttWsUris = mqttWsUris;
    }

    public List<String> getMqttWssUris() {
        return mqttWssUris;
    }

    public void setMqttWssUris(List<String> mqttWssUris) {
        this.mqttWssUris = mqttWssUris;
    }

    public List<String> getManagementHostnames() {
        return managementHostnames;
    }

    public void setManagementHostnames(List<String> managementHostnames) {
        this.managementHostnames = managementHostnames;
    }

    public String getManagementPassword() {
        return managementPassword;
    }

    public void setManagementPassword(String managementPassword) {
        this.managementPassword = managementPassword;
    }

    public String getManagementUsername() {
        return managementUsername;
    }

    public void setManagementUsername(String managementUsername) {
        this.managementUsername = managementUsername;
    }

    public String getActiveManagementHostname() {
        return activeManagementHostname;
    }

    public void setActiveManagementHostname(String activeManagementHostname) {
        this.activeManagementHostname = activeManagementHostname;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("clientUsername", clientUsername)
                .append("clientPassword", clientPassword)
                .append("msgVpnName", msgVpnName)
                .append("smfHost", smfHost)
                .append("smfTlsHost", smfTlsHost)
                .append("smfZipHost", smfZipHost)
                .append("jmsJndiUri", jmsJndiUri)
                .append("jmsJndiTlsUri", jmsJndiTlsUri)
                .append("restUris", restUris)
                .append("restTlsUris", restTlsUris)
                .append("amqpUris", amqpUris)
                .append("amqpTlsUris", amqpTlsUris)
                .append("mqttUris", mqttUris)
                .append("mqttTlsUris", mqttTlsUris)
                .append("mqttWsUris", mqttWsUris)
                .append("mqttWssUris", mqttWssUris)
                .append("managementHostnames", managementHostnames)
                .append("managementPassword", managementPassword)
                .append("managementUsername", managementUsername)
                .append("activeManagementHostname", activeManagementHostname)
                .toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31)
                .append(id)
                .append(clientUsername)
                .append(clientPassword)
                .append(msgVpnName)
                .append(smfHost)
                .append(smfTlsHost)
                .append(smfZipHost)
                .append(jmsJndiUri)
                .append(jmsJndiTlsUri)
                .append(restUris)
                .append(restTlsUris)
                .append(amqpUris)
                .append(amqpTlsUris)
                .append(mqttUris)
                .append(mqttTlsUris)
                .append(mqttWsUris)
                .append(mqttWssUris)
                .append(managementHostnames)
                .append(managementPassword)
                .append(managementUsername)
                .append(activeManagementHostname)
                .toHashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SolaceServiceInfo other = (SolaceServiceInfo) obj;
        return new EqualsBuilder()
                .append(id, other.id)
                .append(clientUsername, other.clientUsername)
                .append(clientPassword, other.clientPassword)
                .append(msgVpnName, other.msgVpnName)
                .append(smfHost, other.smfHost)
                .append(smfTlsHost, other.smfTlsHost)
                .append(smfZipHost, other.smfZipHost)
                .append(jmsJndiUri, other.jmsJndiUri)
                .append(jmsJndiTlsUri, other.jmsJndiTlsUri)
                .append(restUris, other.restUris)
                .append(restTlsUris, other.restTlsUris)
                .append(amqpUris, other.amqpUris)
                .append(amqpTlsUris, other.amqpTlsUris)
                .append(mqttUris, other.mqttUris)
                .append(mqttTlsUris, other.mqttTlsUris)
                .append(mqttWsUris, other.mqttWsUris)
                .append(mqttWssUris, other.mqttWssUris)
                .append(managementHostnames, other.managementHostnames)
                .append(managementPassword, other.managementPassword)
                .append(managementUsername, other.managementUsername)
                .append(activeManagementHostname, other.activeManagementHostname)
                .isEquals();
    }
}
