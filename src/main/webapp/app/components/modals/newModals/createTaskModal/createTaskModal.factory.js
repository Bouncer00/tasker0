(function () {
    angular
        .module('tasker0App')
        .factory('createTaskModal', createTaskModal);

    createTaskModal.$inject = ['$uibModal'];

    function createTaskModal($uibModal) {

        var open = function(userStory) {
            return $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'app/components/modals/newModals/createTaskModal/createTaskModal.html',
                controller: 'createTaskModalCtrl',
                controllerAs: 'vm',
                size: "lg",
                resolve: {
                    userStory: function () {
                        return userStory;
                    }
                }
            });
        };

        return {
            open: open
        };
    }

})();
