 dppic.directive('myDirective', function (httpPostFactory) {
    return {
        restrict: 'A',
        scope: true,
        link: function (scope, element, attr) {

            element.bind('change', function () {
                var formData = new FormData();
               
                formData.append('filedata', element[0].files[0]);
                document.getElementById("upbefor").style.display="none";
                document.getElementById("upafter").style.display="";
                httpPostFactory('piccompare.php', formData, function (callback) {
                	alert($scope.bigposition);
                    document.getElementById("upbefor").style.display="";
                    document.getElementById("upafter").style.display="none";
                });
            });

        }
    };
});


