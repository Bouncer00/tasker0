(function() {
    'use strict';

    angular
        .module('tasker0App')
        .factory('CommentSearch', CommentSearch);

    CommentSearch.$inject = ['$resource'];

    function CommentSearch($resource) {
        var resourceUrl =  'api/_search/comments/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
