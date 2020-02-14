//复制属性
function copyVal(btn){
	var a = btn.parentNode.firstChild;
	var textArea = document.createElement('textarea');
	textArea.value = a.nodeValue;
	document.body.appendChild(textArea);
    textArea.select();
	document.execCommand("copy"); // 执行浏览器复制命令
	document.body.removeChild(textArea);
	btn.value = "复制成功";
}
function copyUrl(pt){
	var textArea = document.createElement('textarea');
	textArea.value = pt.previousSibling.innerHTML;
	document.body.appendChild(textArea);
    textArea.select();
	document.execCommand("copy"); // 执行浏览器复制命令
	document.body.removeChild(textArea);
	pt.value = "复制成功";
}


$(function(){
	//初始化文档
	$.ajax({
	    url:"/lkad/doc",
	    type:"get",
	    dataType:"json",
	    async:false,
	    data:{'random':Math.random()},
	    success:function(data){
	    	if(data != null){
	    		if(data.enabled == 'no'){
	    			$("body").html("");
	    			window.location = "lkad404.html";
	    		}else{
	    			if(data.error != null && data.error != ""){
	    				$("body").html(data.error);
	    			}else{
						$("#projectName").html(data.projectName);
						$("#description").html(data.description);
						var doc = data.apiDoc;
						if(doc != null && doc.length > 0){
							for(var i = 0;i<doc.length;i++){
								$(".box").append(buildMenu(doc[i]));
							}
						}
	    			}
	    		}
			}
	    },
	    error:function(respose){
	    	alert("status:"+respose.status+",statusText:"+respose.statusText);
	    }
	}); 
	
	//生成复制按钮 复制属性名
	$(".box").on("mouseenter",".addinfo",function(){
		$(this).append('<input type="button" class="copyText" onclick="copyVal(this)" value="复制">');
	})
	$(".box").on("mouseleave",".addinfo",function(){
		$(this).find("input").remove();
	})
	
	//生成复制按钮 复制属性名
	$(".box").on("mouseenter","h5",function(){
		$(this).find(".requestPath").after('<input type="button" class="copyText" onclick="copyUrl(this)" value="复制">');
	})
	$(".box").on("mouseleave","h5",function(){
		$(this).find("input").remove();
	})

	//设置高亮参数判断
	function getParamInfo(){
		$.getJSON("/lkad/getParamInfo",{'random':Math.random()},function(data){
			if(data != null && data != 'null'){
				$(".addinfo").each(function(){
					try{
						//let value = $(this).html();
						let value = this.firstChild.nodeValue;
						let type = $(this).parents(".hovertable").find(".reqcls").length > 0 ?1:2;
						let methodurl = $(this).parents(".method-li").find("h5").find("span:eq(1)").html();
						let content = methodurl+"."+type+"."+value;
						let str = data[content];
						if(str != null && str.length>0){
							var arrs = str.split("-");
							//匹配上，设置样式
							$(this).attr("title",arrs[1]);
							if(arrs[0] == 1){
								$(this).css("color","#a00");
							}else if(arrs[0] == 2){
								$(this).css("color","#0a0");
							}else if(arrs[0] == 3){
								$(this).css("color","#555").css("text-decoration","line-through");
							}else if(arrs[0] == 4){
								$(this).css("color","#3385ff");
							}else{
								$(this).css("color","#000").css("text-decoration","none");
								$(this).attr("title",'双击可添加参数修改信息');
							}
						}else{
							$(this).css("color","#000").css("text-decoration","none");
							$(this).attr("title",'双击可添加参数修改信息');
						}
					}catch{
						return false;
					}
				})
			}
		});
	}
	getParamInfo();
	
	//双击属性弹出窗体
	$(".box").off('dblclick','.addinfo');
	$(".box").on("dblclick",".addinfo",function(){
		var tit = $(this).attr("title");
		//获取要保存的属性
		//let value = $(this).html();
		let value = this.firstChild.nodeValue;
		let type = $(this).parents(".hovertable").find(".reqcls").length > 0 ?1:2;
		let methodurl = $(this).parents(".method-li").find("h5").find("span:eq(1)").html();
		if(tit != '双击可添加参数修改信息'){
			var bool = confirm("您确定已经了解修改参数的相关信息吗？");
			if(bool){
				$.ajax({
					url:"/lkad/delParamInfo",
				    type:"post",
				    dataType:"text",
				    data:{"value":value,"type":type,"url":methodurl,'random':Math.random()},
				    success:function(data){
				    	getParamInfo();
						alert(data);
				    },
				    error:function(){
				    	alert("连接服务器异常");
				    }
				})
			}
		}else{
			//获取弹窗元素
			let modal = document.getElementById("simpleModal");
		    
		    modal.style.display = "block";
		    
		    $("#modalconfirm").click(function(){
		    	let modaltype = $("#modaltype").val();
		    	let modalcontent = $("#modalcontent").val();
				console.log(value+"-"+type+"-"+methodurl+"-"+modaltype+"-"+modalcontent);
				$('#modalconfirm').off(); //解除所有绑定事件
				modal.style.display = "none";
				$.ajax({
					url:"/lkad/addParamInfo",
				    type:"post",
				    dataType:"text",
				    data:{"value":value,"type":type,"url":methodurl,"modaltype":modaltype,"content":modalcontent,'random':Math.random()},
				    success:function(data){
				    	getParamInfo();
						alert(data);
				    },
				    error:function(){
				    	alert("连接服务器异常");
				    }
				})
		    });
		    
		    //获取关闭弹窗按钮元素
		    let closeBtn = document.getElementsByClassName("closeBtn")[0];
		 
		    //监听关闭弹窗事件
		    closeBtn.addEventListener("click",closeModal);
		 
		    //监听window关闭弹窗事件
		    window.addEventListener("click",outsideClick);
		 
		    //关闭弹框事件
		    function closeModal () {
		        modal.style.display = "none";
		        $('#modalconfirm').off(); //解除所有绑定事件
		    }
		    $("#modalcancel").click(function(){
		    	 modal.style.display = "none";
		    	 $('#modalconfirm').off(); //解除所有绑定事件
		    });
		    
		    //outsideClick
		    function outsideClick (e) {
		        if(e.target == modal){
		            modal.style.display = "none";
		            $('#modalconfirm').off(); //解除所有绑定事件
		        }
		    }
		}
	})
	
	
	$(".isRequired").each(function(){
		if($(this).html() == 'yes'){
			$(this).css("color","#f00");
		}
	})
	
	//加载令牌
	$(".headerKey").val($.cookie('tokenKey'));
	$(".headerValue").val($.cookie('tokenValue'));
	
	$(".box").on("click","h3",function(){
		$(this).next().toggle()
		if($(this).find("div").attr('class')=='d4'){
			$(this).find("div").removeClass("d4");
			$(this).find("div").addClass("d3");
		}else{
			$(this).find("div").removeClass("d3");
			$(this).find("div").addClass("d4");
		}
	})
	
	$(".box").on("click","li h5",function(){
		//$(this).siblings().toggle();
		$(this).parent().siblings().hide();
		$(this).parent().parent().siblings().hide();
		//$(this).show();
		$(this).siblings().show();
	})
	
	$(".box").on("click",".method-back",function(){
		$("h3").show();
		$(this).parents("table").parent().hide();
		$(this).parents("table").parent().next().hide();
		$(this).parents("table").parent().parent().siblings().show();
	})
	
	$(".box").on("click",".add",function(){
		$(this).prev().before("<input class='prevData' type='text'/>");
	})
	
	$(".box").on("click",".subtract",function(){
		if($(this).prev().attr('class')=='prevData'){
			$(this).prev().remove();
		}
	})
	
	$(".box").on("click",".close-resposeData a",function(){
		$(this).parent().parent().hide();
		$(this).parents("table").find(".resposeData").hide();
	})
	
	$(".saveToken").click(function(){
		if($(this).val()=='edit'){
			$(this).val('save');
			$(".headerKey").attr('disabled',false);
			$(".headerKey").attr('type','text');
			$(".headerValue").attr('disabled',false);
			$(".headerValue").attr('type','text');
		}else if($(this).val()=='save'){
			$(this).val('edit');
			$(".headerKey").attr('disabled',true);
			$(".headerKey").attr('type','password');
			$(".headerValue").attr('disabled',true);
			$(".headerValue").attr('type','password');
			var tokenKey = $(".headerKey").val();
			var tokenValue = $(".headerValue").val();
			if(tokenKey != null && tokenValue != null && tokenKey != "" && tokenValue != ""){
				$.cookie('tokenKey',tokenKey);
				$.cookie('tokenValue',tokenValue);
			}
		}
	})
    
	//请求参数json展示
    $(".box").on("click",".request-json",function (){
    	$(this).parents("table").find(".requestData").toggle();
		//获取请求参数名称
		var paramValues = $(this).parents("table").find(".paramValue");
		//获取请求参数位置
		var paramTypes = $(this).parents("table").find(".paramType");
		//获取请求测试数据
		var testDatas = $(this).parents("table").find(".testData");
		//获取是否必须
		var isRequireds = $(this).parents("table").find(".isRequired");
		//获取数据类型
		var dataTypes = $(this).parents("table").find(".dataType");
		//获取参数说明
		var paramInfos = $(this).parents("table").find(".paramInfo");
		
		var queryJson = {};
		var paramNames = new Array();
		for(var i = 0;i<paramValues.length;i++){
			paramNames.push(paramValues.eq(i).html());
		}
		//带参数说明的json对象
		var queryJson = assembleJson2(paramNames,paramInfos,dataTypes,paramTypes,"query");
		//console.log(queryJson);
		//var headerJson = assembleJson(paramNames,testDatas,dataTypes,paramTypes,"header");
		//var pathJson = assembleJson(paramNames,testDatas,dataTypes,paramTypes,"path");
		var options = {
    			collapsed:false,
    			withQuotes:false
    	}
		$(this).parents("table").find(".requestData").jsonViewer(queryJson,options);
    })
    //响应结果json展示
    $(".box").on("click",".switch-resp-json",function (){
    	var resposeDataJson = $(this).parents("div").next().find("table").find(".resposeDataJson");
		var resposeDataTable = $(this).parents("div").next().find("table").find(".resposeDataTable");
    	if($(this).val()=='表格展示响应内容'){
    		resposeDataTable.show();
    		resposeDataJson.hide();
    		$(this).val('树状展示响应内容');
    	}else if($(this).val()=='树状展示响应内容'){
    		resposeDataTable.hide();
    		resposeDataJson.show();
    		$(this).val('表格展示响应内容');
    		//获取请求参数名称
			var respValues = $(this).parents("div").next().find("table").find(".respValue");
			//获取参数说明
			var respInfos = $(this).parents("div").next().find("table").find(".respInfo");
			//获取数据类型
			var dataTypes = $(this).parents("div").next().find("table").find(".respType");
			//获取数据类型
			var respTypes = $(this).parents("div").next().find("table").find(".respType");
			
			var queryJson = {};
			var respNames = new Array();
			for(var i = 0;i<respValues.length;i++){
				respNames.push(respValues.eq(i).html());
			}
			//带参数说明的json对象
			var respJson = assembleJson2(respNames,respInfos,dataTypes,respTypes,"resp");
			//var headerJson = assembleJson(paramNames,testDatas,dataTypes,paramTypes,"header");
			//var pathJson = assembleJson(paramNames,testDatas,dataTypes,paramTypes,"path");
			console.log(JSON.stringify(respJson))
			var options = {
	    			collapsed:false,
	    			withQuotes:false
	    	}
			resposeDataJson.find("td").jsonViewer(respJson,options);
    	}
    })
    
	
	//接口测试
	$(".box").on("click",".testSendButton",function(){
		$(this).parents("table").find(".resposeData").html("");
		$(this).parents("table").find(".resposeData").show();
		$(this).parents("table").find(".close-resposeData").parent().show();
		//获取请求方式
		var methodType = $(this).parents("table").parent().prev().find(".requestType").html();
		//获取请求路径
		var path = $(this).parents("table").parent().prev().find(".requestPath").html();
		//获取请求参数名称
		var paramValues = $(this).parents("table").find(".paramValue");
		//获取请求参数位置
		var paramTypes = $(this).parents("table").find(".paramType");
		//获取请求测试数据
		var testDatas = $(this).parents("table").find(".testData");
		//获取是否必须
		var isRequireds = $(this).parents("table").find(".isRequired");
		//获取数据类型
		var dataTypes = $(this).parents("table").find(".dataType");
		
		var queryJson = {};
		var headerJson = {};
		var restJson = {};
		var paramNames = new Array();
		for(var i = 0;i<paramValues.length;i++){
			paramNames.push(paramValues.eq(i).html());
		}
		var queryJson = assembleJson(paramNames,testDatas,dataTypes,paramTypes,"query");
		var headerJson = assembleJson(paramNames,testDatas,dataTypes,paramTypes,"header");
		var pathJson = assembleJson(paramNames,testDatas,dataTypes,paramTypes,"path");
		//rest风格url参数设置
		for (var val in pathJson) {
			path = path.replace('{'+val+'}',pathJson[val]);
		}
		
		/*//请求头参数
		//var contentType = "application/json";
		//var contentType = "application/x-www-form-urlencoded";
		var contentType = $('input:radio:checked').val();
		if(contentType != null && contentType !=""){
			headerJson['Content-Type']=contentType;
		}
		//请求头令牌设置
		var tokenKey = $(".headerKey").val();
		var tokenValue = $(".headerValue").val();
		if(tokenKey != null && tokenValue != null && tokenKey != "" && tokenValue != ""){
			headerJson[tokenKey] = tokenValue;
		} 
		
		var resposeData = $(this).parents("table").find(".resposeData");
		
		//是否阻止深度序列化
		var tl = false;
		if($(".app-traditional").is(':checked')){
			tl = true;
		}
		
		var queryData = contentType=="application/json;charset=utf-8"?JSON.stringify(queryJson):queryJson;
		
		$.ajax({
		    url:path,
		    type:methodType=='通用'?'get':methodType,
		    dataType:"text",
		    async:true,
		    data:queryData,
		    headers:headerJson,
		    traditional:tl, //阻止深度序列化
		    success:function(data){
		    	var options = {
		    			collapsed:false,
		    			withQuotes:false
		    	}
		    	resposeData.jsonViewer(JSON.parse(data),options);
		    },
		    error:function(respose){
		    	var json = {};
		    	json['status'] = respose.status;
		    	json['statusText'] = respose.statusText;
		    	json['responseText'] = respose.responseText;
		    	var options = {
		    			collapsed:false,
		    			withQuotes:false
		    	}
		    	resposeData.jsonViewer(json,options);
		    }
		}); */
		
		var contentType = $(this).parent().find('input:radio:checked').val();
		
		if(contentType != null && contentType !=""){
			headerJson['Content-Type']=contentType;
		}else{
			headerJson['Content-Type']="application/x-www-form-urlencoded";
		}
		//请求头令牌设置
		var tokenKey = $(".headerKey").val();
		var tokenValue = $(".headerValue").val();
		if(tokenKey != null && tokenValue != null && tokenKey != "" && tokenValue != ""){
			headerJson[tokenKey] = tokenValue;
		} 
		
		var resposeData = $(this).parents("table").find(".resposeData");
		
		//是否阻止深度序列化
		var tl = false;
		if($(this).parents("table").find(".app-traditional").is(':checked')){
			tl = true;
		}
		var queryData = contentType=="application/json"?JSON.stringify(queryJson):queryJson;
		
		var fileInput = $(this).parents("table").find(".upload");
		var processData = true;
		var contentTypeBool = true;
		if(fileInput != null && fileInput.length > 0){
			queryData = new FormData(fileInput[0]);
			processData=false;   // jQuery不要去处理发送的数据
			contentTypeBool=false;   // jQuery不要去设置Content-Type请求头
			delete headerJson['Content-Type'];
		}
		/*console.log(contentType);
		console.log(path);
		console.log(methodType);
		console.log(queryData);
		console.log(headerJson);
		console.log(tl);*/
		$.ajax({
		    url:path,
		    type:methodType=='通用'?'get':methodType,
		    dataType:"text",
		    async:true,
		    data:queryData,
		    headers:headerJson,
		    traditional:tl, //阻止深度序列化
		    cache:false,
		    processData:processData,
		    contentType:contentTypeBool,
		    success:function(data){
		    	var options = {
		    			collapsed:false,
		    			withQuotes:false
		    	}
		    	try{
		    		resposeData.jsonViewer(JSON.parse(data),options);
		    	}catch{
		    		resposeData.html(data);
		    	}
		    },
		    error:function(respose){
		    	var json = {};
		    	json['status'] = respose.status;
		    	json['statusText'] = respose.statusText;
		    	json['responseText'] = respose.responseText;
		    	var options = {
		    			collapsed:false,
		    			withQuotes:false
		    	}
		    	try{
		    		resposeData.jsonViewer(json,options);
		    	}catch{
		    		resposeData.html(json);
		    	}
		    }
		}); 
	})
	
	//设置方法条color
	$(".requestType").each(function(){
		//console.log($(this).html());
		if($(this).html() == 'POST' || $(this).html() == 'post'){
			$(this).parent().css("background-image","linear-gradient(#c3ffe3,#f8f8f8)").css("border-top","1px solid #60dda0");
			$(this).parent().parent().find(".reqcls").css("backgroundColor","#e3ffe3");
			$(this).parent().parent().find(".respcls").css("backgroundColor","#e3ffe3");
			$(this).css("backgroundColor","#60dda0").css("color","#ffffff");
			//$(this).next().css("backgroundColor","#60dda0");
			//$(this).next().next().css("backgroundColor","#60dda0");
			$(this).next().next().next().next().next().css("backgroundColor","#60dda0");
			$(this).parent().hover(function(){
				$(this).css("background-image","linear-gradient(#a3dfb3,#f8f8f8)");
			},function(){
				$(this).css("background-image","linear-gradient(#c3ffe3,#f8f8f8)");
			});
		}
		if($(this).html() == 'DELETE' || $(this).html() == 'delete'){
			$(this).parent().css("background-image","linear-gradient(#e3c3ff,#f8f8f8)").css("border-top","1px solid #a060dd");
			$(this).parent().parent().find(".reqcls").css("backgroundColor","#e3e3ff");
			$(this).parent().parent().find(".respcls").css("backgroundColor","#e3e3ff");
			$(this).css("backgroundColor","#a060dd").css("color","#ffffff");
			//$(this).next().css("backgroundColor","#a060dd");
			//$(this).next().next().css("backgroundColor","#a060dd");
			$(this).next().next().next().next().next().css("backgroundColor","#a060dd");
			$(this).parent().hover(function(){
				$(this).css("background-image","linear-gradient(#b3a3df,#f8f8f8)");
			},function(){
				$(this).css("background-image","linear-gradient(#e3c3ff,#f8f8f8)");
			});
		}
		if($(this).html() == 'GET' || $(this).html() == 'get'){
			$(this).parent().css("background-image","linear-gradient(#c3e3ff,#f8f8f8)").css("border-top","1px solid #60a0dd");
			$(this).parent().parent().find(".reqcls").css("backgroundColor","#e3e3ff");
			$(this).parent().parent().find(".respcls").css("backgroundColor","#e3e3ff");
			$(this).css("backgroundColor","#60a0dd").css("color","#ffffff");
			//$(this).next().css("backgroundColor","#60a0dd");
			//$(this).next().next().css("backgroundColor","#60a0dd");
			$(this).next().next().next().next().next().css("backgroundColor","#60a0dd");
			$(this).parent().hover(function(){
				$(this).css("background-image","linear-gradient(#a3b3df,#f8f8f8)");
			},function(){
				$(this).css("background-image","linear-gradient(#c3e3ff,#f8f8f8)");
			});
		}
		if($(this).html() == 'PUT' || $(this).html() == 'put'){
			$(this).parent().css("background-image","linear-gradient(#ffc3e3,#f8f8f8)").css("border-top","1px solid #dda060");
			$(this).parent().parent().find(".reqcls").css("backgroundColor","#ffe3e3");
			$(this).parent().parent().find(".respcls").css("backgroundColor","#ffe3e3");
			$(this).css("backgroundColor","#dda060").css("color","#ffffff");
			//$(this).next().css("backgroundColor","#dda060");
			//$(this).next().next().css("backgroundColor","#dda060");
			$(this).next().next().next().next().next().css("backgroundColor","#dda060");
			$(this).parent().hover(function(){
				$(this).css("background-image","linear-gradient(#dfa3b3,#f8f8f8)");
			},function(){
				$(this).css("background-image","linear-gradient(#ffc3e3,#f8f8f8)");
			});
		}
		if($(this).html() == '通用'){
			$(this).parent().css("background-image","linear-gradient(#e3ffc3,#f8f8f8)").css("border-top","1px solid #a0dd60");
			$(this).parent().parent().find(".reqcls").css("backgroundColor","#e3ffe3");
			$(this).parent().parent().find(".respcls").css("backgroundColor","#e3ffe3");
			$(this).css("backgroundColor","#a0dd60").css("color","#ffffff");
			//$(this).next().css("backgroundColor","#a0dd60");
			//$(this).next().next().css("backgroundColor","#a0dd60");
			$(this).next().next().next().next().next().css("backgroundColor","#a0dd60");
			$(this).parent().hover(function(){
				$(this).css("background-image","linear-gradient(#b3dfa3,#f8f8f8)");
			},function(){
				$(this).css("background-image","linear-gradient(#e3ffc3,#f8f8f8)");
			});
		}
		if($(this).html() == '未知'){
			$(this).parent().css("background-image","linear-gradient(#ffe3c3,#f8f8f8)").css("border-top","1px solid #dd60a0");
			$(this).parent().parent().find(".reqcls").css("backgroundColor","#ffe3e3");
			$(this).parent().parent().find(".respcls").css("backgroundColor","#ffe3e3");
			$(this).css("backgroundColor","#dd60a0").css("color","#ffffff");
			//$(this).next().css("backgroundColor","#dd60a0");
			//$(this).next().next().css("backgroundColor","#dd60a0");
			$(this).next().next().next().next().next().css("backgroundColor","#dd60a0");
			$(this).parent().hover(function(){
				$(this).css("background-image","linear-gradient(#dfa3b3,#f8f8f8)");
			},function(){
				$(this).css("background-image","linear-gradient(#ffe3c3,#f8f8f8)");
			});
		}
	});
	
})


