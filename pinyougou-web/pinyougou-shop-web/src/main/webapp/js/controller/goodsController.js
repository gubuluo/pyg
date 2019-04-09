/** 定义控制器层 */
app.controller('goodsController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function(page, rows){
        baseService.findByPage("/goods/findByPage", page,
			rows, $scope.searchEntity)
            .then(function(response){
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 添加或修改 */
    $scope.saveOrUpdate = function(){
        $scope.goods.goodsDesc.introduction  = editor.html();

        /** 发送post请求 */
        baseService.sendPost("/goods/save", $scope.goods)
            .then(function(response){
                if (response.data){
                    //清空goods
                    $scope.goods = {};
                    //清空富文本编辑器的内容
                    editor.html('');
                }else{
                    alert("操作失败！");
                }
            });
    };

    /** 显示修改 */
    $scope.show = function(entity){
       /** 把json对象转化成一个新的json对象 */
       $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/goods/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        /** 重新加载数据 */
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
                });
        }else{
            alert("请选择要删除的记录！");
        }
    };


    $scope.uploadFile = function() {
        baseService.uploadFile().then(function (response) {
            if (response.data.status == 200) {
                //alert(response.data.url);
                $scope.picEntity.url = response.data.url;
            }
        });
    };

    //定义数据存储结构，存储到tb_goodsDesc表中
    $scope.goods = {goodsDesc: {itemImages: [],specificationItems :[]}};
    //增加一行图片信息
    $scope.addPic = function () {
        $scope.goods.goodsDesc.itemImages.push($scope.picEntity);
    };
    //删除一行图片信息
    $scope.removePic = function(idx) {
        $scope.goods.goodsDesc.itemImages.splice(idx, 1);
    };

    //查询一级分类
    $scope.findItemCatByParentId = function (id , name) {
        baseService.sendGet("/itemCat/findItemCatByParentId?parentId=" + id).then(function (response) {
            $scope[name] = response.data;
        });
    };

    /*  $watch方法用于监控某个变量的值，被监控的值发生变化就会调用一个函数，就自动执行相应的函数。
        第一个参数：要监控的值
        function中的参数：
            第一个参数：新的值
            第二个参数：旧的值
    */
    $scope.$watch('goods.category1Id', function (newVal, oldVal) {
        //判断newValue的值不为为undefined、Null,如果newVal发生改变就查询下一级
        if(newVal){
            //查询下一级分类
            $scope.findItemCatByParentId(newVal, "itemCatList2");
        }else{
            $scope.itemCatList2 = [];
        }
    });

    //监控二级分类的category2Id
    $scope.$watch('goods.category2Id', function (newVal, oldVal) {
        //判断newValue的值不为为undefined、Null,如果newVal发生改变就查询下一级
        if(newVal){
            //查询下一级分类
            $scope.findItemCatByParentId(newVal, "itemCatList3");
        }else{
            $scope.itemCatList3 = [];
        }
    });

    //监控三级分类的category3Id
    $scope.$watch('goods.category3Id', function (newVal, oldVal) {
        if (newVal) {
            // 循环三级分类数组 List<ItemCat> : [{},{}]
            for (var i = 0; i < $scope.itemCatList3.length; i++) {
                // 取一个数组元素 {}
                var itemCat = $scope.itemCatList3[i];
                // 判断id
                if (itemCat.id == newVal) {
                    $scope.goods.typeTemplateId = itemCat.typeId;
                    break;
                }
            }
        }else{
            //清空typeTemplateId
            $scope.goods.typeTemplateId = null;
        }
    });

    $scope.$watch('goods.typeTemplateId', function (newVal, oldVal) {
        if(newVal) {
            baseService.findOne("/typeTemplate/findOne", newVal).then(function (response) {
                //获取规格模板表中的品牌
                $scope.brandIds = JSON.parse(response.data.brandIds);
                /** 设置扩展属性 */
                $scope.goods.goodsDesc.customAttributeItems =
                    JSON.parse(response.data.customAttributeItems);
            });

            baseService.findOne("/typeTemplate/findSpecByTemplateId", newVal).then(function(response) {
                $scope.specList = response.data;
            });

        }else {
            $scope.brandIds = [];
            $scope.goods.goodsDesc.customAttributeItems = [];
        }

    });

    //定义修改规格选项方法，规格选项中封装的数据格式是[{"attributeValue":["联通4G","移动4G"] , "attiributeName" : "网络"}]
    $scope.updateSpecAttr = function(event,specName,optionName) {
        var obj = $scope.searchJsonByKey($scope.goods.goodsDesc.specificationItems , specName);
        if(obj){
            //obj.attributeValue.push(optionName);
            //判断是否选中
            if(event.target.checked){
                //添加不同的attributeValue
                obj.attributeValue.push(optionName);
            }else{  //取消选中
                //获取取消选中的索引号
                var idx = obj.attributeValue.indexOf(optionName);
                obj.attributeValue.splice(idx, 1);

                //判断attributeValue数组中的长度
                if(obj.attributeValue.length == 0){
                    //获取attributeValue长度为0的对象的索引号
                    var idx = $scope.goods.goodsDesc.specificationItems.indexOf(obj);
                    $scope.goods.goodsDesc.specificationItems.splice(idx, 1);
                }
            }
        }else{
            //如果返回的对象为空，说明是新的规格
            $scope.goods.goodsDesc.specificationItems.push({attributeValue:[optionName] , attributeName:specName})
        }
    };

    //用于判断当前选项是否是同一个attributeName,是同一个attributeName返回当前对象
    $scope.searchJsonByKey = function(jsonArr,specName) {

        for(var i = 0 ; i < jsonArr.length ; i++) {
            //循环$scope.goods.goodsDesc.specificationItems数组中的值，得到数组中的一条数据
            var json = jsonArr[i];
            if(json.attributeName == specName){
                return json;
            }
        }
        return null;
    };


    //创建SKU商品的方法
    $scope.createItems = function () {
        /** 定义SKU数组，并初始化 */
        $scope.goods.items = [{
            spec: {}, price: 0, num: 9999,
            status: '0', isDefault: '0'
        }];
        var specItems = $scope.goods.goodsDesc.specificationItems;

        for (var i = 0; i < specItems.length; i++) {
            //扩充原有的SKU的方法
            $scope.goods.items = swapItems($scope.goods.items, specItems[i].attributeValue, specItems[i].attributeName);
        }
    };

    //扩充的方法
    var swapItems = function(items,attributeValue,attributeName) {
        //创建新的数组
        var newItems = new Array();

        //遍历items
        for(var i = 0 ; i < items.length ; i++) {
            //获得一个SKU
            var item = items[i];
            //遍历规格选项值数组
            for(var j = 0 ; j < attributeValue.length ; j++) {
                //克隆初始化的SKU，得到新的SKU
                var newItem = JSON.parse(JSON.stringify(item));

                //扩充
                newItem.spec[attributeName] = attributeValue[j];
                newItems.push(newItem);
            }
        }
        return newItems;
    };

    /** 定义商品状态数组 */
    $scope.status = ['未审核','已审核','审核未通过','关闭'];

    /** 定义商品上架状态 */
    $scope.markets = ['已下架', '已上架'];

    $scope.updateMarketable = function (status) {
        if ($scope.ids.length > 0) {
            baseService.sendGet("/goods/updateMarketable?ids=" + $scope.ids + "&status=" + status).then(function (response) {
                if (response.data) {
                    $scope.reload();
                    $scope.ids = [];
                } else {
                    alert("操作失败");
                }
            })
        } else {
            alert("请先选择要上架的商品");
        }
    };

});