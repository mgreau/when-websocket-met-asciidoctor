package com.mgreau.wwsmad;

import java.io.IOException;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class StarterService {
    
	@Inject
    private Logger logger;
    
    public void init() throws IOException{
    	logger.info("[START] App is started.");
    }
    
}
