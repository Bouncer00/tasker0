(function () {
    'use strict';

    angular
        .module('tasker0App')
        .controller('ProjectsCtrl', ProjectsCtrl);

    ProjectsCtrl.$inject = ['Project'];

    function ProjectsCtrl(Project) {
        var vm = this;
    }
})();
