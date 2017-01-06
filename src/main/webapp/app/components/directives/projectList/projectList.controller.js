(function () {
    'use strict';

    angular.module('tasker0App')
        .controller('projectListCtrl', projectListCtrl);

    projectListCtrl.$inject = ['$scope', 'Project', 'projectDetailsModal'];

    function projectListCtrl($scope, Project, projectDetailsModal) {
        $scope.openDetails = openDetails;
        $scope.deleteProject = deleteProject;
        getProjects();

        if($scope.control) {
            $scope.control.getProjects = getProjects;
        }

        function openDetails(project) {
            projectDetailsModal.open(project);
        }

        function deleteProject(project) {
            Project.delete({projectId: project.id}).$promise.then(function (result) {
                if ($scope.control.currentProject && $scope.control.currentProject.id == project.id) {
                    console.log($scope.control.currentProject);
                    console.log(project);
                    $scope.control.resetSprints();
                    $scope.control.resetUserStories();
                    $scope.control.resetTasks();
                    $scope.currentSprint = null;
                    $scope.currentUserStory = null;
                    $scope.currentTask = null;
                }
                getProjects();
            });
        }

        function getProjects(){
            Project.getByCurrentUser().$promise.then(function (projects) {
                $scope.projects = projects.content;
            });
        }
    }
})();
