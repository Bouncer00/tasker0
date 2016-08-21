(function() {
    'use strict';

    angular
        .module('tasker0App')
        .controller('TaskDetailController', TaskDetailController);

    TaskDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Task', 'User', 'Project', 'Comment'];

    function TaskDetailController($scope, $rootScope, $stateParams, previousState, entity, Task, User, Project, Comment) {
        var vm = this;

        vm.task = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('tasker0App:taskUpdate', function(event, result) {
            vm.task = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
