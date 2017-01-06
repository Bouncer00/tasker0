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

    function ProjectsCtrl($scope,
                          Project,
                          Sprint,
                          UserStory,
                          Task,
                          createSprintModal,
                          createProjectModal,
                          createUserStoryModal,
                          createTaskModal) {
        $scope.createProject = createProject;
        $scope.createSprint = createSprint;
        $scope.selectProject = selectProject;
        $scope.selectSprint = selectSprint;
        $scope.selectUserStory = selectUserStory;
        $scope.createUserStory = createUserStory;
        $scope.createTask = createTask;
        $scope.control = {};
        $scope.currentProject;
        $scope.currentSprint;
        $scope.currentUserStory;
        $scope.control.currentProject = null;;
        $scope.control.currentSprint = null;
        $scope.control.currentUserStory = null;

        function selectProject(project) {
            if ($scope.currentProject != project) {
                $scope.currentProject = project;
                $scope.control.currentProject = project;
                $scope.control.resetSprints();
                $scope.control.resetUserStories();
                $scope.control.resetTasks();
                $scope.control.fetchSprints(project.id);
                $scope.currentSprint = null;
                $scope.currentUserStory = null;
                $scope.currentTask = null;
            }
        }

        function selectSprint(sprint) {
            if($scope.currentSprint != sprint) {
                $scope.currentSprint = sprint;
                $scope.control.currentSprint = sprint;
                console.log($scope.control);
                $scope.control.resetUserStories();
                $scope.control.resetTasks();
                $scope.control.fetchUserStories(sprint.id);
                $scope.currentUserStory = null;
                $scope.currentTask = null;
            }
        }

        function selectUserStory(userStory) {
            if($scope.currentUserStory != userStory){
                $scope.currentUserStory = userStory;
                $scope.control.currentUserStory = userStory;
                $scope.control.resetTasks();
                $scope.control.fetchTasks(userStory.id);
                $scope.currentTask = null;
            }
        }

        function createProject() {
            createProjectModal.open().result.then(function (project) {
                Project.save(project).$promise.then(function (result) {
                    $scope.control.getProjects();
                });
            });
        }

        function createSprint() {
            if($scope.currentProject) {
                createSprintModal.open($scope.currentProject).result.then(function (sprint) {
                    Sprint.save(sprint).$promise.then(function (result) {
                        $scope.control.fetchSprints(result.projectId);
                    });
                });
            }
        }

        function createUserStory() {
            if($scope.currentSprint) {
                createUserStoryModal.open($scope.currentSprint).result.then(function (userStory) {
                    UserStory.save(userStory).$promise.then(function (result) {
                        $scope.control.fetchUserStories(result.sprintId);
                    });
                })
            }
        }

        function createTask() {
            if($scope.currentUserStory) {
                createTaskModal.open($scope.currentUserStory).result.then(function (task) {
                    task.projectId = $scope.currentProject.id;
                    Task.save(task).$promise.then(function (result) {
                        $scope.control.fetchTasks(result.userStoryId);
                    });
                })
            }
        }
    }
})();
