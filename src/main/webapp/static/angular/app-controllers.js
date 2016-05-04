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
			$routeProvider.when('/access-denied', {
				templateUrl : 'static/html/page/access-denied.html',
				controller : 'accessDeniedController'
			});
			$routeProvider.when('/admin/account', {
				templateUrl : 'static/html/page/account.html',
				controller : 'accountController'
			});
			$routeProvider.when('/admin/company', {
				templateUrl : 'static/html/page/company.html',
				controller : 'companyController'
			});
			$routeProvider.when('/admin/account/:userId', {
				templateUrl : 'static/html/page/editaccount.html',
				controller : 'accountController'
			});
			$routeProvider.when('/admin/company/:companyId', {
				templateUrl : 'static/html/page/editcompany.html',
				controller : 'companyController'
			});
			$routeProvider.otherwise({
				redirectTo : '/main'
			});
			$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
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
				userService.deleteVideo(video.id).then(function(resp){
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
				userService.saveVideo($routeParams.videoId, $scope.title, $scope.description).then(function(resp){
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
		
		.controller('loginController', ['$scope', '$location', 'authService', function($scope, $location, authService){
			$scope.credentials = {
					username :'',
					password:''
			};
			$scope.rememberMe = false;
			var authenticate = function(callback) {
				authService.getPrincipal().$promise.then(function(data){
	                if (data.name) {
	                    $scope.authenticated = true;
	                    if (data.authorities[0].authority == "ADMIN") {
		                    $scope.roleAdmin = true;
						} else {
		                    $scope.roleAdmin = false;							
						}
	                } else {
	                    $scope.authenticated = false;
	                    $scope.roleAdmin = false;	
	                }
	                callback && callback();
				}, function(errResponse){
					$scope.authenticated = false;
					$scope.roleAdmin = false;	
					callback && callback();
				});
			};
			authenticate();			
			$scope.loginFormSubmit = function (){
				 var data = 'username=' + encodeURIComponent($scope.credentials.username) + '&password=' + encodeURIComponent($scope.credentials.password) + '&remember-me=' + encodeURIComponent($scope.rememberMe);
				 authService.login(data).$promise.then(function(){
					 authenticate(function() {
		                    if ($scope.authenticated) {
		                        $scope.error = false;
		                        if ($scope.roleAdmin) {
		                        	$location.path("/admin/account");
								} else{
			                        $location.path("/my-account/video");
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
				 });
			};
			$scope.logout = function(){
				authService.logout().$promise.then(function(){
					$scope.authenticated = false;
				    $location.path("/main");
				}, function(){
					$scope.authenticated = false;
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

		.controller('accountController', ['$scope', 'adminService', 'controllersFactory', function($scope, adminService, controllersFactory){
			controllersFactory.createPaginationController($scope, {getData : adminService.listAllUsersByPage});
			$scope.allCompanies = adminService.listAllCompanies();
			$scope.clearData = function(){
				$scope.name = '';
				$scope.surname = '';
				$scope.email = '';
				$scope.login = '';
				$scope.password = '';
				$scope.company = '';
				$scope.role = '';
				$scope.avatarFile = null;
				$scope.currentAccountId = -1;			
			};
			$scope.addNewUser = function(){
				var avatar;
				if ($scope.avatarFile != null) {
					avatar = $scope.avatarFile;
				} 
				adminService.addUser($scope.name, $scope.surname, $scope.login, $scope.password, $scope.email, $scope.company, $scope.role, avatar).then(function(resp){
					$scope.records.content.push({
						id: resp.data.id,
						name: resp.data.name,
						surname: resp.data.surname,
						login: resp.data.login,
						password: resp.data.password,
						email: resp.data.email,
						company: resp.data.company,
						role: resp.data.role,
						avatarUrl: resp.data.avatarUrl
					});
					$scope.clearData();
				})
			};
			$scope.saveUser = function(){
				if ($scope.currentAccountId > -1) {
					var id = $scope.currentAccountId;
					adminService.saveUser($scope.records.content[id].id, $scope.name, $scope.surname, $scope.email, $scope.company).then(function(resp){
						$scope.records.content[id].name = resp.data.name;
						$scope.records.content[id].surname = resp.data.surname;
						$scope.records.content[id].email = resp.data.email;
						$scope.records.content[id].company = resp.data.company;
						$scope.clearData();
					})
				}
			}
			$scope.editUser = function(idx){
				$scope.currentAccountId = idx;
				var user = $scope.records.content[idx];
				$scope.name = user.name;
				$scope.surname = user.surname;
				$scope.email = user.email;
				$scope.login = user.login;
				$scope.login = '';
				$scope.company = user.company.id;
				$scope.role = user.role;
				$scope.avatarFile = user.avatarUrl;
			}
			$scope.deleteUser = function(idx){
				var user = $scope.records.content[idx];
				adminService.deleteUser(user.id).then(function(resp){
					$scope.records.content.splice(idx,1);					
				});
			}
			$scope.clearData();
		}])

		.controller('companyController', ['$scope', 'adminService', 'controllersFactory', function($scope, adminService, controllersFactory){
			controllersFactory.createPaginationController($scope, {getData : adminService.listAllCompaniesByPage});
			$scope.clearData = function(){
				$scope.name = '';
				$scope.address = '';
				$scope.email = '';
				$scope.phone = '';
				$scope.currentCompanyId = -1;			
			};
			$scope.addNewCompany = function(){
				adminService.addCompany($scope.name, $scope.address, $scope.email, $scope.phone).then(function(resp){
					$scope.records.content.push({
						id: resp.data.id,
						name: resp.data.name,
						address: resp.data.address,
						email: resp.data.email,
						phone: resp.data.phone
					});
					$scope.clearData();
				})
			}
			$scope.saveCompany = function(){
				if ($scope.currentCompanyId > -1) {
					var id = $scope.currentCompanyId;
					adminService.saveCompany($scope.records.content[id].id, $scope.address, $scope.email, $scope.phone).then(function(resp){
						$scope.records.content[id].address = resp.data.address;
						$scope.records.content[id].email = resp.data.email;
						$scope.records.content[id].phone = resp.data.phone;
						$scope.clearData();
					})
				}
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
				adminService.deleteCompany(company.id).then(function(resp){
					$scope.records.content.splice(idx,1);					
				});
			}
			$scope.clearData();
		}]);
