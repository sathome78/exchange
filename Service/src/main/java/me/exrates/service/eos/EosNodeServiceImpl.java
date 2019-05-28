package me.exrates.service.eos;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class EosNodeServiceImpl implements EosNodeService {

    private RestTemplate restTemplate;
    private final static String getBlockUrl = "http://host/:port/v1/chain/get_block";

    {
        restTemplate = new RestTemplate();
    }
/*
    public String getBlock(long blockNumber) {

    }*/


}
