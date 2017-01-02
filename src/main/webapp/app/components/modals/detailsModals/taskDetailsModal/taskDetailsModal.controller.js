(function () {
    angular
        .module('tasker0App')
        .controller('TaskDetailsModalCtrl', TaskDetailsModalCtrl);

    TaskDetailsModalCtrl.$inject = ['$scope', 'task', 'Comment', 'Board', 'Task'];

    function TaskDetailsModalCtrl($scope, task, Comment, Board, Task) {
        $scope.addComment = addComment;
        $scope.changeTaskBoard = changeTaskBoard;
        $scope.assignTaskToCurrentUser = assignTaskToCurrentUser;

        fetchTaskById(task.id);

        function fetchTaskById(id) {
            Task.getById({taskId: task.id}).$promise.then(function (result) {
                $scope.task = result;
                console.log($scope.task);
                getBoards();
            });
        }

        function assignTaskToCurrentUser() {
            Task.assignToCurrentUser({taskId: $scope.task.id}).$promise.then(function (result) {
                fetchTaskById($scope.task.id);
            });
        }

        function changeTaskBoard(newBoard) {
            if(task.board == null || task.board == undefined){
                Board.moveTask({
                    targetBoardId: newBoard.id,
                    taskId: task.id}
                ).$promise.then(function (result) {
                    $scope.task = result;
                    // $scope.selectedBoard = $scope.task.board;
                    // console.log($scope.task);
                })
            }
            else {
                Board.moveTask({
                        sourceBoardId: task.board.id,
                        targetBoardId: newBoard.id,
                        taskId: task.id
                    }
                ).$promise.then(function (result) {
                    $scope.task = result;
                    // $scope.selectedBoard = $scope.task.board;
                    // console.log($scope.task);
                })
            }
        }

        function addComment(text) {
            var comment = {
                text: text,
                taskId: task.id
            };

            Comment.save(comment).$promise.then(function (result) {
                $scope.task.comments.push(result);
            })
        }

        function getBoards() {
            Board.byProject({projectId: task.projectId}).$promise.then(function (result) {
                $scope.boards = result.content;
            })
        }
    }
})();