function assembleJson(paramNames,testDatas,dataTypes,paramTypes,type){//参数名称，参数值，参数类型
	var paramJson = {};
	for(var i = 0;i<paramNames.length;i++){ //遍历参数名称
		//判断是否有数据类型
		var dataType;
		var paramType;
		try{
			dataType = dataTypes.eq(i);
			paramType = paramTypes.eq(i);
		}catch{
			dataType = dataTypes[i];
			paramType = paramTypes[i];
		}
		if(dataType.html() != null && paramType.html() == type){ //有数据类型
			var paramName = paramNames[i];
			//判断是否是数组
			if(paramName.indexOf("[]")==-1){ //不是数组
				//判断是否有下一级
				if(paramName.indexOf(".") == -1){ //没有下一级
					if(paramJson.paramName == null){//判断是否设置过数据
						var testData;
						try{
							testData = testDatas.eq(i);
						}catch{
							testData = testDatas[i];
						}
						paramJson[paramName] = testData.val();//设置数据
					}
				}else{ //有下一级
					i=i-1;
					var paramStr = paramName.substring(0,paramName.indexOf("."));
					if(paramJson.paramStr == null){
						var arrParam = new Array();
						var arrTest = new Array();
						var arrData = new Array();
						var arrType = new Array();
						for(var j = 0;j<paramNames.length;j++){
							var td;
							var dt;
							var pt;
							try{
								td = testDatas.eq(j);
								dt = dataTypes.eq(j);
								pt = paramTypes.eq(j);
							}catch{
								td = testDatas[j];
								dt = dataTypes[j];
								pt = paramTypes[j];
							}
							if(dt.html() != null && pt.html() == type){ //有数据类型
								if(paramNames[j].indexOf(".") != -1){
									if(paramNames[j].substring(0,paramNames[j].indexOf("."))==paramStr){
										arrParam.push(paramNames[j].substring(paramNames[j].indexOf(".")+1));
										arrTest.push(td);
										arrData.push(dt);
										arrType.push(pt);
										paramNames.splice(j,1);
										testDatas.splice(j,1);
										dataTypes.splice(j,1);
										paramTypes.splice(j,1);
						    			j--;
									}
								}
							}
						}
						paramJson[paramStr] = assembleJson(arrParam,arrTest,arrData,arrType,type);
					}
				}
			}else{//是数组
				//判断是否有下一级
				if(paramName.indexOf(".") == -1){ //没有下一级
					paramName = paramName.substring(0,paramName.indexOf("[]"));
					if(paramJson.paramName == null){//判断是否设置过数据
						var arr = new Array();
						var td;
						try{
							td = testDatas.eq(i);
						}catch{
							td = testDatas[i];
						}
						arr[0] = td.val();
						var prevDatas = td.nextAll(".prevData");
						for(var m=0;m<prevDatas.length;m++){
							arr[m+1] = prevDatas.eq(m).val();
						}
						paramJson[paramName] = arr;
					}
				}else{ //有下一级
					i=i-1;
					var paramStrs = paramName.substring(0,paramName.indexOf("."));
					paramStr = paramStrs.substring(0,paramStrs.indexOf("[]"));
					if(paramJson.paramStr == null){
						var arrParam = new Array();
						var arrTest = new Array();
						var arrData = new Array();
						var arrType = new Array();
						for(var j = 0;j<paramNames.length;j++){
							var td;
							var dt;
							var pt;
							try{
								td = testDatas.eq(j);
								dt = dataTypes.eq(j);
								pt = paramTypes.eq(j);
							}catch{
								td = testDatas[j];
								dt = dataTypes[j];
								pt = paramTypes[j];
							}
							if(dt.html() != null && pt.html() == type){ //有数据类型
								if(paramNames[j].indexOf(".") != -1){
									if(paramNames[j].substring(0,paramNames[j].indexOf("."))==paramStrs){
										arrParam.push(paramNames[j].substring(paramNames[j].indexOf(".")+1));
										arrTest.push(td);
										arrData.push(dt);
										arrType.push(pt);
										paramNames.splice(j,1);
										testDatas.splice(j,1);
										dataTypes.splice(j,1);
										paramTypes.splice(j,1);
						    			j--;
									}
								}
							}
						}
						var arr = new Array();
						arr[0] = assembleJson(arrParam,arrTest,arrData,arrType,type);
						paramJson[paramStr] = arr;
					}
				}
			}
		}
	}
	return paramJson;
}

