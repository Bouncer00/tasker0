(function () {
    angular
        .module('tasker0App')
        .controller('TaskDetailsModalCtrl', TaskDetailsModalCtrl);

    TaskDetailsModalCtrl.$inject = ['$scope', 'task', 'Comment', 'Board', 'Task', '$uibModalInstance'];

    function TaskDetailsModalCtrl($scope, task, Comment, Board, Task, $uibModalInstance) {
        $scope.addComment = addComment;
        $scope.changeTaskBoard = changeTaskBoard;
        $scope.assignTaskToCurrentUser = assignTaskToCurrentUser;
        $scope.updateTitle = updateTitle;
        $scope.updateDescription = updateDescription;
        $scope.ok = ok;
        $scope.cancel = cancel;

        fetchTaskById(task.id);

        function ok() {
            $uibModalInstance.close();
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }

        function fetchTaskById(id) {
            Task.getById({taskId: task.id}).$promise.then(function (result) {
                $scope.task = result;
                getBoards();
            });
        }

        function assignTaskToCurrentUser() {
            Task.assignTaskToCurrentUser({taskId: $scope.task.id}).$promise.then(function (result) {
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

        function updateTitle(task, newTitle) {
            task.title = newTitle;
            Task.update(task).$promise.then(function (result) {
                $scope.task = result;
            })
        }

        function updateDescription(task, newDescription) {
            task.description = newDescription;
            Task.update(task).$promise.then(function (result) {
                $scope.task = result;
            })
        }
    }
})();
