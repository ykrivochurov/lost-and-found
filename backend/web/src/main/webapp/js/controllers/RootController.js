function RootController($scope, $location, UserService, AccessService) {
    $scope.pageName;
    $scope.isLocaleLoaded = false;

    $scope.setPageName = function (name) {
        $scope.pageName = name;
    };

    function safeApply() {
        $scope.isLocaleLoaded = true;
        if (!$scope.$$phase) {
            $scope.$apply();
        }
    }

    $scope.locales = [
        { label: 'Russian', code: 'ru', language: 'ru' },
        { label: 'English', code: 'en', language: 'us' }
    ];

    $scope.selectedLocale = $scope.locales[0];

    function initI18n() {
        var i18n = new I18nAdapter(['ru_RU','en_US'], 'ru_RU');
        i18n.setLocale('ru', ['js/i18n/ru-ru/messages.json']).then(safeApply);
        window.i18nAdapter = i18n;
    }

    initI18n();

    $scope.changeLocale = function () {
        $.datepicker_jq.setDefaults($.datepicker_jq.regional[$scope.selectedLocale.code]);
        $("#calendarHome").datepicker_jq('refresh');
        $scope.isLocaleLoaded = false;
        var locale = $scope.selectedLocale.code + '-' + $scope.selectedLocale.language;
        i18nAdapter.setLocale(locale, ['js/i18n/' + locale + '/messages.json'])
            .then(safeApply);
    };

    $scope.loggedIn = function () {
        $scope.user = UserService.crud.get({userId: "current"});
    };

//    $scope.loggedIn();

    $scope.logout = function () {
        AccessService.logout.do();
        $scope.user = {};
        $location.path("/login");
    }
}
