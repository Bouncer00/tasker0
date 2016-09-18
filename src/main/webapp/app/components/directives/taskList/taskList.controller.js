(function () {
    angular
        .module('tasker0App')
        .controller('taskListCtrl', taskListCtrl);
    
    taskListCtrl.$inject = ['Task'];
    
    function taskListCtrl(Task) {
        var vm = this;
    }
})();
