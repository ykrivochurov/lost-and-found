angular.module('laf').
  factory('UrlBuildingService', function ($location) {
    return {
      deselectItem: function() {
        $location.search('');
      }
    };
  });