(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('ProjectsCtrl', ProjectsCtrl);

    ProjectsCtrl.$inject = ['Project', 'createProjectModal'];

    function ProjectsCtrl(Project, createProjectModal) {
        var vm = this;
        vm.createProject = createProject;

        function createProject() {
            createProjectModal.open().result.then(function (project) {
                Project.save(project);
            });
        }
    }
})();
