(function () {
    angular
        .module('tasker0App')
        .controller('SprintDetailsModalCtrl', SprintDetailsModalCtrl);

    SprintDetailsModalCtrl.$inject = ['$scope', 'sprint', 'Comment', 'Sprint'];

    function SprintDetailsModalCtrl($scope, sprint, Comment, Sprint) {
        $scope.sprint = sprint;
        $scope.updateName = updateName;
        $scope.updateCreated = updateCreated;
        $scope.updateDeadline = updateDeadline;
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

        function updateName(sprint, newName) {
            sprint.name = newName;
            Sprint.update(sprint).$promise.then(function (result) {
                $scope.sprint = result;
            })
        }

        function updateCreated(sprint, newCreated) {
            sprint.created = newCreated;
            Sprint.update(sprint).$promise.then(function (result) {
                $scope.sprint = result;
            })
        }

        function updateDeadline(sprint, newDeadline) {
            sprint.deadline = newDeadline;
            Sprint.update(sprint).$promise.then(function (result) {
                $scope.sprint = result;
            })
        }
    }
})();
