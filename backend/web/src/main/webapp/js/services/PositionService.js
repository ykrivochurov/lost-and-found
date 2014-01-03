angular.module('laf').
    factory('PositionService', function ($resource) {
        return {
            crud: $resource('api/positions/:company/:companyId/:commOrUser/:commOrUserId',
                {companyId: '@companyId', commOrUserId: '@commOrUserId'},
                {
                    getByCompany: {method: 'GET',
                        params: {company: 'company', pageNumber: '@pageNumber', pageCount: '@pageCount'}},
                    getByCommittee: {method: 'GET',
                        params: {commOrUser: 'committee', pageNumber: '@pageNumber', pageCount: '@pageCount'}},
                    create: {method: 'PUT'},
                    delete: {method: 'DELETE', params: {company: 'company', commOrUser: 'user'}}
                })
        };
    });
