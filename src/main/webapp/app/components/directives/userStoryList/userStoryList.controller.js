(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('userStoryListCtrl', userStoryListCtrl);

    userStoryListCtrl.$inject = ['$scope', 'UserStory', 'userStoryDetailsModal'];

    function userStoryListCtrl($scope, UserStory, userStoryDetailsModal) {

        $scope.fetchUserStories = fetchUserStories;
        $scope.openDetails = openDetails;
        $scope.moveUp = moveUp;
        $scope.moveDown = moveDown;
        $scope.remove = remove;
        var currentSprintId;

        if($scope.control) {
            $scope.control.resetUserStories = resetUserStories;
            $scope.control.fetchUserStories = fetchUserStories;
        }

        function resetUserStories() {
            $scope.userStories = [];
        }

        function fetchUserStories(sprintId) {
            currentSprintId = sprintId;
            UserStory.getBySprint({sprintId: sprintId}).$promise.then(function (result) {
                $scope.userStories = result.content;
            });
        }

        function moveUp(userStory) {
            UserStory.moveUp({userStoryId: userStory.id}).$promise.then(function (result) {
                fetchUserStories(userStory.sprintId);
            })
        }

        function moveDown(userStory) {
            UserStory.moveDown({userStoryId: userStory.id}).$promise.then(function (result) {
                fetchUserStories(userStory.sprintId);
            });
        }

        function openDetails(userStory) {
            userStoryDetailsModal.open(userStory);
        }

        function remove(userStory) {
            UserStory.delete({userStoryId: userStory.id}).$promise.then(function (result) {
                if($scope.control.currentUserStory && $scope.control.currentUserStory.id == userStory.id) {
                    $scope.currentUserStory = null;
                    $scope.control.resetTasks();
                }
                fetchUserStories(userStory.sprintId);
            });
        }
    }
})();
