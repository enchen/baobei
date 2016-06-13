/*var bgControllers= angular.module('baobei.bg', ['ui.router']);*/
bgapp.controller('bgLoginCtrl', function($scope, $http, $state) {
	$scope.entity = {};

	$scope.bgLoginSubmit = function(valid) {
		if (valid) {
			//alert("pass!");
			$http({
				method : 'GET',
				url : '../baobei/jsons/1.json'
			}).then(function successCallback(response) {
				console.info(response.data[0].name);
				$state.go('gored');

			}, function errorCallback(response) {

			});
		}
	};
});
bgapp.controller('loginCtrl', function($scope, $state,httpPostFactory) {
	$scope.entity = {};
	$scope.login = function(valid) {
		if(valid)
			{
			var formData = new FormData();
			formData.append('username', $scope.entity.userName);
			formData.append('userpass', $scope.entity.passWord);
			httpPostFactory('login.php', formData, function(callback) {
				if(callback=="0")
					{
					$scope.entity.userName="";
					$scope.entity.passWord="";
					}
				else if(callback.length==32)
					{
					setCookie("token",callback);
					$state.go("gosearch");
					}
			});
			}
	};
});
bgapp.controller('redCtrl', function($scope, $state) {
	$scope.gonewsmain = function(gopath) {
		$state.go(gopath);
	};
});
bgapp.controller('newsCtrl', function($scope, $state) {
	$scope.gonewsmain = function(gopath) {
		$state.go(gopath);
	};
});
bgapp.controller('homeCtrl', function($scope, $state,httpPostFactory) {
	$scope.entity = {};
	$scope.radioModel;

	$scope.uploadbtn = function() {
		$state.go('gostatistics');
	};
	$scope.gosearchbtn = function() {
		$state.go('gosearch');
	};
	$scope.gopass = function() {
		$state.go('gopass');
	};
	$scope.goother = function() {
		window.location="http://192.168.2.186:8080/dp/dpimage.html";
	};
	//登陆退出
	$scope.inout =function()
	  {
		    
		  if(getCookie("token")==null) 
			  {
			  $state.go("login");
			  }
		  else
			  {
			  var formData = new FormData();
				formData.append('token', getCookie('token'));
				
				httpPostFactory('islogin.php', formData, function(callback) {
					if(callback=="0")
						{
						$state.go("login");
						}
					else 
						{
						delCookie("token");
						alert("已安全退出！");
						 $state.go("login");
						}
				});
			  }
	   };
});

