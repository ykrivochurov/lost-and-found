var app = angular.module('laf', ['ngRoute', 'I18nAngular', 'servicesModule', 'etrFilters', 'ngResource', 'ui.bootstrap', 'ngAnimate']).
  filter('dropProps',function () {
    return function (input, props) {
      // waits array
      var result = [];
      if (input) {
        for (var i = 0; i < input.length; i++) {
          var resultItem = {};
          for (var propIndex = 0; propIndex < props.length; propIndex++) {
            resultItem[props[propIndex]] = input[i][props[propIndex]];
          }
          result.push(resultItem);
        }
      }
      return result;
    }
  }).
  filter('addTimeToDate',function () {
    return function (date, time) {
      date.setHours(time.slice(0, 2));
      date.setMinutes(time.slice(3, 5));
      return date;
    }
  }).
  filter('objectsToStrings',function () { // create string array from objects array be spec field
    return function (input, property) {
      // waits array
      var result = [];
      if (input) {
        for (var i = 0; i < input.length; i++) {
          result.push(input[i][property]);
        }
      }
      return result;
    }
  }).
  config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
      when('/meeting/:meetingId/question-number/:questionNumber', {templateUrl: 'results', controller: ResultsController}).
      when('/question/:questionId', {templateUrl: 'question', controller: QuestionController}).
      when('/login', {templateUrl: 'login', controller: LoginController}).
      when('/meeting/', {templateUrl: 'meeting-create', controller: MeetingFormController}).
      when('/meeting/:meetingId', {templateUrl: 'meeting', controller: MeetingDetailController}).
      when('/companies', {templateUrl: 'company-management', controller: CompaniesController}).
      when('/home', {templateUrl: 'home', controller: HomeController}).
      otherwise({redirectTo: '/home'});
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

app.directive('homeCalendar', function (calendarClassPrefix) {
  return function (scope, element, attrs) {
    var calendarDto;
    var events = {};
    var status_style = {
      "DRAFT": "draftMeeting",
      "PUBLISHED": "publishedMeeting",
      "VOTING_OPENED": "voting_openedMeeting",
      "PAST": "pastMeeting",
      "FINISHED": "finishedMeeting"
    };

    scope.$watch(attrs.homeCalendar, function (value) {
      calendarDto = value;
      fillEvents();
    }, true);

    var Event = function (className) {
      this.className = className;
    };

    var fillEvents = function () {
      events = {};
      if (calendarDto !== null && calendarDto.meetings !== null) {
        for (var ind = 0; ind < calendarDto.meetings.length; ind++) {
          var eventDate = new Date(calendarDto.meetings[ind].dateAndTime);
          eventDate = new Date(eventDate.getFullYear(), eventDate.getMonth(), eventDate.getDate());
          events[eventDate] = new Event(status_style[calendarDto.meetings[ind].status] +
            ' ' + calendarClassPrefix + calendarDto.meetings[ind].id);
        }
      }
      $(element[0]).datepicker_jq('refresh');
    };

    $(element[0]).datepicker_jq({
      //firstDay: 1,
      numberOfMonths: 3,
      showOtherMonths: true,
      selectOtherMonths: false,
      onSelect: function (dateText, inst) {
        inst.inline = false;
        calendarDto.selectedDate = new Date(dateText);
        scope.$apply(scope.model);
      },
      beforeShowDay: function (date) {
        var event = events[date];
        if (event) {
          return [true, event.className, ''];
        }
        else {
          return [false, '', ''];
        }
      }
    });
  }
});

app.config(function ($httpProvider) {
  function exampleInterceptor($q, $location) {
    function success(response) {
      return response;
    }

    function error(response) {
      var status = response.status;
      var url = response.config.url;
      if (status == 401 && url.indexOf("api/login?username") != 0) {
        $location.path("/login").search('unauthorised=' + true);
      }
      return $q.reject(response);
    }

    return function (promise) {
      return promise.then(success, error);
    }
  }

  $httpProvider.responseInterceptors.push(exampleInterceptor);
});

app.animation('.hide-to-right', function () {
  return {
    enter: function (element, done) {
      element.hide(2000, function () {
        done();
      });
      return function (cancelled) {
      };
    },
    leave: function (element, done) {
      console.log('leave');
    },
    move: function (element, done) {
      console.log('move');
    },

    beforeAddClass: function (element, className, done) {
      element.hide(2000, function () {
        done();
      });
    },

    addClass: function (element, className, done) {
      console.log('add');
    },

    beforeRemoveClass: function (element, className, done) {
      console.log('bRem');
    },

    removeClass: function (element, className, done) {
      console.log('rem');
    }
  };
});

app.constant('calendarClassPrefix', 'meeting_calendar_id_');
//locale for date picker
angular.module('$strap.config', []).value('$strapConfig', {
  datepicker: {
    language: 'ru'
  }
});