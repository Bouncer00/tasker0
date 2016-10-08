(function () {
    angular
        .module('tasker0App')
        .controller('taskListCtrl', taskListCtrl);

    taskListCtrl.$inject = ['$scope', 'Task', 'taskDetailsModal'];

    function taskListCtrl($scope, Task, taskDetailsModal) {
        $scope.fetchTasks = fetchTasks;
        $scope.openDetails = openDetails;

        $scope.control.resetTasks = resetTasks;
        $scope.control.fetchTasks = fetchTasks;
        function resetTasks() {
            $scope.tasks = [];
        }

        function fetchTasks(userStoryId) {
            Task.getByUserStory({userStoryId: userStoryId}).$promise.then(function (result) {
                $scope.tasks = result.content;
            });
        }
        
        function openDetails(task) {
            taskDetailsModal.open(task);
        }
    }
})();
