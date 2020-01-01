package me.exrates.service;

import me.exrates.model.dto.migrate.ExtendedUserDto;

public interface MigrateService {

    ExtendedUserDto migrate(String email);
}