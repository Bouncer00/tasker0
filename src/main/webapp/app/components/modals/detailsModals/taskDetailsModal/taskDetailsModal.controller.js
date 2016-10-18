(function () {
    angular
        .module('tasker0App')
        .controller('TaskDetailsModalCtrl', TaskDetailsModalCtrl);

    TaskDetailsModalCtrl.$inject = ['$scope', 'task'];

    function TaskDetailsModalCtrl($scope, task) {
        $scope.task = task;
        console.log(task);
    }
})();
