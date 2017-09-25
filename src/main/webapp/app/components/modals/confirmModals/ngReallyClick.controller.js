(function () {
    'use strict';
    
    angular.module('tasker0App')
        .controller('ngReallyClickCtrl', ngReallyClickCtrl);
    
    ngReallyClickCtrl.$inject = ['$scope', '$uibModalInstance'];
    
    function ngReallyClickCtrl($scope, $uibModalInstance) {
        $scope.ok = function() {
            $uibModalInstance.close();
        };
        $scope.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
