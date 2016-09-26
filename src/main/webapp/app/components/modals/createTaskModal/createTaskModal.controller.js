(function () {
    angular
        .module('tasker0App')
        .controller('createTaskModalCtrl', createTaskModalCtrl);

    createTaskModalCtrl.$inject = ['$scope', '$uibModalInstance'];

    function createTaskModalCtrl($scope, $uibModalInstance) {
        $scope.ok = ok;
        $scope.cancel = cancel;

        function ok() {
            $uibModalInstance.close($scope.task);
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }

    }
})();
