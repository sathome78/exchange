package me.exrates.model.form;

import me.exrates.model.NotificationOption;

import java.util.List;

/**
 * Created by OLEG on 15.11.2016.
 */
public class NotificationOptionsForm {
    private List<NotificationOption> options;

    public List<NotificationOption> getOptions() {
        return options;
    }

    public void setOptions(List<NotificationOption> options) {
        this.options = options;
    }
}
