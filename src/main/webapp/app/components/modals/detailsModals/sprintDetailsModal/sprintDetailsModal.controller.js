(function () {
    angular
        .module('tasker0App')
        .controller('SprintDetailsModalCtrl', SprintDetailsModalCtrl);

    SprintDetailsModalCtrl.$inject = ['$scope', 'sprint', 'Comment', 'Sprint', '$uibModalInstance'];

    function SprintDetailsModalCtrl($scope, sprint, Comment, Sprint, $uibModalInstance) {
        Sprint.get({sprintId: sprint.id}).$promise.then(function (result) {
            $scope.sprint = result;
        });
        // $scope.sprint = sprint;
        $scope.updateName = updateName;
        $scope.updateCreated = updateCreated;
        $scope.updateDeadline = updateDeadline;
        $scope.addComment = addComment;
        $scope.cancel = cancel;

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }

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
            sprint.start = newCreated;
            Sprint.update(sprint).$promise.then(function (result) {
                console.log(result);
                $scope.sprint = result;
            })
        }

        function updateDeadline(sprint, newDeadline) {
            sprint.end = newDeadline;
            Sprint.update(sprint).$promise.then(function (result) {
                console.log(result)
                $scope.sprint = result;
            })
        }
    }
})();
