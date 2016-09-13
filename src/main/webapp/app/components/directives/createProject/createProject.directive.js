(function () {
    'use strict';

    angular
        .module('tasker0App')
        .directive('createProject', createProject);

    createProject.$inject = [];

    function createProject() {
        return {
            restrict: 'E',
            templateUrl: "app/components/directives/createProject/createProject.html",
            controller: "createProjectCtrl",
            controllerAs: 'vm',
            replace: true
        }
    }
})();
