(function () {
    'use strict';
    
    angular
        .module('tasker0App')
        .factory('Project', Project);
    
    Project.$inject = ['$resource'];
    
    function Project($resource) {
        var url = '/api/projects';
        
        return $resource(url, {}, {
            'addTaskToProject': {
                url: url + '/:projectId/addTask/:taskId',
                method: 'PUT',
                params: {projectId: '@projectId', taskId: '@taskId'}
            },
            'getByUser': {
                url: url + '/byUser/:userId',
                method: 'GET',
                params: {userId: '@userId'}
            }
        });
    }
})();