//testDatas为参数说明
function assembleJson2(paramNames,testDatas,dataTypes,paramTypes,type){//参数名称，参数值，参数类型
	var paramJson = {};
	for(var i = 0;i<paramNames.length;i++){ //遍历参数名称
		//判断是否有数据类型
		var dataType;
		var paramType;
		try{
			dataType = dataTypes.eq(i);
			paramType = paramTypes.eq(i);
		}catch{
			dataType = dataTypes[i];
			paramType = paramTypes[i];
		}
		if(dataType.html() != null && (type=='resp' || paramType.html() == type)){ //有数据类型
			var paramName = paramNames[i];
			//判断是否是数组
			if(paramName.indexOf("[]")==-1){ //不是数组
				//判断是否有下一级
				if(paramName.indexOf(".") == -1){ //没有下一级
					if(paramJson.paramName == null){//判断是否设置过数据
						var testData;
						try{
							testData = testDatas.eq(i);
						}catch{
							testData = testDatas[i];
						}
						paramJson[paramName] = testData.html();//设置数据
					}
				}else{ //有下一级
					i=i-1;//迭代i回归
					var paramStr = paramName.substring(0,paramName.indexOf("."));
					if(paramJson.paramStr == null){
						var arrParam = new Array();
						var arrTest = new Array();
						var arrData = new Array();
						var arrType = new Array();
						for(var j = 0;j<paramNames.length;j++){
							var td;
							var dt;
							var pt;
							try{
								td = testDatas.eq(j);
								dt = dataTypes.eq(j);
								pt = paramTypes.eq(j);
							}catch{
								td = testDatas[j];
								dt = dataTypes[j];
								pt = paramTypes[j];
							}
							if(dt.html() != null && (type=='resp' || pt.html() == type)){ //有数据类型
								if(paramNames[j].indexOf(".") != -1){
									if(paramNames[j].substring(0,paramNames[j].indexOf("."))==paramStr){
										arrParam.push(paramNames[j].substring(paramNames[j].indexOf(".")+1));
										arrTest.push(td);
										arrData.push(dt);
										arrType.push(pt);
										paramNames.splice(j,1);
										testDatas.splice(j,1);
										dataTypes.splice(j,1);
										paramTypes.splice(j,1);
						    			j--;
									}
								}
							}
						}
						paramJson[paramStr] = assembleJson2(arrParam,arrTest,arrData,arrType,type);
					}
				}
			}else{//是数组
				//判断是否有下一级
				if(paramName.indexOf(".") == -1){ //没有下一级
					paramName = paramName.substring(0,paramName.indexOf("[]"));
					if(paramJson.paramName == null){//判断是否设置过数据
						var arr = new Array();
						var td;
						try{
							td = testDatas.eq(i);
						}catch{
							td = testDatas[i];
						}
						arr[0] = td.html();
						var prevDatas = td.nextAll(".prevData");
						if(prevDatas != null){
							for(var m=0;m<prevDatas.length;m++){
								arr[m+1] = prevDatas.eq(m).val();
							}
						}
						paramJson[paramName] = arr;
					}
				}else{ //有下一级
					i=i-1;//迭代i回归
					var paramStrs = paramName.substring(0,paramName.indexOf("."));
					paramStr = paramStrs.substring(0,paramStrs.indexOf("[]"));
					var bool = true;
					if(paramStr == null || paramStr == ''){
						paramStr = paramStrs;
						bool = false;
					}
					if(paramJson.paramStr == null){
						var arrParam = new Array();
						var arrTest = new Array();
						var arrData = new Array();
						var arrType = new Array();
						for(var j = 0;j<paramNames.length;j++){
							var td;
							var dt;
							var pt;
							try{
								td = testDatas.eq(j);
								dt = dataTypes.eq(j);
								pt = paramTypes.eq(j);
							}catch{
								td = testDatas[j];
								dt = dataTypes[j];
								pt = paramTypes[j];
							}
							if(dt.html() != null && (type=='resp' || pt.html() == type)){ //有数据类型
								if(paramNames[j].indexOf(".") != -1){
									if(paramNames[j].substring(0,paramNames[j].indexOf("."))==paramStrs){
										arrParam.push(paramNames[j].substring(paramNames[j].indexOf(".")+1));
										arrTest.push(td);
										arrData.push(dt);
										arrType.push(pt);
										paramNames.splice(j,1);
										testDatas.splice(j,1);
										dataTypes.splice(j,1);
										paramTypes.splice(j,1);
						    			j--;
									}
								}
							}
						}
						if(bool){
							var arr = new Array();
							arr[0] = assembleJson2(arrParam,arrTest,arrData,arrType,type);
							paramJson[paramStr] = arr;
						}else{
							paramJson[paramStr]= assembleJson2(arrParam,arrTest,arrData,arrType,type);
						}
					}
				}
			}
		}
	}
	return paramJson;
}

