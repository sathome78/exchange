package me.exrates.controller.ngContorollers;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.RequestsLimitExceedException;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.dto.TransferRequestParamsDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.dto.ngDto.TransferMerchantsDataDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.security.service.SecureService;
import me.exrates.service.*;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.util.CharUtils;
import me.exrates.service.util.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import static me.exrates.model.enums.OperationType.USER_TRANSFER;
import static me.exrates.model.enums.UserCommentTopicEnum.TRANSFER_CURRENCY_WARNING;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.PRESENT_VOUCHER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by Maks on 08.02.2018.
 */
@Log4j2
@RestController("/info")
public class InputOutputNgController {

}
