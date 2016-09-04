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
                params: {taskId: '@id'},
                method: 'GET'
            },
            'getByUser': {
                url: url + '/byUser/:userId',
                params: {userId: '@id'},
                method: 'GET'
            },
            'addComment': {
                url: url + '/:taskId/addComment/:commentId',
                params: {
                    taskId: '@taskId',
                    commentId: '@commentId'
                },
                method: 'PUT'
            }
        })
    }
})();
