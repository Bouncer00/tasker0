(function () {
    angular
        .module('tasker0App')
        .controller('createTaskModalCtrl', createTaskModalCtrl);

    createTaskModalCtrl.$inject = ['$scope', '$uibModalInstance', 'userStory'];

    function createTaskModalCtrl($scope, $uibModalInstance, userStory) {
        $scope.ok = ok;
        $scope.cancel = cancel;
        console.log(userStory);

        function ok() {
            $scope.task.userStoryId = userStory.id;
            $uibModalInstance.close($scope.task);
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }

    }
})();
