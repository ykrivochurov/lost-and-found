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
      crud: $resource('api/items/:number:markers', {number: '@number', markers: '@markers'},
        {
          getByNumber: {method: 'GET', params: {number: '@number'}},
          create: {method: 'PUT'},
          getByCatAndTag: {method: 'GET', params: {itemType: '@itemType', category: '@category', tag: '@tag', cityId: '@cityId', pageNumber: '@pageNumber'}},
          getMarkers: {method: 'GET', params: {itemType: '@itemType', category: '@category', tag: '@tag', cityId: '@cityId', markers: 'markers'}, isArray: true}
        })
    };
  });