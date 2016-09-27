(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('ProjectsCtrl', ProjectsCtrl);

    ProjectsCtrl.$inject = ['$scope',
        'Project',
        'Sprint',
        'UserStory',
        'Task',
        'createSprintModal',
        'createProjectModal',
        'createUserStoryModal',
        'createTaskModal'];

    function ProjectsCtrl($scope, Project, Sprint, UserStory, Task, createSprintModal, createProjectModal, createUserStoryModal, createTaskModal) {
        $scope.createProject = createProject;
        $scope.createSprint = createSprint;
        $scope.selectProject = selectProject;
        $scope.selectSprint = selectSprint;
        $scope.selectUserStory = selectUserStory;
        $scope.createUserStory = createUserStory;
        $scope.createTask = createTask;
        $scope.control = {};
        var currentProject;
        var currentSprint;
        var currentUserStory;

        function selectProject(project) {
            currentProject = project;
        }

        function selectSprint(sprint) {
            currentSprint = sprint
        }

        function selectUserStory(userStory) {
            currentUserStory = userStory;
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
            createUserStoryModal.open(currentSprint).result.then(function (userStory) {
                UserStory.save(userStory);
            })
        }

        function createTask() {
            createTaskModal.open(currentUserStory).result.then(function (task) {
                Task.save(task);
            })
        }
    }
})();
