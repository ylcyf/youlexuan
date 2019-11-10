app.controller('searchController', function ($scope, $location, searchService) {

    $scope.searchMap = {'keywords':'', 'brand':'', 'spec':{}};

    //加载查询字符串 添加location服务用于接收参数
    $scope.loadkeywords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }

    //搜索
    $scope.search = function () {
        if($scope.searchMap.pageNum == null){
            $scope.searchItemByPage();
        }
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;//搜索返回的结果
                buildPageLabel();
            }
        );
    }

    //点击商品的效果，前端传入名，后端可以查到物品数据
    $scope.addSearchItem = function (key, value) {
        $scope.searchMap[key] = value;
        $scope.searchItemByPage(1);
        $scope.search();
    }

    //点击商品的规格，查询物品数据
    $scope.addSearchItemSpec = function (key, value) {
        $scope.searchMap.spec[key] = value;
        $scope.searchItemByPage(1);
        $scope.search();
    }

    //点击面包屑以后，将对应的key值致空
    $scope.delSearchItem = function (key) {
        $scope.searchMap[key] = null;
        $scope.searchItemByPage(1);
        $scope.search();
    }

    //点击规格面包屑以后，将对应的key值致空
    $scope.delSearchItemSpec = function (key) {
        delete $scope.searchMap.spec[key];
        $scope.searchItemByPage(1);
        $scope.search();
    }

    //分页查询
    $scope.searchItemByPage = function (pageNum) {
        if(pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        $scope.searchMap.pageNum = pageNum;
        $scope.searchMap.pageSize = 20;
    }

    //本次自己实现页码显示的算法
    //构建分页标签(totalPages为总页数)
    // 整体逻辑: 当总页数大于5时, 进行判断:
    //         即页码最多显示5个, 起始页码和最终页码根据当前选择页面而变动
    //         1.选择的当前页小于等于3时, 最大显示的页码为5
    //         2.选择的当前页大于等于页码总数前2个时, 最小显示的页码为最终页码-4
    //         3.除上述靠近两端页码的情况外, 起始页码显示当前页-2  最终页显示当前页+2
    //  下述注释以搜索”手机”, 返回共21页数据为例
    buildPageLabel = function () {
        $scope.pageLabel = [];//页面中会遍历这个数组来获取页码,即1,2,3,4,5等页码
        var startPage = $scope.searchMap.pageNum;
        var endPage = $scope.resultMap.totalPages;
        //如果总页数小于5， 就直接输出
        if(endPage <= 5){
            for(var i = 1; i <= endPage; i++){
                $scope.pageLabel.push(i);
            }
            return;
        }
        //如果总页数大于5，则判断当前页是否小于等于3，若是，则显示最大页码为5
        if(startPage <= 3){
            for(var i = 1; i <= 5; i++){
                $scope.pageLabel.push(i);
            }
            $scope.pageLabel.push("....");
            return;
        }
        //如果总页数大于5，则判断当前页是否大于等于页码总数前2个时, 若是，则显示的最小页码为最终页码-4
        if(startPage >= endPage - 2){
            $scope.pageLabel.push("...");
            for(var i = endPage - 4; i <= endPage; i++){
                $scope.pageLabel.push(i);
            }
            return;
        }
        //如果总页数大于5，则判断当前页是否靠近两端页码, 若否，则起始页码显示当前页-2  最终页显示当前页+2
        if(startPage <= endPage - 2){
            $scope.pageLabel.push("...");
            for(var i = startPage - 2; i <= startPage + 2; i++){
                $scope.pageLabel.push(i);
            }
            $scope.pageLabel.push("....");
            return;
        }
    }

    //页面跳转
    $scope.jump = function (num) {
        if(num == 0){
            $scope.searchMap.pageNum -= 1;
        }
        if(num == 'N'){
            $scope.searchMap.pageNum += 1;
        }
        if(num != 0 && num != 'N'){
            $scope.searchMap.pageNum = num;
        }
        $scope.search();
    }

    //当前页加粗
    $scope.bold = function (num) {
        if($scope.searchMap.pageNum == num){
            return true;
        }else{
            return false;
        }
    }

    //排序方法
    $scope.sortByField = function(sortField, sort){
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.searchItemByPage(1);
        $scope.search();
    }

    //判断查询关键字是否为品牌，如果是则隐藏品牌列表
    $scope.keywordsIsBrand = function () {
        var keywords = $scope.searchMap.keywords;
        var brand = $scope.resultMap.brandList;
        for (var i = 0; i < brand.length; i++){
            if(keywords == brand[i].text){
                return true;
            }
        }
        return false;
    }


});