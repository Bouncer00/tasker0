(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('CreateProjectModalCtrl', CreateProjectModalCtrl);

    CreateProjectModalCtrl.$inject = ['$scope', '$uibModalInstance'];

    function CreateProjectModalCtrl($scope, $uibModalInstance) {
        var vm = this;
        vm.ok = ok;
        vm.cancel = cancel;


        function ok() {
            $uibModalInstance.close(vm.project);
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
