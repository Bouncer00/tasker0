(function () {
    angular
        .module('tasker0App')
        .controller('UserStoryDetailsModalCtrl', UserStoryDetailsModalCtrl);

    UserStoryDetailsModalCtrl.$inject = ['$scope', 'userStory', 'Comment', 'UserStory'];

    function UserStoryDetailsModalCtrl($scope, userStory, Comment, UserStory) {
        $scope.userStory = userStory;
        $scope.addComment = addComment;
        console.log(userStory);

        function addComment(text) {
            var comment = {
                text: text,
                userStoryId: userStory.id
            };

            Comment.save(comment).$promise.then(function (result) {
                $scope.userStory.comments.push(result);
                UserStory.get({userStoryId: userStory.id}).$promise.then(function (result) {
                    $scope.userStory = result;
                });

            })
        }
    }
})();
