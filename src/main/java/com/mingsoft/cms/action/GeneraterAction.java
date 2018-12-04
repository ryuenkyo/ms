/**
The MIT License (MIT) * Copyright (c) 2016 铭飞科技(mingsoft.net)

 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mingsoft.cms.action;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.mingsoft.base.constant.Const;
import com.mingsoft.basic.action.BaseAction;
import com.mingsoft.basic.biz.IAppBiz;
import com.mingsoft.basic.biz.IColumnBiz;
import com.mingsoft.basic.biz.IModelBiz;
import com.mingsoft.basic.entity.AppEntity;
import com.mingsoft.basic.entity.CategoryEntity;
import com.mingsoft.basic.entity.ColumnEntity;
import com.mingsoft.cms.biz.IArticleBiz;
import com.mingsoft.cms.constant.ModelCode;
import com.mingsoft.cms.constant.e.ColumnTypeEnum;
import com.mingsoft.cms.entity.ArticleEntity;
import com.mingsoft.util.StringUtil;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.mdiy.parser.TagParser;

/**
 * 
 * @ClassName:  GeneraterAction   
 * @Description:TODO 生成器
 * @author: 铭飞开发团队
 * @date:   2018年1月31日 下午2:52:07   
 *     
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
@Controller("cmsGenerater")
@RequestMapping("/${managerPath}/cms/generate")
@Scope("request")
public class GeneraterAction extends BaseAction {
	/**
	 * 存放模版的文件夹
	 */
	final String TEMPLATES="templets";
	/**
	 * 静态文件生成路径;例如：mcms/html/1
	 */
	final String HTML="html";
	/**
	 * 移动端生成的目录
	 */
	final String MOBILE="m";
	/**
	 * index
	 */
	final String INDEX="index";
	/**
	 * 文件夹路径名;例如：1/58/71.html
	 */
	final String HTML_SUFFIX = ".html";
	/**
	 * 生成的静态列表页面名;例如：list1.html
	 */
	final String PAGE_LIST = "list";
	/**
	 * 模版文件后缀名;例如：index.html
	 */
	final String HTM_SUFFIX = ".htm";
	/**
	 * 文章管理业务层
	 */
	@Autowired
	private IArticleBiz articleBiz;
	
	/**
	 * 栏目管理业务层
	 */
	@Autowired
	private IColumnBiz columnBiz;

	/**
	 * 站点管理业务层
	 */
	@Autowired
	private IAppBiz appBiz;
	
	/**
	 * 模块管理业务层
	 */
	@Autowired
	private IModelBiz modelBiz;
	
	@Value("${managerPath}")
	private String managerPath;

	/**
	 * 一键更新所有
	 * 
	 * @return
	 */
	@RequestMapping("/all")
	public String all() {
		return view("/cms/generate/generate_all");
	}
	

	/**
	 * 更新主页
	 * 
	 * @return
	 */
	@RequestMapping("/index")
	public String index(HttpServletRequest request,ModelMap model) {
		// 该站点ID有session提供
		int websiteId =  BasicUtil.getAppId();
		Integer modelId = modelBiz.getEntityByModelCode(ModelCode.CMS_COLUMN).getModelId(); // 查询当前模块编号
		//获取所有的内容管理栏目
		List<ColumnEntity> list  = columnBiz.queryAll(websiteId,modelId);
		model.addAttribute("list",  JSONArray.toJSONString(list));
		model.addAttribute("now", new Date());
		return view("/cms/generate/index");
	}

	/**
	 * 生成主页
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/generateIndex")
	@RequiresPermissions("cms:generate:index")
	@ResponseBody
	public void generateIndex(HttpServletRequest request, HttpServletResponse response) {
		String tmpFileName = request.getParameter("url"); // 模版文件名称
		String generateFileName = request.getParameter("position");// 生成后的文件名称

		// 获取站点信息
		AppEntity app = BasicUtil.getApp();
		// 根据站点id组装站点信息路径　格式：templets／站点ID/模版风格
		String webSiteTmpPath = getRealPath(request, TEMPLATES) + File.separator + app.getAppId() + File.separator + app.getAppStyle();
		// 模版路径加上(用户选择的主页的模版的路径)default/index.html
		String tmpFilePath = webSiteTmpPath + File.separator + tmpFileName;
		//要生成html地址的文件夹
		String filePath = getRealPath(request, HTML) + File.separator + app.getAppId() + File.separator;
		// 生成地址
		String generatePath = filePath + generateFileName;
		String generateMobilePath = filePath + MOBILE + File.separator + generateFileName;
		//生成保存htm页面的文件夹
		FileUtil.mkdir(filePath + File.separator + MOBILE); // 手机端
		// 获取文件所在路径 首先判断用户输入的模版文件是否存在
		File file = new File(tmpFilePath);
		// 判断文件是否存在，若不存在弹出返回信息
		if (!file.exists()) {
			this.outJson(response, false,"模板不存在");
		} else {
			// 当前模版的物理路径
				try {
					Map map = new HashMap();
					//1、设置模板文件夹路径
					FileTemplateLoader ft = new FileTemplateLoader(new File(webSiteTmpPath));
					Configuration cfg = new Configuration();
					cfg.setTemplateLoader(ft);
					try {
						//2、读取模板文件
						Template template = cfg.getTemplate(tmpFileName, Const.UTF8);
						//pc端内容
						StringWriter writer = new StringWriter();
						try {
							template.process(null, writer);
							TagParser tag = new TagParser(writer.toString());
							String content = tag.rendering(map);
							//LOG.debug(tag.getContent());
							//3、将tag.getContent()写入路径
							FileUtil.writeString(content, generatePath, Const.UTF8);
							if(ObjectUtil.isNotNull(app.getAppMobileStyle())){
								//手机端m
								writer = new StringWriter();
								template = cfg.getTemplate(app.getAppMobileStyle() + File.separator +tmpFileName, Const.UTF8);
								template.process(null, writer);
								map.put(MOBILE, app.getAppMobileStyle());
								tag = new TagParser(writer.toString(),map);
								content = tag.rendering(map);
								FileUtil.writeString(content, generateMobilePath, Const.UTF8);
							}
							this.outJson(response, true);
						} catch (TemplateException e) {
							e.printStackTrace();
							this.outJson(response, false);
						}
					} catch (IOException e) {
						e.printStackTrace();
						this.outJson(response, false);
					}
				} catch (IOException e) {
					e.printStackTrace();
					this.outJson(response, false);
				}
		} 
	}

	/**
	 * 生成列表的静态页面
	 * 
	 * @param request
	 * @param response
	 * @param columnId
	 */
	@RequestMapping("/{columnId}/genernateColumn")
	@RequiresPermissions("cms:generate:column")
	@ResponseBody
	public void genernateColumn(HttpServletRequest request, HttpServletResponse response, @PathVariable int columnId) {
		// 获取站点id
		AppEntity app = BasicUtil.getApp();
		String mobileStyle = app.getAppMobileStyle(); // 手机端模版
		String url = app.getAppHostUrl() + File.separator + HTML + File.separator + app.getAppId();
		// 站点生成后保存的html地址
		String generatePath = getRealPath(request, HTML) + File.separator + app.getAppId() + File.separator;
		FileUtil.mkdir(generatePath);
		// 网站风格物理路径
		String tmpPath = getRealPath(request, TEMPLATES) + File.separator + app.getAppId() + File.separator + app.getAppStyle();
		List<ColumnEntity> columns = new ArrayList<ColumnEntity>();
		// 如果栏目id小于0则更新所有的栏目，否则只更新选中的栏目
		int modelId = BasicUtil.getModelCodeId(ModelCode.CMS_COLUMN); // 查询当前模块编号
		if (columnId > 0) {
			List<CategoryEntity> categorys = columnBiz.queryChildrenCategory(columnId, app.getAppId(),modelId);
			for (CategoryEntity c : categorys) {
				columns.add((ColumnEntity) columnBiz.getEntity(c.getCategoryId()));
			}
		} else {
			
			//获取所有的内容管理栏目
			columns = columnBiz.queryAll(app.getAppId(),modelId);
		}
		FileTemplateLoader ft;
		try {
			//1、设置模板文件夹路径
			ft= new FileTemplateLoader(new File(tmpPath));
			Configuration cfg = new Configuration();
			cfg.setTemplateLoader(ft);
			// 获取栏目列表模版
			for (ColumnEntity column : columns) {
				String columnPath = null;// pc端
				String mobilePath = null;// 手机端
				//判断模板文件是否存在
				if(!FileUtil.exist(tmpPath + File.separator + column.getColumnUrl())){
					continue;
				}
				// 生成列表保存路径
				FileUtil.mkdir(generatePath + column.getColumnPath());
				// 判断是否为顶级栏目，进行栏目路径的组合
				if (column.getCategoryCategoryId() == 0) {
					FileUtil.mkdir(generatePath + column.getCategoryId());
					columnPath = generatePath + File.separator + column.getCategoryId();
					if (!StringUtil.isBlank(mobileStyle)) {
						FileUtil.mkdir(generatePath + mobileStyle + File.separator + column.getCategoryId());
						mobilePath = generatePath + mobileStyle + File.separator + column.getCategoryId();
					}
				} else {
					if (!StringUtil.isBlank(mobileStyle)) {
						mobilePath = generatePath + mobileStyle + File.separator + column.getColumnPath();
						FileUtil.mkdir(mobilePath);
					}
					columnPath = generatePath + column.getColumnPath();
				}
				Map map = new HashMap();
				//2、读取模板文件
				Template template = cfg.getTemplate(column.getColumnListUrl(), Const.UTF8);
				StringWriter writer = new StringWriter();
				// 判断列表类型
				switch (column.getColumnType()) {
				case ColumnEntity.COLUMN_TYPE_LIST: // 列表
					// 手机列表模版
					if (!StringUtil.isBlank(mobileStyle)) {
						FileUtil.mkdir(mobilePath);
						String mobileListTtmpContent = FileUtil.readUtf8String(tmpPath + File.separator + mobileStyle + File.separator + column.getColumnListUrl());
						// 如果模版不为空就进行标签替换
						if (!StringUtil.isBlank(mobileListTtmpContent)) {
							// 生成手机端模版
							// 要生成手机的静态页面数
							int mobilePageSize = 10;//cmsParser.getPageSize(app, mobileListTtmpContent, column);
							// 根据页面数,循环生成静态页面个数在
							for (int i = 0; i < mobilePageSize; i++) {
								String writePath = mobilePath + File.separator + PAGE_LIST + (i + 1) + HTML_SUFFIX;
								if (i == 0) {
									writePath = mobilePath + File.separator + INDEX + HTML_SUFFIX;
								}
								String pagePath = url + File.separator + mobileStyle + File.separator + column.getColumnPath() + File.separator + PAGE_LIST ;
							}
						}
	
					}
					try {
						Map parserParams = new HashMap();
						parserParams.put("typeid", column.getCategoryId());
						template.process(null, writer);
						TagParser tag = new TagParser(writer.toString(),parserParams);
						// 读取列表模版地址
						String listTtmpContent = tag.rendering();
						// 要生成的静态页面数
						// 根据页面数,循环生成静态页面个数在
						//3、将tag.getContent()写入路径
						FileUtil.writeString(listTtmpContent, columnPath + File.separator + INDEX + HTML_SUFFIX, Const.UTF8);
						this.outJson(response, true);
					} catch (TemplateException e) {
						e.printStackTrace();
						this.outJson(response, false);
					}
					break;
				case ColumnEntity.COLUMN_TYPE_COVER:// 单页
					// 取该栏目的最后一篇新闻作为显示内容
					List<ArticleEntity> list = articleBiz.queryListByColumnId(column.getCategoryId());
					ArticleEntity article = list.get(0);// 取一篇文章作为封面栏目的内容
					//手机端路径
					String mobileWritePath = "";
					String writePath = "";
					try {
						//2、读取单页模板文件
						template = cfg.getTemplate(column.getColumnUrl(), Const.UTF8);
						writer = new StringWriter();
						Map parserParams = new HashMap();
						parserParams.put("id", article.getBasicId());
						template.process(null, writer);
						TagParser tag = new TagParser(writer.toString(),parserParams);
						// 读取列表模版地址
						String content = tag.rendering();
						//3、将tag.getContent()写入路径
						// 判断是否为顶级栏目
						if (column.getCategoryCategoryId() == 0) {
							FileUtil.mkdir(generatePath + column.getCategoryId());
							writePath = generatePath + String.valueOf(column.getCategoryId()) + File.separator + INDEX + HTML_SUFFIX;
							mobileWritePath = generatePath + mobileStyle + File.separator + String.valueOf(column.getCategoryId()) + File.separator + INDEX + HTML_SUFFIX;
						} else {// 子栏目，子栏目需要获取父级栏目的编号
							writePath = generatePath + column.getColumnPath() + File.separator + INDEX + HTML_SUFFIX;
							mobileWritePath = generatePath + mobileStyle + File.separator + column.getColumnPath() + File.separator + INDEX + HTML_SUFFIX;
						}
						FileUtil.writeString(content, writePath, Const.UTF8);
						// 手机端
						if (!StringUtil.isBlank(mobileStyle)) {
							//2、读取单页模板文件
							template = cfg.getTemplate(mobileStyle + File.separator + column.getColumnUrl(), Const.UTF8);
							writer = new StringWriter();
							template.process(null, writer);
							Map mobileParserParams = new HashMap();
							mobileParserParams.put("id", article.getBasicId());
							//设置手机端style
							mobileParserParams.put(MOBILE, mobileStyle);
							TagParser mobileTag = new TagParser(writer.toString(),mobileParserParams);
							// 读取列表模版地址
							String mobileContent = mobileTag.rendering();
							FileUtil.writeString(mobileContent, mobileWritePath, Const.UTF8);
						}
						this.outJson(response, true);
						break;
					} catch (TemplateException e) {
						e.printStackTrace();
						this.outJson(response, false);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.outJson(response, false);
		}
	}

	/**
	 * 更新文档
	 * 
	 * @return
	 */
	@RequestMapping("/article")
	public String article(HttpServletRequest request,ModelMap model) {
		
		// 获取站点ID和模块ID
		int websiteId =  BasicUtil.getAppId();
		Integer modelId = modelBiz.getEntityByModelCode(ModelCode.CMS_COLUMN).getModelId();
		
		//获取所有的内容管理栏目
		List<ColumnEntity> list  = columnBiz.queryAll(websiteId,modelId);
		
		model.addAttribute("now", new Date());
		model.addAttribute("list",  JSONArray.toJSONString(list));
		return view("/cms/generate/generate_article");
	}

	/**
	 * 更新商品
	 * 
	 * @return
	 */
	@RequestMapping("/product")
	public String product(HttpServletRequest request) {
		request.setAttribute("now", new Date());
		return view("/cms/generate/generate_product");
	}

	/**
	 * 根据栏目id更新所有的文章
	 * 
	 * @param request
	 * @param response
	 * @param columnId
	 */
	@RequestMapping("/{columnId}/generateArticle")
	@RequiresPermissions("cms:generate:article")
	@ResponseBody
	public void generateArticle(HttpServletRequest request, HttpServletResponse response, @PathVariable int columnId) {
		String dateTime = request.getParameter("dateTime");
		AppEntity app = BasicUtil.getApp();
		String mobileStyle = null;
		if (app != null) {
			mobileStyle = app.getAppMobileStyle(); // 手机端模版
		}
		// 站点生成后保存的html地址
		String generatePath = getRealPath(request, HTML) + File.separator + app.getAppId() + File.separator;
		// 网站风格物理路径
		String tmpPath = getRealPath(request, TEMPLATES) + File.separator + app.getAppId() + File.separator + app.getAppStyle(); 
		List<Integer> articleList = null;
		List<ColumnEntity> columns = new ArrayList<ColumnEntity>();
		Integer modelId = modelBiz.getEntityByModelCode(ModelCode.CMS_COLUMN).getModelId(); // 查询当前模块编号
		
		if (columnId > 0) {
			List<CategoryEntity> categorys = columnBiz.queryChildrenCategory(columnId, app.getAppId(),modelId);
			for (CategoryEntity c : categorys) {
				columns.add((ColumnEntity) columnBiz.getEntity(c.getCategoryId()));
			}
		} else {
			columns = columnBiz.queryColumnListByWebsiteId(app.getAppId()); // 读取所有栏目
		}
		// 文章地址前缀
		String url = app.getAppHostUrl() + File.separator + HTML + File.separator + app.getAppId() + File.separator; 
		//1、设置模板文件夹路径
		FileTemplateLoader ft;
		try {
			ft = new FileTemplateLoader(new File(tmpPath));
			Configuration cfg = new Configuration();
			cfg.setTemplateLoader(ft);
			// 如果没有选择栏目，生成规则
			// 1先读取所有的栏目,从最低级的分类取
			for (ColumnEntity tempColumn : columns) {// 循环分类
				//模板文件路径
				String columnPath = tmpPath + File.separator + tempColumn.getColumnUrl();
				//判断模板文件是否存在
				if(!FileUtil.exist(columnPath)){
					continue;
				}
				String writePath = null;
				articleList = articleBiz.queryIdsByCategoryId(tempColumn.getCategoryId(), dateTime, null);
				// 有符合条件的新闻就更新
				if (articleList.size() > 0) {
					// 生成文档
					switch (tempColumn.getColumnType()) {
					case ColumnEntity.COLUMN_TYPE_LIST: // 列表
						for (int ai = 0; ai < articleList.size();) {
							int articleId = articleList.get(ai);
							if (tempColumn.getCategoryCategoryId() == 0) { // 如果是顶级下面有文章，那么文章的生成地址就是　分类id/文章编号
								// 组合文章路径如:html/站点id/栏目id/文章id.html
								writePath = generatePath + tempColumn.getColumnPath() + File.separator + articleId + HTML_SUFFIX;
							} else { // 如果有父级别编号，需要组合路径。格式如:父ID/子id/文章id.html
								String path = File.separator + articleId + HTML_SUFFIX;
								writePath = generatePath + tempColumn.getColumnPath() + File.separator + path;
							}
							// 上一篇文章
							ArticleEntity previous = null;
							// 下一篇文章
							ArticleEntity next = null;
							Map<String,Object> pageMap = new HashMap<String,Object>();
							//第一篇文章没有上一篇
							if(ai>0){
								previous = (ArticleEntity) articleBiz.getEntity(articleList.get(ai-1));
								pageMap.put("prelink", url + previous.getColumn().getColumnPath() + File.separator + previous.getArticleID() + HTML_SUFFIX);
								pageMap.put("pretitle", previous.getBasicTitle());
							}else{
								pageMap.put("prelink", "");
								pageMap.put("pretitle", "");
							}
							//最后一篇文章没有下一篇
							if(ai+1 < articleList.size()){
								next = (ArticleEntity) articleBiz.getEntity(articleList.get(ai+1));
								pageMap.put("nextlink", url + next.getColumn().getColumnPath() + File.separator + next.getArticleID() + HTML_SUFFIX);
								pageMap.put("nexttitle", next.getBasicTitle());
							}else{
								pageMap.put("nextlink", "");
								pageMap.put("nexttitle", "");
							}
							Map<String,Object> parserParams = new HashMap<String,Object>();
							parserParams.put("id", articleId);
							parserParams.put("page", pageMap);
							//2、读取模板文件
							Template template = cfg.getTemplate(tempColumn.getColumnUrl(), Const.UTF8);
							//pc端内容
							StringWriter writer = new StringWriter();
							TagParser tag = null;
							String content = null;
							try {
								template.process(null, writer);
								tag = new TagParser(writer.toString(),parserParams);
								content = tag.rendering();
								//3、将pcTag.getContent()写入路径
								FileUtil.writeString(content, writePath, Const.UTF8);
							} catch (TemplateException e) {
								e.printStackTrace();
							}
	
							// 手机端
							if (!StringUtil.isBlank(mobileStyle)) {
								if (tempColumn.getCategoryCategoryId() == 0) { // 如果是顶级下面有文章，那么文章的生成地址就是　分类id/文章编号
									// 组合文章路径如:html/站点id/栏目id/文章id.html
									writePath = generatePath + mobileStyle + File.separator + tempColumn.getColumnPath() + File.separator + articleId + HTML_SUFFIX;
								} else { // 如果有父级别编号，需要组合路径。格式如:父ID/子id/文章id.html
									String path = File.separator + articleId + HTML_SUFFIX;
									writePath = generatePath + mobileStyle + File.separator + tempColumn.getColumnPath() + File.separator + path;
								}
								writer = new StringWriter();
								template = cfg.getTemplate(app.getAppMobileStyle() + File.separator +tempColumn.getColumnUrl(), Const.UTF8);
								parserParams.put(app.getAppMobileStyle(), app.getAppMobileStyle());
								try {
									template.process(null, writer);
									tag = new TagParser(writer.toString(),parserParams);
									content = tag.rendering(parserParams);
									FileUtil.writeString(content, writePath, Const.UTF8);
								} catch (TemplateException e) {
									e.printStackTrace();
									this.outJson(response, false);
								}
							}
							ai++;
						}
						this.outJson(response, true);
						break;
					// case ColumnEntity.COLUMN_TYPE_COVER:// 单页
					// writePath = null;
					// // 取该栏目的最后一篇新闻作为显示内容
					// List<ArticleEntity> list =
					// articleBiz.queryListByColumnId(tempColumn.getCategoryId());
					//
					// String coverTtmpContent = FileUtil.readUtf8String(tmpPath +
					// File.separator + tempColumn.getColumnUrl());// 读取文章模版地址
					// if (list == null || list.size() == 0) { // 表示该栏目下面没有文章
					// break;
					// }
					// ArticleEntity article = list.get(0);// 取一篇文章作为封面栏目的内容
					// // 判断是否 顶级栏目
					// if (tempColumn.getCategoryCategoryId() == 0) {
					// FileUtil.mkdir(generatePath +
					// tempColumn.getCategoryId());
					// writePath = generatePath +
					// String.valueOf(tempColumn.getCategoryId()) + File.separator +
					// RegexConstant.HTML_INDEX;
					// // 设置文章连接地址
					// article.setArticleLinkURL(url + tempColumn.getColumnPath() +
					// File.separator + RegexConstant.HTML_INDEX);
					// } else {// 子栏目，子栏目需要获取父级栏目的编号
					// writePath = generatePath + tempColumn.getColumnPath() +
					// File.separator + RegexConstant.HTML_INDEX;
					// article.setArticleLinkURL(url + tempColumn.getColumnPath() +
					// File.separator + RegexConstant.HTML_INDEX);
					// }
					//
					// String coverContent = generaterFactory.builderArticle(app,
					// tempColumn, article, coverTtmpContent, tmpPath, null, null);
					// // 解析标签
					//
					// FileUtil.writeString(coverContent, writePath, Const.UTF8);//
					// 写文件
					//
					// //移动端
					// if (!StringUtil.isBlank(mobileStyle)) {
					// String temContent = FileUtil.readUtf8String(tmpPath +
					// File.separator + mobileStyle + File.separator +
					// tempColumn.getColumnUrl());// 读取文章模版地址
					// if (list == null || list.size() == 0) { // 表示该栏目下面没有文章
					// break;
					// }
					// // 判断是否 顶级栏目
					// if (tempColumn.getCategoryCategoryId() == 0) {
					// FileUtil.mkdir(generatePath + mobileStyle +
					// File.separator + tempColumn.getCategoryId());
					// writePath = generatePath +mobileStyle+ File.separator +
					// String.valueOf(tempColumn.getCategoryId()) + File.separator +
					// RegexConstant.HTML_INDEX;
					// // 设置文章连接地址
					// article.setArticleLinkURL(url + File.separator + mobileStyle
					// + tempColumn.getColumnPath() + File.separator +
					// RegexConstant.HTML_INDEX);
					// } else {// 子栏目，子栏目需要获取父级栏目的编号
					// writePath = generatePath +mobileStyle+ File.separator +
					// tempColumn.getColumnPath() + File.separator +
					// RegexConstant.HTML_INDEX;
					// FileUtil.mkdir(generatePath + mobileStyle +
					// File.separator + tempColumn.getColumnPath());
					// article.setArticleLinkURL(url + File.separator + mobileStyle
					// + tempColumn.getColumnPath() + File.separator +
					// RegexConstant.HTML_INDEX);
					// }
					//
					// String temp = generaterFactory.builderArticle(app,
					// tempColumn, article, coverTtmpContent, tmpPath, null,
					// null,mobileStyle); // 解析标签
					//
					// FileUtil.writeString(temp, writePath, Const.UTF8);// 写文件
					// }
					//
					//
					// break;
					}
				}
	
				/*
				 * else { switch (tempColumn.getColumnType()) { case
				 * ColumnEntity.COLUMN_TYPE_COVER: String coverTtmpContent =
				 * FileUtil.readUtf8String(tmpPath + File.separator +
				 * tempColumn.getColumnUrl()); if
				 * (tempColumn.getCategoryCategoryId() == 0) { // 顶级栏目
				 * FileUtil.mkdir(generatePath + tempColumn.getCategoryId());
				 * } writePath = generatePath + tempColumn.getColumnPath() +
				 * File.separator + RegexConstant.HTML_INDEX; String coverContent =
				 * generaterFactory.builderIndex(app, tempColumn, coverTtmpContent,
				 * tmpPath); // 解析标签 // 取最后一篇文章作为栏目内容
				 * FileUtil.writeString(coverContent, writePath, Const.UTF8);// 写文件
				 * break; } }
				 */
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.outJson(response, false);
		}
	}

	/**
	 * 提供给保存与编辑文章时使用
	 * 
	 * @param request
	 * @param response
	 * @param columnId
	 *            　栏目编号
	 */
	@RequestMapping("/{columnId}/genernateForArticle")
	@ResponseBody
	public void genernateForArticle(HttpServletResponse response, HttpServletRequest request, @PathVariable int columnId) {
		// 生成html
		// 1、更新文章
//		Map parms = new HashMap();
//		parms.put("dateTime", StringUtil.getSimpleDateStr(new Date(), "yyyy-MM-dd"));
//		Header header = new Header(this.getHost(request), com.mingsoft.base.constant.Const.UTF8);
//		String cookie = "";
//		for (Cookie c : request.getCookies()) {
//			cookie += c.getName() + "=" + c.getValue() + ";";
//		}
//		header.setCookie(cookie);
//		Result re = Proxy.get(this.getUrl(request) + managerPath + "/cms/generate/" + columnId + "/generateArticle.do", header, parms);
//		ColumnEntity column = (ColumnEntity) columnBiz.getEntity(columnId);
//		if (column != null && column.getColumnType() == ColumnTypeEnum.COLUMN_TYPE_COVER.toInt()) {
//			Proxy.get(this.getUrl(request) + managerPath + "/cms/generate/" + columnId + "/genernateColumn.do", header, null);
//		}
//		// 2、更新栏目
//		// Proxy.get(this.getUrl(request)+"/manager/cms/generate/"+columnId+"/genernateColumn.do",
//		// header, null, Const.UTF8);
//
//		// 3主
//		Map map = new HashMap();
//		map.put("url", INDEX + HTM_SUFFIX);
//		map.put("position", INDEX + HTML_SUFFIX);
//		Proxy.get(this.getUrl(request) + managerPath + "/cms/generate/generateIndex.do", header, map);
		Map parms = new HashMap();
		parms.put("dateTime", StringUtil.getSimpleDateStr(new Date(), "yyyy-MM-dd"));
		StringBuffer cookie = new StringBuffer();
		for (Cookie c : request.getCookies()) {
			cookie.append(c.getName()).append("=").append(c.getValue()).append(";");
		}
		HttpUtil.get(this.getUrl(request) + managerPath + "/cms/generate/" + columnId + "/generateArticle.do",parms);
		ColumnEntity column = (ColumnEntity) columnBiz.getEntity(columnId);
		if (column != null && column.getColumnType() == ColumnTypeEnum.COLUMN_TYPE_COVER.toInt()) {
			HttpUtil.get(this.getUrl(request) + managerPath + "/cms/generate/" + columnId + "/genernateColumn.do");
		}
		// 2、更新栏目
		// Proxy.get(this.getUrl(request)+"/manager/cms/generate/"+columnId+"/genernateColumn.do",
		// header, null, Const.UTF8);

		// 3主
		Map map = new HashMap();
		map.put("url", INDEX + HTM_SUFFIX);
		map.put("position", INDEX + HTML_SUFFIX);
		HttpUtil.get(this.getUrl(request) + managerPath + "/cms/generate/generateIndex.do", map);

		this.outJson(response, ModelCode.CMS_GENERATE_ARTICLE, true);
	}

	/**
	 * 根据栏目id更新所有的文章
	 * 
	 * @param request
	 * @param response
	 * @param columnId
	 */
	@RequestMapping("/{articleId}/generateArticleByArticleId")
	@ResponseBody
	public void generateArticleByArticleId(HttpServletRequest request, HttpServletResponse response, @PathVariable int articleId) {
		AppEntity app = BasicUtil.getApp();
		String generatePath = getRealPath(request, HTML) + File.separator + app.getAppId() + File.separator;// 站点生成后保存的html地址
		FileUtil.mkdir(generatePath);
		String tmpPath = getRealPath(request, TEMPLATES) + File.separator + app.getAppId() + File.separator + app.getAppStyle(); // 网站风格物理路径
		String url = app.getAppHostUrl() + File.separator + HTML + File.separator + app.getAppId() + File.separator; // 文章地址前缀
		ArticleEntity article = (ArticleEntity) articleBiz.getBasic(articleId);
		ColumnEntity tempColumn = article.getColumn();
		FileUtil.mkdir(generatePath + tempColumn.getColumnPath());
		String writePath = null;
		
		// //
		// 根据栏目获取文章列表
		// 生成文档
		switch (tempColumn.getColumnType()) {
		case ColumnEntity.COLUMN_TYPE_LIST: // 列表
			String tmpContent = FileUtil.readUtf8String(tmpPath + File.separator + tempColumn.getColumnUrl());// 读取文章模版地址
			ArticleEntity previous = articleBiz.getPrevious(tempColumn.getCategoryAppId(), articleId,article.getBasicCategoryId());
			if (previous != null) {
				previous.setArticleLinkURL(url + tempColumn.getColumnPath() + File.separator + previous.getArticleID() + HTML_SUFFIX);
			}
			ArticleEntity next = articleBiz.getNext(tempColumn.getCategoryAppId(), articleId,article.getBasicCategoryId());
			if (next != null) {
				next.setArticleLinkURL(url + tempColumn.getColumnPath() + File.separator + next.getArticleID() + HTML_SUFFIX);
			}
			
			//生成页面
			Map map = new HashMap();
			String content = "";//cmsParser.parse(tmpContent,app,tempColumn,article,map);
			
			if (tempColumn.getCategoryCategoryId() == 0) { // 如果是顶级下面有文章，那么文章的生成地址就是　分类id/文章编号
				FileUtil.mkdir(generatePath + tempColumn.getCategoryId());
				// 组合文章路径如:html/站点id/栏目id/文章id.html
				writePath = generatePath + tempColumn.getColumnPath() + File.separator + article.getArticleID() + HTML_SUFFIX;
				article.setArticleLinkURL(url + tempColumn.getColumnPath() + File.separator + article.getArticleID() + HTML_SUFFIX);
			} else { // 如果有父级别编号，需要组合路径。格式如:父ID/子id/文章id.html
				String path = File.separator + article.getArticleID() + HTML_SUFFIX;
				writePath = generatePath + tempColumn.getColumnPath() + File.separator + path;
				article.setArticleLinkURL(url + tempColumn.getColumnPath() + File.separator + article.getArticleID() + HTML_SUFFIX);
			}
			FileUtil.writeString(content, writePath, Const.UTF8);// 写文件
			break;
		case ColumnEntity.COLUMN_TYPE_COVER:// 单页
			writePath = null;
			// 取该栏目的最后一篇新闻作为显示内容
			List<ArticleEntity> list = articleBiz.queryListByColumnId(tempColumn.getCategoryId());

			String coverTtmpContent = FileUtil.readUtf8String(tmpPath + File.separator + tempColumn.getColumnUrl());// 读取文章模版地址
			if (list == null || list.size() == 0) { // 表示该栏目下面没有文章
				break;
			}
			// 判断是否 顶级栏目
			if (tempColumn.getCategoryCategoryId() == 0) {
				FileUtil.mkdir(generatePath + tempColumn.getCategoryId());
				writePath = generatePath + String.valueOf(tempColumn.getCategoryId()) + File.separator + INDEX + HTML_SUFFIX;
				// 设置文章连接地址
				article.setArticleLinkURL(url + tempColumn.getColumnPath() + File.separator + INDEX + HTML_SUFFIX);
			} else {// 子栏目，子栏目需要获取父级栏目的编号
				writePath = generatePath + tempColumn.getColumnPath() + File.separator + INDEX + HTML_SUFFIX;
				article.setArticleLinkURL(url + tempColumn.getColumnPath() + File.separator + INDEX + HTML_SUFFIX);
			}
			
			//生成页面
			String coverContent = "";//cmsParser.parse(coverTtmpContent,app,tempColumn,article);
																																	// 取最后一篇文章作为栏目内容
			FileUtil.writeString(coverContent, writePath, Const.UTF8);// 写文件
			break;
		}
		this.outJson(response, true);
	}

	/**
	 * 更新栏目
	 * 
	 * @return
	 */
	@RequestMapping("/column")
	public String column(HttpServletRequest request,ModelMap model) {
		// 该站点ID有session提供
		int websiteId =  BasicUtil.getAppId();
		Integer modelId = modelBiz.getEntityByModelCode(ModelCode.CMS_COLUMN).getModelId(); // 查询当前模块编号
		//获取所有的内容管理栏目
		List<ColumnEntity> list  = columnBiz.queryAll(websiteId,modelId);
		model.addAttribute("list",  JSONArray.toJSONString(list));
		return view("/cms/generate/generate_column");
	}

	/**
	 * 用户预览主页
	 * @param request 
	 * @return
	 */
	@RequestMapping("/{position}/viewIndex")
	public String viewIndex(HttpServletRequest request, @PathVariable String position, HttpServletResponse response) {
		AppEntity app = BasicUtil.getApp();
		//组织主页预览地址
		String indexPosition = app.getAppHostUrl() +  File.separator + HTML + File.separator + app.getAppId() + File.separator + position;
		return "redirect:" + indexPosition;
	}

}