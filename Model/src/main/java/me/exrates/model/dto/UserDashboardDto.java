package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardDto {

    private Integer allUsers;
    private Integer allVerifiedUsers;
    private Integer allOnlineUsers;
    private Integer allBlockedUsers;
}