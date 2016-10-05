(function () {
    angular
        .module('tasker0App')
        .controller('taskListCtrl', taskListCtrl);

    taskListCtrl.$inject = ['$scope', 'Task'];

    function taskListCtrl($scope, Task) {
        $scope.fetchTasks = fetchTasks;
        $scope.control.fetchTasks = function (userStoryId) {
            fetchTasks(userStoryId)
        };

        function fetchTasks(userStoryId) {
            Task.getByUserStory({userStoryId: userStoryId}).$promise.then(function (result) {
                $scope.tasks = result.content;
                console.log($scope.tasks);
            });
        }
    }
})();
