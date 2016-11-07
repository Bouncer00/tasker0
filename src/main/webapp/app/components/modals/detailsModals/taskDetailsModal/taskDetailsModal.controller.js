(function () {
    angular
        .module('tasker0App')
        .controller('TaskDetailsModalCtrl', TaskDetailsModalCtrl);

    TaskDetailsModalCtrl.$inject = ['$scope', 'task', 'Comment', 'Board'];

    function TaskDetailsModalCtrl($scope, task, Comment, Board) {
        $scope.task = task;
        $scope.addComment = addComment;
        $scope.changeTaskBoard = changeTaskBoard;
        $scope.selectedBoard = task.board;

        console.log($scope.selectedBoard);
        console.log(task);
        getBoards();

        function changeTaskBoard(newBoard) {
            console.log(task);
            console.log("Change task", task.board, newBoard, task.id);
            if(task.board == null || task.board == undefined){
                Board.moveTask({
                    targetBoardId: newBoard.id,
                    taskId: task.id}
                )
            }
            else {
                Board.moveTask({
                        sourceBoardId: task.board.id,
                        targetBoardId: newBoard.id,
                        taskId: task.id
                    }
                )
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
