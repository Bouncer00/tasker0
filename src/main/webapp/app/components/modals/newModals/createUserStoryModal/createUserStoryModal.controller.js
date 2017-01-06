(function () {
    angular
        .module('tasker0App')
        .controller('CreateUserStoryModalCtrl', CreateUserStoryModalCtrl);

    CreateUserStoryModalCtrl.$inject = ['$scope', '$uibModalInstance', 'sprint'];

    function CreateUserStoryModalCtrl($scope, $uibModalInstance, sprint) {
        $scope.ok = ok;
        $scope.cancel = cancel;
        $scope.sprint = sprint;
        $scope.userStory = {};

        function ok() {
            $scope.userStory.sprintId = sprint.id;
            $uibModalInstance.close($scope.userStory);
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }

    }
})();
