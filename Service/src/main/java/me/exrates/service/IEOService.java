package me.exrates.service;

import me.exrates.model.IEODetails;
import me.exrates.model.User;
import me.exrates.model.dto.ieo.ClaimDto;
import me.exrates.model.dto.ieo.IEOStatusInfo;
import me.exrates.model.dto.ieo.IeoDetailsCreateDto;
import me.exrates.model.dto.ieo.IeoDetailsUpdateDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface IEOService {
    ClaimDto addClaim(ClaimDto claimDto, String email);

    IEOStatusInfo checkUserStatusForIEO(String email, int idIeo);

    Collection<IEODetails> findAll(User user);

    IEODetails findOne(int ieoId);

    @Transactional
    void createIeo(IeoDetailsCreateDto dto);

    @Transactional
    void updateIeo(Integer id, IeoDetailsUpdateDto dto);

    void startRevertIEO(Integer idIeo, String email);

    void updateIeoStatuses();

    boolean approveSuccessIeo(int ieoId, String email);
}
