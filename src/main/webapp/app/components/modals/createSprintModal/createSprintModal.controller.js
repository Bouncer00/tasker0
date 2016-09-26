(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('CreateSprintModalCtrl', CreateSprintModalCtrl);

    CreateSprintModalCtrl.$inject = ['$scope', '$uibModalInstance', 'project'];

    function CreateSprintModalCtrl($scope, $uibModalInstance, project) {
        $scope.ok = ok;
        $scope.cancel = cancel;
        $scope.project = project;

        function ok() {
            $scope.sprint.projectId = project.id;
            $uibModalInstance.close($scope.sprint);
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
