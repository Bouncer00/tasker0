(function () {
    angular.module('tasker0App')
        .directive('projectList', projectList);

    projectList.$inject = [];

    function projectList() {
        return {
            templateUrl: 'app/components/directives/projectList/projectList.html',
            controller: 'projectListCtrl',
            controllerAs: 'vm'
        }
    }
})();
