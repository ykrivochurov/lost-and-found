angular.module('laf').
  factory('ItemsService', function ($resource, $http) {
    return {
      upload: function (formData, success, fail) {
        $.ajax({
          url: "api/items/photo",
          data: formData,
          cache: false,
          contentType: false,
          processData: false,
          type: 'POST'
        }).done(success).fail(fail);

//        $http({
//          method: 'POST',
//          url: "api/items/photo",
//          data: formData,
////          contentType: false
//          headers: { 'Content-Type': 'application/json' }
//        }).success(success).error(fail);
      },
      crud: $resource('api/items/:numberOrId:markers', {numberOrId: '@numberOrId', markers: '@markers'},
        {
          getByNumber: {method: 'GET', params: {numberOrId: '@numberOrId'}},
          search: {method: 'GET', params: {markers: 'search', query: '@query', itemType: '@itemType'}, isArray: true},
          create: {method: 'PUT'},
          update: {method: 'POST'},
          close: {method: 'DELETE', params: {numberOrId: '@numberOrId'}},
          getMy: {method: 'GET', params: {markers: 'my', pageNumber: '@pageNumber'}},
          getByCatAndTag: {method: 'GET', params: {itemType: '@itemType', category: '@category', tag: '@tag', cityId: '@cityId', pageNumber: '@pageNumber'}},
          getMarkers: {method: 'GET', params: {itemType: '@itemType', category: '@category', tag: '@tag', cityId: '@cityId', markers: 'markers'}, isArray: true}
        })
    };
  });