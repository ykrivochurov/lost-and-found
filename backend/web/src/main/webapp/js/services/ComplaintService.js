angular.module('laf').
  factory('ComplaintService', function ($resource) {
    return {
      crud: $resource('api/complaints/', {},
        {
          create: {method: 'PUT'}
        })
    };
  });