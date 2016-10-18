(function () {
    angular
        .module('tasker0App')
        .directive('boardList', boardList);
    
    boardList.$inject = [];

    function boardList() {
        return {
            templateUrl: 'app/components/directives/boardList/boardList.html',
            controller: 'boardListCtrl',
            controllerAs: 'vm',
            scope: {
                control: '=',
                selectProject: '='
            },
            link: function (scope, element, attrs) {
                scope.internalControl = scope.control || {};
            }
        }
    }
})();
