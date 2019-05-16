package me.exrates.service.impl.inout;

import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.service.impl.LiqpayServiceImpl;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@Conditional(MicroserviceConditional.class)
public class LiqpayServiceMsImpl extends LiqpayServiceImpl {

}
