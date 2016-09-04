(function () {
    'use strict';

    angular
        .module('tasker0App')
        .factory('Comment', Comment);

    Comment.$inject = ['$resource'];

    function Comment($resource) {
        var url = '/api/comments';

        return $resource(url, {commentId: '@id'}, {
            'get': {
                url: url + '/:commentId',
                params: {commentId: '@id'}
            }
        });
    }
})();
