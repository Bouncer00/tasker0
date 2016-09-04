(function () {
    'use strict';

    angular
        .module('tasker0App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('projects', {
            parent: 'app',
            url: '/projects',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@': {
                    templateUrl: 'app/projects/projects.html',
                    controller: 'ProjectsCtrl',
                    controllerAs: 'vm'
                }
            }
        });
    }

})();
