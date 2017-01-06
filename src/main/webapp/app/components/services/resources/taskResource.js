(function () {
    'use strict';
    angular
        .module('tasker0App')
        .factory('Task', Task);

    Task.$inject = ['$resource'];

    function Task($resource) {
        var url = '/api/tasks';
        return $resource(url, {taskId: '@id'}, {
            'getById': {
                url: url + '/:taskId',
                params: {taskId: '@taskId'},
                method: 'GET'
            },
            'getByUser': {
                url: url + '/byUser/:userId',
                params: {userId: '@userId'},
                method: 'GET'
            },
            'getByUserStory': {
                url: url + '/byUserStory/:userStoryId',
                params: {userStoryId: '@userStoryId'},
                method: 'GET'
            },
            'addComment': {
                url: url + '/:taskId/addComment/:commentId',
                params: {
                    taskId: '@taskId',
                    commentId: '@commentId'
                },
                method: 'PUT'
            },
            'delete': {
                url: url + '/:taskId', params: {taskId: '@taskId'}, method: 'DELETE'
            },
            'assignToCurrentUser': {
                url: url + "/assignedToCurrentUser/:taskId", params: {taskId: '@taskId', method: 'GET'}, isArray: true
            },
            'assignTaskToCurrentUser': {
                url: url + "/assignToCurrentUser/:taskId", params: {taskId: '@taskId', method: 'GET'}
            },
            'moveUp': {
                url: url + "/moveUp/:taskId", param: {taskId: "@taskId"}, method: 'GET'
            },
            'moveDown': {
                url: url + "/moveDown/:taskId", param: {taskId: "@taskId"}, method: 'GET'
            },
            'update': {
                url: url, method: 'PUT'
            }
        })
    }
})();
