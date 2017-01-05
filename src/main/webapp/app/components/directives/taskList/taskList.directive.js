(function () {
    angular.module('tasker0App')
        .directive('taskList', taskList);

    taskList.$inject = [];

    function taskList() {
        return {
            templateUrl: 'app/components/directives/taskList/taskList.html',
            controller: 'taskListCtrl',
            controllerAs: 'vm',
            scope: {
                fetchTasks: '&',
                moveDown: '&',
                moveUp: '&',
                remove: '&',
                control: '=',
                assigned: '='

            }
        }
    }
})();
