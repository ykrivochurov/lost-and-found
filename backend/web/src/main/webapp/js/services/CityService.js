angular.module('laf').
  factory('CityService', function ($resource) {
    return {
      crud: $resource('api/cities', {}, {
        get: {method: 'GET', params: {}, isArray: true}
      })
    };
  });