//bgapp.controller('statisticsCtrl',function($scope, $state) {
//	 $scope.entity = {};
//
//	   
//});
//文字统计
bgapp.controller('statisticsCtrl', function($scope, httpPostFactory) {

	//预制参数
	$scope.labels;
	$scope.series;
	$scope.data;
	$scope.labels1;
	$scope.data1;
	$scope.labels2;
	$scope.data2;
	$scope.labels3;
	$scope.data3;

	//	时间设置
	$scope.open = function($event) {
		$scope.status.opened = true;
	};
	$scope.dt = new Date();
	$scope.status = {
		opened : false
	};
	$scope.dateOptions = {
		formatYear : 'yy',
		startingDay : 1
	};
	$scope.radioModel = 'Right';

	$scope.goget = function() {
		var pt = "week";
		if ($scope.radioModel == 'Right') {
			pt = "week";
		}
		if ($scope.radioModel == 'Middle') {
			pt = "day";
		}
		if ($scope.radioModel == 'Left') {
			pt = "month";
		}

		// 获取总计3变量：
		var formData = new FormData();
		formData.append('passday', $scope.dt.Format("yyyy-MM-dd"));
		formData.append('passtype', pt);
		httpPostFactory('pass7.php', formData, function(callback) {
			$scope.labels = callback.days;
			$scope.series = callback.type;
			$scope.data = callback.counts;
		});
		//		    获取分ip查询  
		var formData = new FormData();
		formData.append('counttype', "query");
		formData.append('passday', $scope.dt.Format("yyyy-MM-dd"));
		formData.append('passtype', pt);

		//从新初始化

		httpPostFactory('pass7area.php', formData, function(callback) {
			$scope.labels1 = callback.ips;

			$scope.data1 = callback.counts;
		});

		//				    获取分ip全文  
		var formData = new FormData();
		formData.append('counttype', "all");
		formData.append('passday', $scope.dt.Format("yyyy-MM-dd"));
		formData.append('passtype', pt);
		httpPostFactory('pass7area.php', formData, function(callback) {
			$scope.labels2 = callback.ips;

			$scope.data2 = callback.counts;
		});

		//					  获取分ip下载  
		var formData = new FormData();
		formData.append('counttype', "down");
		formData.append('passday', $scope.dt.Format("yyyy-MM-dd"));
		formData.append('passtype', pt);
		httpPostFactory('pass7area.php', formData, function(callback) {
			$scope.labels3 = callback.ips;

			$scope.data3 = callback.counts;
		});
		//							  

	};

	$scope.goget();
	$scope.onClick = function(points, evt) {
		console.log(points, evt);
	};
});
//图片统计
dppic.controller('statisticsCtrl', function($scope, httpPostFactory) {

	//预制参数
	$scope.labels;
	$scope.series;
	$scope.data;
	$scope.labels1;
	$scope.data1;
	$scope.labels2;
	$scope.data2;
	$scope.labels3;
	$scope.data3;

	//	时间设置
	$scope.open = function($event) {
		$scope.status.opened = true;
	};
	$scope.dt = new Date();
	$scope.status = {
		opened : false
	};
	$scope.dateOptions = {
		formatYear : 'yy',
		startingDay : 1
	};
	$scope.radioModel = 'Right';

	$scope.goget = function() {
		var pt = "week";
		if ($scope.radioModel == 'Right') {
			pt = "week";
		}
		if ($scope.radioModel == 'Middle') {
			pt = "day";
		}
		if ($scope.radioModel == 'Left') {
			pt = "month";
		}

		// 获取总计3变量：
		var formData = new FormData();
		formData.append('passday', $scope.dt.Format("yyyy-MM-dd"));
		formData.append('passtype', pt);
		httpPostFactory('picpass7.php', formData, function(callback) {
			$scope.labels = callback.days;
			$scope.series = callback.type;
			$scope.data = callback.counts;
		});
		//		    获取描述量  
		var formData = new FormData();
		formData.append('counttype', "despic");
		formData.append('passday', $scope.dt.Format("yyyy-MM-dd"));
		formData.append('passtype', pt);

		//从新初始化

		httpPostFactory('picpass7area.php', formData, function(callback) {
			$scope.labels1 = callback.ips;

			$scope.data1 = callback.counts;
		});

		//				    获取全图量  
		var formData = new FormData();
		formData.append('counttype', "bigpic");
		formData.append('passday', $scope.dt.Format("yyyy-MM-dd"));
		formData.append('passtype', pt);
		httpPostFactory('picpass7area.php', formData, function(callback) {
			$scope.labels2 = callback.ips;

			$scope.data2 = callback.counts;
		});

		//					  获取查询量
		var formData = new FormData();
		formData.append('counttype', "searchpic");
		formData.append('passday', $scope.dt.Format("yyyy-MM-dd"));
		formData.append('passtype', pt);
		httpPostFactory('picpass7area.php', formData, function(callback) {
			$scope.labels3 = callback.ips;

			$scope.data3 = callback.counts;
		});
		//							  

	};

	$scope.goget();
	$scope.onClick = function(points, evt) {
		console.log(points, evt);
	};
});
bgapp.controller('searchCtrl', function($scope, $uibModal, $timeout,$state,
		httpPostFactory, DataTransfer1) {
	$scope.entity = {};
	$scope.results;
	$scope.details;
	$scope.detailname;
	$scope.colorid;
	$scope.getresult = function() {
		var token=getCookie("token");
		var formData = new FormData();
		formData.append("token",token);
		formData.append('queryinput',
				document.getElementById("queryinput").value);
		httpPostFactory('resultsearch.php', formData, function(callback) {
			if(callback=="0000")
				{
				delCookie("token");
				$state.go('login');
				return;
				}
			
			$scope.results = callback;
		});
	};
	$scope.getdoc = function(docname) {//获取文档方法
		//alert(docname);
		//var formData = new FormData();
		//   {      
		//         formData.append('queryinput', docname);
		//		    httpPostFactory('downloadrecord.php', formData, function (callback) {
		//		    	 
		//            });
		while (docname.indexOf("&") != -1) {
			docname = docname.replace('&', 'sb250');
		}

	window.open("http://192.168.2.186:8080/dp/down.php?token="+getCookie('token')+"&docpath="
				//window.open("http://localhost:8888/baobei/down.php?token="+getCookie('token')+"&docpath="
				+ encodeURI(encodeURI(docname)));
	};
	$scope.myKeyup = function(e) {
		var keycode = window.event ? e.keyCode : e.which;
		if (keycode == 13) {
			$scope.getresult();
		}
	};
	$scope.seeall = function(seefilename, inputpath, fileid) {
		document.getElementById("s02").style.display = "";
		document.getElementById("s01").style.display = "none";
		// document.getElementById('s02').scrollIntoView();
		$scope.detailname = seefilename;
		var formData = new FormData();
		formData.append('token', getCookie("token"));
		formData.append('queryinput', seefilename);
		formData.append('inputpath', inputpath);
		DataTransfer1.filename = seefilename;
		DataTransfer1.filepath = inputpath;
		httpPostFactory('resultdetails.php', formData, function(callback) {
			if(callback=="0000")
				{
				delCookie("token");
				$state.go('login');
				return;
				}
			$scope.details = callback;

			$timeout(function() {
				var nowloca = document.getElementById(fileid);
				var beloca = document.getElementById($scope.colorid);
				if (beloca != null) {
					beloca.style.background = "#4E342E";
				}

				$scope.colorid = fileid + "bbb";
				nowloca.style.fontWeight = "bold";
				nowloca.style.background = "#9C27B0";
				nowloca.scrollIntoView();
			}, 500);

		});
	};

	$scope.seeallreturn = function() {
		document.getElementById("s02").style.display = "none";
		document.getElementById("s01").style.display = "";

		var nowloca = document.getElementById($scope.colorid);

		nowloca.style.fontWeight = "bold";
		nowloca.style.background = "#9C27B0";
		nowloca.scrollIntoView(false);
	};
	$scope.open = function(size) {

		var modalInstance = $uibModal.open({
			animation : true,
			templateUrl : 'myModalContent.html',
			controller : 'ModalInstanceCtrl',
			size : size,
			resolve : {

			}

		});
	};
   
	//     $scope.$watch(document.getElementById("rebtn").offsetTop,function calculateDiscount(newValue, oldValue, scope) {
	//    	   alert(newValue);
	//     });

});
bgapp.controller('ModalInstanceCtrl', function($scope, $uibModalInstance,
		httpPostFactory, DataTransfer1) {

	$scope.ok = function() {
		var markreason1 = document.getElementById("check1").checked ? "不喜欢 "
				: "";
		var markreason2 = document.getElementById("check2").checked ? "没价值"
				: "";

		if ((markreason1 + markreason2) == "") {
			$uibModalInstance.dismiss('cancel');
			return;
		}
		var formData = new FormData();
		formData.append('markpath', DataTransfer1.filepath);
		formData.append('markname', DataTransfer1.filename);
		formData.append('markreason', markreason1 + markreason2);
		httpPostFactory('makemark.php', formData, function(callback) {
			if (callback == 0) {
				$uibModalInstance.close();
				document.getElementById("s02").style.display = "none";
				document.getElementById("s01").style.display = "";
			}
		});

	};

	$scope.cancel = function() {
		// alert(document.getElementById("check1").checked);
		$uibModalInstance.dismiss('cancel');
	};
});
// 对Date的扩展，将 Date 转化为指定格式的String   
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，   
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)   
// 例子：   
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423   
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18   
Date.prototype.Format = function(fmt) { //author: meizz   
	var o = {
		"M+" : this.getMonth() + 1, //月份   
		"d+" : this.getDate(), //日   
		"h+" : this.getHours(), //小时   
		"m+" : this.getMinutes(), //分   
		"s+" : this.getSeconds(), //秒   
		"q+" : Math.floor((this.getMonth() + 3) / 3), //季度   
		"S" : this.getMilliseconds()
	//毫秒   
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
					: (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
};
//图片
dppic.controller('pichomeCtrl', function($scope, $state,httpPostFactory) {
	
	$scope.entity = {};
	$scope.radioModel;
	$scope.gostastics=function()
	{
		$state.go('gostatistics');
	};
	$scope.gopicbtn = function() {
		$state.go('gosearch');
	};
	$scope.gopicsearch = function() {
		$state.go('picsearch');
	};
	$scope.gopass = function() {
		$state.go('gopass');
	};
	$scope.goother = function() {
		window.location="http://192.168.2.186:8080/dp/dpsearch.html";
	};
	//登陆退出
	$scope.inout =function()
	  {
		    
		  if(getCookie("token")==null) 
			  {
			  $state.go("login");
			  }
		  else
			  {
			  var formData = new FormData();
				formData.append('token', getCookie('token'));
				
				httpPostFactory('islogin.php', formData, function(callback) {
					if(callback=="0")
						{
						$state.go("login");
						}
					else 
						{
						delCookie("token");
						alert("已安全退出！");
						 $state.go("login");
						}
				});
			  }
	   };
});
//密码
dppic.controller('passCtrl', function($scope,$uibModal,$state,httpPostFactory){
	$scope.entity = {};
	$scope.ok=function()
	{
	if($scope.entity.name==undefined||$scope.entity.pass==undefined||$scope.entity.newpass==undefined||$scope.entity.renewpass==undefined)
		{
		return;
		}
	else
		{
		if($scope.entity.newpass!=$scope.entity.renewpass)
			{
			alert("新输入口令不一致");
			return;
			}
		if($scope.entity.newpass==$scope.entity.pass)
		{
		alert("新旧口令是一样的");
		return;
		}
		var formData = new FormData();
	      formData.append('username',$scope.entity.name);
	      formData.append('userpass', $scope.entity.pass);
	      formData.append('newpass', $scope.entity.newpass);
httpPostFactory('newpass.php', formData, function(callback) {
if(callback=="1")
	{
	alert("修改成功");
	$state.go("login");
	}
else
	{
	alert("修改失败");
	}
});
		}
};
});
bgapp.controller('passCtrl', function($scope,$uibModal,$state,httpPostFactory){
	$scope.entity = {};
	$scope.ok=function()
	{
	
	if($scope.entity.name==undefined||$scope.entity.pass==undefined||$scope.entity.newpass==undefined||$scope.entity.renewpass==undefined)
	{
	return;
	}
else
	{
	if($scope.entity.newpass!=$scope.entity.renewpass)
		{
		alert("新输入口令不一致");
		return;
		}
	if($scope.entity.newpass==$scope.entity.pass)
	{
	alert("新旧口令是一样的");
	return;
	}
	var formData = new FormData();
      formData.append('username',$scope.entity.name);
      formData.append('userpass', $scope.entity.pass);
      formData.append('newpass', $scope.entity.newpass);
httpPostFactory('newpass.php', formData, function(callback) {
if(callback=="1")
{
alert("修改成功");
$state.go("login");
}
else
{
alert("修改失败");
}
});
	}
	};
});
//目录查询
dppic.controller('picCtrl', function($scope,$uibModal,$state,httpPostFactory){
	$scope.isCollapsed = true;
	$scope.entity = {};	
	$scope.bigposition="right";
	$scope.bigvisible="none";
	$scope.bigimage="";
	$scope.cwidth="";
	$scope.cheight="";
	$scope.cname="";
	$scope.nowdir="";
	$scope.picdescribles=[];//图片描述
	$scope.index=0;//当前操作实体序号
	$scope.isdescribled=false;

	$scope.getpics = function(pname) {
		//alert(pname.substring(8));
		var formData = new FormData();
				      formData.append('pname', pname.substring(8));
		//		      formData.append('markname', DataTransfer1.filename);
		//		      formData.append('markreason', markreason1+markreason2);
		httpPostFactory('gettreepic.php', formData, function(callback) {
			$scope.entity=callback;
		});
	};
	//获取树子节点列表
	$scope.gettreeboys = function(data) {
		//obj.style.backgroundColor="blue";
		$scope.nowdir=data.name;
		if(data.nodes.length>0)
		{
			data.nodes = [];
			$scope.getpics(data.pname);
			
			return;
		}
		var formData = new FormData();
	    formData.append('pname', data.pname);
		httpPostFactory('getboys.php', formData, function(callback) {
			for(var i=0;i<callback.length;i++)
				{
				  data.nodes.push({name:callback[i].name,pname:callback[i].pname,nodes: []});
				}
			$scope.getpics(data.pname);
			
		});
	};
	
	//树部分开始
	    $scope.add = function(data) {
	        var post = data.nodes.length + 1;
	        var newName = data.name + '-' + post;
	        data.nodes.push({name: newName,nodes: []});
	    };
	  $scope.tree = [{name:"图库",pname: "dpimage/", nodes: []}];
	    //$scope.tree = [];
	   // var initdata=[{name:"",pname:"dpimage/",nodes:[]}];
	   // initdata.pname="dpimage/";
	    
	   // $scope.gettreeboys(initdata);
	//树部分结束
	//$scope.getpics();
	$scope.picin=function(ee)
	{
		$scope.bigvisible="";
		$scope.bigimage="http://192.168.2.249/www/middle/dpimage/"+$scope.entity[ee].picpath;
		$scope.cwidth=$scope.entity[ee].width;
		$scope.cheight=$scope.entity[ee].height;
		
		$scope.cname=$scope.entity[ee].picpath.substring($scope.entity[ee].picpath.lastIndexOf('/')+1);
		console.log("进入");
	};
	$scope.picout=function(ee)
	{
		$scope.bigvisible="none";
	};
	$scope.bigin=function()
	{
		$scope.bigposition=="right"?$scope.bigposition="left":$scope.bigposition="right";
	};
	
	  $scope.animationsEnabled = true;

	  $scope.toggleAnimation = function () {
	    $scope.animationsEnabled = !$scope.animationsEnabled;
	  };
	  
	  $scope.bigbigpic="456";
	  
	  $scope.gobigdown=function(imageurl,index)
	  {
		  $scope.index=index;
		  $scope.isdescribled=false;
		  $scope.bigbigpic=imageurl.replace("middle/","big/");
		 // alert($scope.bigbigpic);
		  var modalInstance = $uibModal.open({
		      animation: $scope.animationsEnabled,
		      templateUrl: 'myModalContent.html',
		      scope: $scope,
		      size: 'lg',
		 
		    });
		
			$scope.getdescribles(imageurl.substring(imageurl.indexOf("dpimage")+8));
	  };
	 
	  $scope.getdescribles = function(imageurl) {
			
			var formData = new FormData();
		    formData.append('pname', imageurl);
			httpPostFactory('getdescribles.php', formData, function(callback) 
			{
			$scope.picdescribles=callback;	
			});
		};
		//新增描述
		$scope.newdescrible =function()
		{
			var de=document.getElementById("newde").value;
			if(de=="")
				{
				alert("不能添加空描述!");
				return;
				}
			
			var formData = new FormData();
		    formData.append('pname', $scope.bigbigpic.substring($scope.bigbigpic.indexOf("dpimage")+8));
		    formData.append('pwidth', $scope.index.width);
		    formData.append('pheight', $scope.index.height);
		    formData.append('describle', de);
		    formData.append('token', getCookie('token'));
			httpPostFactory('newdescribles.php', formData, function(callback) 
			{
				if(callback=="0000")
					{
					delCookie("token");
					$state.go('login');
					return;
					}
				$scope.isdescribled=true;
				$scope.picdescribles.push(de);
				document.getElementById("newde").value="";
			});
		};
		//点击描述
		$scope.declick =function()
		{
			$scope.isdescribled=true;
		};
		//下载图片
		$scope.ok = function () {
			if($scope.isdescribled)
				{
				var pnamestr=encodeURI(encodeURI($scope.bigbigpic.substring($scope.bigbigpic.indexOf("dpimage")+8)));
			    console.log(pnamestr);
//				window.open("http://192.168.2.186:8080/dp/downpic.php?token="+getCookie('token')+"&pname="
//						+ pnamestr);
			    var formData = new FormData();
				formData.append("token",getCookie('token'));
				formData.append('pname',pnamestr);
				var winRef = window.open("", "_blank");
				httpPostFactory('downpic.php', formData, function(callback) {
					if(callback=="0000")
						{
						delCookie("token");
						$state.go('login');
						return;
						}
					else if(callback=="ok")
						{
						// window.open("http://192.168.2.249/www/dpimage/"+$scope.bigbigpic.substring($scope.bigbigpic.indexOf("dpimage")+8));
						 function loc(){
						      var ll = "http://192.168.2.249/www/dpimage/"+$scope.bigbigpic.substring($scope.bigbigpic.indexOf("dpimage")+8);
						      winRef.location = ll;//改变页面的 location
						     }
						     setTimeout(loc(),400);//这个等待很重要，如果不等待的话将无法实现
						}
					
					else
						{
						alert("服务器错误03");
						}
				});
			   
  
		  };

};
});
//特征查询
dppic.controller('picSearchCtrl', function($scope, $state,$uibModal,httpPostFactory) {
	$scope.keyword="";
	$scope.dirlist = {};
	$scope.entity = {};
	$scope.dirboys = {};
	$scope.choicedirs=new Array();
	$scope.bigposition="right";
	$scope.bigvisible="none";
	$scope.bigimage="";
	$scope.cwidth="";
	$scope.cheight="";
	$scope.cname="";
	$scope.nowdir="";
	$scope.picdescribles=[];//图片描述
	$scope.index=0;//当前操作实体序号
	$scope.isdescribled=false;
	$scope.keyup=function()
	{
		var skey=document.getElementById("skey").value.replace(/\s+/g,"");
		if(skey=="")
			{
			$scope.dirlist = {};
			return;
			}
		if(document.getElementById("ismulu").checked)
		{
		var formData = new FormData();
	    formData.append('pname', skey);
	    formData.append('token', getCookie('token'));
		httpPostFactory('getpicdirs.php', formData, function(callback) 
		{
			
			if(callback=="0000")
				{
				delCookie("token");
				$state.go('login');
				return;
				}
			$scope.dirlist=callback;
			
		});
		}
		
	};
	
	$scope.uploadme = "";
	
	$scope.picfamiliar=function()
	{
		var formData = new FormData();
        var ss=document.getElementById("picfile").files[0];
       
        document.getElementById("picfile").outerHTML=document.getElementById("picfile").outerHTML.replace(/(value=\").+\"/i,"$1\""); 
        if(ss==undefined)
        	{
        	return;
        	}
        var houzui=ss.name.substring(ss.name.length-3).toLowerCase();
      if(houzui=="jpg"|houzui=="png"|houzui=="gif")
    	  {
    	 
    	  
    	  }
      else
    	  {
    	  alert("仅支持jpg,png,gif格式源图片上传");
    	  return;
    	  }
        formData.append('filedata', ss);
        formData.append('token', getCookie('token'));
        httpPostFactory('piccompare.php', formData, function (callback) {
        	if(callback!="0")
        		{
        		if(callback=="0000")
    			{
    			delCookie("token");
    			$state.go('login');
    			return;
    			}
        		 $scope.entity=callback;
        		}
        });
	};
	$scope.gogo=function()
	{
		var skey=document.getElementById("skey").value.replace(/\s+/g,"");
		if(skey=="")
			{
			$scope.dirlist = {};
			return;
			}
		var formData = new FormData();
	      formData.append('pname', skey);
	      formData.append('token', getCookie('token'));
        httpPostFactory('getelasticpic.php', formData, function(callback) {
        	if(callback=="0000")
			{
			delCookie("token");
			$state.go('login');
			return;
			}
        $scope.dirlist = {};
        $scope.entity=callback;
        
});
	};
	$scope.gobydir=function(data)
	{
		$scope.choicedirs.unshift(data);
		var formData = new FormData();
	      formData.append('pname', data+"/");
          httpPostFactory('gettreepic.php', formData, function(callback) {
          $scope.dirlist = {};
          $scope.entity=callback;
          
});
          var formData2 = new FormData();
	      formData2.append('pname', 'dpimage/'+data+"/");
          httpPostFactory('getboys.php', formData2, function(callback) {
         
          $scope.dirboys=callback;
          
});
	};
	//点击文件夹查找
	$scope.gobydir2=function(data)
	{
		
		var ppath=data.pname.substring(8);
		$scope.choicedirs.unshift(ppath.substring(0,ppath.length-1));
		var formData = new FormData();
	      formData.append('pname', ppath);
          httpPostFactory('gettreepic.php', formData, function(callback) {
          $scope.dirlist = {};
          $scope.entity=callback;
          
});
          var formData2 = new FormData();
	      formData2.append('pname', data.pname);
          httpPostFactory('getboys.php', formData2, function(callback) {
         
          $scope.dirboys=callback;
          
});
	};
	//历史点击
	$scope.gobydir3=function(data)
	{
		
		var formData = new FormData();
	      formData.append('pname', data+"/");
          httpPostFactory('gettreepic.php', formData, function(callback) {
          $scope.dirlist = {};
          $scope.entity=callback;
          
});
          var formData2 = new FormData();
	      formData2.append('pname', 'dpimage/'+data+"/");
          httpPostFactory('getboys.php', formData2, function(callback) {
         
          $scope.dirboys=callback;
          
});
	};
	$scope.picin=function(ee)
	{
		$scope.bigvisible="";
		$scope.bigimage="http://192.168.2.249/www/middle/dpimage/"+$scope.entity[ee].picpath;
		if($scope.entity[ee].width==""||$scope.entity[ee].width==undefined)
			{
			$scope.moreinfo($scope.entity[ee].picpath);
			}
		else
			{
		$scope.cwidth=$scope.entity[ee].width;
		$scope.cheight=$scope.entity[ee].height;
			}
		$scope.cname=$scope.entity[ee].picpath.substring($scope.entity[ee].picpath.lastIndexOf('/')+1);
		
	};
	
	$scope.picout=function(ee)
	{
		$scope.bigvisible="none";
	};
	$scope.moreinfo=function(picpath)
	{
		var formData = new FormData();
	    formData.append('pname', picpath);
		httpPostFactory('getmoreinfo.php', formData, function(callback) 
		{
			$scope.cwidth=callback.width;
			$scope.cheight=callback.height;
		});
	};
	$scope.bigin=function()
	{
		$scope.bigposition=="right"?$scope.bigposition="left":$scope.bigposition="right";
	};
	
	  $scope.animationsEnabled = true;

	  $scope.toggleAnimation = function () {
	    $scope.animationsEnabled = !$scope.animationsEnabled;
	  };
	  
	  $scope.bigbigpic="456";
	  
	  $scope.gobigdown=function(imageurl,index)
	  {
		  $scope.index=index;
		  $scope.isdescribled=false;
		  $scope.bigbigpic=imageurl.replace("middle/","big/");
		 // alert($scope.bigbigpic);
		  var modalInstance = $uibModal.open({
		      animation: $scope.animationsEnabled,
		      templateUrl: 'myModalContent.html',
		      scope: $scope,
		      size: 'lg',
		 
		    });
		
			$scope.getdescribles(imageurl.substring(imageurl.indexOf("dpimage")+8));
	  };
	 
	  $scope.getdescribles = function(imageurl) {
			
			var formData = new FormData();
		    formData.append('pname', imageurl);
			httpPostFactory('getdescribles.php', formData, function(callback) 
			{
			$scope.picdescribles=callback;	
			});
		};
		//新增描述
		$scope.issubmiting=false;
		$scope.newdescrible =function()
		{
			if($scope.issubmiting)
				{return;}
			var de=document.getElementById("newde").value;
			if(de=="")
				{
				alert("不能添加空描述!");
				return;
				}
			$scope.issubmiting=true;
			var formData = new FormData();
		    formData.append('pname', $scope.bigbigpic.substring($scope.bigbigpic.indexOf("dpimage")+8));
		    formData.append('pwidth', $scope.index.width);
		    formData.append('pheight', $scope.index.height);
		    formData.append('describle', de);
		    formData.append('token', getCookie('token'));
			httpPostFactory('newdescribles.php', formData, function(callback) 
			{
				$scope.issubmiting=false;
				if(callback=="0000")
					{
					delCookie("token");
					$state.go('login');
					return;
					}
				$scope.isdescribled=true;
				$scope.picdescribles.push(de);
				document.getElementById("newde").value="";
			});
		};
		//点击描述
		$scope.declick =function()
		{
			$scope.isdescribled=true;
		};
		//下载图片
		$scope.ok = function () {
			if($scope.isdescribled)
				{
				var pnamestr=encodeURI(encodeURI($scope.bigbigpic.substring($scope.bigbigpic.indexOf("dpimage")+8)));
			    console.log(pnamestr);
//				window.open("http://192.168.2.186:8080/dp/downpic.php?token="+getCookie('token')+"&pname="
//						+ pnamestr);
			    var formData = new FormData();
				formData.append("token", getCookie('token'));
				formData.append('pname',pnamestr);
				var winRef = window.open("", "_blank");//打开一个新的页面
				httpPostFactory('downpic.php', formData, function(callback) {
					if(callback=="0000")
						{
						delCookie("token");
						$state.go('login');
						return;
						}
					else if(callback=="ok")
						{
						 //window.open("http://192.168.2.249/www/dpimage/"+$scope.bigbigpic.substring($scope.bigbigpic.indexOf("dpimage")+8));
						 function loc(){
						      var ll = "http://192.168.2.249/www/dpimage/"+$scope.bigbigpic.substring($scope.bigbigpic.indexOf("dpimage")+8);
						      winRef.location = ll;//改变页面的 location
						     }
						     setTimeout(loc(),400);//这个等待很重要，如果不等待的话将无法实现
						}
					else
						{
						alert("服务器错误03");
						}
					
					
				});
		  };
		 };
	
});
dppic.controller('ModalInstanceCtrl', function ($scope, $uibModalInstance, bigbigpic) {

	  $scope.bigbigpic = bigbigpic;
	 
	  $scope.ok = function () {
	    $uibModalInstance.close($scope.selected.item);
	  };

	  $scope.cancel = function () {
	    $uibModalInstance.dismiss('cancel');
	  };
	});
dppic.controller('loginCtrl', function($scope, $state,httpPostFactory) {
	$scope.entity = {};
	$scope.login = function(valid) {
		if(valid)
			{
			var formData = new FormData();
			formData.append('username', $scope.entity.userName);
			formData.append('userpass', $scope.entity.passWord);
			httpPostFactory('login.php', formData, function(callback) {
				if(callback=="0")
					{
					alert("无效的用户/密码");
					}
				else if(callback.length==32)
					{
					setCookie("token",callback);
					$state.go("gosearch");
					}
			});
			}
	};
});
function setCookie(name,value)
{
var Days = 1;
var exp = new Date();
exp.setTime(exp.getTime() + Days*24*60*60*1000);
document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}

function getCookie(name)
{
var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
if(arr=document.cookie.match(reg))
return unescape(arr[2]);
else
return null;
}
function delCookie(name)
{
var exp = new Date();
exp.setTime(exp.getTime() - 1);
var cval=getCookie(name);
if(cval!=null)
document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}