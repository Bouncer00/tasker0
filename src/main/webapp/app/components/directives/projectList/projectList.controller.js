(function () {
    'use strict';

    angular.module('tasker0App')
        .controller('projectListCtrl', projectListCtrl);

    projectListCtrl.$inject = ['$scope', 'Project', 'projectDetailsModal'];

    function projectListCtrl($scope, Project, projectDetailsModal) {
        $scope.openDetails = openDetails;
        
        Project.getByCurrentUser().$promise.then(function (projects) {
            $scope.projects = projects.content;
        });
        
        function openDetails(project) {
            projectDetailsModal.open(project);
        }
    }
})();
