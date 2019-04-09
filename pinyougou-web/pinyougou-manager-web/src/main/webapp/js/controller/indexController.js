app.controller("indexController", function ($scope, baseService) {

    $scope.showLoginName = function () {
        baseService.sendGet("/loginName/showLoginName").then(function (response) {
            $scope.loginName = response.data;
        })
    };
});