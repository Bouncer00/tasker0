(function () {
    'use strict';

    angular
        .module('tasker0App')
        .factory('Board', Board);

    Board.$inject = ['$resouce'];

    function Board($resource) {
        var url = '/api/boards';

        return $resource(url, {}, {
            'get': {
                url: url + '/:boardId', params: {boardId: '@id'}, method: 'GET'
            },
            'moveTask': {
                url: url + '/movsTask/:source/:target/:taskId',
                params: {source: '@source', target: '@target', taskId: '@taskId'},
                method: 'PUT'
            }
        });
    }
})();
