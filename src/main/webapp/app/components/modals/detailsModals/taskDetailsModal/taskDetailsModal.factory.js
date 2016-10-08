(function () {
    angular
        .module('tasker0App')
        .factory('taskDetailsModal', taskDetailsModal);

    taskDetailsModal.$inject = ['$uibModal'];

    function taskDetailsModal($uibModal) {

        var open = function(task) {
            return $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'app/components/modals/detailsModals/taskDetailsModal/taskDetailsModal.html',
                controller: 'TaskDetailsModalCtrl',
                controllerAs: 'vm',
                size: "lg",
                resolve: {
                    task: function () {
                        return task;
                    }
                }
            });
        };

        return {
            open: open
        };
    }

})();