function buildMenu(doc) {
	//var str = "<h2><span>"+doc.value+"</span><span>"+doc.name+"</span><span>"+doc.description+"</span><div class='d3'></div></h2>";
	var str = "<h3><span>"+doc.name+"</span><span>"+doc.description+"</span><div class='d3'></div></h3>";
	var methods =doc.methodModels;
    if(methods != null && methods.length>0){
    	str +="<ul hidden='hidden'>"
    	for(var i = 0;i<methods.length;i++){
    		str += "<li class='method-li'><h5><span class='requestType'>"+methods[i].requestType+"</span><span class='requestPath'>"+methods[i].url+"</span><span class='m-contentType'>"+methods[i].contentType+"</span><span class='m-name'>"+methods[i].name+"</span>&nbsp;&nbsp;<span class='m-description'>"+methods[i].description+"</span><span>"+methods[i].version+"</span></h5>";
    		var request = methods[i].request;
    		var respose = methods[i].respose;
    		str +="<div hidden='hidden'>"+buildParams(request,"req","loc_method",1,methods[i].contentType)+"</div>";
    		str +="<div hidden='hidden'>"+buildParams(respose,"resp","loc_method",1)+"</div></li>";
    	}
    	str+="</ul>";
    }
    return str;
}

var radioRandom = 1;

