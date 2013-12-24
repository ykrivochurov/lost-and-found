angular.module('etrFilters', []).
    filter('fromNow', function () {
        return function (dateString, selectedLocale) {
            return moment(new Date(dateString)).lang(selectedLocale.code).fromNow()
        };
    }).filter('calendar', function () {
        return function (dateString, selectedLocale) {
            return moment(new Date(dateString)).lang(selectedLocale.code).calendar()
        };
    });
