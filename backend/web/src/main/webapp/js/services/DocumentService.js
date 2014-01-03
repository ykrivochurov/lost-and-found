angular.module('laf').
    factory('DocumentService', function ($resource, $http) {
        return {
            upload: function (formData, success, fail) {
                $http({
                    method: 'POST',
                    url: "api/questions/document",
                    data: formData,
                    headers: { 'Content-Type': false },
                    transformRequest: function (data) {
                        var fd = new FormData();
                        angular.forEach(data, function (value, key) {
                            fd.append(key, value);
                        });
                        return fd;
                    }
                }).success(success).error(fail);
            },
            crud: $resource('api/questions/document/:documentId', {documentId: '@id'},
                {
                    delete: {method: 'delete'}
                })
        };
    });