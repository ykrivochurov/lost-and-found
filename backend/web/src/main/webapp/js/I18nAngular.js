(function() {
    'use strict';

    var module = angular.module('I18nAngular', []);

    module.filter('translate', function() {
        return function(key) {
            return i18nAdapter.translate(key);
        };
    });

    module.filter('number', function() {
        return function(value) {
            return i18nAdapter.formatNumber(value);
        };
    });

    module.filter('currency', function() {
        return function(value) {
            return i18nAdapter.formatCurrency(value);
        };
    });
}());
