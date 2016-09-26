(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('userStoryListCtrl', userStoryListCtrl);

    userStoryListCtrl.$inject = ['$scope', 'UserStory'];

    function userStoryListCtrl($scope, UserStory) {

        $scope.fetchUserStories = fetchUserStories;
        $scope.control.fetchUserStories = function (sprintId) {
            fetchUserStories(sprintId)
        };

        function fetchUserStories(sprintId) {
            UserStory.getBySprint({sprintId: sprintId}).$promise.then(function (result) {
                $scope.userStories = result.content;
            });
        }
    }
})();
