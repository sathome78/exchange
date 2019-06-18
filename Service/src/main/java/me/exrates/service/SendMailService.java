package me.exrates.service;

import me.exrates.model.Email;
import me.exrates.model.mail.ListingRequest;

public interface SendMailService {

    void sendMail(Email email);

//    void sendMailSes(Email email);

//    void sendInfoMail(Email email);

    void sendListingRequestEmail(ListingRequest request);
}
