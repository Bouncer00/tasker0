(function () {
    'use strict';

    angular
        .module('tasker0App')
        .factory('Sprint', Sprint);

    Sprint.$inject = ['$resource'];

    function Sprint($resource) {
        var url = '/api/sprints';

        return $resource(url, {sprintId: '@id'}, {
            'get': {
                url: url + '/:sprintId',
                params: {sprintId: '@id'},
                method: 'GET'
            },
            'getByProject': {
                url: url + '/byProject/:projectId',
                params: {projectId: '@projectId'},
                method: 'GET'
            }
        });
    }
})();
