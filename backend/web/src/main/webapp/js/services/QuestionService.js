angular.module('laf').
    factory('QuestionService', function ($resource) {
        return {
            crud: $resource('api/questions/:questionId/:meeting/:meetingId/:number/:questNumber',
                {questionId: '@questionId', meetingId: '@meetingId', questNumber: '@questNumber'},
                {
                    byMeetingAndNumber: {method: 'GET', params: {meeting: 'meeting', number: 'number'}},
                    byMeeting: {method: 'GET', params: {meeting: 'meeting'}, isArray: true},
                    create: {method: 'PUT'},
                    save: {method: 'POST'},
                    get: {method: 'GET'}
                })
        };
    });