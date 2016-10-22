(function () {
    'use strict';

    angular
        .module('tasker0App')
        .factory('Board', Board);

    Board.$inject = ['$resource'];

    function Board($resource) {
        var url = '/api/boards';

        return $resource(url, {}, {
            'get': {
                url: url + '/:boardId', params: {boardId: '@id'}, method: 'GET'
            },
            'moveTask': {
                url: url + '/moveTask/:source/:target/:taskId',
                params: {source: '@source', target: '@target', taskId: '@taskId'},
                method: 'PUT'
            },
            'delete': {
                url: url + '/:boardId', params: {boardId: '@boardId'}, method: 'DELETE'
            }
        });
    }
})();
