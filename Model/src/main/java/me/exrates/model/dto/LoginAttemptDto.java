package me.exrates.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.IpBanStatus;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

@Getter @Setter
@EqualsAndHashCode
@ToString
public class LoginAttemptDto {
    private String ip;
    private Queue<LocalDateTime> attempts;
    private boolean wasInShortBan = false;
    private LocalDateTime expiresAt;
    private IpBanStatus status = IpBanStatus.ALLOW;

    {
        attempts = new LinkedList<>();
    }

    public LoginAttemptDto(String ip) {
        this.ip = ip;
    }

    public LoginAttemptDto addNewAttempt() {
        attempts.offer(LocalDateTime.now());
        return this;
    }

}