function buildParams(doc,type,loc,flag,contentType){
	radioRandom++;
	var str = "";
	if(loc == "loc_method"){
	 	str = "<table class='hovertable'>";
		if(type=="req" || type=="param"){
			str += "<thead class='reqcls'><tr><td><a class='method-back'>＜＜back</a></td><td colspan='6'>Parameters</td></tr>"
			str += "<tr><td>名称/Name</td><td>作用/Value</td><td>描述/Description</td><td>是否必须/Required</td><td>数据类型/DataType</td><td>参数类型/ParamType</td><td>测试数据/TestData</td></tr>"
			str +="</thead><tbody>"
		}else if(type=="resp"){
			str += "<thead class='respcls'><tr><td colspan='4'>Responses</td></tr>"
			str += "<tr class='resposeDataJson' hidden='hidden'><td colspan='4'></td></tr>"
			str += "<tr class='resposeDataTable'><td>名称/Name</td><td>作用/Value</td><td>描述/Description</td><td>数据类型/DataType</td></tr>"
			str +="</thead><tbody class='resposeDataTable'>"
		}
		
	}
	if(doc != null && doc.length > 0){
		for(var i = 0;i<doc.length;i++){
			var {value,name,description,array,required,dataType,paramType,testData,parentName} = doc[i];
			var arr = new Array();
			filter(value,doc,arr);
			value = flag == 2?(loc+"."+value):flag == 3 && loc.lastIndexOf(".")!=-1?(loc.substring(0,loc.lastIndexOf(".")+1)+value):value;
			//console.log(value);
			if(arr != null && arr.length > 0){
				var val = array != null && (array==true || array=='true')?value+'[]':value;
				if(type=="req" || type=="param"){
					//str+="<tr><td>"+val+"</td><td>"+doc[i].name+"</td><td>"+doc[i].description+"</td><td>"+doc[i].required+"</td><td>"+doc[i].dataType+"</td><td colspan='2'>"+doc[i].paramType+"</td></tr>"
					str+=buildParams(arr,"param",val,2,contentType);
				}else{
					str+="<tr class='parentParam'><td>"+val+"</td><td>"+name+"</td><td>"+description+"</td><td></td></tr>"
					str+=buildParams(arr,"resp",val,2);
				}
			}else{
				var model =doc[i].modelModel;
				if(model != null && model != "null"){
					var val = array != null && (array==true || array=='true')?value+'[]':value;
					if(type=="req" || type=="param"){
						//str+="<tr><td>"+val+"</td><td>"+doc[i].name+"</td><td>"+doc[i].description+"</td><td>"+doc[i].required+"</td><td>"+doc[i].dataType+"</td><td>"+doc[i].paramType+"</td></tr>"								
						str+=buildParams(model.propertyModels,"params",val,3,contentType);
					}else if(type=="resp"){
						//str+="<tr><td>"+val+"</td><td>"+doc[i].name+"</td><td>"+doc[i].description+"</td><td>"+doc[i].dataType+"</td></tr>"
						str+=buildParams(model.propertyModels,"resps",val,3);
					}
					if(type=="params"){
						str+="<tr class='parentParam'><td>"+val+"</td><td>"+name+"</td><td>"+description+"</td><td></td><td></td><td colspan='2'></td></tr>"								
						str+=buildParams(model.propertyModels,"params",val,2,contentType);
					}else if(type=="resps"){
						str+="<tr class='parentParam'><td>"+val+"</td><td>"+name+"</td><td>"+description+"</td><td></td></tr>"
						str+=buildParams(model.propertyModels,"resps",val,2);
					}
				}else{
					var val = array != null && (array==true || array=='true')?value+'[]':value;
					/*if(type=="req" || type=="param" || type=="params"){
						str+="<tr><td class='paramValue'>"+val+"</td><td class='paramInfo'>"+name+"</td><td>"+description+"</td><td class='isRequired'>"+
						(required==true?'yes':'no')+"</td><td class='dataType'>"+
						dataType+"</td><td class='paramType'>"+
						paramType+"</td><td><input class='testData' type='text' value='"+
						testData+"'>"+(dataType==null?"":dataType.indexOf('[]')==-1?"":"<input type='button' class='subtract' value='-'><input type='button' class='add' value='+'>")+"</td></tr>"
					}else{
						str+="<tr><td class='respValue'>"+val+"</td><td class='respInfo'>"+name+"</td><td>"+description+"</td><td class='respType'>"+dataType+"</td></tr>"
					}*/
					if(type=="req" || type=="param" || type=="params"){
						str+="<tr><td class='paramValue addinfo' title='双击可添加参数修改信息'>"+val+"</td><td class='paramInfo'>"+name+"</td><td>"+description+"</td><td class='isRequired'>"+
						(required==true?'yes':'no')+"</td><td class='dataType'>"+
						dataType+"</td><td class='paramType'>"+
						paramType+"</td><td>"+
						(dataType=='file'?"<form class='upload' enctype='multipart/form-data'>"+
						"<input type='file' name='"+value+"'>"+"</form>":"<input class='testData' type='text' value='"+testData+"'>"+
						(dataType==null?"":dataType.indexOf('[]')==-1?"":"<input type='button' class='subtract' value='-'><input type='button' class='add' value='+'>")+"</td></tr>")
					}else{
						str+="<tr><td class='respValue addinfo' title='双击可添加参数修改信息'>"+val+"</td><td class='respInfo'>"+name+"</td><td>"+description+"</td><td class='respType'>"+dataType+"</td></tr>"
					}
				}
			}
		}
		if(loc == "loc_method"){
			if(type=="req" || type=="param"){
				str+="<tr><td colspan='7' class='requestData' hidden='hidden'></td></tr>"
				str+="<tr class='testSend'><td colspan='7'>"+
				"<input type='button' class='testSendButton' value='测试API请求'>"+
				"<label><input type='radio' name='conType"+radioRandom+"' class='app-form' "+(contentType=='application/x-www-form-urlencoded'?"checked='checked'":"")+" value='application/x-www-form-urlencoded'>application/x-www-form-urlencoded</label>&nbsp;&nbsp;"+
				"<label><input type='radio' name='conType"+radioRandom+"' class='app-json' "+(contentType=='application/json'?"checked='checked'":"")+" value='application/json'>application/json</label>&nbsp;&nbsp;"+
				"<label><input type='checkbox' class='app-traditional' value='1'>阻止深度序列化</label>&nbsp;&nbsp;"+
				"<input type='button' class='request-json' value='树状展示请求参数'>&nbsp;&nbsp;"+
				"<input type='button' class='switch-resp-json' value='树状展示响应内容'>"+
				"</td></tr>"
				str+="<tr><td colspan='7' class='resposeData' hidden='hidden'></td></tr>"
				str+="<tr hidden='hidden'><td colspan='7' class='close-resposeData' align='center'><a style='font-size:24'>▲</a></td></tr>"
			}
		}
	}else if(loc == "loc_method"){
		if(type=="req" || type=="param"){
			str+="<tr><td colspan='7' style='color:red'>该API没有设置请求参数相关注解</td></tr>"
				str+="<tr class='testSend'><td colspan='7'>"+
				"<input type='button' class='testSendButton' value='测试API请求'>"+
				"<label><input type='radio' name='conType"+radioRandom+"' class='app-form' "+(contentType=='application/x-www-form-urlencoded'?"checked='checked'":"")+" value='application/x-www-form-urlencoded'>application/x-www-form-urlencoded</label>&nbsp;&nbsp;"+
				"<label><input type='radio' name='conType"+radioRandom+"' class='app-json' "+(contentType=='application/json'?"checked='checked'":"")+" value='application/json'>application/json</label>&nbsp;&nbsp;"+
				"<label><input type='checkbox' class='app-traditional' value='1'>阻止深度序列化</label>&nbsp;&nbsp;"+
				"<input type='button' class='request-json' value='树状展示请求参数'>&nbsp;&nbsp;"+
				"<input type='button' class='switch-resp-json' value='树状展示响应内容'>"+
				"</td></tr>"
				str+="<tr><td colspan='7' class='resposeData' hidden='hidden'></td></tr>"
				str+="<tr hidden='hidden'><td colspan='7' class='close-resposeData' align='center'><a style='font-size:24'>▲</a></td></tr>"
		}else{
			str+="<tr><td colspan='4' style='color:red'>该API没有设置响应参数相关注解</td></tr>"
		}
	}
	if(loc == "loc_method"){
		str += "</tbody></table>";
	}
	return str;
}

