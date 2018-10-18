package me.exrates.ngcontroller;

import me.exrates.model.dto.OrderCreateDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/info/private/v2/dashboard/",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DashboardController {

    @PostMapping("/order")
    public ResponseEntity createOrder(@RequestBody OrderCreateDto order){
        return null;
    }

}
