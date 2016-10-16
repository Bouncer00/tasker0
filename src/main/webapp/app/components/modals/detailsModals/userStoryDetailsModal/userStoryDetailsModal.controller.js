(function () {
    angular
        .module('tasker0App')
        .controller('UserStoryDetailsModalCtrl', UserStoryDetailsModalCtrl);

    UserStoryDetailsModalCtrl.$inject = ['$scope', 'userStory'];

    function UserStoryDetailsModalCtrl($scope, userStory) {
        $scope.userStory = userStory;
    }
})();
