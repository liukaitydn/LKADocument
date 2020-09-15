# LKADocument测试项目
##### 项目描述：智能、便捷、高效
##### 项目版本：1.0

### 复杂的对象传参
**版本号：**暂无
**Method Type：**POST
**Url：**/lkadocument/demo/addUser
**Content Type：**application/json
**Author：**未设置 **CreateTime：**未设置 **updateTime：**未设置
##### 请求参数
| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |
| :---- | :---- | :---- | :---- | :---- | :---- |
| name | 用户名称 | 是 | String | query |  |
| age | 年龄 | 否 | String | query |  |
| role | 角色对象 | 否 | | |  |
| role.id | 角色ID | 是 | Integer | query |  |
| role.name | 角色名称 | 是 | String | query |  |
| likes[] | 用户爱好 | 是 | String[] | query |  |
| addresses[] | 地址信息 | 否 | | |  |
| addresses[].id | 地址ID | 是 | Integer | query |  |
| addresses[].info | 地址信息 | 是 | String | query |  |
##### 响应参数
| 名称 | 作用 |  数据类型 | 描述 |
| :---- | :---- | :---- | :---- |
| code | 状态码 | String |  |
| msg | 消息 | String |  |
| user |  |  |  |
| user.name | 用户名称 | String |  |
| user.age | 年龄 | String |  |
| user.role | 角色对象 |  |  |
| user.role.id | 角色ID | Integer |  |
| user.role.name | 角色名称 | String |  |
| user.likes[] | 用户爱好 | String[] |  |
| user.addresses[] | 地址信息 |  |  |
| user.addresses[].id | 地址ID | Integer |  |
| user.addresses[].info | 地址信息 | String |  |

### 数组传参
**版本号：**暂无
**Method Type：**POST
**Url：**/lkadocument/demo/arrTest
**Content Type：**application/x-www-form-urlencoded
**Author：**未设置 **CreateTime：**未设置 **updateTime：**未设置
##### 请求参数
| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |
| :---- | :---- | :---- | :---- | :---- | :---- |
| ids[] | 用户id | 是 | String[] | query |  |
##### 响应参数
| 名称 | 作用 |  数据类型 | 描述 |
| :---- | :---- | :---- | :---- |
| code | 状态码 | String |  |
| msg | 消息 | String |  |

### 文件下载
**版本号：**暂无
**Method Type：**POST
**Url：**/lkadocument/demo/fileDownload
**Content Type：**application/x-www-form-urlencoded
**Author：**未设置 **CreateTime：**未设置 **updateTime：**未设置
##### 请求参数
| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |
| :---- | :---- | :---- | :---- | :---- | :---- |
| name | 文件名 | 是 | HttpServletResponse | query |  |
| id | id | 是 | String | query |  |
##### 响应参数
| 名称 | 作用 |  数据类型 | 描述 |
| :---- | :---- | :---- | :---- |
| 该接口没有响应参数 |  |   |  |

### 文件批量上传
**版本号：**暂无
**Method Type：**POST
**Url：**/lkadocument/demo/fileUpload
**Content Type：**application/x-www-form-urlencoded
**Author：**未设置 **CreateTime：**未设置 **updateTime：**未设置
##### 请求参数
| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |
| :---- | :---- | :---- | :---- | :---- | :---- |
| files[] | 上传文件 | 是 | MultipartFile[] | query |  |
| name | 文件名 | 是 | String | query |  |
| id | id | 是 | String | query |  |
##### 响应参数
| 名称 | 作用 |  数据类型 | 描述 |
| :---- | :---- | :---- | :---- |
| code | 状态码 | String |  |
| msg | 消息 | String |  |

### 响应参数复杂的Map结构用法
**版本号：**暂无
**Method Type：**POST
**Url：**/lkadocument/demo/getMap
**Content Type：**application/x-www-form-urlencoded
**Author：**未设置 **CreateTime：**未设置 **updateTime：**未设置
##### 请求参数
| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |
| :---- | :---- | :---- | :---- | :---- | :---- |
| 该接口没有请求参数 |  |  |  |  |  |
##### 响应参数
| 名称 | 作用 |  数据类型 | 描述 |
| :---- | :---- | :---- | :---- |
| code | 状态码 | String |  |
| msg | 消息 | String |  |
| result | 响应数据 |  |  |
| result.total | 总记录数 | String |  |
| result.users[] | 用户对象列表 |  |  |
| result.users[].name | 用户名称 | String |  |
| result.users[].age | 年龄 | String |  |
| result.users[].role | 角色对象 |  |  |
| result.users[].role.id | 角色ID | Integer |  |
| result.users[].role.name | 角色名称 | String |  |
| result.users[].likes[] | 用户爱好 | String[] |  |
| result.users[].addresses[] | 地址信息 |  |  |
| result.users[].addresses[].id | 地址ID | Integer |  |
| result.users[].addresses[].info | 地址信息 | String |  |

