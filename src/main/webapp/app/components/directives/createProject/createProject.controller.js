(function () {
    angular
        .module('tasker0App')
        .controller('createProjectCtrl', createProjectCtrl);

    createProjectCtrl.$inject = ['Project', 'ModalService'];

    function createProjectCtrl(Project, ModalService) {
        var vm = this;
        vm.showModal = showModal;

        function showModal() {
            console.log("Show modal");
            ModalService.showModal({
                templateUrl: "app/components/modals/createProjectModal/createProjectModal.html",
                controller: 'CreateProjectModalCtrl'
            }).then(function(modal) {
                // The modal object has the element built, if this is a bootstrap modal
                // you can call 'modal' to show it, if it's a custom modal just show or hide
                // it as you need to.
                modal.element.modal();
                modal.close.then(function(result) {
                    console.log(result);
                    Project.save(result).$promise.then(function (result) {
                        console.log(result);
                    })
                });
            });
        }
    }
})();