function filter(value,doc,arr){
	if(doc == null || doc.length == null || doc.length < 1 || value == null || value == ''){
		return;
	}
	for(var k = 0;k<doc.length;k++){
		if(value == doc[k].parentName){
			var val = doc[k].value;
			arr.push(doc[k]);
			doc.splice(k,1);
			k--;
			filter(val,doc,arr);
		}
	}
} 








$(function(){
	var canvas = document.getElementById('canvas'), 
	  ctx = canvas.getContext('2d'), 
	  w = canvas.width = window.innerWidth, 
	  h = canvas.height = 170,
	 
	  hue = 217, 
	  stars = [], 
	  count = 0, 
	  maxStars = 1300;//星星数量 
	 
	var canvas2 = document.createElement('canvas'), 
	  ctx2 = canvas2.getContext('2d'); 
	canvas2.width = 100; 
	canvas2.height = 100; 
	var half = canvas2.width / 2, 
	  gradient2 = ctx2.createRadialGradient(half, half, 0, half, half, half); 
	gradient2.addColorStop(0.025, '#CCC'); 
	gradient2.addColorStop(0.1, 'hsl(' + hue + ', 61%, 33%)'); 
	gradient2.addColorStop(0.25, 'hsl(' + hue + ', 64%, 6%)'); 
	gradient2.addColorStop(1, 'transparent'); 
	 
	ctx2.fillStyle = gradient2; 
	ctx2.beginPath(); 
	ctx2.arc(half, half, half, 0, Math.PI * 2); 
	ctx2.fill(); 
	 
	// End cache 
	 
	function random(min, max) { 
	  if (arguments.length < 2) { 
	    max = min; 
	    min = 0; 
	  } 
	 
	  if (min > max) { 
	    var hold = max; 
	    max = min; 
	    min = hold; 
	  } 
	 
	  return Math.floor(Math.random() * (max - min + 1)) + min; 
	} 
	 
	function maxOrbit(x, y) { 
	  var max = Math.max(x, y), 
	    diameter = Math.round(Math.sqrt(max * max + max * max)); 
	  return diameter / 2; 
	  //星星移动范围，值越大范围越小， 
	} 
	 
	var Star = function() { 
	 
	  this.orbitRadius = random(maxOrbit(w, h)); 
	  this.radius = random(60, this.orbitRadius) / 8;  
	  //星星大小 
	  this.orbitX = w / 2; 
	  this.orbitY = h / 2; 
	  this.timePassed = random(0, maxStars); 
	  this.speed = random(this.orbitRadius) / 50000;  
	  //星星移动速度 
	  this.alpha = random(2, 10) / 10; 
	 
	  count++; 
	  stars[count] = this; 
	} 
	 
	Star.prototype.draw = function() { 
	  var x = Math.sin(this.timePassed) * this.orbitRadius + this.orbitX, 
	    y = Math.cos(this.timePassed) * this.orbitRadius + this.orbitY, 
	    twinkle = random(10); 
	 
	  if (twinkle === 1 && this.alpha > 0) { 
	    this.alpha -= 0.05; 
	  } else if (twinkle === 2 && this.alpha < 1) { 
	    this.alpha += 0.05; 
	  } 
	 
	  ctx.globalAlpha = this.alpha; 
	  ctx.drawImage(canvas2, x - this.radius / 2, y - this.radius / 2, this.radius, this.radius); 
	  this.timePassed += this.speed; 
	} 
	 
	for (var i = 0; i < maxStars; i++) { 
	  new Star(); 
	} 
	 
	function animation() { 
	  ctx.globalCompositeOperation = 'source-over'; 
	  ctx.globalAlpha = 0.5; //尾巴 
	  ctx.fillStyle = 'hsla(' + hue + ', 64%, 6%, 2)'; 
	  ctx.fillRect(0, 0, w, h) 
	 
	  ctx.globalCompositeOperation = 'lighter'; 
	  for (var i = 1, l = stars.length; i < l; i++) { 
	    stars[i].draw(); 
	  }; 
	 
	  window.requestAnimationFrame(animation); 
	} 
	 
	animation();
})


