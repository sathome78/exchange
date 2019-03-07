package me.exrates.service;

import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.service.impl.CurrencyServiceImpl;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@Conditional(MicroserviceConditional.class)
public class CurrencyServiceMsImpl extends CurrencyServiceImpl {


}
