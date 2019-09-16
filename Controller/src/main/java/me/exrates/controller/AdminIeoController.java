package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.ieo.IEODetailsFlatDto;
import me.exrates.model.dto.ieo.IeoDetailsCreateDto;
import me.exrates.model.dto.ieo.IeoDetailsUpdateDto;
import me.exrates.model.enums.IEODetailsStatus;
import me.exrates.service.IEOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Log4j2
@Controller
public class AdminIeoController {

    private final IEOService ieoService;

    @Autowired
    public AdminIeoController(IEOService ieoService) {
        this.ieoService = ieoService;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/ieo/page", method = GET)
    public String getIeoPage(Model model) {
        model.addAttribute("statuses", IEODetailsStatus.values());
        return "admin/ieo";
    }


    @RequestMapping(value = "/2a8fy7b07dxe44/ieo/all", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Collection<IEODetailsFlatDto> getAllIEOs() {
        return ieoService.findAll(null)
                .stream()
                .map(IEODetailsFlatDto::new)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/ieo", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity createIeo(@RequestBody @Valid IeoDetailsCreateDto dto) {
        if (LocalDateTime.now().isAfter(dto.getEndDate())) {
            String messageError = "Date endIEO is not after current time";
            log.error(messageError + " {}", dto.getEndDate());
            throw new RuntimeException(messageError);
        }

        if (dto.getAvailableBalance().compareTo(dto.getAmount()) > 0) {
            String messageError = String.format("Error: Available amount %s more than amount %s",
                    dto.getAvailableBalance().toPlainString(),
                    dto.getAmount().toPlainString());
            log.error(messageError);
            throw new RuntimeException(messageError);
        }

        ieoService.createIeo(dto);
        return new ResponseEntity(null, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/ieo/{id}", method = PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity updateIeo(@PathVariable("id") Integer id, @RequestBody @Valid IeoDetailsUpdateDto dto) {
        ieoService.updateIeo(id, dto);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/ieo/revert/{id}", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity revertIeo(@PathVariable("id") Integer id) {
        ieoService.startRevertIEO(id, getPrincipalEmail());
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/ieo/approve/{id}", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity approveSuccessIeo(@PathVariable("id") Integer id) {
        ieoService.approveSuccessIeo(id, getPrincipalEmail());
        return ResponseEntity.ok().build();
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
