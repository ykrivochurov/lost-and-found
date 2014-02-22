var app = angular.module('laf', ['ngRoute', 'ngResource', 'ui.bootstrap', 'ngAnimate']).
  config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
      when('/', {templateUrl: 'landing-page'}).
      when('/home', {templateUrl: 'home', controller: HomeController}).
      otherwise({redirectTo: '/'});
  }]);

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

//locale for date picker
angular.module('$strap.config', []).value('$strapConfig', {
  datepicker: {
    language: 'ru'
  }
});