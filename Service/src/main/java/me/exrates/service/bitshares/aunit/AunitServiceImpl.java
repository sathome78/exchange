package me.exrates.service.bitshares.aunit;

import me.exrates.model.condition.MonolitConditional;
import me.exrates.service.bitshares.BitsharesServiceImpl;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.websocket.ClientEndpoint;

@Service("aunitServiceImpl")
@ClientEndpoint
@Conditional(MonolitConditional.class)
public class AunitServiceImpl extends BitsharesServiceImpl {

    private final static String name = "AUNIT";
    private static final int DECIMAL = 5;

    public AunitServiceImpl() {
        super(name, name, "merchants/aunit.properties", 7, DECIMAL);
    }
}
