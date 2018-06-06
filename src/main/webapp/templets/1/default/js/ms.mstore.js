define(function(require, exports, module) {
	var ms = require("ms");
	var ajaxCfg = {
		"type": "post",
		"dataType": "json",
	};
	var mstore = "http://mstore.mingsoft.net/";//
	return {
		"version": "1.0.0",
		people: {
			/**
			 * 用户分享列表
             ------
			 * @callmethod people.list(function(json){...})
			 * @param {{type:function,have:true}}  回调方法 返回值（json）
			 * @examples
             * ...
             * mmstore.people.list(function(json){
             *   alert(JSON.stringify(returnJson));
             * })
             * ...
             * @function
             * [{
             *   "upgraderVersionName":模板标题,
             *   "upgraderVersionDescription":模板描述,
             *   "upgraderVersionImg":模板图片,
             *   "upgraderVersionPeopleIcon":分享者头像,
             *   "upgraderVersionPeopleName":分享者昵称,
             *   "upgraderVersionUrl":帖子地址,
             *   "upgraderVersionZipUrl":下载地址,
             * }]
             * @return {{type:upgraderVersionName}} 模板标题
             * @return {{type:upgraderVersionDescription}} 模板描述
             * @return {{type:upgraderVersionImg}} 模板图片
             * @return {{type:upgraderVersionPeopleIcon}} 分享者头像
             * @return {{type:upgraderVersionPeopleName}} 分享者昵称
             * @return {{type:upgraderVersionUrl}} 帖子地址
             * @return {{type:upgraderVersionZipUrl}} 下载地址
			 */
			list: function(func) {
				ajaxCfg.url = "/people/upgrader/version/list.do";
				ms.ajax(ajaxCfg, func)
			},
			save: function(data, func) {
				ajaxCfg.url = "/people/upgrader/version/save.do";
				ajaxCfg.params = data;
				ms.ajax(ajaxCfg, func)
			},
			delete: function(id, func) {
				ajaxCfg.url = "/people/upgrader/version/delete.do";
				ms.ajax(ajaxCfg, func)
			},
			release: function(id, data, func) {
				if(validator.isNull(id)) {
					alert("id不能为空");
					return;
				}
				if(!validator.isLength(id, {
						max: 55
					})) {
					ms.alert("id最多为55位");
					return;
				}
				ajaxCfg.url = "/people/upgrader/version/" + id + "/release.do";
				ajaxCfg.params = data;
				ms.ajax(ajaxCfg, func)
			},
			edit: function(id, func) {
				if(validator.isNull(id)) {
					alert("id不能为空");
					return;
				}
				if(!validator.isLength(id, {
						max: 55
					})) {
					ms.alert("id最多为55位");
					return;
				}
				ajaxCfg.url = "/people/upgrader/version/" + id + "/edit.do";
				ms.ajax(ajaxCfg, func)
			},
			/**
             *用户详情
             ------
             * @callmethod people.data(function(json){...})
			 * @param {{type:function,have:true}}  回调方法 返回值（json）
			 * @examples
             * ...
             * mmstore.people.data(function(json){
             *   alert(JSON.stringify(returnJson));
             * })
             * ...
             * @function
             * [{
             *   "down":下载量,
             *   "share":分享数,
             *   "user":用户数,
             *   "pay":我的赞助,
             *   "project":众包收益,
             *   "income":分享收益,
             * }]
             * @return {{type:down}} 下载量
             * @return {{type:share}} 分享数
             * @return {{type:user}} 用户数
             * @return {{type:pay}} 我的赞助
             * @return {{type:project}} 众包收益
             * @return {{type:income}} 分享收益
             */
			data: function(func) {
				ajaxCfg.url = "/people/mstore/data.do";
				ms.ajax(ajaxCfg, func);
			},
			payList: function(func) {
				ajaxCfg.url = "/people/upgrader/upgraderPeopleVersion/payList.do";
				ms.ajax(ajaxCfg, func);
			},
			downList: function(func) {
				ajaxCfg.url = "/people/upgrader/upgraderPeopleVersion/downList.do";
				ms.ajax(ajaxCfg, func);
			},
			userList: function(func) {
				ajaxCfg.url = "/people/upgrader/upgraderPeopleVersion/userList.do";
				ms.ajax(ajaxCfg, func);
			},
			/**
		    * 获取相应的插件信息
		    ------
			* @param {{type:string,have:true}} id 插件id
			* @examples
			* ...
			* mmstore.templates.detail(function(json){
            *   alert(JSON.stringify(returnJson));
            * })
			* ...
			* @return {{type:upgraderVersionDescription}} 版本描述
			* @return {{type:upgraderVersionTime}} 版本信息更新时间
			* @return {{type:upgraderVersionName}} 版本名称
			* @return {{type:upgraderVersionPrice}} 版本价格
			* @return {{type:upgraderVersionImg}} 缩略图
			* @return {{type:upgraderVersionDownload}} 模板下载量
			* @return {{type:upgraderVersionStart}} 模板星级
			* @return {{type:upgraderVersionMaven}} maven依赖
			* @return {{type:upgraderVersionReadme}} 使用信息
		   */
			detail: function(data, func) {
				if(validator.isNull(data)) {
					alert("id不能为空");
					return;
				}
				if(!validator.isLength(data, {
						max: 55
					})) {
					ms.alert("id最多为55位");
					return;
				}
				ajaxCfg.url = "http://localhost:8080/" + "/people/mstore/detail.do";
				ajaxCfg.params = data;
				ms.ajax(ajaxCfg, func);
			},
			/**
		    * 支付购买模版插件
		    ------
			* @param {{type:string,have:true}} id  模版插件编号
			* @examples
			* ...
			* mmstore.templates.pay(function(json){
            *   alert(JSON.stringify(returnJson));
            * })
			* ...
			* @return {{type:data}} 支付链接
		   */
			pay: function(id, func) {
				if(validator.isNull(id)) {
					alert("id不能为空");
					return;
				}
				if(!validator.isLength(id, {
						max: 55
					})) {
					ms.alert("id最多为55位");
					return;
				}
				ajaxCfg.url = mstore + "/people/mstore/" + id + "/pay.do";
				ms.ajax(ajaxCfg, func);
			},
			/**
		    * 下载资源
		    ------
		    * @callmethod mmstore.templates.down(id, function(json) {...})
			* @param {{type:string,have:true}} id 模版插件编号
			* @examples
			* ...
			* mmstore.templates.down(function(json){
            *   alert(JSON.stringify(returnJson));
            * })
			* ...
			* @return {{type:data}} 如果用户下载的资源是需要收费的，但是用户用非法手段请求，就返回失败，如果用户第一次下载 就新增一条纪录，否则就直接更新历史数据，业务合法正常弹出下载浏览器界面
		   */
			down: function(data, func) {
				if(validator.isNull(data)) {
					alert("id不能为空");
					return;
				}
				if(!validator.isLength(data, {
						max: 55
					})) {
					ms.alert("id最多为55位");
					return;
				}
				ajaxCfg.url = mstore + "/people/upgrader/upgraderPeopleVersion/down.do";
				ajaxCfg.params = data;
				ms.ajax(ajaxCfg, func);
			},
		},
		mstore: {
			/**
			  * 赞助者列表
			  ------
			  * @callmethod people.topSponsor(function(json){...})
			  * @param {{type:function,have:true}}  回调方法 返回值（json）
			  * @examples
			  * ...
			  * mmstore.people.topSponsor(function(json){
			  *   alert(JSON.stringify(returnJson));
			  * })
			  * ...
			  * @function
			  * [{
			  *   "upgraderOrderPeopleIcon":赞助者头像,
			  *   "upgraderOrderPeopleNickName":赞助者昵称,
			  * }]
			  * @return {{type:upgraderOrderPeopleIcon}} 赞助者头像
			  * @return {{type:upgraderOrderPeopleNickName}} 赞助者昵称
			  */
			topSponsor: function(func) {
				ajaxCfg.url = mstore + "/mstore/topSponsor.do";
				ms.ajax(ajaxCfg, func);
			},
			/**
			*分享者列表
			------
			* @callmethod people.topShareUser(function(json){...})
			* @param {{type:function,have:true}}  回调方法 返回值（json）
			* @examples
			* ...
			* mmstore.people.topShareUser(function(json){
			*   alert(JSON.stringify(returnJson));
			* })
			* ...
			* @function
			* [{
			*   "upgraderVersionPeopleIcon":分享者头像,
			*   "upgraderVersionPeopleName":分享者昵称,
			* }]
			* @return {{type:upgraderVersionPeopleIcon}} 分享者头像
			* @return {{type:upgraderVersionPeopleName}} 分享者昵称
			*/
			topShareUser: function(func) {
				ajaxCfg.url = mstore + "/mstore/topShareUser.do";
				ms.ajax(ajaxCfg, func);
			},
			topDown: function(func) {
				ajaxCfg.url = mstore + "/mstore/topDown.do";
				ms.ajax(ajaxCfg, func);
			},
			/**
			  * 模板列表
			  ------
			  * @callmethod templates.list(data,function()...)}
              * @param {{type:int,have:true}}  upgraderVersionType  列表分类编号
              * @param {{type:int,have:true}}  upgraderVersionIndustry  模板分类编号
              * @param {{type:int,have:true}}  upgraderVersionColor  颜色分类编号
              * @param {{type:int,have:true}}  orderBy  排序类型
              * @param {{type:function,have:true}}  回调方法 返回值（json）
              * @examples
              * ...
              * mmstore.templates.list(function(json){
              *   alert(JSON.stringify(returnJson));
              * })
              * ...
              * @function
              * [{
              *   "upgraderVersionName":模板标题,
              *   "upgraderVersionDescription":模板描述,
              *   "upgraderVersionImg":模板图片,
              *   "upgraderVersionPeopleIcon":分享者头像,
              *   "upgraderVersionPeopleName":分享者昵称,
              *   "upgraderVersionUrl":帖子地址,
              * }]
              * @return {{type:upgraderVersionName}} 模板标题
              * @return {{type:upgraderVersionDescription}} 模板描述
              * @return {{type:upgraderVersionImg}} 模板图片
              * @return {{type:upgraderVersionPeopleIcon}} 分享者头像
              * @return {{type:upgraderVersionPeopleName}} 分享者昵称
              * @return {{type:upgraderVersionUrl}} 帖子地址
			  */
			list: function(data, func) {
				if(validator.isNull(data)) {
					return;
				}
				ajaxCfg.url = mstore + "/mstore/list.do";
				ajaxCfg.params = data;
				ms.ajax(ajaxCfg, func);
			},
			/**
			* 模板数量
			------
			* @callmethod templates.number(data,function()...)}
			* @param {{type:function,have:true}}  回调方法 返回值（json）
			* @examples
			* ...
			* mmstore.templates.number(function(json){
			*   alert(JSON.stringify(returnJson));
			* })
			* ...
			* @function
			* [{
			*   "all":模板总数,
			*   "mouth":本月上新模板数量,
			*   "week":本周上新模板数量,
			* }]
			* @return {{type:all}} 模板总数
			* @return {{type:mouth}} 本月上新模板数量
			* @return {{type:week}} 本周上新模板数量
			*/
			count: function(func) {
				ajaxCfg.url = mstore + "/mstore/count.do";
				ms.ajax(ajaxCfg, func);
			},
			/**
		    * 获取论坛图片
		    ------
		    * @callmethod mmstore.templates.modelImg(data, function(json){...});
			* @param {{type:string,have:true}} subjectBasicId  基本ID
			* @examples
			* ...
			* mmstore.templates.modelImg(function(json){
            *   alert(JSON.stringify(returnJson));
            * })
			* ...
			* @return {{type:data}} 图片链接
		   */
			img: function(data, func) {
				if(validator.isNull(data)) {
					alert("模板id不能为空");
					return;
				}
				if(!validator.isLength(data, {
						max: 55
					})) {
					ms.alert("id最多为55位");
					return;
				}
				ajaxCfg.url = mstore + "mstore/img.do";
				ajaxCfg.params = data;
				ms.ajax(ajaxCfg, func);
			},
		},
	}
})
