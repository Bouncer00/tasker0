(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('userStoryListCtrl', userStoryListCtrl);

    userStoryListCtrl.$inject = ['$scope', 'UserStory', 'userStoryDetailsModal'];

    function userStoryListCtrl($scope, UserStory, userStoryDetailsModal) {

        $scope.fetchUserStories = fetchUserStories;
        $scope.openDetails = openDetails;
        $scope.remove = remove

        if($scope.control) {
            $scope.control.resetUserStories = resetUserStories;
            $scope.control.fetchUserStories = fetchUserStories;
        }
        
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
        
        function remove(userStory) {
            UserStory.delete({userStoryId: userStory.id}).$promise.then(function (result) {
                fetchUserStories(userStory.sprintId);
            })
        }
    }
})();