// 导出成PDF
function getmes() {
	$("*:not(.resposeData,.requestData,.resposeDataJson,#simpleModal)").show();
	html2canvas(document.getElementById("box-pdf"),// 为页面内容所在元素的ID
		{
			dpi: 300, // 导出pdf清晰度
			onrendered: function (canvas) {
				var contentWidth = canvas.width;
				var contentHeight = canvas.height;
				// 一页pdf显示html页面生成的canvas高度;
				var pageHeight = contentWidth / 592.28 * 841.89;
				// 未生成pdf的html页面高度
				var leftHeight = contentHeight;
				// pdf页面偏移
				var position = 0;
				// html页面生成的canvas在pdf中图片的宽高（a4纸的尺寸[595.28,841.89]）
				var imgWidth = 595.28;
				var imgHeight = 592.28 / contentWidth * contentHeight;
				var pageData = canvas.toDataURL('image/jpeg', 1.0);
				var pdf = new jsPDF('', 'pt', 'a4');
				// 有两个高度需要区分，一个是html页面的实际高度，和生成pdf的页面高度(841.89)
				// 当内容未超过pdf一页显示的范围，无需分页
				if (leftHeight < pageHeight) {
					pdf.addImage(pageData, 'JPEG', 0, 0, imgWidth, imgHeight);
				} else {
					while (leftHeight > 0) {
						pdf.addImage(pageData, 'JPEG', 0, position, imgWidth, imgHeight)
						leftHeight -= pageHeight;
						position -= 841.89;
						// 避免添加空白页
						if (leftHeight > 0) {
							pdf.addPage();
						}
					}
				}
				pdf.save($("#projectName").html()+'.pdf');
			},
			// 背景设为白色（默认为黑色）
			background: "#fff"
		})
}