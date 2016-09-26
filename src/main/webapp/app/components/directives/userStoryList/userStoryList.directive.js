(function () {
    angular.module('tasker0App')
        .directive('userStoryList', userStoryList);

    userStoryList.$inject = [];

    function userStoryList() {
        return {
            templateUrl: 'app/components/directives/userStoryList/userStoryList.html',
            controller: 'userStoryListCtrl',
            controllerAs: 'vm',
            scope: {
                control: '='
            },
            link: function (scope, element, attrs) {
                scope.internalControl = scope.control || {};
            }
        }
    }
})();
