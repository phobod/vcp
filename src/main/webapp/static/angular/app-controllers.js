angular.module('app-controllers', ['ngRoute'])
		.config(function($routeProvider, $httpProvider) {
			$routeProvider.when('/main', {
				templateUrl : 'static/html/page/main.html',
				controller : 'allListVideoController'
			});
			$routeProvider.when('/user/:userId/video/:videoId', {
				templateUrl : 'static/html/page/single.html',
				controller : 'listVideosByUserController'
			});
			$routeProvider.when('/user/:userId/video', {
				templateUrl : 'static/html/page/uservideo.html',
				controller : 'allListVideoByUserController'
			});
			$routeProvider.when('/my-account/upload', {
				templateUrl : 'static/html/page/upload.html',
				controller : 'uploadController'
			});
			$routeProvider.when('/video/search', {
				templateUrl : 'static/html/page/video.html',
				controller : 'searchResultController'
			});
			$routeProvider.when('/login', {
				templateUrl : 'static/html/page/login.html',
				controller : 'loginController'
			});
			$routeProvider.when('/access-denied', {
				templateUrl : 'static/html/page/access-denied.html',
				controller : 'accessDeniedController'
			});
			$routeProvider.when('/admin', {
				templateUrl : 'static/html/page/admin.html',
				controller : 'adminController'
			});
			$routeProvider.otherwise({
				redirectTo : '/main'
			});
			$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
		})
		
		.factory('controllersFactory',function(){
			return{
				createPaginationController : function(scope, service){
					scope.videos = service.getData(0);
					scope.loading = false;
					scope.showMore = function (){
						scope.loading = true;
						var nextPage = scope.videos.page.number + 1;
						service.getData(nextPage).$promise.then(function (value) {
							value.content = scope.videos.content.concat(value.content);
							scope.videos = value;
							scope.loading = false;
						});
					};
				}
			}
		})
		
		.controller('allListVideoController',[ '$scope', 'videoService', 'controllersFactory', function($scope, videoService, controllersFactory) {
			$scope.videoPopularList = videoService.listPopularVideos();
			controllersFactory.createPaginationController($scope, {getData : videoService.listAllVideosByPage});
		} ])
		
		.controller('listVideosByUserController',[ '$scope', '$routeParams', 'videoService', 'controllersFactory', function($scope, $routeParams, videoService, controllersFactory) {
			$scope.currentVideo = videoService.findVideoById($routeParams.videoId);
			controllersFactory.createPaginationController($scope, {
				getData : function(page){
					return videoService.listVideosByUserByPage($routeParams.userId, $routeParams.videoId, page); 
				}
			});				
		} ])

		.controller('allListVideoByUserController',[ '$scope', '$routeParams', 'videoService', 'controllersFactory', function($scope, $routeParams, videoService, controllersFactory) {
			controllersFactory.createPaginationController($scope, {
				getData : function(page){
					return videoService.listAllVideosByUserByPage($routeParams.userId, page); 
				}
			});	
		} ])
		
		.controller('searchResultController',[ '$scope', '$location', 'videoService', 'controllersFactory', function($scope, $location, videoService, controllersFactory) {
			$scope.textQuery = $location.search().query;
			controllersFactory.createPaginationController($scope, {
				getData : function(page){
					return videoService.listBySearchQuery($scope.textQuery, page); 
				}
			});	
		} ])
		
		.controller('uploadController', ['$scope', '$window', 'uploadService', function ($scope, $window, uploadService) {
			$scope.submit = function(){
				uploadService.uploadVideo($scope.title, $scope.description, $scope.file).then(function(resp){
					$scope.clearData();
					$window.alert("File upload SUCCESS");
				}, function(resp){
					$scope.clearData();
					$window.alert("File upload FAILED");
				});
			};
			$scope.clearData = function(){
                $scope.title = null;
                $scope.description = null;
                $scope.file = null;				
			};
		}])
		
		.controller('searchController',[ '$scope', '$location', function($scope, $location) {
			$scope.query = '';
			$scope.find = function(){
				console.log('searchController');
				if($scope.query.trim() != ''){
					$location.path('/video/search').search({query: $scope.query});
					$scope.query = '';
				} else {
					$location.path('/main');
				}
			};
		} ])
		
		.controller('loginController', ['$scope', '$location', 'authServisce', function($scope, $location, authServisce){
			$scope.credentials = {
					username :'',
					password:''
			};
			$scope.rememberMe = false;
			var authenticate = function(callback) {
				authServisce.getPrincipal().$promise.then(function(data){
	                if (data.name) {
	                    $scope.authenticated = true;
	                    console.log("authenticated = " + $scope.authenticated);
	                    if (data.authorities[0].authority == "ADMIN") {
		                    $scope.roleAdmin = true;
						} else {
		                    $scope.roleAdmin = false;							
						}
	                } else {
	                    $scope.authenticated = false;
	                    console.log("authenticated = " + $scope.authenticated);
	                    $scope.roleAdmin = false;	
	                }
	                callback && callback();
				}, function(errResponse){
					$scope.authenticated = false;
                    console.log("authenticated = " + $scope.authenticated);
					$scope.roleAdmin = false;	
					callback && callback();
				});
			};
			authenticate();			
			$scope.loginFormSubmit = function (){
				 var data = 'username=' + encodeURIComponent($scope.credentials.username) + '&password=' + encodeURIComponent($scope.credentials.password) + '&remember-me=' + encodeURIComponent($scope.rememberMe);
				 authServisce.login(data).$promise.then(function(){
					 authenticate(function() {
		                    if ($scope.authenticated) {
		                        $scope.error = false;
		                        if ($scope.roleAdmin) {
		                        	$location.path("/admin");
								} else{
			                        $location.path("/main");
								}
		                    } else {
		                        $scope.error = true;
		                        $location.path("/login");
		                    }
		                })
				 }, function(){
					 $location.path("/login");
                     $scope.error = true;
                     $scope.authenticated = false;
	                    console.log("authenticated = " + $scope.authenticated);
				 });
			};
			$scope.logout = function(){
				authServisce.logout().$promise.then(function(){
					$scope.authenticated = false;
				    $location.path("/main");
                    console.log("authenticated = " + $scope.authenticated);
				}, function(){
					$scope.authenticated = false;
                    console.log("authenticated = " + $scope.authenticated);
				});
			};
		}])
		
		.controller('accessDeniedController', ['$scope', 'authServisce', function($scope, authServisce){
			authServisce.getPrincipal().$promise.then(function(data){
				if (data.name) {
					$scope.msg = "Hi " + data.principal.user.name + ", you do not have permission to access this page!";
				} else {
					$scope.msg = "You do not have permission to access this page!";
				}
			});
		}])

		.controller('adminController', ['$scope', 'adminService', function($scope, adminService){
			$scope.users = adminService.listAllUsers();
			console.log('adminController');
		}]);
