(function () {
    'use strict';

    angular.module('tasker0App')
        .controller('projectListCtrl', projectListCtrl);

    projectListCtrl.$inject = ['$scope', 'Project'];

    function projectListCtrl($scope, Project) {
        Project.getByCurrentUser().$promise.then(function (projects) {
            $scope.projects = projects.content;
        });
    }
})();