### 响应参数复杂的对象结构用法
**版本号：**暂无
**Method Type：**POST
**Url：**/lkadocument/demo/getObj
**Content Type：**application/x-www-form-urlencoded
**Author：**未设置 **CreateTime：**未设置 **updateTime：**未设置
##### 请求参数
| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |
| :---- | :---- | :---- | :---- | :---- | :---- |
| 该接口没有请求参数 |  |  |  |  |  |
##### 响应参数
| 名称 | 作用 |  数据类型 | 描述 |
| :---- | :---- | :---- | :---- |
| code | 响应状态 | String | 200-正常,其它-错误 |
| msg | 响应消息 | String |  |
| result | 响应数据 |  |  |
| result.total | 总记录数 | String |  |
| result.users[] | 用户对象列表 |  |  |
| result.users[].name | 用户名称 | String |  |
| result.users[].age | 年龄 | String |  |
| result.users[].addresses[] | 地址信息 |  |  |
| result.users[].addresses[].info | 地址信息 | String |  |

### 对象参数分组
**版本号：**1.0
**Method Type：**POST
**Url：**/lkadocument/demo/getUser
**Content Type：**application/json
**Author：**未设置 **CreateTime：**未设置 **updateTime：**未设置
##### 请求参数
| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |
| :---- | :---- | :---- | :---- | :---- | :---- |
| name | 用户名称 | 是 | String | query |  |
| age | 年龄 | 否 | String | query |  |
| addresses[] | 地址信息 | 否 | | |  |
| addresses[].info | 地址信息 | 是 | String | query |  |
##### 响应参数
| 名称 | 作用 |  数据类型 | 描述 |
| :---- | :---- | :---- | :---- |
| code | 状态码 | String |  |
| msg | 消息 | String |  |
| name | 用户名称 | String |  |
| age | 年龄 | String |  |
| role | 角色对象 |  |  |
| role.id | 角色ID | Integer |  |
| role.name | 角色名称 | String |  |
| likes[] | 用户爱好 | String[] |  |
| addresses[] | 地址信息 |  |  |
| addresses[].id | 地址ID | Integer |  |
| addresses[].info | 地址信息 | String |  |

### 获取用户列表
**版本号：**暂无
**Method Type：**POST
**Url：**/lkadocument/demo/getUsers/{roleType}
**Content Type：**application/x-www-form-urlencoded
**Author：**未设置 **CreateTime：**未设置 **updateTime：**未设置
##### 请求参数
| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |
| :---- | :---- | :---- | :---- | :---- | :---- |
| name | 用户名 | 是 | String | query | 支持模糊匹配 |
| age | 年龄 | 否 | Integer | query | 范围0-120 |
| roleType | 角色类型 | 否 | Integer | path | 1-经理，2-主管，3-普通员工 |
| token | 授权token | 是 | String | header | 授权token |
##### 响应参数
| 名称 | 作用 |  数据类型 | 描述 |
| :---- | :---- | :---- | :---- |
| code | 状态码 | String |  |
| msg | 消息 | String |  |

### 登录1,用户登录验证
**版本号：**1.0
**Method Type：**POST
**Url：**/lkadocument/demo/login
**Content Type：**application/x-www-form-urlencoded
**Author：**liukai **CreateTime：**2020-6-20 **updateTime：**2020-6-20
##### 请求参数
| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |
| :---- | :---- | :---- | :---- | :---- | :---- |
| name | 用户名 | 是 | String | query |  |
| pwd | 密码 | 是 | String | query |  |
##### 响应参数
| 名称 | 作用 |  数据类型 | 描述 |
| :---- | :---- | :---- | :---- |
| code | 状态码 | String |  |
| msg | 消息 | String |  |

### 登录2
**版本号：**暂无
**Method Type：**POST
**Url：**/lkadocument/demo/test
**Content Type：**application/x-www-form-urlencoded
**Author：**未设置 **CreateTime：**未设置 **updateTime：**未设置
##### 请求参数
| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |
| :---- | :---- | :---- | :---- | :---- | :---- |
| name | 用户名 | 是 | String | query |  |
| pwd | 密码 | 是 | String | query |  |
##### 响应参数
| 名称 | 作用 |  数据类型 | 描述 |
| :---- | :---- | :---- | :---- |
| code | 状态码 | String |  |
| msg | 消息 | String |  |

### 响应参数基本用法
**版本号：**暂无
**Method Type：**POST
**Url：**/lkadocument/demo/resTest
**Content Type：**application/x-www-form-urlencoded
**Author：**未设置 **CreateTime：**未设置 **updateTime：**未设置
##### 请求参数
| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |
| :---- | :---- | :---- | :---- | :---- | :---- |
| name | 用户名 | 是 | String | query |  |
| pwd | 密码 | 是 | String | query |  |
##### 响应参数
| 名称 | 作用 |  数据类型 | 描述 |
| :---- | :---- | :---- | :---- |
| code | 状态码 | String | 200-成功,其它-失败 |
| msg | 响应消息 | String | 响应结果弹窗信息 |
