angular.module('common-controllers', ['ngRoute'])
		.config(['$routeProvider', function($routeProvider) {
			$routeProvider.when('/admin/account', {
				templateUrl : 'static/html/page/account.html',
				controller : 'accountController'
			});
			$routeProvider.when('/admin/company', {
				templateUrl : 'static/html/page/company.html',
				controller : 'companyController'
			});
			$routeProvider.when('/admin/statistics', {
				templateUrl : 'static/html/page/statistics.html',
				controller : 'statisticsController'
			});
			$routeProvider.when('/my-account/video', {
				templateUrl : 'static/html/page/myaccount.html',
				controller : 'allListVideoMyAccountController'
			});
			$routeProvider.when('/my-account/upload', {
				templateUrl : 'static/html/page/upload.html',
				controller : 'uploadVideoController'
			});
			$routeProvider.when('/my-account/video/:videoId', {
				templateUrl : 'static/html/page/editvideo.html',
				controller : 'editVideoController'
			});
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
			$routeProvider.when('/video/search', {
				templateUrl : 'static/html/page/video.html',
				controller : 'searchResultController'
			});
			$routeProvider.when('/login', {
				templateUrl : 'static/html/page/login.html',
				controller : 'loginController'
			});
			$routeProvider.when('/recovery', {
				templateUrl : 'static/html/page/forgot-password.html',
				controller : 'recoveryAccessController'
			});
			$routeProvider.when('/recovery/acsess/:userId/:hash', {
				templateUrl : 'static/html/page/password-recovery.html',
				controller : 'recoveryAccessController'
			});
			$routeProvider.when('/access-denied', {
				templateUrl : 'static/html/page/access-denied.html'
			});
			$routeProvider.when('/error', {
				templateUrl : 'static/html/page/error.html'
			});
			$routeProvider.otherwise({
				redirectTo : '/main'
			});
		}])
		
		.factory('controllersFactory',function(){
			return{
				createPaginationController : function(scope, service){
					scope.records = service.getData(0);
					scope.loading = false;
					scope.showMore = function (){
						scope.loading = true;
						var nextPage = scope.records.number + 1;
						service.getData(nextPage).$promise.then(function (value) {
							value.content = scope.records.content.concat(value.content);
							scope.records = value;
							scope.loading = false;
						});
					};
				}
			}
		})
		
		.controller('allListVideoController',[ '$scope', 'videoService', 'controllersFactory', function($scope, videoService, controllersFactory) {
			$scope.videoPopularList = videoService.listPopularVideos();
			controllersFactory.createPaginationController($scope, {getData : videoService.listAllVideos});
		} ])
		
		.controller('listVideosByUserController',[ '$scope', '$routeParams', 'videoService', 'controllersFactory', function($scope, $routeParams, videoService, controllersFactory) {
			$scope.currentVideo = videoService.findVideoById($routeParams.videoId);
			controllersFactory.createPaginationController($scope, {
				getData : function(page){
					return videoService.listVideosByUserExcludeOne($routeParams.userId, $routeParams.videoId, page); 
				}
			});				
		} ])

		.controller('allListVideoByUserController',[ '$scope', '$routeParams', 'videoService', 'controllersFactory', function($scope, $routeParams, videoService, controllersFactory) {
			controllersFactory.createPaginationController($scope, {
				getData : function(page){
					return videoService.listVideosByUser($routeParams.userId, page); 
				}
			});	
		} ])
		
		.controller('searchResultController',[ '$scope', '$location', 'videoService', 'controllersFactory', function($scope, $location, videoService, controllersFactory) {
			$scope.textQuery = $location.search().query;
			controllersFactory.createPaginationController($scope, {
				getData : function(page){
					return videoService.listVideosBySearchQuery($scope.textQuery, page); 
				}
			});	
		} ])
		
		.controller('searchController',[ '$scope', '$location', function($scope, $location) {
			$scope.query = '';
			$scope.find = function(){
				if($scope.query.trim() != ''){
					$location.path('/video/search').search({query: $scope.query});
					$scope.query = '';
				} else {
					$location.path('/main');
				}
			};
		} ])
		
		.controller('loginController', ['$rootScope', '$scope', '$location', 'authService', function($rootScope, $scope, $location, authService){
			$scope.credentials = {
					username :'',
					password:''
			};	
			$scope.loginFormSubmit = function (){
				 authService.login($scope.credentials, $scope.rememberMe).then(function(data){
					 $scope.error = false;
					 if ($rootScope.principal.role == "ADMIN") {
						 $location.path("/admin/account");
					 } else{
						 $location.path("/my-account/video");
					 }
				 }, function(){
					 $location.path("/login");
                     $scope.error = true;
				 });
			};
			$scope.logout = function(){
				authService.logout().then(function(){
					 $location.path("/main");
				});
			};
		}])
		
		.controller('recoveryAccessController', ['$scope', '$location', '$window', '$routeParams', 'recoveryService', function($scope, $location, $window, $routeParams, recoveryService){
			$scope.sendRestoreEmail = function(){
				recoveryService.sendRestoreEmail($scope.login).$promise.then(function(){
					$window.alert("We have sent you an email with instructions to restore your password.");
					$location.path("/main");
				}, function(){
					$scope.error = true;
				});
			}
			$scope.restorePassword = function(){
				recoveryService.restorePassword($routeParams.userId, $routeParams.hash, $scope.password).$promise.then(function(){
					$window.alert("Your password has been changed successfully.");
					$location.path("/login");
				}, function(){
					$scope.error = true;
				});
			}
		}]);
