(function() {
    'use strict';
    angular
        .module('tasker0App')
        .factory('Project', Project);

    Project.$inject = ['$resource', 'DateUtils'];

    function Project ($resource, DateUtils) {
        var resourceUrl =  'api/projects';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                url: resourceUrl + '/:id',
                params: {id: '@id'},
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.created = DateUtils.convertLocalDateFromServer(data.created);
                        data.deadLine = DateUtils.convertLocalDateFromServer(data.deadLine);
                    }
                    return data;
                }
            },
            'getByCurrentUser': {
                method: 'GET',
                url: resourceUrl + '/byCurrentUser',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.created = DateUtils.convertLocalDateFromServer(data.created);
                        data.deadLine = DateUtils.convertLocalDateFromServer(data.deadLine);
                    }
                    return data;
                }
            },
            'addTaskToProject': {
                url: resourceUrl + '/:projectId/addTask/:taskId',
                method: 'PUT',
                params: {projectId: '@projectId', taskId: '@taskId'}
            },
            'getByUser': {
                url: resourceUrl + '/byUser/:userId',
                method: 'GET',
                params: {userId: '@userId'}
            },
            'getMembers': {
                url: resourceUrl + '/:projectId/members',
                method: 'GET',
                params: {projectId: '@projectId'}
            },
            'addMember': {
                url: resourceUrl + '/:projectId/addMember/:email',
                method: 'PUT',
                params: {projectId: '@projectId', email: '@email'}
            },
            'delete': {
                url: resourceUrl + '/:projectId', params: {projectId: '@projectId'}, method: 'DELETE'
            }
        });
    }
})();
