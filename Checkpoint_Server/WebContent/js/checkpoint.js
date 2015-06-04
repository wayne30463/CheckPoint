function check_aid()
{
	if($.cookie('aid') == -1)
		window.location = 'ActivityManager.html';
}
/*-- (判斷日期格式是否正確) -------------------------*/  
function isDate(year, month, day){  
   var dateStr;  
   if (!month || !day) {  
       if (month == '') {  
           dateStr = year + "/1/1"  
       }else if (day == '') {  
           dateStr = year + '/' + month + '/1';  
       }else {  
           dateStr = year.replace(/[.-]/g, '/');  
       }  
   }else {  
       dateStr = year + '/' + month + '/' + day;  
   }  
   dateStr = dateStr.replace(/\/0+/g, '/');  
  
   var accDate = new Date(dateStr);  
   var tempDate = accDate.getFullYear() + "/";  
   tempDate += (accDate.getMonth() + 1) + "/";  
   tempDate += accDate.getDate();  
  
   if (dateStr == tempDate) {  
       return true;  
   }  
   return false;  
} 
function padLeft(str, len) {
    str = '' + str;
    if (str.length >= len) {
        return str;
    } else {
        return padLeft("0" + str, len);
    }
}
function showMessage(result,paramFunc){
	var button = "btn-default";
	var tittle = "success";
	if(result.success === "N")
	{
		button = "btn-danger";
		tittle = "error";
	}
	bootbox.alert({  
        buttons: {  
           ok: {  
                label: '確定',  
                className: button  
            }  
        },  
        message: result.message,  
        callback: function() {  
        	if (paramFunc && (typeof paramFunc == "function")) 
        		paramFunc();  
        },  
        title: tittle  
    });  
}
function SecondToTime(second)
{
  var hour = Math.floor(second / 3600);
  second = second - hour * 3600;
  var minute = Math.floor(second / 60);
  second = second - minute * 60;
  var result = "";
  result += (hour!=0)?hour+"小時":"";
  result += (minute!=0)?minute+"分鐘":"";
  result += (second!=0)?second+"秒":"";
  return  (result.length == 0)?"無":result;
}