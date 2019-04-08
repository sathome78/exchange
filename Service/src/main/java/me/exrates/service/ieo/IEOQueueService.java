package me.exrates.service.ieo;

import me.exrates.model.IEOClaim;

public interface IEOQueueService {

    boolean add(IEOClaim claim);
}
