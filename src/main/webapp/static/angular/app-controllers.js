angular.module('app-controllers', ['ngRoute'])
		.config(function($routeProvider) {
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
				controller : 'uploadVideoController'
			});
			$routeProvider.when('/my-account/video', {
				templateUrl : 'static/html/page/myaccount.html',
				controller : 'allListVideoMyAccountController'
			});
			$routeProvider.when('/my-account/video/:videoId', {
				templateUrl : 'static/html/page/editvideo.html',
				controller : 'editVideoController'
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
			$routeProvider.otherwise({
				redirectTo : '/main'
			});
		})
		
		.factory('controllersFactory',function(){
			return{
				createPaginationController : function(scope, service){
					scope.records = service.getData(0);
					scope.loading = false;
					scope.showMore = function (){
						scope.loading = true;
						var nextPage = scope.records.page.number + 1;
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
		
		.controller('allListVideoMyAccountController',[ '$scope', '$window', 'videoService', 'userService', 'controllersFactory', function($scope, $window, videoService, userService, controllersFactory) {
			controllersFactory.createPaginationController($scope, {getData : videoService.listAllVideosMyAccountByPage});
			$scope.deleteVideo = function(idx){
				var video = $scope.records.content[idx];
				userService.deleteVideo(video.id).$promise.then(function(resp){
					$window.alert("File delete SUCCESS");
					$scope.records.content.splice(idx,1);
				}, function(resp){
					$window.alert("File delete FAILED");
				});
			};
		} ])
		
		.controller('uploadVideoController', ['$scope', '$window', '$location', 'userService', function ($scope, $window, $location, userService) {
			$scope.submit = function(){
				userService.uploadVideo($scope.title, $scope.description, $scope.file).then(function(resp){
					$scope.clearData();
					$window.alert("File upload SUCCESS");
					$location.path('/my-account/video');
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
		
		.controller('editVideoController',[ '$scope', '$window', '$location', '$routeParams', 'videoService', 'userService', 'controllersFactory', function($scope, $window, $location, $routeParams, videoService, userService, controllersFactory) {
			videoService.findVideoById($routeParams.videoId).$promise.then(function(data){
				$scope.currentVideo = data;
				$scope.title = data.title;
				$scope.description = data.description;
			});
			$scope.saveVideo = function(){
				userService.saveVideo($routeParams.videoId, $scope.title, $scope.description).$promise.then(function(resp){
					$window.alert("File save SUCCESS");
					$location.path('/my-account/video');
				}, function(resp){
					$window.alert("File save FAILED");
				});
			};
		} ])

		.controller('searchResultController',[ '$scope', '$location', 'videoService', 'controllersFactory', function($scope, $location, videoService, controllersFactory) {
			$scope.textQuery = $location.search().query;
			controllersFactory.createPaginationController($scope, {
				getData : function(page){
					return videoService.listBySearchQuery($scope.textQuery, page); 
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
		}])
		
		.controller('accountController', ['$scope', 'adminService', 'controllersFactory', function($scope, adminService, controllersFactory){
			controllersFactory.createPaginationController($scope, {getData : adminService.listAllUsersByPage});
			var maxInt = 2147483647;
			$scope.allCompanies = adminService.listAllCompaniesByPage(0,maxInt);
			$scope.clearData = function(){
				$scope.name = '';
				$scope.surname = '';
				$scope.email = '';
				$scope.login = '';
				$scope.password = '';
				$scope.company = '';
				$scope.role = '';
				$scope.avatarUrl = 'http://www.gravatar.com/avatar/00000000000000000000000000000000?d=mm';
				$scope.avatarPreview = $scope.avatarUrl + '&s=300';
				$scope.currentAccountId = -1;			
			};
			$scope.getAvatarUrl = function(){
				adminService.getAvatarUrl($scope.email).$promise.then(function(data){
					$scope.avatarUrl = data.url;
					$scope.avatarPreview = $scope.avatarUrl + '&s=300';
				});
			}
			$scope.saveUser = function(){
				var id;
				var companyIndex = $scope.allCompanies.content.findIndex(function(item){
					return item.id == $scope.company;
				});
				var company = $scope.allCompanies.content[companyIndex];
				if ($scope.currentAccountId > -1) {
					id = $scope.records.content[$scope.currentAccountId].id;
				} 
				var user = {
							'id' : id,
							'name': $scope.name,
							'surname': $scope.surname,
							'login': $scope.login,
							'password': $scope.password,
							'email': $scope.email,
							'company': {
								'id' : company.id,
								'name': company.name,
								'address': company.address,
								'email': company.email,
								'phone': company.phone,
							},
							'role': $scope.role,
							'avatarUrl': $scope.avatarUrl
						}
				adminService.saveUser(user).$promise.then(function(resp){
					if ($scope.currentAccountId > -1){
						$scope.records.content[$scope.currentAccountId] = resp;
					} else {
						$scope.records.content.push(resp);
					}
					$scope.clearData();
				})
			}
			$scope.editUser = function(idx){
				$scope.currentAccountId = idx;
				var user = $scope.records.content[idx];
				$scope.name = user.name;
				$scope.surname = user.surname;
				$scope.email = user.email;
				$scope.login = user.login;
				$scope.password = user.password;
				$scope.company = user.company.id;
				$scope.role = user.role;
				$scope.avatarUrl = user.avatarUrl;
				$scope.avatarPreview = $scope.avatarUrl + '&s=300';
			}
			$scope.deleteUser = function(idx){
				var user = $scope.records.content[idx];
				adminService.deleteUser(user.id).$promise.then(function(resp){
					$scope.records.content.splice(idx,1);					
				});
			}
			$scope.clearData();
		}])

		.controller('companyController', ['$scope', 'adminService', 'controllersFactory', function($scope, adminService, controllersFactory){
			controllersFactory.createPaginationController($scope, {
				getData : function(page){
					return adminService.listAllCompaniesByPage(page, 10);
				}
			});
			$scope.clearData = function(){
				$scope.name = '';
				$scope.address = '';
				$scope.email = '';
				$scope.phone = '';
				$scope.currentCompanyId = -1;			
			};
			$scope.saveCompany = function(){
				var id;
				if ($scope.currentCompanyId > -1) {
					id = $scope.records.content[$scope.currentCompanyId].id;
				}
				var company = {
						'id' : id,
						'name': $scope.name,
						'address': $scope.address,
						'email': $scope.email,
						'phone': $scope.phone
				}
				adminService.saveCompany(company).$promise.then(function(resp){
					if ($scope.currentCompanyId > -1) {
						$scope.records.content[$scope.currentCompanyId] = resp;
					} else {
						$scope.records.content.push(resp);
					}
					$scope.clearData();
				})
			}
			$scope.editCompany = function(idx){
				$scope.currentCompanyId = idx;
				var company = $scope.records.content[idx];
				$scope.name = company.name;
				$scope.address = company.address;
				$scope.email = company.email;
				$scope.phone = company.phone;
			}
			$scope.deleteCompany = function(idx){
				var company = $scope.records.content[idx];
				adminService.deleteCompany(company.id).$promise.then(function(resp){
					$scope.records.content.splice(idx,1);					
				});
			}
			$scope.clearData();
		}])
		.controller('statisticsController', ['$scope', 'adminService', function($scope, adminService){
			$scope.videoStatisticsList = adminService.listVideoStatistics();
		}]);
