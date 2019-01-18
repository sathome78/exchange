package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MethodMetricsDto {

    private String methodKey;
    private AtomicInteger invocationCounter;
    private AtomicInteger errorCounter;
    private List<Long> executionTimes;
}