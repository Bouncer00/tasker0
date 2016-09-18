(function () {
    angular.module('tasker0App')
        .directive('userStoryList', userStoryList);

    userStoryList.$inject = [];

    function userStoryList() {
        return {
            templateUrl: 'app/components/directives/userStoryList/userStoryList.html',
            controller: 'userStoryListCtrl',
            controllerAs: 'vm'
        }
    }
})();
