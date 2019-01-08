package me.exrates.controller.filter;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingFilter implements ClientRequestFilter {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

    public void filter(ClientRequestContext clientRequestContext) {
        LOG.log(Level.INFO, clientRequestContext.getEntity().toString());
    }
}
