
function changeStyle(sel){
	css=document.getElementById("cssfile");
	css.href='/css/'+sel.value+'.css';
	localStorage.setItem('styleName',sel.value);
}

//点击左边菜单栏展示相对应内容
function leftMenu(){
	
	setTimeout(()=>{
		let uls = $('.secondary')
		
		for(let i = 0; i < uls.length;i++){
			
			
			uls[i].onclick=function(){
				
				indexFlag =this.getAttribute("data")
				

					let flag = true
					let tabBox = $('#tabBox>li')
					for(let m = 0; m < tabBox.length;m++){
						tabBox[m].className=''
						if(indexFlag == tabBox[m].getAttribute("data")){
							flag = false
							tabBox[m].className='activeTab'
						}
						
					}
					if(flag){
						const urlname = this.getAttribute("urlname")
						let lis = `<li data="${indexFlag}" class='activeTab'>
									<span>${urlname}</span>
									<img src ='../img/cuowu.png' data="${indexFlag}" class='delImg' >
								   </li>`
						$('#tabBox').append(lis);
						topMenu()
						delMenu()
					}

				
				
				
				for(let j = 0; j < uls.length;j++){
					uls[j].className = 'secondary'
					//$(uls[j]).find("img").attr("src","img/f.gif")
				}
				this.className = 'secondary active'
				//$(this).find("img").attr("src","img/fw.gif")
				let ids = '#method_'+indexFlag
				let divs = $(ids)[0].getElementsByTagName('div')[0]
				  
				divs.style.left = oldLeft  + 'px'
				
			}
		}
	},0)
}

//点击顶部菜单栏展示内容
function topMenu(){
	
	let addTabBox = $('#tabBox>li')
	for(let i = 0; i < addTabBox.length;i++){
		addTabBox[i].onclick=function(){
			
			indexFlag = this.getAttribute("data")
			/*if(indexFlag == 0) return*/
			switchMenu(addTabBox)
		}
	}
}

//顶部菜单栏点击删除事件
function delMenu(){
	let uls = $('.delImg')
	let tabBoxs =  $('#tabBox>li')
	for(let i = 0; i < uls.length;i++){
		uls[i].onclick = function(event){
			let removeId = this.getAttribute("data")
			let tag = tabBoxs[i]
			
			if(uls.length==1) return //如果只有一项，不允许删除
			//如果当前删除的选项和页面显示的选项一致
			if(removeId==indexFlag){
				if(i==tabBoxs.length-1){//当前删除项是最后一项
					indexFlag = tabBoxs[i-1].getAttribute("data")
				}else{
					indexFlag = tabBoxs[i+1].getAttribute("data")
				}
				switchMenu(tabBoxs)
			}
			tag.parentNode.removeChild(tag);
			delMenu()
			event.stopPropagation();//阻止点击事件冒泡
			return false;
		}
	}
}
//切换菜单事件
function switchMenu(tabs){
	let uls = $('.secondary')
	let ids = '#method_'+indexFlag
	for(let j = 0; j < uls.length;j++){
		if(indexFlag==uls[j].getAttribute("data")){
			uls[j].className = 'secondary active'
			let divs = $(ids)[0].getElementsByTagName('div')[0]
			divs.style.left = oldLeft  + 'px'
		}else{
			uls[j].className = 'secondary'
		}
	}
	for(let n = 0; n < tabs.length;n++){
		if(indexFlag==tabs[n].getAttribute("data")){
			tabs[n].className = 'activeTab'
		}else{
			tabs[n].className = ''
		}
		
	}
	
	$(".method-table").hide();
	$(ids).show()
} 

