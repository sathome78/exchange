/**
 * Created by ValkSam
 */
angular
    .module('app')
    .factory("languageService", LanguageService);

function LanguageService($cookies, $location, $rootScope, $route) {
    var service = {};

    service.languageMenu = ['en', 'ru', 'ch', 'id', 'ar', 'ko'];

    service.currentLanguage = {
        lang: getLangFromCookies(),
    };

    service.convertToIsoLanguage = function (langToConvert) {
        var formatLangMap = {
            "ch": "zh-CN",
            "in": "id"
        };
        lang = formatLangMap[langToConvert];
        if (!lang) {
            return langToConvert;
        } else {
            return lang;
        }
    };

    service.convertToIsoLanguageForMenu = function (langToConvert) {
        var formatLangMap = {
            "in": "id"
        };
        lang = formatLangMap[langToConvert];
        if (!lang) {
            return langToConvert;
        } else {
            return lang;
        }
    };

    service.convertToApplicationLanguage = function (langToConvert) {
        var formatLangMap = {
            "zh-CN": "cn",
            "ch": "cn",
            "id": "in"
        };
        lang = formatLangMap[langToConvert];
        if (!lang) {
            return langToConvert;
        } else {
            return lang;
        }
    };

    service.getCurrentLanguage = function () {
        var lang = service.convertToIsoLanguageForMenu(service.currentLanguage.lang);
        if (!lang) {
            lang = getLangFromCookies();
        }
        return lang;
    };

    service.changeLocale = function (lang) {
        lang = service.convertToApplicationLanguage(lang);
        var url = '/dashboard/locale?locale=' + lang;
        $.get(url)
            .always(function () {
                window.location.reload();
            });
    };

    function checkIfLangPresent(url) {
        var match = /\/.+\/(.+)$/g.exec(url);
        return match && service.languageMenu.indexOf(match[1]) != -1;
    }

    service.syncCurrentLanguageWithUrl = function () {
        var urlLang = service.getLocaleFromUrl();
        if (service.currentLanguage.lang != urlLang) {
            service.currentLanguage.lang = urlLang;
            $cookies.put('myAppLocaleCookie', urlLang);
        }
    };

    service.getLocaleFromUrl = function () {
        var regexp = /([^\/]+)$/g;
        var match = regexp.exec($location.path());
        var lang = match ? match[1] : '';
        if (service.languageMenu.indexOf(lang) == -1) {
            lang = getLangFromCookies();
        }
        return lang;
    };

    service.redirect = function (url) {
        /*$rootScope.$apply(function () {
         $location.path(url);
         });*/
        window.location = url;
    };

    function getLangFromCookies() {
        return $cookies.get('myAppLocaleCookie') ? $cookies.get('myAppLocaleCookie') : service.languageMenu[0];
    }

    return service;
}


