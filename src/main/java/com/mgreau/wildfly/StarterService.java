package com.mgreau.wildfly;

import java.io.IOException;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class StarterService {
    
    private static final Logger logger = Logger.getLogger("StarterService");
    
    
    public void init() throws IOException{
    }
    
}
