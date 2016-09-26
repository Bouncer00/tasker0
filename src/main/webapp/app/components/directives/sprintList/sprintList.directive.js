(function () {
    angular.module('tasker0App')
        .directive('sprintList', sprintList);

    sprintList.$inject = [];

    function sprintList() {
        return {
            templateUrl: 'app/components/directives/sprintList/sprintList.html',
            controller: 'sprintListCtrl',
            controllerAs: 'vm',
            scope: {
                control: '=',
                selectSprint: '='
            },
            link: function (scope, element, attrs) {
                scope.internalControl = scope.control || {};
            }
        }
    }
})();
