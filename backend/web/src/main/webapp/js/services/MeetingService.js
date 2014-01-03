angular.module('laf').
    factory('MeetingService', function ($resource) {
        return {
            crud: $resource('api/meetings/:filtered:meetingId', {meetingId: '@meetingId'},
                {
                    getById: {method: 'GET'},
                    create: {method: 'PUT'},
                    save: {method: 'POST'},
                    getFiltered: {method: 'POST', params: {filtered: 'filtered'}, isArray: true}
                }),
            utils: {
                addDateTimeToScope: function (scopeVar, meeting) {
                    var date = new Date(meeting.dateAndTime);
                    console.log(meeting);
                    console.log(meeting.dateAndTime);
                    scopeVar.date = date;
                    var hours = date.getHours();
                    var minutes = date.getMinutes();
                    if (hours < 10) {
                        hours = "0" + hours;
                    }
                    if (minutes < 10) {
                        minutes = "0" + minutes;
                    }
                    scopeVar.time = hours + ":" + minutes;
                    console.log(scopeVar.time);
                }
            }
        };
    });