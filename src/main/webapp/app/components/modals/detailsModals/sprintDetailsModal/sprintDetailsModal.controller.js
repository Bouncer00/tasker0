(function () {
    angular
        .module('tasker0App')
        .controller('SprintDetailsModalCtrl', SprintDetailsModalCtrl);

    SprintDetailsModalCtrl.$inject = ['$scope', 'sprint', 'Comment'];

    function SprintDetailsModalCtrl($scope, sprint, Comment) {
        $scope.sprint = sprint;
        console.log(sprint);
        $scope.addComment = addComment;

        function addComment(text) {
            var comment = {
                text: text,
                sprintId: sprint.id
            };

            Comment.save(comment).$promise.then(function (result) {
                $scope.sprint.comments.push(result);
            })
        }
    }
})();
