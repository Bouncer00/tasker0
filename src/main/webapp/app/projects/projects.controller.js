(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('ProjectsCtrl', ProjectsCtrl);

    ProjectsCtrl.$inject = ['$scope',
        'Project',
        'Sprint',
        'UserStory',
        'createSprintModal',
        'createProjectModal',
        'createUserStoryModal'];

    function ProjectsCtrl($scope, Project, Sprint, UserStory, createSprintModal, createProjectModal, createUserStoryModal) {
        $scope.createProject = createProject;
        $scope.createSprint = createSprint;
        $scope.selectProject = selectProject;
        $scope.createUserStory = createUserStory;
        $scope.control = {};
        var currentProject;

        function selectProject(project) {
            currentProject = project;
        }

        function createProject() {
            createProjectModal.open().result.then(function (project) {
                Project.save(project);
            });
        }

        function createSprint() {
            createSprintModal.open(currentProject).result.then(function (sprint) {
                Sprint.save(sprint);
            });
        }

        function createUserStory() {
            createUserStoryModal.open().result.then(function (userStory) {
                UserStory.save(userStory);
            })
        }
    }
})();
