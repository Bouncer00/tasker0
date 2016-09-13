(function () {
    angular
        .module('tasker0App')
        .directive('createTask', createTask);

    createTask.$inject = ['Task'];

    function createTask(Task) {
        var vm = this;
    }
})();