//复制属性
function copyVal(btn){
	var a = btn.parentNode.firstChild;
	var textArea = document.createElement('textarea');
	var text = a.nodeValue;
	if(text.lastIndexOf('.') != -1){
		text = text.substring(text.lastIndexOf('.')+1);
	}
	textArea.value = text;
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

let indexFlag = null
let oldWidth = 340
let oldLeft = 365
function bindResize(el) {
	
	  //初始化参数   
	let menuBody = document.getElementById('menu')
	
	  var els = el.style,
	    //鼠标的 X 和 Y 轴坐标   
	    x = y = 0;
	  //邪恶的食指   
	  $(el).mousedown(function (e) {
	    //按下元素后，计算当前鼠标与对象计算后的坐标  
	    x = e.clientX - el.offsetWidth, y = e.clientY - el.offsetHeight;
	    //在支持 setCapture 做些东东  
	    el.setCapture ? (
	      //捕捉焦点   
	      el.setCapture(),
	      //设置事件   
	      el.onmousemove = function (ev) {
	        mouseMove(ev || event)
	      },
	      el.onmouseup = mouseUp
	    ) : (
	      //绑定事件   
	      $(document).bind("mousemove", mouseMove).bind("mouseup", mouseUp)
	    )
	    //防止默认事件发生   
	    e.preventDefault()
	  });
	  //移动事件 
	  let oldFeft_s = 0
	  let oldWidth_s =0
	  let minWidth = 235
	  let maxWidth = 755
	  function mouseMove(e) {		
		  oldFeft_s   = oldLeft+ e.clientX - x
		  
		  oldFeft_s= oldFeft_s< (minWidth+25) ? (minWidth+25) : (oldFeft_s > (maxWidth+25) ? (maxWidth+25) : oldFeft_s)
		  if(indexFlag != null){
			
			let ids = '#method_'+indexFlag
			let divs = $(ids)[0].getElementsByTagName('div')[0]
			divs.style.left = oldFeft_s  + 'px'
			$('#tabBox')[0].style.left = oldFeft_s  + 'px'
		  }
	    
		  oldWidth_s = oldWidth+ e.clientX - x
		 
		  oldWidth_s=oldWidth_s<minWidth?minWidth:(oldWidth_s>maxWidth?maxWidth:oldWidth_s)
		  console.log('左位移距离：'+oldFeft_s)
		  console.log('宽度：'+oldWidth_s)
		  menuBody.style.width = oldWidth_s+ 'px' //改变宽度
//	      els.height = e.clientY - y + 'px' //改变高度 
	  }
	  //停止事件   
	  function mouseUp() {
	    //在支持 releaseCapture 做些东东   
	    el.releaseCapture ? (
	      //释放焦点   
	      el.releaseCapture(),
	      //移除事件   
	      el.onmousemove = el.onmouseup = null
	    ) : (
	      //卸载事件   
	      $(document).unbind("mousemove", mouseMove).unbind("mouseup", mouseUp)
	    )
	    oldLeft = oldFeft_s
	    console.log(oldLeft)
	    oldWidth = oldWidth_s
	  }
	}   
$(function(){
	//加载风格
	css=document.getElementById("cssfile");
	var styleName = localStorage.getItem('styleName');
	if(styleName == null){
		css.href='/css/green.css';
		$("#changeStyle").find("option").eq(0).prop("selected",true)
	}else{
		css.href='/css/'+styleName+'.css';
		if(styleName == 'black'){
			$("#changeStyle").find("option").eq(1).prop("selected",true)
		}
		if(styleName == 'red'){
			$("#changeStyle").find("option").eq(2).prop("selected",true)
		}
		if(styleName == 'blue'){
			$("#changeStyle").find("option").eq(3).prop("selected",true)
		}
		if(styleName == 'default'){
			$("#changeStyle").find("option").eq(4).prop("selected",true)
		}
	}
	
	
	setTimeout(()=>{
		bindResize(document.getElementById('line'));
	})
	
	function reloadDoc(serverName){
		$.ajax({
		    url:"/lkad/doc",
		    type:"get",
		    dataType:"json",
		    async:false,
		    data:{"serverName":serverName},
		    success:function(data){
		    	$(".navBox").html('');
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
							var tVersion = data.version;//获取总版本号
							if(serverName == '' && $("#changeProject").val() != 'now'){
								if(data.serverNames == null || data.serverNames == ''){
									$("#changeProject").append('<option value="now">当前项目</option>');
								}else{
									$("#changeProject").append('<option value="now">当前项目</option>');
									var ips = data.serverNames.split(",");
									if(ips.length > 0){
										for(var i = 0;i<ips.length;i++){
											var arrips = ips[i].split("-");
											if(arrips.length < 2){
												$("#changeProject").append('<option value="'+ips[i]+'">'+ips[i]+'</option>');
											}else{
												$("#changeProject").append('<option value="'+arrips[1]+'">'+arrips[0]+'</option>');
											}
										}
									}
								}
							}
							var doc = data.apiDoc;
							if(doc != null && doc.length > 0){
								for(var i = 0;i<doc.length;i++){
									$(".navBox").append(buildMenu(doc[i],tVersion));
								}
								
								leftMenu()
								
							}
		    			}
		    		}
				}
		    },
		    error:function(respose){
		    	alert("status："+respose.status+"\nstatusText："+respose.statusText+"\nmessage："+respose.responseJSON.message);
		    }
		});
		
		$(".isRequired").each(function(){
		if($(this).html() == 'yes'){
			$(this).css("color","#f00");
		}
		})
		
		
		$(".paramType").each(function(){
			if($(this).html() == 'header'){
				$(this).css("color","#f00");
			}
			if($(this).html() == 'path'){
				$(this).css("color","#0f0");
			}
		})
		
		$(".method-URL").each(function(){
			if($(this).html() == '该API未设置请求路径'){
				$(this).css("color","#f00");
			}
		})
	}
	
	function getServerName(){
		var serverName = $("#changeProject").val();
		if(serverName == null || serverName == 'now'){
			serverName = '';
		}else{
			if(!serverName.toLowerCase().startsWith("http://")){
				serverName = "http://"+serverName;
			}
		}
		return serverName;
	}

	$("#changeProject").change(function(){
		reloadDoc(getServerName());
		indexFlag=null
		$(".method-table").hide();
		$(".welcome").show();
	});
	
	reloadDoc('');
	
	//导出PDF，MD文档
	$("#exportPdf").click(function(){
		if($("#changeProject").val() != 'now' && $("#changeProject").val()!=null){
			alert("目前只支持生成本地项目PDF、MD接口文档，暂不支持远程服务器生成文档！");
		}else{
			var exType = $("#exportDoc").val();
			debugger;
			if(exType != 1 && exType != 2){
				return;
			}
			var xhr = new XMLHttpRequest();
			if(exType == 1){
				xhr.open("post","/lkad/exportPdf",true);
			}
			if(exType == 2){
				xhr.open("post","/lkad/exportMarkDown",true);
			}
			// 设置请求头
			xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
			xhr.responseType = "blob";
			xhr.onreadystatechange = function () {
				// 请求完成
			if (this.status === 200) {
				var blob = this.response;
				var reader = new FileReader();
				// 转换为base64，可以直接放入a表情href
			reader.readAsDataURL(blob);
			reader.onload = function (e) {
				// 转换完成，创建一个a标签用于下载
			var a = document.createElement('a');
			if(exType == 1){
				a.download = $("#projectName").html()+".pdf";
			}
			if(exType == 2){
				a.download = $("#projectName").html()+".md";
			}
			a.href = e.target.result;
			// 修复firefox中无法触发click
			$("body").append(a);  
					a.click();
					$(a).remove();
			    }
			}
			console.log(xhr);
			if(this.readyState == 4){
				if(this.status == 200){
					alert("生成成功！")
			}else{
				alert("生成失败！");
						
					}
				}
			};
			console.log(getServerName())
			//发送ajax请求
			xhr.send("serverName="+getServerName())
		}
	})
	
	//生成复制按钮 复制属性名
	$(".right-box").on("mouseenter",".addinfo",function(){
		$(this).append('<input type="button" class="copyText" onclick="copyVal(this)" value="复制">');
	})
	$(".right-box").on("mouseleave",".addinfo",function(){
		$(this).find(".copyText").remove();
	})
	
	//生成复制按钮 复制属性名
	$(".right-box").on("mouseenter",".method-requestParamInfo",function(){
		$(this).find(".method-URL").after('<input type="button" class="copyText" onclick="copyUrl(this)" value="复制">');
	})
	$(".right-box").on("mouseleave",".method-requestParamInfo",function(){
		$(this).find(".copyText").remove();
	})
	
	//设置高亮参数判断
	function getParamInfo(){
		$.getJSON("/lkad/getParamInfo",{'random':Math.random(),'serverName':getServerName()},function(data){
			if(data != null && data != 'null'){
				$(".addinfo").each(function(){
					try{
						let value = this.firstChild.nodeValue;
						let type = $(this).parents(".hovertable").find(".reqcls").length > 0 ?1:2;
						let methodurl = $(this).parents(".hovertable").parent().parent().find(".method-URL").html();
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
								$(this).attr("title",'双击可添加参数标签信息');
							}
						}else{
							$(this).css("color","#000").css("text-decoration","none");
							$(this).attr("title",'双击可添加参数标签信息');
						}
					}catch(e){
						return false;
					}
				})
			}
		});
	}
	getParamInfo();
	
	//双击属性弹出窗体
	$(".right-box").off('dblclick','.addinfo');
	$(".right-box").on("dblclick",".addinfo",function(){
		var tit = $(this).attr("title");
		//获取要保存的属性
		//let value = $(this).html();
		let value = this.firstChild.nodeValue;
		let type = $(this).parents(".hovertable").find(".reqcls").length > 0 ?1:2;
		let methodurl = $(this).parents(".hovertable").parent().parent().find(".method-URL").html();
		let content = methodurl+"."+type+"."+value;
		//console.log(content);
		if(tit != '双击可添加参数标签信息'){
			var bool = confirm("您确定要删除参数标签信息吗？");
			if(bool){
				$.ajax({
					url:"/lkad/delParamInfo",
				    type:"post",
				    dataType:"text",
				    data:{"value":value,"type":type,"url":methodurl,'random':Math.random(),'serverName':getServerName()},
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
		    	let modalcontent = $("#modaltext").val();
		    	if(modaltype == 5){
			    	if(value.lastIndexOf('.') != -1){
			    		value = value.substring(value.lastIndexOf('.')+1);
			    	}
			    	console.log(value);
			    	if(value.lastIndexOf('[]') != -1){
			    		value = value.substring(0,value.lastIndexOf('[]'));
			    	}
			    	console.log(value);
		    	}
				$('#modalconfirm').off(); //解除所有绑定事件
				modal.style.display = "none";
				$.ajax({
					url:"/lkad/addParamInfo",
				    type:"post",
				    dataType:"text",
				    data:{"value":value,"type":type,"url":methodurl,"modaltype":modaltype,"content":modalcontent,'random':Math.random(),'serverName':getServerName()},
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
	
	
	/*************************接口标签设置***************************/
	//设置高亮参数判断
	function getParamInfo2(){
		$.getJSON("/lkad/getParamInfo",{'random':Math.random(),'serverName':getServerName()},function(data){
			if(data != null && data != 'null'){
				$(".secondary").each(function(){
					try{
						let value = $(this).find("input").val();
						let type = 3;
						let methodurl = "method-api";
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
								$(this).attr("title",'双击可添加接口标签信息');
							}
						}else{
							$(this).css("color","#000").css("text-decoration","none");
							$(this).attr("title",'双击可添加接口标签信息');
						}
					}catch(e){
						return false;
					}
				})
			}
		});
	}
	getParamInfo2();
	
	//双击属性弹出窗体
	$(".navBox").off("dblclick",".secondary");
	$(".navBox").on("dblclick",".secondary",function(){
		var tit = $(this).attr("title");
		//获取要保存的属性
		//let value = $(this).html();
		let value = $(this).find("input").val();
		let type = 3;
		let methodurl = "method-api";
		let content = methodurl+"."+type+"."+value;
		//console.log(content);
		if(tit != '双击可添加接口标签信息'){
			var bool = confirm("您确定要删除接口标签信息吗？");
			if(bool){
				$.ajax({
					url:"/lkad/delParamInfo",
				    type:"post",
				    dataType:"text",
				    data:{"value":value,"type":type,"url":methodurl,'random':Math.random(),'serverName':getServerName()},
				    success:function(data){
				    	getParamInfo2();
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
		    	let modalcontent = $("#modaltext").val();
				$('#modalconfirm').off(); //解除所有绑定事件
				modal.style.display = "none";
				$.ajax({
					url:"/lkad/addParamInfo",
				    type:"post",
				    dataType:"text",
				    data:{"value":value,"type":type,"url":methodurl,"modaltype":modaltype,"content":modalcontent,'random':Math.random(),'serverName':getServerName()},
				    success:function(data){
				    	getParamInfo2();
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
	/****************************************************************/
	
	
	// 加载令牌
	$(".headerKey").val($.cookie('tokenKey'));
	$(".headerValue").val($.cookie('tokenValue'));
	
	
	$(".navBox").on("click","h3",function(){
		$(this).next().toggle()
		//$(".method-table").hide();
		//$(".welcome").show();
		if($(this).find("div").attr('class')=='d4'){
			$(this).find("div").removeClass("d4");
			$(this).find("div").addClass("d3");
		}else{
			$(this).find("div").removeClass("d3");
			$(this).find("div").addClass("d4");
		}
		
		let uls = $('.secondary')
		for(let i = 0; i < uls.length;i++){
			if(i != indexFlag - 1){
				uls[i].className = 'secondary'
			}
			
		}
	})
	
	$(".navBox").on("click","li",function(){
		var index = $(this).attr("data");
		$(".method-table").hide();
		$(".welcome").hide();
		$("#method_"+index).toggle();
		var rightH = $(".right-box").height();
		$("#menu").height(rightH+20);
		$("#menu #open").height(rightH+10);
	})
	
	$(".right-box").on("click",".add",function(){
		$(this).prev().before("<input class='prevData' type='text'/>");
	})
	
	$(".right-box").on("click",".subtract",function(){
		if($(this).prev().attr('class')=='prevData'){
			$(this).prev().remove();
		}
	})
	
	$(".right-box").on("click",".addFile",function(){
		var value = $(this).parent().find(".fileValue").val()
		$(this).prev().before("<input class='prevFileData' type='file' name='"+value+"'/>");
	})
	
	$(".right-box").on("click",".subFile",function(){
		if($(this).prev().attr('class')=='prevFileData'){
			$(this).prev().remove();
		}
	})
	
	$(".right-box").on("click",".close-resposeData a",function(){
		if($(this).html() == '隐藏调试窗口'){
			//$(this).parent().parent().hide();
			$(this).html('打开调试窗口');
			$(this).parents("table").find(".resposeData").hide();
		}else{
			$(this).html('隐藏调试窗口');
			$(this).parents("table").find(".resposeData").show();
		}
	})
	
	$(".right-box").on("change",".ImplementWay",function(){
		if($(this).val() == 'async'){
			$(this).parent().find('.timeNumber').attr("disabled",true).attr("type","text");
			$(this).parent().find('.timeNumber').css("backgroundColor","#ccc")
			$(this).parent().find('.timeNumber').val(1);
		}else{
			$(this).parent().find('.timeNumber').attr("disabled",false).attr("type","number");
			$(this).parent().find('.timeNumber').css("backgroundColor","#fff")
		}
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
    
	// 请求参数json展示
    $(".right-box").on("click",".request-json",function (){
    	$(this).parents("table").find(".requestData").toggle();
		// 获取请求参数名称
		var paramValues = $(this).parents("table").find(".paramValue");
		// 获取请求参数位置
		var paramTypes = $(this).parents("table").find(".paramType");
		// 获取请求测试数据
		var testDatas = $(this).parents("table").find(".testData");
		// 获取是否必须
		var isRequireds = $(this).parents("table").find(".isRequired");
		// 获取数据类型
		var dataTypes = $(this).parents("table").find(".dataType");
		// 获取参数说明
		var paramInfos = $(this).parents("table").find(".paramInfo");
		
		var queryJson = {};
		var paramNames = new Array();
		for(var i = 0;i<paramValues.length;i++){
			paramNames.push(paramValues.eq(i).html());
		}
		// 带参数说明的json对象
		var queryJson = assembleJson3(paramNames,testDatas,dataTypes,paramTypes,"query");
		// console.log(queryJson);
		// var headerJson =
		// assembleJson(paramNames,testDatas,dataTypes,paramTypes,"header");
		// var pathJson =
		// assembleJson(paramNames,testDatas,dataTypes,paramTypes,"path");
		var options = {
    			collapsed:false,
    			withQuotes:false
    	}
		$(this).parents("table").find(".requestData").jsonViewer(queryJson,options);
    })
    // 响应结果json展示
    $(".right-box").on("click",".switch-resp-json",function (){
    	var resposeDataJson = $(this).parents("table").parent().next().find("table").find(".resposeDataJson");
		var resposeDataTable = $(this).parents("table").parent().next().find("table").find(".resposeDataTable");
    	if($(this).val()=='表格展示响应参数'){
    		resposeDataTable.show();
    		resposeDataJson.hide();
    		$(this).val('树状展示响应参数');
    	}else if($(this).val()=='树状展示响应参数'){
    		resposeDataTable.hide();
    		resposeDataJson.show();
    		$(this).val('表格展示响应参数');
    		// 获取请求参数名称
			var respValues = $(this).parents("table").parent().next().find("table").find(".respValue");
			// 获取参数说明
			var respInfos = $(this).parents("table").parent().next().find("table").find(".respInfo");
			// 获取数据类型
			var dataTypes = $(this).parents("table").parent().next().find("table").find(".respType");
			// 获取数据类型
			var respTypes = $(this).parents("table").parent().next().find("table").find(".respType");
			
			var queryJson = {};
			var respNames = new Array();
			for(var i = 0;i<respValues.length;i++){
				respNames.push(respValues.eq(i).html());
			}
			// 带参数说明的json对象
			var respJson = assembleJson2(respNames,respInfos,dataTypes,respTypes,"resp");
			// var headerJson =
			// assembleJson(paramNames,testDatas,dataTypes,paramTypes,"header");
			// var pathJson =
			// assembleJson(paramNames,testDatas,dataTypes,paramTypes,"path");
			//console.log(JSON.stringify(respJson))
			var options = {
	    			collapsed:false,
	    			withQuotes:false
	    	}
			resposeDataJson.find("td").jsonViewer(respJson,options);
    	}
    })
    
	
	// 接口测试
	$(".right-box").on("click",".testSendButton",function(){
		$(this).parents("table").find(".resposeData").html("");
		$(this).parents("table").find(".resposeData").show();
		$(this).parents("table").find(".close-resposeData").parent().show();
		$(this).parents("table").find(".close-resposeData").find("a").html("隐藏调试窗口");
		// 获取请求方式
		var methodType = $(this).parents("table").parent().parent().find(".method-requestType").html();
		//获取是否需要令牌授权
		var token = $(this).parents("table").parent().parent().find(".method-token").html();
		// 获取请求路径
		var path = $(this).parents("table").parent().parent().find(".method-URL").html();
		//获取是否是下载方法
		var download = $(this).parents("table").parent().parent().find(".method-download").val();
		var fileName='fileName.xls';
		if(download == 'true' && (getServerName()==null || getServerName()=='')){
			fileName=window.prompt("请输入要下载的文件名称：","fileName.xls");
		}
		//contentType
		var contentType =  $(this).parents("table").parent().parent().find(".content-TYPE").html();
		// 获取请求参数名称
		var paramValues = $(this).parents("table").find(".paramValue");
		// 获取请求参数位置
		var paramTypes = $(this).parents("table").find(".paramType");
		// 获取请求测试数据
		var testDatas = $(this).parents("table").find(".testData");
		// 获取是否必须
		var isRequireds = $(this).parents("table").find(".isRequired");
		// 获取数据类型
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
		// rest风格url参数设置
		for (var val in pathJson) {
			path = path.replace('{'+val+'}',pathJson[val]);
		}
		// 请求头参数
		if(contentType != null && contentType !=""){
			headerJson['Content-Type']=contentType;
		}else{
			headerJson['Content-Type']="application/x-www-form-urlencoded";
		}
		// 请求头令牌设置
		if(token == 'authorize token'){
			var tokenKey = $(".headerKey").val();
			var tokenValue = $(".headerValue").val();
			if(tokenKey != null && tokenValue != null && tokenKey != "" && tokenValue != ""){
				headerJson[tokenKey] = tokenValue;
			} 
		}
		
		var resposeData = $(this).parents("table").find(".resposeData");
		
		// 是否阻止深度序列化
		var tl = false;
		if($(this).parents("table").find(".app-traditional").is(':checked')){
			tl = true;
		}
		
		var queryData = contentType=="application/json"?JSON.stringify(queryJson):queryJson;
		if(getServerName()!=null && !getServerName()==''){
			queryData = queryJson;
		}
		var fileInput = $(this).parents("table").find(".upload");
		var processData = true;
		var contentTypeBool = true;
		if(fileInput != null && fileInput.length > 0){
			var formData = new FormData(fileInput[0]);
			console.log(queryData)
			Object.keys(queryData).forEach((key) => {
				formData.append(key, queryData[key]);
			});
			queryData = formData;
			processData=false;   // jQuery不要去处理发送的数据
			contentTypeBool=false;   // jQuery不要去设置Content-Type请求头
			path = path+"?random="+Math.random();
			delete headerJson['Content-Type'];
		}
    	var requestFail=$(this).parents("table").find(".request-fail");
    	var requestSuccess=$(this).parents("table").find(".request-success");
		var requestStatus = $(this).parents("table").find(".request-status");
		var requestTime = $(this).parents("table").find(".request-time");
		var conutNumber = $(this).parents("table").find(".conutNumber");//执行次数
		var timeNumber = $(this).parents("table").find(".timeNumber");//执行间隔
		var iw = $(this).parents("table").find(".ImplementWay");//执行方式
		if(conutNumber.val() == null || conutNumber.val() == ''){
			conutNumber.val(1);
		}
		if(timeNumber.val() == null || timeNumber.val() == ''){
			timeNumber.val(1);
		}
		requestFail.html(0);
		requestSuccess.html(0);
		var totalStartTime = new Date().getTime();
		var countNum = 1;
		for(var i=0;i<conutNumber.val();i++){
			var ajaxTime = new Date().getTime();
			if(getServerName()==null || getServerName()==''){
				if(download == 'true'){//下载API
					var url = path;
					var xhr = new XMLHttpRequest();
					var mType = methodType=='通用'?'get':methodType;
					// 组装参数
					var data = "random="+Math.random();
					for (var val in queryJson) {
						data += '&'+val+"="+queryJson[val];
					}
					if(mType == 'get'){
						url = url+"?"+data;
					}
					xhr.open(mType,url,true);
					// 设置请求头
					for (var val in headerJson) {
						xhr.setRequestHeader(val,headerJson[val]);
					}
					xhr.responseType = "blob";
					xhr.onreadystatechange = function () {
						if(this.status == 200){
							requestStatus.html(this.status+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;msg：success');
						}else{
							requestStatus.html(this.status+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;msg：fail');
						}
						
						// 请求完成
						if (this.status === 200) {
							var blob = this.response;
							var reader = new FileReader();
							// 转换为base64，可以直接放入a表情href
							reader.readAsDataURL(blob);  
							reader.onload = function (e) {
								// 转换完成，创建一个a标签用于下载
								var a = document.createElement('a');
								a.download = fileName;
								a.href = e.target.result;
								// 修复firefox中无法触发click
								$("body").append(a);  
								a.click();
								$(a).remove();
						    }
						}
						if(this.readyState == 4){
							var countTime = new Date().getTime()-ajaxTime;
							var totalTime = new Date().getTime()-totalStartTime;
							
							requestTime.html(totalTime+"ms");
							var json = {};
							if(this.status == 200){
								requestSuccess.html(Number(requestSuccess.html())+1);
								json['status'] = this.status;
						    	json['statusText'] = "操作成功！(此提示仅代表此次调用API状态，并不是返回值)";
							}else{
								requestFail.html(Number(requestFail.html())+1);
								json['status'] = this.status;
						    	json['statusText'] = "操作失败！(此提示仅代表此次调用API状态，并不是返回值，具体错误信息可查看浏览器开发者工具里面的调试信息)";
								
							}
					    	var options = {
					    			collapsed:false,
					    			withQuotes:false
					    	}
					    	if(conutNumber.val()==1){
					    		try{
						    		resposeData.jsonViewer(json,options);
						    	}catch(e){
						    		resposeData.html(json);
						    	}
					    	}else{
					    		resposeData.html(resposeData.html()+"<span style='color:"+(this.status==200?"green":"red")+"'>conut："+(countNum++)+"&nbsp;&nbsp;status："+this.status+"&nbsp;&nbsp;"
					    				+"msg："+(this.status==200?"success":"fail")+"&nbsp;&nbsp;time："+countTime+"ms</span><br/>"+"<span style='color:#888'>"+json+"</span><br/><br/>");
					    	}
						}
					};
					//发送ajax请求
					if(mType == 'get'){
						xhr.send()
					}else{
						if(contentType=='application/json'){
							xhr.send(JSON.stringify(queryJson))
						}else{
							xhr.send(data)
						}
					}
				}else{
					$.ajax({
					    url:path,
					    type:methodType=='通用'?'get':methodType,
					    dataType:"text",
					    async:iw.val()=='async'?true:false,
					    data:queryData,
					    headers:headerJson,
					    traditional:tl, // 阻止深度序列化
					    cache:false,
					    processData:processData,
					    contentType:contentTypeBool,
					    success:function(data,status,xhr){
					    	requestSuccess.html(Number(requestSuccess.html())+1);
					    	var countTime = new Date().getTime()-ajaxTime;
					    	var totalTime = new Date().getTime()-totalStartTime;
					    	
					    	requestStatus.html(xhr.status+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;msg：'+xhr.statusText);
							requestTime.html(totalTime+"ms");
					    	var options = {
					    			collapsed:false,
					    			withQuotes:false
					    	}
					    	if(conutNumber.val()==1){
					    		try{
						    		resposeData.jsonViewer(JSON.parse(data),options);
						    	}catch(e){
						    		resposeData.html(data);
						    	}
					    	}else{
					    		resposeData.html(resposeData.html()+"<span style='color:green;'>conut："+(countNum++)+"&nbsp;&nbsp;status："+xhr.status+"&nbsp;&nbsp;"
					    				+"msg："+xhr.statusText+"&nbsp;&nbsp;time："+countTime+"ms</span><br/>"+"<span style='color:#888'>"+data+"</span><br/><br/>");
					    	}
					    	
					    },
					    error:function(respose){
					    	requestFail.html(Number(requestFail.html())+1);
					    	var countTime = new Date().getTime()-ajaxTime;
					    	var totalTime = new Date().getTime()-totalStartTime;
					    	requestStatus.html(respose.status+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;msg：'+respose.statusText);
							requestTime.html(totalTime+"ms");
					    	var json = {};
					    	json['status'] = respose.status;
					    	json['statusText'] = respose.statusText;
					    	json['responseText'] = respose.responseText;
					    	if(respose.status == 0){
					    		json['responseText'] = '连接服务器异常！';
					    	}
					    	var options = {
					    			collapsed:false,
					    			withQuotes:false
					    	}
					    	if(conutNumber.val()==1){
					    		try{
						    		resposeData.jsonViewer(json,options);
						    	}catch(e){
						    		resposeData.html(json);
						    	}
					    	}else{
					    		resposeData.html(resposeData.html()+"<span style='color:red;'>conut："+(countNum++)+"&nbsp;&nbsp;status："+respose.status+"&nbsp;&nbsp;"
					    				+"msg："+respose.statusText+"&nbsp;&nbsp;time："+countTime+"ms</span><br/>"+"<span style='color:#888'>"+json+"</span><br/><br/>");
					    	}
					    }
					});
				}
			}else{
				if(download == 'true'){
					resposeData.html('<span style="color:red">暂时不支持远程项目下载调试！</span>')
					return;
				}
				if(fileInput != null && fileInput.length > 0){
					resposeData.html('<span style="color:red">暂时不支持远程项目上传调试！</span>')
					return;
				}
				if(tl){
					resposeData.html('<span style="color:red">暂时不支持远程项目数组传参调试！</span>')
					return;
				}
				$.ajax({
				    url:"lkad/getServerApi",
				    type:'get',
				    dataType:"text",
				    async:iw.val()=='async'?true:false,
				    traditional:tl, // 阻止深度序列化
				    data:{"path":getServerName()+path,"contentType":contentType,"headerJson":JSON.stringify(headerJson),"queryData":JSON.stringify(queryData),"type":methodType=='通用'?'get':methodType},
				    success:function(data,status,xhr){
				    	requestSuccess.html(Number(requestSuccess.html())+1);
				    	var countTime = new Date().getTime()-ajaxTime;
				    	var totalTime = new Date().getTime()-totalStartTime;
				    	requestStatus.html(xhr.status+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;msg：'+xhr.statusText);
						requestTime.html(totalTime+"ms");
				    	var options = {
				    			collapsed:false,
				    			withQuotes:false
				    	}
				    	if(conutNumber.val()==1){
				    		try{
					    		resposeData.jsonViewer(JSON.parse(data),options);
					    	}catch(e){
					    		resposeData.html(data);
					    	}
				    	}else{
				    		resposeData.html(resposeData.html()+"<span style='color:green;'>conut："+(countNum++)+"&nbsp;&nbsp;status："+xhr.status+"&nbsp;&nbsp;"
				    				+"msg："+xhr.statusText+"&nbsp;&nbsp;time："+countTime+"ms</span><br/>"+"<span style='color:#888'>"+data+"</span><br/><br/>");
				    	}
				    },
				    error:function(respose){
				    	requestFail.html(Number(requestFail.html())+1);
				    	var countTime = new Date().getTime()-ajaxTime;
				    	var totalTime = new Date().getTime()-totalStartTime;
				    	requestStatus.html(respose.status+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;msg：'+respose.statusText);
						requestTime.html(totalTime+"ms");
				    	var json = {};
				    	json['status'] = respose.status;
				    	json['statusText'] = respose.statusText;
				    	json['responseText'] = respose.responseText;
				    	if(respose.status == 0){
				    		json['responseText'] = '连接服务器异常！';
				    	}
				    	var options = {
				    			collapsed:false,
				    			withQuotes:false
				    	}
				    	
				    	if(conutNumber.val()==1){
				    		try{
					    		resposeData.jsonViewer(json,options);
					    	}catch(e){
					    		resposeData.html(json);
					    	}
				    	}else{
				    		resposeData.html(resposeData.html()+"<span style='color:red;'>conut："+(countNum++)+"&nbsp;&nbsp;status："+respose.status+"&nbsp;&nbsp;"
				    				+"msg："+respose.statusText+"&nbsp;&nbsp;time："+countTime+"ms</span><br/>"+"<span style='color:#888'>"+json+"</span><br/><br/>");
				    	}
				    }
				}); 
			}
			if(iw.val()!='async'){
				var start = (new Date()).getTime();
			    while((new Date()).getTime() - start < timeNumber.val()) {
			        continue;
			    }
			}
		}
	})
	
	
	
})


function assembleJson(paramNames,testDatas,dataTypes,paramTypes,type){// 参数名称，参数值，参数类型
	var paramJson = {};
	var namesArr = new Array();
	for(var i = 0;i<paramNames.length;i++){ // 遍历参数名称
		// 判断是否有数据类型
		var dataType;
		var paramType;
		try{
			dataType = dataTypes.eq(i);
			paramType = paramTypes.eq(i);
		}catch(e){
			dataType = dataTypes[i];
			paramType = paramTypes[i];
		}

		if(paramType.parent().children(":first").css("textDecoration").indexOf('line-through') != -1){
			namesArr.push(paramNames[i]);
			continue;
		}
		
		var delBool = false;
		for(var name of namesArr){
			if(paramNames[i].indexOf(".") != -1 
					&& paramNames[i].substr(0,name.length) == name
					&& paramNames[i].charAt(name.length) == '.'){
				delBool = true;
				break;
			}
		}
		if(delBool){
			continue;
		}
		
		if(dataType.html() != null && paramType.html() == type){ // 有数据类型
			var paramName = paramNames[i];
			// 判断是否是数组
			if(paramName.indexOf("[]")==-1){ // 不是数组
				// 判断是否有下一级
				if(paramName.indexOf(".") == -1){ // 没有下一级
					if(paramJson.paramName == null){// 判断是否设置过数据
						var testData;
						try{
							testData = testDatas.eq(i);
						}catch(e){
							testData = testDatas[i];
						}
						paramJson[paramName] = testData.val();// 设置数据
					}
				}else{ // 有下一级
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
							}catch(e){
								td = testDatas[j];
								dt = dataTypes[j];
								pt = paramTypes[j];
							}
							if(dt.html() != null && pt.html() == type){ // 有数据类型
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
			}else{// 是数组
				// 判断是否有下一级
				if(paramName.indexOf(".") == -1){ // 没有下一级
					paramName = paramName.substring(0,paramName.indexOf("[]"));
					if(paramJson.paramName == null){// 判断是否设置过数据
						var arr = new Array();
						var td;
						try{
							td = testDatas.eq(i);
						}catch(e){
							td = testDatas[i];
						}
						if(td.val() != null && td.val() !=''){
							arr[0] = td.val();
							var prevDatas = td.nextAll(".prevData");
							if(prevDatas != null){
								for(var m=0;m<prevDatas.length;m++){
									arr[m+1] = prevDatas.eq(m).val();
								}
							}
						}else{
							var prevDatas = td.nextAll(".prevData");
							if(prevDatas != null){
								for(var m=0;m<prevDatas.length;m++){
									arr[m] = prevDatas.eq(m).val();
								}
							}
						}
						paramJson[paramName] = arr;
					}
				}else{ // 有下一级
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
							}catch(e){
								td = testDatas[j];
								dt = dataTypes[j];
								pt = paramTypes[j];
							}
							if(dt.html() != null && pt.html() == type){ // 有数据类型
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

// testDatas为参数说明
function assembleJson2(paramNames,testDatas,dataTypes,paramTypes,type){// 参数名称，参数值，参数类型
	var paramJson = {};
	var namesArr = new Array();
	for(var i = 0;i<paramNames.length;i++){ // 遍历参数名称
		// 判断是否有数据类型
		var dataType;
		var paramType;
		try{
			dataType = dataTypes.eq(i);
			paramType = paramTypes.eq(i);
		}catch(e){
			dataType = dataTypes[i];
			paramType = paramTypes[i];
		}
		
		if(paramType.parent().children(":first").css("textDecoration").indexOf('line-through') != -1){
			namesArr.push(paramNames[i]);
			continue;
		}
		
		var delBool = false;
		for(var name of namesArr){
			if(paramNames[i].indexOf(".") != -1 
					&& paramNames[i].substr(0,name.length) == name
					&& paramNames[i].charAt(name.length) == '.'){
				delBool = true;
				break;
			}
		}
		if(delBool){
			continue;
		}
		
		if(dataType.html() != null && (type=='resp' || paramType.html() == type)){ // 有数据类型
			var paramName = paramNames[i];
			// 判断是否是数组
			if(paramName.indexOf("[]")==-1){ // 不是数组
				// 判断是否有下一级
				if(paramName.indexOf(".") == -1){ // 没有下一级
					if(paramJson.paramName == null){// 判断是否设置过数据
						var testData;
						try{
							testData = testDatas.eq(i);
						}catch(e){
							testData = testDatas[i];
						}
						paramJson[paramName] = testData.html();// 设置数据
					}
				}else{ // 有下一级
					i=i-1;// 迭代i回归
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
							}catch(e){
								td = testDatas[j];
								dt = dataTypes[j];
								pt = paramTypes[j];
							}
							if(dt.html() != null && (type=='resp' || pt.html() == type)){ // 有数据类型
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
			}else{// 是数组
				// 判断是否有下一级
				if(paramName.indexOf(".") == -1){ // 没有下一级
					paramName = paramName.substring(0,paramName.indexOf("[]"));
					if(paramJson.paramName == null){// 判断是否设置过数据
						var arr = new Array();
						var td;
						try{
							td = testDatas.eq(i);
						}catch(e){
							td = testDatas[i];
						}
						if(td.val() != null && td.val() !=''){
							arr[0] = td.val();
							var prevDatas = td.nextAll(".prevData");
							if(prevDatas != null){
								for(var m=0;m<prevDatas.length;m++){
									arr[m+1] = prevDatas.eq(m).val();
								}
							}
						}else{
							var prevDatas = td.nextAll(".prevData");
							if(prevDatas != null){
								for(var m=0;m<prevDatas.length;m++){
									arr[m] = prevDatas.eq(m).val();
								}
							}
						}
						paramJson[paramName] = arr;
					}
				}else{ // 有下一级
					i=i-1;// 迭代i回归
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
							}catch(e){
								td = testDatas[j];
								dt = dataTypes[j];
								pt = paramTypes[j];
							}
							if(dt.html() != null && (type=='resp' || pt.html() == type)){ // 有数据类型
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

//testDatas为参数说明
function assembleJson3(paramNames,testDatas,dataTypes,paramTypes,type){// 参数名称，参数值，参数类型
	var paramJson = {};
	var namesArr = new Array();
	for(var i = 0;i<paramNames.length;i++){ // 遍历参数名称
		// 判断是否有数据类型
		var dataType;
		var paramType;
		try{
			dataType = dataTypes.eq(i);
			paramType = paramTypes.eq(i);
		}catch(e){
			dataType = dataTypes[i];
			paramType = paramTypes[i];
		}
		
		if(paramType.parent().children(":first").css("textDecoration").indexOf('line-through') != -1){
			namesArr.push(paramNames[i]);
			continue;
		}
		
		var delBool = false;
		for(var name of namesArr){
			if(paramNames[i].indexOf(".") != -1 
					&& paramNames[i].substr(0,name.length) == name
					&& paramNames[i].charAt(name.length) == '.'){
				delBool = true;
				break;
			}
		}
		if(delBool){
			continue;
		}
		
		if(dataType.html() != null && (type=='resp' || paramType.html() == type)){ // 有数据类型
			var paramName = paramNames[i];
			// 判断是否是数组
			if(paramName.indexOf("[]")==-1){ // 不是数组
				// 判断是否有下一级
				if(paramName.indexOf(".") == -1){ // 没有下一级
					if(paramJson.paramName == null){// 判断是否设置过数据
						var testData;
						try{
							testData = testDatas.eq(i);
						}catch(e){
							testData = testDatas[i];
						}
						paramJson[paramName] = testData.val();// 设置数据
					}
				}else{ // 有下一级
					i=i-1;// 迭代i回归
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
							}catch(e){
								td = testDatas[j];
								dt = dataTypes[j];
								pt = paramTypes[j];
							}
							if(dt.html() != null && (type=='resp' || pt.html() == type)){ // 有数据类型
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
						paramJson[paramStr] = assembleJson3(arrParam,arrTest,arrData,arrType,type);
					}
				}
			}else{// 是数组
				// 判断是否有下一级
				if(paramName.indexOf(".") == -1){ // 没有下一级
					paramName = paramName.substring(0,paramName.indexOf("[]"));
					if(paramJson.paramName == null){// 判断是否设置过数据
						var arr = new Array();
						var td;
						try{
							td = testDatas.eq(i);
						}catch(e){
							td = testDatas[i];
						}
						if(td.val() != null && td.val() !=''){
							arr[0] = td.val();
							var prevDatas = td.nextAll(".prevData");
							if(prevDatas != null){
								for(var m=0;m<prevDatas.length;m++){
									arr[m+1] = prevDatas.eq(m).val();
								}
							}
						}else{
							var prevDatas = td.nextAll(".prevData");
							if(prevDatas != null){
								for(var m=0;m<prevDatas.length;m++){
									arr[m] = prevDatas.eq(m).val();
								}
							}
						}
						paramJson[paramName] = arr;
					}
				}else{ // 有下一级
					i=i-1;// 迭代i回归
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
							}catch(e){
								td = testDatas[j];
								dt = dataTypes[j];
								pt = paramTypes[j];
							}
							if(dt.html() != null && (type=='resp' || pt.html() == type)){ // 有数据类型
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
							arr[0] = assembleJson3(arrParam,arrTest,arrData,arrType,type);
							paramJson[paramStr] = arr;
						}else{
							paramJson[paramStr]= assembleJson3(arrParam,arrTest,arrData,arrType,type);
						}
					}
				}
			}
		}
	}
	return paramJson;
}

var met_index = 0;
function buildMenu(doc,tVersion) {
	var methods =doc.methodModels;
	var vbool = false;
	if(methods != null && methods.length>0){
		for(var i = 0;i<methods.length;i++){
			var mVersion = methods[i].version;
			if(mVersion == tVersion){
				vbool = true;
				break;
			}
		}
	}
	var cImgName = vbool?"file2.gif":"file.gif";
	var str = "<h3 class='obtain'><img src='img/"+cImgName+"' height='30px' width='30px'>&nbsp;<span>"+doc.name+"</span>&nbsp;&nbsp;<span class='docDescription'>"+doc.description+"</span></h3>";
    if(methods != null && methods.length>0){
    	str +="<ul hidden='hidden'>"
    	for(var i = 0;i<methods.length;i++){
    		met_index++;
    		var createTime = methods[i].createTime;
    		var updateTime = methods[i].updateTime;
    		var mVersion = methods[i].version;
    		var imgName = mVersion == tVersion?"f4.gif":"f.gif";
    		str += "<li data='"+met_index+"' urlname='"+methods[i].name+"' class='secondary' title='双击可添加接口标签信息'>" +
    				"<input type='hidden' value='"+methods[i].name+"-"+methods[i].url+"'>" +
    				"<h5><img src='img/"+imgName+"' height='25px' width='25px'>&nbsp;<span>"+methods[i].name+"</span></h5></li>";
    		var request = methods[i].request;
    		var respose = methods[i].respose;
    		var str2 ="<div id='method_"+met_index+"' class='method-table' hidden='hidden'><div>" +
    				"<ul class='method-ul'>" +
    				"<li><span class='method-name-pdf'>"+methods[i].name+"</span>&nbsp;&nbsp;<span class='docDescription'>"+methods[i].description+"</span>&nbsp;&nbsp;<span>"+methods[i].version+"</span>&nbsp;&nbsp;<span style='color:red' class='method-token'>"+(methods[i].token==true?"authorize token":"")+"</span><span style='color:red'>&nbsp;&nbsp;"+(methods[i].contentType=="application/json" && methods[i].requestType=="GET"?"注意：ContentType为application/json传参时只支持post、put、delete请求！":"")+"</span></li>"+
    				"<li class='method-requestParamInfo'><span>Method Type：</span><span class='method-requestType'>"+methods[i].requestType+"</span>&nbsp;&nbsp;&nbsp;<span><b>Content Type：</b></span><span class='content-TYPE'>"+methods[i].contentType+"</span></span>&nbsp;&nbsp;&nbsp;<span><b>URL：</b></span><span class='method-URL'>"+methods[i].url+"</li>"+
    				"<li class='method-requestParamInfo'><span></span><span><b>Author：</b>"+(methods[i].author==null || methods[i].author==''?'未设置':methods[i].author)+"&nbsp;&nbsp;&nbsp;<b>CreateTime：</b>"+(createTime==null || createTime==''?'未设置':createTime)+"&nbsp;&nbsp;&nbsp;<b>UpdateTime：</b>"+(updateTime==null || updateTime==''?'未设置':updateTime)+"</span><span><input class='method-download' type='hidden' value='"+methods[i].download+"'></span></li>"+
    				"</ul>"+
    				"</div><div>"+buildParams(request,"req","loc_method",1,methods[i].contentType)+"</div>";
    		str2 +="<div>"+buildParams(respose,"resp","loc_method",1)+"</div></div>";
    		$(".right-box").append(str2);
    	}
    	str+="</ul>";
    }
    return str;
}

var radioRandom = 0;

function buildParams(doc,type,loc,flag,contentType){
	radioRandom++;
	var str = "";
	if(loc == "loc_method"){
	 	str = "<table class='hovertable'>";
		if(type=="req" || type=="param"){
			str += "<thead class='reqcls'><tr><td colspan='7'>请求参数</td></tr>"
			str += "<tr><td>名称</td><td>作用</td><td>是否必须</td><td>数据类型</td><td>参数类型</td><td>测试数据</td><td>描述</td></tr>"
			str +="</thead><tbody>"
		}else if(type=="resp"){
			str += "<thead class='respcls'><tr><td colspan='4'>响应参数</td></tr>"
			str += "<tr class='resposeDataJson' hidden='hidden'><td colspan='4'></td></tr>"
			str += "<tr class='resposeDataTable'><td>名称</td><td>作用</td><td>数据类型</td><td>描述</td></tr>"
			str +="</thead><tbody class='resposeDataTable'>"
		}
		
	}
	if(doc != null && doc.length > 0){
		for(var i = 0;i<doc.length;i++){
			var {value,name,description,array,required,dataType,paramType,testData,parentName} = doc[i];
			var arr = new Array();
			filter(value,doc,arr);
			value = flag == 2?(loc+"."+value):flag == 3 && loc.lastIndexOf(".")!=-1?(loc.substring(0,loc.lastIndexOf(".")+1)+value):value;
			if(arr != null && arr.length > 0){
				var val = array != null && (array==true || array=='true')?value+'[]':value;
				if(type=="req" || type=="param"){
					str+=buildParams(arr,"param",val,2);
				}else{
					str+="<tr class='parentParam'><td class='addinfo' title='双击可添加参数标签信息'>"+val+"</td><td>"+name+"</td><td>"+description+"</td><td></td></tr>"
					str+=buildParams(arr,"resp",val,2);
				}
			}else{
				var model =doc[i].modelModel;
				if(model != null && model != "null"){
					var val = array != null && (array==true || array=='true')?value+'[]':value;
					if(type=="req" || type=="param"){
						str+=buildParams(model.propertyModels,"params",val,3);
					}else if(type=="resp"){
						str+=buildParams(model.propertyModels,"resps",val,3);
					}
					if(type=="params"){
						str+="<tr class='parentParam'><td class='paramValue addinfo' title='双击可添加参数标签信息'>"+val+"</td><td class='paramInfo'>"+name+"</td><td>"+description+"</td><td class='dataType paramType testData'></td><td></td><td colspan='2'></td></tr>"								
						str+=buildParams(model.propertyModels,"params",val,2);
					}else if(type=="resps"){
						str+="<tr class='parentParam'><td class='addinfo' title='双击可添加参数标签信息'>"+val+"</td><td>"+name+"</td><td>"+description+"</td><td></td></tr>"
						str+=buildParams(model.propertyModels,"resps",val,2);
					}
				}else{
					var val = array != null && (array==true || array=='true')?value+'[]':value;
					if(type=="req" || type=="param" || type=="params"){
						str+="<tr><td class='paramValue addinfo' title='双击可添加参数标签信息'>"+val+"</td><td class='paramInfo'>"+name+"</td><td class='isRequired'>"+
						(required==true?'yes':'no')+"</td><td class='dataType'>"+dataType+"</td><td class='paramType'>"+paramType+"</td><td>"+
						(dataType=='MultipartFile'?"<form class='upload' enctype='multipart/form-data'>"+"<input type='file' class='testData' name='"+value+"'>"+"</form>":
							dataType=='MultipartFile[]'?"<form class='upload' enctype='multipart/form-data'>"+"<input type='hidden' value='"+value+"' class='fileValue'><input type='file' class='testData' name='"+value+"'><input type='button' class='subFile' value='-'><input type='button' class='addFile' value='+'></form>":"<input class='testData tdcss' type='"+(dataType=='Date'?'date':'text')+"' value='"+testData+"'>"+
						(dataType==null?"":dataType.indexOf('[]')==-1?"":"<input type='button' class='subtract' value='-'><input type='button' class='add' value='+'>")+"</td><td>"+description+"</td></tr>")
					}else{
						str+="<tr><td class='respValue addinfo' title='双击可添加参数标签信息'>"+val+"</td><td class='respInfo'>"+name+"</td><td class='respType'><input class='reqdatatype' disabled='disabled' type='text' value='"+dataType+"'></td><td>"+description+"</td></tr>"
					}
				}
			}
		}
		if(loc == "loc_method"){
			if(type=="req" || type=="param"){
				str+="<tr><td colspan='7' class='requestData' hidden='hidden'></td></tr>"
				str+="<tr class='testSend'><td colspan='7'>"+
				"<input type='button' class='testSendButton' value='测试API请求'>&nbsp;&nbsp;"+
				"执行次数：<input type='number' class='conutNumber' value='1'>&nbsp;&nbsp;" +
				"时间间隔(ms)：<input class='timeNumber' disabled='true' style='background-color:#ccc' type='number' value='1'>&nbsp;&nbsp;"+
				"执行方式：<select class='ImplementWay'><option value='async'>异步</option><option value='sync'>同步</option></select>"+
				"<label><input type='checkbox' class='app-traditional' value='1'>阻止深度序列化</label>&nbsp;&nbsp;"+
				"<input type='button' class='request-json' value='树状展示请求参数'>&nbsp;&nbsp;"+
				"<input type='button' class='switch-resp-json' value='树状展示响应参数'>"+
				"</td></tr>"
				str+="<tr><td colspan='7' class='close-resposeData' align='center'><a style='font-size:12;color:blue;cursor:pointer'>打开调试窗口</a>" +
						"<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;status：</span><span class='request-status'></span>" +
						"<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;time：</span><span class='request-time'></span>" +
						"<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;successful：</span><span class='request-success'></span>" +
						"<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;failures：</span><span class='request-fail'></span>" +
						"</td></tr>"
				str+="<tr><td colspan='7' class='resposeData' hidden='hidden'>暂无调试信息</td></tr>"
			}
		}
	}else if(loc == "loc_method"){
		if(type=="req" || type=="param"){
			str+="<tr><td colspan='7' style='color:red'>该接口没有设置请求参数</td></tr>"
				str+="<tr class='testSend'><td colspan='7'>"+
				"<input type='button' class='testSendButton' value='测试API请求'>&nbsp;&nbsp;"+
				"执行次数：<input type='number' class='conutNumber' value='1'>&nbsp;&nbsp;" +
				"时间间隔(ms)：<input class='timeNumber' disabled='true' style='background-color:#ccc' type='number' value='1'>&nbsp;&nbsp;"+
				"执行方式：<select class='ImplementWay'><option value='async'>异步</option><option value='sync'>同步</option></select>&nbsp;&nbsp;"+
				"<label><input type='checkbox' class='app-traditional' value='1'>阻止深度序列化</label>&nbsp;&nbsp;"+
				"<input type='button' class='request-json' value='树状展示请求参数'>&nbsp;&nbsp;"+
				"<input type='button' class='switch-resp-json' value='树状展示响应参数'>"+
				"</td></tr>"
				str+="<tr><td colspan='7' class='close-resposeData' align='center'><a style='font-size:12;color:blue;cursor:pointer'>打开调试窗口</a>" +
						"<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;status：</span><span class='request-status'></span>" +
						"<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;time：</span><span class='request-time'></span>" +
						"<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;successful：</span><span class='request-success'></span>" +
						"<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;failures：</span><span class='request-fail'></span>" +
						"</td></tr>"
				str+="<tr><td colspan='7' class='resposeData' hidden='hidden'>暂无调试信息</td></tr>"
		}else{
			str+="<tr><td colspan='4' style='color:red'>该接口没有设置响应参数</td></tr>"
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


/*$(function(){
	var canvas = document.getElementById('canvas'), 
	  ctx = canvas.getContext('2d'), 
	  w = canvas.width = window.innerWidth, 
	  h = canvas.height = 170,
	 
	  hue = 217, 
	  stars = [], 
	  count = 0, 
	  maxStars = 1300;// 星星数量
	 
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
	  // 星星移动范围，值越大范围越小，
	} 
	 
	var Star = function() { 
	 
	  this.orbitRadius = random(maxOrbit(w, h)); 
	  this.radius = random(60, this.orbitRadius) / 8;  
	  // 星星大小
	  this.orbitX = w / 2; 
	  this.orbitY = h / 2; 
	  this.timePassed = random(0, maxStars); 
	  this.speed = random(this.orbitRadius) / 50000;  
	  // 星星移动速度
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
	  ctx.globalAlpha = 0.5; // 尾巴
	  ctx.fillStyle = 'hsla(' + hue + ', 64%, 6%, 2)'; 
	  ctx.fillRect(0, 0, w, h) 
	 
	  ctx.globalCompositeOperation = 'lighter'; 
	  for (var i = 1, l = stars.length; i < l; i++) { 
	    stars[i].draw(); 
	  }; 
	 
	  window.requestAnimationFrame(animation); 
	} 
	 
	animation();
})*/