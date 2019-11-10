app.controller("contentController", function ($scope, contentService) {

    $scope.contentList = [];

    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId] = response;
            }
        )
    }

    //跳转search搜索页面
    $scope.search = function () {
        location.href="http://localhost:9104/search.html#?keywords=" + $scope.keywords;
    }

})