# Liberty
handle .lib files, save into json, and support for filter   
用法如下：  
toJson:  
java -jar Lib.jar -libs libsListFile [-output outputFile]  
filterJson:   
java -jar Lib.jar -json jsonFile -filter filterExpression -outputAttr AttrName -outputFile outputFile  

## toJson
toJson时，需要提供所有待解析的.lib文件，放在一个文本里，格式如下：  
	`lib_file_path <vender_if_stdcell>`   
`<vender_if_stdcell>`用来解析stdcell相关的信息。  
`-outputFile outputFile`为optional，不指定情况下，默认保存到当前目录的libs.json。  

## filterJson
得到json之后，我们对它处理，得到我们需要的信息，用法如下：  
  `java -jar Lib.jar -json jsonFile -filter filterExpression -outputAttr AttrName -outputFile outputFile`   
`-filter`后是filter expression，为String，所以如果是多个条件过滤的话，要加上引号。  
`-outputAttr`指的是需要输出的lib attribute，比如我们需要ss_0.81_-40下的所有link lib，我们只要dbfile attribute，其它我们并不需要。   
`-outputFile`是将输出重定向到你指定的file。  

for more information: read [my blog](https://ileonsun.github.io/save-library-info-in-json/)
