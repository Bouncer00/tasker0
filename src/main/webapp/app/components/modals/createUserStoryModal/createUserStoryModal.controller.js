(function () {
    angular
        .module('tasker0App')
        .controller('CreateUserStoryModalCtrl', CreateUserStoryModalCtrl);

    CreateUserStoryModalCtrl.$inject = ['$scope', '$uibModalInstance'];

    function CreateUserStoryModalCtrl($scope, $uibModalInstance) {
        $scope.ok = ok;
        $scope.cancel = cancel;

        function ok() {
            $scope.sprint.projectId = project.id;
            $uibModalInstance.close($scope.sprint);
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }

    }
})();
