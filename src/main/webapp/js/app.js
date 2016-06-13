
angular.module("bootstrap.fileField",[]).directive("fileField",function(){return{require:"ngModel",restrict:"E",link:function(scope,element,attrs,ngModel){if(!attrs.class&&!attrs.ngClass){element.addClass("btn")}var fileField=element.find("input");fileField.bind("change",function(event){scope.$evalAsync(function(){ngModel.$setViewValue(event.target.files[0]);if(attrs.preview){var reader=new FileReader;reader.onload=function(e){scope.$evalAsync(function(){scope[attrs.preview]=e.target.result})};reader.readAsDataURL(event.target.files[0])}})});fileField.bind("click",function(e){e.stopPropagation()});element.bind("click",function(e){e.preventDefault();fileField[0].click()})},template:'<button type="button"><ng-transclude></ng-transclude><input type="file" style="display:none"></button>',replace:true,transclude:true}});
var bgapp=angular.module('baobei.bg', ['ngAnimate', 'ui.bootstrap','ui.router','ui.scrollpoint','chart.js']);

//文字部分
bgapp.config(function($stateProvider,$urlRouterProvider) {

    $urlRouterProvider.otherwise("/query/searchmain");
    $stateProvider
        .state('login', {
            url: "/login",
            templateUrl: "pages/bg/login.html",
            controller: "loginCtrl"


        })
        .state('gored', {
            url: "/red",
            templateUrl: "pages/bg/test.html",
            controller: "redCtrl"
        })
        .state('gonews', {
            url: "/red/news",
            templateUrl: "pages/bg/news.html",
            controller: 'newsCtrl'
        })
          .state('goupload', {
            url: "/query/upload",
            templateUrl: "pages/bg/login.html",
            controller: 'uploadCtrl'
        })
        .state('gostatistics', {
            url: "/query/statistics",
            templateUrl: "pages/query/statistics.html",
            controller: 'statisticsCtrl'
        })
        .state('gopass', {
            url: "/pass",
            templateUrl: "pages/bg/pass.html",
            controller: "passCtrl" 
    })
        .state('gosearch', {
            url: "/query/searchmain",
            templateUrl: "pages/query/querymain.html",
            controller: 'searchCtrl'
        });
});


//图片部分
var dppic=angular.module('dppic', ['ngAnimate', 'ui.bootstrap','ui.router','ui.scrollpoint','chart.js']);

dppic.config(function($stateProvider,$urlRouterProvider) {

    $urlRouterProvider.otherwise("/picsearch");
    $stateProvider
    .state('login', {
        url: "/login",
        templateUrl: "pages/bg/login.html",
        controller: "loginCtrl" 
    })
     .state('gostatistics', {
            url: "/picture/statistics",
            templateUrl: "pages/picture/statistics.html",
            controller: 'statisticsCtrl'
        })
     .state('picsearch', {
        url: "/search",
        templateUrl: "pages/picture/picsearch.html",
        controller: "picSearchCtrl" 
    })
      .state('gopass', {
        url: "/pass",
        templateUrl: "pages/bg/pass.html",
        controller: "passCtrl" 
    })
    .state('gosearch', {
            url: "/picsearch",
            templateUrl: "pages/picture/picmain.html",
            controller: "picCtrl"
        });
        
});