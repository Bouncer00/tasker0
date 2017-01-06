(function () {
    angular
        .module('tasker0App')
        .controller('UserStoryDetailsModalCtrl', UserStoryDetailsModalCtrl);

    UserStoryDetailsModalCtrl.$inject = ['$scope', 'userStory', 'Comment', 'UserStory', '$uibModalInstance'];

    function UserStoryDetailsModalCtrl($scope, userStory, Comment, UserStory, $uibModalInstance) {
        UserStory.get({userStoryId: userStory.id}).$promise.then(function (result) {
           $scope.userStory = result; 
        });
        // $scope.userStory = userStory;
        $scope.addComment = addComment;
        $scope.updateName = updateName;
        $scope.updateDescription = updateDescription;
        $scope.cancel = cancel;

        function ok() {
            $uibModalInstance.close();
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }

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

        function updateName(userStory, newName) {
            userStory.name = newName;
            UserStory.update(userStory).$promise.then(function (result) {
                $scope.userStory = result;
            })
        }

        function updateDescription(userStory, newDescription) {
            userStory.description = newDescription;
            UserStory.update(userStory).$promise.then(function (result) {
                $scope.userStory = result;
            })
        }
    }
})();
