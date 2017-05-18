package com.apollo.amb;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amb.conf")
public class AmbConfiguration {

	private String server;     
    public String getServer() { return server; }  
    public void setServer(String server) {  this.server = server; }
    
	private String context;     
    public String getContext() { return context; }  
    public void setContext(String context) {  this.context = context; }
    
	private String libreOfficeServer;     
    public String getLibreOfficeServer() { return libreOfficeServer; }  
    public void setLibreOfficeServer(String server) {  this.libreOfficeServer = server; }
    
	private int libreOfficePort;     
    public int getLibreOfficePort() { return libreOfficePort; }  
    public void setLibreOfficePort(int port) {  this.libreOfficePort = port; }
    
}
