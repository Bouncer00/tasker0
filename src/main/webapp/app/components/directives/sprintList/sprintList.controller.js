(function () {
    'use strict';
    
    angular
        .module('tasker0App')
        .controller('sprintListCtrl', sprintListCtrl);
    
    sprintListCtrl.$inject = ['$scope', 'Sprint'];
    
    function sprintListCtrl($scope, Sprint) {
        
        function fetchSprints() {
            Sprint
        }
    }
})();
