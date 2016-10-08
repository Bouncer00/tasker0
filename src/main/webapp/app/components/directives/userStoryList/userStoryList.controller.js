(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('userStoryListCtrl', userStoryListCtrl);

    userStoryListCtrl.$inject = ['$scope', 'UserStory', 'userStoryDetailsModal'];

    function userStoryListCtrl($scope, UserStory, userStoryDetailsModal) {

        $scope.fetchUserStories = fetchUserStories;
        $scope.control.resetUserStories = resetUserStories;
        $scope.control.fetchUserStories = fetchUserStories;
        $scope.openDetails = openDetails;

        function resetUserStories() {
            $scope.userStories = [];
        }

        function fetchUserStories(sprintId) {
            UserStory.getBySprint({sprintId: sprintId}).$promise.then(function (result) {
                $scope.userStories = result.content;
            });
        }
        
        function openDetails(userStory) {
            userStoryDetailsModal.open(userStory);
        }
    }
})();
