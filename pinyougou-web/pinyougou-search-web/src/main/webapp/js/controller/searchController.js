/** 定义搜索控制器 */
app.controller("searchController" ,function ($scope,$sce, baseService) {

    $scope.searchParam = {keywords:'',category:'',brand:'',spec:{},price:'',page:1,rows:10};
    //搜索
    $scope.search = function () {
        baseService.sendPost("/Search", $scope.searchParam).then(function (response) {
            //获取响应数据 response.data : {total : 100 , rows : [{},{}]}
            $scope.resultMap = response.data;
            //调用页面初始化的方法
            initPageNum();
        });
    };

   //将html格式的字符串转成html标签
    $scope.trustHtml = function (html) {
        return $sce.trustAsHtml(html);
    };

    //封装搜索数据
    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchParam[key] = value;
        } else {
            $scope.searchParam.spec[key] = value;
        }
        $scope.search();
    };

    //删除搜索数据
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchParam[key] = '';
        } else {
            /** 删除规格选项 */
            delete $scope.searchParam.spec[key];
        }
        $scope.search();
    };

    //页面初始化的方法
    var initPageNum = function(){
        //定义页码数组
        $scope.pageNums = [];
        //获取总页数
        var totalPages = $scope.resultMap.totalPages;
        //开始页码
        var firstPage = 1;
        //结束页码
        var lastPage = totalPages;
        //如果总页数大于5，显示部分页码
        if(totalPages > 5){
            //如果当前页码处于前面位置
            if($scope.searchParam.page <= 3){
                lastPage = 5;
            }
            //如果当前页码处于后面位置
            else if($scope.searchParam.page >= totalPages - 3){
                firstPage = totalPages - 4;
            }else{
                firstPage = $scope.searchParam.page - 2;
                lastPage = $scope.searchParam.page + 2;
            }
        }
        //循环产生页码
        for (var i = firstPage; i <= lastPage ; i++){
            $scope.pageNums.push(i);
        }

    }
});
