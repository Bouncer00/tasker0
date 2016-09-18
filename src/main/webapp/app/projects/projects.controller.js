(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('ProjectsCtrl', ProjectsCtrl);

    ProjectsCtrl.$inject = ['$scope', 'Project', 'createProjectModal'];

    function ProjectsCtrl($scope, Project, createProjectModal) {
        $scope.createProject = createProject;

        function createProject() {
            createProjectModal.open().result.then(function (project) {
                Project.save(project);
            });
        }
    }
})();
