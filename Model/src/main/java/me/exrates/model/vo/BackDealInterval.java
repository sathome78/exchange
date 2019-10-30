package me.exrates.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.IntervalType;
import me.exrates.model.exceptions.UnsupportedIntervalFormatException;
import me.exrates.model.exceptions.UnsupportedIntervalTypeException;

@Data
@NoArgsConstructor
public class BackDealInterval {

    private Integer intervalValue;
    private IntervalType intervalType;

    public BackDealInterval(Integer intervalValue, IntervalType intervalType) {
        this.intervalValue = intervalValue;
        this.intervalType = intervalType;
    }

    public String getInterval() {
        return intervalValue + " " + intervalType;
    }

    public BackDealInterval(String intervalString) {
        try {
            this.intervalValue = Integer.valueOf(intervalString.split(" ")[0]);
            this.intervalType = IntervalType.convert(intervalString.split(" ")[1]);
        } catch (UnsupportedIntervalTypeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnsupportedIntervalFormatException(intervalString);
        }
    }

    public String getResolution() {
        switch (intervalType) {
            case MINUTE:
                return String.valueOf(intervalValue);
            case HOUR:
                return String.valueOf(intervalValue) + "H";
            case DAY:
                return String.valueOf(intervalValue) + "D";
            case MONTH:
                return String.valueOf(intervalValue) + "M";
            default:
                throw new IllegalArgumentException(String.format("Unsupported resolution with type: %s", intervalType.name()));
        }
    }
}
