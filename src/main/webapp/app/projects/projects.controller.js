(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('ProjectsCtrl', ProjectsCtrl);

    ProjectsCtrl.$inject = ['Project', 'createProjectModal'];

    function ProjectsCtrl(Project, createProjectModal) {
        var vm = this;
        vm.createProject = createProject;
        console.log(Object.getOwnPropertyNames(Project));
        Project.getByCurrentUser().$promise.then(function (projects) {
            console.log(projects);
        });

        function createProject() {
            createProjectModal.open().result.then(function (project) {
                Project.save(project);
            });
        }
    }
})();
