//控制层
app.controller('goodsController', function ($scope, $controller, $location,goodsService, uploadService, itemCatService, typeTemplateService, loginService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }



    //查询实体
    $scope.findOne = function (id) {
        // 此种写法的目的:
        // 可使用如下形式的网络请求进行触发
        // http://localhost:9102/admin/goods_edit.html#?id=149187842867991
        // 注意?前加#

        var id = $location.search()['id'];// 获取参数值
        if (id==null){
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                // 向富文本编辑器添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction);
                // 图片列表
                $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);

                // 显示扩展属性
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);

                // 规格
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                // SKU库存列表
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    }

    //保存
    $scope.save = function () {

        // 提取富文本编辑器的值
        $scope.entity.goodsDesc.introduction = editor.html();

        //获取商家id
        loginService.loginName().success(
            function (response) {
                $scope.entity.goods.sellerId = response.loginName;
            }
        );
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    alert('保存成功');
                    $scope.entity = {goodsDesc:{itemImages:[], specificationItems:[]}};
                    editor.html("");
                    location.href = "goods.html";
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }

    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }


    //保存
    $scope.add = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        //获取商家id
        $scope.entity.goods.sellerId = $location.search()['loginName'];

        console.log(entity);
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    alert('保存成功');
                    // $scope.entity = {};
                    $scope.entity = {goodsDesc:{itemImages:[], specificationItems:[]}};
                    // 保存成功后清除富文本编辑器内容
                    editor.html('');
                } else {
                    alert(response.message);
                }
            }
        );
    }

    // 上传图片
    $scope.uploadFile = function () {

        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.image_entity.url = response.message;
                } else {
                    alert(response.message);
                }
            }
        ).error(function () {
            alert("上传发生错误");
        });
    }

    //$scope.entity={goods:{},goodsDesc:{itemImages:[]}}; // 定义页面实体结构
    //// 添加图片列表
    //$scope.add_image_entity=function(){
    //	$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    //}
    $scope.entity = {goods: {}, goodsDesc: {itemImages: []}};//定义页面实体结构
    //添加图片列表
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    // 列表中移除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    // 读取一级分类
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        );
    }

    //读取二级分类
    // $watch方法用于监控某个变量的值, 当贝监控值发生变化,自动执行相应函数
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {

        if (newValue) {
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat2List = response;
                }
            );
        }
    });


    //读取三级分类
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {

        if (newValue) {
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat3List = response;
                }
            );
        }
    });

    // 读取模板ID
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        if (newValue) {
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.goods.typeTemplateId = response.typeId;
                }
            );
        }
    });

    /*// 品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {

        if (newValue) {
            typeTemplateService.findOne(newValue).success(
                function (response) {
                    $scope.typeTemplate = response;
                    $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);// 转格式
                    console.log(response);
                }
            );
        }
    });*/

    // 更新模板对象
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {

        if (newValue) {
            typeTemplateService.findOne(newValue).success(
                function (response) {
                    $scope.typeTemplate = response;
                    $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);// 转格式
                    if ($location.search()['id'] == null) {
                        $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                    }
                }
            );
            // 查询规格列表
            typeTemplateService.findSpecList(newValue).success(
                function (response) {
                    $scope.specList = response;
                }
            );
        }
    });

    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};

    // 目标数据结构:
    // [{"attributeName":"规格名称","attributeValue":["规格选项1","规格选项2".....]},.....]
    // [{"attributeName":"网络","attributeValue":["联通3G","联通4G","电信3G"]}]
    // 参数:
    // $event, 网络, 联通3G  第一次调用 - 结果 {"attributeName":"网络","attributeValue":["联通3G"]}
    // $event, 网络, 联通4G  第二次调用 - 结果 {"attributeName":"网络","attributeValue":["联通3G","联通4G"]}
    // $event, 网络, 电信3G  第三次调用
    $scope.updateSpecAttribute = function ($event, name, value) {

        // object - map : {"attributeName":"网络","attributeValue":["联通3G","联通4G","电信3G"]}
        //        - null
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', name);

        if (object != null) {

            if ($event.target.checked) {
                object.attributeValue.push(value);
            } else {
                // 取消勾选
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);// 移除选项
                // 若选项全部都取消了,将此条记录移除
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {

            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }
    }

    // 库存SKU
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];
        // items - [{"attributeName":"网络","attributeValue":["联通3G","联通4G"]}]
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            // [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}]
            // 网络
            // ["联通3G","联通4G"]
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    }
    // 添加列值
    // list - [{"attributeName":"网络","attributeValue":["联通3G","联通4G"]}]
    // columnName - 网络
    // columnValues - ["联通3G","联通4G"]
    addColumn = function (list, columnName, columnValues) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {

            // oldRow - map结构:{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}
            var oldRow = list[i];

            // columnValues - ["联通3G","联通4G"]
            for (var j = 0; j < columnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));// 深克隆
                // 第一次: newRow - {spec: {"网络":"联通3G"}, price: 0, num: 99999, status: '0', isDefault: '0'}
                // 第二次: newRow - {spec: {"网络":"联通4G"}, price: 0, num: 99999, status: '0', isDefault: '0'}
                newRow.spec[columnName] = columnValues[j];
                // newList - [{spec: {"网络":"联通3G"}, price: 0, num: 99999, status: '0', isDefault: '0'}]
                // newList - [{spec: {"网络":"联通3G"}, price: 0, num: 99999, status: '0', isDefault: '0'},{spec: {"网络":"联通4G"}, price: 0, num: 99999, status: '0', isDefault: '0'}]
                newList.push(newRow);
            }
        }
        return newList;
    }

    // 根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue = function (specName,optionName) {
        var items = $scope.entity.goodsDesc.specificationItems;
        var object = $scope.searchObjectByKey(items,'attributeName',specName);
        if (object==null) {
            return false;
        } else {
            if (object.attributeValue.indexOf(optionName)>=0) {
                return true;
            } else {
                return false;
            }
        }
    }

    $scope.status=['未审核','已审核','审核未通过','关闭'];// 商品状态

    $scope.itemCatList = [];
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for(var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        );
    }


});