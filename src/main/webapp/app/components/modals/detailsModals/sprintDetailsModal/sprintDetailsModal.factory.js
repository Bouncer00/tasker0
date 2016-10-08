(function () {
    angular
        .module('tasker0App')
        .factory('sprintDetailsModal', sprintDetailsModal);

    sprintDetailsModal.$inject = ['$uibModal'];

    function sprintDetailsModal($uibModal) {

        var open = function(sprint) {
            return $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'app/components/modals/detailsModals/sprintDetailsModal/sprintDetailsModal.html',
                controller: 'SprintDetailsModalCtrl',
                controllerAs: 'vm',
                size: "lg",
                resolve: {
                    sprint: function () {
                        return sprint;
                    }
                }
            });
        };

        return {
            open: open
        };
    }

})();
