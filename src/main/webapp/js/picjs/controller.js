picapp.controller('picCtrl',function($scope,$http, $state,httpPostFactory) {
    $scope.entity = {};
    $scope.$on('$viewContentLoaded', function() { 
        alert('1'); 
    });  
    $scope.getresult= function()
	 {
		 var formData = new FormData();
        
        formData.append('queryinput', document.getElementById("queryinput").value);
		    httpPostFactory('resultsearch.php', formData, function (callback) {
		    	 $scope.results=callback;
           });
	 };
	 $scope.getdoc=function(docname)
	 {
		 window.open("http://192.168.2.102/doc/"+docname);
	 };
	 $scope.myKeyup = function(e){
        var keycode = window.event?e.keyCode:e.which;
        if(keycode==13){
       	 $scope.getresult();
        }
    };
});