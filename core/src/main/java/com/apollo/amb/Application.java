package com.apollo.amb;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AmbConfiguration.class})
public class Application {

	@Autowired
	private AmbConfiguration conf;
	
    @PostConstruct
    public void postProcess() {
    	AppContext.setConf(conf);
    }
    
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
