(function () {
    angular
        .module('tasker0App')
        .controller('taskListCtrl', taskListCtrl);

    taskListCtrl.$inject = ['$scope', 'Task', 'taskDetailsModal'];

    function taskListCtrl($scope, Task, taskDetailsModal) {
        $scope.fetchTasks = fetchTasks;
        $scope.openDetails = openDetails;
        $scope.moveUp = moveUp;
        $scope.moveDown = moveDown;
        $scope.remove = remove;

        if($scope.control) {
            $scope.control.resetTasks = resetTasks;
            $scope.control.fetchTasks = fetchTasks;
        }
        console.log($scope);

        if($scope.assigned){
            Task.assignToCurrentUser().$promise.then(function (result) {
                $scope.tasks = result;
            });
        }

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

        function moveUp(task) {
            Task.moveUp({taskId: task.id}).$promise.then(function (result) {
                fetchTasks(task.userStoryId);
            })
        }

        function moveDown(task) {
            Task.moveDown({taskId: task.id}).$promise.then(function (result) {
                fetchTasks(task.userStoryId);
            });
        }

        function remove(task) {
            Task.delete({taskId: task.id}).$promise.then(function (result) {
                fetchTasks(task.userStoryId);
            })
        }
    }
})();
