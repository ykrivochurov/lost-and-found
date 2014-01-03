angular.module('laf').
    factory('CommentService', function ($resource) {
        return {
            crud: $resource('api/comments/:commentId', {commentId: '@commentId'},
                {
                    create: {method: 'PUT'}
                })
        };
    });