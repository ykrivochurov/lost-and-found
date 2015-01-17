var app = angular.module('laf', ['ngRoute', 'ngResource', 'ui.bootstrap', 'ngAnimate', 'angular-google-analytics']).
  config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
//      when('/', {templateUrl: 'landing-page'}).
      when('/', {templateUrl: 'home', controller: HomeController, reloadOnSearch: false}).
      when('/widget', {templateUrl: 'widget', controller: WidgetController, reloadOnSearch: false}).
      when('/yandex', {templateUrl: 'home', controller: HomeController, reloadOnSearch: false}).
      otherwise({redirectTo: '/'});
  }]);
app.config(function (AnalyticsProvider) {
  AnalyticsProvider.setAccount('UA-49430073-1');
  AnalyticsProvider.trackPages(true);
  AnalyticsProvider.trackPrefix('lost-and-find');
  AnalyticsProvider.useAnalytics(true);
  AnalyticsProvider.ignoreFirstPageLoad(true);
  AnalyticsProvider.setPageEvent('$stateChangeSuccess');
});
app.filter('truncate', function () {
  return function (text, length) {
    if (isNaN(length)) {
      length = 10;
    }

    if (text.length <= length) {
      return text;
    }
    else {
      return String(text).substring(0, length) + "...";
    }

  };
});
app.directive('dndList', function () {
  return function (scope, element, attrs) {
    var toUpdate;

    scope.$watch(attrs.dndList, function (value) {
      toUpdate = value;
    }, true);

    $(element[0]).sortable({
      cursor: "move",
      distance: 30,
      tolerance: "pointer",
      dropOnEmpty: false,
      axis: 'y',
      start: function (event, ui) {
        toUpdate.startIndex = ($(ui.item).index());
      },
      stop: function (event, ui) {
        toUpdate.newIndex = ($(ui.item).index());

        scope.$apply(scope.model);
      }
    })
  }
});
app.directive('resize', function ($window) {
  return function (scope, element, attr) {

    var w = angular.element($window);
    scope.$watch(function () {
      return {
        'h': window.innerHeight,
        'w': window.innerWidth
      };
    }, function (newValue, oldValue) {
      console.log(newValue, oldValue);
      scope.windowHeight = newValue.h;
      scope.windowWidth = newValue.w;

      scope.hideOnMinHeight = function (minHeight) {
        if (newValue.h < minHeight) {
          return {
            'display': 'none'
          };
        } else {
          return {};
        }
      };

    }, true);

    w.bind('resize', function () {
      scope.$apply();
    });
  }
});
//locale for date picker
angular.module('$strap.config', []).value('$strapConfig', {
  datepicker: {
    language: 'ru'
  }
});

angular.module("template/popover/popover.html", []).run(["$templateCache", function ($templateCache) {
  $templateCache.put("template/popover/popover.html",
    "<div class=\"popover {{placement}}\" ng-class=\"{ in: isOpen(), fade: animation() }\">\n" +
      "  <div class=\"arrow\"></div>\n" +
      "\n" +
      "  <div class=\"popover-inner\">\n" +
      "      <h3 class=\"popover-title\" ng-bind=\"title\" ng-show=\"title\"></h3>\n" +
      "      <div class=\"popover-content\" bind-html-unsafe=\"content\"></div>\n" +
      "  </div>\n" +
      "</div>\n" +
      "");
}]);
