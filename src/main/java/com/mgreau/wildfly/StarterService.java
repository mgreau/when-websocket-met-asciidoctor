package com.mgreau.wildfly;

import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class StarterService {
    
    private static final Logger logger = Logger.getLogger("StarterService");
 
}
