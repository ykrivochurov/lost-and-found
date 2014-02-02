angular.module('laf').
  factory('ItemsService', function ($resource) {
    return {
      getItemsByBounds: function (filterParams, callback) {
        //todo lost or found
        return LOST_AND_FOUND_ITEMS;
      },
      crud: $resource('api/items/:itemId', {itemId: '@itemId'},
        {
          create: {method: 'PUT'}
        })
    };
  });