(function () {
    angular
        .module('tasker0App')
        .factory('projectDetailsModal', projectDetailsModal);

    projectDetailsModal.$inject = ['$uibModal'];

    function projectDetailsModal($uibModal) {

        var open = function(project) {
            return $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'app/components/modals/detailsModals/projectDetailsModal/projectDetailsModal.html',
                controller: 'ProjectDetailsModalCtrl',
                controllerAs: 'vm',
                size: "lg",
                resolve: {
                    project: function () {
                        return project;
                    }
                }
            });
        };

        return {
            open: open
        };
    }

})();
