define(function(require, exports, module) {
  var ms = require("ms");
  var IMAGE = "image",FILE="file",ALL="all";
  
  var mimeTypes = { "image":{ title : "Image files", extensions : "jpg,JPG,jpeg,PNG,gif,png" },
                    "file": { title : "Zip files", extensions : "ZIP,zip,DOC,doc,docx,xls,XLS,xlsx,RAR,rar" },
                    "video": { title : "video files", extensions : "MP3,MP4" },
                    "all": { title : "all files", extensions : "jpg,JPG,jpeg,PNG,gif,png,ZIP,zip,DOC,doc,docx,xls,XLS,xlsx,RAR,rar" },
                  };

  var mimeTypesDom = {
                    "image":"<div><img /><span>删除</span></div>",
                    "file": "<div><b></b><span>删除</span></div>",
                    "all": "<div><b></b><span>删除</span></div>",
                    "video": "<div><b></b><span>删除</span></div>",
                    "input":"<input type='hidden' value />"
                  }
   
  //输入参数 1、表单名称 控制要动态生成input标签 2、预览按钮（必需） 3、文件上传成功后需要展现dom区域(展示区域样式) 4、removeClass移除样式
  //4、主题样式 5、文件保存路径（必需）6、上传类型 image  file  video 7、多个单个
  //返回参数 根据上传地址返回的服务器文件地址赋值到动态生成的input标签
  
  
  var upload = {
    
    /**
    *文件上传控件
    ------
    * @param  {{type:html.tag.id,have:true}}  id html标签id属性，用于绑定预览文件上传事件
    * @param  {{type:form.tag.name,have:true}} inputDom 隐藏表单元素name属性，用于获取文件上传地址
    * @param  {{type:string,have:true}} uploadPath 文件上传后的存放路径
    * @param  {{type:type}} json对象 可对上传文件的类型的约束
    * 
    * image:单图(默认)
    * file:文件
    * video:视频
    * @param  {{type:size}} - 限制上传文件的大小,默认大小为1024kb
    * @param  {{type:single}} - 浏览框是否可以多选，默认为true禁止对浏览框多选
    * @param  {{type:domClass}} - 对返回的文件dom进行样式追加
    * @callmethod 
    * 图片上传控件没有具体返回值，但会返回到页面如下内容图片类型返回：
    * <div><img src="http://ms.ming-soft.com/"/><span>删除</span></div> 
    * 视频或文件类型返回：
    * <div><p></p><span>删除</span></div>
    * @examples 
    * ... 
    * <form>
    * ...
    *   <div class="uploadImg">选择图片</div>
    * ...
    * </form>
    * ...
    * upload.init("uploadImg","upgraderVersionZipUrl","http://ms.ming-soft.com/",{"type":"image","size":1024,"single":true,"domClass":"imgshow"})
    * ...
    * @function
    * ...
    * <form>
    *   ...
    *   <div class="imgshow"><img src="http://ms.ming-soft.com/a.jpg"/><span>删除</span></div>
    *   <div class="uploadImg">选择图片</div>
    *    ... 
    *   <input type=""hidden" name="upgraderVersionZipUrl" value="http://ms.ming-soft.com/a.jpg"/>
    * </form>
    * ... 
    */
    init:function(id,inputDom,uploadPath,cfg) {
      var targetDom;
      var uploadCfg = {"url":ms.base+"/file/upload.do","type":"image","size":500,"single":true,"domClass":"","beforeMsg":"上传中...","afterMsg":"更换"};
      targetDom  = $("#"+id);
      //判断cfg是否为json格式，不是则将默认参数传给cfg
      if(ms.isJson(cfg)){
          data(cfg);
      }

      //实例化一个plupload上传对象
      var uploader = new plupload.Uploader({ 
        browse_button : id,//预览按钮元素
        url :uploadCfg.url ,//上传地址
        flash_swf_url : 'js/Moxie.swf',
        silverlight_xap_url : 'js/Moxie.xap',
        multi_selection:uploadCfg.single,//禁止浏览框多选
        multipart_params:{"uploadPath":uploadPath},//文件保存路径参数
        filters: {//文件类型 大小设置,对不同场景的文件上传配置此参数
          mime_types : [ 
            mimeTypes[uploadCfg.type], //允许上传的文件后缀
          ],
          max_file_size : uploadCfg.size+'kb', //最大只能上传400kb的文件
          //prevent_duplicates : true //不允许选取重复文件
        },
      });

      //初始化
      uploader.init(); 

      //绑定文件添加进队列事件
      uploader.bind('FileFiltered',function(uploader,file) {
        targetDom.attr("disabled", true);
        if(targetDom.type == "button"){
          targetDom.val(uploadCfg.beforeMsg);
        }else{
          targetDom.html(uploadCfg.beforeMsg);
        }
        
        uploader.start(); //开始上传
      });
      
      //当队列中的某一个文件上传完成后触发
      //先判断类型，如果是图片，那么进行图片地址的赋值与展示
      uploader.bind('FileUploaded',function(uploader,file,responseObject){
        targetDom.attr("disabled", false);

        if(targetDom.type == "button"){
          targetDom.val(uploadCfg.afterMsg);
        }else{
          targetDom.html(uploadCfg.afterMsg);
        }
        if(uploadCfg.afterDom == "show"){
          targetDom.show();
        }else if(uploadCfg.afterDom == "hide"){
          targetDom.hide();
        }
        var newDom = $(mimeTypesDom[uploadCfg.type]);
        var newInput = $(mimeTypesDom.input);
        targetDom.prev('div').remove();//清空当前的文件
        //添加dom的样式
        newDom.addClass(uploadCfg.domClass);
        //判断上传控件类型进行赋值显示
        if(uploadCfg.type==IMAGE)  {
          newDom.find("img").attr("src",ms.base+responseObject.response)
        } else if(uploadCfg.type==FILE) {
          newDom.find("b").text(file.name);
        }else if(uploadCfg.type==ALL){
          newDom.find("b").text(file.name);
        }else{
          newDom.find("b").text(file.name);
        }
        newInput.attr("name",inputDom);
        newInput.val(responseObject.response);
        targetDom.before(newInput);
        //将DOM对象追加到浏览元素前面
        targetDom.before(newDom);
        //点击删除进行删除操作
        newDom.find("span").click(function(){
          targetDom.prev('div').remove();//清空当前的图片
          newInput.remove();
          targetDom.show();
        })
        if(!ms.isJson(cfg.success)){
          cfg.success(file);
        }
        
      });  



      //上传发生的错误类型判断
      uploader.bind('Error',function(uploader,errObject){
        if(errObject.code==-600){
          ms.alert("文件过大");
        }else if(errObject.code==-700){
          ms.alert("图片格式错误");
        }
      });

      //判断是否存在可选参数
      function data(cfg) {
        if(!ms.isEmpty(cfg.type)){
          uploadCfg.type = cfg.type;
        }
        if(!ms.isEmpty(cfg.size)){
          uploadCfg.size = cfg.size;
        }
        if(!ms.isEmpty(cfg.single+"")){
          uploadCfg.single = cfg.single;
        }    
        if(!ms.isEmpty(cfg.domClass)){
          uploadCfg.domClass = cfg.domClass;
        }      
        if(!ms.isEmpty(cfg.beforeMsg)){
          uploadCfg.beforeMsg = cfg.beforeMsg;
        }    
        if(!ms.isEmpty(cfg.afterMsg)){
          uploadCfg.afterMsg = cfg.afterMsg;
        }   
        if(!ms.isEmpty(cfg.afterDom)){
          uploadCfg.afterDom = cfg.afterDom;
        }    
         
      }
    },
    //销毁上传事件
    Destroy:function(id){

    },

  }

  module.exports = upload;



});


