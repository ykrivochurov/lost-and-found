angular.module('laf').
  factory('ItemsService', function ($resource) {
    return {
      crud: $resource('api/items/:itemId:markers', {itemId: '@itemId', markers: '@markers'},
        {
          create: {method: 'PUT'},
          getByCatAndTag: {method: 'GET', params: {itemType: '@itemType', category: '@category', tag: '@tag', cityId: '@cityId', pageNumber: '@pageNumber'}},
          getMarkers: {method: 'GET', params: {itemType: '@itemType', category: '@category', tag: '@tag', cityId: '@cityId', markers: 'markers'}, isArray: true}
        })
    };
  });