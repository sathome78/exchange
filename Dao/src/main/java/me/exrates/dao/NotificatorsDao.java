package me.exrates.dao;

import me.exrates.model.dto.Notificator;

import java.math.BigDecimal;

/**
 * Created by Maks on 29.09.2017.
 */
public interface NotificatorsDao {
    Notificator getById(int id);

}
