var settings;

$(function settingsInit() {
    try {
        settings = new SettingsClass();
    } catch (e) {
        /*it's need for ignoring error from old interface*/
    }
});
