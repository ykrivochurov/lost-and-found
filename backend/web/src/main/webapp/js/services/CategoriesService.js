angular.module('laf').
  factory('CategoriesService', function ($resource) {
    return {
      crud: $resource('api/categories/:countsCtrl', {},
        {
          all: {method: 'GET', params: {}, isArray: true},
          counts: {method: 'GET', params: {countsCtrl: 'counts', itemType: '@itemType'}}
        })
    };
  });